package com.sena.senacamera.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sena.senacamera.R;

public class CustomTextInput extends LinearLayout {

    private EditText textInput;
    private ImageButton clearButton;
    private TextView charCountText;
    private View underline;

    private final int charCountLimit = 18;

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void afterTextChanged(Editable s) {
            // update char count text
            charCountText.setText(s.toString().length() + "/" + charCountLimit);
        }
    };

    public CustomTextInput(Context context) {
        super(context);
        init(context);
    }

    public CustomTextInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTextInput(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_text_input, this,true);
        textInput = (EditText) view.findViewById(R.id.text_input);
        clearButton = (ImageButton) view.findViewById(R.id.clear_button);
        charCountText = (TextView) view.findViewById(R.id.char_count_text);
        underline = (View) view.findViewById(R.id.underline);

        clearButton.setOnClickListener((v) -> onClear());
        textInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    underline.setBackgroundColor(getResources().getColor(R.color.orange));
                } else {
                    underline.setBackgroundColor(getResources().getColor(R.color.text_input_underline));
                }
            }
        });
        textInput.addTextChangedListener(textWatcher);
    }

    public String getText() {
        return textInput.getText().toString();
    }

    public void setText(String text) {
        textInput.setText(text);
    }

    public void setTextChangedListener(TextWatcher param) {
        textInput.addTextChangedListener(param);
    }

    @SuppressLint("SetTextI18n")
    public void onClear() {
        textInput.setText("");
        charCountText.setText("0/" + charCountLimit);
    }
}
