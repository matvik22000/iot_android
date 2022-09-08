package com.example.iot2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.iot2.dataclasses.AirFresh;
import com.gigamole.library.ShadowLayout;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AirFreshActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_fresh);
        Intent intent = getIntent();
        boolean state = intent.getBooleanExtra("state", false);
        String name = intent.getStringExtra("name");
        String userFriendlyName = intent.getStringExtra("userFriendlyName");
        String room = intent.getStringExtra("room");
        float temperature = intent.getFloatExtra("temperature", 5);
        boolean heater = intent.getBooleanExtra("heater", false);
        boolean alarm = intent.getBooleanExtra("alarm", false);

        AirFresh device = new AirFresh(name);
        device.setHeater(heater);
        device.setAlarm(alarm);

        if (state) {
            Button heaterBtn = findViewById(R.id.heaterBtn);
            ShadowLayout heaterShadow = findViewById(R.id.heaterShadow);
            heaterBtn.setEnabled(true);
            updateBtn(device.heater, heaterBtn, heaterShadow);

            heaterBtn.setOnClickListener(v -> {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        if (device.heater)
                            device.turnHeaterOff();
                        else
                            device.turnHeaterOn();
                        device.heater = !device.heater;
                        updateBtn(device.heater, heaterBtn, heaterShadow);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        }

        Button alarmBtn = findViewById(R.id.alarmBtn);
        ShadowLayout alarmShadow = findViewById(R.id.alarmShadow);
        updateBtn(device.alarm, alarmBtn, alarmShadow);

        alarmBtn.setOnClickListener(v -> {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    if (device.alarm)
                        device.turnAlarmOff();
                    else
                        device.turnAlarmOn();
                    device.alarm = !device.alarm;
                    updateBtn(device.alarm, alarmBtn, alarmShadow);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        TextView deviceName = findViewById(R.id.deviceName);
        TextView deviceRoom = findViewById(R.id.deviceRoom);
        TextView temperatureView = findViewById(R.id.temperature);
        deviceName.setText(userFriendlyName);
        deviceRoom.setText(room);
        temperatureView.setText(String.format(Locale.ENGLISH, "Temperature %.1f°", temperature));
    }

    private void updateBtn(boolean state, Button button, ShadowLayout shadow) {
        if (state) {
            button.setTextColor(getColor(R.color.textTurnedOn));
            shadow.setIsShadowed(true);
        } else {
            button.setTextColor(getColor(R.color.textTurnedOff));
            shadow.setIsShadowed(false);
        }
    }
}