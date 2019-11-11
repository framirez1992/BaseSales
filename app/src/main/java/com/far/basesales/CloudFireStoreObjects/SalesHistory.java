package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.SalesHistoryController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class SalesHistory {
    private String CODE, CODEUSER,USERNAME, CODERECEIPT;
    private int STATUS;
    private double TOTALTAXES, TOTALDISCOUNT, TOTAL;
    private @ServerTimestamp
    Date DATE, MDATE;
    private List<HashMap<String, Object>> salesdetails;

    public SalesHistory(){

    }

    public SalesHistory(String code,String codeuser,String userName, double totalTaxes,  double totalDiscount, double total,
                        int status, String codeReceipt){
        this.CODE = code; this.TOTALDISCOUNT = totalDiscount;this.TOTAL = total;this.STATUS = status;
        this.CODEUSER = codeuser;this.USERNAME = userName;
        this.CODERECEIPT = codeReceipt;this.TOTALTAXES = totalTaxes;
    }
    public SalesHistory(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(SalesHistoryController.CODE));
        this.TOTALTAXES = c.getDouble(c.getColumnIndex(SalesHistoryController.TOTALTAXES));
        this.TOTALDISCOUNT = c.getDouble(c.getColumnIndex(SalesHistoryController.TOTALDISCOUNT));
        this.TOTAL = c.getDouble(c.getColumnIndex(SalesHistoryController.TOTAL));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesHistoryController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(SalesHistoryController.MDATE)));
        this.STATUS = c.getInt(c.getColumnIndex(SalesHistoryController.STATUS));
        this.CODEUSER = c.getString(c.getColumnIndex(SalesHistoryController.CODEUSER));
        this.USERNAME = c.getString(c.getColumnIndex(SalesHistoryController.USERNAME));
        this.CODERECEIPT = c.getString(c.getColumnIndex(SalesHistoryController.CODERECEIPT));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(SalesHistoryController.CODE,CODE);
        data.put(SalesHistoryController.TOTALDISCOUNT,TOTALDISCOUNT);
        data.put(SalesHistoryController.TOTAL, TOTAL);
        data.put(SalesHistoryController.DATE, (DATE == null)? FieldValue.serverTimestamp(): DATE);
        data.put(SalesHistoryController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);
        data.put(SalesHistoryController.STATUS, STATUS);
        data.put(SalesHistoryController.CODEUSER, CODEUSER);
        data.put(SalesHistoryController.USERNAME, USERNAME);
        data.put(SalesHistoryController.TOTALTAXES, TOTALTAXES);
        data.put(SalesHistoryController.CODERECEIPT, CODERECEIPT);
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

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }


    public void setDetails(ArrayList<SalesDetailsHistory> details){
        ArrayList<HashMap<String, Object>> maps = new ArrayList<>();
        for(SalesDetailsHistory sd: details){
            maps.add(sd.toMap());
        }
        this.salesdetails = maps;
    }
}
