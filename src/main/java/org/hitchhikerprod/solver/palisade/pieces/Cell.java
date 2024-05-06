package org.hitchhikerprod.solver.palisade.pieces;

public class Cell {
    public Integer hint;

    public HEdge north;
    public HEdge south;
    public VEdge east;
    public VEdge west;

    public Cell() {}

    public Cell(HEdge north, HEdge south, VEdge east, VEdge west) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }
}
