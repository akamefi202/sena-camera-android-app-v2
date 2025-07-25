package com.sena.senacamera.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.sena.senacamera.ui.component.CustomPasswordInput;
import com.sena.senacamera.utils.ClickUtils;

public class FragmentChangePassword extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentChangePassword.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    Button okButton;
    CustomPasswordInput newPasswordInput, confirmPasswordInput;
    ImageView passwordCountCheck, passwordCharacterCheck, passwordMatchCheck;

    boolean pwdCountConfirmed = false, pwdCharacterConfirmed = false, pwdMatchConfirmed = false;

    TextWatcher newPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // update password length check status
            if (s.length() >= 8 && s.length() <= 12) {
                passwordCountCheck.setImageResource(R.drawable.status_password_check_on);
                pwdCountConfirmed = true;
            } else {
                passwordCountCheck.setImageResource(R.drawable.status_password_check_off);
                pwdCountConfirmed = false;
            }

            // update password character check status
            if (s.toString().contains("[") || s.toString().contains("]") || s.length() == 0) {
                passwordCharacterCheck.setImageResource(R.drawable.status_password_check_off);
                pwdCharacterConfirmed = false;
            } else {
                passwordCharacterCheck.setImageResource(R.drawable.status_password_check_on);
                pwdCharacterConfirmed = true;
            }

            updateFragment();
        }
    };

    TextWatcher confirmPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // update password match check status
            if (s.toString().equals(newPasswordInput.getText())) {
                passwordMatchCheck.setImageResource(R.drawable.status_password_check_on);
                pwdMatchConfirmed = true;
            } else {
                passwordMatchCheck.setImageResource(R.drawable.status_password_check_off);
                pwdMatchConfirmed = false;
            }

            updateFragment();
        }
    };

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_change_password, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.okButton = (Button) this.fragmentLayout.findViewById(R.id.ok_button);
        this.passwordCountCheck = (ImageView) this.fragmentLayout.findViewById(R.id.password_count_check);
        this.passwordCharacterCheck = (ImageView) this.fragmentLayout.findViewById(R.id.password_character_check);
        this.passwordMatchCheck = (ImageView) this.fragmentLayout.findViewById(R.id.password_match_check);
        this.newPasswordInput = this.fragmentLayout.findViewById(R.id.new_password_input);
        this.confirmPasswordInput = this.fragmentLayout.findViewById(R.id.confirm_password_input);

        this.backButton.setOnClickListener(v -> onBack());
        this.okButton.setOnClickListener(v -> onOk());

        this.newPasswordInput.setTextChangedListener(newPasswordWatcher);
        this.confirmPasswordInput.setTextChangedListener(confirmPasswordWatcher);

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
        this.passwordCountCheck = null;
        this.passwordCharacterCheck = null;
        this.passwordMatchCheck = null;
        this.newPasswordInput = null;
        this.confirmPasswordInput = null;
    }

    @Override
    public void onClick(View v) {

    }

    public void updateFragment() {
        this.okButton.setEnabled(pwdCountConfirmed && pwdCharacterConfirmed && pwdMatchConfirmed);
    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void onOk() {
        // change password
    }

}
