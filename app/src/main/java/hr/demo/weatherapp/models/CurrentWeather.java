package hr.demo.weatherapp.models;

import android.content.Intent;

import java.util.List;

public class CurrentWeather {
    private Coordinates coord;
    private List<WeatherData> weather;
    private String base;
    private WeatherTemperatureData main;
    private double visibility;
    private WindData wind;
    private CloudData clouds;
    private Long dt;
    private SysData sys;
    private Long id;
    private String name;
    private Integer cod;

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public List<WeatherData> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherData> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public WeatherTemperatureData getMain() {
        return main;
    }

    public void setMain(WeatherTemperatureData main) {
        this.main = main;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public WindData getWind() {
        return wind;
    }

    public void setWind(WindData wind) {
        this.wind = wind;
    }

    public CloudData getClouds() {
        return clouds;
    }

    public void setClouds(CloudData clouds) {
        this.clouds = clouds;
    }

    public Long getDt() {
        return dt;
    }

    public void setDt(Long dt) {
        this.dt = dt;
    }

    public SysData getSys() {
        return sys;
    }

    public void setSys(SysData sys) {
        this.sys = sys;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }
}

