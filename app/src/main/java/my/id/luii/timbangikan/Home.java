package my.id.luii.timbangikan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import my.id.luii.timbangikan.fragmentdir.FragmentAkun;
import my.id.luii.timbangikan.fragmentdir.FragmentHome;
import my.id.luii.timbangikan.fragmentdir.FragmentIkan;
import my.id.luii.timbangikan.fragmentdir.FragmentInput;
import my.id.luii.timbangikan.fragmentdir.FragmentTransaksi;

public class Home extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {

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

    final FragmentHome fragmentHome = new FragmentHome();
    final FragmentIkan fragmentIkan = new FragmentIkan();
    final FragmentInput fragmentInput = new FragmentInput();
    final FragmentTransaksi fragmentTransaksi = new FragmentTransaksi();
    final FragmentAkun fragmentAkun = new FragmentAkun();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentHome)
                    .commit();
            return true;
        } else if (id == R.id.ikan){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentIkan)
                    .commit();
            return true;
        } else if (id == R.id.add){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentInput)
                    .commit();
            return true;
        } else if (id == R.id.transaksi){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentTransaksi)
                    .commit();
            return true;
        } else if (id == R.id.akun){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, fragmentAkun)
                    .commit();
            return true;
        }
        return false;
    }
}