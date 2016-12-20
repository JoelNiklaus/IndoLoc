package ch.joelniklaus.indoloc;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;

import ch.joelniklaus.indoloc.helpers.ClassifierRating;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClassifierTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testMeanImprovement() throws Exception {

    }

    @Test
    public void testVariancesImprovement() throws Exception {

    }

    @Test
    public void testMagneticFieldValuesImprovement() throws Exception {

    }

    @Test
    public void testAllClassifiers() throws Exception {
        for (Classifier classifier : classifiers) {
            double correctPctSum = 0;
            long trainTimeSum = 0;
            long testTimeSum = 0;
            for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
                //Training
                timer.reset();
                classifier = wekaHelper.train(train, classifier);
                // mean training time per instance
                trainTimeSum += timer.timeElapsedMicroS() / train.numInstances();

                // Testing
                timer.reset();
                wekaHelper.test(test, classifier);
                // mean testing time per instance
                testTimeSum += timer.timeElapsedMicroS() / test.numInstances();

                // Evaluation
                correctPctSum += wekaHelper.evaluate(data, classifier).pctCorrect();
            }
            double meanTrainTime = trainTimeSum / NUMBER_OF_TEST_ROUNDS;
            double meanTestTime = testTimeSum / NUMBER_OF_TEST_ROUNDS;
            double meanAccuracy = correctPctSum / NUMBER_OF_TEST_ROUNDS;
            classifierRatings.add(new ClassifierRating(classifier.getClass().getSimpleName(), meanAccuracy, meanTestTime, meanTrainTime));
        }
        sortClassifierRatings();

        // Display Statistics
        for (ClassifierRating classifierRating : classifierRatings)
            System.out.println(classifierRating);
    }

    private void sortClassifierRatings() {
        // Sort by Accuracy
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanAccuracy));
        Collections.sort(classifierRatings, new Comparator<ClassifierRating>() {
            public int compare(ClassifierRating o1, ClassifierRating o2) {
                if (o1.getMeanAccuracy() == o2.getMeanAccuracy())
                    return 0;
                return o1.getMeanAccuracy() < o2.getMeanAccuracy() ? -1 : 1;
            }
        });
        Collections.reverse(classifierRatings);
    }

    @Test
    public void testAccuracy() throws Exception {
        double currentBest = 0;
        String bestClassifier = "";
        Evaluation evaluation = null;
        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            double correctPctSum = getCorrectPctSum(classifier);

            System.out.println("\n\nLast evaluation: " + classifierName + "\n\n" + wekaHelper.evaluate(data, classifier).toSummaryString());
            if (correctPctSum > currentBest) {
                currentBest = correctPctSum;
                bestClassifier = classifierName;
            }
        }

        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + "% mean correct classification\n\n");
    }

    private double getCorrectPctSum(Classifier classifier) throws Exception {
        Evaluation evaluation;
        double correctPctSum = 0;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            evaluation = wekaHelper.evaluate(data, classifier);
            correctPctSum += evaluation.pctCorrect();
        }
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