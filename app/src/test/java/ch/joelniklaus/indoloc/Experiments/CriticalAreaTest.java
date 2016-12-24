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
public class CriticalAreaTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testCriticalAreaDifferentCollections() throws Exception {
        Instances train = loadFile("experiments/critical_train");
        Instances test = loadFile("experiments/critical_test");


        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testCriticalArea() throws Exception {
        Instances data = loadFile("experiments/critical_area");

        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testCriticalAreaWithoutDuplicates() throws Exception {
        Instances data = loadFile("experiments/critical_area");

        data = wekaHelper.removeDuplicates(data);
        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    /**
     * Tests if using the magnetic field values, mean and variances as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesMeanVariancesImprovement() throws Exception {
        Instances data = loadFile("experiments/critical_area");

        Instances with = wekaHelper.removeDuplicates(data);
        Instances without = wekaHelper.removeAttributes(data, "12-20");
        without = wekaHelper.removeAttributes(without, "2-3");

        testWithAndWithout(with, without);
    }
}