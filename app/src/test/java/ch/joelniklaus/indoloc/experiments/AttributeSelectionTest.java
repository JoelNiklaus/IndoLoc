package ch.joelniklaus.indoloc.experiments;

import org.junit.Test;

import java.util.Random;

import ch.joelniklaus.indoloc.AbstractTest;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AttributeSelectionTest extends AbstractTest {

    @Override
    protected void fetchData() throws Exception {
        loadFiles("final_cds/train_room", "final_cds/test_room");
    }

    /**
     * uses the meta-classifier
     */
    @Test
    public void useClassifier() throws Exception {
        System.out.println("\n1. Meta-classfier");
        AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
        CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        J48 base = new J48();
        classifier.setClassifier(base);
        classifier.setEvaluator(eval);
        classifier.setSearch(search);
        Evaluation evaluation = new Evaluation(train);
        evaluation.crossValidateModel(classifier, train, 10, new Random(1));
        System.out.println(evaluation.toSummaryString());
    }

    /**
     * uses the filter
     */
    @Test
    public void useFilter() throws Exception {
        System.out.println("\n2. Filter");
        weka.filters.supervised.attribute.AttributeSelection filter = new weka.filters.supervised.attribute.AttributeSelection();
        CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        filter.setEvaluator(eval);
        filter.setSearch(search);
        filter.setInputFormat(train);
        Instances newData = Filter.useFilter(train, filter);
        System.out.println(newData);
    }

    /**
     * uses the low level approach
     */
    @Test
    public void useLowLevel() throws Exception {
        System.out.println("\n3. Low-level");
        AttributeSelection attsel = new AttributeSelection();
        CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        attsel.setEvaluator(eval);
        attsel.setSearch(search);
        attsel.SelectAttributes(train);
        int[] indices = attsel.selectedAttributes();
        System.out.println("selected attribute indices (starting with 0):\n" + Utils.arrayToString(indices));
    }


}