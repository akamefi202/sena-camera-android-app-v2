package com.sena.senacamera.ui.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sena.senacamera.R;
import com.sena.senacamera.data.type.MediaStorageType;
import com.sena.senacamera.ui.adapter.PreferencePagerAdapter;
import com.sena.senacamera.ui.fragment.FragmentPreferencePhoto;
import com.sena.senacamera.ui.fragment.FragmentPreferenceVideo;

public class PreferenceActivity extends AppCompatActivity {
    private static final String TAG = PreferenceActivity.class.getSimpleName();

    private ImageButton backButton;
    private TabLayout prefTabView;
    private FragmentPreferenceVideo videoFragment;
    private FragmentPreferencePhoto photoFragment;
    private ViewPager2 prefViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preference);

        backButton = findViewById(R.id.back_button);
        prefTabView = findViewById(R.id.preference_tab_view);
        prefViewPager = findViewById(R.id.preference_viewpager);

        prefViewPager.setAdapter(new PreferencePagerAdapter(this));
        new TabLayoutMediator(prefTabView, prefViewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.title_video);
            } else {
                tab.setText(R.string.title_photo);
            }
        }).attach();

        backButton.setOnClickListener((v) -> {
            finish();
        });

        prefTabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    prefViewPager.setCurrentItem(0);
                } else {
                    prefViewPager.setCurrentItem(1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
