package ddt.chess.ui;

import ddt.chess.util.ThemeLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class BoardThemeManager {
    private final BoardPanel boardPanel;
    private final ThemeLoader themeLoader;
    private final int squareSize;
    private final int fontSize;
    private int currentBoardThemeIndex = 0;

    // Available board themes
    private final String[] boardThemes = {"blue", "blue2", "blue3", "blue-marble", "brown", "canvas2", "green",
            "green-plastic","grey", "horsey", "ic", "leather", "maple", "maple2", "marble", "metal",
            "olive", "pink-pyramid", "purple", "purple-diag", "wood", "wood2", "wood3", "wood4"};

    public BoardThemeManager(BoardPanel boardPanel, ThemeLoader themeLoader, int squareSize) {
        this.boardPanel = boardPanel;
        this.themeLoader = themeLoader;
        this.squareSize = squareSize;
        this.fontSize = (int) (0.2 * squareSize);
    }

    public JButton createBoardThemeButton() {
        JButton button = new JButton("🏁Theme");
        button.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 70));
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension((int)(1.5 * squareSize * 0.5), (int)(0.5 * squareSize)));
        button.setToolTipText("Change Board Theme");
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


        // Add action to change board theme
        button.addActionListener(e -> showBoardThemeDialog());

        return button;
    }

    private void showBoardThemeDialog() {
        // Create a dialog for theme selection
        JDialog themeDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(boardPanel), "Select Board Theme", true);
        themeDialog.setLayout(new BorderLayout());
        themeDialog.setSize(4 * squareSize, 4 * squareSize);
        themeDialog.setLocationRelativeTo(null);
        themeDialog.setUndecorated(true);

        // Create main container with BorderLayout
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(60, 60, 60));

        // Create and add the fixed title panel at NORTH
        JPanel titlePanel = createTitlePanel("Choose Board Theme", themeDialog);
        mainContainer.add(titlePanel, BorderLayout.NORTH);

        // Create panel to hold theme buttons in a vertical layout
        JPanel themesPanel = new JPanel();
        themesPanel.setLayout(new BoxLayout(themesPanel, BoxLayout.Y_AXIS));
        themesPanel.setBackground(new Color(60, 60, 60));

        // Create buttons for each theme
        for (String theme : boardThemes) {
            JPanel themeButtonPanel = new JPanel();
            themeButtonPanel.setLayout(new BorderLayout(10, 0));
            themeButtonPanel.setBackground(new Color(70, 70, 70));
            themeButtonPanel.setBorder(BorderFactory.createLineBorder(null));

            // Create a JLabel for theme name
            JLabel nameLabel = new JLabel(theme);
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
            nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            // Add preview image
            JLabel previewLabel = new JLabel();
            try {
                String previewPath = String.format("resources/board/%s/%s.thumbnail.png", theme, theme);
                ImageIcon previewIcon = new ImageIcon(previewPath);
                Image scaled = previewIcon.getImage().getScaledInstance((int)(squareSize*2), squareSize, Image.SCALE_SMOOTH);
                previewLabel.setIcon(new ImageIcon(scaled));
                previewLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            } catch (Exception ex) {
                System.out.println("Could not load preview for theme: " + theme);
                previewLabel.setText("No preview");
                previewLabel.setForeground(Color.GRAY);
            }

            themeButtonPanel.add(previewLabel, BorderLayout.WEST);
            themeButtonPanel.add(nameLabel, BorderLayout.CENTER);

            // Highlight the current theme
            if (theme.equals(boardThemes[currentBoardThemeIndex])) {
                themeButtonPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray, 2));
            }

            // Add mouse listeners for hover effect and selection
            themeButtonPanel.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (!theme.equals(boardThemes[currentBoardThemeIndex])) {
                        themeButtonPanel.setBackground(new Color(85, 85, 85));
                    }
                }

                public void mouseExited(MouseEvent evt) {
                    if (!theme.equals(boardThemes[currentBoardThemeIndex])) {
                        themeButtonPanel.setBackground(new Color(70, 70, 70));
                    }
                }

                public void mouseClicked(MouseEvent evt) {
                    try {
                        System.out.println("Changing board theme to: " + theme);
                        currentBoardThemeIndex = Arrays.asList(boardThemes).indexOf(theme);
                        themeLoader.setBoardTheme(theme);
                        boardPanel.getThemeLoader().setBoardTheme(theme); // Update theme in BoardPanel directly
                        boardPanel.refreshBoard();
                        boardPanel.repaint(); // Force repaint
                        System.out.println("Board theme changed successfully to: " + theme);
                        themeDialog.dispose();
                    } catch (Exception ex) {
                        System.err.println("Error changing board theme: " + ex.getMessage());
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
        scrollPane.setVerticalScrollBarPolicy((JScrollPane.VERTICAL_SCROLLBAR_NEVER));

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