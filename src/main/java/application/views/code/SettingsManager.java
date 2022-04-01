package application.views.code;

import java.io.Serializable;

public class SettingsManager implements Serializable {
    private double scoreA;
    private double scoreB;
    private double scoreC;
    private Manager manager;
    public SettingsManager(Manager manager) {
        this.manager = manager;
        scoreA = 1.05; //Urgency
        scoreB = 1; //Priority
        scoreC = 1.2; //Time
    }

    public Manager getManager() {
        return this.manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public double getScoreA() {
        return this.scoreA;
    }

    public void setScoreA(double scoreA) {
        this.scoreA = scoreA;
    }

    public double getScoreB() {
        return this.scoreB;
    }

    public void setScoreB(double scoreB) {
        this.scoreB = scoreB;
    }

    public double getScoreC() {
        return this.scoreC;
    }

    public void setScoreC(double scoreC) {
        this.scoreC = scoreC;
    }

}
