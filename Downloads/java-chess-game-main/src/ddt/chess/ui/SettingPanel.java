package ddt.chess.ui;

import ddt.chess.core.Game;
import ddt.chess.core.Move;
import ddt.chess.core.PieceColor;
import ddt.chess.util.ThemeLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingPanel extends JPanel {
    private final Game game;
    private final BoardPanel boardPanel;
    private final JButton undoButton;
    private final JButton newGameButton;
    private final ThemeLoader themeLoader;
    private HistoryPanel historyPanel;
    private TimerPanel timerPanel;
    private int squareSize;
    private int settingPanelWidth;
    private int settingPanelHeight;
    private int settingButtonWidth;
    private int settingButtonHeight;
    private int fontSize;

    // Theme managers
    private final BoardThemeManager boardThemeManager;
    private final PieceThemeManager pieceThemeManager;

    public SettingPanel(Game game, BoardPanel boardPanel, ThemeLoader themeLoader, int squareSize) {
        this.game = game;
        this.boardPanel = boardPanel;
        this.themeLoader = themeLoader;
        this.squareSize = squareSize;

        // Initialize dimensions
        settingPanelWidth = (int)(8 * squareSize);
        settingPanelHeight = (int)(0.67 * squareSize);
        settingButtonWidth = (int)(0.5 * squareSize);
        settingButtonHeight = (int)(0.5 * squareSize);
        fontSize = (int) (0.2 * squareSize);

        // Initialize theme managers
        this.boardThemeManager = new BoardThemeManager(boardPanel, themeLoader, squareSize);
        this.pieceThemeManager = new PieceThemeManager(boardPanel, themeLoader, squareSize);

        // Background color
        Color darkGray = new Color(50, 50, 50);
        setBackground(darkGray);

        // Sử dụng BorderLayout cho panel chính
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(settingPanelWidth, settingPanelHeight));

        // Create buttons
        undoButton = createUndoButton();
        newGameButton = createNewGameButton();

        // Get theme buttons from managers
        JButton boardThemeButton = boardThemeManager.createBoardThemeButton();
        JButton pieceThemeButton = pieceThemeManager.createPieceThemeButton();

        // Create left panel with vertical centering
        JPanel westPanel = createCenteredPanel(darkGray);
        JPanel westButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        westButtonPanel.setBackground(darkGray);
        westButtonPanel.add(undoButton);
        westButtonPanel.add(newGameButton);
        westPanel.add(westButtonPanel);

        // Create center panel with timer
        JPanel centerPanel = createCenteredPanel(darkGray);
        timerPanel = new TimerPanel(game, squareSize);
        centerPanel.add(timerPanel);

        // Create right panel with vertical centering
        JPanel eastPanel = createCenteredPanel(darkGray);
        JPanel eastButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        eastButtonPanel.setBackground(darkGray);
        eastButtonPanel.add(boardThemeButton);
        eastButtonPanel.add(pieceThemeButton);
        eastPanel.add(eastButtonPanel);

        // Add panels to main layout
        add(westPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(eastPanel, BorderLayout.EAST);

        // Add move observer to start timer on first move
        if (game.isTimedGame()) {
            // Start two threads for the clocks
            Thread whiteClockThread = new Thread(game.getWhiteClock());
            Thread blackClockThread = new Thread(game.getBlackClock());

            // Set threads as daemon so they don't prevent application exit
            whiteClockThread.setDaemon(true);
            blackClockThread.setDaemon(true);

            // Pause the clocks initially
            game.getWhiteClock().pause();
            game.getBlackClock().pause();

            // Start the threads
            whiteClockThread.start();
            blackClockThread.start();

            // We'll start the timer when the first move is made
            // This is now handled through BoardPanel observing
        }
    }

    /**
     * Creates a panel with BoxLayout for vertical centering of components
     */
    private JPanel createCenteredPanel(Color backgroundColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);

        // Add vertical glue before the component for centering
        panel.add(Box.createVerticalGlue());

        // The actual component will be added here in the calling method

        // Add vertical glue after the component for centering
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JButton createUndoButton() {
        // Create undo button with undo symbol
        JButton button = new JButton("↩");
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize)); // Larger font
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension((int)(settingButtonWidth), settingButtonHeight)); // Same size as settings button
        button.setToolTipText("Undo Last Move");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setMargin(new Insets(10, 0, 0, 0));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });

        // Add action to undo last move when button is clicked
        button.addActionListener(e -> {
            System.out.println("Undoing last move...");

            // Pause timers before undoing
            if (timerPanel != null && timerPanel.isTimerStarted()) {
                timerPanel.pauseTimers();
            }

            game.undoLastMove();
            System.out.println("Refreshing board...");
            boardPanel.refreshBoard();
            System.out.println("Updating history panel...");
            updateHistoryPanel();

            // Resume timers with the correct one active
            if (timerPanel != null && timerPanel.isTimerStarted()) {
                timerPanel.switchClocks();
            }

            System.out.println("Undo operation complete.");
        });

        return button;
    }

    private JButton createNewGameButton() {
        // Create new game button
        JButton button = new JButton("🔄");
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension((int)(settingButtonWidth), settingButtonHeight));
        button.setToolTipText("New Game");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setMargin(new Insets(0, 0, 0, 0));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(90, 90, 90));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(70, 70, 70));
            }
        });

        // Add action for new game with confirmation dialog
        button.addActionListener(e -> showNewGameConfirmation());

        return button;
    }

    private void showNewGameConfirmation() {
        // Create confirmation dialog
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Game", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.setSize(4 * squareSize, 2 * squareSize);
        confirmDialog.setLocationRelativeTo(null);
        confirmDialog.setUndecorated(true);

        // Create main container with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(60, 60, 60));
        mainContainer.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 70, 70));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Start a New Game?");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, (int)(fontSize * 1.2)));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create message panel
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(new Color(60, 60, 60));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel messageLabel = new JLabel("Current game progress will be lost.");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        messageLabel.setForeground(Color.WHITE);
        messagePanel.add(messageLabel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(60, 60, 60));

        // Create Yes button
        JButton yesButton = new JButton("Yes");
        yesButton.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        yesButton.setForeground(Color.WHITE);
        yesButton.setBackground(new Color(70, 120, 70));
        yesButton.setBorder(BorderFactory.createLineBorder(new Color(90, 140, 90), 2));
        yesButton.setFocusPainted(false);
        yesButton.setPreferredSize(new Dimension(squareSize, (int)(0.4 * squareSize)));

        // Add hover effect
        yesButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                yesButton.setBackground(new Color(90, 140, 90));
            }

            public void mouseExited(MouseEvent evt) {
                yesButton.setBackground(new Color(70, 120, 70));
            }
        });

        // Add action to start new game
        yesButton.addActionListener(e -> {
            System.out.println("Starting new game...");

            // Stop the current timers
            if (timerPanel != null) {
                timerPanel.stopTimers();
            }

            game.resetBoard();
            boardPanel.refreshBoard();
            if (historyPanel != null) {
                historyPanel.updateHistory();
            }

            // Reset the timers if it's a timed game
            if (game.isTimedGame()) {
                // We'll need to reinitialize the timer threads
                if (timerPanel != null) {
                    // Create new threads for the game clocks
                    Thread whiteClockThread = new Thread(game.getWhiteClock());
                    Thread blackClockThread = new Thread(game.getBlackClock());

                    whiteClockThread.setDaemon(true);
                    blackClockThread.setDaemon(true);

                    game.getWhiteClock().pause();
                    game.getBlackClock().pause();

                    whiteClockThread.start();
                    blackClockThread.start();
                }
            }

            System.out.println("New game started.");
            confirmDialog.dispose();
        });

        // Create No button
        JButton noButton = new JButton("No");
        noButton.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        noButton.setForeground(Color.WHITE);
        noButton.setBackground(new Color(120, 70, 70));
        noButton.setBorder(BorderFactory.createLineBorder(new Color(140, 90, 90), 2));
        noButton.setFocusPainted(false);
        noButton.setPreferredSize(new Dimension(squareSize, (int)(0.4 * squareSize)));

        // Add hover effect
        noButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                noButton.setBackground(new Color(140, 90, 90));
            }

            public void mouseExited(MouseEvent evt) {
                noButton.setBackground(new Color(120, 70, 70));
            }
        });

        // Add action to cancel
        noButton.addActionListener(e -> confirmDialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        // Add panels to main container
        mainContainer.add(titlePanel, BorderLayout.NORTH);
        mainContainer.add(messagePanel, BorderLayout.CENTER);
        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Add main container to dialog
        confirmDialog.add(mainContainer);
        confirmDialog.setVisible(true);
    }

    // Method to update history panel
    private void updateHistoryPanel() {
        if (historyPanel != null) {
            historyPanel.updateHistory();
        }
    }

    // Setter for history panel reference
    public void setHistoryPanel(HistoryPanel historyPanel) {
        this.historyPanel = historyPanel;
    }

    // Method to be called when a move is made
    public void onMoveMade(Move move) {
        System.out.println("onMoveMade called! Current turn: " + game.getCurrentTurn());

        // Start the timer if it hasn't started yet
        if (timerPanel != null && !timerPanel.isTimerStarted() && game.isTimedGame()) {
            System.out.println("Starting timers...");
            timerPanel.startTimers();
        } else if (timerPanel != null && timerPanel.isTimerStarted() && game.isTimedGame()) {
            // Always ensure clocks are switched when a move is made
            timerPanel.switchClocks();
        }
    }
    // Methods to pause/resume timers when changing themes
    public void pauseTimers() {
        if (timerPanel != null) {
            timerPanel.pauseTimers();
        }
    }

    public void resumeTimers() {
        if (timerPanel != null) {
            timerPanel.resumeTimers();
        }
    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }
}
