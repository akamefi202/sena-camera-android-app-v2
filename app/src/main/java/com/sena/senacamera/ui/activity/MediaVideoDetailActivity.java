package com.sena.senacamera.ui.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.sena.senacamera.ui.ExtendComponent.ProgressWheel;
import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.Presenter.VideoDetailPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.Interface.VideoDetailView;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;

public class MediaVideoDetailActivity extends AppCompatActivity implements VideoDetailView {
    private String TAG = MediaVideoDetailActivity.class.getSimpleName();
    private TextView timeLapsed, timeDuration;
    private SeekBar seekBar;
    private ImageButton playButton, expandButton, backbutton, playDetailButton;
    private LinearLayout deleteButton, downloadButton;
    private LinearLayout topBar, bottomBar;
    private TextView titleText;
    private ProgressWheel progressWheel;
    private VideoDetailPresenter presenter;
    private SurfaceView mSurfaceView;

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
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        titleText = (TextView) findViewById(R.id.title_text);
        deleteButton = (LinearLayout) findViewById(R.id.delete_button);
        downloadButton = (LinearLayout) findViewById(R.id.save_button);

        presenter = new VideoDetailPresenter(this);
        presenter.setView(this);

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

        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG,"mSurfaceViewImage ClickListener");
                presenter.showBar(topBar.getVisibility() == View.VISIBLE ? false : true);
            }
        });

        mSurfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                View parentView = (View) mSurfaceView.getParent();
                int heigth = parentView.getHeight();
                int width = parentView.getWidth();
                AppLog.d(TAG, "onLayoutChange heigth=" + heigth + " width=" + width);
                presenter.redrawSurface();
            }
        });

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated");
                presenter.initSurface(mSurfaceView.getHolder());
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

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
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
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        presenter.onSurfaceViewTouchPointerUp();
                        break;
                }
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
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        bottomBar.setVisibility(visibility);
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
        if (value >=0) {
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
        View parentView = (View) mSurfaceView.getParent();
        int width = parentView.getWidth();
        return width;
    }

    @Override
    public int getSurfaceViewHeight() {
        View parentView = (View) mSurfaceView.getParent();
        int heigth = parentView.getHeight();
        return heigth;
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
        if(seekBar.isEnabled() != enabled){
            AppLog.d(TAG,"setSeekbarEnabled enabled:" + enabled);
            seekBar.setEnabled(enabled);
        }
    }

    @Override
    public void setDownloadBtnEnabled(boolean enabled) {
        downloadButton.setEnabled(enabled);
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
        AppLog.d(TAG, "onConfigurationChanged newConfig Orientation=" + newConfig.orientation);
    }
}
