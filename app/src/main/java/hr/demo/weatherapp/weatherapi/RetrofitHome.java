package hr.demo.weatherapp.weatherapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHome {
    public static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    Retrofit retrofit;
    WeatherEndPointInterface weatherEndPointInterface;

    //TODO: interceptor koji se koristi da se na svim retrofit requestima mogu dodati Query Parameteri
    OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();
            //TODO: dodavanje Query Parametera na sve retrofit requeste, "APPID" je access token za Weather api, "units" govori apiu da vraca sve vrijednosti u metric sistemu
            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("APPID", "a27f96eac2e0973bc44851dfcbd6d16b")
                    .addQueryParameter("units", "metric")
                    .build();
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    }).build();

    public RetrofitHome() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();
        RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)
                .client(httpClient)
                .build();
        weatherEndPointInterface =
                retrofit.create(WeatherEndPointInterface.class);
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public WeatherEndPointInterface getWeatherEndPointInterface() {
        return weatherEndPointInterface;
    }
}
