package com.example.iot2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        Intent intent = getIntent();
        String userFriendlyName = intent.getStringExtra("userFriendlyName");
        String room = intent.getStringExtra("room");

        TextView deviceName = findViewById(R.id.deviceName);
        TextView deviceRoom = findViewById(R.id.deviceRoom);
        deviceName.setText(userFriendlyName);
        deviceRoom.setText(room);
    }

    protected abstract int getLayoutResourceId();
}
