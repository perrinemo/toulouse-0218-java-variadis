package fr.wildcodeschool.variadis;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


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
    private int idDatabase;
    private LatLng coordonates;

    public VegetalModel(int picture, String name) {
        this.picture = picture;
        this.name = name;
    }

    public VegetalModel(Bitmap bitmapPicture, String name) {
        this.bitmapPicture = bitmapPicture;
        this.name = name;

    }

    public VegetalModel(int picture, String name, int idDatabase, LatLng coordonates) {
        this.picture = picture;
        this.name = name;
        this.idDatabase = idDatabase;
        this.coordonates = coordonates;
    }

    protected VegetalModel(Parcel in) {
        picture = in.readInt();
        bitmapPicture = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        idDatabase = in.readInt();
        coordonates = in.readParcelable(LatLng.class.getClassLoader());
    }

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
        dest.writeInt(idDatabase);
        dest.writeParcelable(coordonates, flags);
    }
}
