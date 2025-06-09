package com.sena.senacamera.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.sena.senacamera.R;
import com.sena.senacamera.data.Mode.MediaViewMode;
import com.sena.senacamera.data.entity.LocalPbItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.ui.BuildConfig;
import com.sena.senacamera.ui.activity.MediaActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalMediaItemArrayAdapter extends ArrayAdapter<LocalPbItemInfo> {
    private String TAG = "LocalMediaItemArrayAdapter";
    /* access modifiers changed from: private */
    public ArrayList<LocalPbItemInfo> arrayList = null;
    private Context context;

    private final View.OnClickListener buttonClickListener = view -> {
        Log.e(TAG, "buttonClickListener");
        int itemIndex = Integer.parseInt(view.getTag().toString());
        if (itemIndex > -1 && itemIndex < LocalMediaItemArrayAdapter.this.arrayList.size()) {
            int id = view.getId();
            if (id == R.id.thumbnail) {
                if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION)) {
                    // select the media
                    LocalMediaItemArrayAdapter.this.checkMedia(itemIndex);
                } else {
                    // normal mode
                    // play the media
                    LocalMediaItemArrayAdapter.this.playMedia(itemIndex);
                }
            } else if (id == R.id.status_selected) {
                // select the media
                LocalMediaItemArrayAdapter.this.checkMedia(itemIndex);
            }
        }
    };

    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    static class ViewHolder {
        ImageView ivMedia = null;
        ImageView ivMediaCheck = null;
        ImageView ivMediaDownload = null;
        ImageView ivMediaInfoType = null;
        RelativeLayout llMediaInfo = null;
        TextView tvMediaInfoRunningTime = null;

        ViewHolder() {
        }
    }

    public LocalMediaItemArrayAdapter(Context context, int resource, ArrayList<LocalPbItemInfo> arrayListParam) {
        super(context, resource, arrayListParam);

        this.context = context;
        this.arrayList = arrayListParam;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public LocalPbItemInfo getItem(int i) {
        return (LocalPbItemInfo) super.getItem(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            this.viewHolder = new ViewHolder();
            view = this.inflater.inflate(R.layout.item_media, (ViewGroup) null);
            this.viewHolder.ivMedia = (ImageView) view.findViewById(R.id.thumbnail);
            this.viewHolder.llMediaInfo = (RelativeLayout) view.findViewById(R.id.media_info);
            this.viewHolder.ivMediaInfoType = (ImageView) view.findViewById(R.id.media_type);
            this.viewHolder.tvMediaInfoRunningTime = (TextView) view.findViewById(R.id.duration);
            this.viewHolder.ivMediaCheck = (ImageView) view.findViewById(R.id.status_selected);
            this.viewHolder.ivMediaDownload = (ImageView) view.findViewById(R.id.status_downloaded);
            view.setTag(this.viewHolder);
        } else {
            this.viewHolder = (ViewHolder) view.getTag();
        }
        this.viewHolder.ivMediaCheck.setVisibility(View.GONE);

        LocalPbItemInfo item = getItem(i);
        if (item == null) {
            return view;
        }

        File file = item.file;
        if (file == null) {
            // draw media icon in media grid item (default icon)
            this.viewHolder.ivMedia.setImageDrawable(ResourcesCompat.getDrawable(this.getContext().getResources(), R.drawable.media_thumbnail, (Resources.Theme) null));
        } else {
            // akamefi202: update the thumbnail, manually set height
            int mediaItemWidth = (context.getResources().getDisplayMetrics().widthPixels - 12) / 3;
            Glide.with(context).load(file).placeholder(R.drawable.media_thumbnail).into(this.viewHolder.ivMedia);
            this.viewHolder.ivMedia.getLayoutParams().height = mediaItemWidth;
            this.viewHolder.ivMedia.setBackgroundColor(ResourcesCompat.getColor(this.context.getResources(), R.color.full_transparent, (Resources.Theme) null));
        }

//        if (item.bitmap == null) {
//            this.viewHolder.ivMedia.setImageDrawable(ResourcesCompat.getDrawable(this.context.getResources(), R.drawable.media, (Resources.Theme) null));
//        } else {
//            this.viewHolder.ivMedia.setImageBitmap(item.bitmap);
//        }

        // update the media grid item by file type (photo or video)
        if (item.fileType.equals(MediaType.PHOTO)) {
            this.viewHolder.ivMediaInfoType.setImageDrawable(ResourcesCompat.getDrawable(this.getContext().getResources(), R.drawable.media_type_photo, (Resources.Theme) null));
        } else {
            // if file type is "video"
            this.viewHolder.ivMediaInfoType.setImageDrawable(ResourcesCompat.getDrawable(this.getContext().getResources(), R.drawable.media_type_video, (Resources.Theme) null));
        }

        // set the video duration text view
        if (item.fileType.equals(MediaType.VIDEO)) {
            this.viewHolder.tvMediaInfoRunningTime.setText(item.fileDuration);
            this.viewHolder.tvMediaInfoRunningTime.setVisibility(View.VISIBLE);
        } else {
            this.viewHolder.tvMediaInfoRunningTime.setVisibility(View.INVISIBLE);
        }

        // update the grid item by mode (normal/selection)
        if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION) && item.isItemChecked) {
            this.viewHolder.ivMediaCheck.setVisibility(View.VISIBLE);
        } else {
            this.viewHolder.ivMediaCheck.setVisibility(View.INVISIBLE);
        }

        // update the downloaded status
        if (item.isItemDownloaded) {
            this.viewHolder.ivMediaDownload.setVisibility(View.VISIBLE);
        } else {
            this.viewHolder.ivMediaDownload.setVisibility(View.INVISIBLE);
        }

        this.viewHolder.ivMedia.setTag(i);
        this.viewHolder.ivMedia.setOnClickListener(this.buttonClickListener);
        this.viewHolder.ivMedia.setFocusable(false);

        return view;
    }

//    public void setData(SenaCameraData senaCameraData) {
//        this.data = senaCameraData;
//    }

//    public SenaCameraData getData() {
//        return this.data;
//    }

    public void setArrayList(ArrayList<LocalPbItemInfo> arrayList) {
        this.arrayList = arrayList;

        this.notifyDataSetChanged();
    }

    public ArrayList<LocalPbItemInfo> getArrayList() {
        return this.arrayList;
    }

    public List<LocalPbItemInfo> getSelectedFileList() {
        return this.arrayList.stream().filter(e -> e.isItemChecked).collect(Collectors.toList());
    }

    public void checkMedia(int i) {
        LocalPbItemInfo item = getItem(i);
        if (item == null) {
            return;
        }

        item.isItemChecked = !item.isItemChecked;
        //item.selected = !item.selected;
        //FragmentMedia.newInstance().updateFragment();

        this.notifyDataSetChanged();
    }

    public void playMedia(int i) {
        Intent intent;
        LocalPbItemInfo item = this.arrayList.get(i);

        File file = item.file;
        String fileName = item.getFileName().toLowerCase();
        if (fileName.endsWith(".mov") || fileName.endsWith(".avi") || fileName.endsWith(".mp4")) {
            intent = new Intent(Intent.ACTION_VIEW);
//            if (Build.VERSION.SDK_INT >= 29) {
//                intent.setDataAndType(Uri.parse("file://" + item.getFilePath()), "video/3gp");
//            } else {
//                intent.setDataAndType(Uri.fromFile(file), "video/3gp");
//            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file), "video/3gp");
        } else if (fileName.endsWith(".jpg")) {
            intent = new Intent(Intent.ACTION_VIEW);
//            if (Build.VERSION.SDK_INT >= 29) {
//                intent.setDataAndType(Uri.parse("file://" + item.getFilePath()), "image/jpeg");
//            } else {
//                intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
//            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file), "image/jpeg");
        } else {
            // unknown file type
            return;
        }

        this.context.startActivity(intent);
    }

    /* access modifiers changed from: protected */
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    private void free() {
        this.viewHolder = null;
        this.inflater = null;
        this.arrayList = null;
        //this.data = null;
        //this.context = null;
    }

    public void deleteSelectedFiles() {
        // delete selected files, which means keep only unselected files
        this.arrayList.removeIf(e -> e.isItemChecked);

        this.notifyDataSetChanged();
    }

    public void selectAllFiles() {
        this.arrayList.forEach(e -> e.isItemChecked = true);

        this.notifyDataSetChanged();
    }

    public void unselectAllFiles() {
        this.arrayList.forEach(e -> e.isItemChecked = false);

        this.notifyDataSetChanged();
    }

    public void updateUI() {
        this.notifyDataSetChanged();
    }
}
