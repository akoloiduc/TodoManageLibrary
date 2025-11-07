package ddt.chess.core;

import ddt.chess.util.Notation;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SaveHistory {
    private Square square;
    private final Game game;
    private final Board board;
    private final Notation notation;
    private final MoveHistory moveHistory;
    private int numGame;

    public SaveHistory(Game game, Board board, Notation notation, MoveHistory moveHistory) {
        this.game = game;
        this.board = board;
        this.notation = notation;
        this.moveHistory = moveHistory;
    }

    public void createFilesHistory(Board startingBoard) {
        String folderPath = "java-chess-game-main/resources/history/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        numGame = getNumGames() + 1;
        File file = new File(folderPath + "game" + numGame + ".txt");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Save notation type
            writer.println(notation.getClass().getName());

            // Create a fresh board for replay
            Board boardCopy = new Board();
            boardCopy.setupPieces();

            // Save initial board position
            writer.println(Notation.gameToFEN(game));

            // Get the moves from the history
            ArrayList<Move> moves = moveHistory.getHistory();

            for (Move move : moves) {
                String moveNotation = Notation.moveToAlgebraicNotation(boardCopy, move);
                boardCopy.makeMove(move);
                writer.println(moveNotation + " " + notation.gameToFEN(game)+ "\n"+ notation.squareToNotation(square));
            }

        } catch (IOException e) {
            System.err.println("Error saving game history: " + e.getMessage());
        }
    }

    public int getNumGames() {
        // Count files in the history directory
        String folderPath = "java-chess-game-main/resources/history/";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            return 0;
        }

        File[] files = folder.listFiles((dir, name) -> name.startsWith("game") && name.endsWith(".txt"));
        return files != null ? files.length : 0;
    }

    public List<String> loadGameHistory(int gameNumber) {
        List<String> moves = new ArrayList<>();
        String folderPath = "java-chess-game-main/resources/history/";
        File file = new File(folderPath + "game" + gameNumber + ".txt");

        if (!file.exists()) {
            return moves;
        }

        try (java.util.Scanner scanner = new java.util.Scanner(file)) {
            // Skip the notation class line
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            // Read the moves and board positions
            while (scanner.hasNextLine()) {
                moves.add(scanner.nextLine());
            }
        } catch (IOException e) {
            System.err.println("Error loading game history: " + e.getMessage());
        }

        return moves;
    }
}