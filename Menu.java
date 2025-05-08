package com.mycompany.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Menu extends JPanel {
    public Menu(JFrame frame) {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Battaglia Navale", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        JButton normal = new JButton("Modalità Normale");
        JButton hard = new JButton("Modalità Difficile");
        center.add(normal);
        center.add(hard);
        add(center, BorderLayout.CENTER);

        normal.addActionListener((ActionEvent e) -> frame.setContentPane(new Gioco(frame, false)));
        hard.addActionListener((ActionEvent e) -> frame.setContentPane(new Gioco(frame, true)));
    }
}
