package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;


/**
 * Measures the training and testing performance of the chosen classifiers.
 *
 * @author joelniklaus
 */
public class PerformanceTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("final_cds/train_room", "final_cds/test_room");
    }

    @Test
    public void testPerformance() throws Exception {
        conductPerformanceExperiment(train, test);
    }



}