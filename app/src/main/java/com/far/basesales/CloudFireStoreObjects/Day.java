package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.DayController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Day {
    String code,codeuser, status, lastreceiptnumber;
    int cashsalescount, creditsalescount, salescount,  cashpaidcount, creditpaidcount, anulatedreceiptscount, anulatedcashpaymentcount, anulatedcreditpaymentcount;
    double cashsalesamount, creditsalesamount, discountamount, salesamount, cashpaidamount, creditpaidamount,
    anulatedreceiptsamount, anulatedcashpaymentamount, anulatedcreditpaymentamount;
    private @ServerTimestamp
    Date datestart, dateend,  date, mdate;
    public Day(){

    }
    public Day(String code, String codeUser,Date datestart,Date dateend,  String status,
               int cashsalescount, double cashsalesamount, int creditsalescount, double creditsalesamount,int salescount, double salesamount,  double discountamount,
               int cashpaidcount, double cashpaidamount, int creditpaidcount,  double creditpaidamount,
               int anulatedreceiptscount, double anulatedreceiptsamount, int anulatedcashpaymentcount,
               double anulatedcashpaymentamount, int anulatedcreditpaymentcount, double anulatedcreditpaymentamount, String lastreceiptnumber){
        this.code = code;
        this.codeuser = codeUser;
        this.datestart = datestart;
        this.dateend = dateend;
        this.status = status;
        this.cashsalescount = cashsalescount;
        this.cashsalesamount = cashsalesamount;
        this.creditsalescount = creditsalescount;
        this.creditsalesamount = creditsalesamount;
        this.salescount = salescount;
        this.salesamount = salesamount;
        this.discountamount = discountamount;
        this.cashpaidcount = cashpaidcount;
        this.cashpaidamount = cashpaidamount;
        this.creditpaidcount = creditpaidcount;
        this.creditpaidamount = creditpaidamount;
        this.anulatedreceiptscount = anulatedreceiptscount;
        this.anulatedreceiptsamount = anulatedreceiptsamount;
        this.anulatedcashpaymentcount = anulatedcashpaymentcount;
        this.anulatedcashpaymentamount = anulatedcashpaymentamount;
        this.anulatedcreditpaymentcount = anulatedcreditpaymentcount;
        this.anulatedcreditpaymentamount = anulatedcreditpaymentamount;
        this.lastreceiptnumber = lastreceiptnumber;
    }

    public Day(Cursor c){
        this.code = c.getString(c.getColumnIndex(DayController.CODE));
        this.codeuser = c.getString(c.getColumnIndex(DayController.CODEUSER));
        this.datestart = Funciones.parseStringToDate(c.getString(c.getColumnIndex(DayController.DATESTART)));
        this.dateend = Funciones.parseStringToDate(c.getString(c.getColumnIndex(DayController.DATEEND)));
        this.status = c.getString(c.getColumnIndex(DayController.STATUS));
        this.cashsalescount = c.getInt(c.getColumnIndex(DayController.CASHSALESCOUNT));
        this.cashsalesamount = c.getDouble(c.getColumnIndex(DayController.CASHSALESAMOUNT));
        this.creditsalescount = c.getInt(c.getColumnIndex(DayController.CREDITSALESCOUNT));
        this.creditsalesamount = c.getDouble(c.getColumnIndex(DayController.CREDITSALESAMOUNT));
        this.salescount = c.getInt(c.getColumnIndex(DayController.SALESCOUNT));
        this.salesamount = c.getDouble(c.getColumnIndex(DayController.SALESAMOUNT));
        this.discountamount = c.getDouble(c.getColumnIndex(DayController.DISCOUNTAMOUNT));
        this.cashpaidcount = c.getInt(c.getColumnIndex(DayController.CASHPAIDCOUNT));
        this.cashpaidamount = c.getDouble(c.getColumnIndex(DayController.CASHPAIDAMOUNT));
        this.creditpaidcount = c.getInt(c.getColumnIndex(DayController.CREDITPAIDCOUNT));
        this.creditpaidamount = c.getDouble(c.getColumnIndex(DayController.CREDITPAIDAMOUNT));
        this.anulatedreceiptscount = c.getInt(c.getColumnIndex(DayController.ANULATEDRECEIPTSCOUNT));
        this.anulatedreceiptsamount = c.getDouble(c.getColumnIndex(DayController.ANULATEDRECEIPTSAMOUNT));
        this.anulatedcashpaymentcount = c.getInt(c.getColumnIndex(DayController.ANULATEDCASHPAYMENTCOUNT));
        this.anulatedcashpaymentamount = c.getDouble(c.getColumnIndex(DayController.ANULATEDCASHPAYMENTAMOUNT));
        this.anulatedcreditpaymentcount = c.getInt(c.getColumnIndex(DayController.ANULATEDCREDITPAYMENTCOUNT));
        this.anulatedcreditpaymentamount = c.getDouble(c.getColumnIndex(DayController.ANULATEDCREDITPAYMENTAMOUNT));
        this.lastreceiptnumber = c.getString(c.getColumnIndex(DayController.LASTRECEIPTNUMBER));
        this.date = Funciones.parseStringToDate(c.getString(c.getColumnIndex(DayController.DATE)));
        this.mdate = Funciones.parseStringToDate(c.getString(c.getColumnIndex(DayController.MDATE)));
    }


    public HashMap<String, Object> toMap(){

        HashMap<String, Object> data = new HashMap<>();
        data.put(DayController.CODE,code);
        data.put(DayController.CODEUSER, codeuser);
        data.put(DayController.DATESTART, datestart);
        data.put(DayController.DATEEND, dateend);
        data.put(DayController.STATUS, status);
        data.put(DayController.CASHSALESCOUNT,cashsalescount);
        data.put(DayController.CASHSALESAMOUNT, cashsalesamount);
        data.put(DayController.CREDITSALESCOUNT, creditsalescount);
        data.put(DayController.CREDITSALESAMOUNT, creditsalesamount);
        data.put(DayController.SALESCOUNT, salescount);
        data.put(DayController.SALESAMOUNT, salesamount);
        data.put(DayController.DISCOUNTAMOUNT, discountamount);
        data.put(DayController.CASHPAIDCOUNT, cashpaidcount);
        data.put(DayController.CASHPAIDAMOUNT, cashpaidamount);
        data.put(DayController.CREDITPAIDCOUNT, creditpaidcount);
        data.put(DayController.CREDITPAIDAMOUNT, creditpaidamount);
        data.put(DayController.ANULATEDRECEIPTSCOUNT,anulatedreceiptscount);
        data.put(DayController.ANULATEDRECEIPTSAMOUNT,anulatedreceiptsamount);
        data.put(DayController.ANULATEDCASHPAYMENTCOUNT,anulatedcashpaymentcount);
        data.put(DayController.ANULATEDCASHPAYMENTAMOUNT,anulatedcashpaymentamount);
        data.put(DayController.ANULATEDCREDITPAYMENTCOUNT,anulatedcreditpaymentcount);
        data.put(DayController.ANULATEDCREDITPAYMENTAMOUNT,anulatedcreditpaymentamount);
        data.put(DayController.LASTRECEIPTNUMBER,lastreceiptnumber);
        data.put(DayController.DATE, (date == null)? FieldValue.serverTimestamp(): date);
        data.put(DayController.MDATE, (mdate == null)? FieldValue.serverTimestamp():mdate);


        return  data;

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getCashsalescount() {
        return cashsalescount;
    }

    public void setCashsalescount(int cashsalescount) {
        this.cashsalescount = cashsalescount;
    }

    public int getCreditsalescount() {
        return creditsalescount;
    }

    public void setCreditsalescount(int creditsalescount) {
        this.creditsalescount = creditsalescount;
    }

    public double getCashsalesamount() {
        return cashsalesamount;
    }

    public void setCashsalesamount(double cashsalesamount) {
        this.cashsalesamount = cashsalesamount;
    }

    public double getCreditsalesamount() {
        return creditsalesamount;
    }

    public void setCreditsalesamount(double creditsalesamount) {
        this.creditsalesamount = creditsalesamount;
    }

    public double getDiscountamount() {
        return discountamount;
    }

    public void setDiscountamount(double discountamount) {
        this.discountamount = discountamount;
    }

    public int getSalescount() {
        return salescount;
    }

    public void setSalescount(int salescount) {
        this.salescount = salescount;
    }

    public double getSalesamount() {
        return salesamount;
    }

    public void setSalesamount(double salesamount) {
        this.salesamount = salesamount;
    }

    public double getCashpaidamount() {
        return cashpaidamount;
    }

    public void setCashpaidamount(double cashpaidamount) {
        this.cashpaidamount = cashpaidamount;
    }

    public double getCreditpaidamount() {
        return creditpaidamount;
    }

    public void setCreditpaidamount(double creditpaidamount) {
        this.creditpaidamount = creditpaidamount;
    }

    public int getCashpaidcount() {
        return cashpaidcount;
    }

    public void setCashpaidcount(int cashpaidcount) {
        this.cashpaidcount = cashpaidcount;
    }

    public int getCreditpaidcount() {
        return creditpaidcount;
    }

    public void setCreditpaidcount(int creditpaidcount) {
        this.creditpaidcount = creditpaidcount;
    }

    public Date getDatestart() {
        return datestart;
    }

    public void setDatestart(Date datestart) {
        this.datestart = datestart;
    }

    public Date getDateend() {
        return dateend;
    }

    public void setDateend(Date dateend) {
        this.dateend = dateend;
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

    public int getAnulatedreceiptscount() {
        return anulatedreceiptscount;
    }

    public void setAnulatedreceiptscount(int anulatedreceiptscount) {
        this.anulatedreceiptscount = anulatedreceiptscount;
    }

    public double getAnulatedreceiptsamount() {
        return anulatedreceiptsamount;
    }

    public void setAnulatedreceiptsamount(double anulatedreceiptsamount) {
        this.anulatedreceiptsamount = anulatedreceiptsamount;
    }

    public int getAnulatedcashpaymentcount() {
        return anulatedcashpaymentcount;
    }

    public void setAnulatedcashpaymentcount(int anulatedcashpaymentcount) {
        this.anulatedcashpaymentcount = anulatedcashpaymentcount;
    }

    public double getAnulatedcashpaymentamount() {
        return anulatedcashpaymentamount;
    }

    public void setAnulatedcashpaymentamount(double anulatedcashpaymentamount) {
        this.anulatedcashpaymentamount = anulatedcashpaymentamount;
    }

    public int getAnulatedcreditpaymentcount() {
        return anulatedcreditpaymentcount;
    }

    public void setAnulatedcreditpaymentcount(int anulatedcreditpaymentcount) {
        this.anulatedcreditpaymentcount = anulatedcreditpaymentcount;
    }

    public double getAnulatedcreditpaymentamount() {
        return anulatedcreditpaymentamount;
    }

    public void setAnulatedcreditpaymentamount(double anulatedcreditpaymentamount) {
        this.anulatedcreditpaymentamount = anulatedcreditpaymentamount;
    }

    public String getLastreceiptnumber() {
        return lastreceiptnumber;
    }

    public void setLastreceiptnumber(String lastreceiptnumber) {
        this.lastreceiptnumber = lastreceiptnumber;
    }
}
