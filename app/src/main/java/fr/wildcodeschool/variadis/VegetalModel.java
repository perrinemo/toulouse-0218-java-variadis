package fr.wildcodeschool.variadis;

import android.graphics.drawable.Drawable;

/**
 * Created by wilder on 23/03/18.
 */

public class VegetalModel {

    private int picture;
    private String name;

    public VegetalModel(int picture, String name) {
        this.picture = picture;
        this.name = name;
    }

    public int getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }
}
