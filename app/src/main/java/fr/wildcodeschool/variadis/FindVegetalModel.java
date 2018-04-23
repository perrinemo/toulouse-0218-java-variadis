package fr.wildcodeschool.variadis;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
/**
 * Created by wilder on 19/04/18.
 */
public class FindVegetalModel {
    private  String address;
    private boolean isFound;
    private String treeName;
    private LatLng latlng;
    public FindVegetalModel(LatLng latLng, String address) {
        this.latlng = latLng;
        this.address = address;
    }
    public FindVegetalModel(String treeName, boolean isFound) {
        this.treeName = treeName;
        this.isFound = isFound;
    }
    public FindVegetalModel(){
    }
    public String getAdresse() {
        return address;
    }
    public void setAdresse(String adresse) {
        this.address = adresse;
    }
    public LatLng getLatlng() {
        return latlng;
    }
    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
