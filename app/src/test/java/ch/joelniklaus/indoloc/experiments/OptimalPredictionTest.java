package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class OptimalPredictionTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception {
        loadFiles("final_cds/train_landmark", "final_cds/test_landmark");
    }

    @Test
    public void testOptimalClassifier() throws Exception {
        train = WekaHelper.removeDuplicates(train);
        conductAccuracyExperiment(train, test, false);
    }


}