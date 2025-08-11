package com.sena.senacamera.ui.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sena.senacamera.R;
import com.sena.senacamera.bluetooth.BluetoothDeviceManager;
import com.sena.senacamera.listener.Callback;
import com.sena.senacamera.log.AppLog;
import com.sena.senacamera.ui.BuildConfig;
import com.sena.senacamera.ui.appdialog.AppDialogManager;
import com.sena.senacamera.utils.SenaXmlParser;
import com.sena.senacamera.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class SenaCameraActivity extends Activity implements View.OnClickListener {
    private static final String TAG = SenaCameraActivity.class.getSimpleName();

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int SUB_ACTIVITY_WRITE_SETTINGS = 2;
    private boolean termsPolicyAgreed = false;

    LinearLayout connectCameraLayout, termsPolicyLayout, allowPermissionsLayout, allowPermissionsWarningLayout;
    Button connectCameraButton, agreeButton, okButton, settingsButton;
    ImageView termsPolicyCheckbox;
    /* access modifiers changed from: private */
    public List<String> missingPermissions = new ArrayList<>();
    public SenaXmlParser senaXmlParser = SenaXmlParser.getInstance();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        requestWindowFeature(1);
        setContentView(R.layout.activity_sena_camera);
        getResources().getDisplayMetrics();

        this.connectCameraLayout = (LinearLayout) findViewById(R.id.connect_camera_layout);
        this.termsPolicyLayout = (LinearLayout) findViewById(R.id.terms_policy_layout);
        this.allowPermissionsLayout = (LinearLayout) findViewById(R.id.allow_permissions_layout);
        this.allowPermissionsWarningLayout = (LinearLayout) findViewById(R.id.allow_permissions_warning_layout);
        this.connectCameraButton = (Button) findViewById(R.id.connect_camera_button);
        this.agreeButton = (Button) findViewById(R.id.agree_button);
        this.okButton = (Button) findViewById(R.id.ok_button);
        this.settingsButton = (Button) findViewById(R.id.settings_button);
        this.termsPolicyCheckbox = (ImageView) findViewById(R.id.terms_agree_checkbox);

        this.connectCameraButton.setOnClickListener(this);
        this.agreeButton.setOnClickListener(this);
        this.okButton.setOnClickListener(this);
        this.settingsButton.setOnClickListener(this);
        this.termsPolicyCheckbox.setOnClickListener(this);

        // check if terms & policy is agreed
        termsPolicyAgreed = (boolean) SharedPreferencesUtils.get(getApplicationContext(), SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.TERMS_AGREED, false);
        if (!termsPolicyAgreed) {
            // show the terms policy layout if terms & policy is not agreed
            this.termsPolicyLayout.setVisibility(View.VISIBLE);
        }

        // check permissions & terms agreement
        if (termsPolicyAgreed && !this.checkPermissions()) {
            // show the permissions layout if all necessary permissions are not granted & terms is agreed
            this.allowPermissionsLayout.setVisibility(View.VISIBLE);

            // request permissions
            if (!this.missingPermissions.isEmpty()) {
                this.getPermissions();
            }
        }

        // initialize sena xml parser
        if (!senaXmlParser.isExecuted) {
            senaXmlParser.setContext(this);
            senaXmlParser.setCallback(new Callback() {
                @Override
                public void processSucceed() {
                    AppLog.e(TAG, "xml read is succeeded");
                }

                @Override
                public void processFailed() {
                    AppLog.e(TAG, "xml read is failed");
                }
            });
            senaXmlParser.readFromSharedPref();
            senaXmlParser.execute();
        }
    }

    public boolean checkPermissions() {
        boolean z = true;
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.INTERNET") != 0) {
            this.missingPermissions.add("android.permission.INTERNET");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.CHANGE_WIFI_STATE") != 0) {
            this.missingPermissions.add("android.permission.CHANGE_WIFI_STATE");
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

        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_SCAN") != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.missingPermissions.add("android.permission.BLUETOOTH_SCAN");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_CONNECT") != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.missingPermissions.add("android.permission.BLUETOOTH_CONNECT");
            z = false;
        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
//            this.missingPermissions.add("android.permission.READ_EXTERNAL_STORAGE");
//            z = false;
//        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
//            this.missingPermissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
//            z = false;
//        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.MANAGE_EXTERNAL_STORAGE") != 0) {
//            this.missingPermissions.add("android.permission.MANAGE_EXTERNAL_STORAGE");
//            z = false;
//        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            this.missingPermissions.add("android.permission.ACCESS_FINE_LOCATION");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            this.missingPermissions.add("android.permission.ACCESS_COARSE_LOCATION");
            z = false;
        }
//        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_SETTINGS") != 0 && !Settings.System.canWrite(this)) {
//            this.missingPermissions.add("android.permission.WRITE_SETTINGS");
//            z = false;
//        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_IMAGES") != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.missingPermissions.add("android.permission.READ_MEDIA_IMAGES");
            z = false;
        }
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_VIDEO") != 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        AppLog.i(TAG, "checkPermissions: " + missingPermissions.toString());

        return z;
    }

    public void getPermissions() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int length = permissions.length - 1; length >= 0; length--) {
                if (!this.missingPermissions.isEmpty()) {
                    getPermissions();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        // combination of onclick listeners of buttons
        int id = v.getId();

        if (id == R.id.connect_camera_button) {
            // go to connect device screen
            startActivity(new Intent(v.getContext(), ConnectDeviceActivity.class));
            finish();
        } else if (id == R.id.agree_button) {
            // terms & policy agree button is clicked
            this.termsPolicyLayout.setVisibility(View.GONE);

            // check & request checkPermissions
            if (!this.checkPermissions()) {
                // show the allow permissions layout if all permissions are not granted
                this.allowPermissionsLayout.setVisibility(View.VISIBLE);

                // request permissions
                if (!this.missingPermissions.isEmpty()) {
                    this.getPermissions();
                }
            }
        } else if (id == R.id.ok_button) {
            // hide the allow permissions layout
            this.allowPermissionsLayout.setVisibility(View.GONE);
        } else if (id == R.id.settings_button) {
            // go to app permission settings
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } else if (id == R.id.terms_agree_checkbox) {
            if (this.termsPolicyAgreed) {
                SharedPreferencesUtils.put(this, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.TERMS_AGREED, false);
                this.termsPolicyAgreed = false;

                this.agreeButton.setEnabled(false);
                this.termsPolicyCheckbox.setImageResource(R.drawable.status_checkbox_off);
            } else {
                SharedPreferencesUtils.put(this, SharedPreferencesUtils.CONFIG_FILE, SharedPreferencesUtils.TERMS_AGREED, true);
                this.termsPolicyAgreed = true;

                this.agreeButton.setEnabled(true);
                this.termsPolicyCheckbox.setImageResource(R.drawable.status_checkbox_on);
            }
        }
    }
}