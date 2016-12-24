package ch.joelniklaus.indoloc.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.commons.lang3.SerializationUtils;
import java.util.ArrayList;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.SensorHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.helpers.WifiHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.RSSData;
import ch.joelniklaus.indoloc.models.SensorData;
import weka.core.Instances;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    private TextView scanText, rss1Text, rss2Text, rss3Text, rss4Text, rss5Text, rss6Text, rss7Text, rss8Text, magneticYText, magneticZText;
    private TextView scanValue, rss1Value, rss2Value, rss3Value, rss4Value, rss5Value, rss6Value, rss7Value, rss8Value, magneticYValue, magneticZValue;
    private Button startButton;
    private EditText roomEditText;

    private ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
    private DataPoint currentDataPoint;

    private int scanNumber = 0;
    private boolean registering = false;

    private FileHelper fileHelper = new FileHelper(this);
    private SensorHelper sensorHelper = new SensorHelper(this);
    private WifiHelper wifiHelper = new WifiHelper(this);
    private WekaHelper wekaHelper = new WekaHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        sensorHelper.setUp();

        wifiHelper.setUp();

        setUpTextViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorHelper.registerListeners();

        wifiHelper.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorHelper.unRegisterListeners();

        wifiHelper.unRegisterListeners();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        DataPoint previousDataPoint = null;
        if(currentDataPoint!= null)
         previousDataPoint = SerializationUtils.clone(currentDataPoint);

        currentDataPoint = new DataPoint(roomEditText.getText().toString(), sensorHelper.readSensorData(event), wifiHelper.readWifiData(getIntent()));

        // Only collect different DataPoints
        if(!currentDataPoint.equals(previousDataPoint))
            saveDataPoint();

        setTextViewValues();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void setUpTextViews() {
        scanText = (TextView) findViewById(R.id.txtScanV);
        scanText.setText("Scan Number");
        scanValue = (TextView) findViewById(R.id.txtScan);

        rss1Text = (TextView) findViewById(R.id.txtRSS1V);
        rss1Text.setText("My Passwort is Monkey");
        rss1Value = (TextView) findViewById(R.id.txtRSS1);

        rss2Text = (TextView) findViewById(R.id.txtRSS2V);
        rss2Text.setText("Chris Breezy");
        rss2Value = (TextView) findViewById(R.id.txtRSS2);

        rss3Text = (TextView) findViewById(R.id.txtRSS3V);
        rss3Text.setText("Core-Guest");
        rss3Value = (TextView) findViewById(R.id.txtRSS3);

        rss4Text = (TextView) findViewById(R.id.txtRSS4V);
        rss4Text.setText("ADCH-Guest");
        rss4Value = (TextView) findViewById(R.id.txtRSS4);

        rss5Text = (TextView) findViewById(R.id.txtRSS5V);
        rss5Text.setText("UPC503960977");
        rss5Value = (TextView) findViewById(R.id.txtRSS5);

        rss6Text = (TextView) findViewById(R.id.txtRSS6V);
        rss6Text.setText("UPC731B685");
        rss6Value = (TextView) findViewById(R.id.txtRSS6);

        rss7Text = (TextView) findViewById(R.id.txtRSS7V);
        rss7Text.setText("UPC2058401");
        rss7Value = (TextView) findViewById(R.id.txtRSS7);

        rss8Text = (TextView) findViewById(R.id.txtRSS8V);
        rss8Text.setText("UPC248577407");
        rss8Value = (TextView) findViewById(R.id.txtRSS8);

        magneticYText = (TextView) findViewById(R.id.txtmagneticYV);
        magneticYText.setText("MagneticY");
        magneticYValue = (TextView) findViewById(R.id.txtmagneticY);

        magneticZText = (TextView) findViewById(R.id.txtmagneticZV);
        magneticZText.setText("MagneticZ");
        magneticZValue = (TextView) findViewById(R.id.txtmagneticZ);

        startButton = (Button) findViewById(R.id.btnStart);
        roomEditText = (EditText) findViewById(R.id.editRoom);
    }

    private void setTextViewValues() {
        SensorData sensorData = currentDataPoint.getSensorData();
        magneticYValue.setText(Double.toString(sensorData.getMagneticY()));
        magneticZValue.setText(Double.toString(sensorData.getMagneticZ()));

        RSSData rssData = currentDataPoint.getRssData();
        rss1Value.setText(rssData.getValues().get(0).toString());
        rss2Value.setText(rssData.getValues().get(1).toString());
        rss3Value.setText(rssData.getValues().get(2).toString());
        rss4Value.setText(rssData.getValues().get(3).toString());
        rss5Value.setText(rssData.getValues().get(4).toString());
        rss6Value.setText(rssData.getValues().get(5).toString());
        rss7Value.setText(rssData.getValues().get(6).toString());
        rss8Value.setText(rssData.getValues().get(7).toString());
    }

    public void start(View view) {
        String label = startButton.getText().toString();
        try {
            // reset scan Number
            scanNumber = 0;
            if (label.equals("START")) {
                startButton.setText("STOP");
                // Start registering
                registering = true;
            } else {
                startButton.setText("START");
                // Stop registering
                registering = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDataPoint() {
        if (registering) {
            scanValue.setText(Integer.toString(scanNumber++));
            this.dataPoints.add(currentDataPoint);
        }
    }

    public void liveTestModel(View v) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            Instances data = WekaHelper.convertToSingleInstance(test, currentDataPoint);

            wekaHelper.testForView(data);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void testModel(View v) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            wekaHelper.testForView(test);
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void trainModel(View v) {
        try {
            Instances data = fileHelper.loadArffFromExternalStorage("data.arff");

            Instances test = wekaHelper.trainForView(data);

            fileHelper.saveArffToExternalStorage(test, "test.arff");
        } catch (Exception e) {
            e.printStackTrace();
            alert(e.getMessage());
        }
    }

    public void readFile(View v) {
        String filePath = "data.arff";
        try {
            Instances instances = fileHelper.loadArffFromInternalStorage(filePath);
            Toast.makeText(this, instances.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, instances.toSummaryString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFile(View v) {
        String filePath = "data.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<DataPoint>();
        alert("Saved data points to " + filePath);
    }

    private void createArffFile(String filePath) {
        Instances data = WekaHelper.buildInstances(dataPoints);

        try {
            fileHelper.saveArffToInternalStorage(data, filePath);
            fileHelper.saveArffToExternalStorage(data, filePath);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
