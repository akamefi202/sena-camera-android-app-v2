package com.sena.senacamera.data.entity;

import android.media.Image;

import com.sena.senacamera.data.type.MediaType;

public class MediaItemInfo {
    public String type = MediaType.PHOTO;
    public boolean isSelected = false;
    public boolean isSaved = false;
    public String date;
    public String time;
    public String duration = "00:00";
    public Image thumbnail;
}
