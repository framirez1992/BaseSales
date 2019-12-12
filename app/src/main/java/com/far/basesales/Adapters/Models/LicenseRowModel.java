package com.far.basesales.Adapters.Models;

import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Utils.Funciones;

public class LicenseRowModel {
    String code, clientName,counter, dateIni, dateEnd, days, devices;
    boolean status, inServer;
    Licenses licenses;

    public LicenseRowModel(String code, String clientName, String counter, String dateIni, String dateEnd, String days, String devices, boolean status, boolean inserver){
        this.code = code; this.clientName = clientName; this.counter = counter; this.dateIni = dateIni; this.dateEnd = dateEnd; this.days = days;
        this.devices = devices; this.status = status;this.inServer = inserver;
    }

    public LicenseRowModel(Licenses l){
        this.code = l.getCODE();
        this.clientName = l.getCLIENTNAME();
        this.counter = l.getCOUNTER()+"";
        this.dateIni = Funciones.getFormatedDateRepDom(l.getDATEINI());
        this.dateEnd = Funciones.getFormatedDateRepDom(l.getDATEEND());
        this.days = l.getDAYS()+"";
        this.devices = l.getDEVICES()+"";
        this.status = l.isENABLED();
        this.inServer = l.getLASTUPDATE()!=null;
        this.licenses = l;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public String getDateIni() {
        return dateIni;
    }

    public void setDateIni(String dateIni) {
        this.dateIni = dateIni;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDevices() {
        return devices;
    }

    public void setDevices(String devices) {
        this.devices = devices;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isInServer() {
        return inServer;
    }

    public void setInServer(boolean inServer) {
        this.inServer = inServer;
    }

    public Licenses getLicenses() {
        return licenses;
    }

    public void setLicenses(Licenses licenses) {
        this.licenses = licenses;
    }
}
