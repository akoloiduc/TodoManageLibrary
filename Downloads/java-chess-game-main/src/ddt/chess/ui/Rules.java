package ddt.chess.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class Rules extends JFrame implements ActionListener {
    private JButton backButton = new JButton("BACK");
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private int size = (int)screenSize.getHeight()*2 / 3;

    public Rules() {
        this.setTitle("Rules");
        this.setSize(size, size);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(33, 33, 33));

        JTextArea textArea = createTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane);

        this.add(backButton, BorderLayout.SOUTH);
        addHoverEffect(backButton, new Color(10, 10, 10), new Color(60, 60, 60));
        backButton.setFont(new Font("Arial", Font.BOLD, 13));
        backButton.addActionListener(this);

        this.setVisible(true);
    }

    private ArrayList<String> readLinesFromFile(String path) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lines;
    }

    private JTextArea createTextArea() {
        ArrayList<String> lines = readLinesFromFile("java-chess-game-main/resources/Rules.txt");
        StringBuilder content = new StringBuilder();

        for (String line : lines) {
            content.append(line).append("\n");
        }

        JTextArea textArea = new JTextArea(content.toString());
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(new Color(33, 33, 33));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        return textArea;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == backButton) {
            this.dispose();
        }
    }
}
