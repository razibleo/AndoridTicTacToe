package adu.ae.tictactow.multiplayerThreads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import adu.ae.tictactow.activities.LocalMultiplayerActivity;
import adu.ae.tictactow.customClasses.EstablishingConnectionDialog;

public class ClientThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private final UUID uuid = UUID.fromString("dca0a298-61ff-42ed-83b4-8fcc213ddc35");
    private EstablishingConnectionDialog establishingConnectionDialog;
    private boolean couldNotConnect = false;
    private Handler handler;
    private Context context;

    public ClientThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, Handler handler, Context context, EstablishingConnectionDialog establishingConnectionDialog) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        this.bluetoothAdapter= bluetoothAdapter;
        this.handler = handler;
        this.context = context;
        this.establishingConnectionDialog = establishingConnectionDialog;

        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            Log.e("Client Thread", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        if(bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();

            if(establishingConnectionDialog.getDialog().isShowing())
            establishingConnectionDialog.dismiss();

        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            couldNotConnect = true;
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Client Thread", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        manageMyConnectedSocket(mmSocket);
    }

    public boolean getCouldNotConnect() {
        return couldNotConnect;
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Client Thread", "Could not close the client socket", e);
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket){

        LocalMultiplayerActivity.mmSocket = socket;
        LocalMultiplayerActivity.player1FirstTurn = false;
        LocalMultiplayerActivity.player1Turn = false;

        //call on main thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                context.startActivity(new Intent(context, LocalMultiplayerActivity.class));

            }
        });






    }
}
