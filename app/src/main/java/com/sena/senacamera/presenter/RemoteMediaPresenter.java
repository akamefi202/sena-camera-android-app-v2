package com.sena.senacamera.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import androidx.fragment.app.Fragment;

import android.util.Log;

import com.sena.senacamera.function.CameraAction.PbDownloadManager;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.presenter.Interface.BasePresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.FileOperation;
import com.sena.senacamera.data.Mode.OperationMode;
import com.sena.senacamera.data.SystemInfo.SystemInfo;
import com.sena.senacamera.data.entity.MultiPbFileResult;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.data.type.PhotoWallLayoutType;
import com.sena.senacamera.ui.activity.MediaActivity;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.ui.Interface.RemoteMediaView;
import com.sena.senacamera.ui.RemoteFileHelper;
import com.sena.senacamera.ui.adapter.MultiPbRecyclerViewAdapter;
import com.sena.senacamera.utils.imageloader.ImageLoaderConfig;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RemoteMediaPresenter extends BasePresenter {
    private static final String TAG = RemoteMediaPresenter.class.getSimpleName();
    private RemoteMediaView multiPbPhotoView;
    private MultiPbRecyclerViewAdapter recyclerViewAdapter;
    private Activity activity;
    private OperationMode curOperationMode = OperationMode.MODE_BROWSE;
    private List<RemoteMediaItemInfo> pbItemInfoList = new LinkedList<>();
    private FileOperation fileOperation;
    private Handler handler;
    private FileType fileType = FileType.FILE_PHOTO;
    private int fileTotalNum;
    PhotoWallLayoutType curLayoutType = PhotoWallLayoutType.PREVIEW_TYPE_LIST;
    private boolean needGetFileNumRemote = true;
    private int curIndex = 1;
    private int maxNum = 15;
    private boolean isMore = true;
    private boolean supportSegmentedLoading = false;
    private Fragment fragment;

    public RemoteMediaPresenter(Activity activity) {
        super(activity);

        this.activity = activity;
        this.handler = new Handler();

        if (CameraManager.getInstance().getCurCamera() != null) {
            fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
        }
        if (fileOperation == null) {
            AppLog.e(TAG, "RemoteMediaPresenter fileOperation is null");
            //finishActivity();
        }
    }
    public RemoteMediaPresenter(Activity activity, FileType fileType) {
        super(activity);

        this.activity = activity;
        this.handler = new Handler();
        this.fileType = fileType;

        if (CameraManager.getInstance().getCurCamera() != null) {
            fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
        }
        if (fileOperation == null) {
            AppLog.e(TAG, "RemoteMediaPresenter fileOperation is null");
            //finishActivity();
        }
    }

    public void setView(RemoteMediaView pbFragmentView) {
        this.multiPbPhotoView = pbFragmentView;
        initCfg();
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void resetCurIndex() {
        curIndex = 1;
    }

    public void resetAdapter() {
        recyclerViewAdapter = null;
    }

    public synchronized List<RemoteMediaItemInfo> getRemotePhotoInfoList() {
        if (CameraManager.getInstance().getCurCamera() == null) {
            Log.e(TAG, "Camera is disconnected");
            return new ArrayList<>();
        }

        if (supportSegmentedLoading) {
            fileTotalNum = RemoteFileHelper.getInstance().getFileCount(fileOperation, fileType);
            MultiPbFileResult multiPbFileResult = RemoteFileHelper.getInstance().getRemoteFile(fileOperation, fileType, fileTotalNum, curIndex);
            curIndex = multiPbFileResult.getLastIndex();
            isMore = multiPbFileResult.isMore();
            return multiPbFileResult.getFileList();
        } else {
            return RemoteFileHelper.getInstance().getRemoteFile(fileOperation, fileType);
        }
    }

    public synchronized void loadMoreFile() {
        if (recyclerViewAdapter == null) {
            return;
        }
        recyclerViewAdapter.setLoadState(recyclerViewAdapter.LOADING);
        AppLog.d(TAG, "loadMoreFile current list size:" + pbItemInfoList.size());
        if (!supportSegmentedLoading) {
            recyclerViewAdapter.setLoadState(recyclerViewAdapter.LOADING_END);
            return;
        }
        if (isMore) {
            // 模拟获取网络数据，延时1s
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<RemoteMediaItemInfo> tempList = getRemotePhotoInfoList();
                    if (tempList != null && tempList.size() > 0) {
                        pbItemInfoList.addAll(tempList);
                    }
                    RemoteFileHelper.getInstance().setLocalFileList(pbItemInfoList, fileType);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewAdapter.setLoadState(recyclerViewAdapter.LOADING_COMPLETE);
                        }
                    });

                }
            }).start();

        } else {
            // 显示加载到底的提示
            recyclerViewAdapter.setLoadState(recyclerViewAdapter.LOADING_END);
        }
    }

    public void loadPhotoWall() {
        MyProgressDialog.showProgressDialog(activity, R.string.message_loading);
        new Thread(new Runnable() {
            @Override
            public void run() {
                supportSegmentedLoading = RemoteFileHelper.getInstance().isSupportSegmentedLoading();
                if (supportSegmentedLoading) {
                    fileTotalNum = RemoteFileHelper.getInstance().getFileCount(fileOperation, fileType);
                    pbItemInfoList.clear();
                    List<RemoteMediaItemInfo> temp = RemoteFileHelper.getInstance().getLocalFileList(fileType);
                    if (fileTotalNum > 0 && temp != null && temp.size() > 0) {
                        pbItemInfoList.addAll(temp);
                    } else if (fileTotalNum > 0) {
                        resetCurIndex();
                        List tempList = getRemotePhotoInfoList();
                        if (tempList != null && !tempList.isEmpty()) {
                            pbItemInfoList.addAll(tempList);
                        }
                        RemoteFileHelper.getInstance().setLocalFileList(pbItemInfoList, fileType);
                    }
                    AppLog.d(TAG, "pbItemInfoList=" + pbItemInfoList);
                    if (fileTotalNum <= 0 || pbItemInfoList.isEmpty()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                //multiPbPhotoView.setRecyclerViewVisibility(View.GONE);
                                //multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
//                            MyToast.show(activity, "no file");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
                                //multiPbPhotoView.setRecyclerViewVisibility(View.VISIBLE);
                                setAdapter();
                                MyProgressDialog.closeProgressDialog();
                            }
                        });
                    }
                } else {
                    pbItemInfoList.clear();
                    List<RemoteMediaItemInfo> temp = RemoteFileHelper.getInstance().getLocalFileList(fileType);
                    if (temp != null) {
                        pbItemInfoList.addAll(temp);
                    } else {
                        resetCurIndex();
                        List tempList = getRemotePhotoInfoList();
                        if (tempList != null && tempList.size() > 0) {
                            pbItemInfoList.addAll(tempList);
                        }
                        RemoteFileHelper.getInstance().setLocalFileList(pbItemInfoList, fileType);
                    }
                    AppLog.d(TAG, "pbItemInfoList=" + pbItemInfoList);
                    if (pbItemInfoList.isEmpty()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                //multiPbPhotoView.setRecyclerViewVisibility(View.GONE);
                                //multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
//                            MyToast.show(activity, "no file");
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
                                //multiPbPhotoView.setRecyclerViewVisibility(View.VISIBLE);
                                setAdapter();
                                MyProgressDialog.closeProgressDialog();
                            }
                        });
                    }
                }

            }
        }).start();
    }

    public void setAdapter() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (pbItemInfoList == null || pbItemInfoList.size() < 0) {
            return;
        }
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        } else {
            recyclerViewAdapter = new MultiPbRecyclerViewAdapter(activity, pbItemInfoList, fileType);
            setLayoutType(curLayoutType);
            //multiPbPhotoView.setRecyclerViewAdapter(recyclerViewAdapter);
        }
//        recyclerViewAdapter = new MultiPbRecyclerViewAdapter(activity, pbItemInfoList, fileType);
//        setLayoutType(curLayoutType);
//        multiPbPhotoView.setRecyclerViewAdapter(recyclerViewAdapter);
    }

    public void refreshAdapter() {
        curOperationMode = OperationMode.MODE_BROWSE;
        if (pbItemInfoList == null) {
            return;
        } else {
            pbItemInfoList.size();
        }
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        } else {
            recyclerViewAdapter = new MultiPbRecyclerViewAdapter(activity, pbItemInfoList, fileType);
            setLayoutType(curLayoutType);
            //multiPbPhotoView.setRecyclerViewAdapter(recyclerViewAdapter);
        }
//        recyclerViewAdapter = new MultiPbRecyclerViewAdapter(activity, pbItemInfoList, fileType);
//        setLayoutType(curLayoutType);
//        multiPbPhotoView.setRecyclerViewAdapter(recyclerViewAdapter);
    }

    public void refreshPhotoWall() {
        if (pbItemInfoList == null || pbItemInfoList.isEmpty()) {
            //multiPbPhotoView.setRecyclerViewVisibility(View.GONE);
            //multiPbPhotoView.setNoContentTxvVisibility(View.VISIBLE);
        } else {
            //multiPbPhotoView.setNoContentTxvVisibility(View.GONE);
            refreshAdapter();
        }
    }

    public void setLayoutType(PhotoWallLayoutType layoutType) {
        if (recyclerViewAdapter == null) {
            return;
        }
        if (layoutType == PhotoWallLayoutType.PREVIEW_TYPE_LIST) {
            recyclerViewAdapter.setCurViewType(MultiPbRecyclerViewAdapter.TYPE_LIST);
            //multiPbPhotoView.setRecyclerViewLayoutManager(new LinearLayoutManager(activity));
        } else if (layoutType == PhotoWallLayoutType.PREVIEW_TYPE_GRID) {
            recyclerViewAdapter.setCurViewType(MultiPbRecyclerViewAdapter.TYPE_GRID);
            //multiPbPhotoView.setRecyclerViewLayoutManager(new GridLayoutManager(activity, 4));
        } else if (layoutType == PhotoWallLayoutType.PREVIEW_TYPE_QUICK_LIST) {
            recyclerViewAdapter.setCurViewType(MultiPbRecyclerViewAdapter.TYPE_QUICK_LIST);
            //multiPbPhotoView.setRecyclerViewLayoutManager(new LinearLayoutManager(activity));
        }
        //multiPbPhotoView.setRecyclerViewAdapter(recyclerViewAdapter);
    }

    public void changePreviewType(PhotoWallLayoutType layoutType) {
        curLayoutType = layoutType;
        setLayoutType(curLayoutType);
    }

    public void enterEditMode(int position) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            curOperationMode = OperationMode.MODE_EDIT;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            recyclerViewAdapter.setOperationMode(curOperationMode);
            recyclerViewAdapter.changeCheckBoxState(position);
            multiPbPhotoView.setPhotoSelectNumText(recyclerViewAdapter.getSelectedCount());
            AppLog.i(TAG, "gridViewSelectOrCancelOnce curOperationMode=" + curOperationMode);
        }
    }

    public void quitEditMode() {
        if (curOperationMode == OperationMode.MODE_EDIT) {
            curOperationMode = OperationMode.MODE_BROWSE;
            multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
            recyclerViewAdapter.quitEditMode();
        }
    }

    public synchronized void itemClick(final int position) {
        AppLog.i(TAG, "listViewSelectOrCancelOnce positon=" + position );
        if (activity == null) {
            Log.e("MultiPbFragmentPresenter - itemClick", "activity is null");
            return;
        }

        if (curOperationMode == OperationMode.MODE_BROWSE) {
            AppLog.i(TAG, "listViewSelectOrCancelOnce curOperationMode=" + curOperationMode);

            if (fileType == FileType.FILE_PHOTO) {
//                Intent intent = new Intent();
//                intent.putExtra("curfilePosition", position);
//                intent.putExtra("fileType", fileType.ordinal());
//                intent.setClass(activity, PhotoPbActivity.class);
////                activity.startActivity(intent);
//                activity.startActivityForResult(intent, 1000);
            } else {
                // file type is "video"
                MyProgressDialog.showProgressDialog(activity, R.string.wait);
                MyCamera myCamera = CameraManager.getInstance().getCurCamera();
                if (myCamera != null) {
                    myCamera.setLoadThumbnail(false);
                }
                stopLoad();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Intent intent = new Intent();
//                        intent.putExtra("curfilePosition", position);
//                        intent.putExtra("fileType", fileType.ordinal());
//                        intent.setClass(activity, VideoPbActivity.class);
////                        activity.startActivity(intent);
//                        activity.startActivityForResult(intent, 1000);
                        MyProgressDialog.closeProgressDialog();
                    }
                }, 1500);
            }
        } else {
            //recyclerViewAdapter.changeCheckBoxState(position);
            //multiPbPhotoView.setPhotoSelectNumText(recyclerViewAdapter.getSelectedCount());
        }
    }

    public void selectOrCancelAll(boolean isSelectAll) {
        if (curOperationMode == OperationMode.MODE_BROWSE) {
            return;
        }
        int selectNum;
        if (isSelectAll) {
            //recyclerViewAdapter.selectAllItems();
            //selectNum = recyclerViewAdapter.getSelectedCount();
        } else {
            //recyclerViewAdapter.cancelAllSelections();
            //selectNum = recyclerViewAdapter.getSelectedCount();
        }
        //multiPbPhotoView.setPhotoSelectNumText(selectNum);
    }

    public List<RemoteMediaItemInfo> getSelectedList() {
        //return recyclerViewAdapter.getCheckedItemsList();
        return new LinkedList<>();
    }


    public void emptyFileList() {
        RemoteFileHelper.getInstance().clearFileList(fileType);
    }

    public void deleteFile() {
        List<RemoteMediaItemInfo> list = getSelectedList();
        if (list == null || list.isEmpty()) {
            MyToast.show(activity, R.string.gallery_no_file_selected);
        } else {
            CharSequence what = activity.getResources().getString(R.string.gallery_delete_des).replace("$1$", String.valueOf(list.size()));
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setMessage(what);
            builder.setPositiveButton(activity.getResources().getString(R.string.gallery_cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            final List<RemoteMediaItemInfo> finalList = list;
            final FileType finalFileType = fileType;
            builder.setNegativeButton(activity.getResources().getString(R.string.gallery_delete), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                    quitEditMode();
                    //new DeleteFileThread(finalList, finalFileType).run();
                    new Thread(new DeleteFileThread(finalList, finalFileType)).start();
                }
            });
            builder.create().show();
        }
    }

    // delete files selected
    public void deleteFiles(List<RemoteMediaItemInfo> list) {
        if (list == null || list.isEmpty()) {
            //MyToast.show(activity, R.string.gallery_no_file_selected);
            return;
        }

        quitEditMode();
        //new DeleteFileThread(finalList, finalFileType).run();
        new Thread(new DeleteFileThread(list, fileType)).start();
    }

    // download files
    public void downloadFiles(List<RemoteMediaItemInfo> list) {
        LinkedList<ICatchFile> linkedList = new LinkedList<>();
        long fileSizeTotal = 0;

        if (list.isEmpty()) {
            return;
        }

        for (RemoteMediaItemInfo temp: list) {
            linkedList.add(temp.iCatchFile);
            fileSizeTotal += temp.getFileSizeInteger();
        }

        if (SystemInfo.getSDFreeSize(activity) < fileSizeTotal) {
            MyToast.show(activity, R.string.text_sd_card_memory_shortage);
        } else {
            quitEditMode();
            PbDownloadManager downloadManager = new PbDownloadManager(activity, linkedList);
            downloadManager.setCallback(new Callback() {
                @Override
                public void processSucceed() {
                    ((MediaActivity) activity).updateData();
                }

                @Override
                public void processFailed() {
                    ((MediaActivity) activity).updateData();
                }
            });
            downloadManager.show();
        }
    }

    public void stopLoad() {
        ImageLoaderConfig.stopLoad();
    }

    private class DeleteFileThread implements Runnable {
        private List<RemoteMediaItemInfo> fileList;
        //        private List<MultiPbItemInfo> deleteFailedList;
        private List<RemoteMediaItemInfo> deleteSucceedList;
        private Handler handler;
        private FileOperation fileOperation;
        private FileType fileType;

        public DeleteFileThread(List<RemoteMediaItemInfo> fileList, FileType fileType) {
            this.fileList = fileList;
            this.handler = new Handler();
            this.fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
            this.fileType = fileType;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "DeleteThread");
//            deleteFailedList = new LinkedList<MultiPbItemInfo>();
            deleteSucceedList = new LinkedList<RemoteMediaItemInfo>();
            for (RemoteMediaItemInfo tempFile : fileList) {
                AppLog.d(TAG, "deleteFile f.getFileHandle =" + tempFile.getFileHandle());
                if (fileOperation.deleteFile(tempFile.iCatchFile)) {
                    deleteSucceedList.add(tempFile);
                }
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    MyProgressDialog.closeProgressDialog();
                    if (deleteSucceedList.size() > 0) {
                        pbItemInfoList.removeAll(deleteSucceedList);
                        RemoteFileHelper.getInstance().setLocalFileList(pbItemInfoList, fileType);
                    }
                    if (supportSegmentedLoading) {
                        //删除后fileHandle变化，需重新获取列表
                        resetCurIndex();
                        RemoteFileHelper.getInstance().clearFileList(fileType);
                        loadPhotoWall();
                    } else {
                        refreshPhotoWall();
                    }
                    curOperationMode = OperationMode.MODE_BROWSE;
                    //multiPbPhotoView.notifyChangeMultiPbMode(curOperationMode);
                }
            });
        }
    }

    public interface OnGetListCompleteListener {
        void onGetFileListComplete();
    }
}