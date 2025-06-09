package com.sena.senacamera.Presenter;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.Presenter.Interface.BasePresenter;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.data.Mode.OperationMode;
import com.sena.senacamera.data.entity.LocalPbItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.MediaType;
import com.sena.senacamera.data.type.PhotoWallLayoutType;
import com.sena.senacamera.ui.ExtendComponent.MyProgressDialog;
import com.sena.senacamera.ui.Interface.LocalMediaView;
import com.sena.senacamera.adapter.LocalMultiPbWallGridAdapter;
import com.sena.senacamera.adapter.LocalMultiPbWallListAdapter;
import com.sena.senacamera.utils.StorageUtil;
import com.sena.senacamera.utils.fileutils.MFileTools;
import com.sena.senacamera.utils.PanoramaTools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class LocalMediaPresenter extends BasePresenter {

    private String TAG = LocalMediaPresenter.class.getSimpleName();
    private LocalMediaView multiPbPhotoView;
    private LocalMultiPbWallListAdapter photoWallListAdapter;
    private LocalMultiPbWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private List<LocalPbItemInfo> pbItemInfoList;
    private Handler handler;
    private FileType fileType = FileType.FILE_PHOTO;

    public LocalMediaPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
    }

    public LocalMediaPresenter(Activity activity, FileType fileType) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
        this.fileType = fileType;
    }

    public void setView(LocalMediaView localPhotoWallView) {
        this.multiPbPhotoView = localPhotoWallView;
        initCfg();
    }

    public List<LocalPbItemInfo> getPhotoInfoList() {
        String fileDate;
        String rootPath = StorageUtil.getRootPath(activity);
        final List<LocalPbItemInfo> photoList = new ArrayList<LocalPbItemInfo>();
        List<File> fileList;
        if (fileType == FileType.FILE_PHOTO) {
            String filePath = rootPath + AppInfo.DOWNLOAD_PATH_PHOTO;

            fileList = MFileTools.getPhotosOrderByDate(filePath);
        } else {
            String filePath = rootPath + AppInfo.DOWNLOAD_PATH_VIDEO;
            fileList = MFileTools.getVideosOrderByDate(filePath);
        }
        if (fileList == null || fileList.isEmpty()) {
            return new ArrayList<>();
        }

        AppLog.i(TAG, "fileList size=" + fileList.size());
        for (int ii = 0; ii < fileList.size(); ii++) {
            long time = fileList.get(ii).lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileDate = format.format(new Date(time));

            if (!sectionMap.containsKey(fileDate)) {
                sectionMap.put(fileDate, section);
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), section, PanoramaTools.isPanorama(fileList.get(ii).getPath()), fileType == FileType.FILE_PHOTO? "photo": "video", "");
                photoList.add(mGridItem);
                section++;
            } else {
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(fileList.get(ii), sectionMap.get(fileDate), PanoramaTools.isPanorama(fileList.get(ii).getPath()), fileType == FileType.FILE_PHOTO? "photo": "video", "");
                photoList.add(mGridItem);
            }
        }

        return photoList;
    }

    public List<LocalPbItemInfo> getMediaList() {
        String fileDate;
        String rootPath = StorageUtil.getRootPath(activity);
        final List<LocalPbItemInfo> mediaList = new ArrayList<LocalPbItemInfo>();
        List<File> photoFileList, videoFileList;

        // get file list
        String photoFilePath = rootPath + AppInfo.DOWNLOAD_PATH_PHOTO;
        String videoFilePath = rootPath + AppInfo.DOWNLOAD_PATH_VIDEO;
        photoFileList = MFileTools.getPhotosOrderByDate(photoFilePath);
        videoFileList = MFileTools.getVideosOrderByDate(videoFilePath);

        if (photoFileList != null && videoFileList != null && photoFileList.isEmpty() && videoFileList.isEmpty()) {
            return new ArrayList<>();
        }

        AppLog.i(TAG, "fileList size = " + (photoFileList.size() + videoFileList.size()));

        // get photo media item list
        for (int i = 0; i < photoFileList.size(); i++) {
            long time = photoFileList.get(i).lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileDate = format.format(new Date(time));

            if (!sectionMap.containsKey(fileDate)) {
                sectionMap.put(fileDate, section);
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(photoFileList.get(i), section, PanoramaTools.isPanorama(photoFileList.get(i).getPath()), MediaType.PHOTO, "");
                mediaList.add(mGridItem);
                section++;
            } else {
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(photoFileList.get(i), sectionMap.get(fileDate), PanoramaTools.isPanorama(photoFileList.get(i).getPath()), MediaType.PHOTO, "");
                mediaList.add(mGridItem);
            }
        }

        // get video media item list
        for (int i = 0; i < videoFileList.size(); i++) {
            long time = videoFileList.get(i).lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileDate = format.format(new Date(time));

            if (!sectionMap.containsKey(fileDate)) {
                sectionMap.put(fileDate, section);
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(videoFileList.get(i), section, PanoramaTools.isPanorama(videoFileList.get(i).getPath()), MediaType.VIDEO, "");
                mediaList.add(mGridItem);
                section++;
            } else {
                LocalPbItemInfo mGridItem = new LocalPbItemInfo(videoFileList.get(i), sectionMap.get(fileDate), PanoramaTools.isPanorama(videoFileList.get(i).getPath()), MediaType.VIDEO, "");
                mediaList.add(mGridItem);
            }
        }

        GlobalInfo.getInstance().setLocalMediaList(mediaList);
        return mediaList;
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, "Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                pbItemInfoList =  getPhotoInfoList();
                if (pbItemInfoList == null || pbItemInfoList.size() <= 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            multiPbPhotoView.setGridViewVisibility(View.GONE);
                            multiPbPhotoView.setListViewVisibility(View.GONE);
                            multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
//                            MyToast.show(activity, "no file");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
                            setAdapter();
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
            }
        }).start();
    }

    public void setAdapter() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (pbItemInfoList != null && !pbItemInfoList.isEmpty()) {
            String fileDate = pbItemInfoList.get(0).getFileDate();
            AppLog.d(TAG, "fileDate=" + fileDate);
            //multiPbPhotoView.setListViewHeaderText(fileDate);
        }
        int curWidth = 0;
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            //multiPbPhotoView.setGridViewVisibility(View.GONE);
            //multiPbPhotoView.setListViewVisibility(View.VISIBLE);
            photoWallListAdapter = new LocalMultiPbWallListAdapter(activity, pbItemInfoList, FileType.FILE_PHOTO);
            //multiPbPhotoView.setListViewAdapter(photoWallListAdapter);
        } else {
            //multiPbPhotoView.setGridViewVisibility(View.VISIBLE);
            //multiPbPhotoView.setListViewVisibility(View.GONE);
            AppLog.d(TAG, "width=" + curWidth);
            photoWallGridAdapter = (new LocalMultiPbWallGridAdapter(activity, pbItemInfoList, FileType.FILE_PHOTO));
            multiPbPhotoView.setGridViewAdapter(photoWallGridAdapter);
        }
    }

    public void refreshPhotoWall() {
        AppLog.d(TAG, "refreshPhotoWall layoutType=" + AppInfo.photoWallLayoutType);
        pbItemInfoList = getPhotoInfoList();
        if (pbItemInfoList == null || pbItemInfoList.isEmpty()) {
            //multiPbPhotoView.setGridViewVisibility(View.GONE);
            //multiPbPhotoView.setListViewVisibility(View.GONE);
            //multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            //multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
            setAdapter();
        }
    }

    public void changePreviewType() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_GRID;
        } else {
            AppInfo.photoWallLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
        }
        loadPhotoWall();
    }

    public void listViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallListAdapter.setOperationMode(curOperationMode);
            photoWallListAdapter.changeSelectionState(position);
            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void gridViewEnterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void quitEditMode() {
        if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            //multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.quitEditMode();
            } else {
                photoWallGridAdapter.quitEditMode();
            }
        }
    }

    public void listViewSelectOrCancelOnce(int position) {
//        AppLog.i(TAG, "listViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
//        String videoPath = pbItemInfoList.get(position).getFilePath();
//        if (curOperationMode == OperationMode.MODE_BROWSE) {
//            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
//            if (fileType == FileType.FILE_PHOTO) {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalPhotoPbActivity.class);
//                activity.startActivity(intent);
//            } else {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePath", videoPath);
//                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalVideoPbActivity.class);
//                activity.startActivity(intent);
//            }
//
//        } else {
//            photoWallListAdapter.changeSelectionState(position);
//            multiPbPhotoView.setPhotoSelectNumText(photoWallListAdapter.getSelectedCount());
//        }
    }

    public void gridViewSelectOrCancelOnce(int position) {
        AppLog.i(TAG, "gridViewSelectOrCancelOnce positon=" + position + " AppInfo.photoWallPreviewType=" + AppInfo.photoWallLayoutType);
//        String videoPath = pbItemInfoList.get(position).getFilePath();
//        if (curOperationMode == OperationMode.MODE_BROWSE) {
//            if (fileType == FileType.FILE_PHOTO) {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalPhotoPbActivity.class);
//                activity.startActivity(intent);
//            } else {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePath", videoPath);
//                intent.putExtra("curfilePosition", position);
//                intent.setClass(activity, LocalVideoPbActivity.class);
//                activity.startActivity(intent);
//            }
//        } else {
//            photoWallGridAdapter.changeCheckBoxState(position, curOperationMode);
//            multiPbPhotoView.setPhotoSelectNumText(photoWallGridAdapter.getSelectedCount());
//        }
    }


    public void selectOrCancelAll(boolean isSelectAll) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            return;
        }
        int selectNum;
        if (isSelectAll) {
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.selectAllItems();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.selectAllItems();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        } else {
            if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
                photoWallListAdapter.cancelAllSelections();
                selectNum = photoWallListAdapter.getSelectedCount();
            } else {
                photoWallGridAdapter.cancelAllSelections();
                selectNum = photoWallGridAdapter.getSelectedCount();
            }
            multiPbPhotoView.setPhotoSelectNumText(selectNum);
        }
    }

    public List<LocalPbItemInfo> getSelectedList() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }

    public void deleteFiles(List<LocalPbItemInfo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        new Thread(new DeleteFileThread(list, fileType)).start();
    }

    class DeleteFileThread implements Runnable {
        private final List<LocalPbItemInfo> fileList;
        private List<LocalPbItemInfo> deleteFailedList;
        private List<LocalPbItemInfo> deleteSucceedList;
        private Handler handler;
        private FileType fileType;

        public DeleteFileThread(List<LocalPbItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileType = fileType;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "DeleteThread");

            if (deleteFailedList == null) {
                deleteFailedList = new LinkedList<LocalPbItemInfo>();
            } else {
                deleteFailedList.clear();
            }
            if (deleteSucceedList == null) {
                deleteSucceedList = new LinkedList<LocalPbItemInfo>();
            } else {
                deleteSucceedList.clear();
            }

            for (LocalPbItemInfo tempFile: fileList) {
                File file = tempFile.file;
                Log.e("DeleteThread", file.getPath());
                Log.e("DeleteThread", file.getAbsolutePath());
                if (file.exists()) {
                    Log.e("DeleteThread", "exist");
                }
                if (!file.delete()) {
                    Log.e("DeleteThread", "failed");
                    deleteFailedList.add(tempFile);
                } else {
                    Log.e("DeleteThread", "succeeded");
                    deleteSucceedList.add(tempFile);
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();

                    curOperationMode = OperationMode.MODE_BROWSE;
                    quitEditMode();
                    refreshPhotoWall();
                }
            });
        }
    }
}
