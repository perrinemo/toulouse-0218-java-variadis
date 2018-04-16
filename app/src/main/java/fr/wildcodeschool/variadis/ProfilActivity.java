package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


public class ProfilActivity extends AppCompatActivity {

    private ImageView mAvatar;
    private EditText mEditPseudo = findViewById(R.id.edit_pseudo);
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mEditPseudo = findViewById(R.id.edit_pseudo);
        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ImageView ivMap = findViewById(R.id.img_map);
        ImageButton deco = findViewById(R.id.btn_logout);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        mAvatar = findViewById(R.id.avatar);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ConnexionActivity.class);
                startActivity(intent);
                auth.signOut();
                finish();
            }
        });

        ivHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HerbariumActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilActivity.this.startActivity(new Intent(ProfilActivity.this, MapsActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        Glide.with(this).load(bitmap).apply(RequestOptions.circleCropTransform()).into(mAvatar);
    }
}
