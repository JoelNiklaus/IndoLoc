package ch.joelniklaus.indoloc.experiments;

import org.junit.Before;
import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.statistics.Statistics;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClassifierTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testTrainAndTestSetOfSameCollection() throws Exception {
        Instances data = loadFile("data");

        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSet() throws Exception {
        Instances train = loadFile("experiments/train");
        Instances test = loadFile("experiments/test");

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSetMerged() throws Exception {
        Instances train = loadFile("experiments/train");
        Instances test = loadFile("experiments/test");

        Instances data = merge(train, test);

        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    private Instances merge(Instances first, Instances second) throws Exception {
        if(!first.equalHeaders(second))
            throw new Exception("The two instances have different headers");

        for(int i = 0; i < second.numInstances(); i++)
            first.add(second.instance(i));
        return first;
    }

}