package org.hitchhikerprod.solver.palisade.pieces;

public class VEdge extends Edge {
    public Junction north;
    public Junction south;
    public Cell east;
    public Cell west;

    public VEdge() {}

    public VEdge(Junction north, Junction south, Cell east, Cell west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }
}
