package ch.joelniklaus.indoloc.experiments;

import org.junit.Before;
import org.junit.Test;

import ch.joelniklaus.indoloc.AbstractTest;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.statistics.Statistics;
import weka.core.Instances;
import weka.filters.unsupervised.instance.RemovePercentage;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClassifierTest extends AbstractTest {

    @Test
    public void testDifferentlyCollectedTrainAndTestSetAmountOfModelData() throws Exception {
        Instances train = loadFile("cds/train");
        RemovePercentage removePercentage = WekaHelper.randomizeAndGetRemovePercentage(train);
        Instances train30Reduced = WekaHelper.getTrainingSet(train, removePercentage);
        Instances train70Reduced = WekaHelper.getTestingSet(train, removePercentage);
        Instances test = loadFile("cds/test");

        System.out.println("===== Full Dataset =====");
        sortAndPrintStatistics(getClassifierRatings(train, test));

        System.out.println("===== 30% Reduced =====");
        sortAndPrintStatistics(getClassifierRatings(train30Reduced, test));

        System.out.println("===== 70% Reduced =====");
        sortAndPrintStatistics(getClassifierRatings(train70Reduced, test));

    }

    @Test
    public void testTrainAndTestSetOfSameCollectionDifferentDivision() throws Exception {
        Instances data = loadFile("cds/train");

        data.randomize(new java.util.Random());
        Instances train = data.trainCV(2, 0);
        Instances test = data.testCV(2, 0);

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testTrainAndTestSetOfSameCollection() throws Exception {
        Instances data = loadFile("cds/train");

        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSet() throws Exception {
        Instances train = loadFile("cds/train");
        Instances test = loadFile("cds/test");

        Statistics statistics = getClassifierRatings(train, test);
        sortAndPrintStatistics(statistics);
    }

    @Test
    public void testDifferentlyCollectedTrainAndTestSetMerged() throws Exception {
        Instances train = loadFile("cds/train");
        Instances test = loadFile("cds/test");

        Instances data = merge(train, test);

        Statistics statistics = getClassifierRatings(data);
        sortAndPrintStatistics(statistics);
    }

    private Instances merge(Instances first, Instances second) throws Exception {
        if (!first.equalHeaders(second))
            throw new Exception("The two instances have different headers");

        for (int i = 0; i < second.numInstances(); i++)
            first.add(second.instance(i));
        return first;
    }
}