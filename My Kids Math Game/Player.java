package com.mycompany.kidsgame;

public class Player {
    private String name;
    private int score;
    private String summary;
    private long timeTakenMs;

    public Player(String name, int score, String summary, long timeTakenMs) {
        this.name = name;
        this.score = score;
        this.summary = summary;
        this.timeTakenMs = timeTakenMs;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getSummary() {
        return summary;
    }

    public long getTimeTakenMs() {
        return timeTakenMs;
    }
}
