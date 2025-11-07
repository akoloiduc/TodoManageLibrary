import ddt.chess.ui.Menu;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Start the application with the menu
        javax.swing.SwingUtilities.invokeLater(Menu::new);
    }
}