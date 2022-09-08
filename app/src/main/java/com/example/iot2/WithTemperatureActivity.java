package com.example.iot2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public abstract class WithTemperatureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        TextView temperatureView = findViewById(R.id.temperature);
        float temperature = intent.getFloatExtra("temperature", 5);
        temperatureView.setText(String.format(Locale.ENGLISH, "Temperature %.1f°", temperature));
    }
}