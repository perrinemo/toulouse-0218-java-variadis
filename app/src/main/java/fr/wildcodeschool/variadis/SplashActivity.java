package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;



public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_TIME_OUT = 1000;
    public static final String PREF = "pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(PREF, false);
                edit.apply();
                Intent i = new Intent(SplashActivity.this, ConnexionActivity.class);
                startActivity(i);
            }

        }, SPLASH_TIME_OUT);
    }
}
