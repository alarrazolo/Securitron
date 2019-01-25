package com.topprint3d.zolotec.securitron;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class ConnectedActivity extends AppCompatActivity {

    boolean btIsChecked = false;
    BluetoothConnection btConnection;
    RelativeLayout layout_joystick;
    JoyStick js;
    TextView value;
    String macAddress;
    WebView webView;
    String videopath;
    int webWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        videopath = "http://192.168.2.143:8000/stream.mp4";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        webView = (WebView) findViewById(R.id.vision);
        webView.setEnabled(false);
        webView.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        value = (TextView) findViewById(R.id.joyu_stick_value);
//        Intent intent = getIntent();
//        macAddress = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        macAddress = "B8:27:EB:B7:6D:62";
        btConnection = new BluetoothConnection();


        layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStick(getApplicationContext(), layout_joystick, R.mipmap.joy_stick);
        js.setStickSize(250, 250);
        js.setLayoutSize(1000, 1000);
        js.setLayoutAlpha(150);
        js.setStickAlpha(100);
        js.setOffset(90);
        js.setMinimumDistance(150);
        layout_joystick.setVisibility(View.GONE);
        layout_joystick.setEnabled(false);

        layout_joystick.setOnTouchListener(new View.OnTouchListener() {
            int oldDirection;
            public boolean onTouch(View arg0, MotionEvent arg1) {
                js.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN
                        || arg1.getAction() == MotionEvent.ACTION_MOVE) {
//                    textView1.setText("X : " + String.valueOf(js.getX()));
//                    textView2.setText("Y : " + String.valueOf(js.getY()));
//                    textView3.setText("Angle : " + String.valueOf(js.getAngle()));
//                    value.setText("Distance : " + Float.toString(js.getDistance()));
//                    Toast.makeText(ConnectedActivity.this, Float.toString(js.getDistance()), Toast.LENGTH_LONG).show();

                    int direction = js.get6Direction();
                    if(direction != oldDirection){
//                        Toast.makeText(ConnectedActivity.this, "Direction Change matha fucka", Toast.LENGTH_LONG).show();
//                        count ++;
//                        value.setText("Number of orientation changes: " + Integer.toString(count));
                        if(direction == JoyStick.STICK_UP) {
                            if(btConnection.getIsConnected()) {
                                String data = "Forward";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
                        else if(direction ==JoyStick.STICK_UPRIGHT) {
                            if(btConnection.getIsConnected()) {
                                String data = "ForwardRight";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
                        else if(direction == JoyStick.STICK_UPLEFT) {
                            if(btConnection.getIsConnected()) {
                                String data = "ForwardLeft";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
                        else if(direction == JoyStick.STICK_DOWNRIGHT) {
                            if(btConnection.getIsConnected()) {
                                String data = "BackRight";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
                        else if(direction == JoyStick.STICK_DOWN) {
                            if(btConnection.getIsConnected()) {
                                String data = "Back";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
                        else if(direction == JoyStick.STICK_DOWNLEFT) {
                            if(btConnection.getIsConnected()) {
                                String data = "BackLeft";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
//                        else if(direction == JoyStick.STICK_RIGHT) {
//                            if(btConnection.getIsConnected()) {
//                                String data = "StrongRight";
//                                btConnection.write(data);
//                                value.setText(data);
//                            }
//                        }
//                        else if(direction == JoyStick.STICK_LEFT) {
//                            if(btConnection.getIsConnected()) {
//                                String data = "StrongLeft";
//                                btConnection.write(data);
//                                value.setText(data);
//                            }
//                        }
                        else if(direction == JoyStick.STICK_NONE) {
                            if(btConnection.getIsConnected()) {
                                String data = "stop";
                                btConnection.write(data);
                                value.setText(btConnection.read());
                            }
                        }
                    }
                    oldDirection = direction;

                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
//                    value.setText("Number of orientation changes: " + Integer.toString(++count));
                    if(btConnection.getIsConnected()) {
                        String data = "stop";
                        btConnection.write(data);
                        value.setText(btConnection.read());
                    }
                }
                return true;
            }
        });

        Button send_data = (Button) findViewById(R.id.send_data);
        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btIsChecked){
                    if(btConnection.getIsConnected()){
                        EditText command = (EditText) findViewById(R.id.commands);
                        String data = command.getText().toString();
                        Toast.makeText(ConnectedActivity.this, data, Toast.LENGTH_LONG).show();
                        btConnection.write(data);
                        command.getText().clear();
                    }
                    else{
                        Toast.makeText(ConnectedActivity.this, "Device is not connected", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(ConnectedActivity.this, "Turn on switch", Toast.LENGTH_LONG).show();
                }

            }
        });

        ToggleButton toggle = (ToggleButton) findViewById(R.id.onOffButton);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
//                    Toast.makeText(ConnectedActivity.this, "Button is checked", Toast.LENGTH_LONG).show();
                    btConnection.pairDevice(macAddress);
                    btConnection.startConnection(macAddress);
                    btConnection.run();
                    if(btConnection.getIsConnected()){
                        btIsChecked = true;
                        layout_joystick.setEnabled(true);
                        layout_joystick.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.GONE);
                        webView.setEnabled(true);
                        webView.setVisibility(View.VISIBLE);
                        webView.loadUrl(videopath);
                        webWidth = 1280;
                        int scale = getScale(webWidth);
                        webView.setInitialScale(scale);
                        Toast.makeText(ConnectedActivity.this, "Connected!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        btConnection.cancel();
                        Toast.makeText(ConnectedActivity.this, "Connection Failed T.T", Toast.LENGTH_LONG).show();
                    }
                } else {
                    btIsChecked = false;
                    if(btConnection.getIsConnected()){
                        toolbar.setVisibility(View.VISIBLE);
                        layout_joystick.setEnabled(false);
                        layout_joystick.setVisibility(View.GONE);
                        webView.setEnabled(false);
                        webView.setVisibility(View.GONE);
                        Toast.makeText(ConnectedActivity.this, "Button is NOT checked", Toast.LENGTH_LONG).show();
                        btConnection.cancel();
                    }

                }
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private int getScale(int wwidth){
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int valInt = width/wwidth;
        double val = (double) valInt;
        val = val * 100d;
        return (int) val;
    }

    public void onBackPressed() {
        if(btConnection.getIsConnected()){
            btConnection.cancel();
        }
        Toast.makeText(ConnectedActivity.this, "Back Button is being Pressed!", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

}
