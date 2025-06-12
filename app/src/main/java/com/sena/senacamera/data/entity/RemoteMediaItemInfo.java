package com.sena.senacamera.data.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.icatchtek.reliant.customer.type.ICatchFile;
import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.data.type.MediaType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class RemoteMediaItemInfo extends MediaItemInfo implements Serializable {
    private static final String TAG = RemoteMediaItemInfo.class.getSimpleName();

    public ICatchFile iCatchFile;
    public int section;
    public boolean isItemDownloaded = false;
    private boolean isPanorama = false;
    public String fileSize;
    public String fileTime;
    public String fileDate;
    public String fileDuration;
    // "photo" or "video"
    public String fileType = MediaType.PHOTO;


    public RemoteMediaItemInfo(ICatchFile file) {
        this.iCatchFile = file;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.REMOTE;
    }

    public RemoteMediaItemInfo(ICatchFile file, int section) {
        this.iCatchFile = file;
        this.section = section;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.REMOTE;
    }

    public RemoteMediaItemInfo(ICatchFile iCatchFile, int section, boolean isPanorama, String fileSize, String fileTime, String fileDate, String fileDuration, String fileType) {
        this.iCatchFile = iCatchFile;
        this.section = section;
        this.isPanorama = isPanorama;
        this.isItemChecked = false;
        this.fileSize = fileSize;
        this.fileTime = fileTime;
        this.fileDate = fileDate;
        this.fileDuration = fileDuration;
        this.fileType = fileType;
        this.mediaItemType = MediaItemType.REMOTE;
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

    public static String serialize(RemoteMediaItemInfo src) {
        try {
            JSONObject dest = new JSONObject();

            dest.put("section", src.section);
            dest.put("isItemDownloaded", src.isItemDownloaded);
            dest.put("isPanorama", src.isPanorama);
            dest.put("fileSize", src.fileSize);
            dest.put("fileTime", src.fileTime);
            dest.put("fileDate", src.fileDate);
            dest.put("fileDuration", src.fileDuration);
            dest.put("fileType", src.fileType);
            dest.put("iCatchFile.fileHandle", src.iCatchFile.getFileHandle());
            dest.put("iCatchFile.filePath", src.iCatchFile.getFilePath());
            dest.put("iCatchFile.fileName", src.iCatchFile.getFileName());
            dest.put("iCatchFile.fileDate", src.iCatchFile.getFileDate());
            dest.put("iCatchFile.fileType", src.iCatchFile.getFileType());
            dest.put("iCatchFile.fileSize", src.iCatchFile.getFileSize());
            dest.put("iCatchFile.fileWidth", src.iCatchFile.getFileWidth());
            dest.put("iCatchFile.fileHeight", src.iCatchFile.getFileHeight());
            dest.put("iCatchFile.frameRate", src.iCatchFile.getFrameRate());
            dest.put("iCatchFile.fileProtection", src.iCatchFile.getFileProtection());
            dest.put("iCatchFile.fileDuration", src.iCatchFile.getFileDuration());

            return dest.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static RemoteMediaItemInfo deserialize(String src) {
        try {
            JSONObject jsonObject = new JSONObject(src);

            int section = (int) jsonObject.get("section");
            boolean isItemDownloaded = (boolean) jsonObject.get("isItemDownloaded");
            boolean isPanorama = (boolean) jsonObject.get("isPanorama");
            String fileSize = (String) jsonObject.get("fileSize");
            String fileTime = (String) jsonObject.get("fileTime");
            String fileDate = (String) jsonObject.get("fileDate");
            String fileDuration = (String) jsonObject.get("fileDuration");
            String fileType = (String) jsonObject.get("fileType");

            int iCatchFileHandle = (int) jsonObject.get("iCatchFile.fileHandle");
            String iCatchFilePath = (String) jsonObject.get("iCatchFile.filePath");
            String iCatchFileName = (String) jsonObject.get("iCatchFile.fileName");
            String iCatchFileDate = (String) jsonObject.get("iCatchFile.fileDate");
            int iCatchFileType = (int) jsonObject.get("iCatchFile.fileType");
            long iCatchFileSize = Long.parseLong(jsonObject.get("iCatchFile.fileSize").toString());
            int iCatchFileWidth = (int) jsonObject.get("iCatchFile.fileWidth");
            int iCatchFileHeight = (int) jsonObject.get("iCatchFile.fileHeight");
            double iCatchFileFrameRate = Double.parseDouble(jsonObject.get("iCatchFile.frameRate").toString());
            int iCatchFileProtection = (int) jsonObject.get("iCatchFile.fileProtection");
            int iCatchFileDuration = (int) jsonObject.get("iCatchFile.fileDuration");
            ICatchFile iCatchFile = new ICatchFile(iCatchFileHandle, iCatchFileType, iCatchFilePath, iCatchFileName, iCatchFileSize, iCatchFileDate, iCatchFileFrameRate, iCatchFileWidth, iCatchFileHeight, iCatchFileProtection, iCatchFileDuration);

            return new RemoteMediaItemInfo(iCatchFile, section, isPanorama, fileSize, fileTime, fileDate, fileDuration, fileType);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
