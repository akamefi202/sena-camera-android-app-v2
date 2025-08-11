package com.sena.senacamera.ui.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sena.senacamera.ui.component.ProgressWheel;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.presenter.VideoDetailPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.Interface.VideoDetailView;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;

public class MediaVideoDetailActivity extends AppCompatActivity implements VideoDetailView {
    private static final String TAG = MediaVideoDetailActivity.class.getSimpleName();

    private TextView timeLapsed, timeDuration;
    private SeekBar seekBar;
    private ImageButton playButton, expandButton, backbutton, playDetailButton;
    private LinearLayout deleteButton, downloadButton, shareButton;
    private LinearLayout topBar, bottomBar;
    private TextView titleText;
    private ProgressWheel progressWheel;
    private VideoDetailPresenter presenter;
    private SurfaceView surfaceView;
    private FrameLayout topBarView, bottomControlView;

    private Handler playDetailButtonHandler;
    private Runnable fadeOutRunnable = new Runnable() {
        @Override
        public void run() {
            playDetailButton.animate().alpha(0f).setDuration(1000)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            playDetailButton.setVisibility(View.GONE);
                        }
                    }).start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_video_detail);
        timeLapsed = (TextView) findViewById(R.id.running_time);
        timeDuration = (TextView) findViewById(R.id.duration_time);
        seekBar = (SeekBar) findViewById(R.id.video_seekbar);
        playButton = (ImageButton) findViewById(R.id.play_button);
        expandButton = (ImageButton) findViewById(R.id.expand_button);
        backbutton = (ImageButton) findViewById(R.id.back_button);
        playDetailButton = (ImageButton) findViewById(R.id.play_detail_button);

        topBar = (LinearLayout) findViewById(R.id.top_bar);
        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        titleText = (TextView) findViewById(R.id.title_text);
        deleteButton = (LinearLayout) findViewById(R.id.delete_button);
        downloadButton = (LinearLayout) findViewById(R.id.save_button);
        shareButton = (LinearLayout) findViewById(R.id.share_button);
        topBarView = (FrameLayout) findViewById(R.id.top_bar_view);
        bottomControlView = (FrameLayout) findViewById(R.id.bottom_control_view);

        presenter = new VideoDetailPresenter(this);
        presenter.setView(this);

        // initialize view
        if (presenter.isCurrentItemLocal()) {
            // local
            downloadButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.VISIBLE);
        } else {
            // remote
            shareButton.setVisibility(View.GONE);
            downloadButton.setVisibility(View.VISIBLE);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // do not display menu bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.back();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.play();
            }
        });

        playDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.play();
                playDetailButton.setVisibility(View.VISIBLE);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // current orientation mode is portrait, change to landscape
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    // current orientation mode is landscape, change to portrait
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                presenter.setTimeLapsedValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                presenter.seekBarOnStartTrackingTouch();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                presenter.seekBarOnStopTrackingTouch();

            }
        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"mSurfaceViewImage ClickListener");
//                presenter.play();
//                playDetailButton.setVisibility(View.VISIBLE);
            }
        });

        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom, int previousLeft, int previousTop, int previousRight, int previousBottom) {
                View parentView = (View) surfaceView.getParent();
                int height = parentView.getHeight();
                int width = parentView.getWidth();
                AppLog.d(TAG, "onLayoutChange height=" + height + " width=" + width);
                presenter.redrawSurface();
            }
        });

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated");
                presenter.initSurface(surfaceView.getHolder());
                presenter.play();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                presenter.destroyVideo(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        presenter.onSurfaceViewTouchDown(event);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        presenter.onSurfaceViewPointerDown(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        presenter.onSurfaceViewTouchMove(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        presenter.onSurfaceViewTouchUp();

                        // show play detail button
                        presenter.play();
                        playDetailButton.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSurfaceViewTouchPointerUp();
                        break;
                }
                return true;
            }
        });

        // define empty onTouchListeners to prevent unnecessary click of surface view
        topBarView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        bottomControlView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.submitAppInfo();
        presenter.setSdCardEventListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.removeActivity();
        presenter.destroyVideo(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);

        playDetailButtonHandler.removeCallbacks(fadeOutRunnable);
    }


    public void adjustSurfaceViewAspectRatio(int videoWidth, int videoHeight) {
        // get screen width & height
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // get aspect ratio
        float videoAspectRatio = (float) videoWidth / videoHeight;
        float screenAspectRatio = (float) screenWidth / screenHeight;

        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();

        if (videoAspectRatio > screenAspectRatio) {
            layoutParams.width = screenWidth;
            layoutParams.height = (int) (screenWidth / videoAspectRatio);
        } else {
            layoutParams.height = screenHeight;
            layoutParams.width = (int) (screenHeight * videoAspectRatio);
        }

        surfaceView.setLayoutParams(layoutParams);
    }

    private void cancelPlayDetailButtonFadeOut() {
        if (playDetailButtonHandler != null) {
            playDetailButtonHandler.removeCallbacks(fadeOutRunnable);
        }
    }

    private void startPlayDetailButtonFadeOut() {
        // start fade out after 3 seconds
        playDetailButtonHandler = new Handler(Looper.getMainLooper());
        playDetailButtonHandler.postDelayed(fadeOutRunnable, 3000);
    }

    @Override
    public void setTopBarVisibility(int visibility) {
//        topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
//        bottomBar.setVisibility(visibility);
    }

    @Override
    public void setTimeLapsedValue(String value) {
        timeLapsed.setText(value);
    }

    @Override
    public void setTimeDurationValue(String value) {
        timeDuration.setText(value);
    }

    @Override
    public void setSeekBarProgress(int value) {
        seekBar.setProgress(value);
    }

    @Override
    public void setSeekBarMaxValue(int value) {
        seekBar.setMax(value);
    }

    @Override
    public int getSeekBarProgress() {
        return seekBar.getProgress();
    }

    @Override
    public void setSeekBarSecondProgress(int value) {
        seekBar.setSecondaryProgress(value);
    }


    @Override
    public void setPlayBtnSrc(int resId) {
        playButton.setBackground(ResourcesCompat.getDrawable(getResources(), resId, (Resources.Theme) null));

        if (resId == R.drawable.selector_button_play) {
            playDetailButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_button_play_media_detail, (Resources.Theme) null));
        } else {
            playDetailButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_button_pause_media_detail, (Resources.Theme) null));
        }
        playDetailButton.setAlpha(1.0f);

        cancelPlayDetailButtonFadeOut();
        startPlayDetailButtonFadeOut();
    }

    @Override
    public void showLoadingCircle(boolean isShow) {
        if (isShow) {
            AppLog.d(TAG, "showLoadingCircle");
            //progressWheel.setVisibility(View.VISIBLE);
            //progressWheel.setText("0%");
            //progressWheel.startSpinning();
        } else {
            AppLog.d(TAG, "display LoadingCircle");
            //progressWheel.stopSpinning();
            //progressWheel.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLoadPercent(int value) {
        if (value >= 0) {
            String temp = value + "%";
            //progressWheel.setText(temp);
        }
    }

    @Override
    public void setVideoNameTxv(String value) {
        titleText.setText(value);
    }

    @Override
    public void setProgress(float progress) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                Log.d("AppStart", "home");
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d("AppStart", "back");
                presenter.back();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public int getSurfaceViewWidth() {
        View parentView = (View) surfaceView.getParent();
        int width = parentView.getWidth();
        return width;
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View) surfaceView.getParent();
        int height = parentView.getHeight();
        return height;
    }

    @Override
    public void setPanoramaTypeImageResource(int resId) {
    }

    @Override
    public void setPanoramaTypeBtnVisibility(int visibility) {
    }

    @Override
    public void setMoreSettingLayoutVisibility(int visibility) {
    }

    @Override
    public void setEisSwitchChecked(boolean checked) {
    }

    @Override
    public void setSeekbarEnabled(boolean enabled) {
        if (seekBar.isEnabled() != enabled) {
            AppLog.d(TAG,"setSeekbarEnabled enabled:" + enabled);
            seekBar.setEnabled(enabled);
        }
    }

    @Override
    public void setDownloadBtnEnabled(boolean enabled) {
//        downloadButton.setEnabled(enabled);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                presenter.redrawSurface();
            }
        }, 50);

        // initialize view
        if (presenter.isCurrentItemLocal()) {
            // local
            downloadButton.setVisibility(View.GONE);
            shareButton.setVisibility(View.VISIBLE);
        } else {
            // remote
            shareButton.setVisibility(View.GONE);
            downloadButton.setVisibility(View.VISIBLE);
        }
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }
}
