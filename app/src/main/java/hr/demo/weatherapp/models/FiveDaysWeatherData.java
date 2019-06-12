package hr.demo.weatherapp.models;

import java.util.List;

public class FiveDaysWeatherData {
    List<FDWeather> list;
    FDCity city;

    public List<FDWeather> getList() {
        return list;
    }

    public void setList(List<FDWeather> list) {
        this.list = list;
    }

    public FDCity getCity() {
        return city;
    }

    public void setCity(FDCity city) {
        this.city = city;
    }
}
