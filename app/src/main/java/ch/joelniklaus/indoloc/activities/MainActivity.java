package ch.joelniklaus.indoloc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {

    private FileHelper fileHelper = new FileHelper(this);
    private WekaHelper wekaHelper = new WekaHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToCollectData(View v) {
        startActivity(new Intent(MainActivity.this, CollectDataActivity.class));
    }

    public void liveTestModel(View v) {

    }

    public void testModel(View v) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            wekaHelper.test(test);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void trainModel(View v) {
        try {
            Instances data = fileHelper.loadArffFromAssets("data.arff");

            Instances test = wekaHelper.train(data);

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

