package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import my.id.luii.timbangikan.bluetooth.BluetoothController;
import my.id.luii.timbangikan.view.DeviceRecyclerViewAdapter;
import my.id.luii.timbangikan.view.ListInteractionListener;
import my.id.luii.timbangikan.view.RecyclerViewProgressEmptySupport;

public class ActivityStart2 extends AppCompatActivity implements ListInteractionListener<BluetoothDevice> {
    private DeviceRecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewProgressEmptySupport recyclerView;

    private BluetoothController bluetooth;

    private FloatingActionButton fab;

    private ProgressDialog bondingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2);

        this.recyclerViewAdapter = new DeviceRecyclerViewAdapter(this);
        this.recyclerView = (RecyclerViewProgressEmptySupport) findViewById(R.id.list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        View emptyView = findViewById(R.id.empty_list);
        this.recyclerView.setEmptyView(emptyView);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.recyclerView.setProgressView(progressBar);

        this.recyclerView.setAdapter(recyclerViewAdapter);

        boolean hasBluetooth = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (!hasBluetooth) {
            AlertDialog dialog = new AlertDialog.Builder(ActivityStart2.this).create();
            dialog.setTitle(getString(R.string.bluetooth_not_available_title));
            dialog.setMessage(getString(R.string.bluetooth_not_available_message));
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Closes the dialog and terminates the activity.
                            dialog.dismiss();
                            ActivityStart2.this.finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }

        this.bluetooth = new BluetoothController(this, BluetoothAdapter.getDefaultAdapter(), recyclerViewAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab_pg1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the bluetooth is not enabled, turns it on.
                if (!bluetooth.isBluetoothEnabled()) {
                    Snackbar.make(view, R.string.enabling_bluetooth, Snackbar.LENGTH_SHORT).show();
                    bluetooth.turnOnBluetoothAndScheduleDiscovery();
                } else {
                    //Prevents the user from spamming the button and thus glitching the UI.
                    if (!bluetooth.isDiscovering()) {
                        // Starts the discovery.
                        Snackbar.make(view, R.string.device_discovery_started, Snackbar.LENGTH_SHORT).show();
                        bluetooth.startDiscovery();
                    } else {
                        Snackbar.make(view, R.string.device_discovery_stopped, Snackbar.LENGTH_SHORT).show();
                        bluetooth.cancelDiscovery();
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        //Log.d(TAG, "Item clicked : " + BluetoothController.deviceToString(device));
        if (bluetooth.isAlreadyPaired(device)) {
            //Log.d(TAG, "Device already paired!");
            Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
        } else {
            //Log.d(TAG, "Device not paired. Pairing.");
            boolean outcome = bluetooth.pair(device);

            // Prints a message to the user.
            String deviceName = BluetoothController.getDeviceName(device);
            if (outcome) {
                // The pairing has started, shows a progress dialog.
                //Log.d(TAG, "Showing pairing dialog");
                bondingProgressDialog = ProgressDialog.show(this, "", "Pairing with device " + deviceName + "...", true, false);
            } else {
                //Log.d(TAG, "Error while pairing with device " + deviceName + "!");
                Toast.makeText(this, "Error while pairing with device " + deviceName + "!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void endLoading(boolean partialResults) {

    }

    @Override
    public void endLoadingWithDialog(boolean error, BluetoothDevice element) {

    }
}