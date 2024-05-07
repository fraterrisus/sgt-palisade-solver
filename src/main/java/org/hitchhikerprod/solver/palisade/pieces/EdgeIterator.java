package org.hitchhikerprod.solver.palisade.pieces;

import org.hitchhikerprod.solver.palisade.Board;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EdgeIterator implements Iterator<Edge> {
    private final Board board;
    private boolean edgeRow = true;
    private Junction rowPtr = null;
    private Junction juncPtr = null;

    public EdgeIterator(Board board) {
        this.board = board;
    }

    @Override
    public boolean hasNext() {
        return juncPtr == null || !edgeRow || juncPtr.east == null || rowPtr.south != null;
    }

    @Override
    public Edge next() {
        if (juncPtr == null) {
            rowPtr = board.getRoot();
            juncPtr = rowPtr;
            edgeRow = true;
            return juncPtr.east;
        }
        if (edgeRow) {
            juncPtr = juncPtr.east.east;
            if (juncPtr.east != null) return juncPtr.east;
            if (rowPtr.south == null) throw new NoSuchElementException();
            juncPtr = rowPtr;
            edgeRow = false;
            return juncPtr.south;
        } else {
            if (juncPtr.east != null) {
                juncPtr = juncPtr.east.east;
                return juncPtr.south;
            }
            rowPtr = rowPtr.south.south;
            juncPtr = rowPtr;
            edgeRow = true;
            return juncPtr.east;
        }
    }
}
