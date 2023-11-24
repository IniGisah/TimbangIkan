package my.id.luii.timbangikan;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONArray;
import org.json.JSONObject;

public class TransactionInput extends AppCompatActivity {

    ArrayList<String> nelayan = new ArrayList<>();
    //String[] nelayan ;//= { "Hasanudin - 3604300806810001", "Burhan - 3604302104730002","Darwis - 3604300606790002", "Abdul Latif - 3604300502850006"};
    ArrayList<String> nelayannikdata = new ArrayList<>();

    ArrayList<String> ikan = new ArrayList<>();
    //String[] ikan; //= { "lemadang Grade A", "tuna gigi anjing Grade A", "tenggiri Grade A", "marlin loreng Grade A" };

    ArrayList<Integer> hargaikandata = new ArrayList<>();
    //Integer[] hargaikandata;// = {15000, 25000, 55000, 23000};

    ArrayList<String> idikandata = new ArrayList<>();

    Integer hargaikanint;

    float berat, totalharga;

    boolean beratinputdone = false;

    String nelayanvalue, ikanvalue, currentDateandTime, idikan, nelayannik, response;

    EscPosPrinter printer;

    BluetoothDevice bluetoothDevice;

    DateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_input);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        Spinner nelayanspin = findViewById(R.id.nelayansel);
        Spinner ikanspin = findViewById(R.id.ikansel);
        TextView hargaikan = findViewById(R.id.harga);
        Button beratcount = findViewById(R.id.beratbutton);
        TextView berattext = findViewById(R.id.berat);
        TextView tanggal = findViewById(R.id.tgl);

        Button simpan = findViewById(R.id.simpan);

        SharedPreferences accessInfoPref = getSharedPreferences("authInfo",MODE_PRIVATE);
        String token = accessInfoPref.getString("token", "");
        //String token = "1|GAPn3KWdwcDHNRBPPXDPUazvnTEznUgJIj8CgO8Fdb5d4469";

        //TODO : get nelayan, ikan, harga data from API
        final Dialog loadingdialog = new Dialog(TransactionInput.this);
        loadingdialog.startLoadingdialog();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            HttpURLConnection urlConnection2 = null;
            //Background work here
            try {
                String urlnelayan = "https://siripikan.serangkab.go.id/api/fishermen?per_page=25&page=1";
                String urlikan = "https://siripikan.serangkab.go.id/api/prices?per_page=50&page=1";
                URL url = new URL(urlnelayan);
                URL url2 = new URL(urlikan);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection2 = (HttpURLConnection) url2.openConnection();

                //urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization","Bearer "+ token);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                //urlConnection2.setRequestMethod("GET");
                urlConnection2.setRequestProperty("Authorization","Bearer "+ token);
                urlConnection2.setRequestProperty("Content-Type", "application/json");
                urlConnection2.setRequestProperty("Accept", "application/json");

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                InputStream is = urlConnection.getInputStream();
                String content = convertInputStream(is, "UTF-8");
                is.close();
                InputStream is2 = urlConnection2.getInputStream();
                String content2 = convertInputStream(is2, "UTF-8");
                is2.close();
                //Log.v("response", content2);

                JSONObject obj = new JSONObject(content);
                JSONObject obj2 = new JSONObject(content2);
                JSONArray dataArray = obj.getJSONObject("data").getJSONArray("data");
                JSONArray dataArray2 = obj2.getJSONObject("data").getJSONArray("data");

                for(int i=0; i<dataArray.length(); i++){
                    JSONObject data = dataArray.getJSONObject(i);
                    nelayan.add(data.getString("name") + " - " + data.getString("nik"));
                    nelayannikdata.add(data.getString("nik"));
                }

                for(int i=0; i<dataArray2.length(); i++){
                    JSONObject data = dataArray2.getJSONObject(i);
                    ikan.add(data.getString("ikan"));
                    hargaikandata.add(data.getInt("price"));
                    idikandata.add(data.getString("unit_price_id"));
                }

            } catch (Exception e){
                e.printStackTrace();
                new MaterialAlertDialogBuilder(TransactionInput.this, R.style.AlertDialogTheme)
                        .setTitle("Koneksi server gagal")
                        .setMessage("Mohon untuk cek kembali koneksi internet anda")
                        .setPositiveButton("OK", (dialogInterface, i) -> {

                        })
                        .show();
            } finally {
                if (urlConnection != null && urlConnection2 != null) {
                    urlConnection.disconnect();
                    urlConnection2.disconnect();
                }
            }
            handler.post(() -> {
                //UI Thread work here
                loadingdialog.dismissdialog();

                nelayanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        nelayanvalue = parent.getItemAtPosition(position).toString();
                        nelayannik = nelayannikdata.get(position);
                        //Toast.makeText(getApplicationContext(), "Nelayan : " + nelayanvalue, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });
                ikanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ikanvalue = parent.getItemAtPosition(position).toString();
                        hargaikanint = hargaikandata.get(position);
                        idikan = idikandata.get(position);
                        hargaikan.setText(ikanvalue + " | Rp " + hargaikandata.get(position) + " /kg");
                        initPriceCount();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }
                });

                ArrayAdapter adnelayan = new ArrayAdapter(this,android.R.layout.simple_spinner_item,nelayan);
                ArrayAdapter adikan = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ikan);

                adnelayan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adikan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                nelayanspin.setAdapter(adnelayan);
                ikanspin.setAdapter(adikan);
            });
        });



        SwitchMaterial printSwitch = findViewById(R.id.printtoggle);
        //("yyyy.MM.dd HH:mm:ss")
        sdf = SimpleDateFormat.getDateTimeInstance();
        currentDateandTime = sdf.format(new Date());
        tanggal.setText(currentDateandTime);

        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        String btprinter = sharedPreferences.getString("btprinter", "");

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothDevice = bluetoothManager.getAdapter().getRemoteDevice(btprinter);

        printerConnect();

        ActivityResultLauncher<Intent> beratGetLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        berattext.setText(data.getStringExtra("result"));
                        berat = Float.parseFloat(Objects.requireNonNull(data.getStringExtra("result")));
                        initPriceCount();
                        printerConnect();
                        beratinputdone = true;
                    }
                });

        beratcount.setOnClickListener(v -> {
            Intent intent =  new Intent(TransactionInput.this,WeightInput.class);
            //printer.disconnectPrinter();
            intent.putExtra("hargaikan", hargaikanint.toString());
            //startActivityForResult(intent, 1);
            beratGetLauncher.launch(intent);
        });

        simpan.setOnClickListener(v -> {
            if (printSwitch.isChecked() && beratinputdone) {
                try {
                    printer.printFormattedText(
                            "[C]<u><font size='big'>TimbangIkan</font></u>\n" +
                                    "[L]\n" +
                                    "[R]" + currentDateandTime + "\n" +
                                    "[C]================================\n" +
                                    "[L]<font size='tall'>Nelayan :</font>\n" +
                                    "[L]" + nelayanvalue + "\n" +
                                    "[C]--------------------------------\n" +
                                    "[L]<b>Jenis Ikan</b>[R]Rp " + hargaikanint +"\n" +
                                    "[L]  +" + ikanvalue +"\n" +
                                    "[L]<b>Berat</b>[R]" + berat +"kg\n" +
                                    "[C]--------------------------------\n" +
                                    "[R]TOTAL HARGA :[R]Rp "+ (int) totalharga +"\n" +
                                    "[C]================================"
                    );
                } catch (EscPosConnectionException | EscPosParserException | EscPosEncodingException | EscPosBarcodeException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!beratinputdone){
                new MaterialAlertDialogBuilder(TransactionInput.this, R.style.AlertDialogTheme)
                        .setTitle("Data Belum Lengkap")
                        .setMessage("Mohon lengkapi seluruh data seblum menyimpan transaksi")
                        .setPositiveButton("OK", (dialogInterface, i) -> {

                        })
                        .show();
            } else {
                //TODO : api post to server
                loadingdialog.startLoadingdialog();
                executor.execute(() -> {
                    try {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json, text/plain, */*");
                        headers.put("Authorization","Bearer "+ token);
                        HttpPostMultipart multipart2 = new HttpPostMultipart("https://siripikan.serangkab.go.id/api/transaction/store", "utf-8", headers);
                        // Add form field
                        multipart2.addFormField("unit_price_id", idikan);
                        multipart2.addFormField("nik", nelayannik);
                        multipart2.addFormField("amount", String.valueOf(berat));

                        // Print result
                        response = multipart2.finish();

                        System.out.println(response);

                        //JSONObject obj = new JSONObject(response);

                        //String tokenresult = obj.getString("token");
                        //Integer code = obj.getInt("code");
                        //int code = client.getResponseCode();

                        handler.post(() -> {
                            loadingdialog.dismissdialog();
                            new MaterialAlertDialogBuilder(TransactionInput.this, R.style.AlertDialogTheme)
                                    .setTitle("Input data sukses!")
                                    .setPositiveButton("OK", (dialogInterface, i) -> {

                                    })
                                    .show();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.post(() -> {
                                new MaterialAlertDialogBuilder(TransactionInput.this, R.style.AlertDialogTheme)
                                        .setTitle("Input data gagal")
                                        .setMessage("Silahkan cek kembali koneksi internet anda, atau tunggu beberapa saat")
                                        .setPositiveButton("OK", (dialogInterface, i) -> {

                                        })
                                        .show();
                                loadingdialog.dismissdialog();
                            }
                        );
                    }
                });
            }

        });
    }

    private String convertInputStream(InputStream is, String encoding) {
        Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void initPriceCount() {
        TextView hargatext = findViewById(R.id.totalharga);
        totalharga = hargaikanint * berat;
        //Integer hargastr = (int) totalharga;
        hargatext.setText("Rp " + (int) totalharga);
    }

    private void printerConnect(){
        SwitchMaterial printSwitch = findViewById(R.id.printtoggle);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                BluetoothConnection btprinterconnect = new BluetoothConnection(bluetoothDevice);
                printer = new EscPosPrinter(btprinterconnect, 203, 48f, 32);
            } catch (EscPosConnectionException e) {
                handler.post(() -> {
                    printSwitch.setChecked(false);
                    printSwitch.setClickable(false);
                    new MaterialAlertDialogBuilder(TransactionInput.this, R.style.AlertDialogTheme)
                            .setTitle("Koneksi Printer Gagal")
                            .setMessage("Koneksi dengan Printer Thermal gagal, silahkan hidupkan printer. Jika tidak ingin menggunakan Printer tekan OK")
                            .setPositiveButton("OK", (dialogInterface, i) -> {

                            })
                            .setNegativeButton("Batal", (dialogInterface, i) -> finish())
                            .show();
                });
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        printer.disconnectPrinter();
    }
}