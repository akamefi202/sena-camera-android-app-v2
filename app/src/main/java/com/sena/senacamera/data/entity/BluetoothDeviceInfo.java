package com.sena.senacamera.data.entity;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceInfo {
    public BluetoothDevice device;
    public String serialData;
    public String wifiPassword;

    public BluetoothDeviceInfo(BluetoothDevice device, String serialData, String wifiPassword) {
        this.device = device;
        this.serialData = serialData;
        this.wifiPassword = wifiPassword;
    }
}
