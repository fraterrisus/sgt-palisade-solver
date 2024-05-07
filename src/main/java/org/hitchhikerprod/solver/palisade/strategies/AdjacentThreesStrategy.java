package org.hitchhikerprod.solver.palisade.strategies;

import org.hitchhikerprod.solver.palisade.Board;
import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.HEdge;
import org.hitchhikerprod.solver.palisade.pieces.VEdge;

public class AdjacentThreesStrategy {
    public static boolean solve(Board board) {
        boolean updates = false;
        for (final Cell cell : board.cells()) {
            if (cell.satisfied) continue;
            if (!cell.hasHint(3)) continue;
            for (Edge e : cell.edges()) {
                if (e.state() != Edge.State.MAYBE) continue;
                if (e instanceof HEdge h) {
                    if (h.north.hasHint(3) && h.south.hasHint(3)) {
                        h.state(Edge.State.YES);
                        updates = true;
                    }
                } else if (e instanceof VEdge v) {
                    if (v.east.hasHint(3) && v.west.hasHint(3)) {
                        v.state(Edge.State.YES);
                        updates = true;
                    }
                } else throw new RuntimeException();
            }
        }
        return updates;
    }
}
