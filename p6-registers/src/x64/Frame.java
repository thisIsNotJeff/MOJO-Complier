/* Copyright (C) 1997-2012, Antony L Hosking.
 * All rights reserved.  */
package x64;

import java.math.BigInteger;
import java.util.*;

import Translate.Temp;
import Translate.Temp.*;
import Translate.Tree.*;
import Translate.Tree.Exp.*;
import Translate.Tree.Stm.*;

public class Frame extends Translate.Frame {
    public Translate.Frame newFrame(String name, boolean hasParent, boolean hasChildren) {
        Frame f = new Frame(globalPrefix, name);
        if (hasParent) {
            if (hasChildren)
                // inner frames must access my link in memory
                f.link = f.allocLocal(wordSize);
            else
                // no inner frames so my link can be in a register
                f.link = f.allocLocal(new Temp("_link"));
        } else
            f.link = null;
        return f;
    }

    static final int wordSize = 8;
    private final String globalPrefix;

    Access link;
    public Access link() { return link; }

    public Frame(String prefix) {
        super(null, wordSize);
        globalPrefix = prefix;
    }

    private Frame(String prefix, String n) {
        super(Temp.getLabel(n), wordSize);
        globalPrefix = prefix;
    }

    public static class InFrame extends Access {
        public final int offset;
        public final int size;
        protected InFrame(int o, int s) {
            offset = o;
            size = s;
        }
        public Exp exp(Exp fp) {
            return new MEM(fp, new Exp.CONST(BigInteger.valueOf(offset)), size);
        }
        public String toString() {
            return "FP+" + offset;
        }
    }
    public static class InReg extends Access {
        public final Temp temp;
        protected InReg(Temp t) {
            temp = t;
        }
        public Exp exp(Exp fp) {
            return new TEMP(temp);
        }
        public String toString() {
            return temp.toString();
        }
    }

    private LinkedList<Access> actuals = new LinkedList<Access>();

    private int formalSize = 0;
    private int numFormals = 0;
    public Access allocFormal(int size) {
        assert size % wordSize == 0;
        Access a;
        int i = numFormals++;
        if (size == wordSize && i < argRegs.length) {
            actuals.add(new InReg(argRegs[i]));
            a = allocLocal(size);
        } else {
            // accounting for saved %rip and %rbp
            a = new InFrame(formalSize + 16, size);
            formalSize += size;
            actuals.add(a);
        }
        formals.add(a);
        return a;
    }
    public Access allocFormal(Temp t) {
        Access a;
        int i = numFormals++;
        if (i < argRegs.length) {
            actuals.add(new InReg(argRegs[i]));
            a = new InReg(t);
        } else {
            // accounting for saved %rip and %rbp
            a = new InFrame(formalSize + 16, wordSize);
            formalSize += wordSize;
            actuals.add(a);
        }
        formals.add(a);
        return a;
    }

    private int localSize = 0;
    public Access allocLocal(int size) {
        assert size % wordSize == 0;
        localSize += size;
        return new InFrame(-localSize, size);
    }
    public Access allocLocal(Temp t) {
        return new InReg(t);
    }

    static final Temp RAX = new Temp("%rax"); // return1
    static final Temp RBX = new Temp("%rbx"); // callee-saved
    static final Temp RCX = new Temp("%rcx"); // argument4
    static final Temp RDX = new Temp("%rdx"); // argument3/return2
    static final Temp RSP = new Temp("%rsp"); // stack pointer
    static final Temp RBP = new Temp("%rbp"); // callee-save (frame pointer)
    static final Temp RSI = new Temp("%rsi"); // argument2
    static final Temp RDI = new Temp("%rdi"); // argument1
    static final Temp R8 = new Temp("%r8"); // argument5
    static final Temp R9 = new Temp("%r9"); // argument6
    static final Temp R10 = new Temp("%r10"); // caller-saved (static chain)
    static final Temp R11 = new Temp("%r11"); // caller-saved
    static final Temp R12 = new Temp("%r12"); // callee-saved
    static final Temp R13 = new Temp("%r13");
    static final Temp R14 = new Temp("%r14");
    static final Temp R15 = new Temp("%r15");

    // Register lists: must not overlap and must include every register that
    // might show up in code
    static final Temp[]
            // registers dedicated to special purposes
            specialRegs = { RSP, RBP },
            // registers to pass outgoing arguments
            argRegs = { RDI, RSI, RDX, RCX, R8, R9 },
            // registers that a callee must preserve for its caller
            calleeSaves = { RBX, R12, R13, R14, R15 },
            // registers that a callee may use without preserving
            callerSaves = { RAX, R10, R11 };

    private static Temp[] registers = {
        RAX, RBX, RCX, RDX, RSP, RBP, RSI, RDI,
         R8, R9,  R10, R11, R12, R13, R14, R15,
    };

    public Temp[] registers() {
        return registers;
    }

    @Override
    public Exp FP() {
        return new TEMP(RBP);
    }

    private Temp RV = null;
    public Exp RV() {
        if (RV == null)
            RV = RAX;
        return new TEMP(RAX);
    }

    public Translate.Tree.Exp external(String s) {
        return new Translate.Tree.Exp.NAME(Temp.getLabel(globalPrefix + s));
    }

    public String string(Label lab, String string) {
//        byte[] bytes = string.getBytes(java.nio.charset.Charset.forName("UTF-8"));
        byte[] bytes = string.getBytes();
        int length = bytes.length;
        StringBuilder lit = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte c = bytes[i];
            if (c < ' ' || c > '~') {
                switch(c) {
                case '\b': lit.append("\\b" ); break;
                case '\t': lit.append("\\t" ); break;
                case '\n': lit.append("\\n" ); break;
                case '\f': lit.append("\\f" ); break;
                case '\r': lit.append("\\r" ); break;
                case '\"': lit.append("\\\""); break;
                case '\\': lit.append("\\\\"); break;
                default:
                    lit.append("\\");
                    lit.append(((c & 0xff) >>> 6) & 7);
                    lit.append(((c & 0xff) >>> 3) & 7);
                    lit.append(((c & 0xff) >>> 0) & 7);
                }
            } else {
                lit.append((char)c);
            }
        }
        return "\t.data\n\t.balign 8\n" + lab + ":\t.asciz\t\"" + lit + "\"";
    }

    public String record(Label lab, int words) {
        StringBuilder result = new StringBuilder("\t.data\n\t.balign 8\n" + lab + ":");
        while (--words >= 0)
            result.append("\n\t.quad 0");
        return result.toString();
    }

    public String vtable(Label lab, Collection<Label> values) {
        StringBuilder result = new StringBuilder("\t.data\n\t.balign 8\n" + lab + ":");
        for (Label l : values) {
            result.append("\n\t.quad ");
            if (l == null)
                result.append("0");
            else
                result.append(l);
        }
        return result.toString();
    }

    public String switchtable(Label lab,
                              int[] values,
                              Label[] labels) {
        StringBuilder result = new StringBuilder("\t.data\n\t.balign 8\n" + lab + ":");
        for (int i = 0; i < values.length; i++) {
            result.append("\n\t.quad ").append(values[i]);
            result.append("\n\t.quad ").append(labels[i]);
        }
        return result.toString();
    }

    private Label badPtr;
    public Label badPtr() {
        if (badPtr == null) badPtr = new Label();
        return badPtr;
    }

    private Label badSub;
    public Label badSub() {
        if (badSub == null) badSub = new Label();
        return badSub;
    }

    // Registers defined by a call
    static Temp[] callDefs = new Temp[argRegs.length + callerSaves.length];
    static {
        int i = 0;
        for (Temp t : argRegs)
            callDefs[i++] = t;
        for (Temp t : callerSaves)
            callDefs[i++] = t;
    }

    private void saveArgs(Iterator<Access> formals, Iterator<Access> actuals,
            LinkedList<Stm> body) {
        if (!formals.hasNext() || !actuals.hasNext())
            return;
        Access formal = formals.next();
        Access actual = actuals.next();
        saveArgs(formals, actuals, body);
        if (formal != actual)
            body.addFirst(new MOVE(formal.exp(FP()), actual.exp(FP())));
    }

    public void procEntryExit1(LinkedList<Stm> body) {
        saveArgs(formals.iterator(), actuals.iterator(), body);
        if (link != null)
            body.addFirst(new MOVE(link.exp(FP()), new TEMP(R10)));
    }

    private static Temp[] T(Temp... a) {
        return a;
    }

    private static Assem.Instr.OPER OPER(String a, Temp[] d, Temp[] s) {
        return new Assem.Instr.OPER(a, d, s);
    }

    private static Assem.Instr.OPER OPER(String a) {
        return OPER(a, T(), T());
    }

    public Codegen codegen() {
        return new Codegen(this);
    }

    // Registers live on return
    private Temp[] returnSink = specialRegs;
    public void procEntryExit2(LinkedList<Assem.Instr> insns) {
        if (RV != null) {
            returnSink = new Temp[specialRegs.length + 1];
            int i = 0;
            for (Temp t : specialRegs)
                returnSink[i++] = t;
            returnSink[i] = RV;
        }
        insns.addLast(OPER("#\treturnSink", T(), returnSink));
    }

    public void procEntryExit3(LinkedList<Assem.Instr> insns, Temp.Map map) {
        int framesize = 0;
        if (maxArgsOut > argRegs.length) {
            // non-leaf
            framesize += (maxArgsOut - argRegs.length) * wordSize;
        }
        framesize += localSize;

        HashSet<Temp> defs = new HashSet<Temp>();
        for (Assem.Instr insn : insns) {
            for (Temp t : insn.def)
                defs.add(map.get(t));
        }

        int o = -localSize;
        for (Temp t : calleeSaves)
          if (defs.contains(t)) {
              o -= wordSize;
              framesize += wordSize;
              insns.addFirst(OPER("\tmovq `s0, " + o + "(%rbp)", T(), T(t, RBP)));
              insns.addLast(OPER("\tmovq " + o + "(%rbp),`d0", T(t), T(RBP)));
          }

        if (framesize % 16 != 0)
            framesize += 16 - (framesize % 16);

        if (framesize != 0) {
            insns.addFirst(OPER("\tsubq $" + framesize + ",%rsp", T(RSP), T(RSP)));
            insns.addLast(OPER("\taddq $" + framesize + ",%rsp", T(RSP), T(RSP)));
        }

        insns.addFirst(OPER("\tmovq %rsp,%rbp", T(RBP), T(RSP)));
        insns.addFirst(OPER("\tpushq %rbp", T(RSP), T(RBP,RSP)));
        insns.addLast(OPER("\tpopq %rbp", T(RSP,RBP), T(RSP)));

        insns.addLast(OPER("\tret", T(), returnSink));
        if (isGlobal) {
            insns.addFirst(OPER("\t.text\n" + globalPrefix + name + ":"));
            insns.addFirst(OPER("\t.globl " + globalPrefix + name));
        } else {
            insns.addFirst(OPER("\t.text\n" + name + ":"));
        }

        if (badPtr != null)
            insns.addLast(OPER(badPtr + ":\n\tcall badPtr"));
        if (badSub != null)
            insns.addLast(OPER(badSub + ":\n\tcall badSub"));
    }
}
