package com.sena.senacamera.bluetooth;

import android.annotation.SuppressLint;

import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.utils.ConvertTools;

public class BluetoothInfo {
    // ble service & characteristic info
    public static final String bleServiceUUID = "5052494d-2eab-0341-6972-6f6861424c45";
    public static final String bleDeviceName = "Prism_2";
    public static final String bleWriteCharUUID = "43484152-2eab-3241-6972-6f6861424c45";
    public static final String bleNotifyCharUUID = "43484152-2eab-3141-6972-6f6861424c45";
    public static final String CLIENT_CHARACTERISTIC_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    public static final int MTU_SIZE = 512;

    // command code list
    public static final String getCameraWifiInfoCmd = "0991", getCameraWifiInfoCmdRep = "8991";
    public static final String setCameraWifiInfoCmd = "0992", setCameraWifiInfoCmdRep = "8992";
    public static final String cameraWifiOnOffCmd = "0990", cameraWifiOnOffCmdRep = "8990";
    public static final String cameraOnOffCmd = "0993", cameraOnOffCmdRep = "8993";
    public static final String getFirmwareVersionCmd = "0502", getFirmwareVersionCmdRep = "8502";
    public static final String getFirmwareUpdateStatusCmd = "0590", getFirmwareUpdateStatusCmdRep = "8590";

    // command header
    public static final byte packetStartByte = (byte) 0xff, packetVersionByte = 0x01, packetFlagByte = 0x00;
    public static final byte[] packetHeader = {packetStartByte, packetVersionByte, packetFlagByte};

    // manufacture & product id
    public static final String MANUFACTURE_ID = "6009", PRODUCT_ID_PRISM_2 = "3568", PRODUCT_ID_PHANTOM_CAMERA = "096a";

    // command bytes
    public static byte[] getCameraWifiInfoCommand() {
        byte[] command = {BluetoothInfo.packetStartByte, BluetoothInfo.packetVersionByte, BluetoothInfo.packetFlagByte};
        // add length byte
        command = ConvertTools.addByteToByteArray(command, (byte) 0x01);
        // add command bytes (2)
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromHexString(BluetoothInfo.getCameraWifiInfoCmd));
        // add payload
        command = ConvertTools.addByteToByteArray(command, (byte) 0x00);

        return command;
    }

    public static byte[] cameraWifiOnOffCommand(boolean enabled) {
        byte[] command = {BluetoothInfo.packetStartByte, BluetoothInfo.packetVersionByte, BluetoothInfo.packetFlagByte};
        // add length byte
        command = ConvertTools.addByteToByteArray(command, (byte) 0x01);
        // add command bytes (2)
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromHexString(BluetoothInfo.cameraWifiOnOffCmd));
        // add payload
        command = ConvertTools.addByteToByteArray(command, (byte) (enabled? 0x01: 0x00));

        return command;
    }

    public static byte[] cameraOnOffCommand(boolean enabled) {
        byte[] command = {BluetoothInfo.packetStartByte, BluetoothInfo.packetVersionByte, BluetoothInfo.packetFlagByte};
        // add length byte
        command = ConvertTools.addByteToByteArray(command, (byte) 0x01);
        // add command bytes (2)
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromHexString(BluetoothInfo.cameraOnOffCmd));
        // add payload
        command = ConvertTools.addByteToByteArray(command, (byte) (enabled? 0x01: 0x00));

        return command;
    }

    public static byte[] setCameraWifiInfoCommand(String ssid, String password) {
        byte[] command = {BluetoothInfo.packetStartByte, BluetoothInfo.packetVersionByte, BluetoothInfo.packetFlagByte};
        // add length byte
        command = ConvertTools.addByteToByteArray(command, (byte) 0x40);
        // add command bytes (2)
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromHexString(BluetoothInfo.setCameraWifiInfoCmd));
        // add payload (ssid & password)
        // add ssid
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromString(ssid, 32));
        // add password
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromString(password, 32));

        return command;
    }

    public static byte[] getFirmwareVersionCommand() {
        byte[] command = {BluetoothInfo.packetStartByte, BluetoothInfo.packetVersionByte, BluetoothInfo.packetFlagByte};
        // add length byte
        command = ConvertTools.addByteToByteArray(command, (byte) 0x01);
        // add command bytes (2)
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromHexString(BluetoothInfo.getFirmwareVersionCmd));
        // add payload
        command = ConvertTools.addByteToByteArray(command, (byte) 0x00);

        return command;
    }

    public static byte[] getFirmwareUpdateStatusCommand() {
        byte[] command = {BluetoothInfo.packetStartByte, BluetoothInfo.packetVersionByte, BluetoothInfo.packetFlagByte};
        // add length byte
        command = ConvertTools.addByteToByteArray(command, (byte) 0x01);
        // add command bytes (2)
        command = ConvertTools.addByteArrayToByteArray(command, ConvertTools.getByteArrayFromHexString(BluetoothInfo.getFirmwareUpdateStatusCmd));
        // add payload
        command = ConvertTools.addByteToByteArray(command, (byte) 0x00);

        return command;
    }

    public static String getFirmwareVersionFromPayload(byte[] payload) {
        String version;
        int majorVersion = payload[0];
        int minorVersion = payload[1];
        int releaseType = payload[2];
        int subVersion = payload[3];
        int buildNumber = payload[4];

        version = majorVersion + "." + minorVersion + "." + subVersion;
        return version;
    }

    public static FirmwareUpdateStatus getFirmwareUpdateStatus(byte[] payload) {
        FirmwareUpdateStatus result = new FirmwareUpdateStatus();
        result.cameraOn = (int) payload[0] != 0;
        result.otaOn = (int) payload[1] != 0;
        result.totalFirmwareCount = payload[2];
        result.currentFirmwareIndex = payload[3];
        result.percent = payload[4];

        return result;
    }
}
