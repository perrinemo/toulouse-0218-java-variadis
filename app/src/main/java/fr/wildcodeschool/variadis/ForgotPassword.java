package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static fr.wildcodeschool.variadis.MapsActivity.sBackPress;

/**
 * Created by perrine on 16/04/18.
 */

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        final EditText etEmail = findViewById(R.id.edit_mail);
        Button reset = findViewById(R.id.btn_reset);
        Button back = findViewById(R.id.btn_back);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, ConnexionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPassword.this, R.string.entrer_email, Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, R.string.mail_envoye, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPassword.this, R.string.mail_inconnu, Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
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
