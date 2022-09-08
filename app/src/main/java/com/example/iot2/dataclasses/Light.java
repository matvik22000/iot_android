package com.example.iot2.dataclasses;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Light extends Switch {
    public Light(String name) {
        super(name);
        this.setType("light");
    }

    public Light(Device d) {
        super(d);
    }

    public int changeBrightness(float value) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        params.put("value", String.valueOf(value));
        return httpRequest("set_brightness", params);
    }

    public int changeColor(String color) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        params.put("color", color);
        return httpRequest("set_color", params);
    }

}
