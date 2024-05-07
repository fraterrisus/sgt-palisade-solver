package org.hitchhikerprod.solver.palisade.pieces;

abstract public class Edge {
    public enum State { YES, NO, MAYBE }

    private State state = State.MAYBE;

    public State state() {
        return state;
    }

    public void state(State state) {
        this.state = state;
    }

    public boolean hasState(State query) {
        return state == query;
    }
}

