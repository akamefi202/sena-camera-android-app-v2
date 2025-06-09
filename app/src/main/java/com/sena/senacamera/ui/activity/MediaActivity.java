package com.sena.senacamera.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.adapter.MediaViewPagerAdapter;
import com.sena.senacamera.data.Mode.MediaViewMode;
import com.sena.senacamera.data.entity.MultiPbItemInfo;
import com.sena.senacamera.ui.ExtendComponent.MyProgressDialog;
import com.sena.senacamera.ui.ExtendComponent.MyToast;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MediaActivity extends AppCompatActivity {
    private String TAG = "MediaActivity";
    private ImageButton backButton, closeButton;
    private Button selectButton, selectAllButton, deselectAllButton;
    private TabLayout mediaTabView;
    private ViewPager2 mediaViewPager;
    private MediaViewPagerAdapter mediaViewPagerAdapter;
    private LinearLayout downloadButton, shareButton, deleteButton, bottomBarLayout;
    private TextView titleText, selectedNumberText;
    //private RecyclerView recyclerView;

    private String mediaViewMode = MediaViewMode.NORMAL;
    private boolean isSelectedAll = false;

    // 0: remote tab, 1: local tab
    private int currentTabIndex = 0;

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
        mediaViewPager = findViewById(R.id.media_viewpager);

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

        mediaViewPagerAdapter = new MediaViewPagerAdapter(this);
        mediaViewPager.setAdapter(mediaViewPagerAdapter);

        mediaTabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mediaViewPager.setCurrentItem(tab.getPosition());
                currentTabIndex = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mediaViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mediaTabView.getTabAt(position).select();
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

        AppLog.d(TAG, "onStop()");
        //presenter.isAppBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //presenter.submitAppInfo();
        //presenter.setSdCardEventListener();
        //AppLog.d(TAG, "onResume()");

        //mediaArrayAdapter.clear();
        Log.e("RemoteMultiPbActivity", "onResume");

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
        if(camera != null){
            camera.setLoadThumbnail(false);
        }

        //presenter.reset();
        //presenter.removeActivity();

        // remove file list
//        imagePresenter.emptyFileList();
//        videoPresenter.emptyFileList();
    }

    public void beginSelectionMode() {
        // change to selection mode
        this.mediaViewMode = MediaViewMode.SELECTION;

        // in selection mode, show bottom bar
        bottomBarLayout.setVisibility(View.VISIBLE);

        if (currentTabIndex == 0) {
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

        updateUI();
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

        updateUI();
    }

    public void updateUI() {
        this.mediaViewPagerAdapter.updateUI();
    }

    public void quitEditMode() {
//        imagePresenter.quitEditMode();
//        videoPresenter.quitEditMode();
    }

    public void selectOrCancelAll(boolean isSelectAll) {
//        imagePresenter.selectOrCancelAll(isSelectAll);
//        videoPresenter.selectOrCancelAll(isSelectAll);
    }

//    public List<MultiPbItemInfo> getSelectedList() {
//        return Stream.concat(imagePresenter.getSelectedList().stream(), videoPresenter.getSelectedList().stream()).collect(Collectors.toList());
//    }

    public void deleteFiles() {
//        List<MultiPbItemInfo> list = this.mediaArrayAdapter.getSelectedFileList();
//        if (list == null || list.isEmpty()) {
//            MyToast.show(this, R.string.gallery_no_file_selected);
//            return;
//        }
//
//        CharSequence what = this.getResources().getString(R.string.gallery_delete_des).replace("$1$", String.valueOf(list.size()));
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(false);
//        builder.setMessage(what);
//
//        builder.setPositiveButton(this.getResources().getString(R.string.gallery_cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.setNegativeButton(this.getResources().getString(R.string.gallery_delete), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                MyProgressDialog.showProgressDialog(context, R.string.dialog_deleting);
//                imagePresenter.deleteFiles(list.stream().filter(e -> e.fileType.equals("photo")).collect(Collectors.toList()));
//                videoPresenter.deleteFiles(list.stream().filter(e -> e.fileType.equals("video")).collect(Collectors.toList()));
//                //this.loadPhotoWall();
//
//                // update the media list of grid view
//                //ArrayList<MultiPbItemInfo> mediaList = Stream.concat(imagePresenter.getRemotePhotoInfoList().stream(), videoPresenter.getRemotePhotoInfoList().stream()).collect(Collectors.toCollection(ArrayList::new));
//                //this.mediaArrayAdapter.setArrayList(mediaList);
//                mediaArrayAdapter.deleteSelectedFiles();
//            }
//        });
//        builder.create().show();

        updateUI();
    }

    public void downloadFiles() {
//        List<MultiPbItemInfo> list = this.mediaArrayAdapter.getSelectedFileList();
//        if (list == null || list.isEmpty()) {
//            MyToast.show(this, R.string.gallery_no_file_selected);
//            return;
//        }
//
//        imagePresenter.downloadFiles(list.stream().filter(e -> e.fileType.equals("photo")).collect(Collectors.toList()));
//        videoPresenter.downloadFiles(list.stream().filter(e -> e.fileType.equals("video")).collect(Collectors.toList()));

        //this.loadPhotoWall();

        // update the media list of grid view
        //mediaArrayAdapter.deleteSelectedFiles();

        updateUI();
    }

    public void shareFiles() {

        updateUI();
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

        mediaViewPagerAdapter.deselectAllFiles();
    }

    private void selectAllFiles() {
        // select all files
        isSelectedAll = true;

        // show deselect all button
        deselectAllButton.setVisibility(View.VISIBLE);
        selectAllButton.setVisibility(View.GONE);
        selectButton.setVisibility(View.GONE);

        updateUI();
    }

    public String getMediaViewMode() {
        return this.mediaViewMode;
    }
}