package com.topprint3d.zolotec.securitron;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by alarr on 9/2/2017.
 */

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Boolean isconn = false;

    public ConnectThread(BluetoothDevice device) {

        BluetoothSocket tmp = null;
        mmDevice = device;
        ParcelUuid[] PUUID = device.getUuids();
        UUID MY_UUID = PUUID[0].getUuid();

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }
            Log.e(TAG, "Could not connect the client socket", connectException);
            return;
        }
        this.isconn = true;

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
//        ConnectedThread myconnection = new ConnectedThread(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    public BluetoothSocket getMmSocket() {
        return mmSocket;
    }

    public Boolean getIsconn() {
        return isconn;
    }
}