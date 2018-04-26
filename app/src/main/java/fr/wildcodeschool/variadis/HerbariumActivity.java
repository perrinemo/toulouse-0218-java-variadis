package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static fr.wildcodeschool.variadis.MapsActivity.sBackPress;

public class HerbariumActivity extends AppCompatActivity {

    public static final String EXTRA_PARCEL_VEGETAL = "EXTRA_PARCEL_VEGETAL";
    public static final String CLASS_FROM = "CLASS_FROM";

    private String mUId ;

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
        ImageView ivMap = findViewById(R.id.img_map);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");
        mUId = auth.getCurrentUser().getUid();


        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(HerbariumActivity.this, ConnexionActivity.class);
            startActivity(intent);
            finish();
        }

        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HerbariumActivity.this, ProfilActivity.class);
                startActivity(intent);
            }
        });

        ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HerbariumActivity.this.startActivity(new Intent(HerbariumActivity.this, MapsActivity.class));
            }
        });


                    userRef.child(mUId).child("defiDone").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot defiSnapshot : dataSnapshot.getChildren()){
                                String vegetalName = defiSnapshot.getValue(String.class);

                                boolean found = defiSnapshot.child(vegetalName).child("isFound").getValue(Boolean.class);
                                String url = defiSnapshot.child(vegetalName).child("image").getValue(String.class);
                                if(found){
                                    vegetalList.add(new VegetalModel(url, vegetalName));

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                herbView.setAdapter(adapter);
                herbView.setEmptyView(emptyHerbarium);




        //TODO Remplacer les Parcelables par des requêtes Firebase
        herbView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Parcelable vegetal = new VegetalModel(vegetalList.get(i).getPictureUrl(), vegetalList.get(i).getName());
                Intent intent = new Intent(HerbariumActivity.this, VegetalActivity.class);
                intent.putExtra(CLASS_FROM, "herbarium");
                intent.putExtra(EXTRA_PARCEL_VEGETAL, vegetal);
                startActivity(intent);
            }
        });

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
                        HerbariumActivity.super.onBackPressed();
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

}
