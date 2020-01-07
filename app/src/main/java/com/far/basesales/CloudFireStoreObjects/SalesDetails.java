package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class SalesDetails {
    private String CODE,CODESALES,CODEUSER, CODEPRODUCT,CODEUND, CODEDAY;
    private int POSITION;
    private double QUANTITY, PRICE,MANUALPRICE,  DISCOUNT, TAX;
    private @ServerTimestamp
    Date DATE, MDATE;

    public SalesDetails(){

    }
    public SalesDetails(String code,String codeSales,String codeuser, String codeProduct, String codeUnd,int position,double quantity,double price,double manualPrice, double discount, double tax, String codeday){
    this.CODE = code; this.CODESALES = codeSales;this.CODEUSER = codeuser; this.CODEPRODUCT = codeProduct; this.CODEUND = codeUnd;
    this.POSITION = position; this.QUANTITY = quantity; this.TAX = tax;
    this.PRICE = price;this.MANUALPRICE = manualPrice; this.DISCOUNT = discount;
    this.CODEDAY = codeday;
    }

    public HashMap<String, Object> toMap(){

        HashMap<String, Object> map = new HashMap<>();
        map.put(SalesController.DETAIL_CODE, CODE);
        map.put(SalesController.DETAIL_CODESALES,CODESALES);
        map.put(SalesController.DETAIL_CODEUSER,CODEUSER);
        map.put(SalesController.DETAIL_CODEPRODUCT, CODEPRODUCT);
        map.put(SalesController.DETAIL_CODEUND, CODEUND);
        map.put(SalesController.DETAIL_POSITION, POSITION);
        map.put(SalesController.DETAIL_QUANTITY, QUANTITY);
        map.put(SalesController.DETAIL_TAX, TAX);
        map.put(SalesController.DETAIL_PRICE, PRICE);
        map.put(SalesController.DETAIL_MANUALPRICE, MANUALPRICE);
        map.put(SalesController.DETAIL_DISCOUNT, DISCOUNT);
        map.put(SalesController.DETAIL_CODEDAY, CODEDAY);
        map.put(SalesController.DETAIL_DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        map.put(SalesController.DETAIL_MDATE, (MDATE == null)? FieldValue.serverTimestamp(): MDATE);
        return map;
    }

    public SalesDetails(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(SalesController.DETAIL_CODE));
        this.CODESALES = c.getString(c.getColumnIndex(SalesController.DETAIL_CODESALES));
        this.CODEUSER = c.getString(c.getColumnIndex(SalesController.DETAIL_CODEUSER));
        this.CODEPRODUCT = c.getString(c.getColumnIndex(SalesController.DETAIL_CODEPRODUCT));
        this.CODEUND = c.getString(c.getColumnIndex(SalesController.DETAIL_CODEUND));
        this.POSITION = c.getInt(c.getColumnIndex(SalesController.DETAIL_POSITION));
        this.QUANTITY = c.getDouble(c.getColumnIndex(SalesController.DETAIL_QUANTITY));
        this.TAX = c.getDouble(c.getColumnIndex(SalesController.DETAIL_TAX));
        this.PRICE = c.getDouble(c.getColumnIndex(SalesController.DETAIL_PRICE));
        this.MANUALPRICE = c.getDouble(c.getColumnIndex(SalesController.DETAIL_MANUALPRICE));
        this.DISCOUNT = c.getDouble(c.getColumnIndex(SalesController.DETAIL_DISCOUNT));
        this.CODEDAY = c.getString(c.getColumnIndex(SalesController.DETAIL_CODEDAY));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.DETAIL_DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.DETAIL_MDATE)));
    }

    public SalesDetails(Map<String, Object> map){
        this.CODE = map.get(SalesController.DETAIL_CODE).toString();
        this.CODESALES = map.get(SalesController.DETAIL_CODESALES).toString();
        this.CODEUSER = map.get(SalesController.DETAIL_CODEUSER).toString();
        this.CODEPRODUCT = map.get(SalesController.DETAIL_CODEPRODUCT).toString();
        this.CODEUND = map.get(SalesController.DETAIL_CODEUND).toString();
        this.POSITION = Integer.parseInt(map.get(SalesController.DETAIL_POSITION).toString());
        this.QUANTITY = Double.parseDouble(map.get(SalesController.DETAIL_QUANTITY).toString());
        this.TAX =  Double.parseDouble(map.get(SalesController.DETAIL_TAX).toString());
        this.PRICE =  Double.parseDouble(map.get(SalesController.DETAIL_PRICE).toString());
        this.MANUALPRICE = Double.parseDouble(map.get(SalesController.DETAIL_MANUALPRICE).toString());
        this.DISCOUNT =  Double.parseDouble(map.get(SalesController.DETAIL_DISCOUNT).toString());
        this.CODEDAY = map.get(SalesController.DETAIL_CODEDAY).toString();
        this.DATE = (Date)map.get(SalesController.DETAIL_DATE);
        this.MDATE = (Date)map.get(SalesController.DETAIL_MDATE);
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCODEUSER() {
        return CODEUSER;
    }

    public void setCODEUSER(String CODEUSER) {
        this.CODEUSER = CODEUSER;
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

    public double getTAX() {
        return TAX;
    }

    public void setTAX(double TAX) {
        this.TAX = TAX;
    }

    public double getPRICE() {
        return PRICE;
    }

    public void setPRICE(double PRICE) {
        this.PRICE = PRICE;
    }

    public double getMANUALPRICE() {
        return MANUALPRICE;
    }

    public void setMANUALPRICE(double MANUALPRICE) {
        this.MANUALPRICE = MANUALPRICE;
    }

    public double getDISCOUNT() {
        return DISCOUNT;
    }

    public void setDISCOUNT(double DISCOUNT) {
        this.DISCOUNT = DISCOUNT;
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

    public String getCODESALES() {
        return CODESALES;
    }

    public void setCODESALES(String CODESALES) {
        this.CODESALES = CODESALES;
    }

}
