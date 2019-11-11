package com.far.basesales.CloudFireStoreObjects;


import android.database.Cursor;

import com.far.basesales.Controllers.SalesHistoryController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SalesDetailsHistory {
    private String CODE,CODESALES, CODEPRODUCT,PRODUCTDESCRIPTION,CODEUND, UNDDESCRIPTION;
    private int POSITION;
    private double QUANTITY, PRICE,TAX, DISCOUNT;
    private @ServerTimestamp
    Date DATE, MDATE;

    public SalesDetailsHistory(){

    }
    public SalesDetailsHistory(String code,String codeSales, String codeProduct,String productDescription, String codeUnd,String undDescription, int position,double quantity, double price, double discount, double tax){
        this.CODE = code; this.CODESALES = codeSales; this.CODEPRODUCT = codeProduct;this.PRODUCTDESCRIPTION = productDescription; this.CODEUND = codeUnd;this.UNDDESCRIPTION = undDescription;this.TAX = tax;
        this.POSITION = position; this.QUANTITY = quantity;
        this.PRICE = price; this.DISCOUNT = discount;
    }

    public SalesDetailsHistory(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_CODE));
        this.CODESALES = c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_CODESALES));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_CODEPRODUCT));
        this.PRODUCTDESCRIPTION = c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_PRODUCTDESCRIPTION));
        this.CODEUND = c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_CODEUND));
        this.UNDDESCRIPTION = c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_UNDDESCRIPTION));
        this.POSITION = c.getInt(c.getColumnIndex(SalesHistoryController.DETAIL_POSITION));
        this.QUANTITY = c.getDouble(c.getColumnIndex(SalesHistoryController.DETAIL_QUANTITY));
        this.TAX = c.getDouble(c.getColumnIndex(SalesHistoryController.DETAIL_TAX));
        this.PRICE = c.getDouble(c.getColumnIndex(SalesHistoryController.DETAIL_PRICE));;
        this.DISCOUNT = c.getDouble(c.getColumnIndex(SalesHistoryController.DETAIL_DISCOUNT));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesHistoryController.DETAIL_MDATE)));
    }
    
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(SalesHistoryController.DETAIL_CODE, CODE);
        map.put(SalesHistoryController.DETAIL_CODESALES,CODESALES);
        map.put(SalesHistoryController.DETAIL_CODEPRODUCT, CODEPRODUCT);
        map.put(SalesHistoryController.DETAIL_PRODUCTDESCRIPTION, PRODUCTDESCRIPTION);
        map.put(SalesHistoryController.DETAIL_CODEUND, CODEUND);
        map.put(SalesHistoryController.DETAIL_UNDDESCRIPTION, UNDDESCRIPTION);
        map.put(SalesHistoryController.DETAIL_POSITION, POSITION);
        map.put(SalesHistoryController.DETAIL_QUANTITY, QUANTITY);
        map.put(SalesHistoryController.DETAIL_TAX, TAX);
        map.put(SalesHistoryController.DETAIL_PRICE, PRICE);
        map.put(SalesHistoryController.DETAIL_DISCOUNT, DISCOUNT);
        map.put(SalesHistoryController.DETAIL_DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        map.put(SalesHistoryController.DETAIL_MDATE, (MDATE == null)? FieldValue.serverTimestamp(): MDATE);
        return map;
    }

    public SalesDetailsHistory(Map<String, Object> map){
        this.CODE = map.get(SalesHistoryController.DETAIL_CODE).toString();
        this.CODESALES = map.get(SalesHistoryController.DETAIL_CODESALES).toString();
        this.CODEPRODUCT = map.get(SalesHistoryController.DETAIL_CODEPRODUCT).toString();
        this.PRODUCTDESCRIPTION = map.get(SalesHistoryController.DETAIL_PRODUCTDESCRIPTION).toString();
        this.CODEUND = map.get(SalesHistoryController.DETAIL_CODEUND).toString();
        this.UNDDESCRIPTION = map.get(SalesHistoryController.DETAIL_UNDDESCRIPTION).toString();
        this.POSITION = Integer.parseInt(map.get(SalesHistoryController.DETAIL_POSITION).toString());
        this.QUANTITY = Double.parseDouble(map.get(SalesHistoryController.DETAIL_QUANTITY).toString());
        this.TAX =  Double.parseDouble(map.get(SalesHistoryController.DETAIL_TAX).toString());
        this.PRICE =  Double.parseDouble(map.get(SalesHistoryController.DETAIL_PRICE).toString());
        this.DISCOUNT =  Double.parseDouble(map.get(SalesHistoryController.DETAIL_DISCOUNT).toString());
        this.DATE = (Date)map.get(SalesHistoryController.DETAIL_DATE);
        this.MDATE = (Date)map.get(SalesHistoryController.DETAIL_MDATE);
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

    public String getCODEUND() {
        return CODEUND;
    }

    public void setCODEUND(String CODEUND) {
        this.CODEUND = CODEUND;
    }

    public int getPOSITION() {
        return POSITION;
    }

    public void setPOSITION(int POSITION) {
        this.POSITION = POSITION;
    }

    public double getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(double QUANTITY) {
        this.QUANTITY = QUANTITY;
    }


    public double getPRICE() {
        return PRICE;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
    }

    public double getDISCOUNT() {
        return DISCOUNT;
    }

    public void setDISCOUNT(double DISCOUNT) {
        this.DISCOUNT = DISCOUNT;
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

    public String getCODESALES() {
        return CODESALES;
    }

    public void setCODESALES(String CODESALES) {
        this.CODESALES = CODESALES;
    }

    public String getPRODUCTDESCRIPTION() {
        return PRODUCTDESCRIPTION;
    }

    public void setPRODUCTDESCRIPTION(String PRODUCTDESCRIPTION) {
        this.PRODUCTDESCRIPTION = PRODUCTDESCRIPTION;
    }

    public String getUNDDESCRIPTION() {
        return UNDDESCRIPTION;
    }

    public void setUNDDESCRIPTION(String UNDDESCRIPTION) {
        this.UNDDESCRIPTION = UNDDESCRIPTION;
    }

    public double getTAX() {
        return TAX;
    }

    public void setTAX(double TAX) {
        this.TAX = TAX;
    }
}
