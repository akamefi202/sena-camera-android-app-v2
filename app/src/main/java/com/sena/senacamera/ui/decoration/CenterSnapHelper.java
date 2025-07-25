package com.sena.senacamera.ui.decoration;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CenterSnapHelper extends LinearSnapHelper {

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        return super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return null;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

        int recyclerCenter = layoutManager.getWidth() / 2;
        View closestView = null;
        int closestDistance = Integer.MAX_VALUE;

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            View child = layoutManager.findViewByPosition(i);
            if (child == null) continue;

            int childCenter = (child.getLeft() + child.getRight()) / 2;
            int distance = Math.abs(childCenter - recyclerCenter);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestView = child;
            }
        }

        return closestView;
    }
}
