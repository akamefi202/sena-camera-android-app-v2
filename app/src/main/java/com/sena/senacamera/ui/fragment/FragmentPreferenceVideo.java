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
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.data.entity.PropertyTypeString;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.PreferenceActivity;
import com.sena.senacamera.ui.component.MenuSelection;
import com.sena.senacamera.ui.component.MenuSwitch;
import com.sena.senacamera.ui.component.MyProgressDialog;

import java.util.Arrays;

public class FragmentPreferenceVideo extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentPreferenceVideo.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    MenuSelection videoModeMenu, videoResolutionMenu, videoQualityMenu, fovMenu, evMenu, meteringMenu, timelapseIntervalMenu, timelapseDurationMenu, loopRecordingMenu, dateCaptionMenu;
    MenuSwitch autoLowLightMenu, eisMenu;
    LinearLayout resetButton, shootModeLayout;

    String currentMode = "";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_preference_video, viewGroup, false);

        this.videoModeMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.video_mode_layout);
        this.videoResolutionMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.video_resolution_layout);
        this.videoQualityMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.video_quality_layout);
        this.fovMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.fov_layout);
        this.evMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.ev_layout);
        this.meteringMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.metering_layout);
        this.timelapseIntervalMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.timelapse_interval_layout);
        this.timelapseDurationMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.timelapse_duration_layout);
        this.loopRecordingMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.loop_recording_layout);
        this.dateCaptionMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.date_caption_layout);
        this.autoLowLightMenu = (MenuSwitch) this.fragmentLayout.findViewById(R.id.auto_low_light_layout);
        this.eisMenu = (MenuSwitch) this.fragmentLayout.findViewById(R.id.eis_layout);
        this.resetButton = (LinearLayout) this.fragmentLayout.findViewById(R.id.reset_button);
        this.shootModeLayout = (LinearLayout) this.fragmentLayout.findViewById(R.id.shoot_mode_layout);

        this.videoModeMenu.setOnClickListener(this);
        this.videoResolutionMenu.setOnClickListener(this);
        this.videoQualityMenu.setOnClickListener(this);
        this.fovMenu.setOnClickListener(this);
        this.evMenu.setOnClickListener(this);
        this.meteringMenu.setOnClickListener(this);
        this.timelapseIntervalMenu.setOnClickListener(this);
        this.timelapseDurationMenu.setOnClickListener(this);
        this.loopRecordingMenu.setOnClickListener(this);
        this.dateCaptionMenu.setOnClickListener(this);
        this.autoLowLightMenu.setOnClickListener(this);
        this.eisMenu.setOnClickListener(this);

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
            AppLog.e(TAG, "initialize camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // initialize option list
        // values (menu selection)
        // video mode
        AppLog.i(TAG, "video mode: " + Arrays.asList(baseProperties.getVideoMode().getValueList()));
        this.videoModeMenu.setOptionList(Arrays.asList(baseProperties.getVideoMode().getValueList()));
        // video resolution
        AppLog.i(TAG, "video resolution: " + baseProperties.getVideoResolution().getValueListUI());
        this.videoResolutionMenu.setOptionList(baseProperties.getVideoResolution().getValueListUI());
        // video quality
        AppLog.i(TAG, "video quality: " + Arrays.asList(baseProperties.getPhotoVideoQuality().getValueList()));
        this.videoQualityMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoQuality().getValueList()));
        // fov
        AppLog.i(TAG, "fov: " + Arrays.asList(baseProperties.getPhotoVideoFov().getValueList()));
        this.fovMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoFov().getValueList()));
        // ev
        AppLog.i(TAG, "ev: " + Arrays.asList(baseProperties.getPhotoVideoEv().getValueList()));
        this.evMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoEv().getValueList()));
        // metering
        AppLog.i(TAG, "metering: " + Arrays.asList(baseProperties.getPhotoVideoMetering().getValueList()));
        this.meteringMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoMetering().getValueList()));
        // timelapse interval
        AppLog.i(TAG, "timelapse interval: " + Arrays.asList(baseProperties.getTimelapseInterval().getValueList()));
        this.timelapseIntervalMenu.setOptionList(Arrays.asList(baseProperties.getTimelapseInterval().getValueList()));
        // timelapse duration
        AppLog.i(TAG, "timelapse duration: " + Arrays.asList(baseProperties.getTimelapseDuration().getValueList()));
        this.timelapseDurationMenu.setOptionList(Arrays.asList(baseProperties.getTimelapseDuration().getValueList()));
        // loop recording
        AppLog.i(TAG, "loop recording: " + Arrays.asList(baseProperties.getVideoLoopRecording().getValueList()));
        this.loopRecordingMenu.setOptionList(Arrays.asList(baseProperties.getVideoLoopRecording().getValueList()));
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
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.videoModeMenu = null;
        this.videoResolutionMenu = null;
        this.videoQualityMenu = null;
        this.fovMenu = null;
        this.evMenu = null;
        this.meteringMenu = null;
        this.timelapseIntervalMenu = null;
        this.timelapseDurationMenu = null;
        this.loopRecordingMenu = null;
        this.dateCaptionMenu = null;
        this.autoLowLightMenu = null;
        this.eisMenu = null;
        this.resetButton = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.video_mode_layout) {
            onMenuSelection(videoModeMenu);
        } else if (id == R.id.video_resolution_layout) {
            onMenuSelection(videoResolutionMenu);
        } else if (id == R.id.video_quality_layout) {
            onMenuSelection(videoQualityMenu);
        } else if (id == R.id.fov_layout) {
            onMenuSelection(fovMenu);
        } else if (id == R.id.ev_layout) {
            onMenuSelection(evMenu);
        } else if (id == R.id.metering_layout) {
            onMenuSelection(meteringMenu);
        } else if (id == R.id.timelapse_interval_layout) {
            onMenuSelection(timelapseIntervalMenu);
        } else if (id == R.id.timelapse_duration_layout) {
            onMenuSelection(timelapseDurationMenu);
        } else if (id == R.id.loop_recording_layout) {
            onMenuSelection(loopRecordingMenu);
        } else if (id == R.id.date_caption_layout) {
            onMenuSelection(dateCaptionMenu);
        } else if (id == R.id.auto_low_light_layout) {
            onMenuSwitch(autoLowLightMenu);
        } else if (id == R.id.eis_layout) {
            onMenuSwitch(eisMenu);
        }
    }

    public void updateFragment() {
        AppLog.i(TAG, "updateFragment");

        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "initialize camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // values (menu selection)
        // video mode
        this.videoModeMenu.setValue(baseProperties.getVideoMode().getCurrentUiStringInSetting());
        this.currentMode = baseProperties.getVideoMode().getCurrentUiStringInSetting();
        // video resolution
        this.videoResolutionMenu.setValue(baseProperties.getVideoResolution().getCurrentUiStringInSetting());
        // video quality
        this.videoQualityMenu.setValue(baseProperties.getPhotoVideoQuality().getCurrentUiStringInSetting());
        // fov
        this.fovMenu.setValue(baseProperties.getPhotoVideoFov().getCurrentUiStringInSetting());
        // ev
        this.evMenu.setValue(baseProperties.getPhotoVideoEv().getCurrentUiStringInSetting());
        // metering
        this.meteringMenu.setValue(baseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting());
        // timelapse interval
        this.timelapseIntervalMenu.setValue(baseProperties.getTimelapseInterval().getCurrentUiStringInSetting());
        // timelapse duration
        this.timelapseDurationMenu.setValue(baseProperties.getTimelapseDuration().getCurrentUiStringInSetting());
        // loop recording
        this.loopRecordingMenu.setValue(baseProperties.getVideoLoopRecording().getCurrentUiStringInSetting());
        // date caption
        this.dateCaptionMenu.setValue(baseProperties.getDateCaption().getCurrentUiStringInSetting());

        // values (menu switch)
        // auto low light
        this.autoLowLightMenu.setValue(baseProperties.getVideoAutoLowLight().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));
        // eis
        this.eisMenu.setValue(baseProperties.getVideoEis().getCurrentUiStringInSetting().equals(requireContext().getResources().getString(R.string.on)));

        // show & hide menus
        this.eisMenu.setVisibility(View.GONE);
        this.timelapseIntervalMenu.setVisibility(View.GONE);
        this.timelapseDurationMenu.setVisibility(View.GONE);
        this.loopRecordingMenu.setVisibility(View.GONE);

        if (this.currentMode.equals(requireContext().getResources().getString(R.string.video_mode_normal))) {
            // normal
            this.eisMenu.setVisibility(View.VISIBLE);
        } else if (this.currentMode.equals(requireContext().getResources().getString(R.string.video_mode_timelapse))) {
            // timelapse
            this.timelapseIntervalMenu.setVisibility(View.VISIBLE);
            this.timelapseDurationMenu.setVisibility(View.VISIBLE);
        } else if (this.currentMode.equals(requireContext().getResources().getString(R.string.video_mode_loop_recording))) {
            // loop
            this.loopRecordingMenu.setVisibility(View.VISIBLE);
            this.eisMenu.setVisibility(View.VISIBLE);
        } else {
            // slow motion
        }

        PropertyTypeString videoResolutionProperty = baseProperties.getVideoResolution();
        String videoResolution = baseProperties.getVideoResolution().getCurrentUiStringInSetting();
        if (videoResolution.equals(videoResolutionProperty.getCurrentUiStringInSetting(1))
                || videoResolution.equals(videoResolutionProperty.getCurrentUiStringInSetting(3))
                || videoResolution.equals(videoResolutionProperty.getCurrentUiStringInSetting(6))) {
            this.eisMenu.setVisibility(View.GONE);
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
                baseProperties.getVideoMode().setValueByPosition(0);
                baseProperties.getVideoResolution().setValueByPosition(0);
                baseProperties.getPhotoVideoQuality().setValueByPosition(0);
                baseProperties.getPhotoVideoIso().setValueByPosition(0);
                baseProperties.getPhotoVideoFov().setValueByPosition(0);
                baseProperties.getPhotoVideoEv().setValueByPosition(2);
                baseProperties.getPhotoVideoMetering().setValueByPosition(1);
                baseProperties.getTimelapseInterval().setValueByPosition(0);
                baseProperties.getTimelapseDuration().setValueByPosition(0);
                baseProperties.getDateCaption().setValueByPosition(0);
                baseProperties.getVideoLoopRecording().setValue(0);
                baseProperties.getVideoAutoLowLight().setValue(0);
                baseProperties.getVideoEis().setValue(0);

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
        args.putString("cameraMode", CameraMode.VIDEO);
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

    public void onMenuSwitch(MenuSwitch menuSwitch) {
        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "initialize camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // toggle menu switch
        boolean newValue = !menuSwitch.getValue();
        menuSwitch.setValue(newValue);

        // update the setting
        if (menuSwitch.getTitle().equals(requireContext().getResources().getString(R.string.auto_low_light))) {
            baseProperties.getVideoAutoLowLight().setValue(newValue ? 1: 0);
            // update eis
            eisMenu.setValue(!newValue);
            baseProperties.getVideoEis().setValue(!newValue ? 1: 0);
        } else if (menuSwitch.getTitle().equals(requireContext().getResources().getString(R.string.eis))) {
            baseProperties.getVideoEis().setValue(newValue ? 1: 0);
            // update auto low light
            autoLowLightMenu.setValue(!newValue);
            baseProperties.getVideoAutoLowLight().setValue(!newValue? 1: 0);
        }
    }

}
