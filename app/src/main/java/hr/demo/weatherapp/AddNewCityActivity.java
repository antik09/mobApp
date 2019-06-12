package hr.demo.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.SharedLibraryInfo;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;


import hr.demo.weatherapp.models.City;
import hr.demo.weatherapp.models.CityId;
import hr.demo.weatherapp.models.CityIdList;
import hr.demo.weatherapp.models.CurrentWeather;
import hr.demo.weatherapp.weatherapi.RetrofitHome;
import hr.demo.weatherapp.weatherapi.WeatherEndPointInterface;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


//TODO: activity za dodavanje novog grada u spremljene gradove
public class AddNewCityActivity extends AppCompatActivity {

    //TODO: permission za dobivanje korisnikove lokacije
    private String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
    //TODO: bilo koja vrijednost bitno da je unikadna od drugih kodova kako bi se mogla prepoznati naknadno u onRequestPermissionsResult
    private int REQUIRED_PERMISSIONS_CODE = 1234;
    City cities;
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<CityId> filteredCities = new ArrayList<>();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_city);

        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);

        //TODO: dohvacanje liste gradova iz assets foldera, sama lista nije jednaka onoj sa OpenWeatherAPI stranice jer ta ima oko 20Mb što je previše za koristit ali smo
        //jednom prije promjenom strukture te liste smanjiti ju na samo 1.8Mb
        Observable<City> cityObservable = Observable.create(new ObservableOnSubscribe<City>() {
            @Override
            public void subscribe(ObservableEmitter<City> emitter) throws Exception {

                City lcs = new City();
                String str = "";
                StringBuilder sb = new StringBuilder();
                InputStream is = null;
                try {
                    is = getAssets().open("weather_city_list.json");

                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                    while ((str = br.readLine()) != null) {
                        sb.append(str);
                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!sb.toString().equals("")) {
                    lcs = new Gson().fromJson(sb.toString(), City.class);
                }
                emitter.onNext(lcs);
                emitter.onComplete();
            }
        });

        //TODO: nakon što se dohvati lista svih gradova na pozadinskom thredu spremaimo ih u cities objekt i ugasimo loader
        cityObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<City>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(City city) {
                        cities = city;
                        ((ConstraintLayout) findViewById(R.id.cl_blocker)).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        recyclerView = ((RecyclerView) findViewById(R.id.city_preview));

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //TODO: kako bi se u Recycler viewu vidjele crte između elemenata potrebno je dodati ova DeviderItemDecoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        //TODO: adapteru za recycler view se proslijeđuje lista filtriranih gradova, koja će u ovom trenutku biti prazna, i itemclicklistener jer  sam recycler view ne omogućuje lagano
        // dodavanje click listenera pa ga treba proslijediti u adapter i u njemu ga dodati na neki view
        // u ovom click listeneru na klik na element liste uzmemo grad na koji je kliknuto i dodamo ga u listu spremljenih gradova u sharedpreferences, te ga postavimo kao odabrani grad
        mAdapter = new AddCityListAdapter(filteredCities, new OnItemClickListener() {
            @Override
            public void onItemClick(CityId item) {

                Gson gson = new Gson();
                String json = sharedPreferences.getString(MainActivity.SP_CITIES_KEY, "{'cityList':[]}");
                CityIdList citySaved = gson.fromJson(json, CityIdList.class);
                citySaved.getCityList().add(item);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.SP_SELECTED_CITY_KEY, ""+item.id);
                String json3 = gson.toJson(citySaved);
                editor.putString(MainActivity.SP_CITIES_KEY, json3);
                editor.commit();
                finish();
            }
        });
        recyclerView.setAdapter(mAdapter);

        //TODO: dodavanje klika na gumb za dohvačanje korisnikove lokacije
        ((ImageButton) findViewById(R.id.locationButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: unos texta u polje kloje će filtrirati listu gradova tek nakon što se unesu barem dva znaka kako ne bi dobivali prevelike rezultate i time usporitli proces
        EditText city = ((EditText) findViewById(R.id.search));
        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int x, int x1, int x2) {
                if (charSequence.length() > 2 &&  cities!=null) {
                    filteredCities.clear();
                    for (int i = 0; i < cities.getId().size(); i++) {
                        if (cities.getName().get(i).toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredCities.add(new CityId(cities.getId().get(i), cities.getName().get(i) + ", " + cities.getCountryCode().get(i)));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getUserLocation() {
        //TODO: provjera da li imamo dopuštenje za dobivanje user lokacije ako nemamo zatražimo ga
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 Toast.makeText(this, "We need your location to access your weather data!", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS_CODE);

        } else {
            getLocation();
        }
    }

    private void getLocation() {
        //TODO: dohvačanje zadnje korisnikove lokacije i pozivanje dohvačanja podataka s OpenWeatherMapa, pošto dobijemo samo koordinate korisnika openweather map će nam vratiti ime i ID grada
        // i to se onda spremi za daljnje korištenje
        ((ConstraintLayout) findViewById(R.id.cl_blocker)).setVisibility(View.VISIBLE);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((ConstraintLayout) findViewById(R.id.cl_blocker)).setVisibility(View.GONE);
        }else
        {
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            if(lastKnownLocation==null) {
                Toast.makeText(this, R.string.no_location, Toast.LENGTH_LONG).show();
            }else
            {
                getDataForUserLocation(lastKnownLocation);
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //TODO: ova metoda se poziva nakon što korisnik nešto klikne kad ga pitamo za dopuštenje da koristimo njegovu lokaciju
        if (REQUIRED_PERMISSIONS_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, R.string.location_not_granted, Toast.LENGTH_LONG).show();
            } else {
                getLocation();
            }
        }
    }

    private void getDataForUserLocation(Location location){
        //TODO: dohvačanje podataka s OpenWeatherAPI-a nakon što dobijemo lokaciju dohvačanje se odvija s Retrofitom i RxJavom
        RetrofitHome retrofitHome = new RetrofitHome();
        Retrofit retrofit = retrofitHome.getRetrofit();
        WeatherEndPointInterface weatherEndPointInterface = retrofitHome.getWeatherEndPointInterface();
        Observable<CurrentWeather> call = weatherEndPointInterface.getCurrentWeatherDataForLatLong(location.getLatitude(), location.getLongitude());
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CurrentWeather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CurrentWeather weatherData) {
                        Log.d("MYTAG", "data fetched");

                        //TODO: kada dobijemo podatke spremimo ime i ID grada u kojem smo i spremimo podatke ta taj grad u shared preferences
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String json3 = gson.toJson(weatherData);
                        editor.putString(MainActivity.SP_SELECTED_CITY_CATCH_KEY, json3);

                        editor.putString(MainActivity.SP_SELECTED_CITY_KEY, ""+weatherData.getId());

                        String json = sharedPreferences.getString(MainActivity.SP_CITIES_KEY, "{'cityList':[]}");
                        CityIdList citySaved = gson.fromJson(json, CityIdList.class);
                        citySaved.getCityList().add(new CityId(weatherData.getId(), weatherData.getName()+", "+ weatherData.getSys().getCountry()));
                        String json2 = gson.toJson(citySaved);
                        editor.putString(MainActivity.SP_CITIES_KEY, json2);

                        editor.commit();

                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("MYTAG", "error");
                        ((ConstraintLayout) findViewById(R.id.cl_blocker)).setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface OnItemClickListener {
        void onItemClick(CityId item);
    }

    public interface OnItemClickListenerPosition {
        void onItemClick(int position);
    }
}
