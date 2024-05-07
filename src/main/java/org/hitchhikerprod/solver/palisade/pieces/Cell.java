package org.hitchhikerprod.solver.palisade.pieces;

import java.util.List;

public class Cell {
    public Long hint;

    public HEdge north;
    public HEdge south;
    public VEdge east;
    public VEdge west;

    public boolean satisfied = false;

    public Cell() {}

    public Cell(HEdge north, HEdge south, VEdge east, VEdge west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    public boolean hasHint(int query) {
        return hasHint((long)query);
    }

    public boolean hasHint(long query) {
        return hint != null && hint == query;
    }

    public List<Edge> edges() {
        return List.of(north, south, east, west);
    }

    @Override
    public String toString() {
        return "Cell{" +
            "hint=" + hint +
            ",satisfied=" + satisfied +
            '}';
    }
}
