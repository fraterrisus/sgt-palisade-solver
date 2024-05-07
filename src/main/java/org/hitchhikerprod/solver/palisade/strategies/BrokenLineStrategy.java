package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.Junction;

import java.util.Map;
import java.util.stream.Collectors;

public class BrokenLineStrategy {
    public static boolean solve(Board board) {
        boolean updates = false;
        for (final Junction junc: board.junctions()) {
            final Map<Edge.State, Long> counts = junc.edges().stream()
                .collect(Collectors.groupingBy(Edge::state, Collectors.counting()));
            for (Edge.State s : Edge.State.values()) counts.putIfAbsent(s, 0L);

            if (counts.get(Edge.State.YES) == 1
                && counts.get(Edge.State.NO) == 2
                && counts.get(Edge.State.MAYBE) == 1) {
                // 1Y, 2N, 1M: the unknown edge must be YES to continue the unbroken line
                junc.edges().stream()
                    .filter(edge -> edge.state() == Edge.State.MAYBE)
                    .forEach(edge -> edge.state(Edge.State.YES));
                updates = true;
            } else if (counts.get(Edge.State.NO) == 3
                && counts.get(Edge.State.MAYBE) == 1) {
                // 3N + 1M: the lone edge can't possibly be YES or it would be broken
                for (Edge e : junc.edges()) {
                    if (e.state() != Edge.State.MAYBE) continue;
                    e.state(Edge.State.NO);
                    board.joinGroups(e);
                }
                updates = true;
            }

        }
        return updates;
    }
}
