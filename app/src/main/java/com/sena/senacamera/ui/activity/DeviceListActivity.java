package com.sena.senacamera.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sena.senacamera.R;

public class DeviceListActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button addNewDeviceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_device_list);

        backButton = findViewById(R.id.back_button);
        addNewDeviceButton = findViewById(R.id.add_new_device_button);

        backButton.setOnClickListener((v) -> {
            finish();
        });
        addNewDeviceButton.setOnClickListener((v) -> {
            startActivity(new Intent(v.getContext(), ConnectDeviceActivity.class));
        });
    }
}