package com.far.basesales.CloudFireStoreObjects;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class UsersDevices {
        private String CODE,CODEUSER, CODEDEVICE;
        private @ServerTimestamp
        Date DATE, MDATE;

        public UsersDevices(){

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
