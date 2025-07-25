package com.sena.senacamera.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.R;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.data.entity.BluetoothCommand;
import com.sena.senacamera.listener.BluetoothCommandCallback;
import com.sena.senacamera.listener.BluetoothConnectCallback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.utils.ConvertTools;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BluetoothCommandManager {
    private final String TAG = BluetoothCommandManager.class.getSimpleName();
    private Context context;

    // instance
    private static final class InstanceHolder {
        private static final BluetoothCommandManager instance = new BluetoothCommandManager();
    }
    public static BluetoothCommandManager getInstance() {
        return InstanceHolder.instance;
    }

    private final ConcurrentLinkedQueue<BluetoothCommand> commandQueue = new ConcurrentLinkedQueue<>();
    private boolean isProcessing = false;
    private static final long connectPeriod = 60000;
    private String currentWifiSsid = "", currentWifiPassword = "", currentFirmwareVersion = "";
    private BluetoothGatt bluetoothGatt;
    public BluetoothDevice currentDevice;
    private Handler connectTimeoutHandler;
    private BottomSheetDialog connectDeviceDialog, connectFailedDialog;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic writeGattChar, notifyGattChar;
    private BluetoothConnectCallback connectCallback;

    private Runnable connectTimeoutRunnable = new Runnable() {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            AppLog.i(TAG, "stopping connect due to time out");

            connectDeviceDialog.dismiss();
            // show dialog after the previous dialog is dismissed
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                showConnectFailedDialog();
            }, 300);
        }
    };

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // update mtu size
                gatt.requestMtu(BluetoothInfo.MTU_SIZE);
                new Handler(Looper.getMainLooper()).postDelayed(gatt::discoverServices, 1000);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                clearQueue();
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
            AppLog.i(TAG, "onCharacteristicChanged value: " + ConvertTools.getHexStringFromByteArray(value));

            BluetoothCommand command = commandQueue.peek();
            if (command == null) {
                return;
            }

            // need to be lowercase to ensure accurate comparison
            String responseCode = ConvertTools.getHexStringFromByteArray(Arrays.copyOfRange(value, 4, 6)).toLowerCase();
            int status = value[6];
            AppLog.i(TAG, "onCharacteristicChanged responseCode: " + responseCode + ", status: " + status);

            if (command.getResponseCode().equals(responseCode)) {
                if (status == 0) {
                    if (command.getCallback() != null) {
                        command.getCallback().onSuccess(value);
                    }
                    commandQueue.poll(); // remove processed command
                    isProcessing = false;
                    processNextCommand(); // process next command
                } else if (responseCode.equals(BluetoothInfo.cameraWifiOnOffCmdRep) && status == 3) {
                    AppLog.i(TAG, "onCharacteristicChanged cameraWifiOnOffCmdRep already turned on");
                    if (command.getCallback() != null) {
                        command.getCallback().onSuccess(value);
                    }
                    commandQueue.poll(); // remove processed command
                    isProcessing = false;
                    processNextCommand(); // process next command
                }
            } else {
                // response is not related to command
                AppLog.i(TAG, "onCharacteristicChanged response is not related to command: " + command.getResponseCode());
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            // get the ble gatt service
            bluetoothGatt = gatt;
            bluetoothGattService = gatt.getService(UUID.fromString(BluetoothInfo.bleServiceUUID));
            // if failed, connection is failed
            if (bluetoothGattService == null) {
                AppLog.i(TAG, "connection is failed because the ble gatt service is null");
                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                onConnectionFailed();
                return;
            }

            // get write & read gatt characteristics
            notifyGattChar = bluetoothGattService.getCharacteristic(UUID.fromString(BluetoothInfo.bleNotifyCharUUID));
            writeGattChar = bluetoothGattService.getCharacteristic(UUID.fromString(BluetoothInfo.bleWriteCharUUID));

            // if failed, connection is failed
            if (notifyGattChar == null || writeGattChar == null) {
                AppLog.i(TAG, "connection is failed because notify gatt char or write gatt char is null");
                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                onConnectionFailed();
                return;
            }

            // get wifi info from camera device
            if (!gatt.setCharacteristicNotification(notifyGattChar, true)) {
                AppLog.i(TAG, "setCharacteristicNotification is failed");
                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                onConnectionFailed();
                return;
            }

            BluetoothGattDescriptor descriptor = notifyGattChar.getDescriptor(UUID.fromString(BluetoothInfo.CLIENT_CHARACTERISTIC_CONFIG_UUID));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (!gatt.writeDescriptor(descriptor)) {
                AppLog.i(TAG, "writeDescriptor is failed");
                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                onConnectionFailed();
                return;
            }

            // get wifi info (ssid & password), turn on wifi of camera device
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // get wifi info
                addCommand(BluetoothInfo.getCameraWifiInfoCommand(), BluetoothInfo.getCameraWifiInfoCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "getCameraWifiInfoCommand succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);

                        // get current wifi ssid and password
                        currentWifiSsid = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 1, 33));
                        currentWifiPassword = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 33, 65));
                        AppLog.i(TAG, "getCameraWifiInfoCommand ssid: " + currentWifiSsid + ", password: " + currentWifiPassword);
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "getCameraWifiInfoCommand failed");
                        // cancel connect timeout handler
                        connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                        onConnectionFailed();
                    }
                });
                // get firmware version
                addCommand(BluetoothInfo.getFirmwareVersionCommand(), BluetoothInfo.getFirmwareVersionCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "getFirmwareVersionCommand succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);

                        // get firmware version
                        currentFirmwareVersion = BluetoothInfo.getFirmwareVersionFromPayload(payload);
                        AppLog.i(TAG, "getFirmwareVersionCommand firmwareVersion: " + currentFirmwareVersion);
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "getCameraWifiInfoCommand failed");
                        // cancel connect timeout handler
                        connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                        onConnectionFailed();
                    }
                });
                // turn on wifi
                addCommand(BluetoothInfo.cameraWifiOnOffCommand(true), BluetoothInfo.cameraWifiOnOffCmdRep, new BluetoothCommandCallback() {
                    @SuppressLint("NewApi")
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "cameraWifiOnOffCommand succeeded");

                        // cancel connect timeout handler
                        connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);

                        // complete bluetooth connecting process by turning on wifi of camera device
                        connectDeviceDialog.dismiss();
                        if (connectCallback != null) {
                            connectCallback.onConnected();
                        }
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "cameraWifiOnOffCommand failed");
                        // cancel connect timeout handler
                        connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                        onConnectionFailed();
                    }
                });
            }, 1000);
        }
    };

    public BluetoothCommandManager() {
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCurrentWifiSsid() {
        return this.currentWifiSsid;
    }

    public String getCurrentWifiPassword() {
        return this.currentWifiPassword;
    }

    public String getCurrentFirmwareVersion() {
        return this.currentFirmwareVersion;
    }

    public void setCurrentWifiSsid(String param) {
        if (param.isEmpty()) {
            return;
        }
        this.currentWifiSsid = param;
    }

    public void setCurrentWifiPassword(String param) {
        if (param.isEmpty()) {
            return;
        }
        this.currentWifiPassword = param;
    }

    public void setCurrentFirmwareVersion(String param) {
        if (param.isEmpty()) {
            return;
        }
        this.currentFirmwareVersion = param;
    }

    public boolean isConnected() {
        return !currentWifiSsid.isEmpty() && !currentWifiPassword.isEmpty();
    }

    // add command to queue
    public void addCommand(byte[] data, String responseCode, BluetoothCommandCallback callback) {
        commandQueue.offer(new BluetoothCommand(data, responseCode, callback));
        processNextCommand();
    }

    // process next command from queue
    private synchronized void processNextCommand() {
        if (isProcessing || commandQueue.isEmpty()) {
            return;
        }

        isProcessing = true;
        BluetoothCommand command = commandQueue.peek();
        if (command != null) {
            sendCommand(command);
        }
    }

    // send command to ble camera device
    @SuppressLint("MissingPermission")
    private void sendCommand(BluetoothCommand command) {
        writeGattChar.setValue(command.getData());
        if (!bluetoothGatt.writeCharacteristic(writeGattChar)) {
            if (command.getCallback() != null) {
                command.getCallback().onFailure();
            }
            isProcessing = false;
            commandQueue.poll();
            processNextCommand();
        }
    }

    // clear queue when it is disconnecting
    public void clearQueue() {
        commandQueue.clear();
        currentWifiSsid = "";
        currentWifiPassword = "";
        isProcessing = false;
    }

    @SuppressLint("MissingPermission")
    public void connectDevice(BluetoothDevice device, BluetoothConnectCallback callback, boolean autoConnect) {
        // close previous connection if exists and clear queue
        clearQueue();
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        this.currentDevice = device;
        this.connectCallback = callback;
        showConnectDeviceDialog(autoConnect);
    }

    public void onConnectionFailed() {
        connectDeviceDialog.dismiss();
        // show dialog after the previous dialog is dismissed
        new Handler(Looper.getMainLooper()).postDelayed(this::showConnectFailedDialog, 300);
    }

    public void showConnectFailedDialog() {
        connectFailedDialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_connection_failed, null);
        connectFailedDialog.setContentView(dialogLayout);
        connectFailedDialog.show();

        Button retryButton = dialogLayout.findViewById(R.id.retry_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectFailedDialog.dismiss();
                if (connectCallback != null) {
                    connectCallback.onFailed();
                }
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectFailedDialog.dismiss();
                // show dialog after 100 ms to avoid conflict between 2 dialogs
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    showConnectDeviceDialog(true);
                }, 300);
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void showConnectDeviceDialog(boolean autoConnect) {
        connectDeviceDialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_connect_device, null);
        connectDeviceDialog.setContentView(dialogLayout);
        connectDeviceDialog.show();

        Button connectButton = dialogLayout.findViewById(R.id.connect_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);
        TextView deviceModelText = dialogLayout.findViewById(R.id.device_model_text);
        TextView deviceNameText = dialogLayout.findViewById(R.id.device_name_text);
        ProgressBar connectingProgress = dialogLayout.findViewById(R.id.connecting_progress);

        // device name
        deviceModelText.setText(currentDevice.getName());
        // device address
        deviceNameText.setText(String.format("B/D: %s", currentDevice.getAddress()));

        if (autoConnect) {
            // try connecting automatically
            // show the loading icon
            closeButton.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);
            connectingProgress.setVisibility(View.VISIBLE);

            // set connect timeout handler
            connectTimeoutHandler = new Handler();
            connectTimeoutHandler.postDelayed(connectTimeoutRunnable, connectPeriod);

            currentDevice.connectGatt(context, false, gattCallback);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectDeviceDialog.dismiss();
                if (connectCallback != null) {
                    connectCallback.onCancelled();
                }
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectCallback != null) {
                    connectCallback.onConnecting();
                }

                // show the loading icon
                closeButton.setVisibility(View.GONE);
                connectButton.setVisibility(View.GONE);
                connectingProgress.setVisibility(View.VISIBLE);

                // set connect timeout handler
                connectTimeoutHandler = new Handler();
                connectTimeoutHandler.postDelayed(connectTimeoutRunnable, connectPeriod);

                currentDevice.connectGatt(context, false, gattCallback);
            }
        });
    }
}
