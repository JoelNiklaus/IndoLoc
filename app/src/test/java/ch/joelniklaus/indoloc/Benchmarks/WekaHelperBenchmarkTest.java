package ch.joelniklaus.indoloc.Benchmarks;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

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

    private WekaHelper wekaHelper = new WekaHelper();
    private FileHelper fileHelper = new FileHelper();

    private ArrayList<Classifier> classifiers = new ArrayList<Classifier>();
    private ArrayList<Classifier> trainedClassifiers = new ArrayList<Classifier>();

    private Instances data, train, test;
    String filePath = "/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/data.arff";


    @Before
    public void setUp() throws Exception {
        data = fileHelper.loadArff(filePath);
        RemovePercentage remove = wekaHelper.getRemovePercentage(data);

        train = wekaHelper.getTrainingSet(data, remove);

        test = wekaHelper.getTestingSet(data, remove);

        classifiers.add(new IBk());
        classifiers.add(new LibSVM());
        classifiers.add(new NaiveBayes());
        classifiers.add(new Logistic());
        classifiers.add(new Bagging());
        classifiers.add(new AdaBoostM1());

        for (int i = 0; i < classifiers.size(); i++)
            trainedClassifiers.add(i, wekaHelper.train(train, classifiers.get(i)));
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Test
    public void testAccuracy() throws Exception {
        double currentBest = 0;
        String bestClassifier = "";
        Evaluation evaluation;
        for (Classifier classifier : classifiers) {
            String classifierName = classifier.getClass().getSimpleName();
            evaluation = wekaHelper.evaluate(data, classifier);
            System.out.println("\n\n" + classifierName + "\n\n" + evaluation.toSummaryString());
            if (evaluation.pctCorrect() > currentBest) {
                currentBest = evaluation.pctCorrect();
                bestClassifier = classifierName;
            }
        }

        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + "% classified correctly\n\n");
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Test
    public void testTrainPerformance() throws Exception {
        int numberOfTrainRounds = 1000;
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (int i = 0; i < classifiers.size(); i++) {
            long testTimeSum = 0;
            Classifier classifier = classifiers.get(i);
            for (int round = 0; round < numberOfTrainRounds; round++) {
                timer.reset();
                wekaHelper.train(train, classifier);
                testTimeSum += timer.timeElapsedMicroS();
            }
            testTimeSum /= numberOfTrainRounds;
            String classifierName = classifier.getClass().getSimpleName();
            System.out.println("\n\nClassifier: " + classifierName + ": " + testTimeSum + "micros mean train time\n\n");
            if (testTimeSum < currentBest) {
                currentBest = testTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean train time\n\n");
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 1)
    @Test
    public void testTestPerformance() throws Exception {
        int numberOfTestRounds = 1000;
        long currentBest = Long.MAX_VALUE;
        String bestClassifier = "";

        for (int i = 0; i < trainedClassifiers.size(); i++) {
            long testTimeSum = 0;
            Classifier classifier = trainedClassifiers.get(i);
            for (int round = 0; round < numberOfTestRounds; round++) {
                timer.reset();
                wekaHelper.test(test, classifier);
                testTimeSum += timer.timeElapsedMicroS();
            }
            testTimeSum /= numberOfTestRounds;
            String classifierName = classifier.getClass().getSimpleName();
            System.out.println("\n\nClassifier: " + classifierName + ": " + testTimeSum + "micros mean test time\n\n");
            if (testTimeSum < currentBest) {
                currentBest = testTimeSum;
                bestClassifier = classifierName;
            }
        }
        System.out.println("\n\nBest Classifier: " + bestClassifier + ": " + currentBest + " micros mean test time\n\n");
    }
}