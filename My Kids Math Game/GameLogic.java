package com.mycompany.kidsgame;

import java.util.Random;

public class GameLogic {

    private int score;
    private String summary;

    private Random rand;

    // Current question info
    private String currentExpression;
    private boolean divisionQuestion;
    private int correctIntAnswer;
    private double correctDoubleAnswer;

    public GameLogic() {
        this.score = 0;
        this.summary = "";
        this.rand = new Random();
        this.currentExpression = "";
        this.divisionQuestion = false;
    }

    public int getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }

    // Create a new random question (1=+,2=-,3=*,4=/)
    public String generateNextQuestion() {
        int op = rand.nextInt(4) + 1; // 1..4

        int num1, num2;

        divisionQuestion = false;
        correctIntAnswer = 0;
        correctDoubleAnswer = 0.0;

        if (op == 1) { // addition
            num1 = rand.nextInt(20) + 1;
            num2 = rand.nextInt(20) + 1;
            correctIntAnswer = num1 + num2;
            currentExpression = num1 + " + " + num2;
        } else if (op == 2) { // subtraction (no negative)
            int a = rand.nextInt(20) + 1;
            int b = rand.nextInt(20) + 1;
            int big = (a > b) ? a : b;
            int small = (a > b) ? b : a;
            correctIntAnswer = big - small;
            currentExpression = big + " - " + small;
        } else if (op == 3) { // multiplication
            num1 = rand.nextInt(20) + 1;
            num2 = rand.nextInt(20) + 1;
            correctIntAnswer = num1 * num2;
            currentExpression = num1 + " * " + num2;
        } else { // division (double, 2 decimals)
            int numerator = rand.nextInt(20) + 1;
            int divisor = rand.nextInt(10) + 1;
            correctDoubleAnswer = (double) numerator / divisor;
            divisionQuestion = true;
            currentExpression = numerator + " / " + divisor;
        }

        return currentExpression + " = ?";
    }

    private double roundTwo(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // Check user answer (string text from GUI)
    // Returns true if correct, false if wrong.
    // Throws NumberFormatException if input is not a number.
    public boolean checkCurrentQuestion(String userInput) throws NumberFormatException {
        boolean correct;

        if (divisionQuestion) {
            double userValue = Double.parseDouble(userInput);
            double roundedUser = roundTwo(userValue);
            double roundedCorrect = roundTwo(correctDoubleAnswer);
            correct = (roundedUser == roundedCorrect);
            if (correct) {
                score++;
            }
            summary += currentExpression + "=" + String.format("%.2f", roundedUser)
                    + ":" + correct + "\n";
        } else {
            int userValue = Integer.parseInt(userInput);
            correct = (userValue == correctIntAnswer);
            if (correct) {
                score++;
            }
            summary += currentExpression + "=" + userValue + ":" + correct + "\n";
        }

        return correct;
    }
}
