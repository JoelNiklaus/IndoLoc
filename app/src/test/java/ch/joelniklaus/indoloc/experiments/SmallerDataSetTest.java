package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instances;


/**
 * Tests if the accuracy is improved if some parts of the datasets are left out.
 *
 * @author joelniklaus
 */
public class SmallerDataSetTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception, CouldNotLoadArffException {
        loadFiles("thesis/exeter/landmark/train", "thesis/exeter/landmark/test");
    }

    @Test
    public void testOneHalf() throws Exception {
        testOneNth(2);
    }

    @Test
    public void testOneThird() throws Exception {
        testOneNth(3);
    }

    @Test
    public void testOneFourth() throws Exception {
        testOneNth(4);
    }

    @Test
    public void testOneFifth() throws Exception {
        testOneNth(5);
    }

    @Test
    public void testOneTenth() throws Exception {
        testOneNth(10);
    }

    @Test
    public void testOneTwentieth() throws Exception {
        testOneNth(20);
    }


    private void testOneNth(int n) throws Exception {
        Instances newTrain = WekaHelper.getEveryNThInstance(train, n);
        Instances newTest = WekaHelper.getEveryNThInstance(test, n);

        conductPerformanceExperiment(newTrain, newTest, true);
        conductAccuracyExperiment(newTrain, newTest, false);
    }

}