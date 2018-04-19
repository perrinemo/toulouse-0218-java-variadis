package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.concurrent.TimeUnit;


public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_TIME_OUT = 8000;
    public static final String PREF = "pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ImageView imgLogo = findViewById(R.id.img_logo);
        ImageView imgTree = findViewById(R.id.img_tree);
        ImageView imgTree2 = findViewById(R.id.img_tree2);
        ImageView imgTree3 = findViewById(R.id.img_tree3);
        ImageView imgMark1 = findViewById(R.id.img_marker2);
        ImageView imgMark2 = findViewById(R.id.img_marker4);
        ImageView imgMark3 = findViewById(R.id.img_marker1);
        ImageView imgMark4 = findViewById(R.id.img_marker3);
        ImageView imgMark5 = findViewById(R.id.img_marker7);
        ImageView imgMark6 = findViewById(R.id.img_marker5);
        ImageView imgMark7 = findViewById(R.id.img_marker8);
        ImageView imgMark8 = findViewById(R.id.img_marker6);
        ImageView imgMark9 = findViewById(R.id.img_marker9);
        ImageView imgMark10 = findViewById(R.id.img_marker);
        ImageView imgMark11 = findViewById(R.id.img_defi);



        animSplash(imgLogo, 5000);
        animSplash(imgTree2, 3500);
        animSplash(imgTree, 4000);
        animSplash(imgTree3, 4200);
        animSplash(imgMark1, 500);
        animSplash(imgMark3, 700);
        animSplash(imgMark2, 900);
        animSplash(imgMark4, 1100);
        animSplash(imgMark5, 1300);
        animSplash(imgMark6, 1500);
        animSplash(imgMark7, 1700);
        animSplash(imgMark8, 1900);
        animSplash(imgMark9, 2100);
        animSplash(imgMark10, 2300);
        animSplash(imgMark11, 2800);
















        SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
        pref.edit().clear().apply();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, ConnexionActivity.class);
                startActivity(i);
            }

        }, SPLASH_TIME_OUT);
    }
    public void animSplash(ImageView imageView, int startOffset) {
        Animation animationLogo = new AlphaAnimation(0.0f, 1.0f);
        animationLogo.setDuration(1000);
        animationLogo.setStartOffset(startOffset);
        animationLogo.setRepeatMode(Animation.ABSOLUTE);
        imageView.setAnimation(animationLogo);
    }

}
