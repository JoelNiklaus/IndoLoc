package ch.joelniklaus.indoloc.models;

import java.io.Serializable;

/**
 * Data object containing all the measured sensor data of one data point.
 * At the moment only the magnetic field is measured.
 * <p>
 * Created by joelniklaus on 11.11.16.
 */

// TODO alle möglichen anderen sensordaten hinzufügen. Nützt nicht so schadets nicht.
// TODO light, inclination data
public class SensorData implements Serializable {

    private int magneticY, magneticZ;


    public SensorData(int magneticY, int magneticZ) {
        this.magneticY = magneticY;
        this.magneticZ = magneticZ;
    }

    public int getMagneticY() {
        return magneticY;
    }

    public void setMagneticY(int magneticY) {
        this.magneticY = magneticY;
    }

    public int getMagneticZ() {
        return magneticZ;
    }

    public void setMagneticZ(int magneticZ) {
        this.magneticZ = magneticZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorData)) return false;

        SensorData that = (SensorData) o;

        if (magneticY != that.magneticY) return false;
        return magneticZ == that.magneticZ;

    }

    @Override
    public int hashCode() {
        int result = magneticY;
        result = 31 * result + magneticZ;
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
