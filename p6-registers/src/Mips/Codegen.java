/* Copyright (C) 1997-2005, Antony L Hosking.
 * All rights reserved.  */
package Mips;

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
    private Frame frame;
    public Codegen(Frame f) {
        frame = f;
    }

    final LinkedList<Instr> insns = new LinkedList<Instr>();
    public LinkedList<Instr> insns() { return insns; }

    static Instr MOVE(Temp d, Temp s) {
        return new Instr.MOVE("\tmove `d0 `s0", d, s);
    }

    private static Temp[] T(Temp... a) {
        return a;
    }

    static Instr OPER(String a, Temp[] d, Temp[] s, Label... j) {
        return new Instr.OPER("\t" + a, d, s, j);
    }

    /**
     * Returns true if the argument represents an integer constant that can be
     * expressed as a 16-bit literal.
     *
     * @param e a tree expression
     * @return true if a 16-bit constant, false otherwise
     */
    private static boolean CONST16(Exp e) {
        if (!(e instanceof CONST)) return false;
        return ((CONST)e).value.bitLength() <= 16;
    }

    public Temp visit(MOVE s) {
        if (s.dst instanceof MEM) {
            MEM dst = (MEM)s.dst;
            assert dst.size == Frame.wordSize;
            if (CONST16(dst.offset)) {
                // MOVE(MEM(Exp, CONST16), Exp)
                String offset = String.valueOf(dst.offset.value);
                if (dst.exp instanceof NAME) {
                    NAME exp = (NAME) dst.exp;
                    Temp src = s.src.accept(this);
                    insns.add(OPER("sw `s0 " + exp.label + "+" + offset, T(),
                                   T(src)));
                    return null;
                } else {
                    Temp exp =
                        dst.exp instanceof TEMP ? ((TEMP) dst.exp).temp
                            : dst.exp.accept(this);
                    Temp src = s.src.accept(this);
                    if (exp == Frame.FP) {
                        exp = Frame.SP;
                        offset += "+" + frame.name + ".framesize";
                    }
                    insns.add(OPER("sw `s0 " + offset + "(`s1)", T(), T(src,
                                                                        exp)));
                    return null;
                }
            }
            // MOVE(MEM, Exp)
            Temp exp = dst.exp.accept(this);
            Temp off = dst.offset.accept(this);
            Temp src = s.src.accept(this);
            Temp addr = new Temp();
            insns.add(OPER("add `d0 `s0 `s1", T(addr), T(exp, off)));
            insns.add(OPER("sw `s0 (`s1)", T(), T(src, addr)));
            return null;
        }

        // MOVE(Exp, Exp)
        Temp dst = s.dst.accept(this);
        Temp src = s.src.accept(this);
        insns.add(MOVE(dst, src));
        return null;
    }

    public Temp visit(EXP s) {
        return s.exp.accept(this);
    }

    public Temp visit(JUMP s) {
        if (s.exp instanceof NAME) {
            // JUMP(NAME, List<Label>)
            insns.add(OPER("b `j0", T(), T(), s.targets));
            return null;
        }
        // JUMP(Exp, List<Label>)
        Temp s0 = s.exp.accept(this);
        insns.add(OPER("jr `s0", T(), T(s0), s.targets));
        return null;
    }

    public Temp visit(CJUMP s) {
        String op;
        switch (s.op) {
        case BEQ: op = "beq"; break;
        case BNE: op = "bne"; break;
        case BLT: op = "blt"; break;
        case BGT: op = "bgt"; break;
        case BLE: op = "ble"; break;
        case BGE: op = "bge"; break;
        default: throw new Error();
        }
        if (CONST16(s.left) && !CONST16(s.right)) {
            // CJUMP(CONST16, Exp, Label, Label)
            s.swap().accept(this);
            return null;
        }
        if (CONST16(s.right)) {
            // CJUMP(Exp, CONST16, Label, Label)
            CONST c = (CONST) s.right;
            Temp s0 = s.left.accept(this);
            insns.add(OPER(op + " `s0 " + c.value + " `j0", T(), T(s0),
                           s.iftrue, s.iffalse));
            return null;
        }
        // CJUMP(Exp, Exp, Label, Label)
        Temp[] src = T(s.left.accept(this), s.right.accept(this));
        insns.add(OPER(op + " `s0 `s1 `j0", T(), src, s.iftrue, s.iffalse));
        return null;
    }

    public Temp visit(LABEL l) {
        insns.add(new Instr.LABEL(l.label.toString() + ":", l.label));
        return null;
    }

    public Temp visit(CONST e) {
        if (e.value.signum() == 0)
            return Frame.ZERO;
        Temp d0 = new Temp();
        insns.add(OPER("li `d0 " + e.value, T(d0), T()));
        return d0;
    }

    public Temp visit(NAME e) {
        Temp d0 = new Temp();
        insns.add(OPER("la `d0 " + e.label, T(d0), T()));
        return d0;
    }

    public Temp visit(TEMP e) {
        Temp t = e.temp;
        if (t == Frame.FP) {
            t = new Temp();
            insns.add(OPER("addu `d0 `s0 " + frame.name + ".framesize", T(t),
                           T(Frame.SP)));
        }
        return t;
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
        if ((i.compareTo(BigInteger.ONE) > 0) && ((i.and(i.subtract(BigInteger.ONE))).signum() == 0)) {
            while (i.compareTo(BigInteger.ONE) > 0) {
                shift += 1;
                i = i.shiftRight(1);
            }
        }
        return shift;
    }

    public Temp visit(BINOP b) {
        String op;
        Exp l = b.left;
        Exp r = b.right;
        switch (b.op) {
        case ADD:
            if (CONST16(l) && !CONST16(r)) {
                // ADD(CONST16, Exp)
                Exp t = l;
                l = r;
                r = t;
            }
            if (CONST16(r)) {
                // ADD(Exp, CONST16)
                CONST c = (CONST) r;
                String offset = String.valueOf(c.value);
                Temp d0 = new Temp();
                Temp s0 =
                    l instanceof TEMP ? ((TEMP) l).temp : l.accept(this);
                if (s0 == Frame.FP) {
                    // ADD(FP, CONST16)
                    s0 = Frame.SP;
                    offset += "+" + frame.name + ".framesize";
                }
                insns.add(OPER("addu `d0 `s0 " + offset, T(d0), T(s0)));
                return d0;
            }
            op = "addu";
            break;
        case AND:
            if (CONST16(l) && !CONST16(r)) {
                // AND(CONST16, Exp)
                Exp t = l;
                l = r;
                r = t;
            }
            op = "and";
            break;
        case DIV:
            if (r instanceof CONST) {
                CONST c = (CONST) r;
                int shift = shift(c.value);
                if (shift != 0) {
                    // DIV(Exp, CONST 2^k)
                    Temp d0 = new Temp();
                    Temp s0 = l.accept(this);
                    insns.add(OPER("sra `d0 `s0 " + shift, T(d0), T(s0)));
                    return d0;
                }
            }
            op = "div";
            break;
        case DIVU:
            if (r instanceof CONST) {
                CONST c = (CONST) r;
                int shift = shift(c.value);
                if (shift != 0) {
                    // DIVU(Exp, CONST 2^k)
                    Temp d0 = new Temp();
                    Temp s0 = l.accept(this);
                    insns.add(OPER("srl `d0 `s0 " + shift, T(d0), T(s0)));
                    return d0;
                }
            }
            op = "divu";
            break;
        case MOD: {
            Temp s0 = l.accept(this);
            Temp s1 = r.accept(this);
            Temp d0 = new Temp();
            insns.add(OPER("divu `s0 `s1", T(), T(s0, s1)));
            insns.add(OPER("mfhi `d0", T(d0), T()));
            return d0;
        }
        case MUL:
            if (r instanceof CONST) {
                CONST c = (CONST) r;
                int shift = shift(c.value);
                if (shift != 0) {
                    // MUL(Exp, CONST 2^k)
                    Temp d0 = new Temp();
                    Temp s0 = l.accept(this);
                    insns.add(OPER("sll `d0 `s0 " + shift, T(d0), T(s0)));
                    return d0;
                }
            }
            if (l instanceof CONST) {
                CONST c = (CONST) l;
                int shift = shift(c.value);
                if (shift != 0) {
                    // MUL(CONST 2^k, Exp)
                    Temp d0 = new Temp();
                    Temp s0 = r.accept(this);
                    insns.add(OPER("sll `d0 `s0 " + shift, T(d0), T(s0)));
                    return d0;
                }
            }
            if (CONST16(l) && !CONST16(r)) {
                // MUL(CONST16, Exp)
                Exp t = l;
                l = r;
                r = t;
            }
            op = "mul";
            break;
        case OR:
            if (CONST16(l) && !CONST16(r)) {
                // OR(CONST16, Exp)
                Exp t = l;
                l = r;
                r = t;
            }
            op = "or";
            break;
        case SLL:
            op = "sll";
            break;
        case SRA:
            op = "sra";
            break;
        case SRL:
            op = "srl";
            break;
        case SUB:
            op = "subu";
            break;
        case XOR:
            if (CONST16(l) && !CONST16(r)) {
                // XOR(CONST16, Exp)
                Exp t = l;
                l = r;
                r = t;
            }
            op = "xor";
            break;
        default:
            throw new Error();
        }
        if (CONST16(r)) {
            // op(Exp, CONST16)
            CONST c = (CONST) r;
            String offset = String.valueOf(c.value);
            Temp d0 = new Temp();
            Temp s0 = l.accept(this);
            insns.add(OPER(op + " `d0 `s0 " + offset, T(d0), T(s0)));
            return d0;
        }
        Temp d0 = new Temp();
        Temp s0 = l.accept(this);
        Temp s1 = r.accept(this);
        insns.add(OPER(op + " `d0 `s0 `s1", T(d0), T(s0, s1)));
        return d0;
    }

    public Temp visit(MEM mem) {
        assert mem.size == Frame.wordSize;
        Temp dst = new Temp();
        if (CONST16(mem.offset)) {
            // MEM(Exp, CONST16)
            String offset = String.valueOf(mem.offset.value);
            if (mem.exp instanceof NAME) {
                // MEM(NAME, CONST16)
                NAME exp = (NAME) mem.exp;
                insns.add(OPER("lw `d0 " + exp.label + "+" + offset, T(dst),
                               T()));
                return dst;
            } else {
                Temp exp =
                    (mem.exp instanceof TEMP) ? ((TEMP) mem.exp).temp
                        : mem.exp.accept(this);
                if (exp == Frame.FP) {
                    // MEM(FP, CONST16)
                    exp = Frame.SP;
                    offset += "+" + frame.name + ".framesize";
                }
                insns.add(OPER("lw `d0 " + offset + "(`s0)", T(dst), T(exp)));
                return dst;
            }
        }
        // MEM(Exp, CONST)
        Temp exp = mem.exp.accept(this);
        Temp off = mem.offset.accept(this);
        Temp addr = new Temp();
        insns.add(OPER("add `d0 `s0 `s1", T(addr), T(exp, off)));
        insns.add(OPER("lw `d0 (`s0)", T(dst), T(addr)));
        return dst;
    }

    public Temp visit(CALL s) {
        String op;
        LinkedList<Temp> uses = new LinkedList<Temp>();
        if (s.func instanceof NAME) {
            // CALL(NAME, ...)
            op = "jal " + ((NAME) s.func).label.toString();
        } else {
            // CALL(Exp, ...)
            uses.add(s.func.accept(this));
            op = "jalr `s0";
        }
        if (s.link instanceof CONST) {
            assert ((CONST)s.link).value.signum() == 0;
        } else {
            Temp link = s.link.accept(this);
            insns.add(MOVE(Frame.V0, link));
            uses.add(Frame.V0);
        }
        int size = 0;
        for (Tree.Exp arg: s.args) {
            int i = size / Frame.wordSize;
            if (arg instanceof Tree.Exp.MEM) {
                Tree.Exp.MEM mem = (Tree.Exp.MEM)arg;
                if (mem.size == Frame.wordSize) {
                    if (i < Frame.argRegs.length) {
                        Temp d0 = Frame.argRegs[i];
                        Temp s0 = arg.accept(this);
                        if (d0 != s0)
                            insns.add(MOVE(d0, s0));
                        uses.add(d0);
                    } else {
                        insns.add(OPER("sw `s0,"
                                + (i * Frame.wordSize) + "(`s1)", T(),
                                T(arg.accept(this), Frame.SP)));
                    }
                } else {
                    Temp s0 = mem.exp.accept(this);
                    for (int k = 0; k < mem.size; k += Frame.wordSize) {
                        new MOVE(
                                new MEM(new TEMP(Frame.SP), new CONST(BigInteger.valueOf(k + size)), Frame.wordSize),
                                new MEM(new TEMP(s0), new CONST(mem.offset.value.add(BigInteger.valueOf(k))), Frame.wordSize)).accept(this);
                    }
                }
                size += mem.size;
            } else {
                if (i < Frame.argRegs.length) {
                    Temp d0 = Frame.argRegs[i];
                    Temp s0 = arg.accept(this);
                    if (d0 != s0)
                        insns.add(MOVE(d0, s0));
                    uses.add(d0);
                } else {
                    insns.add(OPER("sw `s0,"
                            + (i * Frame.wordSize) + "(`s1)", T(),
                            T(arg.accept(this), Frame.SP)));
                }
                size += Frame.wordSize;
            }

        }
        insns.add(OPER(op, Frame.callDefs, uses.toArray(new Temp[uses.size()])));
        return Frame.V0;
    }

    // Canonical trees shouldn't have SEQ so throw an error
    public Temp visit(SEQ n) {
        throw new Error();
    }

    // Canonical trees shouldn't have ESEQ so thrown an error
    public Temp visit(ESEQ n) {
        throw new Error();
    }
}
