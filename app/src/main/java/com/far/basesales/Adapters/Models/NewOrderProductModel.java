package com.far.basesales.Adapters.Models;

import com.far.basesales.Generic.KV;
import com.far.basesales.Generic.KV2;

import java.util.ArrayList;


public class NewOrderProductModel {
    String codeOrderDetail;
    String codeProduct;
    String name;
    String quantity;
    String measure;
    boolean blocked;
    ArrayList<KV2> measures;

    public NewOrderProductModel(String codeOrderDetail, String codeProduct, String name, String quantity, String measure, String bloqued, ArrayList<KV2> measures){
        this.codeOrderDetail = codeOrderDetail; this.codeProduct = codeProduct;
        this.name = name; this.quantity = quantity; this.measure = measure;
        this.measures = measures;this.blocked = (bloqued.equals("1"));
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

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public ArrayList<KV2> getMeasures() {
        return measures;
    }

    public void setMeasures(ArrayList<KV2> measures) {
        this.measures = measures;
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

}
