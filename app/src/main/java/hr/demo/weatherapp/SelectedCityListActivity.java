package hr.demo.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hr.demo.weatherapp.models.City;
import hr.demo.weatherapp.models.CityId;
import hr.demo.weatherapp.models.CityIdList;

public class SelectedCityListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<CityId> cities = new ArrayList<>();
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_city_list);


        recyclerView = ((RecyclerView) findViewById(R.id.cityList));
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        //TODO: adapteru prosljeđujemo listu trenutno sačuvanih gradova i dva onClicklistenera, prvi se pobrine da kliknuti grad spremimo u shered preferences kao odabrani grad kako bi se mogao prikazati u MainActivity
        // drugi onClicklistener je za brisanje grada iz liste spremljenih gradova
        mAdapter = new CityListAdapter(cities, new AddNewCityActivity.OnItemClickListener() {
            @Override
            public void onItemClick(CityId item) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MainActivity.SP_SELECTED_CITY_KEY, "" + item.id);
                editor.commit();
                finish();
            }
        }, new AddNewCityActivity.OnItemClickListenerPosition() {
            @Override
            public void onItemClick(int position) {
                deleteCity(position);
            }
        });
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        ((FloatingActionButton) findViewById(R.id.floatingActionButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectedCityListActivity.this, AddNewCityActivity.class);
                startActivity(i);
            }
        });
        loadCityList();
    }

    private void deleteCity(final int position) {
//TODO: kod brisanja grada iz liste otvori se popup za potvrdu brisanja
        MaterialDialog materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.txt_delete_text)
                .positiveText(R.string.txt_delete_btn)
                .negativeText(R.string.txt_cancel_btn)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String json = sharedPreferences.getString(MainActivity.SP_CITIES_KEY, "{'cityList':[]}");
                        CityIdList citySaved = gson.fromJson(json, CityIdList.class);
                        //TODO: ako je grad kojeg brisemo trenutno selektiran za prikaz  pokušamo odabrati drugi grad za prikaz
                        if(citySaved.getCityList().size()>1){
                            String cityid= sharedPreferences.getString(MainActivity.SP_SELECTED_CITY_KEY, "");
                            if((""+citySaved.getCityList().get(position).id).equals(cityid)){
                                if(position==0){
                                    editor.putString(MainActivity.SP_CITIES_KEY, ""+citySaved.getCityList().get(1).id);
                                    editor.commit();
                                }else{
                                    editor.putString(MainActivity.SP_SELECTED_CITY_KEY, ""+citySaved.getCityList().get(0).id);
                                    editor.commit();
                                }
                            }
                        }
                        citySaved.getCityList().remove(position);


                        String json3 = gson.toJson(citySaved);
                        editor.putString(MainActivity.SP_CITIES_KEY, json3);
                        editor.commit();

                        loadCityList();
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void loadCityList() {
        //TODO: dohvacanje i prikaz gradova na listi iz shared preferences
        Gson gson = new Gson();
        String json = sharedPreferences.getString(MainActivity.SP_CITIES_KEY, "{'cityList':[]}");
        CityIdList citySaved = gson.fromJson(json, CityIdList.class);
        cities = citySaved.getCityList();

        //TODO: adapteru prosljeđujemo nove podatke za gradove koje će on setirati i pozvati onDataSetChange koji onda natjera recyclerview da se ponovno iscrta s novim gradovima
        ((CityListAdapter) mAdapter).setNewCitiesData(cities);
    }
}
