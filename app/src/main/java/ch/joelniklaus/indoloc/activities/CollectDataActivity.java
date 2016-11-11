package ch.joelniklaus.indoloc.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ch.joelniklaus.indoloc.R;
import ch.joelniklaus.indoloc.models.DataPoint;
import ch.joelniklaus.indoloc.models.SensorsValue;
import ch.joelniklaus.indoloc.services.WekaService;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

public class CollectDataActivity extends AppCompatActivity implements SensorEventListener {

    protected WekaService wekaService = new WekaService(this);
    protected ArrayList<DataPoint> dataPoints = new ArrayList<DataPoint>();

    private TextView RSS1, RSS2, RSS3, RSS4, RSS5, RSS6, RSS7, scan;
    private Button btnStart;
    private EditText editRoom;

    private WifiReceiver wifiReceiver;
    private WifiManager wifiManager;
    private ArrayList<Integer> rssList;

    private SensorManager sensorManager;
    private Sensor ambientTemperatureSensor, lightSensor, pressureSensor, relativeHumiditySensor;
    private double ambientTemperature, light, pressure, relativeHumidity;
    private SensorsValue sensorsValue = null;

    private int scanNumber = 0;
    private boolean registering;

    //WIFI broadcaster class
    public class WifiReceiver extends BroadcastReceiver {

        public void onReceive(Context c, Intent intent) {
            scan.setText(Integer.toString(scanNumber++));
            List<ScanResult> scanResults = wifiManager.getScanResults();
            rssList = new ArrayList<Integer>();

            for (int i = 0; i < scanResults.size(); i++) {
                ScanResult scanResult = scanResults.get(i);

                // search by SSID
                switch (scanResult.SSID) {
                    case "My Passwort is Monkey":
                        rssList.add(0, scanResult.level);
                        RSS1.setText(Integer.toString(scanResult.level));
                    case "ADCH-Guest":
                        rssList.add(0, scanResult.level);
                        RSS2.setText(Integer.toString(scanResult.level));
                    case "ADCH-Intern":
                        rssList.add(0, scanResult.level);
                        RSS3.setText(Integer.toString(scanResult.level));
                    case "Core-Guest":
                        rssList.add(0, scanResult.level);
                        RSS4.setText(Integer.toString(scanResult.level));
                    case "Core-Intern":
                        rssList.add(0, scanResult.level);
                        RSS5.setText(Integer.toString(scanResult.level));
                    case "UPC248577407":
                        rssList.add(0, scanResult.level);
                        RSS6.setText(Integer.toString(scanResult.level));
                    case "UPC503960977":
                        rssList.add(0, scanResult.level);
                        RSS7.setText(Integer.toString(scanResult.level));
                }
            }

            saveDataPoint();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        // WiFi
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        // Sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);


        scan = (TextView) findViewById(R.id.txtScan);
        RSS1 = (TextView) findViewById(R.id.txtRSS1);
        RSS2 = (TextView) findViewById(R.id.txtRSS2);
        RSS3 = (TextView) findViewById(R.id.txtRSS3);
        RSS4 = (TextView) findViewById(R.id.txtRSS4);
        RSS5 = (TextView) findViewById(R.id.txtRSS5);
        RSS6 = (TextView) findViewById(R.id.txtRSS6);
        RSS7 = (TextView) findViewById(R.id.txtRSS7);
        btnStart = (Button) findViewById(R.id.btnStart);

        editRoom = (EditText) findViewById(R.id.editRoom);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        sensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, relativeHumiditySensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            //take the values
            ambientTemperature = event.values[0];
        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            //take the values
            light = event.values[0];
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            //take the values
            pressure = event.values[0];
        }

        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            //take the values
            relativeHumidity = event.values[0];
        }

        sensorsValue = new SensorsValue(ambientTemperature, light, pressure, relativeHumidity);


        wifiManager.startScan();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start(View view) {

        String label = btnStart.getText().toString();
        try {
            if (label.equals("START")) {
                scanNumber = 0;
                btnStart.setText("STOP");
                // Start registering
                registering = true;
            } else {
                btnStart.setText("START");
                // Stop registering
                registering = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveDataPoint() {
        if (registering) {
            String room = editRoom.getText().toString();
            DataPoint dataPoint = new DataPoint(room, rssList, sensorsValue);
            this.dataPoints.add(dataPoint);
            alert(dataPoint.toString());
        }
    }

    public void readFile(View v) {
        String filePath = "localizationData.arff";
        try {
            Instances instances = wekaService.loadArffFromExternalStorage(filePath);
            alert(instances.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createFile(View v) {
        String filePath = "localizationData.arff";
        createArffFile(filePath);
        this.dataPoints = new ArrayList<DataPoint>();
        Toast.makeText(this, "Saved data points to " + filePath, Toast.LENGTH_LONG).show();
    }

    private void createArffFile(String filePath) {
        //                                                     rooms + number of rss  + 4 sensors
        ArrayList<Attribute> attributes = new ArrayList<Attribute>(1 + rssList.size() + 4);
        ArrayList<String> rooms = new ArrayList<String>(6);

        for (DataPoint dataPoint : dataPoints)
            if (!rooms.contains(dataPoint.getRoom()))
                rooms.add(dataPoint.getRoom());
        /*
        rooms.add(getString(R.string.corridor));
        rooms.add(getString(R.string.kitchen));
        rooms.add(getString(R.string.living_room));
        rooms.add(getString(R.string.joel_room));
        rooms.add(getString(R.string.tobias_room));
        rooms.add(getString(R.string.nicola_room));
        */

        // room
        attributes.add(new Attribute("room", rooms));

        // rss
        for (int i = 0; i < rssList.size(); i++)
            attributes.add(new Attribute("rss" + i, Attribute.NUMERIC));

        // sensors
        attributes.add(new Attribute("ambient_temperature", Attribute.NUMERIC));
        attributes.add(new Attribute("light", Attribute.NUMERIC));
        attributes.add(new Attribute("pressure", Attribute.NUMERIC));
        attributes.add(new Attribute("relative_humidity", Attribute.NUMERIC));

        Instances dataRaw = new Instances("TestInstances", attributes, dataPoints.size());

        System.out.println("Before adding any instance");
        System.out.println("--------------------------");
        System.out.println(dataRaw);
        System.out.println("--------------------------");

        double[] instanceValues = null;
        for (DataPoint dataPoint : dataPoints) {
            instanceValues = new double[dataRaw.numAttributes()];

            // room
            instanceValues[0] = rooms.indexOf(dataPoint.getRoom());

            // rss
            ArrayList<Integer> rssListTemp = dataPoint.getRssList();
            ;
            for (int i = 0; i < rssListTemp.size(); i++)
                instanceValues[1 + i] = rssListTemp.get(i);

            // sensors
            SensorsValue sensors = dataPoint.getSensors();
            instanceValues[rssListTemp.size() + 1] = sensors.getAmbientTemperature();
            instanceValues[rssListTemp.size() + 2] = sensors.getLight();
            instanceValues[rssListTemp.size() + 3] = sensors.getPressure();
            instanceValues[rssListTemp.size() + 4] = sensors.getRelativeHumidity();

            dataRaw.add(new DenseInstance(1.0, instanceValues));

            System.out.println("After instance i");
            System.out.println("--------------------------");
            System.out.println(dataRaw);
            System.out.println("--------------------------");
        }

        System.out.println("After adding all instances");
        System.out.println("--------------------------");
        System.out.println(dataRaw);
        System.out.println("--------------------------");

        try {
            wekaService.saveArffToInternalStorage(dataRaw, filePath);
            wekaService.saveArffToExternalStorage(dataRaw, filePath);
            Instances data = wekaService.loadArffFromInternalStorage(filePath);
            alert(data.toSummaryString());
            System.out.println(data.toSummaryString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    public void addDataPoint(View v) {
        dataPoints.add(new DataPoint(getRadioButtonText(), null, null));

        String output = "Data Point added:\n" +
                "Room: " + getRadioButtonText() + "\n" +
                "Barometer: ..." + "\n";

        Toast.makeText(this, output, Toast.LENGTH_SHORT).show();
    }

    private String getRadioButtonText() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonId);
        return (String) radioButton.getText();
    }
*/

    public void alert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
