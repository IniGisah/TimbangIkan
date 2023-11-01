package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ActivityStart1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start1);
        Button nextButton = (Button) findViewById(R.id.next_pg1);
        nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActivityStart2.class);
            startActivity(intent);
        });

    }
}