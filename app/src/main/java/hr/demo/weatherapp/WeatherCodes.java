package hr.demo.weatherapp;

import android.content.Context;

public class WeatherCodes {

    //TODO: da na jednom mejstu imamo dohvacanje ikona za vremenske pojava na temelju id-a vremenske pojave koji nam vrati API
    // dohvačanje drawablea pomoću context.getResources().getIdentifier je nužno jer smo u static metodi
    public static int getImageResourceIdForWeatherCode(Context context, int id) {
        int iid = R.drawable.sun;

        if (id >= 200 && id < 300) {
            iid = context.getResources().getIdentifier("cloudlightning", "drawable", context.getPackageName());
        }
        if (id >= 300 && id < 600) {
            iid = context.getResources().getIdentifier("cloudrain", "drawable", context.getPackageName());
        }
        if (id >= 600 && id < 700) {
            iid = context.getResources().getIdentifier("cloudsnow", "drawable", context.getPackageName());
        }
        if (id >= 700 && id < 800) {
            iid = context.getResources().getIdentifier("cloudcloud", "drawable", context.getPackageName());
        }
        if (id == 800) {
            iid = context.getResources().getIdentifier("sun", "drawable", context.getPackageName());
        }
        if (id == 801 || id == 802) {
            iid = context.getResources().getIdentifier("suncloud", "drawable", context.getPackageName());
        }
        if (id > 802) {
            iid = context.getResources().getIdentifier("cloudcloud", "drawable", context.getPackageName());
        }
        return iid;
    }
}
