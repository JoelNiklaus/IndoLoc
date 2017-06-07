package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.helpers.WekaHelper;


/**
 * Tests the optimally tailored dataset with its best classifier to achieve maximal accuracy.
 *
 * @author joelniklaus
 */
public class OptimalPredictionTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("final_cds/train_landmark", "final_cds/test_landmark");
    }

    @Test
    public void testOptimalClassifier() throws Exception {
        train = WekaHelper.removeDuplicates(train);
        conductAccuracyExperiment(train, test, false);
    }


}