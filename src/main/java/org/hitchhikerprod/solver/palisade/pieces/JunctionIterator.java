package org.hitchhikerprod.solver.palisade.pieces;

import org.hitchhikerprod.solver.palisade.Board;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class JunctionIterator implements Iterator<Junction> {
    private final Board board;
    private Junction rowPtr = null;
    private Junction cellPtr = null;

    public JunctionIterator(Board board) {
        this.board = board;
    }

    @Override
    public boolean hasNext() {
        return cellPtr == null || rowPtr.south != null || cellPtr.east != null;
    }

    @Override
    public Junction next() {
        if (cellPtr == null) {
            cellPtr = board.getRoot();
            rowPtr = cellPtr;
            return cellPtr;
        }
        if (cellPtr.east != null) {
            cellPtr = cellPtr.east.east;
            return cellPtr;
        }
        if (rowPtr.south != null) {
            rowPtr = rowPtr.south.south;
            cellPtr = rowPtr;
            return cellPtr;
        }
        throw new NoSuchElementException();
    }
}
