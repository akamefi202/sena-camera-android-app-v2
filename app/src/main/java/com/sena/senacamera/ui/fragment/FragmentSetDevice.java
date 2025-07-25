package com.sena.senacamera.ui.fragment;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.utils.ClickUtils;

public class FragmentSetDevice extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentSetDevice.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton editDeviceNameButton;
    Button nextButton;
    ImageView cameraImage;
    LinearLayout changePasswordButton;
    TextView deviceNameText;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_set_device, viewGroup, false);

        this.editDeviceNameButton = (ImageButton) this.fragmentLayout.findViewById(R.id.edit_device_name_button);
        this.nextButton = (Button) this.fragmentLayout.findViewById(R.id.next_button);
        this.cameraImage = (ImageView) this.fragmentLayout.findViewById(R.id.camera_image);
        this.changePasswordButton = (LinearLayout) this.fragmentLayout.findViewById(R.id.change_password_button);
        this.deviceNameText = (TextView) this.fragmentLayout.findViewById(R.id.device_name_text);
        this.deviceNameText.setText(BluetoothCommandManager.getInstance().getCurrentWifiSsid());

        this.editDeviceNameButton.setOnClickListener(v -> onEditDeviceName());
        this.nextButton.setOnClickListener(v -> onNext());
        this.changePasswordButton.setOnClickListener(v -> onChangePassword());

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
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
