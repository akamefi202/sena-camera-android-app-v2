package com.sena.senacamera.ui.fragment;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.component.MenuInfo;

public class FragmentDeviceInfo extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentDeviceInfo.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    MenuInfo nameMenu, cameraVersionMenu, softwareVersionMenu;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_device_info, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.nameMenu = (MenuInfo) this.fragmentLayout.findViewById(R.id.name_layout);
        this.cameraVersionMenu = (MenuInfo) this.fragmentLayout.findViewById(R.id.camera_version_layout);
        this.softwareVersionMenu = (MenuInfo) this.fragmentLayout.findViewById(R.id.software_version_layout);

        this.backButton.setOnClickListener(v -> onBack());

        initialize();

        return this.fragmentLayout;
    }

    public void initialize() {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "initialize camera is disconnected");
            requireActivity().finish();
            return;
        }

        // camera device name
        this.nameMenu.setValue(myCamera.getCameraFixedInfo().getCameraName());
        // firmware version
        this.cameraVersionMenu.setValue(myCamera.getCameraFixedInfo().getCameraVersion());
        // sdk version
        this.softwareVersionMenu.setValue(BluetoothDeviceManager.getInstance().getCurrentDevice().firmwareVersion);
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
        this.backButton = null;
        this.nameMenu = null;
        this.cameraVersionMenu = null;
        this.softwareVersionMenu = null;
    }

    public void updateFragment() {

    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onClick(View v) {

    }
}
