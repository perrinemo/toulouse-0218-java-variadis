package fr.wildcodeschool.variadis;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class VegetalModel implements Parcelable {


    public static final Creator<VegetalModel> CREATOR = new Creator<VegetalModel>() {
        @Override
        public VegetalModel createFromParcel(Parcel in) {
            return new VegetalModel(in);
        }

        @Override
        public VegetalModel[] newArray(int size) {
            return new VegetalModel[size];
        }
    };
    private int picture;
    private Bitmap bitmapPicture;
    private String name;
    private String address;
    private boolean isFound;
    private String date;
    private int idDatabase;
    private LatLng coordinates;

    public VegetalModel(int picture, String name) {
        this.picture = picture;
        this.name = name;
    }

    public VegetalModel(Bitmap bitmapPicture, String name, String address, String date, boolean isFound) {
        this.bitmapPicture = bitmapPicture;
        this.name = name;
        this.address = address;
        this.date = date;
        this.isFound = isFound;

    }

    public VegetalModel() {

    }

    protected VegetalModel(Parcel in) {
        picture = in.readInt();
        bitmapPicture = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        address = in.readString();
        idDatabase = in.readInt();
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static Creator<VegetalModel> getCREATOR() {
        return CREATOR;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getBitmapPicture() {
        return bitmapPicture;
    }

    public void setBitmapPicture(Bitmap bitmapPicture) {
        this.bitmapPicture = bitmapPicture;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdDatabase() {
        return idDatabase;
    }

    public void setIdDatabase(int idDatabase) {
        this.idDatabase = idDatabase;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(picture);
        dest.writeParcelable(bitmapPicture, flags);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeInt(idDatabase);
        dest.writeParcelable(coordinates, flags);
    }
}
