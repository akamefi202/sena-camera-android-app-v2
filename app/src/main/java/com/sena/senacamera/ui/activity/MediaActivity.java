package com.sena.senacamera.ui.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.adapter.MediaViewPagerAdapter;

public class MediaActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TabLayout mediaTabView;
    private ViewPager2 mediaViewPager;
    private MediaViewPagerAdapter mediaViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_media);

        backButton = findViewById(R.id.back_button);
        mediaTabView = findViewById(R.id.media_tab_view);
        mediaViewPager = findViewById(R.id.media_viewpager);

        mediaViewPagerAdapter = new MediaViewPagerAdapter(this);
        mediaViewPager.setAdapter(mediaViewPagerAdapter);

        backButton.setOnClickListener((v) -> {
            finish();
        });

        mediaTabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mediaViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mediaViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mediaTabView.getTabAt(position).select();
            }
        });
    }
}