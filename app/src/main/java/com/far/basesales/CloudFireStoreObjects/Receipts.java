package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


public class Receipts {
    String code,receiptnumber, codeuser,codesale,codeclient, ncf, status, codeday;
    double subtotal, taxes, discount, total, paidamount;
    private @ServerTimestamp
    Date date, mdate;

    public Receipts(){

    }
    public Receipts(String code,String receiptnumber,  String codeUser,String codesale, String codeclient,  String status, String ncf, double subtotal, double taxes, double discount, double total, double paidAmount, String codeday){
        this.code = code;
        this.receiptnumber = receiptnumber;
        this.codeuser = codeUser;
        this.codesale = codesale;
        this.codeclient = codeclient;
        this.status = status;
        this.ncf = ncf;
        this.subtotal = subtotal;
        this.taxes = taxes;
        this.discount = discount;
        this.total = total;
        this.paidamount = paidAmount;
        this.codeday = codeday;
    }

    public Receipts(Cursor c){
        this.code = c.getString(c.getColumnIndex(ReceiptController.CODE));
        this.receiptnumber = c.getString(c.getColumnIndex(ReceiptController.RECEIPTNUMBER));
        this.codeuser =c.getString(c.getColumnIndex(ReceiptController.CODEUSER));
        this.codesale = c.getString(c.getColumnIndex(ReceiptController.CODESALE));
        this.codeclient = c.getString(c.getColumnIndex(ReceiptController.CODECLIENT));
        this.status = c.getString(c.getColumnIndex(ReceiptController.STATUS));
        this.ncf = c.getString(c.getColumnIndex(ReceiptController.NCF));
        this.subtotal = c.getDouble(c.getColumnIndex(ReceiptController.SUBTOTAL));
        this.taxes = c.getDouble(c.getColumnIndex(ReceiptController.TAXES));
        this.discount = c.getDouble(c.getColumnIndex(ReceiptController.DISCOUNT));
        this.total = c.getDouble(c.getColumnIndex(ReceiptController.TOTAL));
        this.paidamount = c.getDouble(c.getColumnIndex(ReceiptController.PAIDAMOUNT));
        this.codeday = c.getString(c.getColumnIndex(ReceiptController.CODEDAY));
        this.date = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ReceiptController.DATE)));
        this.mdate = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ReceiptController.MDATE)));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(ReceiptController.CODE,code);
        data.put(ReceiptController.RECEIPTNUMBER,receiptnumber);
        data.put(ReceiptController.CODEUSER, codeuser);
        data.put(ReceiptController.CODESALE, codesale);
        data.put(ReceiptController.CODECLIENT, codeclient);
        data.put(ReceiptController.STATUS, status);
        data.put(ReceiptController.NCF,ncf);
        data.put(ReceiptController.SUBTOTAL, subtotal);
        data.put(ReceiptController.TAXES, taxes);
        data.put(ReceiptController.DISCOUNT, discount);
        data.put(ReceiptController.TOTAL, total);
        data.put(ReceiptController.PAIDAMOUNT, paidamount);
        data.put(ReceiptController.CODEDAY, codeday);
        data.put(ReceiptController.DATE, (date == null)? FieldValue.serverTimestamp(): date);
        data.put(ReceiptController.MDATE, (mdate == null)? FieldValue.serverTimestamp():mdate);


        return  data;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNcf() {
        return ncf;
    }

    public void setNcf(String ncf) {
        this.ncf = ncf;
    }

    public String getCodeuser() {
        return codeuser;
    }

    public void setCodeuser(String codeuser) {
        this.codeuser = codeuser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getTaxes() {
        return taxes;
    }

    public void setTaxes(double taxes) {
        this.taxes = taxes;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCodesale() {
        return codesale;
    }

    public void setCodesale(String codesale) {
        this.codesale = codesale;
    }

    public String getCodeclient() {
        return codeclient;
    }

    public void setCodeclient(String codeclient) {
        this.codeclient = codeclient;
    }

    public double getPaidamount() {
        return paidamount;
    }

    public void setPaidamount(double paidamount) {
        this.paidamount = paidamount;
    }

    public String getCodeday() {
        return codeday;
    }

    public void setCodeday(String codeday) {
        this.codeday = codeday;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getReceiptnumber() {
        return receiptnumber;
    }

    public void setReceiptnumber(String receiptnumber) {
        this.receiptnumber = receiptnumber;
    }
}
