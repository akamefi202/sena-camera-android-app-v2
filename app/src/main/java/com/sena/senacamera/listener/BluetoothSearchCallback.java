package com.sena.senacamera.listener;

import android.bluetooth.BluetoothDevice;

public interface BluetoothSearchCallback {
    default void onFound(BluetoothDevice device) {

    }

    default void onFailed() {

    }
}
