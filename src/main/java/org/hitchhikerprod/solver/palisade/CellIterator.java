package org.hitchhikerprod.solver.palisade;

import org.hitchhikerprod.solver.palisade.pieces.Cell;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CellIterator implements Iterator<Cell> {
    private final Board board;
    private Cell rowPtr = null;
    private Cell cellPtr = null;

    public CellIterator(Board board) {
        this.board = board;
    }

    @Override
    public boolean hasNext() {
        return cellPtr == null || rowPtr.south.south != null || cellPtr.east.east != null;
    }

    @Override
    public Cell next() {
        if (cellPtr == null) {
            cellPtr = board.getRoot().south.east;
            rowPtr = cellPtr;
            return cellPtr;
        }
        if (cellPtr.east.east != null) {
            cellPtr = cellPtr.east.east;
            return cellPtr;
        }
        if (cellPtr.south.south != null) {
            rowPtr = rowPtr.south.south;
            cellPtr = rowPtr;
            return cellPtr;
        }
        throw new NoSuchElementException();
    }
}