package com.far.basesales.CloudFireStoreObjects;

import com.far.basesales.Controllers.UsersDevicesController;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;

@IgnoreExtraProperties
public class UsersDevices {
        private String CODE,CODEUSER, CODEDEVICE;
        private @ServerTimestamp
        Date DATE, MDATE;

        public UsersDevices(){

        }
        public UsersDevices(String code, String codeUser, String codeDevice){
            this.CODE = code;this.CODEUSER = codeUser; this.CODEDEVICE = codeDevice;
        }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(UsersDevicesController.CODE, CODE);
        map.put(UsersDevicesController.CODEUSER, CODEUSER);
        map.put(UsersDevicesController.CODEDEVICE, CODEDEVICE);
        map.put(UsersDevicesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(UsersDevicesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
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

    public String getCODEDEVICE() {
        return CODEDEVICE;
    }

    public void setCODEDEVICE(String CODEDEVICE) {
        this.CODEDEVICE = CODEDEVICE;
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
