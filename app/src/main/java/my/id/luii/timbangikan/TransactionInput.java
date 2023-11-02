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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;

public class TransactionInput extends AppCompatActivity {

    String[] nelayan = { "Hasanudin - 3604300806810001", "Burhan - 3604302104730002",
            "Darwis - 3604300606790002", "Abdul Latif - 3604300502850006"};

    String[] ikan = { "lemadang Grade A", "tuna gigi anjing Grade A", "tenggiri Grade A", "marlin loreng Grade A" };

    Integer[] hargaikandata = {15000, 25000, 55000, 23000};

    Button dateButton;

    Integer berat, hargaikanint, totalharga;

    String nelayanvalue, ikanvalue;

    EscPosPrinter printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_input);

        Spinner nelayanspin = findViewById(R.id.nelayansel);
        Spinner ikanspin = findViewById(R.id.ikansel);
        TextView hargaikan = findViewById(R.id.harga);
        Button beratcount = findViewById(R.id.beratbutton);
        TextView berattext = findViewById(R.id.berat);

        Button simpan = findViewById(R.id.simpan);

        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        String btprinter = sharedPreferences.getString("btprinter", "");

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothDevice bluetoothDevice = bluetoothManager.getAdapter().getRemoteDevice(btprinter);

        BluetoothConnection btprinterconnect = new BluetoothConnection(bluetoothDevice);

        try {
            printer = new EscPosPrinter(btprinterconnect, 203, 48f, 32);
        } catch (EscPosConnectionException e) {
            throw new RuntimeException(e);
        }

        ActivityResultLauncher<Intent> beratGetLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        berattext.setText(data.getStringExtra("result"));
                        berat = Integer.parseInt(data.getStringExtra("result"));
                        initPriceCount();
                    }
                });

        beratcount.setOnClickListener(v -> {
            Intent intent =  new Intent(TransactionInput.this,WeightInput.class);
            //startActivityForResult(intent, 1);
            beratGetLauncher.launch(intent);
        });

        simpan.setOnClickListener(v -> {
            try {
                printer.printFormattedText(
                        "[C]<u><font size='big'>TimbangIkan</font></u>\n" +
                        "[L]\n" +
                        "[C]================================\n" +
                        "[L]<font size='tall'>Nelayan :</font>\n" +
                        "[L]" + nelayanvalue + "\n" +
                        "[C]--------------------------------\n" +
                        "[L]<b>Jenis Ikan</b>[R]Rp " + hargaikanint +"\n" +
                        "[L]  +" + ikanvalue +"\n" +
                        "[L]<b>Berat</b>[R]" + berat +"kg\n" +
                        "[C]--------------------------------\n" +
                        "[R]TOTAL HARGA :[R]"+ totalharga +"\n" +
                        "[L]\n" +
                        "[C]================================\n" +
                        "[L]\n"
                );
            } catch (EscPosConnectionException | EscPosParserException | EscPosEncodingException | EscPosBarcodeException e) {
                throw new RuntimeException(e);
            }
        });

        nelayanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nelayanvalue = parent.getItemAtPosition(position).toString();
                Toast.makeText(getApplicationContext(), "Nelayan : " + nelayanvalue, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        ikanspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ikanvalue = parent.getItemAtPosition(position).toString();
                hargaikanint = hargaikandata[position];
                hargaikan.setText(ikanvalue + " | Rp " + hargaikandata[position] + " /kg");
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

        dateButton = findViewById(R.id.tanggalbutton);
        dateButton.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(TransactionInput.this,
                    (view, year1, monthOfYear, dayOfMonth) -> dateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1),
                    year, month, day);
            datePickerDialog.show();
        });
    }

    private void initPriceCount() {
        TextView hargatext = findViewById(R.id.totalharga);
        totalharga = hargaikanint * berat;
        String hargastr = totalharga.toString();
        hargatext.setText("Rp " + hargastr);

    }
}