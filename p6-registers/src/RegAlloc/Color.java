/* Copyright (C) 1997-2005, Antony L Hosking.
 * All rights reserved.  */
package RegAlloc;

import java.util.*;

import Translate.Temp;

import javax.imageio.plugins.tiff.TIFFImageReadParam;

public class Color {
    final InterferenceGraph ig;
    final int K;
    /**
     * The colors available to the allocator.
     */
    final LinkedList<Temp> colors = new LinkedList<Temp>();
    /**
     * Precolored nodes corresponding to the machine registers.
     */
    final LinkedHashSet<Node> precolored = new LinkedHashSet<Node>();

    /**
     * Return the spilled registers from this round of allocation.
     */
    public Temp[] spills() {
        Temp[] spills = null;
        int spillCount = spilledNodes.size();
        if (spillCount > 0) {
            spills = new Temp[spilledNodes.size()];
            int i = 0;
            for (Node n : spilledNodes)
                spills[i++] = n.temp;
        }
        return spills;
    }

    // Node worklists, sets and stacks
    final LinkedHashSet<Node> simplifyWorklist; // low-degree non-move-related nodes
    final LinkedHashSet<Node> freezeWorklist; // low-degree move-related nodes
    final LinkedHashSet<Node> coalescedNodes; // registers that have been coalesced
    final LinkedList<Node> initial; // temporary registers, not precolored and not yet processed
    final LinkedList<Node> spillWorklist; // high-degree nodes
    final LinkedList<Node> spilledNodes; // nodes marked for spilling during this round
    final LinkedList<Node> coloredNodes; // nodes successfully colored
    final LinkedList<Node> selectStack; // stack containing temporaries removed from the graph
    {
        initial = new LinkedList<Node>();
        simplifyWorklist = new LinkedHashSet<Node>();
        freezeWorklist = new LinkedHashSet<Node>();
        spillWorklist = new LinkedList<Node>();
        spilledNodes = new LinkedList<Node>();
        coalescedNodes = new LinkedHashSet<Node>();
        coloredNodes = new LinkedList<Node>();
        selectStack = new LinkedList<Node>();
    }

    /*
     * Move sets. There are five sets of move instructions, and every move is in
     * exactly one of these sets (after Build through end of Color).
     */

    /**
     * Moves enabled for possible coalescing.
     */
    final LinkedList<Move> worklistMoves = new LinkedList<Move>();
    /**
     * Moves that have been coalesced.
     */
    final LinkedHashSet<Move> coalescedMoves = new LinkedHashSet<Move>();
    /**
     * Moves whose source and target interfere.
     */
    final LinkedHashSet<Move> constrainedMoves = new LinkedHashSet<Move>();
    /**
     * Moves no longer considered for coalescing.
     */
    final LinkedHashSet<Move> frozenMoves = new LinkedHashSet<Move>();
    /**
     * Moves not yet ready for coalescing.
     */
    final LinkedHashSet<Move> activeMoves = new LinkedHashSet<Move>();

    /*
     * Other data structures.
     */

    /**
     * Current degree of each node.
     */
    final LinkedHashMap<Node, Integer> degree = new LinkedHashMap<Node, Integer>();
    /**
     * Moves associated with each node.
     */
    final LinkedHashMap<Node, LinkedList<Move>> moveList = new LinkedHashMap<Node, LinkedList<Move>>();
    /**
     * When a move (u,v) has been coalesced, and v put in coalescedNodes, then
     * alias(v) = u
     */
    final LinkedHashMap<Node, Node> alias = new LinkedHashMap<Node, Node>();

    LinkedList<Move> MoveList(Node n) {
        LinkedList<Move> moves = moveList.get(n);
        if (moves == null) {
            moves = new LinkedList<Move>();
            moveList.put(n, moves);
        }
        return moves;
    }

    void Build() {
        // TODO
        

    }

    void AddEdge(Node u, Node v) {
        if (u != v && !u.adj(v)) {
            if (!precolored.contains(u)) {
                ig.addEdge(u, v);
                degree.put(u, Degree(u) + 1);
            }
            if (!precolored.contains(v)) {
                ig.addEdge(v, u);
                degree.put(v, Degree(v) + 1);
            }
        }
    }

    void MakeWorkList() {
        // TODO
        //LinkedList<Node> initial_copy = new LinkedList<>(initial);
        while (!initial.isEmpty()) {

            // initial = initial \ {n}
            Node n = initial.removeFirst();

            if (n.degree() >= K) {
                spillWorklist.add(n);
            } else if (MoveRelated(n)) {
                freezeWorklist.add(n);
            } else simplifyWorklist.add(n);
        }

    }

    /*
     * Adjacency changes as nodes are selected and coalesced, corresponding to
     * removing them from the interference graph.
     */
    LinkedHashSet<Node> Adjacent(Node n) {
        LinkedHashSet<Node> adj = new LinkedHashSet<Node>(n.succs);
        adj.removeAll(selectStack);
        adj.removeAll(coalescedNodes);
        return adj;
    }

    /*
     * A nodes moves change as adjacency changes, and include only those moves
     * still active (not frozen) but not ready for coalescing, or  moves
     * available for coalescing.
     */
    LinkedHashSet<Move> NodeMoves(Node n) {
        LinkedHashSet<Move> moves = new LinkedHashSet<Move>();
        moves.addAll(activeMoves);
        moves.addAll(worklistMoves);
        moves.retainAll(MoveList(n));
        return moves;
    }

    /*
     * A node is move-related if any of its moves are still active
     */
    boolean MoveRelated(Node n) {
        return !NodeMoves(n).isEmpty();
    }

    void Simplify() {
        // TODO
        Node n = null;

        for(Node na : simplifyWorklist) {
            n = na;
            break;
        }
        if (n != null) {
            simplifyWorklist.remove(n);
            selectStack.add(n);

            for (Node nadj : Adjacent(n)) {
                DecrementDegree(nadj);
            }
        }
    }

    void DecrementDegree(Node m) {
        int d = Degree(m);
        if (d >= 1) {
            degree.computeIfPresent(m, (k,v)->v-1);
        }
        if (d == K) {

            //EnableMoves({m} U Adjacent(m))
            LinkedList<Node> union = new LinkedList<>(Adjacent(m));
            union.add(m);
            EnableMoves(union);

            spillWorklist.remove(m);
            if (MoveRelated(m))
                freezeWorklist.add(m);
            else
                simplifyWorklist.add(m);
        }

    }

    void EnableMoves(List<Node> nodes) {
        for (Node n : nodes) {
            for (Move m : NodeMoves(n)) {
                if (activeMoves.contains(m)) {
                    activeMoves.remove(m);
                    worklistMoves.add(m);
                }
            }
        }
    }

    int Degree(Node n) {
        Integer d = degree.get(n);
        if (d == null)
            return 0;
        return d;
    }

    void Coalesce() {
        // TODO
        Move m = worklistMoves.getFirst();

        //x
        m.src = GetAlias(m.src);
        //y
        m.dst = GetAlias(m.dst);

        Node u = null;
        Node v = null;

        if (precolored.contains(m.dst)) {
            u = m.dst;
            v = m.src;
        } else {
            u = m.src;
            v = m.dst;
        }
        worklistMoves.remove(m);
        if (u.equals(v)) {
            coalescedMoves.add(m);
            AddWorkList(u);
        } else if (precolored.contains(v) || (Adjacent(u).contains(v) || Adjacent(v).contains(u))) {
            constrainedMoves.add(m);
            AddWorkList(u);
            AddWorkList(v);
        } else {
            Node finalU = u;
            // temp = Adjacent(v) U Adjacent(v)
            List<Node> temp = new LinkedList<Node>(Adjacent(u));
            temp.addAll(Adjacent(v));

            if (precolored.contains(u)
                    && (Adjacent(v).stream().reduce(true,(boolSofar, next)->boolSofar && OK(next, finalU),Boolean::logicalAnd))
                    || precolored.contains(u) && Conservative(temp)) {
                coalescedMoves.add(m);
                Combine(u,v);
                AddWorkList(u);
            } else {
                activeMoves.add(m);
            }
        }
    }

    void Combine(Node u, Node v){
        if (!freezeWorklist.remove(v)) {
            spillWorklist.remove(v);
        }
        coalescedNodes.add(v);
        alias.put(v,u);

        // ????? Need to check here.
        // is nodeMoves a data structure? note it's lower case on the paper.
        NodeMoves(u).addAll(NodeMoves(v));

        for (Node t : Adjacent(v)) {
            AddEdge(t,u);
            DecrementDegree(t);
        }

        if (Degree(u) >= K && freezeWorklist.contains(u)) {
            freezeWorklist.remove(u);
            spillWorklist.add(u);
        }
    }




    boolean Conservative(List<Node> nodes) {
        int k = 0;
        for (Node n : nodes) {
            if (Degree(n)>= K)
                k+=1;
        }
        return k < K;
    }
    boolean OK(Node t, Node r) {
        return t.degree() < K || precolored.contains(t) || (Adjacent(r).contains(t) || Adjacent(t).contains(r));
    }
    void AddWorkList(Node n) {
        if (precolored.contains(n) && !MoveRelated(n) && n.degree() < K) {
            freezeWorklist.remove(n);
            simplifyWorklist.add(n);
        }
    }

    void Freeze() {
        // TODO
        Node n = null;
        for (Node na : freezeWorklist) {
            n = na;
        }
        if (n != null) {
            freezeWorklist.remove(n);
            simplifyWorklist.add(n);
            FreezeMoves(n);
        }
    }

    void FreezeMoves(Node n) {
        for (Move m : NodeMoves(n)) {
            boolean isSrc = false;
            if ((m.dst == n) || (isSrc = (m.src == n))) {
                if (!activeMoves.remove(m)) {
                    worklistMoves.remove(m);
                }
                frozenMoves.add(m);
                Node temp = isSrc?m.dst:m.src;

                if (NodeMoves(temp).isEmpty() && Degree(temp) < K) {
                    freezeWorklist.remove(temp);
                    simplifyWorklist.add(temp);
                }

            }
        }
    }

    void SelectSpill() {
        // TODO
        Comparator<Node> c = (o1, o2) -> {
            double diff = (o1.spillCost/o1.degree()) - (o2.spillCost/ o2.degree());

            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else return -1;
        };

        Node n = Collections.min(spillWorklist,c);
        spillWorklist.remove(n);
        simplifyWorklist.add(n);
        FreezeMoves(n);
    }

    void AssignColors() {
        // TODO
        while (!selectStack.isEmpty()) {
            Node n = selectStack.removeFirst();

            LinkedList<Temp> okColors = new LinkedList<>(colors);

            for (Node adj : Adjacent(n)) {
                Node alias = GetAlias(adj);
                if (coloredNodes.contains(alias) || precolored.contains(alias)) {
                    okColors.remove(alias.color);
                }
            }
            if (okColors.isEmpty()) {
                spilledNodes.add(n);
            } else {
                coloredNodes.add(n);
                n.color = okColors.removeFirst();
            }
        }

        for (Node n : coalescedNodes) {
            n.color = GetAlias(n).color;
        }
    }

    Node GetAlias(Node n) {
        if (coalescedNodes.contains(n)) {
            return GetAlias(alias.get(n));
        } else return n;
    }

    private <R> void SetRem(java.util.Collection<R> set, R e) {
        if (!set.remove(e))
            Error(e);
    }

    private <R> void SetAdd(java.util.Collection<R> set, R e) {
        if (!set.add(e))
            Error(e);
    }

    private <R> String Error(R e) {
        String error = "";
        if (e instanceof Node) {
            Node n = (Node) e;
            error += n.temp + "(" + Degree(n) + "):";
            if (precolored.contains(n))
                error += " precolored";
            if (initial.contains(n))
                error += " initial";
            if (simplifyWorklist.contains(n))
                error += " simplifyWorklist";
            if (freezeWorklist.contains(n))
                error += " freezeWorklist";
            if (spillWorklist.contains(n))
                error += " spillWorklist";
            if (spilledNodes.contains(n))
                error += " spilledNodes";
            if (coalescedNodes.contains(n))
                error += " coalescedNodes";
            if (coloredNodes.contains(n))
                error += " coloredNodes";
            if (selectStack.contains(n))
                error += " selectStack";
        } else if (e instanceof Move) {
            Move m = (Move) e;
            error += m.dst + "<=" + m.src + ":";
            if (coalescedMoves.contains(m))
                error += " coalescedMoves";
            if (constrainedMoves.contains(m))
                error += " constrainedMoves";
            if (frozenMoves.contains(m))
                error += " frozenMoves";
            if (worklistMoves.contains(m))
                error += " worklistMoves";
            if (activeMoves.contains(m))
                error += " activeMoves";
        }
        throw new Error(error);
    }

    public Color(InterferenceGraph ig, Translate.Frame frame) {
        this.ig = ig;
        this.K = frame.registers().length;
        for (Temp r : frame.registers()) {
            Node n = ig.get(r);
            precolored.add(n);
            colors.add(r);
        }
        for (Node n : ig.nodes())
            if (n.color == null) {
                initial.add(n);
                degree.put(n, n.outDegree());
            }

        Build();
        MakeWorkList();

        do {
            if (!simplifyWorklist.isEmpty()) {
                // System.err.println("Simplify");
                Simplify();
            } else if (!worklistMoves.isEmpty()) {
                // System.err.println("Coalesce");
                Coalesce();
            } else if (!freezeWorklist.isEmpty()) {
                // System.err.println("Freeze");
                Freeze();
            } else if (!spillWorklist.isEmpty()) {
                // System.err.println("SelectSpill");
                SelectSpill();
            }
        } while (!(simplifyWorklist.isEmpty() && worklistMoves.isEmpty()
            && freezeWorklist.isEmpty() && spillWorklist.isEmpty()));
        AssignColors();
    }

}
