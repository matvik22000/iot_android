package com.example.iot2.colorpicker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Color {
    String name;
    String id;
    @JsonProperty("value")
    HSV value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HSV getHsv() {
        return value;
    }

    public void setHsv(HSV hsv) {
        this.value = hsv;
    }
}
