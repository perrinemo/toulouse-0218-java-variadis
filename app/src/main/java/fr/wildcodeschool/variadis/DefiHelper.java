package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by perrine on 09/04/18.
 */

public class DefiHelper {

    public static Dialog openDialogDefi(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.dialog_defi, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(subView);

        builder.setCancelable(false)
                .setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
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
