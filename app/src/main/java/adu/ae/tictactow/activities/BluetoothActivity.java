package adu.ae.tictactow.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import adu.ae.tictactow.R;
import adu.ae.tictactow.customClasses.DeviceAdapter;
import adu.ae.tictactow.customClasses.EstablishingConnectionDialog;
import adu.ae.tictactow.multiplayerThreads.ClientThread;
import adu.ae.tictactow.multiplayerThreads.ServerThread;
import adu.ae.tictactow.utils.Device;


public class BluetoothActivity extends AppCompatActivity {


    private String bluetoothActivityTag = "BluetoothActivity";
    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView deviceRecyclerView;
    private DeviceAdapter deviceAdapter;
    private RecyclerView.LayoutManager deviceLayoutManager;
    private LinearLayout mainLinearLayout;
    private TextView availablePlayerTextView, searchPlayerTextView;
    private ImageView reloadImageView1, reloadImageView2;
    private int REQUEST_ENABLE_BT = 1;
    private int REQUEST_ENABLE_DISCOVERABLE = 2;
    private BroadcastReceiver receiver;
    private ArrayList<Device> bluetoothDevicesList;
    private Handler startMultiplayerTestHandler;
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private EstablishingConnectionDialog establishingConnectionDialog = new EstablishingConnectionDialog();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();

        bluetoothDevicesList = new ArrayList<>();
        startMultiplayerTestHandler = new Handler();


        setImmersiveMode();
        setViews();
        turnOnBluetooth();
        enableDiscoverability();
        setListeners();



    }

    private void setImmersiveMode(){

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    private void setViews() {
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        availablePlayerTextView = findViewById(R.id.availablePlayerTextView);
        searchPlayerTextView = findViewById(R.id.searchPlayersTextView);
        reloadImageView1 = findViewById(R.id.reloadImageView1);
        reloadImageView2 = findViewById(R.id.reloadImageView2);


        deviceRecyclerView = findViewById(R.id.deviceRecyclerView);
        deviceAdapter = new DeviceAdapter(bluetoothDevicesList);
        deviceLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        deviceRecyclerView.setAdapter(deviceAdapter);
        deviceRecyclerView.setHasFixedSize(true);
        deviceRecyclerView.setLayoutManager(deviceLayoutManager);
        deviceAdapter.setOnitemClickListener(new DeviceAdapter.OnitemClickListener() {
            @Override
            public void onItemClick(int position) {
                Device device = bluetoothDevicesList.get(position);
                Toast.makeText(BluetoothActivity.this, "MAC ADDRESS IS : " + device.getMacAddress(), Toast.LENGTH_SHORT).show();
                clientThread = new ClientThread(device.getBluetoothDevice(), bluetoothAdapter, startMultiplayerTestHandler, BluetoothActivity.this, establishingConnectionDialog);
                clientThread.start();
                openDialog();

            }
        });
    }

    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Your device does not have bluetooth functionality", Toast.LENGTH_SHORT).show();
            finish();
        } else if (!bluetoothAdapter.isEnabled()) {

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        }
    }

    private void enableDiscoverability() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERABLE);
    }

    private void setListeners() {

        reloadImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadClicked();
            }
        });

        reloadImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reloadClicked();

            }
        });
    }

    private void reloadClicked() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        bluetoothDevicesList.clear();
        deviceAdapter.notifyDataSetChanged();
        startDiscovery();
        searchPlayerTextView.setText("Searching for players...");
        makeViewsInvisibile();
    }

    private void discoverBLuetoothDevices() {

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART){

                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address

                        bluetoothDevicesList.add(new Device(deviceName, deviceHardwareAddress, device));
                        deviceAdapter.notifyDataSetChanged();
                        makeViewsVisibile();

                    }

                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        checkBTPermissions();

        startDiscovery();

    }

    private void makeViewsVisibile() {

        searchPlayerTextView.setVisibility(View.INVISIBLE);

        availablePlayerTextView.setVisibility(View.VISIBLE);
        reloadImageView1.setVisibility(View.VISIBLE);
        deviceRecyclerView.setVisibility(View.VISIBLE);

        mainLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void makeViewsInvisibile() {

        searchPlayerTextView.setVisibility(View.VISIBLE);

        availablePlayerTextView.setVisibility(View.GONE);
        reloadImageView1.setVisibility(View.GONE);
        deviceRecyclerView.setVisibility(View.GONE);

        mainLinearLayout.setGravity(Gravity.CENTER);
        reloadImageView2.setVisibility(View.GONE);
    }

    private void startDiscovery() {
        bluetoothAdapter.startDiscovery();

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                bluetoothAdapter.cancelDiscovery();

                if (bluetoothDevicesList.size() <= 0) {
                    searchPlayerTextView.setText("No Players Found");
                    reloadImageView2.setVisibility(View.VISIBLE);
                }
            }
        }.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth has been switched on", Toast.LENGTH_SHORT).show();


            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth could not be switched on", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        if (requestCode == REQUEST_ENABLE_DISCOVERABLE) {

            if (resultCode == 120) {

                discoverBLuetoothDevices();
                Toast.makeText(this, "Phone now Discoverable", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Phone not discoverable", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void openDialog() {

        establishingConnectionDialog.show(getSupportFragmentManager(), "Establishing Connection Dialog");

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                establishingConnectionDialog.dismiss();

                if (clientThread.getCouldNotConnect()) {

                    Toast.makeText(BluetoothActivity.this, "Could not establish a connection!", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();


    }


    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }


        } else {
            Log.d("BlueTooth Activity", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location Permission is required for this app to run", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();

        try{
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e){
            Log.e(bluetoothActivityTag,"Receiver not registered");
            e.printStackTrace();
        }


        if (clientThread != null)
            clientThread.cancel();

        if (serverThread != null)
            serverThread.cancel();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();

        //check here
        if (serverThread != null){
            serverThread.cancel();
        }


        if (clientThread != null)
            clientThread.cancel();

        reloadClicked();

    }


    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

        setImmersiveMode();

        if(bluetoothAdapter.isEnabled() ){

            if(serverThread == null){
                serverThread = new ServerThread(bluetoothAdapter, this, startMultiplayerTestHandler);
                serverThread.start();
            } else if(serverThread.getState() == Thread.State.TERMINATED){
                serverThread = new ServerThread(bluetoothAdapter, this, startMultiplayerTestHandler);
                serverThread.start();
            }



        }

    }
}
