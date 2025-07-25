package com.sena.senacamera.listener;

public interface BluetoothCommandCallback {
    default void onSuccess(byte[] response) {

    }

    default void onFailure() {

    }

}
