package com.sena.senacamera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.sena.senacamera.data.entity.LocalMediaItemInfo;
import com.sena.senacamera.data.entity.MediaItemInfo;
import com.sena.senacamera.data.type.MediaItemType;
import com.sena.senacamera.function.SDKEvent;
import com.sena.senacamera.function.streaming.VideoStreaming;
import com.sena.senacamera.listener.DialogButtonListener;
import com.sena.senacamera.listener.VideoFramePtsChangedListener;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.presenter.Interface.BasePresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.FileOperation;
import com.sena.senacamera.SdkApi.PanoramaVideoPlayback;
import com.sena.senacamera.SdkApi.StreamStablization;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.data.Message.AppMessage;
import com.sena.senacamera.data.Mode.TouchMode;
import com.sena.senacamera.data.Mode.VideoPbMode;
import com.sena.senacamera.data.SystemInfo.SystemInfo;
import com.sena.senacamera.data.entity.RemoteMediaItemInfo;
import com.sena.senacamera.data.type.FileType;
import com.sena.senacamera.ui.activity.MediaVideoDetailActivity;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.ui.appdialog.CustomDownloadDialog;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.ui.Interface.VideoDetailView;
import com.sena.senacamera.ui.RemoteFileHelper;
import com.sena.senacamera.ui.appdialog.AppDialog;
import com.sena.senacamera.utils.ConvertTools;
import com.sena.senacamera.utils.MediaRefresh;
import com.sena.senacamera.utils.PanoramaTools;
import com.sena.senacamera.utils.StorageUtil;
import com.sena.senacamera.utils.fileutils.FileOper;
import com.sena.senacamera.utils.fileutils.FileTools;
import com.icatchtek.pancam.customer.exception.IchGLFormatNotSupportedException;
import com.icatchtek.pancam.customer.type.ICatchGLEventID;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.reliant.customer.type.ICatchFile;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yh.zhang on 2016/9/14.
 */
public class VideoDetailPresenter extends BasePresenter implements SensorEventListener {
    private static final String TAG = VideoDetailPresenter.class.getSimpleName();
    private VideoDetailView videoDetailView;
    private Activity activity;
    private FileOperation fileOperation;
    private VideoPbMode videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
    private boolean needUpdateSeekBar = true;
    private ICatchFile curRemoteVideoFile;
    private File curLocalVideoFile;
    private VideoPbHandler handler = new VideoPbHandler();
    private boolean cacheFlag = false;
    private Boolean waitForCaching = false;
    private double currentTime = -1.0;
    private int videoDuration = 0;
    private int lastSeekBarPosition;
    private static final float MIN_ZOOM = 0.5f;
    private static final float MAX_ZOOM = 2.2f;

    private static final float FIXED_OUTSIDE_DISTANCE = 1 / MIN_ZOOM;
    private static final float FIXED_INSIDE_DISTANCE = 1 / MAX_ZOOM;
    private PanoramaVideoPlayback panoramaVideoPlayback;
    private TouchMode touchMode = TouchMode.NONE;
    private float mPreviousY;
    private float mPreviousX;
    private float beforeLenght;
    private float afterLenght;
    private float currentZoomRate = MAX_ZOOM;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    //private int curVideoPosition;
    private ExecutorService executor;
    protected Timer downloadProgressTimer;
    //private List<RemoteMediaItemInfo> fileList;
    private CustomDownloadDialog customDownloadDialog;
    private SDKEvent sdkEvent;
    private VideoStreaming videoStreaming;
    private boolean enableRender = AppInfo.enableRender;
    private int curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
    private FileType fileType;
    private boolean hasDeleted = false;
    private MediaItemInfo currentItemInfo;
    private MediaPlayer localMediaPlayer;
    private Handler seekBarUpdateHandler = new Handler(Looper.getMainLooper());
    private int localPausePosition = 0;
    public long downloadProgress;
    private String downloadFilePath = "";

    public VideoDetailPresenter(Activity activity) {
        super(activity);

        this.activity = activity;
        Intent intent = activity.getIntent();
        Bundle data = intent.getExtras();
        //curVideoPosition = data.getInt("curfilePosition");
        int fileTypeInt = data.getInt("fileType");
        fileType = FileType.values()[fileTypeInt];

        if (data.getString("remoteMedia") == null) {
            // local
            currentItemInfo = LocalMediaItemInfo.deserialize(data.getString("localMedia"));
            curLocalVideoFile = ((LocalMediaItemInfo) currentItemInfo).file;
        } else {
            // remote
            currentItemInfo = RemoteMediaItemInfo.deserialize(data.getString("remoteMedia"));
            curRemoteVideoFile = ((RemoteMediaItemInfo) currentItemInfo).iCatchFile;
        }
//        fileList = RemoteFileHelper.getInstance().getLocalFileList(fileType);
//        if (fileList != null && fileList.isEmpty() == false) {
//            this.curVideoFile = fileList.get(curVideoPosition).iCatchFile;
//        }
//        AppLog.i(TAG, "cur video fileType=" + fileType + " position=" + curVideoPosition + " video name=" + curVideoFile.getFileName());

        initClint();
    }

    @Override
    public void isAppBackground() {
        stopVideoStream();
        super.isAppBackground();
    }

    public void updatePbSeekbar(double pts) {
        if (videoPbMode != VideoPbMode.MODE_VIDEO_PLAY || !needUpdateSeekBar) {
            return;
        }
        currentTime = pts;
        int temp = new Double(currentTime * 100).intValue();
        videoDetailView.setSeekBarProgress(temp);
        //handler.obtainMessage(AppMessage.MESSAGE_UPDATE_VIDEOPB_BAR, temp, 0).sendToTarget();
    }

    public void setView(VideoDetailView VideoDetailView) {
        this.videoDetailView = VideoDetailView;
        initCfg();
        initView();
    }

    private void initView() {
        String videoName;
        if (isCurrentItemLocal()) {
            videoName = curLocalVideoFile.getName();
        } else {
            String fileName = curRemoteVideoFile.getFileName();
            int start = fileName.lastIndexOf("/");
            videoName = fileName.substring(start + 1);
        }
        videoDetailView.setVideoNameTxv(videoName);

        if (isCurrentItemLocal()) {
            AppLog.e(TAG, "initView: currentItemInfo is not remote file");
            return;
        }

        if (enableRender && PanoramaTools.isPanorama(curRemoteVideoFile.getFileWidth(), curRemoteVideoFile.getFileHeight())) {
            videoDetailView.setPanoramaTypeBtnVisibility(View.VISIBLE);
        } else {
            videoDetailView.setPanoramaTypeBtnVisibility(View.GONE);
        }
    }

    public void initClint() {
        MyCamera camera = CameraManager.getInstance().getCurCamera();
        panoramaVideoPlayback = camera.getPanoramaVideoPlayback();
        fileOperation = camera.getFileOperation();
        videoStreaming = new VideoStreaming(panoramaVideoPlayback);
//        panoramaVideoPlayback.enableGLRender();
    }

    public void addEventListener() {
        if (panoramaVideoPlayback == null) {
            return;
        }
        if (sdkEvent == null) {
            sdkEvent = new SDKEvent(handler);
        }
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED);
//        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_STREAM_CLOSED);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS);
        sdkEvent.addPanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION);
    }

    public void removeEventListener() {
        if (panoramaVideoPlayback == null) {
            return;
        }
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_STATUS);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_PLAYING_ENDED);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_CHANGED);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_PLAYBACK_CACHING_PROGRESS);
        sdkEvent.removePanoramaEventListener(ICatchGLEventID.ICH_GL_EVENT_VIDEO_STREAM_NO_EIS_INFORMATION);
    }

    public void play() {
        // TODO Auto-generated method stub
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            if (isCurrentItemLocal()) {
                localMediaPlayer.start();
                setSeekBarUpdateHandler();
            } else {
                AppLog.i(TAG, "start play video enableRender:" + enableRender);
                addEventListener();
                if (enableRender) {
                    registerGyroscopeSensor();
                }
                if (!enableRender) {
                    videoStreaming.setFramePtsChangedListener(new VideoFramePtsChangedListener() {
                        @Override
                        public void onFramePtsChanged(double pts) {
                            updatePbSeekbar(pts);
                        }
                    });
                }
                boolean ret;
                try {
                    ret = videoStreaming.play(curRemoteVideoFile, false, true);
                } catch (IchGLFormatNotSupportedException e) {
                    e.printStackTrace();
                    MyToast.show(activity, R.string.video_format_not_support);
                    AppLog.e(TAG, "failed to startPlaybackStream");
                    return;
                }

                if (!ret) {
                    MyToast.show(activity, R.string.dialog_failed);
                    AppLog.e(TAG, "failed to startPlaybackStream");
                    return;
                }
            }

            videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
            videoDetailView.showLoadingCircle(true);
            cacheFlag = true;
            waitForCaching = true;
            needUpdateSeekBar = true;
            AppLog.i(TAG, "seekBar.getProgress() =" + videoDetailView.getSeekBarProgress());
            videoDetailView.setPlayBtnSrc(R.drawable.selector_button_pause);
            videoDetailView.setSeekbarEnabled(true);
            videoDetailView.setTimeLapsedValue("00:00");

            // set duration
            if (isCurrentItemLocal()) {
                videoDuration = localMediaPlayer.getDuration() / 10;
            } else {
                videoDuration = panoramaVideoPlayback.getLength();
            }
            AppLog.i(TAG, "end getLength = " + videoDuration);
            videoDetailView.setTimeDurationValue(ConvertTools.secondsToMinuteOrHours(videoDuration / 100));

            videoDetailView.setSeekBarMaxValue(videoDuration);
            videoDetailView.setDownloadBtnEnabled(false);
            // temp attempt to avoid sdk
//            startVideoPb();
            AppLog.i(TAG, "has start the GetVideoFrameThread() to get play video");
        } else if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
            resumeVideoPb();
        } else if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            pauseVideoPb();
        }
    }

    private void resumeVideoPb() {
        AppLog.i(TAG, "mode == MODE_VIDEO_PAUSE");
        if (isCurrentItemLocal()) {
            // local
            localMediaPlayer.seekTo(localPausePosition);
            localMediaPlayer.start();
            setSeekBarUpdateHandler();
        } else {
            // remote
            if (!panoramaVideoPlayback.resumePlayback()) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.i(TAG, "failed to resumePlayback");
                return;
            }
        }

//            needUpdateSeekBar = true;
        videoDetailView.setPlayBtnSrc(R.drawable.selector_button_pause);
        videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
        videoDetailView.setDownloadBtnEnabled(false);
    }

    private void pauseVideoPb() {
        AppLog.i(TAG, "begin pause the playing");
        if (isCurrentItemLocal()) {
            // local
            localPausePosition = localMediaPlayer.getCurrentPosition();
            localMediaPlayer.pause();
        } else {
            // remote
            if (!panoramaVideoPlayback.pausePlayback()) {
                MyToast.show(activity, R.string.dialog_failed);
                AppLog.i(TAG, "failed to pausePlayback");
                return;
            }
        }

//        removeEventListener();
//        removeGyroscopeListener();
        videoDetailView.setPlayBtnSrc(R.drawable.selector_button_play);
        videoPbMode = VideoPbMode.MODE_VIDEO_PAUSE;
        videoDetailView.showLoadingCircle(false);
        videoDetailView.setDownloadBtnEnabled(true);
    }

    private void setupLocalMediaPlayer() {
        try {
            localMediaPlayer.setDataSource(curLocalVideoFile.getPath());
            localMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSeekBarUpdateHandler() {
        seekBarUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (localMediaPlayer != null && localMediaPlayer.isPlaying()) {
                    updatePbSeekbar((double) localMediaPlayer.getCurrentPosition() / 1000);
                    seekBarUpdateHandler.postDelayed(this, 100);
                }
            }
        }, 100);
    }

    public void seekBarOnStopTrackingTouch() {
        AppLog.d(TAG, "seekBarOnStopTrackingTouch lastSeekBarPosition=" + lastSeekBarPosition + " videoDuration=" + videoDuration);
        int curProgress = videoDetailView.getSeekBarProgress();
        if (videoDuration - curProgress < 500) {
            videoDetailView.setSeekbarEnabled(false);
        }
        if (videoDuration - curProgress < 100) {
            lastSeekBarPosition = videoDuration - 100;
            videoDetailView.setSeekBarProgress(lastSeekBarPosition);
        } else {
            lastSeekBarPosition = curProgress;
        }
        videoDetailView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(lastSeekBarPosition / 100));

        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isCurrentItemLocal()) {
                    // local
                    localMediaPlayer.seekTo(lastSeekBarPosition * 10);
                    localMediaPlayer.start();
                    setSeekBarUpdateHandler();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            videoDetailView.setPlayBtnSrc(R.drawable.selector_button_pause);
                            videoDetailView.setDownloadBtnEnabled(false);
                            videoDetailView.showLoadingCircle(true);
                        }
                    });
                } else {
                    // remote
                    if (panoramaVideoPlayback.videoSeek(lastSeekBarPosition / 100.0)) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
//                    if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
                        panoramaVideoPlayback.resumePlayback();
//                    resumeVideoPb();
//                    }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                videoDetailView.setPlayBtnSrc(R.drawable.selector_button_pause);
//                            VideoDetailView.setPlayCircleImageViewVisibility(View.GONE);
//                            VideoDetailView.setDeleteBtnEnabled(false);
                                videoDetailView.setDownloadBtnEnabled(false);
                                videoDetailView.showLoadingCircle(true);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                MyProgressDialog.closeProgressDialog();
                                videoDetailView.setSeekBarProgress(lastSeekBarPosition);
                                MyToast.show(activity, R.string.dialog_failed);
                            }
                        });
                    }
                }

                videoPbMode = VideoPbMode.MODE_VIDEO_PLAY;
                needUpdateSeekBar = true;
            }
        }).start();

    }

//    public void seekBarOnStopTrackingTouch() {
//        needUpdateSeekBar = false;
//        lastSeekBarPosition = VideoDetailView.getSeekBarProgress();
//        //VideoDetailView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(lastSeekBarPosition / 100));
//        if (panoramaVideoPlayback.videoSeek(lastSeekBarPosition / 100.0)) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        } else {
//            VideoDetailView.setSeekBarProgress(lastSeekBarPosition);
//            MyToast.show(activity, R.string.dialog_failed);
//        }
//    }

    public void seekBarOnStartTrackingTouch() {
        needUpdateSeekBar = false;
        lastSeekBarPosition = videoDetailView.getSeekBarProgress();

        if (isCurrentItemLocal()) {
            // local
            localMediaPlayer.pause();
        } else {
            // remote
            panoramaVideoPlayback.pausePlayback();
        }

        videoDetailView.showLoadingCircle(false);
        videoPbMode = VideoPbMode.MODE_VIDEO_PAUSE;
    }

    public void setTimeLapsedValue(int progress) {
        if (needUpdateSeekBar && videoDuration > 0 && (videoDuration - progress) < 500) {
            AppLog.i(TAG, "setTimeLapsedValue setSeekbarEnabled");
            videoDetailView.setSeekbarEnabled(false);
        }
        videoDetailView.setTimeLapsedValue(ConvertTools.secondsToMinuteOrHours(progress / 100));
    }

    public void initSurface(SurfaceHolder surfaceHolder) {
        AppLog.i(TAG, "begin initSurface");
        if (isCurrentItemLocal()) {
            try {
                localMediaPlayer = new MediaPlayer();
                localMediaPlayer.setDataSource(curLocalVideoFile.getPath());
                localMediaPlayer.setDisplay(surfaceHolder);
                localMediaPlayer.prepare();

                localMediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                    if (width == 0 || height == 0) {
                        return;
                    }

                    ((MediaVideoDetailActivity) activity).adjustSurfaceViewAspectRatio(width, height);
                });

                localMediaPlayer.setOnCompletionListener(mp -> {
                    stopVideoStream();
                    videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
                    videoDetailView.setProgress(0);
                });
            } catch (Exception e) {
                AppLog.e(TAG, "initSurface error: " + e.getMessage());
            }
        } else {
            videoStreaming.initSurface(enableRender, surfaceHolder, curRemoteVideoFile.getFileWidth(), curRemoteVideoFile.getFileHeight());
            if (enableRender) {
                locate(FIXED_INSIDE_DISTANCE);
            }
            if (!enableRender) {
                int width = videoDetailView.getSurfaceViewWidth();
                int height = videoDetailView.getSurfaceViewHeight();
                AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + height);
                if (width <= 0 || height <= 0) {
                    width = 1080;
                    height = 1920;
                }
                videoStreaming.setViewParam(width, height);
            }
//        iCatchSurfaceContext = new ICatchSurfaceContext(surface);
//        panoramaVideoPlayback.setSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);
        }
        AppLog.i(TAG, "end initSurface");
    }

    public void stopVideoStream() {
        AppLog.i(TAG, "Begin stopVideoStream");
//        ICatchPancamConfig.getInstance().disableDumpTransportStream(true);
        if (isCurrentItemLocal()) {
            if (localMediaPlayer != null) {
                localMediaPlayer.stop();
                localMediaPlayer.reset();
                setupLocalMediaPlayer();
            }
        } else {
            removeEventListener();
            if (enableRender) {
                removeGyroscopeListener();
            }
            videoStreaming.stop();
        }

        videoDetailView.setTimeLapsedValue("00:00");
        videoDetailView.setPlayBtnSrc(R.drawable.selector_button_play);
        videoDetailView.setSeekBarProgress(0);
        videoDetailView.setSeekBarSecondProgress(0);
//        videoDetailView.setTopBarVisibility(View.VISIBLE);
//        videoDetailView.setBottomBarVisibility(View.VISIBLE);
        videoDetailView.showLoadingCircle(false);
        videoDetailView.setSeekbarEnabled(false);
        videoDetailView.setDownloadBtnEnabled(true);
        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
        AppLog.i(TAG, "End stopVideoStream");
    }

    public void locate(float progress) {
        if (enableRender) {
            panoramaVideoPlayback.locate(progress);
        }
    }

    public void destroyVideo(int iCatchSphereType) {
        if (isCurrentItemLocal()) {
            if (localMediaPlayer != null) {
                localMediaPlayer.stop();
                localMediaPlayer.release();
                localMediaPlayer = null;
                seekBarUpdateHandler.removeCallbacksAndMessages(null);
                seekBarUpdateHandler = null;
            }
        } else {
            removeEventListener();
            if (enableRender) {
                removeGyroscopeListener();
            }
            videoStreaming.removeSurface(iCatchSphereType);
            videoStreaming.stop();
            videoStreaming.release();
        }
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        ICatchGLPoint prev = new ICatchGLPoint(prevX, prevY);
        ICatchGLPoint curr = new ICatchGLPoint(e.getX(), e.getY());
        panoramaVideoPlayback.rotate(prev, curr);
    }

    public void delete() {
        //ICOM-4097 ADD by b.jiang 20170112
        if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            pauseVideoPb();
        }
        showDeleteEnsureDialog();
    }

    public void download() {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY) {
            pauseVideoPb();
        }

        if (isCurrentItemLocal()) {
            AppLog.e(TAG, "download: currentItemInfo is not remote file");
            return;
        }

        if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
            stopVideoStream();
        }

        downloadProgress = 0;
        if (SystemInfo.getSDFreeSize(activity) < curRemoteVideoFile.getFileSize()) {
            MyToast.show(activity, R.string.text_sd_card_memory_shortage);
        } else {
            executor = Executors.newSingleThreadExecutor();
            String path = StorageUtil.getRootPath(activity) + AppInfo.DOWNLOAD_PATH_VIDEO;
            String fileName = curRemoteVideoFile.getFileName();
            AppLog.d(TAG, "------------fileName = " + fileName);
            FileOper.createDirectory(path);
            downloadFilePath = FileTools.chooseUniqueFilename(path + fileName);
            executor.submit(new DownloadThread(downloadFilePath), null);

            downloadProgressTimer = new Timer();
            downloadProgressTimer.schedule(new DownloadProgressTask(downloadFilePath), 500, 1000);

            showDownloadManagerDialog();
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

    private void updateDownloadStatus() {
        int progress = (int) downloadProgress;
        String message = String.format("%d/1 %s(%d%%)", downloadProgress == 100? 1: 0, activity.getResources().getString(R.string.saving), progress);

        if (customDownloadDialog != null) {
            customDownloadDialog.setMessage(message);
            customDownloadDialog.setProgress(progress);
        }
    }

    public void alertForQuitDownload() {
        AppDialogManager.getInstance().showStopDownloadDialog(activity, new DialogButtonListener() {
            @Override
            public void onStop() {
                if (downloadFilePath != null) {
                    File file = new File(downloadFilePath);
                    if (!file.exists()) {
                        return;
                    }
                    if (file.delete()) {
                        AppLog.d(TAG, "alertForQuitDownload file delete success == " + downloadFilePath);
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

    public void back() {
        stopVideoStream();
        Intent intent = new Intent();
        intent.putExtra("hasDeleted", hasDeleted);
        intent.putExtra("fileType", fileType.ordinal());
        activity.setResult(1000, intent);
        activity.finish();
    }

    private class VideoPbHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppMessage.EVENT_CACHE_STATE_CHANGED:
//                    AppLog.i( TAG, "EVENT_CACHE_STATE_CHANGED ---------msg.arg1 = " + msg.arg1 );
                    if (!cacheFlag) {
                        return;
                    }
                    if (msg.arg1 == 1) {
                        videoDetailView.showLoadingCircle(true);
                        waitForCaching = true;
                    } else if (msg.arg1 == 2) {
                        videoDetailView.showLoadingCircle(false);
                        waitForCaching = false;
                        needUpdateSeekBar = true;
                    }
                    break;

                case AppMessage.EVENT_CACHE_PROGRESS_NOTIFY:
//                    AppLog.i( TAG, "receive EVENT_CACHE_PROGRESS_NOTIFY msg.arg1=" + msg.arg1 + "waitForCaching=" + waitForCaching );
                    if (!cacheFlag) {
                        return;
                    }
                    if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE || videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                        return;
                    }
                    if (waitForCaching) {
                        videoDetailView.setLoadPercent(msg.arg1);
                    }
                    videoDetailView.setSeekBarSecondProgress(msg.arg2);
                    break;

                case SDKEvent.EVENT_VIDEO_PLAY_PTS:
                    double temp = (double) msg.obj;
//                    AppLog.i( TAG, "EVENT_VIDEO_PLAY_PTS ---------temp = " + temp );
                    updatePbSeekbar(temp);
                    break;
                case SDKEvent.EVENT_VIDEO_PLAY_CLOSED:
                    AppLog.i(TAG, "receive EVENT_VIDEO_PLAY_CLOSED");
                    removeEventListener();
                    removeGyroscopeListener();
                    if (videoPbMode == VideoPbMode.MODE_VIDEO_PLAY || videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                        cacheFlag = false;
                        stopVideoStream();
                        videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
                        videoDetailView.setProgress(0);
                    }
                    break;
                case AppMessage.MESSAGE_CANCEL_VIDEO_DOWNLOAD:
                    AppLog.d(TAG, "receive CANCEL_VIDEO_DOWNLOAD_SUCCESS");
                    if (customDownloadDialog != null) {
                        customDownloadDialog.dismissDownloadDialog();
                        customDownloadDialog = null;
                    }
                    if (downloadProgressTimer != null) {
                        downloadProgressTimer.cancel();
                    }
                    if (!fileOperation.cancelDownload()) {
                        MyToast.show(activity, R.string.dialog_cancel_downloading_failed);
                        break;
                    }
                    try {
                        Thread.currentThread().sleep(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String filePath = StorageUtil.getRootPath(activity) + AppInfo.DOWNLOAD_PATH_VIDEO + curRemoteVideoFile.getFileName();
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    videoPbMode = VideoPbMode.MODE_VIDEO_IDLE;
                    MyToast.show(activity, R.string.dialog_cancel_downloading_succeeded);
                    break;
                case AppMessage.MESSAGE_VIDEO_STREAM_NO_EIS_INFORMATION:
                    enableEIS(false);
                    videoDetailView.setEisSwitchChecked(false);
                    break;
            }
        }
    }

    private class DownloadThread implements Runnable {
        String TAG = DownloadThread.class.getSimpleName();
        String fileType;
        String targetPath;

        DownloadThread(String targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public void run() {
            AppLog.d(TAG, "begin DownloadThread");
            AppInfo.isDownloading = true;

            boolean temp = fileOperation.downloadFile(curRemoteVideoFile, targetPath);
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

            if (targetPath.endsWith(".mov") || targetPath.endsWith(".MOV")) {
                fileType = "video/quicktime";
            } else {
                fileType = "video/mp4";
            }

            MediaRefresh.addMediaToDB(activity, targetPath, fileType);
            AppLog.d(TAG, "end downloadFile temp = " + temp);
            AppInfo.isDownloading = false;
            final String message = activity.getResources().getString(R.string.message_download_to).replace("$1$", targetPath);
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
            boolean retValue;
            if (isCurrentItemLocal()) {
                // local
                retValue = curLocalVideoFile.exists() && curLocalVideoFile.delete();
            } else {
                // remote
                retValue = fileOperation.deleteFile(curRemoteVideoFile);
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

                        if (!isCurrentItemLocal()) {
                            RemoteFileHelper.getInstance().remove((RemoteMediaItemInfo) currentItemInfo, fileType);
                            hasDeleted = true;
                        }

                        Intent intent = new Intent();
                        intent.putExtra("hasDeleted", hasDeleted);
                        intent.putExtra("fileType", fileType.ordinal());
                        activity.setResult(1000, intent);
                        activity.finish();
                    }
                });
            }
            AppLog.d(TAG, "end DeleteThread");
        }
    }

    public void showDeleteEnsureDialog() {
        // show delete confirmation dialog
        AppDialogManager.getInstance().showDeleteConfirmDialog(activity, new DialogButtonListener() {
            @Override
            public void onDelete() {
                if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                    stopVideoStream();
                }

                MyProgressDialog.showProgressDialog(activity, R.string.dialog_deleting);
                executor = Executors.newSingleThreadExecutor();
                executor.submit(new DeleteThread(), null);
            }

            @Override
            public void onCancel() {
                if (videoPbMode == VideoPbMode.MODE_VIDEO_PAUSE) {
                    resumeVideoPb();
                }
            }
        }, activity.getResources().getString(R.string.dialog_confirm_delete_this_file));
    }

    class DownloadProgressTask extends TimerTask {
        long fileSize;
        long curFileLength;
        String curDownloadFile;

        public DownloadProgressTask(String downloadFile) {
            curDownloadFile = downloadFile;
        }

        @Override
        public void run() {
            File file = new File(curDownloadFile);
            fileSize = curRemoteVideoFile.getFileSize();
            if (file != null) {
                curFileLength = file.length();
                if (curFileLength == fileSize) {
                    downloadProgress = 100;
                } else {
                    downloadProgress = (int) (file.length() * 100 / fileSize);
                }
            } else {
                downloadProgress = 0;
            }

            activity.runOnUiThread(() -> {
                updateDownloadStatus();
            });
            AppLog.d(TAG, "update downloadProgress = " + downloadProgress);
        }
    }

    public void onSurfaceViewTouchDown(MotionEvent event) {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        touchMode = TouchMode.DRAG;
        mPreviousY = event.getY();
        mPreviousX = event.getX();
        beforeLenght = 0;
        afterLenght = 0;
    }

    public void onSurfaceViewPointerDown(MotionEvent event) {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        if (event.getPointerCount() == 2) {
            touchMode = TouchMode.ZOOM;
            beforeLenght = getDistance(event);//
        }
    }

    public void onSurfaceViewTouchMove(MotionEvent event) {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        if (touchMode == TouchMode.DRAG) {
            rotateB(event, mPreviousX, mPreviousY);
            mPreviousY = event.getY();
            mPreviousX = event.getX();
        } else if (touchMode == TouchMode.ZOOM) {
            afterLenght = getDistance(event);//

            float gapLenght = afterLenght - beforeLenght;

            if (Math.abs(gapLenght) > 5f) {
                float scale_temp = afterLenght / beforeLenght;
                this.setScale(scale_temp);
                beforeLenght = afterLenght;
            }
        }
    }

    float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) StrictMath.sqrt(x * x + y * y);
    }

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
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
        touchMode = TouchMode.NONE;
    }

    public void onSurfaceViewTouchPointerUp() {
        if (videoPbMode == VideoPbMode.MODE_VIDEO_IDLE) {
            return;
        }
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
            rotate(speedX, speedY, speedZ, event.timestamp);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void rotate(float speedX, float speedY, float speedZ, long timestamp) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        panoramaVideoPlayback.rotate(rotation, speedX, speedY, speedZ, timestamp);
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
            sensorManager = null;
        }
    }

    public void setDrawingArea(int windowW, int windowH) {
        videoStreaming.setDrawingArea(windowW, windowH);
//        if (iCatchSurfaceContext != null) {
//            try {
//                iCatchSurfaceContext.setViewPort(0, 0, windowW, windowH);
//            } catch (IchGLSurfaceNotSetException e) {
//                e.printStackTrace();
//            }
//        }
    }

    public void redrawSurface() {
        int width = videoDetailView.getSurfaceViewWidth();
        int height = videoDetailView.getSurfaceViewHeight();
        videoStreaming.setViewParam(width, height);
        videoStreaming.setSurfaceViewArea();
    }

    public void setPanoramaType() {
        if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE) {
            videoStreaming.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            videoDetailView.setPanoramaTypeImageResource(R.drawable.asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID) {
            videoStreaming.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            videoDetailView.setPanoramaTypeImageResource(R.drawable.vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            videoStreaming.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            videoDetailView.setPanoramaTypeImageResource(R.drawable.panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public void enableEIS(boolean enable) {
        StreamStablization streamStablization = panoramaVideoPlayback.getStreamStablization();
        if (streamStablization == null) {
            return;
        }
        if (enable) {
            streamStablization.enableStablization();
        } else {
            streamStablization.disableStablization();
        }
    }

    public void setSdCardEventListener() {
        GlobalInfo.getInstance().setOnEventListener(new GlobalInfo.OnEventListener() {
            @Override
            public void eventListener(int sdkEventId) {
                switch (sdkEventId) {
                    case SDKEvent.EVENT_SDCARD_REMOVED:
//                        stopVideoStream();
                        videoStreaming.stopForSdRemove();
                        RemoteFileHelper.getInstance().clearAllFileList();
                        AppDialog.showDialogWarn(activity, R.string.dialog_card_removed_and_back, false,new AppDialog.OnDialogSureClickListener() {
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

