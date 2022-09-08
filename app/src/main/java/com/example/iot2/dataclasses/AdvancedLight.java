package com.example.iot2.dataclasses;

public class AdvancedLight extends Light {

    public AdvancedLight(String name) {
        super(name);
    }

    public AdvancedLight(Device d) {
        super(d);
        mode = d.mode;
        rgb = d.rgb;
    }
}
