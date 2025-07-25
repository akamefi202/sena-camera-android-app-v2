package com.sena.senacamera.data.entity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

public class CameraDeviceInfo {
    public String bleName, bleAddress;
    public String wifiSsid, wifiPassword;
    public String firmwareVerison;
    public boolean connected = false;

    public CameraDeviceInfo(String bleName, String bleAddress, String wifiSsid, String wifiPassword, String firmwareVerison) {
        this.bleName = bleName;
        this.bleAddress = bleAddress;
        this.wifiSsid = wifiSsid;
        this.wifiPassword = wifiPassword;
        this.firmwareVerison = firmwareVerison;
    }
}
