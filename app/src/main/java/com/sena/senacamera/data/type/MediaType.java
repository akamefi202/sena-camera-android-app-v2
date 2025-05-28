package com.sena.senacamera.data.type;

public enum MediaType {
    PHOTO,
    VIDEO;

    public static String getValue(MediaType type) {
        if (type == PHOTO) {
            return "photo";
        } else {
            // if the type is VIDEO
            return "video";
        }
    }
}
