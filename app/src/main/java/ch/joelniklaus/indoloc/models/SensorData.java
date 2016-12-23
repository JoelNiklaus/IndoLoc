package ch.joelniklaus.indoloc.models;

import java.io.Serializable;

/**
 * Created by joelniklaus on 11.11.16.
 */
public class SensorData implements Serializable {

    private double magneticY, magneticZ;


    public SensorData(double magneticY, double magneticZ) {
        this.magneticY = magneticY;
        this.magneticZ = magneticZ;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorData)) return false;

        SensorData that = (SensorData) o;

        if (Double.compare(that.magneticY, magneticY) != 0) return false;
        return Double.compare(that.magneticZ, magneticZ) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(magneticY);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(magneticZ);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "magneticY=" + magneticY +
                ", magneticZ=" + magneticZ +
                '}';
    }
}
