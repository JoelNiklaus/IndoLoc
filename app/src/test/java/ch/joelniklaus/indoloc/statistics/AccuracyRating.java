package ch.joelniklaus.indoloc.statistics;

import java.text.DecimalFormat;

import weka.classifiers.Evaluation;

/**
 * Data object containing the accuracy and evaluation of a classifier.
 * <p>
 * Created by joelniklaus on 26.11.16.
 */

public class AccuracyRating extends Rating {
    private Evaluation evaluation;

    public AccuracyRating(String name, Evaluation evaluation) {
        this.name = name;
        this.evaluation = evaluation;
    }

    public double getAccuracy() {
        return evaluation.pctCorrect();
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    @Override
    public String toString() {
        return "\n" + fixedLengthString(name, 25) +
                fixedLengthString("Accuracy: " + new DecimalFormat("#.##").format(getAccuracy()) + " %", 25);
    }

}
