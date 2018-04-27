package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import static fr.wildcodeschool.variadis.HerbariumActivity.CLASS_FROM;
import static fr.wildcodeschool.variadis.MapsActivity.ADRESS;
import static fr.wildcodeschool.variadis.MapsActivity.NAME;

import static fr.wildcodeschool.variadis.HerbariumActivity.CLASS_FROM;

public class VegetalHelperActivity extends Application{

    public static final String EXTRA_PARCEL_FOUNDVEGETAL = "EXTRA_PARCEL_FOUNDVEGETAL";
    Bitmap mBitmap;


    public static Dialog openDialogVegetal(final Context context, final String vegetal, final String url) {

        final LayoutInflater inflater = LayoutInflater.from(context);
        final View subView = inflater.inflate(R.layout.layout_popup, null);



        final ImageView vegetalImg = subView.findViewById(R.id.img_found_vegetal);
        TextView vegetalName = subView.findViewById(R.id.vegetal_name);
        Button goTo = subView.findViewById(R.id.btn_goto_vegetal);
        Button back = subView.findViewById(R.id.btn_quit);

        Glide.with(context).load(url).into(vegetalImg);
        vegetalName.setText(vegetal);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(subView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        goTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Parcelable foundVegetal = new VegetalModel(url, vegetal);
                VegetalHelperActivity.goToVegetal(context, foundVegetal);
                alertDialog.dismiss();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });

        return alertDialog;
    }

    public static void goToVegetal(Context mContext, Parcelable foundVegetal) {
        Intent intent = new Intent(mContext, VegetalActivity.class);
        intent.putExtra(CLASS_FROM, "helper");
        intent.putExtra(EXTRA_PARCEL_FOUNDVEGETAL, foundVegetal);
        mContext.startActivity(intent);
    }


       /* ImageView ivVegetal = findViewById(R.id.img_found_vegetal);


        TextView txtVegetal = findViewById(R.id.vegetal_name);
        txtVegetal.setText(getIntent().getStringExtra(NAME));

        Button goToVegetal = findViewById(R.id.btn_goto_vegetal);
        goToVegetal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(VegetalHelperActivity.this, VegetalActivity.class);
                intent.putExtra(CLASS_FROM, "helper");
                startActivity(intent);
                finish();
            }
        });

        Button quit = findViewById(R.id.btn_quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VegetalHelperActivity.this, MapsActivity.class));
            }
        });*/


    }






