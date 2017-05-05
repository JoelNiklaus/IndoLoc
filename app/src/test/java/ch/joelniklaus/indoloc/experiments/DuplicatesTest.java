package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;

/**
 *
 */
public class DuplicatesTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception {
        loadFiles("final_cds/train_room", "final_cds/test_room");
    }

    @Test
    public void removeDuplicates() throws Exception {
        train = WekaHelper.removeDuplicates(train);
        test = WekaHelper.removeDuplicates(test);
        conductPerformanceExperiment(train, test);
    }

}