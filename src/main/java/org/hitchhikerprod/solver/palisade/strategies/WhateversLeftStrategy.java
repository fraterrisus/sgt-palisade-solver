package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Edge;

import java.util.Set;

public class WhateversLeftStrategy {
    public static boolean solve(Board board) {
        // FIXME: account for multiple disparate sets of total-size 10
        int remainingGroupsSize = board.cellGroups().stream()
            .map(Set::size)
            .filter(size -> size < board.getRegionSize())
            .reduce(Integer::sum)
            .orElse(0);

        if (remainingGroupsSize == board.getRegionSize()) {
            for (Edge e : board.edges()) {
                if (e.hasState(Edge.State.MAYBE)) e.state(Edge.State.NO);
            }
            return true;
        }
        return false;
    }
}
