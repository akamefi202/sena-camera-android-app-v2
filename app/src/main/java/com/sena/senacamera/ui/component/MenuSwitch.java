package com.sena.senacamera.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.sena.senacamera.R;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.fragment.FragmentOptions;

public class MenuSwitch extends LinearLayout {
    private static final String TAG = MenuSwitch.class.getSimpleName();

    LinearLayout componentLayout;
    TextView titleText;
    SwitchCompat menuSwitch;
    View divider;

    boolean dividerVisibility = true;

    public MenuSwitch(Context context) {
        super(context);
        init(context, null);
    }

    public MenuSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.componentLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_menu_switch, this,true);

        this.titleText = (TextView) this.componentLayout.findViewById(R.id.menu_title_text);
        this.menuSwitch = (SwitchCompat) this.componentLayout.findViewById(R.id.menu_switch);
        this.divider = (View) this.componentLayout.findViewById(R.id.menu_divider);

        // set value from attrs
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuSwitch);

            this.titleText.setText(a.getString(R.styleable.MenuSwitch_menu_switch_title));
            this.menuSwitch.setChecked(a.getBoolean(R.styleable.MenuSwitch_menu_switch_value, false));
            this.dividerVisibility = a.getBoolean(R.styleable.MenuInfo_menu_info_underline, true);

            // set visibility of the divider
            if (this.dividerVisibility) {
                this.divider.setVisibility(View.VISIBLE);
            } else {
                this.divider.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String getTitle() {
        return (String) this.titleText.getText();
    }

    public void setValue(boolean value) {
        this.menuSwitch.setChecked(value);
    }

    public boolean getValue() {
        return this.menuSwitch.isChecked();
    }
}
