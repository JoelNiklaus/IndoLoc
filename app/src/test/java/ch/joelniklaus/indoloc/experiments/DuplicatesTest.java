package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.helpers.WekaHelper;

/**
 * Tests if there is an increase in the accuracy if duplicate datapoints are removed.
 *
 * @author joelniklaus
 */
public class DuplicatesTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("thesis/exeter/landmark/train", "thesis/exeter/landmark/test");
    }

    @Test
    public void removeDuplicates() throws Exception {
        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);
        conductPerformanceExperiment(train, test);
    }

}