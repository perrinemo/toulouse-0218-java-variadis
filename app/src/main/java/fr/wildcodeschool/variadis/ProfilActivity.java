package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static fr.wildcodeschool.variadis.MapsActivity.back_pressed;


public class ProfilActivity extends AppCompatActivity {

    private ImageView mAvatar;
    private EditText mEditPseudo;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mEditPseudo = findViewById(R.id.edit_pseudo);
        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ImageView ivMap = findViewById(R.id.img_map);
        ImageButton deco = findViewById(R.id.btn_logout);
        Button okPseudo = findViewById(R.id.btn_ok_pseudo);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();


        mAvatar = findViewById(R.id.avatar);

        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageReference = firebaseStorage.getReference();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference("users").child(mUid).child("pseudo");
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEditPseudo.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        okPseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pseudo = mEditPseudo.getText().toString();
                if (TextUtils.isEmpty(mUid)) {
                    createUser(pseudo);
                } else {
                    updateUser(pseudo);
                }
            }
        });

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

    private void createUser(String pseudo) {
        if (!TextUtils.isEmpty(mUid)) {
            ProfilModel profilModel = new ProfilModel(pseudo);
            mDatabaseReference.child(mUid).setValue(profilModel);
            addUserChangeListener();
        }
    }

    private void addUserChangeListener() {
        mDatabaseReference.child(mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfilModel profilModel = dataSnapshot.getValue(ProfilModel.class);
                if (profilModel == null) {
                    return;
                }
                mEditPseudo.setText(profilModel.getPseudo());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUser(String pseudo) {
        FirebaseDatabase.getInstance().getReference("users").child(mUid).child("pseudo").setValue(pseudo);
    }
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            System.exit(0);
            super.onBackPressed();
        } else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
