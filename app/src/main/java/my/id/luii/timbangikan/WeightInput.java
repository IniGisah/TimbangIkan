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

        TextView textView = findViewById(R.id.kambingkg);

        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("bttimbangan", "");

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice bluetoothDevice = myBluetooth.getRemoteDevice(jsonbt);

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
    public void dataReceiveDone(float datakg) {
        Button buttondone = findViewById(R.id.buttondone);

        clientThread.cancel();
        clientThread.interrupt();

        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        SharedPreferences.Editor IdKambing = sharedPreferences.edit();
        IdKambing.putFloat("berat", datakg);
        IdKambing.commit();

        buttondone.setVisibility(View.VISIBLE);
        Intent intent = new Intent(WeightInput.this,TransactionInput.class);
        intent.putExtra("result", String.valueOf(datakg));

        buttondone.setOnClickListener(v -> {
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    public void needReconnect(boolean hasil) {
        Button buttonretry = (Button) findViewById(R.id.buttonretry);

        TextView textView = (TextView) findViewById(R.id.kambingkg);
        SharedPreferences sharedPreferences = getSharedPreferences("IDKambing",MODE_PRIVATE);
        String jsonbt = sharedPreferences.getString("btdevice", "");

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