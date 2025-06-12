package com.sena.senacamera.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.Presenter.LocalMediaPresenter;
import com.sena.senacamera.Presenter.RemoteMediaPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.adapter.MediaRecyclerViewAdapter;
import com.sena.senacamera.data.Mode.MediaViewMode;
import com.sena.senacamera.data.entity.GroupMediaItemInfo;
import com.sena.senacamera.data.entity.LocalMediaItemInfo;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.data.type.MediaStorageType;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.ui.ExtendComponent.MyProgressDialog;
import com.sena.senacamera.ui.ExtendComponent.MyToast;
import com.sena.senacamera.ui.decoration.GridSpacingItemDecoration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MediaActivity extends AppCompatActivity {
    private String TAG = MediaActivity.class.getSimpleName();
    private ImageButton backButton, closeButton;
    private Button selectButton, selectAllButton, deselectAllButton;
    private TabLayout mediaTabView;
    private LinearLayout downloadButton, shareButton, deleteButton, bottomBarLayout;
    private TextView titleText, selectedNumberText;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private MediaRecyclerViewAdapter recyclerViewAdapter;
    private LocalMediaPresenter localPhotoPresenter, localVideoPresenter;
    private RemoteMediaPresenter remotePhotoPresenter, remoteVideoPresenter;

    private String mediaViewMode = MediaViewMode.NORMAL;
    private String mediaStorageType = MediaStorageType.REMOTE;
    private boolean isSelectedAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_media);

        backButton = findViewById(R.id.back_button);
        closeButton = findViewById(R.id.close_button);
        selectButton = findViewById(R.id.select_button);
        selectAllButton = findViewById(R.id.select_all_button);
        deselectAllButton = findViewById(R.id.deselect_button);
        downloadButton = findViewById(R.id.save_button);
        shareButton = findViewById(R.id.share_button);
        deleteButton = findViewById(R.id.delete_button);
        bottomBarLayout = findViewById(R.id.bottom_bar);
        titleText = findViewById(R.id.title_text);
        selectedNumberText = findViewById(R.id.selected_number_text);
        mediaTabView = findViewById(R.id.media_tab_view);
        recyclerView = findViewById(R.id.recycler_view);

        backButton.setOnClickListener((v) -> {
            finish();
        });
        selectButton.setOnClickListener((v) -> beginSelectionMode());
        closeButton.setOnClickListener((v) -> finishSelectionMode());
        selectAllButton.setOnClickListener((v) -> selectAllFiles());
        deselectAllButton.setOnClickListener((v) -> deselectAllFiles());
        downloadButton.setOnClickListener((v) -> downloadFiles());
        shareButton.setOnClickListener((v) -> shareFiles());
        deleteButton.setOnClickListener((v) -> deleteFiles());


        // initialize presenters
        localPhotoPresenter = new LocalMediaPresenter(this, FileType.FILE_PHOTO);
        localVideoPresenter = new LocalMediaPresenter(this, FileType.FILE_VIDEO);
        remotePhotoPresenter = new RemoteMediaPresenter(this, FileType.FILE_PHOTO);
        remoteVideoPresenter = new RemoteMediaPresenter(this, FileType.FILE_VIDEO);

        // initialize recycler view
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 6));
        this.updateData();

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (recyclerViewAdapter.getCurMediaItemType(position).equals(MediaItemType.GROUP)) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });

        mediaTabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    mediaStorageType = MediaStorageType.REMOTE;
                } else {
                    mediaStorageType = MediaStorageType.LOCAL;
                }

                updateData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        AppLog.d(TAG, "onStop");
        //presenter.isAppBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume");

        //presenter.submitAppInfo();
        //presenter.setSdCardEventListener();
        //AppLog.d(TAG, "onResume()");

        loadPhotoWall();

        // update media list
//        List<MultiPbItemInfo> mediaList = Stream.concat(imagePresenter.getRemotePhotoInfoList().stream(), videoPresenter.getRemotePhotoInfoList().stream()).collect(Collectors.toList());
//        mediaArrayAdapter.clear();
//        mediaArrayAdapter.addAll(mediaList);
//        mediaArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyCamera camera = CameraManager.getInstance().getCurCamera();
        if(camera != null) {
            camera.setLoadThumbnail(false);
        }

        //presenter.reset();
        //presenter.removeActivity();

        // remove file list
        remotePhotoPresenter.emptyFileList();
        remoteVideoPresenter.emptyFileList();
    }

    public void beginSelectionMode() {
        // change to selection mode
        this.mediaViewMode = MediaViewMode.SELECTION;

        // in selection mode, show bottom bar
        bottomBarLayout.setVisibility(View.VISIBLE);

        if (this.mediaStorageType.equals(MediaStorageType.REMOTE)) {
            // in remote tab, show download button & hide share button
            downloadButton.setVisibility(View.VISIBLE);
            shareButton.setVisibility(View.GONE);
        } else {
            // in local tab, hide download button & show share button
            downloadButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.VISIBLE);
        }

        // show close button
        closeButton.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.GONE);

        // show select all button
        isSelectedAll = false;
        selectAllButton.setVisibility(View.VISIBLE);
        deselectAllButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

        // show selected number text
        selectedNumberText.setText("");
        selectedNumberText.setVisibility(View.VISIBLE);
        titleText.setVisibility(View.GONE);

        // change tab indicator color & disable tab change
        mediaTabView.setEnabled(false);
        for (int i = 0; i < mediaTabView.getTabCount(); i ++) {
            View tab = ((ViewGroup) mediaTabView.getChildAt(0)).getChildAt(i);
            tab.setOnTouchListener((v, event) -> true);
        }

        mediaTabView.setTabTextColors(getResources().getColor(R.color.button_disabled_color), getResources().getColor(R.color.button_disabled_color));
        mediaTabView.setSelectedTabIndicatorColor(getResources().getColor(R.color.button_disabled_color));

        //this.mediaArrayAdapter.updateMode(this.isSelectMode? "selection": "normal");
        //mediaArrayAdapter.unselectAllFiles();

        this.recyclerViewAdapter.updateUI();
        this.updateScreenTitle();
    }

    private void finishSelectionMode() {
        // change to normal mode
        this.mediaViewMode = MediaViewMode.NORMAL;

        // in normal mode, hide bottom bar
        bottomBarLayout.setVisibility(View.GONE);

        // show back button
        backButton.setVisibility(View.VISIBLE);
        closeButton.setVisibility(View.GONE);

        // show select button
        selectButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        deselectAllButton.setVisibility(View.GONE);

        // show title text
        titleText.setVisibility(View.VISIBLE);
        selectedNumberText.setVisibility(View.GONE);

        // change tab indicator color & enable tab change
        mediaTabView.setEnabled(true);
        for (int i = 0; i < mediaTabView.getTabCount(); i ++) {
            View tab = ((ViewGroup) mediaTabView.getChildAt(0)).getChildAt(i);
            tab.setOnTouchListener(null);
        }

        mediaTabView.setTabTextColors(getResources().getColor(R.color.button_pressed_color), getResources().getColor(R.color.yellow));
        mediaTabView.setSelectedTabIndicatorColor(getResources().getColor(R.color.yellow));

        this.recyclerViewAdapter.deselectAll();
        this.recyclerViewAdapter.updateUI();
    }

    public void quitEditMode() {
        remotePhotoPresenter.quitEditMode();
        remoteVideoPresenter.quitEditMode();
    }

//    public List<MultiPbItemInfo> getSelectedList() {
//        return Stream.concat(imagePresenter.getSelectedList().stream(), videoPresenter.getSelectedList().stream()).collect(Collectors.toList());
//    }

    public void deleteFiles() {
        MediaActivity activity = this;

        if (this.mediaStorageType.equals(MediaStorageType.LOCAL)) {
            List<LocalMediaItemInfo> arrayList = new ArrayList<>();
            for (Object item: this.recyclerViewAdapter.getSelectedItemList()) {
                arrayList.add((LocalMediaItemInfo) item);
            }

            if (arrayList.isEmpty()) {
                MyToast.show(this, R.string.gallery_no_file_selected);
                return;
            }

            BottomSheetDialog deleteDialog = new BottomSheetDialog(this);
            View deleteDialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
            deleteDialog.setContentView(deleteDialogLayout);
            deleteDialog.show();

            Button deleteButton = deleteDialogLayout.findViewById(R.id.delete_button);
            Button cancelButton = deleteDialogLayout.findViewById(R.id.cancel_button);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();

                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                    localPhotoPresenter.deleteFiles(arrayList.stream().filter(e -> e.fileType.equals(MediaType.PHOTO)).collect(Collectors.toList()));
                    localVideoPresenter.deleteFiles(arrayList.stream().filter(e -> e.fileType.equals(MediaType.VIDEO)).collect(Collectors.toList()));
                    MyProgressDialog.closeProgressDialog();

                    // update the recycler view
                    activity.updateData();
                }
            });
        } else {
            List<RemoteMediaItemInfo> arrayList = new ArrayList<>();
            for (Object item: this.recyclerViewAdapter.getSelectedItemList()) {
                arrayList.add((RemoteMediaItemInfo) item);
            }

            if (arrayList.isEmpty()) {
                MyToast.show(this, R.string.gallery_no_file_selected);
                return;
            }

            BottomSheetDialog deleteDialog = new BottomSheetDialog(this);
            View deleteDialogLayout = LayoutInflater.from(this).inflate(R.layout.dialog_delete, null);
            deleteDialog.setContentView(deleteDialogLayout);
            deleteDialog.show();

            Button deleteButton = deleteDialogLayout.findViewById(R.id.delete_button);
            Button cancelButton = deleteDialogLayout.findViewById(R.id.cancel_button);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();

                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                    remotePhotoPresenter.deleteFiles(arrayList.stream().filter(e -> e.fileType.equals(MediaType.PHOTO)).collect(Collectors.toList()));
                    remoteVideoPresenter.deleteFiles(arrayList.stream().filter(e -> e.fileType.equals(MediaType.VIDEO)).collect(Collectors.toList()));
                    MyProgressDialog.closeProgressDialog();

                    // update the recycler view
                    activity.updateData();
                }
            });
        }
    }

    public void downloadFiles() {
        if (this.mediaStorageType.equals(MediaStorageType.LOCAL)) {
            Log.e(TAG, "Downloading is not available in local storage");
            return;
        }

        List<RemoteMediaItemInfo> arrayList = new ArrayList<>();
        for (Object item: this.recyclerViewAdapter.getSelectedItemList()) {
            arrayList.add((RemoteMediaItemInfo) item);
        }

        if (arrayList.isEmpty()) {
            MyToast.show(this, R.string.gallery_no_file_selected);
            return;
        }

        remotePhotoPresenter.downloadFiles(arrayList.stream().filter(e -> e.fileType.equals(MediaType.PHOTO)).collect(Collectors.toList()));
        remoteVideoPresenter.downloadFiles(arrayList.stream().filter(e -> e.fileType.equals(MediaType.VIDEO)).collect(Collectors.toList()));

        // update the recycler view
        this.updateData();
    }

    public void shareFiles() {
    }

    public void loadPhotoWall(){
//        imagePresenter.loadPhotoWall();
//        videoPresenter.loadPhotoWall();
    }

    public void playPhoto(int position) {
//        this.imagePresenter.itemClick(position);
    }

    public void playVideo(int position) {
        // current position is the position in merged list (photo + video)
        // need to get the correct position in the list which only contains video
        // need to reduce the count of photo
//        this.videoPresenter.itemClick(position - this.imagePresenter.getRemotePhotoInfoList().size());
    }

    private void deselectAllFiles() {
        // deselect all files
        isSelectedAll = false;

        // show select all button
        selectAllButton.setVisibility(View.VISIBLE);
        deselectAllButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

        this.recyclerViewAdapter.deselectAll();
    }

    private void selectAllFiles() {
        // select all files
        isSelectedAll = true;

        // show deselect all button
        deselectAllButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

        this.recyclerViewAdapter.selectAll();
    }

    public String getMediaViewMode() {
        return this.mediaViewMode;
    }

    public void updateData() {
        List<Object> updatedMediaItemList = new ArrayList<>();

        if (this.mediaStorageType.equals(MediaStorageType.LOCAL)) {
            // initialize the media item list
            List<LocalMediaItemInfo> mediaInfoList = Stream.concat(this.localPhotoPresenter.getPhotoInfoList().stream(), localVideoPresenter.getPhotoInfoList().stream()).collect(Collectors.toList());
            HashMap<String, List<Object>> mediaGroupList = new HashMap<>();

            for (LocalMediaItemInfo mediaItem: mediaInfoList) {
                String curKey = mediaItem.getFileDate();
                if (mediaGroupList.containsKey(curKey)) {
                    List<Object> arrayList = mediaGroupList.get(curKey);
                    if (arrayList == null) {
                        Log.e(TAG, "arrayList is null");
                        continue;
                    }

                    arrayList.add(mediaItem);
                    mediaGroupList.replace(curKey, arrayList);
                } else {
                    List<Object> arrayList = new ArrayList<>();
                    arrayList.add(mediaItem);
                    mediaGroupList.put(curKey, arrayList);
                }
            }

            // sort media item list by date
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            Collections.sort(mediaItemList, new Comparator<LocalMediaItemInfo>() {
//                public int compare(LocalMediaItemInfo e1, LocalMediaItemInfo e2) {
//                    try {
//                        return dateFormat.parse(e2.getFileDate()).compareTo(dateFormat.parse(e1.getFileDate()));
//                    } catch (ParseException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });

            List<String> keyList = new ArrayList<>(mediaGroupList.keySet());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Collections.sort(keyList, new Comparator<String>() {
                public int compare(String e1, String e2) {
                    try {
                        return dateFormat.parse(e2).compareTo(dateFormat.parse(e1));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            for (String key: keyList) {
                List<Object> arrayList = mediaGroupList.get(key);
                if (arrayList == null) {
                    Log.e(TAG, "arrayList is null");
                    continue;
                }

                updatedMediaItemList.add(new GroupMediaItemInfo(key, arrayList.size()));
                updatedMediaItemList.addAll(arrayList);
            }
        } else {
            // initialize the media item list
            List<RemoteMediaItemInfo> mediaInfoList = Stream.concat(this.remotePhotoPresenter.getRemotePhotoInfoList().stream(), remoteVideoPresenter.getRemotePhotoInfoList().stream()).collect(Collectors.toList());
            HashMap<String, List<Object>> mediaGroupList = new HashMap<>();

            for (RemoteMediaItemInfo mediaItem: mediaInfoList) {
                String curKey = mediaItem.getFileDate();
                if (mediaGroupList.containsKey(curKey)) {
                    List<Object> arrayList = mediaGroupList.get(curKey);
                    if (arrayList == null) {
                        Log.e(TAG, "arrayList is null");
                        continue;
                    }

                    arrayList.add(mediaItem);
                    mediaGroupList.replace(curKey, arrayList);
                } else {
                    List<Object> arrayList = new ArrayList<>();
                    arrayList.add(mediaItem);
                    mediaGroupList.put(curKey, arrayList);
                }
            }

            List<String> keyList = new ArrayList<>(mediaGroupList.keySet());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Collections.sort(keyList, new Comparator<String>() {
                public int compare(String e1, String e2) {
                    try {
                        return dateFormat.parse(e2).compareTo(dateFormat.parse(e1));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            for (String key: keyList) {
                List<Object> arrayList = mediaGroupList.get(key);
                if (arrayList == null) {
                    Log.e(TAG, "arrayList is null");
                    continue;
                }

                updatedMediaItemList.add(new GroupMediaItemInfo(key, arrayList.size()));
                updatedMediaItemList.addAll(arrayList);
            }
        }

        // update the adapter
        this.recyclerViewAdapter = new MediaRecyclerViewAdapter(this, updatedMediaItemList);
        this.recyclerView.setAdapter(recyclerViewAdapter);
        this.updateScreenTitle();
    }

    public void updateScreenTitle() {
        this.selectedNumberText.setText(this.recyclerViewAdapter.getSelectedCount() + "/" + this.recyclerViewAdapter.getTotalCount());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            boolean hasDeleted = data.getBooleanExtra("hasDeleted",false);
            if (hasDeleted) {
                this.updateData();
            }
        }
    }
}