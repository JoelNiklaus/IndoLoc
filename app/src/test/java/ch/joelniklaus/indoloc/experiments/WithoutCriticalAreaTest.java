package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instances;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WithoutCriticalAreaTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("experiments/without_critical_new", "experiments/without_critical_new");
    }

    /* Attribute Indices:
     * 1 -> Class Attribute: Room
     * 2 - 3 -> Magnetic Field
     * 4 - 11 -> RSSI Values
     * 12 -> RSS Mean
     * 13 - 20 RSS Variances
     */


    /**
     * Tests if using the magnetic field values as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMagneticFieldValuesImprovement() throws Exception {
        Instances with = WekaHelper.removeDuplicates(train);
        Instances without = WekaHelper.removeAttributes(train, "2-3");

        testWithAndWithout(with, without);
    }

    /**
     * Tests if using the mean as a feature really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testMeanImprovement() throws Exception {
        Instances with = WekaHelper.removeDuplicates(train);
        Instances without = WekaHelper.removeAttributes(train, "12");

        testWithAndWithout(with, without);
    }

    /**
     * Tests if using the variances as features really improves the accuracy
     *
     * @throws Exception
     */
    @Test
    public void testVariancesImprovement() throws Exception {
        Instances with = WekaHelper.removeDuplicates(train);
        Instances without = WekaHelper.removeAttributes(train, "13-20");

        testWithAndWithout(with, without);
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