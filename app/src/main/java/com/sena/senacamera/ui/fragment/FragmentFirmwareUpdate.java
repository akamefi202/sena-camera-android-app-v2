package com.sena.senacamera.ui.fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.bluetooth.BluetoothCommandManager;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.bluetooth.BluetoothInfo;
import com.sena.senacamera.bluetooth.FirmwareUpdateStatus;
import com.sena.senacamera.data.SystemInfo.MWifiManager;
import com.sena.senacamera.data.entity.BluetoothDeviceInfo;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.data.entity.DownloadInfo;
import com.sena.senacamera.function.CameraAction.PbDownloadManager;
import com.sena.senacamera.listener.BluetoothCommandCallback;
import com.sena.senacamera.listener.BluetoothConnectCallback;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.activity.MainActivity;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.component.MenuSelection;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.utils.ClickUtils;
import com.sena.senacamera.utils.ConvertTools;
import com.sena.senacamera.utils.FileDownloader;
import com.sena.senacamera.utils.SenaXmlParser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentFirmwareUpdate extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentFirmwareUpdate.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    ImageView currentCameraImage, versionStatusIcon;
    TextView currentVersionText, latestVersionText, firmwareUpdateStatusText;
    Button updateButton, okButton;
    LinearLayout warningTextLayout, versionUpdatingLayout, versionInfoLayout, firmwareLanguageLayout;
    RelativeLayout currentVersionLayout, latestVersionLayout;
    MenuSelection firmwareLanguageMenu;
    View versionDivider;
    ProgressBar firmwareUpdateProgressBar;

    AppDialogManager appDialogManager = AppDialogManager.getInstance();
    SenaXmlParser xmlManager = SenaXmlParser.getInstance();
    public BluetoothCommandManager bleCommandManager = BluetoothCommandManager.getInstance();
    public BluetoothDeviceManager bleDeviceManager = BluetoothDeviceManager.getInstance();
    String TEMP_FIRMWARE_FILENAME = "firmware_temp.img";
    String firmwareUrl = "", firmwareFolderPath = "", firmwareLanguage = "";
    List<String> firmwareLanguageList = new ArrayList<>();
    boolean isInstalling = false, isInstalled = false;
    int installPercent = 0;
    long firmwareInstallDuration = 200000;
    private Timer updateProgressTimer;

    static {
        System.loadLibrary("native-firmware-lib");
    }

    public native int splitFirmware(String filePath, String folderPath);

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_firmware_update, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.currentCameraImage = (ImageView) this.fragmentLayout.findViewById(R.id.current_camera_image);
        this.versionStatusIcon = (ImageView) this.fragmentLayout.findViewById(R.id.version_status_icon);
        this.currentVersionText = (TextView) this.fragmentLayout.findViewById(R.id.current_version_text);
        this.latestVersionText = (TextView) this.fragmentLayout.findViewById(R.id.latest_version_text);
        this.updateButton = (Button) this.fragmentLayout.findViewById(R.id.update_button);
        this.okButton = (Button) this.fragmentLayout.findViewById(R.id.ok_button);
        this.versionDivider = (View) this.fragmentLayout.findViewById(R.id.version_divider);
        this.firmwareLanguageMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.firmware_language_menu);
        this.warningTextLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.warning_layout);
        this.versionUpdatingLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.version_updating_layout);
        this.versionInfoLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.version_info_layout);
        this.firmwareLanguageLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.firmware_language_layout);
        this.currentVersionLayout = (RelativeLayout) this.fragmentLayout.findViewById(R.id.current_version_layout);
        this.latestVersionLayout = (RelativeLayout) this.fragmentLayout.findViewById(R.id.latest_version_layout);
        this.firmwareUpdateProgressBar = (ProgressBar) this.fragmentLayout.findViewById(R.id.firmware_update_progress_bar);
        this.firmwareUpdateStatusText = (TextView) this.fragmentLayout.findViewById(R.id.firmware_update_status_text);

        this.firmwareLanguageMenu.setOnClickListener(this);
        this.backButton.setOnClickListener(v -> onBack());
        this.updateButton.setOnClickListener(v -> onUpdate());
        this.okButton.setOnClickListener(v -> onOk());

        initialize();

        return this.fragmentLayout;
    }

    public void initialize() {
        // get current & latest firmware versions
        latestVersionText.setText(xmlManager.getLatestFirmwareVersion());
        currentVersionText.setText(bleDeviceManager.getCurrentDevice().firmwareVersion);

        // firmware language menu
        firmwareLanguageList = xmlManager.getFirmwareLanguageList();
        firmwareLanguageMenu.setOptionList(firmwareLanguageList);

        if (!firmwareLanguageList.isEmpty()) {
            firmwareLanguage = firmwareLanguageList.get(0);
            firmwareLanguageMenu.setValue(firmwareLanguage);
        }

        // set firmware url & path
        int index = firmwareLanguageList.indexOf(firmwareLanguage);
        if (index != -1) {
            firmwareUrl = xmlManager.getFirmwareUrlList().get(index);
        }
        firmwareFolderPath = requireContext().getExternalFilesDir(null).getPath() + "/";

        updateFragment();
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
        this.currentCameraImage = null;
        this.versionStatusIcon = null;
        this.currentVersionText = null;
        this.latestVersionText = null;
        this.updateButton = null;
        this.okButton = null;
        this.versionDivider = null;
        this.firmwareLanguageMenu = null;
        this.warningTextLayout = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (ClickUtils.isFastClick()) {
            return;
        }

        if (id == R.id.firmware_language_menu) {
            // open options fragment
            Fragment fragment = new FragmentOptions();
            Bundle args = new Bundle();
            args.putString("title", requireContext().getResources().getString(R.string.firmware_language));
            args.putString("value", firmwareLanguage);
            args.putString("optionList", new Gson().toJson(firmwareLanguageList));
            fragment.setArguments(args);

            // set fragment result listener
            requireActivity().getSupportFragmentManager().setFragmentResultListener("FragmentOptions", getViewLifecycleOwner(), (resultKey, result) -> {
                firmwareLanguage = result.getString("value");
                firmwareLanguageMenu.setValue(firmwareLanguage);
            });

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void updateFragment() {
        if (isInstalling) {
            // firmware is being installed currently
            versionInfoLayout.setVisibility(View.GONE);
            versionUpdatingLayout.setVisibility(View.VISIBLE);

            firmwareLanguageLayout.setVisibility(View.GONE);
            warningTextLayout.setVisibility(View.VISIBLE);

            updateButton.setVisibility(View.GONE);
            okButton.setVisibility(View.GONE);
        } else if (isInstalled || currentVersionText.getText().equals(latestVersionText.getText())) {
            // current firmware version is latest version, not installing
            versionUpdatingLayout.setVisibility(View.GONE);
            versionInfoLayout.setVisibility(View.VISIBLE);

            firmwareLanguageLayout.setVisibility(View.GONE);
            warningTextLayout.setVisibility(View.GONE);

            updateButton.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);

            currentVersionLayout.setVisibility(View.GONE);
            versionDivider.setVisibility(View.GONE);
            latestVersionText.setVisibility(View.VISIBLE);

            versionStatusIcon.setImageResource(R.drawable.status_firmware_check);
        } else {
            // current firmware version is not latest version, not installing
            versionUpdatingLayout.setVisibility(View.GONE);
            versionInfoLayout.setVisibility(View.VISIBLE);

            firmwareLanguageLayout.setVisibility(View.VISIBLE);
            warningTextLayout.setVisibility(View.VISIBLE);

            okButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);

            currentVersionLayout.setVisibility(View.VISIBLE);
            latestVersionText.setVisibility(View.VISIBLE);
            versionDivider.setVisibility(View.VISIBLE);

            versionStatusIcon.setImageResource(R.drawable.status_firmware_new);
        }
    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void onOk() {
        if (isInstalled) {
            reconnectDevice();
        } else {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    public void onUpdate() {
        if (ClickUtils.isFastClick()) {
            return;
        }

        if (firmwareUrl.isEmpty()) {
            appDialogManager.showUpdateFailedDialog(requireContext(), null);
            return;
        }

        // check if battery is higher than 50%
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            AppLog.e(TAG, "onUpdate - curCamera is disconnected");
            appDialogManager.showUpdateFailedDialog(requireContext(), null);
            return;
        }
        CameraProperties properties = curCamera.getCameraProperties();
        int batteryLevel = properties.getBatteryElectric();
        if (batteryLevel < 50) {
            appDialogManager.showLowBatteryDialog(requireContext(), null);
            return;
        }

        // update ui
        isInstalling = true;
        installPercent = 0;
        updateFragment();
        updateInstallUI();

        // download the firmware file
        FileDownloader downloader = new FileDownloader(this.getContext(), new Callback() {
            @Override
            public void processSucceed() {
                AppLog.e(TAG, "firmware download is succeeded");
                splitUploadFirmware();
            }

            @Override
            public void processFailed() {
                AppLog.e(TAG, "firmware download is failed");
                appDialogManager.showAlertDialog(requireContext(), null, requireContext().getResources().getString(R.string.failed_to_access_ota_server));

                isInstalling = false;
                updateFragment();
            }
        });

        downloader.execute(firmwareUrl, firmwareFolderPath + TEMP_FIRMWARE_FILENAME);
    }

    public void splitUploadFirmware() {
        // split firmware file
        if (splitFirmware(firmwareFolderPath + TEMP_FIRMWARE_FILENAME, firmwareFolderPath) == -1) {
            Log.e(TAG, "firmware split is failed");
            appDialogManager.showUpdateFailedDialog(requireContext(), null);

            isInstalling = false;
            updateFragment();
            return;
        }

        // upload firmware files
        // get current camera
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null) {
            Log.e(TAG, "camera is disconnected");
            appDialogManager.showUpdateFailedDialog(requireContext(), null);

            isInstalling = false;
            updateFragment();
            return;
        }

        // get img & brn filename
        String imgFilename = "";
        String brnFilename = "";
        File directory = new File(firmwareFolderPath);
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".img")) {
                imgFilename = file.getName();
            }
            if (file.getName().endsWith(".BRN")) {
                brnFilename = file.getName();
            }
        }

        AppLog.i(TAG, imgFilename + " " + brnFilename);
        if (imgFilename.isEmpty() || brnFilename.isEmpty()) {
            AppLog.e(TAG, "all files are not found");
            appDialogManager.showUpdateFailedDialog(requireContext(), null);

            isInstalling = false;
            updateFragment();
            return;
        }

        // start timer
        bleCommandManager.isFirmwareUpdating = true;
//        updateProgressTimer = new Timer();
//        updateProgressTimer.schedule(new UpdateProgressTask(), 0, 3000);
        new CountDownTimer(firmwareInstallDuration, firmwareInstallDuration / 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                installPercent ++;

                requireActivity().runOnUiThread(() -> {
                    updateInstallUI();
                });
            }

            @Override
            public void onFinish() {
                bleCommandManager.isFirmwareUpdating = false;
                bleCommandManager.addCommand(BluetoothInfo.getFirmwareVersionCommand(), BluetoothInfo.getFirmwareVersionCmdRep, new BluetoothCommandCallback() {
                    @Override
                    public void onSuccess(byte[] response) {
                        AppLog.i(TAG, "getFirmwareVersionCmdRep succeeded");
                        byte[] payload = Arrays.copyOfRange(response, 6, response.length);

                        // get firmware version
                        String currentVersion = BluetoothInfo.getFirmwareVersionFromPayload(payload);
                        currentVersionText.setText(currentVersion);
                        AppLog.i(TAG, "getFirmwareVersionCmdRep firmwareVersion: " + currentVersion);

                        if (latestVersionText.getText().equals(currentVersion)) {
                            // firmware update success
                            isInstalling = false;
                            isInstalled = true;

                            requireActivity().runOnUiThread(() -> {
                                updateFragment();
                            });
                        } else {
                            // firmware update failed
                            isInstalling = false;
                            isInstalled = false;

                            requireActivity().runOnUiThread(() -> {
                                updateFragment();
                                appDialogManager.showUpdateFailedDialog(requireContext(), null);
                            });
                        }
                    }

                    @Override
                    public void onFailure() {
                        AppLog.i(TAG, "getFirmwareVersionCmdRep failed");
                    }
                });
            }
        }.start();

        curCamera.getFileOperation().uploadFile(firmwareFolderPath + imgFilename, imgFilename);
        curCamera.getCameraAction().updateFW(firmwareFolderPath + brnFilename);
    }

    public void updateInstallUI() {
        if (installPercent > 100) {
            installPercent = 100;
        }
        firmwareUpdateProgressBar.setProgress(installPercent);
        firmwareUpdateStatusText.setText(String.format("Updating... %d%%", installPercent));
    }

    @SuppressLint({"MissingPermission"})
    private void reconnectDevice() {
        CameraManager.getInstance().setCurCamera(null);

        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("autoConnect", true);
        startActivity(intent);
    }

    class UpdateProgressTask extends TimerTask {
        String TAG = UpdateProgressTask.class.getSimpleName();

        @Override
        public void run() {
            bleCommandManager.addCommand(BluetoothInfo.getFirmwareUpdateStatusCommand(), BluetoothInfo.getFirmwareUpdateStatusCmdRep, new BluetoothCommandCallback() {
                @Override
                public void onSuccess(byte[] response) {
                    AppLog.i(TAG, "getFirmwareUpdateStatusCmdRep succeeded");
                    byte[] payload = Arrays.copyOfRange(response, 6, response.length);
                    FirmwareUpdateStatus result = BluetoothInfo.getFirmwareUpdateStatus(payload);
                    AppLog.i(TAG, "getFirmwareUpdateStatusCmdRep cameraOn: " + result.cameraOn + ", otaOn: " + result.otaOn + ", totalFirmwareCount: " +
                            result.totalFirmwareCount + ", currentFirmwareIndex" + result.currentFirmwareIndex + ", percent: " + result.percent);
                    firmwareUpdateProgressBar.setProgress((result.percent + (result.currentFirmwareIndex - 1) * 100) / result.totalFirmwareCount);
                }

                @Override
                public void onFailure() {
                    AppLog.i(TAG, "getFirmwareUpdateStatusCmdRep failed");
                }
            });
        }
    }
}
