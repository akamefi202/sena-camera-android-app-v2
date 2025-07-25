package com.sena.senacamera.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.utils.ClickUtils;

public class FragmentDeviceManual extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentDeviceManual.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    Button nextButton, settingsButton;
    ImageView slideStatusImage, guideImage;
    TextView deviceModelText, manualTitleText, manualDescText;

    int slideIndex = 0;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_device_manual, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.nextButton = (Button) this.fragmentLayout.findViewById(R.id.next_button);
        this.settingsButton = (Button) this.fragmentLayout.findViewById(R.id.settings_button);
        this.deviceModelText = (TextView) this.fragmentLayout.findViewById(R.id.device_model_text);
        this.manualTitleText = (TextView) this.fragmentLayout.findViewById(R.id.manual_title_text);
        this.manualDescText = (TextView) this.fragmentLayout.findViewById(R.id.manual_desc_text);
        this.slideStatusImage = (ImageView) this.fragmentLayout.findViewById(R.id.slide_status_image);
        this.guideImage = (ImageView) this.fragmentLayout.findViewById(R.id.guide_image);

        this.backButton.setOnClickListener(v -> onBack());
        this.nextButton.setOnClickListener(v -> onNext());
        this.settingsButton.setOnClickListener(v -> onSettings());

        return this.fragmentLayout;
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
        this.nextButton = null;
        this.settingsButton = null;
        this.deviceModelText = null;
        this.manualTitleText = null;
        this.manualDescText = null;
        this.slideStatusImage = null;
        this.guideImage = null;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateFragment() {

    }

    private void onBack() {
        if (this.slideIndex == 0) {
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            this.slideIndex = 0;
            this.guideImage.setImageResource(R.drawable.guide_power);
            this.slideStatusImage.setImageResource(R.drawable.status_slide_1);
            this.manualTitleText.setText(R.string.power_on);
            this.manualDescText.setText(R.string.power_on_desc);

            this.settingsButton.setVisibility(View.GONE);
            this.nextButton.setVisibility(View.VISIBLE);
        }
    }

    private void onNext() {
        this.slideIndex = 1;
        this.guideImage.setImageResource(R.drawable.guide_wifi_1);
        this.slideStatusImage.setImageResource(R.drawable.status_slide_2);
        this.manualTitleText.setText(R.string.wifi);
        this.manualDescText.setText(R.string.power_on_desc);

        this.nextButton.setVisibility(View.GONE);
        this.settingsButton.setVisibility(View.VISIBLE);
    }

    private void onSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

}
