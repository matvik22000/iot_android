package com.example.iot2.colorpicker;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.iot2.Iot;
import com.example.iot2.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Color[] color;
        try {
            String res = doGet(Iot.HOST + "/devices");
            color = getColor(res);

        } catch (Exception e) {
            color = createColors(14);
            e.printStackTrace();
        }
        if (color != null) {
            ConstraintLayout constraintLayout = findViewById(R.id.layout);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            View view = new ColorRound(this, color.length, color, width, 100, "led", "white");
            constraintLayout.addView(view);
        }

    }

    public static String doGet(String url)
            throws Exception {

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        System.out.println(url);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        connection.setRequestProperty("Content-Type", "application/json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        return response.toString();
    }

    Color[] createColors(int n){
        Color[] colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = new Color();
            HSV hsv = new HSV();
            hsv.setH(360*i/(n-1));
            hsv.setS(90);
            hsv.setV(90);
            colors[i].setHsv(hsv);
        }
        return colors;
    }

    public Color[] getColor(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Man man = objectMapper.readValue(response.toString(), Man.class);
        return man.palette;
    }
}