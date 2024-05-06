package org.hitchhikerprod.solver.palisade.pieces;

abstract public class Edge {
    public enum State { YES, NO, MAYBE }

    public State state = State.MAYBE;
}

