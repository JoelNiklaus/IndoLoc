package ch.joelniklaus.indoloc.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import ch.joelniklaus.indoloc.models.SensorsValue;

/**
 * Created by joelniklaus on 13.11.16.
 */

public class SensorHelper {

    private Context context;

    private SensorManager sensorManager;
    private Sensor ambientTemperatureSensor, lightSensor, pressureSensor, relativeHumiditySensor;
    private double ambientTemperature, light, pressure, relativeHumidity;

    public SensorHelper(Context context) {
        this.context = context;
    }

    public void setUpSensors() {
        sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

/*
        if (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            alert("Success: ambient temperature");
        }
        else {
            alert("Failure: ambient temperature");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null){
            relativeHumiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            alert("Success: relative humidity");
        }
        else {
            alert("Failure: reltive humidity");
        }
        */

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            alert("Success: light");
        } else {
            alert("Failure: light");
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            alert("Success: pressure");
        } else {
            alert("Failure: pressure");
        }
    }

    public void registerListeners() {
        //sensorManager.registerListener(this, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_UI);
        //sensorManager.registerListener(this, relativeHumiditySensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener((SensorEventListener) context, lightSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener((SensorEventListener) context, pressureSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void unRegisterListeners() {
        sensorManager.unregisterListener((SensorEventListener) context);
    }

    public SensorsValue readSensorData(SensorEvent event) {
                /*
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            //take the values
            ambientTemperature = event.values[0];
        }

         if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            //take the values
            relativeHumidity = event.values[0];
        }
        */

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            //take the values
            light = event.values[0];

        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            //take the values
            pressure = event.values[0];

        }

        return new SensorsValue(light, pressure);
    }

    public void alert(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
