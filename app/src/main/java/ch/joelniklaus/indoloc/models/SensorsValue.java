package ch.joelniklaus.indoloc.models;

/**
 * Created by joelniklaus on 11.11.16.
 */
public class SensorsValue {

    private double ambientTemperature;
    private double light;
    private double pressure;
    private double relativeHumidity;

    public SensorsValue(double ambientTemperature, double light, double pressure, double relativeHumidity) {
        this.ambientTemperature = ambientTemperature;
        this.light = light;
        this.pressure = pressure;
        this.relativeHumidity = relativeHumidity;
    }

    public double getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(double relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
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

    @Override
    public String toString() {
        return "SensorsValue{" +
                "ambientTemperature=" + ambientTemperature +
                ", light=" + light +
                ", pressure=" + pressure +
                ", relativeHumidity=" + relativeHumidity +
                '}';
    }
}
