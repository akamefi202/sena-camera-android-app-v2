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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.Presenter.LaunchPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraProperties;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageButton deviceListButton, settingsButton;
    private LinearLayout mediaButton, preferenceButton;
    private Button previewButton, connectButton;
    private ImageView batteryStatusIcon, sdCardStatusIcon;
    private TextView batteryPercentText, firmwareVersionText;

    public LaunchPresenter presenter;

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
        batteryPercentText = findViewById(R.id.camera_battery_text);
        batteryStatusIcon = findViewById(R.id.camera_battery_status);
        sdCardStatusIcon = findViewById(R.id.camera_sd_card_status);
        firmwareVersionText = findViewById(R.id.firmware_version_status);

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

        // akamefi202: set LaunchPresenter
        presenter = new LaunchPresenter(MainActivity.this);
        presenter.setViewSena();
    }

    private void showDeviceList() {
        startActivity(new Intent(this, DeviceListActivity.class));
    }

    private void showSettings() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void startCameraPreview() {
        //this.presenter.launchCameraSena();
        if (checkCameraConnectionStatus()) {
            startActivity(new Intent(this, PreviewActivity.class));
        } else {
            Log.e(TAG, "camera is not connected");
            updateUI();
        }
    }

    private void connectCamera() {
        this.startWifiSetting();
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

    public boolean checkCameraConnectionStatus() {
        String wifiSsid = getWifiSSID();
        if (wifiSsid == null || wifiSsid.length() <= 2) {
            return false;
        }

        // akamefi202: remove double quoatation marks from wifiSsid
        wifiSsid = wifiSsid.substring(1, wifiSsid.length() - 1).toLowerCase();
        Log.e("MainActivity - checkCameraConnectionStatus", wifiSsid);
        return wifiSsid.startsWith(getResources().getString(R.string.prism2).toLowerCase());
    }

    public void onResume() {
        super.onResume();

        updateUI();
    }

    public void updateUI() {
        if (checkCameraConnectionStatus()) {
            previewButton.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.GONE);

            this.presenter.connectCameraSena();
        } else {
            previewButton.setVisibility(View.GONE);
            connectButton.setVisibility(View.VISIBLE);
        }
    }

    public void updateCameraStatusInfo() {
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if(curCamera == null || !curCamera.isConnected()) {
            return;
        }

        CameraProperties properties = curCamera.getCameraProperties();
        int batteryLevel = properties.getBatteryElectric();
        boolean isSdCardExist = properties.isSDCardExist();
        String firmwareVersion = curCamera.getCameraFixedInfo().getCameraVersion();

        // update battery icon
        if (batteryLevel > 100) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_charge_white);
        } else if (batteryLevel == 100) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_100_white);
        } else if (batteryLevel >= 80) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_80_white);
        } else if (batteryLevel >= 60) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_60_white);
        } else if (batteryLevel >= 40) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_40_white);
        } else if (batteryLevel >= 20) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_20_white);
        } else {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_10);
        }

        // update battery percent text
        this.batteryPercentText.setText(batteryLevel + "%");

        // update firmware version text
        this.firmwareVersionText.setText("v" + firmwareVersion);

        // update sd card icon
        if (isSdCardExist) {
            sdCardStatusIcon.setImageResource(R.drawable.status_sd_card);
        } else {
            sdCardStatusIcon.setImageResource(R.drawable.status_no_sd_card);
        }
    }

    public void startWifiSetting() {
        startActivityForResult(new Intent("android.settings.WIFI_SETTINGS"), 1001);
    }
}