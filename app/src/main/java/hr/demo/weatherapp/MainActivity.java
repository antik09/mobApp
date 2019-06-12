package hr.demo.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import hr.demo.weatherapp.models.CityId;
import hr.demo.weatherapp.models.CityIdList;
import hr.demo.weatherapp.models.CurrentWeather;
import hr.demo.weatherapp.models.FDDayWeather;
import hr.demo.weatherapp.models.FDWeather;
import hr.demo.weatherapp.models.FiveDaysWeatherData;
import hr.demo.weatherapp.models.WeatherData;
import hr.demo.weatherapp.weatherapi.RetrofitHome;
import hr.demo.weatherapp.weatherapi.WeatherEndPointInterface;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFERENCES_KEY = "SHARED_PREFERENCES_KEY_WEATHER_APP";
    public static final String SP_CITIES_KEY = "SP_CITIES_KEY_WEATHER_APP";
    public static final String SP_SELECTED_CITY_KEY = "SP_SELECTED_CITY_KEY";
    public static final String SP_SELECTED_CITY_CATCH_KEY = "SP_SELECTED_CITY_CATCH_KEY";
    public static final String SP_FORECAST_CATCH_KEY = "SP_FORECAST_CATCH_KEY";
    SharedPreferences sharedPreferences;
    DecimalFormat precision = new DecimalFormat("0.00");
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<FDDayWeather> forecastWeather=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        //TODO: click za otvaranje liste spremljenih gradova
        ((ImageButton) findViewById(R.id.buttonSettings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectedCitiesList();
            }
        });

        //TODO: click na button za refresh podataka sa API-a
        ((ImageButton) findViewById(R.id.imageViewRefresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ConstraintLayout)findViewById(R.id.cl_blocker)).setVisibility(View.VISIBLE);
                getDataFromApi();
            }
        });
        recyclerView=((RecyclerView) findViewById(R.id.recViewFiveDays));
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new ForecastWeatherListAdapter(getApplicationContext(), forecastWeather);
        recyclerView.setAdapter(mAdapter);
        getFiveDaysForecast();

    }

    @Override
    protected void onResume() {
        super.onResume();


        String cityKey = sharedPreferences.getString(MainActivity.SP_SELECTED_CITY_KEY, "");

        //TODO: ako nemamo niti jednog spremljenog grada idemo izravno na listu spremljenih gradova gdje možemo dodati neki grad
        if (cityKey.equals("")) {
            openSelectedCitiesList();
            return;
        }


        String jsonOldData = sharedPreferences.getString(MainActivity.SP_SELECTED_CITY_CATCH_KEY, "");
        //TODO: provjera da li za trenutni grad imaom neke spremljene podatke u shared preferences te ih prikazemo prije nego dohvatimo nove podatke sa API-a
        if (!jsonOldData.equals("")) {
            Gson gson = new Gson();
            CurrentWeather currentWeather = gson.fromJson(jsonOldData, CurrentWeather.class);
            populateData(currentWeather);
        }


        getDataFromApi();
        populateForecastData();

    }

    private void openSelectedCitiesList() {
        Intent i = new Intent(this, SelectedCityListActivity.class);
        startActivity(i);
    }

    private void getDataFromApi() {
        //TODO: dohvacanje podataka sa OpenWeatherAPi-a s retrofitom i RxJavom za spremljeni city id
        String cityKey = sharedPreferences.getString(MainActivity.SP_SELECTED_CITY_KEY, "");
        RetrofitHome retrofitHome = new RetrofitHome();
        Retrofit retrofit = retrofitHome.getRetrofit();
        WeatherEndPointInterface weatherEndPointInterface = retrofitHome.getWeatherEndPointInterface();
        Observable<CurrentWeather> call = weatherEndPointInterface.getCurrentWeatherDataForCityId(cityKey);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrentWeather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CurrentWeather weatherData) {
                        //TODO: nakon što dohvatimo podatke spremimo ih i prikažemo na ekranu
                        Log.d("MYTAG", "data fetched");
                        populateData(weatherData);

                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String json3 = gson.toJson(weatherData);
                        editor.putString(MainActivity.SP_SELECTED_CITY_CATCH_KEY, json3);
                        editor.commit();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MYTAG", "error");
                    }

                    @Override
                    public void onComplete() {
                        ((ConstraintLayout)findViewById(R.id.cl_blocker)).setVisibility(View.GONE);
                    }
                });
    }

    public void populateData(CurrentWeather weatherData) {

        if (weatherData.getWeather().size() > 0) {
            //TODO: od API-a dobijemo id vremenskog uvjeta pa iz getImageResourceIdForWeatherCode metode dobijemo ikonu za taj id
            ((ImageView) findViewById(R.id.imageWeather)).setImageResource(WeatherCodes.getImageResourceIdForWeatherCode(getApplicationContext(), weatherData.getWeather().get(0).getId()));
            ((TextView) findViewById(R.id.textDescription)).setText(weatherData.getWeather().get(0).getMain());
        }
        ((TextView) findViewById(R.id.textCity)).setText(weatherData.getName());
        ((TextView) findViewById(R.id.texthumidty)).setText(precision.format(weatherData.getMain().getHumidity()) + "%");
        ((TextView) findViewById(R.id.textTemperature)).setText(Math.round(weatherData.getMain().getTemp()) + "°C");
        ((TextView) findViewById(R.id.textViewLastRefresh)).setText(getDateCurrentTimeZone(weatherData.getDt()));
    }

    public void populateForecastData(){
        Gson gson = new Gson();
        String json = sharedPreferences.getString(MainActivity.SP_FORECAST_CATCH_KEY, "[]");
        Type listType = new TypeToken<List<FDDayWeather>>() {}.getType();
        List<FDDayWeather> savedForecast = gson.fromJson(json, listType);
        Collections.sort(savedForecast, new Comparator<FDDayWeather>(){
            public int compare(FDDayWeather obj1, FDDayWeather obj2) {
                return Long.compare(obj1.getDt(),obj2.getDt());
            }
        });
        ((ForecastWeatherListAdapter)mAdapter).setNewForecastData(savedForecast);
    }

    public String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("UTC");
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }


    private void getFiveDaysForecast() {
        //TODO: dobivanje višednevne prognoze za odabrani grad
        String cityKey = sharedPreferences.getString(MainActivity.SP_SELECTED_CITY_KEY, "");
        RetrofitHome retrofitHome = new RetrofitHome();
        Retrofit retrofit = retrofitHome.getRetrofit();
        WeatherEndPointInterface weatherEndPointInterface = retrofitHome.getWeatherEndPointInterface();
        Observable<FiveDaysWeatherData> call = weatherEndPointInterface.getFiveDayWeatherForCityId(cityKey);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FiveDaysWeatherData>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(FiveDaysWeatherData fiveDaysWeatherData) {
                        Log.d("MYTAG", "data fetched");

                        //TODO: višednevnu prognozu API vraća po satima pa trebamo ju malo obraditi kako bi dobili podatke po danima
                        Map<Long, FDDayWeather> dayMap = new HashMap<>();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(new Date().getTime());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        dayMap.put((calendar.getTime().getTime() + 86400000), new FDDayWeather(calendar.getTime().getTime() + 86400000, 1000, -1000, 800, "Sunny"));
                        dayMap.put((calendar.getTime().getTime() + 86400000 * 2), new FDDayWeather(calendar.getTime().getTime() + 86400000 * 2, 1000, -1000, 800, "Sunny"));
                        dayMap.put((calendar.getTime().getTime() + 86400000 * 3), new FDDayWeather(calendar.getTime().getTime() + 86400000 * 3, 1000, -1000, 800, "Sunny"));
                        dayMap.put((calendar.getTime().getTime() + 86400000 * 4), new FDDayWeather(calendar.getTime().getTime() + 86400000 * 4, 1000, -1000, 800, "Sunny"));

                        for (FDWeather fdWeather : fiveDaysWeatherData.getList()) {
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.setTimeInMillis(fdWeather.getDt()*1000);
                            calendar2.set(Calendar.HOUR_OF_DAY, 0);
                            calendar2.set(Calendar.MINUTE, 0);
                            calendar2.set(Calendar.SECOND, 0);
                            calendar2.set(Calendar.MILLISECOND, 0);
                            if (dayMap.containsKey(calendar2.getTime().getTime())) {
                                FDDayWeather fdw = dayMap.get(calendar2.getTime().getTime());
                                if (fdw.getMaxTemp() < fdWeather.getMain().getTemp_max()) {
                                    fdw.setMaxTemp(fdWeather.getMain().getTemp_max());
                                }
                                if (fdw.getMinTemp() > fdWeather.getMain().getTemp_min()) {
                                    fdw.setMinTemp(fdWeather.getMain().getTemp_min());
                                }
                                if (fdWeather.getWeather().size() > 0) {
                                    if ( (fdWeather.getWeather().get(0).getId() < 800 && fdWeather.getWeather().get(0).getId() < fdw.getCode())) {
                                        fdw.setCode(fdWeather.getWeather().get(0).getId());
                                        fdw.setDescription(fdWeather.getWeather().get(0).getMain());
                                    }else if( (fdw.getCode() >= 800 && fdWeather.getWeather().get(0).getId() > 800 && fdWeather.getWeather().get(0).getId() > fdw.getCode())){
                                        fdw.setCode(fdWeather.getWeather().get(0).getId());
                                        fdw.setDescription(fdWeather.getWeather().get(0).getMain());
                                    }
                                }
                            }
                        }

                        List<FDDayWeather> fddList=new ArrayList<>();
                        for (Map.Entry<Long, FDDayWeather> entry : dayMap.entrySet()) {
                            fddList.add(entry.getValue());
                        }
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String json3 = gson.toJson(fddList);
                        editor.putString(MainActivity.SP_FORECAST_CATCH_KEY, json3);
                        editor.commit();
                        populateForecastData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MYTAG", "error");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
