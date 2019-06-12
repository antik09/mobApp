package hr.demo.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private static final String SP_LOGIN_TOKEN = "SP_LOGIN_TOKEN";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //TODO: ako u shared preferences postoji token da je login bio uspješan i nije stariji od jednog dana ne pitamo za logi  podatke već
        // idemo izravno na prikaz vremena
        sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        if (sharedPreferences.contains(SP_LOGIN_TOKEN)) {
            if (new Date().getTime() - sharedPreferences.getLong(SP_LOGIN_TOKEN, 0) < 86400000) {
                goToMain();
            }
        }
        ((Button) findViewById(R.id.buttonSignin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUsernameAndPassword();
            }
        });
    }

    private void checkUsernameAndPassword() {
        //TODO: provjera username i password i kreiranje tokena da je bio uspješan login
        String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
        if (username != null && password != null && username.length()>0 && password.length()>0) {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putLong(SP_LOGIN_TOKEN, new Date().getTime());
            editor.commit();
            goToMain();
        } else {
            Toast.makeText(this, "Wrong username or password!", Toast.LENGTH_LONG).show();
        }
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
