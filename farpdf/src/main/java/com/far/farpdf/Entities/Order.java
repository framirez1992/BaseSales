package com.far.farpdf.Entities;

import java.util.ArrayList;

public class Order {
    ArrayList<OrderDetail> detail;
    ArrayList<AmountsResume> amountsResume;

    public Order(ArrayList<OrderDetail> detail, ArrayList<AmountsResume> amountsResume){
        this.detail = detail;
        this.amountsResume = amountsResume;
    }

    public ArrayList<OrderDetail> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<OrderDetail> detail) {
        this.detail = detail;
    }

    public ArrayList<AmountsResume> getAmountsResume() {
        return amountsResume;
    }

    public void setAmountsResume(ArrayList<AmountsResume> amountsResume) {
        this.amountsResume = amountsResume;
    }
}
