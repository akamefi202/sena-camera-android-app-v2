package com.sena.senacamera.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothScanManager;
import com.sena.senacamera.data.entity.BluetoothDeviceInfo;
import com.sena.senacamera.listener.BluetoothConnectCallback;
import com.sena.senacamera.listener.BluetoothSearchCallback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.R;
import com.sena.senacamera.data.type.BluetoothConnectStatus;
import com.sena.senacamera.ui.fragment.FragmentBluetoothSearch;
import com.sena.senacamera.ui.fragment.FragmentChangeDeviceName;
import com.sena.senacamera.ui.fragment.FragmentDeviceManual;
import com.sena.senacamera.ui.fragment.FragmentSetDevice;
import com.sena.senacamera.utils.GpsUtil;

import java.util.ArrayList;
import java.util.List;

public class ConnectDeviceActivity extends AppCompatActivity {
    private static final String TAG = ConnectDeviceActivity.class.getSimpleName();
    public Activity activity = this;

    private LinearLayout scanningLayout, failedLayout, connectGuideButton;
    private Button retryButton;
    private ImageView scanningImage, failedImage, scanningRadarImage;

    private List<String> missingPermissions = new ArrayList<>();
    private int bluetoothConnectionStatus = BluetoothConnectStatus.NONE;
    private BluetoothScanManager bluetoothScanManager = BluetoothScanManager.getInstance();

    public final BluetoothSearchCallback bluetoothSearchCallback = new BluetoothSearchCallback() {
        @Override
        public void onFound(BluetoothDeviceInfo device) {
            if (BluetoothScanManager.getInstance().getDeviceCount() != 1) {
                return;
            }

            // set fragment result listener
            getSupportFragmentManager().setFragmentResultListener ("FragmentBluetoothSearch", (LifecycleOwner) activity, new FragmentResultListener() {
                @Override
                public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                    String command = result.getString("command");
                    if (command != null && command.equals("retry")) {
                        // retry scanning
                        bluetoothConnectionStatus = BluetoothConnectStatus.NONE;
                        updateUI();
                        scanBluetoothDevice();
                    } else if (command != null && command.equals("stop")) {
                        // show failed layout
                        bluetoothConnectionStatus = BluetoothConnectStatus.FAILED;
                        updateUI();
                        bluetoothScanManager.stopScan();
                    }
                }
            });

            // open bluetooth search fragment
            Fragment fragment = new FragmentBluetoothSearch();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        @Override
        public void onFailed() {
            bluetoothConnectionStatus = BluetoothConnectStatus.FAILED;
            updateUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect_device);

        scanningLayout = (LinearLayout) findViewById(R.id.searching_layout);
        failedLayout = (LinearLayout) findViewById(R.id.failed_layout);
        connectGuideButton = (LinearLayout) findViewById(R.id.connect_guide_layout);
        retryButton = (Button) findViewById(R.id.retry_button);
        scanningImage = (ImageView) findViewById(R.id.searching_image);
        failedImage = (ImageView) findViewById(R.id.failed_image);
        scanningRadarImage = (ImageView) findViewById(R.id.searching_radar_image);

        retryButton.setOnClickListener(v -> onRetry());
        connectGuideButton.setOnClickListener(v -> onConnectGuide());

        // start scanning
        scanBluetoothDevice();
    }

    public void onRetry() {
        // start scanning again
        scanBluetoothDevice();
    }

    public void onConnectGuide() {
        // open device manual fragment
        Fragment fragment = new FragmentDeviceManual();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @SuppressLint("MissingPermission")
    public void scanBluetoothDevice() {
        // check if bluetooth is turned on
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // turn on bluetooth if it is off currently
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

            // scan is failed
            this.bluetoothConnectionStatus = BluetoothConnectStatus.FAILED;
            this.updateUI();
            return;
        }

        // check & request permission
        if (!checkPermissions()) {
            AppLog.i(TAG, "permissions are missing, so request permissions");
            getPermissions();

            // scan is failed
            bluetoothConnectionStatus = BluetoothConnectStatus.FAILED;
            updateUI();
            return;
        }

        // start bluetooth scan
        // check if already scanning
        if (this.bluetoothConnectionStatus == BluetoothConnectStatus.SCANNING) {
            AppLog.i(TAG, "already scanning");
            return;
        }

        this.bluetoothConnectionStatus = BluetoothConnectStatus.SCANNING;
        updateUI();

        // start scanning
        this.bluetoothScanManager.clearSearchCallbackList();
        this.bluetoothScanManager.addSearchCallback(bluetoothSearchCallback);
        this.bluetoothScanManager.startScan(bluetoothAdapter);
    }

    public void getPermissions() {
        String[] requestedPermission = {this.missingPermissions.get(0)};
        this.missingPermissions.remove(0);
        ActivityCompat.requestPermissions(this, requestedPermission, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            for (int length = permissions.length - 1; length >= 0; length--) {
                if (!this.missingPermissions.isEmpty()) {
                    // if some permissions are missing, request again
                    getPermissions();
                } else {
                    // if all permissions are granted, start scan again
                    scanBluetoothDevice();
                }
            }
        }
    }

    public boolean checkPermissions() {
        boolean ret = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppLog.i(TAG, "checkPermissions: BLUETOOTH_SCAN is missing");
            this.missingPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
            ret = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppLog.i(TAG, "checkPermissions: BLUETOOTH_CONNECT is missing");
            this.missingPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            ret = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            AppLog.i(TAG, "checkPermissions: BLUETOOTH is missing");
            this.missingPermissions.add(Manifest.permission.BLUETOOTH);
            ret = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            AppLog.i(TAG, "checkPermissions: BLUETOOTH_ADMIN is missing");
            this.missingPermissions.add(Manifest.permission.BLUETOOTH_ADMIN);
            ret = false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppLog.i(TAG, "checkPermissions: ACCESS_FINE_LOCATION is missing");
            this.missingPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            ret = false;
        }

//        if (!GpsUtil.isLocationEnabled(this)) {
//            GpsUtil.openGpsSettings(this);
//            ret = false;
//        }

        return ret;
    }

    public void updateUI() {
        if (this.bluetoothConnectionStatus == BluetoothConnectStatus.SCANNING) {
            // show scanning layout
            this.failedLayout.setVisibility(View.GONE);
            this.scanningLayout.setVisibility(View.VISIBLE);

            // show the scanning image
            this.failedImage.setVisibility(View.GONE);
            this.scanningImage.setVisibility(View.VISIBLE);

            // rotate the radar image
            scanningRadarImage.setVisibility(View.VISIBLE);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(scanningRadarImage, "rotation", 0f, 360f);
            rotation.setDuration(2000);
            rotation.setRepeatCount(ObjectAnimator.INFINITE);
            rotation.setInterpolator(new LinearInterpolator());
            rotation.start();
        } else if (this.bluetoothConnectionStatus == BluetoothConnectStatus.FAILED) {
            // show failed layout
            this.scanningLayout.setVisibility(View.GONE);
            this.failedLayout.setVisibility(View.VISIBLE);

            // show the failed image
            this.scanningImage.setVisibility(View.GONE);
            this.scanningRadarImage.setVisibility(View.GONE);
            this.failedImage.setVisibility(View.VISIBLE);
        } else if (this.bluetoothConnectionStatus == BluetoothConnectStatus.NONE) {
            // show scanning layout
            this.failedLayout.setVisibility(View.GONE);
            this.scanningLayout.setVisibility(View.VISIBLE);

            // show scanning image & hide radar image
            this.failedImage.setVisibility(View.GONE);
            this.scanningRadarImage.setVisibility(View.GONE);
            this.scanningImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.bluetoothScanManager.stopScan();
        this.bluetoothScanManager.clearSearchCallbackList();
    }
}
