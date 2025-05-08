package com.mycompany.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Menu extends JPanel {
    public Menu(JFrame finestra) {
        setLayout(new BorderLayout());

        // Titolo del menu
        JLabel titolo = new JLabel("Battaglia Navale", SwingConstants.CENTER);
        titolo.setFont(new Font("Arial", Font.BOLD, 28));
        add(titolo, BorderLayout.NORTH);

        // Pulsanti per selezionare la modalità di gioco
        JPanel centro = new JPanel();
        JButton normale = new JButton("Modalità Normale");
        JButton difficile = new JButton("Modalità Difficile");
        centro.add(normale);
        centro.add(difficile);
        add(centro, BorderLayout.CENTER);

        // Azione del pulsante per la modalità normale
        normale.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finestra.setContentPane(new Gioco(finestra, false));
            }
        });

        // Azione del pulsante per la modalità difficile
        difficile.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finestra.setContentPane(new Gioco(finestra, true));
            }
        });
    }
}
