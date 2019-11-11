package com.far.basesales.Adapters.Models;

public class SimpleSeleccionRowModel {
    String code, name;
    boolean checked;

    public SimpleSeleccionRowModel(String code, String name, boolean checked){
        this.code = code;
        this.name = name;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
