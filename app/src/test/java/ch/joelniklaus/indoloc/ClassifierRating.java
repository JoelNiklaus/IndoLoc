package ch.joelniklaus.indoloc;

import java.text.DecimalFormat;

/**
 * Created by joelniklaus on 26.11.16.
 */

public class ClassifierRating {
    private String name;
    private double meanTrainTime;
    private double meanTestTime;
    private double meanAccuracy;

    public ClassifierRating(String name,double meanAccuracy, double meanTestTime, double meanTrainTime) {
        this.name = name;
        this.meanAccuracy = meanAccuracy;
        this.meanTestTime = meanTestTime;
        this.meanTrainTime = meanTrainTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMeanTrainTime() {
        return meanTrainTime;
    }

    public void setMeanTrainTime(double meanTrainTime) {
        this.meanTrainTime = meanTrainTime;
    }

    public double getMeanTestTime() {
        return meanTestTime;
    }

    public void setMeanTestTime(double meanTestTime) {
        this.meanTestTime = meanTestTime;
    }

    public double getMeanAccuracy() {
        return meanAccuracy;
    }

    public void setMeanAccuracy(double meanAccuracy) {
        this.meanAccuracy = meanAccuracy;
    }

    @Override
    public String toString() {
        return "\n"+name + ": " + new DecimalFormat("#.##").format(meanAccuracy) + " % Accuracy, " + meanTestTime +" micros Test Time, " + meanTrainTime + " micros Train Time";
    }
}
