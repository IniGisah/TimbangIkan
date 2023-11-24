package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Login extends AppCompatActivity {

    JSONObject jsonObject = new JSONObject();

    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameText = findViewById(R.id.email);
        EditText passwordText = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.button);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences accPref = getSharedPreferences("authInfo",MODE_PRIVATE);

        final Dialog loadingdialog = new Dialog(Login.this);

        loginBtn.setOnClickListener(v -> {
            loadingdialog.startLoadingdialog();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
                //HttpURLConnection client = null;
                try {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json, text/plain, */*");
                    HttpPostMultipart multipart = new HttpPostMultipart("https://siripikan.serangkab.go.id/api/login", "utf-8", headers);
                    // Add form field
                    multipart.addFormField("email", username);
                    multipart.addFormField("password", password);
                    // Print result
                    response = multipart.finish();

                    System.out.println(response);

                    JSONObject obj = new JSONObject(response);

                    String tokenresult = obj.getString("token");
                    Integer code = obj.getInt("code");
                    //int code = client.getResponseCode();

                    if (code == 200) {
                        handler.post(() -> loadingdialog.dismissdialog());
                        SharedPreferences.Editor accPrefeditor = accPref.edit();
                        accPrefeditor.putString("token", tokenresult);
                        accPrefeditor.apply();

                        Intent intent = new Intent(this, Home.class);
                        startActivity(intent);
                    } else {
                        throw new IOException("Login gagal : " + code);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(() -> {
                            new MaterialAlertDialogBuilder(Login.this, R.style.AlertDialogTheme)
                                .setTitle("Login Gagal")
                                .setMessage("Silahkan cek kembali email dan password anda")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .show();
                            loadingdialog.dismissdialog();
                        }
                    );
                }
            });
        });
    }
}