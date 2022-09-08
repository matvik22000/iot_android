package com.example.iot2.dataclasses;

public class CPU {
    float percent;
    CPUFreq freq;

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public CPUFreq getFreq() {
        return freq;
    }

    public void setFreq(CPUFreq freq) {
        this.freq = freq;
    }
}
