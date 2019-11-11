package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

public class UserControl {
    private String CODE,TARGET,TARGETCODE, CONTROL,VALUE;
    private Boolean ACTIVE;
    private @ServerTimestamp
    Date DATE, MDATE;

    public UserControl(){

    }
    public UserControl(String code, String target, String targetCode, String control, String value, boolean active){
        this.CODE = code; this.TARGET = target;this.TARGETCODE = targetCode;
        this.CONTROL = control; this.VALUE = value;this.ACTIVE = active;
    }

    public UserControl(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(UserControlController.CODE));
        this.TARGET = c.getString(c.getColumnIndex(UserControlController.TARGET));
        this.TARGETCODE = c.getString(c.getColumnIndex(UserControlController.TARGETCODE));
        this.CONTROL = c.getString(c.getColumnIndex(UserControlController.CONTROL));
        this.VALUE = c.getString(c.getColumnIndex(UserControlController.VALUE));
        this.ACTIVE = c.getString(c.getColumnIndex(UserControlController.ACTIVE)).equals("1");
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UserControlController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UserControlController.MDATE)));

    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(UserControlController.CODE,CODE);
        map.put(UserControlController.TARGET,TARGET);
        map.put(UserControlController.TARGETCODE,TARGETCODE);
        map.put(UserControlController.CONTROL,CONTROL);
        map.put(UserControlController.VALUE,VALUE);
        map.put(UserControlController.ACTIVE, ACTIVE);
        map.put(UserControlController.DATE,(DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(UserControlController.MDATE,(MDATE == null)? FieldValue.serverTimestamp():MDATE);
        return map;

    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getCONTROL() {
        return CONTROL;
    }

    public void setCONTROL(String CONTROL) {
        this.CONTROL = CONTROL;
    }

    public String getVALUE() {
        return VALUE;
    }

    public void setVALUE(String VALUE) {
        this.VALUE = VALUE;
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

    public String getTARGET() {
        return TARGET;
    }

    public void setTARGET(String TARGET) {
        this.TARGET = TARGET;
    }

    public String getTARGETCODE() {
        return TARGETCODE;
    }

    public void setTARGETCODE(String TARGETCODE) {
        this.TARGETCODE = TARGETCODE;
    }

    public Boolean getACTIVE() {
        return ACTIVE;
    }

    public void setACTIVE(Boolean ACTIVE) {
        this.ACTIVE = ACTIVE;
    }
}
