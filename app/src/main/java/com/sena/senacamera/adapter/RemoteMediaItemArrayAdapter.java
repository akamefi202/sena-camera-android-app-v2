package com.sena.senacamera.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.sena.senacamera.R;
import com.sena.senacamera.data.Mode.MediaViewMode;
import com.sena.senacamera.data.entity.MultiPbItemInfo;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.ui.activity.MediaActivity;
import com.sena.senacamera.utils.imageloader.ImageLoaderUtil;
import com.sena.senacamera.utils.imageloader.TutkUriUtil;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteMediaItemArrayAdapter extends ArrayAdapter<MultiPbItemInfo> {
    private String TAG = "RemoteMediaItemArrayAdapter";
    /* access modifiers changed from: private */
    public ArrayList<MultiPbItemInfo> arrayList = null;
    private Context context;

    private final View.OnClickListener buttonClickListener = view -> {
        int itemIndex = Integer.parseInt(view.getTag().toString());
        if (itemIndex > -1 && itemIndex < RemoteMediaItemArrayAdapter.this.arrayList.size()) {
            int id = view.getId();
            if (id == R.id.thumbnail) {
                if (((MediaActivity) this.context).getMediaViewMode().equals(MediaViewMode.SELECTION)) {
                    // select the media
                    RemoteMediaItemArrayAdapter.this.checkMedia(itemIndex);
                } else {
                    // mode is "normal", show the preview
                    RemoteMediaItemArrayAdapter.this.playMedia(itemIndex);
                }
            }
        }
    };
    private final View.OnLongClickListener buttonLongClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View view) {
            return false;
//            if (!SenaCameraArrayAdapterMedias.this.context.actionEnabled() || SenaCameraArrayAdapterMedias.this.context.navigationDrawerFragment.isDrawerOpen()) {
//                return true;
//            }
//            int parseInt = Integer.parseInt(view.getTag().toString());
//            int id = view.getId();
//            if (id == R.id.iv_media) {
//                FragmentMedia.newInstance().changeMode(parseInt);
//                return true;
//            } else if (id != R.id.iv_media_check && id != R.id.tv_media_check_background) {
//                return false;
//            } else {
//                if (FragmentMedia.newInstance().mode == 1) {
//                    FragmentMedia.newInstance().changeMode(parseInt);
//                } else {
//                    if (view.getId() != R.id.iv_media_check) {
//                        FragmentMedia.newInstance().changeMode();
//                    } else if (SenaCameraArrayAdapterMedias.this.context.data.checkAllMediasSelected()) {
//                        SenaCameraArrayAdapterMedias.this.context.data.selectAllMedia(false);
//                    } else {
//                        SenaCameraArrayAdapterMedias.this.context.data.selectAllMedia(true);
//                    }
//                    FragmentMedia.newInstance().updateFragment();
//                }
//                return true;
//            }
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

    public RemoteMediaItemArrayAdapter(Context context, int resource, ArrayList<MultiPbItemInfo> arrayListParam) {
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
    public MultiPbItemInfo getItem(int i) {
        return (MultiPbItemInfo) super.getItem(i);
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

        MultiPbItemInfo item = getItem(i);
        if (item == null) {
            return view;
        }

        ICatchFile file = item.iCatchFile;
        if (file == null) {
            // draw media icon in media grid item (default icon)
            this.viewHolder.ivMedia.setImageDrawable(ResourcesCompat.getDrawable(this.getContext().getResources(), R.drawable.media_thumbnail, (Resources.Theme) null));
        } else {
            // akamefi202: update the thumbnail, manually set height
            int mediaItemWidth = (context.getResources().getDisplayMetrics().widthPixels - 12) / 3;
            String uri = TutkUriUtil.getTutkThumbnailUri(file);
            ImageLoaderUtil.loadImageView(uri, this.viewHolder.ivMedia, R.drawable.media_thumbnail);
            this.viewHolder.ivMedia.getLayoutParams().height = mediaItemWidth;
            this.viewHolder.ivMedia.setBackgroundColor(ResourcesCompat.getColor(this.getContext().getResources(), R.color.full_transparent, (Resources.Theme) null));
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
        this.viewHolder.ivMedia.setOnLongClickListener(this.buttonLongClickListener);
        this.viewHolder.ivMedia.setFocusable(false);

        return view;
    }

//    public void setData(SenaCameraData senaCameraData) {
//        this.data = senaCameraData;
//    }

//    public SenaCameraData getData() {
//        return this.data;
//    }

    public void setArrayList(ArrayList<MultiPbItemInfo> arrayList) {
        this.arrayList = arrayList;

        this.notifyDataSetChanged();
    }

    public ArrayList<MultiPbItemInfo> getArrayList() {
        return this.arrayList;
    }

    public List<MultiPbItemInfo> getSelectedFileList() {
        return this.arrayList.stream().filter(e -> e.isItemChecked).collect(Collectors.toList());
    }

    public void checkMedia(int i) {
        MultiPbItemInfo item = getItem(i);
        item.isItemChecked = !item.isItemChecked;
        //item.selected = !item.selected;
        //FragmentMedia.newInstance().updateFragment();

        this.notifyDataSetChanged();
    }

    public void playMedia(int i) {
        MultiPbItemInfo item = getItem(i);
        if (item == null) {
            Log.e("MediaArrayAdapter - playMedia", "item is null");
            return;
        }

        // akamefi202: to be fixed
//        if (item.fileType.equals("photo")) {
//            ((MediaActivity) this.context).play(i);
//        } else {
//            // if file type is "video"
//            ((MediaActivity) this.context).play(i);
//        }
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
