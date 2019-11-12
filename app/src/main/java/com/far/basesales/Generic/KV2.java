package com.far.basesales.Generic;

public class KV2 {
    public String key, value, value2;

    public KV2(String key, String value, String value2){
        this.key = key; this.value = value; this.value2 = value2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
