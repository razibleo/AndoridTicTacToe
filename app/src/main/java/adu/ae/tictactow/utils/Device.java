package adu.ae.tictactow.utils;

import android.bluetooth.BluetoothDevice;

public class Device {

    private String name;
    private String macAddress;
    private BluetoothDevice bluetoothDevice;


    public Device(String name, String macAddress, BluetoothDevice bluetoothDevice) {
        this.name = name;
        this.macAddress = macAddress;
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getName() {
        return name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
}
