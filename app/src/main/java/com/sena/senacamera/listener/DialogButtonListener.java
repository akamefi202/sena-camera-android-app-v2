package com.sena.senacamera.listener;

import android.bluetooth.BluetoothDevice;

public interface DialogButtonListener {
    default void onCancel() {}
    default void onDelete() {}
    default void onSelect() {}
    default void onConfirm() {}
    default void onContinue() {}
    default void onClose() {}
    default void onReset() {}
    default void onOk() {}
    default void onDone() {}
    default void onDone(String param) {}
    default void onUpdate() {}
    default void onStop() {}
}
