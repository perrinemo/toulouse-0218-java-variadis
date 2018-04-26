package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InfosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos);

        //test
        ImageButton btnOk = findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InfosActivity.this, MapsActivity.class));
            }
        });


        // Crée une file d'attente pour les requêtes vers l'API
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=arbres-d-alignement&rows=10000";
        final Map<String, Integer> treeMap = new HashMap<>();
        // Création de la requête vers l'API, ajout des écouteurs pour les réponses et erreurs possibles
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray records = response.getJSONArray("records");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("Vegetaux");
                            final DatabaseReference userRef = database.getReference("users");
                            final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            for (int i = 0; i < records.length(); i++) {
                                JSONObject recordsInfos = (JSONObject) records.get(i);
                                JSONObject fields = recordsInfos.getJSONObject("fields");
                                String patrimoine = fields.getString("patrimoine"); // "32 PLATANE,2 IF"
                                String[] allTrees = patrimoine.split(","); // ["32 PLATANE", "2 IF"]
                                String adresse = fields.getString("adresse");
                                JSONArray coordonates = (JSONArray) fields.get("geo_point_2d");
                                String latitude = coordonates.get(0).toString();
                                String longitude = coordonates.get(1).toString();
                                double lat = Double.parseDouble(latitude);
                                double lng = Double.parseDouble(longitude);
                                final LatLng latLng = new LatLng(lat, lng);

                                int minTreeNumber = 0;
                                String minTreeName = "";
                                // parcours la liste des arbres séparés par des virgules
                                for (String tree : allTrees) {
                                    // tree = "32 PLATANE"
                                    String[] infos = tree.split(" "); // ["32", " PLATANE"]
                                    int currentTreeNumber = infos[0].isEmpty() ? 1 : Integer.parseInt(infos[0]); // 32
                                    String currentTreeName = tree.replaceAll("[0-9]", ""); // " PLATANE"
                                    currentTreeName = currentTreeName.trim(); // "PLATANE"
                                    if (minTreeNumber == 0 ||
                                            (currentTreeNumber < minTreeNumber && !currentTreeName.equals("DIVERS"))) {
                                        minTreeNumber = currentTreeNumber;
                                        minTreeName = currentTreeName;
                                    }
                                }
                                FindVegetalModel findVegetalModel = new FindVegetalModel(latLng, adresse);
                                if (!treeMap.containsKey(minTreeName)) {
                                    treeMap.put(minTreeName, minTreeNumber);
                                    //reference.child(minTreeName).child("latLng").push().setValue(findVegetalModel);
                                    userRef.child(id).child("defiDone").child(minTreeName).child("isFound").setValue(false);
                                    String minTreeImage = minTreeName.toLowerCase() + ".jpg";

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                    StorageReference imageRef = storageReference.child("vegetalpics/" + minTreeImage);
                                    final String currentTreeName = minTreeName;
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            userRef.child(id).child("defiDone").child(currentTreeName).child("image").setValue(imageUrl);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                                } else {
                                    treeMap.put(minTreeName, treeMap.get(minTreeName) + minTreeNumber);
                                    final DatabaseReference refLat = reference.child(minTreeName);
                                    //refLat.child("latLng").push().setValue(findVegetalModel);
                                }
                            }
                            Log.d("VOLLEY_SUCCESS", "onResponse: " + treeMap.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Afficher l'erreur
                        Log.d("VOLLEY_ERROR", "onErrorResponse: " + error.getMessage());
                    }
                }
        );
        // On ajoute la requête à la file d'attente
        requestQueue.add(jsonObjectRequest);
    }
}
