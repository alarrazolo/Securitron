package com.topprint3d.zolotec.securitron;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    ListView listview;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    ArrayList<String> btdevices;
    BluetoothConnection btconnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listview = (ListView) findViewById(R.id.list);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listItems = new ArrayList<>();
        btdevices = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                listItems);

        btconnection = new BluetoothConnection();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
        Button scan = (Button) findViewById(R.id.button1);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listItems.clear();
                btdevices.clear();
                listview.setAdapter(adapter);


                IntentFilter filter = new IntentFilter();

                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

                registerReceiver(mReceiver, filter);
                int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                mBluetoothAdapter.startDiscovery();

            }
        });

        listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        BluetoothDevice mmDevice = mBluetoothAdapter.getRemoteDevice(btdevices.get(i));
//                        Toast.makeText(MainActivity.this, mmDevice.getAddress(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, ConnectedActivity.class);
                        intent.putExtra(EXTRA_MESSAGE, mmDevice.getAddress());
                        startActivity(intent);
                    }
                }
        );

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(MainActivity.this, "Started Scanning", Toast.LENGTH_SHORT).show();
                listItems.clear();
                btdevices.clear();
                listview.setAdapter(adapter);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(MainActivity.this, "Stopped Scanning", Toast.LENGTH_SHORT).show();

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        listItems.add(device.getName());
                        btdevices.add(device.getAddress());
                    }
                    listview.setAdapter(adapter);
                }

                listview.setAdapter(adapter);

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                listItems.add(device.getName());
                btdevices.add(device.getAddress());
                listview.setAdapter(adapter);
            }
        }
    };

    @Override
    protected void onDestroy() {
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
