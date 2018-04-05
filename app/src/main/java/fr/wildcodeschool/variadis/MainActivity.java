package fr.wildcodeschool.variadis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText enterPseudo = findViewById(R.id.edit_pseudo_main);
        Button btnGo = findViewById(R.id.btn_go);
        final TextView msgErrorPseudo = findViewById(R.id.msg_error_pseudo);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPseudo = enterPseudo.getText().toString();

                if (newPseudo.length() < 3) {
                    msgErrorPseudo.setVisibility(View.VISIBLE);
                } else {
                    msgErrorPseudo.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
