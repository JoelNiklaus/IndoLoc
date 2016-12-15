package ch.joelniklaus.indoloc.models;

/**
 * Created by joelniklaus on 11.11.16.
 */
public class SensorsValue {

    //private double ambientTemperature;
    //private double relativeHumidity;
    //private double light;
    //private double pressure;

    private double[] magneticFingerprint = new double[3];

    private float[] magnetic = new float[3];
    private float[] gravity = new float[3];


    public SensorsValue(float[] magnetic, float[] gravity) {
        //this.ambientTemperature = ambientTemperature;
        //this.relativeHumidity = relativeHumidity;
        //this.light = light;
        //this.pressure = pressure;
        this.magnetic = magnetic;
        this.gravity = gravity;
    }

    public float[] getMagnetic() {
        return magnetic;
    }

    public void setMagnetic(float[] magnetic) {
        this.magnetic = magnetic;
    }

    public float[] getGravity() {
        return gravity;
    }

    public void setGravity(float[] gravity) {
        this.gravity = gravity;
    }
}
