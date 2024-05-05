package org.hitchhikerprod.solver.palisade;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board {
    private static final Pattern GAME_SPEC = Pattern.compile(
        "\\A(\\d+)x(\\d+)n(\\d+):([a-z0-9]+)\\z"
    );

    private final int width;
    private final int height;
    private final int region_size;
    private final int[][] clues;

    private Board(int width, int height, int region_size, int[][] clues) {
        this.width = width;
        this.height = height;
        this.region_size = region_size;
        this.clues = clues;
    }

    private static class Builder {
        private record Clue(int x, int y, int hint) {}

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

            int[][] clues = new int[height][width];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    clues[y][x] = -1;
                }
            }
            for (Clue c: clueList) {
                clues[c.y][c.x] = c.hint;
            }
            return new Board(width, height, region_size, clues);
        }
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

    public void solve() {

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                sb.append(clues[y][x] == -1 ? "." : clues[y][x]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
