package com.far.basesales.Adapters.Models;

public class ProductMeasureRowModel {
    String  codeMeasure, measureDescription;
    double amount,minPrice, maxPrice;
    boolean priceRange, checked;

    public ProductMeasureRowModel(String codeMeasure,String measureDescription,double amount,  boolean priceRange,double minPrice, double maxPrice,  boolean checked){
        this.amount = amount;
        this.codeMeasure = codeMeasure;
        this.measureDescription = measureDescription;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.checked = checked;
        this.priceRange = priceRange;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public String getCodeMeasure() {
        return codeMeasure;
    }

    public void setCodeMeasure(String codeMeasure) {
        this.codeMeasure = codeMeasure;
    }

    public String getMeasureDescription() {
        return measureDescription;
    }

    public void setMeasureDescription(String measureDescription) {
        this.measureDescription = measureDescription;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }



    public boolean isPriceRange() {
        return priceRange;
    }

    public void setPriceRange(boolean priceRange) {
        this.priceRange = priceRange;
    }
}
