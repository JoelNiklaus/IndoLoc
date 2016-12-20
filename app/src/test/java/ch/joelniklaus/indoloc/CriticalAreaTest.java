package ch.joelniklaus.indoloc;

import org.junit.Before;
import org.junit.Test;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CriticalAreaTest extends AbstractTest {

    @Before
    public void setUp() throws Exception {
        setFile("critical_area.arff");
        super.setUp();
    }

    @Test
    public void testCriticalArea() throws Exception {
        makeClassifierRatings(data);
    }

    // TODO Maybe as good because of duplicates
}