package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.CounterController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class Counter {
    String code, type, codeuser, data, data2, data3;
    int count;
    @ServerTimestamp
    Date date, mdate;
    
    public Counter(){
        
    }

    public Counter(String code, String codeuser, String type, int count, String data, String data2, String data3){
        this.code = code; this.codeuser = codeuser; this.type = type; this.count = count; this.data = data; this.data2 = data2; this.data3 = data3;
    }


    public Counter(Cursor c){
        this.code = c.getString(c.getColumnIndex(CounterController.CODE));
        this.type = c.getString(c.getColumnIndex(CounterController.TYPE));
        this.codeuser = c.getString(c.getColumnIndex(CounterController.CODEUSER));
        this.count = c.getInt(c.getColumnIndex(CounterController.COUNT));
        this.data =  c.getString(c.getColumnIndex(CounterController.DATA));
        this.data2 =  c.getString(c.getColumnIndex(CounterController.DATA2));
        this.data3 =  c.getString(c.getColumnIndex(CounterController.DATA3));
        this.date = Funciones.parseStringToDate(c.getString(c.getColumnIndex(CounterController.DATE)));
        this.mdate = Funciones.parseStringToDate(c.getString(c.getColumnIndex(CounterController.MDATE)));
    }
    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(CounterController.CODE, code);
        map.put(CounterController.TYPE, type);
        map.put(CounterController.CODEUSER, codeuser);
        map.put(CounterController.COUNT, count);
        map.put(CounterController.DATA, data);
        map.put(CounterController.DATA2, data2);
        map.put(CounterController.DATA3, data3);
        map.put(CounterController.DATE, (date == null)? FieldValue.serverTimestamp():date);
        map.put(CounterController.MDATE, (mdate == null)? FieldValue.serverTimestamp():mdate);

        return map;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCodeuser() {
        return codeuser;
    }

    public void setCodeuser(String codeuser) {
        this.codeuser = codeuser;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
}
