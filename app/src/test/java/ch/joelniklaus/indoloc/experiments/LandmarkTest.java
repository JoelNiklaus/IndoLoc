package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import java.util.Arrays;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.statistics.Statistics;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.Dagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LandmarkTest extends AbstractTest {
    public static final String UNDEFINED = "undefined";
    public static final double BORDER = 0.5;

    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_landmarks", "exeter/test_landmarks");
    }

    @Test
    public void testPredictNormally() throws Exception {
        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);

        WekaHelper.removeAllOfSpecificClass(train, 4);
        WekaHelper.removeAllOfSpecificClass(test, 4);

        statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testPredictOnlyWithHighConfidence() throws Exception {
        LogitBoost logitBoost = new LogitBoost();
        logitBoost.buildClassifier(train);

        RandomForest randomForest = new RandomForest();
        randomForest.buildClassifier(train);

        IBk ibk = new IBk();
        ibk.buildClassifier(train);

        Dagging dagging = new Dagging();
        dagging.buildClassifier(train);



        System.out.println(relevantClassesAccuracy(test, randomForest));
    }

    /**
     * Calculates the accuracy of only the relevant classes. So the undefined area is not so important.
     *
     * @param test
     * @param classifier
     * @return
     * @throws Exception
     */
    private double relevantClassesAccuracy(Instances test, Classifier classifier) throws Exception {
        int relevantClasses = 0;
        int correctlyClassified = 0;
        for (Instance instance : test) {
            String actual = test.classAttribute().value((int) instance.classValue());
            String predicted = predictOnlyHighConfidence(test, BORDER, classifier, instance);

            if (!predicted.equals(UNDEFINED)) {
                relevantClasses++;
                if (actual.equals(predicted))
                    correctlyClassified++;
            }
        }
        return (double) correctlyClassified / (double) relevantClasses;
    }

    /**
     * Only classifies the instance if the confidence is above a certain border level
     *
     * @param test
     * @param border
     * @param classifier
     * @param instance
     * @throws Exception
     */
    private String predictOnlyHighConfidence(Instances test, double border, Classifier classifier, Instance instance) throws Exception {
        String actual = test.classAttribute().value((int) instance.classValue());

        String predicted = "";
        double[] confidences = classifier.distributionForInstance(instance);
        Arrays.sort(confidences);
        double confidence = confidences[confidences.length - 1];
        if (confidence > border)
            predicted = test.classAttribute().value((int) classifier.classifyInstance(instance));
        else
            predicted = UNDEFINED;

        System.out.println("Actual: " + actual + ", Predicted: " + predicted + ", Confidence: " + confidence);

        return predicted;
    }


}