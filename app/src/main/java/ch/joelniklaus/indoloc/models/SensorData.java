package ch.joelniklaus.indoloc.models;

import android.hardware.SensorManager;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Data object containing all the measured sensor data of one data point.
 * At the moment only the magnetic field is measured.
 * <p>
 * Created by joelniklaus on 11.11.16.
 */

public class SensorData implements Serializable {

    private float ambientTemperature, light, pressure, relativeHumidity;
    private float geomagneticMagnitude, gravityMagnitude, magneticYProcessedOld, magneticZProcessedOld;
    private float[] gravity, magnetic;
    private float[] rotation = new float[9], inclination = new float[9];

    public static SensorData getSensorDataTest(float ambientTemperature, float light, float pressure, float relativeHumidity, float[] gravity, float[] magnetic,
                                        float geomagneticMagnitude, float gravityMagnitude, float magneticYProcessedOld, float magneticZProcessedOld) {
        SensorData sensorData = new SensorData();

        sensorData.setAmbientTemperature(ambientTemperature);
        sensorData.setLight(light);
        sensorData.setPressure(pressure);
        sensorData.setRelativeHumidity(relativeHumidity);

        sensorData.setGravity(gravity);
        sensorData.setMagnetic(magnetic);

        sensorData.setGeomagneticMagnitude(geomagneticMagnitude);
        sensorData.setGravityMagnitude(gravityMagnitude);
        sensorData.setMagneticYProcessedOld(magneticYProcessedOld);
        sensorData.setMagneticZProcessedOld(magneticZProcessedOld);

        return sensorData;
    }

    public SensorData() {

    }

    public SensorData(float ambientTemperature, float light, float pressure, float relativeHumidity, float[] gravity, float[] magnetic) {
        this.ambientTemperature = ambientTemperature;
        this.light = light;
        this.pressure = pressure;
        this.relativeHumidity = relativeHumidity;
        this.gravity = gravity;
        this.magnetic = magnetic;
        if (!SensorManager.getRotationMatrix(rotation, inclination, gravity, magnetic))
            System.out.println("Could not compute inclination and rotation matrices.");
        computeMagneticProcessedOld(magnetic);
        this.gravityMagnitude = rotation[6] * gravity[0] + rotation[7] * gravity[1] + rotation[8] * gravity[2]; // Z-Direction
        float[] rotInc = multiplyMatrix(inclination, rotation);
        this.geomagneticMagnitude = rotInc[3] * magnetic[0] + rotInc[4] * magnetic[1] + rotInc[5] * magnetic[2]; // Y-Direction (To Magnetic North Pole)
    }

    private void computeMagneticProcessedOld(float[] magnetic) {
        float[] magneticFingerprint = new float[3];
        magneticFingerprint[0] = rotation[0] * magnetic[0] + rotation[1] * magnetic[1] + rotation[2] * magnetic[2];
        magneticFingerprint[1] = rotation[3] * magnetic[0] + rotation[4] * magnetic[1] + rotation[5] * magnetic[2];
        magneticFingerprint[2] = rotation[6] * magnetic[0] + rotation[7] * magnetic[1] + rotation[8] * magnetic[2];

        // round the values to the sensors accuracy.
        // Does not work like this because this reports the accuracy level the sensor is working with between -1 and 3.
        // So not a decimal number
        //float accuracy = event.accuracy;

        // round to 0.2 to be sure because most sensors have an accuracy of around 0.15
        // TODO Round as an analysing tool.
       /*
        float accuracy = 0.2f;
        magneticFingerprint[0] = round(magneticFingerprint[0], accuracy); // x-value: should always be 0
        //assertion(magneticFingerprint[0] == 0.0);
        magneticFingerprint[1] = round(magneticFingerprint[1], accuracy); // y-value
        magneticFingerprint[2] = round(magneticFingerprint[2], accuracy); // z-value
        */
        // set values
        this.magneticYProcessedOld = magneticFingerprint[1];
        this.magneticZProcessedOld = magneticFingerprint[2];
    }

    /**
     * Helper method which multiplies the 3-by-3 Matrices rotation and inclination.
     *
     * @param first
     * @param second
     * @return
     */
    public float[] multiplyMatrix(float[] first, float[] second) {
        float[] result = new float[9];

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                for (int i = 0; i < 3; i++)
                    result[3 * row + col] += first[3 * row + i] * second[col + 3 * i];

        return result;
    }

    /**
     * Rounds the given number to a given number of decimal places.
     *
     * @param number
     * @param decimalPlaces
     * @return
     */
    public float round(float number, int decimalPlaces) {
        float factor = (float) Math.pow(10, decimalPlaces);
        return Math.round(number * factor) / factor;
    }

    /**
     * Rounds the given number to a given nearest fraction.
     * Eg. round(0.523, 0.2) => 0.6
     *
     * @param number
     * @param fraction
     * @return
     */
    public float round(float number, float fraction) {
        float factor = 1 / fraction;
        return Math.round(number * factor) / factor;
    }


    public float getAmbientTemperature() {
        return ambientTemperature;
    }

    public void setAmbientTemperature(float ambientTemperature) {
        this.ambientTemperature = ambientTemperature;
    }

    public float getLight() {
        return light;
    }

    public void setLight(float light) {
        this.light = light;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(float relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    public float getGeomagneticMagnitude() {
        return geomagneticMagnitude;
    }

    public void setGeomagneticMagnitude(float geomagneticMagnitude) {
        this.geomagneticMagnitude = geomagneticMagnitude;
    }

    public float getGravityMagnitude() {
        return gravityMagnitude;
    }

    public void setGravityMagnitude(float gravityMagnitude) {
        this.gravityMagnitude = gravityMagnitude;
    }

    public float getMagneticYProcessedOld() {
        return magneticYProcessedOld;
    }

    public void setMagneticYProcessedOld(float magneticYProcessedOld) {
        this.magneticYProcessedOld = magneticYProcessedOld;
    }

    public float getMagneticZProcessedOld() {
        return magneticZProcessedOld;
    }

    public void setMagneticZProcessedOld(float magneticZProcessedOld) {
        this.magneticZProcessedOld = magneticZProcessedOld;
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

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }

    public float[] getInclination() {
        return inclination;
    }

    public void setInclination(float[] inclination) {
        this.inclination = inclination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorData)) return false;

        SensorData that = (SensorData) o;

        if (Float.compare(that.ambientTemperature, ambientTemperature) != 0) return false;
        if (Float.compare(that.light, light) != 0) return false;
        if (Float.compare(that.pressure, pressure) != 0) return false;
        if (Float.compare(that.relativeHumidity, relativeHumidity) != 0) return false;
        if (Float.compare(that.geomagneticMagnitude, geomagneticMagnitude) != 0) return false;
        if (Float.compare(that.gravityMagnitude, gravityMagnitude) != 0) return false;
        if (Float.compare(that.magneticYProcessedOld, magneticYProcessedOld) != 0) return false;
        if (Float.compare(that.magneticZProcessedOld, magneticZProcessedOld) != 0) return false;
        if (!Arrays.equals(gravity, that.gravity)) return false;
        if (!Arrays.equals(magnetic, that.magnetic)) return false;
        if (!Arrays.equals(rotation, that.rotation)) return false;
        return Arrays.equals(inclination, that.inclination);

    }

    @Override
    public int hashCode() {
        int result = (ambientTemperature != +0.0f ? Float.floatToIntBits(ambientTemperature) : 0);
        result = 31 * result + (light != +0.0f ? Float.floatToIntBits(light) : 0);
        result = 31 * result + (pressure != +0.0f ? Float.floatToIntBits(pressure) : 0);
        result = 31 * result + (relativeHumidity != +0.0f ? Float.floatToIntBits(relativeHumidity) : 0);
        result = 31 * result + (geomagneticMagnitude != +0.0f ? Float.floatToIntBits(geomagneticMagnitude) : 0);
        result = 31 * result + (gravityMagnitude != +0.0f ? Float.floatToIntBits(gravityMagnitude) : 0);
        result = 31 * result + (magneticYProcessedOld != +0.0f ? Float.floatToIntBits(magneticYProcessedOld) : 0);
        result = 31 * result + (magneticZProcessedOld != +0.0f ? Float.floatToIntBits(magneticZProcessedOld) : 0);
        result = 31 * result + Arrays.hashCode(gravity);
        result = 31 * result + Arrays.hashCode(magnetic);
        result = 31 * result + Arrays.hashCode(rotation);
        result = 31 * result + Arrays.hashCode(inclination);
        return result;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "ambientTemperature=" + ambientTemperature +
                ", light=" + light +
                ", pressure=" + pressure +
                ", relativeHumidity=" + relativeHumidity +
                ", geomagneticMagnitude=" + geomagneticMagnitude +
                ", gravityMagnitude=" + gravityMagnitude +
                ", magneticYProcessedOld=" + magneticYProcessedOld +
                ", magneticZProcessedOld=" + magneticZProcessedOld +
                ", gravity=" + Arrays.toString(gravity) +
                ", magnetic=" + Arrays.toString(magnetic) +
                ", rotation=" + Arrays.toString(rotation) +
                ", inclination=" + Arrays.toString(inclination) +
                '}';
    }
}
