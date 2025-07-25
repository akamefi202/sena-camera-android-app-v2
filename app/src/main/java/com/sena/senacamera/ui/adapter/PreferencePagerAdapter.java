package com.sena.senacamera.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sena.senacamera.ui.fragment.FragmentPreferencePhoto;
import com.sena.senacamera.ui.fragment.FragmentPreferenceVideo;

public class PreferencePagerAdapter extends FragmentStateAdapter {
    public PreferencePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FragmentPreferenceVideo();
        } else {
            return new FragmentPreferencePhoto();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
