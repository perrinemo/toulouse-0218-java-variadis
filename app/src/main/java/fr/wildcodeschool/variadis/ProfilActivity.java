package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.content.SharedPreferences;

import android.support.design.widget.FloatingActionButton;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static fr.wildcodeschool.variadis.MainActivity.EXTRA_PSEUDO;

public class ProfilActivity extends AppCompatActivity {

    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        TextView changementPseudo = findViewById(R.id.nom_pseudo);
        ImageView ivHerbier = findViewById(R.id.img_herbier);
        FloatingActionButton returnToMap = findViewById(R.id.return_to_map);
        ImageView ivDefi = findViewById(R.id.img_defi);
        avatar = findViewById(R.id.avatar);

        Intent intent = getIntent();
        String pseudo = intent.getStringExtra(EXTRA_PSEUDO);

        changementPseudo.setText(pseudo);

        ivHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HerbariumActivity.class);
                startActivity(intent);
            }
        });

        returnToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilActivity.this, MapsActivity.class);
                ProfilActivity.this.startActivity(intent);
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        ivDefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefiHelper.openDialogDefi(ProfilActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        Glide.with(this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(avatar);
    }
}
