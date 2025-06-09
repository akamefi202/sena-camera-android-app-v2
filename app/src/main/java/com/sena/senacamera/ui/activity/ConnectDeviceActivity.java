package com.sena.senacamera.ui.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.sena.senacamera.R;

public class ConnectDeviceActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect_device);

        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener((v) -> {
            finish();
        });
    }
}
