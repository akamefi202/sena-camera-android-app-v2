package com.sena.senacamera.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.sena.senacamera.R;

public class MainActivity extends AppCompatActivity {

    private ImageButton deviceListButton, settingsButton;
    private LinearLayout mediaButton, preferenceButton;
    private Button previewButton, connectButton;

    private boolean cameraConnectionStatus = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // set status bar color
        //getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        deviceListButton = findViewById(R.id.device_list_button);
        settingsButton = findViewById(R.id.setting_button);
        previewButton = findViewById(R.id.preview_button);
        connectButton = findViewById(R.id.connect_button);
        mediaButton = findViewById(R.id.media_button);
        preferenceButton = findViewById(R.id.preference_button);

        deviceListButton.setOnClickListener(v -> showDeviceList());
        settingsButton.setOnClickListener(v -> showSettings());
        previewButton.setOnClickListener(v -> startCameraPreview());
        connectButton.setOnClickListener(v -> connectCamera());
        mediaButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(v.getContext(), MediaActivity.class));
                return false;
            }

            return true;
        });
        preferenceButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(v.getContext(), PreferenceActivity.class));
                return false;
            }

            return true;
        });

        loadCameraInfo();

    }

    private void showDeviceList() {
        startActivity(new Intent(this, DeviceListActivity.class));
    }

    private void showSettings() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void startCameraPreview() {
        startActivity(new Intent(this, PreviewActivity.class));
    }

    private void connectCamera() {
        startActivity(new Intent(this, ConnectDeviceActivity.class));
    }

    private void loadCameraInfo() {
    }

    public String getWifiSSID() {
        WifiInfo connectionInfo;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled() || (connectionInfo = wifiManager.getConnectionInfo()) == null) {
            return null;
        }
        NetworkInfo.DetailedState detailedStateOf = WifiInfo.getDetailedStateOf(connectionInfo.getSupplicantState());
        if (detailedStateOf == NetworkInfo.DetailedState.CONNECTED || detailedStateOf == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
            return connectionInfo.getSSID();
        }
        return null;
    }

    public void checkCameraConnectionStatus() {
        String wifiSsid = getWifiSSID();
        if (wifiSsid == null || wifiSsid.length() <= 2) {
            cameraConnectionStatus = false;
            return;
        }

        // akamefi202: remove double quoatation marks from wifiSsid
        wifiSsid = wifiSsid.substring(1, wifiSsid.length() - 1).toLowerCase();
        Log.e("MainActivity - checkCameraConnectionStatus", wifiSsid);
        cameraConnectionStatus = wifiSsid.startsWith(getResources().getString(R.string.prism2).toLowerCase());
    }

    public void onResume() {
        super.onResume();

        checkCameraConnectionStatus();

        if (cameraConnectionStatus) {
            previewButton.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.GONE);
        } else {
            previewButton.setVisibility(View.GONE);
            connectButton.setVisibility(View.VISIBLE);
        }
    }
}