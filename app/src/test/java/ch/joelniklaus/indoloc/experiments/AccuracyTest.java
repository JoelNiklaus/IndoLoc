package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AccuracyTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception {
        loadFiles("final_cds/train_landmark", "final_cds/test_landmark");
    }

    @Test
    public void testAccuracyTrainTest() throws Exception {
        conductAccuracyExperiment(train, test, false);
    }

    @Test
    public void testAccuracyCrossValidation() throws Exception {
        conductAccuracyExperiment(train, test, true);
    }



}