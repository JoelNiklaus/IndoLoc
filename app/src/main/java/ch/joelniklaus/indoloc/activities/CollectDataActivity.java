package ch.joelniklaus.indoloc.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.helpers.FileHelper;
import ch.joelniklaus.indoloc.helpers.SensorHelper;
import ch.joelniklaus.indoloc.helpers.WekaHelper;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.SensorsValue;
import weka.core.Instances;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    private TextView scanText, rss1Text, rss2Text, rss3Text, rss4Text, rss5Text, rss6Text, rss7Text, lightText, pressureText;
    private Button startButton;
    private EditText roomEditText;

    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;

    private ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();
    private ArrayList<Integer> rssList = new ArrayList<Integer>(NUMBER_OF_ACCESS_POINTS);
    private SensorsValue sensorsValue;

    private int scanNumber = 0;
    private boolean registering = false;

    public static final int NUMBER_OF_ACCESS_POINTS = 7;
    public static final int NUMBER_OF_SENSORS = 2;


    private FileHelper fileHelper = new FileHelper(this);
    private SensorHelper sensorHelper = new SensorHelper(this);
    private WekaHelper wekaHelper = new WekaHelper(this);

    //WIFI broadcaster class
    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            List<ScanResult> scanResults = wifiManager.getScanResults();
            rssList = new ArrayList<Integer>(NUMBER_OF_ACCESS_POINTS);
            for (int i = 0; i < NUMBER_OF_ACCESS_POINTS; i++)
                rssList.add(i, 1);

            // search by SSID
            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);
                switch (scanResult.SSID) {
                    case "jxx-10375": // My Passwort is Monkey
                        rssList.set(0, scanResult.level);
                        rss1Text.setText(Integer.toString(scanResult.level));
                    case "ADCH-Guest":
                        rssList.set(1, scanResult.level);
                        rss2Text.setText(Integer.toString(scanResult.level));
                    case "ADCH-Intern":
                        rssList.set(2, scanResult.level);
                        rss3Text.setText(Integer.toString(scanResult.level));
                    case "Core-Guest":
                        rssList.set(3, scanResult.level);
                        rss4Text.setText(Integer.toString(scanResult.level));
                    case "Core-Intern":
                        rssList.set(4, scanResult.level);
                        rss5Text.setText(Integer.toString(scanResult.level));
                    case "UPC248577407":
                        rssList.set(5, scanResult.level);
                        rss6Text.setText(Integer.toString(scanResult.level));
                    case "UPC503960977":
                        rssList.set(6, scanResult.level);
                        rss7Text.setText(Integer.toString(scanResult.level));
                }
            }
            saveDataPoint();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        // Sensors
        sensorHelper.setUpSensors();

        // WiFi
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        setUpTextViews();
    }

    private void setUpTextViews() {
        scanText = (TextView) findViewById(R.id.txtScan);
        rss1Text = (TextView) findViewById(R.id.txtRSS1);
        rss2Text = (TextView) findViewById(R.id.txtRSS2);
        rss3Text = (TextView) findViewById(R.id.txtRSS3);
        rss4Text = (TextView) findViewById(R.id.txtRSS4);
        rss5Text = (TextView) findViewById(R.id.txtRSS5);
        rss6Text = (TextView) findViewById(R.id.txtRSS6);
        rss7Text = (TextView) findViewById(R.id.txtRSS7);
        lightText = (TextView) findViewById(R.id.txtLight);
        pressureText = (TextView) findViewById(R.id.txtPressure);
        startButton = (Button) findViewById(R.id.btnStart);
        roomEditText = (EditText) findViewById(R.id.editRoom);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorHelper.registerListeners();

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorHelper.unRegisterListeners();

        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        sensorsValue = sensorHelper.readSensorData(event);
        pressureText.setText(Double.toString(sensorsValue.getPressure()));
        lightText.setText(Double.toString(sensorsValue.getLight()));

        wifiManager.startScan();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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
            scanText.setText(Integer.toString(scanNumber++));
            DataPoint dataPoint = registerDataPoint();
            this.dataPoints.add(dataPoint);
        }
    }

    @NonNull
    private DataPoint registerDataPoint() {
        String room = roomEditText.getText().toString();
        return new DataPoint(room, rssList, sensorsValue);
    }

    public void liveTestModel(View v) {
        try {
            Instances test = fileHelper.loadArffFromExternalStorage("test.arff");

            Instances data = WekaHelper.convertToSingleInstance(test, registerDataPoint());
            data.setClassIndex(0);

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
            Instances data = fileHelper.loadArffFromExternalStorage("rickenbach.arff");

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
