package com.example.iot2.dataclasses;

public class Humidifier extends Switch{
    public Humidifier(String name) {
        super(name);
        this.setType("humidifier");
    }
}
