package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Clients {
    private String CODE,DOCUMENT, NAME,PHONE;
    private @ServerTimestamp
    Date DATE, MDATE;

    public Clients(){

    }
    public Clients(String code,String document,  String name,  String phone){
        this.CODE = code; this.DOCUMENT = document; this.NAME = name;  this.PHONE = phone;
    }

    public Clients(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ClientsController.CODE));
        this.DOCUMENT = c.getString(c.getColumnIndex(ClientsController.DOCUMENT));
        this.NAME = c.getString(c.getColumnIndex(ClientsController.NAME));
        this.PHONE = c.getString(c.getColumnIndex(ClientsController.PHONE));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ClientsController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ClientsController.MDATE)));
    }
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ClientsController.CODE, CODE);
        map.put(ClientsController.DOCUMENT, DOCUMENT);
        map.put(ClientsController.NAME, NAME);
        map.put(ClientsController.PHONE, PHONE);
        map.put(ClientsController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(ClientsController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }


    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getNAME() {
        return NAME;
    }

    public String getDOCUMENT() {
        return DOCUMENT;
    }

    public void setDOCUMENT(String DOCUMENT) {
        this.DOCUMENT = DOCUMENT;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
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
