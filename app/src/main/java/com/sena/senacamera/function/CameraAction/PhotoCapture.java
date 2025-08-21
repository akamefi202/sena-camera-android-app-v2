package com.sena.senacamera.function.CameraAction;

import static com.sena.senacamera.data.PropertyId.PropertyId.CAPTURE_DELAY_MODE;

import android.content.Context;
import android.media.MediaPlayer;

import com.icatchtek.control.customer.type.ICatchCamBurstNumber;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.data.GlobalApp.GlobalInfo;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.SdkApi.CameraProperties;
import com.icatchtek.control.customer.type.ICatchCamProperty;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhang yanhu C001012 on 2015/12/30 13:22.
 */
public class PhotoCapture {
    private static final String TAG = PhotoCapture.class.getSimpleName();

    private Context context;
    private MediaPlayer stillCaptureStartBeep;
    private MediaPlayer delayBeep;
    private MediaPlayer continuousCaptureBeep;
    private OnStopPreviewListener onStopPreviewListener;
    private OnCaptureListener onCaptureListener;
    private static final int TYPE_BURST_CAPTURE = 1;
    private static final int TYPE_NORMAL_CAPTURE = 2;
    private CameraProperties cameraProperties;
    private CameraAction cameraAction;
    private BaseProperties baseProperties;

    public PhotoCapture(Context context) {
        this.context = context;
        stillCaptureStartBeep = MediaPlayer.create(context, R.raw.captureshutter);
        delayBeep = MediaPlayer.create(context, R.raw.delay_beep);
        continuousCaptureBeep = MediaPlayer.create(context, R.raw.captureburst);
        baseProperties = CameraManager.getInstance().getCurCamera().getBaseProperties();
        cameraProperties = CameraManager.getInstance().getCurCamera().getCameraProperties();
        cameraAction = CameraManager.getInstance().getCurCamera().getCameraAction();
    }

    public void startCapture() {
        new CaptureThread().run();
    }

    public int getBurstCount() {
        int currentBurstNumber = cameraProperties.getCurrentBurstNumber();
        if (currentBurstNumber == ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_3) {
            return 3;
        } else if (currentBurstNumber == ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_5) {
            return 5;
        } else if (currentBurstNumber == ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_7) {
            return 7;
        } else if (currentBurstNumber == ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_10) {
            return 10;
        } else if (currentBurstNumber == ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_15) {
            return 15;
        } else if (currentBurstNumber == ICatchCamBurstNumber.ICH_CAM_BURST_NUMBER_30) {
            return 30;
        }
        return 1;
    }

    class CaptureThread implements Runnable {
        @Override
        public void run() {
            //long lastTime = System.currentTimeMillis();
            AppLog.i(TAG, "start CameraCaptureThread");
            //notify stopMPreview preview
            //JIRA BUG IC-564 Begin modify by b.jiang 2016-8-16
//            CameraProperties.getInstance().getCurrentCaptureDelay();
            //check property support then setting the value.
            int delayTime = 0;
            // if current photo mode is self-timer, get delay time
            if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_CAPTURE_DELAY) && baseProperties.getPhotoMode().getCurrentUiStringInSetting().equals(context.getResources().getString(R.string.photo_mode_self_timer))) {
                delayTime = cameraProperties.getCurrentCaptureDelay();
            }
            if (delayTime < 1000) {//ms
                onStopPreviewListener.onStop();
            } else if (cameraProperties.hasFunction(CAPTURE_DELAY_MODE)) {
                // do not stopMPreview media stream and preview right now
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        onStopPreviewListener.onStop();
                    }
                };
                Timer timer = new Timer(true);
                timer.schedule(task, delayTime - 500);
            } else {
                onStopPreviewListener.onStop();
            }

            //start capture audio
            int needCaptureCount = 1;
            // if current photo mode is burst, get burst capture count
            if (cameraProperties.hasFunction(ICatchCamProperty.ICH_CAM_CAP_BURST_NUMBER) && baseProperties.getPhotoMode().getCurrentUiStringInSetting().equals(context.getResources().getString(R.string.photo_mode_burst))) {
                needCaptureCount = getBurstCount();
            }
            if (needCaptureCount == 1) {
                CaptureAudioTask captureAudioTask = new CaptureAudioTask(needCaptureCount, TYPE_NORMAL_CAPTURE);
                Timer captureAudioTimer = new Timer(true);
//                captureAudioTimer.schedule(captureAudioTask, delayTime, 200);
                captureAudioTimer.schedule(captureAudioTask, delayTime);
            } else {
                CaptureAudioTask captureAudioTask = new CaptureAudioTask(needCaptureCount, TYPE_BURST_CAPTURE);
                Timer captureAudioTimer = new Timer(true);
                captureAudioTimer.schedule(captureAudioTask, delayTime, 420);
            }

            // start delay audio
            int count = delayTime / 1000;
            int timerDelay = 0;
            if (delayTime >= 5000) {
                Timer delayTimer = new Timer(true);
                DelayTimerTask delayTimerTask = new DelayTimerTask(count / 2, delayTimer);
                delayTimer.schedule(delayTimerTask, 0, 1000);
                timerDelay = delayTime;
            } else {
                timerDelay = 0;
                count = delayTime / 500;
            }
            if (delayTime >= 3000) {
                Timer delayTimer1 = new Timer(true);
                DelayTimerTask delayTimerTask1 = new DelayTimerTask(count / 2, delayTimer1);
                delayTimer1.schedule(delayTimerTask1, timerDelay / 2, 500);
                timerDelay = delayTime;
            } else {
                timerDelay = 0;
                count = delayTime / 250;
            }
            Timer delayTimer2 = new Timer(true);
            DelayTimerTask delayTimerTask2 = new DelayTimerTask(count, delayTimer2);
            delayTimer2.schedule(delayTimerTask2, timerDelay - timerDelay / 4, 250);
            cameraAction.triggerCapturePhoto();
            if (onCaptureListener != null) {
                onCaptureListener.onCompleted();
            }
            AppLog.i(TAG, "delayTime = " + delayTime + " needCaptureCount=" + needCaptureCount);
            AppLog.i(TAG, "end CameraCaptureThread");
        }
    }

    public void addOnStopPreviewListener(OnStopPreviewListener onStopPreviewListener) {
        this.onStopPreviewListener = onStopPreviewListener;
    }

    public interface OnStopPreviewListener {
        void onStop();
    }

    public void setOnCaptureListener(OnCaptureListener onCaptureListener) {
        this.onCaptureListener = onCaptureListener;
    }

    public interface OnCaptureListener {
        void onCompleted();
    }


    private class CaptureAudioTask extends TimerTask {
        private int burstNumber;
        private int type = TYPE_NORMAL_CAPTURE;

        public CaptureAudioTask(int burstNumber, int type) {
            this.burstNumber = burstNumber;
            this.type = type;
        }

        @Override
        public void run() {
            if (type == TYPE_NORMAL_CAPTURE) {
                if (burstNumber > 0) {
                    AppLog.i(TAG, "CaptureAudioTask remainBurstNumber =" + burstNumber);
                    stillCaptureStartBeep.start();
                    burstNumber --;
                } else {
                    cancel();
                }
            } else {
                if (burstNumber > 0) {
                    AppLog.i(TAG, "CaptureAudioTask remainBurstNumber =" + burstNumber);
                    continuousCaptureBeep.start();
                    burstNumber --;
                } else {
                    cancel();
                }
            }

        }
    }

    private class DelayTimerTask extends TimerTask {
        private int count;
        private Timer timer;

        public DelayTimerTask(int count, Timer timer) {
            this.count = count;
            this.timer = timer;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (count-- > 0) {
                delayBeep.start();
            } else {
                if (timer != null) {
                    timer.cancel();
                }
            }
        }
    }

}
