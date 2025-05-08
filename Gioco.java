package com.mycompany.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gioco extends JPanel {
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

    // Crea la griglia di bottoni per il giocatore o il bot
    private JPanel creaGriglia(JButton[][] griglia, boolean èBot) {
        JPanel pannello = new JPanel(new GridLayout(DIMENSIONE, DIMENSIONE));
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                JButton bottone = new JButton();
                bottone.setBackground(Color.CYAN);
                int x = i, y = j;
                if (!èBot) {
                    bottone.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            gestisciPosizionamentoGiocatore(x, y);
                        }
                    });

                    bottone.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            anteprimaPosizionamentoNave(x, y, true);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            anteprimaPosizionamentoNave(x, y, false);
                        }
                    });
                } else {
                    bottone.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            gestisciAttaccoGiocatore(x, y);
                        }
                    });
                }
                griglia[i][j] = bottone;
                pannello.add(bottone);
            }
        }
        return pannello;
    }

    // Gestisce il posizionamento delle navi del giocatore
    private void gestisciPosizionamentoGiocatore(int x, int y) {
        if (indiceNaveCorrente >= dimensioniNavi.length) return;
        int lunghezza = dimensioniNavi[indiceNaveCorrente];

        if (!puòPosizionareNave(naviGiocatore, x, y, orizzontale)) {
            registro.append("Posizione non valida\n");
            return;
        }

        posizionaNave(naviGiocatore, grigliaGiocatore, x, y, orizzontale, false, lunghezza);
        indiceNaveCorrente++;
        if (indiceNaveCorrente < dimensioniNavi.length) {
            registro.append("Posiziona la prossima nave: dimensione " + dimensioniNavi[indiceNaveCorrente] + "\n");
        } else {
            registro.append("Tutte le navi posizionate. Inizia il gioco!\n");
        }
    }

    // Gestisce l'attacco del giocatore contro il bot
    private void gestisciAttaccoGiocatore(int x, int y) {
        if (colpiBot >= COLPI_MASSIMI || colpiGiocatore >= COLPI_MASSIMI) return;
        if (indiceNaveCorrente < dimensioniNavi.length || !turnoGiocatore) return;
        JButton bottone = grigliaBot[x][y];
        if (!bottone.getText().isEmpty()) return;
        if (naviBot[x][y]) {
            bottone.setBackground(Color.RED);
            bottone.setText("X");
            registro.append("Colpito!\n");
            colpiGiocatore++;
            controllaFineGioco();
        } else {
            bottone.setBackground(Color.WHITE);
            bottone.setText("O");
            registro.append("Acqua.\n");
        }
        turnoGiocatore = false;
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attaccoBot();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Gestisce l'attacco del bot contro il giocatore
    private void attaccoBot() {
        if (colpiBot >= COLPI_MASSIMI || colpiGiocatore >= COLPI_MASSIMI) return;

        Point bersaglio = null;

        if (!bersagliBot.isEmpty()) {
            bersaglio = bersagliBot.remove(0);
        } else {
            do {
                int x = casuale.nextInt(DIMENSIONE);
                int y = casuale.nextInt(DIMENSIONE);
                bersaglio = new Point(x, y);
            } while (!attaccoValido(bersaglio));
        }

        int x = bersaglio.x;
        int y = bersaglio.y;
        JButton bottone = grigliaGiocatore[x][y];

        if (naviGiocatore[x][y]) {
            bottone.setText("X");
            bottone.setBackground(Color.RED);
            registro.append("Il bot ha colpito una nave!\n");
            colpiBot++;
            aggiungiBersagliAdiacenti(x, y);
            controllaFineGioco();
        } else {
            bottone.setText("O");
            bottone.setBackground(Color.GRAY);
            registro.append("Il bot ha mancato.\n");
        }

        turnoGiocatore = true;
    }

    // Controlla se un attacco è valido
    private boolean attaccoValido(Point p) {
        int x = p.x;
        int y = p.y;
        return x >= 0 && x < DIMENSIONE && y >= 0 && y < DIMENSIONE && grigliaGiocatore[x][y].getText().isEmpty();
    }

    // Aggiunge bersagli adiacenti per il bot
    private void aggiungiBersagliAdiacenti(int x, int y) {
        int[][] direzioni = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] direzione : direzioni) {
            int nx = x + direzione[0];
            int ny = y + direzione[1];
            if (nx >= 0 && nx < DIMENSIONE && ny >= 0 && ny < DIMENSIONE && grigliaGiocatore[nx][ny].getText().isEmpty()) {
                bersagliBot.add(new Point(nx, ny));
            }
        }
    }

    // Controlla se il gioco è terminato
    private void controllaFineGioco() {
        if (colpiGiocatore >= COLPI_MASSIMI) {
            registro.append("Hai vinto!\n");
            disabilitaTutto();
        } else if (colpiBot >= COLPI_MASSIMI) {
            registro.append("Hai perso! Il bot ha vinto.\n");
            disabilitaTutto();
        }
    }

    // Disabilita tutti i bottoni della griglia del bot
    private void disabilitaTutto() {
        for (int i = 0; i < DIMENSIONE; i++) {
            for (int j = 0; j < DIMENSIONE; j++) {
                grigliaBot[i][j].setEnabled(false);
            }
        }
    }

    // Posiziona le navi del bot casualmente
    private void posizionaNaviBot() {
        for (int lunghezza : dimensioniNavi) {
            int x, y;
            boolean oriz;
            do {
                x = casuale.nextInt(DIMENSIONE);
                y = casuale.nextInt(DIMENSIONE);
                oriz = casuale.nextBoolean();
            } while (!puòPosizionareNave(naviBot, x, y, oriz));
            posizionaNave(naviBot, grigliaBot, x, y, oriz, true, lunghezza);
        }
    }

    // Resetta il gioco
    private void resettaGioco() {
        finestra.setContentPane(new Gioco(finestra, modalitaDifficile));
        finestra.revalidate();
    }

    // Mostra un'anteprima del posizionamento della nave
    private void anteprimaPosizionamentoNave(int x, int y, boolean mostra) {
        if (indiceNaveCorrente >= dimensioniNavi.length) return;
        int lunghezza = dimensioniNavi[indiceNaveCorrente];

        if (!puòPosizionareNave(naviGiocatore, x, y, orizzontale)) return;

        for (int i = 0; i < lunghezza; i++) {
            int nx = orizzontale ? x : x + i;
            int ny = orizzontale ? y + i : y;

            if (nx >= DIMENSIONE || ny >= DIMENSIONE) return;

            grigliaGiocatore[nx][ny].setBackground(mostra ? Color.YELLOW : Color.CYAN);
        }
    }

    // Verifica se una nave può essere posizionata
    private boolean puòPosizionareNave(boolean[][] griglia, int x, int y, boolean oriz) {
        int lunghezza = dimensioniNavi[indiceNaveCorrente];
        if (oriz) {
            if (y + lunghezza > DIMENSIONE) return false;
            for (int i = 0; i < lunghezza; i++) if (griglia[x][y + i]) return false;
        } else {
            if (x + lunghezza > DIMENSIONE) return false;
            for (int i = 0; i < lunghezza; i++) if (griglia[x + i][y]) return false;
        }
        return true;
    }

    // Posiziona una nave sulla griglia
    private void posizionaNave(boolean[][] griglia, JButton[][] bottoni, int x, int y, boolean oriz, boolean èBot, int lunghezza) {
        for (int i = 0; i < lunghezza; i++) {
            if (oriz) {
                griglia[x][y + i] = true;
                if (!èBot) bottoni[x][y + i].setBackground(Color.GREEN);
            } else {
                griglia[x + i][y] = true;
                if (!èBot) bottoni[x + i][y].setBackground(Color.GREEN);
            }
        }
    }
}
