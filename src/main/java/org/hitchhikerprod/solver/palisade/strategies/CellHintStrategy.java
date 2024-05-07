package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.Edge;

import java.util.Map;
import java.util.stream.Collectors;

public class CellHintStrategy {
    public static boolean solve(Board board) {
        boolean updates = false;
        for (final Cell cell: board.cells()) {
            if (cell.satisfied) continue;
            if (cell.hint == null) continue;

            final Map<Edge.State, Long> counts = cell.edges().stream()
                .collect(Collectors.groupingBy(Edge::state, Collectors.counting()));
            for (Edge.State s : Edge.State.values()) counts.putIfAbsent(s, 0L);

            if (cell.hint.equals(counts.get(Edge.State.YES))) {
                // If the correct number of edges is already YES, set everything unknown to NO.
                for (Edge e : cell.edges()) {
                    if (e.state() != Edge.State.MAYBE) continue;
                    e.state(Edge.State.NO);
                    board.joinGroups(e);
                }
                cell.satisfied = true;
                updates = true;
            } else if (cell.hint.equals(counts.get(Edge.State.YES) + counts.get(Edge.State.MAYBE))) {
                // If the only way to get to the correct number of edges is to assign all
                // unknown edges to YES, then do so.
                cell.edges().stream()
                    .filter(edge -> edge.state() == Edge.State.MAYBE)
                    .forEach(edge -> edge.state(Edge.State.YES));
                cell.satisfied = true;
                updates = true;
            }
        }
        return updates;
    }
}
