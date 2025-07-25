package com.sena.senacamera.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.bluetooth.BluetoothInfo;
import com.sena.senacamera.data.Mode.DeviceListMode;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.listener.BluetoothCommandCallback;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.adapter.BluetoothDeviceListAdapter;
import com.sena.senacamera.ui.adapter.CameraDeviceListAdapter;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.utils.ConvertTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {
    private static final String TAG = DeviceListActivity.class.getSimpleName();
    private Activity activity = this;

    private ImageButton backButton;
    private Button addNewDeviceButton, cancelButton, saveButton, editButton, deleteAllButton;
    private ListView deviceListView;
    private LinearLayout deviceListLayout, bottomButtonLayout;
    private TextView titleText;

    private String deviceListMode = DeviceListMode.NORMAL;
    private CameraDeviceListAdapter deviceListAdapter;
    private BluetoothDeviceManager bluetoothDeviceManager = BluetoothDeviceManager.getInstance();
    private BluetoothCommandManager bluetoothCommandManager = BluetoothCommandManager.getInstance();
    private AppDialogManager appDialogManager = AppDialogManager.getInstance();
    private int draggedIndex = -1, orgCurrentIndex = -1;
    private List<CameraDeviceInfo> orgDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_list);

        backButton = (ImageButton) findViewById(R.id.back_button);
        addNewDeviceButton = (Button) findViewById(R.id.add_new_device_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        saveButton = (Button) findViewById(R.id.save_button);
        editButton = (Button) findViewById(R.id.edit_button);
        deleteAllButton = (Button) findViewById(R.id.delete_all_button);
        deviceListView = (ListView) findViewById(R.id.device_list);
        deviceListLayout = (LinearLayout) findViewById(R.id.device_list_layout);
        bottomButtonLayout = (LinearLayout) findViewById(R.id.bottom_button_layout);
        titleText = (TextView) findViewById(R.id.title_text);

        backButton.setOnClickListener((v) -> onBack());
        cancelButton.setOnClickListener((v) -> onCancel());
        saveButton.setOnClickListener((v) -> onSave());
        editButton.setOnClickListener((v) -> onEdit());
        deleteAllButton.setOnClickListener((v) -> onDeleteAll());
        addNewDeviceButton.setOnClickListener((v) -> addNewDevice());

        deviceListAdapter = new CameraDeviceListAdapter(this, R.id.device_list, new ArrayList<CameraDeviceInfo>(bluetoothDeviceManager.getDeviceList()));
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnDragListener((v, event) -> {
            AppLog.i(TAG, "setOnDragListener dragEvent: " + event.getAction());
            if (event.getAction() == DragEvent.ACTION_DROP) {
                int dropPosition = deviceListView.pointToPosition((int) event.getX(), (int) event.getY());
                if (dropPosition != ListView.INVALID_POSITION && dropPosition != draggedIndex) {
                    saveOriginalDeviceInfo();

                    bluetoothDeviceManager.swapDevices(draggedIndex, dropPosition);
                    updateDeviceListView();
                }
            }
            return true;
        });
    }

    public void updateUI() {
        if (this.deviceListMode.equals(DeviceListMode.NORMAL)) {
            // normal mode
            this.deleteAllButton.setVisibility(View.GONE);
            this.editButton.setVisibility(View.VISIBLE);

            this.titleText.setText(R.string.my_devices);

            this.bottomButtonLayout.setVisibility(View.GONE);
            this.addNewDeviceButton.setVisibility(View.VISIBLE);

            this.deviceListLayout.setPadding(32, 0, 32, 0);
            this.deviceListLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_gray_section, (Resources.Theme) null));

            this.deviceListView.setDivider(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.divider_gray_color, (Resources.Theme) null)));
            this.deviceListView.setDividerHeight(1);
        } else {
            // edit mode
            this.editButton.setVisibility(View.GONE);
            this.deleteAllButton.setVisibility(View.VISIBLE);

            this.titleText.setText(R.string.action_edit);

            this.addNewDeviceButton.setVisibility(View.GONE);
            this.bottomButtonLayout.setVisibility(View.VISIBLE);

            this.deviceListLayout.setPadding(0, 0, 0, 0);
            this.deviceListLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.full_transparent, (Resources.Theme) null));

            this.deviceListView.setDivider(new ColorDrawable(ResourcesCompat.getColor(getResources(), R.color.full_transparent, (Resources.Theme) null)));
            this.deviceListView.setDividerHeight(16);
        }

        this.deleteAllButton.setEnabled(bluetoothDeviceManager.getDeviceCount() != 0);
        this.deviceListAdapter.notifyDataSetChanged();
    }

    public void onBack() {
        if (this.deviceListMode.equals(DeviceListMode.NORMAL)) {
            // normal mode
            finish();
        } else {
            // edit mode
            onCancel();
        }
    }

    public void addNewDevice() {
        startActivity(new Intent(this, ConnectDeviceActivity.class));
    }

    public void onCancel() {
        if (this.deviceListMode.equals(DeviceListMode.NORMAL)) {
            return;
        }

        // check if anything is changed
        if (orgDeviceList == null) {
            deviceListMode = DeviceListMode.NORMAL;
            updateUI();
            return;
        }

        appDialogManager.showUnsavedChangesDialog(this, new DialogButtonListener() {
            @Override
            public void onContinue() {
                // cancel changes, set current device with original info
                loadOriginalDeviceInfo();
                updateDeviceListView();

                deviceListMode = DeviceListMode.NORMAL;
                updateUI();

                clearOriginalDeviceInfo();
            }
        });
    }

    public void onSave() {
        if (this.deviceListMode.equals(DeviceListMode.NORMAL)) {
            return;
        }

        // check if anything is changed
        if (this.orgDeviceList != null) {

            CameraDeviceInfo currentDeviceInfo, orgDeviceInfo = null;
            currentDeviceInfo = bluetoothDeviceManager.getCurrentDevice();
            if (this.orgCurrentIndex < this.orgDeviceList.size()) {
                orgDeviceInfo = this.orgDeviceList.get(this.orgCurrentIndex);
            }

            // check if current device name is edited & it is connected
            if (currentDeviceInfo != null && orgDeviceInfo != null && !currentDeviceInfo.wifiSsid.equals(orgDeviceInfo.wifiSsid)
                    && bluetoothDeviceManager.isThisDeviceConnected(this, orgDeviceInfo.wifiSsid)) {
                // update current device name via bluetooth command
                String currentSsid = bluetoothDeviceManager.getCurrentDevice().wifiSsid;
                String currentPassword = bluetoothDeviceManager.getCurrentDevice().wifiPassword;
                CameraDeviceInfo finalOrgDeviceInfo = orgDeviceInfo;
                bluetoothCommandManager.addCommand(BluetoothInfo.setCameraWifiInfoCommand(currentSsid, currentPassword), BluetoothInfo.setCameraWifiInfoCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);
                        String ssid = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 1, 33));
                        String password = ConvertTools.getStringFromByteArray(Arrays.copyOfRange(payload, 33, 65));
                        bluetoothCommandManager.setCurrentWifiSsid(ssid);
                        bluetoothCommandManager.setCurrentWifiPassword(password);
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep ssid: " + ssid + ", password: " + password);

                        // disconnect wifi connection & remove the device with original name from network
                        MWifiManager.disconnect(activity);
                        MWifiManager.removeCurrentNetwork(activity, finalOrgDeviceInfo.wifiSsid, finalOrgDeviceInfo.wifiPassword, null);

                        // write current device list to shared preferences
                        bluetoothDeviceManager.writeToSharedPref(activity);
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "setCameraWifiInfoCmdRep failed");
                    }
                });
            } else {
                // current device name is not edited
                // write current device list to shared preferences
                this.bluetoothDeviceManager.writeToSharedPref(this);
            }
        }

        this.deviceListMode = DeviceListMode.NORMAL;
        updateUI();

        clearOriginalDeviceInfo();
    }

    public void onEdit() {
        clearOriginalDeviceInfo();

        this.deviceListMode = DeviceListMode.EDIT;
        updateUI();
    }

    public void onDeleteAll() {
        if (!bluetoothDeviceManager.isCurrentDeviceConnected(this)) {
            appDialogManager.showDeleteConfirmDialog(this, new DialogButtonListener() {
                @Override
                public void onDelete() {
                    saveOriginalDeviceInfo();

                    bluetoothDeviceManager.updateDeviceList(new ArrayList<>());
                    bluetoothDeviceManager.currentIndex = 0;
                    updateDeviceListView();
                }
            }, getResources().getString(R.string.all_devices));
        } else {
            appDialogManager.showAlertDialog(activity, null, activity.getResources().getString(R.string.not_allowed_to_delete_device));
        }
    }

    public String getDeviceListMode() {
        return this.deviceListMode;
    }

    public void setDraggedIndex(int index) {
        this.draggedIndex = index;
    }

    public void updateDeviceListView() {
        deviceListAdapter = new CameraDeviceListAdapter(this, R.id.device_list, new ArrayList<CameraDeviceInfo>(bluetoothDeviceManager.getDeviceList()));
        deviceListView.setAdapter(deviceListAdapter);
    }

    public void saveOriginalDeviceInfo() {
        if (orgDeviceList != null) {
            // already saved
            return;
        }

        orgDeviceList = new ArrayList<>();
        for (CameraDeviceInfo item: bluetoothDeviceManager.getDeviceList()) {
            orgDeviceList.add(new CameraDeviceInfo(item.bleName, item.bleAddress, item.wifiSsid, item.wifiPassword, item.firmwareVerison));
        }
        orgCurrentIndex = bluetoothDeviceManager.currentIndex;
    }

    public void clearOriginalDeviceInfo() {
        orgDeviceList = null;
        orgCurrentIndex = -1;
    }

    public void loadOriginalDeviceInfo() {
        if (orgDeviceList == null) {
            return;
        }

        bluetoothDeviceManager.currentIndex = orgCurrentIndex;
        bluetoothDeviceManager.updateDeviceList(orgDeviceList);
    }
}