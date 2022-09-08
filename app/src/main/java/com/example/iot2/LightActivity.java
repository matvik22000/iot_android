package com.example.iot2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import com.example.iot2.colorpicker.Color;
import com.example.iot2.colorpicker.ColorRound;
import com.example.iot2.colorpicker.Man;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thebluealliance.spectrum.SpectrumDialog;

public class LightActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ObjectMapper mapper = new ObjectMapper();
        Color[] colors = new Color[0];
        try {
            System.out.println(intent.getStringExtra("colors"));
            colors = mapper.readValue(intent.getStringExtra("colors"), Man.class).getPalette();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int brightness = (int) intent.getFloatExtra("brightness", 0);
        String name = intent.getStringExtra("name");
        String color = intent.getStringExtra("currentColor");
        ColorRound cr = new ColorRound(this.getBaseContext(), colors.length, colors, width, brightness, name, color);
        LinearLayout layout = findViewById(R.id.parent);

        layout.addView(cr);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_light;
    }
}