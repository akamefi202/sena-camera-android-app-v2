package com.sena.senacamera.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.sena.senacamera.R;
import com.sena.senacamera.data.entity.LocalPbItemInfo;
import com.sena.senacamera.data.entity.MediaItemGroup;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.data.type.StorageType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MediaGroupArrayAdapter extends ArrayAdapter<MediaItemGroup> {
    private String TAG = "MediaGroupArrayAdapter";
    private Context context;
    private LayoutInflater inflater = null;
    public ArrayList<MediaItemGroup> arrayList = null;
    public String storageType = StorageType.LOCAL;
    private LocalMediaItemArrayAdapter localMediaItemArrayAdapter;
    private RemoteMediaItemArrayAdapter remoteMediaItemArrayAdapter;
    private ViewHolder viewHolder = null;
    static class ViewHolder {
        TextView mediaDateText = null;
        TextView mediaCountText = null;
        GridView mediaGridView = null;

        ViewHolder() {
        }
    }

    public MediaGroupArrayAdapter(@NonNull Context context, int resource, ArrayList<MediaItemGroup> arrayListParam, String storageTypeParam) {
        super(context, resource, arrayListParam);

        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.arrayList = arrayListParam;
        this.storageType = storageTypeParam;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public MediaItemGroup getItem(int i) {
        return (MediaItemGroup) super.getItem(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            this.viewHolder = new ViewHolder();
            view = this.inflater.inflate(R.layout.item_media_group, (ViewGroup) null);
            this.viewHolder.mediaDateText = (TextView) view.findViewById(R.id.file_date_text);
            this.viewHolder.mediaCountText = (TextView) view.findViewById(R.id.file_count_text);
            this.viewHolder.mediaGridView = (GridView) view.findViewById(R.id.file_grid_view);
            view.setTag(this.viewHolder);
        } else {
            this.viewHolder = (ViewHolder) view.getTag();
        }

        MediaItemGroup item = getItem(i);
        if (item == null) {
            return view;
        }

        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (todayDate.equals(item.fileDate)) {
            this.viewHolder.mediaDateText.setText(R.string.today);
        } else {
            this.viewHolder.mediaDateText.setText(item.fileDate);
        }
        if (Objects.equals(this.storageType, StorageType.LOCAL)) {
            this.viewHolder.mediaCountText.setText(Integer.toString(item.localFileList.size()));
            this.localMediaItemArrayAdapter = new LocalMediaItemArrayAdapter(this.context, R.id.file_grid_view, new ArrayList<>(item.localFileList));
            this.viewHolder.mediaGridView.setAdapter(this.localMediaItemArrayAdapter);

            // set the height of the gridview manually
            setGridViewHeight(this.viewHolder.mediaGridView);
        } else {
            this.viewHolder.mediaCountText.setText(Integer.toString(item.remoteFileList.size()));
            this.remoteMediaItemArrayAdapter = new RemoteMediaItemArrayAdapter(this.context, R.id.file_grid_view, new ArrayList<>(item.remoteFileList));
            this.viewHolder.mediaGridView.setAdapter(this.remoteMediaItemArrayAdapter);

            // set the height of the gridview manually
            setGridViewHeight(this.viewHolder.mediaGridView);
        }

        return view;
    }

    public void setGridViewHeight(GridView gridView) {
        ListAdapter adapter = gridView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        int items = adapter.getCount();
        int rows = (int) Math.ceil((double) items / 3);

        View listItem = adapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        int itemHeight = listItem.getMeasuredHeight();

        totalHeight = itemHeight * rows;
        int verticalSpacing = gridView.getVerticalSpacing();

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (verticalSpacing * (rows - 1));
        gridView.setLayoutParams(params);

    }

    public void updateUI() {
        if (this.storageType.equals(StorageType.LOCAL)) {
            localMediaItemArrayAdapter.updateUI();
        } else {
            remoteMediaItemArrayAdapter.updateUI();
        }
    }
}
