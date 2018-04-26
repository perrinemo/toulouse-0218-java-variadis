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
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference userRef = database.getReference("users");
    private String mUId;
    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private LatLng mMyPosition;
    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Marker> markersDefi = new ArrayList<>();
    private String mVegetalDefi;
    private boolean isPreviouslyLaunched;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private boolean mIsWaitingAPILoaded = false;
    private LatLng mLocationDefi;
    private int mProgressDefi;
    private int mRandom;
    private SharedPreferences mCurrentDefi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");
        mUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCurrentDefi = getSharedPreferences(DEFI_PREF, MODE_PRIVATE);
        mProgressDefi = mCurrentDefi.getInt(DEFI_PREF, 0);

        //Attribution d'un nouveau défi
        if (mProgressDefi == 0) {
            userRef.child(mUId).child("defiDone").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Integer> availableDefi = new ArrayList<>();
                    int i = 0;
                    boolean isDefiDone;
                    for (DataSnapshot defiSnapshot : dataSnapshot.getChildren()) {
                        isDefiDone = defiSnapshot.child("isFound").getValue(Boolean.class);
                        if (!isDefiDone) {
                            availableDefi.add(i);
                        }
                        i++;
                    }
                    Random r2 = new Random();

                    if (!availableDefi.isEmpty()) {
                        mRandom = r2.nextInt(availableDefi.size());
                        mProgressDefi = availableDefi.get(mRandom);
                        mCurrentDefi.edit().putInt(DEFI_PREF, mProgressDefi).apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


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
                DefiHelper.openDialogDefi(MapsActivity.this, mVegetalDefi, null,  mLocationDefi, mMap);
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


    /**
     * Localisation du GPS, et par défaut se met sur Toulouse
     */

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
                                mVegetalDefi = vegetalName;
                                mLocationDefi = latLng;
                                markersDefi.add(markerDefi);

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
                SharedPreferences pref = getSharedPreferences(PREF, MODE_PRIVATE);
                isPreviouslyLaunched = pref.getBoolean(PREF, false);
                if (!isPreviouslyLaunched) {
                    DefiHelper.openDialogDefi(MapsActivity.this, mVegetalDefi, null, mLocationDefi, mMap);
                    pref.edit().putBoolean(PREF, true).apply();
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

        Location loc1 = new Location("");
        loc1.setLatitude(mMyPosition.latitude);
        loc1.setLongitude(mMyPosition.longitude);

        for (int i = 0; i < markersDefi.size(); i++) {
            final Marker markerDefi = markersDefi.get(i);
            Location loc3 = new Location("");
            loc3.setLatitude(markerDefi.getPosition().latitude);
            loc3.setLongitude(markerDefi.getPosition().longitude);
            float distanceDefi = loc1.distanceTo(loc3);

            //Lorsque le défi est relevé
            if (distanceDefi < MIN_DEFI_DISTANCE) {
                userRef.child(mUId).child("defiDone").child(markerDefi.getTitle()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isFound = dataSnapshot.child("isFound").getValue(Boolean.class);
                        if (!isFound) {
                            Intent intent = new Intent(MapsActivity.this, VegetalHelperActivity.class);
                            userRef.child(mUId).child("defiDone").child(markerDefi.getTitle()).child("isFound").setValue(true);
                            mCurrentDefi.edit().clear().apply();
                            markerDefi.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
            final Marker markerFound = markersDefi.get(i);
            userRef.child(mUId).child("defiDone").child(markerFound.getTitle()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isFound = dataSnapshot.child("isFound").getValue(Boolean.class);
                    if (isFound) {
                        markerFound.setVisible(true);
                        markerFound.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        for (int i = 0; i < markers.size(); i++) {
            final Marker marker = markers.get(i);
            Location loc2 = new Location("");
            loc2.setLatitude(markers.get(i).getPosition().latitude);
            loc2.setLongitude(markers.get(i).getPosition().longitude);
            float distance = loc1.distanceTo(loc2);

            //Lorsqu'un végétal est trouvé (hors défi)
            if (distance < MIN_DEFI_DISTANCE) {
                userRef.child(mUId).child("defiDone").child(marker.getTitle()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isFound = dataSnapshot.child("isFound").getValue(Boolean.class);
                        if (!isFound) {
                            Intent intent = new Intent(MapsActivity.this, VegetalHelperActivity.class);
                            userRef.child(mUId).child("defiDone").child(marker.getTitle()).child("isFound").setValue(true);
                            startActivity(intent);
                        } else {
                            mCurrentDefi.edit().putInt(DEFI_PREF, mProgressDefi).apply();
                        }
                        marker.setVisible(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            final Marker markerFound = markers.get(i);
            userRef.child(mUId).child("defiDone").child(markerFound.getTitle()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean isFound = dataSnapshot.child("isFound").getValue(Boolean.class);
                    if (isFound) {
                        markerFound.setVisible(true);
                        markerFound.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur));
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
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.quitter)
                .setMessage(R.string.confirm_quit)
                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        MapsActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.non, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }


}
