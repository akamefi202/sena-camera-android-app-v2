package com.sena.senacamera.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.sena.senacamera.DataConvert.StreamInfoConvert;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.function.CameraAction.PhotoCapture;
import com.sena.senacamera.function.CameraAction.ZoomInOut;
import com.sena.senacamera.function.SDKEvent;
import com.sena.senacamera.function.streaming.CameraStreaming;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.CameraType;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.presenter.Interface.BasePresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.sena.senacamera.SdkApi.CameraState;
import com.sena.senacamera.SdkApi.FileOperation;
import com.sena.senacamera.SdkApi.PanoramaPreviewPlayback;
import com.sena.senacamera.data.AppInfo.AppInfo;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.data.Message.AppMessage;
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.data.Mode.LiveMode;
import com.sena.senacamera.data.Mode.PreviewMode;
import com.sena.senacamera.data.Mode.TouchMode;
import com.sena.senacamera.data.PropertyId.PropertyId;
import com.sena.senacamera.data.entity.CameraSlot;
import com.sena.senacamera.data.entity.StreamInfo;
import com.sena.senacamera.data.type.SlowMotion;
import com.sena.senacamera.data.type.TimeLapseInterval;
import com.sena.senacamera.data.type.TimeLapseMode;
import com.sena.senacamera.data.type.Tristate;
import com.sena.senacamera.data.type.Upside;
import com.sena.senacamera.db.CameraSlotSQLite;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.component.MyToast;
import com.sena.senacamera.ui.Interface.PreviewView;
import com.sena.senacamera.ui.appdialog.AppDialog;
import com.sena.senacamera.utils.BitmapTools;
import com.sena.senacamera.utils.ConvertTools;
import com.sena.senacamera.utils.PanoramaTools;
import com.sena.senacamera.utils.StorageUtil;
import com.sena.senacamera.utils.fileutils.FileOper;
import com.sena.senacamera.utils.fileutils.FileTools;
import com.icatchtek.control.customer.type.ICatchCamDateStamp;
import com.icatchtek.control.customer.type.ICatchCamEventID;
import com.icatchtek.control.customer.type.ICatchCamMode;
import com.icatchtek.control.customer.type.ICatchCamPreviewMode;
import com.icatchtek.control.customer.type.ICatchCamProperty;
import com.icatchtek.pancam.customer.ICatchPancamConfig;
import com.icatchtek.pancam.customer.exception.IchGLSurfaceNotSetException;
import com.icatchtek.pancam.customer.surface.ICatchSurfaceContext;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.icatchtek.pancam.customer.type.ICatchGLPoint;
import com.icatchtek.pancam.customer.type.ICatchGLSurfaceType;
import com.icatchtek.reliant.customer.type.ICatchCustomerStreamParam;
import com.icatchtek.reliant.customer.type.ICatchFile;
import com.icatchtek.reliant.customer.type.ICatchH264StreamParam;
import com.icatchtek.reliant.customer.type.ICatchJPEGStreamParam;
import com.icatchtek.reliant.customer.type.ICatchStreamParam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.sena.senacamera.data.Mode.PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;

/**
 * Created by zhang yanhu C001012 on 2015/12/4 14:22.
 */
public class PreviewPresenter extends BasePresenter implements SensorEventListener {
    private static final String TAG = PreviewPresenter.class.getSimpleName();

    private static final float MIN_ZOOM = 0.4f;
    private static final float MAX_ZOOM = 2.2f;

    private static final float FIXED_OUTSIDE_DISTANCE = 3.0f;
    private static final float FIXED_INSIDE_DISTANCE = 0.5f;
    private PanoramaPreviewPlayback panoramaPreviewPlayback;
    private TouchMode touchMode = TouchMode.NONE;
    private float mPreviousY;
    private float mPreviousX;
    private float beforeLenght;
    private float afterLenght;
    private float currentZoomRate = MAX_ZOOM;
    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private MediaPlayer videoCaptureStartBeep;
    private MediaPlayer modeSwitchBeep;
    private MediaPlayer stillCaptureStartBeep;
    private MediaPlayer continuousCaptureBeep;
    private Activity activity;
    private PreviewView previewView;
    private CameraProperties cameraProperties;
    private CameraAction cameraAction;
    private CameraState cameraState;
    private FileOperation fileOperation;
    private BaseProperties BaseProperties;
    private MyCamera curCamera;
    private PreviewHandler previewHandler;
    private SDKEvent sdkEvent;
    private int curAppStateMode = PreviewMode.APP_STATE_NONE_MODE;
    private Timer videoCaptureButtonChangeTimer;
    public boolean videoCaptureButtonChangeFlag = true;
    private Timer recordingLapseTimeTimer;
    private int lapseTime = 0;
    //private List<SettingMenu> settingMenuList;
    //private SettingListAdapter settingListAdapter;
    private boolean allowClickButtons = true;
    private int currentSettingMenuMode;
    private WifiSSReceiver wifiSSReceiver;
    private long lastCilckTime = 0;
    private long lastRecodeTime;
    private int curIcatchMode;
    private ICatchSurfaceContext iCatchSurfaceContext;
    private boolean hasInitSurface = false;
    private boolean isLive = false;
    private LiveMode liveMode = LiveMode.MODE_YOUTUBE_LIVE;
    private ZoomInOut zoomInOut;
    private int curVideoWidth = 1920;
    private int curVideoHeight = 960;
    private int curVideoFps = 30;
    private String curCodecType = "H264";
    private CameraStreaming cameraStreaming;
    private int curPanoramaType= ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
    boolean isDelEvent = false;
    private ICatchStreamParam defaultStreamParam = null;

    public PreviewPresenter(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public void setView(PreviewView previewView) {
        this.previewView = previewView;
        initCfg();
        initData();
    }

    public void initData() {
        curCamera = CameraManager.getInstance().getCurCamera();
        // akamefi202: to be fixed
        if (curCamera == null) {
            AppLog.i(TAG, "initData curCamera is null");
            finishActivity();
            return;
        }

        panoramaPreviewPlayback = curCamera.getPanoramaPreviewPlayback();

        cameraStreaming = new CameraStreaming(panoramaPreviewPlayback);
        cameraProperties = curCamera.getCameraProperties();
        if (cameraProperties == null) {
            AppLog.i(TAG, "initData cameraProperties is null");
            finishActivity();
            return;
        }
        cameraAction = curCamera.getCameraAction();
        cameraState = curCamera.getCameraState();
        fileOperation = curCamera.getFileOperation();
        BaseProperties = curCamera.getBaseProperties();
        zoomInOut = new ZoomInOut();
        videoCaptureStartBeep = MediaPlayer.create(activity, R.raw.camera_timer);
        stillCaptureStartBeep = MediaPlayer.create(activity, R.raw.captureshutter);
        continuousCaptureBeep = MediaPlayer.create(activity, R.raw.captureburst);
        modeSwitchBeep = MediaPlayer.create(activity, R.raw.focusbeep);
        previewHandler = new PreviewHandler();
        sdkEvent = new SDKEvent(previewHandler);
        if (cameraProperties.hasFunction(0xD7F0)) {
            cameraProperties.setCaptureDelayMode(1);
        }

        if (curCamera.getCameraType() == CameraType.USB_CAMERA) {
            Intent intent = activity.getIntent();
            curVideoWidth = intent.getIntExtra("videoWidth", 1920);
            curVideoHeight = intent.getIntExtra("videoHeight", 960);
            curVideoFps = intent.getIntExtra("videoFps", 30);
            curCodecType = intent.getStringExtra("videoCodec");
            if (curCodecType == null) {
                curCodecType = "H264";
            }
            AppLog.d(TAG, "initData videoWidth=" + curVideoWidth + " videoHeight=" + curVideoHeight + " videoFps=" + curVideoFps + " curCodecType=" +
                    curCodecType);
        }
        AppLog.i(TAG, "cameraProperties.getMaxZoomRatio() =" + cameraProperties.getMaxZoomRatio());
    }

    public void initStatus() {
        if (AppInfo.enableLive) {
            previewView.setYouTubeLiveLayoutVisibility(View.VISIBLE);
        } else {
            previewView.setYouTubeLiveLayoutVisibility(View.GONE);
        }

        if (!cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_BATTERY_LEVEL)) {
            previewView.setBatteryStatusVisibility(View.GONE);
        } else {
            int batteryLevel = cameraProperties.getBatteryElectric();
            previewView.setBatteryStatusVisibility(View.VISIBLE);
            previewView.setBatteryIcon(batteryLevel);

            if (batteryLevel < 20) {
                AppDialog.showLowBatteryWarning(activity);
            }
        }

        if (!cameraProperties.isSDCardExist()) {
            AppDialog.showDialogWarn(activity, R.string.dialog_card_lose);
        }
        previewView.setSdCardIcon(cameraProperties.isSDCardExist());

        IntentFilter wifiSSFilter = new IntentFilter(WifiManager.RSSI_CHANGED_ACTION);
        wifiSSReceiver = new WifiSSReceiver();
        activity.registerReceiver(wifiSSReceiver, wifiSSFilter);
    }


    public void changeCameraMode(final int previewMode, final int ichVideoPreviewMode) {
        AppLog.i(TAG, "start changeCameraMode ichVideoPreviewMode=" + ichVideoPreviewMode);
        AppLog.i(TAG, "start changeCameraMode previewMode=" + previewMode + " hasInitSurface=" + hasInitSurface);

        curIcatchMode = ichVideoPreviewMode;
        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);

        new Thread(new Runnable() {
            @Override
            public void run() {
                cameraAction.changePreviewMode(ichVideoPreviewMode);
                startPreview();
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        curAppStateMode = previewMode;
                        createUIByMode(curAppStateMode);
                        MyProgressDialog.closeProgressDialog();
                        previewView.dismissPopupWindow();
                    }
                });
            }
        }).start();
    }

    public void redrawSurface() {
        if (curCamera.isStreamReady && !AppInfo.enableRender) {
            int width = previewView.getSurfaceViewWidth();
            int heigth = previewView.getSurfaceViewHeight();
            AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
            if (width > 0 || heigth > 0) {
                cameraStreaming.setViewParam(width, heigth);
                cameraStreaming.setSurfaceViewArea();
            }
        }
    }


    public void startOrStopCapture() {
        final int duration = videoCaptureStartBeep.getDuration();
        if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            if (!cameraProperties.isSDCardExist()) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRecordingRemainTime() <= 0) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    videoCaptureStartBeep.start();
                    AppLog.d(TAG,"duration:" + duration);
                    try {
                        Thread.sleep(duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lastRecodeTime = System.currentTimeMillis();
                    final boolean ret = cameraAction.startMovieRecord();
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            if (ret) {
                                AppLog.i(TAG, "startRecordingLapseTimeTimer(0)");
                                curAppStateMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                                startVideoCaptureButtonChangeTimer();
                                startRecordingLapseTimeTimer(0);
                            } else {
                                MyToast.show(activity, R.string.text_operation_failed);
                            }
                        }
                    });

                }
            }).start();

        } else if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {

            if (System.currentTimeMillis() - lastRecodeTime < 2000) {
                return;
            }
            MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final boolean ret = cameraAction.stopVideoCapture();
                    videoCaptureStartBeep.start();
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyProgressDialog.closeProgressDialog();
                            if (ret) {
                                curAppStateMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                                stopVideoCaptureButtonChangeTimer();
                                stopRecordingLapseTimeTimer();
                                previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
                            } else {
                                MyToast.show(activity,R.string.text_operation_failed);
                            }

                        }
                    });
                }
            }).start();

        } else if (curAppStateMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.hideZoomView();
            if (!cameraProperties.isSDCardExist()) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRemainImageNum() < 1) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
//            stillCaptureStartBeep.start();
            curAppStateMode = PreviewMode.APP_STATE_STILL_CAPTURE;
            startPhotoCapture();
        } else if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {

            if (!cameraProperties.isSDCardExist()) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRemainImageNum() < 1) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
            if (cameraProperties.getCurrentTimeLapseInterval() == TimeLapseInterval.TIME_LAPSE_INTERVAL_OFF) {
                AppDialog.showDialogWarn(activity, R.string.timelapse_not_allow);
                return;
            }
            continuousCaptureBeep.start();
            if (!cameraAction.startTimeLapse()) {
                AppLog.e(TAG, "failed to start startTimeLapse");
                return;
            }

            previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_photo);
            curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE;
        } else if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppLog.d(TAG, "curMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE");
            if (!cameraAction.stopTimeLapse()) {
                AppLog.e(TAG, "failed to stopTimeLapse");
                return;
            }
            stopRecordingLapseTimeTimer();

            curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
        } else if (curAppStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            AppLog.d(TAG, "curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
            if (!cameraProperties.isSDCardExist()) {
                AppDialog.showDialogWarn(activity, R.string.dialog_card_not_exist);
                return;
            }
            if (cameraProperties.getRemainImageNum() < 1) {
                AppDialog.showDialogWarn(activity, R.string.dialog_sd_card_is_full);
                return;
            }
            if (cameraProperties.getCurrentTimeLapseInterval() == TimeLapseInterval.TIME_LAPSE_INTERVAL_OFF) {
                AppLog.d(TAG, "time lapse is not allowed because of timelapse interval is OFF");
                AppDialog.showDialogWarn(activity, R.string.timelapse_not_allow);
                return;
            }

            videoCaptureStartBeep.start();
            if (!cameraAction.startTimeLapse()) {
                AppLog.e(TAG, "failed to start startTimeLapse");
                return;
            }
            curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
            startVideoCaptureButtonChangeTimer();
            startRecordingLapseTimeTimer(0);

        } else if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            AppLog.d(TAG, "curMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE");
            videoCaptureStartBeep.start();
            if (!cameraAction.stopTimeLapse()) {
                AppLog.e(TAG, "failed to stopTimeLapse");
                return;
            }
            stopVideoCaptureButtonChangeTimer();
            stopRecordingLapseTimeTimer();
            curAppStateMode = APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
        }
        AppLog.d(TAG, "end processing for responsing captureBtn clicking");
    }

    public String getCurrentCameraMode() {
        if (curAppStateMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                curAppStateMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                curAppStateMode == PreviewMode.APP_STATE_STILL_MODE) {
            return CameraMode.PHOTO;
        } else if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                curAppStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                curAppStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                curAppStateMode == PreviewMode.APP_STATE_VIDEO_MODE) {
            return CameraMode.VIDEO;
        }

        return CameraMode.PHOTO;
    }

    public void createUIByMode(int appStateMode) {
        AppLog.i(TAG, "start createUIByMode previewMode=" + appStateMode);
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_VIDEO)) {
            if (appStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW || appStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                previewView.setPvModeBtnBackgroundResource(R.drawable.video_toggle_btn_on);
            }
        }
        if (appStateMode == PreviewMode.APP_STATE_STILL_PREVIEW || appStateMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
            previewView.setPvModeBtnBackgroundResource(R.drawable.capture_toggle_btn_on);
        }
        if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
            if (appStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                    appStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    appStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                    appStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                previewView.setPvModeBtnBackgroundResource(R.drawable.timelapse_toggle_btn_on);
            }
        }

        if (appStateMode == PreviewMode.APP_STATE_STILL_CAPTURE ||
                appStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE ||
                appStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                appStateMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_photo);
            previewView.updateUIByPhotoMode();
        } else if (appStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE ||
                appStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE ||
                appStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW ||
                appStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_video);
            previewView.updateUIByVideoMode();
        }

        if (BaseProperties.getPhotoSelfTimer().needDisplayByMode(appStateMode)) {
            previewView.setDelayCaptureLayoutVisibility(View.VISIBLE);
            previewView.setDelayCaptureTextTime(BaseProperties.getPhotoSelfTimer().getCurrentUiStringInSetting());
        } else {
            previewView.setDelayCaptureLayoutVisibility(View.GONE);
        }

        if (BaseProperties.getPhotoResolution().needDisplayByMode(appStateMode)) {
            previewView.setImageSizeLayoutVisibility(View.VISIBLE);
            previewView.setImageSizeInfo(BaseProperties.getPhotoResolution().getCurrentUiStringInSetting());
            previewView.setRemainCaptureCount(Integer.valueOf(cameraProperties.getRemainImageNum()).toString());
        } else {
            previewView.setImageSizeLayoutVisibility(View.GONE);
        }

        if (BaseProperties.getVideoResolution().needDisplayByMode(appStateMode)) {
            previewView.setVideoSizeLayoutVisibility(View.VISIBLE);
            previewView.setVideoSizeInfo(BaseProperties.getVideoResolution().getCurrentUiStringInSetting());
            previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
        } else {
            previewView.setVideoSizeLayoutVisibility(View.GONE);
        }

        if (BaseProperties.getPhotoBurst().needDisplayByMode(appStateMode)) {
            previewView.setBurstStatusVisibility(View.VISIBLE);
            int iconId = BaseProperties.getPhotoBurst().getCurrentIcon();
            if (iconId > 0) {
                previewView.setBurstStatusIcon(iconId);
            }
        } else {
            previewView.setBurstStatusVisibility(View.GONE);
        }

        if (BaseProperties.getWhiteBalance().needDisplayByMode(appStateMode)) {
            previewView.setWbStatusVisibility(View.VISIBLE);
            int iconId = BaseProperties.getWhiteBalance().getCurrentIcon();
            if (iconId > 0) {
                previewView.setWbStatusIcon(iconId);
            }
        } else {
            previewView.setWbStatusVisibility(View.GONE);
        }

        if (BaseProperties.getUpside().needDisplayByMode(appStateMode) && cameraProperties.getCurrentUpsideDown() == Upside.UPSIDE_ON) {
            previewView.setUpsideVisibility(View.VISIBLE);
        } else {
            previewView.setUpsideVisibility(View.GONE);
        }

        if (BaseProperties.getSlowMotion().needDisplayByMode(appStateMode) && cameraProperties.getCurrentSlowMotion() == SlowMotion.SLOW_MOTION_ON) {
            previewView.setSlowMotionVisibility(View.VISIBLE);
        } else {
            previewView.setSlowMotionVisibility(View.GONE);
        }

        if (BaseProperties.getTimelapseMode().needDisplayByMode(appStateMode)) {
            previewView.setTimeLapseModeVisibility(View.VISIBLE);
            int iconId = BaseProperties.getTimelapseMode().getCurrentIcon();
            if (iconId > 0) {
                previewView.setTimeLapseModeIcon(iconId);
            }
        } else {
            previewView.setTimeLapseModeVisibility(View.GONE);
        }
    }

    public void initPreview() {
        AppLog.i(TAG, "initPreview curMode=" + curAppStateMode);
        //set min first ,then max;
        GlobalInfo.getInstance().setOnEventListener(new GlobalInfo.OnEventListener() {
            @Override
            public void eventListener(int sdkEventId) {
                switch (sdkEventId) {
                    case SDKEvent.EVENT_SDCARD_REMOVED:
                        MyToast.show(activity, R.string.dialog_card_removed);
                        if (BaseProperties.getPhotoResolution().needDisplayByMode(curAppStateMode)) {
                            previewView.setRemainCaptureCount("0");
                        } else if (BaseProperties.getVideoResolution().needDisplayByMode(curAppStateMode)) {
                            previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(0));
                        }
                        break;
                    case SDKEvent.EVENT_SDCARD_INSERT:
                        MyToast.show(activity, R.string.dialog_card_inserted);
                        if (BaseProperties.getPhotoResolution().needDisplayByMode(curAppStateMode)) {
                            previewView.setRemainCaptureCount(String.valueOf(cameraProperties.getRemainImageNum()));
                        } else if (BaseProperties.getVideoResolution().needDisplayByMode(curAppStateMode)) {
                            previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
                        }
                        break;
                }
            }
        });

        previewView.setMinZoomRate(1.0f);
        previewView.setMaxZoomRate(cameraProperties.getMaxZoomRatio() * 1.0f);
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());

        int icatchMode = cameraAction.getCurrentCameraMode();
        if (cameraState.isMovieRecording()) {
            AppLog.i(TAG, "camera is recording...");
            curAppStateMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
            startVideoCaptureButtonChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());
        } else if (cameraState.isTimeLapseVideoOn()) {
            AppLog.i(TAG, "camera is TimeLapseVideoOn...");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
            curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE;
            startVideoCaptureButtonChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());
        } else if (cameraState.isTimeLapseStillOn()) {
            AppLog.i(TAG, "camera is TimeLapseStillOn...");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
            curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE;
            startVideoCaptureButtonChangeTimer();
            startRecordingLapseTimeTimer(cameraProperties.getVideoRecordingTime());
        } else if (curAppStateMode == PreviewMode.APP_STATE_NONE_MODE) {
            // akamefi202: to be fixed
//            if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_VIDEO)) {
//                curAppStateMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
//                curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
//            }
            if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_CAMERA)) {
                curAppStateMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                curIcatchMode = ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE;
            } else if (cameraProperties.cameraModeSupport(ICatchCamMode.ICH_CAM_MODE_TIMELAPSE)) {
                curAppStateMode = APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE;
            } else {
                curAppStateMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                curIcatchMode = ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE;
//                curAppStateMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
//                curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
            }
        } else if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_VIDEO_PREVIEW");
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
            // normal state, app show preview
        } else if (curAppStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_VIDEO");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_VIDEO;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE;
            // normal state, app show preview
        } else if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == PreviewMode.APP_STATE_TIMELAPSE_PREVIEW_STILL");
            curCamera.timeLapsePreviewMode = TimeLapseMode.TIME_LAPSE_MODE_STILL;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE;
            // normal state, app show preview
        } else if (curAppStateMode == PreviewMode.APP_STATE_STILL_PREVIEW) {
            AppLog.i(TAG, "initPreview curMode == ICH_STILL_PREVIEW_MODE");
//            changeCameraMode(curAppStateMode, ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE;
        } else {
            curAppStateMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
            curIcatchMode = ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE;
        }
        cameraAction.changePreviewMode(curIcatchMode);
        createUIByMode(curAppStateMode);
    }

    public void startVideoCaptureButtonChangeTimer() {
        AppLog.d(TAG, "startVideoCaptureButtonChangeTimer videoCaptureButtonChangeTimer=" + videoCaptureButtonChangeTimer);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                if (videoCaptureButtonChangeFlag) {
                    videoCaptureButtonChangeFlag = false;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_video_recording);
                            }
                        }
                    });
                } else {
                    videoCaptureButtonChangeFlag = true;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                                previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_video_recording);
                            }
                        }
                    });
                }
            }
        };

        videoCaptureButtonChangeTimer = new Timer(true);
        videoCaptureButtonChangeTimer.schedule(task, 0, 1000);
    }

    public void stopVideoCaptureButtonChangeTimer() {
        AppLog.d(TAG, "stopVideoCaptureButtonChangeTimer videoCaptureButtonChangeTimer=" + videoCaptureButtonChangeTimer);
        if (videoCaptureButtonChangeTimer != null) {
            videoCaptureButtonChangeTimer.cancel();
        }
        previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_video);
    }

    private void startRecordingLapseTimeTimer(int startTime) {
        if (cameraProperties.hasFunction(PropertyId.VIDEO_RECORDING_TIME) == false) {
            return;
        }
        AppLog.i(TAG, "startRecordingLapseTimeTimer curMode=" + curAppStateMode);
        if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE || curAppStateMode == PreviewMode
                .APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            AppLog.i(TAG, "startRecordingLapseTimeTimer");
            if (recordingLapseTimeTimer != null) {
                recordingLapseTimeTimer.cancel();
            }

            lapseTime = startTime;
            recordingLapseTimeTimer = new Timer(true);
            previewView.setRecordingTimeVisibility(View.VISIBLE);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            previewView.setRecordingTime(ConvertTools.secondsToHours(lapseTime ++));
                        }
                    });
                }
            };
            recordingLapseTimeTimer.schedule(timerTask, 0, 1000);
        }
    }

    private void stopRecordingLapseTimeTimer() {
        if (recordingLapseTimeTimer != null) {
            recordingLapseTimeTimer.cancel();
        }
        previewView.setRecordingTime("00:00:00");
        previewView.setRecordingTimeVisibility(View.GONE);
    }

    public void changePreviewMode(int previewMode) {
        AppLog.d(TAG, "changePreviewMode previewMode=" + previewMode);
        AppLog.d(TAG, "changePreviewMode curAppStateMode=" + curAppStateMode);
        long timeInterval = System.currentTimeMillis() - lastCilckTime;
        AppLog.d(TAG, "repeat click: timeInterval=" + timeInterval);

        if (System.currentTimeMillis() - lastCilckTime < 2000) {
            AppLog.d(TAG, "repeat click: timeInterval < 2000");
            return;
        } else {
            lastCilckTime = System.currentTimeMillis();
        }

        if (!checkModeSwitch(curAppStateMode)) {
            int resId = getSwitchErrorResId(curAppStateMode);
            if (resId > 0) {
                MyToast.show(activity, resId);
            }
            return;
        }

        modeSwitchBeep.start();
        if (previewMode == PreviewMode.APP_STATE_VIDEO_MODE) {
            if (curAppStateMode == PreviewMode.APP_STATE_STILL_PREVIEW ||
                    curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    curAppStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                stopPreview();
                changeCameraMode(PreviewMode.APP_STATE_VIDEO_PREVIEW, ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE);
            }
        } else if (previewMode == PreviewMode.APP_STATE_STILL_MODE) {
            if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW ||
                    curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW ||
                    curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                stopPreview();
//                cameraAction.changePreviewMode(ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
                changeCameraMode(PreviewMode.APP_STATE_STILL_PREVIEW, ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
            }
        } else if (previewMode == PreviewMode.APP_STATE_TIMELAPSE_MODE) {
            if (curAppStateMode == PreviewMode.APP_STATE_STILL_PREVIEW || curAppStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                stopPreview();
                if (curCamera.timeLapsePreviewMode == TimeLapseMode.TIME_LAPSE_MODE_VIDEO) {
                    changeCameraMode(PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW, ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_VIDEO_PREVIEW_MODE);
                } else {
                    changeCameraMode(PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW, ICatchCamPreviewMode.ICH_CAM_TIMELAPSE_STILL_PREVIEW_MODE);
                }
            }
        }
    }

    private void startPhotoCapture() {
        previewView.setCaptureBtnEnability(false);
        previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_photo);
        PhotoCapture photoCapture = new PhotoCapture();
        if (cameraProperties.hasFunction(0xD7F0)) {
            photoCapture.addOnStopPreviewListener(new PhotoCapture.OnStopPreviewListener() {
                @Override
                public void onStop() {
                    if (!cameraProperties.hasFunction(0xd704)) {
                        stopPreview();
                    }
                }
            });
            photoCapture.setOnCaptureListener(new PhotoCapture.OnCaptureListener() {
                @Override
                public void onCompleted() {
                    //curAppStateMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            previewView.setCaptureBtnEnability(true);
                        }
                    });
                }
            });
            photoCapture.startCapture();
        } else {
            stillCaptureStartBeep.start();
            if (!cameraProperties.hasFunction(0xd704)) {
                stopPreview();
            }
//            previewHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (!cameraAction.capturePhoto()) {
//
//                        MyToast.show(activity,R.string.text_operation_failed);
//                    }
//                    //curAppStateMode = PreviewMode.APP_STATE_STILL_PREVIEW;
//                    previewView.setCaptureBtnEnability(true);
//
//                }
//            }, 500);
            MyProgressDialog.showProgressDialog(activity, R.string.dialog_capturing);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final boolean ret = cameraAction.capturePhoto();
                    previewHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!ret) {
                                MyToast.show(activity,R.string.text_operation_failed);
                                curAppStateMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                            }
                            previewView.setCaptureBtnEnability(true);
                            MyProgressDialog.closeProgressDialog();
                        }
                    });
                }
            }).start();
        }
    }

    public synchronized boolean disconnectCamera() {
        if (curCamera != null) {
            GlobalInfo.getInstance().delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_REMOVED);
            GlobalInfo.getInstance().delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN);
            GlobalInfo.getInstance().delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED);
            GlobalInfo.getInstance().delete();
            return curCamera.disconnect();
        } else {
            return false;
        }
    }

    public void delConnectFailureListener() {
        // GlobalInfo.getInstance().enableConnectCheck(false);
    }

    public void unregisterWifiSSReceiver() {
        if (wifiSSReceiver != null) {
            activity.unregisterReceiver(wifiSSReceiver);
            wifiSSReceiver = null;
        }

    }

    public void zoomIn() {
        if (curAppStateMode == PreviewMode.APP_STATE_STILL_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return;
        }
        zoomInOut.zoomIn();
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());
    }

    public void zoomOut() {
        if (curAppStateMode == PreviewMode.APP_STATE_STILL_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return;
        }
        zoomInOut.zoomOut();
        previewView.updateZoomViewProgress(cameraProperties.getCurrentZoomRatio());
    }

    public void zoomBySeekBar() {
        zoomInOut.addZoomCompletedListener(new ZoomInOut.ZoomCompletedListener() {
            @Override
            public void onCompleted(final float currentZoomRate) {
                previewHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyProgressDialog.closeProgressDialog();
                        AppLog.i(TAG, "addZoomCompletedListener currentZoomRate =" + currentZoomRate);
                        previewView.updateZoomViewProgress(currentZoomRate);
                    }
                });
            }
        });

        zoomInOut.startZoomInOutThread(this);
        MyProgressDialog.showProgressDialog(activity, "");
    }

    public void showZoomView() {
        if (curAppStateMode == PreviewMode.APP_STATE_STILL_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE || curAppStateMode == PreviewMode
                .APP_STATE_TIMELAPSE_VIDEO_CAPTURE || (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_DATE_STAMP) == true && ICatchCamDateStamp
                .ICH_CAM_DATE_STAMP_OFF != cameraProperties.getCurrentDateStamp())) {
            return;
        }
        previewView.showZoomView();
    }

    public float getMaxZoomRate() {
        return previewView.getZoomViewMaxZoomRate();
    }

    public float getZoomViewProgress() {
        AppLog.d(TAG, "getZoomViewProgress value=" + previewView.getZoomViewProgress());
        return previewView.getZoomViewProgress();
    }

    private class PreviewHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Tristate ret = Tristate.FALSE;

            switch (msg.what) {
                case SDKEvent.EVENT_BATTERY_ELECTRIC_CHANGED:
                    AppLog.i(TAG, "receive EVENT_BATTERY_ELETRIC_CHANGED power =" + msg.arg1);
                    //need to update battery eletric
                    int batteryLevel = msg.arg1;
                    previewView.setBatteryIcon(batteryLevel);
                    if (batteryLevel < 20) {
                        AppDialog.showLowBatteryWarning(activity);
                    }
                    break;
                case SDKEvent.EVENT_CONNECTION_FAILURE:
                    AppLog.i(TAG, "receive EVENT_CONNECTION_FAILURE");
                    stopPreview();
                    delEvent();
                    disconnectCamera();
                    break;
                case SDKEvent.EVENT_SD_CARD_FULL:
                    AppLog.i(TAG, "receive EVENT_SD_CARD_FULL");
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_full);
                    break;
                case SDKEvent.EVENT_VIDEO_OFF://only receive if fw request to stopMPreview video recording
                    AppLog.i(TAG, "receive EVENT_VIDEO_OFF:curAppStateMode=" + curAppStateMode);
                    if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
                        if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE) {
                            curAppStateMode = PreviewMode.APP_STATE_VIDEO_PREVIEW;
                        } else {
                            curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
                        }
                        stopVideoCaptureButtonChangeTimer();
                        stopRecordingLapseTimeTimer();
                        previewView.setRemainRecordingTimeText(ConvertTools.secondsToMinuteOrHours(cameraProperties.getRecordingRemainTime()));
                    }
                    break;
                case SDKEvent.EVENT_VIDEO_ON:
                    AppLog.i(TAG, "receive EVENT_VIDEO_ON:curAppStateMode =" + curAppStateMode);
                    // video from camera when file exceeds 4g
                    if (curAppStateMode == PreviewMode.APP_STATE_VIDEO_PREVIEW) {
                        curAppStateMode = PreviewMode.APP_STATE_VIDEO_CAPTURE;
                        startVideoCaptureButtonChangeTimer();
                        startRecordingLapseTimeTimer(0);
                    } else if (curAppStateMode == APP_STATE_TIMELAPSE_VIDEO_PREVIEW) {
                        curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE;
                        startVideoCaptureButtonChangeTimer();
                        startRecordingLapseTimeTimer(0);
                    }
                    break;
                case SDKEvent.EVENT_CAPTURE_START:
                    AppLog.i(TAG, "receive EVENT_CAPTURE_START:curAppStateMode=" + curAppStateMode);
                    if (curAppStateMode != PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        return;
                    }
                    continuousCaptureBeep.start();
                    MyToast.show(activity, R.string.capture_start);
                    break;
                case SDKEvent.EVENT_CAPTURE_COMPLETED:
                    AppLog.i(TAG, "receive EVENT_CAPTURE_COMPLETED:curAppStateMode=" + curAppStateMode);
                    if (curAppStateMode == PreviewMode.APP_STATE_STILL_CAPTURE) {
                        curAppStateMode = PreviewMode.APP_STATE_STILL_PREVIEW;
                        MyProgressDialog.showProgressDialog(activity, R.string.action_processing);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (!cameraProperties.hasFunction(0xd704)) {
                                    startPreview();
                                }
                                final String remainImageNum = String.valueOf(cameraProperties.getRemainImageNum());
                                previewHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        previewView.setCaptureBtnEnability(true);
                                        previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_photo);
                                        previewView.setRemainCaptureCount(remainImageNum);
                                        MyProgressDialog.closeProgressDialog();
                                    }
                                });
                            }
                        }).start();

                        return;
                    }
                    if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
                        previewView.setCaptureBtnEnability(true);
                        previewView.setCaptureBtnBackgroundResource(R.drawable.shutter_photo);
                        previewView.setRemainCaptureCount(String.valueOf(cameraProperties.getRemainImageNum()));
                        MyToast.show(activity, R.string.capture_completed);
                    }

                    break;
                case SDKEvent.EVENT_FILE_ADDED:
                    AppLog.i(TAG, "EVENT_FILE_ADDED");
//                    if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
//                        lapseTime = 0;
//                    }
                    break;

                case SDKEvent.EVENT_TIME_LAPSE_STOP:
                    AppLog.i(TAG, "receive EVENT_TIME_LAPSE_STOP:curAppStateMode=" + curAppStateMode);
                    //BSP-1419 收到 Event 時，就表示FW 已經自己停止了，APP 不需要再去執行 stopTimeLapse
                    if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
//                        if (cameraAction.stopTimeLapse()) {
                        stopVideoCaptureButtonChangeTimer();
                        stopRecordingLapseTimeTimer();
                        previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
                        curAppStateMode = APP_STATE_TIMELAPSE_VIDEO_PREVIEW;
//                        }

                    } else if (curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
//                        if (cameraAction.stopTimeLapse()) {
                        stopRecordingLapseTimeTimer();
                        previewView.setRemainCaptureCount(new Integer(cameraProperties.getRemainImageNum()).toString());
                        curAppStateMode = PreviewMode.APP_STATE_TIMELAPSE_STILL_PREVIEW;
//                        }
                    }
                    break;
                case SDKEvent.EVENT_VIDEO_RECORDING_TIME:
                    AppLog.i(TAG, "receive EVENT_VIDEO_RECORDING_TIME");
                    startRecordingLapseTimeTimer(0);
                    break;
                case SDKEvent.EVENT_FILE_DOWNLOAD:
                    AppLog.i(TAG, "receive EVENT_FILE_DOWNLOAD");
                    AppLog.d(TAG, "receive EVENT_FILE_DOWNLOAD  msg.arg1 =" + msg.arg1);
                    if (AppInfo.autoDownloadAllow == false) {
                        AppLog.d(TAG, "GlobalInfo.autoDownload == false");

                        return;
                    }
                    final String path = StorageUtil.getRootPath(activity)+ AppInfo.AUTO_DOWNLOAD_PATH;
//                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//                        path = Environment.getExternalStorageDirectory().toString() + AppInfo.AUTO_DOWNLOAD_PATH;
//                    } else {
//                        return;
//                    }
                    File directory = new File(path);

                    if (FileTools.getFileSize(directory) / 1024 >= AppInfo.autoDownloadSizeLimit * 1024 * 1024) {
                        AppLog.d(TAG, "can not download because size limit");
                        return;
                    }
                    final ICatchFile file = (ICatchFile) msg.obj;
                    FileOper.createDirectory(path);
                    new Thread() {
                        @Override
                        public void run() {
                            AppLog.d(TAG, "receive downloadFile file =" + file);
                            AppLog.d(TAG, "receive downloadFile path =" + path);
                            boolean retvalue = fileOperation.downloadFile(file, path + file.getFileName());
                            if (retvalue == true) {
                                previewHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String path1 = path + file.getFileName();
                                        Bitmap bitmap = BitmapTools.getImageByPath(path1, 150, 150);
                                        previewView.setAutoDownloadBitmap(bitmap);
                                    }
                                });
                            }
                            AppLog.d(TAG, "receive downloadFile retvalue =" + retvalue);
                        }
                    }.start();
                    break;
                case AppMessage.SETTING_OPTION_AUTO_DOWNLOAD:
                    AppLog.d(TAG, "receive SETTING_OPTION_AUTO_DOWNLOAD");
                    Boolean switcher = (Boolean) msg.obj;
                    if (switcher == true) {
                        // AutoDownLoad
                        AppInfo.autoDownloadAllow = true;
                        previewView.setAutoDownloadVisibility(View.VISIBLE);
                    } else {
                        AppInfo.autoDownloadAllow = false;
                        previewView.setAutoDownloadVisibility(View.GONE);
                    }
                    break;
                case SDKEvent.EVENT_SDCARD_INSERT:
                    AppLog.i(TAG, "receive EVENT_SDCARD_INSERT");
                    AppDialog.showDialogWarn(activity, R.string.dialog_card_inserted);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void addEvent() {
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED);
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP);
        sdkEvent.addCustomizeEvent(0x5001);// video recording event
        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD);
//        sdkEvent.addCustomizeEvent(0x3701);// Insert SD card event
//        sdkEvent.addEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN);
        isDelEvent = false;

//        addPanoramaEventListener();
    }

    public synchronized void delEvent() {
        if (curCamera != null && curCamera.isConnected() && !isDelEvent) {
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_FULL);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_BATTERY_LEVEL_CHANGED);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_COMPLETE);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CAPTURE_START);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_OFF);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_ADDED);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_VIDEO_ON);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_CONNECTION_DISCONNECTED);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_TIMELAPSE_STOP);
            sdkEvent.delCustomizeEventListener(0x5001);
            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_FILE_DOWNLOAD);
//        sdkEvent.delCustomizeEventListener(0x3701);// Insert SD card event
//            sdkEvent.delEventListener(ICatchCamEventID.ICH_CAM_EVENT_SDCARD_IN);
            isDelEvent = true;
        }
    }

    @Override
    public void isAppBackground() {
        super.isAppBackground();
    }

    @Override
    public void finishActivity() {
        Tristate ret = Tristate.NORMAL;
        if (isLive) {
            panoramaPreviewPlayback.stopPublishStreaming();
            isLive = false;
        }

        savePvThumbnail();
        destroyPreview();
        super.finishActivity();
    }

    @Override
    public void redirectToAnotherActivity(final Context context, final Class<?> cls) {
        AppLog.i(TAG, "pbBtn is clicked curAppStateMode=" + curAppStateMode);
        if (allowClickButtons == false) {
            AppLog.i(TAG, "do not allow to response button clicking");
            return;
        }
        if (!checkModeSwitch(curAppStateMode)) {
            int resId  = getSwitchErrorResId(curAppStateMode);
            if (resId >0 ) {
                MyToast.show(activity, resId);
            }
            return;
        }
        allowClickButtons = false;
        if (!cameraProperties.isSDCardExist()) {
            Log.d("dialog_card_lose", "222222222");
            AppDialog.showDialogWarn(activity, R.string.dialog_card_lose);
            allowClickButtons = true;
            return;
        }
        AppLog.i(TAG, "curAppStateMode =" + curAppStateMode);
        destroyPreview();
        delEvent();
        allowClickButtons = true;
        //BSP-1209
        MyProgressDialog.showProgressDialog(context, R.string.action_processing);
        previewHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyProgressDialog.closeProgressDialog();
                Intent intent = new Intent();
                AppLog.i(TAG, "intent:start PbMainActivity.class");
                intent.putExtra("cameraMode", getCurrentCameraMode());
                if (getCurrentCameraMode().equals(CameraMode.PHOTO)) {
                    // photo
                    intent.putExtra("shootMode", curCamera.getBaseProperties().getPhotoMode().getCurrentUiStringInSetting());
                } else {
                    // video
                    intent.putExtra("shootMode", curCamera.getBaseProperties().getVideoMode().getCurrentUiStringInSetting());
                }
                intent.setClass(context, cls);
                context.startActivity(intent);
                AppLog.i(TAG, "intent:end start PbMainActivity.class");
            }
        }, 500);
        allowClickButtons = true;
        AppLog.i(TAG, "end processing for responding pbBtn clicking");
    }

    private boolean checkModeSwitch(int appStateMode) {
        if (appStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE
                || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE
                || curAppStateMode == PreviewMode.APP_STATE_STILL_CAPTURE
                || curAppStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return false;
        } else {
            return true;
        }
    }

    private int getSwitchErrorResId(int appStateMode) {
        if (appStateMode == PreviewMode.APP_STATE_VIDEO_CAPTURE || appStateMode == PreviewMode.APP_STATE_TIMELAPSE_VIDEO_CAPTURE) {
            return R.string.stream_error_recording;
        } else if (appStateMode == PreviewMode.APP_STATE_STILL_CAPTURE || appStateMode == PreviewMode.APP_STATE_TIMELAPSE_STILL_CAPTURE) {
            return R.string.stream_error_capturing;
        } else {
            return -1;
        }
    }

    private class WifiSSReceiver extends BroadcastReceiver {
        private WifiManager wifi;

        public WifiSSReceiver() {
            super();

            wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            changeWifiStatusIcon();
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            changeWifiStatusIcon();
        }

        private void changeWifiStatusIcon() {
            WifiInfo info = wifi.getConnectionInfo();
            if (info.getBSSID() != null) {
                int strength = WifiManager.calculateSignalLevel(info.getRssi(), 8);

                AppLog.d(TAG, "change Wifi Status：" + strength);
                switch (strength) {
                    case 0:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_0_bar_green_24dp);
                        break;
                    case 1:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_1_bar_green_24dp);
                        break;
                    case 2:
                    case 3:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_2_bar_green_24dp);
                        break;
                    case 4:
                    case 5:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_3_bar_green_24dp);
                        break;
                    case 6:
                    case 7:
                        previewView.setWifiIcon(R.drawable.ic_signal_wifi_4_bar_green_24dp);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void startPreview() {
        AppLog.d(TAG, "start startPreview hasInitSurface=" + hasInitSurface);
        //ICOM-4274 begin add 20170906 b.jiang
        if (hasInitSurface == false) {
            return;
        }
        if (panoramaPreviewPlayback == null) {
            AppLog.d(TAG, "null point");
            return;
        }
        if (curCamera.isStreamReady) {
            return;
        }
        boolean isSupportPreview = cameraProperties.isSupportPreview();
        AppLog.d(TAG, "start startPreview isSupportPreview=" + isSupportPreview);
        if (!isSupportPreview) {
            previewHandler.post(new Runnable() {
                @Override
                public void run() {
                    previewView.setSupportPreviewTxvVisibility(View.VISIBLE);
                }
            });
            return;
        }
        //ICOM-4274 end add 20170906 b.jiang

        if (AppInfo.enableDumpVideo) {
            String streamOutputPath = Environment.getExternalStorageDirectory().toString() + AppInfo.STREAM_OUTPUT_DIRECTORY_PATH;
            FileOper.createDirectory(streamOutputPath);
            try {
                ICatchPancamConfig.getInstance().enableDumpTransportStream(true, streamOutputPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int cacheTime = cameraProperties.getPreviewCacheTime();
        //cache 設置太小會造成畫面不順暢；
//        if (cacheTime > 0 && cacheTime < 200) {
//            cacheTime = 200;
//        }

        cacheTime = 400;
        AppLog.d(TAG,"setPreviewCacheParam cacheTime:" +cacheTime);
        ICatchPancamConfig.getInstance().setPreviewCacheParam(cacheTime,200);
//        ICatchPancamConfig.getInstance().enableRTPOverTCP();
        ICatchStreamParam iCatchStreamParam = getStreamParam();

        Tristate retValue;
        if (AppInfo.enableRender) {
            if (PanoramaTools.isPanorama(iCatchStreamParam.getWidth(),iCatchStreamParam.getHeight())) {
                registerGyroscopeSensor();
            }
            retValue = panoramaPreviewPlayback.start(iCatchStreamParam, !AppInfo.disableAudio);
        } else {
            retValue = cameraStreaming.start(iCatchStreamParam, !AppInfo.disableAudio);
        }

        if (retValue == Tristate.FALSE) {
            iCatchStreamParam = new ICatchCustomerStreamParam(554 ,"MJPG?W=640&H=360&Q=50&BR=5000000");
            if (AppInfo.enableRender) {
                retValue = panoramaPreviewPlayback.start(iCatchStreamParam, !AppInfo.disableAudio);
            } else {
                retValue = cameraStreaming.start(iCatchStreamParam, !AppInfo.disableAudio);
            }

            if (retValue == Tristate.NORMAL) {
                defaultStreamParam = iCatchStreamParam;
            }
        }

        if (retValue == Tristate.NORMAL) {
            curCamera.isStreamReady = true;
        } else {
            curCamera.isStreamReady = false;
        }

        final Tristate finalRetValue = retValue;
        previewHandler.post(new Runnable() {
            @Override
            public void run() {
                if (finalRetValue == Tristate.ABNORMAL) {
                    previewView.setSupportPreviewTxvVisibility(View.VISIBLE);
                } else if (finalRetValue == Tristate.NORMAL) {
                    previewView.setSupportPreviewTxvVisibility(View.GONE);
                } else {
                    previewView.setSupportPreviewTxvVisibility(View.GONE);
                    MyToast.show(activity, R.string.open_preview_failed);
                }
            }
        });

        AppLog.d(TAG, "end startPreview retValue=" + retValue);
    }

    private ICatchStreamParam getStreamParam() {
        StreamInfo streamInfo = null;
        if (curCamera.getCameraType() == CameraType.USB_CAMERA) {
            streamInfo = new StreamInfo(curCodecType, curVideoWidth, curVideoHeight, 5000000, curVideoFps);
            AppLog.d(TAG, "start startPreview videoWidth=" + curVideoWidth + " videoHeight=" + curVideoHeight + " videoFps=" + curVideoFps + " curCodecType=" +
                    curCodecType);
        } else {
            String streamUrl = cameraProperties.getCurrentStreamInfo();
            AppLog.d(TAG, " start startStreamAndPreview streamUrl=[" + streamUrl + "]");
            if (streamUrl != null) {
                streamInfo = StreamInfoConvert.convertToStreamInfoBean(streamUrl);
            }
        }
        ICatchStreamParam iCatchStreamParam = null;
        if (streamInfo == null) {
            if (defaultStreamParam != null) {
                iCatchStreamParam = defaultStreamParam;
            } else {
                iCatchStreamParam = new ICatchH264StreamParam(1280, 720, 30);
            }
//            iCatchStreamParam = new ICatchH264StreamParam(1920, 960, 30);
        } else if (streamInfo.mediaCodecType.equals("MJPG")) {
            iCatchStreamParam = new ICatchJPEGStreamParam(streamInfo.width, streamInfo.height, streamInfo.fps, streamInfo.bitrate);
        } else if (streamInfo.mediaCodecType.equals("H264")) {
            iCatchStreamParam = new ICatchH264StreamParam(streamInfo.width, streamInfo.height, streamInfo.fps, streamInfo.bitrate);
        } else {
            iCatchStreamParam = new ICatchH264StreamParam(1280, 720, 30);
//            iCatchStreamParam = new ICatchH264StreamParam(1920, 960, 30);
        }

        return iCatchStreamParam;
    }

    public void stopPreview() {
        if (AppInfo.enableDumpVideo) {
            ICatchPancamConfig.getInstance().disableDumpTransportStream(true);
        }

        if (AppInfo.enableRender) {
            removeGyroscopeListener();
            if (panoramaPreviewPlayback != null && curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                panoramaPreviewPlayback.stop();
            }
        } else {
            if (curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                cameraStreaming.stop();
            }
        }
    }

    public void locate(float progerss) {
        panoramaPreviewPlayback.locate(progerss);
    }

    //pancamGLRelease surface;

    public void savePvThumbnail() {
        if (curCamera != null && panoramaPreviewPlayback != null && curCamera.isStreamReady) {
            Bitmap bitmap = panoramaPreviewPlayback.getPvThumbnail();
            if (bitmap != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
//        AppLog.d(TAG, "bitmapToByteArray bitmap size=" + thumbnailBitmap.getByteCount());
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);//把bitmap100%高质量压缩 到 output对象里
                byte[] result = output.toByteArray();//转换成功了
                try {
                    output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CameraSlotSQLite.getInstance().update(new CameraSlot(curCamera.getPosition(), true, curCamera.getCameraName(), curCamera.getCameraType(), result, true));
            }
        }
    }
    public void destroyPreview() {
//        removePanoramaEventListener();
        if (AppInfo.enableDumpVideo) {
            ICatchPancamConfig.getInstance().disableDumpTransportStream(true);
        }
        hasInitSurface = false;
        if (AppInfo.enableRender) {
            removeGyroscopeListener();
            if (panoramaPreviewPlayback != null && curCamera.isStreamReady) {
                if (iCatchSurfaceContext != null) {
                    AppLog.d(TAG, "destroyPreview.....");
                    panoramaPreviewPlayback.removeSurface(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE, iCatchSurfaceContext);
                }
                panoramaPreviewPlayback.stop();
                panoramaPreviewPlayback.release();
                curCamera.isStreamReady = false;
            }
        } else {
            if (curCamera.isStreamReady) {
                curCamera.isStreamReady = false;
                cameraStreaming.stop();
            }
        }
    }

    public void rotateB(MotionEvent e, float prevX, float prevY) {
        ICatchGLPoint prev = new ICatchGLPoint(prevX, prevY);
        ICatchGLPoint curr = new ICatchGLPoint(e.getX(), e.getY());
        panoramaPreviewPlayback.rotate(prev, curr);
    }

    public void onSurfaceViewTouchDown(MotionEvent event) {
        touchMode = TouchMode.DRAG;
        mPreviousY = event.getY();
        mPreviousX = event.getX();
        beforeLenght = 0;
        afterLenght = 0;
    }

    public void onSurfaceViewPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            touchMode = TouchMode.ZOOM;
            beforeLenght = getDistance(event);//
        }
    }

    public void onSurfaceViewTouchMove(MotionEvent event) {
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
        showZoomView();
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
//            AppLog.d(TAG, "onSensorChanged speedX=" + speedX + " speedY=" +speedY + " speedZ=" + speedZ);
            if (Math.abs(speedY) < 0.05 && Math.abs(speedZ) < 0.05) {
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
        panoramaPreviewPlayback.rotate(rotation, speedX, speedY, speedZ, timestamp);
    }

    private void registerGyroscopeSensor() {
        AppLog.d(TAG, "registerGyroscopeSensor");
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

    public void setDrawingArea(int width, int height) {
        if (panoramaPreviewPlayback != null && iCatchSurfaceContext != null) {
            AppLog.d(TAG, "start setDrawingArea width=" + width + " height=" + height);
            try {
                iCatchSurfaceContext.setViewPort(0, 0, width, height);
            } catch (IchGLSurfaceNotSetException e) {
                e.printStackTrace();
            }
            AppLog.d(TAG, "end setDrawingArea");
        }
    }

    public void initSurface(SurfaceHolder surfaceHolder) {
        hasInitSurface = false;
        AppLog.i(TAG, "begin initSurface");
        if (panoramaPreviewPlayback == null) {
            return;
        }
        if (AppInfo.enableRender) {
            iCatchSurfaceContext = new ICatchSurfaceContext(surfaceHolder.getSurface());
            ICatchStreamParam iCatchStreamParam = getStreamParam();
            if (iCatchStreamParam!= null&& PanoramaTools.isPanorama(iCatchStreamParam.getWidth(),iCatchStreamParam.getHeight())) {
                panoramaPreviewPlayback.enableGLRender();
                panoramaPreviewPlayback.init(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
                panoramaPreviewPlayback.setSurface(ICatchGLSurfaceType.ICH_GL_SURFACE_TYPE_SPHERE, iCatchSurfaceContext);
                previewView.setPanoramaTypeBtnVisibility(View.VISIBLE);
            } else {
                panoramaPreviewPlayback.enableCommonRender(iCatchSurfaceContext);
                previewView.setPanoramaTypeBtnVisibility(View.GONE);
            }
        } else {
            previewView.setPanoramaTypeBtnVisibility(View.GONE);
            cameraStreaming.disnableRender();
            int width = previewView.getSurfaceViewWidth();
            int heigth = previewView.getSurfaceViewHeight();
            AppLog.i(TAG, "SurfaceViewWidth=" + width + " SurfaceViewHeight=" + heigth);
            if (width <= 0 || heigth <= 0) {
                width = 1080;
                heigth = 1920;
            }
            cameraStreaming.setSurface(surfaceHolder);
            cameraStreaming.setViewParam(width, heigth);
        }
        hasInitSurface = true;
        AppLog.i(TAG, "end initSurface");
    }

    public void setPanoramaType() {
        if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE) {
            panoramaPreviewPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID;
            previewView.setPanoramaTypeBtnSrc(R.drawable.asteroid);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }  if (curPanoramaType == ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_ASTEROID) {
            panoramaPreviewPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_VIRTUAL_R;
            previewView.setPanoramaTypeBtnSrc(R.drawable.vr);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            panoramaPreviewPlayback.changePanoramaType(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            curPanoramaType = ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE;
            previewView.setPanoramaTypeBtnSrc(R.drawable.panorama);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }
}
