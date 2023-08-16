/* Copyright (C) 1997-2023, Antony L Hosking.
 * All rights reserved.  */

package mojo;

import java.math.BigInteger;
import java.util.*;

import mojo.parse.*;
import static mojo.Absyn.*;

public class Semant {
    private static void usage() {
        throw new java.lang.Error("Usage: java mojo.Semant <source>.mj");
    }

    /**
     * The target word size (in bytes).
     */
    final int wordSize;

    /**
     * The target's minimum and maximum word values.
     */
    final BigInteger MIN_VALUE, MAX_VALUE;

    Semant(int wordBits) {
        this.wordSize = wordBits / 8;
        if (wordBits == Integer.SIZE) {
            MIN_VALUE = BigInteger.valueOf(Integer.MIN_VALUE);
            MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);
        } else if (wordBits == Long.SIZE) {
            MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);
            MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
        } else {
            MIN_VALUE = null;
            MAX_VALUE = null;
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) usage();
        java.io.File file = new java.io.File(args[0]);
        Type.init();
        try {
            Value.Unit unit = new Parser(file).Unit();
            if (Error.nErrors() > 0) return;
            Semant semant = new Semant(Integer.SIZE);
            semant.Check(unit);
            semant.print(Scope.Top());
        } catch (java.io.FileNotFoundException e) {
            System.err.println("File not found:" + file.getName());
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        } catch (TokenMgrError e) {
            System.err.println(e.getMessage());
        } finally {
            System.err.flush();
        }
    }

    /**
     * Type-check values.
     */
    void Check(Value v) {
        class Visitor implements Value.Visitor<Void> {
            public Void visit(Value.Field v) {
                // TODO: Check type, "fields must not be open arrays"
                Type t = Check(TypeOf(v));
                if(IsOpenArray(t) != null)
                    Error.ID(v.token, "fields may not be open arrays");
                return null;
            }
            public Void visit(Value.Formal v) {
                // TODO: Check type
                Type t = Check(TypeOf(v));
                if (IsOpenArray(t) != null) {
                    if ((v.mode == Value.Formal.Mode.VALUE) || (v.mode == null))
                        Error.WarnID(v.token, "open array passed by value");
                }
                return null;
            }
            public Void visit(Value.Method v) {
                // TODO:
                // - Check and set sig
                // - Check expression and resolve default
                // - Check resulting value
                // - If value non-null
                //   - Check value
                //   - if type Null set value null
                //   - or check value is procedure:
                //     "default is not a procedure"
                //     "default is a nested procedure"
                //     "default is incompatible with method type"

                if (v.sig != null) {
                    v.sig = Is(Check(v.sig),Type.Proc.class);
                    v.sig.scope = Scope.PushNewClosed();
                    Scope.Insert(v.sig.formals);
                    Scope.PopNew();
                }

                Check(v.expr);
                ResolveDefault(v);
//                if (v.expr == null) {v.value = null;}
                Type result = null;
                if (v.sig != null) result = Check(v.sig.result);

                if (IsOpenArray(result) != null)
                    Error.WarnID(v.token, "procedures may not return open arrays");

                if (v.value != null) {
                    Check(v.value);
                    Type t = TypeOf(v.value);
                    if (IsEqual(t, Type.Null.T, null)) { // how to check if type is "Null"
                        v.value = null;
                    } else if (v.value instanceof Value.Procedure){
                        if (IsNested((Value.Procedure) v.value)) Error.WarnID(v.token, "default is a nested procedure");
                    }
                }

                return null;
            }
            public Void visit(Value.Procedure p) {
                Check(p.sig);
                // NOTE: don't save the signature returned, otherwise
                // the formals will be reused by procedures with the
                // same signature.

                p.body = new ProcBody() {
                        public void accept(Value.Visitor<Void> v) {
                            p.accept(v);
                        }
                    };

                // defer the rest to CheckBody
                return null;
            }
            public Void visit(Value.Variable v) {
                v.proc = ProcBody.Top();
                Type type = Check(TypeOf(v));
                int size = v.size = type.size;
                if (v.formal == null && IsOpenArray(type) != null)
                    Error.ID(v.token, "variable cannot be an open array");
                if (type != Type.Err.T && IsEqual(type, Type.Null.T, null))
                    Error.WarnID(v.token, "variable has type Null");
                if (Scope.OuterMost(v.scope)) v.global(true);
                v.checked(true); // allow recursions through the init expr
                if (!v.indirect() && !v.global()) {
                    if (v.formal != null && size > 8 * wordSize)
                        Error.WarnID(v.token, "large parameter passed by value");
                    else if (size > 8192)
                        Error.WarnID(v.token, "large local variable");
                } else if (v.formal != null && v.formal.refType != null)
                    Error.WarnID(v.token, "open array passed by value");
                if (IsStructured(type)) v.needsAddress(true);
                Check(v.formal);
                if (v.external()) {
                    if (v.expr != null) {
                        Error.ID(v.token, "External variables cannot be initialized");
                        Check(v.expr);
                        CheckAssign(v.token, type, v.expr);
                    }
                } else if (v.expr != null) {
                    Check(v.expr);
                    CheckAssign(v.token, type, v.expr);
                    Expr e = ConstValue(v.expr);
                    if (e == null) {
                        if (Value.Unit.IsInterface()) {
                            Error.ID(v.token, "initial value is not a constant");
                        }
                        if (v.global() && size > 64 * wordSize) {
                            assert !v.indirect();
                            v.indirect(true);
                        }
                    } else {
                        // initialize the variable to an explicit constant
                        if (!v.indirect()) {
                            if (v.global()) {
                                //
                            } else if (IsStructured(type)) {
                                v.initStatic(true);
                            }
                        }
                        v.expr = e;
                    }
                } else if (v.global()) {
                    // no explicit initialization is given but the var is global
                    if (IsOrdinal(type)) {
                        // synthesize an initialization expression
                        BigInteger min = Min(type), max = Max(type);
                        if (min.compareTo(BigInteger.ZERO) <= 0 && max.compareTo(BigInteger.ZERO) >= 0) {
                            v.initDone(true);
                        } else
                            v.expr = new Expr.Int(v.token, min);
                    } else v.initDone(true);
                }
                return null;
            }
            public Void visit(Value.Const v) {
                // TODO:
                // - Check expr
                // - Check type
                // - If procedure: "nested procedures are not constants"
                // - If type is Err.T: "value is not a constant expression"
                // - Otherwise:
                //   - check expr assignable to type
                //   - compute constant from expr: "value is not a constant"
                //   - set expr to constant
                Check(v.expr);
                Type t = Check(v.type);
                if (Is(v.type, Type.Proc.class) != null) {
                    Error.Msg(v.token, "nested procedures are not constants");
                } else if (IsEqual(t, Type.Err.T, null)) {
                    Error.Msg(v.token, "value is not a constant expression");
                } else {
                    CheckAssign(v.token, v.type, v.expr);
                    Expr cv = ConstValue(v.expr);
                    if (cv == null) {
                        Error.Msg(v.token, "value is not a constant");
                    } else {
                        v.expr = cv;
                    }

                }
                return null;
            }
            public Void visit(final Value.Unit t) {
                Value.Unit save = Value.Unit.Switch(t);
                // open my private local scope
                t.localScope = Scope.PushNewModule(t.name);
                {
                    Scope.Insert(t.decls);
                    Check(t.localScope);
                    t.body = new ProcBody() {
                            public void accept(Value.Visitor<Void> v) {
                                t.accept(v);
                            }
                        };
                    ProcBody.Push(t.body);
                    if (t.block != null)
                        Check(t.block, false, null, null);
                    ProcBody.Pop();
                }
                Scope.PopNew();
                Value.Unit.Switch(save);
                return null;
            }
            public Void visit(Value.Tipe v) {
                // TODO: check and set value
                v.value = Check(v.value);
                return null;
            }
        }
        if (v == null) return;
        if (v.checked()) return;
        if (v.checkDepth < 0) {
            // this node is not currently being checked
            v.checkDepth = recursionDepth;
            v.accept(new Visitor());
            v.checkDepth = 0;
            v.checked(true);
        } else if (v.checkDepth != recursionDepth) {
            // this is a legal recursion, just return
        } else {
            IllegalRecursion(v);
        }
    }

    Type TypeOf(Value v) {
        class Visitor implements Value.Visitor<Type> {
            public Type visit(Value.Const v) {
                // TODO: If type null set type to type of expr
                if (v.type == null) v.type = TypeOf(v.expr);
                return v.type;
            }
            public Type visit(Value.Field v) {
                // TODO: If type null set type to type of expr
                if (v.type == null) v.type = TypeOf(v.expr);
                return v.type;
            }
            public Type visit(Value.Variable v) {
                // TODO:
                // If type null:
                // - if expr not null set type to type of expr
                // - else if formal not null set type to type of formal
                // - if type is null: "variable has no type"
                if (v.type == null) {
                    if (v.expr != null) v.type = TypeOf(v.expr);
                    else if (v.formal != null) {
                        v.type = TypeOf(v.formal);
                    }
                }
                if (v.type == null) Error.ID(v.token, "variable has no type");
                return v.type;
            }
            public Type visit(Value.Formal v) { return v.type; }
            public Type visit(Value.Method v) { return v.sig; }
            public Type visit(Value.Procedure v) { return v.sig; }
            public Type visit(Value.Unit v) { return null; }
            public Type visit(Value.Tipe v) { return null; }
        }
        if (v == null) return Type.Err.T;
        if (inTypeOf.contains(v)) {
            IllegalRecursion(v);
            return Type.Err.T;
        }
        inTypeOf.add(v);
        Type x = v.accept(new Visitor());
        inTypeOf.remove(v);
        return x;
    }
    final HashSet<Node> inTypeOf = new HashSet<Node>();

    Expr ToExpr(Value v) {
        class Visitor implements Value.Visitor<Expr> {
            public Expr visit(Value.Field v) { return null; }
            public Expr visit(Value.Formal v) { return null; }
            public Expr visit(Value.Method v) { return null; }
            public Expr visit(Value.Procedure v) { return new Expr.Proc(v); }
            public Expr visit(Value.Variable v) { return null; }
            public Expr visit(Value.Const v) { return v.expr; }
            public Expr visit(Value.Unit v) { return null; }
            public Expr visit(Value.Tipe v) { return null; }
        }
        if (v == null) return null;
        if (inToExpr.contains(v)) {
            IllegalRecursion(v);
            return null;
        }
        inToExpr.add(v);
        Expr x = v.accept(new Visitor());
        inToExpr.remove(v);
        return x;
    }
    final HashSet<Value> inToExpr = new HashSet<Value>();

    Type ToType(Value v) {
        class Visitor implements Value.Visitor<Type> {
            public Type visit(Value.Field v) { return null; }
            public Type visit(Value.Formal v) { return null; }
            public Type visit(Value.Method v) { return null; }
            public Type visit(Value.Procedure v) { return null; }
            public Type visit(Value.Variable v) { return null; }
            public Type visit(Value.Const v) { return null; }
            public Type visit(Value.Unit v) { return null; }
            public Type visit(Value.Tipe v) { return v.value; }
        }
        if (v == null) return null;
        if (inToType.contains(v)) {
            IllegalRecursion(v);
            return null;
        }
        inToType.add(v);
        Type x = v.accept(new Visitor());
        inToType.remove(v);
        return x;
    }
    final HashSet<Value> inToType = new HashSet<Value>();

    /**
     * Report an illegal recursive declaration of a value
     * @param v the value in error
     */
    void IllegalRecursion(Value v) {
        if (v.error()) return;
        Error.ID(v.token, "illegal recursive declaration");
        v.error(true);
    }

    boolean IsExternal(Value v) { return v.external(); }
    boolean IsImported(Value v) { return v.imported(); }
    boolean IsWritable(Value v) { return !v.readonly(); }

    String GlobalName(Value v, boolean dots, boolean withModule) {
        return NameToPrefix(v, !dots, dots, withModule);
    }
    String GlobalName(Value v) {
        return GlobalName(v, true, true);
    }

    /**
     * Casting instance check
     */
    <R> R Is(Value v, Class<R> c) {
        if (c.isInstance(v)) return c.cast(v);
        return null;
    }

    boolean IsEqual(Value.Procedure a, Value.Procedure b) {
        return a == b;
    }

    /**
     * Returns "true" if the two scopes represented by "aa" and "ab"
     * have the same length and for each pair of values "a" and "b",
     * "IsEqual(a, b, x, types)" returns "true".  Otherwise, returns "false".
     */
    boolean IsFieldEqual(Scope aa, Scope bb, Assumption x, boolean types) {
        Iterator<Value> a = Scope.ToList(aa).iterator();
        Iterator<Value> b = Scope.ToList(bb).iterator();
        while (a.hasNext() && b.hasNext())
            if (!IsEqual(Is(a.next(), Value.Field.class), Is(b.next(), Value.Field.class), x, types))
                return false;
        return !a.hasNext() && !b.hasNext();
    }

    /**
     * If "types" is "false", only the surface syntax (name & field index) are
     * checked.  Otherwise, the field types and default values are checked too.
     */
    boolean IsEqual(Value.Field a, Value.Field b, Assumption x, boolean types) {
        if (a == null || b == null || !a.name.equals(b.name)) return false;
        if (!types) return true;
        return IsEqual(TypeOf(a), TypeOf(b), x);
    }

    /**
     * Check that the actuals and formals match in call to a procedure.
     */
    boolean CheckArgs(
            Collection<Expr> actuals,
            Collection<Value> formals,
            Expr.Call ce) {
        Iterator<Expr> aa = actuals.iterator();
        Iterator<Value> ff = formals.iterator();
        boolean ok = true;

        while (aa.hasNext() && ff.hasNext()) {
            Expr e = aa.next();
            Value.Formal formal = Is(ff.next(), Value.Formal.class);
            if (e != null && formal != null) {
                // we've got both a formal and an actual
                Check(e);
                Type te = TypeOf(e);
                Type t = TypeOf(formal);
                switch (formal.mode) {
                case VALUE:
                    if (!IsAssignable(t, te)) {
                        Error.Msg(e.token, "incompatible types");
                        ok = false;
                    }
                    break;
                case VAR:
                    if (!IsDesignator(e)) {
                        Error.Msg(e.token, "VAR actual must be a designator");
                        ok = false;
                    } else if (!IsWritable(e)) {
                        Error.Msg(e.token, "VAR actual must be writable");
                        ok = false;
                    } else if (IsEqual(t, te, null)) {
                        NeedsAddress(e);
                    } else if (Is(t, Type.Array.class) != null && IsAssignable(t, te)) {
                        NeedsAddress(e);
                    } else {
                        Error.Msg(e.token, "incompatible types");
                        ok = false;
                    }
                    break;
                case READONLY:
                    if (!IsAssignable(t, te)) {
                        Error.Msg(e.token, "incompatible types");
                        ok = false;
                    } else if (!IsDesignator(e)) {
                        // we'll make a copy when it's generated
                    } else if (IsEqual(t, te, null)) {
                        NeedsAddress(e);
                    } else {
                        // we'll make a copy when it's generated
                    }
                    break;
                }
            } else if (e == null) {
                Error.Txt(ce.token, formal.token.image, "parameter not specified");
                ok = false;
            } else if (formal == null) {
                Error.Msg(e.token, "too many actual parameters");
                ok = false;
            }
        }
        while (ff.hasNext()) {
            Value.Formal f = Is(ff.next(), Value.Formal.class);
            Error.Txt(ce.token, f.token.image, "parameter not specified");
            ok = false;
        }
        while (aa.hasNext()) {
            Expr e = aa.next();
            Error.Msg(e.token, "too many actual parameters");
            Check(e);
            ok = false;
        }
        return ok;
    }

    /**
     * Returns "true" if the two scopes represented by "aa" and "ab"
     * have the same length and for each pair of values "a" and "b",
     * "IsEqual(a, b, x, types)" returns "true".  Otherwise, returns "false".
     */
    boolean IsMethodEqual(Scope aa, Scope bb, Assumption x, boolean types) {
        Iterator<Value> a = Scope.ToList(aa).iterator();
        Iterator<Value> b = Scope.ToList(bb).iterator();
        while (a.hasNext() && b.hasNext())
            if (!IsEqual(Is(a.next(), Value.Method.class), Is(b.next(), Value.Method.class), x, types))
                return false;
        return !a.hasNext() && !b.hasNext();
    }

    /**
     * If "types" is "false", only the surface syntax (name & method index) are
     * checked.  Otherwise, the method types and default values are checked too.
     */
    boolean IsEqual(Value.Method a, Value.Method b, Assumption x, boolean types) {
        if (a == null || b == null || !a.name.equals(b.name) || a.override != b.override)
            return false;
        if (!types) return true;
        // now we'll do the harder type-based checks
        ResolveDefault(a);
        ResolveDefault(b);
        return IsEqual(a.sig, b.sig, x) && a.value == b.value;
    }
    void ResolveDefault(Value.Method v) {
        if (v.value != null) return;
        if (v.expr == null) return;
        if ((v.value = IsProcedureLiteral(v.expr)) != null) return;
        Type t = TypeOf(v.expr);
        if (IsEqual(t, Type.Null.T, null)) return;
        if (Is(t, Type.Proc.class) == null)
            Error.ID(v.token, "default is not a procedure");
        else
            Error.ID(v.token, "default is not a procedure constant");
    }
    Value.Procedure IsProcedureLiteral(Expr e) {
        e = ConstValue(e);
        if (e == null) return null;
        if (e instanceof Expr.Named)
            return Is(Resolve((Expr.Named)e), Value.Procedure.class);
        if (e instanceof Expr.Qualify)
            return Is(Resolve((Expr.Qualify)e), Value.Procedure.class);
        if (e instanceof Expr.Proc)
            return Is(((Expr.Proc)e).proc, Value.Procedure.class);
        return null;
    }

    void CheckBody(final Value.Procedure p) {
        if (p == null || p.block == null) return;
        ProcBody.Push(p.body);
        p.localScope = Scope.PushNewProc(p.name);
        // create local variables for each of the formals
        for (Value formal: Formals(p.sig)) {
            Value.Formal f = Is(formal, Value.Formal.class);
            Value.Variable v
                = new Value.Variable(f, IsOpenArray(f.type) != null);
            Scope.Insert(v);
            f.scope = v.scope;
        }
        p.checked(true);
        recursionDepth++;
        {
            Check(p.localScope);
            Type result = Check(p.block, true, Result(p.sig), null);
            if (Result(p.sig) != null)
                if (result == null)
                    Error.WarnID(p.token, "function may not return a value");
        }
        recursionDepth--;
        Scope.PopNew();
        ProcBody.Pop();
    }
    boolean IsNested(Value.Procedure t) {
        return t != null && t.body != null && t.body.level != 0;
    }

    /**
     * Check type 't'. Return the underlying constructed type of 't' (i.e.,
     * strip renaming).
     */
    Type Check(Type t) {
        class Visitor implements Type.Visitor<Void> {
            public Void visit(Type.Err t) {
                t.size = 0;
                return null;
            }
            public Void visit(Type.Int t) {
                t.size = wordSize;
                return null;
            }
            public Void visit(Type.Bool t) {
                t.size = wordSize;
                return null;
            }
            public Void visit(Type.Named t) {
                if (t.type == null) Resolve(t);
                int nErrs = Error.nErrors();
                Check(t.value);
                if (nErrs == Error.nErrors())
                    // no errors yet
                    t.type = Check(t.type);
                else
                    t.type = Type.Err.T;
                t.size = t.type.size;
                return null;
            }
            public Void visit(Type.Object t) {
                t.size = wordSize;
                t.methodSize = t.methods.size() * wordSize;
                t.overrideSize = t.overrides.size() * wordSize;

                // TODO: build field and method scopes
                t.fieldScope = Scope.PushNewOpen();
                Scope.Insert(t.fields);
                Scope.PopNew();

                t.methodScope = Scope.PushNewOpen();
                Scope.Insert(t.methods);
                Scope.Insert(t.overrides);
                Scope.PopNew();

                // TODO: check my super type
                // - "illegal recursive super type"
                // - "super type must be an object type"
                if (IsEqual(t.parent, t, null)) {
                    Error.Msg(t.token, "illegal recursive super type");
                }


                if (Is(t.parent, Type.Object.class)==null) {
                    Error.Msg(t.token, "super type must be an object type");
                }

                // TODO: check scopes have non-overlapping symbols
                // - "field and method with the same name"
                for (Value v : t.fields) {
                    if (Scope.LookUp(t.methodScope, v.token.image, true) != null) {
                        Error.Msg(t.token, "field and method with the same name");
                    }
                }

                recursionDepth++;
                {
                    t.checked(true);
                    // TODO: bind method overrides to their original declarations
                    // For each method in this object type's scope:
                    // - if method is not an override:
                    //   - set and increment method offset
                    // - if method is an override:
                    //   - lookup method declaration in parent
                    //   - if found then set overriding method's sig and offset
                    //     to those of overridden method
                    //   - if not found "no method to override in supertype"
                    int mOffset = 0;
                    boolean first = true;
                    for (Value k : Scope.ToList(t.methodScope)) {
                        Value.Method m = (Value.Method) k;
                        // m is not override
                        if (!(m.override)) {
                            if (first) {
                                m.offset = 0;
                                first = false;
                            } else {
                                m.offset = mOffset;
                            }
                            mOffset += t.size;

                        } else {
//
                            Value pmm = Scope.LookUp(m.parent.methodScope, m.expr.token.image, true);
                            //Error.Msg(m.token, "---->" + (pmm instanceof Value.Procedure));
                            Value.Method pm = (Value.Method) pmm;

                            if (pm != null) {
                                m.sig = pm.sig;
                                m.offset = pm.offset;
                            } else {
                                Error.ID(m.token, "no method to override in supertype");
                            }

                        }

                    }
                    // check my fields and methods
                    Check(t.fieldScope);
                    Check(t.methodScope);
                }
                recursionDepth--;
                GetOffsets(t);
                return null;
            }
            public Void visit(Type.Array t) {
                // TODO:
                // - check number
                // - check type of number
                // - if type not Int or Err
                //   "array length must be an integer expression"
                // - check element type (set to result)
                // - if number is not a constant integer n return null
                //   (open array)
                // - if element type is open array
                //   "array element type cannot be an open array"
                // - check n can be encoded in Integer.SIZE bits
                //   "array type has too many elements"
                // - if n > 0
                //   and element size > 0
                //   and n > Integer.MAX_VALUE/element size
                //   "array type is too large"
                Check(t.number);
                if (!IsEqual(TypeOf(t.number),Type.Int.T,null)) {
                    Error.Msg(t.token, "array length must be an integer expression");
                }
                Type et = Check(t.element);
                Expr.Int num;
                if ((num = (Expr.Int) ConstValue(t.number)) == null) return null;
                if (IsOpenArray(t.element) != null) {
                    Error.Msg(t.token, "array element type cannot be an open array");
                }

                if(((num.value)).bitLength() > Integer.SIZE)
                    Error.Msg(t.token, "array type has too many elements");

                if(num.value.compareTo(BigInteger.valueOf(0)) == 1) {
                    int size = et.size;
                    Error.Msg(t.token, "-------->" + et + size);
                    if (size > 0) {
                        if (num.value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE / size)) == 1) {
                            Error.Msg(t.token, "array type is too large");
                        }
                    }

                }
                return null;
            }
            public Void visit(Type.Proc t) {
                // TOOO:
                // - push and set scope
                // - insert formals
                // - pop scope
                // - recursively (inc/dec recursionDepth)
                //   - set size to wordSize
                //   - check result
                //   - if result is open array
                //     "procedures may not return open arrays"
                t.scope = Scope.PushNewProc(null);
                Scope.Insert(t.formals);
                Scope.PopNew();
                recursionDepth++;
                {
                    t.size = wordSize;
                    Type rt = Check(t.result);
                    if (IsOpenArray(rt) != null) {
                        Error.Msg(t.token, "procedures may not return open arrays");
                    }
                }

                recursionDepth--;


                return null;
            }
            public Void visit(Type.Record t) {
                // TODO:
                // - push and set scope
                // - insert fields
                // - pop scope
                // - for each field
                //   - check
                //   - set offset
                //   - add field type size to record size
                // - check size can be encoded in Integer.SIZE bits:
                //   "record type is too large"
                // - set record size
                t.fieldScope = Scope.PushNewClosed();
                Scope.Insert(t.fields);
                Scope.PopNew();

                int recordSize = t.recSize;
                boolean first = true;
                int total_offsets = 0;
                for (Value.Field f : t.fields) {
                    Check(f);
                    if (first) {
                        f.offset = 0;
                        first = false;
                    } else {
                        f.offset = total_offsets;
                    }
                    total_offsets += f.type.size;
                    recordSize += f.type.size;
                }

                if (BigInteger.valueOf(recordSize).bitLength() > Integer.SIZE) {
                    Error.Msg(t.token, "record type is too large");
                } else {
                    t.size = recordSize;
                }
                return null;
            }
            public Void visit(Type.Ref t) {
                t.size = wordSize;
                t.checked(true);
                recursionDepth++;
                {
                    // TODO: Check and set target (recursively)
                    Type tt =  Check(t.target);
                    t.target = tt;
                }
                recursionDepth--;
                return null;
            }
        }
        if (t == null) return Type.Err.T;
        if (!t.checked()) {
            if (t.checkDepth == recursionDepth) {
                IllegalRecursion(t);
            } else {
                int saveDepth = t.checkDepth;
                t.checkDepth = recursionDepth;
                t.accept(new Visitor());
                t.checkDepth = saveDepth;
                t.checked(true);
            }
        }
        if (t instanceof Type.Named) t = Strip(t);
        return t;
    }
    int recursionDepth = 0;
    // incremented/decremented every time the type checker enters/leaves one of
    // the types that's allowed to introduce recursions

    /**
     * Return the constructed type of 't' (i.e., strip renaming).
     */
    Type Strip(Type t) {
        Type u = t, v = t;
        if (u == null) return null;
        for (;;) {
            if (!(u instanceof Type.Named)) return u;
            u = Strip((Type.Named)u);
            if (!(v instanceof Type.Named)) return v;
            v = Strip((Type.Named)v);
            if (!(v instanceof Type.Named)) return v;
            v = Strip((Type.Named)v);
            if (u == v) {
                IllegalRecursion(t);
                return Type.Err.T;
            }
        }
    }
    Type Strip(Type.Named t) {
        if (t.type == null) Resolve(t);
        return t.type;
    }
    void Resolve(Type.Named p) {
        if (p.type != null) return;
        if ((p.value = Scope.LookUp(Scope.Top(), p.name, false)) == null) {
            Error.Txt(p.token, p.name, "undefined");
            p.type = Type.Err.T;
        } else if ((p.type = ToType(p.value)) != null) {
            // ok
        } else {
            Error.Txt(p.token, p.name, "name isn't bound to a type");
            p.type = Type.Err.T;
        }
    }

    void IllegalRecursion(Type t) {
        if (t.errored()) return;
        if (t instanceof Type.Named) {
            Type.Named n = (Type.Named)t;
            if (n.value != null)
                IllegalRecursion(n.value);
            else
                Error.Txt(n.token, n.name, "illegal recursive type declaration");
        } else
            Error.Msg(t.token, "illegal recursive type decalaration");
        t.errored(true);
    }

    /**
     * Return the base type of 't' (strip renaming, packing & subranges)
     */
    Type Base(Type t) {
        return Strip(t); // for now, no packing or subranges
    }

    /**
     * Used to terminate recursion in type equivalence.
     */
    class Assumption {
        final Assumption prev;
        final Type a, b;

        Assumption(Assumption prev, Type a, Type b) {
            this.prev = prev;
            this.a = a;
            this.b = b;
        }
    }

    /**
     * Return true iff (a == b) given the assumptions x.
     */
    boolean IsEqual(Type a, Type b, Assumption x) {
        class Visitor implements Type.Visitor<Boolean> {
            final Assumption xx;
            Visitor(Assumption xx) {
                this.xx = xx;
            }
            public Boolean visit(Type.Err a) {
                assert a == xx.a;
                assert a != xx.b;
                return false;
            }
            public Boolean visit(Type.Int a) {
                assert a == xx.a;
                assert a != xx.b;
                return false;
            }
            public Boolean visit(Type.Bool a) {
                assert a == xx.a;
                assert a != xx.b;
                return false;
            }
            public Boolean visit(Type.Named a) {
                assert a == xx.a;
                assert a != xx.b;
                return false;
            }
            public Boolean visit(Type.Object a) {
                assert a == xx.a;
                Type.Object b = (Type.Object)xx.b;

                // check the field names and offsets
                if (!IsFieldEqual(a.fieldScope, b.fieldScope, xx, false))
                    return false;

                // check the method names and offsets
                if (!IsMethodEqual(a.methodScope, b.methodScope, xx, false))
                    return false;

                // check the super types
                if (!IsEqual(a.parent, b.parent, xx))
                    return false;

                // check the field types and default values
                if (!IsFieldEqual(a.fieldScope, b.fieldScope, xx, true))
                    return false;

                // check the method types and defaults
                if (!IsMethodEqual(a.methodScope, b.methodScope, xx, true))
                    return false;

                return true;
            }
            public Boolean visit(Type.Array a) {
                assert a == xx.a;
                Type.Array b = (Type.Array)xx.b;
                if (!IsEqual(a.element, b.element, xx)) return false;
                if (OpenDepth(a) != OpenDepth(b)) return false;
                return IsEqual(ConstValue(a.number), ConstValue(b.number), xx);
            }
            public Boolean visit(Type.Proc a) {
                assert a == xx.a;
                Type.Proc b = (Type.Proc)xx.b;
                if (a.result == null && b.result == null) {
                    // ok
                } else if (!IsEqual(a.result, b.result, xx))
                    return false;
                return FormalsMatch(a.scope, b.scope, true, true, xx);
            }
            public Boolean visit(Type.Record a) {
                assert a == xx.a;
                Type.Record b = (Type.Record)xx.b;

                // check the field names and offsets
                if (!IsFieldEqual(a.fieldScope, b.fieldScope, xx, false)) return false;

                // check the field types and default values
                if (!IsFieldEqual(a.fieldScope, b.fieldScope, xx, true)) return false;

                return true;
            }
            public Boolean visit(Type.Ref a) {
                Type.Ref b = (Type.Ref) xx.b;
                if (a.target == null && b.target == null)
                    return a == b;
                return IsEqual(a.target, b.target, xx);
            }
        }
        if (a == null || b == null) return false;
        if (a == b) return true;
        if (a instanceof Type.Named) a = Strip(a);
        if (b instanceof Type.Named) b = Strip(b);
        if (a == b) return true;
        if (a == Type.Err.T || b == Type.Err.T) return true;
        if (a.getClass() != b.getClass()) return false;
        for (Assumption y = x; y != null; y = y.prev) {
            if (y.a == a) {
                if (y.b == b) return true;
            } else if (y.a == b) {
                if (y.b == a) return true;
            }
        }
        return a.accept(new Visitor(new Assumption(x, a, b)));
    }

    /**
     * Return true iff (value(a) == value(b)) assuming a constant global store
     * and the type equalities represented by 'x'.
     */
    boolean IsEqual(final Expr a, final Expr b, final Assumption x) {
        class Visitor implements Expr.Visitor<Boolean> {
            public Boolean visit(Expr.Add a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.And a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Div a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Mod a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Mul a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Or  a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Sub a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Subscript  a) {
                return b instanceof Expr.Subscript
                        && IsEqual(a.expr, ((Expr.Subscript)b).expr, x)
                        && IsEqual(a.index, ((Expr.Subscript)b).index, x);
            }
            public Boolean visit(Expr.Address a) {
                return b instanceof Expr.Address
                        && a.value.equals(((Expr.Address)b).value);
            }
            public Boolean visit(Expr.Bool a) {
                return b instanceof Expr.Bool
                        && a.value == ((Expr.Bool)b).value;
            }
            public Boolean visit(Expr.Int a) {
                return b instanceof Expr.Int
                        && a.value.equals(((Expr.Int)b).value);
            }
            public Boolean visit(Expr.Call e) { return false; }
            public Boolean visit(Expr.Check a) {
                return b instanceof Expr.Check
                        && a.min.equals(((Expr.Check)b).min)
                        && a.max.equals(((Expr.Check)b).max)
                        && IsEqual(a.expr, ((Expr.Check)b).expr, x);
            }
            public Boolean visit(Expr.Deref a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Neg a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Not a) { return EqCheck(a, b, x); }
            public Boolean visit(Expr.Equal a) {
                return b instanceof Expr.Equal
                        && a.op == ((Expr.Equal)b).op
                        && IsEqual(a.left, ((Expr.Equal)b).left, x)
                        && IsEqual(a.right, ((Expr.Equal)b).right, x);
            }
            public Boolean visit(Expr.Compare a) {
                return b instanceof Expr.Compare
                        && a.op == ((Expr.Compare)b).op
                        && IsEqual(a.left, ((Expr.Compare)b).left, x)
                        && IsEqual(a.left, ((Expr.Compare)b).left, x);
            }
            public Boolean visit(Expr.Method a) {
                return b instanceof Expr.Method
                        && a.name.equals(((Expr.Method)b).name)
                        && IsEqual(a.object, ((Expr.Method)b).object, x);
            }
            public Boolean visit(Expr.Named a) {
                if (!(b instanceof Expr.Named)) return false;
                return Resolve(a) == Resolve((Expr.Named)b);
            }
            public Boolean visit(Expr.Pos a) {
                return b instanceof Expr.Pos
                        && IsEqual(a.expr, ((Expr.Pos)b).expr, x)
                        || IsEqual(a.expr, b, x);
            }
            public Boolean visit(Expr.Proc a) {
                return b instanceof Expr.Proc
                    && IsEqual(Is(a.proc, Value.Procedure.class),
                               Is(((Expr.Proc)b).proc, Value.Procedure.class));
            }
            public Boolean visit(Expr.Qualify a) {
                if (!(b instanceof Expr.Qualify)) return false;
                Value va = Resolve(a); Value vb = Resolve((Expr.Qualify)b);
                return va == vb
                        && TypeOf(a) == TypeOf(b)
                        && IsEqual(a.expr, ((Expr.Qualify)b).expr, x);
            }
            public Boolean visit(Expr.Text a) {
                return b instanceof Expr.Text
                        && Objects.equals(a.value, ((Expr.Text) b).value);
            }
            public Boolean visit(Expr.TypeExpr a) {
                return b instanceof Expr.TypeExpr
                        && IsEqual(a.value, ((Expr.TypeExpr)b).value, x);
            }
        }
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.accept(new Visitor());
    }
    Boolean EqCheck(Expr.Unary a, Expr b, Assumption x) {
        return b instanceof Expr.Unary
                && a.getClass() == b.getClass()
                && IsEqual(a.expr, ((Expr.Unary)b).expr, x);
    }
    Boolean EqCheck(Expr.Binary a, Expr b, Assumption x) {
        return b instanceof Expr.Binary
                && a.getClass() == b.getClass()
                && IsEqual(a.left, ((Expr.Binary)b).left, x)
                && IsEqual(a.right, ((Expr.Binary)b).right, x);
    }

    /**
     * Returns true iff (a <: b).
     */
    boolean IsSubtype(Type a, Type b) {
        class Visitor implements Type.Visitor<Boolean> {
            final Type b;
            Visitor(Type b) {
                this.b = b;
            }
            public Boolean visit(Type.Object a) {
                // TODO: Return true if a <: b
                return IsEqual(b, Type.Root.T, null);
            }
            public Boolean visit(Type.Array a) {
                Type ta = a, tb = b;

                // TODO: An array type A is a subtype of an array type
                // B if they have the same ultimate element type, the
                // same number of dimensions, and, for each dimension,
                // either both are open (for the first m), or A is
                // fixed and B is open (for the next n dimensions),
                // or they are both fixed and have the same size (for
                // the last p dimensions).
                if (!(b instanceof Type.Array)) return false;
                Type.Array array_b = Is(this.b, Type.Array.class);
                if (a.number == null && array_b.number != null) {
                    return false;
                } else if (a.number != null && array_b.number == null) {
                    //ok
                } else if (a.number != null && array_b.number != null) {
                    if (!IsEqual(a.number, array_b.number, null)) {
                        return false;
                    }
                }


                return IsEqual(ta, tb, null);
            }
            public Boolean visit(Type.Proc a) {
                Type.Proc b = Is(this.b, Type.Proc.class);
                if (b == null) return false;
                if (a.result == null && b.result == null) {
                    // ok
                } else if (!IsEqual(a.result, b.result, null)) return false;
                return FormalsMatch(a.scope, b.scope, false, true, null);
            }
            public Boolean visit(Type.Ref a) {
                // TODO:
                // Null <: ^T <: Refany
                // That is, Refany contains all references,
                // respectively, and nil is a member of every
                // reference type.
                if (IsEqual(a,b,null)) return true;
                if (IsSubtype(b, Type.Refany.T)) return true;

//                if (a != null && b != null) {
//                    if (IsEqual(b, Type.Null.T, null)) return false;
//                    else if (b instanceof Type.Ref) {
//                        return true;
//                    } else if (IsEqual(b, Type.Refany.T, null)) {
//                        return true;
//                    }
//                }


                return IsEqual(a, b, null);
            }
            public Boolean visit(Type.Err a) { return false; }
            public Boolean visit(Type.Int a) { return false; }
            public Boolean visit(Type.Bool a) { return false; }
            public Boolean visit(Type.Named a) { return false; }
            public Boolean visit(Type.Record a) { return false; }
        }
        if (a == null || b == null) return false;
        if (a == b) return true;
        if (a instanceof Type.Named) a = Strip(a);
        if (b instanceof Type.Named) b = Strip(b);
        if (a == b) return true;
        if (a == Type.Err.T || b == Type.Err.T) return true;
        if (IsEqual(a, b, null)) return true;
        // I give up, call the methods.
        return a.accept(new Visitor(b));
    }

    /**
     * Returns true iff (a := b) type-checks.
     */
    boolean IsAssignable(Type a, Type b) {
        return IsEqual(a, b, null) || IsSubtype(b, a);
    }

    /**
     * Returns true if the type 't' is an ordinal (integer, enumeration, subrange)
     */
    boolean IsOrdinal(Type t) {
        Type u = Check(t);
        return u == Type.Err.T || u == Type.Int.T || u == Type.Bool.T;
    }

    /**
     * @return the number of values of the type 't'
     */
    BigInteger Number(Type t) {
        class Visitor implements Type.Visitor<BigInteger> {
            public BigInteger visit(Type.Bool t) { return BigInteger.TWO; }
            public BigInteger visit(Type.Err t) { return BigInteger.ZERO; }
            public BigInteger visit(Type.Int t) { return MAX_VALUE.subtract(MIN_VALUE).add(BigInteger.ONE); }
            public BigInteger visit(Type.Named t) { assert false; return null; }
            public BigInteger visit(Type.Object t) { return null; }
            public BigInteger visit(Type.Array t) { return null; }
            public BigInteger visit(Type.Proc t) { return null; }
            public BigInteger visit(Type.Record t) { return null; }
            public BigInteger visit(Type.Ref t) { return null; }
        }
        Type u = Check(t);
        return u.accept(new Visitor());
    }

    BigInteger Min(Type t) {
        Type u = Check(t);
        if (u == Type.Bool.T) return BigInteger.ZERO;
        if (u == Type.Int.T) return MIN_VALUE;
        return BigInteger.ZERO;
    }
    BigInteger Max(Type t) {
        Type u = Check(t);
        if (u == Type.Bool.T) return BigInteger.ONE;
        if (u == Type.Int.T) return MAX_VALUE;
        return BigInteger.ONE.negate();
    }

    int count = 0;
    String GlobalUID(Type t) {
        if (t == null) return null;
        if (t.tipe != null) return GlobalName(t.tipe, true, true);
        if (t.uid == null) t.uid = "_T" + count++;
        return t.uid;
    }

    /**
     * Returns true iff the type 't' is a record, set, or array.
     */
    boolean IsStructured(Type t) {
        class Visitor implements Type.Visitor<Boolean> {
            public Boolean visit(Type.Bool t) { return false; }
            public Boolean visit(Type.Err t) { return false; }
            public Boolean visit(Type.Int t) { return false; }
            public Boolean visit(Type.Named t) { return null; }
            public Boolean visit(Type.Object t) { return false; }
            public Boolean visit(Type.Array t) { return true; }
            public Boolean visit(Type.Proc t) { return false; }
            public Boolean visit(Type.Record t) { return true; }
            public Boolean visit(Type.Ref t) { return false; }
        }
        if (t == null) return false;
        return t.accept(new Visitor());
    }

    <R extends Type> R Reduce(Type t, Class<R> c) {
        if (t == null) return null;
        if (t instanceof Type.Named) t = Strip(t);
        if (c.isInstance(t)) return c.cast(t);
        return null;
    }
    <R extends Type> R Is(Type t, Class<R> c) {
        return Reduce(t, c);
    }

    Type.Array IsOpenArray(Type t) {
        Type.Array a = Reduce(t, Type.Array.class);
        if (a == null || ConstValue(a.number) != null) return null;
        return a;
    }
    int OpenDepth(Type t) {
        Type.Array a = Reduce(t, Type.Array.class);
        if (a == null || ConstValue(a.number) != null ) return 0;
        return 1 + OpenDepth(a.element);
    }
    Type OpenType(Type t) {
        Type.Array a = Reduce(t, Type.Array.class);
        if (a == null || ConstValue(a.number) != null) return t;
        return OpenType(a.element);
    }

    Value LookUp(Type.Record p, String name) {
        if (p == null) return null;
        return Scope.LookUp(p.fieldScope, name, true);
    }

    /*
     * Look up field or method with name in object type p.
     * Return field/method and set visible[0] to the type in which found.
     */
    Value LookUp(Type.Object p, String name, Type visible[]) {
        Type t = p;
        for (;;) {
            t = Check(t);
            if (t == Type.Err.T) {
                if (visible != null) visible[0] = Type.Err.T;
                return null;
            }
            if ((p = Is(t, Type.Object.class)) != null) {
                // found an object type => try it!
                Value v = Scope.LookUp(p.methodScope, name, true);
                if (v != null) {
                    // find the first non-override declaration for this method
                    p = PrimaryMethodDeclaration(p, v);
                    if (p == null) return null;
                } else {
                    // try for a field
                    v = Scope.LookUp(p.fieldScope, name, true);
                }
                if (v != null) {
                    if (visible != null) visible[0] = p;
                    return v;
                }
                t = p.parent;
            } else return null;
        }
    }

    Type.Object PrimaryMethodDeclaration(Type.Object p, Value v) {
        if (p == null) return null;
        Value.Method method = Is(v, Value.Method.class);
        if (!method.override) return p;
        if (inPrimLookUp.contains(p)) {
            Error.Msg(p.token, "illegal recursive supertype");
            return null;
        }
        inPrimLookUp.add(p);
        Type.Object[] visible = {null};
        v = LookUp(Is(p.parent, Type.Object.class), method.name, visible);
        inPrimLookUp.remove(p);
        if (v != null) return visible[0];
        return null;
    }
    final HashSet<Type.Object> inPrimLookUp = new HashSet<Type.Object>();

    void GetSizes(Type.Object t) {
        if (t.fieldSize >= 0) return;
        if (t.parent == null)
            t.fieldSize = 0;
        else {
            BigInteger size = BigInteger.ZERO;
            for (Value v: Scope.ToList(t.fieldScope)) {
                Value.Field f = Is(v, Value.Field.class);
                Type type = Check(f.type);
                f.offset = size.intValue();
                size = size.add(BigInteger.valueOf(type.size));
            }
            if (size.bitLength() > Integer.SIZE) {
                Error.Msg(t.token, "object type is too large");
                t.fieldSize = 0;
            } else t.fieldSize = size.intValue();
        }
    }

    void GetOffsets(Type.Object t) {
        if (t.fieldOffset >= 0) return;
        GetSizes(t);
        if (t.parent == null) {
            t.fieldOffset = 0;
            t.methodOffset = 0;
        } else {
            Type.Object parent = Is(t.parent, Type.Object.class);
            GetOffsets(parent);
            t.fieldOffset = parent.fieldOffset + parent.fieldSize;
            t.methodOffset = parent.methodOffset + parent.methodSize;
        }
    }

    /**
     * Convert a method signature into a procedure signature
     * @param method the method signature to convert
     * @param objType the object type
     * @return the resulting procedure type
     */
    Type MethodSigAsProcSig(Type.Proc method, Type objType) {
        if (method == null) return Type.Err.T;
        Type.Proc proc = new Type.Proc.User(method.token, null, method.result);
        // insert the "self" formal
        proc.formals.add(new Value.Formal(ID("_self_"), Value.Formal.Mode.VALUE, objType));
        // copy the remaining formals
        for (Value formal: Formals(method)) {
            Value.Formal f = (Value.Formal)formal;
            proc.formals.add(new Value.Formal(f.token, f.mode, f.type));
        }
        return proc;
    }

    boolean IsCompatible(Type.Proc proc, Type objectType, Type.Proc method) {
        if (proc == null || method == null) return false;
        if (Formals(proc).size() != Formals(method).size() + 1)
            return false;
        if (proc.result == null && method.result == null) {
            // ok
        } else if (!IsEqual(proc.result, method.result, null))
            return false;
        if (!FirstArgOK(proc.scope, objectType)) return false;
        if (!FormalsMatch(proc.scope, method.scope, false, false, null)) return false;
        return true;
    }
    boolean FirstArgOK(Scope s, Type t) {
        for (Value v: Scope.ToList(s)) {
            Value.Formal formal = Is(v, Value.Formal.class);
            switch (formal.mode) {
            case VALUE:
                return IsSubtype(t, formal.type);
            default:
                return false;
            }
        }
        return false;
    }
    boolean FormalsMatch(Scope a, Scope b, boolean strict, boolean useFirst, Assumption x) {
        Iterator<Value> va = Scope.ToList(a).iterator();
        Iterator<Value> vb = Scope.ToList(b).iterator();
        if (!useFirst) {
            if (!va.hasNext()) return false;
            va.next();
        }
        while (va.hasNext() && vb.hasNext()) {
            Value.Formal ia = Is(va.next(), Value.Formal.class);
            Value.Formal ib = Is(vb.next(), Value.Formal.class);
            if (ia.mode != ib.mode) return false;
            if (!IsEqual(TypeOf(ia), TypeOf(ib), x)) return false;
            if (strict)
                if (!ia.name.equals(ib.name)) return false;
        }
        return (!va.hasNext() && !vb.hasNext());
    }
    Type Result(Type.Proc p) {
        if (p == null) return Type.Err.T;
        return p.result;
    }
    Collection<Value> Formals(Type.Proc p) {
        if (p == null) return Collections.emptyList();
        return Scope.ToList(p.scope);
    }

    /**
     * Type-check statements.
     */
    Type Check(Stmt stmt, boolean returnOK, Type returnType, Stmt loop) {
        class Visitor implements Stmt.Visitor<Type> {
            public Type visit(Stmt.Assign s) {
                Check(s.lhs);
                Check(s.rhs);
                Type tlhs = TypeOf(s.lhs);
                if (!IsDesignator(s.lhs))
                    Error.Msg(s.token, "left-hand side is not a designator");
                else if (!IsWritable(s.lhs))
                    Error.Msg(s.token, "left-hand side is read-only");
                CheckAssign(s.token, tlhs, s.rhs);
                return null;
            }
            public Type visit(Stmt.Call s) {
                Check(s.expr);
                return null;
            }
            public Type visit(Stmt.Break s) {
                if (loop == null)
                    Error.Msg(s.token, "break not contained in a loop");
                return null;
            }
            public Type visit(Stmt.For s) {
                Check(s.from);
                Check(s.to);
                Type tFrom = Base(TypeOf(s.from));
                Type tTo = Base(TypeOf(s.to));
                if (tFrom == Type.Err.T || tTo == Type.Err.T) {
                    //  already an error
                    tFrom = Type.Err.T;
                    tTo = Type.Err.T;
                } else if (tFrom == Type.Int.T && tTo == Type.Int.T) {
                    // ok
                } else
                    Error.Msg(s.token, "'from' and 'to' expressions must be compatible ordinals");
                Check(s.by);
                if (!IsSubtype(TypeOf(s.by), Type.Int.T))
                    Error.Msg(s.token, "'by' expression must be an integer");
                // set the type of the control variable
                s.var = new Value.Variable(s, tFrom);
                s.scope = Scope.PushNewOpen();
                Scope.Insert(s.var);
                Check(s.var);
                Check(s.stmt, returnOK, returnType, s);
                Scope.PopNew();
                return null;
            }
            public Type visit(Stmt.If s) {
                // TODO:
                // - check expr
                // - if expr not boolean: "if condition must be a boolean"
                // - check block
                // - check statement
                // - return null if ok Err.T otherwise
                Check(s.expr);
                Type et = TypeOf(s.expr);
                if (!(et instanceof Type.Bool)) {
                    Error.Msg(s.token, "if condition must be a boolean");
                    return Type.Err.T;
                }
                Check(s.block, returnOK, returnType, null);

                return null;
            }
            public Type visit(Stmt.Loop s) {
                // TODO:
                // - check whileExpr
                // - must be boolean: "loop while condition must be a boolean"
                // - check block
                // - check untilExpr: "loop until condition must be a boolean"
                Check(s.whileExpr);
                Type wt = TypeOf(s.whileExpr);
                if(!(wt instanceof Type.Bool)) {
                    Error.Msg(s.token, "loop while condition must be a boolean");
                }

                Check(s.block, returnOK, returnType, null);

                Check(s.untilExpr);
                Type ut = TypeOf(s.untilExpr);
                if(!(ut instanceof Type.Bool)) {
                    Error.Msg(s.token, "loop until condition must be a boolean");
                }
                return null;
            }
            public Type visit(Stmt.Return s) {
                Check(s.expr);
                Type t = TypeOf(s.expr);
                if (!returnOK) {
                    Error.Msg(s.token, "return not in a procedure");
                    return t;
                }
                Type result = returnType;
                if (s.expr == null) {
                    if (result != null) {
                        Error.Msg(s.token, "missing return result");
                        t = Type.Err.T;
                    }
                } else if (result == null) {
                    Error.Msg(s.token, "procedure does not have a return result");
                    t = null;
                } else {
                    CheckAssign(s.token, result, s.expr);
                    return result;
                }
                return t;
            }
            public Type visit(Stmt.Block s) {
                // TODO:
                // - push and build scope from decls
                // - check scope
                // - check stmts
                // - pop scope
                // - return type from checking block
                s.scope = Scope.PushNewClosed();
                Scope.Insert(s.decls);
                Check(s.scope);
                Type tt = null;
                for (Stmt t : s.body) {
                   tt = Check(t, returnOK, returnType, null);
                }
                Scope.PopNew();


                return tt;
            }
        }
        return stmt.accept(new Visitor());
    }

    /**
     * Check that 'rhs' is assignable to 'tlhs'.
     */
    void CheckAssign(Token token, Type tlhs, Expr rhs) {
        Type t = Base(tlhs);
        Type trhs = TypeOf(rhs);
        tlhs = Check(tlhs);
        t = Check(t);
        Check(rhs);
        if (!IsAssignable(tlhs, trhs)) {
            if (tlhs != Type.Err.T && trhs != Type.Err.T)
                Error.Msg(token, "types are not assignable");
        } else if (IsOrdinal(tlhs)) {
            CheckOrdinal(token, tlhs, rhs);
        } else if (tlhs instanceof Type.Ref || tlhs instanceof Type.Object) {
            // ok
        } else if (tlhs instanceof Type.Proc) {
            CheckProcedure(token, rhs);
        } else {
            // ok
        }
    }
    void CheckOrdinal(Token token, Type tlhs, Expr rhs) {
        // ok, but must generate a check
        Expr constant = ConstValue(rhs);
        if (constant != null) rhs = constant;
        Bounds r = GetBounds(rhs);
        BigInteger lmin = Min(tlhs), lmax = Max(tlhs);
        if (lmin.compareTo(lmax) <= 0 && r.min.compareTo(r.max) <= 0
                && (r.min.compareTo(lmax) > 0 || r.max.compareTo(lmin) < 0))
            // non-overlapping, non-empty ranges
            Error.Warn(token, "value not assignable (range fault)");
    }
    void CheckProcedure(Token token, Expr rhs) {
        if (NeedsClosureCheck(token, rhs, true)) {
            // may generate a more detailed message
        }
    }
    boolean NeedsClosureCheck(Token token, Expr proc, boolean errors) {
        Value v;
        if (proc instanceof Expr.Named)
            v = Resolve((Expr.Named)proc);
        else if (proc instanceof Expr.Qualify)
            v = Resolve((Expr.Qualify)proc);
        else if (proc instanceof Expr.Proc)
            v = ((Expr.Proc)proc).proc;
        else
            // non-constant, non-variable => ok
            return false;
        if (IsNested(Is(v, Value.Procedure.class))) {
            if (errors)
                Error.Msg(token, "cannot assign nested procedures");
            return false;
        } else if (HasClosure(Is(v, Value.Variable.class))) {
            return true;
        } else // non-formal, non-const => no check
            return false;
    }
    boolean HasClosure(Value.Variable v) {
        return v != null && v.formal != null && HasClosure(v.formal);
    }
    boolean HasClosure(Value.Formal v) {
        return v != null && v.mode != Value.Formal.Mode.VAR
                && Is(Base(TypeOf(v)), Type.Proc.class) != null;
    }

    /**
     * Return the type of an expression 'e'
     * @param e the expression
     * @return its type or null if it has no type
     */
    Type TypeOf(Expr e) {
        class Visitor implements Expr.Visitor<Type> {
            public Type visit(Expr.Add e) {
                Type t = Check(TypeOf(e.left));
                if (IsSubtype(t, Type.Addr.T)) t = Type.Addr.T;
                return Base(t);
            }
            public Type visit(Expr.Address e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.And e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Call ce) {
                Resolve(ce);
                if (ce.procType == null)
                    return Type.Err.T;
                else if (ce.procType.fixedType != null)
                    return ce.procType.fixedType;
                else {
                    FixArgs(ce);
                    return ce.procType.accept(new Type.Proc.Visitor<Type>() {
                        public Type visit(Type.Proc.User p) {
                            Type t = TypeOf(ce.proc);
                            if (t == Type.Err.T) return t;
                            if (t == null) t = MethodType(Is(ce.proc, Expr.Qualify.class));
                            return Result(Is(t, Type.Proc.class));
                        }
                        public Type visit(Type.Proc.First p) {
                            Type index;
                            Type t = TypeOf(ce.args[0]);
                            Type.Array a = Is(t, Type.Array.class);
                            if (a != null) {
                                index = ConstValue(a.number) == null ? Type.Int.T : TypeOf(a.number);
                            } else if ((t = IsType(ce.args[0])) != null) {
                                a = Is(t, Type.Array.class);
                                if (a != null) {
                                    index = ConstValue(a.number) == null ? Type.Int.T : TypeOf(a.number);
                                } else index = t;
                            } else index = Type.Int.T;
                            return Base(index);
                        }
                        public Type visit(Type.Proc.Last p) {
                            return visit(Type.Proc.First.T);
                        }
                        public Type visit(Type.Proc.Number p) {
                            assert p.fixedType == Type.Int.T;
                            return p.fixedType;
                        }
                        public Type visit(Type.Proc.New p) {
                            Type t;
                            if ((t = IsType(ce.args[0])) == null) {
                                t = Type.Null.T;
                            } else if (Is(t, Type.Ref.class) != null) {
                                // ok
                            } else if (Is(t, Type.Object.class) != null) {
                                // sleazy bug!! ignore method overrides
                            } else t = Type.Null.T;
                            return t;
                        }
                    });
                }
            }
            public Type visit(Expr.Check e) { return TypeOf(e.expr); }
            public Type visit(Expr.Deref e) {
                Type t = TypeOf(e.expr);
                Type.Ref ref = Is(t, Type.Ref.class);
                if (ref != null) return ref.target;
                return Type.Err.T;
            }
            public Type visit(Expr.Div e) {
                return Base(TypeOf(e.left));
            }
            public Type visit(Expr.Bool e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Equal e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Compare e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Neg e) {
                return Base(TypeOf(e.expr));
            }
            public Type visit(Expr.Method e) {
                Value.Method m = (Value.Method)e.method;
                return MethodSigAsProcSig(m.sig, e.object);
            }
            public Type visit(Expr.Mod e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Mul e) {
                return TypeOf(e.left);
            }
            public Type visit(Expr.Named e) {
                Value v = Resolve(e);
                if (inTypeOf.contains(e)) {
                    IllegalRecursion(v);
                    return Type.Err.T;
                }
                inTypeOf.add(e);
                Type t = TypeOf(v);
                inTypeOf.remove(e);
                return t;
            }
            public Type visit(Expr.Not e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Int e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Or e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.Pos e) {
                return Base(TypeOf(e.expr));
            }
            public Type visit(Expr.Proc e) {
                return TypeOf(e.proc);
            }
            public Type visit(Expr.Qualify e) {
                Value v = Resolve(e);
                if (inTypeOf.contains(e)) {
                    IllegalRecursion(v);
                    return e.type = Type.Err.T;
                }
                inTypeOf.add(e);
                Type t = TypeOf(v);
                if (e.objType != null)
                    t = MethodSigAsProcSig(Is(t, Type.Proc.class), e.objType);
                else if (Is(v, Value.Method.class) != null)
                    t = null;
                inTypeOf.remove(e);
                return t;
            }
            public Type visit(Expr.Subscript e) {
                Type t = Base(TypeOf(e.expr));
                if (Is(t, Type.Ref.class) != null) {
                    // auto-magic dereference
                    e.expr = new Expr.Deref(e.token, e.expr);
                    t = Base(TypeOf(e.expr));
                }
                Type.Array a = Is(t, Type.Array.class);
                if (a != null) return a.element;
                return t;
            }
            public Type visit(Expr.Sub e) {
                Type t = Base(TypeOf(e.left));
                if (IsSubtype(t, Type.Addr.T)
                    && IsSubtype(Base(TypeOf(e.right)), Type.Addr.T))
                    t = Type.Int.T;
                return t;
            }
            public Type visit(Expr.Text e) {
                assert e.type != null;
                return e.type;
            }
            public Type visit(Expr.TypeExpr e) {
                return null;
            }
        }
        if (e == null) return Type.Err.T;
        if (e.type != null) return e.type;
        return e.type = e.accept(new Visitor());
    }

    /**
     * Type-check an expression 'e'
     * @param e the expression to check
     */
    void Check(Expr e) {
        class Visitor implements Expr.Visitor<Void> {
            public Void visit(Expr.Add e) {
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsEqual(lt,rt,null)) {
                    BadOperands(e,lt,rt);
                } else if (!IsSubtype(lt, Type.Int.T)) {
                    BadOperands(e,lt,rt);
                }
                return null;
            }
            public Void visit(Expr.Address e) { assert e.type != null; return null; }
            public Void visit(Expr.And e) {
                assert e.type != null;
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsEqual(lt,rt,null)) {
                    BadOperands(e,lt,rt);
                } else if (!IsSubtype(lt, Type.Bool.T)) {
                    BadOperands(e,lt,rt);
                }
                return null;
            }
            public Void visit(Expr.Call ce) {
                // check the procedure
                int nErrs0 = Error.nErrors();
                Check(ce.proc);
                Resolve(ce);
                int nErrs1 = Error.nErrors();
                if (ce.procType == null) {
                    if (nErrs0 == nErrs1 && TypeOf(ce.proc) != Type.Err.T)
                        Error.Msg(ce.token, "attempting to call a non-procedure");
                    ce.type = Type.Err.T;
                }
                // check its args
                for (Expr arg : ce.args) {
                    Check(arg);
                    if (TypeOf(arg) == Type.Err.T)
                        ce.type = Type.Err.T;
                }
                // finally do the procedure specific checking
                if (ce.type != Type.Err.T && ce.procType != null) {
                    FixArgs(ce);
                    ce.procType.accept(new Type.Proc.Visitor<Void>() {
                        public Void visit(Type.Proc.User __) {
                            Type t = TypeOf(ce.proc);
                            if (t == null) t = MethodType(Is(ce.proc, Expr.Qualify.class));
                            Type.Proc proc = Is(t, Type.Proc.class);
                            CheckArgs(Arrays.asList(ce.args), Formals(proc), ce);
                            ce.type = Result(proc);
                            return null;
                        }
                        public Void visit(Type.Proc.First __) {
                            Expr e = ce.args[0];
                            Type t = TypeOf(e);
                            Type index;
                            Type.Array a = Is(t, Type.Array.class);
                            if (a != null) {
                                index = ConstValue(a.number) == null ? Type.Int.T : TypeOf(a.number);
                            } else if ((t = IsType(e)) != null) {
                                a = Is(t, Type.Array.class);
                                if (a != null) {
                                    if (ConstValue(a.number) == null) {
                                        Error.Msg(ce.token, "argument cannot be an open array type");
                                        index = Type.Int.T;
                                    } else index = TypeOf(a.number);
                                } else index = t;
                            } else {
                                Error.Msg(ce.token, "argument must be a type or array");
                                index = Type.Int.T;
                            }
                            if (IsOrdinal(index)) {
                                // ok
                            } else {
                                Error.Msg(ce.token, "argument must be an ordinal type, array type or array");
                            }
                            ce.type = Base(index);
                            return null;
                        }
                        public Void visit(Type.Proc.Last __) {
                            return visit(Type.Proc.First.T);
                        }
                        public Void visit(Type.Proc.Number __) {
                            Expr e = ce.args[0];
                            Type t = TypeOf(e);
                            Type index;
                            Type.Array a = Is(t, Type.Array.class);
                            if (a != null) {
                                index = ConstValue(a.number) == null ? Type.Int.T : TypeOf(a.number);
                            } else if ((t = IsType(e)) != null) {
                                a = Is(t, Type.Array.class);
                                if (a != null) {
                                    if (ConstValue(a.number) == null) {
                                        Error.Msg(ce.token, "argument cannot be an open array type");
                                        index = Type.Int.T;
                                    } else index = TypeOf(a.number);
                                } else index = t;
                            } else {
                                Error.Msg(ce.token, "argument must be a type or array");
                                index = Type.Int.T;
                            }
                            if (IsOrdinal(index)) {
                                // ok
                            } else {
                                Error.Msg(ce.token, "argument must be an ordinal type, array type or array");
                            }
                            ce.type = Type.Int.T;
                            return null;
                        }
                        public Void visit(Type.Proc.New __) {
                            Expr e = ce.args[0];
                            Type t = IsType(e);
                            if (t == null) {
                                Error.Msg(ce.token, "New must be applied to a reference type");
                                t = Type.Null.T;
                            } else if (Is(t, Type.Ref.class) != null) {
                                CheckRef(t, ce);
                            } else if (Is(t, Type.Object.class) != null) {
                                Type r = CheckObject(t, ce);
                                if (r != t) {
                                    ce.args[0] = new Expr.TypeExpr(r);
                                    Check(ce.args[0]);
                                    t = r;
                                }
                            } else if (t != Type.Err.T)
                                Error.Msg(ce.token, "New must be applied to a reference type");
                            ce.type = t;
                            return null;
                        }
                        private void CheckRef(Type t, Expr.Call ce) {
                            Type r = ((Type.Ref)t).target;
                            if (r == null) {
                                Error.Msg(ce.token, "cannot New a variable of type refany, address, or null");
                                return;
                            }
                            r = Check(r);
                            Type base;
                            if (IsOpenArray(r) != null)
                                CheckOpenArray(r, ce);
                            else if (ce.args.length > 1) {
                                Error.Msg(ce.token, "too many arguments to New");
                            }
                        }
                        private void CheckOpenArray (Type r, Expr.Call ce) {
                            Type.Array a;
                            for (int i = 1; i < ce.args.length; ++i) {
                                Type x = Base(TypeOf(ce.args[i]));
                                if (!IsEqual(x, Type.Int.T, null))
                                    Error.Int(ce.args[i].token, i, "argument must be an integer");
                                else if ((a = IsOpenArray(r)) == null)
                                    Error.Int(ce.args[i].token, i, "too many dimensions specified");
                                else r = a.element;
                            }
                            if (IsOpenArray(r) != null)
                                Error.Msg(ce.token, "not enough dimensions specified");
                        }
                        private Type CheckObject(Type t, Expr.Call ce) {
                            t = Check(t);
                            return t;
                        }
                    });
                }
                return null;
            }
            public Void visit(Expr.Check e) {
                Check(e.expr);
                return null;
            }
            public Void visit(Expr.Deref e) {
                int err0 = Error.nErrors();
                Check(e.expr);
                Type tx = TypeOf(e.expr);
                int err1 = Error.nErrors();
                Type ta = Base(tx);
                Type.Ref ref;
                if ((tx == null || tx == Type.Err.T) && err0 != err1)
                    // already an error, don't generate any more
                    e.type = Type.Err.T;
                else if ((ref = Is(ta, Type.Ref.class)) == null) {
                    Error.Msg(e.token, "cannot dereference a non-reference value");
                    e.type = Type.Err.T;
                } else if (ref.target == null) {
                    Error.Msg(e.token, "cannot dereference Refany or Null");
                    e.type = Type.Err.T;
                } else
                    e.type = ref.target;
                return null;
            }
            public Void visit(Expr.Div e) {
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsEqual(lt,rt,null)) {
                    BadOperands(e,lt,rt);
                } else if (!IsSubtype(lt,Type.Int.T)) {
                    BadOperands(e,lt,rt);
                }
                return null;
            }
            public Void visit(Expr.Equal e) {
                assert e.type != null;
                // TODO
                // lhs and rhs must be assignable
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsAssignable(lt,rt)) {
                    BadOperands(e, lt, rt);
                } // should I check if they are of the same type?
                return null;
            }
            public Void visit(Expr.Compare e) {
                assert e.type != null;
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if(!IsEqual(lt,rt,null)) {
                    BadOperands(e, lt, rt);
                } else if (!IsOrdinal(lt)) {
                    BadOperands(e, lt, rt);
                }
                return null;
            }
            public Void visit(Expr.Neg e) {
                // TODO
                Type et = TypeOf(e.expr);
                if(!IsSubtype(et, Type.Int.T)) {
                    BadOperands(e, et, null);
                }
                return null;
            }
            public Void visit(Expr.Method e) {
                e.object = Check(e.object);
                Check(e.method);
                Check(TypeOf(e));
                return null;
            }
            public Void visit(Expr.Mod e) {
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsEqual(lt,rt,null)){
                    BadOperands(e, lt, rt);
                } else if (!IsSubtype(lt, Type.Int.T)) {
                    BadOperands(e, lt, rt);
                }
                return null;
            }
            public Void visit(Expr.Mul e) {
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsEqual(lt,rt,null)){
                    BadOperands(e, lt, rt);
                } else if (!IsSubtype(lt, Type.Int.T)) {
                    BadOperands(e, lt, rt);
                }
                return null;
            }
            public Void visit(Expr.Named e) {
                Value v = Resolve(e);
                Check(v);
                e.type = TypeOf(v);
                e.value = v;
                return null;
            }
            public Void visit(Expr.Not e) {
                assert e.type != null;
                // TODO
                Type et = TypeOf(e.expr);
                if (!IsSubtype(et, Type.Bool.T)) {
                    BadOperands(e, et, null);
                }
                return null;
            }
            public Void visit(Expr.Int e) { assert e.type != null; return null; }
            public Void visit(Expr.Bool e) { assert e.type != null; return null; }
            public Void visit(Expr.Or e) {
                assert e.type != null;
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if(!IsEqual(lt,rt,null)) {
                    BadOperands(e,lt,rt);
                } else if(!IsSubtype(lt, Type.Bool.T)) {
                    BadOperands(e,lt,rt);
                }
                return null;
            }
            public Void visit(Expr.Pos e) {
                // TODO
                Type et = TypeOf(e.expr);
                if(!IsSubtype(et, Type.Int.T)) {
                    BadOperands(e, et, null);
                }
                return null;
            }
            public Void visit(Expr.Proc e) {
                Check(e.proc);
                e.type = TypeOf(e.proc);
                return null;
            }
            public Void visit(Expr.Qualify e) {
                Value.Field field;
                int nErrs0 = Error.nErrors();
                Check(e.expr);
                Value v = Resolve(e);
                Check(e.expr);
                int nErrs1 = Error.nErrors();
                if (v == null) {
                    if (nErrs0 == nErrs1)
                        Error.ID(e.name, "unknown qualification");
                    v = new Value.Variable(e.name, Type.Err.T, null);
                    e.value = v;
                } else if (e.objType != null && Is(v, Value.Method.class) == null) {
                    Error.ID(e.name, "doesn't name a method");
                } else if ((field = Is(v, Value.Field.class)) != null) {
                    Check(field.parent);
                }
                Check(v);
                TypeOf(e); // check for cycles
                if (e.type != null)
                    e.type = Check(e.type);
                return null;
            }
            public Void visit(Expr.Subscript e) {
                Check(e.expr);
                Check(e.index);
                Type ta = Base(TypeOf(e.expr));
                Type tb = TypeOf(e.index);
                if (ta == null) {
                    Error.Msg(e.token, "subscripted expression is not an array");
                    e.type = Type.Err.T;
                    return null;
                }
                if (Is(ta, Type.Ref.class) != null) {
                    // auto-magic dereference
                    e.expr = new Expr.Deref(e.token, e.expr);
                    Check(e.expr);
                    ta = Base(TypeOf(e.expr));
                }
                ta = Check(ta);
                if (ta == Type.Err.T) {
                    e.type = Type.Err.T;
                    return null;
                }
                Type.Array a = Is(ta, Type.Array.class);
                if (a == null) {
                    Error.Msg(e.token, "subscripted expression is not an array");
                    e.type = Type.Err.T;
                    return null;
                }
                e.type = a.element;
                NeedsAddress(e.expr);
                Expr number = ConstValue(a.number);
                if (number == null) {
                    // a is an open array
                    if (!IsSubtype(tb, Type.Int.T))
                        Error.Msg(e.token, "open arrays must be indexed by integer expressions");
                } else if (IsSubtype(tb, Base(TypeOf(a.number)))) {
                    // the index value's type has a common base type with the index type
                    BigInteger
                        min = BigInteger.ZERO,
                        max = ((Expr.Int)number).value.max(BigInteger.ZERO).subtract(BigInteger.ONE);
                    Bounds b = GetBounds(e.index);
                    if (b.min.compareTo(min) < 0 && b.max.compareTo(max) > 0) {
                        Check(e.index = new Expr.Check(e.index, min, max));
                    } else if (b.min.compareTo(min) < 0) {
                        if (b.max.compareTo(min) < 0)
                            Error.Warn(e.token, "subscript is out of range");
                        Check(e.index = new Expr.Check(e.index, min, null));
                    } else if (b.max.compareTo(max) > 0) {
                        if (b.min.compareTo(max) > 0)
                            Error.Warn(e.token, "subscript is out of range");
                        Check(e.index = new Expr.Check(e.index, null, max));
                    }
                } else {
                    Error.Msg(e.token, "incompatible array index");
                }
                return null;
            }
            public Void visit(Expr.Sub e) {
                // TODO
                Type lt = TypeOf(e.left);
                Type rt = TypeOf(e.right);
                if (!IsEqual(lt,rt,null)) {
                    BadOperands(e,lt,rt);
                } else if(!IsSubtype(lt, Type.Int.T)) {
                    BadOperands(e,lt,rt);
                }
                return null;
            }
            public Void visit(Expr.Text e) { assert e.type != null; return null; }
            public Void visit(Expr.TypeExpr e) {
                e.value = Check(e.value);
                assert e.type == null;
                return null;
            }
        }
        if (e == null) return;
        if (e.checked) return;
        e.accept(new Visitor());
        e.checked = true;
    }

    /**
     * Note an "illegal operand(s)" error for an expression
     * @param e the erroring expression
     * @param a the type of the first operand (can be null)
     * @param b the type of the second operand (can be null)
     * @return the error type
     */
    Type BadOperands(Expr e, Type a, Type b) {
        if (a != Type.Err.T && b != Type.Err.T)
            Error.Msg(e.token, "illegal operand(s) for " + e.token);
        return Type.Err.T;
    }

    /**
     * Returns null if e is not a constant, otherwise returns a simplified
     * expression that denotes e.  ConstValue may be called before the expression
     * is typechecked.
     */
    Expr ConstValue(Expr e) {
        class Visitor implements Expr.Visitor<Expr> {
            public Expr visit(Expr.Add e) {
                // TODO
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                if (!(e1 instanceof Expr.Int)) return null;
                if (!(e2 instanceof Expr.Int)) return null;
                BigInteger z1 = ((Expr.Int)e1).value;
                BigInteger z2 = ((Expr.Int)e2).value;
                return new Expr.Int(e.token, z1.add(z2));
            }
            public Expr visit(Expr.Address e) { return e; }

            public Expr visit(Expr.And e) {
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (Base(TypeOf(e1)) != Type.Bool.T) return null;
                if (Base(TypeOf(e2)) != Type.Bool.T) return null;
                boolean z1 = ((Expr.Bool)e1).value;
                boolean z2 = ((Expr.Bool)e2).value;
                return z1 && z2 ? Type.Bool.True : Type.Bool.False;
            }
            public Expr visit(Expr.Call ce) {
                Resolve(ce);
                if (ce.procType == null) return null;
                return ce.procType.accept(new Type.Proc.Visitor<Expr>() {
                    public Expr visit(Type.Proc.User p) {
                        return null;
                    }
                    public Expr visit(Type.Proc.First p) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        if (t != null) return FirstOfType(t);
                        t = TypeOf(e);
                        Type.Array a = Is(t, Type.Array.class);
                        if (a == null) return null;
                        return FirstOfType(t);
                    }
                    Expr FirstOfType(Type t) {
                        if (Is(t, Type.Array.class) != null)
                            return new Expr.Int(ce.token, BigInteger.ZERO);
                        if (IsOrdinal(t)) return new Expr.Int(ce.token, Min(t));
                        return null;
                    }
                    public Expr visit(Type.Proc.Last p) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        if (t == null) {
                            t = TypeOf(e);
                            Type.Array a = Is(t, Type.Array.class);
                            if (a != null) return LastOfType(t);
                            return null;
                        }
                        return LastOfType(t);
                    }
                    Expr LastOfType(Type t) {
                        Type.Array a = Is(t, Type.Array.class);
                        if (a != null) {
                            Expr e = ConstValue(a.number);
                            if (e == null) return null;
                            if (!(e instanceof Expr.Int)) return null;
                            BigInteger n = ((Expr.Int)e).value.subtract(BigInteger.ONE);
                            return new Expr.Int(ce.token, n);
                        }
                        if (IsOrdinal(t)) return new Expr.Int(ce.token, Max(t));
                        return null;
                    }
                    public Expr visit(Type.Proc.Number p) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        if (t == null) {
                            t = TypeOf(e);
                            Type.Array a = Is(t, Type.Array.class);
                            if (a != null) return ConstValue(a.number);
                            return null;
                        }
                        Type.Array a = Is(t, Type.Array.class);
                        if (a != null) return ConstValue(a.number);
                        if (!IsOrdinal(t)) return null;
                        BigInteger min = Min(t), max = Max(t);
                        return min.compareTo(max) > 0
                            ? new Expr.Int(ce.token, BigInteger.ZERO)
                            : new Expr.Int(ce.token, max.subtract(min).add(BigInteger.ONE));
                    }
                    public Expr visit(Type.Proc.New p) { return null; }
                });
            }
            public Expr visit(Expr.Check e) {
                Expr e1 = ConstValue(e.expr);
                if (e1 == null) return null;
                BigInteger i;
                if (e1 instanceof Expr.Int)
                    i = ((Expr.Int)e1).value;
                else return null;
                if (e.min != null && e.max != null) {
                    if (i.compareTo(e.min) < 0 || i.compareTo(e.max) > 0) return null;
                } else if (e.min != null) {
                    if (i.compareTo(e.min) < 0) return null;
                } else if (e.max != null) {
                    if (i.compareTo(e.max) > 0) return null;
                }
                return e1;
            }
            public Expr visit(Expr.Compare e) {
                // TODO
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                if (!(e1 instanceof Expr.Int)) return null;
                if (!(e2 instanceof Expr.Int)) return null;
                BigInteger z1 = ((Expr.Int)e1).value;
                BigInteger z2 = ((Expr.Int)e2).value;
                boolean result = false;
                switch (e.op) {
                    case GT:
                        result = z1.compareTo(z2) > 0;
                        break;
                    case GE:
                        result = z1.compareTo(z2) >= 0;
                        break;
                    case LT:
                        result = z1.compareTo(z2) < 0;
                        break;
                    case LE:
                        result = z1.compareTo(z2) <= 0;
                        break;
                }
                return result ? Type.Bool.True : Type.Bool.False;
            }
            public Expr visit(Expr.Deref e) { return null; }
            public Expr visit(Expr.Div e) {
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                if (!(e1 instanceof Expr.Int)) return null;
                if (!(e2 instanceof Expr.Int)) return null;
                if (((Expr.Int)e2).value.signum() == 0) {
                    Error.Msg(e.token, "attempt to divide by 0");
                    return null;
                }
                return new Expr.Int(e.token, ((Expr.Int)e1).value.divide(((Expr.Int)e2).value));
            }
            public Expr visit(Expr.Equal e) {
                // TODO
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                // maybe should check more here?
                if (IsEqual(e1, e2, null)) {
                    return Type.Bool.True;
                } else return Type.Bool.False;

            }
            public Expr visit(Expr.Neg e) {
                Expr e1 = ConstValue(e.expr);
                if (e1 == null) return null;
                if (e1 instanceof Expr.Int)
                    return new Expr.Int(e.token, ((Expr.Int)e1).value.negate());
                return null;
            }
            public Expr visit(Expr.Method e) { return e; }
            public Expr visit(Expr.Mod e) {
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                if (!(e1 instanceof Expr.Int)) return null;
                if (!(e2 instanceof Expr.Int)) return null;
                if (((Expr.Int)e2).value.signum() == 0) {
                    Error.Msg(e.token, "attempt to modulus by 0");
                    return null;
                }
                return new Expr.Int(e.token, ((Expr.Int)e1).value.remainder(((Expr.Int)e2).value));
            }
            public Expr visit(Expr.Mul e) {
                // TODO
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                if (!(e1 instanceof Expr.Int)) return null;
                if (!(e2 instanceof Expr.Int)) return null;
                BigInteger z1 = ((Expr.Int)e1).value;
                BigInteger z2 = ((Expr.Int)e2).value;
                return new Expr.Int(e.token, z1.multiply(z2));
            }
            public Expr visit(Expr.Named e) {
                Value v = Resolve(e);
                inFold.add(e);
                Expr x = ToExpr(v);
                inFold.remove(e);
                return ConstValue(x);
            }
            public Expr visit(Expr.Not e) {
                Expr e1 = ConstValue(e.expr);
                if (Base(TypeOf(e1)) != Type.Bool.T) return null;
                boolean z1 = ((Expr.Bool)e1).value;
                return !z1 ? Type.Bool.True : Type.Bool.False;
            }
            public Expr visit(Expr.Int e) { return e; }
            public Expr visit(Expr.Bool e) { return e; }
            public Expr visit(Expr.Or e) {
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (Base(TypeOf(e1)) != Type.Bool.T) return null;
                if (Base(TypeOf(e2)) != Type.Bool.T) return null;
                boolean z1 = ((Expr.Bool)e1).value;
                boolean z2 = ((Expr.Bool)e2).value;
                return z1 || z2 ? Type.Bool.True : Type.Bool.False;
            }
            public Expr visit(Expr.Pos e) {
                Expr e1 = ConstValue(e.expr);
                if (e1 == null) return null;
                if (e1 instanceof Expr.Int) return e1;
                return null;
            }
            public Expr visit(Expr.Proc e) { return e; }
            public Expr visit(Expr.Qualify e) {
                Value v = Resolve(e);
                if (inFold.contains(e)) {
                    IllegalRecursion(v);
                    return null;
                }
                try {
                    inFold.add(e);
                    // evaluate the qualified expression
                    LHS lhs = new LHS();
                    lhs.kind = LHS.Kind.Expr;
                    lhs.expr = e.expr;
                    DoQualify(lhs, e.name.image);
                    // finally simplify the result to an Expr if possible
                    switch (lhs.kind) {
                    default: return null;
                    case Expr:
                        return ConstValue(lhs.expr);
                    case Type:
                        return new Expr.TypeExpr(lhs.type);
                    case Value:
                        return ConstValue(ToExpr(lhs.value));
                    }
                } finally {
                    inFold.remove(e);
                }
            }
            public Expr visit(Expr.Subscript e) { return null; }
            public Expr visit(Expr.Sub e) {
                Expr e1 = ConstValue(e.left);
                Expr e2 = ConstValue(e.right);
                if (e1 == null || e2 == null) return null;
                if (!(e1 instanceof Expr.Int)) return null;
                if (!(e2 instanceof Expr.Int)) return null;
                BigInteger result = ((Expr.Int)e1).value.subtract(((Expr.Int)e2).value);
                return new Expr.Int(e.token, result);
            }
            public Expr visit(Expr.Text e) { return e; }
            public Expr visit(Expr.TypeExpr e) { return e; }
        }
        if (e == null) return null;
        Expr value = e.accept(new Visitor());
        if (value != e) Check(value);
        return value;
    }
    final HashSet<Expr> inFold = new HashSet<Expr>();

    class Bounds {
        final BigInteger min, max;
        Bounds(BigInteger min, BigInteger max) { this.min = min; this.max = max; }
    }
    Bounds GetBounds(final Expr e) {
        class Visitor implements Expr.Visitor<Bounds> {
            Bounds NoBounds() { return new Bounds(Min(e.type), Max(e.type)); }
            public Bounds visit(Expr.Add p) {
                BigInteger min = Min(p.type), max = Max(p.type);
                Bounds a = GetBounds(p.left);
                Bounds b = GetBounds(p.right);
                BigInteger sum;
                sum = a.min.add(b.min); if (sum.compareTo(min) > 0) min = sum;
                sum = a.max.add(b.max); if (sum.compareTo(max) < 0) max = sum;
                return new Bounds(min, max);
            }
            public Bounds visit(Expr.Address p) { return new Bounds(p.value, p.value); }
            public Bounds visit(Expr.And p) { return NoBounds(); }
            public Bounds visit(Expr.Call ce) {
                Expr e = ConstValue(ce);
                if (e != null && e != ce) return GetBounds(e);
                if (ce.procType == null) return NoBounds();
                return ce.procType.accept(new Type.Proc.Visitor<Bounds>() {
                    public Bounds visit(Type.Proc.User e) { return NoBounds(); }
                    public Bounds visit(Type.Proc.First e) { return NoBounds(); }
                    public Bounds visit(Type.Proc.Last e) { return NoBounds(); }
                    public Bounds visit(Type.Proc.Number e) { return NoBounds(); }
                    public Bounds visit(Type.Proc.New e) { return NoBounds(); }
                });
            }
            public Bounds visit(Expr.Check p) {
                Bounds b = GetBounds(p.expr);
                BigInteger min = b.min, max = b.max;
                if (p.min != null && min.compareTo(p.min) < 0) min = p.min;
                if (p.max != null && max.compareTo(p.max) > 0) max = p.max;
                return new Bounds(min, max);
            }
            public Bounds visit(Expr.Compare p) { return NoBounds(); }
            public Bounds visit(Expr.Deref p) { return NoBounds(); }
            public Bounds visit(Expr.Div p) { return NoBounds(); }
            public Bounds visit(Expr.Equal p) { return NoBounds(); }
            public Bounds visit(Expr.Neg p) {
                BigInteger min = Min(p.type), max = Max(p.type);
                Bounds b = GetBounds(p.expr);
                BigInteger diff;
                diff = b.max.negate(); if (diff.compareTo(min) > 0) min = diff;
                diff = b.min.negate(); if (diff.compareTo(max) < 0) max = diff;
                return new Bounds(min, max);
            }
            public Bounds visit(Expr.Method p) { return NoBounds(); }
            public Bounds visit(Expr.Mod p) {
                Bounds b = GetBounds(p.right);
                if (b.min.compareTo(BigInteger.ZERO) < 0 || b.max.compareTo(BigInteger.ZERO) < 0)
                    return NoBounds();
                return new Bounds(BigInteger.ZERO, b.max.subtract(BigInteger.ONE));
            }
            public Bounds visit(Expr.Mul p) { return NoBounds(); }
            public Bounds visit(Expr.Named p) {
                Value v = Resolve(p);
                if (inGetBounds.contains(p)) {
                    IllegalRecursion(v);
                    return null;
                }
                inGetBounds.add(p);
                Expr x = ToExpr(v);
                inGetBounds.remove(p);
                return GetBounds(x);
            }
            public Bounds visit(Expr.Not p) { return NoBounds(); }
            public Bounds visit(Expr.Int p) { return new Bounds(p.value, p.value); }
            public Bounds visit(Expr.Bool p) { return NoBounds(); }
            public Bounds visit(Expr.Or p) { return NoBounds(); }
            public Bounds visit(Expr.Pos p) { return NoBounds(); }
            public Bounds visit(Expr.Proc p) { return NoBounds(); }
            public Bounds visit(Expr.Qualify p) {
                Value v = Resolve(p);
                if (inGetBounds.contains(p)) {
                    IllegalRecursion(v);
                    return null;
                }
                inGetBounds.add(p);
                Expr x = ToExpr(v);
                inGetBounds.remove(p);
                return GetBounds(x);
            }
            public Bounds visit(Expr.Subscript p) { return NoBounds(); }
            public Bounds visit(Expr.Sub p) {
                BigInteger min = Min(p.type), max = Max(p.type);
                Bounds a = GetBounds(p.left);
                Bounds b = GetBounds(p.right);
                BigInteger diff;
                diff = a.min.subtract(b.max); if (diff.compareTo(min) > 0) min = diff;
                diff = a.max.subtract(b.min); if (diff.compareTo(max) < 0) max = diff;
                return new Bounds(min, max);
            }
            public Bounds visit(Expr.Text p) { return NoBounds(); }
            public Bounds visit(Expr.TypeExpr p) { return NoBounds(); }
        }
        if (e == null)
            return new Bounds(BigInteger.ZERO, BigInteger.ONE.negate()) ;
        assert e.checked;
        return e.accept(new Visitor());
    }
    final HashSet<Expr> inGetBounds = new HashSet<Expr>();

    boolean IsDesignator(Expr e) {
        class Visitor implements Expr.Visitor<Boolean> {
            public Boolean visit(Expr.Named e) {
                Value v = Resolve(e);
                return Is(v, Value.Variable.class) != null;
            }
            public Boolean visit(Expr.Qualify e) {
                Value v = Resolve(e);
                if (e.objType != null) return false;
                if (Is(v, Value.Field.class) != null) return IsDesignator(e.expr);
                return Is(v, Value.Variable.class) != null;
            }
            public Boolean visit(Expr.Add e) { return false; }
            public Boolean visit(Expr.Address e) { return false; }
            public Boolean visit(Expr.And e) { return false; }
            public Boolean visit(Expr.Bool e) { return false; }
            public Boolean visit(Expr.Call e) { return false; }
            public Boolean visit(Expr.Check e) { return false; }
            public Boolean visit(Expr.Compare e) { return false; }
            public Boolean visit(Expr.Deref e) { return true; }
            public Boolean visit(Expr.Div e) { return false; }
            public Boolean visit(Expr.Equal e) { return false; }
            public Boolean visit(Expr.Neg e) { return false; }
            public Boolean visit(Expr.Method e) { return false; }
            public Boolean visit(Expr.Mod e) { return false; }
            public Boolean visit(Expr.Mul e) { return false; }
            public Boolean visit(Expr.Not e) { return false; }
            public Boolean visit(Expr.Int e) { return false; }
            public Boolean visit(Expr.Or e) { return false; }
            public Boolean visit(Expr.Pos e) { return false; }
            public Boolean visit(Expr.Proc e) { return false; }
            public Boolean visit(Expr.Subscript e) { return IsDesignator(e.expr); }
            public Boolean visit(Expr.Sub e) { return false; }
            public Boolean visit(Expr.Text e) { return false; }
            public Boolean visit(Expr.TypeExpr e) { return false; }
        }
        if (e == null) return true;
        assert e.checked;
        return e.accept(new Visitor());
    }

    boolean IsWritable(Expr e) {
        class Visitor implements Expr.Visitor<Boolean> {
            public Boolean visit(Expr.Named e) {
                Value v = Resolve(e);
                return IsWritable(v);
            }
            public Boolean visit(Expr.Qualify e) {
                Value v = Resolve(e);
                if (e.objType != null) return false;
                if (v instanceof Value.Field) return IsWritable(e.expr);
                return IsWritable(v);
            }
            public Boolean visit(Expr.Subscript e) { return IsWritable(e.expr); }
            public Boolean visit(Expr.Deref e) { return true; }
            public Boolean visit(Expr.Add e) { return false; }
            public Boolean visit(Expr.Address e) { return false; }
            public Boolean visit(Expr.And e) { return false; }
            public Boolean visit(Expr.Bool e) { return false; }
            public Boolean visit(Expr.Call e) { return false; }
            public Boolean visit(Expr.Check e) { return false; }
            public Boolean visit(Expr.Compare e) { return false; }
            public Boolean visit(Expr.Div e) { return false; }
            public Boolean visit(Expr.Equal e) { return false; }
            public Boolean visit(Expr.Neg e) { return false; }
            public Boolean visit(Expr.Method e) { return false; }
            public Boolean visit(Expr.Mod e) { return false; }
            public Boolean visit(Expr.Mul e) { return false; }
            public Boolean visit(Expr.Not e) { return false; }
            public Boolean visit(Expr.Int e) { return false; }
            public Boolean visit(Expr.Or e) { return false; }
            public Boolean visit(Expr.Pos e) { return false; }
            public Boolean visit(Expr.Proc e) { return false; }
            public Boolean visit(Expr.Sub e) { return false; }
            public Boolean visit(Expr.Text e) { return false; }
            public Boolean visit(Expr.TypeExpr e) { return false; }
        }
        if (e == null) return true;
        assert e.checked;
        return e.accept(new Visitor());
    }

    Void NeedsAddress(Expr e) {
        class Visitor implements Expr.Visitor<Void> {
            public Void visit(Expr.Named e) {
                Value v = Resolve(e);
                Value.Variable var = Is(v, Value.Variable.class);
                if (var != null)
                    var.needsAddress(true);
                else
                    assert false;
                return null;
            }
            public Void visit(Expr.Qualify e) {
                Value v = Resolve(e);
                assert(e.objType == null);
                Value.Variable var = Is(v, Value.Variable.class);
                if (var != null)
                    var.needsAddress(true);
                else if (Is(v, Value.Field.class) != null)
                    NeedsAddress(e.expr);
                else
                    assert false;
                return null;
            }
            public Void visit(Expr.Subscript e) {
                NeedsAddress(e.expr);
                return null;
            }
            public Void visit(Expr.Deref e) {
                // ok
                return null;
            }
            public Void visit(Expr.Add e)      { assert false; return null; }
            public Void visit(Expr.Address e)  { assert false; return null; }
            public Void visit(Expr.And e)      { assert false; return null; }
            public Void visit(Expr.Bool e)     { assert false; return null; }
            public Void visit(Expr.Call e)     { assert false; return null; }
            public Void visit(Expr.Compare e)  { assert false; return null; }
            public Void visit(Expr.Check e)    { assert false; return null; }
            public Void visit(Expr.Div e)      { assert false; return null; }
            public Void visit(Expr.Equal e)    { assert false; return null; }
            public Void visit(Expr.Neg e)      { assert false; return null; }
            public Void visit(Expr.Mod e)      { assert false; return null; }
            public Void visit(Expr.Method e)   { assert false; return null; }
            public Void visit(Expr.Mul e)      { assert false; return null; }
            public Void visit(Expr.Not e)      { assert false; return null; }
            public Void visit(Expr.Int e)      { assert false; return null; }
            public Void visit(Expr.Or e)       { assert false; return null; }
            public Void visit(Expr.Pos e)      { assert false; return null; }
            public Void visit(Expr.Proc e)     { assert false; return null; }
            public Void visit(Expr.Sub e)      { assert false; return null; }
            public Void visit(Expr.Text e)     { assert false; return null; }
            public Void visit(Expr.TypeExpr e) { assert false; return null; }
        }
        if (e == null) return null;
        assert e.checked;
        return e.accept(new Visitor());
    }

    <R> R Is(Expr e, Class<R> c) {
        if (e == null) return null;
        if (c.isInstance(e)) return c.cast(e);
        return null;
    }

    Type IsType(Expr e) {
        if (e == null) return null;
        if (e instanceof Expr.TypeExpr exp) return exp.value;
        if (e instanceof Expr.Named exp) return ToType(Resolve(exp));
        if (e instanceof Expr.Qualify exp) return ToType(Resolve(exp));
        return null;
    }

    Value Resolve(Expr.Named e) {
        if (e.value != null) return e.value;
        Value v = Scope.LookUp(Scope.Top(), e.name, false);
        if (v == null) {
            Error.ID(e.token, "undefined");
            v = new Value.Variable(e.token, Type.Err.T, null);
        }
        return e.value = v;
    }

    Value Resolve(Expr.Qualify e) {
        if (e.value != null) return e.value;
        Type t = TypeOf(e.expr);
        if (Is(t, Type.Ref.class) != null) {
            // auto-magic dereference
            e.expr = new Expr.Deref(e.token, e.expr);
            t = TypeOf(e.expr);
        }
        e.holder = t;
        assert e.value == null;
        Type base = Base(t);
        final Value v;
        if (t == Type.Err.T)
            // the lhs already contains an error => silently make it look like
            // everything is ok
            v = new Value.Variable(e.name, Type.Err.T, null);
        else if (t == null) {
            // a module or type
            if ((t = IsType(e.expr)) != null) {
                Type.Object[] visible = {null};
                v = LookUp(Is(t, Type.Object.class), e.name.image, visible);
                if (v != null) {                    
                    e.objType = t;
                    e.holder = visible[0];
                }
            } else if (Resolve(Is(e.expr, Expr.Named.class))
                       instanceof Value.Unit m) {
                v = Scope.LookUp(m.localScope, m.name, true);
            } else v = null;
        } else if (base instanceof Type.Record p) {
            v = LookUp(p, e.name.image);
        } else if (base instanceof Type.Object p) {
            Type.Object[] visible = {null};
            v = LookUp(p, e.name.image, visible);
            if (v != null) e.holder = visible[0];
        } else v = null;  
        return e.value = v;
    }

    Type MethodType(Expr.Qualify e) {
        if (e == null) return null;
        Value v = Resolve(e);
        if (Is(v, Value.Method.class) != null) return TypeOf(v);
        return null;
    }
    static class LHS {
        enum Kind {Value,Expr,Type,None}

        Kind kind;
        Value value;
        Expr expr;
        Type type;
    }
    void DoQualify(LHS lhs, String name) {
        switch (lhs.kind) {
        case None: // don't even try
            return;
        case Expr: {
            Type t;
            Value v;
            Expr e;
            if (lhs.expr == null)
                lhs.kind = LHS.Kind.None;
            else if (lhs.expr instanceof Expr.Qualify p) {
                lhs.kind = LHS.Kind.Expr;
                lhs.expr = p.expr;
                DoQualify(lhs, p.name.image);
                DoQualify(lhs, name);
            } else if ((t = IsType(lhs.expr)) != null) {
                lhs.kind = LHS.Kind.Type;
                lhs.type = t;
                DoQualify(lhs, name);
            } else if (lhs.expr instanceof Expr.Named n
                       && (v = Resolve(n)) != null) {
                lhs.kind = LHS.Kind.Value;
                lhs.value = v;
                DoQualify(lhs, name);
            } else {
                e = ConstValue(lhs.expr);
                if (e != lhs.expr) {
                    // try qualifying the constant value
                    lhs.kind = LHS.Kind.Expr;
                    lhs.expr = e;
                    DoQualify(lhs, name);
                } else {
                    lhs.kind = LHS.Kind.None;
                }
            }
            return;
        }
        case Type: {
            Value v;
            Type t = Strip(lhs.type);
            Type.Object[] visible = {null};
            if (LookUp(Is(t, Type.Object.class), name, visible)
                instanceof Value.Method m) {
                lhs.kind = LHS.Kind.Expr;
                lhs.expr = new Expr.Method(t, name, m);
            } else // type that can't be qualified
                lhs.kind = LHS.Kind.None;
            return;
        }
        case Value: {
            class Visitor implements Value.Visitor<Void> {
                public Void visit(Value.Const d) {
                    lhs.kind = LHS.Kind.Expr;
                    lhs.expr = d.expr;
                    DoQualify(lhs, name);
                    return null;
                }
                public Void visit(Value.Field d) { return null; }
                public Void visit(Value.Formal d) { return null; }
                public Void visit(Value.Method d) { return null; }
                public Void visit(Value.Unit d) {
                    lhs.kind = LHS.Kind.Value;
                    lhs.value = Scope.LookUp(d.localScope, name, true);
                    return null;
                }
                public Void visit(Value.Procedure d) { return null; }
                public Void visit(Value.Tipe d) {
                    lhs.kind = LHS.Kind.Type;
                    lhs.type = d.value;
                    DoQualify(lhs, name);
                    return null;
                }
                public Void visit(Value.Variable d) { return null; }
            }
            lhs.value.accept(new Visitor());
            return;
        }
        }
    }

    void Resolve(Expr.Call e) {
        if (e.procType != null) return;
        Type t = TypeOf(e.proc);
        if (t == null)
            // we need this hack because "TypeOf(obj.method)" returns null
            // so that you can't use it as a vanilla procedure value.
            t = MethodType(Is(e.proc, Expr.Qualify.class));
        e.procType = Is(t, Type.Proc.class);
    }

    void FixArgs(Expr.Call e) {
        int minArgs = e.procType.minArgs;
        int maxArgs = e.procType.maxArgs;
        if (e.args.length < minArgs) {
            Error.Msg(e.token, "too few arguments");
            Expr[] z = new Expr[minArgs];
            System.arraycopy(e.args, 0, z, 0, e.args.length);
            e.args = z;
        } else if (maxArgs >= 0 && e.args.length > maxArgs) {
            Error.Msg(e.token, "too many arguments");
            Expr[] z = new Expr[maxArgs];
            System.arraycopy(e.args, 0, z, 0, maxArgs);
            e.args = z;
        }
    }

    /**
     * Type-check the values in a scope, including procedure bodies.
     * @param t the scope to check
     */
    void Check(Scope t) {
        for (Value v : Scope.ToList(t))
            Check(v);
        for (Value v : Scope.ToList(t)) {
            Value.Procedure p = Is(v, Value.Procedure.class);
            if (p != null) CheckBody(p);
        }
    }

    String ModuleName(Value v) {
        return v.name;
    }

    String NameToPrefix(Value v, boolean considerExternal, boolean dots, boolean withModule) {
        String dot = dots ? "." : "__";
        if (considerExternal && v.external())
            // simple external name: foo
            return v.extName;
        else if (v.exported() || v.imported() || v.scope.module) {
            // global names: foo, module.foo
            if (v.scope == null || v.scope.name == null || !withModule)
                return v.name;
            else
                return v.scope.name + dot + v.name;
        } else if (withModule || ToExpr(v) != null) {
            // procedure => fully qualified name: module.p1.p2.p
            StringBuilder result = new StringBuilder(v.name);
            for (Scope t = v.scope; t != null; t = t.parent) {
                if (t.name != null) result.insert(0, t.name + dot);
                if (!t.open) break;
                if (t.module) break;
            }
            return result.toString();
        } else {
            // variable => simple name: foo
            StringBuilder result = new StringBuilder(v.name);
            for (Scope t = v.scope; t != null; t = t.parent) {
                if (t.name != null) result.insert(0, t.name + dot);
                if (!t.open || !t.proc) break;
            }
            return result.toString();
        }
    }

    void print(Scope scope) {
        if (!scope.open) return;
        System.out.println("BEGIN " + scope);
        for (Value v: Scope.ToList(scope)) System.out.println(ToString(v));
        for (Scope s: scope.children) print(s);
        System.out.println("END " + scope);
        System.out.flush();
    }

    String ToString(Type t, boolean useName) {
        class Visitor implements Type.Visitor<String> {
            public String visit(Type.Bool t) { return t.token.image; }
            public String visit(Type.Err t) { return t.token.image; }
            public String visit(Type.Int t) { return t.token.image; }
            public String visit(Type.Named t) { return t.name; }
            public String visit(Type.Object t) {
                String s = "";
                s += ToString(t.parent, true);
                s += String.format("%nobject {%n");
                for (Value.Field f : t.fields) {
                    Type.Object p = Is(f.parent, Type.Object.class);
                    s += (p.fieldOffset + f.offset) + ": " + String.format(ToString(f) + "%n");
                }
                for (Value.Method m : t.overrides) {
                    Type.Object p = PrimaryMethodDeclaration(m.parent, m);
                    s += (p.methodOffset + m.offset) + ": " + String.format(ToString(m) + "%n");
                }
                for (Value.Method m : t.methods)
                    s += (m.parent.methodOffset + m.offset) + ": " + String.format(ToString(m) + "%n");
                s += "}";
                return s;
            }
            public String visit(Type.Array t) {
                return "[" + ToString(t.number) + "]" + ToString(t.element, true);
            }
            public String visit(Type.Proc p) {
                String s = "(";
                int i = 0;
                if (p.scope == null) s += "...";
                else for (Value o : Scope.ToList(p.scope)) {
                    if (i != 0) s += "; ";
                    s += ToString(o);
                    ++i;
                }
                s += ")";
                if (p.result != null) {
                    s += ":";
                    s += ToString(p.result, true);
                }
                return s;
            }
            public String visit(Type.Record p) {
                String s = String.format("%nstruct {%n");
                for (Value.Field f : p.fields)
                    s += f.offset + ": " + String.format(ToString(f) + "%n");
                s += "}";
                return s;
            }
            public String visit(Type.Ref t) {
                return "^" + ToString(t.target, true);
            }
        }
        if (t == null) return null;
        if (useName && t.tipe != null) return t.tipe.name;
        return t.accept(new Visitor());
    }
    String ToString(Expr e) {
        class Visitor implements Expr.Visitor<String> {
            public String visit(Expr.Named e)     { return null; }
            public String visit(Expr.Qualify e)   { return null; }
            public String visit(Expr.Subscript e) { return null; }
            public String visit(Expr.Deref e)     { return null; }
            public String visit(Expr.Add e)       { return null; }
            public String visit(Expr.Address e)   { return e.value.toString(16); }
            public String visit(Expr.And e)       { return null; }
            public String visit(Expr.Bool e)      { return e.value ? "true" : "false"; }
            public String visit(Expr.Call e)      { return null; }
            public String visit(Expr.Compare e)   { return null; }
            public String visit(Expr.Check e)     { return null; }
            public String visit(Expr.Div e)       { return null; }
            public String visit(Expr.Equal e)     { return null; }
            public String visit(Expr.Neg e)       { return null; }
            public String visit(Expr.Mod e)       { return null; }
            public String visit(Expr.Method e)    {
                return ToString(e.object, true) + "." + e.method.name;
            }
            public String visit(Expr.Mul e)       { return null; }
            public String visit(Expr.Not e)       { return null; }
            public String visit(Expr.Int e)       { return e.value.toString(); }
            public String visit(Expr.Or e)        { return null; }
            public String visit(Expr.Pos e)       { return null; }
            public String visit(Expr.Proc e)      { return ToString(e.proc); }
            public String visit(Expr.Sub e)       { return null; }
            public String visit(Expr.Text e)      { return e.value; }
            public String visit(Expr.TypeExpr e)  { return ToString(e.value, true); }
        }
        e = ConstValue(e);
        if (e == null) return null;
        return e.accept(new Visitor());
    }
    String ToString(Value v) {
        class Visitor implements Value.Visitor<String> {
            public String visit(Value.Const v) {
                return GlobalName(v) + ":" + ToString(v.type, true) + "=" + ToString(v.expr);
            }
            public String visit(Value.Field v) {
                return v.name + ":" + ToString(v.type, true);
            }
            public String visit(Value.Formal v) {
                return v.mode + " " + v.name + ":" + ToString(v.type, true);
            }
            public String visit(Value.Method v) {
                return v.name + ":" + ToString(v.sig, true) + "=" + ToString(v.value);
            }
            public String visit(Value.Procedure v) {
                return GlobalName(v) + ":" + ToString(v.sig, true);
            }
            public String visit(Value.Variable v) {
                return GlobalName(v) + ":" + ToString(v.type, true);
            }
            public String visit(Value.Unit v) { return null; }
            public String visit(Value.Tipe v) {
                return GlobalName(v) + "=" + ToString(v.value, false);
            }
        }
        if (v == null) return null;
        return v.accept(new Visitor());
    }
}
