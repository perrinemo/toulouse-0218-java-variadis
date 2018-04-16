package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by perrine on 09/04/18.
 */

public class DefiHelper {

    public static Dialog openDialogDefi(Context context, String vegetal, final LatLng location, final GoogleMap map) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.dialog_defi, null);

        TextView textView = subView.findViewById(R.id.vegetal_defi);
        textView.setText(vegetal);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(subView);


        builder.setCancelable(false)
                .setPositiveButton(R.string.visualiser_vegetal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map.moveCamera(CameraUpdateFactory.newLatLng(location));

                    }
                })
                .setNegativeButton(R.string.fermer_popup, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }


}
