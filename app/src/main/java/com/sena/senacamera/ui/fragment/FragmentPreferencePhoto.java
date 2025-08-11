package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.PreferenceActivity;
import com.sena.senacamera.ui.component.MenuSelection;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.utils.ClickUtils;

import java.util.Arrays;

public class FragmentPreferencePhoto extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentPreferencePhoto.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    MenuSelection photoModeMenu, photoResolutionMenu, photoQualityMenu, isoMenu, fovMenu, evMenu, meteringMenu, photoBurstMenu, selfTimerMenu, timelapseIntervalMenu, timelapseDurationMenu, dateCaptionMenu;
    LinearLayout resetButton, shootModeLayout;

    String currentMode = "";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_preference_photo, viewGroup, false);

        this.photoModeMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.photo_mode_menu);
        this.photoResolutionMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.photo_resolution_layout);
        this.photoQualityMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.image_quality_layout);
        this.isoMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.iso_layout);
        this.fovMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.fov_layout);
        this.evMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.ev_layout);
        this.meteringMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.metering_layout);
        this.photoBurstMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.photo_burst_layout);
        this.selfTimerMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.self_timer_layout);
        this.timelapseIntervalMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.timelapse_interval_layout);
        this.timelapseDurationMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.timelapse_duration_layout);
        this.dateCaptionMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.date_caption_layout);
        this.resetButton = (LinearLayout) this.fragmentLayout.findViewById(R.id.reset_button);
        this.shootModeLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.shoot_mode_layout);

        this.photoModeMenu.setOnClickListener(this);
        this.photoResolutionMenu.setOnClickListener(this);
        this.photoQualityMenu.setOnClickListener(this);
        this.isoMenu.setOnClickListener(this);
        this.fovMenu.setOnClickListener(this);
        this.evMenu.setOnClickListener(this);
        this.meteringMenu.setOnClickListener(this);
        this.photoBurstMenu.setOnClickListener(this);
        this.selfTimerMenu.setOnClickListener(this);
        this.timelapseIntervalMenu.setOnClickListener(this);
        this.timelapseDurationMenu.setOnClickListener(this);
        this.dateCaptionMenu.setOnClickListener(this);

        this.resetButton.setOnClickListener(v -> onReset());

        // initialize value, option list, and callbacks
        initializeOptionList();

        // show or hide shoot mode layout
        if (!PreferenceActivity.shootModeParam.isEmpty()) {
            // hide shoot mode layout
            this.shootModeLayout.setVisibility(View.GONE);
        } else {
            // show shoot mode layout
            this.shootModeLayout.setVisibility(View.VISIBLE);
        }

        return this.fragmentLayout;
    }

    public void initializeOptionList() {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // initialize option list
        // values (menu selection)
        // photo mode
        AppLog.i(TAG, "photo mode: " + Arrays.asList(baseProperties.getPhotoMode().getValueList()));
        this.photoModeMenu.setOptionList(Arrays.asList(baseProperties.getPhotoMode().getValueList()));
        // photo resolution
        AppLog.i(TAG, "photo resolution: " + baseProperties.getPhotoResolution().getValueListUI());
        this.photoResolutionMenu.setOptionList(baseProperties.getPhotoResolution().getValueListUI());
        // photo quality
        AppLog.i(TAG, "photo quality: " + Arrays.asList(baseProperties.getPhotoVideoQuality().getValueList()));
        this.photoQualityMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoQuality().getValueList()));
        // iso
        AppLog.i(TAG, "iso: " + Arrays.asList(baseProperties.getPhotoVideoIso().getValueList()));
        this.isoMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoIso().getValueList()));
        // fov
        AppLog.i(TAG, "fov: " + Arrays.asList(baseProperties.getPhotoVideoFov().getValueList()));
        this.fovMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoFov().getValueList()));
        // ev
        AppLog.i(TAG, "ev: " + Arrays.asList(baseProperties.getPhotoVideoEv().getValueList()));
        this.evMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoEv().getValueList()));
        // metering
        AppLog.i(TAG, "metering: " + Arrays.asList(baseProperties.getPhotoVideoMetering().getValueList()));
        this.meteringMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoMetering().getValueList()));
        // photo burst
        AppLog.i(TAG, "photo burst: " + Arrays.asList(baseProperties.getPhotoBurst().getValueList()));
        this.photoBurstMenu.setOptionList(Arrays.asList(baseProperties.getPhotoBurst().getValueList()));
        // self timer
        AppLog.i(TAG, "self timer: " + Arrays.asList(baseProperties.getPhotoSelfTimer().getValueList()));
        this.selfTimerMenu.setOptionList(Arrays.asList(baseProperties.getPhotoSelfTimer().getValueList()));
        // timelapse interval
        AppLog.i(TAG, "timelapse interval: " + Arrays.asList(baseProperties.getTimelapseInterval().getValueList()));
        this.timelapseIntervalMenu.setOptionList(Arrays.asList(baseProperties.getTimelapseInterval().getValueList()));
        // timelapse duration
        AppLog.i(TAG, "timelapse duration: " + Arrays.asList(baseProperties.getTimelapseDuration().getValueList()));
        this.timelapseDurationMenu.setOptionList(Arrays.asList(baseProperties.getTimelapseDuration().getValueList()));
        // date caption
        AppLog.i(TAG, "date caption: " + Arrays.asList(baseProperties.getDateCaption().getValueList()));
        this.dateCaptionMenu.setOptionList(Arrays.asList(baseProperties.getDateCaption().getValueList()));
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
        this.photoModeMenu = null;
        this.photoResolutionMenu = null;
        this.photoQualityMenu = null;
        this.isoMenu = null;
        this.fovMenu = null;
        this.evMenu = null;
        this.meteringMenu = null;
        this.photoBurstMenu = null;
        this.selfTimerMenu = null;
        this.timelapseIntervalMenu = null;
        this.timelapseDurationMenu = null;
        this.dateCaptionMenu = null;
        this.resetButton = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (ClickUtils.isFastClick()) {
            return;
        }

        if (id == R.id.photo_mode_menu) {
            onMenuSelection(photoModeMenu);
        } else if (id == R.id.photo_resolution_layout) {
            onMenuSelection(photoResolutionMenu);
        } else if (id == R.id.image_quality_layout) {
            onMenuSelection(photoQualityMenu);
        } else if (id == R.id.iso_layout) {
            onMenuSelection(isoMenu);
        } else if (id == R.id.fov_layout) {
            onMenuSelection(fovMenu);
        } else if (id == R.id.ev_layout) {
            onMenuSelection(evMenu);
        } else if (id == R.id.metering_layout) {
            onMenuSelection(meteringMenu);
        } else if (id == R.id.photo_burst_layout) {
            onMenuSelection(photoBurstMenu);
        } else if (id == R.id.self_timer_layout) {
            onMenuSelection(selfTimerMenu);
        } else if (id == R.id.timelapse_interval_layout) {
            onMenuSelection(timelapseIntervalMenu);
        } else if (id == R.id.timelapse_duration_layout) {
            onMenuSelection(timelapseDurationMenu);
        } else if (id == R.id.date_caption_layout) {
            onMenuSelection(dateCaptionMenu);
        }
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
        // photo mode
        this.photoModeMenu.setValue(baseProperties.getPhotoMode().getCurrentUiStringInSetting());
        this.currentMode = baseProperties.getPhotoMode().getCurrentUiStringInSetting();
        // photo resolution
        this.photoResolutionMenu.setValue(baseProperties.getPhotoResolution().getCurrentUiStringInSetting());
        // photo quality
        this.photoQualityMenu.setValue(baseProperties.getPhotoVideoQuality().getCurrentUiStringInSetting());
        // iso
        this.isoMenu.setValue(baseProperties.getPhotoVideoIso().getCurrentUiStringInSetting());
        // fov
        this.fovMenu.setValue(baseProperties.getPhotoVideoFov().getCurrentUiStringInSetting());
        // ev
        this.evMenu.setValue(baseProperties.getPhotoVideoEv().getCurrentUiStringInSetting());
        // metering
        this.meteringMenu.setValue(baseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting());
        // photo burst
        this.photoBurstMenu.setValue(baseProperties.getPhotoBurst().getCurrentUiStringInSetting());
        // self timer
        this.selfTimerMenu.setValue(baseProperties.getPhotoSelfTimer().getCurrentUiStringInSetting());
        // timelapse interval
        this.timelapseIntervalMenu.setValue(baseProperties.getTimelapseInterval().getCurrentUiStringInSetting());
        // timelapse duration
        this.timelapseDurationMenu.setValue(baseProperties.getTimelapseDuration().getCurrentUiStringInSetting());
        // date caption
        this.dateCaptionMenu.setValue(baseProperties.getDateCaption().getCurrentUiStringInSetting());

        // show & hide menus
        this.fovMenu.setVisibility(View.GONE);
        this.photoBurstMenu.setVisibility(View.GONE);
        this.selfTimerMenu.setVisibility(View.GONE);
        this.timelapseIntervalMenu.setVisibility(View.GONE);
        this.timelapseDurationMenu.setVisibility(View.GONE);

        if (this.currentMode.equals(requireContext().getResources().getString(R.string.photo_mode_single))) {
            // single
        } else if (this.currentMode.equals(requireContext().getResources().getString(R.string.photo_mode_burst))) {
            // burst
            this.photoBurstMenu.setVisibility(View.VISIBLE);
        } else if (this.currentMode.equals(requireContext().getResources().getString(R.string.photo_mode_timelapse))) {
            // timelapse
            this.fovMenu.setVisibility(View.VISIBLE);
            this.timelapseIntervalMenu.setVisibility(View.VISIBLE);
            this.timelapseDurationMenu.setVisibility(View.VISIBLE);
        } else {
            // self timer
            this.selfTimerMenu.setVisibility(View.VISIBLE);
        }
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
                baseProperties.getPhotoMode().setValueByPosition(0);
                baseProperties.getPhotoResolution().setValueByPosition(0);
                baseProperties.getPhotoVideoQuality().setValueByPosition(0);
                baseProperties.getPhotoVideoIso().setValueByPosition(0);
                baseProperties.getPhotoVideoFov().setValueByPosition(0);
                baseProperties.getPhotoVideoEv().setValueByPosition(2);
                baseProperties.getPhotoVideoMetering().setValueByPosition(1);
                baseProperties.getTimelapseInterval().setValueByPosition(0);
                baseProperties.getTimelapseDuration().setValueByPosition(0);
                baseProperties.getDateCaption().setValueByPosition(0);
                baseProperties.getPhotoBurst().setValueByPosition(0);
                baseProperties.getPhotoSelfTimer().setValueByPosition(0);

                updateFragment();
                MyProgressDialog.closeProgressDialog();
            }
        }).start();
    }

    public void onMenuSelection(MenuSelection menuSelection) {
        // open options fragment
        Fragment fragment = new FragmentOptions();
        Bundle args = new Bundle();
        args.putString("title", menuSelection.getTitle());
        args.putString("cameraMode", CameraMode.PHOTO);
        args.putString("value", menuSelection.getValue());
        args.putString("optionList", new Gson().toJson(menuSelection.getOptionList()));
        fragment.setArguments(args);

        // set fragment result listener
        requireActivity().getSupportFragmentManager().setFragmentResultListener("FragmentOptions", getViewLifecycleOwner(), (resultKey, result) -> {
            String title = result.getString("title");
            if (title != null && title.equals(requireContext().getResources().getString(R.string.modes))) {
                currentMode = result.getString("value");
            }

            updateFragment();
        });

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container_preference, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
