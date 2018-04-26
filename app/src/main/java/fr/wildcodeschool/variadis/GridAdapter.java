package fr.wildcodeschool.variadis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<VegetalModel> {


    public GridAdapter(Context context, ArrayList<VegetalModel> vegetals) {
        super(context, 0, vegetals);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        VegetalModel vegetal = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_vegetal, parent, false);

        }

        ImageView vegetalImage = convertView.findViewById(R.id.item_picture);
        TextView vegetalName = convertView.findViewById(R.id.item_name);


        Glide.with(getContext())
                .load(vegetal.getPictureUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(vegetalImage);
        vegetalName.setText(vegetal.getName());


        return convertView;

    }
}

