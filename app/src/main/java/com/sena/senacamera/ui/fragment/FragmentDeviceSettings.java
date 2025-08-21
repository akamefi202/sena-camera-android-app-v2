package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.component.MenuLink;
import com.sena.senacamera.ui.component.MenuSelection;
import com.sena.senacamera.ui.component.MenuSwitch;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.utils.ClickUtils;

import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;

public class FragmentDeviceSettings extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentDeviceSettings.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    MenuSelection screenSaverLayout, colorEffectLayout, whiteBalanceLayout, autoPowerOffLayout, deviceLanguageLayout;
    MenuLink wifiLayout, sdCardLayout, deviceInfoLayout, factoryResetLayout;
    MenuSwitch frontDisplayMenu, mainStatusLedMenu, batteryStatusLedMenu;
    LinearLayout resetButton;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_device_settings, viewGroup, false);

        this.screenSaverLayout = (MenuSelection) this.fragmentLayout.findViewById(R.id.screen_saver_layout);
        this.colorEffectLayout = (MenuSelection) this.fragmentLayout.findViewById(R.id.color_effect_layout);
        this.whiteBalanceLayout = (MenuSelection) this.fragmentLayout.findViewById(R.id.white_balance_layout);
        this.autoPowerOffLayout = (MenuSelection) this.fragmentLayout.findViewById(R.id.auto_power_off_layout);
        this.deviceLanguageLayout = (MenuSelection) this.fragmentLayout.findViewById(R.id.device_language_layout);
        this.wifiLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.wifi_layout);
        this.sdCardLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.sd_card_layout);
        this.deviceInfoLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.device_info_layout);
        this.factoryResetLayout = (MenuLink) this.fragmentLayout.findViewById(R.id.factory_reset_layout);
        this.frontDisplayMenu = (MenuSwitch) this.fragmentLayout.findViewById(R.id.front_display_layout);
        this.mainStatusLedMenu = (MenuSwitch) this.fragmentLayout.findViewById(R.id.main_status_led_layout);
        this.batteryStatusLedMenu = (MenuSwitch) this.fragmentLayout.findViewById(R.id.battery_status_led_layout);
        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.resetButton = (LinearLayout) this.fragmentLayout.findViewById(R.id.reset_button);

        this.backButton.setOnClickListener(v -> onBack());
        this.resetButton.setOnClickListener(v -> onReset());

        this.screenSaverLayout.setOnClickListener(this);
        this.colorEffectLayout.setOnClickListener(this);
        this.whiteBalanceLayout.setOnClickListener(this);
        this.autoPowerOffLayout.setOnClickListener(this);
        this.deviceLanguageLayout.setOnClickListener(this);
        this.wifiLayout.setOnClickListener(this);
        this.sdCardLayout.setOnClickListener(this);
        this.deviceInfoLayout.setOnClickListener(this);
        this.factoryResetLayout.setOnClickListener(this);
        this.frontDisplayMenu.setOnClickListener(this);
        this.mainStatusLedMenu.setOnClickListener(this);
        this.batteryStatusLedMenu.setOnClickListener(this);

        // initialize value, option list, and callbacks
        initialize();

        return this.fragmentLayout;
    }

    public void initialize() {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // values (menu selection)
        // screen saver
        AppLog.i(TAG, "screen saver: " + Arrays.asList(baseProperties.getScreenSaver().getValueList()));
        this.screenSaverLayout.setOptionList(Arrays.asList(baseProperties.getScreenSaver().getValueList()));
        this.screenSaverLayout.setValue(baseProperties.getScreenSaver().getCurrentUiStringInSetting());
        // color effect
        AppLog.i(TAG, "color effect: " + Arrays.asList(baseProperties.getGeneralColorEffect().getValueList()));
        this.colorEffectLayout.setOptionList(Arrays.asList(baseProperties.getGeneralColorEffect().getValueList()));
        this.colorEffectLayout.setValue(baseProperties.getGeneralColorEffect().getCurrentUiStringInSetting());
        // white balance
        AppLog.i(TAG, "white balance: " + Arrays.asList(baseProperties.getWhiteBalance().getValueList()));
        this.whiteBalanceLayout.setOptionList(Arrays.asList(baseProperties.getWhiteBalance().getValueList()));
        this.whiteBalanceLayout.setValue(baseProperties.getWhiteBalance().getCurrentUiStringInSetting());
        // auto power off
        AppLog.i(TAG, "auto power off: " + Arrays.asList(baseProperties.getAutoPowerOff().getValueList()));
        this.autoPowerOffLayout.setOptionList(Arrays.asList(baseProperties.getAutoPowerOff().getValueList()));
        this.autoPowerOffLayout.setValue(baseProperties.getAutoPowerOff().getCurrentUiStringInSetting());
        // language
        AppLog.i(TAG, "language: " + Arrays.asList(baseProperties.getGeneralLanguage().getValueList()));
        this.deviceLanguageLayout.setOptionList(Arrays.asList(baseProperties.getGeneralLanguage().getValueList()));
        this.deviceLanguageLayout.setValue(baseProperties.getGeneralLanguage().getCurrentUiStringInSetting());

        // values (menu switch)
        // front display
        this.frontDisplayMenu.setValue(baseProperties.getGeneralFrontDisplay().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
        // main status led
        this.mainStatusLedMenu.setValue(baseProperties.getGeneralMainStatusLed().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
        // battery status led
        this.batteryStatusLedMenu.setValue(baseProperties.getGeneralBatteryStatusLed().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
    }

    @Override
    public void onResume() {
        super.onResume();

        updateFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.backButton = null;
        this.screenSaverLayout = null;
        this.colorEffectLayout = null;
        this.whiteBalanceLayout = null;
        this.autoPowerOffLayout = null;
        this.deviceLanguageLayout = null;
        this.wifiLayout = null;
        this.sdCardLayout = null;
        this.deviceInfoLayout = null;
        this.factoryResetLayout = null;
        this.resetButton = null;
        this.frontDisplayMenu = null;
        this.mainStatusLedMenu = null;
        this.batteryStatusLedMenu = null;
    }

    public void updateFragment() {
        AppLog.i(TAG, "updateFragment");

        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // values (menu selection)
        // screen saver
        this.screenSaverLayout.setValue(baseProperties.getScreenSaver().getCurrentUiStringInSetting());
        // color effect
        this.colorEffectLayout.setValue(baseProperties.getGeneralColorEffect().getCurrentUiStringInSetting());
        // white balance
        this.whiteBalanceLayout.setValue(baseProperties.getWhiteBalance().getCurrentUiStringInSetting());
        // auto power off
        this.autoPowerOffLayout.setValue(baseProperties.getAutoPowerOff().getCurrentUiStringInSetting());
        // language
        this.deviceLanguageLayout.setValue(baseProperties.getGeneralLanguage().getCurrentUiStringInSetting());

        // values (menu switch)
        // front display
        this.frontDisplayMenu.setValue(baseProperties.getGeneralFrontDisplay().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
        // main status led
        this.mainStatusLedMenu.setValue(baseProperties.getGeneralMainStatusLed().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
        // battery status led
        this.batteryStatusLedMenu.setValue(baseProperties.getGeneralBatteryStatusLed().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public void onReset() {
        BottomSheetDialog resetDialog = new BottomSheetDialog(requireContext());
        View resetDialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_reset_to_default, null);
        resetDialog.setContentView(resetDialogLayout);
        resetDialog.show();

        Button resetButton = resetDialogLayout.findViewById(R.id.reset_button);
        ImageButton closeButton = resetDialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDialog.dismiss();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset settings
                resetToDefault();

                resetDialog.dismiss();
            }
        });
    }

    public void resetToDefault() {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        MyProgressDialog.showProgressDialog(requireContext(), R.string.resetting);
        new Thread(new Runnable() {
            @Override
            public void run() {
                baseProperties.getScreenSaver().setValueByPosition(0);
                baseProperties.getGeneralColorEffect().setValueByPosition(0);
                baseProperties.getWhiteBalance().setValueByPosition(0);
                baseProperties.getAutoPowerOff().setValueByPosition(0);
                baseProperties.getGeneralLanguage().setValueByPosition(0);
                baseProperties.getGeneralFrontDisplay().setValue(0);
                baseProperties.getGeneralMainStatusLed().setValue(1);
                baseProperties.getGeneralBatteryStatusLed().setValue(1);

                updateFragment();
                MyProgressDialog.closeProgressDialog();
            }
        }).start();
    }

    public void onFactoryReset() {
        // get camera properties
        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null || !curCamera.isConnected()) {
            AppLog.e(TAG, "camera is disconnected");
            return;
        }
        CameraProperties properties = curCamera.getCameraProperties();

        BottomSheetDialog factoryResetDialog = new BottomSheetDialog(requireContext());
        View factoryResetDialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_factory_reset, null);
        factoryResetDialog.setContentView(factoryResetDialogLayout);
        factoryResetDialog.show();

        Button resetButton = factoryResetDialogLayout.findViewById(R.id.reset_button);
        ImageButton closeButton = factoryResetDialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                factoryResetDialog.dismiss();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // factory reset
                properties.factoryReset();
                requireActivity().finish();

                factoryResetDialog.dismiss();
            }
        });
    }

    public void onWifi() {
        Fragment fragment = new FragmentWifi();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_device_settings, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onSdCard() {
        Fragment fragment = new FragmentSdCard();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_device_settings, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onDeviceInfo() {
        Fragment fragment = new FragmentDeviceInfo();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_device_settings, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
        transaction.add(R.id.fragment_container_device_settings, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onMenuSwitch(MenuSwitch menuSwitch) {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // toggle menu switch
        boolean newValue = !menuSwitch.getValue();
        menuSwitch.setValue(newValue);

        // update the setting
        // 0 is on, 1 is off here
        if (menuSwitch.getTitle().equals(requireContext().getResources().getString(R.string.front_display))) {
            baseProperties.getGeneralFrontDisplay().setValue(newValue ? 0: 1);
        } else if (menuSwitch.getTitle().equals(requireContext().getResources().getString(R.string.main_status_led))) {
            baseProperties.getGeneralMainStatusLed().setValue(newValue ? 0: 1);
        } else if (menuSwitch.getTitle().equals(requireContext().getResources().getString(R.string.battery_status_led))) {
            baseProperties.getGeneralBatteryStatusLed().setValue(newValue ? 0: 1);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (ClickUtils.isFastClick()) {
            return;
        }

        if (id == R.id.screen_saver_layout) {
            onMenuSelection(screenSaverLayout);
        } else if (id == R.id.color_effect_layout) {
            onMenuSelection(colorEffectLayout);
        } else if (id == R.id.white_balance_layout) {
            onMenuSelection(whiteBalanceLayout);
        } else if (id == R.id.auto_power_off_layout) {
            onMenuSelection(autoPowerOffLayout);
        } else if (id == R.id.device_language_layout) {
            onMenuSelection(deviceLanguageLayout);
        } else if (id == R.id.front_display_layout) {
            onMenuSwitch(frontDisplayMenu);
        } else if (id == R.id.main_status_led_layout) {
            onMenuSwitch(mainStatusLedMenu);
        } else if (id == R.id.battery_status_led_layout) {
            onMenuSwitch(batteryStatusLedMenu);
        } else if (id == R.id.wifi_layout) {
            onWifi();
        } else if (id == R.id.sd_card_layout) {
            onSdCard();
        } else if (id == R.id.device_info_layout) {
            onDeviceInfo();
        } else if (id == R.id.factory_reset_layout) {
            onFactoryReset();
        }
    }
}
