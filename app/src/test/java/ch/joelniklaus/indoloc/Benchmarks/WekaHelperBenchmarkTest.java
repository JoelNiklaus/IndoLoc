package ch.joelniklaus.indoloc.Benchmarks;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ch.joelniklaus.indoloc.LibSVM;
import ch.joelniklaus.indoloc.helpers.FileHelper;
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

    private WekaHelper wekaHelper = new WekaHelper();
    private FileHelper fileHelper = new FileHelper();

    private ArrayList<Classifier> classifiers = new ArrayList<Classifier>();

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
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
    @Test
    public void testEvaluate() throws Exception {
        Evaluation evaluation;
        for (Classifier classifier : classifiers) {
            evaluation = wekaHelper.evaluate(data, classifier);
            System.out.println("\n\n" + classifier.toString() + "\n\n" + evaluation.toSummaryString());
        }
    }

    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
    @Test
    public void testTrainPerformance() throws Exception {
        wekaHelper.train(train, classifiers.get(0));
    }

    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
    @Test
    public void testTestPerformance() throws Exception {
        wekaHelper.test(test, classifiers.get(0));
    }
}