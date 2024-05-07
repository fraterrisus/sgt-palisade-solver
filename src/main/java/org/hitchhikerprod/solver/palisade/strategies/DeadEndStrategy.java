package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.HEdge;
import org.hitchhikerprod.solver.palisade.pieces.VEdge;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/* If a region needs to expand and there is only one way to do it, then it must expand.
 * Check the cell groups that are on the 'opposite' side of every MAYBE edge surrounding
 * this group. If there are multiple opposite groups, then we have options, so do nothing.
 * If there's only one, we have to join with it. */
public class DeadEndStrategy {
    public static boolean solve(Board board) {
        boolean updates = false;
        final Set<Edge> edgesToRemove = new HashSet<>();
        for (Set<Cell> cellSet : board.cellGroups()) {
            if (cellSet.size() == board.getRegionSize()) continue;

            final Set<Edge> candidates = cellSet.stream().flatMap(c -> c.edges().stream())
                .filter(e -> e.hasState(Edge.State.MAYBE))
                .collect(Collectors.toSet());

            Set<Cell> oppositeSet = null;
            Edge toRemove = null;
            for (Edge e : candidates) {
                final Cell other = getOppositeCell(cellSet, e);

                if (other != null) {
                    final Set<Cell> otherSet = board.cellGroups().stream()
                        .filter(g -> g.contains(other))
                        .findFirst().orElseThrow();
                    if (oppositeSet == null) {
                        // first iteration; remove this edge
                        oppositeSet = otherSet;
                        toRemove = e;
                    } else if (oppositeSet != otherSet) {
                        // more than one opposite set has been found; terminate and do nothing
                        oppositeSet = null;
                        break;
                    } // else we re-found the same opposite set already
                }
            }

            if (oppositeSet != null) edgesToRemove.add(toRemove);
        }
        for (Edge e : edgesToRemove) {
            if (e.hasState(Edge.State.MAYBE)) { // it may get cleaned up by joinGroups
                e.state(Edge.State.NO);
                board.joinGroups(e);
                updates = true;
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
