package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NelayanInput extends AppCompatActivity {

    ArrayList<String> nelayan = new ArrayList<>();
    //String[] nelayan ;//= { "Hasanudin - 3604300806810001", "Burhan - 3604302104730002","Darwis - 3604300606790002", "Abdul Latif - 3604300502850006"};
    ArrayList<String> nelayannikdata = new ArrayList<>();

    ListView nelayanlist;

    ListAdapter nelayanadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nelayan_input);

        nelayanlist = (ListView) findViewById(R.id.nelayanlist);

        SharedPreferences accessInfoPref = getSharedPreferences("authInfo",MODE_PRIVATE);
        String token = accessInfoPref.getString("token", "");

        final Dialog loadingdialog = new Dialog(NelayanInput.this);
        loadingdialog.startLoadingdialog(R.layout.loading);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            //Background work here
            try {
                String urlnelayan = "https://siripikan.serangkab.go.id/api/fishermen?per_page=25&page=1";
                URL url = new URL(urlnelayan);
                urlConnection = (HttpURLConnection) url.openConnection();

                //urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization","Bearer "+ token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                InputStream is = urlConnection.getInputStream();
                String content = convertInputStream(is, "UTF-8");
                is.close();
                //Log.v("response", content2);

                JSONObject obj = new JSONObject(content);
                JSONArray dataArray = obj.getJSONObject("data").getJSONArray("data");

                for(int i=0; i<dataArray.length(); i++){
                    JSONObject data = dataArray.getJSONObject(i);
                    nelayan.add(data.getString("name") + " - " + data.getString("nik"));
                    nelayannikdata.add(data.getString("nik"));
                }

            } catch (Exception e){
                e.printStackTrace();
                new MaterialAlertDialogBuilder(NelayanInput.this, R.style.AlertDialogTheme)
                        .setTitle("Koneksi server gagal")
                        .setMessage("Mohon untuk cek kembali koneksi internet anda")
                        .setPositiveButton("OK", (dialogInterface, i) -> {

                        })
                        .show();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            handler.post(() -> {
                //UI Thread work here
                loadingdialog.dismissdialog();

                nelayanadapter = new nelayanListAdapter(NelayanInput.this, nelayan, nelayannikdata);
                nelayanlist.setAdapter(nelayanadapter);
            });
        });

        nelayanlist.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(NelayanInput.this, "Nama: " + nelayan.get(i) +" - "+ nelayannikdata.get(i), Toast.LENGTH_SHORT).show();
        });

        //TODO : implement search function
    }

    private String convertInputStream(InputStream is, String encoding) {
        Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}