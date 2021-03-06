package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import static fr.wildcodeschool.variadis.HerbariumActivity.CLASS_FROM;

public class VegetalHelperActivity {


    public static final String EXTRA_PARCEL_FOUNDVEGETAL = "EXTRA_PARCEL_FOUNDVEGETAL";
    Bitmap mBitmap;


    public static Dialog openDialogVegetal(final Context context, final String vegetal, final String url) {

        Typeface face = Typeface.createFromAsset(context.getAssets(), "classic.ttf");
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View subView = inflater.inflate(R.layout.layout_popup, null);



        final ImageView vegetalImg = subView.findViewById(R.id.img_found_vegetal);
        TextView vegetalName = subView.findViewById(R.id.vegetal_name);
        TextView congrats = subView.findViewById(R.id.congrats);
        TextView message = subView.findViewById(R.id.tv_message);
        Button goTo = subView.findViewById(R.id.btn_goto_vegetal);
        Button back = subView.findViewById(R.id.btn_quit);

        congrats.setTypeface(face);
        vegetalName.setTypeface(face);
        message.setTypeface(face);
        goTo.setTypeface(face);
        back.setTypeface(face);

        Glide.with(context.getApplicationContext()).load(url).apply(RequestOptions.circleCropTransform()).into(vegetalImg);
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

    public static Dialog openDialogDefiDone(final Context context, final String vegetal, final String url) {

        Typeface face = Typeface.createFromAsset(context.getAssets(), "classic.ttf");
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View subView = inflater.inflate(R.layout.layout_popup, null);


        TextView defiDone = subView.findViewById(R.id.congrats);
        TextView message = subView.findViewById(R.id.tv_message);
        defiDone.setText(R.string.good_game);
        message.setText(R.string.msg_defi);

        final ImageView vegetalImg = subView.findViewById(R.id.img_found_vegetal);
        TextView vegetalName = subView.findViewById(R.id.vegetal_name);
        Button goTo = subView.findViewById(R.id.btn_goto_vegetal);
        Button back = subView.findViewById(R.id.btn_quit);

        defiDone.setTypeface(face);
        message.setTypeface(face);
        vegetalName.setTypeface(face);
        goTo.setTypeface(face);
        back.setTypeface(face);

        Glide.with(context.getApplicationContext()).load(url).apply(RequestOptions.circleCropTransform()).into(vegetalImg);
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
                context.startActivity(new Intent(context, MapsActivity.class));
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



}






