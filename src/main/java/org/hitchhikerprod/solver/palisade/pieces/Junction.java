package org.hitchhikerprod.solver.palisade.pieces;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    public List<Edge> edges() {
        return Stream.of(north, south, east, west)
            .filter(Objects::nonNull)
            .toList();
    }
}
