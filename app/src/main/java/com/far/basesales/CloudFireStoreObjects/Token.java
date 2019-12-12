package com.far.basesales.CloudFireStoreObjects;

import java.util.HashMap;
import java.util.Map;

public class Token {
    private String code;
    private boolean autodelete;

    public Token(){

    }
    public Token(String code, boolean autodelete){
        this.code = code;
        this.autodelete = autodelete;
    }

    public String getCODE() {
        return code;
    }

    public void setCODE(String CODE) {
        this.code = CODE;
    }

    public boolean isAutodelete() {
        return autodelete;
    }

    public void setAutodelete(boolean autodelete) {
        this.autodelete = autodelete;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("autodelete", autodelete);
        return map;
    }
}
