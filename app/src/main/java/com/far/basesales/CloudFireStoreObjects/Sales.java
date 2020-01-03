package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@IgnoreExtraProperties
public class Sales {
    private String CODE, CODEUSER,  CODERECEIPT, CODEDAY;
    private int STATUS;
    private double TOTALTAXES, TOTALDISCOUNT, TOTAL;
    private @ServerTimestamp
    Date DATE, MDATE;
    private List<HashMap<String, Object>> salesdetails;

    public Sales(){

    }

    public Sales(String code,String codeuser, double totalDiscount,double totalTaxes,  double total, int status,  String codeReceipt, String codeDay){
        this.CODE = code; this.TOTALDISCOUNT = totalDiscount;this.TOTALTAXES = totalTaxes; this.TOTAL = total;
        this.STATUS = status;this.CODEUSER = codeuser; this.CODERECEIPT = codeReceipt;this.CODEDAY = codeDay;
    }
    public Sales(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(SalesController.CODE));
        this.TOTALDISCOUNT = c.getDouble(c.getColumnIndex(SalesController.TOTALDISCOUNT));
        this.TOTALTAXES = c.getDouble(c.getColumnIndex(SalesController.TOTALTAXES));
        this.TOTAL = c.getDouble(c.getColumnIndex(SalesController.TOTAL));
        this.STATUS = c.getInt(c.getColumnIndex(SalesController.STATUS));
        this.CODEUSER = c.getString(c.getColumnIndex(SalesController.CODEUSER));
        this.CODERECEIPT = c.getString(c.getColumnIndex(SalesController.CODERECEIPT));
        this.CODEDAY = c.getString(c.getColumnIndex(SalesController.CODEDAY));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesController.MDATE)));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(SalesController.CODE,CODE);
        data.put(SalesController.TOTALDISCOUNT,TOTALDISCOUNT);
        data.put(SalesController.TOTALTAXES,TOTALTAXES);
        data.put(SalesController.TOTAL, TOTAL);
        data.put(SalesController.STATUS, STATUS);
        data.put(SalesController.CODEUSER, CODEUSER);
        data.put(SalesController.CODERECEIPT, CODERECEIPT);
        data.put(SalesController.CODEDAY, CODEDAY);
        data.put(SalesController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        data.put(SalesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        if(salesdetails != null){
        data.put("salesdetails", salesdetails);
        }

        return  data;

    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public double getTOTALDISCOUNT() {
        return TOTALDISCOUNT;
    }

    public void setTOTALDISCOUNT(double TOTALDISCOUNT) {
        this.TOTALDISCOUNT = TOTALDISCOUNT;
    }

    public double getTOTAL() {
        return TOTAL;
    }

    public void setTOTAL(double TOTAL) {
        this.TOTAL = TOTAL;
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

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public String getCODEUSER() {
        return CODEUSER;
    }

    public void setCODEUSER(String CODEUSER) {
        this.CODEUSER = CODEUSER;
    }

    public String getCODERECEIPT() {
        return CODERECEIPT;
    }

    public void setCODERECEIPT(String CODERECEIPT) {
        this.CODERECEIPT = CODERECEIPT;
    }

    public String getCODEDAY() {
        return CODEDAY;
    }

    public void setCODEDAY(String CODEDAY) {
        this.CODEDAY = CODEDAY;
    }

    public double getTOTALTAXES() {
        return TOTALTAXES;
    }

    public void setTOTALTAXES(double TOTALTAXES) {
        this.TOTALTAXES = TOTALTAXES;
    }

    public void setDetails(ArrayList<SalesDetails> details){
        ArrayList<HashMap<String, Object>> maps = new ArrayList<>();
        for(SalesDetails sd: details){
            maps.add(sd.toMap());
        }
        this.salesdetails = maps;
    }
}
