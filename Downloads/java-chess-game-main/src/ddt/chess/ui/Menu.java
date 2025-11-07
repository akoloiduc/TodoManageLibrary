package ddt.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ddt.chess.core.Game;
import ddt.chess.util.ThemeLoader;
import ddt.chess.core.*;
import ddt.chess.util.Notation;

import static ddt.chess.ui.ChessGameGUI.findClosest;

public class Menu extends JFrame implements ActionListener {
    private Game game;
    private Board board;
    private Notation notation;
    private ThemeLoader themeLoader;
    private BoardPanel boardPanel;
    private HistoryPanel historyPanel;
    private ChessGameGUI gameUI;
    private JButton newGameButton;
    private JButton historyButton;
    private JButton themeButton;
    private JButton pieceButton;
    private JButton rulesButton;
    private JButton exitButton;
    private MoveHistory moveHistory;
    private SaveHistory history;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final int size = screenSize.height * 2 / 3;
    private int squareSize = size;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(Menu::new);
    }

    public Menu() {
        moveHistory = new MoveHistory();
        game = new Game();
        board = new Board();
        notation = new Notation();
        int screenHeight = screenSize.height;
        squareSize = findClosest(new int[]{32, 64, 96, 128, 256, 512, 1024}, screenHeight / 12);
        themeLoader = new ThemeLoader("brown", "staunty", squareSize);

        // Tạo historyPanel trước khi truyền vào boardPanel
        historyPanel = new HistoryPanel(game, squareSize);
        boardPanel = new BoardPanel(game, historyPanel, squareSize);
        history = new SaveHistory(game, board, notation, moveHistory);

        setTitle("Chess");
        setSize(size, size);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (isConfirmExit()) {
                    dispose();
                    System.exit(0);
                }
            }
        });
        setResizable(false);
        getContentPane().setBackground(new Color(33, 33, 33));
        setLayout(null);

        initializeComponents();

        setVisible(true);
    }

    private boolean isConfirmExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Do you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );
        return confirm == JOptionPane.YES_OPTION;
    }

    private void initializeComponents() {
        newGameButton = createButton("New Game");
        historyButton = createButton("History");
        pieceButton = createButton("Change Piece Type");
        themeButton = createButton("Change Theme");
        rulesButton = createButton("Rules");
        exitButton = createButton("Exit");

        int buttonWidth = size / 2;
        int buttonHeight = 50;
        int startY = size / 7;
        int gap = 20;

        newGameButton.setBounds((size - buttonWidth) / 2, startY, buttonWidth, buttonHeight);
        historyButton.setBounds((size - buttonWidth) / 2, startY + buttonHeight + gap, buttonWidth, buttonHeight);
        pieceButton.setBounds((size - buttonWidth) / 2, startY + 2 * (buttonHeight + gap), buttonWidth, buttonHeight);
        themeButton.setBounds((size - buttonWidth) / 2, startY + 3 * (buttonHeight + gap), buttonWidth, buttonHeight);
        rulesButton.setBounds((size - buttonWidth) / 2, startY + 4 * (buttonHeight + gap), buttonWidth, buttonHeight);
        exitButton.setBounds((size - buttonWidth) / 2, startY + 5 * (buttonHeight + gap), buttonWidth, buttonHeight);

        add(newGameButton);
        add(historyButton);
        add(pieceButton);
        add(themeButton);
        add(rulesButton);
        add(exitButton);

        newGameButton.addActionListener(this);
        historyButton.addActionListener(this);
        pieceButton.addActionListener(this);
        themeButton.addActionListener(this);
        rulesButton.addActionListener(this);
        exitButton.addActionListener(this);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        addHoverEffect(button, new Color(33, 33, 33), new Color(60, 60, 60));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.setBackground(normalColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }

    private void showErrorDialog(String message, Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                message + ": " + ex.getMessage() + "\n\nCheck if image files are in the correct directory.",
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
        SwingUtilities.invokeLater(Menu::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == newGameButton) {
            history.createFilesHistory(board);
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

            // Start only one instance of the game with the selected mode
            if (option != JOptionPane.CLOSED_OPTION) {
                this.dispose(); // Close the menu first

                // Then start the game
                SwingUtilities.invokeLater(() -> {
                    try {
                        boolean isComputerGame = (option == 1);
                        gameUI = new ChessGameGUI(isComputerGame);
                    } catch (Exception ex) {
                        showErrorDialog("Error starting game", ex);
                    }
                });
            }
        } else if (source == historyButton) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new HistoryPanel(game, squareSize));
        } else if (source == pieceButton) {
            new PieceThemeManager(boardPanel, themeLoader, squareSize);
        } else if (source == themeButton) {
            new BoardThemeManager(boardPanel, themeLoader, squareSize);
        } else if (source == rulesButton) {
            new Rules();
        } else if (source == exitButton) {
            if (isConfirmExit()) {
                System.exit(0);
            }
        }
    }
}