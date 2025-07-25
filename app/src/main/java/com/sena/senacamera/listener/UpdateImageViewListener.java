package com.sena.senacamera.listener;

import android.graphics.Bitmap;

public interface UpdateImageViewListener {
    void onBitmapLoadComplete(String tag, Bitmap bitmap);
}
