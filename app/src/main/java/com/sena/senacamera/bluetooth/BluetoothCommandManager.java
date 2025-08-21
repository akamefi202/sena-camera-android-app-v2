package com.sena.senacamera.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.data.entity.BluetoothCommand;
import com.sena.senacamera.data.entity.BluetoothDeviceInfo;
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
    private boolean isProcessing = false, isFirstConnect = false;
    public boolean isFirmwareUpdating = false, isConnected = false;
    private static final long connectPeriod = 60000;
    private String currentWifiSsid = "", currentWifiPassword = "", currentFirmwareVersion = "";
    private BluetoothGatt bluetoothGatt;
    public BluetoothDeviceInfo currentDevice;
    private Handler connectTimeoutHandler;
    private BottomSheetDialog connectDeviceDialog, connectFailedDialog;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic writeGattChar, notifyGattChar;
    private BluetoothConnectCallback connectCallback;

    private Runnable connectTimeoutRunnable = new Runnable() {
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

            // update mtu size
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                AppLog.i(TAG, "onConnectionStateChange: STATE_CONNECTED");
                gatt.requestMtu(BluetoothInfo.MTU_SIZE);
                new Handler(Looper.getMainLooper()).postDelayed(gatt::discoverServices, 1000);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                AppLog.i(TAG, "onConnectionStateChange: STATE_DISCONNECTING");

                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                clearQueue();
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                AppLog.i(TAG, "onConnectionStateChange: STATE_DISCONNECTED");

                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                clearQueue();
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }

                // if updating firmware currently, setup bluetooth connection again
                if (isFirmwareUpdating) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        connectCallback = null;
                        currentDevice.device.connectGatt(context, false, gattCallback);
                    }, 30000);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            onCharacteristicChanged(gatt, characteristic, characteristic.getValue());
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            // need to be lowercase to ensure accurate comparison
            String responseCode = ConvertTools.getHexStringFromByteArray(Arrays.copyOfRange(value, 4, 6)).toLowerCase();
            int status = value[6];
            AppLog.i(TAG, "onCharacteristicChanged responseCode: " + responseCode + ", status: " + status);

            BluetoothCommand command = commandQueue.peek();
            if (command == null) {
                return;
            }

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
                } else if (responseCode.equals(BluetoothInfo.setCameraWifiInfoCmdRep) && status == 1 && currentDevice.getProductId().equals(BluetoothInfo.PRODUCT_ID_PHANTOM_CAMERA)) {
                    // akamefi202: to be fixed
                    AppLog.i(TAG, "onCharacteristicChanged cameraWifiOnOffCmdRep succeeded (phantom camera)");
                    if (command.getCallback() != null) {
                        command.getCallback().onSuccess(value);
                    }
                    commandQueue.poll(); // remove processed command
                    isProcessing = false;
                    processNextCommand(); // process next command
                } else if (responseCode.equals(BluetoothInfo.cameraOnOffCmdRep) && status == 1 && command.setEnabled && currentDevice.getProductId().equals(BluetoothInfo.PRODUCT_ID_PHANTOM_CAMERA)) {
                    // akamefi202: to be fixed
                    AppLog.i(TAG, "onCharacteristicChanged cameraOnOffCmdRep on succeeded (phantom camera)");
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

            isConnected = setupBluetoothConnection(gatt);
            if (!isConnected) {
                // cancel connect timeout handler
                connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                // if updating firmware, do not show connect failed dialog
                if (!isFirmwareUpdating) {
                    // if not updating firmware (normal process), show connect failed dialog
                    onConnectionFailed();
                }
                return;
            }

            // if updating firmware, do not connect via wifi
            if (!isFirmwareUpdating) {
                // if not updating firmware (normal process), connect via wifi
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    connectWifi();
                }, 1000);
            }
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

    @SuppressLint("MissingPermission")
    public boolean setupBluetoothConnection(BluetoothGatt gatt) {// get the bluetooth gatt service
        AppLog.i(TAG, "setupBluetoothConnection");
        bluetoothGatt = gatt;
        bluetoothGattService = gatt.getService(UUID.fromString(BluetoothInfo.bleServiceUUID));
        AppLog.i(TAG, "getService");
        if (bluetoothGattService == null) {
            AppLog.i(TAG, "connection is failed because the ble gatt service is null");
            return false;
        }

        // get write & read gatt characteristics
        notifyGattChar = bluetoothGattService.getCharacteristic(UUID.fromString(BluetoothInfo.bleNotifyCharUUID));
        writeGattChar = bluetoothGattService.getCharacteristic(UUID.fromString(BluetoothInfo.bleWriteCharUUID));
        AppLog.i(TAG, "getCharacteristic");
        if (notifyGattChar == null || writeGattChar == null) {
            AppLog.i(TAG, "connection is failed because notify gatt char or write gatt char is null");
            return false;
        }

        AppLog.i(TAG, "getProperties");
        if ((notifyGattChar.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            AppLog.i(TAG, "characteristic does not support notifications");
            return false;
        }

        AppLog.i(TAG, "setCharacteristicNotification");
        if (!gatt.setCharacteristicNotification(notifyGattChar, true)) {
            AppLog.i(TAG, "setCharacteristicNotification is failed");
            return false;
        }

        AppLog.i(TAG, "getDescriptor");
        BluetoothGattDescriptor descriptor = notifyGattChar.getDescriptor(UUID.fromString(BluetoothInfo.CLIENT_CHARACTERISTIC_CONFIG_UUID));
        if (descriptor == null) {
            AppLog.i(TAG, "descriptor is null");
            return false;
        }

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        AppLog.i(TAG, "writeDescriptor");
        if (!gatt.writeDescriptor(descriptor)) {
            AppLog.i(TAG, "writeDescriptor is failed");
            return false;
        }

        return true;
    }

    public void disconnectWifi() {
        // turn off wifi of camera device
        addCommand(BluetoothInfo.cameraWifiOnOffCommand(false), BluetoothInfo.cameraWifiOnOffCmdRep, false, new BluetoothCommandCallback() {
            @Override
            public void onSuccess(byte[] response) {
                AppLog.i(TAG, "cameraWifiOnOffCmdRep off succeeded");
            }

            @Override
            public void onFailure() {
                AppLog.i(TAG, "cameraWifiOnOffCmdRep off failed");
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void connectWifi() {
        // get wifi info (ssid & password), get firmware version, and turn on wifi of camera device
        // but doesn't turn on wifi if first connect, because need to set ssid & password first

        // turn off & on camera if phantom camera
        if (currentDevice.getProductId().equals(BluetoothInfo.PRODUCT_ID_PHANTOM_CAMERA)) {
            addCommand(BluetoothInfo.cameraOnOffCommand(false), BluetoothInfo.cameraOnOffCmdRep, false, new BluetoothCommandCallback() {
                @Override
                public void onSuccess(byte[] response) {
                    AppLog.i(TAG, "cameraOnOffCmdRep off succeeded");
                }

                @Override
                public void onFailure() {
                    AppLog.i(TAG, "cameraOnOffCmdRep off failed");
                    // cancel connect timeout handler
                    connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                    onConnectionFailed();
                }
            });
            addCommand(BluetoothInfo.cameraOnOffCommand(true), BluetoothInfo.cameraOnOffCmdRep, true, new BluetoothCommandCallback() {
                @Override
                public void onSuccess(byte[] response) {
                    AppLog.i(TAG, "cameraOnOffCmdRep on succeeded");
                }

                @Override
                public void onFailure() {
                    AppLog.i(TAG, "cameraOnOffCmdRep on failed");
                    // cancel connect timeout handler
                    connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                    onConnectionFailed();
                }
            });
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // get wifi info
            addCommand(BluetoothInfo.getCameraWifiInfoCommand(), BluetoothInfo.getCameraWifiInfoCmdRep, new BluetoothCommandCallback() {
                @Override
                public void onSuccess(byte[] response) {
                    AppLog.i(TAG, "getCameraWifiInfoCmdRep succeeded");
                    byte[] payload = Arrays.copyOfRange(response, 6, response.length);

                    // get current wifi ssid and password
                    currentWifiSsid = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 1, 33));
                    currentWifiPassword = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 33, 65));
                    AppLog.i(TAG, "getCameraWifiInfoCmdRep ssid: " + currentWifiSsid + ", password: " + currentWifiPassword);
                }

                @Override
                public void onFailure() {
                    AppLog.i(TAG, "getCameraWifiInfoCmdRep failed");
                    // cancel connect timeout handler
                    connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                    onConnectionFailed();
                }
            });
            // get firmware version
            addCommand(BluetoothInfo.getFirmwareVersionCommand(), BluetoothInfo.getFirmwareVersionCmdRep, new BluetoothCommandCallback() {
                @Override
                public void onSuccess(byte[] response) {
                    AppLog.i(TAG, "getFirmwareVersionCmdRep succeeded");
                    byte[] payload = Arrays.copyOfRange(response, 6, response.length);

                    // get firmware version
                    currentFirmwareVersion = BluetoothInfo.getFirmwareVersionFromPayload(payload);
                    AppLog.i(TAG, "getFirmwareVersionCmdRep firmwareVersion: " + currentFirmwareVersion);

                    if (isFirstConnect) {
                        // this is first connecting to the device, so need to set up device information (ssid & password) before connecting via wifi
                        // cancel connect timeout handler
                        connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);

                        // complete bluetooth connecting process by turning on wifi of camera device
                        connectDeviceDialog.dismiss();
                        if (connectCallback != null) {
                            connectCallback.onConnected();
                        }
                    }
                }

                @Override
                public void onFailure() {
                    AppLog.i(TAG, "getFirmwareVersionCmdRep failed");

                    // cancel connect timeout handler
                    connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                    onConnectionFailed();
                }
            });
        }, 3000);

        // turn on wifi
        if (!isFirstConnect) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                addCommand(BluetoothInfo.cameraWifiOnOffCommand(true), BluetoothInfo.cameraWifiOnOffCmdRep, true, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "cameraWifiOnOffCmdRep on succeeded");

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
                        AppLog.i(TAG, "cameraWifiOnOffCmdRep on failed");
                        // cancel connect timeout handler
                        connectTimeoutHandler.removeCallbacks(connectTimeoutRunnable);
                        onConnectionFailed();
                    }
                });
            }, 6000);
        }
    }

    // add command to queue
    public void addCommand(byte[] data, String responseCode, BluetoothCommandCallback callback) {
        addCommand(data, responseCode, false, callback);
    }

    public void addCommand(byte[] data, String responseCode, boolean setEnabled,  BluetoothCommandCallback callback) {
        if (!isConnected) {
            return;
        }

        commandQueue.offer(new BluetoothCommand(data, responseCode, setEnabled, callback));
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
        AppLog.i(TAG, "sendCommand: " + ConvertTools.getHexStringFromByteArray(command.getData()));

        // if send command is failed, process next command
        if (bluetoothGatt != null && !bluetoothGatt.writeCharacteristic(writeGattChar)) {
            if (command.getCallback() != null) {
                command.getCallback().onFailure();
            }
            isProcessing = false;
            commandQueue.poll();
            processNextCommand();
            return;
        }

        // if command callback is null, process next command immediately
        if (command.getCallback() == null) {
            isProcessing = false;
            commandQueue.poll();
            processNextCommand();
        }
    }

    // clear queue when it is disconnecting
    public void clearQueue() {
        currentWifiSsid = "";
        currentWifiPassword = "";
        commandQueue.clear();
        isProcessing = false;
        isConnected = false;
    }

    // firstConnect: connect to the bluetooth device (only get wifi info & firmware version), after bluetooth scan and before set up device
    @SuppressLint("MissingPermission")
    public void connectDevice(BluetoothDeviceInfo device, BluetoothConnectCallback callback, boolean autoConnect, boolean firstConnect) {
        // close previous connection if exists and clear queue
        clearQueue();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        this.currentDevice = device;
        this.connectCallback = callback;
        this.isFirstConnect = firstConnect;
        showConnectDeviceDialog(autoConnect);
    }

    @SuppressLint("MissingPermission")
    public void disconnectDevice() {
        // close previous connection if exists and clear queue
        clearQueue();
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
        }

        disconnectWifi();
    }

    public void onConnectionFailed() {
        connectDeviceDialog.dismiss();
        // show dialog after the previous dialog is dismissed
        new Handler(Looper.getMainLooper()).postDelayed(this::showConnectFailedDialog, 300);
    }

    public void showConnectFailedDialog() {
        connectFailedDialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_connection_failed_retry, null);
        connectFailedDialog.setContentView(dialogLayout);
        connectDeviceDialog.setCanceledOnTouchOutside(false);
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
        connectDeviceDialog.setCanceledOnTouchOutside(false);
        connectDeviceDialog.show();

        Button connectButton = dialogLayout.findViewById(R.id.connect_button);
        ImageButton closeButton = dialogLayout.findViewById(R.id.close_button);
        TextView deviceModelText = dialogLayout.findViewById(R.id.device_model_text);
        TextView deviceNameText = dialogLayout.findViewById(R.id.device_name_text);
        ProgressBar connectingProgress = dialogLayout.findViewById(R.id.connecting_progress);

        // device name
        deviceModelText.setText(currentDevice.device.getName());
        // device address
        deviceNameText.setText(String.format("B/D: %s", currentDevice.serialData));

        if (autoConnect) {
            // try connecting automatically
            // show the loading icon
            closeButton.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);
            connectingProgress.setVisibility(View.VISIBLE);

            // close previous connection if exists and clear queue
            clearQueue();
            if (bluetoothGatt != null) {
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                bluetoothGatt = null;
            }

            // set connect timeout handler
            connectTimeoutHandler = new Handler();
            connectTimeoutHandler.postDelayed(connectTimeoutRunnable, connectPeriod);

            currentDevice.device.connectGatt(context, false, gattCallback);
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

                // close previous connection if exists and clear queue
                clearQueue();
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }

                // set connect timeout handler
                connectTimeoutHandler = new Handler();
                connectTimeoutHandler.postDelayed(connectTimeoutRunnable, connectPeriod);

                currentDevice.device.connectGatt(context, false, gattCallback);
            }
        });
    }
}
