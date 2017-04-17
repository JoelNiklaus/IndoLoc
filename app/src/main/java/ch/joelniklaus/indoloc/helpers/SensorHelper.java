package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Reads and prepares sensor data.
 * <p>
 * Created by joelniklaus on 13.11.16.
 */
public class SensorHelper extends AbstractHelper {

    private SensorManager sensorManager;
    private Sensor ambientTemperatureSensor, lightSensor, pressureSensor, relativeHumiditySensor, magnetometer, accelerometer;
    private double ambientTemperature, light, pressure, relativeHumidity;
    private float[] magnetic = new float[3];
    private float[] gravity = new float[3];
    private final double[] magneticFingerprint = new double[3];


    /**
     * Constructs the SensorHelper using the Super-Constructor
     *
     * @param context
     */
    public SensorHelper(Context context) {
        super(context);
    }

    /**
     * Sets up the sensor manager and assigns the fields. Needs to be called before any reading of the sensors can happen.
     */
    public void setUp() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            //(alert("Success: magnetometer");
        } else
            alert("Failure: No magnetometer available");

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            //alert("Success: accelerometer");
        } else
            alert("Failure: No accelerometer available");
    }

    /**
     * Registers the listeners of the sensors and sets the delay time. Must be called before reading sensor data.
     */
    public void registerListeners() {
        if (!sensorManager.registerListener((SensorEventListener) context, magnetometer, SensorManager.SENSOR_DELAY_NORMAL))
            alert("Could not register listener for magnetometer");
        if (!sensorManager.registerListener((SensorEventListener) context, accelerometer, SensorManager.SENSOR_DELAY_NORMAL))
            alert("Could not register listener for accelerometer");
    }

    /**
     * Unregisters the listeners of the sensors. Should be called after reading sensor data.
     */
    public void unRegisterListeners() {
        sensorManager.unregisterListener((SensorEventListener) context);
    }

    /**
     * Reads the sensor data of the registered sensors. After reading passes the data through low pass filter to reduce noise.
     * Using gravity and magnetic field values computes y and z values of the magnetic field
     * normalized to the earth coordinate system rather than the device coordinate system, using the rotation matrix.
     *
     * @param event
     * @return
     */
    public int[] readSensorData(SensorEvent event) {
        // used for low-pass-filter
        //float alpha = (float) 0.8;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //take the values
            gravity = lowPass(event.values, gravity);
            //gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            //gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            //gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //take the values
            magnetic = lowPass(event.values, magnetic);
            //magnetic[0] = alpha * magnetic[0] + (1 - alpha) * event.values[0];
            //magnetic[1] = alpha * magnetic[1] + (1 - alpha) * event.values[1];
            //magnetic[2] = alpha * magnetic[2] + (1 - alpha) * event.values[2];
        }

        float[] R = new float[9];
        float[] I = new float[9];
        SensorManager.getRotationMatrix(R, I, gravity, magnetic);
        magneticFingerprint[0] = R[0] * magnetic[0] + R[1] * magnetic[1] + R[2] * magnetic[2];
        magneticFingerprint[1] = R[3] * magnetic[0] + R[4] * magnetic[1] + R[5] * magnetic[2];
        magneticFingerprint[2] = R[6] * magnetic[0] + R[7] * magnetic[1] + R[8] * magnetic[2];

        // round to 0 decimal place because of sensor resolution
        magneticFingerprint[0] = round(magneticFingerprint[0], 0); // x-value: should always be 0
        assertion(magneticFingerprint[0] == 0.0);
        magneticFingerprint[1] = round(magneticFingerprint[1], 0); // y-value
        magneticFingerprint[2] = round(magneticFingerprint[2], 0); // z-value

        int[] magneticField = {(int) magneticFingerprint[1], (int) magneticFingerprint[2]};
        return magneticField;
    }

    /**
     * Low pass filter for a given input array of sensor data. Is used to smooth the input data and
     * reduce noise, so that the data points are more stable.
     * https://www.built.io/blog/applying-low-pass-filter-to-android-sensor-s-readings
     *
     * @param input
     * @param output
     * @return
     */
    protected float[] lowPass(float[] input, float[] output) {
        float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    // TODO only round to 0.2
    // TODO sehen ob genauigkeit vom sensor ausgelesen werden kann.

    /**
     * Rounds the given number to a given number of decimal places.
     *
     * @param number
     * @param decimalPlaces
     * @return
     */
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
