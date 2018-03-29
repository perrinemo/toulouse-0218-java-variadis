package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        TextView mPseudo = findViewById(R.id.nom_pseudo);
        mPseudo.setFocusable(true);
        mPseudo.setEnabled(true);
        mPseudo.setClickable(true);
        mPseudo.setFocusableInTouchMode(true);

        ImageView mImgHerbier = findViewById(R.id.img_herbier);
        mImgHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HerbariumActivity.class);
                startActivity(intent);
            }
        });
    }
}
