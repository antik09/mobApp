package hr.demo.weatherapp.weatherapi;

import hr.demo.weatherapp.models.CurrentWeather;
import hr.demo.weatherapp.models.FiveDaysWeatherData;
import hr.demo.weatherapp.models.WeatherData;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface  WeatherEndPointInterface {
    @GET("weather")
    Observable<CurrentWeather> getCurrentWeatherDataForCity(@Query("q") String city );

    @GET("weather")
    Observable<CurrentWeather> getCurrentWeatherDataForCityId(@Query("id") String cityId );

    @GET("weather")
    Observable<CurrentWeather> getCurrentWeatherDataForLatLong(@Query("lat") double lat, @Query("lon") double lon );


    @GET("forecast")
    Observable<FiveDaysWeatherData> getFiveDayWeatherForCityId(@Query("id") String cityId );
}
