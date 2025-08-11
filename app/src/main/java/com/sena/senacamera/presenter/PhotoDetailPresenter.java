package com.sena.senacamera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.sena.senacamera.data.entity.DownloadInfo;
import com.sena.senacamera.data.entity.LocalMediaItemInfo;
import com.sena.senacamera.data.entity.MediaItemInfo;
import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.function.CameraAction.PbDownloadManager;
import com.sena.senacamera.function.SDKEvent;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.presenter.Interface.BasePresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.FileOperation;
import com.sena.senacamera.SdkApi.PanoramaPhotoPlayback;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.data.Mode.TouchMode;
import com.sena.senacamera.data.SystemInfo.SystemInfo;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.appdialog.CustomDownloadDialog;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.ui.Interface.PhotoDetailView;
import com.sena.senacamera.ui.RemoteFileHelper;
import com.sena.senacamera.ui.appdialog.AppDialog;
import com.sena.senacamera.utils.MediaRefresh;
import com.sena.senacamera.utils.StorageUtil;
import com.sena.senacamera.utils.fileutils.FileOper;
import com.sena.senacamera.utils.fileutils.FileTools;
import com.sena.senacamera.utils.imageloader.ImageLoaderUtil;
import com.sena.senacamera.utils.imageloader.TutkUriUtil;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLImage;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.type.ICatchCodec;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PhotoDetailPresenter extends BasePresenter implements SensorEventListener {
    private static final String TAG = PhotoDetailPresenter.class.getSimpleName();
    private PhotoDetailView photoPbView;
    private Activity activity;
    //private List<RemoteMediaItemInfo> fileList;
    //private PhotoViewPagerAdapter viewPagerAdapter;
    private Handler handler;
    //private int curPhotoIdx;
    private int lastItem = -1;
    private int tempLastItem = -1;
    private boolean isScrolling = false;
    private static final int DIRECTION_RIGHT = 0x1;
    private static final int DIRECTION_LEFT = 0x2;
    private static final int DIRECTION_UNKNOWN = 0x4;

    public String downloadingFilename;
    public long downloadProgress;
    public long downloadingFileSize;
    private ExecutorService executor;
    private PanoramaPhotoPlayback panoramaPhotoPlayback = CameraManager.getInstance().getCurCamera().getPanoramaPhotoPlayback();
    private FileOperation fileOperation = CameraManager.getInstance().getCurCamera().getFileOperation();

    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2.2f;
    private static final float FIXED_OUTSIDE_FOCUS = 1.0f;
    private static final float FIXED_INSIDE_FOCUS = 2.0f;
    private static final float FIXED_NEAR_DISTANCE = 0.6f;

    private static final float FIXED_OUTSIDE_DISTANCE = 1 / MIN_ZOOM;
    private static final float FIXED_INSIDE_DISTANCE = 1 / MAX_ZOOM;
    private TouchMode touchMode = TouchMode.NONE;
    private float mPreviousY;
    private float mPreviousX;
    private float beforeLength;
    private float afterLength;
    private float currentZoomRate = MAX_ZOOM;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private String curFilePath = "";
    private int curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
    private boolean hasDeleted = false;
    private boolean surfaceCreated = false;
    private MediaItemInfo currentItemInfo;
    private CustomDownloadDialog customDownloadDialog;
    private Timer downloadProgressTimer;

    public PhotoDetailPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
        handler = new Handler();
    }

    public void setView(PhotoDetailView photoPbView) {
        this.photoPbView = photoPbView;
        initCfg();

        Bundle data = activity.getIntent().getExtras();
        if (data.getString("remoteMedia") == null) {
            currentItemInfo = LocalMediaItemInfo.deserialize(data.getString("localMedia"));
        } else {
            currentItemInfo = RemoteMediaItemInfo.deserialize(data.getString("remoteMedia"));
        }
    }

    public void loadPanoramaImage() {
        //int curIndex = photoPbView.getViewPagerCurrentItem();
        if (isCurrentItemLocal()) {
            AppLog.e(TAG, "loadPanoramaImage: currentItemInfo is not remote file");
            return;
        }

        loadPanoramaPhoto((RemoteMediaItemInfo) currentItemInfo);
    }

    public void initView() {
        CameraManager.getInstance().getCurCamera().setLoadThumbnail(true);
//        viewPagerAdapter = new PhotoViewPagerAdapter(activity, fileList);
//        viewPagerAdapter.setOnPhotoTapListener(new PhotoViewPagerAdapter.OnPhotoTapListener() {
//            @Override
//            public void onPhotoTap() {
//                showBar();
//            }
//        });
//        photoPbView.setViewPagerAdapter(viewPagerAdapter);
//        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
        updateUI();
//        photoPbView.setOnPageChangeListener(new MyViewPagerOnPagerChangeListener());
    }

    public void delete() {
        showDeleteEnsureDialog();
    }

    public void download() {
        if (isCurrentItemLocal()) {
            AppLog.e(TAG, "download: currentItemInfo is not remote file");
            return;
        }

        RemoteMediaItemInfo itemInfo = (RemoteMediaItemInfo) currentItemInfo;
        downloadProgress = 0;
        if (SystemInfo.getSDFreeSize(activity) < itemInfo.getFileSizeInteger()) {
            MyToast.show(activity, R.string.text_sd_card_memory_shortage);
        } else {
            downloadProgressTimer = new Timer();
            downloadProgressTimer.schedule(new PhotoDetailPresenter.DownloadProgressTask(), 500, 500);

            showDownloadManagerDialog();

            executor = Executors.newSingleThreadExecutor();
            executor.submit(new DownloadThread(), null);
        }
    }

    public void showDownloadManagerDialog() {
        customDownloadDialog = new CustomDownloadDialog(activity);
        customDownloadDialog.showDownloadDialog();
        customDownloadDialog.setBackBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                alertForQuitDownload();
            }
        });
        updateDownloadStatus();
    }

    public void alertForQuitDownload() {
        AppDialogManager.getInstance().showStopDownloadDialog(activity, new DialogButtonListener() {
            @Override
            public void onStop() {
                if (curFilePath != null) {
                    File file = new File(curFilePath);
                    if (!file.exists()) {
                        return;
                    }
                    if (file.delete()) {
                        AppLog.d(TAG, "alertForQuitDownload file delete success == " + curFilePath);
                    }
                }

                if (!fileOperation.cancelDownload()) {
                    Toast.makeText(activity, R.string.dialog_cancel_downloading_failed, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (customDownloadDialog != null) {
                        customDownloadDialog.dismissDownloadDialog();
                        customDownloadDialog = null;
                    }
                    if (downloadProgressTimer != null) {
                        downloadProgressTimer.cancel();
                    }
                    Toast.makeText(activity, R.string.dialog_cancel_downloading_succeeded, Toast.LENGTH_SHORT).show();
                }
                AppLog.d(TAG, "cancel download");
            }
        });
    }

    private void updateDownloadStatus() {
        int progress = (int) downloadProgress;
        String message = String.format("%d/1 %s(%d%%)", downloadProgress == 100? 1: 0, activity.getResources().getString(R.string.saving), progress);

        if (customDownloadDialog != null) {
            customDownloadDialog.setMessage(message);
            customDownloadDialog.setProgress(progress);
        }
    }

    public void loadPreviousImage() {
//        AppLog.d(TAG, "loadPreviousImage=");
//        if (curPhotoIdx > 0) {
//            curPhotoIdx--;
//        }
//        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }

    public void loadNextImage() {
//        AppLog.d(TAG, "loadNextImage=");
//        if (curPhotoIdx < fileList.size() - 1) {
//            curPhotoIdx++;
//        }
//        photoPbView.setViewPagerCurrentItem(curPhotoIdx);
    }

    public void back() {
        clearImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
        Intent intent = new Intent();
        intent.putExtra("hasDeleted", hasDeleted);
        intent.putExtra("fileType", FileType.FILE_PHOTO.ordinal());
        activity.setResult(1000, intent);
        activity.finish();
    }


    private class MyViewPagerOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
////            AppLog.d(TAG,"onPageScrollStateChanged arg0:" + arg0);
//            switch (arg0) {
//                case ViewPager.SCROLL_STATE_DRAGGING:
//                    isScrolling = true;
//                    tempLastItem = photoPbView.getViewPagerCurrentItem();
//                    break;
//                case ViewPager.SCROLL_STATE_SETTLING:
//                    if (isScrolling == true && tempLastItem != -1 && tempLastItem != photoPbView.getViewPagerCurrentItem()) {
//                        lastItem = tempLastItem;
//                    }
//
//                    curPhotoIdx = photoPbView.getViewPagerCurrentItem();
//                    isScrolling = false;
////                    updateUI();
//                    break;
//                case ViewPager.SCROLL_STATE_IDLE:
//                    break;
//
//                default:
//            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
//            AppLog.d(TAG,"onPageScrolled arg0:" + arg0 + " arg1:" + arg1 + " arg2:" + arg2);
//            if (isScrolling) {
//                if (lastItem > arg2) {
//                    // 递减，向右侧滑动
//                } else if (lastItem < arg2) {
//                    // 递减，向右侧滑动
//                } else if (lastItem == arg2) {
//                }
//            }
//            lastItem = arg2;
        }

        @Override
        public void onPageSelected(int arg0) {
//            AppLog.d(TAG, "onPageSelected arg0:" + arg0);
//            updateUI();
        }
    }

    private class DownloadThread implements Runnable {
        String TAG = DownloadThread.class.getSimpleName();
        //private int curIdx = photoPbView.getViewPagerCurrentItem();

        @Override
        public void run() {
            AppLog.d(TAG, "begin DownloadThread");
            AppInfo.isDownloading = true;
            RemoteMediaItemInfo itemInfo = (RemoteMediaItemInfo) currentItemInfo;
            String path = StorageUtil.getRootPath(activity) + AppInfo.DOWNLOAD_PATH_PHOTO;
            String fileName = itemInfo.getFileName();

            AppLog.d(TAG, "------------fileName = " + fileName);
            FileOper.createDirectory(path);
            downloadingFilename = path + fileName;
            downloadingFileSize = itemInfo.iCatchFile.getFileSize();
            File tempFile = new File(downloadingFilename);
            curFilePath = FileTools.chooseUniqueFilename(downloadingFilename);
            boolean temp = fileOperation.downloadFile(itemInfo.iCatchFile, curFilePath);

            //ICOM-4116 End modify by b.jiang 20170315
            if (!temp) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (customDownloadDialog != null) {
                            customDownloadDialog.dismissDownloadDialog();
                            customDownloadDialog = null;
                        }
                        if (downloadProgressTimer != null) {
                            downloadProgressTimer.cancel();
                        }
                        MyToast.show(activity, R.string.message_download_failed);
                    }
                });
                AppInfo.isDownloading = false;
                return;
            }

            MediaRefresh.scanFileAsync(activity, curFilePath);
            AppLog.d(TAG, "end downloadFile temp = " + temp);
            AppInfo.isDownloading = false;
            final String message = activity.getResources().getString(R.string.message_download_to).replace("$1$", curFilePath);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (customDownloadDialog != null) {
                        customDownloadDialog.dismissDownloadDialog();
                        customDownloadDialog = null;
                    }

                    MyToast.show(activity, message);
                }
            });
            AppLog.d(TAG, "end DownloadThread");
        }
    }

    private class DeleteThread implements Runnable {
        @Override
        public void run() {
//            curPhotoIdx = photoPbView.getViewPagerCurrentItem();
//            ICatchFile curFile = fileList.get(curPhotoIdx).iCatchFile;
            boolean retValue;
            if (isCurrentItemLocal()) {
                // local
                File curFile = ((LocalMediaItemInfo) currentItemInfo).file;
                retValue = curFile.exists() && curFile.delete();
            } else {
                // remote
                ICatchFile curFile = ((RemoteMediaItemInfo) currentItemInfo).iCatchFile;
                retValue = fileOperation.deleteFile(curFile);
            }
            if (!retValue) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        MyToast.show(activity, R.string.dialog_delete_failed_single);
                    }
                });
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        back();
                    }
                });
            }
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    class DownloadProgressTask extends TimerTask {
        String TAG = PhotoDetailPresenter.DownloadProgressTask.class.getSimpleName();
        long lastTime = 0;

        @Override
        public void run() {
            final ICatchFile iCatchFile = ((RemoteMediaItemInfo) currentItemInfo).iCatchFile;
            File file = new File(curFilePath);
            AppLog.d(TAG, "filename: " + file + ", iCatchFile name: " + iCatchFile.getFileName() + ", fileHandle: " + iCatchFile.getFileHandle());

            if (file != null) {
                long fileLength = file.length();
                if (fileLength == iCatchFile.getFileSize()) {
                    downloadProgress = 100;
                } else {
                    downloadProgress = file.length() * 100 / iCatchFile.getFileSize();
                }
            } else {
                downloadProgress = 0;
            }

            AppLog.d(TAG, "downloadProgress = " + downloadProgress);
            activity.runOnUiThread(() -> {
                updateDownloadStatus();
            });
        }
    }

    private void updateUI() {
//        int curIndex = photoPbView.getViewPagerCurrentItem();
//        String indexInfo = (curIndex + 1) + "/" + fileList.size();
//        photoPbView.setTitleText(indexInfo);
//        RemoteMediaItemInfo itemInfo = fileList.get(curIndex);

        if (isCurrentItemLocal()) {
            // local
            photoPbView.setTitleText(((LocalMediaItemInfo) currentItemInfo).file.getName());
        } else {
            // remote
            photoPbView.setTitleText(((RemoteMediaItemInfo) currentItemInfo).iCatchFile.getFileName());
        }

        if (isCurrentItemLocal()) {
            AppLog.e(TAG, "updateUI: currentItemInfo is not remote file");
            return;
        }

        RemoteMediaItemInfo itemInfo = (RemoteMediaItemInfo) currentItemInfo;
        photoPbView.setSurfaceViewVisibility(itemInfo.isPanorama()? View.VISIBLE: View.GONE);
        photoPbView.setViewPagerVisibility(itemInfo.isPanorama()? View.GONE: View.VISIBLE);
        if (itemInfo.isPanorama() && surfaceCreated) {
            loadPanoramaPhoto(itemInfo);
        }
    }

    public void showDeleteEnsureDialog() {
        // show delete confirmation dialog
        AppDialogManager.getInstance().showDeleteConfirmDialog(activity, new DialogButtonListener() {
            @Override
            public void onDelete() {
                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                hasDeleted = true;
                executor.submit(new DeleteThread(), null);
            }
        }, activity.getResources().getString(R.string.dialog_confirm_delete_this_file));
    }

    public void setShowArea(Surface surface) {
        AppLog.d(TAG, "start initSurface");
        iCatchSurfaceContext = new ICatchSurfaceContext(surface);
        panoramaPhotoPlayback.setSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);
        AppLog.d(TAG, "end initSurface");
        surfaceCreated = true;
    }

    public void insidePanorama() {
        locate(FIXED_INSIDE_DISTANCE);
    }


    private void locate(float distance) {
        panoramaPhotoPlayback.pancamGLTransLocate(distance);
    }


    public void clearImage(int iCatchSphereType) {
        surfaceCreated = false;
        removeGyroscopeListener();
        if (panoramaPhotoPlayback != null) {
            if (iCatchSurfaceContext != null) {
                panoramaPhotoPlayback.removeSurface(iCatchSphereType, iCatchSurfaceContext);
                iCatchSurfaceContext = null;
            }
            panoramaPhotoPlayback.clear();
        }
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        ICatchGLPoint prev = new ICatchGLPoint(prevX, prevY);
        ICatchGLPoint curr = new ICatchGLPoint(e.getX(), e.getY());
        panoramaPhotoPlayback.pancamGLTransformRotate(prev, curr);
    }

    public void onSurfaceViewTouchDown(MotionEvent event) {
        touchMode = TouchMode.DRAG;
        mPreviousY = event.getY();// 记录触控笔位置
        mPreviousX = event.getX();// 记录触控笔位置
        beforeLength = 0;
        afterLength = 0;
    }

    public void onSurfaceViewPointerDown(MotionEvent event) {
        Log.d("2222", "event.getPointerCount()................=" + event.getPointerCount());
        if (event.getPointerCount() == 2) {
            touchMode = TouchMode.ZOOM;
            beforeLength = getDistance(event);// 获取两点的距离
        }
    }

    public void onSurfaceViewTouchMove(MotionEvent event) {
        if (touchMode == TouchMode.DRAG) {
            rotateB(event, mPreviousX, mPreviousY);
            mPreviousY = event.getY();// 记录触控笔位置
            mPreviousX = event.getX();// 记录触控笔位置
        }
        /** 处理缩放 **/
        else if (touchMode == TouchMode.ZOOM) {
            afterLength = getDistance(event);// 获取两点的距离

            float gapLenght = afterLength - beforeLength;// 变化的长度

            if (Math.abs(gapLenght) > 5f) {
                float scale_temp = afterLength / beforeLength;// 求的缩放的比例
                this.setScale(scale_temp);
                beforeLength = afterLength;
            }
        }
    }

    /**
     * 获取两点的距离
     **/
    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) StrictMath.sqrt(x * x + y * y);
    }

    /**
     * 处理缩放
     **/
    void setScale(float scale) {
        if ((currentZoomRate >= MAX_ZOOM && scale > 1) || (currentZoomRate <= MIN_ZOOM && scale < 1)) {
            return;
        }
        float temp = currentZoomRate * scale;
        if (scale > 1) {
            if (temp <= MAX_ZOOM) {
                currentZoomRate = currentZoomRate * scale;
                zoom(currentZoomRate);
            } else {
                currentZoomRate = MAX_ZOOM;
                zoom(currentZoomRate);
            }
        } else if (scale < 1) {
            if (temp >= MIN_ZOOM) {
                currentZoomRate = currentZoomRate * scale;
                zoom(currentZoomRate);
            } else {
                currentZoomRate = MIN_ZOOM;
                zoom(currentZoomRate);
            }
        }

    }

    private void zoom(float currentZoomRate) {
        locate(1 / currentZoomRate);
    }

    public void onSurfaceViewTouchUp() {
        touchMode = TouchMode.NONE;
    }

    public void onSurfaceViewTouchPointerUp() {
        touchMode = TouchMode.NONE;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // 从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            float speedX = event.values[0];
            float speedY = event.values[1];
            float speedZ = event.values[2];
            if (Math.abs(speedY) < 0.02 && Math.abs(speedZ) < 0.02) {
                return;
            }
            rotate(speedX, speedY, speedZ, event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private void rotate(float speedX, float speedY, float speedZ, long timestamp) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        panoramaPhotoPlayback.pancamGLTransformRotate(rotation, speedX, speedY, speedZ, timestamp);
    }

    private void registerGyroscopeSensor() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        // 注册陀螺仪传感器，并设定传感器向应用中输出的时间间隔类型是SensorManager.SENSOR_DELAY_GAME(20000微秒)
        // SensorManager.SENSOR_DELAY_FASTEST(0微秒)：最快。最低延迟，一般不是特别敏感的处理不推荐使用，该模式可能在成手机电力大量消耗，由于传递的为原始数据，诉法不处理好会影响游戏逻辑和UI的性能
        // SensorManager.SENSOR_DELAY_GAME(20000微秒)：游戏。游戏延迟，一般绝大多数的实时性较高的游戏都是用该级别
        // SensorManager.SENSOR_DELAY_NORMAL(200000微秒):普通。标准延时，对于一般的益智类或EASY级别的游戏可以使用，但过低的采样率可能对一些赛车类游戏有跳帧现象
        // SensorManager.SENSOR_DELAY_UI(60000微秒):用户界面。一般对于屏幕方向自动旋转使用，相对节省电能和逻辑处理，一般游戏开发中不使用
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void removeGyroscopeListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    public void setDrawingArea(int windowW, int windowH) {
        AppLog.d(TAG, "start setDrawingArea windowW= " + windowW + " windowH= " + windowH);
        if (iCatchSurfaceContext != null) {
            try {
                iCatchSurfaceContext.setViewPort(0, 0, windowW, windowH);
            } catch (IchGLSurfaceNotSetException e) {
                e.printStackTrace();
            }
        }
        AppLog.d(TAG, "end setDrawingArea");
    }

    private void loadPanoramaPhoto(final RemoteMediaItemInfo itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (itemInfo.isPanorama()) {
            String url = TutkUriUtil.getTutkOriginalUri(itemInfo.iCatchFile);
            ImageLoaderUtil.loadImage(url, new ImageLoaderUtil.OnLoadListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    AppLog.d(TAG, "onLoadingStarted imageUri:" + imageUri);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view) {
                    AppLog.d(TAG, "onLoadingFailed imageUri:" + imageUri);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    AppLog.d(TAG, "onLoadingComplete imageUri:" + imageUri);
                    ICatchFile iCatchFile = TutkUriUtil.getInfoOfUri(imageUri);
                    if (loadedImage != null && iCatchFile != null && iCatchFile.getFileHandle() == itemInfo.getFileHandle()) {
                        photoPbView.setViewPagerVisibility(View.GONE);
                        panoramaPhotoPlayback.pancamGLSetFormat(ICatchCodec.ICH_CODEC_BITMAP, loadedImage.getWidth(), loadedImage.getHeight());
                        panoramaPhotoPlayback.update(new ICatchGLImage(loadedImage));
                        registerGyroscopeSensor();
                        insidePanorama();
                    }
                }
            });
        }
    }

    public void release() {
        if (panoramaPhotoPlayback != null) {
            panoramaPhotoPlayback.release();
        }
    }

    public void initPanorama() {
        panoramaPhotoPlayback.pancamGLInit();
    }

    public void setPanoramaType() {
        if (isCurrentItemLocal() || !((RemoteMediaItemInfo) currentItemInfo).isPanorama()) {
            MyToast.show(activity, R.string.non_360_picture_not_support_switch);
            return;
        }
        if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE) {
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            photoPbView.setPanoramaTypeTxv(R.string.text_asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID) {
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            photoPbView.setPanoramaTypeTxv(R.string.text_vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            panoramaPhotoPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            photoPbView.setPanoramaTypeTxv(R.string.text_panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        loadPanoramaImage();
    }

    public void setSdCardEventListener() {
        GlobalInfo.getInstance().setOnEventListener(new GlobalInfo.OnEventListener() {
            @Override
            public void eventListener(int sdkEventId) {
                switch (sdkEventId) {
                    case SDKEvent.EVENT_SDCARD_REMOVED:
                        RemoteFileHelper.getInstance().clearAllFileList();
                        AppDialog.showDialogWarn(activity, R.string.dialog_card_removed_and_back_photo_pb, false,new AppDialog.OnDialogSureClickListener() {
                            @Override
                            public void onSure() {
                                back();
                            }
                        });
                        break;
//                    case SDKEvent.EVENT_SDCARD_INSERT:
//                        MyToast.show(activity,R.string.dialog_card_inserted);
//                        break;
                }
            }
        });
    }

    public MediaItemInfo getCurrentItemInfo() {
        return currentItemInfo;
    }

    public boolean isCurrentItemPanorama() {
        if (isCurrentItemLocal()) {
            return false;
        }

        return ((RemoteMediaItemInfo) currentItemInfo).isPanorama();
    }

    public boolean isCurrentItemLocal() {
        return currentItemInfo.mediaItemType.equals(MediaItemType.LOCAL);
    }
}


