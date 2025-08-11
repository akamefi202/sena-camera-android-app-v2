package com.sena.senacamera.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.R;
import com.sena.senacamera.data.Mode.MediaViewMode;
import com.sena.senacamera.data.entity.GroupMediaItemInfo;
import com.sena.senacamera.data.entity.LocalMediaItemInfo;
import com.sena.senacamera.data.entity.MediaItemInfo;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.ui.BuildConfig;
import com.sena.senacamera.ui.activity.MediaActivity;
import com.sena.senacamera.ui.activity.MediaPhotoDetailActivity;
import com.sena.senacamera.ui.activity.MediaVideoDetailActivity;
import com.sena.senacamera.utils.imageloader.ImageLoaderUtil;
import com.sena.senacamera.utils.imageloader.TutkUriUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MediaRecyclerViewAdapter extends RecyclerView.Adapter<MediaRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = MediaRecyclerViewAdapter.class.getSimpleName();
    List<Object> mediaItemList;
    private Context context;

    private final View.OnClickListener buttonClickListener = view -> {
        AppLog.i(TAG, "buttonClickListener");
        int itemIndex = Integer.parseInt(view.getTag().toString());
        if (itemIndex > -1 && itemIndex < mediaItemList.size()) {
            int id = view.getId();
            if (id == R.id.media_layout) {
                if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION)) {
                    // select the media
                    this.checkMedia(itemIndex);
                } else {
                    // normal mode
                    // play the media
                    this.playMedia(itemIndex);
                }
            } else if (id == R.id.group_checked_status) {
                // select all medias of the group
                this.checkGroup(itemIndex);
            }
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // group media item
        LinearLayout groupLayout = null;
        TextView groupDateText = null;
        TextView groupCountText = null;
        ImageView groupCheck = null;

        // local & remote media item
        FrameLayout mediaLayout = null;
        ImageView ivMediaThumbnail = null;
        ImageView ivMediaCheck = null;
        ImageView ivMediaDownload = null;
        ImageView ivMediaInfoType = null;
        TextView tvMediaInfoRunningTime = null;

        ViewHolder(View itemView) {
            super(itemView);

            groupLayout = itemView.findViewById(R.id.group_layout);
            groupDateText = itemView.findViewById(R.id.group_date_text);
            groupCountText = itemView.findViewById(R.id.group_count_text);
            groupCheck = itemView.findViewById(R.id.group_checked_status);

            mediaLayout = itemView.findViewById(R.id.media_layout);
            ivMediaThumbnail = itemView.findViewById(R.id.thumbnail);
            ivMediaInfoType = itemView.findViewById(R.id.media_type);
            tvMediaInfoRunningTime = itemView.findViewById(R.id.duration);
            ivMediaCheck = itemView.findViewById(R.id.status_selected);
            ivMediaDownload = itemView.findViewById(R.id.status_downloaded);
        }
    }

    public MediaRecyclerViewAdapter(Context context, List<Object> mediaItemList) {
        this.context = context;
        this.mediaItemList = mediaItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String curMediaItemType = this.getCurMediaItemType(position);
        if (curMediaItemType.equals(MediaItemType.GROUP)) {
            // media item type is group
            GroupMediaItemInfo curMediaItem = ((GroupMediaItemInfo) mediaItemList.get(position));
            if (curMediaItem == null) {
                return;
            }

            holder.groupLayout.setVisibility(View.VISIBLE);
            holder.mediaLayout.setVisibility(View.GONE);

            // show the group date text
            String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (todayDate.equals(curMediaItem.fileDate)) {
                holder.groupDateText.setText(R.string.today);
            } else {
                holder.groupDateText.setText(curMediaItem.fileDate);
            }

            // show the group count text
            holder.groupCountText.setText(Integer.toString(curMediaItem.fileCount));

            // show the group selection status
            if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION)) {
                if (curMediaItem.isItemChecked) {
                    holder.groupCheck.setImageResource(R.drawable.status_selected);
                } else {
                    holder.groupCheck.setImageResource(R.drawable.status_unselected);
                }
                holder.groupCheck.setVisibility(View.VISIBLE);
            } else {
                holder.groupCheck.setVisibility(View.GONE);
            }

            holder.groupCheck.setTag(position);
            holder.groupCheck.setOnClickListener(this.buttonClickListener);
            holder.groupCheck.setFocusable(false);
        } else if (curMediaItemType.equals(MediaItemType.REMOTE)) {
            // media item type is remote
            RemoteMediaItemInfo curMediaItem = ((RemoteMediaItemInfo) mediaItemList.get(position));
            if (curMediaItem == null) {
                return;
            }

            holder.groupLayout.setVisibility(View.GONE);
            holder.mediaLayout.setVisibility(View.VISIBLE);

            ICatchFile file = curMediaItem.iCatchFile;
            if (file == null) {
                // draw media icon in media grid item (default icon)
                holder.ivMediaThumbnail.setImageResource(R.drawable.media_thumbnail);
            } else {
                // akamefi202: update the thumbnail, manually set height
                int mediaItemWidth = (context.getResources().getDisplayMetrics().widthPixels - 12) / 3;
                String uri = TutkUriUtil.getTutkThumbnailUri(file);
                ImageLoaderUtil.loadImageView(uri, holder.ivMediaThumbnail, R.drawable.media_thumbnail);
                holder.ivMediaThumbnail.getLayoutParams().height = mediaItemWidth;
                holder.ivMediaThumbnail.setBackgroundColor(ResourcesCompat.getColor(this.context.getResources(), R.color.background_primary_color, null));
            }

            // update the media grid item by file type (photo or video)
            if (curMediaItem.fileType.equals(MediaType.PHOTO)) {
                holder.ivMediaInfoType.setImageResource(R.drawable.media_type_photo);
            } else {
                // if file type is "video"
                holder.ivMediaInfoType.setImageResource(R.drawable.media_type_video);
            }

            // set the video duration text view
            if (curMediaItem.fileType.equals(MediaType.VIDEO)) {
                holder.tvMediaInfoRunningTime.setText(curMediaItem.fileDuration);
                holder.tvMediaInfoRunningTime.setVisibility(View.VISIBLE);
            } else {
                holder.tvMediaInfoRunningTime.setVisibility(View.INVISIBLE);
            }

            // update the grid item by mode (normal/selection)
            if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION)) {
                if (curMediaItem.isItemChecked) {
                    holder.ivMediaCheck.setImageResource(R.drawable.status_selected);
                } else {
                    holder.ivMediaCheck.setImageResource(R.drawable.status_unselected_transparent);
                }
                holder.ivMediaCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivMediaCheck.setVisibility(View.INVISIBLE);
            }

            // update the downloaded status
            if (curMediaItem.isItemDownloaded) {
                holder.ivMediaDownload.setVisibility(View.VISIBLE);
            } else {
                holder.ivMediaDownload.setVisibility(View.INVISIBLE);
            }

            holder.mediaLayout.setTag(position);
            holder.mediaLayout.setOnClickListener(this.buttonClickListener);
            holder.mediaLayout.setFocusable(false);
        } else {
            // media item type is local
            LocalMediaItemInfo curMediaItem = ((LocalMediaItemInfo) mediaItemList.get(position));
            if (curMediaItem == null) {
                return;
            }

            holder.groupLayout.setVisibility(View.GONE);
            holder.mediaLayout.setVisibility(View.VISIBLE);
            
            File file = curMediaItem.file;
            if (file == null) {
                // draw media icon in media grid item (default icon)
                holder.ivMediaThumbnail.setImageResource(R.drawable.media_thumbnail);
            } else {
                // akamefi202: update the thumbnail, manually set height
                int mediaItemWidth = (context.getResources().getDisplayMetrics().widthPixels - 12) / 3;
                Glide.with(context).load(file).placeholder(R.drawable.media_thumbnail).into(holder.ivMediaThumbnail);
                holder.ivMediaThumbnail.getLayoutParams().height = mediaItemWidth;
                holder.ivMediaThumbnail.setBackgroundColor(ResourcesCompat.getColor(this.context.getResources(), R.color.background_primary_color, null));
            }

            // update the media grid item by file type (photo or video)
            if (curMediaItem.fileType.equals(MediaType.PHOTO)) {
                holder.ivMediaInfoType.setImageResource(R.drawable.media_type_photo);
            } else {
                // if file type is "video"
                holder.ivMediaInfoType.setImageResource(R.drawable.media_type_video);
            }

            // set the video duration text view
            if (curMediaItem.fileType.equals(MediaType.VIDEO)) {
                holder.tvMediaInfoRunningTime.setText(curMediaItem.fileDuration);
                holder.tvMediaInfoRunningTime.setVisibility(View.VISIBLE);
            } else {
                holder.tvMediaInfoRunningTime.setVisibility(View.INVISIBLE);
            }

            // update the grid item by mode (normal/selection)
            if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION)) {
                if (curMediaItem.isItemChecked) {
                    holder.ivMediaCheck.setImageResource(R.drawable.status_selected);
                } else {
                    holder.ivMediaCheck.setImageResource(R.drawable.status_unselected_transparent);
                }
                holder.ivMediaCheck.setVisibility(View.VISIBLE);
            } else {
                holder.ivMediaCheck.setVisibility(View.INVISIBLE);
            }

            // update the downloaded status
            if (curMediaItem.isItemDownloaded) {
                holder.ivMediaDownload.setVisibility(View.VISIBLE);
            } else {
                holder.ivMediaDownload.setVisibility(View.INVISIBLE);
            }

            holder.mediaLayout.setTag(position);
            holder.mediaLayout.setOnClickListener(this.buttonClickListener);
            holder.mediaLayout.setFocusable(false);
        }
    }

    @Override
    public int getItemCount() {
        return mediaItemList.size();
    }

    public String getCurMediaItemType(int index) {
        return ((MediaItemInfo) mediaItemList.get(index)).mediaItemType;
    }

    public void checkMedia(int index) {
        String curMediaItemType = this.getCurMediaItemType(index);

        if (curMediaItemType.equals(MediaItemType.LOCAL)) {
            LocalMediaItemInfo item = (LocalMediaItemInfo) this.mediaItemList.get(index);
            if (item == null) {
                return;
            }

            item.isItemChecked = !item.isItemChecked;
        } else if (curMediaItemType.equals(MediaItemType.REMOTE)) {
            RemoteMediaItemInfo item = (RemoteMediaItemInfo) this.mediaItemList.get(index);
            if (item == null) {
                return;
            }

            item.isItemChecked = !item.isItemChecked;
        }

        ((MediaActivity) this.context).updateScreenTitle();
        this.notifyDataSetChanged();
    }

    public void playMedia(int index) {
        Intent intent;
        String curMediaItemType = this.getCurMediaItemType(index);

        if (curMediaItemType.equals(MediaItemType.LOCAL)) {
            // play the local media
            LocalMediaItemInfo item = (LocalMediaItemInfo) this.mediaItemList.get(index);
            if (item == null) {
                return;
            }

            String fileName = item.getFileName().toLowerCase();
            if (fileName.endsWith(".mov") || fileName.endsWith(".avi") || fileName.endsWith(".mp4")) {
                intent = new Intent();
                intent.putExtra("localMedia", LocalMediaItemInfo.serialize(item));
                intent.putExtra("fileType", FileType.FILE_PHOTO.ordinal());
                intent.setClass(context, MediaVideoDetailActivity.class);
            } else if (fileName.endsWith(".jpg")) {
                intent = new Intent();
                intent.putExtra("localMedia", LocalMediaItemInfo.serialize(item));
                intent.putExtra("fileType", FileType.FILE_VIDEO.ordinal());
                intent.setClass(context, MediaPhotoDetailActivity.class);
            } else {
                // unknown file type
                return;
            }

            this.context.startActivity(intent);
        } else if (curMediaItemType.equals(MediaItemType.REMOTE)) {
            // play the remote media
            RemoteMediaItemInfo item = (RemoteMediaItemInfo) this.mediaItemList.get(index);
            if (item == null) {
                return;
            }

            if (item.fileType.equals(MediaType.PHOTO)) {
                intent = new Intent();
                intent.putExtra("remoteMedia", RemoteMediaItemInfo.serialize(item));
                intent.putExtra("fileType", FileType.FILE_PHOTO.ordinal());
                intent.setClass(context, MediaPhotoDetailActivity.class);
            } else {
                intent = new Intent();
                intent.putExtra("remoteMedia", RemoteMediaItemInfo.serialize(item));
                intent.putExtra("fileType", FileType.FILE_VIDEO.ordinal());
                intent.setClass(context, MediaVideoDetailActivity.class);
            }

            this.context.startActivity(intent);
        }
    }

    public void checkGroup(int index) {
        GroupMediaItemInfo groupItem = (GroupMediaItemInfo) this.mediaItemList.get(index);
        if (groupItem == null) {
            return;
        }

        groupItem.isItemChecked = !groupItem.isItemChecked;

        for (int i = index + 1; i <= index + groupItem.fileCount; i ++) {
            String curMediaItemType = this.getCurMediaItemType(i);

            if (curMediaItemType.equals(MediaItemType.LOCAL)) {
                LocalMediaItemInfo item = (LocalMediaItemInfo) this.mediaItemList.get(i);
                item.isItemChecked = groupItem.isItemChecked;
            } else if (curMediaItemType.equals(MediaItemType.REMOTE)) {
                RemoteMediaItemInfo item = (RemoteMediaItemInfo) this.mediaItemList.get(i);
                item.isItemChecked = groupItem.isItemChecked;
            }
        }

        ((MediaActivity) this.context).updateScreenTitle();
        this.notifyDataSetChanged();
    }

    public void selectAll() {
        for (Object item: this.mediaItemList) {
            ((MediaItemInfo) item).isItemChecked = true;
        }

        ((MediaActivity) this.context).updateScreenTitle();
        this.notifyDataSetChanged();
    }

    public void deselectAll() {
        for (Object item: this.mediaItemList) {
            ((MediaItemInfo) item).isItemChecked = false;
        }

        ((MediaActivity) this.context).updateScreenTitle();
        this.notifyDataSetChanged();
    }

    public void updateUI() {
        this.notifyDataSetChanged();
    }

    public int getTotalCount() {
        return (int) this.mediaItemList.stream().filter(e -> {
            return !((MediaItemInfo) e).mediaItemType.equals(MediaItemType.GROUP);
        }).count();
    }

    public int getSelectedCount() {
        return (int) this.mediaItemList.stream().filter(e -> {
            return !((MediaItemInfo) e).mediaItemType.equals(MediaItemType.GROUP) && ((MediaItemInfo) e).isItemChecked;
        }).count();
    }

    public List<Object> getSelectedItemList() {
        return this.mediaItemList.stream().filter(e -> {
            return !((MediaItemInfo) e).mediaItemType.equals(MediaItemType.GROUP) && ((MediaItemInfo) e).isItemChecked;
        }).collect(Collectors.toList());
    }
}
