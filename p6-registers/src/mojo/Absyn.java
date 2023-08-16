/* Copyright (C) 1997-2023, Antony L Hosking.
 * All rights reserved.  */

package mojo;

import java.math.BigInteger;
import java.util.*;

import mojo.parse.*;

public abstract class Absyn {
    private static void usage() {
        throw new java.lang.Error("Usage: java mojo.Absyn <source>");
    }
    public static void main (String[] args) {
        if (args.length != 1) usage();
        java.io.File file = new java.io.File(args[0]);
        try {
            Value.Unit unit = new Parser(file).Unit();
            new Print(unit);
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

    public static Token ID(String s) {
        return Token.newToken(ParserConstants.ID, s);
    }

    public static abstract class Node {
        public final Token token;
        Node(Token token) {
            this.token = token;
        }
    }

    interface Visitor<R> extends Expr.Visitor<R>, Value.Visitor<R>, Type.Visitor<R>, Stmt.Visitor<R> {}

    public static abstract class Stmt extends Node {
        public Stmt(Token t) {
            super(t);
        }
        public interface Visitor<R> {
            R visit(Assign s);
            R visit(Block s);
            R visit(Call s);
            R visit(Break s);
            R visit(For s);
            R visit(If s);
            R visit(Loop s);
            R visit(Return s);
        }
        public abstract <R> R accept(Visitor<R> v);

        /**
         * AssignSt = Expr ":=" Expr ";".
         */
        public static class Assign extends Stmt {
            public final Expr lhs, rhs;
            public Assign(Token t, Expr lhs, Expr rhs) {
                super(t);
                this.lhs = lhs;
                this.rhs = rhs;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * CallSt = Expr "(" [ Actual { "," Actual } ] ")" ";".
         */
        public static class Call extends Stmt {
            public final Expr expr;
            public Call(Token t, Expr expr) {
                super(t);
                this.expr = expr;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * BreakSt = break ";".
         */
        public static class Break extends Stmt {
            public Break(Token t) {
                super(t);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * ForSt = for Id ":=" Expr ".." Expr [ "by" Expr ] Block.
         */
        public static class For extends Stmt {
            public final Token id;
            public final Expr from, to, by;
            public final Stmt stmt;
            public For(Token t, Token id, Expr from, Expr to, Expr by, Stmt stmt)
            {
                super(t);
                this.id = id;
                this.from = from;
                this.to = to;
                if (by == null) by = new Expr.Int(t, BigInteger.ONE);
                this.by = by;
                this.stmt = stmt;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Value.Variable var;
            public Scope scope;
        }
        /**
         * IfSt = if Expr Block [ else ( IfSt | Block ) ].
         */
        public static class If extends Stmt {
            public final Expr expr;
            public final Block block;
            public final Stmt stmt;
            public If(Token t, Expr expr, Block block, Stmt stmt) {
                super(t);
                this.expr = expr;
                this.block = block;
                this.stmt = stmt;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * LoopSt = loop [ while Expr ] Block [ until Expr ";" ]
         */
        public static class Loop extends Stmt {
            public final Block block;
            public final Expr whileExpr, untilExpr; 
            public Loop(Token t, Expr whileExpr, Block block, Expr untilExpr) {
                super(t);
                this.whileExpr = whileExpr;
                this.block = block;
                this.untilExpr = untilExpr;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * ReturnSt = return [ Expr ] ";".
         */
        public static class Return extends Stmt {
            public final Expr expr;
            public Return(Token t, Expr expr) {
                super(t);
                this.expr = expr;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Block = "{" { Decl } { Stmt } "}".
         */
        public static class Block extends Stmt {
            public final List<Value> decls;
            public final List<Stmt> body;
            public Block(Token t, List<Value> decls, List<Stmt> body) {
                super(t);
                if (decls == null)
                    decls = new LinkedList<Value>();
                this.decls = decls;
                if (body == null)
                    body = new LinkedList<Stmt>();
                this.body = body;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Scope scope;
        }
    }

    public static abstract class Type extends Node {
        public int checkDepth = -1;
        public Type(Token t) {
            super(t);
        }
        public interface Visitor<R> {
            R visit(Array t);
            R visit(Bool t);
            R visit(Err t);
            R visit(Int t);
            R visit(Named t);
            R visit(Object t);
            R visit(Proc t);
            R visit(Record t);
            R visit(Ref t);
        }
        public abstract <R> R accept(Visitor<R> v);

        public int size = -1;
        public Value.Tipe tipe;
        private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
        enum Flag { CHECKED, ERRORED }
        public boolean checked() { return flags.contains(Flag.CHECKED); }
        public boolean errored() { return flags.contains(Flag.ERRORED); }
        public void checked(boolean b) { assert b; flags.add(Flag.CHECKED); }
        public void errored(boolean b) { assert b; flags.add(Flag.ERRORED); }
        public String uid;

        /**
         * ArrayType = "[" [ Expr ] "]" Type.
         */
        public static class Array extends Type {
            public final Expr number; // null => open array
            public Type element;
            public Array(Token t, Expr number, Type element) {
                super(t);
                this.number = number;
                this.element = element;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }

        /**
         * TypeName = Id.
         */
        public static class Named extends Type {
            public final String name;
            public Named(Token t) {
                super(t);
                this.name = t.image;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Type type;
            public Value value;
        }

        /**
         * RecordType = "(" Fields ")".
         */
        public static class Record extends Type {
            public final List<Value.Field> fields;
            public Record(Token t, List<Value.Field> fields) {
                super(t);
                if (fields == null)
                    fields = new LinkedList<Value.Field>();
                this.fields = fields;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Scope fieldScope;
        }

        /**
         * ObjectType = [ TypeName | ObjectType ] "object" "{" Members "}".
         */
        public static class Object extends Type {
            public Type parent;
            public final List<Value.Field> fields;
            public final List<Value.Method> methods, overrides;
            public Object(Token t, Type parent,
                          List<Value.Field> fields,
                          List<Value.Method> methods,
                          List<Value.Method> overrides) {
                super(t);
                if (parent == null)
                    parent = Type.Root.T;
                this.parent = parent;
                if (fields == null)
                    fields = new LinkedList<Value.Field>();
                this.fields = fields;
                if (methods == null)
                    methods = new LinkedList<Value.Method>();
                this.methods = methods;
                if (overrides == null)
                    overrides = new LinkedList<Value.Method>();
                this.overrides = overrides;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Scope fieldScope, methodScope;
            public int fieldOffset = -1, fieldSize = -1;
            public int methodOffset = -1, methodSize = 0, overrideSize = 0;
        }

        /**
         * Signature = "(" Formals ")" [ ":" Type ]
         */
        public static abstract class Proc extends Type {
            public final List<Value.Formal> formals;
            public Type result;
            public Proc(Token t, List<Value.Formal> formals, Type result) {
                super(t);
                if (formals == null)
                    formals = new LinkedList<Value.Formal>();
                this.formals = formals;
                this.result = result;
                this.minArgs = 0;
                this.maxArgs = Integer.MAX_VALUE;
                this.fixedType = null;
            }
            public Proc(int minArgs, int maxArgs, Type fixedType) {
                super(null);
                this.formals = null;
                this.result = null;
                this.minArgs = minArgs;
                this.maxArgs = maxArgs;
                this.fixedType = fixedType;
            }
            public <R> R accept(Type.Visitor<R> v) { return v.visit(this); }
            public abstract <R> R accept(Visitor<R> v);
            public Scope scope;

            public final int minArgs, maxArgs;
            public final Type fixedType;
            public final boolean keywords = false;

            interface Visitor<R> {
                R visit(User t);
                R visit(First t);
                R visit(Last t);
                R visit(Number t);
                R visit(New t);
            }

            /**
             * Methods for handling calls to user-defined procedures and builtins
             * FIRST, LAST, ORD, VAL, NUMBER, NEW
             */
            public static class User extends Proc {
                public User(Token t, List<Value.Formal> formals, Type result) {
                    super(t, formals, result);
                }
                public <R> R accept(Visitor<R> v) { return v.visit(this); }
            }
            public static class First extends Proc {
                private First() {
                    super(1, 1, null);
                }
                public <R> R accept(Visitor<R> v) { return v.visit(this); }
                static final First T = new First();
                static void init() {
                    Scope.Insert(new Value.Procedure(ID("First"), T, null));
                    T.checked(true);
                }
            }
            public static class Last extends Proc {
                private Last() {
                    super(1, 1, null);
                }
                public <R> R accept(Visitor<R> v) { return v.visit(this); }
                static final Last T = new Last();
                static void init() {
                    Scope.Insert(new Value.Procedure(ID("Last"), T, null));
                    T.checked(true);
                }
            }
            public static class New extends Proc {
                private New() {
                    super(1, Integer.MAX_VALUE, null);
                }
                public <R> R accept(Visitor<R> v) { return v.visit(this); }
                static final New T = new New();
                static void init() {
                    Scope.Insert(new Value.Procedure(ID("New"), T, null));
                    T.checked(true);
                }
            }
            public static class Number extends Proc {
                private Number() {
                    super(1, 1, Type.Int.T);
                }
                public <R> R accept(Visitor<R> v) { return v.visit(this); }
                static final Number T = new Number();
                static void init() {
                    Scope.Insert(new Value.Procedure(ID("Number"), T, null));
                    T.checked(true);
                }
            }
        }

        /**
         * RefType = "^" Type.
         */
        public static class Ref extends Type {
            public Type target;
            public Ref(Token t, Type target) {
                super(t);
                this.target = target;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }

        /*
         * Builtin types
         */

        /**
         * A type for errors.
         */
        public final static class Err extends Type {
            public static final Type T = new Err();
            private Err() { super(ID("_ERROR")); }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }

        /**
         * int
         */
        public final static class Int extends Type {
            public static final Type T = new Int();
            private Int() { super(ID("int")); }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            static void init() {
                Scope.Insert(new Value.Tipe(ID("int"), T));
            }
        }

        /**
         * bool
         */
        public final static class Bool extends Type {
            public static final Type T = new Bool();
            private Bool() { super(ID("bool")); }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public static final Expr False = new Expr.Bool(ID("false"), false);
            public static final Expr True  = new Expr.Bool(ID("true"), true);
            static void init () {
                Scope.Insert(new Value.Tipe(ID("bool"), T));
                Scope.Insert(new Value.Const(ID("false"), T, False));
                Scope.Insert(new Value.Const(ID("true"), T, True));
            }
        }

        /**
         * Null
         */
        public abstract static class Null {
            public static final Type T = new Ref(ID("Null"), null);
            public static final Expr Nil = new Expr.Address(ID("nil"), BigInteger.ZERO);
            static void init () {
                Scope.Insert(new Value.Tipe(ID("Null"), T));
                Scope.Insert(new Value.Const(ID("nil"), T, Nil));
            }
        }

        /**
         * Refany
         */
        public abstract static class Refany {
            public static final Type T  = new Ref(ID("Refany"), null);
            static void init () {
                Scope.Insert(new Value.Tipe(ID("Refany"), T));
            }
        }

        /**
         * Address
         */
        public abstract static class Addr {
            public static final Type T = new Ref(ID("Address"), null);
            static void init() {
                Scope.Insert(new Value.Tipe(ID("Address"), T));
            }
        }

        /**
         * Root
         */
        public abstract static class Root {
            public static final Type T;
            static {
                Scope s = Scope.PushNewClosed();
                Scope.PopNew();
                Object t = new Object(ID("Root"), null, null, null, null);
                t.fieldScope = s;
                t.methodScope = s;
                T = t;
            }
            static void init() {
                Scope.Insert(new Value.Tipe(ID("Root"), T));
            }
        }

        /**
         * Text
         */
        public abstract static class Text {
            public static final Type T = new Ref(ID("Text"), null);
            static void init() {
                Scope.Insert(new Value.Tipe(ID("Text"), T));
            }
        }

        static void init() {
            Int.init();
            Bool.init();
            Null.init();
            Refany.init();
            Addr.init();
            Root.init();
            Text.init();
            Proc.First.init();
            Proc.Last.init();
            Proc.New.init();
            Proc.Number.init();
        }
    }

    /**
     * Decl = "const" { ConstDecl ";" }
     *      | "type" { TypeDecl ";" }
     *      | "var" { VariableDecl ";" }
     *      | "def" ProcDecl.
     */
    public static abstract class Value extends Node {
        public final String name;
        public String extName;
        public Scope scope;
        public int checkDepth = -1;
        /**
         * @param id the symbol (Id) being declared
         */
        public Value(Token id) {
            super(id);
            this.name = id.image;
        }
        public interface Visitor<R> {
            R visit(Const d);
            R visit(Field d);
            R visit(Formal d);
            R visit(Method d);
            R visit(Unit d);
            R visit(Procedure d);
            R visit(Tipe d);
            R visit(Variable d);
        }
        public abstract <R> R accept(Visitor<R> v);

        private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
        private enum Flag {
            CHECKED, READONLY, EXTERNAL, UPLEVEL, ERROR, DECLARED, INITED, COMPILED,
            IMPORTED, EXPORTED, EXPORTABLE
        }
        public boolean checked() { return flags.contains(Flag.CHECKED); }
        public boolean readonly() { return flags.contains(Flag.READONLY); }
        public boolean external() { return flags.contains(Flag.EXTERNAL); }
        public boolean up_level() { return flags.contains(Flag.UPLEVEL); }
        public boolean error() { return flags.contains(Flag.ERROR); }
        public boolean declared() { return flags.contains(Flag.DECLARED); }
        public boolean inited() { return flags.contains(Flag.INITED); }
        public boolean compiled() { return flags.contains(Flag.COMPILED); }
        public boolean imported() { return flags.contains(Flag.IMPORTED); }
        public boolean exported() { return flags.contains(Flag.EXPORTED); }
        public boolean exportable() { return flags.contains(Flag.EXPORTABLE); }

        public void checked(boolean b) { assert b; flags.add(Flag.CHECKED); }
        public void readonly(boolean b) { assert b; flags.add(Flag.READONLY); }
        public void external(boolean b) { assert b; flags.add(Flag.EXTERNAL); }
        public void up_level(boolean b) { assert b; flags.add(Flag.UPLEVEL); }
        public void error(boolean b) { assert b; flags.add(Flag.ERROR); }
        public void declared(boolean b) { assert b; flags.add(Flag.DECLARED); }
        public void compiled(boolean b) { assert b; flags.add(Flag.COMPILED); }
        public void imported(boolean b) { if (b) flags.add(Flag.IMPORTED); else flags.remove(Flag.IMPORTED); }
        public void exported(boolean b) { if (b) flags.add(Flag.EXPORTED); else flags.remove(Flag.EXPORTED); }
        public void exportable(boolean b) { assert b; flags.add(Flag.EXPORTABLE); }

        /**
         * ConstDecl = Id [ ":" Type ] "=" ConstExpr.
         */
        public static class Const extends Value {
            public Type type;
            public Expr expr;
            public Const(Token id, Type type, Expr expr) {
                super(id);
                this.type = type;
                this.expr = expr;
                readonly(true);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }

        /**
         * Field = IdList ":" Type.
         */
        public static class Field extends Value {
            public Type type;
            public final Type parent;
            public final Expr expr;
            public Field(Token id, Type parent, Type type) {
                super(id);
                this.parent = parent;
                this.type = type;
                this.expr = null;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public int offset = -1;
        }
        /**
         * Formal = [ "val" | "var" ] IdList ":" Type.
         */
        public static class Formal extends Value {
            public enum Mode { VALUE, VAR, READONLY }
            public final Mode mode;
            public Type type;
            public Formal(Token id, Mode mode, Type type) {
                super(id);
                this.mode = mode;
                this.type = type;
                if (mode == Mode.READONLY) readonly(true);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Type refType = null;
        }
        /**
         * Method = Id Signature [ ":=" ConstExpr ].
         */
        public static class Method extends Value {
            public final boolean override;
            public final Type.Object parent;
            public Type.Proc sig;
            public final Expr expr;
            public Method(Token id, Type.Object parent, Type.Proc sig, Expr expr) {
                super(id);
                this.parent = parent;
                this.sig = sig;
                override = sig == null;
                this.expr = expr;
                readonly(true);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public int offset = -1;
            public Value value;
        }

        /**
         * Unit = { Decl } [ Block ].
         */
        public static class Unit extends Value {
            public final List<Value> decls;
            public final Stmt block;
            public Unit(Token id, List<Value> decls, Stmt block) {
                super(id);
                this.decls = decls;
                this.block = block;
                readonly(true);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }

            private static Unit current = null;
            public static Unit Current() { return current; }
            public static Unit Switch(Unit newUnit) {
                Unit oldUnit = current;
                current = newUnit;
                return oldUnit;
            }
            public static boolean IsInterface() {
                return current == null || current.block == null;
            }

            public Scope localScope;
            public ProcBody body;
            public final List<Type> types = new LinkedList<Type>();
        }
        /**
         * ProcDecl = Id Signature ( Block | ";" ).
         */
        public static class Procedure extends Value {
            public final Type.Proc sig;
            public final Stmt block;
            public Procedure(Token id, Type.Proc sig, Stmt block) {
                super(id);
                this.sig = sig;
                this.block = block;
                readonly(true);
                if (sig instanceof Type.Proc.User && block == null) {
                    external(true);
                    this.extName = id.image;
                }
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Scope localScope;
            public ProcBody body;
            public Variable result;
        }
        /**
         * TypeDecl = Id "=" Type.
         */
        public static class Tipe extends Value {
            public Type value;
            public Tipe(Token id, Type type) {
                super(id);
                this.value = type;
                type.tipe = this;
                readonly(true);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * VarDecl = IdList ( ":" Type & ":=" Expr ).
         */
        public static class Variable extends Value {
            public Type type;
            public Expr expr;
            public final Value.Formal formal;
            public Variable(Token id, Type type, Expr expr) {
                super(id);
                this.formal = null;
                this.type = type;
                this.expr = expr;
            }
            public Variable(Formal formal, boolean indirect) {
                super(formal.token);
                this.formal = formal;
                this.type = formal.type;
                this.expr = null;
                if (formal.mode != Formal.Mode.VALUE) indirect(true);
                if (formal.mode == Formal.Mode.READONLY) readonly(true);
                initDone(true);
                imported(false); // in spite of module depth
                if (indirect) indirect(true);
            }
            public Variable(Stmt.For stmt, Type type) {
                super(stmt.id);
                this.formal = null;
                this.type = type;
                this.expr = null;
                readonly(true);
                initDone(true);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }

            private enum Flag { INDIRECT, ADDRESS, GLOBAL, DONE, ZERO, PENDING, STATIC }
            private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
            public boolean indirect() { return flags.contains(Flag.INDIRECT); }
            public boolean needsAddress() { return flags.contains(Flag.ADDRESS); }
            public boolean global() { return flags.contains(Flag.GLOBAL); }
            public boolean initDone() { return flags.contains(Flag.DONE); }
            public boolean initZero() { return flags.contains(Flag.ZERO); }
            public boolean initPending() { return flags.contains(Flag.PENDING); }
            public boolean initStatic() { return flags.contains(Flag.STATIC); }
            public void indirect(boolean b) { assert b; flags.add(Flag.INDIRECT); }
            public void needsAddress(boolean b) { assert b; flags.add(Flag.ADDRESS); }
            public void global(boolean b) { assert b; flags.add(Flag.GLOBAL); }
            public void initDone(boolean b) { assert b; flags.add(Flag.DONE); }
            public void initPending(boolean b) { if (b) flags.add(Flag.PENDING); else flags.remove(Flag.PENDING); }
            public void initStatic(boolean b) { if (b) flags.add(Flag.STATIC); else flags.remove(Flag.STATIC); }

            public ProcBody proc;
            int size = -1;
        }
    }

    public static abstract class Expr extends Node {
        public Type type = null;
        public boolean checked = false;
        public final int precedence;
        public Expr(Token t, int precedence) {
            super(t);
            this.precedence = precedence;
        }
        public interface Visitor<R> {
            R visit(Add e);
            R visit(Bool e);
            R visit(Address e);
            R visit(And e);
            R visit(Call e);
            R visit(Check e);
            R visit(Compare e);
            R visit(Deref e);
            R visit(Div e);
            R visit(Equal e);
            R visit(Int e);
            R visit(Mod e);
            R visit(Method e);
            R visit(Mul e);
            R visit(Named e);
            R visit(Neg e);
            R visit(Not e);
            R visit(Or e);
            R visit(Pos e);
            R visit(Proc e);
            R visit(Qualify e);
            R visit(Sub e);
            R visit(Subscript e);
            R visit(Text e);
            R visit(TypeExpr e);
        }
        public abstract <R> R accept(Visitor<R> v);

        public static abstract class Binary extends Expr {
            public final Expr left, right;
            Binary(Token t, int precedence, Expr left, Expr right) {
                super(t, precedence);
                this.left = left;
                this.right = right;
            }
        }
        public static abstract class Unary extends Expr {
            public final Expr expr;
            Unary(Token t, int precedence, Expr expr) {
                super(t, precedence);
                this.expr = expr;
            }
        }

        /**
         * Expr = Expr "||" Expr.
         */
        public static class Or extends Binary {
            public Or(Token t, Expr left, Expr right) {
                super(t, 0, left,right);
                type = Type.Bool.T;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "&&" Expr.
         */
        public static class And extends Binary {
            public And(Token t, Expr left, Expr right) {
                super(t, 1, left, right);
                type = Type.Bool.T;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = "!" Expr.
         */
        public static class Not extends Unary {
            public Not(Token t, Expr expr) {
                super(t, 2, expr);
                type = Type.Bool.T;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "<" Expr | Expr ">" Expr | Expr "<=" Expr | Expr ">=" Expr
         */
        public static class Compare extends Binary {
            public enum Op {
                GT(1, 1), GE(1, 0), LT(-1, -1), LE(-1, 0);
                public final int signA, signB;
                Op(int signA, int signB) { this.signA = signA; this.signB = signB; }
            }
            public final Op op;
            public Compare(Token t, Expr left, Expr right, Op op) {
                super(t, 3, left, right);
                this.op = op;
                type = Type.Bool.T;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "==" Expr | Expr "!=" Expr.
         */
        public static class Equal extends Binary {
            public enum Op { EQ, NE }
            public final Op op;
            public Equal(Token t, Expr left, Expr right, Op op) {
                super(t, 3, left, right);
                this.op = op;
                type = Type.Bool.T;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "+" Expr.
         */
        public static class Add extends Binary {
            public Add(Token t, Expr left, Expr right) {
                super(t, 4, left, right);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "-" Expr.
         */
        public static class Sub extends Binary {
            public Sub(Token t, Expr left, Expr right) {
                super(t, 4, left, right);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "*" Expr.
         */
        public static class Mul extends Binary {
            public Mul(Token t, Expr left, Expr right) {
                super(t, 5, left, right);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "/" Expr.
         */
        public static class Div extends Binary {
            public Div(Token t, Expr left, Expr right) {
                super(t, 5, left, right);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "%" Expr.
         */
        public static class Mod extends Binary {
            public Mod(Token t, Expr left, Expr right) {
                super(t, 5, left, right);
                type = Type.Int.T;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = "+" Expr.
         */
        public static class Pos extends Unary {
            public Pos(Token t, Expr expr) {
                super(t, 6, expr);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = "-" Expr.
         */
        public static class Neg extends Unary {
            public Neg(Token t, Expr expr) {
                super(t, 6, expr);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = "^" Expr.
         */
        public static class Deref extends Unary {
            public Deref(Token t, Expr expr) {
                super(t, 6, expr);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = Expr "." Id.
         */
        public static class Qualify extends Expr {
            public Expr expr; // not final to allow for auto-magic dereference
            public final Token name;
            public Qualify(Token t, Expr expr, Token name) {
                super(t, 7);
                this.expr = expr;
                this.name = name;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Value value;
            public Type.Object objType;
        }
        /**
         * Expr = Expr "[" Expr "]".
         */
        public static class Subscript extends Expr {
            public Expr expr; // not final to allow for auto-magic dereference
            public Expr index; // not final to allow for bounds check
            public Subscript(Token t, Expr expr, Expr index) {
                super(t, 7);
                this.expr = expr;
                this.index = index;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public int depth; // open array depth before subscripting
        }
        /**
         * Expr = Expr "(" [ Actual { "," Actual } ] ")".
         */
        public static class Call extends Expr {
            public final Expr proc;
            public Expr[] args;
            public Call(Token t, Expr proc, List<Expr> args) {
                super(t, 7);
                this.proc = proc;
                this.args = args.toArray(new Expr[args.size()]);
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Type.Proc procType;
        }

        /**
         * Expr = Id.
         */
        public static class Named extends Expr {
            public final String name;
            public Named(Token id) {
                super(id, 8);
                this.name = id.image;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
            public Value value;
        }
        /**
         * Expr = Number.
         */
        public static class Int extends Expr {
            public final BigInteger value;
            public Int(Token t, BigInteger value) {
                super(t, 8);
                this.value = value;
                this.type = Type.Int.T;
                this.checked = true;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        public static class Bool extends Expr {
            public final boolean value;
            public Bool(Token t, boolean value) {
                super(t, 8);
                this.value = value;
                this.type = Type.Bool.T;
                this.checked = true;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        public static class Address extends Expr {
            public final BigInteger value;
            public Address(Token t, BigInteger value) {
                super(t, 8);
                this.value = value;
                this.type = value.compareTo(BigInteger.ZERO) == 0 ? Type.Null.T : Type.Addr.T;
                this.checked = true;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Expr = TextLiteral.
         */
        public static class Text extends Expr {
            public final String value;
            public Text(Token t, String value) {
                super(t, 8);
                this.value = value;
                this.type = Type.Text.T;
                this.checked = true;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        /**
         * Actual = Type.
         *
         * This allows us to parse a type expression where we expect to
         * find an expression.
         */
        public static class TypeExpr extends Expr {
            public Type value;
            public TypeExpr(Type value) {
                super(value.token, 8);
                this.value = value;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }

        public static class Check extends Expr {
            public final Expr expr;
            public BigInteger min, max;
            public Check(Expr expr, BigInteger min, BigInteger max) {
                super(null, 8);
                this.expr = expr;
                this.min = min;
                this.max = max;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        public static class Proc extends Expr {
            public final Value proc;
            public Proc(Value proc) {
                super(null, 8);
                this.proc = proc;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
        public static class Method extends Expr {
            public Type object;
            public final String name;
            public final Value.Method method;
            public Method(Type.Object object, String name, Value.Method method) {
                super(null, 8);
                this.object = object;
                this.name = name;
                this.method = method;
            }
            public <R> R accept(Visitor<R> v) { return v.visit(this); }
        }
    }

    public static class Print implements Visitor<Void> {
        int i = 0;
        public Print(Value.Unit absyn) {
            if (absyn == null) return;
            absyn.accept(this);
            System.out.flush();
        }
        void print(Value d, int i) {
            int save = this.i;
            this.i = i;
            d.accept(this);
            this.i = save;
        }
        void print(Stmt s, int i) {
            if (s == null) {
                indent(i);
                sayln(";");
            } else {
                int save = this.i;
                this.i = i;
                s.accept(this);
                this.i = save;
            }
        }
        void print(Type t, int i) {
            int save = this.i;
            this.i = i;
            t.accept(this);
            this.i = save;
        }
        void print(Expr e, int i) {
            int save = this.i;
            this.i = i;
            e.accept(this);
            this.i = save;
        }
        @Override
        public Void visit(Value.Unit d) {
            for (Value v: d.decls) print(v, i);
            if (d.block != null) print(d.block, i);
            return null;
        }
        @Override
        public Void visit(Stmt.Block s) {
            indent(i); sayln(s.token);
            for (Value v: s.decls)
                print(v, i+2);
            for (Stmt stmt: s.body)
                print(stmt, i+2);
            indent(i); sayln("}");
            return null;
        }
        @Override
        public Void visit(Value.Tipe d) {
            indent(i); say("type " + d.token + " = ");
            print(d.value, i+2);
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Value.Variable d) {
            indent(i); say("var " + d.token);
            if (d.type != null) {
                say(": ");
                print(d.type, i+2);
            }
            if (d.expr != null) {
                say(" := ");
                print(d.expr, i+2);
            }
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Value.Const d) {
            indent(i); say("const " + d.token);
            if (d.type != null) {
                say(": ");
                print(d.type, i+2);
            }
            if (d.expr != null) {
                say(" = ");
                print(d.expr, i+2);
            }
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Value.Procedure d) {
            indent(i); say("def " + d.token);
            print(d.sig, i+2);
            if (d.block != null) {
                sayln("");
                print(d.block, i);
            } else
                sayln(";");
            return null;
        }
        @Override
        public Void visit(Type.Err t) {
            say(t.token);
            return null;
        }
        @Override
        public Void visit(Type.Int t) {
            say(t.token);
            return null;
        }
        @Override
        public Void visit(Type.Bool t) {
            say(t.token);
            return null;
        }
        @Override
        public Void visit(Type.Proc t) {
            if (t.formals == null) {
                say("(...)");
                return null;
            }
            if (t.formals.isEmpty()) {
                say(t.token); say(")");
            } else {
                sayln(t.token);
                for (Value f: t.formals) print(f, i+2);
                indent(i); say(")");
            }
            if (t.result != null) {
                say(": ");
                print(t.result, i+2);
            }
            return null;
        }
        @Override
        public Void visit(Value.Formal d) {
            indent(i);
            switch (d.mode) {
            case VALUE:
                say("val ");
                break;
            case VAR:
                say("var ");
                break;
            case READONLY:
                say("readonly ");
                break;
            }
            say(d.token);
            say(": ");
            print(d.type, i+2);
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Stmt.Assign s) {
            indent(i);
            print(s.lhs, i+2);
            say(" "); say(s.token); say(" ");
            print(s.rhs, i+2);
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Stmt.Call s) {
            indent(i);
            print(s.expr, i+2);
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Stmt.Break s) {
            indent(i);
            say(s.token);
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Stmt.For s) {
            indent(i);
            say(s.token); say(" "); say(s.id);
            say(" := "); print(s.from, i+2);
            say(" .. "); print(s.to, i+2);
            if (s.by != null) {
                say(" by "); print(s.by, i+2);
            }
            sayln("");
            print(s.stmt, i+2);
            return null;
        }
        @Override
        public Void visit(Stmt.If s) {
            indent(i); say(s.token); say(" ");
            print(s.expr, i+2); sayln("");
            print(s.block, i);
            if (s.stmt != null) {
                indent(i); sayln("else");
                print(s.stmt, i+2);
            }
            return null;
        }
        @Override
        public Void visit(Stmt.Loop s) {
            indent(i); say(s.token);
            if (s.whileExpr != null) {
                say(" while ");
                print(s.whileExpr, i+2);
            }
            sayln("");
            print(s.block, i);
            if (s.untilExpr != null) {
                indent(i);
                say("until ");
                print(s.untilExpr, i+2);
                sayln(";");
            }
            return null;
        }
        @Override
        public Void visit(Stmt.Return s) {
            indent(i);
            say(s.token);
            if (s.expr != null) {
                say(" ");
                print(s.expr, i+2);
            }
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Type.Array t) {
            say(t.token);
            if (t.number != null) print(t.number, i+2);
            say("] ");
            print(t.element, i+2);
            return null;
        }
        @Override
        public Void visit(Type.Record t) {
            say(t.token);
            boolean first = true;
            for (Value v: t.fields) {
                if (first) {
                    sayln("");
                    first = false;
                }
                print(v, i+2);
            }
            say(")");
            return null;
        }
        @Override
        public Void visit(Type.Object t) {
            if (t == Type.Root.T) {
                say(t.token);
                return null;
            }
            if (t.parent != null) {
                print(t.parent, i);
                say(" ");
            }
            say(t.token);
            say(" {");
            boolean first = true;
            for (Value v: t.fields) {
                if (first) {
                    sayln("");
                    first = false;
                }
                print(v, i+2);
            }
            for (Value v: t.methods) {
                if (first) {
                    sayln("");
                    first = false;
                }
                print(v, i+2);
            }
            for (Value v: t.overrides) {
                if (first) {
                    sayln("");
                    first = false;
                }
                print(v, i+2);
            }
            say("}");
            return null;
        }
        @Override
        public Void visit(Type.Ref t) {
            say(t.token);
            print(t.target, i+2);
            return null;
        }
        @Override
        public Void visit(Type.Named t) {
            say(t.name);
            return null;
        }
        @Override
        public Void visit(Value.Field d) {
            indent(i);
            say(d.token + ": ");
            print(d.type, i+2);
            sayln(";");
            return null;
        }
        @Override
        public Void visit(Value.Method d) {
            indent(i);
            say(d.token);
            if (d.sig != null)
                print(d.sig, i);
            if (d.expr != null) {
                say(" := ");
                print(d.expr, i+2);
            }
            sayln(";");
            return null;
        }
        private void visit(Expr child, Expr parent) {
            if (child.precedence <= parent.precedence) say("(");
            print(child, i+2);
            if (child.precedence <= parent.precedence) say(")");
        }
        @Override
        public Void visit(Expr.Or e) {
            visit(e.left, e);
            sayln("");
            indent(i); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.And e) {
            visit(e.left, e);
            sayln("");
            indent(i); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Not e) {
            say(e.token);
            visit(e.expr, e);
            return null;
        }
        @Override
        public Void visit(Expr.Compare e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Equal e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Add e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Sub e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Mul e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Div e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Mod e) {
            visit(e.left, e);
            say(" "); say(e.token); say(" ");
            visit(e.right, e);
            return null;
        }
        @Override
        public Void visit(Expr.Pos e) {
            say(e.token); visit(e.expr, e);
            return null;
        }
        @Override
        public Void visit(Expr.Neg e) {
            say(e.token); visit(e.expr, e);
            return null;
        }
        @Override
        public Void visit(Expr.Deref e) {
            visit(e.expr, e); say(e.token);
            return null;
        }
        @Override
        public Void visit(Expr.Qualify e) {
            visit(e.expr, e);
            say(e.token);
            say(e.name);
            return null;
        }
        @Override
        public Void visit(Expr.Subscript e) {
            visit(e.expr, e);
            say(e.token);
            print(e.index, i+2);
            say("]");
            return null;
        }
        @Override
        public Void visit(Expr.Call e) {
            boolean first = true;
            visit(e.proc, e);
            say(e.token);
            for (Expr expr: e.args) {
                if (first) first = false; else say(", ");
                print(expr, i+2);
            }
            say(")");
            return null;
        }
        @Override
        public Void visit(Expr.Named e) {
            say(e.name);
            return null;
        }
        @Override
        public Void visit(Expr.Bool e) {
            say(e.token);
            return null;
        }
        @Override
        public Void visit(Expr.Int e) {
            say(e.value.toString());
            return null;
        }
        @Override
        public Void visit(Expr.Address e) {
            say("16_"); say(e.value.toString(16));
            return null;
        }
        @Override
        public Void visit(Expr.Text e) {
            say(e.token);
            return null;
        }
        @Override
        public Void visit(Expr.TypeExpr e) {
            print(e.value, i);
            return null;
        }
        @Override
        public Void visit(Expr.Proc e) {
            say(e.proc.name);
            return null;
        }
        @Override
        public Void visit(Expr.Method e) {
            print(e.object, i);
            say("." + e.name);
            return null;
        }
        @Override
        public Void visit(Expr.Check e) {
            say("_rangecheck_");
            print(e.expr, i+2);
            say(", " + e.min + ", " + e.max + ")");
            return null;
        }

        private void indent(int d) {
            for (int i = 0; i < d; i++)
                System.out.print(' ');
        }
        private void say(String s) {
            System.out.print(s);
        }
        private void sayln(String s) {
            System.out.println(s);
            System.out.flush();
        }
        private void say(Token s) {
            System.out.print(s.image);
        }
        private void sayln(Token s) {
            System.out.println(s.image);
            System.out.flush();
        }
    }
}
