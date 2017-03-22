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
public class CriticalAreaTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("experiments/critical_area", "experiments/critical_area");
    }

    @Test
    public void testCriticalAreaDifferentCollections() throws Exception {
        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testCriticalArea() throws Exception {
        Statistics statistics = getClassifierRatings(train);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testCriticalAreaWithoutDuplicates() throws Exception {
        Instances data = WekaHelper.removeDuplicates(train);
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
        Instances with = WekaHelper.removeDuplicates(train);
        Instances without = WekaHelper.removeAttributes(train, "12-20");
        without = WekaHelper.removeAttributes(without, "2-3");

        testWithAndWithout(with, without);
    }
}