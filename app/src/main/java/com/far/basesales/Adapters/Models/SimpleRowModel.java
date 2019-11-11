package com.far.basesales.Adapters.Models;

public class SimpleRowModel {
    String id, text;
    boolean inServer;
    public SimpleRowModel(String id, String text, boolean inServer){
        this.id = id;
        this.text = text;
        this.inServer = inServer;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
    public boolean isInServer(){
        return inServer;
    }
}
