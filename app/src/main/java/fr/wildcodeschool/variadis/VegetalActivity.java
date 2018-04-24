package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static fr.wildcodeschool.variadis.HerbariumActivity.CLASS_FROM;
import static fr.wildcodeschool.variadis.HerbariumActivity.EXTRA_PARCEL_VEGETAL;
import static fr.wildcodeschool.variadis.VegetalHelperActivity.EXTRA_PARCEL_FOUNDVEGETAL;

public class VegetalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vegetal);

        //TODO Remplacer les Parcelables par des requêtes Firebase
        VegetalModel vegetal = getIntent().getParcelableExtra(EXTRA_PARCEL_VEGETAL);
        VegetalModel foundVegetal = getIntent().getParcelableExtra(EXTRA_PARCEL_FOUNDVEGETAL);
        ImageView imgVegetal = findViewById(R.id.img_vegetal);
        TextView txtVegetal = findViewById(R.id.nom_vegetal);
        TextView placeVegetal = findViewById(R.id.lieu);
        TextView lastFind = findViewById(R.id.last_find);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.FRANCE);
        Date date = Calendar.getInstance().getTime();
        String dateFormat = format.format(date);

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(VegetalActivity.this, ConnexionActivity.class);
            startActivity(intent);
            finish();
        }

        if (getIntent().getStringExtra(CLASS_FROM).equals("helper")) {
            //imgVegetal.setImageBitmap(foundVegetal.getBitmapPicture());
            txtVegetal.setText(foundVegetal.getName());
            placeVegetal.setText(foundVegetal.getAddress());
            lastFind.setText(foundVegetal.getDate());
        }

        if (getIntent().getStringExtra(CLASS_FROM).equals("herbarium")) {
            imgVegetal.setImageResource(vegetal.getPicture());
            txtVegetal.setText(vegetal.getName());
        }

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
