package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.CompanyController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
public class Company {
    private String CODE,NAME,RNC,ADDRESS,ADDRESS2,PHONE, PHONE2, LOGO;
    private @ServerTimestamp
    Date DATE, MDATE;
    public Company(){

    }
    public Company(String code, String name, String rnc, String address, String address2, String phone, String phone2,String logo, Date date, Date mdate){
        this.CODE = code; this.NAME = name; this.RNC = rnc;
        this.ADDRESS = address; this.ADDRESS2 = address2; this.PHONE = phone;
        this.PHONE2 = phone2; this.LOGO = logo; this.DATE = date; this.MDATE = mdate;
    }

    public Company(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(CompanyController.CODE));
        this.NAME = c.getString(c.getColumnIndex(CompanyController.NAME));
        this.RNC = c.getString(c.getColumnIndex(CompanyController.RNC));
        this.ADDRESS = c.getString(c.getColumnIndex(CompanyController.ADDRESS));
        this.ADDRESS2 = c.getString(c.getColumnIndex(CompanyController.ADDRESS2));
        this.PHONE = c.getString(c.getColumnIndex(CompanyController.PHONE));
        this.PHONE2 =c.getString(c.getColumnIndex(CompanyController.PHONE2));
        this.LOGO =c.getString(c.getColumnIndex(CompanyController.LOGO));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(CompanyController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(CompanyController.MDATE)));
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(CompanyController.CODE, CODE);
        map.put(CompanyController.RNC, RNC);
        map.put(CompanyController.NAME, NAME);
        map.put(CompanyController.ADDRESS, ADDRESS);
        map.put(CompanyController.ADDRESS2, ADDRESS2);
        map.put(CompanyController.PHONE, PHONE);
        map.put(CompanyController.PHONE2, PHONE2);
        map.put(CompanyController.LOGO, LOGO);
        map.put(CompanyController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(CompanyController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

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

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getRNC() {
        return RNC;
    }

    public void setRNC(String RNC) {
        this.RNC = RNC;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getADDRESS2() {
        return ADDRESS2;
    }

    public void setADDRESS2(String ADDRESS2) {
        this.ADDRESS2 = ADDRESS2;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getPHONE2() {
        return PHONE2;
    }

    public void setPHONE2(String PHONE2) {
        this.PHONE2 = PHONE2;
    }

    public String getLOGO() {
        return LOGO;
    }

    public void setLOGO(String LOGO) {
        this.LOGO = LOGO;
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
