package com.mycompany.kidsgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KidsGameGUI extends JFrame {

    // Modes
    private static final int MODE_SINGLE_MAKE_WISH = 1;
    private static final int MODE_SINGLE_TIME_TRIAL = 2;
    private static final int MODE_MULTI_TIME_TRIAL = 3;
    private static final int MODE_MULTI_TAKE_CHANCES = 4;

    // Layout
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Start panel
    private JPanel startPanel;
    private JButton btnSingle;
    private JButton btnMulti;

    // Single player setup
    private JPanel singleSetupPanel;
    private JTextField txtSingleName;
    private JTextField txtSingleQuestions;
    private JTextField txtSingleTime;
    private JRadioButton rbSingleMakeWish;
    private JRadioButton rbSingleTimeTrial;
    private JButton btnSingleStart;

    // Multiplayer setup
    private JPanel multiSetupPanel;
    private JTextField txtMultiNumPlayers;
    private JTextField txtMultiQuestions;
    private JTextField txtMultiTime;
    private JRadioButton rbMultiTimeTrial;
    private JRadioButton rbMultiTakeChances;
    private JButton btnMultiStart;

    // Question panel
    private JPanel questionPanel;
    private JLabel lblPlayerInfo;
    private JLabel lblModeInfo;
    private JLabel lblQuestion;
    private JTextField txtAnswer;
    private JButton btnSubmit;
    private JLabel lblFeedback;
    private JLabel lblStatus;   // score, lives, time left

    // Result panel
    private JPanel resultPanel;
    private JTextArea txtResult;
    private JButton btnBackToMenu;

    // Game state
    private int currentMode;
    private boolean singlePlayer;
    private GameLogic currentGame;
    private Player[] players;
    private String[] playerNames;
    private int currentPlayerIndex;
    private int totalQuestions;   // for Make a Wish / Take Chances
    private int askedQuestions;
    private int lives;            // for Take Chances
    private long startTimeMs;     // when this player's turn started
    private long timeLimitMs;     // milliseconds for time trial
    private int remainingSeconds; // NEW: countdown shown in label

    // NEW: Swing Timer for live countdown
    private javax.swing.Timer timer;
    // NEW: to avoid finishing same player twice
    private boolean playerFinished;

    public KidsGameGUI() {
        super("Kids Math Game - GUI Version");

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createStartPanel();
        createSingleSetupPanel();
        createMultiSetupPanel();
        createQuestionPanel();
        createResultPanel();

        cardPanel.add(startPanel, "start");
        cardPanel.add(singleSetupPanel, "singleSetup");
        cardPanel.add(multiSetupPanel, "multiSetup");
        cardPanel.add(questionPanel, "question");
        cardPanel.add(resultPanel, "result");

        add(cardPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // center
        setResizable(false);
    }

    private void createStartPanel() {
        startPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JLabel lblTitle = new JLabel("Kids Math Game", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));

        btnSingle = new JButton("Single Player");
        btnMulti = new JButton("Multiplayer");

        btnSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "singleSetup");
            }
        });

        btnMulti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "multiSetup");
            }
        });

        startPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        startPanel.add(lblTitle);
        startPanel.add(btnSingle);
        startPanel.add(btnMulti);
    }

    private void createSingleSetupPanel() {
        singleSetupPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        singleSetupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Single Player Setup", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblName = new JLabel("Player name:");
        txtSingleName = new JTextField();

        JLabel lblMode = new JLabel("Choose mode:");
        rbSingleMakeWish = new JRadioButton("Make a Wish (fixed questions)");
        rbSingleTimeTrial = new JRadioButton("Time Trial (seconds)");

        ButtonGroup group = new ButtonGroup();
        group.add(rbSingleMakeWish);
        group.add(rbSingleTimeTrial);

        JLabel lblQuestions = new JLabel("Number of questions (Make a Wish):");
        txtSingleQuestions = new JTextField();

        JLabel lblTime = new JLabel("Time limit in seconds (Time Trial):");
        txtSingleTime = new JTextField();

        btnSingleStart = new JButton("Start Game");
        JButton btnBack = new JButton("Back");

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "start");
            }
        });

        btnSingleStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSinglePlayerGame();
            }
        });

        singleSetupPanel.add(lblHeader);
        singleSetupPanel.add(new JLabel("")); // empty

        singleSetupPanel.add(lblName);
        singleSetupPanel.add(txtSingleName);

        singleSetupPanel.add(lblMode);
        singleSetupPanel.add(new JLabel("")); // empty

        singleSetupPanel.add(rbSingleMakeWish);
        singleSetupPanel.add(rbSingleTimeTrial);

        singleSetupPanel.add(lblQuestions);
        singleSetupPanel.add(txtSingleQuestions);

        singleSetupPanel.add(lblTime);
        singleSetupPanel.add(txtSingleTime);

        singleSetupPanel.add(btnBack);
        singleSetupPanel.add(btnSingleStart);
    }

    private void createMultiSetupPanel() {
        multiSetupPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        multiSetupPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Multiplayer Setup", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblNumPlayers = new JLabel("Number of players:");
        txtMultiNumPlayers = new JTextField();

        JLabel lblMode = new JLabel("Choose mode:");
        rbMultiTimeTrial = new JRadioButton("Time Trial (seconds)");
        rbMultiTakeChances = new JRadioButton("Take Chances (3 lives)");

        ButtonGroup group = new ButtonGroup();
        group.add(rbMultiTimeTrial);
        group.add(rbMultiTakeChances);

        JLabel lblQuestions = new JLabel("Number of questions (Take Chances):");
        txtMultiQuestions = new JTextField();

        JLabel lblTime = new JLabel("Time limit in seconds (Time Trial):");
        txtMultiTime = new JTextField();

        btnMultiStart = new JButton("Start Multiplayer");
        JButton btnBack = new JButton("Back");

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "start");
            }
        });

        btnMultiStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMultiplayerGame();
            }
        });

        multiSetupPanel.add(lblHeader);
        multiSetupPanel.add(new JLabel(""));

        multiSetupPanel.add(lblNumPlayers);
        multiSetupPanel.add(txtMultiNumPlayers);

        multiSetupPanel.add(lblMode);
        multiSetupPanel.add(new JLabel(""));

        multiSetupPanel.add(rbMultiTimeTrial);
        multiSetupPanel.add(rbMultiTakeChances);

        multiSetupPanel.add(lblQuestions);
        multiSetupPanel.add(txtMultiQuestions);

        multiSetupPanel.add(lblTime);
        multiSetupPanel.add(txtMultiTime);

        multiSetupPanel.add(btnBack);
        multiSetupPanel.add(btnMultiStart);
    }

    private void createQuestionPanel() {
        questionPanel = new JPanel(new BorderLayout(10, 10));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new GridLayout(2, 1));
        lblPlayerInfo = new JLabel("Player: ", SwingConstants.CENTER);
        lblModeInfo = new JLabel("Mode: ", SwingConstants.CENTER);
        top.add(lblPlayerInfo);
        top.add(lblModeInfo);

        JPanel center = new JPanel(new GridLayout(3, 1, 5, 5));
        lblQuestion = new JLabel("Question: ", SwingConstants.CENTER);
        txtAnswer = new JTextField();
        btnSubmit = new JButton("Submit Answer");

        center.add(lblQuestion);
        center.add(txtAnswer);
        center.add(btnSubmit);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        lblFeedback = new JLabel(" ", SwingConstants.CENTER);
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        bottom.add(lblFeedback);
        bottom.add(lblStatus);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmitAnswer();
            }
        });

        questionPanel.add(top, BorderLayout.NORTH);
        questionPanel.add(center, BorderLayout.CENTER);
        questionPanel.add(bottom, BorderLayout.SOUTH);
    }

    private void createResultPanel() {
        resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblHeader = new JLabel("Game Result", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 18));

        txtResult = new JTextArea();
        txtResult.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtResult);

        btnBackToMenu = new JButton("Back to Main Menu");
        btnBackToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "start");
            }
        });

        resultPanel.add(lblHeader, BorderLayout.NORTH);
        resultPanel.add(scroll, BorderLayout.CENTER);
        resultPanel.add(btnBackToMenu, BorderLayout.SOUTH);
    }

    // ----------------- GAME LOGIC (GUI SIDE) -----------------

    private void startSinglePlayerGame() {
        String name = txtSingleName.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter player name.");
            return;
        }

        if (!rbSingleMakeWish.isSelected() && !rbSingleTimeTrial.isSelected()) {
            showError("Please select a mode (Make a Wish or Time Trial).");
            return;
        }

        singlePlayer = true;
        currentPlayerIndex = 0;
        playerNames = new String[1];
        playerNames[0] = name;
        players = new Player[1];

        if (rbSingleMakeWish.isSelected()) {
            currentMode = MODE_SINGLE_MAKE_WISH;
            String txtQ = txtSingleQuestions.getText().trim();
            if (txtQ.isEmpty()) {
                showError("Please enter number of questions.");
                return;
            }
            try {
                totalQuestions = Integer.parseInt(txtQ);
                if (totalQuestions <= 0) {
                    showError("Number of questions must be > 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Number of questions must be an integer.");
                return;
            }
        } else {
            currentMode = MODE_SINGLE_TIME_TRIAL;
            String txtT = txtSingleTime.getText().trim();
            if (txtT.isEmpty()) {
                showError("Please enter time limit in seconds.");
                return;
            }
            try {
                int sec = Integer.parseInt(txtT);
                if (sec <= 0) {
                    showError("Time limit must be > 0.");
                    return;
                }
                timeLimitMs = sec * 1000L;
            } catch (NumberFormatException ex) {
                showError("Time must be an integer.");
                return;
            }
        }

        startGameForCurrentPlayer();
    }

    private void startMultiplayerGame() {
        String txtN = txtMultiNumPlayers.getText().trim();
        if (txtN.isEmpty()) {
            showError("Please enter number of players.");
            return;
        }

        int nPlayers;
        try {
            nPlayers = Integer.parseInt(txtN);
            if (nPlayers <= 0) {
                showError("Number of players must be > 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Number of players must be an integer.");
            return;
        }

        if (!rbMultiTimeTrial.isSelected() && !rbMultiTakeChances.isSelected()) {
            showError("Please select a mode (Time Trial or Take Chances).");
            return;
        }

        singlePlayer = false;
        players = new Player[nPlayers];
        playerNames = new String[nPlayers];
        currentPlayerIndex = 0;

        // Ask names one by one using dialogs (simple).
        for (int i = 0; i < nPlayers; i++) {
            String name = JOptionPane.showInputDialog(this, "Enter name for Player " + (i + 1) + ":");
            if (name == null || name.trim().isEmpty()) {
                name = "Player" + (i + 1);
            }
            playerNames[i] = name.trim();
        }

        if (rbMultiTimeTrial.isSelected()) {
            currentMode = MODE_MULTI_TIME_TRIAL;
            String txtT = txtMultiTime.getText().trim();
            if (txtT.isEmpty()) {
                showError("Please enter time limit in seconds.");
                return;
            }
            try {
                int sec = Integer.parseInt(txtT);
                if (sec <= 0) {
                    showError("Time limit must be > 0.");
                    return;
                }
                timeLimitMs = sec * 1000L;
            } catch (NumberFormatException ex) {
                showError("Time must be an integer.");
                return;
            }
        } else { // Take Chances
            currentMode = MODE_MULTI_TAKE_CHANCES;
            String txtQ = txtMultiQuestions.getText().trim();
            if (txtQ.isEmpty()) {
                showError("Please enter number of questions.");
                return;
            }
            try {
                totalQuestions = Integer.parseInt(txtQ);
                if (totalQuestions <= 0) {
                    showError("Number of questions must be > 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showError("Number of questions must be an integer.");
                return;
            }
        }

        startGameForCurrentPlayer();
    }

    private void startGameForCurrentPlayer() {
        // stop any previous timer
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        playerFinished = false;
        currentGame = new GameLogic();
        askedQuestions = 0;
        lives = 3; // used only for Take Chances
        startTimeMs = System.currentTimeMillis();

        String playerName = playerNames[currentPlayerIndex];

        lblPlayerInfo.setText("Player: " + playerName);

        String modeText;
        if (currentMode == MODE_SINGLE_MAKE_WISH) {
            modeText = "Single Player - Make a Wish";
        } else if (currentMode == MODE_SINGLE_TIME_TRIAL) {
            modeText = "Single Player - Time Trial";
        } else if (currentMode == MODE_MULTI_TIME_TRIAL) {
            modeText = "Multiplayer - Time Trial";
        } else {
            modeText = "Multiplayer - Take Chances";
        }
        lblModeInfo.setText(modeText);

        // For time-trial modes, set remainingSeconds based on timeLimitMs
        if (isTimeTrialMode()) {
            remainingSeconds = (int) (timeLimitMs / 1000L);
        }

        cardLayout.show(cardPanel, "question");
        askNewQuestion();

        // If this is a time-trial mode, start the Swing timer for live countdown.
        if (isTimeTrialMode()) {
            timer = new javax.swing.Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (playerFinished) {
                        timer.stop();
                        return;
                    }
                    remainingSeconds--;
                    if (remainingSeconds <= 0) {
                        remainingSeconds = 0;
                        lblFeedback.setText("Time's up!");
                        updateStatusLabel();
                        endCurrentPlayer();
                    } else {
                        updateStatusLabel();
                    }
                }
            });
            timer.start();
        }
    }

    private void askNewQuestion() {
        String q = currentGame.generateNextQuestion();
        lblQuestion.setText(q);
        txtAnswer.setText("");
        lblFeedback.setText(" ");
        updateStatusLabel();
    }

    private void updateStatusLabel() {
        String status = "Score: " + currentGame.getScore();

        if (currentMode == MODE_MULTI_TAKE_CHANCES) {
            status += " | Lives: " + lives + " | Questions asked: " + askedQuestions + "/" + totalQuestions;
        } else if (currentMode == MODE_SINGLE_MAKE_WISH) {
            status += " | Questions asked: " + askedQuestions + "/" + totalQuestions;
        } else { // time trial modes
            status += " | Time left: " + remainingSeconds + " sec";
        }

        lblStatus.setText(status);
    }

    private void handleSubmitAnswer() {
        if (playerFinished) {
            // game already ended for this player, ignore clicks
            return;
        }

        String answer = txtAnswer.getText().trim();
        if (answer.isEmpty()) {
            lblFeedback.setText("Please enter your answer.");
            return;
        }

        // If time-trial game and time already expired, end immediately (ignore this answer).
        if (isTimeTrialMode() && remainingSeconds <= 0) {
            if (timer != null) {
                timer.stop();
            }
            lblFeedback.setText("Time's up!");
            updateStatusLabel();
            endCurrentPlayer();
            return;
        }

        boolean correct;
        try {
            correct = currentGame.checkCurrentQuestion(answer);
        } catch (NumberFormatException ex) {
            lblFeedback.setText("Please enter a valid number.");
            return;
        }

        if (correct) {
            lblFeedback.setText("Correct! Good job!");
        } else {
            lblFeedback.setText("Wrong answer.");
            if (currentMode == MODE_MULTI_TAKE_CHANCES) {
                lives--;
            }
        }

        askedQuestions++;
        updateStatusLabel();

        if (shouldEndCurrentPlayer()) {
            endCurrentPlayer();
        } else {
            askNewQuestion();
        }
    }

    private boolean isTimeTrialMode() {
        return currentMode == MODE_SINGLE_TIME_TRIAL || currentMode == MODE_MULTI_TIME_TRIAL;
    }

    private boolean shouldEndCurrentPlayer() {
        if (currentMode == MODE_SINGLE_MAKE_WISH) {
            return askedQuestions >= totalQuestions;
        } else if (currentMode == MODE_MULTI_TAKE_CHANCES) {
            if (lives <= 0) {
                return true;
            }
            return askedQuestions >= totalQuestions;
        } else { // time trial
            return remainingSeconds <= 0;
        }
    }

    private void endCurrentPlayer() {
        if (playerFinished) {
            return; // already ended
        }

        playerFinished = true;

        if (timer != null) {
            timer.stop();
            timer = null;
        }

        long end = System.currentTimeMillis();
        long elapsed = end - startTimeMs;
        String name = playerNames[currentPlayerIndex];

        Player p = new Player(name, currentGame.getScore(), currentGame.getSummary(), elapsed);
        players[currentPlayerIndex] = p;

        // Move to next player or show results
        if (!singlePlayer && currentPlayerIndex < players.length - 1) {
            currentPlayerIndex++;
            JOptionPane.showMessageDialog(this,
                    "Next player: " + playerNames[currentPlayerIndex],
                    "Next Player",
                    JOptionPane.INFORMATION_MESSAGE);
            startGameForCurrentPlayer();
        } else {
            // All done → show result panel
            showFinalResults();
        }
    }

    private void showFinalResults() {
        if (!singlePlayer) {
            // Bubble sort by score descending (like Assignment 1)
            for (int i = 0; i < players.length - 1; i++) {
                for (int j = 0; j < players.length - 1 - i; j++) {
                    if (players[j].getScore() < players[j + 1].getScore()) {
                        Player temp = players[j];
                        players[j] = players[j + 1];
                        players[j + 1] = temp;
                    }
                }
            }
        }

        StringBuilder sb = new StringBuilder();

        if (singlePlayer) {
            Player p = players[0];
            sb.append("Single Player Result\n");
            sb.append("---------------------\n");
            sb.append("Player: ").append(p.getName()).append("\n");
            sb.append("Score: ").append(p.getScore()).append("\n");
            sb.append("Time: ").append(p.getTimeTakenMs() / 1000.0).append(" seconds\n\n");
            sb.append("Summary:\n");
            sb.append(p.getSummary());
        } else {
            sb.append("Multiplayer Result\n");
            sb.append("---------------------\n");

            for (int i = 0; i < players.length; i++) {
                Player p = players[i];
                sb.append((i + 1)).append(". ")
                        .append(p.getName())
                        .append(" - Score: ").append(p.getScore())
                        .append(" - Time: ").append(p.getTimeTakenMs() / 1000.0).append(" sec\n");
            }

            if (players.length > 0) {
                sb.append("\nWinner: ").append(players[0].getName()).append("!\n");
            }
        }

        txtResult.setText(sb.toString());
        cardLayout.show(cardPanel, "result");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                KidsGameGUI gui = new KidsGameGUI();
                gui.setVisible(true);
            }
        });
    }
}
