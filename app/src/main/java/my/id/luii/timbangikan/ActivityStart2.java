package my.id.luii.timbangikan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import my.id.luii.timbangikan.bluetooth.BluetoothController;
import my.id.luii.timbangikan.btviewscan.DeviceRecyclerViewAdapter;
import my.id.luii.timbangikan.btviewscan.ListInteractionListener;
import my.id.luii.timbangikan.btviewscan.RecyclerViewProgressEmptySupport;

public class ActivityStart2 extends AppCompatActivity implements ListInteractionListener<BluetoothDevice> {
    private DeviceRecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewProgressEmptySupport recyclerView;

    private BluetoothController bluetooth;

    private FloatingActionButton fab;

    private ProgressDialog bondingProgressDialog;

    private Button btnnext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2);

        this.recyclerViewAdapter = new DeviceRecyclerViewAdapter(this);
        this.recyclerView = findViewById(R.id.list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        View emptyView = findViewById(R.id.empty_list);
        this.recyclerView.setEmptyView(emptyView);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        this.recyclerView.setProgressView(progressBar);

        this.recyclerView.setAdapter(recyclerViewAdapter);

        btnnext = findViewById(R.id.next_pg1);

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

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ActivityStart2.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(ActivityStart2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setRationaleTitle("Izin dibutuhkan")
                .setRationaleMessage("Mohon untuk trima izin untuk mengakses Lokasi dan bluetooth sebagai fungsi dari aplikasi ini")
                .setDeniedTitle("Permission denied")
                .setDeniedMessage(
                        "If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setGotoSettingButtonText("Settings")
                .setPermissions(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT)
                .check();

        this.bluetooth = new BluetoothController(this, BluetoothAdapter.getDefaultAdapter(), recyclerViewAdapter);

        fab = findViewById(R.id.fab_pg1);
        fab.setOnClickListener(view -> {

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
        });

        btnnext.setOnClickListener(view -> {
            Intent intent = new Intent(this, ActivityStart3.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
        SharedPreferences.Editor BTDevices = sharedPreferences.edit();
        //Log.d(TAG, "Item clicked : " + BluetoothController.deviceToString(device));
        if (bluetooth.isAlreadyPaired(device)) {
            //Log.d(TAG, "Device already paired!");
            BTDevices.putString("bttimbangan", device.toString());
            BTDevices.apply();
            Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
            btnnext.setVisibility(View.VISIBLE);
        } else {
            //Log.d(TAG, "Device not paired. Pairing.");
            boolean outcome = bluetooth.pair(device);

            // Prints a message to the user.
            String deviceName = BluetoothController.getDeviceName(device);
            if (outcome) {
                // The pairing has started, shows a progress dialog.
                //Log.d(TAG, "Showing pairing dialog");
                bondingProgressDialog = ProgressDialog.show(this, "", "Mengkoneksikan dengan perangkat " + deviceName + "...", true, false);
            } else {
                //Log.d(TAG, "Error while pairing with device " + deviceName + "!");
                Toast.makeText(this, "Ada error saat mengkoneksikan perangkat " + deviceName + "!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void startLoading() {
        this.recyclerView.startLoading();
    }

    @Override
    public void endLoading(boolean partialResults) {
        this.recyclerView.endLoading();
    }

    @Override
    public void endLoadingWithDialog(boolean error, BluetoothDevice device) {
        if (this.bondingProgressDialog != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("BTDevices",MODE_PRIVATE);
            SharedPreferences.Editor BTDevices = sharedPreferences.edit();
            View view = findViewById(R.id.step1);
            String message;
            String deviceName = BluetoothController.getDeviceName(device);

            // Gets the message to print.
            if (error) {
                message = "Gagal mengkoneksikan dengan " + deviceName + "!";
            } else {
                BTDevices.putString("bttimbangan", device.toString());
                BTDevices.apply();
                btnnext.setVisibility(View.VISIBLE);
                message = "Sukses mengkoneksikan dengan " + deviceName + "!";
            }

            // Dismisses the progress dialog and prints a message to the user.
            this.bondingProgressDialog.dismiss();
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();

            // Cleans up state.
            this.bondingProgressDialog = null;
        }

    }

    @Override
    protected void onDestroy() {
        bluetooth.close();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Stops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
        // Cleans the view.
        if (this.recyclerViewAdapter != null) {
            this.recyclerViewAdapter.cleanView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stoops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }
}