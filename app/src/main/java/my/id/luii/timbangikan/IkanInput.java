package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

public class IkanInput extends AppCompatActivity {

    ArrayList<String> ikan = new ArrayList<>();
    ArrayList<Integer> hargaikandata = new ArrayList<>();
    ArrayList<String> gradeikandata = new ArrayList<>();

    ListView ikanlist;

    ListAdapter ikanadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ikan_input);

        ikanlist = (ListView) findViewById(R.id.ikanlist);

        SharedPreferences accessInfoPref = getSharedPreferences("authInfo",MODE_PRIVATE);
        String token = accessInfoPref.getString("token", "");

        final Dialog loadingdialog = new Dialog(IkanInput.this);
        loadingdialog.startLoadingdialog(R.layout.loading);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            //Background work here
            try {
                String urlikan = "https://siripikan.serangkab.go.id/api/prices?per_page=50&page=1";
                URL url = new URL(urlikan);
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
                    ikan.add(data.getString("ikan"));
                    hargaikandata.add(data.getInt("price"));
                    gradeikandata.add(data.getString("unit_price_id"));
                }

            } catch (Exception e){
                e.printStackTrace();
                new MaterialAlertDialogBuilder(IkanInput.this, R.style.AlertDialogTheme)
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

                ikanadapter = new ikanListAdapter(IkanInput.this, ikan, gradeikandata, hargaikandata);
                ikanlist.setAdapter(ikanadapter);
            });
        });

        ikanlist.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(IkanInput.this, "Nama: " + ikan.get(i) +" - "+ gradeikandata.get(i) +" - "+ hargaikandata.get(i), Toast.LENGTH_SHORT).show();
        });

        //TODO : implement search function
    }

    private String convertInputStream(InputStream is, String encoding) {
        Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

}