package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
public class Users {
    private String CODE,SYSTEMCODE, PASSWORD, USERNAME,COMPANY,ROLE;
    private  boolean ENABLED;
    private @ServerTimestamp
    Date DATE, MDATE;
    DocumentReference documentReference;

    public Users(){

    }
    public Users(String cod, String systemCode, String passwrd, String usrName, String role, String company, boolean enabled){

        this.CODE = cod; this.SYSTEMCODE = systemCode; this.PASSWORD = passwrd; this.USERNAME = usrName;
        this.ROLE = role; this.ENABLED = enabled;this.COMPANY = company;
    }
    public Users(String cod, String systemCode, String passwrd, String usrName, boolean enabled){

        this.CODE = cod; this.SYSTEMCODE = systemCode; this.PASSWORD = passwrd; this.USERNAME = usrName;
        this.ROLE = ""; this.ENABLED = enabled;this.COMPANY = "";
    }

    public HashMap<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put(UsersController.CODE, CODE);
        map.put(UsersController.SYSTEMCODE, SYSTEMCODE);
        map.put(UsersController.PASSWORD, PASSWORD);
        map.put(UsersController.USERNAME, USERNAME);
        map.put(UsersController.ROLE, ROLE);
        map.put(UsersController.ENABLED, ENABLED);
        map.put(UsersController.COMPANY, COMPANY);
        map.put(UsersController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
        map.put(UsersController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

        return map;
    }

    public Users(Cursor c){
        this.CODE = c.getString(c.getColumnIndex(UsersController.CODE));
        this.SYSTEMCODE = c.getString(c.getColumnIndex(UsersController.SYSTEMCODE));
        this.PASSWORD = c.getString(c.getColumnIndex(UsersController.PASSWORD));
        this.USERNAME = c.getString(c.getColumnIndex(UsersController.USERNAME));
        this.ROLE = c.getString(c.getColumnIndex(UsersController.ROLE));
        this.ENABLED = c.getString(c.getColumnIndex(UsersController.ENABLED)).equals("1");
        this.COMPANY = c.getString(c.getColumnIndex(UsersController.COMPANY));;
        this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UsersController.DATE)));
        this.MDATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(UsersController.MDATE)));
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getCOMPANY() {
        return COMPANY;
    }

    public void setCOMPANY(String COMPANY) {
        this.COMPANY = COMPANY;
    }

    public String getROLE() {
        return ROLE;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    public boolean isENABLED() {
        return ENABLED;
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

    public String getSYSTEMCODE() {
        return SYSTEMCODE;
    }

    public void setSYSTEMCODE(String SYSTEMCODE) {
        this.SYSTEMCODE = SYSTEMCODE;
    }

    public DocumentReference getDocumentReference() {
        return documentReference;
    }

    public void setDocumentReference(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }
}
