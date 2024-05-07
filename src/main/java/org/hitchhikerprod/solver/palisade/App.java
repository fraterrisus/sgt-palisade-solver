package org.hitchhikerprod.solver.palisade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class App {
    public static void main(String[] args) {
        try {
            final BufferedReader reader;
            if (args.length == 0) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            } else {
                reader = new BufferedReader(new StringReader(args[0]));
            }
            final Board board = Board.from(reader);
            board.solve();
        } catch (IOException e) {
            System.err.println("Error reading input board: " + e.getMessage());
            System.exit(1);
        }
    }
}
