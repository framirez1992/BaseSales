package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;



public class MeasureUnits {
    private String CODE, DESCRIPTION;
    private @ServerTimestamp
    Date DATE, MDATE;
    public  MeasureUnits(){

    }
    public MeasureUnits( String code, String description){
        this.CODE = code; this.DESCRIPTION = description;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(MeasureUnitsController.CODE, CODE);
        map.put(MeasureUnitsController.DESCRIPTION,DESCRIPTION);
        map.put(MeasureUnitsController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(MeasureUnitsController.MDATE,(MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }
    public MeasureUnits(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(MeasureUnitsController.CODE));
        this.DESCRIPTION = c.getString(c.getColumnIndex(MeasureUnitsController.DESCRIPTION));
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(MeasureUnitsController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(MeasureUnitsController.MDATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
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
