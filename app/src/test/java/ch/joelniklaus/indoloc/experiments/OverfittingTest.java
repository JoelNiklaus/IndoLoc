package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.statistics.Statistics;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class OverfittingTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_landmarks", "exeter/test_landmarks");
    }

    @Test
    public void testRemoveDuplicates() throws Exception {
        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);

        train = WekaHelper.removeDuplicates(train);

        statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }


    @Test
    public void testOneHalf() throws Exception {
        testOneNth(train, test, 2);
    }

    @Test
    public void testOneThird() throws Exception {
        testOneNth(train, test, 3);
    }

    @Test
    public void testOneFourth() throws Exception {
        testOneNth(train, test, 4);
    }

    @Test
    public void testOneFifth() throws Exception {
        testOneNth(train, test, 5);
    }

    @Test
    public void testOneTenth() throws Exception {
        testOneNth(train, test, 10);
    }


    private void testOneNth(Instances train, Instances test, int n) throws Exception {
        System.out.println(train.numInstances() + " number of instances");
        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);

        train = WekaHelper.getEveryNThInstance(train, n);

        System.out.println(train.numInstances() + " number of instances");
        statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }
}