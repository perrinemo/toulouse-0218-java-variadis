package fr.wildcodeschool.variadis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.Random;


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
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference userRef = database.getReference("users");
    private String mUId;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private LatLng mMyPosition;
    private ArrayList<Marker> markers = new ArrayList<>();
    private String mVegetalDefi;
    private ArrayList<Integer> mDefiDone = new ArrayList<>();
    //Attribut qui sera utile ultérieurement
    private ArrayList<VegetalModel> mFoundVegetals = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private boolean mIsWaitingAPILoaded = false;
    private LatLng mLocationDefi;
    private int mProgressDefi;
    private SharedPreferences mCurrentDefi;
    private SharedPreferences.Editor mEditCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        mCurrentDefi = getSharedPreferences(DEFI_PREF, MODE_PRIVATE);
        mProgressDefi = mCurrentDefi.getInt(DEFI_PREF, -1);
        if (mProgressDefi == -1) {
            Random r2 = new Random();
            int random = r2.nextInt(65);
            mEditCurrent = mCurrentDefi.edit();
            mEditCurrent.putInt(DEFI_PREF, random).apply();
            mProgressDefi = random;
        }
        mUId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        fireBaseReady();
    }

    public void fireBaseReady() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Vegetaux");
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

                            if (i == mProgressDefi) {
                                Marker markerDefi = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title(vegetalName).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_defi)));
                                markerDefi.setVisible(true);
                                if (mDefiDone.contains(i)) {
                                    markerDefi.setTag("found");
                                    markerDefi.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
                                }
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

    }


    /**
     * Localisation du GPS, et par défaut se met sur Toulouse
     */

    public void updateMarker(final Location location) {

        mMyPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyPosition, DEFAULT_ZOOM));

        for (int i = 0; i < markers.size(); i++) {
            Marker markerDefi = markers.get(mProgressDefi);
            Location loc1 = new Location("");
            loc1.setLatitude(mMyPosition.latitude);
            loc1.setLongitude(mMyPosition.longitude);
            Location loc2 = new Location("");
            Location loc3 = new Location("");
            loc2.setLatitude(markers.get(i).getPosition().latitude);
            loc2.setLongitude(markers.get(i).getPosition().longitude);
            loc3.setLatitude(markerDefi.getPosition().latitude);
            loc3.setLongitude(markerDefi.getPosition().longitude);
            float distance = loc1.distanceTo(loc2);
            float distanceDefi = loc1.distanceTo(loc3);
            if (distance < MIN_DEFI_DISTANCE) {
                final Marker marker = markers.get(i);
                userRef.child(mUId).child("defiDone").child(marker.getTitle()).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isFound = dataSnapshot.getValue(Boolean.class);
                        if (!isFound) {
                            Intent intent = new Intent(MapsActivity.this, VegetalHelperActivity.class);
                            userRef.child(mUId).child("defiDone").child(marker.getTitle()).setValue(true);
                            startActivity(intent);
                        } else {
                            marker.setVisible(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            if (distanceDefi < MIN_DEFI_DISTANCE && !markerDefi.getTag().equals("found")) {
                final Marker marker = markers.get(i);
                userRef.child(mUId).child("defiDone").child(marker.getTitle()).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isFound = dataSnapshot.getValue(Boolean.class);
                        if (!isFound) {
                            Intent intent = new Intent(MapsActivity.this, VegetalHelperActivity.class);
                            userRef.child(mUId).child("defiDone").child(marker.getTitle()).setValue(true);
                            startActivity(intent);
                        } else {
                            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mEditCurrent.clear().apply();
            }
            final Marker marker = markers.get(i);
            userRef.child(mUId).child("defiDone").child(marker.getTitle()).orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isFound = dataSnapshot.getValue(Boolean.class);
                    if (isFound) {
                        marker.setVisible(true);
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
