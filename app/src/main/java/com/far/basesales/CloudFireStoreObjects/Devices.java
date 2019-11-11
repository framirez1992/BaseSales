package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


@IgnoreExtraProperties
public class Devices {
    private String CODE;
    private boolean ENABLED;
    private @ServerTimestamp
    Date DATE, MDATE;
    public Devices(){

    }
    public Devices(String code, boolean enabled){
        this.CODE = code; this.ENABLED = enabled;
    }
    public Devices(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(DevicesController.CODE));
        this.ENABLED = c.getInt(c.getColumnIndex(DevicesController.ENABLED)) == 1;
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(DevicesController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(DevicesController.MDATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public boolean isENABLED() {
        return ENABLED;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public void setENABLED(boolean ENABLED) {
        this.ENABLED = ENABLED;
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
