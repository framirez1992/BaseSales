package com.far.basesales.Adapters.Models;

public class EditSelectionRowModel {
    String code, description, text;
    boolean checked;

    public EditSelectionRowModel(String code, String description, String text, boolean checked){
        this.code = code;
        this.description = description;
        this.text = text;
        this.checked = checked;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
