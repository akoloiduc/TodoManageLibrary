package ddt.chess.ui;

import ddt.chess.core.Game;
import ddt.chess.core.PieceColor;
import ddt.chess.util.TimerClock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimerPanel extends JPanel {
    private final Game game;
    private final JLabel whiteTimerLabel;
    private final JLabel blackTimerLabel;
    private Timer refreshTimer;
    private boolean timerStarted = false;
    private int fontSize;

    public TimerPanel(Game game, int squareSize) {
        this.game = game;
        this.fontSize = (int)(0.25 * squareSize);

        // Use a dark background to match the setting panel
        setBackground(new Color(50, 50, 50));

        // Create a panel with GridLayout for the timers
        setLayout(new GridLayout(2, 1, 0, 5));

        // White timer panel
        JPanel whitePanel = new JPanel(new BorderLayout());
        whitePanel.setBackground(new Color(50, 50, 50));
        whiteTimerLabel = new JLabel("White: --:--", SwingConstants.CENTER);
        whiteTimerLabel.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        whiteTimerLabel.setForeground(Color.WHITE);
        whitePanel.add(whiteTimerLabel, BorderLayout.CENTER);

        // Black timer panel
        JPanel blackPanel = new JPanel(new BorderLayout());
        blackPanel.setBackground(new Color(50, 50, 50));
        blackTimerLabel = new JLabel("Black: --:--", SwingConstants.CENTER);
        blackTimerLabel.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        blackTimerLabel.setForeground(Color.WHITE);
        blackPanel.add(blackTimerLabel, BorderLayout.CENTER);

        // Add timer panels
        add(whitePanel);
        add(blackPanel);

        // Set up the timer refresh
        setupRefreshTimer();

        // Set border
        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Initial update of timer display
        if (game.isTimedGame()) {
            updateTimerDisplay();
        }
    }

    private void setupRefreshTimer() {
        // Create a Swing timer that updates the clock display every 100ms
        refreshTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimerDisplay();
            }
        });
    }

    public void startTimers() {
        System.out.println("startTimers called. Timer already started: " + timerStarted);
        System.out.println("Game is timed game: " + game.isTimedGame());

        if (!timerStarted && game.isTimedGame()) {
            System.out.println("Starting refresh timer...");
            refreshTimer.start();
            timerStarted = true;

            // Log clock states
            System.out.println("White clock status before: " + (game.getWhiteClock() != null ? "exists" : "null"));
            System.out.println("Black clock status before: " + (game.getBlackClock() != null ? "exists" : "null"));

            // Don't start the clocks here - let Game handle the clock state
            // Just update the display
            updateTimerDisplay();

            System.out.println("Timer started successfully.");
        }
    }

    public void updateTimerDisplay() {
        if (game.isTimedGame()) {
            // Update the timer labels
            whiteTimerLabel.setText("White: " + game.getWhiteClock().getTimeLeftString());
            blackTimerLabel.setText("Black: " + game.getBlackClock().getTimeLeftString());

            // Highlight the active timer
            if (game.getCurrentTurn() == PieceColor.WHITE) {
                whiteTimerLabel.setForeground(new Color(255, 255, 100));
                blackTimerLabel.setForeground(Color.WHITE);
            } else {
                blackTimerLabel.setForeground(new Color(255, 255, 100));
                whiteTimerLabel.setForeground(Color.WHITE);
            }

            // Check if game is over due to time
            if (game.isOver() && game.getGameOverCause() != null &&
                    game.getGameOverCause().equals("time")) {
                refreshTimer.stop();
            }
        }
    }

    public void pauseTimers() {
        if (game.isTimedGame()) {
            game.getWhiteClock().pause();
            game.getBlackClock().pause();
        }
    }

    public void resumeTimers() {
        if (game.isTimedGame() && timerStarted) {
            // Only resume the clock for the current turn
            if (game.getCurrentTurn() == PieceColor.WHITE) {
                game.getWhiteClock().resume();
            } else {
                game.getBlackClock().resume();
            }
        }
    }

    public void switchClocks() {
        // Don't handle clock switching here - Game.switchClocks() should handle it
        updateTimerDisplay();
    }

    public void stopTimers() {
        if (refreshTimer != null && refreshTimer.isRunning()) {
            refreshTimer.stop();
        }
        if (game.isTimedGame()) {
            game.getWhiteClock().pause();
            game.getBlackClock().pause();
        }
        timerStarted = false;
    }

    public boolean isTimerStarted() {
        return timerStarted;
    }
}