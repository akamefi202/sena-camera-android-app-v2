package com.sena.senacamera.bluetooth;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.utils.SharedPreferencesUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BluetoothDeviceManager {
    private final String TAG = BluetoothDeviceManager.class.getSimpleName();

    // instance
    private static final class InstanceHolder {
        private static final BluetoothDeviceManager instance = new BluetoothDeviceManager();
    }
    public static BluetoothDeviceManager getInstance() {
        return InstanceHolder.instance;
    }

    private List<CameraDeviceInfo> deviceList = new ArrayList<>();
    public int currentIndex = 0;

    public BluetoothDeviceManager() {}

    public CameraDeviceInfo getCurrentDevice() {
        if (currentIndex >= deviceList.size()) {
            AppLog.e(TAG, "getCurrentDevice: invalid index value");
            return null;
        }

        return deviceList.get(currentIndex);
    }

    public void updateCurrentDevice(CameraDeviceInfo device) {
        if (currentIndex >= deviceList.size()) {
            AppLog.e(TAG, "updateCurrentDevice: invalid index value");
            return;
        }
        deviceList.set(currentIndex, device);
    }

    public void addDevice(CameraDeviceInfo device) {
        deviceList.add(device);
    }

    public void updateDevice(int index, CameraDeviceInfo device) {
        if (index >= deviceList.size()) {
            AppLog.e(TAG, "updateDevice: invalid index value");
            return;
        }
        deviceList.set(index, device);
    }

    public void deleteDevice(int index) {
        if (index >= deviceList.size()) {
            AppLog.e(TAG, "deleteDevice: invalid index value");
            return;
        }
        deviceList.remove(index);

        if (index < currentIndex) {
            currentIndex --;
        }
    }

    public void swapDevices(int index1, int index2) {
        if (index1 >= deviceList.size() || index2 >= deviceList.size()) {
            AppLog.e(TAG, "swapDevices: invalid index value");
            return;
        }

        CameraDeviceInfo temp = deviceList.get(index1);
        deviceList.set(index1, deviceList.get(index2));
        deviceList.set(index2, temp);

        if (index1 == currentIndex) {
            currentIndex = index2;
        } else if (index2 == currentIndex) {
            currentIndex = index1;
        }
    }

    public void updateDeviceList(List<CameraDeviceInfo> deviceList) {
        this.deviceList = new ArrayList<>();
        this.deviceList.addAll(deviceList);
        this.currentIndex = 0;
    }

    public int findDevice(String bluetoothAddress) {
        int index = -1;
        for (int i = 0; i < deviceList.size(); i ++) {
            if (deviceList.get(i).bleAddress.equals(bluetoothAddress)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public List<CameraDeviceInfo> getDeviceList() {
        return deviceList;
    }

    public CameraDeviceInfo getDeviceByIndex(int index) {
        return deviceList.get(index);
    }

    public int getDeviceCount() {
        return deviceList.size();
    }

    public void readFromSharedPref(Context context) {
        currentIndex = (int) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.CURRENT_DEVICE_INDEX, 0);

        // read device list via json
        String json = (String) SharedPreferencesUtils.get(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.DEVICE_LIST, "");
        if (json != null && !json.isEmpty()) {
            AppLog.i(TAG, "readFromSharedPref " + json);
            Gson gson = new Gson();
            Type type = TypeToken.getParameterized(List.class, CameraDeviceInfo.class).getType();
            deviceList = gson.fromJson(json, type);
        } else {
            AppLog.i(TAG, "readFromSharedPref json is null");
            deviceList = new ArrayList<>();
        }
    }

    public void writeToSharedPref(Context context) {
        Gson gson = new Gson();
        String json = gson.toJson(deviceList);
        AppLog.i(TAG, "writeToSharedPref " + json);
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.DEVICE_LIST, json);
        SharedPreferencesUtils.put(context, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.CURRENT_DEVICE_INDEX, currentIndex);
    }

    public boolean isCurrentDeviceConnected(Context context) {
        return isDeviceConnected(context, currentIndex);
    }

    public boolean isDeviceConnected(Context context, int index) {
        if (index != currentIndex) {
            return false;
        }
        if (!MWifiManager.isWifiEnabled(context)) {
            return false;
        }
        if (index >= deviceList.size()) {
            return false;
        }

        CameraDeviceInfo device = deviceList.get(index);
        String ssid = MWifiManager.getSsid(context);
        AppLog.i(TAG, "isDeviceConnected index: " + index + ", ssid: " + ssid + ", deviceName: " + device.wifiSsid);
        return device.wifiSsid.equals(ssid);
    }

    public boolean isThisDeviceConnected(Context context, String deviceName) {
        if (!MWifiManager.isWifiEnabled(context)) {
            return false;
        }
        if (currentIndex >= deviceList.size()) {
            return false;
        }

        String ssid = MWifiManager.getSsid(context);
        return deviceName.equals(ssid);
    }
}
