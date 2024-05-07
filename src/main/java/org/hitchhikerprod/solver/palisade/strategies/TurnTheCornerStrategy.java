package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.Junction;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TurnTheCornerStrategy {
    public static boolean solve(Board board) {
        final Set<Edge> edgesToRemove = new HashSet<>();
        final Set<Edge> edgesToAdd = new HashSet<>();
        for (final Junction junc : board.junctions()) {
            final Map<Edge.State, Long> counts = junc.edges().stream()
                .collect(Collectors.groupingBy(Edge::state, Collectors.counting()));
            for (Edge.State s : Edge.State.values()) counts.putIfAbsent(s, 0L);

            if (counts.get(Edge.State.NO) == 2 && counts.get(Edge.State.MAYBE) == 2) {
                final Cell neighbor = getNeighbor(junc);
                if (neighbor == null) continue;

                final Map<Edge.State, Long> nCounts = neighbor.edges().stream()
                    .collect(Collectors.groupingBy(Edge::state, Collectors.counting()));
                for (Edge.State s : Edge.State.values()) nCounts.putIfAbsent(s, 0L);

                if (neighbor.hasHint(1) && nCounts.get(Edge.State.YES) == 0) {
                    edgesToRemove.addAll(junc.edges());
                } else if (neighbor.hasHint(2) && nCounts.get(Edge.State.YES) == 1) {
                    edgesToRemove.addAll(junc.edges());
                } else if (neighbor.hasHint(3) && nCounts.get(Edge.State.YES) < 2) {
                    edgesToAdd.addAll(junc.edges());
                }
            }
        }
        boolean updates = false;
        for (Edge e : edgesToAdd) {
            if (e.hasState(Edge.State.MAYBE)) {
                e.state(Edge.State.YES);
                updates = true;
            }
        }
        for (Edge e : edgesToRemove) {
            if (e.hasState(Edge.State.MAYBE)) {
                e.state(Edge.State.NO);
                board.joinGroups(e);
                updates = true;
            }
        }
        return updates;
    }

    private static Cell getNeighbor(Junction junc) {
        if (junc.north.hasState(Edge.State.MAYBE)) {
            if (junc.east.hasState(Edge.State.MAYBE)) {
                return junc.north.east;
            } else if (junc.west.hasState(Edge.State.MAYBE)) {
                return junc.north.west;
            }
        } else if (junc.south.hasState(Edge.State.MAYBE)) {
            if (junc.east.hasState(Edge.State.MAYBE)) {
                return junc.south.east;
            } else if (junc.west.hasState(Edge.State.MAYBE)) {
                return junc.south.west;
            }
        }
        return null;
    }
}
