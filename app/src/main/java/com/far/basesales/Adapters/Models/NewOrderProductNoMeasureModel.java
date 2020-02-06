package com.far.basesales.Adapters.Models;

import com.far.basesales.Generic.KV2;

import java.util.ArrayList;

public class NewOrderProductNoMeasureModel {
    String codeOrderDetail;
    String codeProduct;
    String name;
    String quantity;
    double price, minPrice, maxPrice;
    boolean blocked, range;

    String manualPrice;
    /**
     *productos sin unidad de medida (control CODES.USERSCONTROL_PRODUCTS_MEASURE = false)
     */
    public NewOrderProductNoMeasureModel(String codeOrderDetail, String codeProduct, String name, String quantity,double price,boolean range, double minPrice, double maxPrice, String manualPrice, boolean bloqued){
        this.codeOrderDetail = codeOrderDetail; this.codeProduct = codeProduct;
        this.name = name; this.quantity = quantity; this.price = price; this.range = range;
        this.minPrice = minPrice; this.maxPrice = maxPrice; this.blocked = bloqued;
        this.manualPrice = manualPrice;
    }

    public String getCodeOrderDetail() {
        return codeOrderDetail;
    }

    public void setCodeOrderDetail(String codeOrderDetail) {
        this.codeOrderDetail = codeOrderDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCodeProduct() {
        return codeProduct;
    }

    public void setCodeProduct(String codeProduct) {
        this.codeProduct = codeProduct;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getManualPrice() {
        if(manualPrice == null || (manualPrice!= null && manualPrice.isEmpty())){
            return "";
        }
        return manualPrice;
    }

    public void setManualPrice(String manualPrice) {
        this.manualPrice = manualPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isRange() {
        return range;
    }

    public void setRange(boolean range) {
        this.range = range;
    }

}
