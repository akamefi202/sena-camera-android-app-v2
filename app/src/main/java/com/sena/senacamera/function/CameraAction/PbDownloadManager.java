package com.sena.senacamera.function.CameraAction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.data.type.MediaStorageType;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.ui.adapter.DownloadManagerAdapter;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.entity.DownloadInfo;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.data.Message.AppMessage;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.FileOperation;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.appdialog.CustomDownloadDialog;
import com.sena.senacamera.utils.StorageUtil;
import com.sena.senacamera.utils.fileutils.FileTools;
import com.sena.senacamera.utils.MediaRefresh;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchFileType;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Added by zhangyanhu C01012,2014-6-20
 */
public class PbDownloadManager {
    private static final String TAG = PbDownloadManager.class.getSimpleName();

    public long downloadProgress;
    public FileOperation fileOperation;
    private LinkedList<ICatchFile> downloadTaskList;
    private LinkedList<ICatchFile> downloadChooseList;
//    private LinkedList<ICatchFile> downloadProgressList;
    private ICatchFile curDownloadFile;
    private Context context;
    private HashMap<Integer, DownloadInfo> downloadInfoMap = new HashMap<Integer, DownloadInfo>();
    private ICatchFile currentDownloadFile;
    private Timer downloadProgressTimer;
    private int downloadFailed = 0;
    private int downloadSucceed = 0;
    private CustomDownloadDialog customDownloadDialog;
    private String curFilePath = "";
    private Callback callback;

    public PbDownloadManager(Context context, LinkedList<ICatchFile> downloadList) {
        this.context = context;
        this.downloadTaskList = downloadList;
        this.fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();
//        filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH;
        downloadChooseList = new LinkedList<>();
//        downloadProgressList = new LinkedList<ICatchFile>();
        downloadChooseList.addAll(downloadTaskList);
        for (int i = 0; i < downloadChooseList.size(); i ++) {
            DownloadInfo downloadInfo = new DownloadInfo(downloadChooseList.get(i), downloadChooseList.get(i).getFileSize(), 0, 0, false);
            downloadInfoMap.put(downloadChooseList.get(i).getFileHandle(), downloadInfo);
        }
    }

    public void show() {
        showDownloadManagerDialog();

        if (!downloadTaskList.isEmpty()) {
            currentDownloadFile = downloadTaskList.getFirst();
            new DownloadAsyncTask(currentDownloadFile).execute();
            downloadProgressTimer = new Timer();
            downloadProgressTimer.schedule(new DownloadProgressTask(), 500, 500);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    Handler downloadManagerHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // super.handleMessage(msg)
            String message;
            switch (msg.what) {
                case AppMessage.UPDATE_LOADING_PROGRESS:
                    ICatchFile icatchFile = ((DownloadInfo) msg.obj).file;
                    downloadInfoMap.put(icatchFile.getFileHandle(), (DownloadInfo) msg.obj);
                    break;
                case AppMessage.CANCEL_DOWNLOAD_ALL:
                    AppLog.d(TAG, "receive CANCEL_DOWNLOAD_ALL");
                    alertForQuitDownload();
                    break;
                case AppMessage.MESSAGE_CANCEL_DOWNLOAD_SINGLE:
                    ICatchFile temp = (ICatchFile) msg.obj;
                    AppLog.d(TAG, "receive MESSAGE_CANCEL_DOWNLOAD_SINGLE");
                    if (currentDownloadFile == temp) {
                        if (fileOperation.cancelDownload() == false) {
                            Toast.makeText(context, R.string.dialog_cancel_downloading_failed, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        //ICOM-4224 Begin modify by b.jiang 20170329
//                        String fileName = currentDownloadFile.getFileName();
//                        String filePath = null;
//                        if (currentDownloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
//                            filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO
//                                    + fileName;
//                        } else if (currentDownloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
//                            filePath = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO
//                                    + fileName;
//                        }
                        if (curFilePath != null) {
                            File file = new File(curFilePath);
                            //ICOM-4224 End modify by b.jiang 20170329
                            if (file == null || !file.exists()) {
                                return;
                            }
                            if (file.delete()) {
                                AppLog.d(TAG, "delete file success == " + curFilePath);
                            }
                        }


                    }
                    Toast.makeText(context, R.string.dialog_cancel_downloading_succeeded, Toast.LENGTH_SHORT).show();
                    downloadInfoMap.remove(temp.getFileHandle());
                    downloadChooseList.remove(temp);
                    downloadTaskList.remove(temp);
                    AppLog.d(TAG, "receive MESSAGE_CANCEL_DOWNLOAD_SINGLE downloadChooseList size=" + downloadChooseList.size() + "downloadInfoMap size=" + downloadInfoMap.size());

                    updateDownloadStatus();
                    if (downloadTaskList.size() <= 0) {
                        if (customDownloadDialog != null) {
                            customDownloadDialog.dismissDownloadDialog();
                            customDownloadDialog = null;
                        }
                    }
                    break;
                case AppMessage.DOWNLOAD_FAILURE:
                    AppLog.d(TAG, "receive DOWNLOAD_FAILURE downloadFailed=" + downloadFailed);
                    downloadFailed ++;
                    updateDownloadStatus();
                    break;
                case AppMessage.DOWNLOAD_SUCCEED:
                    downloadSucceed ++;
                    updateDownloadStatus();
                    break;
            }
        }
    };


    public void showDownloadManagerDialog() {
        customDownloadDialog = new CustomDownloadDialog(context);
        customDownloadDialog.showDownloadDialog();
        customDownloadDialog.setBackBtnOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                alertForQuitDownload();
                //downloadManagerHandler.obtainMessage(AppMessage.CANCEL_DOWNLOAD_ALL).sendToTarget();
            }
        });
        updateDownloadStatus();
    }

    public void cancelDownload(ICatchFile downloadFile) {
        if (currentDownloadFile == downloadFile) {
            if (!fileOperation.cancelDownload()) {
                Toast.makeText(context, R.string.dialog_cancel_downloading_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            if (curFilePath != null) {
                File file = new File(curFilePath);
                if (!file.exists()) {
                    return;
                }
                if (file.delete()) {
                    AppLog.d(TAG, "delete file success == " + curFilePath);
                }
            }
        }

        Toast.makeText(context, R.string.dialog_cancel_downloading_succeeded, Toast.LENGTH_SHORT).show();
        downloadInfoMap.remove(downloadFile.getFileHandle());
        downloadChooseList.remove(downloadFile);
        downloadTaskList.remove(downloadFile);
        updateDownloadStatus();

        if (downloadTaskList.isEmpty()) {
            if (customDownloadDialog != null) {
                customDownloadDialog.dismissDownloadDialog();
                customDownloadDialog = null;
            }
        }
    }


    public void alertForQuitDownload() {
        AppDialogManager.getInstance().showStopDownloadDialog(context, new DialogButtonListener() {
            @Override
            public void onStop() {
                downloadTaskList.clear();
                //ICOM-4224 Begin modify by b.jiang 20170329
                if (curFilePath != null) {
                    File file = new File(curFilePath);
                    if (!file.exists()) {
                        return;
                    }
                    if (file.delete()) {
                        AppLog.d(TAG, "alertForQuitDownload file delete success == " + curFilePath);
                    }
                }
                //ICOM-4224 End modify by b.jiang 20170329

                if (!fileOperation.cancelDownload()) {
                    Toast.makeText(context, R.string.dialog_cancel_downloading_failed, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (customDownloadDialog != null) {
                        customDownloadDialog.dismissDownloadDialog();
                        customDownloadDialog = null;
                    }

                    if (downloadProgressTimer != null) {
                        downloadProgressTimer.cancel();
                    }
                    Toast.makeText(context, R.string.dialog_cancel_downloading_succeeded, Toast.LENGTH_SHORT).show();
                }
                AppLog.d(TAG, "cancel download task and quit download manager");
            }
        });
    }

    /**
     * 单个文件下载完成
     */
    public void singleDownloadComplete(boolean result,ICatchFile iCatchFile) {
        if (downloadInfoMap.containsKey(iCatchFile.getFileHandle())) {
            DownloadInfo downloadInfo = downloadInfoMap.get(iCatchFile.getFileHandle());
            downloadInfo.setDone(true);
            downloadInfo.progress = 100;
        }
    }

    public void downloadCompleted() {
        if (customDownloadDialog != null) {
            customDownloadDialog.dismissDownloadDialog();
            customDownloadDialog = null;
        }

        curFilePath = null;
        callback.processSucceed();
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(context.getResources().getString(R.string.download_manager));
//        String message = context.getResources().getString(R.string.download_complete_result).replace("$1$", String.valueOf(downloadSucceed))
//                .replace("$2$", String.valueOf(downloadFailed));
//        builder.setMessage(message);
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);
//        dialog.show();
    }

    class DownloadProgressTask extends TimerTask {
        String TAG = DownloadProgressTask.class.getSimpleName();
        long lastTime = 0;

        @Override
        public void run() {

            if (curDownloadFile == null) {
                return;
            }

            final ICatchFile iCatchFile = curDownloadFile;
//            String path = null;
//            if (iCatchFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
//                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_PHOTO
//                        + iCatchFile.getFileName();
//            } else if (iCatchFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
//                path = Environment.getExternalStorageDirectory().toString() + AppInfo.DOWNLOAD_PATH_VIDEO
//                        + iCatchFile.getFileName();
//            }
            File file = new File(curFilePath);
            AppLog.d(TAG, "Filename:" + file + " iCatchFile name:" + iCatchFile.getFileName() + " fileHandle:" + iCatchFile.getFileHandle());
            if (downloadInfoMap.containsKey(iCatchFile.getFileHandle()) == false) {
                //downloadProgressList.removeFirst();
                return;
            }
            final DownloadInfo downloadInfo = downloadInfoMap.get(iCatchFile.getFileHandle());
            AppLog.d(TAG, "downloadInfo isDone:" + downloadInfo.isDone());
            if (downloadInfo.isDone()) {
                return;
            }

            long fileLength = file.length();
            if (file != null) {
                if (fileLength == iCatchFile.getFileSize()) {
                    downloadProgress = 100;
                    downloadInfo.setDone(true);
                    //downloadProgressList.removeFirst();
                } else {
                    downloadProgress = file.length() * 100 / iCatchFile.getFileSize();
                }
            } else {
                downloadProgress = 0;
            }
            downloadInfo.curFileLength = fileLength;
            downloadInfo.progress = (int) downloadProgress;
            AppLog.d(TAG, "downloadProgress = " + downloadProgress);
            downloadManagerHandler.post(new Runnable() {
                @Override
                public void run() {
                    downloadInfoMap.put(iCatchFile.getFileHandle(), downloadInfo);
                    updateDownloadStatus();
//                    downloadManagerAdapter.notifyDataSetChanged();
                }
            });
//            downloadManagerHandler.obtainMessage(AppMessage.UPDATE_LOADING_PROGRESS, (int) downloadProgress, 0, downloadInfo).sendToTarget();
        }
    }

    class DownloadAsyncTask extends AsyncTask<String, Integer, Boolean> {
        String TAG = DownloadAsyncTask.class.getSimpleName();

        ICatchFile downloadFile;
        String fileName;
        String fileType = null;
        String filePath = null;
        PowerManager.WakeLock wakeLock;

        public DownloadAsyncTask(ICatchFile iCatchFile) {
            super();
            downloadFile = iCatchFile;
            curDownloadFile = iCatchFile;
            //downloadProgressList.addLast(downloadFile);
            if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
                filePath = StorageUtil.getRootPath(context) + AppInfo.DOWNLOAD_PATH_PHOTO;
            } else if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
                filePath = StorageUtil.getRootPath(context) + AppInfo.DOWNLOAD_PATH_VIDEO;
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {//处理后台执行的任务，在后台线程执行
            boolean retvalue = false;
            fileName = downloadFile.getFileName();
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //ICOM-4116 Begin Delete by b.jiang 20170315
//            File tempFile = new File(filePath + fileName);
//            if (tempFile.exists()) {
//                if (tempFile.length() == downloadFile.getFileSize()) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//                    downloadTaskList.remove(downloadFile);
//                    return true;
//                }
//            }
            //ICOM-4116 End Delete by b.jiang 20170315

            AppLog.d(TAG, "start downloadFile=" + filePath + fileName);
            //ICOM-4116 Begin Add by b.jiang 20170315
            curFilePath = FileTools.chooseUniqueFilename(filePath + fileName);
            retvalue = fileOperation.downloadFile(downloadFile, curFilePath);
            //ICOM-4116 End Add by b.jiang 2017031
            AppLog.d(TAG, "end downloadFile retvalue =" + retvalue);
            if (retvalue) {
                if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_VIDEO) {
                    AppLog.d(TAG, "fileName = " + fileName);
                    if (fileName.endsWith(".mov") || fileName.endsWith(".MOV")) {
                        fileType = "video/quicktime";
                    } else {
                        fileType = "video/mp4";
                    }
                    MediaRefresh.addMediaToDB(context, filePath + downloadFile.getFileName(), fileType);
                } else if (downloadFile.getFileType() == ICatchFileType.ICH_FILE_TYPE_IMAGE) {
                    MediaRefresh.scanFileAsync(context, filePath + downloadFile.getFileName());
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return retvalue;
        }

        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // set the wake lock
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakeLock");
            wakeLock.acquire(10*60*1000L /*10 minutes*/);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            //后台任务执行完之后被调用，在ui线程执行
            if (!result) {
                AppLog.d(TAG, "receive DOWNLOAD_FAILURE downloadFailed=" + downloadFailed);
                //downloadProgressList.remove(downloadFile);
                downloadFailed ++;
                updateDownloadStatus();
            } else {
                downloadSucceed ++;
                updateDownloadStatus();
            }

            singleDownloadComplete(result, downloadFile);
            downloadTaskList.remove(downloadFile);
            if (!downloadTaskList.isEmpty()) {
                currentDownloadFile = downloadTaskList.getFirst();
                new DownloadAsyncTask(currentDownloadFile).execute();
            } else {
                if (customDownloadDialog != null) {
                    customDownloadDialog.dismissDownloadDialog();
                    customDownloadDialog = null;
                }
                if (downloadProgressTimer != null) {
                    downloadProgressTimer.cancel();
                }
                downloadCompleted();
            }

            // release the wake lock
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            // release the wake lock
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
    }

    private void updateDownloadStatus() {
        int progress = (int) downloadProgress;
        String message = String.format("%d/%d %s(%d%%)", downloadSucceed, downloadChooseList.size(), context.getResources().getString(R.string.saving), progress);

        if (customDownloadDialog != null) {
            customDownloadDialog.setMessage(message);
            customDownloadDialog.setProgress(progress);
        }
    }
}
