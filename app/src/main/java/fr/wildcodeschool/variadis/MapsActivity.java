package fr.wildcodeschool.variadis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final LatLng TOULOUSE = new LatLng(43.604652, 1.444209);
    private static final float DEFAULT_ZOOM = 17;

    private boolean mLocationPermissionGranted;
    private GoogleMap mMap;
    private LatLng mMyPosition;
    private ArrayList<Marker> markers = new ArrayList<>();
    private String mVegetalDefi;
    private Random r2 = new Random();
    private int mRandom;
    private ArrayList<Integer> defiDone = new ArrayList<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private boolean mIsWaitingAPILoaded = false;

    public static final String DEFI_OK = "DEFI_OK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        apiReady();

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
            }
        });

        ImageView ivProfil = findViewById(R.id.img_profile);
        ivProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ProfilActivity.class);
                startActivity(intent);
            }
        });

        ImageView ivDefi = findViewById(R.id.img_map);
        TextView txtDefi = findViewById(R.id.txt_map);
        ivDefi.setImageResource(R.drawable.defi);
        txtDefi.setText(R.string.defis);
        ivDefi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DefiHelper.openDialogDefi(MapsActivity.this);
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

    private void apiReady() {

        //Fil d'attente API
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=arbres-d-alignement&rows=551&sort=id";

        // Création de la requête vers l'API, ajout des écouteurs pour les réponses et erreurs possibles
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray records = response.getJSONArray("records");
                            mRandom = r2.nextInt(records.length());
                            defiDone.add(mRandom);
                            for (int j = 0; j < records.length(); j++) {
                                JSONObject recordsInfo = (JSONObject) records.get(j);

                                JSONObject fields = recordsInfo.getJSONObject("fields");
                                String patrimoine = fields.getString("patrimoine");
                                String adresse = fields.getString("adresse");
                                String vegetalId = fields.getString("id");
                                JSONArray coordonates = (JSONArray) fields.get("geo_point_2d");
                                String latitude = coordonates.get(0).toString();
                                String longitude = coordonates.get(1).toString();
                                double lat = Double.parseDouble(latitude);
                                double lng = Double.parseDouble(longitude);

                                //Ajout des points de tous les végétaux sur la carte
                                //TODO: Afficher que les vegetaux trouver
                                //

                                Marker marker;
                                Marker markerDefi;
                                if (j == mRandom) {
                                    mVegetalDefi = patrimoine;
                                    DefiHelper.openDialogDefi(MapsActivity.this, patrimoine);
                                    markerDefi = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .title(patrimoine).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_defi)));
                                    markers.add(markerDefi);
                                } else {

                                    marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .title(patrimoine).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marqueur)));
                                    marker.setVisible(false);
                                    markers.add(marker);

                                }
                            }
                            if (mIsWaitingAPILoaded) {
                                updateMarker(mLastLocation);
                                mIsWaitingAPILoaded = false;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mIsWaitingAPILoaded = true;

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY_ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        // On ajoute la requête à la file d'attente
        requestQueue.add(jsonObjectRequest);
    }

    /**
     * Localisation du GPS, et par défaut se met sur Toulouse
     */

    public void updateMarker(Location location) {
        mMyPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mLastLocation = location;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMyPosition, DEFAULT_ZOOM));
        if (markers.size() == 0) {
            mIsWaitingAPILoaded = true;
        }
        for (Marker marker : markers) {
            if (marker == markers.get(mRandom)) {
                marker.setVisible(true);
            } else {
                Location loc1 = new Location("");
                loc1.setLatitude(mMyPosition.latitude);
                loc1.setLongitude(mMyPosition.longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(marker.getPosition().latitude);
                loc2.setLongitude(marker.getPosition().longitude);

                float distance = loc1.distanceTo(loc2);

                marker.setVisible(distance < 500);
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


}
