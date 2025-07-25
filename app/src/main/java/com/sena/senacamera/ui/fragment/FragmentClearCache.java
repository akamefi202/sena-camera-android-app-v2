package com.sena.senacamera.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sena.senacamera.R;
import com.sena.senacamera.utils.ClickUtils;

import java.io.File;
import java.text.DecimalFormat;

public class FragmentClearCache extends Fragment implements View.OnClickListener {
    private static final String TAG = FragmentClearCache.class.getSimpleName();

    ConstraintLayout fragmentLayout;
    ImageButton backButton;
    Button clearCacheButton;
    TextView storageStatusText;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.fragmentLayout = (ConstraintLayout) layoutInflater.inflate(R.layout.fragment_clear_cache, viewGroup, false);

        this.backButton = (ImageButton) this.fragmentLayout.findViewById(R.id.back_button);
        this.clearCacheButton = (Button) this.fragmentLayout.findViewById(R.id.clear_cache_button);
        this.storageStatusText = (TextView) this.fragmentLayout.findViewById(R.id.storage_text);

        this.backButton.setOnClickListener(v -> onBack());
        this.clearCacheButton.setOnClickListener(v -> clearCache());

        return this.fragmentLayout;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateFragment();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        this.fragmentLayout = null;
        this.backButton = null;
        this.clearCacheButton = null;
        this.storageStatusText = null;
    }

    public void updateFragment() {
        // get cache size
        this.storageStatusText.setText(formatSize(getCacheSize(requireContext())));
    }

    private void onBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void clearCache() {
        // prevent button double click
        if (ClickUtils.isFastClick()) {
            return;
        }

        BottomSheetDialog clearCacheDialog = new BottomSheetDialog(requireContext());
        View clearCacheDialogLayout = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_clear_cache, null);
        clearCacheDialog.setContentView(clearCacheDialogLayout);
        clearCacheDialog.show();

        Button confirmButton = clearCacheDialogLayout.findViewById(R.id.confirm_button);
        ImageButton closeButton = clearCacheDialogLayout.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCacheDialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear cache
                clearAppCache(requireContext());
                updateFragment();

                clearCacheDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void clearAppCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] list = dir.list();
            for (String item : list) {
                boolean success = deleteDir(new File(dir, item));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public String formatSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public long getCacheSize(Context context) {
        File cacheDir = context.getCacheDir();
        return getFolderSize(cacheDir);
    }

    private long getFolderSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    size += getFolderSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }
}
