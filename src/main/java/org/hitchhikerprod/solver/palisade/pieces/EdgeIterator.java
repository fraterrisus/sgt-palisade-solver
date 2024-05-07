package org.hitchhikerprod.solver.palisade.pieces;

import org.hitchhikerprod.solver.palisade.Board;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class EdgeIterator implements Iterator<Edge> {
    private final Board board;

    public EdgeIterator(Board board) {
        this.board = board;
    }

    @Override
    public boolean hasNext() {
        return false; // TODO
    }

    @Override
    public Edge next() {
        throw new NoSuchElementException(); // TODO
    }
}
