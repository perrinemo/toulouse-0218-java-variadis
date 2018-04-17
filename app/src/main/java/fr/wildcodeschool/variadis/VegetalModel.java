package fr.wildcodeschool.variadis;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;


public class VegetalModel implements Parcelable {


    private int picture;
    private Bitmap bitmapPicture;
    private String name;
    private String adress;
    private boolean isFound;

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public void setBitmapPicture(Bitmap bitmapPicture) {
        this.bitmapPicture = bitmapPicture;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setIdDatabase(int idDatabase) {
        this.idDatabase = idDatabase;
    }

    public void setCoordonates(LatLng coordonates) {
        this.coordonates = coordonates;
    }

    public String getAdress() {

        return adress;
    }

    public Date getDate() {
        return date;
    }

    public static Creator<VegetalModel> getCREATOR() {
        return CREATOR;
    }

    private Date date;
    private int idDatabase;
    private LatLng coordonates;

    public VegetalModel(int picture, String name) {
        this.picture = picture;
        this.name = name;
    }

    public VegetalModel(Bitmap bitmapPicture, String name, String adress, Date date, boolean isFound) {
        this.bitmapPicture = bitmapPicture;
        this.name = name;
        this.adress = adress;
        this.date = date;
        this.isFound = isFound;

    }


    public VegetalModel() {

    }


    protected VegetalModel(Parcel in) {
        picture = in.readInt();
        bitmapPicture = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        adress = in.readString();
        idDatabase = in.readInt();
        coordonates = in.readParcelable(LatLng.class.getClassLoader());
    }

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

    public Bitmap getBitmapPicture() {
        return bitmapPicture;
    }


    public int getPicture() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public int getIdDatabase() {
        return idDatabase;
    }

    public LatLng getCoordonates() {
        return coordonates;
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
        dest.writeString(adress);
        dest.writeInt(idDatabase);
        dest.writeParcelable(coordonates, flags);
    }
}
