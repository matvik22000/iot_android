package com.example.iot2.colorpicker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HSV{
    int h, s, v;

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public void setV(int v) {
        this.v = v;
    }

    public int getV() {
        return v;
    }
}
