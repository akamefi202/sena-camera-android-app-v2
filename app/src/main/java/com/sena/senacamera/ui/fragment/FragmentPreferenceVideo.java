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
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.component.MenuSelection;
import com.sena.senacamera.ui.component.MenuSwitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentPreferenceVideo extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentPreferenceVideo.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    MenuSelection modeMenu, resolutionMenu, videoQualityMenu, isoMenu, fovMenu, evMenu, meteringMenu, timelapseIntervalMenu, timelapseDurationMenu, loopRecordingMenu, dateCaptionMenu;
    MenuSwitch autoLowLightMenu, eisMenu;
    LinearLayout resetButton;

    List<String> modeList = new ArrayList<>();
    String currentMode = "";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_preference_video, viewGroup, false);

        this.modeMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.video_mode_layout);
        this.resolutionMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.video_resolution_layout);
        this.videoQualityMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.video_quality_layout);
        this.isoMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.iso_layout);
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

        this.modeMenu.setOnClickListener(this);
        this.resolutionMenu.setOnClickListener(this);
        this.videoQualityMenu.setOnClickListener(this);
        this.isoMenu.setOnClickListener(this);
        this.fovMenu.setOnClickListener(this);
        this.evMenu.setOnClickListener(this);
        this.meteringMenu.setOnClickListener(this);
        this.timelapseIntervalMenu.setOnClickListener(this);
        this.timelapseDurationMenu.setOnClickListener(this);
        this.loopRecordingMenu.setOnClickListener(this);
        this.dateCaptionMenu.setOnClickListener(this);
        this.autoLowLightMenu.setOnClickListener(this);
        this.eisMenu.setOnClickListener(this);

        this.resetButton.setOnClickListener(v -> reset());

        // initialize value, option list, and callbacks
        initialize();

        return this.fragmentLayout;
    }

    public void initialize() {
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
        modeList = new ArrayList<>(Arrays.asList(
                requireContext().getResources().getString(R.string.video_mode_video),
                requireContext().getResources().getString(R.string.video_mode_slow_motion),
                requireContext().getResources().getString(R.string.video_mode_timelapse),
                requireContext().getResources().getString(R.string.video_mode_loop_recording)
        ));
        this.modeMenu.setOptionList(modeList);
        this.modeMenu.setValue(modeList.get(0));
        // resolution
        this.resolutionMenu.setOptionList(new ArrayList<>());
        this.resolutionMenu.setValue("");
        // video quality
        AppLog.i(TAG, "image quality: " + Arrays.asList(baseProperties.getPhotoVideoImageQuality().getValueList()));
        this.videoQualityMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoImageQuality().getValueList()));
        this.videoQualityMenu.setValue(baseProperties.getPhotoVideoImageQuality().getCurrentUiStringInSetting());
        // iso
        AppLog.i(TAG, "iso: " + Arrays.asList(baseProperties.getPhotoIso().getValueList()));
        this.isoMenu.setOptionList(Arrays.asList(baseProperties.getPhotoIso().getValueList()));
        this.isoMenu.setValue(baseProperties.getPhotoIso().getCurrentUiStringInSetting());
        // fov
        AppLog.i(TAG, "fov: " + Arrays.asList(baseProperties.getVideoFov().getValueList()));
        this.fovMenu.setOptionList(Arrays.asList(baseProperties.getVideoFov().getValueList()));
        this.fovMenu.setValue(baseProperties.getVideoFov().getCurrentUiStringInSetting());
        // ev
        AppLog.i(TAG, "ev: " + Arrays.asList(baseProperties.getPhotoVideoEv().getValueList()));
        this.evMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoEv().getValueList()));
        this.evMenu.setValue(baseProperties.getPhotoVideoEv().getCurrentUiStringInSetting());
        // metering
        AppLog.i(TAG, "metering: " + Arrays.asList(baseProperties.getPhotoVideoMetering().getValueList()));
        this.meteringMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoMetering().getValueList()));
        this.meteringMenu.setValue(baseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting());
        // timelapse interval
        this.timelapseIntervalMenu.setOptionList(Arrays.asList(baseProperties.getTimeLapseStillInterval().getValueStringList()));
        this.timelapseIntervalMenu.setValue(baseProperties.getTimeLapseStillInterval().getCurrentValue());
        // timelapse duration
        AppLog.i(TAG, "timelapse duration: " + Arrays.asList(baseProperties.getPhotoVideoTimelapseDuration().getValueList()));
        this.timelapseDurationMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoTimelapseDuration().getValueList()));
        this.timelapseDurationMenu.setValue(baseProperties.getPhotoVideoTimelapseDuration().getCurrentUiStringInSetting());
        // loop recording
        this.loopRecordingMenu.setOptionList(new ArrayList<>());
        this.loopRecordingMenu.setValue("");
        // date caption
        AppLog.i(TAG, "date caption: " + Arrays.asList(baseProperties.getDateStamp().getValueList()));
        this.dateCaptionMenu.setOptionList(Arrays.asList(baseProperties.getDateStamp().getValueList()));
        this.dateCaptionMenu.setValue(baseProperties.getDateStamp().getCurrentUiStringInSetting());

        // values (menu switch)
        // auto low light
        this.autoLowLightMenu.setValue(false);
        // eis
        this.eisMenu.setValue(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        //updateFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.modeMenu = null;
        this.resolutionMenu = null;
        this.videoQualityMenu = null;
        this.isoMenu = null;
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
            onMenuSelection(modeMenu);
        } else if (id == R.id.video_resolution_layout) {
            onMenuSelection(resolutionMenu);
        } else if (id == R.id.video_quality_layout) {
            onMenuSelection(videoQualityMenu);
        } else if (id == R.id.iso_layout) {
            onMenuSelection(isoMenu);
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
        // photo mode
        this.modeMenu.setValue(currentMode);
        // resolution
//        this.resolutionMenu.setValue(baseProperties.);
        // video quality
        this.videoQualityMenu.setValue(baseProperties.getPhotoVideoImageQuality().getCurrentUiStringInSetting());
        // iso
        this.isoMenu.setValue(baseProperties.getPhotoIso().getCurrentUiStringInSetting());
        // fov
        this.fovMenu.setValue(baseProperties.getVideoFov().getCurrentUiStringInSetting());
        // ev
        this.evMenu.setValue(baseProperties.getPhotoVideoEv().getCurrentUiStringInSetting());
        // metering
        this.meteringMenu.setValue(baseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting());
        // timelapse interval
        this.timelapseIntervalMenu.setValue(baseProperties.getTimeLapseStillInterval().getCurrentValue());
        // timelapse duration
        this.timelapseDurationMenu.setValue(baseProperties.getPhotoVideoTimelapseDuration().getCurrentUiStringInSetting());
        // loop recording
        this.dateCaptionMenu.setValue("");
        // date caption
        this.dateCaptionMenu.setValue(baseProperties.getDateStamp().getCurrentUiStringInSetting());

        // values (menu switch)
        // auto low light
        this.autoLowLightMenu.setValue(false);
        // eis
        this.eisMenu.setValue(false);
    }

    public void reset() {
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

                resetDialog.dismiss();
            }
        });
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
        // toggle menu switch
        boolean newValue = !menuSwitch.getValue();
        menuSwitch.setValue(newValue);

        // update the setting
    }

}
