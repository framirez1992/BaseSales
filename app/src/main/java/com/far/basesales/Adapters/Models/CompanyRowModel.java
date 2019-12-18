package com.far.basesales.Adapters.Models;

public class CompanyRowModel {
    String code, name,rnc, address, address2, phone, phone2, logo;
    boolean isInserver;

    public CompanyRowModel(String code, String name,String rnc,  String address, String address2, String phone, String phone2,String logo,  boolean isInserver){
        this.name = name; this.code = code; this.rnc = rnc; this.address = address; this.address2 = address2; this.phone = phone;
        this.phone2 = phone2; this.logo = logo; this.isInserver = isInserver;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRnc() {
        return rnc;
    }

    public void setRnc(String rnc) {
        this.rnc = rnc;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public boolean isInserver() {
        return isInserver;
    }

    public void setInserver(boolean inserver) {
        isInserver = inserver;
    }
}
