package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.HEdge;
import org.hitchhikerprod.solver.palisade.pieces.VEdge;

import java.util.Set;
import java.util.stream.Collectors;

/* If two adjacent groups are too large to join (i.e. the resulting group would be too big)
 * then we can set all intermediate edges to YES. */
public class BloatedGroupStrategy {
    public static boolean solve(Board board) {
        boolean updates = false;
        for (Set<Cell> set1 : board.cellGroups()) {
            final Set<Edge> candidates = set1.stream().flatMap(c -> c.edges().stream())
                .filter(e -> e.hasState(Edge.State.MAYBE))
                .collect(Collectors.toSet());

            for (Edge e : candidates) {
                final Cell other = getOppositeCell(set1, e);
                if (other != null) {
                    final Set<Cell> set2 = board.cellGroups().stream()
                        .filter(g -> g.contains(other))
                        .findFirst().orElseThrow();
                    if (set1.size() + set2.size() > board.getRegionSize()) {
                        e.state(Edge.State.YES);
                        updates = true;
                    }
                }
            }
        }
        return updates;
    }

    private static Cell getOppositeCell(Set<Cell> cellSet, Edge e) {
        final Cell other;
        if (e instanceof HEdge h) {
            if (cellSet.contains(h.north)) other = h.south;
            else if (cellSet.contains(h.south)) other = h.north;
            else other = null;
        } else if (e instanceof VEdge v) {
            if (cellSet.contains(v.east)) other = v.west;
            else if (cellSet.contains(v.west)) other = v.east;
            else other = null;
        } else other = null;
        return other;
    }
}
