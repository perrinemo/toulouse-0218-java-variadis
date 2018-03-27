package fr.wildcodeschool.variadis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wilder on 23/03/18.
 */

public class GridAdapter extends ArrayAdapter<VegetalModel> {



        GridAdapter(Context context, ArrayList<VegetalModel> vegetals) {
            super(context, 0, vegetals);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            VegetalModel vegetal = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_vegetal, parent, false);

            }

            ImageView vegetalImage = convertView.findViewById(R.id.item_picture);
            TextView vegetalName = convertView.findViewById(R.id.item_name);


            vegetalImage.setImageResource(vegetal.getPicture());
            vegetalName.setText(vegetal.getName());


            return convertView;

        }
    }

