package com.sena.senacamera.data.entity;

import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.utils.ConvertTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LocalMediaItemInfo extends MediaItemInfo implements Serializable {
    public File file;
    public int section;
    public boolean isItemDownloaded = false;
    private boolean isPanorama = false;
    // "photo" or "video"
    public String fileType = MediaType.PHOTO;
    public String fileDuration;

    public LocalMediaItemInfo(File file, int section) {
        this.file = file;
        this.section = section;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.LOCAL;
    }

    public LocalMediaItemInfo(File file, int section, boolean isPanorama, String fileTypeParam, String fileDurationParam) {
        this.file = file;
        this.section = section;
        this.isItemChecked = false;
        this.isPanorama = isPanorama;
        this.fileType = fileTypeParam;
        this.fileDuration = fileDurationParam;
        this.mediaItemType = MediaItemType.LOCAL;
    }

    public LocalMediaItemInfo(File file) {
        super();
        this.file = file;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.LOCAL;
    }

    public void setSection(int section){
        this.section = section;
    }

    public String getFilePath(){
        return file.getPath();
    }

    public String getFileDate(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public String getFileSize(){
        int size = (int)file.length();
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public String getFileName(){
        return file.getName();
    }
    public String getFileDateMMSS(){
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(time));
    }

    public boolean isPanorama() {
        return isPanorama;
    }

    public void setPanorama(boolean panorama) {
        isPanorama = panorama;
    }

    //读取文件创建时间
    public void getCreateTime() {
        String filePath = file.getPath();
        String strTime = null;
        try {
            Process p = Runtime.getRuntime().exec("cmd /C dir " + filePath + "/tc");
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(".txt")) {
                    strTime = line.substring(0, 17);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("创建时间    " + strTime);
    }

}
