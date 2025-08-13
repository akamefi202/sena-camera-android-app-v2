package com.sena.senacamera.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.bluetooth.BluetoothInfo;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.listener.BluetoothCommandCallback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.MainActivity;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.utils.ClickUtils;
import com.sena.senacamera.utils.ConvertTools;

import java.util.Arrays;

public class FragmentSetDevice extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentSetDevice.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton editDeviceNameButton;
    Button nextButton, okButton;
    ImageView cameraImage;
    LinearLayout changePasswordButton;
    TextView deviceNameText;

    private boolean firstConnect = true;
    private String currentSsid, currentPassword;
    public BluetoothCommandManager bleCommandManager = BluetoothCommandManager.getInstance();

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_set_device, viewGroup, false);

        this.editDeviceNameButton = (ImageButton) this.fragmentLayout.findViewById(R.id.edit_device_name_button);
        this.nextButton = (Button) this.fragmentLayout.findViewById(R.id.next_button);
        this.okButton = (Button) this.fragmentLayout.findViewById(R.id.ok_button);
        this.cameraImage = (ImageView) this.fragmentLayout.findViewById(R.id.camera_image);
        this.changePasswordButton = (LinearLayout) this.fragmentLayout.findViewById(R.id.change_password_button);
        this.deviceNameText = (TextView) this.fragmentLayout.findViewById(R.id.device_name_text);
        this.deviceNameText.setText(bleCommandManager.getCurrentWifiSsid());

        this.editDeviceNameButton.setOnClickListener(v -> onEditDeviceName());
        this.nextButton.setOnClickListener(v -> onNext());
        this.okButton.setOnClickListener(v -> onOk());
        this.changePasswordButton.setOnClickListener(v -> onChangePassword());

        // check if this time is first connect or second connect
        if (getArguments() != null) {
            firstConnect = getArguments().getBoolean("firstConnect");
        }
        // if first connect, need to set the password
        // if second connect, show change password button
        if (firstConnect) {
            changePasswordButton.setVisibility(View.GONE);
            okButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        } else {
            changePasswordButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
        }

        // get current ssid & password
        currentSsid = bleCommandManager.getCurrentWifiSsid();
        currentPassword = bleCommandManager.getCurrentWifiPassword();

        return this.fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.editDeviceNameButton = null;
        this.nextButton = null;
        this.cameraImage = null;
        this.changePasswordButton = null;
        this.deviceNameText = null;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateFragment() {

    }

    private void onNext() {
        // input the device name as a parameter
        Bundle args = new Bundle();
        args.putString("deviceName", (String) deviceNameText.getText());

        // open new password fragment
        Fragment fragment = new FragmentNewPassword();
        fragment.setArguments(args);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void onOk() {
        if (!bleCommandManager.isConnected) {
            AppLog.i(TAG, "onOk ble camera device is disconnected");
            MyToast.show(requireContext(), R.string.error_occurred);
            return;
        }

        MyProgressDialog.showProgressDialog(requireContext(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // set ssid & password
                bleCommandManager.addCommand(BluetoothInfo.setCameraWifiInfoCommand(currentSsid, currentPassword), BluetoothInfo.setCameraWifiInfoCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);
                        String ssid = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 1, 33));
                        String password = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 33, 65));
                        bleCommandManager.setCurrentWifiSsid(ssid);
                        bleCommandManager.setCurrentWifiPassword(password);
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep ssid: " + ssid + ", password: " + password);
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep failed");
                    }
                });
                // turn on wifi & connect to the device
                bleCommandManager.addCommand(BluetoothInfo.cameraWifiOnOffCommand(true), BluetoothInfo.cameraWifiOnOffCmdRep, new BluetoothCommandCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "cameraWifiOnOffCommand succeeded");

                        // check if wifi of phone is turned on
//                        if (!MWifiManager.isWifiEnabled(requireContext().getApplicationContext())) {
//                            // wifi is turned off
//                            AppLog.i(TAG, "cameraWifiOnOffCommand wifi is turned off");
//                            MyProgressDialog.closeProgressDialog();
//                            MyToast.show(requireContext(), R.string.wifi_turned_off);
//                            return;
//                        }

                        // connect to the camera device via wifi
                        MWifiManager.connect(requireContext(), currentSsid, currentPassword, null);

                        // open wifi settings
//                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                            requireActivity().startActivity(intent);
//                        }, 300);

                        // register device if first connect or update passwordConfirmed if second connect
                        BluetoothDeviceManager deviceManager = BluetoothDeviceManager.getInstance();
                        deviceManager.addDevice(new CameraDeviceInfo(bleCommandManager.currentDevice.device.getName(), bleCommandManager.currentDevice.device.getAddress(), currentSsid, currentPassword, bleCommandManager.getCurrentFirmwareVersion(), bleCommandManager.currentDevice.serialData));
                        deviceManager.currentIndex = deviceManager.getDeviceCount() - 1;
                        deviceManager.writeToSharedPref(requireContext().getApplicationContext());
//                        if (firstConnect) {
//                            deviceManager.addDevice(new CameraDeviceInfo(bleCommandManager.currentDevice.device.getName(), bleCommandManager.currentDevice.device.getAddress(), currentSsid, currentPassword, bleCommandManager.getCurrentFirmwareVersion(), bleCommandManager.currentDevice.serialData));
//                            deviceManager.currentIndex = deviceManager.getDeviceCount() - 1;
//                            deviceManager.writeToSharedPref(requireContext().getApplicationContext());
//                        } else {
//                            int index = deviceManager.findDevice(bleCommandManager.currentDevice.device.getAddress());
//                            // update passwordConfirmed field of device
//                            CameraDeviceInfo deviceInfo = deviceManager.getDeviceByIndex(index);
//                            deviceInfo.passwordConfirmed = true;
//                            deviceManager.updateDevice(index, deviceInfo);
//                            deviceManager.currentIndex = index;
//
//                            deviceManager.writeToSharedPref(requireContext().getApplicationContext());
//                        }

                        MyProgressDialog.closeProgressDialog();
                        startActivity(new Intent(requireContext(), MainActivity.class));
                        requireActivity().finish();
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "cameraWifiOnOffCommand failed");
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(requireContext(), R.string.error_occurred);
                    }
                });
            }
        }).start();
    }

    private void onEditDeviceName() {
        // input the device name as a parameter
        Bundle args = new Bundle();
        args.putString("deviceName", (String) deviceNameText.getText());

        // set fragment result listener
        requireActivity().getSupportFragmentManager().setFragmentResultListener("FragmentChangeDeviceName", getViewLifecycleOwner(), new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                AppLog.i(TAG, "onFragmentResult requestKey: " + requestKey);
                String deviceName = result.getString("deviceName");
                deviceNameText.setText(deviceName);
            }
        });

        // open edit device name fragment
        Fragment fragment = new FragmentChangeDeviceName();
        fragment.setArguments(args);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void onChangePassword() {
        // open change password fragment
        Fragment fragment = new FragmentChangePassword();
        // set fragment result listener
        requireActivity().getSupportFragmentManager().setFragmentResultListener("FragmentChangePassword", getViewLifecycleOwner(), (resultKey, result) -> {
            String password = result.getString("password");
            if (password != null) {
                currentPassword = password;
            }
        });

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
