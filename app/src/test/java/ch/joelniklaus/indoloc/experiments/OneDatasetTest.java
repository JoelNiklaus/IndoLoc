package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.exceptions.DifferentHeaderException;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;


/**
 * Tests the optimally tailored dataset with its best classifier to achieve maximal accuracy.
 *
 * @author joelniklaus
 */
public class OneDatasetTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("thesis/exeter/landmark/train", "thesis/exeter/landmark/test");
    }

    @Test
    public void testMergeInstances() throws Exception, DifferentHeaderException {
        conductPerformanceExperiment(train, test, true);

        Instances data = WekaHelper.mergeInstances(train, test);

        RemovePercentage removePercentage = WekaHelper.randomizeAndGetRemovePercentage(data);
        train = WekaHelper.getTrainingSet(data, removePercentage);
        test = WekaHelper.getTestingSet(data, removePercentage);

        conductPerformanceExperiment(train, test, true);
        conductAccuracyExperiment(train, test, false);
    }


}