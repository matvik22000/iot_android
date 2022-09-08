package com.example.iot2.dataclasses;

import androidx.annotation.NonNull;

import com.example.iot2.Iot;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Device implements Serializable {
    public String name;
    public String type;
    public boolean isGroup;
    public String userFriendlyName;
    public boolean state;

    public String color;
    public float brightness;

    public float temperature;
    public int humidity;

    public Sensor[] sensors;
    public CPU cpu;
    public Memory memory;

    public int fanLevel;
    public boolean heater;
    public boolean alarm;

    public int[] rgb;
    public int mode;

    public float download;
    public float upload;
    public float ping;

    public float getDownload() {
        return download;
    }

    public void setDownload(float download) {
        this.download = download;
    }

    public float getUpload() {
        return upload;
    }

    public void setUpload(float upload) {
        this.upload = upload;
    }

    public float getPing() {
        return ping;
    }

    public void setPing(float ping) {
        this.ping = ping;
    }

    public int[] getRgb() {
        return rgb;
    }

    public void setRgb(int[] rgb) {
        this.rgb = rgb;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getFanLevel() {
        return fanLevel;
    }

    public void setFanLevel(float fanLevel) {
        this.fanLevel = (int) fanLevel;
    }

    public boolean getHeater() {
        return heater;
    }

    public void setHeater(boolean heater) {
        this.heater = heater;
    }

    public boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    public void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        isGroup = group;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name + " " + this.type;
    }

    int httpRequest(String q, Map<String, String> params) throws IOException {
        OkHttpClient client = new OkHttpClient();
        StringBuilder url = new StringBuilder(Iot.HOST + q + "?");
        boolean first = true;
        for (String param_name : params.keySet()) {
            if (!first) url.append("&");
            url.append(param_name).append("=").append(params.get(param_name));
            first = false;
        }
        System.out.println(url.toString());
        Request request = new Request.Builder()
                .url(url.toString())
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.code());
            return response.code();
        }
    }

}
