package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.bluetoothlibrary.Printer.Print;
import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;


public class ReceiptController {
    public static final String TABLE_NAME ="RECEIPTS";
    //public static final String TABLE_NAME_HISTORY ="RECEIPTS_HISTORY";
    public static  String CODE = "code",CODEUSER = "codeuser",CODESALE = "codesale",CODECLIENT="codeclient", STATUS = "status",  NCF = "ncf" ,SUBTOTAL="subtotal",TAXES = "taxes", DISCOUNT="discount", TOTAL = "total",PAIDAMOUNT="paidamount",
            DATE = "date", MDATE = "mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+CODEUSER+" TEXT,"+CODESALE+" TEXT, "+CODECLIENT+" TEXT,  "+STATUS+" TEXT, "+NCF+" TEXT,"+SUBTOTAL+" NUMERIC,"+TAXES+" NUMERIC,"+DISCOUNT+" NUMERIC, "+TOTAL+", "+PAIDAMOUNT+" NUMERIC, " +
            ""+DATE+" TEXT, "+MDATE+" TEXT)";
    public static String[] columns = new String[]{CODE,CODEUSER,CODESALE, CODECLIENT, STATUS, NCF,SUBTOTAL,TAXES,DISCOUNT,TOTAL,PAIDAMOUNT,  DATE, MDATE};
    Context context;
    FirebaseFirestore db;
    private static ReceiptController instance;
    private ReceiptController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }
    public static ReceiptController getInstance(Context c){
        if(instance == null){
            instance = new ReceiptController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersReceipts);
        return reference;
    }


    public long insert(Receipts r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(CODESALE, r.getCodesale());
        cv.put(CODECLIENT, r.getCodeclient());
        cv.put(STATUS, r.getStatus());
        cv.put(NCF,r.getNcf());
        cv.put(SUBTOTAL, r.getSubTotal());
        cv.put(TAXES, r.getTaxes());
        cv.put(DISCOUNT, r.getDiscount());
        cv.put(TOTAL, r.getTotal());
        cv.put(PAIDAMOUNT, r.getPaidamount());
        cv.put(DATE, Funciones.getFormatedDate(r.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Receipts r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(CODESALE, r.getCodesale());
        cv.put(CODECLIENT, r.getCodeclient());
        cv.put(STATUS, r.getStatus());
        cv.put(NCF,r.getNcf());
        cv.put(SUBTOTAL, r.getSubTotal());
        cv.put(TAXES, r.getTaxes());
        cv.put(DISCOUNT, r.getDiscount());
        cv.put(TOTAL, r.getTotal());
        cv.put(PAIDAMOUNT, r.getPaidamount());
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,CODE+"=?", new String[]{r.getCode()});
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Receipts> getReceipts(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Receipts> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DATE;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Receipts(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Receipts getReceiptByCode(String code){
       ArrayList<Receipts> array = getReceipts(new String[]{CODE}, new String[]{code}, null);
       return array.size()>0?array.get(0):null;
    }


    public void getDataFromFireBase(OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> receipts = getReferenceFireStore().get();
            receipts.addOnSuccessListener(onSuccessListener);
            receipts.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Receipts object = dc.getDocument().toObject(Receipts.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCode()};
                            delete(where, args);
                            insert(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendToFireBase(Receipts receipt){
        try {
            WriteBatch lote = db.batch();
                if (receipt.getMdate() == null) {
                    lote.set(getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
                } else {
                    lote.update(getReferenceFireStore().document(receipt.getCode()), receipt.toMap());
                }

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


    /*public ArrayList<ReceiptSavedModel> getReceiptsSM(String codeAreaDetail){
        ArrayList<ReceiptSavedModel> result = new ArrayList<>();
        try {
            String sql = "SELECT r." + CODE + " as CODE,r."+STATUS+" as STATUS, r." + CODEUSER + " as CODEUSER, u." + UsersController.USERNAME + " as USERNAME, r." + NCF + " as NCF, " +
                    "ad." + AreasDetailController.CODEAREA + " as CODEAREA, a." + AreasController.DESCRIPTION + " as AREADESCRIPTION, ad." + AreasDetailController.CODE + " as CODEAREADETAIL, ad." + AreasDetailController.DESCRIPTION + " as AREADETAILDESCRIPTION, " +
                    "r." + SUBTOTAL + " as SUBTOTAL, r." + TAXES + " as TAXES, r." + DISCOUNT + " as DISCOUNT, r." + TOTAL + " as TOTAL, r." + DATE + " as DATE, r." + MDATE + " as MDATE " +
                    "FROM " + TABLE_NAME + " r " +
                    "INNER JOIN " + UsersController.TABLE_NAME + " u ON r." + CODEUSER + " = u." + UsersController.CODE + " " +
                    "INNER JOIN " + AreasDetailController.TABLE_NAME + " ad on r." + CODEAREADETAIL + " = ad." + AreasDetailController.CODE + " " +
                    "INNER JOIN " + AreasController.TABLE_NAME + " a on ad." + AreasDetailController.CODEAREA + " = a." + AreasController.CODE + " " +
                    "ORDER BY r." + DATE + " DESC";

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                result.add(new ReceiptSavedModel(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

    }*/


    public ArrayList<ReceiptRowModel> getReceiptsRM(String where, String[]args){
        ArrayList<ReceiptRowModel> result = new ArrayList<>();
        try {
            String sql = "SELECT r." + CODE + " as CODE,r."+STATUS+" as STATUS, r." + DATE + " as DATE, c." + ClientsController.CODE + " as CODECLIENT, c." + ClientsController.NAME + " as CLIENTNAME, " +
                    "c." + ClientsController.DOCUMENT + " as DOCUMENT, c." + ClientsController.PHONE + " as PHONE,"+
                    "r." + TOTAL + " as TOTAL, r."+PAIDAMOUNT+" as PAID " +
                    "FROM " + TABLE_NAME + " r " +
                    "INNER JOIN " + ClientsController.TABLE_NAME + " c ON r." + CODECLIENT + " = c." + ClientsController.CODE + " " +
                    "ORDER BY r." + DATE + " DESC";

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                String code = c.getString(c.getColumnIndex("CODE"));
                String status = c.getString(c.getColumnIndex("STATUS"));
                String date = c.getString(c.getColumnIndex("DATE"));
                String codeClient = c.getString(c.getColumnIndex("CODECLIENT"));
                String clientName = c.getString(c.getColumnIndex("CLIENTNAME"));
                String document = c.getString(c.getColumnIndex("DOCUMENT"));
                String phone = c.getString(c.getColumnIndex("PHONE"));
                double total = c.getDouble(c.getColumnIndex("TOTAL"));
                double paid = c.getDouble(c.getColumnIndex("PAID"));

               //String code, String status, String codeClient, String clientName, String clientDocument, String clientPhone, String date, double total
                result.add(new ReceiptRowModel(code,status,codeClient,clientName,document,phone,date,total, paid));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;

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


    public void consumeQuerySnapshot(QuerySnapshot querySnapshot){
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Receipts obj = doc.toObject(Receipts.class);
                if(update(obj) <=0){
                    insert(obj);
                }
            }
        }

    }

    public  String printReceipt(String codeReceipt)throws Exception{

        Print p = new Print(context,Print.PULGADAS.PULGADAS_2);
        CompanyController.getInstance(context).addCompanyToPrint(p);

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        p.drawLine();
        p.drawText("Detalle");
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);

        double total = 0.0;
        for(SalesDetailModel sdm :SalesController.getInstance(context).getSaleDetailModels(codeReceipt)){
            total+=Double.parseDouble(sdm.getTotal());
            p.drawText(sdm.getProductDescription());
            p.drawText(Funciones.reservarCaracteres("Cant:"+sdm.getQuantity(),9)+Funciones.reservarCaracteres(sdm.getMeasureDescription(),10)+Funciones.reservarCaracteresAlinearDerecha(" $"+Funciones.formatDecimal(sdm.getTotal()),13));
        }
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_RIGHT);
        p.drawText("Total:"+Funciones.formatDecimal(total), Print.TEXT_ALIGN.RIGHT);


        p.printText("02:3D:D3:DB:D5:06");
        return null;
    }

}
