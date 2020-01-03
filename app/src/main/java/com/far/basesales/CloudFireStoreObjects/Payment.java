package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Payment {
    private String CODE, CODERECEIPT,CODEUSER, CODECLIENT, TYPE, CODEDAY;
    private double SUBTOTAL, TAX, DISCOUNT, TOTAL;
    private  @ServerTimestamp
    Date DATE, MDATE;

    public Payment(){

    }

    public Payment(String code, String codeReceipt,String codeUser, String codeClient, String type, double subTotal, double tax, double discount, double total, String codeday){
        this.CODE = code;this.CODERECEIPT = codeReceipt;this.CODEUSER = codeUser; this.CODECLIENT = codeClient; this.TYPE = type;
        this.SUBTOTAL = subTotal; this.TAX = tax; this.DISCOUNT = discount;this.TOTAL = total;this.CODEDAY = codeday;
    }
    public Payment(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(PaymentController.CODE));
        this.CODERECEIPT = c.getString(c.getColumnIndex(PaymentController.CODERECEIPT));
        this.CODEUSER = c.getString(c.getColumnIndex(PaymentController.CODEUSER));
        this.CODECLIENT = c.getString(c.getColumnIndex(PaymentController.CODECLIENT));
        this.TYPE = c.getString(c.getColumnIndex(PaymentController.TYPE));
        this.SUBTOTAL = c.getDouble(c.getColumnIndex(PaymentController.SUBTOTAL));
        this.TAX = c.getDouble(c.getColumnIndex(PaymentController.TAX));
        this.DISCOUNT = c.getDouble(c.getColumnIndex(PaymentController.DISCOUNT));
        this.TOTAL = c.getDouble(c.getColumnIndex(PaymentController.TOTAL));
        this.CODEDAY = c.getString(c.getColumnIndex(PaymentController.CODEDAY));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(PaymentController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(PaymentController.MDATE)));
    }
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(PaymentController.CODE, CODE);
        map.put(PaymentController.CODERECEIPT, CODERECEIPT);
        map.put(PaymentController.CODEUSER, CODEUSER);
        map.put(PaymentController.CODECLIENT, CODECLIENT);
        map.put(PaymentController.TYPE, TYPE);
        map.put(PaymentController.SUBTOTAL, SUBTOTAL);
        map.put(PaymentController.TAX, TAX);
        map.put(PaymentController.DISCOUNT, DISCOUNT);
        map.put(PaymentController.TOTAL, TOTAL);
        map.put(PaymentController.CODEDAY, CODEDAY);
        map.put(PaymentController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(PaymentController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODERECEIPT() {
        return CODERECEIPT;
    }

    public void setCODERECEIPT(String CODERECEIPT) {
        this.CODERECEIPT = CODERECEIPT;
    }

    public String getCODEUSER() {
        return CODEUSER;
    }

    public void setCODEUSER(String CODEUSER) {
        this.CODEUSER = CODEUSER;
    }

    public String getCODECLIENT() {
        return CODECLIENT;
    }

    public void setCODECLIENT(String CODECLIENT) {
        this.CODECLIENT = CODECLIENT;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public double getSUBTOTAL() {
        return SUBTOTAL;
    }

    public void setSUBTOTAL(double SUBTOTAL) {
        this.SUBTOTAL = SUBTOTAL;
    }

    public double getTAX() {
        return TAX;
    }

    public void setTAX(double TAX) {
        this.TAX = TAX;
    }

    public double getDISCOUNT() {
        return DISCOUNT;
    }

    public void setDISCOUNT(double DISCOUNT) {
        this.DISCOUNT = DISCOUNT;
    }

    public double getTOTAL() {
        return TOTAL;
    }

    public void setTOTAL(double TOTAL) {
        this.TOTAL = TOTAL;
    }

    public String getCODEDAY() {
        return CODEDAY;
    }

    public void setCODEDAY(String CODEDAY) {
        this.CODEDAY = CODEDAY;
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
