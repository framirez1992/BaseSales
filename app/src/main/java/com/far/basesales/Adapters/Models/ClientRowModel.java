package com.far.basesales.Adapters.Models;

public class ClientRowModel {
    String code,document,  name,phone;
    boolean isInserver;
    public ClientRowModel(String code,String document,  String name, String phone, boolean isInserver){
        this.code = code;
        this.document = document;
        this.name = name;
        this.phone = phone;
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

    public boolean isInserver() {
        return isInserver;
    }

    public void setInserver(boolean inserver) {
        isInserver = inserver;
    }
}
