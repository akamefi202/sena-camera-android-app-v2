package com.sena.senacamera.ui.appdialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.data.type.MediaStorageType;
import com.sena.senacamera.ui.adapter.DownloadManagerAdapter;

public class CustomDownloadDialog {

    private Context context;
    private BottomSheetDialog dialog;
    private TextView downloadStatusText;
    private ImageButton closeButton;
    private LinearLayout saveButton, shareButton, deleteButton;
    private ProgressBar downloadProgressBar;

    public CustomDownloadDialog(Context context) {
        this.context = context;

        dialog = new BottomSheetDialog(context);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_downloading, null);
        dialog.setContentView(dialogLayout);
        dialog.setCanceledOnTouchOutside(false);

        closeButton = dialogLayout.findViewById(R.id.close_button);
        saveButton = dialogLayout.findViewById(R.id.save_button);
        deleteButton = dialogLayout.findViewById(R.id.delete_button);
        downloadProgressBar = dialogLayout.findViewById(R.id.download_progress_bar);
        downloadStatusText = dialogLayout.findViewById(R.id.download_status_text);
    }

    public void dismissDownloadDialog() {
        dialog.dismiss();
    }

    public void setBackBtnOnClickListener(View.OnClickListener onClickListener) {
        closeButton.setOnClickListener(onClickListener);
    }

    public void showDownloadDialog() {
        dialog.show();
    }

    public void setMessage(String message) {
        downloadStatusText.setText(message);
    }

    public void setProgress(int progress) {
        downloadProgressBar.setProgress(progress);
    }
}
