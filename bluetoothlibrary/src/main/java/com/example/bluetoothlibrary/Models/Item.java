package com.example.bluetoothlibrary.Models;

public class Item {
    String code;
    String label;
    public Item(String code, String label){
     this.code = code;
     this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
