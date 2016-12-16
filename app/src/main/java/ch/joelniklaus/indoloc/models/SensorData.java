package ch.joelniklaus.indoloc.models;

/**
 * Created by joelniklaus on 11.11.16.
 */
public class SensorData {

    //private double ambientTemperature;
    //private double relativeHumidity;
    //private double light;
    //private double pressure;
    private float magneticY, magneticZ;


    public SensorData(float magneticY, float magneticZ) {
        //this.ambientTemperature = ambientTemperature;
        //this.relativeHumidity = relativeHumidity;
        //this.light = light;
        //this.pressure = pressure;
        this.magneticY = magneticY;
        this.magneticZ = magneticZ;
    }

    public float getMagneticY() {
        return magneticY;
    }

    public void setMagneticY(float magneticY) {
        this.magneticY = magneticY;
    }

    public float getMagneticZ() {
        return magneticZ;
    }

    public void setMagneticZ(float magneticZ) {
        this.magneticZ = magneticZ;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "magneticY=" + magneticY +
                ", magneticZ=" + magneticZ +
                '}';
    }
}
