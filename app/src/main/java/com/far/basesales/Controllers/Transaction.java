package com.far.basesales.Controllers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.DataBase.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


    public void sendToFireBase(Sales sale, ArrayList<SalesDetails> salesDetails, Receipts receipt, Payment payment, Day day,  OnFailureListener failureListener, OnCompleteListener onCompleteListener, OnSuccessListener onSuccessListener){
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

            lote.set(DayController.getInstance(context).getReferenceFireStore().document(day.getCode()), day.toMap());

            lote.commit()
                    .addOnFailureListener(failureListener)
                    .addOnCompleteListener(onCompleteListener)
                    .addOnSuccessListener(onSuccessListener);

    }

//ABONAR RECIBO
    public void sendToFireBase(Receipts receipt, Payment payment,Day day,  OnFailureListener failureListener, OnCompleteListener onCompleteListener, OnSuccessListener onSuccessListener){
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
                    .addOnFailureListener(failureListener)
                    .addOnCompleteListener(onCompleteListener)
                    .addOnSuccessListener(onSuccessListener);

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
