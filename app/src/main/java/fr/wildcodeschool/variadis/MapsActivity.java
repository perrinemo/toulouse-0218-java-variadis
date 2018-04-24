package fr.wildcodeschool.variadis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import static fr.wildcodeschool.variadis.SplashActivity.PREF;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String DEFI_OK = "DEFI_OK";
    public static final String NAME = "NAME";
    public static final String DATE = "DATE";
    public static final String ADRESS = "ADRESS";
    public static final String DEFI_PREF = "DEFI";
    public static final int RADIUS_DISTANCE = 500;
    public static final int MIN_DEFI_DISTANCE = 20;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng TOULOUSE = new LatLng(43.604652, 1.444209);
    private static final float DEFAULT_ZOOM = 17;
    public static long sBackPress;


    private String mUId;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private LatLng mMyPosition;
    private ArrayList<Marker> markers = new ArrayList<>();
    private String mVegetalDefi;
    private int mRandom;
    private ArrayList<Integer> mDefiDone = new ArrayList<>();
    //Attribut qui sera utile ultérieurement
    private ArrayList<VegetalModel> mFoundVegetals = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private boolean mIsWaitingAPILoaded = false;
    private LatLng mLocationDefi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        mUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");


        // Vérifie que le GPS est actif, dans le cas contraire l'utilisateur est invité à l'activer

        if (!isLocationEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.gps_disabled_title)
                    .setMessage(R.string.gps_disabled_message)
                    .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView ivHerbier = findViewById(R.id.img_herbier);
        ivHerbier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, HerbariumActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView ivProfil = findViewById(R.id.img_profile);
        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ProfilActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView ivDefi = findViewById(R.id.img_map);
        TextView txtDefi = findViewById(R.id.txt_map);
        ivDefi.setImageResource(R.drawable.defi);
        txtDefi.setText(R.string.defis);
        ivDefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefiHelper.openDialogDefi(MapsActivity.this, mVegetalDefi, mLocationDefi, mMap);
            }
        });
    }


    /**
     * Méthode qui demande la permission d'accéder au GPS du téléphone
     */

    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    /**
     * Méthode qui récupère la réponse à la demande de permission
     */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    setDeviceLocation();
                }
            }
        }
        updateLocationUI();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getLocationPermission();

        updateLocationUI();

        //Style de la map, fichier json créé depuis mapstyle
        MapStyleOptions mapFilter = MapStyleOptions.loadRawResourceStyle(MapsActivity.this, R.raw.map_style);
        googleMap.setMapStyle(mapFilter);

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLastLocation = location;
                    updateMarker(location);

                }
            }
        });

    }


    /**
     * Localisation du GPS, et par défaut se met sur Toulouse
     */

    public void updateMarker(final Location location) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Vegetaux");
        Random r2 = new Random();
        //défi aléatoire


        mRandom = r2.nextInt(65);
        final SharedPreferences currentDefi = getSharedPreferences(DEFI_PREF, MODE_PRIVATE);
        final SharedPreferences.Editor editCurrent = currentDefi.edit();


        //recuperation des marqueurs.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int i = 0;
                for (DataSnapshot dataSnapVegetal : dataSnapshot.getChildren()) {
                    String vegetalName = dataSnapVegetal.getKey();
                    for (DataSnapshot dataSnapLatLngList : dataSnapVegetal.getChildren()) {
                        for (DataSnapshot dataSnapLatLngInfos : dataSnapLatLngList.getChildren()) {
                            String key = dataSnapLatLngInfos.getKey();
                            String address = dataSnapLatLngInfos.child("adresse").getValue(String.class);
                            double latitude = dataSnapLatLngInfos.child("latlng").child("latitude").getValue(Double.class);
                            double longitude = dataSnapLatLngInfos.child("latlng").child("longitude").getValue(Double.class);
                            LatLng latLng = new LatLng(latitude, longitude);
                            int progressDefi = currentDefi.getInt(DEFI_PREF,0 );
                            if (progressDefi != mRandom || progressDefi == 0 ) {
                                editCurrent.putInt(DEFI_PREF, mRandom);
                            }
                            if (i == progressDefi) {
                                Marker markerDefi = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(vegetalName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_defi)));
                                Toast.makeText(MapsActivity.this, latLng.toString(), Toast.LENGTH_SHORT).show();
                                markerDefi.setVisible(true);
                                markers.add(markerDefi);


                            } else {
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(vegetalName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur)));
                                marker.setVisible(false);
                                markers.add(marker);
                            }
                        }
                        i++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mMyPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyPosition, DEFAULT_ZOOM));
        int progressDefi = currentDefi.getInt(DEFI_PREF,mRandom );

        for (Marker marker : markers) {
            Marker markerDefi = markers.get(progressDefi);
            Location loc1 = new Location("");
            loc1.setLatitude(mMyPosition.latitude);
            loc1.setLongitude(mMyPosition.longitude);
            Location loc2 = new Location("");
            Location loc3 = new Location("");
            loc2.setLatitude(marker.getPosition().latitude);
            loc2.setLongitude(marker.getPosition().longitude);
            loc3.setLatitude(markerDefi.getPosition().latitude);
            loc3.setLongitude(markerDefi.getPosition().longitude);
            float distance = loc1.distanceTo(loc2);
            float distanceDefi = loc1.distanceTo(loc3);
            if (distance < MIN_DEFI_DISTANCE) {
                Intent intent = new Intent(MapsActivity.this, VegetalHelperActivity.class);
                marker.setVisible(true);
                startActivity(intent);
            } else if (distanceDefi < MIN_DEFI_DISTANCE) {
                Intent intent = new Intent(MapsActivity.this, VegetalHelperActivity.class);
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
                startActivity(intent);
                mDefiDone.add(progressDefi);
                editCurrent.clear().apply();
            }
        }


    }


    @SuppressLint("MissingPermission")
    private void setDeviceLocation() {
        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMarker(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0,
                25,
                locationListener);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TOULOUSE, DEFAULT_ZOOM));

    }


    /**
     * Si la permission au GPS est accordé, affiche la position
     * Sinon redemande à accéder à la position
     */

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                setDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    //Méthode qui vérifie si le GPS est actif
    protected boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onBackPressed() {
        if (sBackPress + 2000 > System.currentTimeMillis()) {
            System.exit(0);
            super.onBackPressed();
        } else
            Toast.makeText(getBaseContext(), R.string.back_again, Toast.LENGTH_SHORT).show();
        sBackPress = System.currentTimeMillis();
    }


}
