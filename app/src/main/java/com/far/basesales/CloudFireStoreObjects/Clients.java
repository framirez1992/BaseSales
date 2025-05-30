package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Clients {
    private String CODE,DOCUMENT, NAME,PHONE, DATA, DATA2, DATA3;
    private @ServerTimestamp
    Date DATE, MDATE;

    public Clients(){

    }
    public Clients(String code,String document,  String name,  String phone, String data, String data2, String data3){
        this.CODE = code; this.DOCUMENT = document; this.NAME = name;  this.PHONE = phone;
        this.DATA = data; this.DATA2 = data2; this.DATA3 = data3;
    }

    public Clients(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(ClientsController.CODE));
        this.DOCUMENT = c.getString(c.getColumnIndex(ClientsController.DOCUMENT));
        this.NAME = c.getString(c.getColumnIndex(ClientsController.NAME));
        this.PHONE = c.getString(c.getColumnIndex(ClientsController.PHONE));
        this.DATA =  c.getString(c.getColumnIndex(ClientsController.DATA));
        this.DATA2 =  c.getString(c.getColumnIndex(ClientsController.DATA2));
        this.DATA3 =  c.getString(c.getColumnIndex(ClientsController.DATA3));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ClientsController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ClientsController.MDATE)));
    }
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(ClientsController.CODE, CODE);
        map.put(ClientsController.DOCUMENT, DOCUMENT);
        map.put(ClientsController.NAME, NAME);
        map.put(ClientsController.PHONE, PHONE);
        map.put(ClientsController.DATA, DATA);
        map.put(ClientsController.DATA2, DATA2);
        map.put(ClientsController.DATA3, DATA3);
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

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getDATA2() {
        return DATA2;
    }

    public void setDATA2(String DATA2) {
        this.DATA2 = DATA2;
    }

    public String getDATA3() {
        return DATA3;
    }

    public void setDATA3(String DATA3) {
        this.DATA3 = DATA3;
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
