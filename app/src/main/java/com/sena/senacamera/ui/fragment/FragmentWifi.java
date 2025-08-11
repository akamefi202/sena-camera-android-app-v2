package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.component.MenuInfo;
import com.sena.senacamera.ui.component.MenuSelection;

import java.util.Arrays;

public class FragmentWifi extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentWifi.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    MenuSelection wifiFrequencyMenu;
    MenuInfo ssidMenu, passwordMenu;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_wifi, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.wifiFrequencyMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.wifi_frequency_layout);
        this.ssidMenu = (MenuInfo) this.fragmentLayout.findViewById(R.id.ssid_layout);
        this.passwordMenu = (MenuInfo) this.fragmentLayout.findViewById(R.id.password_layout);

        this.wifiFrequencyMenu.setOnClickListener(this);
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
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // wifi frequency
        this.wifiFrequencyMenu.setOptionList(Arrays.asList(baseProperties.getWifiFrequency().getValueList()));
        this.wifiFrequencyMenu.setValue(baseProperties.getWifiFrequency().getCurrentUiStringInSetting());

        CameraDeviceInfo currentDevice = BluetoothDeviceManager.getInstance().getCurrentDevice();
        this.ssidMenu.setValue(currentDevice.wifiSsid);
        this.passwordMenu.setValue(currentDevice.wifiPassword);
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
        this.wifiFrequencyMenu = null;
        this.ssidMenu = null;
        this.passwordMenu = null;
    }

    public void updateFragment() {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "initialize camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // wifi frequency
        this.wifiFrequencyMenu.setValue(baseProperties.getWifiFrequency().getCurrentUiStringInSetting());
    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void onMenuSelection(MenuSelection menuSelection) {
        // open options fragment
        Fragment fragment = new FragmentOptions();
        Bundle args = new Bundle();
        args.putString("title", menuSelection.getTitle());
        args.putString("value", menuSelection.getValue());
        args.putString("optionList", new Gson().toJson(menuSelection.getOptionList()));
        fragment.setArguments(args);

        // set fragment result listener
        requireActivity().getSupportFragmentManager().setFragmentResultListener("FragmentOptions", getViewLifecycleOwner(), (resultKey, result) -> {
            updateFragment();
        });

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_wifi, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.wifi_frequency_layout) {
            onMenuSelection(wifiFrequencyMenu);
        }
    }
}
