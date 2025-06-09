package com.sena.senacamera.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.Presenter.PreviewPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.adapter.SettingListAdapter;
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.data.Mode.PreviewMode;
import com.sena.senacamera.ui.Interface.PreviewView;
import com.sena.senacamera.utils.ClickUtils;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener,  PreviewView {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private ImageButton closeButton, settingButton, mediaButton, preferenceButton, cameraModeButton, shutterButton;
    private CameraMode cameraMode;
    private SurfaceView mSurfaceView;
    private PreviewPresenter presenter;
    private LinearLayout cameraPhotoStatus, cameraVideoStatus, photoModeLayout, videoModeLayout, recordingTimeLayout, topBarLayout, cameraZoomLayout, cameraModeLayout, cameraStatusLayout;
    private TextView photoLimitText, videoLimitText, recordingTimeText, cameraZoomOneButton, cameraZoomOneHalfButton, cameraZoomTwoButton, cameraZoomTwoHalfButton, cameraZoomThreeButton, photoModeSingleButton, photoModeBurstButton, photoModeTimelapseButton, photoModeSelfTimerButton, videoModeVideoButton, videoModeSlowMotionButton, videoModeTimelapseButton, videoModeLoopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview);

        presenter = new PreviewPresenter(PreviewActivity.this);
        presenter.setView(this);

        closeButton = findViewById(R.id.close_button);
        settingButton = findViewById(R.id.setting_button);
        mediaButton = findViewById(R.id.media_button);
        preferenceButton = findViewById(R.id.preference_button);
        cameraModeButton = findViewById(R.id.camera_mode_button);
        mSurfaceView = findViewById(R.id.camera_preview);
        shutterButton = findViewById(R.id.shutter_button);
        cameraPhotoStatus = findViewById(R.id.camera_photo_status);
        cameraVideoStatus = findViewById(R.id.camera_video_status);
        photoLimitText = findViewById(R.id.photo_limit_text);
        videoLimitText = findViewById(R.id.video_limit_text);
        photoModeLayout = findViewById(R.id.photo_mode_layout);
        videoModeLayout = findViewById(R.id.video_mode_layout);
        recordingTimeLayout = findViewById(R.id.recording_time_layout);
        recordingTimeText = findViewById(R.id.recording_time_text);
        topBarLayout = findViewById(R.id.top_bar_layout);
        cameraStatusLayout = findViewById(R.id.camera_status_layout);
        cameraZoomLayout = findViewById(R.id.camera_zoom_layout);
        cameraModeLayout = findViewById(R.id.camera_mode_layout);
        cameraZoomOneButton = findViewById(R.id.camera_zoom_1_0_button);
        cameraZoomOneHalfButton = findViewById(R.id.camera_zoom_1_5_button);
        cameraZoomTwoButton = findViewById(R.id.camera_zoom_2_0_button);
        cameraZoomTwoHalfButton = findViewById(R.id.camera_zoom_2_5_button);
        cameraZoomThreeButton = findViewById(R.id.camera_zoom_3_0_button);
        photoModeSingleButton = findViewById(R.id.photo_mode_single_button);
        photoModeBurstButton = findViewById(R.id.photo_mode_burst_button);
        photoModeTimelapseButton = findViewById(R.id.photo_mode_timelapse_button);
        photoModeSelfTimerButton = findViewById(R.id.photo_mode_self_timer_button);
        videoModeVideoButton = findViewById(R.id.video_mode_video_button);
        videoModeSlowMotionButton = findViewById(R.id.video_mode_slow_motion_button);
        videoModeTimelapseButton = findViewById(R.id.video_mode_timelapse_button);
        videoModeLoopButton = findViewById(R.id.video_mode_loop_button);

        cameraMode = CameraMode.PHOTO;

        closeButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        mediaButton.setOnClickListener(this);
        preferenceButton.setOnClickListener(this);
        cameraZoomOneButton.setOnClickListener(this);
        cameraZoomOneHalfButton.setOnClickListener(this);
        cameraZoomTwoButton.setOnClickListener(this);
        cameraZoomTwoHalfButton.setOnClickListener(this);
        cameraZoomThreeButton.setOnClickListener(this);
        photoModeSingleButton.setOnClickListener(this);
        photoModeBurstButton.setOnClickListener(this);
        photoModeTimelapseButton.setOnClickListener(this);
        photoModeSelfTimerButton.setOnClickListener(this);
        videoModeVideoButton.setOnClickListener(this);
        videoModeSlowMotionButton.setOnClickListener(this);
        videoModeTimelapseButton.setOnClickListener(this);
        videoModeLoopButton.setOnClickListener(this);

        cameraModeButton.setOnClickListener(v -> toggleCameraMode());
        shutterButton.setOnClickListener(v -> onShutterPressed());

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated!!!");
                presenter.initSurface(mSurfaceView.getHolder());
                presenter.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AppLog.d(TAG, "surfaceChanged!!!");
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                presenter.destroyPreview();
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        presenter.onSufaceViewTouchDown(event);
                        break;
                    // 多点触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSufaceViewPointerDown(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        presenter.onSufaceViewTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSufaceViewTouchUp();
                        break;

                    // 多点松开
                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSufaceViewTouchPointerUp();
                        break;
                }
                return true;
            }
        });
    }

    private void toggleCameraMode() {
        if (cameraMode == CameraMode.PHOTO) {
            // change to video mode
            cameraMode = CameraMode.VIDEO;
            cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_video, (Resources.Theme) null));
            shutterButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shutter_video, (Resources.Theme) null));

            photoLimitText.setVisibility(View.GONE);
            cameraPhotoStatus.setVisibility(View.GONE);
            photoModeLayout.setVisibility(View.GONE);
            videoLimitText.setVisibility(View.VISIBLE);
            cameraVideoStatus.setVisibility(View.VISIBLE);
            videoModeLayout.setVisibility(View.VISIBLE);

            presenter.changePreviewMode(PreviewMode.APP_STATE_VIDEO_MODE);
        } else {
            // cameraMode is CameraMode.VIDEO
            // change to photo mode
            cameraMode = CameraMode.PHOTO;
            cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_photo, (Resources.Theme) null));
            shutterButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shutter_photo, (Resources.Theme) null));

            photoLimitText.setVisibility(View.VISIBLE);
            cameraPhotoStatus.setVisibility(View.VISIBLE);
            photoModeLayout.setVisibility(View.VISIBLE);
            videoLimitText.setVisibility(View.GONE);
            cameraVideoStatus.setVisibility(View.GONE);
            videoModeLayout.setVisibility(View.GONE);

            presenter.changePreviewMode(PreviewMode.APP_STATE_STILL_MODE);
        }
    }

    private void onShutterPressed() {
        AppLog.i(TAG, "shutter button is pressed");
        if (!ClickUtils.isFastDoubleClick(R.id.shutter_button)) {
            presenter.startOrStopCapture();
        }
    }

    @Override
    public void onClick(View v) {
        // combination of onclick listeners of buttons
        int id = v.getId();
        AppLog.i(TAG, "pressed button is " + id);

        if (id == R.id.media_button) {
            AppLog.i(TAG, "media button is pressed");
            if (!ClickUtils.isFastDoubleClick(R.id.media_button)) {
                presenter.redirectToAnotherActivity(PreviewActivity.this, MediaActivity.class);
            }
        } else if (id == R.id.close_button) {
            // back to main activity
            presenter.finishActivity();
        } else if (id == R.id.setting_button) {
            AppLog.i(TAG, "setting button is pressed");
            if (!ClickUtils.isFastDoubleClick(R.id.setting_button)) {
                presenter.redirectToAnotherActivity(PreviewActivity.this, SettingActivity.class);
            }
        } else if (id == R.id.preference_button) {
            AppLog.i(TAG, "preference button is pressed");
            if (!ClickUtils.isFastDoubleClick(R.id.preference_button)) {
                presenter.redirectToAnotherActivity(PreviewActivity.this, PreferenceActivity.class);
            }
        } else if (id == R.id.camera_zoom_1_0_button || id == R.id.camera_zoom_1_5_button || id == R.id.camera_zoom_2_0_button || id == R.id.camera_zoom_2_5_button || id == R.id.camera_zoom_3_0_button) {
            // set style of zoom buttons
            cameraZoomOneButton.setTextColor(getResources().getColor(R.color.white));
            cameraZoomOneButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag, (Resources.Theme) null));
            cameraZoomOneHalfButton.setTextColor(getResources().getColor(R.color.white));
            cameraZoomOneHalfButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag, (Resources.Theme) null));
            cameraZoomTwoButton.setTextColor(getResources().getColor(R.color.white));
            cameraZoomTwoButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag, (Resources.Theme) null));
            cameraZoomTwoHalfButton.setTextColor(getResources().getColor(R.color.white));
            cameraZoomTwoHalfButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag, (Resources.Theme) null));
            cameraZoomThreeButton.setTextColor(getResources().getColor(R.color.white));
            cameraZoomThreeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag, (Resources.Theme) null));

            if (id == R.id.camera_zoom_1_0_button) {
                cameraZoomOneButton.setTextColor(getResources().getColor(R.color.black));
                cameraZoomOneButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag_selected, (Resources.Theme) null));
            } else if (id == R.id.camera_zoom_1_5_button) {
                cameraZoomOneHalfButton.setTextColor(getResources().getColor(R.color.black));
                cameraZoomOneHalfButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag_selected, (Resources.Theme) null));
            } else if (id == R.id.camera_zoom_2_0_button) {
                cameraZoomTwoButton.setTextColor(getResources().getColor(R.color.black));
                cameraZoomTwoButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag_selected, (Resources.Theme) null));
            } else if (id == R.id.camera_zoom_2_5_button) {
                cameraZoomTwoHalfButton.setTextColor(getResources().getColor(R.color.black));
                cameraZoomTwoHalfButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag_selected, (Resources.Theme) null));
            } else if (id == R.id.camera_zoom_3_0_button) {
                cameraZoomThreeButton.setTextColor(getResources().getColor(R.color.black));
                cameraZoomThreeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag_selected, (Resources.Theme) null));
            }
        } else if (id == R.id.photo_mode_single_button || id == R.id.photo_mode_burst_button || id == R.id.photo_mode_timelapse_button || id == R.id.photo_mode_self_timer_button) {
            // set style of photo mode buttons
            photoModeSingleButton.setTextColor(getResources().getColor(R.color.white));
            photoModeBurstButton.setTextColor(getResources().getColor(R.color.white));
            photoModeTimelapseButton.setTextColor(getResources().getColor(R.color.white));
            photoModeSelfTimerButton.setTextColor(getResources().getColor(R.color.white));

            if (id == R.id.photo_mode_single_button) {
                photoModeSingleButton.setTextColor(getResources().getColor(R.color.yellow));
            } else if (id == R.id.photo_mode_burst_button) {
                photoModeBurstButton.setTextColor(getResources().getColor(R.color.yellow));
            } else if (id == R.id.photo_mode_timelapse_button) {
                photoModeTimelapseButton.setTextColor(getResources().getColor(R.color.yellow));
            } else if (id == R.id.photo_mode_self_timer_button) {
                photoModeSelfTimerButton.setTextColor(getResources().getColor(R.color.yellow));
            }
        } else if (id == R.id.video_mode_video_button || id == R.id.video_mode_slow_motion_button || id == R.id.video_mode_timelapse_button || id == R.id.video_mode_loop_button) {
            // set style of video mode buttons
            videoModeVideoButton.setTextColor(getResources().getColor(R.color.white));
            videoModeSlowMotionButton.setTextColor(getResources().getColor(R.color.white));
            videoModeTimelapseButton.setTextColor(getResources().getColor(R.color.white));
            videoModeLoopButton.setTextColor(getResources().getColor(R.color.white));

            if (id == R.id.video_mode_video_button) {
                videoModeVideoButton.setTextColor(getResources().getColor(R.color.yellow));
            } else if (id == R.id.video_mode_slow_motion_button) {
                videoModeSlowMotionButton.setTextColor(getResources().getColor(R.color.yellow));
            } else if (id == R.id.video_mode_timelapse_button) {
                videoModeTimelapseButton.setTextColor(getResources().getColor(R.color.yellow));
            } else if (id == R.id.video_mode_loop_button) {
                videoModeLoopButton.setTextColor(getResources().getColor(R.color.yellow));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLog.d(TAG, "onResume");
        presenter.submitAppInfo();
        presenter.initPreview();
        presenter.initStatus();
        presenter.addEvent();
//        AppDialog.showDialogWarn( PanoramaPreviewActivity.this, R.string.text_preview_hint_info );
    }

    @Override
    protected void onStop() {
        AppLog.d(TAG, "onStop");
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                AppLog.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                AppLog.d("AppStart", "back");
                presenter.finishActivity();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        AppLog.d(TAG, "onDestroy");
        super.onDestroy();
        presenter.removeActivity();
        presenter.destroyPreview();
        presenter.delEvent();
        presenter.disconnectCamera();
        presenter.delConnectFailureListener();
        presenter.unregisterWifiSSReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.redrawSurface();
            }
        }, 200);
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }

    @Override
    public void setWbStatusVisibility(int visibility) {

    }

    @Override
    public void setBurstStatusVisibility(int visibility) {

    }

    @Override
    public void setWifiStatusVisibility(int visibility) {

    }

    @Override
    public void setWifiIcon(int drawableId) {

    }

    @Override
    public void setBatteryStatusVisibility(int visibility) {

    }

    @Override
    public void setBatteryIcon(int drawableId) {

    }

    @Override
    public void settimeLapseModeVisibility(int visibility) {

    }

    @Override
    public void settimeLapseModeIcon(int drawableId) {

    }

    @Override
    public void setSlowMotionVisibility(int visibility) {

    }

    @Override
    public void setCarModeVisibility(int visibility) {

    }

    @Override
    public void setRecordingTimeVisibility(int visibility) {
        recordingTimeLayout.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            // hide all elements except recordingTimeLayout
            topBarLayout.setVisibility(View.INVISIBLE);
            cameraStatusLayout.setVisibility(View.INVISIBLE);
            cameraZoomLayout.setVisibility(View.INVISIBLE);
            cameraModeLayout.setVisibility(View.INVISIBLE);
            mediaButton.setVisibility(View.INVISIBLE);
            preferenceButton.setVisibility(View.INVISIBLE);
        } else {
            // visibility is View.GONE
            // show all elements except recordingTimeLayout
            topBarLayout.setVisibility(View.VISIBLE);
            cameraStatusLayout.setVisibility(View.VISIBLE);
            cameraZoomLayout.setVisibility(View.VISIBLE);
            cameraModeLayout.setVisibility(View.VISIBLE);
            mediaButton.setVisibility(View.VISIBLE);
            preferenceButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAutoDownloadVisibility(int visibility) {

    }

    @Override
    public void setCaptureBtnBackgroundResource(int id) {
        shutterButton.setBackground(ResourcesCompat.getDrawable(getResources(), id, (Resources.Theme) null));
    }

    @Override
    public void setRecordingTime(String time) {
        recordingTimeText.setText(time);
    }

    @Override
    public void setDelayCaptureLayoutVisibility(int visibility) {

    }

    @Override
    public void setDelayCaptureTextTime(String delayCaptureTime) {

    }

    @Override
    public void setImageSizeLayoutVisibility(int visibility) {

    }

    @Override
    public void setRemainCaptureCount(String remainCaptureCount) {
        photoLimitText.setText(remainCaptureCount);
    }

    @Override
    public void setVideoSizeLayoutVisibility(int visibility) {

    }

    @Override
    public void setRemainRecordingTimeText(String remainRecordingTime) {
        videoLimitText.setText(remainRecordingTime);
    }

    @Override
    public void setBurstStatusIcon(int drawableId) {

    }

    @Override
    public void setWbStatusIcon(int drawableId) {

    }

    @Override
    public void setUpsideVisibility(int visibility) {

    }

    @Override
    public void setCaptureBtnEnability(boolean value) {
        shutterButton.setEnabled(value);
    }

    @Override
    public void setVideoSizeInfo(String sizeInfo) {

    }

    @Override
    public void setImageSizeInfo(String sizeInfo) {

    }

    @Override
    public void showZoomView() {

    }

    @Override
    public void hideZoomView() {

    }

    @Override
    public void setMaxZoomRate(float maxZoomRate) {

    }

    @Override
    public float getZoomViewProgress() {
        return 0;
    }

    @Override
    public float getZoomViewMaxZoomRate() {
        return 0;
    }

    @Override
    public void updateZoomViewProgress(float currentZoomRatio) {

    }

    @Override
    public void setSettingMenuListAdapter(SettingListAdapter settingListAdapter) {

    }

    @Override
    public int getSetupMainMenuVisibility() {
        return 0;
    }

    @Override
    public void setSetupMainMenuVisibility(int visibility) {

    }

    @Override
    public void setAutoDownloadBitmap(Bitmap bitmap) {

    }

    @Override
    public void setActionBarTitle(int resId) {

    }

    @Override
    public void setSettingBtnVisible(boolean isVisible) {

    }

    @Override
    public void setBackBtnVisibility(boolean isVisible) {

    }

    @Override
    public void setSupportPreviewTxvVisibility(int visibility) {

    }

    @Override
    public void setPvModeBtnBackgroundResource(int drawableId) {

    }

    @Override
    public void showPopupWindow(int curMode) {

    }

    @Override
    public void setTimepLapseRadioBtnVisibility(int visibility) {

    }

    @Override
    public void setCaptureRadioBtnVisibility(int visibility) {

    }

    @Override
    public void setVideoRadioBtnVisibility(int visibility) {

    }

    @Override
    public void setTimepLapseRadioChecked(boolean checked) {

    }

    @Override
    public void setCaptureRadioBtnChecked(boolean checked) {

    }

    @Override
    public void setVideoRadioBtnChecked(boolean checked) {

    }

    @Override
    public void dismissPopupWindow() {

    }

    @Override
    public void setMinZoomRate(float minZoomRate) {

    }

    @Override
    public void setFacebookBtnTxv(String value) {

    }

    @Override
    public void setYouTubeBtnTxv(String value) {

    }

    @Override
    public void setCustomerLiveBtnTxv(String value) {

    }

    @Override
    public void setFacebookBtnTxv(int resId) {

    }

    @Override
    public void setYouTubeBtnTxv(int resId) {

    }

    @Override
    public void setYouTubeLiveLayoutVisibility(int visibility) {

    }

    @Override
    public int getSurfaceViewWidth() {
        View parentView = (View) mSurfaceView.getParent();
        return parentView.getWidth();
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View) mSurfaceView.getParent();
        return parentView.getHeight();
    }

    @Override
    public void setPanoramaTypeBtnSrc(int srcId) {

    }

    @Override
    public void setPanoramaTypeBtnVisibility(int visibility) {

    }
}