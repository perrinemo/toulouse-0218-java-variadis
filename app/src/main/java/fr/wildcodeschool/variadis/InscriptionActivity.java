package fr.wildcodeschool.variadis;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static fr.wildcodeschool.variadis.MapsActivity.sBackPress;

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
        Button incription = findViewById(R.id.btn_log);
        Button alreadyRegistered = findViewById(R.id.btn_already_registered);

        mAuth = FirebaseAuth.getInstance();

        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
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
                                    Intent intent = new Intent(InscriptionActivity.this, InfosActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });

            }
        });
    }
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.quitter)
                .setMessage(R.string.confirm_quit)
                .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        InscriptionActivity.super.onBackPressed();
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
