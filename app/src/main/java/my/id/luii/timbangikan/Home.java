package my.id.luii.timbangikan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class Home extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

    private MaterialCardView mtcard, settingcard;

    BottomNavigationView btnavbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnavbar = findViewById(R.id.bottomnav);

        btnavbar.setOnItemSelectedListener(this);
        btnavbar.setSelectedItemId(R.id.home);

        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        SharedPreferences accPref = getSharedPreferences("authInfo",MODE_PRIVATE);
        if (!sharedPreferences.contains("bttimbangan") && !sharedPreferences.contains("btprinter")) {
            Intent intent = new Intent(this, ActivityStart1.class);
            startActivity(intent);
        } else if (!accPref.contains("token")) {
            Intent intent = new Intent(this, LandingPage.class);
            startActivity(intent);
        }
    }

    FragmentHome fragmentHome = new FragmentHome();
    FragmentIkan fragmentIkan = new FragmentIkan();
    FragmentInput fragmentInput = new FragmentInput();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fragmentHome)
                        .commit();
                return true;

            case R.id.ikan:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fragmentIkan)
                        .commit();
                return true;

            case R.id.add:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fragmentInput)
                        .commit();
                return true;

            case R.id.transaksi:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, thirdFragment)
                        .commit();
                return true;

            case R.id.akun:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, thirdFragment)
                        .commit();
                return true;
        }
        return false;
    }
}