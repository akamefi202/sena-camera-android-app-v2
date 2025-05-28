package com.sena.senacamera.Listener;

import android.graphics.Bitmap;

public interface UpdateImageViewListener {
    void onBitmapLoadComplete(String tag, Bitmap bitmap);
}
