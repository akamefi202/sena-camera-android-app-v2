package com.sena.senacamera.data.entity;

import com.icatchtek.reliant.customer.type.ICatchFile;

import java.util.List;

/**
 * @author b.jiang
 * @date 2020/2/25
 * @description
 */
public class MultiPbFileResult {
    private List<RemoteMediaItemInfo> fileList;
    private int lastIndex;
    private ICatchFile lastFile;
    private boolean isMore;

    public List<RemoteMediaItemInfo> getFileList() {
        return fileList;
    }

    public void setFileList(List<RemoteMediaItemInfo> fileList) {
        this.fileList = fileList;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    public ICatchFile getLastFile() {
        return lastFile;
    }

    public void setLastFile(ICatchFile lastFile) {
        this.lastFile = lastFile;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}
