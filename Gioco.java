package com.mycompany.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gioco extends JPanel {

    // Variabili principali della classe
    private final int DIMENSIONE = 10;
    private JButton[][] grigliaGiocatore = new JButton[DIMENSIONE][DIMENSIONE];
    private JButton[][] grigliaBot = new JButton[DIMENSIONE][DIMENSIONE];
    private boolean[][] naviGiocatore = new boolean[DIMENSIONE][DIMENSIONE];
    private boolean[][] naviBot = new boolean[DIMENSIONE][DIMENSIONE];
    private boolean turnoGiocatore = true;
    private boolean orizzontale = true;
    private int[] dimensioniNavi = {5, 4, 3, 3, 2};
    private int indiceNaveCorrente = 0;
    private Random casuale = new Random();
    private JTextArea registro = new JTextArea(10, 20);
    private int colpiGiocatore = 0;
    private int colpiBot = 0;
    private final int COLPI_MASSIMI = 17;
    private JFrame finestra;
    private boolean modalitaDifficile;
    private List<Point> bersagliBot = new ArrayList<>();

    // Costruttore pubblico
    public Gioco(JFrame finestra, boolean difficile) {
        this.finestra = finestra;
        this.modalitaDifficile = difficile;
        setLayout(new BorderLayout());

        JPanel griglie = new JPanel(new GridLayout(1, 2, 10, 10));
        griglie.add(creaGriglia(grigliaGiocatore, false));
        griglie.add(creaGriglia(grigliaBot, true));
        posizionaNaviBot();

        JPanel barraLaterale = new JPanel(new BorderLayout());
        JButton reset = new JButton("Reset");
        JButton menu = new JButton("Torna al menu");

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resettaGioco();
            }
        });

        menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finestra.setContentPane(new Menu(finestra));
            }
        });

        registro.setEditable(false);
        registro.append("Posiziona le tue navi: dimensione attuale " + dimensioniNavi[indiceNaveCorrente] + "\n");

        JPanel controlli = new JPanel(new GridLayout(2, 1));
        controlli.add(reset);
        controlli.add(menu);
        barraLaterale.add(controlli, BorderLayout.NORTH);
        barraLaterale.add(new JScrollPane(registro), BorderLayout.CENTER);

        add(griglie, BorderLayout.CENTER);
        add(barraLaterale, BorderLayout.EAST);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "ruota");
        getActionMap().put("ruota", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orizzontale = !orizzontale;
                registro.append("Orientamento ruotato a " + (orizzontale ? "orizzontale" : "verticale") + "\n");
            }
        });
    }

    // Metodo pubblico per resettare il gioco
    public void resettaGioco() {
        finestra.setContentPane(new Gioco(finestra, modalitaDifficile));
        finestra.revalidate();
    }

    // Metodo pubblico per controllare se il gioco è terminato
    public boolean controllaFineGioco() {
        if (colpiGiocatore >= COLPI_MASSIMI) {
            registro.append("Hai vinto!\n");
            disabilitaTutto();
            return true;
        } else if (colpiBot >= COLPI_MASSIMI) {
            registro.append("Hai perso! Il bot ha vinto.\n");
            disabilitaTutto();
            return true;
        }
        return false;
    }

    // Metodo pubblico per abilitare/disabilitare la modalità difficile
    public boolean isModalitaDifficile() {
        return modalitaDifficile;
    }

    // Metodo pubblico per ottenere i colpi effettuati dal giocatore
    public int getColpiGiocatore() {
        return colpiGiocatore;
    }
package com.mycompany.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gioco extends JPanel {

    // Variabili principali della classe
    private final int DIMENSIONE = 10;
    private JButton[][] grigliaGiocatore = new JButton[DIMENSIONE][DIMENSIONE];
    private JButton[][] grigliaBot = new JButton[DIMENSIONE][DIMENSIONE];
    private boolean[][] naviGiocatore = new boolean[DIMENSIONE][DIMENSIONE];
    private boolean[][] naviBot = new boolean[DIMENSIONE][DIMENSIONE];
    private boolean turnoGiocatore = true;
    private boolean orizzontale = true;
    private int[] dimensioniNavi = {5, 4, 3, 3, 2};
    private int indiceNaveCorrente = 0;
    private Random casuale = new Random();
    private JTextArea registro = new JTextArea(10, 20);
    private int colpiGiocatore = 0;
    private int colpiBot = 0;
    private final int COLPI_MASSIMI = 17;
    private JFrame finestra;
    private boolean modalitaDifficile;
    private List<Point> bersagliBot = new ArrayList<>();

    // Costruttore pubblico
    public Gioco(JFrame finestra, boolean difficile) {
        this.finestra = finestra;
        this.modalitaDifficile = difficile;
        setLayout(new BorderLayout());

        JPanel griglie = new JPanel(new GridLayout(1, 2, 10, 10));
        griglie.add(creaGriglia(grigliaGiocatore, false));
        griglie.add(creaGriglia(grigliaBot, true));
        posizionaNaviBot();

        JPanel barraLaterale = new JPanel(new BorderLayout());
        JButton reset = new JButton("Reset");
        JButton menu = new JButton("Torna al menu");

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resettaGioco();
            }
        });

        menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finestra.setContentPane(new Menu(finestra));
            }
        });

        registro.setEditable(false);
        registro.append("Posiziona le tue navi: dimensione attuale " + dimensioniNavi[indiceNaveCorrente] + "\n");

        JPanel controlli = new JPanel(new GridLayout(2, 1));
        controlli.add(reset);
        controlli.add(menu);
        barraLaterale.add(controlli, BorderLayout.NORTH);
        barraLaterale.add(new JScrollPane(registro), BorderLayout.CENTER);

        add(griglie, BorderLayout.CENTER);
        add(barraLaterale, BorderLayout.EAST);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "ruota");
        getActionMap().put("ruota", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orizzontale = !orizzontale;
                registro.append("Orientamento ruotato a " + (orizzontale ? "orizzontale" : "verticale") + "\n");
            }
        });
    }

    // Metodo pubblico per resettare il gioco
    public void resettaGioco() {
        finestra.setContentPane(new Gioco(finestra, modalitaDifficile));
        finestra.revalidate();
    }

    // Metodo pubblico per controllare se il gioco è terminato
    public boolean controllaFineGioco() {
        if (colpiGiocatore >= COLPI_MASSIMI) {
            registro.append("Hai vinto!\n");
            disabilitaTutto();
            return true;
        } else if (colpiBot >= COLPI_MASSIMI) {
            registro.append("Hai perso! Il bot ha vinto.\n");
            disabilitaTutto();
            return true;
        }
        return false;
    }

    // Metodo pubblico per abilitare/disabilitare la modalità difficile
    public boolean isModalitaDifficile() {
        return modalitaDifficile;
    }

    // Metodo pubblico per ottenere i colpi effettuati dal giocatore
    public int getColpiGiocatore() {
        return colpiGiocatore;
    }

    // Metodo pubblico per ottenere i colpi effettuati dal bot
    public int getColpiBot() {
        return colpiBot;
    }
}
    // Metodo pubblico per ottenere i colpi effettuati dal bot
    public int getColpiBot() {
        return colpiBot;
    }
}
