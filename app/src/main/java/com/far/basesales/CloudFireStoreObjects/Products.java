package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties
public class Products {
    private String CODE, DESCRIPTION,TYPE, SUBTYPE;
    private boolean COMBO;
    private @ServerTimestamp
    Date DATE, MDATE;
    public Products(){

    }
    public Products(String code, String description, String type,String subType,boolean combo){
    this.CODE = code; this.DESCRIPTION = description; this.TYPE = type;
    this.SUBTYPE = subType; this.COMBO = combo;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ProductsController.CODE,CODE);
        map.put(ProductsController.DESCRIPTION,DESCRIPTION);
        map.put(ProductsController.TYPE,TYPE );
        map.put(ProductsController.SUBTYPE, SUBTYPE);
        map.put(ProductsController.COMBO,COMBO );
        map.put(ProductsController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(ProductsController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }

    public Products (Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ProductsController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(ProductsController.DESCRIPTION));
        this.TYPE = c.getString(c.getColumnIndex(ProductsController.TYPE));
        this.SUBTYPE = c.getString(c.getColumnIndex(ProductsController.SUBTYPE));
        this.COMBO = c.getString(c.getColumnIndex(ProductsController.COMBO)).equals("1");
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsController.MDATE)));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsController.DATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getSUBTYPE() {
        return SUBTYPE;
    }

    public void setSUBTYPE(String SUBTYPE) {
        this.SUBTYPE = SUBTYPE;
    }

    public boolean isCOMBO() {
        return COMBO;
    }

    public void setCOMBO(boolean COMBO) {
        this.COMBO = COMBO;
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
