package fr.wildcodeschool.variadis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static fr.wildcodeschool.variadis.MapsActivity.sBackPress;


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

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ImageView ivMap = findViewById(R.id.img_map);
        ImageButton deco = findViewById(R.id.btn_logout);
        Button validPseudo = findViewById(R.id.btn_ok_pseudo);
        SingletonClass singletonClass = SingletonClass.getInstance();

        TextView tvPoints = findViewById(R.id.text_points);
        ImageView badge1 = findViewById(R.id.img_badge1_ok);
        ImageView badge2 = findViewById(R.id.img_badge2_ok);
        ImageView badge3 = findViewById(R.id.img_badge3_ok);
        ImageView badge4 = findViewById(R.id.img_badge4_ok);
        ImageView badge5 = findViewById(R.id.img_badge5_ok);

        mAvatar = findViewById(R.id.avatar);
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mEditPseudo = findViewById(R.id.edit_pseudo);
        mAvatar = findViewById(R.id.avatar);

        if (singletonClass.getProfil() != null) {
            mEditPseudo.setText(singletonClass.getProfil().getPseudo());
        }

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(ProfilActivity.this, ConnexionActivity.class);
            startActivity(intent);
            finish();
        }

        mDatabaseReference = firebaseDatabase.getReference("users").child(mUid);
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfilModel profilModel = dataSnapshot.getValue(ProfilModel.class);
                if (dataSnapshot.child("pseudo").getValue() != null) {
                    mEditPseudo.setText(profilModel.getPseudo());
                }
                if (dataSnapshot.child("avatar").getValue() != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    String url = profilModel.getAvatar();
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
                Intent intent = new  Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                    }

                    if (photoFile != null) {
                        mFileUri = FileProvider.getUriForFile(ProfilActivity.this,
                                "fr.wildcodeschool.variadis",
                                photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                        startActivityForResult(intent, APP_PHOTO);

                    }

                }
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
                Toast.makeText(ProfilActivity.this, R.string.pseudo_enregistre, Toast.LENGTH_SHORT).show();
            }
        });

        // Test pour afficher les badges avec des points fictifs
        tvPoints.setText("12");
        int points = Integer.parseInt(tvPoints.getText().toString());

        if (points > 0 && points <= 10) {
            badge1.setVisibility(View.VISIBLE);
        } else if (points <= 50) {
            badge1.setVisibility(View.VISIBLE);
            badge2.setVisibility(View.VISIBLE);
        } else if (points > 50 && points <= 100) {
            badge1.setVisibility(View.VISIBLE);
            badge2.setVisibility(View.VISIBLE);
            badge3.setVisibility(View.VISIBLE);
        } else if (points > 100 && points <= 200) {
            badge1.setVisibility(View.VISIBLE);
            badge2.setVisibility(View.VISIBLE);
            badge3.setVisibility(View.VISIBLE);
            badge4.setVisibility(View.VISIBLE);
        } else if (points < 200) {
            badge1.setVisibility(View.VISIBLE);
            badge2.setVisibility(View.VISIBLE);
            badge3.setVisibility(View.VISIBLE);
            badge4.setVisibility(View.VISIBLE);
            badge5.setVisibility(View.VISIBLE);
        }

        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, ConnexionActivity.class);
                startActivity(intent);
                auth.signOut();
            }
        });

        ivHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilActivity.this, HerbariumActivity.class);
                startActivity(intent);
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilActivity.this.startActivity(new Intent(ProfilActivity.this, MapsActivity.class));
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
                SingletonClass singletonClass = SingletonClass.getInstance();
                singletonClass.setProfil(profilModel);
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
        if (sBackPress + 2000 > System.currentTimeMillis()) {
            System.exit(0);
            super.onBackPressed();
        } else

            Toast.makeText(getBaseContext(), R.string.back_again, Toast.LENGTH_SHORT).show();
        sBackPress = System.currentTimeMillis();
    }

    // Méthodes relatives à l'avatar

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

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
