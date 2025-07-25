package com.sena.senacamera.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sena.senacamera.R;
import com.sena.senacamera.listener.Callback;

public class MenuLink extends LinearLayout {
    private static final String TAG = MenuLink.class.getSimpleName();

    LinearLayout componentLayout;
    TextView titleText;
    View divider;

    boolean dividerVisibility = true;
    Callback clickCallback;

    public MenuLink(Context context) {
        super(context);
        init(context, null);
    }

    public MenuLink(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuLink(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setClickCallback(Callback callback) {
        this.clickCallback = callback;
    }

    private void init(Context context, AttributeSet attrs) {
        this.componentLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_menu_link, this,true);

        this.titleText = (TextView) this.componentLayout.findViewById(R.id.menu_title_text);
        this.divider = (View) this.componentLayout.findViewById(R.id.menu_divider);

        this.componentLayout.setOnClickListener((v) -> onClick());

        // set value from attrs
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuLink);

            this.titleText.setText(a.getString(R.styleable.MenuLink_menu_link_title));
            this.dividerVisibility = a.getBoolean(R.styleable.MenuInfo_menu_info_underline, true);

            // set visibility of the divider
            if (this.dividerVisibility) {
                this.divider.setVisibility(View.VISIBLE);
            } else {
                this.divider.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onClick() {
        if (this.clickCallback != null) {
            this.clickCallback.execute();
        }
    }
}
