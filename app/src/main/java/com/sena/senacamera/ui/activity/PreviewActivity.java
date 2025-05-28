package com.sena.senacamera.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sena.senacamera.R;

public class PreviewActivity extends AppCompatActivity {

    private ImageButton closeButton, settingButton, mediaButton, preferenceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preview);

        closeButton = findViewById(R.id.close_button);
        settingButton = findViewById(R.id.setting_button);
        mediaButton = findViewById(R.id.media_button);
        preferenceButton = findViewById(R.id.preference_button);

        closeButton.setOnClickListener((v) -> {
            finish();
        });
        settingButton.setOnClickListener((v) -> {
            startActivity(new Intent(this, SettingActivity.class));
        });
        mediaButton.setOnClickListener((v) -> {
            startActivity(new Intent(this, MediaActivity.class));
        });
        preferenceButton.setOnClickListener((v) -> {
            startActivity(new Intent(this, PreferenceActivity.class));
        });
    }
}