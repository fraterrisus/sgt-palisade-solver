package org.hitchhikerprod.solver.palisade;

import org.hitchhikerprod.solver.palisade.pieces.Cell;
import org.hitchhikerprod.solver.palisade.pieces.Edge;
import org.hitchhikerprod.solver.palisade.pieces.HEdge;
import org.hitchhikerprod.solver.palisade.pieces.Junction;
import org.hitchhikerprod.solver.palisade.pieces.VEdge;
import org.hitchhikerprod.solver.palisade.strategies.CellHintStrategy;
import org.hitchhikerprod.solver.palisade.strategies.Strategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board {
    private record Clue(int x, int y, long hint) {}

    private static final Pattern GAME_SPEC = Pattern.compile(
        "\\A(\\d+)x(\\d+)n(\\d+):([a-z0-9]+)\\z"
    );

    private static List<Strategy> STRATEGIES = List.of(
        CellHintStrategy::solve
    );

    private final int region_size;
    private final Junction boardRoot;

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

        this.boardRoot = junctions[0][0];
    }

    public static Board from(BufferedReader reader) throws IOException {
        final Matcher matcher = GAME_SPEC.matcher(reader.readLine());
        if (!matcher.matches()) {
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
        boolean anyHelp = true;
        while (anyHelp) {
            anyHelp = false;
            for (Strategy strat : STRATEGIES) {
                anyHelp = anyHelp || strat.solve(this);
            }
            System.out.println(this);
        }
    }

    public Iterable<Cell> cells() {
        return () -> new CellIterator(this);
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
                    case YES -> sb.append("-");
                    case NO -> sb.append(" ");
                    case MAYBE -> sb.append("?");
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
                    case MAYBE -> sb.append("?");
                }
                if (j.east == null) break;
                sb.append(j.east.south.hint == null ? " " : j.east.south.hint);
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
