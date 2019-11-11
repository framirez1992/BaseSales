package com.far.basesales.Adapters.Models;

public class ReceiptRowModel {
    String code,status, codeClient, clientName, clientDocument, clientPhone, date;
    double total;

    public ReceiptRowModel(String code, String status, String codeClient, String clientName, String clientDocument, String clientPhone, String date, double total){
        this.code = code; this.status = status; this.codeClient = codeClient; this.clientName = clientName;
        this.clientDocument = clientDocument; this.clientPhone = clientPhone; this.date = date; this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCodeClient() {
        return codeClient;
    }

    public void setCodeClient(String codeClient) {
        this.codeClient = codeClient;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientDocument() {
        return clientDocument;
    }

    public void setClientDocument(String clientDocument) {
        this.clientDocument = clientDocument;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
