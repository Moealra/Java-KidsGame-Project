package com.mycompany.kidsgame;
import java.util.Scanner;

public class Game {
    public int score;
    public String summary = "";

    public void generateAddQuestions(){
        Scanner input = new Scanner(System.in);
        // 1..20 for both operands
        int num1 = 1 + (int)(Math.random() * 20);
        int num2 = 1 + (int)(Math.random() * 20);
        int actualAnswer = num1 + num2;

        System.out.println("What is " + num1 + " + " + num2 + " ?");
        int userAnswer = input.nextInt();

        summary += "\n" + num1 + "+" + num2 + "=" + userAnswer + ":" + (userAnswer == actualAnswer);
        if (actualAnswer == userAnswer) {
            ++score;
        }
    }

    public void printSummary(){
        System.out.println("Your Score:" + score);
        System.out.println("------Summary-----" + summary);
    }

    public void generateSubQuestion(){
        Scanner input = new Scanner(System.in);
        int a = 1 + (int)(Math.random() * 20);
        int b = 1 + (int)(Math.random() * 20);

        int big = a, small = b;
        if (small > big) { int t = big; big = small; small = t; }

        int actualAnswer = big - small;

        System.out.println("What is " + big + " - " + small + " ?");
        int userAnswer = input.nextInt();

        summary += "\n" + big + "-" + small + "=" + userAnswer + ":" + (userAnswer == actualAnswer);
        if (userAnswer == actualAnswer) { ++score; }
    }

    public void generateMulQuestion(){
        Scanner input = new Scanner(System.in);
        int num1 = 1 + (int)(Math.random() * 20);
        int num2 = 1 + (int)(Math.random() * 20);

        int actualAnswer = num1 * num2;

        System.out.println("What is " + num1 + " * " + num2 + " ?");
        int userAnswer = input.nextInt();

        summary += "\n" + num1 + "*" + num2 + "=" + userAnswer + ":" + (userAnswer == actualAnswer);
        if (userAnswer == actualAnswer) { ++score; }
    }
    
    public void generateDivQuestion(){
        Scanner input = new Scanner(System.in);

        int numerator = 1 + (int)(Math.random() * 20);
        int divisor   = 1 + (int)(Math.random() * 10);

        System.out.println("What is " + numerator + " / " + divisor + " ?");
        double user = input.nextDouble();

        double actual = (double) numerator / divisor;
        double actual2dp = Math.round(actual * 100) / 100.0;
        double user2dp   = Math.round(user   * 100) / 100.0;

        boolean correct  = (user2dp == actual2dp);

        summary += "\n" + numerator + "/" + divisor + "=" + String.format("%.2f", user2dp) + ":" + correct;
        if (correct) { ++score; }
    }
}
