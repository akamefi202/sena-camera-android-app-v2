package com.sena.senacamera.presenter;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.presenter.Interface.BasePresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.Mode.OperationMode;
import com.sena.senacamera.data.entity.LocalMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.PhotoWallLayoutType;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.Interface.LocalMediaView;
import com.sena.senacamera.ui.adapter.LocalMultiPbWallGridAdapter;
import com.sena.senacamera.ui.adapter.LocalMultiPbWallListAdapter;
import com.sena.senacamera.utils.ConvertTools;
import com.sena.senacamera.utils.StorageUtil;
import com.sena.senacamera.utils.fileutils.MFileTools;
import com.sena.senacamera.utils.PanoramaTools;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by b.jiang on 2017/5/19.
 */

public class LocalMediaPresenter extends BasePresenter {

    private static final String TAG = LocalMediaPresenter.class.getSimpleName();
    private LocalMediaView multiPbPhotoView;
    private LocalMultiPbWallListAdapter photoWallListAdapter;
    private LocalMultiPbWallGridAdapter photoWallGridAdapter;
    private Activity activity;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private List<LocalMediaItemInfo> pbItemInfoList;
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

    public List<LocalMediaItemInfo> getPhotoInfoList() {
        String fileDate;
        String rootPath = StorageUtil.getRootPath(activity);
        final List<LocalMediaItemInfo> photoList = new ArrayList<LocalMediaItemInfo>();
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
        for (int i = 0; i < fileList.size(); i++) {
            long time = fileList.get(i).lastModified();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            fileDate = format.format(new Date(time));

            if (!sectionMap.containsKey(fileDate)) {
                sectionMap.put(fileDate, section);
                LocalMediaItemInfo mGridItem = new LocalMediaItemInfo(fileList.get(i), section, PanoramaTools.isPanorama(fileList.get(i).getPath()), fileType == FileType.FILE_PHOTO? "photo": "video", getVideoDuration(fileList.get(i).getPath()));
                photoList.add(mGridItem);
                section++;
            } else {
                LocalMediaItemInfo mGridItem = new LocalMediaItemInfo(fileList.get(i), sectionMap.get(fileDate), PanoramaTools.isPanorama(fileList.get(i).getPath()), fileType == FileType.FILE_PHOTO? "photo": "video", getVideoDuration(fileList.get(i).getPath()));
                photoList.add(mGridItem);
            }
        }

        return photoList;
    }

    public String getVideoDuration(String path) {
        if (!path.endsWith(".MOV")) {
            return "";
        }

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(path); // 本地文件路径
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            if (duration == null || duration.isEmpty()) {
                return "";
            }

            mmr.release();
            return ConvertTools.millisecondsToMinuteOrHours((int) Math.ceil(Double.parseDouble(duration)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, R.string.loading);
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

    public List<LocalMediaItemInfo> getSelectedList() {
        if (AppInfo.photoWallLayoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            return photoWallListAdapter.getSelectedList();
        } else {
            return photoWallGridAdapter.getCheckedItemsList();
        }
    }

    public void deleteFiles(List<LocalMediaItemInfo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        new Thread(new DeleteFileThread(list, fileType)).start();
    }

    class DeleteFileThread implements Runnable {
        private final List<LocalMediaItemInfo> fileList;
        private List<LocalMediaItemInfo> deleteFailedList;
        private List<LocalMediaItemInfo> deleteSucceedList;
        private Handler handler;
        private FileType fileType;

        public DeleteFileThread(List<LocalMediaItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileType = fileType;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "DeleteThread");

            if (deleteFailedList == null) {
                deleteFailedList = new LinkedList<LocalMediaItemInfo>();
            } else {
                deleteFailedList.clear();
            }
            if (deleteSucceedList == null) {
                deleteSucceedList = new LinkedList<LocalMediaItemInfo>();
            } else {
                deleteSucceedList.clear();
            }

            for (LocalMediaItemInfo tempFile: fileList) {
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
