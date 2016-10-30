package ch.joelniklaus.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class MainActivity extends AppCompatActivity {

    // Weka version = 3.7.3

    private String testTrain() {
        try {
            // Train and test set
            Instances train = loadArff("weather_train.arff");   // from internal storage
            Instances test = loadArff("weather_train.arff");    // from internal storage
            // train classifier
            Classifier cls = new J48();
            cls.buildClassifier(train);
            // evaluate classifier and print some statistics
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(cls, test);
            System.out.println(eval.toSummaryString("\nResults\n======\n", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    private String testWeka() {
        try {
            // Load
            Instances data = loadArffFromAssets("weather.arff");

            // Filtering
            String[] options = new String[2];
            options[0] = "-R";                                    // "range"
            options[1] = "1";                                     // first attribute
            Remove remove = new Remove();                         // new instance of filter
            remove.setOptions(options);                           // set options
            remove.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
            Instances newData = Filter.useFilter(data, remove);   // apply filter

            // Save
            saveArff(data, "weather_new.arff");

            newData = loadArff("weather_new.arff");


            // Classifier
            options = new String[1];
            options[0] = "-U";            // unpruned tree
            J48 tree = new J48();         // new instance of tree
            tree.setOptions(options);     // set the options
            tree.buildClassifier(data);   // build classifier

            // Evaluation
            Evaluation eval = new Evaluation(newData);
            eval.crossValidateModel(tree, data, 10, new Random(1));


            return newData.toSummaryString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "fail";
    }

    // save Arff to internal storage
    private void saveArff(Instances data, String filePath) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(getBaseContext().getFilesDir(), filePath)); // save to internal storage
        saver.writeBatch();
    }

    // Load Arff from internal storage
    private Instances loadArff(String filePath) throws Exception {
        DataSource source = new DataSource(getBaseContext().getFilesDir()+"/"+filePath);
        return source.getDataSet();
    }

    private Instances loadArffFromAssets(String filePath) throws Exception {
        DataSource source = new DataSource(getAssets().open(filePath));
        return source.getDataSet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonTap(View v) {
        Toast myToast = Toast.makeText(getApplicationContext(), testWeka(), Toast.LENGTH_LONG);
        myToast.show();
    }
}
