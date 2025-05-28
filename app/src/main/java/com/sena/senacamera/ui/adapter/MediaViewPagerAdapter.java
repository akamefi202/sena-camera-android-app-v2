package com.sena.senacamera.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sena.senacamera.ui.fragment.MediaListFragment;

public class MediaViewPagerAdapter extends FragmentStateAdapter {

    public MediaViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new MediaListFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
