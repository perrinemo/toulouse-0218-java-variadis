package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class InscriptionActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private TextView mError;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mEmail = findViewById(R.id.edit_email);
        mPassword = findViewById(R.id.edit_password);
        mError = findViewById(R.id.msg_error);
        Button incription = findViewById(R.id.btn_auth);
        Button dejaInscrit = findViewById(R.id.btn_deja_inscrit);

        mAuth = FirebaseAuth.getInstance();

        dejaInscrit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InscriptionActivity.this, ConnexionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        incription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = mEmail.getText().toString();
                String pass = mPassword.getText().toString();

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), R.string.entrer_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), R.string.entrer_mot_de_passe, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pass.length() < 6) {
                    mError.setVisibility(View.VISIBLE);
                }

                mAuth.createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener(InscriptionActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(InscriptionActivity.this, getString(R.string.mauvaise_authentification) + task.getException(), Toast.LENGTH_LONG).show();
                                } else {
                                    Intent intent = new Intent(InscriptionActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
}
