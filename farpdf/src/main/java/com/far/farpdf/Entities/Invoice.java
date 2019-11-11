package com.far.farpdf.Entities;

import java.util.ArrayList;

public class Invoice {
    String label,footer, invoiceNo,date;
    Header header;
    Client client;
    Order order;
    ArrayList<Payment> payments;

    public Invoice(Header h,String label,String footer, String invoiceNo, String date,Client c,  Order o, ArrayList<Payment> p){
        this.header = h;
        this.label = label;
        this.footer = footer;
        this.invoiceNo = invoiceNo;
        this.date = date;
        this.client = c;
        this.order = o;
        this.payments = p;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }
}
