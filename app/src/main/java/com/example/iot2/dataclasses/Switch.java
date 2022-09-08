package com.example.iot2.dataclasses;

import com.example.iot2.Iot;
import com.example.iot2.dataclasses.Device;
import com.example.iot2.dataclasses.MyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Switch extends Device {

    public Switch(String name) {
        this.name = name;
    }

    public Switch(Device d) {
        this.name = d.getName();
        this.state = d.getState();
        this.type = d.getType();
        this.userFriendlyName = d.getUserFriendlyName();

        this.brightness = d.getBrightness();
        this.color = d.getColor();

        this.alarm = d.getAlarm();
        this.fanLevel = d.getFanLevel();
        this.heater = d.getHeater();

        this.humidity = d.getHumidity();
        this.temperature = d.getTemperature();
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

    public int turnOn() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("turn_on", params);
    }

    public int turnOff() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("turn_off", params);
    }

    public MyResponse getCurrentState() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = Iot.HOST + "get_state?device=" + name;
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ObjectMapper mapper = new ObjectMapper();
            String r = Objects.requireNonNull(response.body()).string();
            System.out.println(r);
            return mapper.readValue(r, MyResponse.class);
        }
    }
}
