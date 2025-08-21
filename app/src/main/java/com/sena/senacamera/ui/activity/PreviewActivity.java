package com.sena.senacamera.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.function.BaseProperties;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.presenter.PreviewPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.adapter.SettingListAdapter;
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.data.Mode.PreviewMode;
import com.sena.senacamera.ui.Interface.PreviewView;
import com.sena.senacamera.ui.adapter.ShootModeAdapter;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.decoration.CenterSnapHelper;
import com.sena.senacamera.utils.ClickUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener,  PreviewView {
    private static final String TAG = PreviewActivity.class.getSimpleName();

    private ImageButton closeButton, settingsButton, mediaButton, preferenceButton, cameraModeButton, shutterButton;
    private SurfaceView mSurfaceView;
    private PreviewPresenter presenter;
    private LinearLayout cameraVideoStatus, recordingTimeLayout, topBarLayout, cameraZoomLayout, shootModeLayout, cameraStatusLayout;
    private TextView photoLimitText, videoLimitText, recordingTimeText, cameraZoomOneButton, cameraZoomOneHalfButton, cameraZoomTwoButton, cameraZoomTwoHalfButton, cameraZoomThreeButton, batteryPercentText, osdStatusText;
    private ImageView batteryStatusIcon, sdCardStatusIcon, osdStatusIcon;
    private List<TextView> cameraZoomButtonList;
    private RecyclerView shootModeRecyclerView;

    private float zoomRate = 1.0f, maxZoomRate = 3.0f, minZoomRate = 1.0f;
    private boolean isZoomBarExpanded = false;
    private List<String> shootModeList;
    private String currentShootMode;
    private LinearSnapHelper shootModeSnapHelper;
    private ShootModeAdapter shootModeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview);

        presenter = new PreviewPresenter(PreviewActivity.this);
        presenter.setView(this);

        closeButton = findViewById(R.id.close_button);
        settingsButton = findViewById(R.id.settings_button);
        mediaButton = findViewById(R.id.media_button);
        preferenceButton = findViewById(R.id.preference_button);
        cameraModeButton = findViewById(R.id.camera_mode_button);
        mSurfaceView = findViewById(R.id.camera_preview);
        shutterButton = findViewById(R.id.shutter_button);
        cameraVideoStatus = findViewById(R.id.camera_video_status);
        photoLimitText = findViewById(R.id.photo_limit_text);
        videoLimitText = findViewById(R.id.video_limit_text);
        recordingTimeLayout = findViewById(R.id.recording_time_layout);
        recordingTimeText = findViewById(R.id.recording_time_text);
        topBarLayout = findViewById(R.id.top_bar_layout);
        cameraStatusLayout = findViewById(R.id.camera_status_layout);
        cameraZoomLayout = findViewById(R.id.camera_zoom_layout);
        shootModeLayout = findViewById(R.id.shoot_mode_layout);
        shootModeRecyclerView = findViewById(R.id.shoot_mode_recycler_view);
        cameraZoomOneButton = findViewById(R.id.camera_zoom_1_0_button);
        cameraZoomOneHalfButton = findViewById(R.id.camera_zoom_1_5_button);
        cameraZoomTwoButton = findViewById(R.id.camera_zoom_2_0_button);
        cameraZoomTwoHalfButton = findViewById(R.id.camera_zoom_2_5_button);
        cameraZoomThreeButton = findViewById(R.id.camera_zoom_3_0_button);
        batteryPercentText = findViewById(R.id.camera_battery_percent);
        batteryStatusIcon = findViewById(R.id.camera_battery_status);
        sdCardStatusIcon = findViewById(R.id.camera_sd_card_status);
        osdStatusText = findViewById(R.id.camera_osd_text);
        osdStatusIcon = findViewById(R.id.camera_osd_status);

        closeButton.setOnClickListener(this);
        settingsButton.setOnClickListener(this);
        mediaButton.setOnClickListener(this);
        preferenceButton.setOnClickListener(this);
        cameraZoomOneButton.setOnClickListener(this);
        cameraZoomOneHalfButton.setOnClickListener(this);
        cameraZoomTwoButton.setOnClickListener(this);
        cameraZoomTwoHalfButton.setOnClickListener(this);
        cameraZoomThreeButton.setOnClickListener(this);

        // zoom tag list
        cameraZoomButtonList = new ArrayList<>();
        cameraZoomButtonList.add(cameraZoomOneButton);
        cameraZoomButtonList.add(cameraZoomOneHalfButton);
        cameraZoomButtonList.add(cameraZoomTwoButton);
        cameraZoomButtonList.add(cameraZoomTwoHalfButton);
        cameraZoomButtonList.add(cameraZoomThreeButton);

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
                        presenter.onSurfaceViewTouchDown(event);
                        break;
                    // 多点触摸
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSurfaceViewPointerDown(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        presenter.onSurfaceViewTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSurfaceViewTouchUp();
                        break;
                    // 多点松开
                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSurfaceViewTouchPointerUp();
                        break;
                }
                return true;
            }
        });

        // initialize zoom bar
        presenter.zoomBySeekBar();
        updateZoomBarUI();

        // initialize shoot mode recycler view
        updateShootModeList();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        shootModeRecyclerView.setLayoutManager(layoutManager);
        shootModeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && shootModeSnapHelper != null) {
                    View centerView = shootModeSnapHelper.findSnapView(layoutManager);
                    if (centerView != null) {
                        int pos = layoutManager.getPosition(centerView);
                        // select new shoot mode if current shoot mode is different from shoot mode at pos
                        if (!currentShootMode.equals(shootModeList.get(pos))) {
                            selectShootMode(pos);
                            scrollToSelectedShootMode(pos);
                        }
                    }
                }
            }
        });
        shootModeRecyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                int maxFlingVelocity = 1000;
                velocityX = Math.max(Math.min(velocityX, maxFlingVelocity), -maxFlingVelocity);
                return false;
            }
        });
    }

    private void toggleCameraMode() {
        if (presenter.getCurrentCameraMode().equals(CameraMode.PHOTO)) {
            // change to video mode
            presenter.changePreviewMode(PreviewMode.APP_STATE_VIDEO_MODE);
        } else {
            // cameraMode is CameraMode.VIDEO
            // change to photo mode
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
        } else if (id == R.id.settings_button) {
            AppLog.i(TAG, "setting button is pressed");
            if (!ClickUtils.isFastDoubleClick(R.id.settings_button)) {
                presenter.redirectToAnotherActivity(PreviewActivity.this, SettingActivity.class);
            }
        } else if (id == R.id.preference_button) {
            AppLog.i(TAG, "preference button is pressed");
            if (!ClickUtils.isFastDoubleClick(R.id.preference_button)) {
                presenter.redirectToAnotherActivity(PreviewActivity.this, PreferenceActivity.class);
            }
        } else if (id == R.id.camera_zoom_1_0_button || id == R.id.camera_zoom_1_5_button || id == R.id.camera_zoom_2_0_button || id == R.id.camera_zoom_2_5_button || id == R.id.camera_zoom_3_0_button) {
            if (isZoomBarExpanded) {
                int tagIndex = getZoomTagIndex();
                if ((id == R.id.camera_zoom_1_0_button && tagIndex == 0)
                        || (id == R.id.camera_zoom_1_5_button && tagIndex == 1)
                        || (id == R.id.camera_zoom_2_0_button && tagIndex == 2)
                        || (id == R.id.camera_zoom_2_5_button && tagIndex == 3)
                        || (id == R.id.camera_zoom_3_0_button && tagIndex == 4)) {
                    isZoomBarExpanded = false;
                } else {
                    if (id == R.id.camera_zoom_1_0_button) {
                        this.zoomRate = 1.0f;
                    } else if (id == R.id.camera_zoom_1_5_button) {
                        this.zoomRate = 1.5f;
                    } else if (id == R.id.camera_zoom_2_0_button) {
                        this.zoomRate = 2.0f;
                    } else if (id == R.id.camera_zoom_2_5_button) {
                        this.zoomRate = 2.5f;
                    } else {
                        // camera_zoom_3_0_button
                        this.zoomRate = 3.0f;
                    }

                    this.presenter.zoomBySeekBar();
                }
            } else {
                // expand the zoom bar
                isZoomBarExpanded = true;
            }

            this.updateZoomBarUI();
        }
    }

    private void updateZoomBarUI() {
        AppLog.i(TAG, "updateZoomBarUI zoomRate: " + zoomRate);

        // set style of zoom buttons
        for (TextView item: cameraZoomButtonList) {
            item.setTextColor(getResources().getColor(R.color.white));
            item.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag, (Resources.Theme) null));
        }

        if (isZoomBarExpanded) {
            // show all zoom tags and highlight the selected tag
            for (TextView item: cameraZoomButtonList) {
                item.setVisibility(View.VISIBLE);
            }

            int tagIndex = getZoomTagIndex();
            cameraZoomButtonList.get(tagIndex).setTextColor(getResources().getColor(R.color.black));
            cameraZoomButtonList.get(tagIndex).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.rounded_zoom_tag_selected, (Resources.Theme) null));

        } else {
            // show only selected zoom tag
            for (TextView item: cameraZoomButtonList) {
                item.setVisibility(View.GONE);
            }

            cameraZoomButtonList.get(getZoomTagIndex()).setVisibility(View.VISIBLE);
        }
    }

    private int getZoomTagIndex() {
        if (this.zoomRate <= 1.2f) {
            // 1.0 tag
            return 0;
        } else if (this.zoomRate > 1.2f && this.zoomRate <= 1.8f) {
            // 1.5 tag
            return 1;
        } else if (this.zoomRate > 1.8f && this.zoomRate <= 2.2f) {
            // 2.0 tag
            return 2;
        } else if (this.zoomRate > 2.2f && this.zoomRate <= 2.8f) {
            // 2.5 tag
            return 3;
        } else {
            // 3.0 tag
            return 4;
        }
    }

    private void updateShootModeList() {
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "updateShootModeList: camera is disconnected");
            finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        if (presenter.getCurrentCameraMode().equals(CameraMode.PHOTO)) {
            this.shootModeList = Arrays.asList(baseProperties.getPhotoMode().getValueList());
            this.currentShootMode = baseProperties.getPhotoMode().getCurrentUiStringInSetting();
        } else {
            this.shootModeList = Arrays.asList(baseProperties.getVideoMode().getValueList());
            this.currentShootMode = baseProperties.getVideoMode().getCurrentUiStringInSetting();
        }

        shootModeAdapter = new ShootModeAdapter(this, this.shootModeList);
        shootModeRecyclerView.setAdapter(shootModeAdapter);

        if (shootModeSnapHelper == null) {
            shootModeSnapHelper = new LinearSnapHelper();
            shootModeSnapHelper.attachToRecyclerView(shootModeRecyclerView);
        }

        scrollToSelectedShootMode(this.shootModeList.indexOf(this.currentShootMode));
    }

    private void updateOsd() {
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "updateShootModeList: camera is disconnected");
            finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        if (presenter.getCurrentCameraMode().equals(CameraMode.PHOTO)) {
            this.osdStatusText.setVisibility(View.VISIBLE);
            this.osdStatusText.setText(baseProperties.getPhotoResolution().getCurrentUiStringInPreview());
        } else {
            String videoSize = baseProperties.getVideoResolution().getCurrentUiStringInPreview();
            if (videoSize == null) {
                this.osdStatusIcon.setVisibility(View.GONE);
                this.osdStatusText.setVisibility(View.GONE);
                return;
            }

            String[] strings = videoSize.split(" ");
            if (strings == null || strings.length != 2) {
                this.osdStatusIcon.setVisibility(View.GONE);
                this.osdStatusText.setVisibility(View.GONE);
                return;
            }

            String size = strings[0];
            String pts = strings[1];

            this.osdStatusText.setText(pts);
            this.osdStatusText.setVisibility(View.VISIBLE);

            if (size.equals("4K")) {
                this.osdStatusIcon.setImageResource(R.drawable.status_osd_4k);
                this.osdStatusIcon.setVisibility(View.VISIBLE);
            } else if (size.equals("2.7K")) {
                this.osdStatusIcon.setImageResource(R.drawable.status_osd_27k);
                this.osdStatusIcon.setVisibility(View.VISIBLE);
            } else if (size.equals("1080P")) {
                this.osdStatusIcon.setImageResource(R.drawable.status_osd_1080p);
                this.osdStatusIcon.setVisibility(View.VISIBLE);
            } else if (size.equals("720P")) {
                this.osdStatusIcon.setImageResource(R.drawable.status_osd_720p);
                this.osdStatusIcon.setVisibility(View.VISIBLE);
            } else {
                this.osdStatusIcon.setVisibility(View.GONE);
            }
        }
    }

    public void scrollToSelectedShootMode(int position) {
//        AppLog.i(TAG, "scrollToSelectedShootMode position: " + position);
//        shootModeRecyclerView.smoothScrollToPosition(position);
        RecyclerView.ViewHolder vh = shootModeRecyclerView.findViewHolderForAdapterPosition(position);

        if (vh != null) {
            int itemCenterX = vh.itemView.getLeft() + (vh.itemView.getWidth() / 2);
            int recyclerCenterX = shootModeRecyclerView.getWidth() / 2;
            int scrollByX = itemCenterX - recyclerCenterX;

            shootModeRecyclerView.smoothScrollBy(scrollByX, 0);
        } else {
            shootModeRecyclerView.scrollToPosition(position);
            shootModeRecyclerView.post(() -> scrollToSelectedShootMode(position));
        }
    }

    public String getCurrentShootMode() {
        return this.currentShootMode;
    }

    public void selectShootMode(int position) {
        MyCamera myCamera = CameraManager.getInstance().getCurCamera();
        if (myCamera == null || !myCamera.isConnected()) {
            AppLog.e(TAG, "selectShootMode: camera is disconnected");
            finish();
            return;
        }
        BaseProperties baseProperties = myCamera.getBaseProperties();

        if (!this.shootModeList.get(position).equals(this.currentShootMode)) {
            this.currentShootMode = this.shootModeList.get(position);
            this.shootModeAdapter.notifyDataSetChanged();

            MyProgressDialog.showProgressDialog(this, R.string.action_processing);
            new Thread(new Runnable() {
                public void run() {
                    presenter.stopPreview();
                    if (presenter.getCurrentCameraMode().equals(CameraMode.PHOTO)) {
                        // photo
                        baseProperties.getPhotoMode().setValueByPosition(position);
                    } else {
                        // video
                        baseProperties.getVideoMode().setValueByPosition(position);
                    }
                    presenter.startPreview();

                    MyProgressDialog.closeProgressDialog();
                }
            }).start();
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

        updateOsd();
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
                AppLog.d(TAG, "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                AppLog.d(TAG, "back");
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
        //presenter.disconnectCamera();
        presenter.delConnectFailureListener();
        presenter.unregisterWifiSSReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_preview);
            if (presenter.getCurrentCameraMode().equals(CameraMode.PHOTO)) {
                cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_photo, (Resources.Theme) null));
            } else {
                cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_video, (Resources.Theme) null));
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_preview);
            if (presenter.getCurrentCameraMode().equals(CameraMode.PHOTO)) {
                cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_photo_landscape, (Resources.Theme) null));
            } else {
                cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_video_landscape, (Resources.Theme) null));
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.redrawSurface();
            }
        }, 200);
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }

    @Override
    public void updateUIByPhotoMode() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_photo, (Resources.Theme) null));
        } else {
            cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_photo_landscape, (Resources.Theme) null));
        }
        shutterButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shutter_photo, (Resources.Theme) null));

        photoLimitText.setVisibility(View.VISIBLE);
        videoLimitText.setVisibility(View.GONE);
        cameraVideoStatus.setVisibility(View.GONE);

        updateShootModeList();
        updateOsd();
    }

    @Override
    public void updateUIByVideoMode() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_video, (Resources.Theme) null));
        } else {
            cameraModeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.camera_mode_toggle_video_landscape, (Resources.Theme) null));
        }
        shutterButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shutter_video, (Resources.Theme) null));

        photoLimitText.setVisibility(View.GONE);
        videoLimitText.setVisibility(View.VISIBLE);
        cameraVideoStatus.setVisibility(View.VISIBLE);

        updateShootModeList();
        updateOsd();
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
    public void setBatteryIcon(int batteryLevel) {
        if (batteryLevel > 100) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_charge_white);
        } else if (batteryLevel == 100) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_100_white);
        } else if (batteryLevel >= 80) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_80_white);
        } else if (batteryLevel >= 60) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_60_white);
        } else if (batteryLevel >= 40) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_40_white);
        } else if (batteryLevel >= 20) {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_20_white);
        } else {
            batteryStatusIcon.setImageResource(R.drawable.camera_battery_10);
        }

        batteryPercentText.setText(batteryLevel + "%");
    }

    @Override
    public void setSdCardIcon(boolean isExist) {
        if (isExist) {
            sdCardStatusIcon.setImageResource(R.drawable.status_sd_card);
        } else {
            sdCardStatusIcon.setImageResource(R.drawable.status_no_sd_card);
        }
    }

    @Override
    public void setTimeLapseModeVisibility(int visibility) {

    }

    @Override
    public void setTimeLapseModeIcon(int drawableId) {

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
            shootModeLayout.setVisibility(View.INVISIBLE);
            mediaButton.setVisibility(View.INVISIBLE);
            preferenceButton.setVisibility(View.INVISIBLE);
        } else {
            // visibility is View.GONE
            // show all elements except recordingTimeLayout
            topBarLayout.setVisibility(View.VISIBLE);
            cameraStatusLayout.setVisibility(View.VISIBLE);
            cameraZoomLayout.setVisibility(View.VISIBLE);
            shootModeLayout.setVisibility(View.VISIBLE);
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
        this.maxZoomRate = maxZoomRate;
    }

    @Override
    public float getZoomViewProgress() {
        return this.zoomRate;
    }

    @Override
    public float getZoomViewMaxZoomRate() {
        return this.maxZoomRate;
    }

    @Override
    public void updateZoomViewProgress(float currentZoomRatio) {
        //this.zoomRate = currentZoomRatio;
        //updateZoomBarUI();
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
        this.minZoomRate = minZoomRate;
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