package com.far.farpdf.Entities;

import com.far.farpdf.Objects.Image;

public class Header {
    String name,address, phone, email;
    public Image logo;


    public Header(String name,String address, String phone,String email, Image logo){
        this.name = name;
        this.logo = logo;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public Image getLogo() {
        return logo;
    }
}
