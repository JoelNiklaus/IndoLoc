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
        loadFiles("exeter/train_small", "exeter/test_small");
    }

    @Test
    public void testOneHalf() throws Exception {
        Instances train = loadFile("exeter/train_large");
        Instances test = loadFile("exeter/test_large_middle");

        train = WekaHelper.getEveryNThInstance(train, 2);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testOneThird() throws Exception {
        Instances train = loadFile("exeter/train_large");
        Instances test = loadFile("exeter/test_large_middle");

        train = WekaHelper.getEveryNThInstance(train, 3);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testOneFourth() throws Exception {
        Instances train = loadFile("exeter/train_large");
        Instances test = loadFile("exeter/test_large_middle");

        train = WekaHelper.getEveryNThInstance(train, 4);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testOneFifth() throws Exception {
        Instances train = loadFile("exeter/train_large");
        Instances test = loadFile("exeter/test_large_middle");

        train = WekaHelper.getEveryNThInstance(train, 5);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testOneTenth() throws Exception {
        Instances train = loadFile("exeter/train_large");
        Instances test = loadFile("exeter/test_large_middle");

        train = WekaHelper.getEveryNThInstance(train, 10);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

}