/* Copyright (C) 1997-2023, Antony L Hosking.
 * All rights reserved.  */

package mojo;

import java.io.*;
import java.util.*;

import Translate.*;
import mojo.parse.*;
import static mojo.Absyn.*;

public class Main {
    static PrintWriter out, dbg;

    static void emitProc(Frag.Proc f) {
        Frame frame = f.frame;
        dbg.println("PROCEDURE " + frame.name);
        LinkedList<Tree.Stm> traced = new LinkedList<Tree.Stm>();
        if (f.body != null) {
            dbg.println("# Before canonicalization (trees):");
            new Tree.Print(dbg, f.body);

            LinkedList<Tree.Stm> stms = new Canon.Canon(f.body).stms();

            dbg.println("# After canonicalization (trees):");
            new Tree.Print(dbg, stms);

            dbg.println("# Basic Blocks:");
            Canon.BasicBlocks blocks = new Canon.BasicBlocks(stms);
            for (LinkedList<Tree.Stm> b : blocks.list)
                new Tree.Print(dbg, b);

            dbg.println("# Trace Scheduled:");
            new Canon.TraceSchedule(blocks, traced);
            new Tree.Print(dbg, traced);
        }
        dbg.println("# With procedure entry/exit:");
        frame.procEntryExit1(traced);
        new Tree.Print(dbg, traced);
        dbg.println("# Instructions:");
        Frame.CodeGen cg = frame.codegen();
        for (Tree.Stm s : traced)
            s.accept(cg);
        LinkedList<Assem.Instr> insns = cg.insns();
        Temp.Map map = new Temp.Map.Default();
        for (Assem.Instr i : insns) {
            dbg.print(i.format(map));
            dbg.print("\t# ");
            for (Temp d : i.def)
                dbg.print(d + " ");
            dbg.print((i instanceof Assem.Instr.MOVE) ? ":= " : "<- ");
            for (Temp u : i.use)
                dbg.print(u + " ");
            if (i.jumps.length > 0) {
                dbg.print(": goto ");
                for (Temp.Label l : i.jumps)
                    dbg.print(l + " ");
            }
            dbg.println();
        }
        dbg.flush();
        frame.procEntryExit2(insns);
        map = new RegAlloc.RegAlloc(frame, insns, dbg);
        dbg.println("# Assembly code:");
        frame.procEntryExit3(insns, map);
        for (Assem.Instr i : insns) {
            String insn = i.format(map);
            out.println(insn);
            dbg.println(insn);
        }
        out.flush();
        dbg.flush();
        dbg.println("END " + frame.name);
        dbg.flush();
    }

    public static boolean useFP = false;
    public static boolean verbose = true;
    public static boolean spilling = true;
    public static boolean coalescing = true;

    private static void usage() {
        String usage =
            "Usage: java mojo.Main [-useFP|-nouseFP]"
            + "[-target=[Mips|PPCDarwin|PPCLinux|x64]]"
            + "[-quiet|-verbose]"
            + "[-spill|-nospill] [-coalesce|-nocoalesce]"
            + "<source>.mj";
        throw new java.lang.Error(usage);
    }

    static String mainClass;

    public static void main(String[] args) {
        if (args.length < 1) usage();
        boolean main = false;
        Frame target = new x64.Frame("");
        if (args.length > 1)
            for (String arg : args) {
                if (arg.equals("-main"))
                    main = true;
                else if (arg.equals("-useFP")) useFP = true;
                else if (arg.equals("-nouseFP")) useFP = false;
                else if (arg.equals("-quiet")) verbose = false;
                else if (arg.equals("-verbose")) verbose = true;
                else if (arg.equals("-target=Mips"))
                    target = new Mips.Frame();
                else if (arg.equals("-target=x64"))
                    target = new x64.Frame("");
                else if (arg.equals("-target=x64osx"))
                    target = new x64.Frame("_");
                else if (arg.startsWith("-")) usage();
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
            String[] split = file.getName().split("\\.");
            if (split.length != 2) {
                split[0] = file.getName();
                split[1] = null;
            }
            String dst = split[0] + ".s";
            out = new PrintWriter(new FileOutputStream(dst));
            dbg =
                (verbose) ? new PrintWriter(System.out)
                : new PrintWriter(new NullOutputStream());
            for (Frag f : semant.frags) {
                if (f instanceof Frag.Proc proc)
                    emitProc(proc);
                else {
                    dbg.println(f);
                    dbg.flush();
                    out.println(f);
                }
            }
            out.close();
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
}

class NullOutputStream extends java.io.OutputStream {
    public void write(int b) {}
}
