package com.sena.senacamera.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.bluetooth.BluetoothInfo;
import com.sena.senacamera.data.SystemInfo.SystemInfo;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.listener.BluetoothCommandCallback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.MainActivity;
import com.sena.senacamera.ui.component.CustomPasswordInput;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.utils.ConvertTools;
import com.sena.senacamera.utils.WifiCheck;

import java.util.Arrays;

public class FragmentNewPassword extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentNewPassword.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    LinearLayout newPasswordLayout, confirmPasswordLayout;
    CustomPasswordInput newPasswordInput, confirmPasswordInput;
    Button nextButton, okButton, backButton;
    ImageView passwordCountCheck, passwordCharacterCheck, passwordMatchCheck;

    public WifiCheck wifiCheck;
    public String currentSsid, newPassword = "";
    public BluetoothCommandManager bluetoothCommandManager = BluetoothCommandManager.getInstance();

    TextWatcher newPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean forwardAvailable = true;

            // update password length check status
            if (s.length() >= 8 && s.length() <= 12) {
                passwordCountCheck.setImageResource(R.drawable.status_password_check_on);
            } else {
                passwordCountCheck.setImageResource(R.drawable.status_password_check_off);
                forwardAvailable = false;
            }

            // update password character check status
            if (s.toString().contains("[") || s.toString().contains("]")) {
                passwordCharacterCheck.setImageResource(R.drawable.status_password_check_off);
                forwardAvailable = false;
            } else {
                passwordCharacterCheck.setImageResource(R.drawable.status_password_check_on);
            }

            nextButton.setEnabled(forwardAvailable && !s.toString().isBlank());
        }
    };

    TextWatcher confirmPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean forwardAvailable = true;

            // update password match check status
            if (s.toString().equals(newPassword) & !newPassword.isBlank()) {
                passwordMatchCheck.setImageResource(R.drawable.status_password_check_on);
            } else {
                passwordMatchCheck.setImageResource(R.drawable.status_password_check_off);
                forwardAvailable = false;
            }

            okButton.setEnabled(forwardAvailable && !s.toString().isBlank());
        }
    };

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_new_password, viewGroup, false);

        this.newPasswordLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.new_password_layout);
        this.confirmPasswordLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.confirm_password_layout);
        this.backButton = (Button) this.fragmentLayout.findViewById(R.id.back_button);
        this.nextButton = (Button) this.fragmentLayout.findViewById(R.id.next_button);
        this.okButton = (Button) this.fragmentLayout.findViewById(R.id.ok_button);
        this.newPasswordInput = (CustomPasswordInput) this.fragmentLayout.findViewById(R.id.new_password_input);
        this.confirmPasswordInput = (CustomPasswordInput) this.fragmentLayout.findViewById(R.id.confirm_password_input);
        this.passwordCountCheck = (ImageView) this.fragmentLayout.findViewById(R.id.password_count_check);
        this.passwordCharacterCheck = (ImageView) this.fragmentLayout.findViewById(R.id.password_character_check);
        this.passwordMatchCheck = (ImageView) this.fragmentLayout.findViewById(R.id.password_match_check);

        // read the device name (ssid) from parameter
        this.currentSsid = getArguments().getString("deviceName");
        this.wifiCheck = new WifiCheck(requireActivity());

        this.backButton.setOnClickListener(v -> onBack());
        this.okButton.setOnClickListener(v -> onOk());
        this.nextButton.setOnClickListener(v -> onNext());

        this.newPasswordInput.setTextChangedListener(newPasswordWatcher);
        this.confirmPasswordInput.setTextChangedListener(confirmPasswordWatcher);

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
        this.newPasswordLayout = null;
        this.confirmPasswordLayout = null;
        this.backButton = null;
        this.nextButton = null;
        this.okButton = null;
        this.newPasswordInput = null;
        this.confirmPasswordInput = null;
        this.passwordCountCheck = null;
        this.passwordCharacterCheck = null;
        this.passwordMatchCheck = null;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateFragment() {

    }

    private void onBack() {
        SystemInfo.hideInputMethod(requireActivity());

        // back to new password layout
        this.confirmPasswordLayout.setVisibility(View.GONE);
        this.newPasswordLayout.setVisibility(View.VISIBLE);
    }

    private void onOk() {
        SystemInfo.hideInputMethod(requireActivity());

        if (!bluetoothCommandManager.isConnected()) {
            AppLog.i(TAG, "onOk ble camera device is disconnected");
            MyToast.show(requireContext(), R.string.error_occurred);
            return;
        }

        MyProgressDialog.showProgressDialog(requireContext(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // set ssid & password
                bluetoothCommandManager.addCommand(BluetoothInfo.setCameraWifiInfoCommand(currentSsid, newPassword), BluetoothInfo.setCameraWifiInfoCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);
                        String ssid = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 1, 33));
                        String password = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 33, 65));
                        bluetoothCommandManager.setCurrentWifiSsid(ssid);
                        bluetoothCommandManager.setCurrentWifiPassword(password);
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep ssid: " + ssid + ", password: " + password);
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep failed");
                    }
                });
                bluetoothCommandManager.addCommand(BluetoothInfo.getFirmwareVersionCommand(), BluetoothInfo.getFirmwareVersionCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "getFirmwareVersionCommand succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);

                        // get firmware version
                        String firmwareVersion = BluetoothInfo.getFirmwareVersionFromPayload(payload);
                        bluetoothCommandManager.setCurrentFirmwareVersion(firmwareVersion);
                        AppLog.i(TAG, "getFirmwareVersionCommand firmwareVersion: " + firmwareVersion);
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "getFirmwareVersionCommand failed");
                    }
                });
                // turn on wifi & register the device
                bluetoothCommandManager.addCommand(BluetoothInfo.cameraWifiOnOffCommand(true), BluetoothInfo.cameraWifiOnOffCmdRep, new BluetoothCommandCallback() {
                    @SuppressLint({"MissingPermission"})
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "cameraWifiOnOffCommand succeeded");

                        // register device
                        BluetoothDeviceManager deviceManager = BluetoothDeviceManager.getInstance();
                        deviceManager.addDevice(new CameraDeviceInfo(bluetoothCommandManager.currentDevice.device.getName(), bluetoothCommandManager.currentDevice.device.getAddress(), currentSsid, newPassword, bluetoothCommandManager.getCurrentFirmwareVersion(), bluetoothCommandManager.currentDevice.serialData));
                        deviceManager.currentIndex = deviceManager.getDeviceCount() - 1;
                        deviceManager.writeToSharedPref(requireContext().getApplicationContext());

                        MyProgressDialog.closeProgressDialog();

                        SystemInfo.hideInputMethod(requireActivity());
                        startActivity(new Intent(requireContext(), MainActivity.class));
                        requireActivity().finish();

                        // open wifi settings
//                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                            requireActivity().startActivity(intent);
//                        }, 300);
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

    private void onNext() {
        SystemInfo.hideInputMethod(requireActivity());

        // show confirm password layout
        this.newPasswordLayout.setVisibility(View.GONE);
        this.confirmPasswordLayout.setVisibility(View.VISIBLE);

        this.confirmPasswordInput.setText("");
        this.okButton.setEnabled(false);
        this.newPassword = newPasswordInput.getText();
    }
}
