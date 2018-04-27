package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

    public final static int GALLERY = 123;
    public final static int APP_PHOTO = 456;

    private ImageView mAvatar;
    private EditText mEditPseudo;
    private DatabaseReference mDatabaseUsers;
    private String mUid;
    private Uri mFileUri = null;
    private String mGetImageUrl = "";
    private boolean mIsOk;
    private int mPoints = 0;

    private String mCurrentPhotoPath;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mProgressBar = findViewById(R.id.progress_bar);

        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ImageView ivMap = findViewById(R.id.img_map);
        ImageButton deco = findViewById(R.id.btn_logout);
        final Button validPseudo = findViewById(R.id.btn_ok_pseudo);
        SingletonClass singletonClass = SingletonClass.getInstance();

        final TextView tvPoints = findViewById(R.id.text_points);
        final ImageView badge1 = findViewById(R.id.img_badge1_ok);
        final ImageView badge2 = findViewById(R.id.img_badge2_ok);
        final ImageView badge3 = findViewById(R.id.img_badge3_ok);
        final ImageView badge4 = findViewById(R.id.img_badge4_ok);
        final ImageView badge5 = findViewById(R.id.img_badge5_ok);

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

        mDatabaseUsers = firebaseDatabase.getReference("users").child(mUid);
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProfilModel profilModel = dataSnapshot.getValue(ProfilModel.class);
                if (dataSnapshot.child("pseudo").getValue() != null) {
                    mEditPseudo.setText(profilModel.getPseudo());
                }
                if (dataSnapshot.child("avatar").getValue() != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    String url = profilModel.getAvatar();
                    Glide.with(getApplicationContext())
                            .load(url)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot plante: dataSnapshot.child("defiDone").getChildren()) {
                    String nomPlante = plante.getKey().toString();
                    final DatabaseReference databaseVegetaux = firebaseDatabase.getReference("Vegetaux").child(nomPlante);
                    final boolean isfound = plante.child("isFound").getValue(Boolean.class);

                    databaseVegetaux.child("latLng").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int nbPlante;
                            if (isfound) {
                                String strPlante = String.valueOf(dataSnapshot.getChildrenCount());
                                nbPlante = Integer.parseInt(strPlante);

                                if (nbPlante == 1) {
                                    mPoints += 5;
                                } else if (nbPlante > 1 && nbPlante <= 5) {
                                    mPoints += 4;
                                } else if (nbPlante > 5 && nbPlante <= 10) {
                                    mPoints += 3;
                                } else if (nbPlante > 10 && nbPlante <= 20) {
                                    mPoints += 2;
                                } else {
                                    mPoints += 1;
                                }
                            }

                            tvPoints.setText(String.valueOf(mPoints));

                            if (mPoints > 0 && mPoints <= 10) {
                                badge1.setVisibility(View.VISIBLE);
                            } else if (mPoints > 10 && mPoints <= 50) {
                                badge1.setVisibility(View.VISIBLE);
                                badge2.setVisibility(View.VISIBLE);
                            } else if (mPoints > 50 && mPoints <= 100) {
                                badge1.setVisibility(View.VISIBLE);
                                badge2.setVisibility(View.VISIBLE);
                                badge3.setVisibility(View.VISIBLE);
                            } else if (mPoints > 100 && mPoints <= 200) {
                                badge1.setVisibility(View.VISIBLE);
                                badge2.setVisibility(View.VISIBLE);
                                badge3.setVisibility(View.VISIBLE);
                                badge4.setVisibility(View.VISIBLE);
                            } else if (mPoints > 200) {
                                badge1.setVisibility(View.VISIBLE);
                                badge2.setVisibility(View.VISIBLE);
                                badge3.setVisibility(View.VISIBLE);
                                badge4.setVisibility(View.VISIBLE);
                                badge5.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






            mAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfilActivity.this);
                    builder.setTitle(R.string.add_image)
                            .setMessage(R.string.select_resource)
                            .setPositiveButton(R.string.picture_app, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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
                                    mProgressBar.setVisibility(View.VISIBLE);
                                }
                            })
                            .setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY);
                                    mProgressBar.setVisibility(View.VISIBLE);
                                }
                            })
                            .show();

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


        deco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfilActivity.this);
                builder.setTitle(R.string.deco)
                        .setMessage(R.string.confirm_deco)
                        .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ProfilActivity.this, ConnexionActivity.class);
                                startActivity(intent);
                                auth.signOut();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        ivHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this, HerbariumActivity.class));
                finish();
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfilActivity.this, MapsActivity.class));
                finish();
            }
        });
    }

    // Méthodes relatives au pseudo
    private void createUser(String pseudo) {
        if (!TextUtils.isEmpty(mUid)) {
            ProfilModel profilModel = new ProfilModel(pseudo);
            mDatabaseUsers.child(mUid).setValue(profilModel);
            addUserChangeListener();
        }
    }

    private void addUserChangeListener() {
        mDatabaseUsers.child(mUid).addValueEventListener(new ValueEventListener() {
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.quitter)
                .setMessage(R.string.confirm_quit)
                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        ProfilActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
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
                        saveCaptureImage();
                    } else {
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case GALLERY:
                try {
                    if (resultCode == RESULT_OK) {
                        mFileUri = data.getData();
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
