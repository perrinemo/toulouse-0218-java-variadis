package fr.wildcodeschool.variadis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static String EXTRA_PSEUDO = "EXTRA_PSEUDO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText enterPseudo = findViewById(R.id.edit_pseudo_main);
        Button btnGo = findViewById(R.id.btn_go);
        final TextView msgErrorPseudo = findViewById(R.id.msg_error_pseudo);
        final SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        String pseudoCache = sharedPreferences.getString("pseudo", "");
        enterPseudo.setText(pseudoCache);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPseudo = enterPseudo.getText().toString();

                if (newPseudo.length() < 3) {
                    msgErrorPseudo.setVisibility(View.VISIBLE);
                } else {
                    msgErrorPseudo.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("pseudo", newPseudo);
                    editor.commit();

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra(EXTRA_PSEUDO, newPseudo);
                    startActivity(intent);
                }
            }
        });
    }
}
