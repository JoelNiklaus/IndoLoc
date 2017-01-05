package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import ch.joelniklaus.indoloc.models.SensorData;

/**
 * Created by joelniklaus on 13.11.16.
 */

public class SensorHelper {

    private Context context;

    private SensorManager sensorManager;
    private Sensor ambientTemperatureSensor, lightSensor, pressureSensor, relativeHumiditySensor, magnetometer, accelerometer;
    private double ambientTemperature, light, pressure, relativeHumidity;
    private float[] magnetic = new float[3], gravity = new float[3];
    private double[] magneticFingerprint = new double[3];
    private final float alpha = (float) 0.8;


    public SensorHelper(Context context) {
        this.context = context;
    }

    public void setUp() {
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            //alert("Success: magnetometer");
        } else {
            alert("Failure: No magnetometer available");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            //alert("Success: accelerometer");
        } else {
            alert("Failure: No accelerometer available");
        }
    }

    public void registerListeners() {
        sensorManager.registerListener((SensorEventListener) context, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener((SensorEventListener) context, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unRegisterListeners() {
        sensorManager.unregisterListener((SensorEventListener) context);
    }

    public SensorData readSensorData(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //take the values
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //take the values
            magnetic[0] = alpha * magnetic[0] + (1 - alpha) * event.values[0];
            magnetic[1] = alpha * magnetic[1] + (1 - alpha) * event.values[1];
            magnetic[2] = alpha * magnetic[2] + (1 - alpha) * event.values[2];

            float[] R = new float[9];
            float[] I = new float[9];
            SensorManager.getRotationMatrix(R, I, gravity, magnetic);
            //float[] A_D = event.values.clone();
            //float[] A_W = new float[3];
            magneticFingerprint[0] = R[0] * magnetic[0] + R[1] * magnetic[1] + R[2] * magnetic[2];
            magneticFingerprint[1] = R[3] * magnetic[0] + R[4] * magnetic[1] + R[5] * magnetic[2];
            magneticFingerprint[2] = R[6] * magnetic[0] + R[7] * magnetic[1] + R[8] * magnetic[2];

            // round to 1 decimal place because of sensor resolution
            magneticFingerprint[0] = round(magneticFingerprint[0], 0); // x-value: should always be 0
            assert magneticFingerprint[0] == 0.0;
            magneticFingerprint[1] = round(magneticFingerprint[1], 0); // y-value
            magneticFingerprint[2] = round(magneticFingerprint[2], 0); // z-value
        }

        return new SensorData((int) magneticFingerprint[1], (int) magneticFingerprint[2]);
    }

    private void alert(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public double round(double number, int decimalPlaces) {
        double factor = Math.pow(10, decimalPlaces);
        return Math.round(factor * number) / factor;
        /*
        BigDecimal bd = new BigDecimal(number);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
        */
    }

}
