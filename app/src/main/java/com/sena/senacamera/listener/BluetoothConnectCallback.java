package com.sena.senacamera.listener;

public interface BluetoothConnectCallback {
    default void onConnected() {

    }
    default void onConnecting() {

    }
    default void onFailed() {

    }
    default void onCancelled() {

    }
}
