package com.sena.senacamera.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.Presenter.LocalMediaPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.adapter.LocalMediaItemArrayAdapter;
import com.sena.senacamera.adapter.LocalMultiPbWallGridAdapter;
import com.sena.senacamera.adapter.LocalMultiPbWallListAdapter;
import com.sena.senacamera.adapter.MediaGroupArrayAdapter;
import com.sena.senacamera.data.Mode.OperationMode;
import com.sena.senacamera.data.entity.LocalPbItemInfo;
import com.sena.senacamera.data.entity.MediaItemGroup;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.StorageType;
import com.sena.senacamera.ui.Interface.LocalMediaView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalMediaListFragment extends Fragment implements LocalMediaView {
    private String TAG = "LocalMediaListFragment";
    private LocalMediaPresenter photoPresenter, videoPresenter;
    boolean isSelectMode = false;
    boolean isSelectedAll = false;
    GridView gvMedia;
    MediaGroupArrayAdapter mediaGroupArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_media_list, container, false);

        // initialize presenter
        photoPresenter = new LocalMediaPresenter(this.getActivity(), FileType.FILE_PHOTO);
        videoPresenter = new LocalMediaPresenter(this.getActivity(), FileType.FILE_VIDEO);

        // initialize MediaGroupArrayAdapter to show grid item list
        List<LocalPbItemInfo> mediaItemList = Stream.concat(photoPresenter.getPhotoInfoList().stream(), videoPresenter.getPhotoInfoList().stream()).collect(Collectors.toList());
        List<MediaItemGroup> mediaGroupList = new ArrayList<>();

        // sort media item list by date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Collections.sort(mediaItemList, new Comparator<LocalPbItemInfo>() {
           public int compare(LocalPbItemInfo e1, LocalPbItemInfo e2) {
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
            LocalPbItemInfo item = mediaItemList.get(i);
            String currentDate = item.getFileDate();
            if (filteredDates.contains(currentDate)) {
                continue;
            }
            filteredDates.add(currentDate);

            MediaItemGroup itemGroup = new MediaItemGroup();
            itemGroup.fileDate = currentDate;
            itemGroup.localFileList = mediaItemList.stream().filter(c -> c.getFileDate().equals(currentDate)).collect(Collectors.toList());
            mediaGroupList.add(itemGroup);
        }

        this.gvMedia = (GridView) view.findViewById(R.id.gv_media);
        this.mediaGroupArrayAdapter = new MediaGroupArrayAdapter(this.getActivity(), R.layout.item_media_group, new ArrayList<>(mediaGroupList), StorageType.LOCAL);
        this.gvMedia.setAdapter(mediaGroupArrayAdapter);

        return view;
    }

    @Override
    public void setListViewVisibility(int visibility) {

    }

    @Override
    public void setGridViewVisibility(int visibility) {

    }

    @Override
    public void setListViewAdapter(LocalMultiPbWallListAdapter photoWallListAdapter) {

    }

    @Override
    public void setGridViewAdapter(LocalMultiPbWallGridAdapter PhotoWallGridAdapter) {

    }

    @Override
    public void setListViewSelection(int position) {

    }

    @Override
    public void setGridViewSelection(int position) {

    }

    @Override
    public void setListViewHeaderText(String headerText) {

    }

    @Override
    public View listViewFindViewWithTag(int tag) {
        return null;
    }

    @Override
    public View gridViewFindViewWithTag(int tag) {
        return null;
    }

    @Override
    public void updateGridViewBitmaps(String tag, Bitmap bitmap) {

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

    @Override
    public void onResume() {
        super.onResume();
    }
}
