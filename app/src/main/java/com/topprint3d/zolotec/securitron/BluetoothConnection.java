package com.topprint3d.zolotec.securitron;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by alarr on 9/2/2017.
 * as a test
 */

public class BluetoothConnection {

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mmDevice = null;
    private BluetoothSocket mmSocket = null;
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler;
//    private Boolean isConnected = false;
    public String[] deviceList;

    private InputStream mmInStream;
    private OutputStream mmOutStream;
//    private Handler mHandler; // handler that gets info from Bluetooth service

    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;

        // ... (Add other message types here as needed.)
    }

    public BluetoothConnection() {
        if (mBluetoothAdapter == null) {
            return;
            // Device does not support Bluetooth
        }
        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            int REQUEST_ENABLE_BT = 1234;
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothAdapter.enable();
        }

    }

    public void startConnection(String device) {

        mmDevice = mBluetoothAdapter.getRemoteDevice(device);

        BluetoothSocket tmp = null;
        ParcelUuid[] PUUID = mmDevice.getUuids();
        UUID MY_UUID = PUUID[0].getUuid();

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            assert mmSocket != null;
            tmpIn = mmSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

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
//        isConnected = true;

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
    }

    public String read() {
        byte[] mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()
        StringBuilder buf = new StringBuilder();

        // Keep listening to the InputStream until an exception occurs.
//        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
//                return numBytes;
//                 Send the obtained bytes to the UI activity.
//                Message readMsg = mHandler.obtainMessage(
//                        MessageConstants.MESSAGE_READ, numBytes, -1,
//                        mmBuffer);
//                readMsg.sendToTarget();

//                for (int i = 0; i < numBytes; i++) {
//                    int b = mmBuffer[i] & 0xff;
//                    if (b < 0x10) {
//                        buf.append("0");
//                    }
//                    buf.append(Integer.toHexString(b));
//                }

//                for (int i = 0; i < numBytes; i++) {
//                    byte b = mmBuffer[i];
//                    buf.append(Byte.toString(b));
//                }
//                byte[] bytes = data.getBytes();
                String message = new String(mmBuffer);
                buf.append(message);


            }
            catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
//                break;
            }
            return buf.toString();
        }
//    }

    // Call this from the main activity to send data to the remote device.
    public void write(String data) {
        byte[] bytes = data.getBytes();
        try {
            mmOutStream.write(bytes);

            // Share the sent message with the UI activity.
//            Message writtenMsg = mHandler.obtainMessage(
//                    MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
//            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

            // Send a failure message back to the activity.
//            Message writeErrorMsg =
//                    mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
//            Bundle bundle = new Bundle();
//            bundle.putString("toast",
//                    "Couldn't send data to the other device");
//            writeErrorMsg.setData(bundle);
//            mHandler.sendMessage(writeErrorMsg);
        }
    }

    public void pairDevice(String device) {
        BluetoothDevice btdevice = mBluetoothAdapter.getRemoteDevice(device);
        try {
            Method method = btdevice.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(btdevice, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unpairDevice(String device) {
        BluetoothDevice btdevice = mBluetoothAdapter.getRemoteDevice(device);
        try {
            Method method = btdevice.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(btdevice, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
//            isConnected = false;

        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    public BluetoothSocket getMmSocket() {
        return mmSocket;
    }

    public Boolean getIsConnected() {
        return mmSocket.isConnected();
    }

    public BluetoothDevice getMmDevice() {
        return mmDevice;
    }

    @Override
    public String toString() {
        return "Update this soon!";
    }
}
