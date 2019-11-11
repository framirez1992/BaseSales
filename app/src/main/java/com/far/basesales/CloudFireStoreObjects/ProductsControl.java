package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.ProductsControlController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ProductsControl {
    private String CODE, CODEPRODUCT, BLOQUED;
    private @ServerTimestamp
    Date DATE, MDATE;

    public ProductsControl(){

    }
    public ProductsControl(String code, String codeproduct, String bloqued){
    this.CODE = code; this.CODEPRODUCT = codeproduct; this.BLOQUED = bloqued;
    }
    public ProductsControl(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ProductsControlController.CODE));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(ProductsControlController.CODEPRODUCT));
        this.BLOQUED = c.getString(c.getColumnIndex(ProductsControlController.BLOQUED));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsControlController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsControlController.MDATE)));
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ProductsControlController.CODE, CODE);
        map.put(ProductsControlController.CODEPRODUCT, CODEPRODUCT);
        map.put(ProductsControlController.BLOQUED, BLOQUED);
        map.put(ProductsControlController.DATE, (DATE != null)? DATE: FieldValue.serverTimestamp());
        map.put(ProductsControlController.MDATE, (MDATE != null)? MDATE: FieldValue.serverTimestamp());

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

    public String getBLOQUED() {
        return BLOQUED;
    }

    public void setBLOQUED(String BLOQUED) {
        this.BLOQUED = BLOQUED;
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
}
