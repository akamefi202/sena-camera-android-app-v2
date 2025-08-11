package com.sena.senacamera.data.entity;

public class CameraDeviceInfo {
    public String bleName, bleAddress;
    public String wifiSsid, wifiPassword;
    public String firmwareVersion;
    public String serialData;
    public boolean connected = false, passwordConfirmed = false;

    public CameraDeviceInfo(String bleName, String bleAddress, String wifiSsid, String wifiPassword, String firmwareVersion, String serialData) {
        this.bleName = bleName;
        this.bleAddress = bleAddress;
        this.wifiSsid = wifiSsid;
        this.wifiPassword = wifiPassword;
        this.firmwareVersion = firmwareVersion;
        this.serialData = serialData;
    }

    public String getProductId() {
        String[] array = serialData.split("-");
        if (array.length != 2) {
            return null;
        }
        return array[0];
    }

    public String getSerialNumber() {
        String[] array = serialData.split("-");
        if (array.length != 2) {
            return null;
        }
        return array[1];
    }
}
