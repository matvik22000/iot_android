package com.example.iot2.dataclasses;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AirFresh extends Light {
    public AirFresh(String name) {
        super(name);
    }

    public AirFresh(Device d) {
        super(d);
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

    public int changeFanLevel(float speed) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        params.put("speed", String.valueOf((int) speed));
        return httpRequest("set_fan_level", params);

    }

    public int turnHeaterOn() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("turn_on_heat", params);
    }

    public int turnHeaterOff() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("turn_off_heat", params);
    }

    public int turnAlarmOn() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("turn_on_sound", params);
    }

    public int turnAlarmOff() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("device", name);
        return httpRequest("turn_off_sound", params);
    }

}
