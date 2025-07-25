package com.sena.senacamera.ui.activity;

import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.fragment.FragmentClearCache;
import com.sena.senacamera.ui.fragment.FragmentDeviceSettings;
import com.sena.senacamera.ui.fragment.FragmentFirmwareUpdate;
import com.sena.senacamera.ui.fragment.FragmentHelpGuide;
import com.sena.senacamera.utils.WifiCheck;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SettingActivity.class.getSimpleName();

    private ImageButton backButton;
    private LinearLayout deviceSettingsLayout, clearCacheLayout, firmwareUpdateLayout, helpGuideLayout;
    private TextView tempText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        backButton = findViewById(R.id.back_button);
        deviceSettingsLayout = findViewById(R.id.layout_device_settings);
        clearCacheLayout = findViewById(R.id.layout_clear_cache);
        firmwareUpdateLayout = findViewById(R.id.layout_firmware_update);
        helpGuideLayout = findViewById(R.id.layout_help_guide);
        tempText = findViewById(R.id.temp_text);

        deviceSettingsLayout.setOnClickListener(this);
        clearCacheLayout.setOnClickListener(this);
        firmwareUpdateLayout.setOnClickListener(this);
        helpGuideLayout.setOnClickListener(this);

        backButton.setOnClickListener((v) -> {
            finish();
        });
    }

    public void setLayoutEnabled(ViewGroup viewGroup, boolean enabled) {
        viewGroup.setEnabled(enabled);

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enabled);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check connection
        if (!checkConnection()) {
            this.setLayoutEnabled(this.deviceSettingsLayout, false);
            this.setLayoutEnabled(this.clearCacheLayout, false);
            this.setLayoutEnabled(this.firmwareUpdateLayout, false);
            this.setLayoutEnabled(this.helpGuideLayout, false);

            WifiCheck.showCameraDisconnectedDialog(this, new Callback() {
                @Override
                public void processSucceed() {
                    finish();
                }

                @Override
                public void processFailed() {

                }

                @Override
                public void processAbnormal() {

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        // combination of onclick listeners of buttons
        int id = v.getId();

        if (id == R.id.layout_device_settings) {
            AppLog.i(TAG, "device settings layout is pressed");

            Fragment fragment = new FragmentDeviceSettings();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.layout_firmware_update) {
            AppLog.i(TAG, "firmware update layout is pressed");

            Fragment fragment = new FragmentFirmwareUpdate();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.layout_clear_cache) {
            AppLog.i(TAG, "clear cache layout is pressed");

            Fragment fragment = new FragmentClearCache();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.layout_help_guide) {
            AppLog.i(TAG, "help guide layout is pressed");

            Fragment fragment = new FragmentHelpGuide();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public boolean checkConnection() {
        // check if wifi is working
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo == null || !wifiInfo.isConnected()) {
            return false;
        }

        // check if the camera is connected
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            return false;
        }

        return true;
    }
}
