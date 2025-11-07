package ddt.chess.ui;

import ddt.chess.core.ComputerGame;
import ddt.chess.core.Game;
import ddt.chess.core.PieceColor;
import ddt.chess.core.PieceType;
import ddt.chess.util.ThemeLoader;
import javax.swing.*;
import java.awt.*;

public class ChessGameGUI extends JFrame {
    private final Game game;
    private final BoardPanel boardPanel;
    private final HistoryPanel historyPanel;
    private final SettingPanel settingPanel;
    private final ThemeLoader themeLoader;

    public ChessGameGUI(boolean isComputerGame) {
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Chess Game");

        // Calculate appropriate square size based on screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int squareSize = findClosest(new int[]{32, 64, 96, 128, 256, 512, 1024}, screenHeight / 12);

        // Initialize themeLoader for board and piece images
        themeLoader = new ThemeLoader("brown", "staunty", squareSize);

        // Initialize game based on selected mode (computer or human opponent)
        if (isComputerGame) {
            game = new ComputerGame(PieceColor.WHITE,"00:10:00", 2000) {
                @Override
                public PieceType askForPromotion() {
                    PromotionPrompt prompt = new PromotionPrompt(boardPanel.getThemeLoader(), getCurrentTurn(), squareSize);
                    prompt.setSize(new Dimension(squareSize * 4, squareSize + squareSize / 2));
                    prompt.setModal(true); // <- block until the user picks
                    prompt.pack(); // adjust size
                    prompt.setLocationRelativeTo(ChessGameGUI.this); // center over the main window
                    prompt.setVisible(true); // show dialog
                    return prompt.getResult();
                }
            };
        } else {
            game = new Game("00:10:00", "00:10:00") {
                @Override
                public PieceType askForPromotion() {
                    PromotionPrompt prompt = new PromotionPrompt(boardPanel.getThemeLoader(), getCurrentTurn(), squareSize);
                    prompt.setSize(new Dimension(squareSize * 4, squareSize + squareSize / 2));
                    prompt.setModal(true); // <- block until the user picks
                    prompt.pack(); // adjust size
                    prompt.setLocationRelativeTo(ChessGameGUI.this); // center over the main window
                    prompt.setVisible(true); // show dialog
                    return prompt.getResult();
                }
            };
        }

        // Initialize UI components
        historyPanel = new HistoryPanel(game, squareSize);
        boardPanel = new BoardPanel(game, historyPanel, squareSize);
        settingPanel = new SettingPanel(game, boardPanel, themeLoader, squareSize);

        // Set references between components
        boardPanel.setSettingPanel(settingPanel);
        settingPanel.setHistoryPanel(historyPanel);


        // Create main content panel with BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout());

        // Create a panel for the board with history
        JPanel boardWithHistoryPanel = new JPanel(new BorderLayout());
        boardWithHistoryPanel.add(historyPanel, BorderLayout.NORTH);
        boardWithHistoryPanel.add(boardPanel, BorderLayout.CENTER);

        // Add components to the main content panel
        contentPanel.add(boardWithHistoryPanel, BorderLayout.CENTER);
        contentPanel.add(settingPanel, BorderLayout.SOUTH);

        // Add content panel to frame
        this.add(contentPanel);

        // Pack and display the frame
        this.pack();
        this.setLocationRelativeTo(null); // Center on screen
        this.setVisible(true);
    }

    /**
     * Find the closest value in an array to a target value
     * @param arr Array of values to search
     * @param target Target value
     * @return Closest value in the array to the target
     */
    public static int findClosest(int[] arr, int target) {
        int closest = arr[0];
        for (int n : arr) {
            if (Math.abs(n - target) < Math.abs(closest - target)) {
                closest = n;
            }
        }
        return closest;
    }

    /**
     * Method to change the square size of the board
     * @param newSize The new square size
     */
    public void setSquareSize(int newSize) {
        boardPanel.setSquareSize(newSize);
        // Resize other components as needed
        this.pack();
    }

    /**
     * Method to change the piece theme
     * @param newTheme The new piece theme name
     */
    public void setPieceTheme(String newTheme) {
        boardPanel.getThemeLoader().setPieceTheme(newTheme);
        boardPanel.repaint();
    }

    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Use the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show a dialog to choose game mode
        int option = JOptionPane.showOptionDialog(
                null,
                "Choose game mode:",
                "Chess Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Human vs Human", "Human vs Computer"},
                "Human vs Human"
        );

        // Start the application with the selected game mode
        SwingUtilities.invokeLater(() -> {
            boolean isComputerGame = (option == 1);
            new ChessGameGUI(isComputerGame);
        });
    }
}