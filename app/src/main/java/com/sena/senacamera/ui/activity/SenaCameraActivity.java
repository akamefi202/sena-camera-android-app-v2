package com.sena.senacamera.ui.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sena.senacamera.R;

import java.util.ArrayList;
import java.util.List;

public class SenaCameraActivity extends Activity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int SUBACTIVITY_WRITE_SETTINGS = 2;
    private static final long introTimeLimit = 2000;
    ImageView intro;
    /* access modifiers changed from: private */
    public long introTimeElapsed = 0;
    /* access modifiers changed from: private */
    public List<String> missingPermissions = new ArrayList();

    /* access modifiers changed from: protected */
    public boolean checkPermission() {
        boolean z = true;
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != 0) {
            this.missingPermissions.add("android.permission.INTERNET");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_WIFI_STATE") != 0) {
            this.missingPermissions.add("android.permission.ACCESS_WIFI_STATE");
            z = false;
        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.CHANGE_WIFI_MULTICAST_STATE") != 0) {
//            this.missingPermissions.add("android.permission.CHANGE_WIFI_MULTICAST_STATE");
//            z = false;
//        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_NETWORK_STATE") != 0) {
            this.missingPermissions.add("android.permission.ACCESS_NETWORK_STATE");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.CHANGE_NETWORK_STATE") != 0) {
            this.missingPermissions.add("android.permission.CHANGE_NETWORK_STATE");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH") != 0) {
            this.missingPermissions.add("android.permission.BLUETOOTH");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN") != 0) {
            this.missingPermissions.add("android.permission.BLUETOOTH_ADMIN");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            this.missingPermissions.add("android.permission.READ_EXTERNAL_STORAGE");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            this.missingPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            this.missingPermissions.add("android.permission.ACCESS_FINE_LOCATION");
            z = false;
        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_SETTINGS") != 0 && !Settings.System.canWrite(this)) {
//            this.missingPermissions.add("android.permission.WRITE_SETTINGS");
//            z = false;
//        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_IMAGES") != 0) {
            this.missingPermissions.add("android.permission.READ_MEDIA_IMAGES");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VIDEO") != 0) {
            this.missingPermissions.add("android.permission.READ_MEDIA_VIDEO");
            z = false;
        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
//            this.missingPermissions.add("android.permission.RECORD_AUDIO");
//            z = false;
//        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.WAKE_LOCK") != 0) {
            this.missingPermissions.add("android.permission.WAKE_LOCK");
            z = false;
        }

        return z;
    }

    public void getPermission() {
        Log.e("SenaCameraActivity - getPermission", String.join(",", this.missingPermissions));
        String[] requestedPermission = {this.missingPermissions.get(0)};
        this.missingPermissions.remove(0);
//        if (requestedPermission[0].equals("android.permission.WRITE_SETTINGS")) {
//            Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
//            intent.setData(Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, 2);
//            return;
//        }
        ActivityCompat.requestPermissions(this, requestedPermission, 1);
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        requestWindowFeature(1);
        setContentView(R.layout.activity_sena_camera);
        this.intro = (ImageView) findViewById(R.id.intro);
        getResources().getDisplayMetrics();
        this.intro.setAlpha(0.0f);
        this.intro.animate().alpha(1.0f).setDuration(introTimeLimit).setListener((Animator.AnimatorListener) null);

        new Thread() {
            public void run() {
                try {
                    // Initialization: Set introTimeElapsed to 0
                    SenaCameraActivity activity = SenaCameraActivity.this;
                    activity.introTimeElapsed = 0;

                    // Loop until introTimeElapsed reaches 2000ms
                    while (activity.introTimeElapsed < 2000) {
                        // Add 100ms to introTimeElapsed and wait
                        activity.introTimeElapsed += 100;
                        Thread.sleep(100); // Wait for 100ms
                    }

                    // Get the width and height of the intro ImageView and set screen size
                    ImageView introImage = activity.intro;
                    //MainActivity.screenWidth = introImage.getWidth();
                    //MainActivity.screenHeight = introImage.getHeight();

                    // Calculate the screen size and status bar height
                    WindowManager windowManager = activity.getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int screenHeight = size.y;
                    //MainActivity.statusBarHeight = screenHeight - MainActivity.screenHeight;

                    // Check permissions
                    if (!activity.checkPermission()) {
                        List<String> missingPermissions = activity.missingPermissions;
                        if (!missingPermissions.isEmpty()) {
                            activity.getPermission(); // Request permissions
                        }
                        return;
                    }

                    // If permissions are granted, start the main activity
                    activity.startMainActivity();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int length = permissions.length - 1; length >= 0; length--) {
                //int i2 = grantResults[length];
                if (!this.missingPermissions.isEmpty()) {
                    getPermission();
                } else {
                    startMainActivity();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (!this.missingPermissions.isEmpty()) {
                getPermission();
            } else {
                startMainActivity();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.intro = null;
    }
}