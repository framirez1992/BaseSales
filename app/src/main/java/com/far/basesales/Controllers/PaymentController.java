package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.bluetoothlibrary.Printer.Print;
import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentController {
    public static final String TABLE_NAME ="PAYMENTS";
    public static  String CODE = "code",CODERECEIPT = "codereceipt",CODEUSER="codeuser", CODECLIENT="codeclient",  TYPE = "type" ,
            SUBTOTAL = "subtotal",TAX="tax", DISCOUNT = "discount", TOTAL = "total",CODEDAY = "codeday",  DATE = "date", MDATE = "mdate";
    String[] columns = new String[]{CODE,CODERECEIPT,CODEUSER, CODECLIENT, TYPE, SUBTOTAL,TAX, DISCOUNT,TOTAL,CODEDAY, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+CODERECEIPT+" TEXT,"+CODEUSER+" TEXT, "+CODECLIENT+" TEXT, "+TYPE+" TEXT, "+SUBTOTAL+" NUMERIC,"+TAX+" NUMERIC,"+DISCOUNT+" NUMERIC, "+TOTAL+" NUMERIC," +
            CODEDAY+" TEXT,  "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;

    private static PaymentController instance;
    private PaymentController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static PaymentController getInstance(Context context){
        if(instance == null){
            instance = new PaymentController(context);
        }
        return instance;
    }
    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersPayments);
        return reference;
    }


    public void sendToFireBase(Payment payment, OnSuccessListener successListener, OnCompleteListener completeListener, OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(payment.getCODE()), payment.toMap());
            lote.commit().addOnSuccessListener(successListener)
                    .addOnCompleteListener(completeListener)
                    .addOnFailureListener(failureListener);

    }

    public void deleteFromFireBase(Payment payment){
        try {
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(payment.getCODE()));


            lote.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public long insert(Payment p){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(CODERECEIPT,p.getCODERECEIPT() );
        cv.put(CODEUSER,p.getCODEUSER() );
        cv.put(CODECLIENT,p.getCODECLIENT() );
        cv.put(TYPE,p.getTYPE());
        cv.put(SUBTOTAL,p.getSUBTOTAL() );
        cv.put(TAX,p.getTAX());
        cv.put(DISCOUNT,p.getDISCOUNT() );
        cv.put(TOTAL,p.getTOTAL() );
        cv.put(CODEDAY, p.getCODEDAY());
        cv.put(DATE, Funciones.getFormatedDate((Date) p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate((Date) p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Payment p){
        long result = update(p, CODE+" = ?", new String[]{p.getCODE()});
        return result;
    }
    public long update(Payment p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(CODERECEIPT,p.getCODERECEIPT() );
        cv.put(CODEUSER,p.getCODEUSER() );
        cv.put(CODECLIENT,p.getCODECLIENT() );
        cv.put(TYPE,p.getTYPE());
        cv.put(SUBTOTAL,p.getSUBTOTAL() );
        cv.put(TAX,p.getTAX());
        cv.put(DISCOUNT,p.getDISCOUNT() );
        cv.put(TOTAL,p.getTOTAL() );
        cv.put(CODEDAY, p.getCODEDAY());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersPayments).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getAllDataFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> company =getReferenceFireStore().get();
            company.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Payment object = dc.getDocument().toObject(Payment.class);
                            delete(null, null);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Payment> getPayments(String where, String[]args, String orderBy){
        ArrayList<Payment> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Payment(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public Payment getPaymentByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Payment> pts = getPayments(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }



    public void searchChanges(OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        Date mdate = DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore().
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }

    }

    public void getPaymentFromFireBase(String code, OnSuccessListener<QuerySnapshot> success, OnFailureListener failure){
            getReferenceFireStore().
                    whereEqualTo(CODE, code).
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);

    }


    public void consumeQuerySnapshot(QuerySnapshot querySnapshot){

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                for(DocumentSnapshot doc: querySnapshot){
                    Payment obj = doc.toObject(Payment.class);
                    if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                        insert(obj);
                    }
                }
            }

    }



    public void searchPaymentFromFireBaseByCodeReceipt(String codeReceipt, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){
            getReferenceFireStore().
                    whereEqualTo(CODERECEIPT, codeReceipt).
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);

    }


    public void searchAllPaymentsFromFireBase(OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){
        getReferenceFireStore().whereEqualTo(CODEUSER, Funciones.getCodeuserLogged(context)).
                get().
                addOnSuccessListener(success).
                addOnFailureListener(failure);

    }

    public DocumentReference getDocumentReference(Payment p){

        return getReferenceFireStore().document(p.getCODE());
    }


    public void fillSpinnerPaymentType(Spinner spn){
        ArrayList<KV> data = new ArrayList<>();
        data.add(new KV(CODES.PAYMENTTYPE_CASH, "EFECTIVO"));
        data.add(new KV(CODES.PAYMENTTYPE_CREDIT, "CREDITO"));

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }


    public String printPayment(String codePayment)throws Exception{

        boolean clientsControl = UserControlController.getInstance(context).searchSimpleControl(CODES.USERSCONTROL_CLIENTS)!= null;

        Print p = new Print(context,Print.PULGADAS.PULGADAS_2);
        CompanyController.getInstance(context).addCompanyToPrint(p);
        Payment payment = getPaymentByCode(codePayment);
        Receipts receipt = ReceiptController.getInstance(context).getReceiptByCode(payment.getCODERECEIPT());
        Clients c = null;
        if(clientsControl){
            c = ClientsController.getInstance(context).getClientByCode(receipt.getCodeclient());
        }

        Users u = UsersController.getInstance(context).getUserByCode(receipt.getCodeuser());

        p.drawText(" ");
        p.drawText("Fecha: "+Funciones.getFormatedDateRepDomHour(payment.getDATE()));
        p.drawText("Codigo: "+payment.getCODE());
        p.drawText(" ");
        p.drawText("Vendedor: "+u.getUSERNAME());
        if(clientsControl){
            p.drawText("Cliente:  "+c.getNAME());
        }
        p.drawText(" ");
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        p.drawText("RECIBO DE PAGO");
        p.drawLine();
        p.drawText("Detalle");
        p.drawLine();
        String paymentMethod ="UNKNOWN";
        if(payment.getTYPE().equals(CODES.PAYMENTTYPE_CASH)){
            paymentMethod = "EFECTIVO";
        }else if(payment.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)){
            paymentMethod = "TARJETA DE CREDITO";
        }
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);
        p.drawText("Factura: "+receipt.getCode());
        p.drawText("Pago   :"+paymentMethod);
        p.drawText("Total pagado: $"+Funciones.formatMoney(payment.getTOTAL()));

        p.drawLine();

        p.drawText(" ");
        p.drawText(" ");


        p.printText(Funciones.getMacAddress(context));
        return null;
    }

}
