package com.sena.senacamera.data.entity;

import com.sena.senacamera.data.type.MediaItemType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupMediaItemInfo extends MediaItemInfo {
    public String fileDate;
    public int fileCount;

    public GroupMediaItemInfo() {
        this.fileDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.fileCount = 0;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.GROUP;
    }

    public GroupMediaItemInfo(String fileDate, int fileCount) {
        this.fileDate = fileDate;
        this.fileCount = fileCount;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.GROUP;
    }
}
