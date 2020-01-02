package com.example.bluetoothlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluetoothlibrary.Adapters.ScanAdapter;
import com.example.bluetoothlibrary.Interfaces.ListableActivity;
import com.example.bluetoothlibrary.Models.Item;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class BluetoothScan extends AppCompatActivity implements ListableActivity {

    public static String EXTRA_MAC_ADDRESS = "macaddress";
    ListView rvDiscovery;
    EditText etMacAddress;
    ArrayList<Item> list;
    ScanAdapter myAdapter;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothSocket socket;
    public static final int REQUEST_ENABLE_BT=1;
    Dialog waitingToConect;
    Item selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);

        if(getIntent().getExtras()!= null && getIntent().getExtras().containsKey(EXTRA_MAC_ADDRESS)){
            String value = getIntent().getExtras().getString(EXTRA_MAC_ADDRESS);
            selectedItem = new Item(value, "");
        }

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter != null) {
            init();
        }else{
            Toast.makeText(BluetoothScan.this, "BlueTooth no disponible", Toast.LENGTH_LONG).show();
        }
        requestPermissions();
    }

    public void init(){
        rvDiscovery =  findViewById(R.id.rvDiscovery);
        etMacAddress = findViewById(R.id.etMacAddress);

        list = new ArrayList<>();
        myAdapter = new ScanAdapter(BluetoothScan.this, this,0, list);
        rvDiscovery.setAdapter(myAdapter);

        if(selectedItem != null){
            etMacAddress.setText(selectedItem.getCode());
        }

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    saveData();
                }

            }
        });

        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });


        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBluetooth();
            }
        });
        /*goPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main.this, ActivityPrintText.class));
            }
        });*/

        waitingToConect = new Dialog(BluetoothScan.this);
        waitingToConect.setContentView(R.layout.loading);

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver myBroadcast = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                list.add(new Item(device.getAddress(), (device.getName()==null?"NO NAME":device.getName())+"\n"+device.getAddress()));
                myAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT && requestCode == RESULT_OK){
            scanBluetooth();
        }/*else{
            finish();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            // Don't forget to unregister the ACTION_FOUND receiver.
            unregisterReceiver(myBroadcast);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void requestPermissions(){
        ActivityCompat.requestPermissions(BluetoothScan.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN},
                1);
    }

    @Override
    public void onClick(Object o) {
        Item i = (Item)o;
        BluetoothDevice myParingDevice = myBluetoothAdapter.getRemoteDevice(i.getCode());

        if(selectedItem!= null && myParingDevice.getAddress().toString().equals(selectedItem.getCode()) && myParingDevice.getBondState() == BluetoothDevice.BOND_BONDED){
            Toast.makeText(BluetoothScan.this, "El dispositivo esta conectado", Toast.LENGTH_LONG).show();
        }else {
            //////////////////////////////////////////////////////////////////////////////

            AsyncTask<Item, Void, String> a = new AsyncTask<Item, Void, String>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    waitingToConect.setCancelable(false);
                    waitingToConect.show();
                }

                @Override
                protected String doInBackground(Item... items) {
                    try {
                        Item i = items[0];
                        BluetoothDevice myParingDevice = myBluetoothAdapter.getRemoteDevice(i.getCode());
                        //myParingDevice.setPairingConfirmation(true);
                        //esto es para conexiones standar a bluetooh
                        Method m = myParingDevice.getClass().getMethod("createRfcommSocket", int.class);
                        socket = (BluetoothSocket) m.invoke(myParingDevice, 1);
                        socket.connect();
                        Thread.sleep(500);
                        if (socket.isConnected()) {
                            socket.getOutputStream().write("Configurado\n\n".getBytes());
                            //macaddress = i.getCode();
                            selectedItem = i;

                            Thread.sleep(500);

                        }else{
                            selectedItem = null;
                        }
                        // el socket debe cerrarse y nulificarse siempre que se acabe de usar ya que si no se hace simpre estara caducado
                        // y sera inaccesible.
                    }catch(Exception e){
                        return  e.getMessage().toString();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s != null) {
                        Toast.makeText(BluetoothScan.this, s, Toast.LENGTH_LONG).show();
                    }
                    try {
                        socket.close();
                        socket = null;
                    }catch (Exception e){e.printStackTrace();}

                    if(selectedItem!= null){
                        etMacAddress.setText(selectedItem.getCode());
                    }

                    waitingToConect.dismiss();
                }
            };
            a.execute(i);
        }


    }




    public void scanBluetooth(){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            list.clear();
            myAdapter.notifyDataSetChanged();

            if(myBluetoothAdapter.isDiscovering()){
                myBluetoothAdapter.cancelDiscovery();
            }
            myBluetoothAdapter.startDiscovery();
            Toast.makeText(BluetoothScan.this, "Searching", Toast.LENGTH_LONG).show();

            // Register for broadcasts when a device is discovered.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(myBroadcast, filter);

        }
    }

    public boolean validate(){
        if(etMacAddress.getText().toString().trim().isEmpty()){
            Toast.makeText(BluetoothScan.this, "Device Address Can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void saveData(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_MAC_ADDRESS,selectedItem.getCode());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
