package com.example.iot2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class HumidifierActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        TextView temperatureView = findViewById(R.id.temperature);
        TextView humidityView = findViewById(R.id.humidity);
        float temperature = intent.getFloatExtra("temperature", 5);
        int humidity = intent.getIntExtra("humidity", 5);
        temperatureView.setText(String.format(Locale.ENGLISH, "Temperature %.0f°", temperature));
        humidityView.setText(String.format(Locale.ENGLISH, "Humidity %s%%", humidity));

    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_humidifier;
    }
}