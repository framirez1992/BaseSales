package com.far.basesales.Generic;

public class KV2 {
    public String code, description, description2;

    public KV2(String code, String description, String description2){
        this.code = code; this.description = description; this.description2 = description2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }
}
