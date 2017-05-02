package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import ch.joelniklaus.indoloc.models.SensorData;

/**
 * Reads and prepares sensor data.
 * <p>
 * Created by joelniklaus on 13.11.16.
 */
public class SensorHelper extends AbstractHelper {
    
    private SensorManager sensorManager;
    private Sensor ambientTemperatureSensor, lightSensor, pressureSensor, relativeHumiditySensor, magnetometer, accelerometer;
    private float ambientTemperature, light, pressure, relativeHumidity;
    private float[] magnetic = new float[3];
    private float[] gravity = new float[3];


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

        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            //(alert("Success: ambientTemperatureSensor");
        } else
            alert("Failure: No ambientTemperatureSensor available");

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            //(alert("Success: lightSensor");
        } else
            alert("Failure: No lightSensor available");

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            //(alert("Success: pressureSensor");
        } else
            alert("Failure: No pressureSensor available");

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            //(alert("Success: relativeHumiditySensor");
        } else
            alert("Failure: No relativeHumiditySensor available");

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
        if (!sensorManager.registerListener((SensorEventListener) context, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL))
            alert("Could not register listener for ambientTemperatureSensor");
        if (!sensorManager.registerListener((SensorEventListener) context, lightSensor, SensorManager.SENSOR_DELAY_NORMAL))
            alert("Could not register listener for lightSensor");
        if (!sensorManager.registerListener((SensorEventListener) context, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL))
            alert("Could not register listener for pressureSensor");
        if (!sensorManager.registerListener((SensorEventListener) context, relativeHumiditySensor, SensorManager.SENSOR_DELAY_NORMAL))
            alert("Could not register listener for relativeHumiditySensor");
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
    public SensorData readSensorData(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            ambientTemperature = lowPass(event.values[0], ambientTemperature);
        }
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = lowPass(event.values[0], light);
        }
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            pressure = lowPass(event.values[0], pressure);
        }
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            relativeHumidity = lowPass(event.values[0], relativeHumidity);
        }

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

        return new SensorData(ambientTemperature, light, pressure, relativeHumidity, gravity, magnetic);
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
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = lowPass(input[i], output[i]);
        }
        return output;
    }

    /**
     * Low pass filter for a given input value.
     *
     * @param input
     * @param output
     * @return
     */
    protected float lowPass(float input, float output) {
        float ALPHA = 0.25f; // if ALPHA = 1 OR 0, no filter applies.
        return output + ALPHA * (input - output);
    }
}
