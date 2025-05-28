package com.sena.senacamera.data.entity;


import com.icatchtek.reliant.customer.type.ICatchFile;


public class MultiPbItemInfo {
    private static final String TAG = MultiPbItemInfo.class.getSimpleName();
    public ICatchFile iCatchFile;

    public int section;
    public boolean isItemChecked = false;
    private boolean isPanorama = false;
    public String fileSize;
    public String fileTime;
    public String fileDate;
    public String fileDuration;
    public String fileType = "photo"; // "photo" or "video"


    public MultiPbItemInfo(ICatchFile file) {
        super();
        this.iCatchFile = file;
        this.isItemChecked = false;
    }
    public MultiPbItemInfo(ICatchFile file, int section) {
        super();
        this.iCatchFile = file;
        this.section = section;
        this.isItemChecked = false;
    }


    public MultiPbItemInfo(ICatchFile iCatchFile, int section, boolean isPanorama, String fileSize, String fileTime, String fileDate, String fileDuration, String fileType) {
        this.iCatchFile = iCatchFile;
        this.section = section;
        this.isPanorama = isPanorama;
        this.isItemChecked = false;
        this.fileSize = fileSize;
        this.fileTime = fileTime;
        this.fileDate = fileDate;
        this.fileDuration = fileDuration;
        this.fileType = fileType;
    }

    public void setPanorama(boolean panorama) {
        isPanorama = panorama;
    }

    public boolean isPanorama() {
        return this.isPanorama;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getFilePath() {
        return iCatchFile.getFilePath();
    }

    public int getFileHandle() {
        return iCatchFile.getFileHandle();
    }

    public String getFileDate() {
        return fileDate;
    }

    public String getFileSize() {
        return fileSize;
    }

    public long getFileSizeInteger() {
        long fileSize = iCatchFile.getFileSize();
        return  fileSize;
    }

    public String getFileDuration() {
        return fileDuration;
    }

    public String getFileName() {
        return iCatchFile.getFileName();
    }

    public String getFileDateMMSS() {
        return fileTime;
    }

    public String getFileType() {
        return fileType;
    }
}
