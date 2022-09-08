package com.example.iot2.dataclasses;


import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sensor implements Serializable {
    String name;
    String unit;
    String comparativeName;
    float value;
    float comparative;
    boolean hasComparative;
    String format = "%.1f";


    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean getHasComparative() {
        return hasComparative;
    }

    public void setHasComparative(boolean hasComparative) {
        this.hasComparative = hasComparative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getComparativeName() {
        return comparativeName;
    }

    public void setComparativeName(String comparativeName) {
        this.comparativeName = comparativeName;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getComparative() {
        return comparative;
    }

    public void setComparative(float comparative) {
        this.comparative = comparative;
    }


}
