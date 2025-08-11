package com.sena.senacamera.listener;

import android.bluetooth.BluetoothDevice;

import com.sena.senacamera.data.entity.BluetoothDeviceInfo;

public interface BluetoothSearchCallback {
    default void onFound(BluetoothDeviceInfo device) {

    }

    default void onFailed() {

    }
}
