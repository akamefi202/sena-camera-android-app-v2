package com.sena.senacamera.data.entity;

import com.icatchtek.reliant.customer.type.ICatchFile;
import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.utils.ConvertTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LocalMediaItemInfo extends MediaItemInfo implements Serializable {
    private static final String TAG = LocalMediaItemInfo.class.getSimpleName();

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
        this.file = file;
        this.isItemChecked = false;
        this.mediaItemType = MediaItemType.LOCAL;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getFilePath() {
        return file.getPath();
    }

    public String getFileDate() {
        long time = file.lastModified();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }

    public String getFileSize() {
        int size = (int)file.length();
        return  ConvertTools.ByteConversionGBMBKB(size);
    }

    public String getFileName() {
        return file.getName();
    }
    public String getFileDateMMSS() {
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
    }

    public static String serialize(LocalMediaItemInfo src) {
        try {
            JSONObject dest = new JSONObject();

            dest.put("filePath", src.file.getPath());
            dest.put("section", src.section);
            dest.put("isItemDownloaded", src.isItemDownloaded);
            dest.put("isPanorama", src.isPanorama);
            dest.put("fileType", src.fileType);
            dest.put("fileDuration", src.fileDuration);

            return dest.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalMediaItemInfo deserialize(String src) {
        try {
            JSONObject jsonObject = new JSONObject(src);

            String filePath = (String) jsonObject.get("filePath");
            File file = new File(filePath);
            int section = (int) jsonObject.get("section");
            boolean isItemDownloaded = (boolean) jsonObject.get("isItemDownloaded");
            boolean isPanorama = (boolean) jsonObject.get("isPanorama");
            String fileType = (String) jsonObject.get("fileType");
            String fileDuration = (String) jsonObject.get("fileDuration");

            return new LocalMediaItemInfo(file, section, isPanorama, fileType, fileDuration);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
