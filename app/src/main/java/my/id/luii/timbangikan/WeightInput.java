package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import my.id.luii.timbangikan.btreceive.ConnectedThreadReadWriteData;

public class WeightInput extends AppCompatActivity implements my.id.luii.timbangikan.btreceive.Notify {
    private Handler h;

    my.id.luii.timbangikan.btreceive.ClientThread clientThread;
    my.id.luii.timbangikan.btreceive.ConnectedThreadReadWriteData connectedThreadReadWriteData;

    BluetoothAdapter myBluetooth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        h = new Handler();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_input);

        Intent intent = getIntent();
        String hargaikan = intent.getStringExtra("hargaikan");

        TextView textView = findViewById(R.id.kambingkg);

        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("bttimbangan", "");
        Log.v("DEBUGBT", jsonbt);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice bluetoothDevice = myBluetooth.getRemoteDevice(jsonbt);

        //connectedThreadReadWriteData.send(hargaikan);

        startscan(bluetoothDevice, textView);
    }

    @Override
    public void connectionSuccessful() {
        Log.v("CON:", "Succeeded");
        connectedThreadReadWriteData = clientThread.getConnectedThread();

    }

    @Override
    public void messageIncomming(String message) {

    }

    @Override
    public void dataReceiveDone(String datakg) {
        Button buttondone = findViewById(R.id.buttondone);

        //clientThread.cancel();
        clientThread.interrupt();

        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        SharedPreferences.Editor IdKambing = sharedPreferences.edit();
        IdKambing.putString("berat", datakg);
        IdKambing.apply();

        runOnUiThread(() -> {
            buttondone.setVisibility(View.VISIBLE);

            Intent intent = new Intent(WeightInput.this,TransactionInput.class);
            intent.putExtra("result", String.valueOf(datakg));

            buttondone.setOnClickListener(v -> {
                clientThread.cancel();
                setResult(RESULT_OK, intent);
                finish();
            });
        });
    }

    @Override
    public void needReconnect(boolean hasil) {
        Button buttonretry = findViewById(R.id.buttonretry);

        TextView textView = findViewById(R.id.kambingkg);
        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("bttimbangan", "");

        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothDevice bluetoothDevice = bluetoothManager.getAdapter().getRemoteDevice(jsonbt);

        runOnUiThread(() -> {
            buttonretry.setVisibility(View.VISIBLE);
            buttonretry.setOnClickListener(v -> {
                startscan(bluetoothDevice, textView);
                buttonretry.setVisibility(View.GONE);
            });
        });

        Log.v("Thread Debug:", "inside needreconnect");

    }

    public void startscan(BluetoothDevice bluetoothDevice, TextView textView){
        clientThread = new my.id.luii.timbangikan.btreceive.ClientThread(WeightInput.this, bluetoothDevice, textView, "", h);
        clientThread.start();
    }
}