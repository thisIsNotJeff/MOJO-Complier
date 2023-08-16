/* Copyright (C) 1997-2023, Antony L Hosking.
 * All rights reserved.  */

package mojo;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

import Translate.*;
import Translate.Temp.Label;

import mojo.parse.*;
import static mojo.Absyn.*;

public class Translate extends Semant {
    private static void usage() {
        throw new java.lang.Error("Usage: java mojo.Translate "
            + "[ -target= Mips|PPCDarwin|PPCLinux|x64|interp ] <source>.mj");
    }

    final List<Frag> frags = new LinkedList<Frag>();
    final Frame target, badPtr, badSub;
    Translate(Frame target) {
        super(target.wordSize * 8);
        this.target = target;
        this.badPtr = target.newFrame("badPtr");
        this.badSub = target.newFrame("badSub");
    }

    public static void main(String[] args) {
        if (args.length < 1) usage();
        boolean main = false, trace = false;
        Frame target = new x64.Frame("");
        if (args.length > 1)
            for (String arg : args) {
                if (arg.equals("-main"))
                    main = true;
                else if (arg.equals("-trace"))
                    trace = true;
                else if (arg.equals("-target=interp"))
                    target = new interp.Frame();
                else if (arg.startsWith("-"))
                    usage();
            }
        java.io.File file = new java.io.File(args[args.length - 1]);
        Type.init();
        try {
            Value.Unit unit = new Parser(file).Unit();
            if (Error.nErrors() > 0) return;
            Translate semant = new Translate(target);
            semant.Check(unit);
            if (Error.nErrors() > 0) return;
            semant.Compile(unit, main);
            if (Error.nErrors() > 0) return;
            if (main && target instanceof interp.Frame t) {
                interp.Interpreter i = new interp.Interpreter(t.dataLayout(), semant.frags, trace);
                Frag.Proc proc = null;
                for (Frag f: semant.frags)
                    if (f instanceof Frag.Proc p
                        && p.frame.name.toString().equals("main"))
                        proc = p;
                assert proc != null;
                i.run(proc);
            } else {
                PrintWriter out = new PrintWriter(System.out);
                for (Frag f : semant.frags) {
                    out.println(f);
                    out.flush();
                }
            }
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

    ProcBody currentBody;
    Frame currentFrame;
    Label returnLabel;
    final Map<ProcBody,Frame> frames = new HashMap<ProcBody,Frame>();

    void Compile(final Value.Unit m, boolean main) {
        Frame f = target.newFrame(m.name);
        Frame old = frames.put(m.body, f);
        assert old == null;
        Scope zz = Scope.Push(m.localScope);
        for (Type t : m.types) Compile(t);
        EnterScope(m.localScope);
        // generate any internal procedures
        Value.Visitor<Void> emitDecl = new Value.Visitor<Void>() {
            public Void visit(Value.Procedure v) {
                currentBody = v.body;
                currentFrame = frames.get(v.body);
                Declare(v);
                return null;
            }
            public Void visit(Value.Unit v) {
                currentBody = v.body;
                currentFrame = frames.get(v.body);
                return null;
            }
            public Void visit(Value.Field v) { assert false; return null; }
            public Void visit(Value.Formal v) { assert false; return null; }
            public Void visit(Value.Method v) { assert false; return null; }
            public Void visit(Value.Variable v) { assert false; return null; }
            public Void visit(Value.Const v) { assert false; return null; }
            public Void visit(Value.Tipe v) { assert false; return null; }
        };
        Value.Visitor<Void> emitBody = new Value.Visitor<Void>() {
            public Void visit(Value.Procedure v) {
                returnLabel = new Label();
                currentBody = v.body;
                currentFrame = frames.get(v.body);
                Tree.Stm stm = null;
                Scope zz = Scope.Push(v.localScope);
                EnterScope(v.localScope);
                stm = SEQ(stm, InitValues(v.localScope));
                stm = SEQ(stm, Compile(v.block, null));
                stm = SEQ(stm, LABEL(returnLabel));
                Scope.Pop(zz);
                frags.add(new Frag.Proc(stm, currentFrame));
                return null;
            }
            public Void visit(Value.Unit v) {
                returnLabel = null;
                currentBody = v.body;
                currentFrame = frames.get(v.body);
                Tree.Stm stm = null;
                stm = SEQ(stm, InitValues(m.localScope));
                // perform the main body
                stm = SEQ(stm, Compile(m.block, null));
                frags.add(new Frag.Proc(stm, currentFrame));
                return null;
            }
            public Void visit(Value.Field v) { assert false; return null; }
            public Void visit(Value.Formal v) { assert false; return null; }
            public Void visit(Value.Method v) { assert false; return null; }
            public Void visit(Value.Variable v) { assert false; return null; }
            public Void visit(Value.Const v) { assert false; return null; }
            public Void visit(Value.Tipe v) { assert false; return null; }
        };
        ProcBody.EmitAll(emitDecl, emitBody);
        Scope.Pop(zz);

        if (!main) return;

        // generate code for main
        {
            Frame frame = target.newFrame("main");
            frame.isGlobal = true;
            Tree.Stm stm = EXP(CALL(NAME(f.name)));
            frags.add(new Frag.Proc(stm, frame));
        }
        {
            Label msg = stringLabel("Attempt to use a null pointer");
            Tree.Stm stm = SEQ(
                    EXP(CALL(badPtr.external("puts"), NAME(msg))),
                    EXP(CALL(badPtr.external("exit"), CONST(1))));
            frags.add(new Frag.Proc(stm, badPtr));
        }
        {
            Label msg = stringLabel("Subscript out of bounds");
            Tree.Stm stm = SEQ(
                    EXP(CALL(badSub.external("puts"), NAME(msg))),
                    EXP(CALL(badSub.external("exit"), CONST(1))));
            frags.add(new Frag.Proc(stm, badSub));
        }
    }

    Tree.Stm InitValues(Scope scope) {
        if (scope == null) return null;
        Tree.Stm stm = null;
        for (Value v : Scope.ToList(scope)) stm = SEQ(stm, LangInit(v));
        for (Value v : Scope.ToList(scope)) stm = SEQ(stm, UserInit(v));
        return stm;
    }

    /* Tree-building helper methods */
    Tree.Exp.MEM MEM(Tree.Exp exp, Tree.Exp.CONST offset, int size) {
        return new Tree.Exp.MEM(exp, offset, size);
    }
    Tree.Exp.MEM MEM(Tree.Exp exp, BigInteger i, int size) {
        return MEM(exp, CONST(i), size);
    }
    Tree.Exp.MEM MEM(Tree.Exp exp, Tree.Exp.CONST offset) {
        return MEM(exp, offset, wordSize);
    }
    Tree.Exp.MEM MEM(Tree.Exp exp) {
        return MEM(exp, CONST(0));
    }
    Tree.Exp TEMP(Temp temp) {
        return new Tree.Exp.TEMP(temp);
    }
    Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
        return (stm == null) ? exp : new Tree.Exp.ESEQ(stm, exp);
    }
    Tree.Exp NAME(Label label) {
        return new Tree.Exp.NAME(label);
    }
    Tree.Exp.CONST CONST(int i) {
        return CONST(BigInteger.valueOf(i));
    }
    Tree.Exp.CONST CONST(BigInteger value) {
        assert value.bitLength() <= wordSize * 8;
        return new Tree.Exp.CONST(value);
    }
    Tree.Exp CALL(Tree.Exp f, Tree.Exp l, Collection<Tree.Exp> args) {
        if (args.size() > currentFrame.maxArgsOut)
            currentFrame.maxArgsOut = args.size();
        assert f != null;
        return new Tree.Exp.CALL(f, l, args.toArray(new Tree.Exp[args.size()]));
    }
    Tree.Exp CALL(Tree.Exp f, Tree.Exp...a) {
        if (a.length > currentFrame.maxArgsOut)
            currentFrame.maxArgsOut = a.length;
        assert f != null;
        return new Tree.Exp.CALL(f, CONST(0), a);
    }

    Tree.Exp.CONST ADD(Tree.Exp.CONST l, Tree.Exp.CONST r) {
        return CONST(l.value.add(r.value));
    }
    Tree.Exp ADD(Tree.Exp l, Tree.Exp r) {
        if (l instanceof Tree.Exp.CONST c && c.value.signum() == 0) return r;
        if (r instanceof Tree.Exp.CONST c && c.value.signum() == 0) return l;
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.ADD, l, r);
    }

    Tree.Exp AND(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.AND, l, r);
    }

    Tree.Exp DIV(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.DIV, l, r);
    }

    Tree.Exp MOD(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.MOD, l, r);
    }

    Tree.Exp.CONST MUL(Tree.Exp.CONST l, Tree.Exp.CONST r) {
        return CONST(l.value.multiply(r.value));
    }
    Tree.Exp MUL(Tree.Exp l, Tree.Exp r) {
        if (l instanceof Tree.Exp.CONST c && c.value.equals(BigInteger.ONE))
            return r;
        if (r instanceof Tree.Exp.CONST c && c.value.equals(BigInteger.ONE))
            return l;
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.MUL, l, r);
    }

    Tree.Exp OR(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.OR, l, r);
    }

    Tree.Exp SLL(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.SLL, l, r);
    }

    Tree.Exp SRA(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.SRA, l, r);
    }

    Tree.Exp SRL(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.SRL, l, r);
    }

    Tree.Exp SUB(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.SUB, l, r);
    }

    Tree.Exp XOR(Tree.Exp l, Tree.Exp r) {
        return new Tree.Exp.BINOP(Tree.Exp.BINOP.Operator.XOR, l, r);
    }

    Tree.Stm SEQ(Tree.Stm l, Tree.Stm r) {
        if (l == null)
            return r;
        if (r == null)
            return l;
        return new Tree.Stm.SEQ(l, r);
    }

    Tree.Stm SEQ(Tree.Stm... a) {
        Tree.Stm stm = null;
        for (Tree.Stm s : a)
            stm = SEQ(stm, s);
        return stm;
    }

    Tree.Stm LABEL(Label label) {
        return new Tree.Stm.LABEL(label);
    }

    Tree.Stm JUMP(Label target) {
        return target == null ? null : new Tree.Stm.JUMP(target);
    }

    Tree.Stm JUMP(Tree.Exp exp, Label[] targets) {
        return new Tree.Stm.JUMP(exp, targets);
    }

    Tree.Stm MOVE(Tree.Exp d, Tree.Exp s) {
        assert d != null;
        assert s != null;
        int dSize = wordSize, sSize = wordSize;
        if (d instanceof Tree.Exp.MEM m) dSize = m.size;
        if (s instanceof Tree.Exp.MEM m) sSize = m.size;
        assert dSize == sSize;
        assert dSize == wordSize;
        return new Tree.Stm.MOVE(d, s);
    }

    Tree.Stm EXP(Tree.Exp exp) {
        return new Tree.Stm.EXP(exp);
    }

    Tree.Stm BEQ(Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.Stm.CJUMP(Tree.Stm.CJUMP.Operator.BEQ, l, r, t, f);
    }

    Tree.Stm BGE(Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.Stm.CJUMP(Tree.Stm.CJUMP.Operator.BGE, l, r, t, f);
    }

    Tree.Stm BGT(Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.Stm.CJUMP(Tree.Stm.CJUMP.Operator.BGT, l, r, t, f);
    }

    Tree.Stm BLE(Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.Stm.CJUMP(Tree.Stm.CJUMP.Operator.BLE, l, r, t, f);
    }

    Tree.Stm BLT(Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.Stm.CJUMP(Tree.Stm.CJUMP.Operator.BLT, l, r, t, f);
    }

    Tree.Stm BNE(Tree.Exp l, Tree.Exp r, Label t, Label f) {
        return new Tree.Stm.CJUMP(Tree.Stm.CJUMP.Operator.BNE, l, r, t, f);
    }

    /**
     * Generate declarations for all the values in a scope.
     * Generate procedure declarations after non-procedure declarations.
     */
    void EnterScope(Scope scope) {
        for (Value v : Scope.ToList(scope))
            if (v instanceof Value.Procedure) /* skip */; else Declare(v);
        for (Value v : Scope.ToList(scope))
            if (v instanceof Value.Procedure) Declare(v); else /* skip */;
    }

    /**
     * Every variable declaration has an associated access.
     * This map keeps track of them.
     */
    final Map<Value.Variable,Frame.Access> accesses
        = new HashMap<Value.Variable,Frame.Access>();

    /**
     * Generate declaration for v.
     */
    void Declare(Value v) {
        class Visitor implements Value.Visitor<Void> {
            public Void visit(Value.Field v) { return null; }
            public Void visit(Value.Formal v) { return null; }
            public Void visit(Value.Method v) { return null; }
            public Void visit(Value.Unit v) { return null; }
            public Void visit(final Value.Procedure v) {
                Compile(v.sig);
                // try to compile the imported type first

                if (v.body == null) {
                    // it's not a local procedure
                    ImportProc(v);
                    return null;
                }
                Frame frame = target.newFrame(GlobalName(v),
                                              v.body.parent != null,
                                              v.body.children != null);
                Frame f = frames.put(v.body, frame);
                assert f == null;
                currentBody = v.body;
                currentFrame = frame;
                Scope zz = Scope.Push(v.localScope);
                EnterScope(v.localScope);
                Scope.Pop(zz);
                return null;
            }
            Void ImportProc(Value.Procedure v) {
                if (v.localScope != null) {
                    Scope zz = Scope.Push(v.localScope);
                    EnterScope(v.localScope);
                    Scope.Pop(zz);
                } else {
                    DeclareFormals(v);
                }
                return null;
            }
            Void DeclareFormals(Value.Procedure p) {
                // declare types for each of the formals
                for (Value v : Scope.ToList(p.sig.scope)) {
                    Value.Formal f = (Value.Formal)v;
                    Compile(TypeOf(f));
                    Compile(f.refType);
                }
                return null;
            }
            public Void visit(Value.Variable v) {
                int size = v.size;
                Compile(TypeOf(v));
                if (v.indirect()) size = wordSize;
                // declare the actual variable
                if (v.external()) {
                    // external
                } else if (v.imported()) {
                    // imported
                } else if (v.global()) {
                    // global
                    Label l = Temp.getLabel(GlobalName(v));
                    frags.add(new Frag.Data(target.record(l, size / wordSize)));
                    if (v.initZero()) v.initDone(true);
                } else {
                    Temp t = null;
                    Frame.Access a;
                    if (!v.up_level() && !v.needsAddress())
                        t = new Temp(GlobalName(v));
                    if (v.formal == null) {
                        // simple local variable
                        if (t == null)
                            a = currentFrame.allocLocal(size);
                        else
                            a = currentFrame.allocLocal(t);
                    } else if (v.indirect()) {
                        // formal passed by reference => param is an address
                        if (t == null)
                            a = currentFrame.allocFormal(wordSize);
                        else
                            a = currentFrame.allocFormal(t);
                    } else {
                        // simple parameter
                        if (t == null)
                            a = currentFrame.allocFormal(size);
                        else
                            a = currentFrame.allocFormal(t);
                    }
                    accesses.put(v, a);
                }
                return null;
            }
            public Void visit(Value.Const v) {
                if (v.exported()) Compile(TypeOf(v));
                return null;
            }
            public Void visit(Value.Tipe v) {
                Compile(v.value);
                return null;
            }
        }
        if (v == null) return;
        if (v.declared()) return;
        v.declared(true);
        v.accept(new Visitor());
    }

    /**
     * Generate language-defined initialization code for v.
     */
    Tree.Stm LangInit(Value v) {
        class Visitor implements Value.Visitor<Tree.Stm> {
            public Tree.Stm visit(Value.Const v) { return null; }
            public Tree.Stm visit(Value.Unit v) { return null; }
            public Tree.Stm visit(Value.Procedure v) { return null; }
            public Tree.Stm visit(Value.Tipe v) { return null; }
            public Tree.Stm visit(Value.Field v) {
                Compile(v.type);
                return null;
            }
            public Tree.Stm visit(Value.Formal v) {
                Compile(v.type);
                Compile(v.refType);
                return null;
            }
            public Tree.Stm visit(Value.Method v) {
                Compile(v.sig);
                return null;
            }
            @Override
            public Tree.Stm visit(Value.Variable v) {
                Tree.Stm stm = null;
                if (v.imported() || v.external()) {
                    v.initDone(true);
                } else if (v.formal != null) {
                    if (v.indirect() && v.formal.refType != null)
                        stm = CopyOpenArray(v, v.formal.refType);
                    // formal parameters don't need any further initialization
                    v.initDone(true);
                } else if (v.indirect() && !v.global()) {
                    // WITH variable bound to a designator
                    v.initDone(true);
                }

                if (v.initDone()) return stm;

                // initialize the value
                if (v.expr != null && !v.up_level() && !v.imported()) {
                    // variable has a user specified init value and
                    // isn't referenced by any nested procedures =>
                    // try to avoid the language defined init and wait
                    // until we get to the user defined
                    // initialization.
                    v.initPending(true);
                } else {
                    stm = InitValue(LoadLValue(v), v.type);
                }
                return stm;
            }
            Tree.Stm CopyOpenArray(Value.Variable v, Type ref) {
                Error.ID(v.token, "open array passed by value unimplemented");
                return null;
            }
        }
        if (v == null) return null;
        if (v.compiled()) return null;
        assert v.checked();
        v.compiled(true);
        return v.accept(new Visitor());
    }

    /**
     * Generate code to load v.
     */
    Tree.Exp Load(final Expr e, Value v) {
        class Visitor implements Value.Visitor<Tree.Exp> {
            public Tree.Exp visit(Value.Field v) { assert false; return null; }
            public Tree.Exp visit(Value.Tipe v) { assert false; return null; }
            public Tree.Exp visit(Value.Unit v) { assert false; return null; }
            public Tree.Exp visit(Value.Method v) { assert false; return null; }
            public Tree.Exp visit(Value.Formal v) {
                Error.ID(v.token, "formal has no default value");
                return null;
            }
            public Tree.Exp visit(Value.Const v) {
                return Compile(v.expr);
            }
            public Tree.Exp visit(Value.Procedure v) {
                if (!(v.sig instanceof Type.Proc.User))
                    Error.Txt(e.token, e.token.image, "builtin operation is not a procedure");
                Declare(v);
                if (v.external()) return target.external(v.extName);
                return NAME(Temp.getLabel(GlobalName(v)));
            }
            public Tree.Exp visit(Value.Variable v) {
                return LoadLValue(v);
            }
        }
        if (v == null) return null;
        assert v.checked();
        return v.accept(new Visitor());
    }

    Tree.Exp LoadLValue(Value.Variable v) {
        Declare(v);
        if (v.initPending()) ForceInit(v);
        Frame.Access a = accesses.get(v);
        Tree.Exp exp;
        if (a == null) {
            assert v.global();
            if (v.external())
                exp = target.external(v.extName);
            else
                exp = NAME(Temp.getLabel(GlobalName(v)));
            if (v.indirect())
                exp = MEM(exp);
            exp = MEM(exp, CONST(0), v.size);
        } else {
            Tree.Exp fp = currentFrame.FP();
            ProcBody home = v.proc;
            for (ProcBody body = currentBody; body != home; body = body.parent)
                fp = frames.get(body).link().exp(fp);
            exp = a.exp(fp);
            if (v.indirect())
                exp = MEM(exp, CONST(0), v.size);
        }
        return exp;
    }

    Tree.Stm ForceInit(Value.Variable v) {
        v.initPending(false);
        return InitValue(LoadLValue(v), v.type);
    }

    /**
     * Generate language-defined initialization value for a variable of type t.
     */
    Tree.Stm InitValue(final Tree.Exp lvalue, Type t) {
        class Visitor implements Type.Visitor<Tree.Stm> {
            public Tree.Stm visit(Type.Named t) {
                if (t.type == null) Resolve(t);
                return InitValue(lvalue, t.type);
            }
            public Tree.Stm visit(Type.Array t) {
                if (IsOpenArray(t) == null)
                    return InitFixed(t);
                else
                    return InitOpen(t);
            }
            Tree.Stm InitFixed(Type.Array t) {
                // TODO
                //
                // Generate a loop to initialize each element of the fixed
                // size array.
                // Initialize each element using a recursive call to InitValue.

                Tree.Exp.MEM a = (Tree.Exp.MEM)lvalue;
                Tree.Stm stm = Force(a);

//                int i = (((Expr.Int) ConstValue(t.number)).value).intValue();
//                int j = 1;
//                int eleSize = t.element.size;
//
//                while (j <= i) {
//                    stm = SEQ(stm, InitValue(MEM(a.exp, ADD(CONST(eleSize*(j-1)), a.offset), eleSize), t.element));
//                    j = j+1;
//                }

                return null;
            }
            Tree.Stm InitOpen(Type.Array t) {
                // Extra Credit
                return null;
            }
            public Tree.Stm visit(Type.Err t) {
                assert false; return null;
            }
            public Tree.Stm visit(Type.Int t) {
                return MOVE(lvalue, CONST(0));
            }
            public Tree.Stm visit(Type.Bool t) {
                return MOVE(lvalue, CONST(0));
            }
            public Tree.Stm visit(Type.Ref t) {
                return MOVE(lvalue, CONST(0));
            }
            public Tree.Stm visit(Type.Proc t) {
                return MOVE(lvalue, CONST(0)); }
            public Tree.Stm visit(Type.Object t) {
                return MOVE(lvalue, CONST(0));
            }
            public Tree.Stm visit(Type.Record t) {
                // grab the record's lvalue
                Tree.Exp.MEM a = (Tree.Exp.MEM)lvalue;
                Tree.Stm stm = Force(a);
                for (Value.Field f: t.fields) {
                    if (f.expr == null) {
                        stm = SEQ(stm,
                                  InitValue(MEM(a.exp,
                                                ADD(a.offset,
                                                    CONST(f.offset)),
                                                f.type.size),
                                            f.type));
                    } else {
                        assert false;
                    }
                }
                return stm;
            }
        }
        t = Check(t);
        return t.accept(new Visitor());
    }

    /**
     * Generate user-defined initialization code for v.
     */
    Tree.Stm UserInit(Value v) {
        class Visitor implements Value.Visitor<Tree.Stm> {
            public Tree.Stm visit(Value.Field v) { return null; }
            public Tree.Stm visit(Value.Formal v) { return null; }
            public Tree.Stm visit(Value.Method v) { return null; }
            public Tree.Stm visit(Value.Procedure v) { return null; }
            public Tree.Stm visit(Value.Const v) { return null; }
            public Tree.Stm visit(Value.Unit v) { return null; }
            public Tree.Stm visit(Value.Tipe v) { return null; }
            public Tree.Stm visit(Value.Variable v) {
                Tree.Stm stm = null;
                if (v.expr != null && !v.initDone() && !v.imported()) {
                    v.initPending(false);
                    stm = EmitAssign(LoadLValue(v), v.type, v.expr);
                }
                v.initDone(true);
                return stm;
            }
        }
        if (v == null) return null;
        return v.accept(new Visitor());
    }

    /**
     * Compile a type t.
     */
    final Map<Type,Type> compiled = new HashMap<Type,Type>();
    {
        compiled.put(Check(Type.Bool.T),   Type.Bool.T);
        compiled.put(Check(Type.Int.T),    Type.Int.T);
        compiled.put(Check(Type.Null.T),   Type.Null.T);
        compiled.put(Check(Type.Root.T),   Type.Root.T);
        compiled.put(Check(Type.Refany.T), Type.Refany.T);
        compiled.put(Check(Type.Addr.T),   Type.Addr.T);
        compiled.put(Check(Type.Text.T),   Type.Text.T);
        compiled.put(Check(Type.Err.T),    Type.Err.T);
    }
    void Compile(Type t) {
        class Visitor implements Type.Visitor<Void> {
            public Void visit(Type.Bool t) { return null; }
            public Void visit(Type.Int t) { return null; }
            public Void visit(Type.Err t) { return null; }
            public Void visit(Type.Named t) {
                if (t.type == null) Resolve(t);
                if (t.type != null) Compile(t.type);
                return null;
            }
            public Void visit(Type.Object t) {
                Compile(t.parent);
                GetOffsets(t);
                for (Value.Field f : t.fields) Compile(f.type);
                for (Value.Method m : t.methods) Compile(m.sig);
                for (Value.Method o : t.overrides) Compile(o.sig);
                Label[] defaults = new Label[(t.methodOffset + t.methodSize) / wordSize];
                GenMethods(t, defaults);
                String vtable = target.vtable(Temp.getLabel(GlobalUID(t)),
                                              Arrays.asList(defaults));
                frags.add(new Frag.Data(vtable));
                return null;
            }
            void GenMethods(Type.Object t, Label[] defaults) {
                if (t == null) return;
                GenMethods(Is(t.parent, Type.Object.class), defaults);
                for (Value v : Scope.ToList(t.methodScope)) {
                    Value.Method m = (Value.Method)v;
                    Value.Method p = PrimaryMethodDeclaration(m.parent, m);
                    defaults[(p.parent.methodOffset + m.offset) / wordSize]
                        = m.value == null ? badPtr.name : Temp.getLabel(GlobalName(m.value));
                }
            }
            public Void visit(Type.Array t) {
                Compile(t.element);
                return null;
            }
            public Void visit(Type.Proc t) {
                Compile(t.result);
                for (Value v : Scope.ToList(t.scope)) Compile(TypeOf(v));
                return null;
            }
            public Void visit(Type.Ref t) {
                Compile(t.target);
                return null;
            }
            public Void visit(Type.Record t) {
                for (Value.Field f: t.fields) Compile(f.type);
                return null;
            }
        }
        if (t == null) return;
        Type u = Check(t);
        if (compiled.put(u, u) != null) return;
        t.accept(new Visitor());
    }

    Tree.Stm EmitAssign(final Tree.Exp lhs, final Type tlhs, final Expr rhs) {
        class Visitor implements Type.Visitor<Tree.Stm> {
            public Tree.Stm visit(Type.Bool t) {
                return AssignOrdinal(lhs, tlhs, rhs);
            }
            public Tree.Stm visit(Type.Int t) {
                return AssignOrdinal(lhs, tlhs, rhs);
            }
            public Tree.Stm visit(Type.Err t) {
                return null;
            }
            public Tree.Stm visit(Type.Named t) {
                assert false; return null;
            }
            public Tree.Stm visit(Type.Object t) {
                return AssignReference(lhs, tlhs, rhs);
            }
            public Tree.Stm visit(Type.Array t) {
                return AssignArray((Tree.Exp.MEM)lhs, tlhs, rhs);
            }
            public Tree.Stm visit(Type.Proc t) {
                return AssignProcedure(lhs, tlhs, rhs);
            }
            public Tree.Stm visit(Type.Ref t) {
                return AssignReference(lhs, tlhs, rhs);
            }
            public Tree.Stm visit(Type.Record t) {
                return AssignRecord((Tree.Exp.MEM)lhs, tlhs, rhs);
            }
        }
        final Type t = Base(tlhs); // strip renaming and packing
        assert t.checked();
        assert tlhs.checked();
        return t.accept(new Visitor());
    }
    Tree.Stm AssignOrdinal(Tree.Exp lhs, Type tlhs, Expr rhs) {
        Tree.Exp exp = EmitChecks(rhs, Min(tlhs), Max(tlhs));
        return MOVE(lhs, exp);
    }
    Tree.Stm AssignReference(Tree.Exp lhs, Type tlhs, Expr rhs) {
        // TODO
        Tree.Exp exp = Compile(rhs);
        return MOVE(lhs, exp);
    }
    Tree.Stm AssignProcedure(Tree.Exp lhs, Type tlhs, Expr rhs) {
        Tree.Exp exp = Compile(rhs);
        return MOVE(lhs, exp);
    }
    Tree.Stm AssignRecord(Tree.Exp.MEM lhs, Type tlhs, Expr rhs) {
        Type.Record t = Is(tlhs, Type.Record.class);
        Tree.Exp.MEM a = (Tree.Exp.MEM)Compile(rhs);
        return EXP(CALL(target.external("memcpy"),
                        ADD(lhs.exp, lhs.offset),
                        ADD(a.exp, a.offset),
                        CONST(t.size)));
    }
    Tree.Stm AssignArray(Tree.Exp.MEM lhs, Type tlhs, Expr e_rhs) {
        Tree.Stm stm = null;
        Type trhs = TypeOf(e_rhs);
        Type.Array openRHS = IsOpenArray(trhs);
        Type.Array openLHS = IsOpenArray(tlhs);
        // capture the lhs and rhs pointers
        if (openRHS != null || openLHS != null) stm = SEQ(stm, Force(lhs));
        Tree.Exp.MEM rhs = (Tree.Exp.MEM)Compile(e_rhs);
        if (openRHS != null || openLHS != null) stm = SEQ(stm, Force(rhs));
        if (openRHS != null && openLHS != null) {
            stm = SEQ(stm, GenOpenArraySizeChecks(lhs, rhs, tlhs, trhs));
            stm = SEQ(stm, GenOpenArrayCopy(lhs, rhs, tlhs, trhs));
        } else if (openRHS != null) {
            stm = SEQ(stm, GenOpenArraySizeChecks(lhs, rhs, tlhs, trhs));
            stm = SEQ(stm, EXP(CALL(target.external("memmove"),
                                    ADD(lhs.exp, lhs.offset),
                                    rhs,
                                    CONST(tlhs.size))));
        } else if (openLHS != null) {
            stm = SEQ(stm, GenOpenArraySizeChecks(lhs, rhs, tlhs, trhs));
            stm = SEQ(stm, EXP(CALL(target.external("memmove"),
                                    lhs,
                                    ADD(rhs, rhs.offset),
                                    CONST(trhs.size))));
        } else {
            stm = SEQ(stm, EXP(CALL(target.external("memmove"),
                                    ADD(lhs.exp, lhs.offset),
                                    ADD(rhs.exp, rhs.offset),
                                    CONST(trhs.size))));
        }
        return stm;
    }
    Tree.Stm GenOpenArraySizeChecks(Tree.Exp.MEM lhs, Tree.Exp.MEM rhs, Type tlhs, Type trhs) {
        Tree.Stm stm = null;
        Type.Array alhs, arhs;
        int n = 0;
        Label badSub = currentFrame.badSub();
        if (badSub == null) return null;
        while ((alhs = Is(tlhs, Type.Array.class)) != null &&
               (arhs = Is(trhs, Type.Array.class)) != null) {
            Expr
                nlhs = ConstValue(alhs.number),
                nrhs = ConstValue(arhs.number);
            if (nlhs != null && nrhs != null) return stm;
            Label l = new Label();
            if (nlhs != null) {
                stm = SEQ(stm, BNE(
                        Compile(nlhs),
                        MEM(rhs.exp, ADD(rhs.offset, CONST((n+1)*wordSize))),
                        badSub, l), LABEL(l));
            } else if (nrhs != null) {
                stm = SEQ(stm, BNE(
                        MEM(lhs.exp, ADD(lhs.offset, CONST((n+1)*wordSize))),
                        Compile(nrhs),
                        badSub, l), LABEL(l));
            } else {
                stm = SEQ(stm, BNE(
                        MEM(lhs.exp, ADD(lhs.offset, CONST((n+1)*wordSize))),
                        MEM(rhs.exp, ADD(rhs.offset, CONST((n+1)*wordSize))),
                        badSub, l), LABEL(l));
            }
            ++n;
            tlhs = alhs.element;
            trhs = arhs.element;
        }
        return stm;
    }
    Tree.Stm GenOpenArrayCopy(Tree.Exp.MEM lhs, Tree.Exp.MEM rhs, Type tlhs, Type trhs) {
        assert rhs.exp instanceof Tree.Exp.TEMP;
        assert lhs.exp instanceof Tree.Exp.TEMP;
        Tree.Stm stm = null;
        int lhsDepth = OpenDepth(tlhs);
        int rhsDepth = OpenDepth(trhs);
        assert lhsDepth > 0 && rhsDepth > 0;
        int depth = Math.min(lhsDepth, rhsDepth);
        Temp max = new Temp();
        stm = SEQ(stm, MOVE(TEMP(max),
                            MEM(rhs.exp, ADD(rhs.offset, CONST(wordSize)))));
        for (int i = 1; i < depth; i++) {
            stm = SEQ(stm, MOVE(TEMP(max),
                                MUL(TEMP(max),
                                    MEM(rhs.exp,
                                        ADD(rhs.offset,
                                            CONST((i+1)*wordSize))))));
        }
        Type eltType = OpenType((lhsDepth < rhsDepth) ? tlhs : trhs);
        return SEQ(stm,
                   EXP(CALL(target.external("memmove"), lhs, rhs,
                            MUL(TEMP(max),
                                CONST(eltType.size)))));
    }

    Tree.Stm Compile(Stmt s, final Label loopExit) {
        class Visitor implements Stmt.Visitor<Tree.Stm> {
            public Tree.Stm visit(Stmt.Assign s) {
                return EmitAssign(Compile(s.lhs), TypeOf(s.lhs), s.rhs);
            }
            public Tree.Stm visit(Stmt.Call s) {
                // TODO
                Tree.Stm stm = null;
                Expr.Call exprCall = (Expr.Call) s.expr;
                Label l = Temp.getLabel(exprCall.proc.token.image);
                Tree.Exp[] args;


                if ((exprCall.args).length == 0) {
                    args = new Tree.Exp[0];
                    return EXP(CALL(NAME(l), args));
                } else {
                    args = new Tree.Exp[(exprCall.args).length];
                    int i = 0;
                    for (Expr arg : exprCall.args) {
                        args[i] = Compile(arg);
                        i++;
                    }
                    return EXP(CALL(NAME(l), args));
                }
            }
            public Tree.Stm visit(Stmt.Break s) {
                // TODO
//                Tree.Stm stm = null;
                return null;
            }
            public Tree.Stm visit(Stmt.For s) {
                Tree.Stm stm = null;
                Expr step, limit, from;
                BigInteger step_val = BigInteger.ZERO;
                BigInteger limit_val = BigInteger.ZERO;
                BigInteger from_val = BigInteger.ZERO;
                Temp t_index, t_to, t_by;

                t_index = new Temp();
                from = ConstValue(s.from);
                if (from == null) {
                    stm = SEQ(stm, MOVE(TEMP(t_index), Compile(s.from)));
                } else {
                    from_val = ((Expr.Int)from).value;
                    stm = SEQ(stm, MOVE(TEMP(t_index), CONST(from_val)));
                }
                t_to = new Temp();
                limit = ConstValue(s.to);
                if (limit == null) {
                    stm = SEQ(stm, MOVE(TEMP(t_to), Compile(s.to)));
                } else {
                    limit_val = ((Expr.Int)limit).value;
                    stm = SEQ(stm, MOVE(TEMP(t_to), CONST(limit_val)));
                }
                t_by = new Temp();
                step = ConstValue(s.by);
                if (step == null) {
                    stm = SEQ(stm, MOVE(TEMP(t_by), Compile(s.by)));
                } else {
                    step_val = ((Expr.Int)step).value;
                    stm = SEQ(stm, MOVE(TEMP(t_by), Compile(step)));
                }
                Label l_top = new Label();
                Label l_test = new Label();
                Label l_exit = new Label();

                Scope zz = Scope.Push(s.scope);
                EnterScope(s.scope);
                stm = SEQ(stm, InitValues(s.scope));

                if (from == null || limit == null || step == null) {
                    // we don't know all three values
                    stm = SEQ(stm, JUMP(l_test));
                } else if (step_val.signum() >= 0 && from_val.compareTo(limit_val) <= 0) {
                    // we know we'll execute the loop at least once
                } else if (step_val.signum() <= 0 && from_val.compareTo(limit_val) >= 0) {
                    // we know we'll execute the loop at least once
                } else {
                    // we won't execute the loop
                    stm = SEQ(stm, JUMP(l_test));
                }
                stm = SEQ(stm, LABEL(l_top));

                // make the user's variable equal to the counter
                stm = SEQ(stm, MOVE(LoadLValue(s.var), TEMP(t_index)));

                stm = SEQ(stm, Compile(s.stmt, l_exit));

                // increment the counter
                stm = SEQ(stm, MOVE(TEMP(t_index), ADD(TEMP(t_index), TEMP(t_by))));

                // generate the loop test
                stm = SEQ(stm, LABEL(l_test));
                if (step != null) {
                    // constant step value
                    if (step_val.signum() >= 0)
                        stm = SEQ(stm, BLE(TEMP(t_index), TEMP(t_to), l_top, l_exit));
                    else
                        stm = SEQ(stm, BGE(TEMP(t_index), TEMP(t_to), l_top, l_exit));
                } else {
                    // variable step value
                    Label l_up = new Label();
                    Label l_down = new Label();

                    stm = SEQ(stm, BLT(TEMP(t_by), CONST(0), l_down, l_up));
                    stm = SEQ(stm, LABEL(l_up));
                    stm = SEQ(stm, BLE(TEMP(t_index), TEMP(t_to), l_top, l_exit));
                    stm = SEQ(stm, LABEL(l_down));
                    stm = SEQ(stm, BGE(TEMP(t_index), TEMP(t_to), l_top, l_exit));
                }
                Scope.Pop(zz);
                return SEQ(stm, LABEL(l_exit));
            }
            public Tree.Stm visit(Stmt.If s) {

                Label t = new Label();
                if (s.stmt == null) {
                    Label f = new Label();
                    return
                        SEQ(CompileBranch(s.expr, t, f),
                            LABEL(t),
                            Compile(s.block, loopExit),
                            LABEL(f));
                } else {
                    // TODO
                    Label f = new Label();
                    return
                            SEQ(CompileBranch(s.expr, t, f),
                                    LABEL(t),
                                    Compile(s.block, loopExit),
                                    LABEL(f),
                                    Compile(s.stmt, loopExit));
                }
            }
            public Tree.Stm visit(Stmt.Loop s) {
                // TODO
                Label test = new Label();
                Label done = new Label();


                return null;
            }
            public Tree.Stm visit(Stmt.Return s) {
                if (s.expr == null)
                    return JUMP(returnLabel);
                return SEQ(MOVE(currentFrame.RV(), Compile(s.expr)), JUMP(returnLabel));
            }
            public Tree.Stm visit(Stmt.Block s) {
                Tree.Stm stm = null;
                if (s.scope != null) {
                    Scope zz = Scope.Push(s.scope);
                    EnterScope(s.scope);
                    stm = SEQ(stm, InitValues(s.scope));
                    for (Stmt stmt : s.body) {
                        stm = SEQ(stm, Compile(stmt, loopExit));
                    }
                    Scope.Pop(zz);
                } else {
                    for (Stmt stmt : s.body)
                        stm = SEQ(stm, Compile(stmt, loopExit));
                }
                return stm;
            }
        }
        if (s == null) return null;
        return s.accept(new Visitor());
    }

    Tree.Exp EmitChecks(Expr e, BigInteger min, BigInteger max) {
        Expr x = ConstValue(e);
        if (x != null) e = x;
        Tree.Exp exp = Compile(e);
        Label err = currentFrame.badSub();
        if (err == null) return exp;
        Bounds b = GetBounds(e);
        if (b.min.compareTo(min) < 0 && b.max.compareTo(max) > 0) {
            Temp t = new Temp();
            Label l1 = new Label();
            Label l2 = new Label();
            return ESEQ(SEQ(
                    MOVE(TEMP(t), exp),
                    BLT(TEMP(t), CONST(min), err, l1),
                    LABEL(l1),
                    BGT(TEMP(t), CONST(max), err, l2),
                    LABEL(l2)),
                    TEMP(t));
        } else if (b.min.compareTo(min) < 0) {
            if (b.max.compareTo(min) < 0)
                Error.Warn(e.token, "value out of range");
            Temp t = new Temp();
            Label l = new Label();
            return ESEQ(SEQ(
                    MOVE(TEMP(t), exp),
                    BLT(TEMP(t), CONST(min), err, l),
                    LABEL(l)),
                    TEMP(t));
        } else if (b.max.compareTo(max) > 0) {
            if (b.min.compareTo(max) > 0)
                Error.Warn(e.token, "value out of range");
            Temp t = new Temp();
            Label l = new Label();
            return ESEQ(SEQ(
                    MOVE(TEMP(t), exp),
                    BGT(TEMP(t), CONST(max), err, l),
                    LABEL(l)),
                    TEMP(t));
        } else {
            return exp;
        }
    }

    final HashMap<Expr,Temp> passObject = new HashMap<Expr,Temp>();

    Tree.Exp Compile(Expr e) {
        class Visitor implements Expr.Visitor<Tree.Exp> {
            public Tree.Exp visit(Expr.Proc e) {
                return Load(e, e.proc);
            }
            public Tree.Exp visit(Expr.Method e) {
                Value.Method m = e.method;
                return MEM(NAME(Temp.getLabel(GlobalUID(m.parent))),
                           CONST(m.parent.methodOffset + m.offset));
            }
            public Tree.Exp visit(Expr.Equal e) {
                Tree.Exp l = Compile(e.left);
                Tree.Exp r = Compile(e.right);
                Label t = new Label();
                Label f = new Label();
                Temp temp = new Temp();
                Tree.Stm stm;
                switch(e.op) {
                case EQ: stm = BEQ(l, r, t, f); break;
                case NE: stm = BNE(l, r, t, f); break;
                default: assert false; stm = null;
                }
                return
                    ESEQ(SEQ(MOVE(TEMP(temp), CONST(0)),
                             stm,
                             LABEL(t), MOVE(TEMP(temp), CONST(1)),
                             LABEL(f)),
                         TEMP(temp));
            }
            public Tree.Exp visit(Expr.Compare e) {
                // TODO
                Tree.Exp l = Compile(e.left);
                Tree.Exp r = Compile(e.right);
                Label t = new Label();
                Label f = new Label();
                Temp temp = new Temp();
                Tree.Stm stm;
                switch(e.op) {
                    case GT: stm = BGT(l, r, t, f); break;
                    case GE: stm = BGE(l, r, t, f); break;
                    case LT: stm = BLT(l, r, t, f); break;
                    case LE: stm = BLE(l, r, t, f); break;
                    default: assert false; stm = null;
                }
                return
                        ESEQ(SEQ(MOVE(TEMP(temp), CONST(0)),
                                stm,
                                LABEL(t), MOVE(TEMP(temp), CONST(1)),
                                LABEL(f)),
                                TEMP(temp));


            }
            public Tree.Exp visit(Expr.Address e) {
                return CONST(e.value.intValue());
            }
            public Tree.Exp visit(Expr.Check e) {
                Tree.Exp exp = Compile(e.expr);
                Label badSub = currentFrame.badSub();
                if (badSub == null) return exp;
                if (e.min != null && e.max != null) {
                    Temp t = new Temp();
                    Label l1 = new Label();
                    Label l2 = new Label();
                    return
                        ESEQ(SEQ(MOVE(TEMP(t), exp),
                                 BLT(TEMP(t), CONST(e.min), badSub, l1),
                                 LABEL(l1),
                                 BGT(TEMP(t), CONST(e.max), badSub, l2),
                                 LABEL(l2)),
                             TEMP(t));
                } else if (e.min != null) {
                    Temp t = new Temp();
                    Label l = new Label();
                    return
                        ESEQ(SEQ(MOVE(TEMP(t), exp),
                                 BLT(TEMP(t), CONST(e.min), badSub, l),
                                 LABEL(l)),
                             TEMP(t));
                } else if (e.max != null) {
                    Temp t = new Temp();
                    Label l = new Label();
                    return
                        ESEQ(SEQ(MOVE(TEMP(t), exp),
                                 BGT(TEMP(t), CONST(e.max), badSub, l),
                                 LABEL(l)),
                             TEMP(t));
                } else {
                    return exp;
                }
            }
            public Tree.Exp visit(final Expr.Call ce) {
                return ce.procType.accept(new Type.Proc.Visitor<Tree.Exp>() {
                    public Tree.Exp visit(Type.Proc.User _v) {
                        Expr proc = ce.proc;
                        assert ce.proc.checked;
                        Type p_type = TypeOf(proc);
                        if (p_type == null)
                            p_type = MethodType(Is(proc, Expr.Qualify.class));
                        p_type = Base(p_type);
                        // grab the formals list
                        Value.Procedure p_value = IsProcedureLiteral(proc);
                        Tree.Exp fp = CONST(0);
                        if (p_value != null) {
                            ProcBody caller = currentBody;
                            ProcBody callee = p_value.body;
                            if (callee != null && callee.parent != null) {
                                fp = frames.get(caller).FP();
                                for (ProcBody body = caller;
                                     body != callee.parent;
                                     body = body.parent)
                                    fp = frames.get(body).link().exp(fp);
                            }
                        }
                        Tree.Exp exp = Compile(proc);
                        LinkedList<Tree.Exp>args = new LinkedList<Tree.Exp>();
                        Temp t = passObject.get(proc);
                        if (t != null) args.add(TEMP(t));
                        int n = 0;
                        for (Value f : Formals(Is(p_type, Type.Proc.class))) {
                            args.add(EmitArg(Is(f, Value.Formal.class),
                                             ce.args[n++]));
                        }
                        return CALL(exp, fp, args);
                    }
                    Tree.Exp EmitArg(final Value.Formal formal,
                                     final Expr actual) {
                        return formal.type.accept(new Type.Visitor<Tree.Exp>() {
                                public Tree.Exp visit(Type.Err t) {
                                    assert false; return null;
                                }
                                public Tree.Exp visit(Type.Named t) {
                                    assert false; return null;
                                }
                                public Tree.Exp visit(Type.Int t) {
                                    return GenOrdinal(formal, actual);
                                }
                                public Tree.Exp visit(Type.Bool t) {
                                    return GenOrdinal(formal, actual);
                                }
                                public Tree.Exp visit(Type.Ref t) {
                                    return GenReference(formal, actual);
                                }
                                public Tree.Exp visit(Type.Object t) {
                                    return GenReference(formal, actual);
                                }
                                public Tree.Exp visit(Type.Proc t) {
                                    return GenProcedure(formal, actual);
                                }
                                public Tree.Exp visit(Type.Record t) {
                                    return GenRecord(formal, actual);
                                }
                                public Tree.Exp visit(Type.Array t) {
                                    return GenArray(formal, actual);
                                }
                            });
                    }
                    Tree.Exp GenOrdinal(Value.Formal f, Expr actual) {
                        BigInteger min = Min(f.type), max = Max(f.type);
                        switch (f.mode) {
                        case VALUE: return EmitChecks(actual, min, max);
                        case VAR: return CompileAddress(actual);
                        case READONLY:
                            if (!IsEqual(f.type, TypeOf(actual), null)) {
                                return GenCopy(f.type, EmitChecks(actual, min, max));
                            } else if (IsDesignator(actual)) {
                                return CompileAddress(actual);
                            } else // non-designator, same type
                                return GenCopy(f.type, Compile(actual));
                        }
                        assert false; return null;
                    }
                    Tree.Exp GenReference(Value.Formal f, Expr actual) {
                        switch(f.mode) {
                        case VALUE: return Compile(actual);
                        case VAR: return CompileAddress(actual);
                        case READONLY:
                            if (!IsEqual(f.type, TypeOf(actual), null)) {
                                return GenCopy(f.type, Compile(actual));
                            } else if (IsDesignator(actual)) {
                                return CompileAddress(actual);
                            } else // non-designator, same type
                                return GenCopy(f.type, Compile(actual));
                        }
                        assert false; return null;
                    }
                    Tree.Exp GenProcedure(Value.Formal f, Expr actual) {
                        switch(f.mode) {
                        case VALUE: return GenClosure(actual);
                        case VAR: return CompileAddress(actual);
                        case READONLY:
                            if (IsDesignator(actual)) {
                                return CompileAddress(actual);
                            } else {
                                return GenCopy(f.type, GenClosure(actual));
                            }
                        }
                        assert false; return null;
                    }
                    final int MarkerOffset = 0;
                    final int ProcOffset = MarkerOffset + wordSize;
                    final int LinkOffset = ProcOffset + wordSize;
                    final int ClosureSize = LinkOffset + wordSize;
                    Tree.Exp GenClosure(Expr actual) {
                        Value.Procedure v = RequiresClosure(ce.proc);
                        if (v == null) return Compile(actual);
                        // actual is a nested procedure literal passed by value
                        if (IsExternalProcedure(ce.proc)) {
                            Error.Warn(actual.token, "passing nested procedure to external procedure");
                        }
                        Error.ID(actual.token, "passing closure unimplemented");
                        return null;
                    }
                    Value.Procedure RequiresClosure(Expr e) {
                        Value.Procedure proc = IsProcedureLiteral(e);
                        if (IsNested(proc)) return proc;
                        return null;
                    }
                    boolean IsExternalProcedure(Expr e) {
                        Value.Procedure proc = IsProcedureLiteral(e);
                        if (proc != null) return IsExternal(proc);
                        return false;
                    }
                    Tree.Exp GenRecord(Value.Formal f, Expr actual) {
                        switch(f.mode) {
                        case VALUE: return Compile(actual);
                        case VAR: return CompileAddress(actual);
                        case READONLY:
                            if (IsDesignator(actual))
                                return CompileAddress(actual);
                            else
                                return Compile(actual);
                        }
                        assert false; return null;
                    }
                    Tree.Exp GenArray(Value.Formal f, Expr actual) {
                        Type t = TypeOf(actual);
                        switch(f.mode) {
                        case VALUE: {
                            Tree.Exp.MEM mem
                                = ReshapeArray(f.type, t, Compile(actual));
                            if (IsOpenArray(f.type) != null)
                                return ADD(mem.exp, mem.offset);
                            return mem;
                        }
                        case VAR: {
                            Tree.Exp.MEM mem
                                = ReshapeArray(f.type, t, Compile(actual));
                            return ADD(mem.exp, mem.offset);
                        }
                        case READONLY:
                            if (!IsEqual(f.type, t, null)) {
                                Tree.Exp.MEM mem
                                    = ReshapeArray(f.type, t, Compile(actual));
                                return ADD(mem.exp, mem.offset);
                            } else if (IsDesignator(actual)) {
                                return CompileAddress(actual);
                            } else {
                                return Compile(actual);
                            }
                        }
                        assert false; return null;
                    }
                    Tree.Exp.MEM ReshapeArray(Type tlhs, Type trhs, Tree.Exp actual) {
                        if (IsEqual(tlhs, trhs, null))
                            return (Tree.Exp.MEM)actual;
                        int d_lhs = OpenDepth(tlhs);
                        int d_rhs = OpenDepth(trhs);
                        if (d_lhs == d_rhs)
                            return (Tree.Exp.MEM)actual;
                        // capture the actual
                        Tree.Exp.MEM rhs = (Tree.Exp.MEM)actual;
                        Tree.Stm stm = Force(rhs);
                        if (d_lhs > d_rhs) {
                            // build a bigger dope vector
                            Frame.Access a = currentFrame.allocLocal((d_lhs + 1) * wordSize);
                            Tree.Exp.MEM lhs = (Tree.Exp.MEM)a.exp(currentFrame.FP());
                            stm = SEQ(stm, Force(lhs));
                            // copy the data pointer
                            stm = SEQ(stm, MOVE(MEM(lhs.exp, lhs.offset), d_rhs > 0 ? rhs : ADD(rhs.exp, rhs.offset)));
                            // fill in the sizes
                            for (int i = 0; i < d_lhs; i++) {
                                Type.Array b = Is(trhs, Type.Array.class);
                                int o = (i+1) * wordSize;
                                Expr nrhs = ConstValue(b.number);
                                if (nrhs == null)
                                    stm = SEQ(stm,
                                              MOVE(MEM(lhs.exp,
                                                       ADD(lhs.offset,
                                                           CONST(o))),
                                                   MEM(rhs.exp,
                                                       ADD(rhs.offset,
                                                           CONST(o)))));
                                else
                                    stm = SEQ(stm,
                                              MOVE(MEM(lhs.exp,
                                                       ADD(lhs.offset,
                                                           CONST(o))),
                                                   Compile(nrhs)));
                                trhs = b.element;
                            }
                            // leave the result
                            return MEM(ESEQ(stm, lhs.exp), lhs.offset, lhs.size);
                        } else {
                            assert d_lhs < d_rhs;
                            // check some array bounds
                            // don't build a smaller dope vector just reuse the existing one
                            Type t = OpenType(tlhs);
                            Label badSub = currentFrame.badSub();
                            if (badSub != null)
                                for (int i = d_lhs; i < d_rhs; i++) {
                                    int o = (i+1) * wordSize;
                                    Type.Array b = Is(t, Type.Array.class);
                                    Expr nrhs = ConstValue(b.number);
                                    assert nrhs != null;
                                    Label ok = new Label();
                                    stm = SEQ(stm,
                                              BNE(MEM(rhs.exp,
                                                      ADD(rhs.offset,
                                                          CONST(o))),
                                                  Compile(nrhs), badSub, ok),
                                            LABEL(ok));
                                    t = b.element;
                                }
                            // leave the old dope vector as the result
                            if (d_lhs <= 0)
                                return MEM(ESEQ(stm,
                                                MEM(rhs.exp,
                                                    rhs.offset)),
                                           CONST(0),
                                           tlhs.size);
                            return MEM(ESEQ(stm, rhs.exp),
                                       rhs.offset,
                                       (d_lhs + 1) * wordSize);
                        }
                    }
                    Tree.Exp GenCopy(Type t, Tree.Exp exp) {
                        Frame.Access a = currentFrame.allocLocal(t.size);
                        Tree.Exp.MEM lhs = (Tree.Exp.MEM)a.exp(currentFrame.FP());
                        Tree.Stm stm = Force(lhs);
                        if (IsStructured(t)) {
                            Tree.Exp.MEM rhs = (Tree.Exp.MEM)exp;
                            return ESEQ(SEQ(stm,
                                    EXP(CALL(target.external("memcpy"), ADD(lhs.exp, lhs.offset), ADD(rhs.exp, rhs.offset), CONST(t.size)))),
                                    ADD(lhs.exp, lhs.offset));
                        } else {
                            return ESEQ(SEQ(stm,
                                    MOVE(lhs, exp)),
                                    ADD(lhs.exp, lhs.offset));
                        }
                    }
                    Tree.Exp CompileAddress(Expr e) {
                        Tree.Exp.MEM mem = (Tree.Exp.MEM)Compile(e);
                        return ADD(mem.exp, mem.offset);
                    }
                    public Tree.Exp visit(Type.Proc.First _v) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        if (t == null) t = TypeOf(e);
                        Compile(t);
                        Type.Array a = Is(t, Type.Array.class);
                        if (a != null) return CONST(0);
                        assert IsOrdinal(t);
                        return CONST(Min(t));
                    }
                    public Tree.Exp visit(Type.Proc.Last _v) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        if (t == null) t = TypeOf(e);
                        Compile(t);
                        Type.Array a = Is(t, Type.Array.class);
                        if (a != null) {
                            Expr.Int n
                                = Is(ConstValue(a.number), Expr.Int.class);
                            if (n == null) {
                                // open array
                                Tree.Exp.MEM mem = (Tree.Exp.MEM)Compile(e);
                                return SUB(MEM(mem.exp,
                                               ADD(mem.offset,
                                                   CONST(wordSize))),
                                           CONST(1));
                            }
                            return CONST(n.value.subtract(BigInteger.ONE));
                        }
                        assert IsOrdinal(t);
                        return CONST(Max(t));
                    }
                    public Tree.Exp visit(Type.Proc.Number _v) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        if (t == null) t = TypeOf(e);
                        Compile(t);
                        Type.Array a = Is(t, Type.Array.class);
                        if (a != null) {
                            Expr.Int n
                                = Is(ConstValue(a.number), Expr.Int.class);
                            if (n == null) {
                                // open array
                                Tree.Exp.MEM mem = (Tree.Exp.MEM)Compile(e);
                                return MEM(mem.exp,
                                           ADD(mem.offset,
                                               CONST(wordSize)));
                            }
                            return CONST(n.value); 
                        }
                        assert IsOrdinal(t);
                        BigInteger min = Min(t), max = Max(t);
                        if (max.compareTo(min) < 0) return CONST(0);
                        max = max.subtract(min).add(BigInteger.ONE);
                        if (max.compareTo(MAX_VALUE) > 0) {
                            Error.Warn(ce.token, "result of Number too large");
                            Label badSub = currentFrame.badSub();
                            return ESEQ(JUMP(badSub), CONST(0));
                        }
                        return CONST(max);
                    }
                    public Tree.Exp visit(Type.Proc.New _v) {
                        Expr e = ce.args[0];
                        Type t = IsType(e);
                        assert t != null;
                        Compile(t);
                        if (Is(t, Type.Ref.class) != null)
                            return GenRef(t);
                        if (Is(t, Type.Object.class) != null)
                            return GenObject(t);
                        Error.Msg(ce.token, "New must be applied to a variable of a reference type");
                        return null;
                    }
                    Tree.Exp GenRef(Type t) {
                        Type r = Strip(((Type.Ref)t).target);
                        if (IsOpenArray(r) != null) return GenOpenArray(r);
                        if (Is(Base(r), Type.Record.class) != null)
                            return GenRecord(r);
                        Temp result = new Temp();
                        return
                            ESEQ(SEQ(MOVE(TEMP(result),
                                          CALL(target.external("malloc"),
                                               CONST(r.size))),
                                     InitValue(MEM(TEMP(result)), r)),
                                 TEMP(result));
                    }
                    Tree.Exp GenOpenArray(Type r) {
                        Tree.Stm stm = null;
                        int n = OpenDepth(r);
                        // compute the sizes
                        Tree.Exp number = CONST(1);
                        Temp[] sizes = new Temp[n];
                        Label badSub = currentFrame.badSub();
                        for (int i = 0; i < n; i++) {
                            Label l = new Label();
                            Temp size = sizes[i] = new Temp();
                            Temp temp = new Temp();
                            stm = SEQ(stm,
                                      MOVE(TEMP(size), Compile(ce.args[i+1])),
                                      badSub == null ? null : BLT(TEMP(size), CONST(0), badSub, l),
                                      LABEL(l),
                                      MOVE(TEMP(temp), MUL(TEMP(size), number)));
                            number = TEMP(temp);
                        }
                        Temp size = new Temp();
                        Temp result = new Temp();
                        stm = SEQ(stm,
                                  MOVE(TEMP(size), CONST((n+1) * wordSize)),
                                  MOVE(TEMP(size), ADD(TEMP(size), MUL(number, CONST(OpenType(r).size)))),
                                  MOVE(TEMP(result), CALL(target.external("malloc"), TEMP(size))),
                                  MOVE(MEM(TEMP(result)), ADD(TEMP(result), CONST((n+1) * wordSize))));
                        for (int i = 0; i < n; i++)
                            stm = SEQ(stm,
                                      MOVE(MEM(TEMP(result),
                                               CONST((i+1) * wordSize)),
                                           TEMP(sizes[i])));
                        stm = SEQ(stm,
                                  InitValue(MEM(TEMP(result),
                                                CONST(0),
                                                (n+1) * wordSize),
                                            r));
                        return ESEQ(stm, TEMP(result));
                    }
                    Tree.Exp GenRecord(Type t) {
                        Type r = Base(t);
                        Temp result = new Temp();
                        return ESEQ(SEQ(MOVE(TEMP(result),
                                             CALL(target.external("malloc"),
                                                  CONST(t.size))),
                                        InitValue(MEM(TEMP(result)), r)),
                                    TEMP(result));
                    }
                    Tree.Exp GenObject(Type r) {
                        Type.Object t = Is(r, Type.Object.class);
                        Temp result = new Temp();
                        Tree.Stm stm
                            = SEQ(MOVE(TEMP(result),
                                       CALL(target.external("malloc"),
                                            CONST(wordSize + t.fieldOffset + t.fieldSize))),
                                  MOVE(TEMP(result),
                                       ADD(TEMP(result),
                                           CONST(wordSize))),
                                  MOVE(MEM(TEMP(result), CONST(-wordSize)),
                                       NAME(Temp.getLabel(GlobalUID(t)))));
                        while (t != null) {
                            for (Value.Field f: t.fields) {
                                int x = t.fieldOffset + f.offset;
                                stm = SEQ(stm,
                                          InitValue(MEM(TEMP(result), CONST(x)),
                                                    f.type));
                            }
                            t = Is(t.parent, Type.Object.class);
                        }
                        return ESEQ(stm, TEMP(result));
                    }
                });
            }
            public Tree.Exp visit(Expr.Or e) {
                Label t = new Label();
                Label f = new Label();
                Temp temp = new Temp();
                return ESEQ(SEQ(MOVE(TEMP(temp), CONST(0)),
                                CompileBranch(e, t, f),
                                LABEL(t), MOVE(TEMP(temp), CONST(1)),
                                LABEL(f)),
                            TEMP(temp));
            }
            public Tree.Exp visit(Expr.And e) {
                // TODO
                Label t = new Label();
                Label f = new Label();
                Temp temp = new Temp();
                return ESEQ(SEQ(MOVE(TEMP(temp), CONST(0)),
                        CompileBranch(e, t, f),
                        LABEL(t), MOVE(TEMP(temp), CONST(1)),
                        LABEL(f)),

                        TEMP(temp));
            }
            public Tree.Exp visit(Expr.Not e) {
                // TODO
                Label t = new Label();
                Label f = new Label();
                Temp temp = new Temp();
                return ESEQ(SEQ(MOVE(TEMP(temp), CONST(0)),
                                CompileBranch(e, t, f),
                                LABEL(t), MOVE(TEMP(temp), CONST(1)),
                                LABEL(f)),

                        TEMP(temp));
            }
            public Tree.Exp visit(Expr.Add e) { return ADD(Compile(e.left), Compile(e.right)); }
            public Tree.Exp visit(Expr.Sub e) { return SUB(Compile(e.left), Compile(e.right)); }
            public Tree.Exp visit(Expr.Mul e) { return MUL(Compile(e.left), Compile(e.right)); }
            public Tree.Exp visit(Expr.Div e) { return DIV(Compile(e.left), Compile(e.right)); }
            public Tree.Exp visit(Expr.Mod e) { return MOD(Compile(e.left), Compile(e.right)); }
            public Tree.Exp visit(Expr.Pos e) { return Compile(e.expr); }
            public Tree.Exp visit(Expr.Neg e) {
                // TODO
                return SUB(CONST(0), Compile(e.expr));
            }
            public Tree.Exp visit(Expr.Deref e) {
                Tree.Exp exp = Compile(e.expr);
                Label badPtr = currentFrame.badPtr();
                if (badPtr != null) {
                    Temp t = new Temp();
                    Label okPtr = new Label();
                    exp = ESEQ(SEQ(MOVE(TEMP(t), exp),
                                   BEQ(TEMP(t), CONST(0), badPtr, okPtr),
                                   LABEL(okPtr)),
                               TEMP(t));
                }
                int size = e.type.size;
                if (size < 0) size = (OpenDepth(e.type)+1) * wordSize;
                return MEM(exp, CONST(0), size);
            }
            public Tree.Exp visit(Expr.Qualify e) {
                assert e.expr.checked;
                Value v = Resolve(e);
                assert v != null;
                if (e.objType != null) {
                    Compile(e.objType);
                    Value.Method m = (Value.Method)v;
                    assert !m.override;
                    return MEM(NAME(Temp.getLabel(GlobalUID(e.objType))),
                               CONST(m.parent.methodOffset + m.offset));
                }
                if (v instanceof Value.Method m) {
                    assert !m.override;
                    Tree.Exp exp = Compile(e.expr);
                    Temp temp;
                    Tree.Stm prep;
                    if (exp instanceof Tree.Exp.TEMP t) {
                        temp = t.temp;
                        prep = null;
                    } else {
                        temp = new Temp();
                        prep = MOVE(TEMP(temp), exp);
                    }
                    passObject.put(e, temp);
                    // TODO: make sure to include prep!
                    Type.Object p = Is(m.parent, Type.Object.class);
//                    if (prep != null) return MEM(exp, p.)
                }
                if (v instanceof Value.Field f) {
                    Tree.Exp exp = Compile(e.expr);
                    if (Is(f.parent, Type.Record.class) != null) {
                        Tree.Exp.MEM mem = (Tree.Exp.MEM)exp;
                        return MEM(mem.exp, ADD(mem.offset, CONST(f.offset)),
                                   f.type.size);
                    }
                    Type.Object p = Is(f.parent, Type.Object.class);
                    if (p != null) {
                        Label badPtr = currentFrame.badPtr();
                        if (badPtr != null) {
                            Temp t = new Temp();
                            Label okPtr = new Label();
                            exp = ESEQ(SEQ(MOVE(TEMP(t), exp),
                                           BEQ(TEMP(t), CONST(0), badPtr, okPtr),
                                           LABEL(okPtr)),
                                       TEMP(t));
                        }
                        int x = p.fieldOffset + f.offset;
                        return MEM(exp, CONST(x), f.type.size);
                    }
                }
                return Load(e, v);
            }
            public Tree.Exp visit(Expr.Subscript p) {
                Type ta = Base(TypeOf(p.expr));
                Type.Array b = Is(ta, Type.Array.class); assert b != null;
                Tree.Exp.MEM a = (Tree.Exp.MEM)Compile(p.expr);
                int depth = OpenDepth(ta);
                if (depth == 0) {
                    // a fixed array
                    // TODO:
                    // Treat constant and variable indexes differently.
                    // No need for bounds check here as already injected in
                    // semantic processing as an Expr.Check.
                    return null;
                } else if (depth == 1) {
                    // a single dimension open array
                    int size = OpenType(b).size;
                    Tree.Exp index = Compile(p.index);
                    Label badSub = currentFrame.badSub();
                    if (badSub != null) {
                        Temp t = new Temp();
                        Label okLo = new Label();
                        Label okHi = new Label();
                        index
                            = ESEQ(SEQ(Force(a),
                                       MOVE(TEMP(t), index),
                                       BLT(TEMP(t), CONST(0), badSub, okLo),
                                       LABEL(okLo),
                                       BGE(TEMP(t),
                                           MEM(a.exp,
                                               ADD(a.offset, CONST(wordSize))),
                                           badSub, okHi),
                                       LABEL(okHi)),
                                   TEMP(t));
                    }
                    return MEM(ADD(MEM(a.exp, a.offset),
                                   MUL(index, CONST(size))),
                               CONST(0),
                               size);
                } else {
                    Tree.Stm stm = Force(a);
                    // a multi dimension open array
                    // evaluate the subexpressions and allocate space for the result
                    Tree.Exp index = Compile(p.index);
                    Label badSub = currentFrame.badSub();
                    if (badSub != null) {
                        Temp t = new Temp();
                        Label okLo = new Label();
                        Label okHi = new Label();
                        index
                            = ESEQ(SEQ(MOVE(TEMP(t), index),
                                       BLT(TEMP(t), CONST(0), badSub, okLo),
                                       LABEL(okLo),
                                       BGE(TEMP(t),
                                           MEM(a.exp,
                                               ADD(a.offset, CONST(wordSize))),
                                           badSub, okHi),
                                       LABEL(okHi)),
                                   TEMP(t));
                    }
                    // allocate a new dope vector
                    Tree.Exp.MEM t = (Tree.Exp.MEM)currentFrame.allocLocal(depth * wordSize).exp(currentFrame.FP());
                    stm = Force(t);
                    // copy the rest of the dope vector
                    for (int i = 1; i < depth; i++) {
                        stm = SEQ(stm,
                                  MOVE(MEM(t.exp,
                                           ADD(t.offset,
                                               CONST(i * wordSize))),
                                       MEM(a.exp,
                                           ADD(a.offset,
                                               CONST((i+1) * wordSize)))));
                    }
                    // build the new data pointer
                    Temp o = new Temp();
                    stm = SEQ(stm, MOVE(TEMP(o), index));
                    for (int i = 0; i < depth-1; i++) {
                        stm = SEQ(stm,
                                  MOVE(TEMP(o),
                                       MUL(TEMP(o),
                                           MEM(t.exp,
                                               ADD(t.offset,
                                                   CONST((i+1) * wordSize))))));
                    }
                    index = ESEQ(stm, TEMP(o));
                    return MEM(ESEQ(MOVE(MEM(t.exp, t.offset),
                                         ADD(MEM(a.exp, a.offset),
                                             MUL(index,
                                                 CONST(OpenType(ta).size)))),
                                    t.exp),
                               t.offset,
                               depth * wordSize);
                }
            }
            public Tree.Exp visit(Expr.Named e) {
                Value v = Resolve(e);
                return Load(e, v);
            }
            public Tree.Exp visit(Expr.Int e) {
                if (e.value.bitLength() >= wordSize * 8) {
                    Error.Msg(e.token, "value is not a constant");
                    return CONST(0);
                }
                return CONST(e.value);
            }
            public Tree.Exp visit(Expr.Text e) {
                return NAME(stringLabel(e.value));
            }
            public Tree.Exp visit(Expr.TypeExpr e) {
                assert false; return null;
            }
            public Tree.Exp visit(Expr.Bool e) {
                return e.value ? CONST(1) : CONST(0);
            }
        }
        if (e == null) return null;
        assert e.checked;
        return e.accept(new Visitor());
    }
    Tree.Stm Force(Tree.Exp.MEM mem) {
        if (mem.exp instanceof Tree.Exp.TEMP) return null;
        Temp t = new Temp();
        Tree.Stm s = MOVE(TEMP(t), mem.exp);
        mem.exp = TEMP(t);
        return s;
    }

    Tree.Stm CompileBranch(Expr e, final Label t, final Label f) {
        class Visitor implements Expr.Visitor<Tree.Stm> {
            public Tree.Stm visit(Expr.Proc e) { assert false; return null; }
            public Tree.Stm visit(Expr.Method e) { assert false; return null; }
            public Tree.Stm visit(Expr.Equal e) {
                Tree.Exp l = Compile(e.left);
                Tree.Exp r = Compile(e.right);
                switch(e.op) {
                case EQ: return BEQ(l, r, t, f);
                case NE: return BNE(l, r, t, f);
                default: assert false; return null;
                }
            }
            public Tree.Stm visit(Expr.Compare e) {
                Tree.Exp l = Compile(e.left);
                Tree.Exp r = Compile(e.right);
                switch(e.op) {
                case GT: return BGT(l, r, t, f);
                case GE: return BGE(l, r, t, f);
                case LT: return BLT(l, r, t, f);
                case LE: return BLE(l, r, t, f);
                default: assert false;
                }
                return null;
            }
            public Tree.Stm visit(Expr.Address e) { assert false; return null; }
            public Tree.Stm visit(Expr.Check e) {
                return BEQ(Compile(e), CONST(0), f, t);
            }
            public Tree.Stm visit(final Expr.Call ce) {
                return ce.procType.accept(new Type.Proc.Visitor<Tree.Stm>() {
                    public Tree.Stm visit(Type.Proc.User _v) {
                        return BEQ(Compile(ce), CONST(0), f, t);
                    }
                    public Tree.Stm visit(Type.Proc.First _v) {
                        return BEQ(Compile(ce), CONST(0), f, t);
                    }
                    public Tree.Stm visit(Type.Proc.Last _v) {
                        return BEQ(Compile(ce), CONST(0), f, t);
                    }
                    public Tree.Stm visit(Type.Proc.Number _v) { assert false; return null; }
                    public Tree.Stm visit(Type.Proc.New _v) { assert false; return null; }
                });
            }
            public Tree.Stm visit(Expr.Or e) {
                // TODO
                Label ff = new Label();
                return SEQ(CompileBranch(e.left, t, ff), LABEL(ff), CompileBranch(e.right, t, f));
            }
            public Tree.Stm visit(Expr.And e) {
                // TODO
                Label tt = new Label();
                return SEQ(CompileBranch(e.left, tt, f), LABEL(tt), CompileBranch(e.right, t, f));
            }
            public Tree.Stm visit(Expr.Not e) {
                // TODO
                return CompileBranch(e.expr, f, t);
            }
            public Tree.Stm visit(Expr.Add e) { assert false; return null; }
            public Tree.Stm visit(Expr.Sub e) { assert false; return null; }
            public Tree.Stm visit(Expr.Mul e) { assert false; return null; }
            public Tree.Stm visit(Expr.Div e) { assert false; return null; }
            public Tree.Stm visit(Expr.Mod e) { assert false; return null; }
            public Tree.Stm visit(Expr.Pos e) { assert false; return null; }
            public Tree.Stm visit(Expr.Neg e) { assert false; return null; }
            public Tree.Stm visit(Expr.Deref e) { assert false; return null; }
            public Tree.Stm visit(Expr.Qualify e) {
                return BEQ(Compile(e), CONST(0), f, t);
            }
            public Tree.Stm visit(Expr.Subscript e) {
                return BEQ(Compile(e), CONST(0), f, t);
            }
            public Tree.Stm visit(Expr.Named e) {
                return BEQ(Compile(e), CONST(0), f, t);
            }
            public Tree.Stm visit(Expr.Int e) { assert false; return null; }
            public Tree.Stm visit(Expr.Text e) { assert false; return null; }
            public Tree.Stm visit(Expr.TypeExpr e) { assert false; return null; }
            public Tree.Stm visit(Expr.Bool e) { assert false; return null; }
        }
        if (e == null) return null;
        assert e.checked;
        return e.accept(new Visitor());
    }

    final HashMap<String,Label> strings = new HashMap<String,Label>();
    Label stringLabel(String s) {
        Label l = strings.get(s);
        if (l == null) {
            l = new Label();
            strings.put(s, l);
            frags.add(new Frag.Data(target.string(l, s)));
        }
        return l;
    }
}
