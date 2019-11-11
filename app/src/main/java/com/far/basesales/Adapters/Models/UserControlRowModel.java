package com.far.basesales.Adapters.Models;

public class UserControlRowModel {
    String code,description, target,targetDescription,targetCode, targetCodedescription;
    boolean active, inserver;

    public UserControlRowModel(String code, String description, String target, String targetDescription, String targetCode, String targetCodedescription, boolean active, boolean inServer){
        this.code = code;
        this.description = description;
        this.target = target;
        this.targetDescription = targetDescription;
        this.targetCode = targetCode;
        this.targetCodedescription = targetCodedescription;
        this.active = active;
        this.inserver = inServer;
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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(String targetDescription) {
        this.targetDescription = targetDescription;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public String getTargetCodedescription() {
        return targetCodedescription;
    }

    public void setTargetCodedescription(String targetCodedescription) {
        this.targetCodedescription = targetCodedescription;
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
}
