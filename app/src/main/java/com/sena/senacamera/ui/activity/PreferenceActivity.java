package com.sena.senacamera.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.icatchtek.control.customer.type.ICatchCamPreviewMode;
import com.sena.senacamera.MyCamera.CameraManager;
import com.sena.senacamera.MyCamera.MyCamera;
import com.sena.senacamera.R;
import com.sena.senacamera.SdkApi.CameraAction;
import com.sena.senacamera.data.Mode.CameraMode;
import com.sena.senacamera.data.type.MediaStorageType;
import com.sena.senacamera.function.streaming.CameraStreaming;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.adapter.PreferencePagerAdapter;
import com.sena.senacamera.ui.component.MyProgressDialog;
import com.sena.senacamera.ui.fragment.FragmentPreferencePhoto;
import com.sena.senacamera.ui.fragment.FragmentPreferenceVideo;

public class PreferenceActivity extends AppCompatActivity {
    private static final String TAG = PreferenceActivity.class.getSimpleName();

    private ImageButton backButton;
    private TabLayout prefTabView;
    private FragmentPreferenceVideo videoFragment;
    private FragmentPreferencePhoto photoFragment;
    private ViewPager2 prefViewPager;
    public static String cameraModeParam = "", shootModeParam = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preference);

        backButton = findViewById(R.id.back_button);
        prefTabView = findViewById(R.id.preference_tab_view);
        prefViewPager = findViewById(R.id.preference_viewpager);

        prefViewPager.setAdapter(new PreferencePagerAdapter(this));
        new TabLayoutMediator(prefTabView, prefViewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(R.string.title_video);
            } else {
                tab.setText(R.string.title_photo);
            }
        }).attach();

        backButton.setOnClickListener((v) -> {
            finish();
        });

        prefTabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    prefViewPager.setCurrentItem(0);
                    changeCameraMode(ICatchCamPreviewMode.ICH_CAM_VIDEO_PREVIEW_MODE);
                } else {
                    prefViewPager.setCurrentItem(1);
                    changeCameraMode(ICatchCamPreviewMode.ICH_CAM_STILL_PREVIEW_MODE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // disable swipe of tab pages
        prefViewPager.setUserInputEnabled(false);

        // check if opened from preview screen
        // show only photo or video tab & hide the tab bar
        Intent intent = getIntent();
        cameraModeParam = intent.getStringExtra("cameraMode");
        shootModeParam = intent.getStringExtra("shootMode");

        if (cameraModeParam != null && shootModeParam != null) {
            prefTabView.setVisibility(View.GONE);
            if (cameraModeParam.equals(CameraMode.PHOTO)) {
                // photo
                prefViewPager.setCurrentItem(1);
            } else {
                // video
                prefViewPager.setCurrentItem(0);
            }
        } else {
            cameraModeParam = "";
            shootModeParam = "";
        }
    }

    public void changeCameraMode(final int ichVideoPreviewMode) {
        AppLog.i(TAG, "changeCameraMode: ichVideoPreviewMode = " + ichVideoPreviewMode);

        MyCamera curCamera = CameraManager.getInstance().getCurCamera();
        if (curCamera == null) {
            AppLog.e(TAG, "changeCameraMode: curCamera is null");
            finish();
            return;
        }
        CameraAction cameraAction = curCamera.getCameraAction();
        MyProgressDialog.showProgressDialog(this, "");

        new Thread(new Runnable() {
            @Override
            public void run() {
                cameraAction.changePreviewMode(ichVideoPreviewMode);
                MyProgressDialog.closeProgressDialog();
            }
        }).start();
    }
}
