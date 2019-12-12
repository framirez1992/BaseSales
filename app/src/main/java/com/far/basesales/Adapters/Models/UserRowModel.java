package com.far.basesales.Adapters.Models;

import com.far.basesales.Globales.CODES;

public class UserRowModel {
    String code,systemCode, userName,userPassword,  userRole;
    boolean active, inserver;

    public UserRowModel(String code, String systemCode, String userName, String userRole, boolean active, boolean inServer){
       this.code = code;
       this.systemCode = systemCode;
       this.userName = userName;
       this.userRole = userRole;
       this.active = active;
       this.inserver = inServer;
    }

    /*admin*/
    public UserRowModel(String code, String systemCode, String userName,String password, String userRole, boolean active, boolean inServer){
        this.code = code;
        this.systemCode = systemCode;
        this.userName = userName;
        this.userPassword = password;
        this.userRole = userRole;
        this.active = active;
        this.inserver = inServer;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isInserver() {
        return inserver;
    }

    public void setInserver(boolean inserver) {
        this.inserver = inserver;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getSystemCodeDescription(){
        if(systemCode.equals(CODES.USER_SYSTEM_CODE_SU)){
            return "Super User";
        }else if(systemCode.equals(CODES.USER_SYSTEM_CODE_ADMIN)){
            return "Adminitrator";
        }else{
            return "User";
        }
    }
}
