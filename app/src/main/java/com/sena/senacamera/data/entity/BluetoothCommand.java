package com.sena.senacamera.data.entity;

import com.sena.senacamera.listener.BluetoothCommandCallback;

public class BluetoothCommand {
    private final byte[] data;
    private final String responseCode;
    private final BluetoothCommandCallback callback;

    public BluetoothCommand(byte[] data, String responseCode, BluetoothCommandCallback callback) {
        this.data = data;
        this.responseCode = responseCode;
        this.callback = callback;
    }

    public byte[] getData() {
        return data;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public BluetoothCommandCallback getCallback() {
        return callback;
    }
}
