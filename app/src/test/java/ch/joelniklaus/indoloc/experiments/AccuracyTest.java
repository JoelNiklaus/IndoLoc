package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;


/**
 * Tests the accuracy of certain classifiers with train/test set and with cross validation.
 *
 * @author joelniklaus
 */
public class AccuracyTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("thesis/bern/room/train", "thesis/bern/room/test");
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