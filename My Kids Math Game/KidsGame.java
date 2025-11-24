package com.mycompany.kidsgame;
import java.util.Scanner;

public class KidsGame {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("==== Math Learning Game ====");
        System.out.println("1) Make a wish");
        System.out.println("2) No Mistakes");
        System.out.println("3) Take Chances (3 lives)");
        System.out.println("4) Time Trial");
        System.out.print("Choose a mode: ");
        int mode = input.nextInt();

        System.out.print("How many players? ");
        int nPlayers = input.nextInt();

        int commonQuestions = 0;
        int commonSecs = 0;
        if (mode == 1) {
            System.out.print("How many questions? ");
            commonQuestions = input.nextInt();
        } else if (mode == 4) {
            System.out.print("Enter time limit (seconds): ");
            commonSecs = input.nextInt();
        }

        Player[] players = new Player[nPlayers];

        for (int pi = 0; pi < nPlayers; pi++) {
            System.out.print("Enter player " + (pi + 1) + " name: ");
            String name = input.next();   // single token name (nextLine not required by brief)

            Game game = new Game();
            long t0 = System.currentTimeMillis();

            switch (mode) {
                case 1: {
                    for (int i = 0; i < commonQuestions; i++) {
                        int op = 1 + (int)(Math.random() * 4);
                        switch (op) {
                            case 1: game.generateAddQuestions(); break;
                            case 2: game.generateSubQuestion();  break;
                            case 3: game.generateMulQuestion();  break;
                            case 4: game.generateDivQuestion();  break;
                        }
                    }
                    break;
                }
                case 2: {
                    boolean wrong = false;
                    while (!wrong) {
                        int before = game.score;
                        int op = 1 + (int)(Math.random() * 4);
                        switch (op) {
                            case 1: game.generateAddQuestions(); break;
                            case 2: game.generateSubQuestion();  break;
                            case 3: game.generateMulQuestion();  break;
                            case 4: game.generateDivQuestion();  break;
                        }
                        if (game.score == before) {
                            wrong = true;
                        }
                    }
                    break;
                }
                case 3: {
                    int lives = 3;
                    while (lives > 0) {
                        System.out.println("[Lives: " + lives + "]");
                        int before = game.score;
                        int op = 1 + (int)(Math.random() * 4);
                        switch (op) {
                            case 1: game.generateAddQuestions(); break;
                            case 2: game.generateSubQuestion();  break;
                            case 3: game.generateMulQuestion();  break;
                            default: game.generateDivQuestion();  break;
                        }
                        if (game.score == before) {
                            lives--;
                        }
                    }
                    break;
                }
                case 4: {
                    long limitMs = (long) commonSecs * 1000L;
                    long start = System.currentTimeMillis();
                    int asked = 0;
                    while (System.currentTimeMillis() - start < limitMs) {
                        int op = 1 + (int)(Math.random() * 4);
                        switch (op) {
                            case 1: game.generateAddQuestions(); break;
                            case 2: game.generateSubQuestion();  break;
                            case 3: game.generateMulQuestion();  break;
                            case 4: game.generateDivQuestion();  break;
                        }
                        asked++;
                    }
                    System.out.println("Time's up!");
                    System.out.println("-- Results --");
                    System.out.println("Questions answered: " + asked);
                    break;
                }
                default:
                    System.out.println("Invalid mode. Exiting.");
                    return;
            }

            long elapsed = System.currentTimeMillis() - t0;

            System.out.println("-- Results --");
            System.out.println("Time taken: " + (elapsed / 1000) + " seconds");
            System.out.println("Score: " + game.score);
            System.out.println("Summary:");
            System.out.println(game.summary);

            players[pi] = new Player(name, game.score, game.summary, elapsed);
        }

        // Stable bubble sort by score (descending), preserves play order for ties
        for (int i = 0; i < nPlayers - 1; i++) {
            for (int j = 0; j < nPlayers - 1 - i; j++) {
                if (players[j].getScore() < players[j + 1].getScore()) {
                    Player tmp = players[j];
                    players[j] = players[j + 1];
                    players[j + 1] = tmp;
                }
            }
        }

        System.out.println();
        System.out.println("-- Leaderboard (Score only) --");
        for (int i = 0; i < nPlayers; i++) {
            System.out.println(players[i].getName() + " : " + players[i].getScore());
        }
    }
}

