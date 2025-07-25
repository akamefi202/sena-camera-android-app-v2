package com.sena.senacamera.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Handler;

import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.listener.BluetoothSearchCallback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.utils.ConvertTools;

import java.util.ArrayList;
import java.util.List;

public class BluetoothScanManager {
    private final String TAG = BluetoothScanManager.class.getSimpleName();

    // instance
    private static final class InstanceHolder {
        private static final BluetoothScanManager instance = new BluetoothScanManager();
    }
    public static BluetoothScanManager getInstance() {
        return BluetoothScanManager.InstanceHolder.instance;
    }

    private final List<BluetoothDevice> deviceList = new ArrayList<>();
    private BluetoothLeScanner bleScanner;
    private final List<BluetoothSearchCallback> searchCallbackList = new ArrayList<>();
    private Handler scanTimeoutHandler;
    private static final long scanPeriod = 120000;

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device.getName() == null || device.getAddress() == null) {
                return;
            }
            AppLog.i(TAG, "Device found: " + device.getName() + ", Address: " + device.getAddress());

            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord != null) {
                byte[] advertisingData = scanRecord.getBytes();
                AppLog.i(TAG, "scanRecord: " + ConvertTools.getHexStringFromByteArray(advertisingData));
            }

            // check if device name is same with prism 2
            if (!device.getName().equals(BluetoothInfo.bleDeviceName)) {
                return;
            }
            // check if device is not found
            boolean isAlreadyFound = false;
            String newDeviceAddress = device.getAddress();
            for (int i = 0; i < deviceList.size(); i ++) {
                if (newDeviceAddress.equals(deviceList.get(i).getAddress())) {
                    isAlreadyFound = true;
                    break;
                }
            }
            if (isAlreadyFound) {
                return;
            }
            // check if device is not registered
            boolean isAlreadyRegistered = false;
            List<CameraDeviceInfo> registeredList = BluetoothDeviceManager.getInstance().getDeviceList();
            for (int i = 0; i < registeredList.size(); i ++) {
                if (newDeviceAddress.equals(registeredList.get(i).bleAddress)) {
                    isAlreadyRegistered = true;
                    break;
                }
            }
            if (isAlreadyRegistered) {
                return;
            }

            deviceList.add(device);
            for (BluetoothSearchCallback callback: searchCallbackList) {
                callback.onFound(device);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onScanFailed(int errorCode) {
            AppLog.i(TAG, "Scan failed: " + errorCode);

            stopScan();
            for (BluetoothSearchCallback callback: searchCallbackList) {
                callback.onFailed();
            }
        }
    };

    private Runnable scanTimeoutRunnable = new Runnable() {
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            AppLog.i(TAG, "stopping scan due to time out");
            bleScanner.stopScan(scanCallback);
            for (BluetoothSearchCallback callback: searchCallbackList) {
                callback.onFailed();
            }
        }
    };

    BluetoothScanManager() {

    }

    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    public int getDeviceCount() {
        return deviceList.size();
    }

    public void addSearchCallback(BluetoothSearchCallback callback) {
        this.searchCallbackList.add(callback);
    }

    public void clearSearchCallbackList() {
        this.searchCallbackList.clear();
    }

    @SuppressLint("MissingPermission")
    public void startScan(BluetoothAdapter bluetoothAdapter) {
        scanTimeoutHandler = new Handler();
        scanTimeoutHandler.postDelayed(scanTimeoutRunnable, scanPeriod);

        deviceList.clear();
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        bleScanner.startScan(scanCallback);
    }

    @SuppressLint("MissingPermission")
    public void stopScan() {
        bleScanner.stopScan(scanCallback);
        // cancel time out handler
        scanTimeoutHandler.removeCallbacks(scanTimeoutRunnable);
    }
}
