package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.utils.ClickUtils;

public class FragmentForgotPassword extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentForgotPassword.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    Button okButton;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_clear_cache, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.okButton = (Button) this.fragmentLayout.findViewById(R.id.ok_button);

        this.backButton.setOnClickListener(v -> onBack());
        this.okButton.setOnClickListener(v -> onOk());

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
        this.okButton = null;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateFragment() {

    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void onOk() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
