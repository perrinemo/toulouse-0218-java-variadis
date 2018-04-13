package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static fr.wildcodeschool.variadis.HerbariumActivity.EXTRA_PARCEL_VEGETAL;

public class VegetalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetal);

        VegetalModel vegetal = getIntent().getParcelableExtra(EXTRA_PARCEL_VEGETAL);
        ImageView imgVegetal = findViewById(R.id.img_vegetal);
        TextView txtVegetal = findViewById(R.id.nom_vegetal);
        imgVegetal.setImageResource(vegetal.getPicture());
        txtVegetal.setText(vegetal.getName());

        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ivHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VegetalActivity.this, HerbariumActivity.class));
            }
        });

        ImageView ivProfile = findViewById(R.id.img_profile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(VegetalActivity.this, ProfilActivity.class));
            }
        });

        ImageView ivMap = findViewById(R.id.img_map);
        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VegetalActivity.this, MapsActivity.class));
            }
        });

    }
}
