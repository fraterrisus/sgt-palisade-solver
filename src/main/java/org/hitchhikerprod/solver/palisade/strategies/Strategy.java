package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;

@FunctionalInterface
public interface Strategy {
    boolean solve(Board board);
}
