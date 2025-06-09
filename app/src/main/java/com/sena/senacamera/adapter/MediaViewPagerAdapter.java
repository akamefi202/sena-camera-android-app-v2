package com.sena.senacamera.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sena.senacamera.ui.fragment.LocalMediaListFragment;
import com.sena.senacamera.ui.fragment.RemoteMediaListFragment;

public class MediaViewPagerAdapter extends FragmentStateAdapter {
    RemoteMediaListFragment remoteMediaListFragment;
    LocalMediaListFragment localMediaListFragment;

    public MediaViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        remoteMediaListFragment = new RemoteMediaListFragment();
        localMediaListFragment = new LocalMediaListFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return remoteMediaListFragment;
        } else {
            // position is 1
            return localMediaListFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public void updateUI() {
        remoteMediaListFragment.updateUI();
        localMediaListFragment.updateUI();
    }

    public void deselectAllFiles() {
        //remoteMediaListFragment.deselectAllFiles();
        //localMediaListFragment.deselectAllFiles();
    }
}
