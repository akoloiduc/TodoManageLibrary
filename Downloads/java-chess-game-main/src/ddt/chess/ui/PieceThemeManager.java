package ddt.chess.ui;

import ddt.chess.util.ThemeLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class PieceThemeManager {
    private final BoardPanel boardPanel;
    private final ThemeLoader themeLoader;
    private final int squareSize;
    private final int fontSize;
    private int currentPieceThemeIndex = 0;

    // Available piece themes
    private final String[] pieceThemes = {"alpha", "anarcandy","caliente", "california", "cburnett", "chess7", "cardinal", "celtic",
            "chesnut", "companion", "cooke", "dubrovny", "fantasy", "fresca", "gioco", "governor", "horsey", "icpieces", "kiwen-suwi",
            "kosal", "leipzig", "letter", "maestro", "merida", "monarchy", "mpchess", "pirouetti","pixel", "reillycraig",
            "rhosgfx", "riohacha", "spatial", "staunty", "tatiana", "xkcd"};

    public PieceThemeManager(BoardPanel boardPanel, ThemeLoader themeLoader, int squareSize) {
        this.boardPanel = boardPanel;
        this.themeLoader = themeLoader;
        this.squareSize = squareSize;
        this.fontSize = (int) (0.2 * squareSize);
    }

    public JButton createPieceThemeButton() {
        JButton button = new JButton("🎨♟");
        button.setFont(new Font("SansSerif", Font.BOLD, (int)(1.5*fontSize)));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension((int)(0.5 * squareSize), (int)(0.5 * squareSize)));
        button.setToolTipText("Change Piece Theme");
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

        // Add action to change piece theme
        button.addActionListener(e -> showPieceThemeDialog());

        return button;
    }

    private void showPieceThemeDialog() {
        // Create a dialog for theme selection
        JDialog themeDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(boardPanel), "Select Piece Theme", true);
        themeDialog.setLayout(new BorderLayout());
        themeDialog.setSize(500, 400);
        themeDialog.setLocationRelativeTo(null);
        themeDialog.setUndecorated(true);

        // Create main container with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(60, 60, 60));

        // Create and add the fixed title panel at NORTH
        JPanel titlePanel = createTitlePanel("Choose Piece Theme", themeDialog);
        mainContainer.add(titlePanel, BorderLayout.NORTH);

        // Create panel to hold theme buttons in a vertical layout
        JPanel themesPanel = new JPanel();
        themesPanel.setLayout(new BoxLayout(themesPanel, BoxLayout.Y_AXIS));
        themesPanel.setBackground(new Color(60, 60, 60));

        // Create buttons for each theme
        for (String theme : pieceThemes) {
            JPanel themeButtonPanel = new JPanel();
            themeButtonPanel.setLayout(new BorderLayout(10, 0));
            themeButtonPanel.setBackground(new Color(70, 70, 70));
            themeButtonPanel.setBorder(BorderFactory.createLineBorder(new Color(90, 90, 90), 1));

            // Create a JLabel for theme name
            JLabel nameLabel = new JLabel(theme);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            // Add preview image
            JPanel previewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            previewPanel.setBackground(new Color(70, 70, 70));

            JLabel previewKingLabel = new JLabel();
            JLabel previewQueenLabel = new JLabel();
            try {
                String previewKingPath = String.format("resources/piece/%s/32x32/wK.png", theme);
                String previewQueenPath = String.format("resources/piece/%s/32x32/bQ.png", theme);
                ImageIcon previewKingIcon = new ImageIcon(previewKingPath);
                ImageIcon previewQueenIcon = new ImageIcon(previewQueenPath);
                Image kingScaled = previewKingIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                Image queenScaled = previewQueenIcon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                previewKingLabel.setIcon(new ImageIcon(kingScaled));
                previewQueenLabel.setIcon(new ImageIcon(queenScaled));
            } catch (Exception ex) {
                System.out.println("Could not load preview for theme: " + theme);
                previewKingLabel.setText("No preview");
                previewKingLabel.setForeground(Color.GRAY);
                previewQueenLabel.setText("No preview");
                previewQueenLabel.setForeground(Color.GRAY);
            }

            previewPanel.add(previewKingLabel);
            previewPanel.add(previewQueenLabel);

            themeButtonPanel.add(previewPanel, BorderLayout.WEST);
            themeButtonPanel.add(nameLabel, BorderLayout.CENTER);

            // Highlight the current theme
            if (theme.equals(pieceThemes[currentPieceThemeIndex])) {
                themeButtonPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));
            }

            // Add mouse listeners for hover effect
            themeButtonPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (!theme.equals(pieceThemes[currentPieceThemeIndex])) {
                        themeButtonPanel.setBackground(new Color(85, 85, 85));
                    }
                }

                public void mouseExited(MouseEvent evt) {
                    if (!theme.equals(pieceThemes[currentPieceThemeIndex])) {
                        themeButtonPanel.setBackground(new Color(70, 70, 70));
                    }
                }

                public void mouseClicked(MouseEvent evt) {
                    try {
                        System.out.println("Changing piece theme to: " + theme);
                        currentPieceThemeIndex = Arrays.asList(pieceThemes).indexOf(theme);

                        // Update both ThemeLoader instances
                        themeLoader.setPieceTheme(theme);
                        boardPanel.getThemeLoader().setPieceTheme(theme); // Update theme in BoardPanel directly

                        // Force repaint
                        boardPanel.refreshBoard();
                        boardPanel.repaint();
                        System.out.println("Piece theme changed successfully to: " + theme);
                        themeDialog.dispose();
                    } catch (Exception ex) {
                        System.err.println("Error changing piece theme: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });

            // Add panel to themes container
            themesPanel.add(themeButtonPanel);
        }

        // Create scroll pane for themes panel
        JScrollPane scrollPane = new JScrollPane(themesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // Add the scrollpane to the CENTER of the main container
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        // Add the main container to the dialog
        themeDialog.add(mainContainer);
        themeDialog.setVisible(true);
    }

    private JPanel createTitlePanel(String title, JDialog dialog) {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 70, 70));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create close button
        JButton closeButton = new JButton("x");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        closeButton.setForeground(Color.WHITE);
        closeButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        closeButton.setBackground(new Color(60, 60, 60));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension((int)(0.25 * squareSize), (int)(0.25 * squareSize)));
        closeButton.setMargin(new Insets(0, 0, 0, 0));

        // Add hover effect
        closeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                closeButton.setBackground(new Color(200, 60, 60)); // Red highlight
            }

            public void mouseExited(MouseEvent evt) {
                closeButton.setBackground(new Color(60, 60, 60));
            }
        });

        // Add close action
        closeButton.addActionListener(e -> dialog.dispose());

        titlePanel.add(closeButton, BorderLayout.EAST);

        return titlePanel;
    }
}