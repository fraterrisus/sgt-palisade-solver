package org.hitchhikerprod.solver.palisade.pieces;

public class HEdge extends Edge {
    public Cell north;
    public Cell south;
    public Junction east;
    public Junction west;

    public HEdge() {}

    public HEdge(Cell north, Cell south, Junction east, Junction west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }
}
