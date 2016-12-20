package ch.joelniklaus.indoloc;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.joelniklaus.indoloc.helpers.ClassifierRating;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;

import static org.junit.Assert.assertTrue;


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

    // TODO implement these experiments
    /* Attribute Indices:
     * 1 -> Class Attribute: Room
     * 2 - 3 -> Magnetic Field
     * 4 - 11 -> RSSI Values
     * 12 -> RSS Mean
     * 13 - 20 RSS Variances
     */


    /**
     * Tests if using the magnetic field values as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesImprovement() throws Exception {
        Instances with = data;
        Instances without = wekaHelper.removeAttributes(data, "2-3");

        // With
        ArrayList<ClassifierRating> ratingsWith = makeClassifierRatings(with);

        // Without
        ArrayList<ClassifierRating> ratingsWithout = makeClassifierRatings(without);

    }

    /**
     * Tests if using the mean as a feature really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMeanImprovement() throws Exception {
        Instances with = data;
        Instances without = wekaHelper.removeAttributes(data, "12");

        // With
        ArrayList<ClassifierRating> ratingsWith = makeClassifierRatings(with);

        // Without
        ArrayList<ClassifierRating> ratingsWithout = makeClassifierRatings(without);

        ClassifierRating bestWith = ratingsWith.get(0);
        ClassifierRating bestWithout = ratingsWithout.get(0);
        assertTrue(bestWith.getMeanAccuracy() > bestWithout.getMeanAccuracy());
        assertTrue(bestWith.getName().equals(bestWithout.getName()));
    }

    /**
     * Tests if using the variances as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testVariancesImprovement() throws Exception {
        Instances with = data;
        Instances without = wekaHelper.removeAttributes(data, "13-20");

        // With
        ArrayList<ClassifierRating> ratingsWith = makeClassifierRatings(with);

        // Without
        ArrayList<ClassifierRating> ratingsWithout = makeClassifierRatings(without);
    }

    @Test
    public void testAllClassifiers() throws Exception {
        makeClassifierRatings(data);
    }

    private ArrayList<ClassifierRating> makeClassifierRatings(Instances data) throws Exception {
        ArrayList<ClassifierRating> classifierRatings = testAllClassifiers(data);

        classifierRatings = sortClassifierRatings(classifierRatings);

        // Display Statistics
        for (ClassifierRating classifierRating : classifierRatings)
            System.out.println(classifierRating);

        return classifierRatings;
    }

    private ArrayList<ClassifierRating> testAllClassifiers(Instances data) throws Exception {
        ArrayList<ClassifierRating> classifierRatings = new ArrayList<>();
        for (Classifier classifier : classifiers) {
            ClassifierRating classifierRating = testClassifier(classifier, data);
            classifierRatings.add(classifierRating);
        }
        return classifierRatings;
    }

    @NonNull
    private ClassifierRating testClassifier(Classifier classifier, Instances data) throws Exception {
        double correctPctSum = 0;
        long trainTimeSum = 0;
        long testTimeSum = 0;
        for (int round = 0; round < NUMBER_OF_TEST_ROUNDS; round++) {
            // Generate new Training and Testing set
            RemovePercentage remove = wekaHelper.getRemovePercentage(data);
            Instances train = wekaHelper.getTrainingSet(data, remove);
            Instances test = wekaHelper.getTestingSet(data, remove);

            // Training
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

        return new ClassifierRating(classifier.getClass().getSimpleName(), meanAccuracy, meanTestTime, meanTrainTime);
    }

    private ArrayList<ClassifierRating> sortClassifierRatings(ArrayList<ClassifierRating> classifierRatings) {
        // Sort by Accuracy
        // Only possible in Java 8
        //classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanAccuracy));
        Collections.sort(classifierRatings, new Comparator<ClassifierRating>() {
            public int compare(ClassifierRating o1, ClassifierRating o2) {
                if (o1.getMeanAccuracy() == o2.getMeanAccuracy())
                    return 0;
                return o1.getMeanAccuracy() > o2.getMeanAccuracy() ? -1 : 1;
            }
        });
        //Collections.reverse(classifierRatings);
        return classifierRatings;
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