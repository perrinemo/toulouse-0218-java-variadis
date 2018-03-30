package fr.wildcodeschool.variadis;

import android.content.Intent;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HerbariumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_herbarium);

        final GridView herbView = findViewById(R.id.herbview);
        final TextView emptyHerbarium = findViewById(R.id.empty_herbarium);
        final CheckBox checkIfEmpty = findViewById(R.id.check_if_empty);
        final ArrayList<VegetalModel> vegetalList = new ArrayList<>();
        final GridAdapter adapter = new GridAdapter(this, vegetalList);

        ImageView ivProfil = findViewById(R.id.img_profile);
        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HerbariumActivity.this, ProfilActivity.class);
                startActivity(intent);
            }
        });

        checkIfEmpty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    vegetalList.clear();
                }
                else {
                    vegetalList.add(new VegetalModel(R.drawable.tilleul_arbre_300x300, "Tilleul"));
                    vegetalList.add(new VegetalModel(R.drawable.erable_sucre_fr_500_0006237, "Erable"));
                    vegetalList.add(new VegetalModel(R.drawable.img_ulmus_americana_2209, "Orme"));
                    vegetalList.add(new VegetalModel(R.drawable.micocoulier_300x300, "Micocoulier"));
                    vegetalList.add(new VegetalModel(R.drawable.pinus_pinea_pin_parasol_ou_pin_pignon, "Pin Parasol"));
                    vegetalList.add(new VegetalModel(R.drawable.c_dre_liban_ch_teau_de_hautefort_23, "Cèdre"));
                    vegetalList.add(new VegetalModel(R.drawable.charme_commun_fastigiata_, "Charme"));
                    vegetalList.add(new VegetalModel(R.drawable.murier_platane_sterile, "Platane"));
                    vegetalList.add(new VegetalModel(R.drawable.betula_papyrifera, "Bouleau"));

                }
                herbView.setAdapter(adapter);
                herbView.setEmptyView(emptyHerbarium);

            }
        });

        FloatingActionButton returnToMap = findViewById(R.id.return_to_map);
        returnToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (HerbariumActivity.this, MapsActivity.class);
                HerbariumActivity.this.startActivity(intent);
            }
        });


    }
}
