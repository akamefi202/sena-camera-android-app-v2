package com.sena.senacamera.data.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MediaItemGroup {
    public String fileDate;
    public List<LocalPbItemInfo> localFileList;
    public List<MultiPbItemInfo> remoteFileList;

    public MediaItemGroup() {
        fileDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        localFileList = new ArrayList<>();
        remoteFileList = new ArrayList<>();
    }
}
