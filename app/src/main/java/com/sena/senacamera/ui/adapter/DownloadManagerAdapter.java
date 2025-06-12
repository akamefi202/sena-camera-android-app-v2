package com.sena.senacamera.ui.adapter;

import android.content.Context;
import android.os.Handler;

import com.icatchtek.reliant.customer.type.ICatchFile;
import com.sena.senacamera.data.entity.DownloadInfo;

import java.util.HashMap;
import java.util.LinkedList;

public class DownloadManagerAdapter {
    public DownloadManagerAdapter(Context context, HashMap<Integer, DownloadInfo> downloadInfoMap, LinkedList<ICatchFile> downloadChooseList, Handler downloadManagerHandler) {
    }

    public void notifyDataSetChanged() {
    }

    public void setOnCancelBtnClickListener(DownloadManagerAdapter.OnCancelBtnClickListener onCancelBtnClickListener) {
    }

    public static class OnCancelBtnClickListener {
    }
}
