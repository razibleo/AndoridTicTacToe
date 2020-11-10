package adu.ae.tictactow.multiplayerThreads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import adu.ae.tictactow.activities.LocalMultiplayerActivity;


public class ServerThread extends Thread {

    private final String appName ="Tic Tac Toe";
    private final UUID uuid = UUID.fromString("dca0a298-61ff-42ed-83b4-8fcc213ddc35");
    private final BluetoothServerSocket mmServerSocket;
    private boolean couldNotConnect = false;
    private BluetoothSocket socket;
    private Context context;
    private Handler handler;



    public ServerThread(BluetoothAdapter bluetoothAdapter, Context context, Handler handler) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        this.context = context;
        this.handler = handler;

        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, uuid);
        } catch (IOException e) {
            Log.e("Server Thread", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
         socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e("Server Thread", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);

                try{
                    mmServerSocket.close();
                } catch (IOException e){
                    Log.e("Server Thread", "Socket's close() method failed", e);
                    couldNotConnect = true;
                    break;
                }

                break;
            }
        }
    }

    public boolean isCouldNotConnect() {
        return couldNotConnect;
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e("Server Thread", "Could not close the connect socket", e);
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket){

        LocalMultiplayerActivity.mmSocket = socket;
        LocalMultiplayerActivity.player1FirstTurn = true;
        LocalMultiplayerActivity.player1Turn = true;

        handler.post(new Runnable() {
            @Override
            public void run() {

                context.startActivity(new Intent(context,LocalMultiplayerActivity.class));
            }
        });

    }

    public BluetoothSocket getSocket() {
        return socket;
    }

}
