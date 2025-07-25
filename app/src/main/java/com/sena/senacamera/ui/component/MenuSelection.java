package com.sena.senacamera.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.Gson;
import com.sena.senacamera.R;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.activity.PreferenceActivity;
import com.sena.senacamera.ui.fragment.FragmentOptions;

import java.util.ArrayList;
import java.util.List;

public class MenuSelection extends LinearLayout {
    private static final String TAG = MenuSelection.class.getSimpleName();
    private Context context;

    LinearLayout componentLayout;
    TextView titleText, valueText;
    View divider;

    boolean dividerVisibility = true;
    List<String> optionList = new ArrayList<>();

    public MenuSelection(Context context) {
        super(context);
        init(context, null);
    }

    public MenuSelection(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuSelection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        this.componentLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_menu_selection, this,true);

        this.titleText = (TextView) this.componentLayout.findViewById(R.id.menu_title_text);
        this.valueText = (TextView) this.componentLayout.findViewById(R.id.menu_value_text);
        this.divider = (View) this.componentLayout.findViewById(R.id.menu_divider);

        // set value from attrs
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuSelection);

            this.titleText.setText(a.getString(R.styleable.MenuSelection_menu_selection_title));
            this.valueText.setText(a.getString(R.styleable.MenuSelection_menu_selection_value));
            this.dividerVisibility = a.getBoolean(R.styleable.MenuSelection_menu_selection_underline, true);

            // set visibility of the divider
            if (this.dividerVisibility) {
                this.divider.setVisibility(View.VISIBLE);
            } else {
                this.divider.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setValue(String value) {
        this.valueText.setText(value);
    }

    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public String getTitle() {
        return this.titleText.getText().toString();
    }

    public String getValue() {
        return this.valueText.getText().toString();
    }

    public List<String> getOptionList() {
        return this.optionList;
    }
}
