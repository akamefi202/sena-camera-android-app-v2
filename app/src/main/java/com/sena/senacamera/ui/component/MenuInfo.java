package com.sena.senacamera.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.sena.senacamera.R;

public class MenuInfo extends LinearLayout {
    private static final String TAG = MenuInfo.class.getSimpleName();

    LinearLayout componentLayout;
    TextView titleText, valueText;
    View divider;

    boolean dividerVisibility = true;

    public MenuInfo(Context context) {
        super(context);
        init(context, null);
    }

    public MenuInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.componentLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_menu_info, this,true);

        this.titleText = (TextView) this.componentLayout.findViewById(R.id.menu_title_text);
        this.valueText = (TextView) this.componentLayout.findViewById(R.id.menu_value_text);
        this.divider = (View) this.componentLayout.findViewById(R.id.menu_divider);

        // set value from attrs
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuInfo);

            this.titleText.setText(a.getString(R.styleable.MenuInfo_menu_info_title));
            this.valueText.setText(a.getString(R.styleable.MenuInfo_menu_info_value));
            this.dividerVisibility = a.getBoolean(R.styleable.MenuInfo_menu_info_underline, true);

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
}
