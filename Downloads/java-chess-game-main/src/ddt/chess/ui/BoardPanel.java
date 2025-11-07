package ddt.chess.ui;

import ddt.chess.core.*;
import ddt.chess.util.SoundPlayer;
import ddt.chess.util.ThemeLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ddt.chess.core.MoveValidator.isValidCastling;
import static ddt.chess.core.MoveValidator.isValidPromotion;

public class BoardPanel extends JPanel {
    private final Game game;
    private final Board board;
    private final HistoryPanel historyPanel;
    private SettingPanel settingPanel;
    private Square selectedSquare;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int squareSize;
    private int boardSize;

    private String boardTheme = "brown";
    private String pieceTheme = "staunty";
    private final ThemeLoader theme;
    private SoundPlayer soundPlayer;

    private Square lastMoveFrom;
    private Square lastMoveTo;
    private List<Square> validMoves = new ArrayList<>();
    private Map<Square, Color> highlightedSquares = new HashMap<>();

    public BoardPanel(Game game, HistoryPanel historyPanel, int squareSize) {
        theme = new ThemeLoader(boardTheme, pieceTheme, squareSize);
        this.game = game;
        this.historyPanel = historyPanel;
        this.squareSize = squareSize;
        boardSize = 8 * squareSize;
        board = game.getBoard();
        board.setupPieces();
        this.setPreferredSize(new Dimension(boardSize, boardSize));

        // Initialize the sound player
        soundPlayer = new SoundPlayer();

        // Mouse adapter for handling mouse events
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Convert screen coordinates to board coordinates
                int x = e.getX() / squareSize;
                int y = e.getY() / squareSize;
                handleSquareClick(x, y);
            }
        };

        this.addMouseListener(mouseAdapter);
    }

    private void handleSquareClick(int x, int y) {
        Board board = game.getBoard();
        Square clickedSquare = board.getSquare(y, x);

        if (selectedSquare == null) {
            if (!clickedSquare.isEmpty() &&
                    clickedSquare.getPiece().getColor() == game.getCurrentTurn()) {
                selectedSquare = clickedSquare;
                calculateValidMoves();
                clearHighlights(); // clear all previous highlights
                highlightSquare(selectedSquare, new Color(255, 255, 100, 100)); // highlight the selected square
                repaint();
            }
        } else {
            if (selectedSquare == clickedSquare) {
                selectedSquare = null;
                validMoves.clear();
                clearHighlights(); // clear highlights when deselecting
                repaint();
                return;
            }

            boolean isValidMove = false;
            for (Square validMove : validMoves) {
                if (validMove.equals(clickedSquare)) {
                    isValidMove = true;
                    break;
                }
            }

            // Save move information before executing
            lastMoveFrom = selectedSquare;
            lastMoveTo = clickedSquare;

            // Try to make the move
            Move move = new Move(selectedSquare, clickedSquare);
            boolean moveSuccess = game.makeMove(move);
            if (moveSuccess) {
                // Notify the SettingPanel that a move has been made (for timer)
                if (settingPanel != null) {
                    settingPanel.onMoveMade(move);
                }

                playAppropriateSound(move);

                // highlight the squares involved in the move
                clearHighlights();
                highlightSquare(lastMoveFrom, new Color(255, 255, 0, 64));
                highlightSquare(lastMoveTo, new Color(255, 255, 0, 64));

                if (historyPanel != null) {
                    historyPanel.updateHistory();
                }

                // execute stockfish move if player has made a valid move
                if (game instanceof ComputerGame computerGame
                        && computerGame.getPlayerSide() != game.getCurrentTurn()) {
                    SwingUtilities.invokeLater(() -> {
                        Move computerMove = computerGame.executeComputerMove();

                        // Notify the SettingPanel about the computer's move (for timer)
                        if (settingPanel != null) {
                            settingPanel.onMoveMade(computerMove);
                        }

                        clearHighlights();
                        highlightSquare(computerMove.getFromSquare(), new Color(255, 255, 0, 64));
                        highlightSquare(computerMove.getToSquare(), new Color(255, 255, 0, 64));
                        historyPanel.updateHistory();
                        repaint();
                    });
                }

                if (game.isOver()) {
                    try {
                        soundPlayer.playGameOverSound();
                    } catch (Exception e) {
                        System.err.println("Error playing game over sound: " + e.getMessage());
                    }
                    // Delay the game over message until after repaint
                    SwingUtilities.invokeLater(this::displayGameOverMessage);
                }
            } else if (!isValidMove) {
                // If clicking on another piece of the same color, select that piece
                if (!clickedSquare.isEmpty() &&
                        clickedSquare.getPiece().getColor() == game.getCurrentTurn()) {
                    selectedSquare = clickedSquare;
                    calculateValidMoves();
                    clearHighlights(); // Clear previous highlights
                    highlightSquare(selectedSquare, new Color(255, 255, 100, 100)); // Highlight the newly selected square
                    repaint();
                    return;
                }
            }

            selectedSquare = null;
            validMoves.clear(); // Clear valid moves
            repaint();
        }
    }

    private void calculateValidMoves() {
        validMoves.clear();
        if (selectedSquare == null || selectedSquare.isEmpty()) {
            return;
        }

        Board board = game.getBoard();
        PieceColor color = selectedSquare.getPiece().getColor();

        // Get all valid moves for this color
        ArrayList<Move> allValidMoves = board.generateAllValidMoves(color, game.getHistory());

        // Filter moves starting from the selected square
        for (Move move : allValidMoves) {
            if (move.getFromSquare().equals(selectedSquare)) {
                // Add destination square to validMoves list
                Square destination = move.getToSquare();
                validMoves.add(destination);

                // we don't need to apply highlights here anymore since we'll draw circles in drawMoveHints

                // debug: display information about en passant moves
                if (MoveValidator.isValidEnPassant(game.getBoard(), move, game.getHistory())) {
                    System.out.println("found en passant move from " +
                            move.getFromSquare().getX() + "," + move.getFromSquare().getY() +
                            " to " + move.getToSquare().getX() + "," + move.getToSquare().getY());
                    // we'll handle special highlighting in drawMoveHints
                }
            }
        }
    }

    private void playAppropriateSound(Move move) {
        try {
            // Check current turn after the move has been made
            PieceColor currentTurn = game.getCurrentTurn();
            Board board = game.getBoard();

            // check for check more clearly
            if (board.isCheck(currentTurn)) {
                System.out.println("check detected! playing check sound for " + currentTurn);
                soundPlayer.playCheckSound();
                return; // exit to avoid playing other sounds
            }

            // Other cases
            if (isValidPromotion(move)) {
                soundPlayer.playPromotionSound();
            } else if (isValidCastling(game.getBoard(), move)) {
                soundPlayer.playCastlingSound();
            } else if (move.getCapturedPiece() != null) {
                soundPlayer.playCaptureSound();
            } else {
                soundPlayer.playMoveSound();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    // display game over message
    private void displayGameOverMessage() {
        String message;
        if (game.isCheckMate()) {
            PieceColor winner = (game.getCurrentTurn() == PieceColor.WHITE) ?
                    PieceColor.BLACK : PieceColor.WHITE;
            message = winner + " wins!";
        } else {
            message = "Draw!";
        }

        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // draw move hints (valid moves)
    private void drawMoveHints(Graphics2D g2D) {
        if (selectedSquare != null) {
            for (Square square : validMoves) {
                int x = square.getY() * squareSize;
                int y = square.getX() * squareSize;

                if (square.isOccupied()) {
                    // For capture moves, draw a circle around the piece
                    g2D.setColor(new Color(0, 0, 0, 85)); // Red color for capture hint
                    g2D.setStroke(new BasicStroke(5)); // Thicker stroke for visibility
                    int padding = 2; // Padding around the piece
                    g2D.drawOval(x + padding, y + padding, squareSize - (2 * padding), squareSize - (2 * padding));
                } else {
                    // For normal moves, draw a filled circle
                    g2D.setColor(new Color(0, 0, 0, 80));
                    g2D.fillOval(x + squareSize/3, y + squareSize/3, squareSize/3, squareSize/3);
                }

                // Special highlighting for en passant
                Move testMove = new Move(selectedSquare, square);
                if (MoveValidator.isValidEnPassant(game.getBoard(), testMove, game.getHistory())) {
                    g2D.setColor(new Color(255, 0, 255, 180)); // Magenta color for en passant
                    g2D.setStroke(new BasicStroke(3));
                    g2D.drawOval(x + 2, y + 2, squareSize - 4, squareSize - 4);
                }

                // Special highlighting for castling
                if (isValidCastling(game.getBoard(), new Move(selectedSquare, square))) {
                    g2D.setColor(new Color(0, 0, 255, 180)); // Blue color for castling
                    g2D.setStroke(new BasicStroke(3));
                    g2D.drawOval(x + 2, y + 2, squareSize - 4, squareSize - 4);
                }
            }
        }
    }

    // Draw highlighted squares (for check, selected piece, etc.)
    private void drawSquareHighlights(Graphics2D g2D) {
        // Draw other highlighted squares (check, etc.)
        for (Map.Entry<Square, Color> entry : highlightedSquares.entrySet()) {
            Square square = entry.getKey();
            Color color = entry.getValue();

            int x = square.getY() * squareSize;
            int y = square.getX() * squareSize;
            g2D.setColor(color);
            g2D.fillRect(x, y, squareSize, squareSize);
        }
    }

    /**
     * Highlights a square with the specified color
     * @param square The square to highlight
     * @param color The color to use for highlighting
     */
    public void highlightSquare(Square square, Color color) {
        if (square != null && color != null) {
            highlightedSquares.put(square, color);
        }
    }

    /**
     * Clears all highlighted squares
     */
    public void clearHighlights() {
        highlightedSquares.clear();
    }

    public void drawBoard(Graphics2D g2D) {
        g2D.drawImage(theme.getBoardImage(), 0, 0, null);
    }

    public void drawPieces(Graphics2D g2D) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Square square = board.getSquare(i, j);
                if (square != null && square.isOccupied()) {
                    g2D.drawImage(theme.getImageOfPiece(square.getPiece()), j * squareSize, i * squareSize, null);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Always call super.paintComponent first
        Graphics2D g2D = (Graphics2D)g;

        // Draw board image
        drawBoard(g2D);

        // Kiểm tra và highlight vua đang bị chiếu
        if (board.isCheck(game.getCurrentTurn())) {
            Square kingSquare = board.findKingSquare(game.getCurrentTurn());
            // Dùng màu đỏ đặc biệt cho vua đang bị chiếu
            highlightSquare(kingSquare, new Color(255, 0, 0, 80));
        }

        drawSquareHighlights(g2D);

        // Draw piece images
        drawPieces(g2D);

        // Draw move hints
        drawMoveHints(g2D);
    }

    public void setSquareSize(int squareSize) {
        this.squareSize = squareSize;
        theme.setSquareSize(squareSize);
        repaint();
    }

    public ThemeLoader getThemeLoader() {
        return theme;
    }

    public int getSquareSize() {
        return squareSize;
    }
    public void refreshBoard() {
        // Clear existing highlights
        clearHighlights();

        // Reset the selection state
        selectedSquare = null;
        validMoves.clear();

        // Clear tracking of last move
        lastMoveFrom = null;
        lastMoveTo = null;

        // Repaint to show the updated board state
        repaint();
    }
    public void setSettingPanel(SettingPanel settingPanel) {
        this.settingPanel = settingPanel;
    }
}