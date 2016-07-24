/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.akexorcist.bluetoothspp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class AutoConnectActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoconnect);

        if(!BluetoothSPP.getInstance().isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        BluetoothSPP.getInstance().setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext()
                        , "Connection lost"
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() {
                Log.i("Check", "Unable to connect");
            }
        });

        BluetoothSPP.getInstance().setAutoConnectionListener(new BluetoothSPP.AutoConnectionListener() {
            public void onNewConnection(String name, String address) {
                Log.i("Check", "New Connection - " + name + " - " + address);
            }

            public void onAutoConnectionStarted() {
                Log.i("Check", "Auto menu_connection started");
            }
        });

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                if(BluetoothSPP.getInstance().getServiceState() == BluetoothState.STATE_CONNECTED) {
                    BluetoothSPP.getInstance().disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        BluetoothSPP.getInstance().stopService();
    }

    public void onStart() {
        super.onStart();
        if(!BluetoothSPP.getInstance().isBluetoothEnabled()) {
            BluetoothSPP.getInstance().enable();
        } else {
            if(!BluetoothSPP.getInstance().isServiceAvailable()) {
                BluetoothSPP.getInstance().setupService();
                BluetoothSPP.getInstance().startService(BluetoothState.DEVICE_OTHER);
            }
            setup();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                BluetoothSPP.getInstance().connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                BluetoothSPP.getInstance().setupService();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void setup() {
        Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new OnClickListener(){
            public void onClick(View v){
                String msg = "";
                Random rand = new Random();
                int randomNum = rand.nextInt(3);
                Log.d("TEST", "randomNum: " + randomNum);
                switch(randomNum) {
                    case 0:
                        msg = "{\"sketchId\":2, \"r\":255, \"g\":0, \"b\":0}";
                        break;

                    case 1:
                        msg = "{\"sketchId\":2, \"r\":0, \"g\":255, \"b\":0}";
                        break;

                    case 2:
                        msg = "{\"sketchId\":2, \"r\":0, \"g\":0, \"b\":255}";
                        break;

                }
                BluetoothSPP.getInstance().send(msg, true);
                //BluetoothSPP.getInstance().send("Text", true);
            }
        });
        //BluetoothSPP.getInstance().autoConnect("IOIO");
    }
}
