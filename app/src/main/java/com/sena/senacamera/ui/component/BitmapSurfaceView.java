package com.sena.senacamera.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class BitmapSurfaceView extends SurfaceView {
    public BitmapSurfaceView(Context context) {
        super(context);
    }

    public BitmapSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public Bitmap drawBitmap() {
        Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        draw(new Canvas(createBitmap));
        return createBitmap;
    }
}
