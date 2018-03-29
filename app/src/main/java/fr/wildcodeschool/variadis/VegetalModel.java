package fr.wildcodeschool.variadis;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by wilder on 23/03/18.
 */

public class VegetalModel {

    private int picture;
    private String name;
    private int idDatabase;
    private LatLng coordonates;

    public VegetalModel(int picture, String name) {
        this.picture = picture;
        this.name = name;
    }

    public VegetalModel(int picture, String name, int idDatabase, LatLng coordonates) {
        this.picture = picture;
        this.name = name;
        this.idDatabase = idDatabase;
        this.coordonates = coordonates;
    }



    public int getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }
    public int getIdDatabase(){
        return idDatabase;
    }
    public  LatLng getCoordonates(){
        return coordonates;
    }

}
