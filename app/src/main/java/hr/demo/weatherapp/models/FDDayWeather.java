package hr.demo.weatherapp.models;

public class FDDayWeather {
    long dt;
    double minTemp;
    double maxTemp;
    int code;
    String description;

    public FDDayWeather(long dt) {
        this.dt = dt;
    }

    public FDDayWeather(long dt, double minTemp, double maxTemp, int code, String description) {
        this.dt = dt;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.code = code;
        this.description = description;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
