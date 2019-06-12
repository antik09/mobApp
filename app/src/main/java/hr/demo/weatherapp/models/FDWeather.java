package hr.demo.weatherapp.models;

import java.util.List;

public class FDWeather {
    long dt;
    WeatherTemperatureData main;
    List<WeatherData> weather;

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public WeatherTemperatureData getMain() {
        return main;
    }

    public void setMain(WeatherTemperatureData main) {
        this.main = main;
    }

    public List<WeatherData> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherData> weather) {
        this.weather = weather;
    }
}
