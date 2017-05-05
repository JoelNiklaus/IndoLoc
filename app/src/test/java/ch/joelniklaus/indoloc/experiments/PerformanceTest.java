package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PerformanceTest extends AbstractTest {


    @Override
    protected void fetchData() throws Exception {
        loadFiles("exeter/train_landmarks", "exeter/test_landmarks");
    }

    @Test
    public void testPerformance() throws Exception {
        conductPerformanceExperiment(train, test);
    }



}