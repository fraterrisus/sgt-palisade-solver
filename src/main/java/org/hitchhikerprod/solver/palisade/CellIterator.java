package org.hitchhikerprod.solver.palisade;

import org.hitchhikerprod.solver.palisade.pieces.Cell;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CellIterator implements Iterator<Cell> {
    Cell rowPtr;
    Cell cellPtr;

    public CellIterator(Board board) {
        cellPtr = board.getRoot().south.east;
        rowPtr = cellPtr;
    }

    @Override
    public boolean hasNext() {
        return cellPtr.south.south != null || cellPtr.east.east != null;
    }

    @Override
    public Cell next() {
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
