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
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.component.MenuLink;
import com.sena.senacamera.ui.component.MenuSelection;
import com.sena.senacamera.ui.component.MenuSwitch;
import com.sena.senacamera.utils.ClickUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentPreferencePhoto extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentPreferencePhoto.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    MenuSelection modeMenu, resolutionMenu, imageQualityMenu, isoMenu, fovMenu, evMenu, meteringMenu, photoBurstMenu, selfTimerMenu, timelapseIntervalMenu, timelapseDurationMenu, dateCaptionMenu;
    LinearLayout resetButton;

    List<String> modeList = new ArrayList<>();
    String currentMode = "";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_preference_photo, viewGroup, false);

        this.modeMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.photo_mode_layout);
        this.resolutionMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.photo_resolution_layout);
        this.imageQualityMenu = (MenuSelection) this.fragmentLayout.findViewById(R.id.image_quality_layout);
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

        this.resetButton.setOnClickListener(v -> onReset());

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
        // photo mode
        modeList = new ArrayList<>(Arrays.asList(
                requireContext().getResources().getString(R.string.photo_mode_single),
                requireContext().getResources().getString(R.string.photo_mode_burst),
                requireContext().getResources().getString(R.string.photo_mode_timelapse),
                requireContext().getResources().getString(R.string.photo_mode_self_timer)
        ));
        this.modeMenu.setOptionList(modeList);
        this.modeMenu.setValue(modeList.get(0));
        // resolution
        this.resolutionMenu.setOptionList(new ArrayList<>());
        this.resolutionMenu.setValue("");
        // image quality
        AppLog.i(TAG, "image quality: " + Arrays.asList(baseProperties.getPhotoVideoImageQuality().getValueList()));
        this.imageQualityMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoImageQuality().getValueList()));
        this.imageQualityMenu.setValue(baseProperties.getPhotoVideoImageQuality().getCurrentUiStringInSetting());
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
        // photo burst
        this.photoBurstMenu.setOptionList(new ArrayList<>());
        this.photoBurstMenu.setValue("");
        // self timer
        this.selfTimerMenu.setOptionList(new ArrayList<>());
        this.selfTimerMenu.setValue("");
        // timelapse interval
        this.timelapseIntervalMenu.setOptionList(Arrays.asList(baseProperties.getTimeLapseStillInterval().getValueStringList()));
        this.timelapseIntervalMenu.setValue(baseProperties.getTimeLapseStillInterval().getCurrentValue());
        // timelapse duration
        AppLog.i(TAG, "timelapse duration: " + Arrays.asList(baseProperties.getPhotoVideoTimelapseDuration().getValueList()));
        this.timelapseDurationMenu.setOptionList(Arrays.asList(baseProperties.getPhotoVideoTimelapseDuration().getValueList()));
        this.timelapseDurationMenu.setValue(baseProperties.getPhotoVideoTimelapseDuration().getCurrentUiStringInSetting());
        // date caption
        AppLog.i(TAG, "date caption: " + Arrays.asList(baseProperties.getDateStamp().getValueList()));
        this.dateCaptionMenu.setOptionList(Arrays.asList(baseProperties.getDateStamp().getValueList()));
        this.dateCaptionMenu.setValue(baseProperties.getDateStamp().getCurrentUiStringInSetting());
    }

    @Override
    public void onResume() {
        super.onResume();

        //updateFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.modeMenu = null;
        this.resolutionMenu = null;
        this.imageQualityMenu = null;
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

        if (id == R.id.photo_mode_layout) {
            onMenuSelection(modeMenu);
        } else if (id == R.id.photo_resolution_layout) {
            onMenuSelection(resolutionMenu);
        } else if (id == R.id.image_quality_layout) {
            onMenuSelection(imageQualityMenu);
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
            AppLog.e(TAG, "initialize camera is disconnected");
            requireActivity().finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // values (menu selection)
        // photo mode
        this.modeMenu.setValue(currentMode);
        // resolution
        this.resolutionMenu.setValue("");
        // image quality
        this.imageQualityMenu.setValue(baseProperties.getPhotoVideoImageQuality().getCurrentUiStringInSetting());
        // iso
        this.isoMenu.setValue(baseProperties.getPhotoIso().getCurrentUiStringInSetting());
        // fov
        this.fovMenu.setValue(baseProperties.getVideoFov().getCurrentUiStringInSetting());
        // ev
        this.evMenu.setValue(baseProperties.getPhotoVideoEv().getCurrentUiStringInSetting());
        // metering
        this.meteringMenu.setValue(baseProperties.getPhotoVideoMetering().getCurrentUiStringInSetting());
        // photo burst
        this.photoBurstMenu.setValue("");
        // self timer
        this.selfTimerMenu.setValue("");
        // timelapse interval
        this.timelapseIntervalMenu.setValue(baseProperties.getTimeLapseStillInterval().getCurrentValue());
        // timelapse duration
        this.timelapseDurationMenu.setValue(baseProperties.getPhotoVideoTimelapseDuration().getCurrentUiStringInSetting());
        // date caption
        this.dateCaptionMenu.setValue(baseProperties.getDateStamp().getCurrentUiStringInSetting());
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

}
