package com.far.basesales.Controllers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.far.basesales.CloudFireStoreObjects.Counter;
import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class Transaction {
    Context context;
    FirebaseFirestore db;
    DB sqlite;
    private static Transaction instance;

    private Transaction(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
        sqlite = DB.getInstance(c);
    }
    public static Transaction getInstance(Context c){
        if(instance == null){
            instance = new Transaction(c);
        }
        return instance;
    }


    public void sendToFireBase(Sales sale, ArrayList<SalesDetails> salesDetails, Receipts receipt, Payment payment, Day day, Counter counter,  OnFailureListener failureListener){
            WriteBatch lote = db.batch();

            if (sale.getMDATE() == null) {
                lote.set(SalesController.getInstance(context).getReferenceFireStore().document(sale.getCODE()), sale.toMap());
            } else {
                lote.update(SalesController.getInstance(context).getReferenceFireStore().document(sale.getCODE()), sale.toMap());
            }


            for (SalesDetails sd : salesDetails){

            if (sd.getMDATE() == null) {
                lote.set(SalesController.getInstance(context).getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
            } else {
                lote.update(SalesController.getInstance(context).getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
            }

        }

            if (receipt.getMdate() == null) {
                lote.set(ReceiptController.getInstance(context).getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
            } else {
                lote.update(ReceiptController.getInstance(context).getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
            }

            if(payment.getMDATE()== null){
                lote.set(PaymentController.getInstance(context).getReferenceFireStore().document(payment.getCODE()), payment.toMap());
            }else{
                lote.update(PaymentController.getInstance(context).getReferenceFireStore().document(payment.getCODE()), payment.toMap());
            }

            if(counter != null){
                lote.set(CounterController.getInstance(context).getReferenceFireStore().document(counter.getCode()), counter.toMap());
            }

            lote.set(DayController.getInstance(context).getReferenceFireStore().document(day.getCode()), day.toMap());

            lote.commit()
                    .addOnFailureListener(failureListener);

    }

//ABONAR RECIBO
    public void sendToFireBase(Receipts receipt, Payment payment,Day day,  OnFailureListener failureListener){
        try {
            WriteBatch lote = db.batch();

            if (receipt.getMdate() == null) {
                lote.set(ReceiptController.getInstance(context).getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
            } else {
                lote.update(ReceiptController.getInstance(context).getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
            }

            if(payment.getMDATE()== null){
                lote.set(PaymentController.getInstance(context).getReferenceFireStore().document(payment.getCODE()), payment.toMap());
            }else{
                lote.update(PaymentController.getInstance(context).getReferenceFireStore().document(payment.getCODE()), payment.toMap());
            }

            lote.set(DayController.getInstance(context).getReferenceFireStore().document(day.getCode()), day.toMap());

            lote.commit()
                    .addOnFailureListener(failureListener);

        }catch(Exception e){
            e.printStackTrace();
        }

    }



    //ANULAR FACTURA
    public void sendToFireBase(Receipts receipt, ArrayList<Payment> payments, ArrayList<Sales> sales, Day day,  OnFailureListener failureListener){
        try {
            WriteBatch lote = db.batch();

            lote.set(ReceiptController.getInstance(context).getReferenceFireStore().document(receipt.getCode()), receipt.toMap());

            if(payments != null){
                for(Payment payment: payments){
                    lote.set(PaymentController.getInstance(context).getReferenceFireStore().document(payment.getCODE()), payment.toMap());
                }
            }

           if(sales != null){
               for(Sales s : sales){
                   lote.set(SalesController.getInstance(context).getReferenceFireStore().document(s.getCODE()), s.toMap());
               }

           }


            lote.set(DayController.getInstance(context).getReferenceFireStore().document(day.getCode()), day.toMap());

            lote.commit()
                    .addOnFailureListener(failureListener);

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //elimina todos los sales, receipts y payments cuyos recibos ya hayan sido saldados.
    public void deleteDataFromFireBase( OnFailureListener failureListener){
        WriteBatch lote = db.batch();
        for(Receipts r: ReceiptController.getInstance(context).getReceipts(new String[]{ReceiptController.STATUS, ReceiptController.CODEUSER}, new String[]{CODES.CODE_RECEIPT_STATUS_CLOSED, Funciones.getCodeuserLogged(context)}, null)){
            lote.delete(ReceiptController.getInstance(context).getDocumentReference(r));

            for(Sales s: SalesController.getInstance(context).getSales(SalesController.CODERECEIPT+" = ?", new String[]{r.getCode()})){
                lote.delete(SalesController.getInstance(context).getDocumentReference(s));

                for(SalesDetails sd: SalesController.getInstance(context).getSalesDetailsByCodeSales(s.getCODE())){
                    lote.delete(SalesController.getInstance(context).getDocumentReference(sd));
                }
            }

            for(Payment p: PaymentController.getInstance(context).getPayments(PaymentController.CODERECEIPT+" = ?", new String[]{r.getCode()}, null)){
                lote.delete(PaymentController.getInstance(context).getDocumentReference(p));
            }
        }

        lote.commit()
                .addOnFailureListener(failureListener);
    }


    public void deleteLocalData(){
        for(Receipts r: ReceiptController.getInstance(context).getReceipts(new String[]{ReceiptController.STATUS, ReceiptController.CODEUSER}, new String[]{CODES.CODE_RECEIPT_STATUS_CLOSED, Funciones.getCodeuserLogged(context)}, null)){
            ReceiptController.getInstance(context).delete(ReceiptController.CODE+" = ?", new String[]{r.getCode()});

            for(Sales s: SalesController.getInstance(context).getSales(SalesController.CODERECEIPT+" = ?", new String[]{r.getCode()})){
                SalesController.getInstance(context).deleteHeadDetail(s);
            }

            for(Payment p: PaymentController.getInstance(context).getPayments(PaymentController.CODERECEIPT+" = ?", new String[]{r.getCode()}, null)){
                PaymentController.getInstance(context).delete(PaymentController.CODERECEIPT+" = ?", new String[]{p.getCODE()});
            }
        }
    }
}
