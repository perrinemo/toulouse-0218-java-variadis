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

        SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
        pref.edit().clear().apply();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MapsActivity.class);
                startActivity(i);
            }

        }, SPLASH_TIME_OUT);
    }
}
