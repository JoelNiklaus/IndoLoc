package ch.joelniklaus.indoloc.Benchmarks;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ch.joelniklaus.indoloc.ClassifierRating;
import ch.joelniklaus.indoloc.LibSVM;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.Timer;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperBenchmarkTest extends AbstractBenchmark {

    private Timer timer = new Timer();

    private int numberOfTestRounds = 50;

    private WekaHelper wekaHelper = new WekaHelper();
    private FileHelper fileHelper = new FileHelper();

    private ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
    private ArrayList<Classifier> trainedClassifiers = new ArrayList<Classifier>();

    private ArrayList<ClassifierRating> classifierRatings = new ArrayList<ClassifierRating>();

    private Instances data, train, test;
    String filePath = "/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/data.arff";


    @Before
    public void setUp() throws Exception {
        data = fileHelper.loadArff(filePath);
        RemovePercentage remove = wekaHelper.getRemovePercentage(data);

        train = wekaHelper.getTrainingSet(data, remove);

        test = wekaHelper.getTestingSet(data, remove);

        // Support Vector Machine
        classifiers.add(new LibSVM());

        // K nearest neighbour
        classifiers.add(new IBk());

        // Naive Bayes
        classifiers.add(new NaiveBayes());

        // Logistic Regression
        classifiers.add(new Logistic());

        // Random Forest
        classifiers.add(new RandomForest());

        // Ensemble methods
        classifiers.add(new LogitBoost());
        classifiers.add(new Bagging());
        classifiers.add(new AdaBoostM1());

        for (int i = 0; i < classifiers.size(); i++)
            trainedClassifiers.add(i, wekaHelper.train(train, classifiers.get(i)));
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
    @Test
    public void testEverything() throws Exception {
        for (Classifier classifier : classifiers) {
            double correctPctSum = 0;
            long trainTimeSum = 0;
            long testTimeSum = 0;
            for (int round = 0; round < numberOfTestRounds; round++) {
                //Training
                timer.reset();
                classifier = wekaHelper.train(train, classifier);
                trainTimeSum += timer.timeElapsedMicroS();

                // Testing
                timer.reset();
                wekaHelper.test(test, classifier);
                testTimeSum += timer.timeElapsedMicroS();

                // Evaluation
                correctPctSum += wekaHelper.evaluate(data, classifier).pctCorrect();
            }
            double meanTrainTime = trainTimeSum / numberOfTestRounds;
            double meanTestTime = testTimeSum / numberOfTestRounds;
            double meanAccuracy = correctPctSum / numberOfTestRounds;
            classifierRatings.add(new ClassifierRating(classifier.getClass().getSimpleName(),meanAccuracy, meanTestTime, meanTrainTime));
        }
        // Sort by Accuracy
        classifierRatings.sort(Comparator.comparing(ClassifierRating::getMeanAccuracy));
        Collections.reverse(classifierRatings);
        // Display Statistics
        for (ClassifierRating classifierRating : classifierRatings)
            System.out.println(classifierRating);
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Test
    public void testAccuracy() throws Exception {
        double currentBest = 0;
        String bestClassifier = "";
        Evaluation evaluation = null;
        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long correctPctSum = 0;
            for (int round = 0; round < numberOfTestRounds; round++) {
                evaluation = wekaHelper.evaluate(data, classifier);
                correctPctSum += evaluation.pctCorrect();
            }
            correctPctSum /= numberOfTestRounds;
            System.out.println("\n\nLast evaluation: " + classifierName + "\n\n" + evaluation.toSummaryString());
            if (correctPctSum > currentBest) {
                currentBest = correctPctSum;
                bestClassifier = classifierName;
            }
        }

        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + "% mean correct classification\n\n");
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
    @Test
    public void testTrainPerformance() throws Exception {
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long trainTimeSum = 0;
            for (int round = 0; round < numberOfTestRounds; round++) {
                timer.reset();
                wekaHelper.train(train, classifier);
                trainTimeSum += timer.timeElapsedMicroS();
            }
            trainTimeSum /= numberOfTestRounds;

            System.out.println("\n\nClassifier: " + classifierName + ": " + trainTimeSum + " micros mean train time\n\n");
            if (trainTimeSum < currentBest) {
                currentBest = trainTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean train time\n\n");
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Test
    public void testTestPerformance() throws Exception {
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            long testTimeSum = 0;
            for (int round = 0; round < numberOfTestRounds; round++) {
                timer.reset();
                wekaHelper.test(test, classifier);
                testTimeSum += timer.timeElapsedMicroS();
            }
            testTimeSum /= numberOfTestRounds;
            System.out.println("\n\nClassifier: " + classifierName + ": " + testTimeSum + " micros mean test time\n\n");
            if (testTimeSum < currentBest) {
                currentBest = testTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean test time\n\n");
    }
}