package org.hitchhikerprod.solver.palisade.pieces;

public class Junction {
    public VEdge north;
    public VEdge south;
    public HEdge east;
    public HEdge west;

    public Junction() {}

    public Junction(VEdge north, VEdge south, HEdge east, HEdge west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }
}
