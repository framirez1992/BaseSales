package com.far.basesales.CloudFireStoreObjects;

import android.os.Parcelable;

import com.far.basesales.Controllers.LicenseController;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties
public class Licenses implements Serializable {
    private String CODE,PASSWORD, CLIENTNAME ;
    private int COUNTER,DAYS,DEVICES, STATUS;
    private boolean ENABLED, UPDATED;
    private @ServerTimestamp
    Date DATEINI, DATEEND, LASTUPDATE;

    public Licenses(){

    }
    public Licenses(String code, String password,String clientName, Date dateIni, Date dateEnd, int counter, int days, int devices, boolean enabled, boolean updated, Date lastUpdate, int status){
        this.CODE = code; this.CLIENTNAME = clientName;this.DATEINI = dateIni; this.DATEEND = dateEnd;
        this.COUNTER = counter; this.DAYS = days; this.DEVICES = devices;
        this.ENABLED = enabled;this.PASSWORD = password;this.STATUS = status;
        this.UPDATED = updated; this.LASTUPDATE =lastUpdate;
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(LicenseController.CODE, CODE);
        map.put(LicenseController.CLIENTNAME, CLIENTNAME);
        map.put(LicenseController.PASSWORD, PASSWORD);
        map.put(LicenseController.COUNTER, COUNTER);
        map.put(LicenseController.DAYS, DAYS);
        map.put(LicenseController.DEVICES, DEVICES);
        map.put(LicenseController.STATUS, STATUS);
        map.put(LicenseController.ENABLED, ENABLED);
        map.put(LicenseController.UPDATED, UPDATED);
        map.put(LicenseController.DATEINI, (DATEINI != null)?DATEINI: FieldValue.serverTimestamp());
        map.put(LicenseController.DATEEND, (DATEEND != null)?DATEEND:  FieldValue.serverTimestamp());
        map.put(LicenseController.LASTUPDATE, (LASTUPDATE != null)?LASTUPDATE:  FieldValue.serverTimestamp());

        return map;
    }
    public String getCODE() {
        return CODE;
    }

    public String getCLIENTNAME() {
        return CLIENTNAME;
    }

    public void setCLIENTNAME(String CLIENTNAME) {
        this.CLIENTNAME = CLIENTNAME;
    }

    public int getCOUNTER() {
        return COUNTER;
    }

    public int getDAYS() {
        return DAYS;
    }

    public int getDEVICES() {
        return DEVICES;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public boolean isENABLED() {
        return ENABLED;
    }

    public boolean isUPDATED() {
        return UPDATED;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }


    public void setCOUNTER(int COUNTER) {
        this.COUNTER = COUNTER;
    }

    public void setDAYS(int DAYS) {
        this.DAYS = DAYS;
    }

    public void setDEVICES(int DEVICES) {
        this.DEVICES = DEVICES;
    }

    public void setSTATUS(int STATUS) {
        this.STATUS = STATUS;
    }

    public void setENABLED(boolean ENABLED) {
        this.ENABLED = ENABLED;
    }

    public void setUPDATED(boolean UPDATED) {
        this.UPDATED = UPDATED;
    }

    public Date getDATEINI() {
        return DATEINI;
    }

    public void setDATEINI(Date DATEINI) {
        this.DATEINI = DATEINI;
    }

    public Date getDATEEND() {
        return DATEEND;
    }

    public void setDATEEND(Date DATEEND) {
        this.DATEEND = DATEEND;
    }

    public Date getLASTUPDATE() {
        return LASTUPDATE;
    }

    public void setLASTUPDATE(Date LASTUPDATE) {
        this.LASTUPDATE = LASTUPDATE;
    }
}
