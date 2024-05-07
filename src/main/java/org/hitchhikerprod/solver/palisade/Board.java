package org.hitchhikerprod.solver.palisade;

import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.CellIterator;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.EdgeIterator;
import org.hitchhikerprod.solver.palisade.pieces.HEdge;
import org.hitchhikerprod.solver.palisade.pieces.Junction;
import org.hitchhikerprod.solver.palisade.pieces.JunctionIterator;
import org.hitchhikerprod.solver.palisade.pieces.VEdge;
import org.hitchhikerprod.solver.palisade.strategies.AdjacentThreesStrategy;
import org.hitchhikerprod.solver.palisade.strategies.BloatedGroupStrategy;
import org.hitchhikerprod.solver.palisade.strategies.BrokenLineStrategy;
import org.hitchhikerprod.solver.palisade.strategies.CellHintStrategy;
import org.hitchhikerprod.solver.palisade.strategies.DeadEndStrategy;
import org.hitchhikerprod.solver.palisade.strategies.Strategy;
import org.hitchhikerprod.solver.palisade.strategies.TurnTheCornerStrategy;
import org.hitchhikerprod.solver.palisade.strategies.WhateversLeftStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    private record Clue(int x, int y, long hint) {}

    private static final Pattern GAME_SPEC = Pattern.compile(
        "\\A(\\d+)x(\\d+)n(\\d+):([a-z0-9]+)\\z"
    );

    private static final List<Strategy> ONE_TIME_STRATEGIES = List.of(
        AdjacentThreesStrategy::solve
    );

    private static final List<Strategy> REPEAT_STRATEGIES = List.of(
        BrokenLineStrategy::solve,
        CellHintStrategy::solve,
        DeadEndStrategy::solve,
        BloatedGroupStrategy::solve,
        TurnTheCornerStrategy::solve
    );

    private final int region_size;
    private final Junction boardRoot;
    private final Set<Set<Cell>> cellGroups;

    private Board(int width, int height, int region_size, List<Clue> clues) {
        this.region_size = region_size;

        Junction[][] junctions = new Junction[height+1][width+1];
        HEdge[][] horizontal_edges = new HEdge[height+1][width];
        VEdge[][] vertical_edges = new VEdge[height][width+1];
        Cell[][] cells = new Cell[height][width];

        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                junctions[y][x] = new Junction();
                if (x < width) horizontal_edges[y][x] = new HEdge();
                if (y < height) vertical_edges[y][x] = new VEdge();
                if (x < width && y < height) cells[y][x] = new Cell();
            }
        }

        for (Clue clue : clues) {
            cells[clue.y][clue.x].hint = clue.hint;
        }

        for (int y = 0; y <= height; y++) {
            for (int x = 0; x <= width; x++) {
                final Junction thisJunction = junctions[y][x];
                if (y > 0) thisJunction.north = vertical_edges[y-1][x];
                if (x > 0) thisJunction.west = horizontal_edges[y][x-1];
                if (y < height) thisJunction.south = vertical_edges[y][x];
                if (x < width) thisJunction.east = horizontal_edges[y][x];

                if (x < width) {
                    final HEdge thisHEdge = horizontal_edges[y][x];
                    thisHEdge.west = junctions[y][x];
                    thisHEdge.east = junctions[y][x + 1];
                    if (y > 0) thisHEdge.north = cells[y - 1][x];
                    if (y < height) thisHEdge.south = cells[y][x];
                }

                if (y < height) {
                    final VEdge thisVEdge = vertical_edges[y][x];
                    thisVEdge.north = junctions[y][x];
                    thisVEdge.south = junctions[y+1][x];
                    if (x > 0) thisVEdge.west = cells[y][x - 1];
                    if (x < width) thisVEdge.east = cells[y][x];
                }

                if (x < width && y < height) {
                    final Cell thisCell = cells[y][x];
                    thisCell.north = horizontal_edges[y][x];
                    thisCell.west = vertical_edges[y][x];
                    thisCell.south = horizontal_edges[y + 1][x];
                    thisCell.east = vertical_edges[y][x + 1];
                }
            }
        }

        for (int x = 0; x < width; x++) {
            horizontal_edges[0][x].state(Edge.State.YES);
            horizontal_edges[height][x].state(Edge.State.YES);
        }

        for (int y = 0; y < height; y++) {
            vertical_edges[y][0].state(Edge.State.YES);
            vertical_edges[y][width].state(Edge.State.YES);
        }

        cellGroups = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cellGroups.add(Set.of(cells[y][x]));
            }
        }

        this.boardRoot = junctions[0][0];
    }

    public static Board from(BufferedReader reader) throws IOException {
        final String rawGameId = reader.readLine();
        final Matcher matcher = GAME_SPEC.matcher(rawGameId);
        if (matcher.matches()) {
            System.out.println(rawGameId);
        } else {
            throw new IOException("Couldn't parse gameID string");
        }

        reader.close();

        final int max_x = Integer.parseInt(matcher.group(1));
        final Builder builder = new Builder()
            .width(max_x)
            .height(Integer.parseInt(matcher.group(2)))
            .region_size(Integer.parseInt(matcher.group(3)));

        int x = 0;
        int y = 0;
        for (char ch : matcher.group(4).toCharArray()) {
            final int delta;
            if (ch >= '0' && ch <= '9') {
                builder.clue(x, y, ch - '0');
                delta = 1;
            } else {
                delta = (ch - 'a') + 1;
            }
            x += delta;
            while (x >= max_x) {
                y++;
                x -= max_x;
            }
        }

        return builder.build();
    }

    public int getRegionSize() {
        return region_size;
    }

    public Junction getRoot() {
        return boardRoot;
    }

    public void solve() {
        for (Strategy strat : ONE_TIME_STRATEGIES) {
            if (strat.solve(this)) System.out.println(this);
        }

        boolean anyHelp = true;
        while (anyHelp) {
            anyHelp = false;
            for (Strategy strat : REPEAT_STRATEGIES) {
                if (strat.solve(this)) {
                    System.out.println(this);
                    anyHelp = true;
                }
            }

            if (WhateversLeftStrategy.solve(this)) {
                System.out.println(this);
                anyHelp = false;
            }
        }
    }

    public Set<Set<Cell>> cellGroups() {
        return cellGroups;
    }

    public Iterable<Cell> cells() {
        return () -> new CellIterator(this);
    }

    public Iterable<Edge> edges() {
        return () -> new EdgeIterator(this);
    }

    public Iterable<Junction> junctions() {
        return () -> new JunctionIterator(this);
    }

    /* To mark an edge as NO is to join two cell groups that are adjacent across that edge.
     * In doing so, we must search all the other MAYBE edges that separate the two groups and
     * mark them NO as well. */
    public void joinGroups(Edge edge) {
        final Set<Cell> set1, set2;
        if (edge instanceof HEdge h) {
            set1 = cellGroups.stream()
                .filter(set -> set.contains(h.north))
                .findFirst().orElseThrow();
            set2 = cellGroups.stream()
                .filter(set -> set.contains(h.south))
                .findFirst().orElseThrow();
        } else if (edge instanceof VEdge v) {
            set1 = cellGroups.stream()
                .filter(set -> set.contains(v.west))
                .findFirst().orElseThrow();
            set2 = cellGroups.stream()
                .filter(set -> set.contains(v.east))
                .findFirst().orElseThrow();
        } else throw new RuntimeException();

        cellGroups.remove(set1);
        cellGroups.remove(set2);

        final Set<Cell> newSet = Stream.concat(set1.stream(), set2.stream())
            .collect(Collectors.toUnmodifiableSet());
        cellGroups.add(newSet);

        final Set<Edge> edgeSet = set1.stream().flatMap(c -> c.edges().stream())
            .filter(e -> e.hasState(Edge.State.MAYBE))
            .collect(Collectors.toSet()); // apply uniqueness
        for (Edge e : edgeSet) {
            if (e instanceof HEdge h) {
                if (set2.contains(h.north) || set2.contains(h.south)) {
                    h.state(Edge.State.NO);
                }
            } else if (e instanceof VEdge v) {
                if (set2.contains(v.west) || set2.contains(v.east)) {
                    v.state(Edge.State.NO);
                }
            } else throw new RuntimeException();
        }

        // If the new set is complete and there are any MAYBE edges, mark them YES.
        if (newSet.size() == this.region_size) {
            newSet.stream().flatMap(c -> c.edges().stream())
                .filter(e -> e.hasState(Edge.State.MAYBE))
                .forEach(e -> e.state(Edge.State.YES));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        Junction rowRoot = boardRoot;
        while (true) {
            Junction j = rowRoot;
            while (true) {
                sb.append("+");
                if (j.east == null) break;
                switch (j.east.state()) {
                    case YES -> sb.append("===");
                    case NO -> sb.append("   ");
                    case MAYBE -> sb.append(" - ");
                }
                j = j.east.east;
            }
            sb.append("\n");

            j = rowRoot;
            if (j.south == null) break;
            while (true) {
                switch (j.south.state()) {
                    case YES -> sb.append("|");
                    case NO -> sb.append(" ");
                    case MAYBE -> sb.append("'");
                }
                if (j.east == null) break;
                sb.append(" ");
                sb.append(j.east.south.hint == null ? " " : j.east.south.hint);
                sb.append(" ");
                j = j.east.east;
            }
            sb.append("\n");
            rowRoot = rowRoot.south.south;
        }

        return sb.toString();
    }

    private static class Builder {
        private int width = -1;
        private int height = -1;
        private int region_size = -1;
        private final List<Clue> clueList = new ArrayList<>();

        Builder() {}

        Builder width(int width) {
            this.width = width;
            return this;
        }

        Builder height(int height) {
            this.height = height;
            return this;
        }

        Builder region_size(int region_size) {
            this.region_size = region_size;
            return this;
        }

        Builder clue(int x, int y, int hint) {
            clueList.add(new Clue(x, y, hint));
            return this;
        }

        Board build() {
            if ((width == -1) || (height == -1) || (region_size == -1)) {
                throw new IllegalArgumentException("Can't build; not all parameters have been specified");
            }

            return new Board(width, height, region_size, clueList);
        }
    }
}
