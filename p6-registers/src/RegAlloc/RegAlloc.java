/* Copyright (C) 1997-2005, Antony L Hosking.
 * All rights reserved.  */
package RegAlloc;

import java.util.*;
import Translate.Temp;
import Translate.Tree;
import Translate.Tree.Exp.*;
import Translate.Tree.Stm.*;
import Assem.Instr;

public class RegAlloc implements Temp.Map {
    FlowGraph.AssemFlowGraph cfg;
    Liveness ig;
    public Temp[] spills;
    Color color;

    public Temp get(Temp temp) {
        Temp t = ig.get(temp).color;
        if (t == null)
            t = temp;
        return t;
    }

    // set spilling to true when the spill method is implemented
    public static boolean spilling = true;

    public RegAlloc(Translate.Frame frame, LinkedList<Instr> insns,
            java.io.PrintWriter out) {
        for (;;) {
            out.println("# Control Flow Graph:");
            cfg = new FlowGraph.AssemFlowGraph(frame, insns);
            cfg.show(out);
            out.println("# Interference Graph:");
            ig = new Liveness(cfg, frame);
            ig.show(out);
            color = new Color(ig, frame);
            spills = color.spills();
            if (spills == null)
                break;
            out.println("# Spills:");
            for (Temp s : spills)
                out.println(s);
            if (!spilling)
                throw new Error("Spilling unimplemented");
            for (Temp s : spills) {
                Translate.Frame.Access access = frame.allocLocal(frame.wordSize);
                for (ListIterator<Instr> i = insns.listIterator(); i.hasNext();) {
                    Instr insn = i.next();
                    for (Temp u : insn.use) {
                        if (u == s) {
                            Temp t = new Temp();
                            t.spillable = false;
                            Tree.Stm stm = new MOVE(new TEMP(t), access.exp(frame.FP()));
                            i.previous();
                            Translate.Frame.CodeGen cg = frame.codegen();
                            stm.accept(cg);
                            for (Instr asm : cg.insns())
                                i.add(asm);
                            insn.replaceUse(s, t);
                            if (insn != i.next())
                                throw new Error();
                            break;
                        }
                    }
                    for (Temp d : insn.def) {
                        if (d == s) {
                            Temp t = new Temp();
                            t.spillable = false;
                            insn.replaceDef(s, t);
                            Tree.Stm stm = new MOVE(access.exp(frame.FP()), new TEMP(t));
                            Translate.Frame.CodeGen cg = frame.codegen();
                            stm.accept(cg);
                            for (Instr asm : cg.insns())
                                i.add(asm);
                            break;
                        }
                    }
                }
            }
            out.println("# Instructions (after spilling):");
            Temp.Map map = new Temp.Map.Default();
            for (Assem.Instr i : insns)
                out.println(i.format(map));
            out.flush();
        }
        out.println("# Register Allocation:");
        for (Node n : ig.nodes()) {
            out.print(n.temp);
            out.print("->");
            out.println(n.color);
        }
    }
}
