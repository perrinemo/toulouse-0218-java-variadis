package fr.wildcodeschool.variadis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ProgressBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static fr.wildcodeschool.variadis.MapsActivity.back_pressed;


public class ProfilActivity extends AppCompatActivity {

    public final static int CAMERA = 123;
    public final static int APP_PHOTO = 456;

    private ImageView mAvatar;
    private EditText mEditPseudo;
    private DatabaseReference mDatabaseReference;
    private String mUid;
    private Uri mFileUri = null;
    private String mGetImageUrl = "";
    private boolean mIsOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        checkPermission();

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ImageView ivMap = findViewById(R.id.img_map);
        ImageButton deco = findViewById(R.id.btn_logout);
        ImageView info = findViewById(R.id.btn_info);
        Button okPseudo = findViewById(R.id.btn_ok_pseudo);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        mAvatar = findViewById(R.id.avatar);

        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mEditPseudo = findViewById(R.id.edit_pseudo);
        mAvatar = findViewById(R.id.avatar);

        mDatabaseReference = firebaseDatabase.getReference("users").child(mUid);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("pseudo").getValue() != null) {
                    String pseudo = (String) dataSnapshot.child("pseudo").getValue();
                    mEditPseudo.setText(pseudo);
                }
                if (dataSnapshot.child("avatar").getValue() != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    String url = (String) dataSnapshot.child("avatar").getValue();
                    Glide.with(ProfilActivity.this)
                            .load(url)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFileUri = CameraUtils.getOutputMediaFileUri(ProfilActivity.this);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                startActivityForResult(intent, APP_PHOTO);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        validPseudo.setOnClickListener(new View.OnClickListener() {
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

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, InfosActivity.class);
                startActivity(intent);
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

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilActivity.this.startActivity(new Intent(ProfilActivity.this, MapsActivity.class));
                finish();
            }
        });
    }

    // Méthodes relatives au pseudo
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


    // Permission pour utiliser l'appareil photo
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(ProfilActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mIsOk = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.CAMERA}, CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int request, String permissions[], int[] results) {
        mIsOk = false;
        switch (request) {
            case CAMERA:
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    mIsOk = true;
                }
        }
    }

    // Méthodes relatives à l'avatar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case APP_PHOTO:
                try {
                    if (resultCode == RESULT_OK) {
                        mGetImageUrl = mFileUri.getPath();
                    }
                    saveCaptureImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void saveCaptureImage() {
        if (!mGetImageUrl.equals("") && mGetImageUrl != null) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(mUid).child("avatar.jpg");
            ref.putFile(mFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(mUid).child("avatar").setValue(downloadUri.toString());
                }
            });
        }

    }
}
