package com.sena.senacamera.ui.component;

import android.content.Context;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sena.senacamera.R;

import java.util.jar.Attributes;

public class CustomPasswordInput extends LinearLayout {

    private ImageButton inputShowButton, inputHideButton;
    private EditText passwordInput;

    private boolean inputHide = true;

    public CustomPasswordInput(Context context) {
        super(context);
        init(context);
    }

    public CustomPasswordInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPasswordInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_password_input, this,true);
        inputShowButton = (ImageButton) view.findViewById(R.id.show_button);
        inputHideButton = (ImageButton) view.findViewById(R.id.hide_button);
        passwordInput = (EditText) view.findViewById(R.id.password_input);

        inputShowButton.setOnClickListener((v) -> onInputShow());
        inputHideButton.setOnClickListener((v) -> onInputHide());
    }

    public void onInputShow() {
        // hide characters of inputted password
        inputHide = true;
        inputShowButton.setVisibility(View.GONE);
        inputHideButton.setVisibility(View.VISIBLE);

        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordInput.setSelection(passwordInput.getText().length());
    }

    public void onInputHide() {
        // show characters of inputted password
        inputHide = false;
        inputHideButton.setVisibility(View.GONE);
        inputShowButton.setVisibility(View.VISIBLE);

        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        passwordInput.setSelection(passwordInput.getText().length());
    }

    public String getText() {
        return passwordInput.getText().toString();
    }

    public void setText(String text) {
        passwordInput.setText(text);
    }

    public void setTextChangedListener(TextWatcher param) {
        passwordInput.addTextChangedListener(param);
    }
}
