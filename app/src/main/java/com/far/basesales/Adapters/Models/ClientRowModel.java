package com.far.basesales.Adapters.Models;

public class ClientRowModel {
    String code,document,  name,phone, data, data2, data3;
    boolean isInserver;
    public ClientRowModel(String code,String document,  String name, String phone,String data, String data2, String data3,  boolean isInserver){
        this.code = code;
        this.document = document;
        this.name = name;
        this.phone = phone;
        this.data = data;
        this.data2 = data2;
        this.data3 = data3;
        this.isInserver = isInserver;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public boolean isInserver() {
        return isInserver;
    }

    public void setInserver(boolean inserver) {
        isInserver = inserver;
    }
}
