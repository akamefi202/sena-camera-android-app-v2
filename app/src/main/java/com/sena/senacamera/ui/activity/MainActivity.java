package com.sena.senacamera.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;

import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.data.entity.BluetoothDeviceInfo;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.listener.BluetoothConnectCallback;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.presenter.LaunchPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.ui.fragment.FragmentFirmwareUpdate;
import com.sena.senacamera.utils.ClickUtils;
import com.sena.senacamera.utils.SenaXmlParser;
import com.sena.senacamera.utils.WifiCheck;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private final Activity activity = this;

    private ImageButton deviceListButton, settingsButton;
    private LinearLayout mediaButton, preferenceButton, connectedStatusLayout, disconnectedStatusLayout;
    private Button previewButton, connectButton;
    private ImageView batteryStatusIcon, sdCardStatusIcon;
    private TextView batteryPercentText, firmwareVersionText, deviceNameText;


    public LaunchPresenter presenter;
    public BluetoothCommandManager bleCommandManager = BluetoothCommandManager.getInstance();
    public BluetoothDeviceManager bluetoothDeviceManager = BluetoothDeviceManager.getInstance();
    public SenaXmlParser senaXmlParser = SenaXmlParser.getInstance();
    private boolean isFirstConnect = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // set status bar color
        //getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        deviceListButton = findViewById(R.id.device_list_button);
        settingsButton = findViewById(R.id.settings_button);
        previewButton = findViewById(R.id.preview_button);
        connectButton = findViewById(R.id.connect_button);
        mediaButton = findViewById(R.id.media_button);
        preferenceButton = findViewById(R.id.preference_button);
        batteryPercentText = findViewById(R.id.camera_battery_text);
        batteryStatusIcon = findViewById(R.id.camera_battery_status);
        sdCardStatusIcon = findViewById(R.id.camera_sd_card_status);
        firmwareVersionText = findViewById(R.id.firmware_version_status);
        connectedStatusLayout = findViewById(R.id.connected_status_layout);
        disconnectedStatusLayout = findViewById(R.id.disconnected_status_layout);
        deviceNameText = findViewById(R.id.device_name_text);

        // get registered camera device list
        BluetoothDeviceManager.getInstance().readFromSharedPref(getApplicationContext());

        // initialize sena xml parser
        senaXmlParser.setCurrentModel(BluetoothDeviceManager.getInstance().getCurrentDevice().getProductId());
        if (!senaXmlParser.isExecuted) {
            senaXmlParser.setContext(this);
            senaXmlParser.setCallback(new Callback() {
                @Override
                public void processSucceed() {
                    if (bluetoothDeviceManager.isCurrentDeviceConnected(activity)) {
                        compareFirmwareVersion();
                    }
                }

                @Override
                public void processFailed() {
                    AppLog.e(TAG, "xml read is failed");

                    runOnUiThread(() -> {
                        AppDialogManager.getInstance().showAlertDialog(activity, null, activity.getResources().getString(R.string.failed_to_access_ota_server_xml));
                    });
                }
            });
            senaXmlParser.readFromSharedPref();
            senaXmlParser.execute();
        }

        // akamefi202: set LaunchPresenter
        presenter = new LaunchPresenter(MainActivity.this);
        presenter.setViewSena();

        deviceListButton.setOnClickListener(v -> showDeviceList());
        settingsButton.setOnClickListener(v -> showSettings());
        previewButton.setOnClickListener(v -> onPreview());
        connectButton.setOnClickListener(v -> onConnect());
        mediaButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                presenter.redirectToAnotherActivity(this, MediaActivity.class);
                //startActivity(new Intent(v.getContext(), MediaActivity.class));
                return false;
            }

            return true;
        });
        preferenceButton.setOnTouchListener((v, event) -> {
            // check if the camera is connected
            if (!bluetoothDeviceManager.isCurrentDeviceConnected(this)) {
                WifiCheck.showCameraDisconnectedDialog(this, null);
                updateUI();
                return false;
            }

            if (event.getAction() == MotionEvent.ACTION_UP) {
                startActivity(new Intent(v.getContext(), PreferenceActivity.class));
                return false;
            }

            return true;
        });

        // initialize bluetooth command manager
        bleCommandManager.setContext(this);
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("autoConnect")) {
            boolean autoConnect = intent.getBooleanExtra("autoConnect", false);

            if (autoConnect) {
                onConnect();
            }
        }
    }

    private void showDeviceList() {
        startActivity(new Intent(this, DeviceListActivity.class));
    }

    private void showSettings() {
        startActivity(new Intent(this, SettingActivity.class));
    }

    private void onPreview() {
        //this.presenter.launchCameraSena();
        if (bluetoothDeviceManager.isCurrentDeviceConnected(this)) {
            this.presenter.redirectToAnotherActivity(this);
        } else {
            AppLog.e(TAG, "camera is disconnected");
            updateUI();
        }
    }

    @SuppressLint({"MissingPermission"})
    private void onConnect() {
        if (ClickUtils.isFastClick()) {
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Activity activity = this;

        // check if bluetooth is turned on
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            AppLog.i(TAG, "connectCamera bluetooth is turned off");
            // turn on bluetooth if it is off currently
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            return;
        }

        // get current device info (bluetooth name & address)
        CameraDeviceInfo currentDevice = bluetoothDeviceManager.getCurrentDevice();
        if (currentDevice == null) {
            // no device is registered
            return;
        }

        // get bluetooth device object from address
        BluetoothDevice bleDevice = bluetoothAdapter.getRemoteDevice(currentDevice.bleAddress);
        if (bleDevice == null) {
            AppLog.i(TAG, "connectCamera bleDevice is null");
            return;
        }

        // bluetooth connection callback
        BluetoothConnectCallback bluetoothConnectCallback = new BluetoothConnectCallback() {
            @Override
            public void onConnected() {
                AppLog.i(TAG, "connectCamera bluetooth connection is succeeded");

                // check if wifi of phone is turned on
                if (!MWifiManager.isWifiEnabled(getApplicationContext())) {
                    MyToast.show(activity, R.string.wifi_turned_off);
                    return;
                }

                // update current device with wifi ssid & password
                currentDevice.wifiSsid = bleCommandManager.getCurrentWifiSsid();
                currentDevice.wifiPassword = bleCommandManager.getCurrentWifiPassword();
                currentDevice.firmwareVersion = bleCommandManager.getCurrentFirmwareVersion();
                bluetoothDeviceManager.updateCurrentDevice(currentDevice);
                bluetoothDeviceManager.writeToSharedPref(getApplicationContext());

                // connect to the camera device via wifi
                // add wifi connect suggestion
                runOnUiThread(() -> {
                    MyProgressDialog.showProgressDialog(activity, R.string.action_connecting);
                });

                MWifiManager.connect(activity, bleCommandManager.getCurrentWifiSsid(), bleCommandManager.getCurrentWifiPassword(), new Callback() {
                    @Override
                    public void processSucceed() {
                        AppLog.e(TAG, "onConnected: wifi connect is succeeded");
                        runOnUiThread(() -> {
                            MyProgressDialog.closeProgressDialog();
                            updateUI();
                        });
                    }

                    @Override
                    public void processFailed() {
                        AppLog.e(TAG, "onConnected: wifi connect is failed");
                        MyProgressDialog.closeProgressDialog();
                    }
                });

                // open wifi settings
//                new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                    activity.startActivity(intent);
//                }, 1000);
            }

            @Override
            public void onFailed() {
                AppLog.i(TAG, "connectCamera bluetooth connection is failed");
            }
        };

        // connect to the current device via bluetooth
        // wifi of camera device will be turned on
        bleCommandManager.connectDevice(new BluetoothDeviceInfo(bleDevice, currentDevice.serialData, ""), bluetoothConnectCallback, true, false);
    }

    public void onResume() {
        super.onResume();

        if (bluetoothDeviceManager.getDeviceCount() == 0) {
            startActivity(new Intent(this, ConnectDeviceActivity.class));
            finish();
            return;
        }

        updateUI();
    }

    public void updateUI() {
        AppLog.i(TAG, "updateUI");

        // check camera wifi connection status and update ui (preview/connect button)
        // initialize sdk if it is connected
        if (bluetoothDeviceManager.isCurrentDeviceConnected(this)) {
            previewButton.setVisibility(View.VISIBLE);
            connectButton.setVisibility(View.GONE);

            this.presenter.connectCameraSena();
            // updateCameraStatusInfo will be called inside the presenter
        } else {
            previewButton.setVisibility(View.GONE);
            connectButton.setVisibility(View.VISIBLE);

            this.presenter.disconnectCamera();
            updateCameraStatusInfo();
        }

        // show current device name if current device exists
        // disable preview, connect button if no device is registered
        if (bluetoothDeviceManager.getCurrentDevice() != null) {
            senaXmlParser.setCurrentModel(bluetoothDeviceManager.getCurrentDevice().getProductId());
            deviceNameText.setText(bluetoothDeviceManager.getCurrentDevice().wifiSsid);
            connectButton.setEnabled(true);
            previewButton.setEnabled(true);
        } else {
            deviceNameText.setText("");
            connectButton.setEnabled(false);
            previewButton.setEnabled(false);
        }

        // initialize bluetooth command manager
        bleCommandManager.setContext(this);
    }

    public void updateCameraStatusInfo() {
        // camera is disconnected
        if (!bluetoothDeviceManager.isCurrentDeviceConnected(this)) {
            connectedStatusLayout.setVisibility(View.GONE);
            disconnectedStatusLayout.setVisibility(View.VISIBLE);
            return;
        }

        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            return;
        }

        // show status info if camera is connected
        disconnectedStatusLayout.setVisibility(View.GONE);
        connectedStatusLayout.setVisibility(View.VISIBLE);

        // update camera status info
        CameraProperties properties = curCamera.getCameraProperties();
        int batteryLevel = properties.getBatteryElectric();
        boolean isSdCardExist = properties.isSDCardExist();

        // update battery icon
        if (batteryLevel > 100) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_charge);
        } else if (batteryLevel == 100) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_100);
        } else if (batteryLevel >= 80) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_80);
        } else if (batteryLevel >= 60) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_60);
        } else if (batteryLevel >= 40) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_40);
        } else if (batteryLevel >= 20) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_20);
        } else {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_10);
        }

        // update battery percent text
        this.batteryPercentText.setText(batteryLevel + "%");

        // update firmware version text
        this.firmwareVersionText.setText(String.format("v%s", bluetoothDeviceManager.getCurrentDevice().firmwareVersion));

        // update sd card icon
        if (isSdCardExist) {
            sdCardStatusIcon.setImageResource(R.drawable.status_sd_card);
        } else {
            sdCardStatusIcon.setImageResource(R.drawable.status_no_sd_card);
        }

        if (isFirstConnect) {
            AppDialogManager.getInstance().showConnectionReadyDialog(this, new DialogButtonListener() {
                @Override
                public void onOk() {
                    if (senaXmlParser.getUserGuideUrl().isEmpty()) {
                        return;
                    }

                    // open user guide
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(senaXmlParser.getUserGuideUrl()), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            });
            isFirstConnect = false;
        } else {
            AppDialogManager.getInstance().showConnectionSuccessDialog(this, null);
        }
    }

    public void compareFirmwareVersion() {
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            return;
        }

        String currentFirmwareVersion = bluetoothDeviceManager.getCurrentDevice().firmwareVersion;
        if (!senaXmlParser.getLatestFirmwareVersion().equals(currentFirmwareVersion)) {
            firmwareVersionText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_version_tag_update_available, null));
            AppDialogManager.getInstance().showNewFirmwareAvailableDialog(this, new DialogButtonListener() {
                @Override
                public void onUpdate() {
                    Fragment fragment = new FragmentFirmwareUpdate();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        } else {
            firmwareVersionText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_version_tag, null));
        }
    }
}