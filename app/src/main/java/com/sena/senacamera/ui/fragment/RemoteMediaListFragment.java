package com.sena.senacamera.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.sena.senacamera.Presenter.LocalMediaPresenter;
import com.sena.senacamera.Presenter.RemoteMediaPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.adapter.MediaGroupArrayAdapter;
import com.sena.senacamera.adapter.MultiPbRecyclerViewAdapter;
import com.sena.senacamera.adapter.RemoteMediaItemArrayAdapter;
import com.sena.senacamera.data.Mode.OperationMode;
import com.sena.senacamera.data.entity.LocalPbItemInfo;
import com.sena.senacamera.data.entity.MediaItemGroup;
import com.sena.senacamera.data.entity.MultiPbItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.StorageType;
import com.sena.senacamera.ui.Interface.RemoteMediaView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RemoteMediaListFragment extends Fragment implements RemoteMediaView {
    private String TAG = "RemoteMediaListFragment";
    private RemoteMediaPresenter photoPresenter, videoPresenter;
    boolean isSelectMode = false;
    boolean isSelectedAll = false;

    public Context context;
    GridView gvMedia;
    MediaGroupArrayAdapter mediaGroupArrayAdapter;

    public void play(int position) {
        //this.photoPresenter.itemClick(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_media_list, container, false);

        // initialize presenter
        photoPresenter = new RemoteMediaPresenter(this.getActivity(), FileType.FILE_PHOTO);
        videoPresenter = new RemoteMediaPresenter(this.getActivity(), FileType.FILE_VIDEO);

        // initialize MediaGroupArrayAdapter to show grid item list
        List<MultiPbItemInfo> mediaItemList = Stream.concat(photoPresenter.getRemotePhotoInfoList().stream(), videoPresenter.getRemotePhotoInfoList().stream()).collect(Collectors.toList());
        List<MediaItemGroup> mediaGroupList = new ArrayList<>();
        Log.e(TAG, "mediaItemList size: " + mediaItemList.size());

        // sort media item list by date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Collections.sort(mediaItemList, new Comparator<MultiPbItemInfo>() {
            public int compare(MultiPbItemInfo e1, MultiPbItemInfo e2) {
                try {
                    return dateFormat.parse(e2.getFileDate()).compareTo(dateFormat.parse(e1.getFileDate()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // set MediaGroupArrayAdapter
        List<String> filteredDates = new ArrayList<>();
        for (int i = 0; i < mediaItemList.size(); i ++) {
            MultiPbItemInfo item = mediaItemList.get(i);
            String currentDate = item.getFileDate();
            if (filteredDates.contains(currentDate)) {
                continue;
            }
            filteredDates.add(currentDate);

            MediaItemGroup itemGroup = new MediaItemGroup();
            itemGroup.fileDate = currentDate;
            itemGroup.remoteFileList = mediaItemList.stream().filter(c -> c.getFileDate().equals(currentDate)).collect(Collectors.toList());
            mediaGroupList.add(itemGroup);
        }

        Log.e(TAG, "mediaGroupList size: " + mediaGroupList.size());

        this.gvMedia = (GridView) view.findViewById(R.id.gv_media);
        this.mediaGroupArrayAdapter = new MediaGroupArrayAdapter(this.getActivity(), R.layout.item_media_group, new ArrayList<>(mediaGroupList), StorageType.REMOTE);
        this.gvMedia.setAdapter(mediaGroupArrayAdapter);

        return view;
    }

    @Override
    public void setRecyclerViewVisibility(int visibility) {

    }

    @Override
    public void setRecyclerViewAdapter(MultiPbRecyclerViewAdapter recyclerViewAdapter) {

    }

    @Override
    public void setRecyclerViewLayoutManager(RecyclerView.LayoutManager layout) {

    }

    @Override
    public void notifyChangeMultiPbMode(OperationMode operationMode) {

    }

    @Override
    public void setPhotoSelectNumText(int selectNum) {

    }

    @Override
    public void setNoContentTxvVisibility(int visibility) {

    }

    public void updateUI() {
        mediaGroupArrayAdapter.updateUI();
    }
}
