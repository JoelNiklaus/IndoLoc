package ch.joelniklaus.indoloc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.exceptions.CouldNotLoadArffException;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {

    private final FileHelper fileHelper = new FileHelper(this);
    private final WekaHelper wekaHelper = new WekaHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToCollectData(View view) {
        startActivity(new Intent(MainActivity.this, CollectDataActivity.class));
    }

    public void evaluateModel(View view) {
        try {
            Instances data = fileHelper.loadArffFromAssets("eigerstrasse/data.arff");

            wekaHelper.evaluateForView(data);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void testModel(View view) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            wekaHelper.testForView(test);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        } catch (CouldNotLoadArffException e) {
            e.printStackTrace();
        }
    }

    public void trainModel(View view) {
        try {
            Instances data = fileHelper.loadArffFromAssets("eigerstrasse/data.arff");

            Instances test = wekaHelper.trainForView(data);

            fileHelper.saveArffToExternalStorage(test, "test.arff");
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

