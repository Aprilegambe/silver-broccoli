package com.mycompany.main;

import javax.swing.JFrame;

public class Principale {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Creazione della finestra principale del gioco
                JFrame finestra = new JFrame("Battaglia Navale");
                finestra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                finestra.setSize(900, 700); // Dimensioni della finestra
                finestra.setLocationRelativeTo(null); // Posiziona la finestra al centro dello schermo
                finestra.setContentPane(new Menu(finestra)); // Imposta il menu come contenuto iniziale
                finestra.setVisible(true); // Rendi visibile la finestra
            }
        });
    }
}
