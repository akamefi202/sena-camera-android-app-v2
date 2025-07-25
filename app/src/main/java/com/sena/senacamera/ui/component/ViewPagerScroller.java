package com.sena.senacamera.ui.component;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;
//import org.videolan.libvlc.media.MediaPlayer;

public class ViewPagerScroller extends Scroller {
    //private int duration = MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING;
    private int duration = 0;

    public ViewPagerScroller(Context context) {
        super(context);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public void startScroll(int i, int i2, int i3, int i4, int i5) {
        super.startScroll(i, i2, i3, i4, this.duration);
    }

    public void startScroll(int i, int i2, int i3, int i4) {
        super.startScroll(i, i2, i3, i4, this.duration);
    }
}
