package com.sena.senacamera.ui.activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final long introTimeLimit = 2000;
    public long introTimeElapsed = 0;
    public List<String> missingPermissions = new ArrayList<>();

    LinearLayout splashLayout;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        requestWindowFeature(1);
        setContentView(R.layout.activity_splash);
        getResources().getDisplayMetrics();

        this.splashLayout = (LinearLayout) findViewById(R.id.splash_layout);
        this.splashLayout.setAlpha(0.0f);
        this.splashLayout.animate().alpha(1.0f).setDuration(introTimeLimit).setListener((Animator.AnimatorListener) null);

        new Thread() {
            public void run() {
                try {
                    // Initialization: Set introTimeElapsed to 0
                    SplashActivity activity = SplashActivity.this;
                    activity.introTimeElapsed = 0;

                    // Loop until introTimeElapsed reaches 2000ms
                    while (activity.introTimeElapsed < 2000) {
                        // Add 100ms to introTimeElapsed and wait
                        activity.introTimeElapsed += 100;
                        Thread.sleep(100); // Wait for 100ms
                    }

                    // get registered camera device list
                    BluetoothDeviceManager.getInstance().readFromSharedPref(getApplicationContext());

                    if (BluetoothDeviceManager.getInstance().getDeviceList().isEmpty()) {
                        // start the sena camera activity if no device is registered
                        activity.startSenaCameraActivity();
                    } else {
                        // start the main activity if any device is registered
                        activity.startMainActivity();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void startSenaCameraActivity() {
        startActivity(new Intent(this, SenaCameraActivity.class));
        finish();
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.splashLayout = null;
    }
}