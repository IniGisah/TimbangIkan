package my.id.luii.timbangikan.btreceive;

/**
 * When a bluetooth connection has been established, this thread sends
 * or receives a data- stream to/ from the devices connected.
 * Send or received data is displayed in the console textView.
 */

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ConnectedThreadReadWriteData extends Thread {

    // Debug
    private String tag;

    // BT
    private final BluetoothSocket mSocket;
    private final InputStream mIs;
    private final OutputStream mOs;

    float kgresult;

    my.id.luii.timbangikan.btreceive.Notify notify;

    // UI
    private Handler h;
    private TextView console;
    private String dataToSend;


    public ConnectedThreadReadWriteData(my.id.luii.timbangikan.btreceive.Notify notify, BluetoothSocket socket, Handler h, TextView console, String dataToSend) {
        this.notify = notify;
        mSocket = socket;
        this.h = h;
        this.console = console;
        this.dataToSend = dataToSend;
        InputStream tmpIs = null;
        OutputStream tmpOs = null;

        try {
            tmpIs = mSocket.getInputStream();
            //tmpOs = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.v("Connected:", e.toString());
        }
        mOs = tmpOs;
        mIs = tmpIs;
    }

    /**
     * Listen to incoming data
     */
    public void run() {

        // Debug
        tag = this.getClass().getSimpleName();

        // Notify class which created this instance. This class then
        // needs to get the instance of this thread in order to
        // communicate with it.
        notify.connectionSuccessful();

        //consoleOut("Waiting for incoming data.......\n");

        int length;
        float before;
        //float[] datareceive = new float[1];
        ArrayList<Float> datareceive = new ArrayList<>();

        try {
            while (true) {
                byte[] inBuffer = new byte[1024];
                String received = null;
                int readBytes = 0;
                length = mIs.read(inBuffer);
                length = mIs.read(inBuffer);
                while (readBytes != length) {
                    received = new String(inBuffer, 0, length);
                    readBytes++;
                }
                // Wait before next data chunk arrives.
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                }
                consoleOut(received + " kg");
                //float datakg = Float.parseFloat(received.replaceAll("[\\D]" , "" ) );
                Log.v("datakg", received + " kg");
                Log.v("msgdata", received + "Done receive");
                notify.dataReceiveDone(received);

            }
        } catch (IOException e) {
            //consoleOut("Error during data transfer. Connection has been lost!");
            Log.v("Thread Debug:", "inside catch receive data error, call needreconnect rwthread");
            notify.needReconnect(true);
        }
    }

    private void donereceive(ArrayList<Float> dataarr){
        float datatot = 0;
        for(int i=0; i<dataarr.size(); i++){
            datatot = datatot + dataarr.get(i);
        }
        kgresult = datatot / dataarr.size();
        //notify.dataReceiveDone(kgresult);
        cancel();
    }

    /**
     * Send stream to device.
     */

    public void send(String dataToSend) {
        try {
            if (dataToSend.length() > 0) {
                mOs.write(dataToSend.getBytes());
            }

            consoleOut("Send:" + dataToSend + "\n");
        } catch (IOException ee) {
            closeOutputStream();
        }
    }

    /**
     * Shows text inside console- textView
     */
    private void consoleOut(String message) {
        final String m;
        m = message;
        h.post(() -> console.setText(m));
    }

    private void closeInputStream() {
        try {
            mIs.close();
        } catch (IOException e) {
            consoleOut("Could not close input stream:" + e.toString() + "\n");
        }
    }

    private void closeOutputStream() {
        try {
            mOs.close();
        } catch (IOException e) {
            consoleOut("Could not close output stream:" + e.toString() + "\n");
        }
    }

    public void cancel() {
        //closeOutputStream();
        closeInputStream();
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.v(tag, " Error while closing Socket");
        } finally {
            this.interrupt();
        }
    }
}
