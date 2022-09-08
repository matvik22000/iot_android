package com.example.iot2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RaspberryActivity extends WithTemperatureActivity {

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

//        float download = intent.getFloatExtra("download", 1.1f);
//        float upload = intent.getFloatExtra("upload", 1.2f);
//        float ping = intent.getFloatExtra("ping", 1f);
//
//        TextView downloadView = findViewById(R.id.downloadSpeed);
//        TextView uploadView = findViewById(R.id.uploadSpeed);
//        TextView pingView = findViewById(R.id.ping);
//
//        downloadView.setText(String.format("%.1f", download));
//        uploadView.setText(String.format("%.1f", upload));
//        pingView.setText(String.format("%.0f", ping));

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_raspberry;
    }
}