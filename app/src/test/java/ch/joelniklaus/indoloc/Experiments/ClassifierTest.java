package ch.joelniklaus.indoloc.Experiments;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.ClassifierRating;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClassifierTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        setFile("data.arff");
        super.setUp();
    }

    @Test
    public void testAllClassifiers() throws Exception {
        ArrayList<ClassifierRating> classifierRatings = getClassifierRatings(data);
        sortAndPrintClassifierRatings(classifierRatings);

        for (double[] row : classifierRatings.get(6).getEvaluation().confusionMatrix())
            System.out.println(Arrays.toString(row));
    }

    @Test
    public void testAccuracy() throws Exception {
        double currentBest = 0;
        String bestClassifier = "";
        Evaluation evaluation = null;
        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            double correctPctSum = getCorrectPctSum(classifier, data);

            System.out.println("\n\nLast evaluation: " + classifierName + "\n\n" + wekaHelper.evaluate(data, classifier).toSummaryString());
            if (correctPctSum > currentBest) {
                currentBest = correctPctSum;
                bestClassifier = classifierName;
            }
        }

        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + "% mean correct classification\n\n");
    }

    private double getCorrectPctSum(Classifier classifier, Instances data) throws Exception {
        double correctPctSum = 0;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++)
            correctPctSum += wekaHelper.evaluate(data, classifier).pctCorrect();

        return correctPctSum / NUMBER_OF_TEST_ROUNDS;
    }

    @Test
    public void testTrainPerformance() throws Exception {
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long trainTimeSum = getMeanTrainTime(classifier);

            System.out.println("\n\nClassifier: " + classifierName + ": " + trainTimeSum + " micros mean train time\n\n");
            if (trainTimeSum < currentBest) {
                currentBest = trainTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean train time\n\n");
    }

    private long getMeanTrainTime(Classifier classifier) throws Exception {
        long trainTimeSum = 0;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            timer.reset();
            wekaHelper.train(train, classifier);
            trainTimeSum += timer.timeElapsedMicroS();
        }
        return trainTimeSum / NUMBER_OF_TEST_ROUNDS;
    }

    @Test
    public void testTestPerformance() throws Exception {
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long testTimeSum = getMeanTestTime(classifier);

            System.out.println("\n\nClassifier: " + classifierName + ": " + testTimeSum + " micros mean test time\n\n");
            if (testTimeSum < currentBest) {
                currentBest = testTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean test time\n\n");
    }

    private long getMeanTestTime(Classifier classifier) throws Exception {
        long testTimeSum = 0;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            timer.reset();
            wekaHelper.test(test, classifier);
            testTimeSum += timer.timeElapsedMicroS();
        }
        return testTimeSum / NUMBER_OF_TEST_ROUNDS;
    }
}