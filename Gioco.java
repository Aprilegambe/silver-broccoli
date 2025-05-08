package com.mycompany.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gioco extends JPanel {
    private final int SIZE = 10;
    private JButton[][] playerGrid = new JButton[SIZE][SIZE];
    private JButton[][] botGrid = new JButton[SIZE][SIZE];
    private boolean[][] playerShips = new boolean[SIZE][SIZE];
    private boolean[][] botShips = new boolean[SIZE][SIZE];
    private boolean playerTurn = true;
    private boolean horizontal = true;
    private int[] shipSizes = {5, 4, 3, 3, 2}; // Dimensioni variabili delle navi
    private int currentShipIndex = 0;
    private Random random = new Random();
    private JTextArea log = new JTextArea(10, 20);
    private int playerHits = 0;
    private int botHits = 0;
    private final int MAX_HITS = 17; // Somma delle dimensioni delle navi
    private JFrame frame;
    private boolean isHardMode;
    private List<Point> botTargets = new ArrayList<>(); // Lista dei bersagli successivi per il bot

    public Gioco(JFrame frame, boolean difficile) {
        this.frame = frame;
        this.isHardMode = difficile;
        setLayout(new BorderLayout());
        JPanel grids = new JPanel(new GridLayout(1, 2, 10, 10));

        grids.add(createGrid(playerGrid, false));
        grids.add(createGrid(botGrid, true));
        placeBotShips();

        JPanel sidebar = new JPanel(new BorderLayout());
        JButton reset = new JButton("Reset");
        JButton back = new JButton("Torna al menu");
        reset.addActionListener(e -> resetGame());
        back.addActionListener(e -> frame.setContentPane(new Menu(frame)));

        log.setEditable(false);
        log.append("Posiziona le tue navi: dimensione attuale " + shipSizes[currentShipIndex] + "\n");

        JPanel controls = new JPanel(new GridLayout(2, 1));
        controls.add(reset);
        controls.add(back);
        sidebar.add(controls, BorderLayout.NORTH);
        sidebar.add(new JScrollPane(log), BorderLayout.CENTER);

        add(grids, BorderLayout.CENTER);
        add(sidebar, BorderLayout.EAST);

        // Configura KeyBinding per il tasto "R"
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "rotate");
        getActionMap().put("rotate", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                horizontal = !horizontal;
                log.append("Orientamento ruotato a " + (horizontal ? "orizzontale" : "verticale") + "\n");
            }
        });
    }

    private JPanel createGrid(JButton[][] grid, boolean isBot) {
        JPanel panel = new JPanel(new GridLayout(SIZE, SIZE));
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton btn = new JButton();
                btn.setBackground(Color.CYAN);
                int x = i, y = j;
                if (!isBot) {
                    btn.addActionListener(e -> handlePlayerPlacement(x, y));
                    btn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            previewShipPlacement(x, y, true);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            previewShipPlacement(x, y, false);
                        }
                    });
                } else {
                    btn.addActionListener(e -> handlePlayerAttack(x, y));
                }
                grid[i][j] = btn;
                panel.add(btn);
            }
        }
        return panel;
    }

    private void handlePlayerPlacement(int x, int y) {
        if (currentShipIndex >= shipSizes.length) return;
        int len = shipSizes[currentShipIndex];

        if (!canPlaceShip(playerShips, x, y, horizontal)) {
            log.append("Posizione non valida\n");
            return;
        }

        placeShip(playerShips, playerGrid, x, y, horizontal, false, len);
        currentShipIndex++;
        if (currentShipIndex < shipSizes.length) {
            log.append("Posiziona la prossima nave: dimensione " + shipSizes[currentShipIndex] + "\n");
        } else {
            log.append("Tutte le navi posizionate. Inizia il gioco!\n");
        }
    }

    private void handlePlayerAttack(int x, int y) {
        if (botHits >= MAX_HITS || playerHits >= MAX_HITS) return;
        if (currentShipIndex < shipSizes.length || !playerTurn) return;
        JButton btn = botGrid[x][y];
        if (!btn.getText().isEmpty()) return;
        if (botShips[x][y]) {
            btn.setBackground(Color.RED);
            btn.setText("X");
            log.append("Colpito!\n");
            playerHits++;
            checkGameOver();
        } else {
            btn.setBackground(Color.WHITE);
            btn.setText("O");
            log.append("Acqua.\n");
        }
        playerTurn = false;
        Timer timer = new Timer(1000, e -> botAttack());
        timer.setRepeats(false);
        timer.start();
    }

    private void botAttack() {
        if (botHits >= MAX_HITS || playerHits >= MAX_HITS) return;

        Point target = null;

        // Modalità "Normale" e "Difficile"
        if (!botTargets.isEmpty()) {
            target = botTargets.remove(0); // Attacca il primo bersaglio nella lista
        } else {
            // Se non ci sono bersagli, scegli casualmente
            do {
                int x = random.nextInt(SIZE);
                int y = random.nextInt(SIZE);
                target = new Point(x, y);
            } while (!isValidAttack(target));
        }

        int x = target.x;
        int y = target.y;
        JButton btn = playerGrid[x][y];

        if (playerShips[x][y]) {
            btn.setText("X");
            btn.setBackground(Color.RED);
            log.append("Il bot ha colpito una nave!\n");
            botHits++;
            addAdjacentTargets(x, y); // Aggiungi le caselle adiacenti come bersagli
            checkGameOver();
        } else {
            btn.setText("O");
            btn.setBackground(Color.GRAY);
            log.append("Il bot ha mancato.\n");
        }

        if (isHardMode && botHits > 0) {
            strategizeAttack(); // Pianifica un attacco più strategico
        }

        playerTurn = true;
    }

    private boolean isValidAttack(Point p) {
        int x = p.x;
        int y = p.y;
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE && playerGrid[x][y].getText().isEmpty();
    }

    private void addAdjacentTargets(int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && playerGrid[nx][ny].getText().isEmpty()) {
                botTargets.add(new Point(nx, ny));
            }
        }
    }

    private void strategizeAttack() {
        // Logica "Difficile": considera le dimensioni delle navi rimanenti e le caselle disponibili
        log.append("Il bot sta pianificando un attacco strategico...\n");
        // Implementazione futura per la modalità più complessa
    }

    private void checkGameOver() {
        if (playerHits >= MAX_HITS) {
            log.append("Hai vinto!\n");
            disableAll();
        } else if (botHits >= MAX_HITS) {
            log.append("Hai perso! Il bot ha vinto.\n");
            disableAll();
        }
    }

    private boolean canPlaceShip(boolean[][] grid, int x, int y, boolean horizontal) {
        int len = shipSizes[currentShipIndex];
        if (horizontal) {
            if (y + len > SIZE) return false;
            for (int i = 0; i < len; i++) if (grid[x][y + i]) return false;
        } else {
            if (x + len > SIZE) return false;
            for (int i = 0; i < len; i++) if (grid[x + i][y]) return false;
        }
        return true;
    }

    private void placeShip(boolean[][] grid, JButton[][] buttons, int x, int y, boolean horizontal, boolean isBot, int len) {
        for (int i = 0; i < len; i++) {
            if (horizontal) {
                grid[x][y + i] = true;
                if (!isBot) buttons[x][y + i].setBackground(Color.GREEN);
            } else {
                grid[x + i][y] = true;
                if (!isBot) buttons[x + i][y].setBackground(Color.GREEN);
            }
        }
    }

    private void disableAll() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                botGrid[i][j].setEnabled(false);
            }
        }
    }

    private void placeBotShips() {
        for (int len : shipSizes) {
            int x, y;
            boolean hor;
            do {
                x = random.nextInt(SIZE);
                y = random.nextInt(SIZE);
                hor = random.nextBoolean();
            } while (!canPlaceShip(botShips, x, y, hor));
            placeShip(botShips, botGrid, x, y, hor, true, len);
        }
    }

    private void resetGame() {
        frame.setContentPane(new Gioco(frame, isHardMode));
        frame.revalidate();
    }
    private void previewShipPlacement(int x, int y, boolean show) {
    if (currentShipIndex >= shipSizes.length) return; // Controlla se tutte le navi sono già state posizionate
    int len = shipSizes[currentShipIndex]; // Ottieni la dimensione della nave corrente

    // Controlla se la nave può essere posizionata nella posizione specificata
    if (!canPlaceShip(playerShips, x, y, horizontal)) return;

    // Mostra o rimuove l'anteprima
    for (int i = 0; i < len; i++) {
        int nx = horizontal ? x : x + i; // Calcola la coordinata X per posizionamento verticale
        int ny = horizontal ? y + i : y; // Calcola la coordinata Y per posizionamento orizzontale

        // Controlla i limiti del tabellone
        if (nx >= SIZE || ny >= SIZE) return;

        // Cambia il colore del pulsante per mostrare l'anteprima
        playerGrid[nx][ny].setBackground(show ? Color.YELLOW : Color.CYAN);
    }
}
}
