package com.sena.senacamera.ui.Interface;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public interface PhotoDetailView {
    void setViewPagerAdapter(PagerAdapter adapter);
    void setTopBarVisibility(int visibility);
    void setBottomBarVisibility(int visibility);
    void setTitleText(String indexInfo);
    void setViewPagerCurrentItem(int position);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);
    int getViewPagerCurrentItem();
    int getTopBarVisibility();
    void setViewPagerVisibility(int visibility);
    void setSurfaceViewVisibility(int visibility);
    void setPanoramaTypeTxv( int resId);
}
