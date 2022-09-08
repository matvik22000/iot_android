package com.example.iot2.dataclasses;

import com.example.iot2.Iot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Scenario {
    String name;
    String userFriendlyName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }

    public void setUserFriendlyName(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
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

    public int execute() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("execute", params);
    }
}
