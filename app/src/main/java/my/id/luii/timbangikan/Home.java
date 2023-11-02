package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;

public class Home extends AppCompatActivity {

    private MaterialCardView mtcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        if (!sharedPreferences.contains("bttimbangan") && !sharedPreferences.contains("btprinter")) {
            Intent intent = new Intent(this, ActivityStart1.class);
            startActivity(intent);
        }

        mtcard = findViewById(R.id.inputbutton);
        mtcard.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransactionInput.class);
            startActivity(intent);
        });
    }
}