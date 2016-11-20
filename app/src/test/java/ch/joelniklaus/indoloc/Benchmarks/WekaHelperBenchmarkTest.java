package ch.joelniklaus.indoloc.Benchmarks;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;

import org.junit.Before;
import org.junit.Test;

import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WekaHelperBenchmarkTest extends AbstractBenchmark {

    private WekaHelper wekaHelper = new WekaHelper();
    private FileHelper fileHelper = new FileHelper();

    private Instances data;


    @Before
    public void setUp() throws Exception {
        data = fileHelper.loadArff("/Users/joelniklaus/Google Drive/Studium/Bachelor/Informatik/Bachelorarbeit/Code/IndoLoc/app/src/main/assets/data.arff");
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @BenchmarkOptions(benchmarkRounds = 20, warmupRounds = 5)
    @Test
    public void testEvaluate() throws Exception {
        Evaluation evaluation = wekaHelper.evaluate(data, new IBk());
    }

    public void testTrainPerformance() throws  Exception {

    }

    public void testTestPerformance() throws  Exception {

    }
}