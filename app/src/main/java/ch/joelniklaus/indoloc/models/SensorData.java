package ch.joelniklaus.indoloc.models;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Data object containing all the measured sensor data of one data point.
 * At the moment only the magnetic field is measured.
 * <p>
 * Created by joelniklaus on 11.11.16.
 */

// TODO alle möglichen anderen sensordaten hinzufügen. Nützt nicht so schadets nicht.
// TODO light, inclination data
public class SensorData implements Serializable {

    private double ambientTemperature, light, pressure, relativeHumidity, magneticY, magneticZ;
    private float[] gravity, magnetic;


    public SensorData(double ambientTemperature, double light, double pressure, double relativeHumidity, float[] gravity, float[] magnetic, double magneticY, double magneticZ) {
        this.ambientTemperature = ambientTemperature;
        this.light = light;
        this.pressure = pressure;
        this.relativeHumidity = relativeHumidity;
        this.gravity = gravity;
        this.magnetic = magnetic;
        this.magneticY = magneticY;
        this.magneticZ = magneticZ;
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(double ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public double getLight() {
        return light;
    }

    public void setLight(double light) {
        this.light = light;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public double getMagneticY() {
        return magneticY;
    }

    public void setMagneticY(double magneticY) {
        this.magneticY = magneticY;
    }

    public double getMagneticZ() {
        return magneticZ;
    }

    public void setMagneticZ(double magneticZ) {
        this.magneticZ = magneticZ;
    }

    public float[] getGravity() {
        return gravity;
    }

    public void setGravity(float[] gravity) {
        this.gravity = gravity;
    }

    public float[] getMagnetic() {
        return magnetic;
    }

    public void setMagnetic(float[] magnetic) {
        this.magnetic = magnetic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorData)) return false;

        SensorData that = (SensorData) o;

        if (Double.compare(that.ambientTemperature, ambientTemperature) != 0) return false;
        if (Double.compare(that.light, light) != 0) return false;
        if (Double.compare(that.pressure, pressure) != 0) return false;
        if (Double.compare(that.relativeHumidity, relativeHumidity) != 0) return false;
        if (Double.compare(that.magneticY, magneticY) != 0) return false;
        if (Double.compare(that.magneticZ, magneticZ) != 0) return false;
        if (!Arrays.equals(gravity, that.gravity)) return false;
        return Arrays.equals(magnetic, that.magnetic);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(ambientTemperature);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(light);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pressure);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(relativeHumidity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(magneticY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(magneticZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(gravity);
        result = 31 * result + Arrays.hashCode(magnetic);
        return result;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "ambientTemperature=" + ambientTemperature +
                ", light=" + light +
                ", pressure=" + pressure +
                ", relativeHumidity=" + relativeHumidity +
                ", magneticY=" + magneticY +
                ", magneticZ=" + magneticZ +
                ", gravity=" + Arrays.toString(gravity) +
                ", magnetic=" + Arrays.toString(magnetic) +
                '}';
    }
}
