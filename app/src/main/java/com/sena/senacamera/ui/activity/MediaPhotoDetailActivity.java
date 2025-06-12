package com.sena.senacamera.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sena.senacamera.Log.AppLog;
import com.sena.senacamera.Presenter.PhotoDetailPresenter;
import com.sena.senacamera.R;
import com.sena.senacamera.ui.ExtendComponent.ProgressWheel;
import com.sena.senacamera.ui.Interface.PhotoDetailView;
import com.icatchtek.pancam.customer.type.ICatchGLPanoramaType;
import com.sena.senacamera.utils.imageloader.ImageLoaderUtil;
import com.sena.senacamera.utils.imageloader.TutkUriUtil;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MediaPhotoDetailActivity extends AppCompatActivity implements PhotoDetailView {
    private static final String TAG = MediaPhotoDetailActivity.class.getSimpleName();
    private LinearLayout downloadButton, deleteButton, topBar, bottomBar;
    private TextView titleText;
    private ImageButton backButton;
    private SurfaceView mSurfaceView;
    private PhotoView photoView;
    private PhotoDetailPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_photo_detail);

        titleText = (TextView) findViewById(R.id.title_text);
        downloadButton = (LinearLayout) findViewById(R.id.save_button);
        deleteButton = (LinearLayout) findViewById(R.id.delete_button);
        topBar = (LinearLayout) findViewById(R.id.top_bar);
        bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
        backButton = (ImageButton) findViewById(R.id.back_button);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        photoView = findViewById(R.id.photo_view);

        presenter = new PhotoDetailPresenter(this);
        presenter.setView(this);

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceCreated");
                presenter.setShowArea(mSurfaceView.getHolder().getSurface());
                presenter.loadPanoramaImage();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                AppLog.d(TAG, "surfaceChanged........width=" + width);
                presenter.setDrawingArea(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                AppLog.d(TAG, "surfaceDestroyed");
                presenter.clearImage(ICatchGLPanoramaType.ICH_GL_PANORAMA_TYPE_SPHERE);
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

        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLog.d(TAG, "mSurfaceView.setOnClickListener");
            }
        });


        final ProgressWheel progressBar = (ProgressWheel) findViewById(R.id.progress_wheel);
        if(photoView != null && !presenter.getCurrentItemInfo().isPanorama()){
            String url = TutkUriUtil.getTutkOriginalUri(presenter.getCurrentItemInfo().iCatchFile);
            ImageLoaderUtil.loadImageView(url, photoView, new ImageLoaderUtil.OnLoadListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.startSpinning();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                    progressBar.stopSpinning();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                    progressBar.stopSpinning();
                }
            });

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {

                }

                @Override
                public void onOutsidePhotoTap() {

                }
            });
        }

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.download();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.delete();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.back();
            }
        });
        presenter.initPanorama();
    }

    @Override
    protected void onResume() {
        AppLog.d(TAG, "onResume");
        super.onResume();

        presenter.initView();
        presenter.submitAppInfo();
        presenter.setSdCardEventListener();
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
    protected void onStop() {
        super.onStop();
        presenter.isAppBackground();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
        presenter.removeActivity();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        presenter.reloadBitmap();
//    }

    @Override
    public void setViewPagerAdapter(PagerAdapter adapter) {
    }

    @Override
    public void setTopBarVisibility(int visibility) {
        //topBar.setVisibility(visibility);
    }

    @Override
    public void setBottomBarVisibility(int visibility) {
        //bottomBar.setVisibility(visibility);
    }

    @Override
    public void setTitleText(String photoName) {
        titleText.setText(photoName);
    }

    @Override
    public void setViewPagerCurrentItem(int position) {
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
    }

    @Override
    public int getViewPagerCurrentItem() {
        return 0;
    }

    @Override
    public int getTopBarVisibility() {
        return topBar.getVisibility();
    }

    @Override
    public void setSurfaceViewVisibility(int visibility) {
        int curVisibility = mSurfaceView.getVisibility();
        if(curVisibility != visibility){
            mSurfaceView.setVisibility(visibility);
        }
    }

    @Override
    public void setPanoramaTypeTxv(int resId) {
    }

    @Override
    public void setViewPagerVisibility(int visibility) {
    }

}
