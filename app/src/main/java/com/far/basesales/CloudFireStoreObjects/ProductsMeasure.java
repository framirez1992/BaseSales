package com.far.basesales.CloudFireStoreObjects;

import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
public class ProductsMeasure {
        private String CODE, CODEPRODUCT,CODEMEASURE;
        private Double PRICE, MINPRICE, MAXPRICE;
    private @ServerTimestamp
    Date DATE, MDATE;
        private Boolean ENABLED, RANGE;
        public ProductsMeasure(){

        }
        public ProductsMeasure(String code, String codeProduct, String codeMeasure,double price,boolean range, double minPrice, double maxPrice, boolean enabled, String date, String mdate){
            this.CODE = code; this.CODEPRODUCT = codeProduct; this.CODEMEASURE =codeMeasure;this.PRICE = price;
            this.ENABLED = enabled;this.RANGE = range; this.MINPRICE = minPrice; this.MAXPRICE = maxPrice;
            this.DATE = Funciones.parseStringToDate(date); this.MDATE = Funciones.parseStringToDate(mdate);
        }

        public HashMap<String, Object> toMap(){
            HashMap<String, Object> map = new HashMap<>();
            map.put(ProductsMeasureController.CODE,CODE);
            map.put(ProductsMeasureController.CODEPRODUCT, CODEPRODUCT);
            map.put(ProductsMeasureController.CODEMEASURE, CODEMEASURE);
            map.put(ProductsMeasureController.PRICE, PRICE);
            map.put(ProductsMeasureController.ENABLED, ENABLED);
            map.put(ProductsMeasureController.RANGE, RANGE);
            map.put(ProductsMeasureController.MINPRICE, MINPRICE);
            map.put(ProductsMeasureController.MAXPRICE, MAXPRICE);
            map.put(ProductsMeasureController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
            map.put(ProductsMeasureController.MDATE,(MDATE == null)? FieldValue.serverTimestamp():MDATE);

            return map;
        }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODEPRODUCT() {
        return CODEPRODUCT;
    }

    public void setCODEPRODUCT(String CODEPRODUCT) {
        this.CODEPRODUCT = CODEPRODUCT;
    }

    public String getCODEMEASURE() {
        return CODEMEASURE;
    }

    public void setCODEMEASURE(String CODEMEASURE) {
        this.CODEMEASURE = CODEMEASURE;
    }

    public Date getDATE() {
        return DATE;
    }

    public void setDATE(Date DATE) {
        this.DATE = DATE;
    }

    public Date getMDATE() {
        return MDATE;
    }

    public void setMDATE(Date MDATE) {
        this.MDATE = MDATE;
    }

    public Double getPRICE() {
        return PRICE;
    }

    public void setPRICE(Double PRICE) {
        this.PRICE = PRICE;
    }

    public Boolean getENABLED() {
        return ENABLED;
    }

    public void setENABLED(Boolean ENABLED) {
        this.ENABLED = ENABLED;
    }

    public Double getMINPRICE() {
        return MINPRICE;
    }

    public void setMINPRICE(Double MINPRICE) {
        this.MINPRICE = MINPRICE;
    }

    public Double getMAXPRICE() {
        return MAXPRICE;
    }

    public void setMAXPRICE(Double MAXPRICE) {
        this.MAXPRICE = MAXPRICE;
    }

    public Boolean getRANGE() {
        return RANGE;
    }

    public void setRANGE(Boolean RANGE) {
        this.RANGE = RANGE;
    }
}
