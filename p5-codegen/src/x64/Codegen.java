/* Copyright (C) 1997-2012, Antony L Hosking.
 * All rights reserved.  */
package x64;

import java.math.BigInteger;
import java.util.*;

import Assem.Instr;
import Translate.Temp;
import Translate.Tree;
import Translate.Temp.*;
import Translate.Tree.*;
import Translate.Tree.Exp.*;
import Translate.Tree.Stm.*;

public class Codegen implements Frame.CodeGen {
    public Codegen(Frame f) {}

    final LinkedList<Instr> insns = new LinkedList<Instr>();
    public LinkedList<Instr> insns() { return insns; }

    static Instr MOVE(Temp d, Temp s) {
        return new Instr.MOVE("\tmovq `s0,`d0", d, s);
    }

    private static Temp[] T(Temp... a) {
        return a;
    }

    static Instr OPER(String a, Temp[] d, Temp[] s, Label... j) {
        return new Instr.OPER("\t" + a, d, s, j);
    }

    public Temp visit(MOVE s) {
        // TODO
        if (s.dst instanceof MEM) {
            MEM dst = (MEM)s.dst;
            assert dst.size == Frame.wordSize;
            if (CONST32(dst.offset)) {
                // MOVE(MEM(Exp, CONST16), Exp)
                String offset = String.valueOf(dst.offset.value);
                if (dst.exp instanceof NAME) {
                    NAME exp = (NAME) dst.exp;
                    Temp src = s.src.accept(this);
                    insns.add(OPER("movq `s0, +"+offset+"("+exp.label+")", T(), T(src)));
                    return null;
                } else {
                    Temp exp =
                            dst.exp instanceof TEMP ? ((TEMP) dst.exp).temp
                                    : dst.exp.accept(this);
                    Temp src = s.src.accept(this);
                    // stack pointer
                    insns.add(OPER("movq `s0,"+offset+"(`s1)", T(), T(src,exp)));
                    return null;
                }
            }
            //MOVE(MEM,Exp)
            Temp exp = dst.exp.accept(this);
            Temp off = dst.offset.accept(this);
            Temp src = s.src.accept(this);
            insns.add(OPER("movq `s0,(`s1,`s2)", T(), T(src, exp, off)));
            return null;
        }

        //MOVE(Exp, Exp)
        Temp dst = s.dst.accept(this);
        Temp src = s.src.accept(this);
        insns.add(MOVE(dst, src));
        return null;
    }

    public Temp visit(EXP s) {
        return s.exp.accept(this);
    }

    public Temp visit(JUMP s) {
        // TODO
        if (s.exp instanceof NAME) {
            insns.add(OPER("jmpq `j0", T(), T(), s.targets));
            return null;
        }

        Temp s0 = s.exp.accept(this);
        insns.add(OPER("jmpq `s0", T(), T(s0), s.targets));
        return null;
    }

    public Temp visit(CJUMP s) {
        // TODO
        String op;
        switch (s.op) {
            case BEQ: op = "je"; break;
            case BNE: op = "jne"; break;
            case BLT: op = "jl"; break;
            case BGT: op = "jg"; break;
            case BLE: op = "jle"; break;
            case BGE: op = "jge"; break;
            default: throw new Error();
        }
        if (CONST32(s.left) && !CONST32(s.right)) {
            // CJUMP(CONST16, Exp, Label, Label)
            s.swap().accept(this);
            return null;
        }

        if (CONST32(s.right)) {
            // CJUMP(Exp, CONST32, Label, Label)
            CONST c = (CONST) s.right;
            Temp s1 = s.left.accept(this);
            insns.add(OPER("cmpq $"+c.value+",`s0", T(), T(s1)));
            insns.add(OPER(op + " " +s.iftrue, T(), T()));
            //insns.add(OPER("jmp `j0", T(), T(), s.iffalse));
            return null;
        }
        // CJUMP(Exp, Exp, Label, Label)
        Temp s0 = s.left.accept(this);
        Temp s1 = s.right.accept(this);
        insns.add(OPER("cmpq `s1,`s0", T(), T(s0,s1)));
        insns.add(OPER(op + " " +s.iftrue, T(), T()));
        //insns.add(OPER("jmp `j0", T(), T(), s.iffalse));
        return null;
    }

    public Temp visit(LABEL l) {
        // TODO
        insns.add(new Instr.LABEL(l.label.toString()+":", l.label));
        return null;
    }

    public Temp visit(CONST e) {
        // TODO
        Temp d0 = new Temp();
        insns.add(OPER("movabsq $" + e.value +",`d0", T(d0), T()));
        return d0;
    }

    public Temp visit(NAME e) {
        // TODO
        Temp d0 = new Temp();
        insns.add(OPER("leaq " + e.label+"(%rip),`d0", T(d0), T()));
        return d0;
        //return null;
    }

    public Temp visit(TEMP e) {
        return e.temp;
    }

    static int shift(long i) {
        int shift = 0;
        if ((i > 1) && ((i & (i - 1)) == 0)) {
            while (i > 1) {
                shift += 1;
                i >>= 1;
            }
        }
        return shift;
    }

    private static int shift(BigInteger i) {
        int shift = 0;
        if (i.compareTo(BigInteger.ONE) > 0
            && ((i.and(i.subtract(BigInteger.ONE))).signum() == 0)) {
            while (i.compareTo(BigInteger.ONE) > 0) {
                shift += 1;
                i = i.shiftRight(1);
            }
        }
        return shift;
    }

    private static boolean CONST32(Exp e) {
        return e instanceof CONST c && c.value.bitLength() <= 32;
    }

    public Temp visit(BINOP b) {
        // TODO
        String op;
        Exp l = b.left;
        Exp r = b.right;

        switch (b.op) {
            case ADD:
                if (CONST32(l) && !CONST32(r)) {
                    // ADD(CONST32, Exp)
                    Exp t = l;
                    l = r;
                    r =t;
                }
                if (CONST32(r)) {
                    //(Exp, CONST32)
                    CONST c = (CONST) r;
                    Temp d0 = l instanceof TEMP ? ((TEMP) l).temp : l.accept(this);
                    String value = String.valueOf(c.value);
                    insns.add(OPER("addq $"+value+",`d0", T(d0), T(d0)));
                    return d0;
                }
                op = "addq";
                break;
            case AND:
                if (CONST32(l) && !CONST32(r)) {
                    //AND(CONST32, Exp)
                    Exp t = l;
                    l = r;
                    r = t;
                }
                op = "andq";
                break;
            case DIV:
                if (r instanceof CONST) {
                    CONST c = (CONST) r;
                    int shift = shift(c.value);
                    if (shift != 0) {
                        // DIV(Exp, CONST 2^k)
                        Temp d0 = l.accept(this);
                        insns.add(OPER("sarq $"+shift+",`d0", T(d0), T(d0)));
                        return d0;
                    }
                }

                op = "idivq";
                break;

            case DIVU:
                if (r instanceof CONST) {
                    CONST c = (CONST) r;
                    int shift = shift(c.value);
                    if (shift != 0) {
                        // DIVU (Exp, CONST 2^k)
                        Temp d0 = l.accept(this);
                        insns.add(OPER("shrq $"+shift+",`d0", T(d0), T(d0)));
                        return d0;
                    }
                }

                op = "divq";
                break;
            case MOD: {
                Temp s0 = l.accept(this);
                Temp s1 = r.accept(this);
                Temp d0 = new Temp();
                // move left to rax and clear rdx
                insns.add(OPER("mov `s0,%rax", T(), T(s0)));
                insns.add(OPER("cqto", T(), T()));
                //insns.add(OPER("xor `rdx,`rdx", T(), T(Frame.RDX, Frame.RDX)));
                insns.add(OPER("idivq `s0", T(), T(s1)));
                // move result to d0
                insns.add(OPER("mov %rax,`d0", T(d0), T(d0)));
                return d0;
            }

            case MUL: {
                if (r instanceof CONST) {
                    CONST c = (CONST) r;
                    int shift = shift(c.value);
                    if (shift != 0) {
                        // MUL(Exp, CONST 2^k)
                        Temp d0 = l.accept(this);
                        insns.add(OPER("shlq $"+shift+",`d0", T(d0), T(d0)));
                        return d0;
                    }
                }
                if (l instanceof CONST) {
                    CONST c = (CONST) l;
                    int shift = shift(c.value);
                    if (shift != 0) {
                        // MUL(CONST 2^k, Exp)
                        Temp d0 = r.accept(this);
                        insns.add(OPER("shlq $"+shift+",`d0", T(d0), T(d0)));
                        return d0;
                    }
                }
                if (CONST32(l) && !CONST32(r)) {
                    // MUL(CONST32, Exp)
                    Exp t = l;
                    l = r;
                    r = t;
                }
                op = "imulq";
                break;
            }
            case OR:
                if (CONST32(l) && CONST32(r)){
                    // OR(CONST32, Exp)
                    Exp t = l;
                    l = r;
                    r = t;
                }
                op = "orq";
                break;
            case SLL:
                op = "shlq";
                break;
            case SRA:
                op = "sarq";
                break;
            case SRL:
                op = "shrq";
                break;
            case SUB:
                op = "subq";
                break;
            case XOR:
                if (CONST32(l) && !CONST32(r)) {
                    // XOR(CONST16, Exp)
                    Exp t = l;
                    l = r;
                    r = t;
                }
                op = "xorq";
                break;

            default:
                throw new Error();
        }

        Temp s1;
        // move the constant to the register s1
        if (CONST32(r)) {
            CONST c = (CONST) r;
            String offset = String.valueOf(c.value);
            s1 = new Temp();
            insns.add(OPER("movq $"+offset+",`s0", T(s1), T(s1)));
        } else {
            s1 = r.accept(this);
        }
//        if (CONST32(r)) {
//            // op(Exp, CONST32)
//            CONST c = (CONST) r;
//            String offset = String.valueOf(c.value);
//
//
//            if (!op.equals("divq") && !op.equals("idiv")) {
//                Temp d0 = l.accept(this);
//                insns.add(OPER(op+ " $"+offset+",`d0", T(d0), T(d0)));
//                return d0;
//            } else {
//                Temp s0 = l.accept(this);
//                Temp d0 = new Temp();
//                insns.add(OPER("mov `s0,`radivx", T(), T(s0, Frame.RAX)));
//                insns.add(OPER("xor `rdx,`rdx", T(), T(Frame.RDX, Frame.RDX)));
//                insns.add(OPER("div $"+offset, T(), T()));
//                // move result to d0
//                insns.add(OPER("mov `rax,`d0", T(), T(d0,Frame.RAX)));
//                return d0;
//            }
//
//        }
        // op(Exp,Exp)
        Temp d0 = new Temp();
        Temp s0 = l.accept(this);
//        Temp s1 = r.accept(this);
        // using the two register mode for imulq instruction
        if (!op.equals("divq") && !op.equals("idivq")) {
            insns.add(OPER(op + " `s1, `s0", T(s0), T(s0,s1)));
            return s0;
        } else {
            insns.add(OPER("movq `s0, %rax", T(), T(s0)));

            if (op.equals("divq")) {
                insns.add(OPER("xor %rdx,%rdx", T(), T()));
                insns.add(OPER(op+" `s0", T(), T(s1)));
            } else {
                insns.add(OPER("cqto", T(), T()));
                insns.add(OPER(op+" `s0", T(), T(s1)));
            }

            // move result to d0              add d0 to dst T() here????
            insns.add(OPER("movq %rax,`d0", T(d0), T(d0)));
            return d0;
        }

    }

    public Temp visit(MEM mem) {
        // TODO
        assert mem.size == Frame.wordSize;
        Temp dst = new Temp();
        if (CONST32(mem.offset)) {
            //MEM(Exp, CONST32)
            String offset = String.valueOf(mem.offset.value);
            if (mem.exp instanceof NAME) {
                // MEM(NAME, CONST32)
                NAME exp = (NAME) mem.exp;
                // calculate offset + exp.label
                Temp s0 = exp.accept(this);
                insns.add(OPER("movq "+offset+"(`s0),`d0",T(dst), T(s0)));
                return dst;
            } else {
                Temp exp =
                        (mem.exp instanceof TEMP) ? ((TEMP) mem.exp).temp
                                : mem.exp.accept(this);
                // if exp == Frame.FP / Frame.SP???
                insns.add(OPER("movq "+offset+"(`s0),`d0", T(dst), T(exp)));
                return dst;
            }
        }
        //MEM(Exp, CONST)
        Temp exp = mem.exp.accept(this);
        Temp off = mem.offset.accept(this);
        insns.add(OPER("movq (`exp,`off),`d0", T(dst), T(exp,off)));
        return dst;
    }

    public Temp visit(CALL s) {
        String op;
        LinkedList<Temp> uses = new LinkedList<Temp>();
        if (s.func instanceof NAME n) {
            // CALL(NAME, ...)
            op = "call " + n.label;
        } else {
            // CALL(Exp, ...)
            uses.add(s.func.accept(this));
            op = "call *`s0";
        }
        if (s.link instanceof CONST c) {
            assert c.value.signum() == 0;
        } else {
            insns.add(MOVE(Frame.R10, s.link.accept(this)));
            uses.add(Frame.R10);
        }
        int size = 0;
        for (Tree.Exp arg: s.args) {
            int i = size / Frame.wordSize;
            if (arg instanceof Tree.Exp.MEM mem) {
                if (mem.size == Frame.wordSize) {
                    if (i < Frame.argRegs.length) {
                        Temp d0 = Frame.argRegs[i];
                        Temp s0 = arg.accept(this);
                        if (d0 != s0) insns.add(MOVE(d0, s0));
                        uses.add(d0);
                    } else {
                        insns.add(OPER("movq `s0,"
                                + ((i - Frame.argRegs.length) * Frame.wordSize) + "(`s1)", T(),
                                T(arg.accept(this), Frame.RSP)));
                    }
                } else {
                    Temp s0 = mem.exp.accept(this);
                    for (int k = 0; k < mem.size; k += Frame.wordSize) {
                        new MOVE
                            (new MEM
                             (new TEMP(Frame.RSP),
                              new CONST(BigInteger.valueOf(k + size)),
                              Frame.wordSize),
                             new MEM
                             (new TEMP(s0),
                              new CONST(mem.offset.value.add(BigInteger.valueOf(k))),
                              Frame.wordSize)).accept(this);
                    }
                }
                size += mem.size;
            } else {
                if (i < Frame.argRegs.length) {
                    Temp d0 = Frame.argRegs[i];
                    Temp s0 = arg.accept(this);
                    if (d0 != s0) insns.add(MOVE(d0, s0));
                    uses.add(d0);
                } else {
                    insns.add(OPER("movq `s0,"
                            + ((i - Frame.argRegs.length) * Frame.wordSize) + "(`s1)", T(),
                            T(arg.accept(this), Frame.RSP)));
                }
                size += Frame.wordSize;
            }

        }
        insns.add(OPER(op, Frame.callDefs, uses.toArray(new Temp[uses.size()])));
        return Frame.RAX;
    }

    // Canonical trees shouldn't have SEQ so throw an error
    public Temp visit(SEQ n) {
        throw new Error();
    }

    // Canonical trees shouldn't have ESEQ so throw an error
    public Temp visit(ESEQ n) {
        throw new Error();
    }
}
