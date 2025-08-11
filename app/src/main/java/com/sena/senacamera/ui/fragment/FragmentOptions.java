package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.data.entity.CameraDeviceInfo;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.ui.adapter.OptionListAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentOptions extends Fragment {
    private static final String TAG = FragmentOptions.class.getSimpleName();

    private ConstraintLayout fragmentLayout;
    private ImageButton backButton;
    private TextView titleText;
    private ListView optionListView;
    private OptionListAdapter optionListAdapter;
    private List<String> optionList = new ArrayList<>();
    private String title = "", value = "", cameraMode = "", originalValue = "";

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_options, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.titleText = (TextView) this.fragmentLayout.findViewById(R.id.setting_title);
        this.optionListView = (ListView) this.fragmentLayout.findViewById(R.id.option_list);

        this.backButton.setOnClickListener(v -> onBack());

        // get the setting title id
        if (getArguments() != null) {
            this.title = getArguments().getString("title");
            this.cameraMode = getArguments().getString("cameraMode");
            this.value = getArguments().getString("value");
            this.originalValue = this.value;

            String json = getArguments().getString("optionList");
            Type type = TypeToken.getParameterized(List.class, String.class).getType();
            this.optionList = new Gson().fromJson(json, type);
            this.titleText.setText(this.title);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        onBack();
                    }
                }
        );

        // initialize the view
        initialize();

        return this.fragmentLayout;
    }

    private void initialize() {
        optionListAdapter = new OptionListAdapter(requireContext(), R.id.option_list, new ArrayList<String>(optionList), this);
        optionListView.setAdapter(optionListAdapter);
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
        this.titleText = null;
        this.optionListView = null;
    }

    public void updateFragment() {

    }

    private void onBack() {
        updateSettingValue();

        Bundle result = new Bundle();
        result.putString("title", this.title);
        result.putString("cameraMode" ,this.cameraMode);
        result.putString("value", this.value);
        requireActivity().getSupportFragmentManager().setFragmentResult(TAG, result);
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    public String getValue() {
        return this.value;
    }

    public void selectOption(int position) {
        this.value = this.optionList.get(position);
    }

    public void updateSettingValue() {
        // check if value is changed
        if (this.originalValue.equals(this.value)) {
            return;
        }

        // check if option list is not empty
        if (this.optionList.isEmpty()) {
            return;
        }

        // get camera setting information
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        // get position
        int position = this.optionList.indexOf(this.value);
        if (position == -1) {
            return;
        }

        if (this.title.equals(requireContext().getResources().getString(R.string.screen_saver))) {
            baseProperties.getScreenSaver().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.color_effect))) {
            baseProperties.getGeneralColorEffect().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.white_balance))) {
            baseProperties.getWhiteBalance().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.auto_power_off))) {
            baseProperties.getAutoPowerOff().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.language))) {
            baseProperties.getGeneralLanguage().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.modes))) {
            if (this.cameraMode.equals(CameraMode.PHOTO)) {
                // photo
                baseProperties.getPhotoMode().setValueByPosition(position);
            } else {
                // video
                baseProperties.getVideoMode().setValueByPosition(position);
            }
        } else if (this.title.equals(requireContext().getResources().getString(R.string.metering))) {
            baseProperties.getPhotoVideoMetering().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.iso))) {
            baseProperties.getPhotoVideoIso().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.ev))) {
            baseProperties.getPhotoVideoEv().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.setting_time_lapse_duration))) {
            baseProperties.getTimelapseDuration().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.setting_time_lapse_interval))) {
            baseProperties.getTimelapseInterval().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.image_quality)) || this.title.equals(requireContext().getResources().getString(R.string.video_quality))) {
            baseProperties.getPhotoVideoQuality().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.date_caption))) {
            baseProperties.getDateCaption().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.photo_burst))) {
            baseProperties.getPhotoBurst().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.photo_mode_self_timer))) {
            baseProperties.getPhotoSelfTimer().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.fov))) {
            baseProperties.getPhotoVideoFov().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.loop_recording))) {
            baseProperties.getVideoLoopRecording().setValueByPosition(position);
        } else if (this.title.equals(requireContext().getResources().getString(R.string.resolution))) {
            if (this.cameraMode.equals(CameraMode.PHOTO)) {
                // photo
                baseProperties.getPhotoResolution().setValueByPosition(position);
            } else {
                // video
                baseProperties.getVideoResolution().setValueByPosition(position);
            }
        } else if (this.title.equals(requireContext().getResources().getString(R.string.wifi_frequency))) {
            baseProperties.getWifiFrequency().setValueByPosition(position);
        }
    }
}
