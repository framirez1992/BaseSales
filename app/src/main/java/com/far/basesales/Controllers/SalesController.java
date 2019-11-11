package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class SalesController {
    public static final String TABLE_NAME = Tablas.generalUsersSales;
    public static String CODE = "code",STATUS = "status", SUBTOTAL="subtotal",TOTALTAXES="totaltaxes" ,TOTALDISCOUNT = "totaldiscount",TOTAL="total",CODEUSER = "codeuser",  CODERECEIPT = "codereceipt", DATE = "date",MDATE = "mdate" ;
    String[]columns = new String[]{CODE,STATUS, DATE, MDATE, SUBTOTAL, TOTALTAXES,TOTALDISCOUNT, TOTAL , CODEUSER, CODERECEIPT};

    public static final String TABLE_NAME_DETAIL = Tablas.generalUsersSalesDetails;
    public static String DETAIL_CODE = "code",DETAIL_CODESALES = "codesales", DETAIL_CODEPRODUCT = "codeproduct",
            DETAIL_DISCOUNT = "discount", DETAIL_POSITION = "position", DETAIL_PRICE = "price",DETAIL_TAX = "tax",
            DETAIL_QUANTITY = "quantity", DETAIL_CODEUND = "codeund", DETAIL_DATE="date", DETAIL_MDATE="mdate";
    String[]columnsDetails = new String[]{DETAIL_CODE,DETAIL_CODESALES, DETAIL_CODEPRODUCT,DETAIL_CODEUND,DETAIL_DISCOUNT,DETAIL_POSITION,DETAIL_QUANTITY,DETAIL_PRICE,DETAIL_TAX, DATE, MDATE};


    Context context;
    FirebaseFirestore db;
    DB sqlite;
    private static SalesController instance;

    private SalesController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
        sqlite = DB.getInstance(c);
    }
    public static SalesController getInstance(Context c){
        if(instance == null){
           instance = new SalesController(c);
        }
        return instance;
    }

    public static String getQueryCreateHead(){
              String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
                +CODE+" TEXT, "+CODEUSER+" TEXT,"+STATUS+" TEXT,"+SUBTOTAL+" DECIMAL(11, 3), "+TOTALTAXES+" DECIMAL(11, 3), "+TOTALDISCOUNT+" DECIMAL(11, 3),"+TOTAL+" DECIMAL(11, 3), " +CODERECEIPT+" TEXT, "+DATE+" TEXT,"+MDATE+" TEXT )";
              return QUERY_CREATE;
    }

    public static String getQueryCreateDetail(){
              String QUERY_CREATE_DETAIL = "CREATE TABLE "+TABLE_NAME_DETAIL+" ("
                +DETAIL_CODE+" TEXT, "+DETAIL_CODESALES+" TEXT, "+DETAIL_CODEPRODUCT+" TEXT, "+DETAIL_POSITION+" INTEGER, "
                +DETAIL_QUANTITY+" DOUBLE, "+DETAIL_CODEUND+" TEXT,"+DETAIL_PRICE+" DECIMAL(11, 3),"+DETAIL_DISCOUNT+" DECIMAL(11,3), "+DETAIL_TAX+"  DECIMAL(11,3), " +
                DETAIL_DATE+" TEXT, "+DETAIL_MDATE+" TEXT)";
              return QUERY_CREATE_DETAIL;

    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersSales);
        return reference;
    }
    public CollectionReference getReferenceDetailFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersSalesDetails);
        return reference;
    }



    public void save(){

            Sales s = TempOrdersController.getInstance(context).getTempSale();
            s.setSTATUS(CODES.CODE_ORDER_STATUS_OPEN);
            ArrayList<SalesDetails> arraySd = TempOrdersController.getInstance(context).getTempSalesDetails(s);
            sendToFireBase(s, arraySd);
    }

    public long insert(Sales s){
        String table = TABLE_NAME;

        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(TOTALTAXES,s.getTOTALTAXES());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(TOTAL,s.getTOTAL());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(CODERECEIPT, s.getCODERECEIPT());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(table,null,cv);
        return result;
    }

    public long update(Sales s){
        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(TOTALTAXES,s.getTOTALTAXES());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(TOTAL,s.getTOTAL());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(CODERECEIPT, s.getCODERECEIPT());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));

        String where = CODE+" = ? ";
        String table = TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().update(table,cv,where, new String[]{s.getCODE()} );
        return result;
    }

    public long delete(String where, String[] args){
        String table =TABLE_NAME;
        long result = DB.getInstance(context).getWritableDatabase().delete(table,where, args);
        return result;
    }

    public void deleteHeadDetail(ArrayList<Sales> sales){
        for(Sales sale: sales){
            deleteHeadDetail(sale);
        }
    }
    public long deleteHeadDetail(Sales s){
        String where = CODE+" = ? ";
        String[]args = new String[]{s.getCODE()};
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        if(result >0){
           where = DETAIL_CODESALES+" = ? ";
           result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL,where, args);
        }
        return result;
    }


    public long insert_Detail(SalesDetails sd){
        String tabla = TABLE_NAME_DETAIL;
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_TAX,sd.getTAX());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_DATE, Funciones.getFormatedDate(sd.getDATE()));
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(tabla,null,cv);
        return result;
    }

    public long update_Detail(SalesDetails sd){
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_TAX,sd.getTAX());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

        String where = DETAIL_CODE+"= ?  AND "+DETAIL_CODEPRODUCT+"= ? AND "+DETAIL_CODEUND+" = ?";

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME_DETAIL,cv,where, new String[] {sd.getCODE(), sd.getCODEPRODUCT(), sd.getCODEUND()});
        return result;
    }


    public ArrayList<Sales> getSales(String where, String[] args){
        String tabla = TABLE_NAME;
       ArrayList<Sales> result = new ArrayList<>();

        Cursor c = DB.getInstance(context).getReadableDatabase().query(tabla,columns,where, args,null,null,null);
        while(c.moveToNext()){
            result.add(new Sales(c));
        }
        c.close();
        return result;
    }

    public ArrayList<SalesDetails> getSalesDetailsByCodeSales(String code){
        String where = DETAIL_CODESALES+" = ?";
        return getSalesDetails(where, new String[]{code});
    }
    public ArrayList<SalesDetails> getSalesDetails(String where, String[] args){
        String tabla = TABLE_NAME_DETAIL;
        ArrayList<SalesDetails> result = new ArrayList<>();
        Cursor c = DB.getInstance(context).getReadableDatabase().query(tabla,columnsDetails,where, args,null,null,null);
        while(c.moveToNext()){
            result.add(new SalesDetails(c));
        }
        c.close();
        return result;
    }

    public Sales getSaleByCode(String code){
        String table = TABLE_NAME;
        Sales s = null;
        String where = CODE+" = ?";
        String[]args = new String[]{code};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(table,columns,where, args,null,null,null);
        if(c.moveToFirst()){
            s = new Sales(c);
        }
        c.close();
        return s;
    }


    public long delete_Detail(String where, String[] args){
        String table = TABLE_NAME_DETAIL;
        long result = DB.getInstance(context).getWritableDatabase().delete(table,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> sales = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersSales).get();
            sales.addOnSuccessListener(onSuccessListener);
            sales.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getDataDetailsFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> salesDetails = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersSalesDetails).get();
            salesDetails.addOnSuccessListener(onSuccessListener);
            salesDetails.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

  /*  public ArrayList<OrderModel> getOrderModels(String where){
        ArrayList<OrderModel> objects = new ArrayList<>();
        String sql = "Select DISTINCT "+CODE+","+STATUS+", "+NOTES+", "+TOTALDISCOUNT+", "+TOTAL+", " +DATE+", "+MDATE+" "+
                "FROM "+TABLE_NAME+" " +
                ((where == null)?"":"WHERE "+where+" ")
                +"ORDER BY "+MDATE+" DESC";
        Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){

            String code = c.getString(c.getColumnIndex(CODE));
            String status = c.getString(c.getColumnIndex(STATUS));
            String notes = c.getString(c.getColumnIndex(NOTES));
            String lastUpdate = c.getString(c.getColumnIndex(MDATE));
            String date = c.getString(c.getColumnIndex(DATE));
            String minutos = (lastUpdate == null)?"NONE":String.valueOf(Funciones.calcularMinutos(new Date(), Funciones.parseStringToDate(date)))+" Mins";
            boolean edited = (lastUpdate != null && !(date.equals(lastUpdate)));
            OrderModel om = new OrderModel(code,status, notes,minutos,edited, getOrderDetailModels(code));
            objects.add(om);
        }
        return objects;
    }*/


    /*public ArrayList<WorkedOrdersRowModel> getWorkedOrderModels(String where){
        ArrayList<WorkedOrdersRowModel> objects = new ArrayList<>();
        try {
            String sql = "Select DISTINCT s." + CODE + " as CODE, ifnull(ifnull(s." + MDATE + ", s." + DATE + "), 'NONE') as FECHA, a." + AreasController.CODE + " as ACODE,  " +
                    "a." + AreasController.DESCRIPTION + " as ADESCRIPTION,  ad." + AreasDetailController.CODE + " as ADCODE, ad." + AreasDetailController.DESCRIPTION + " as ADDESCRIPTION, s." + STATUS + " as STATUS, " +
                    "s."+TOTAL+" as TOTAL,u."+UsersController.USERNAME+" as USERNAME  " +
                    "FROM " + TABLE_NAME + " s  " +
                    "INNER JOIN " + AreasDetailController.TABLE_NAME + " ad on ad." + AreasDetailController.CODE + " = s." + CODEAREADETAIL + " " +
                    "INNER JOIN " + AreasController.TABLE_NAME + " a on a." + AreasController.CODE + " = ad." + AreasDetailController.CODEAREA + " " +
                    "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" "+
                    ((where == null) ? "" : "WHERE " + where + " ")
                    + "ORDER BY s." + MDATE + " DESC";
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {

                String code = c.getString(c.getColumnIndex("CODE"));
                String total = c.getString(c.getColumnIndex("TOTAL"));
                String userName = c.getString(c.getColumnIndex("USERNAME"));
                String fecha = c.getString(c.getColumnIndex("FECHA"));
                String areaCode = c.getString(c.getColumnIndex("ACODE"));
                String areaDescription = c.getString(c.getColumnIndex("ADESCRIPTION"));
                String areaDetailCode = c.getString(c.getColumnIndex("ADCODE"));
                String areaDetailDescription = c.getString(c.getColumnIndex("ADDESCRIPTION"));
                int s = c.getInt(c.getColumnIndex("STATUS"));
                String status = "NONE";
                switch (s) {
                    case CODES.CODE_ORDER_STATUS_OPEN:
                        status = "Abierta";
                        break;
                    case CODES.CODE_ORDER_STATUS_CLOSED:
                        status = "Cerrada";
                        break;
                    case CODES.CODE_ORDER_STATUS_CANCELED:
                        status = "Cancelada";
                        break;
                    case CODES.CODE_ORDER_STATUS_READY:
                        status = "Lista";
                        break;
                    case CODES.CODE_ORDER_STATUS_DELIVERED:
                        status = "Entregada";
                        break;

                }

                WorkedOrdersRowModel om = new WorkedOrdersRowModel(code, fecha, areaCode, areaDescription, areaDetailCode, areaDetailDescription, status,userName,total);
                objects.add(om);
            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return objects;
    }*/
/*
    public ArrayList<OrderDetailModel> getOrderDetailModels(String code){

        ArrayList<OrderDetailModel> objects = new ArrayList<>();
        try {
            String where = "sd."+DETAIL_CODESALES + " = '" + code +"'";

            String sql = "Select sd."+DETAIL_CODE+" as CODE,sd."+DETAIL_CODESALES+" as CODESALES,p."+ProductsController.CODE+" AS CODEPRODUCT, " +
                    "p." + ProductsController.DESCRIPTION + " as PRODUCTO, sd." + DETAIL_QUANTITY + " as CANTIDAD, sd." + DETAIL_UNIT + " as UNIDAD," +
                    "m."+MeasureUnitsController.CODE+" AS CODEMEDIDA,  m." + MeasureUnitsController.DESCRIPTION + " AS MEDIDA,  ifnull(pc."+ProductsControlController.BLOQUED+", '0') as BLOQUED, " +
                    "sd."+DETAIL_DATE+" as DATE, sd."+DETAIL_MDATE+" as MDATE "+
                    "FROM " + TABLE_NAME_DETAIL + " sd " +
                    "LEFT JOIN " + ProductsController.TABLE_NAME + " p on sd." + DETAIL_CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                    "LEFT JOIN " + MeasureUnitsController.TABLE_NAME + " m on m." + MeasureUnitsController.CODE + " = sd." + DETAIL_CODEUND + " " +
                    "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+ " = p." + ProductsController.CODE + " " +
                    "WHERE " + where+" " +
                    "GROUP BY p."+ProductsController.CODE+", m."+MeasureUnitsController.CODE ;
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {

                OrderDetailModel om = new OrderDetailModel(
                        c.getString(c.getColumnIndex("CODEPRODUCT")),
                        c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("CODESALES")),
                        c.getString(c.getColumnIndex("PRODUCTO")),
                        c.getString(c.getColumnIndex("CANTIDAD")),
                        c.getString(c.getColumnIndex("CODEMEDIDA")),
                        c.getString(c.getColumnIndex("MEDIDA")),
                        c.getString(c.getColumnIndex("BLOQUED")),
                        ProductsMeasureController.getInstance(context).getProductsMeasureKVByCodeProduct(c.getString(c.getColumnIndex("CODEPRODUCT"))));
                objects.add(om);

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return objects;
    }*/

/*
    public ArrayList<SelectableOrderRowModel> getSelectableOrderModelsByAreaDetail(String areaDetail){
        ArrayList<SelectableOrderRowModel> data = new ArrayList<>();
        String sql = "Select DISTINCT s." + CODE + " as CODE, ifnull(ifnull(s." + MDATE + ", s." + DATE + "), 'NONE') as FECHA, a." + AreasController.CODE + " as ACODE,  " +
                "a." + AreasController.DESCRIPTION + " as ADESCRIPTION,  ad." + AreasDetailController.CODE + " as ADCODE, ad." + AreasDetailController.DESCRIPTION + " as ADDESCRIPTION, s." + STATUS + " as STATUS," +
                "s."+TOTAL+" as TOTAL,u."+UsersController.CODE+" as CODEUSER,  u."+UsersController.USERNAME+" as USERNAME " +
                "FROM " + TABLE_NAME + " s  " +
                "INNER JOIN " + AreasDetailController.TABLE_NAME + " ad on ad." + AreasDetailController.CODE + " = s." + CODEAREADETAIL + " " +
                "INNER JOIN " + AreasController.TABLE_NAME + " a on a." + AreasController.CODE + " = ad." + AreasDetailController.CODEAREA + " " +
                "INNER JOIN "+UsersController.TABLE_NAME+" u on u."+UsersController.CODE+" = s."+CODEUSER+" "+
                "WHERE ad."+AreasDetailController.CODE+" = ? AND s."+STATUS+" IN(?, ?, ?) "+
                "ORDER BY a." + AreasController.DESCRIPTION + ", ad."+AreasDetailController.ORDER;
        Cursor c = sqlite.getReadableDatabase().rawQuery(sql, new String[]{areaDetail,CODES.CODE_ORDER_STATUS_OPEN+"", CODES.CODE_ORDER_STATUS_READY+"", CODES.CODE_ORDER_STATUS_DELIVERED+""});
        while (c.moveToNext()) {

            String code = c.getString(c.getColumnIndex("CODE"));
            String fecha = c.getString(c.getColumnIndex("FECHA"));
            String areaCode = c.getString(c.getColumnIndex("ACODE"));
            String areaDescription = c.getString(c.getColumnIndex("ADESCRIPTION"));
            String areaDetailCode = c.getString(c.getColumnIndex("ADCODE"));
            String areaDetailDescription = c.getString(c.getColumnIndex("ADDESCRIPTION"));
            String total = c.getString(c.getColumnIndex("TOTAL"));
            String codeUser = c.getString(c.getColumnIndex("CODEUSER"));
            String userName = c.getString(c.getColumnIndex("USERNAME"));
            int s = c.getInt(c.getColumnIndex("STATUS"));
            String status = "NONE";
            switch (s) {
                case CODES.CODE_ORDER_STATUS_OPEN:
                    status = "Abierta";
                    break;
                case CODES.CODE_ORDER_STATUS_CLOSED:
                    status = "Cerrada";
                    break;
                case CODES.CODE_ORDER_STATUS_CANCELED:
                    status = "Cancelada";
                    break;
                case CODES.CODE_ORDER_STATUS_READY:
                    status = "Lista";
                    break;
                case CODES.CODE_ORDER_STATUS_DELIVERED:
                    status = "Entregada";
                    break;

            }

            SelectableOrderRowModel om = new SelectableOrderRowModel(code, fecha, codeUser,userName,areaCode, areaDescription, areaDetailCode, areaDetailDescription, status,total,false);
            data.add(om);
        }c.close();
        return data;
    }*/


    public void sendToFireBase(Sales s){
        sendToFireBase(s, null);
    }
    public void sendToFireBase(Sales s,  ArrayList<SalesDetails> salesDetails){
        ArrayList<Sales> sales = new ArrayList<>(); sales.add(s);
        sendToFireBase(sales, salesDetails);
    }

    public void sendToFireBase(ArrayList<Sales> sales,  ArrayList<SalesDetails> salesDetails){
        try {
            WriteBatch lote = db.batch();
            for(Sales s: sales) {
                if (s.getMDATE() == null) {
                    lote.set(getReferenceFireStore().document(s.getCODE()), s.toMap());
                } else {
                    lote.update(getReferenceFireStore().document(s.getCODE()), s.toMap());
                }
            }

            if (salesDetails != null){
                for (SalesDetails sd : salesDetails) {//Insertando o actualizando el nuevo detalle
                    if (sd.getMDATE() == null) {
                        lote.set(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
                    } else {
                        lote.update(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
                    }

                }
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





    public void getAllDataFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Sales object = dc.getDocument().toObject(Sales.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
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

    public void getAllDataDetailFromFireBase(OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceDetailFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            SalesDetails object = dc.getDocument().toObject(SalesDetails.class);
                            String where = CODE+" = ?";
                            String[]args = new String[]{object.getCODE()};
                            delete(where, args);
                            insert_Detail(object);
                        }
                    }
                }
            }).addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    public void massiveDelete(ArrayList<Sales> sales){
        try {
            WriteBatch lote = db.batch();
            for(Sales s: sales){
                for(SalesDetails sd: getSalesDetailsByCodeSales(s.getCODE())){
                    lote.delete(getReferenceDetailFireStore().document(sd.getCODE()));
                }
                lote.delete(getReferenceFireStore().document(s.getCODE()));
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

    public void editDetailToFireBase(final Sales s, final ArrayList<SalesDetails> newsalesDetails){
        WriteBatch lote = db.batch();
        lote.update(getReferenceFireStore().document(s.getCODE()), s.toMap());
        ArrayList<SalesDetails> sdetails = getSalesDetailsByCodeSales(s.getCODE());
        for(SalesDetails sdDelete: getdifference(sdetails, newsalesDetails)){
            lote.delete(getReferenceDetailFireStore().document(sdDelete.getCODE()));
        }

        for(SalesDetails sd: newsalesDetails){//Insertando o actualizando el nuevo detalle
            if(sd.getMDATE() == null){
                lote.set(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
            }else{
                lote.update(getReferenceDetailFireStore().document(sd.getCODE()), sd.toMap());
            }

        }

        lote.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }



    public ArrayList<SalesDetails> getdifference(ArrayList<SalesDetails> sdOriginal, ArrayList<SalesDetails> newsalesDetails){
        ArrayList<SalesDetails>toDelete = new ArrayList<>();
        for(SalesDetails del: sdOriginal){
            boolean delete = true;
            for(SalesDetails update: newsalesDetails){
                if(del.getCODE().equals(update.getCODE())){
                    delete = false;
                    break;
                }
            }

            if(delete)
                toDelete.add(del);


        }

        return toDelete;
    }


   /* public void fillSpinnerOrderStatus(Spinner spn, boolean addTodos){
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("-1", "TODOS");
            data.add(obj);
        }
        data.add(new KV(CODES.CODE_ORDER_STATUS_OPEN+"","Abiertas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_CANCELED+"","Canceladas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_CLOSED+"","Cerradas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_DELIVERED+"","Entregadas") );
        data.add(new KV(CODES.CODE_ORDER_STATUS_READY+"","Lista") );


        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }*/



/*
    public void fillSpnVista(Spinner spn){
        ArrayList<KV> spnList = new ArrayList<>();
        spnList.add(new KV("0", "Detalle")); spnList.add(new KV("1", "General"));
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }*/
/*
    public ArrayList<OrderReceiptModel> getOrderReceipt(String codeAreaDetail){

        String status = CODES.CODE_ORDER_STATUS_DELIVERED+"";
        ArrayList<OrderReceiptModel> result = new ArrayList<>();
        String sql = "SELECT a."+AreasController.CODE+" AS CODEAREA, a."+AreasController.DESCRIPTION+" AS  DESCRIPTIONAREA, " +
                "ad."+AreasDetailController.CODE+" AS CODEMESA, ad."+AreasDetailController.DESCRIPTION+" AS DESCRIPTIONMESA, " +
                "sum(s."+SalesController.TOTAL+") AS SALESTOTAL " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+AreasDetailController.TABLE_NAME+" ad ON s."+CODEAREADETAIL+" = ad."+AreasDetailController.CODE+" " +
                "INNER JOIN "+AreasController.TABLE_NAME+" a ON ad."+AreasDetailController.CODEAREA+" = a."+AreasController.CODE+" " +
                "WHERE s."+SalesController.STATUS+" = ? AND s."+SalesController.CODEAREADETAIL+" = ? " +
                "GROUP BY a."+AreasController.CODE+", ad."+AreasDetailController.CODE;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{status, codeAreaDetail});
        while (c.moveToNext()){
            result.add(new OrderReceiptModel(Funciones.generateCode(),
                    c.getString(c.getColumnIndex("CODEAREA")),
                    c.getString(c.getColumnIndex("DESCRIPTIONAREA")),
                    c.getString(c.getColumnIndex("CODEMESA")),
                    c.getString(c.getColumnIndex("DESCRIPTIONMESA")),
                    c.getDouble(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;

    }

    public ArrayList<ReceiptResumeModel> getOrderReceiptResume(String codeAreaDetail){
        String status = CODES.CODE_ORDER_STATUS_DELIVERED+"";
        ArrayList<ReceiptResumeModel> result = new ArrayList<>();
        String sql = "SELECT sd."+SalesController.DETAIL_CODEPRODUCT+" AS CODEPRODUCT, p."+ProductsController.DESCRIPTION+" AS  PRODUCTDESCRIPTION, " +
                "sd."+SalesController.DETAIL_CODEUND+" AS CODEMEASURE, mu."+MeasureUnitsController.DESCRIPTION+" AS MEASUREDESCRIPTION, "+
                "SUM(sd."+SalesController.DETAIL_QUANTITY+") AS QUANTITY, SUM(sd."+SalesController.DETAIL_PRICE+" * "+SalesController.DETAIL_QUANTITY+" ) AS SALESTOTAL "+
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.CODE+" = sd."+SalesController.DETAIL_CODESALES+" "+
                "INNER JOIN "+ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+SalesController.DETAIL_CODEPRODUCT+" "+
                "LEFT JOIN "+MeasureUnitsController.TABLE_NAME+" mu on mu."+MeasureUnitsController.CODE+" = sd."+SalesController.DETAIL_CODEUND+" "+
                "INNER JOIN "+AreasDetailController.TABLE_NAME+" ad ON s."+CODEAREADETAIL+" = ad."+AreasDetailController.CODE+" " +
                "WHERE s."+SalesController.STATUS+" = ? AND s."+SalesController.CODEAREADETAIL+" = ? " +
                "GROUP BY sd."+SalesController.DETAIL_CODEPRODUCT+",sd."+SalesController.DETAIL_CODEUND+" " +
                "ORDER BY p."+ProductsController.DESCRIPTION+",  mu."+MeasureUnitsController.DESCRIPTION;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{status, codeAreaDetail});
        while (c.moveToNext()){
            result.add(new ReceiptResumeModel( codeAreaDetail,
                    c.getString(c.getColumnIndex("CODEPRODUCT")),
                    c.getString(c.getColumnIndex("PRODUCTDESCRIPTION")),
                    c.getString(c.getColumnIndex("CODEMEASURE")),
                    c.getString(c.getColumnIndex("MEASUREDESCRIPTION")),
                    c.getString(c.getColumnIndex("QUANTITY")),
                    c.getString(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;
    }

    public ArrayList<Sales> getDeliveredOrdersByCodeAreadetail(String codeAreaDetail){
        ArrayList<Sales> deliveredOrders = new ArrayList<>();
        String where = SalesController.STATUS+" = ? AND "+SalesController.CODEAREADETAIL+" = ? ";
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns, where,new String[]{CODES.CODE_ORDER_STATUS_DELIVERED+"", codeAreaDetail},null,null,null);
        while(c.moveToNext()){
            deliveredOrders.add(new Sales(c));
        }
        return deliveredOrders;
    }


    public Receipts getReceiptByCodeAreadetail(String codeAreaDetail){
        Receipts receipt = null;
        String where = "s."+SalesController.STATUS+" = ? AND s."+SalesController.CODEAREADETAIL+" = ? ";
        String sql = "SELECT SUM(sd."+SalesController.DETAIL_PRICE+" * sd."+SalesController.DETAIL_QUANTITY+" ) AS TOTAL " +
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.CODE+" = sd."+SalesController.DETAIL_CODESALES+" "+
                " WHERE "+where;
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,new String[]{CODES.CODE_ORDER_STATUS_DELIVERED+"", codeAreaDetail});
        if(c.moveToFirst()){
            double total = c.getDouble(0);
            receipt = new Receipts(Funciones.generateCode(),Funciones.getCodeuserLogged(context),codeAreaDetail,null,null,total,0.0,0.0,total);
        }c.close();
        return receipt;
    }*/

    public void closeOrders(Receipts receipt, ArrayList<Sales> deliveredSales){
        if(deliveredSales != null && deliveredSales.size() > 0) {

            for(Sales sales : deliveredSales){
                sales.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);
                sales.setCODERECEIPT(receipt.getCode());
                sales.setMDATE(null);//actualizar fecha de ultima actualizacion.

            }


            ///////////////////////////////////////////////////////////////////
            ///////////   ENVIANDO AL HISTORICO     ///////////////////////////

            SalesHistoryController.getInstance(context).sendToHistory(deliveredSales);
            ///////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////      ELIMINANDO DE LA TABLA SALES Y SALES_DETAIL EN FIREBASE   ////////
            massiveDelete(deliveredSales);
            //////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////
            //////////  ELIMINANDOLA EN EL MOVIL   ///////////////////////////
            deleteHeadDetail(deliveredSales);//esto es porque la lista se actualizara antes de que el server retorne la actualizacion.
            //////////////////////////////////////////////////////////////////



        }
    }
/*
    public static String printReceipt(Context c, String codeAreaDetail, ArrayList<ReceiptResumeModel> list){
        Print p = new Print(c,Print.PULGADAS.PULGADAS_2);
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        p.addImage(R.drawable.gordi);
        p.drawText("Restaurant Los Gorditos");
        p.drawText("C: La gloria #14 El almirante");
        p.drawText("Tel:809-236-1503");
        p.drawText("");
        p.drawLine();
        p.drawText("Detalle");
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);

        double total = 0.0;
        for(ReceiptResumeModel r :list){
            total+=Double.parseDouble(r.getTotal());
            p.drawText(r.getProductDescription());
            p.drawText(Funciones.reservarCaracteres("Cant:"+r.getQuantity(),9)+Funciones.reservarCaracteres(r.getMeasureDescription(),10)+Funciones.reservarCaracteresAlinearDerecha(" $"+Funciones.formatDecimal(r.getTotal()),13));
        }
        p.drawLine();

        p.addAlign(Print.PRINTER_ALIGN.ALIGN_RIGHT);
        p.drawText("Total:"+Funciones.formatDecimal(total), Print.TEXT_ALIGN.RIGHT);


        p.printText("02:3D:D3:DB:D5:06");
        return null;
    }*/

  public void searchProductInSalesDetail(String codeProduct, OnSuccessListener success, OnFailureListener failure){
      getReferenceDetailFireStore().
              whereEqualTo(DETAIL_CODEPRODUCT, codeProduct).get().
              addOnSuccessListener(success).
              addOnFailureListener(failure);
  }

  public boolean orderContainsBlockedProduct(Sales s){
      boolean result;
      String sql = "SELECT sd."+CODE+" " +
              "FROM "+TABLE_NAME_DETAIL +" sd "+
              "INNER JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+" = sd."+DETAIL_CODEPRODUCT+" " +
              "AND pc."+ProductsControlController.BLOQUED+" = '1' " +
              "WHERE sd."+DETAIL_CODESALES+" = ? ";
      Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{s.getCODE()});
      result = c.moveToFirst();
      c.close();
      return result;
  }

    public ArrayList<KV> getBloquedProductsInOrder(Sales s){
       ArrayList<KV> result = new ArrayList<>();
        String sql = "SELECT p."+ProductsController.CODE+", p."+ProductsController.DESCRIPTION+" " +
                "FROM "+TABLE_NAME_DETAIL +" sd " +
                "INNER JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+" = sd."+DETAIL_CODEPRODUCT+" " +
                "AND pc."+ProductsControlController.BLOQUED+" = '1' " +
                "INNER JOIN " +ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = pc."+ProductsControlController.CODEPRODUCT+" "+
                "WHERE sd."+DETAIL_CODESALES+" = ? " +
                "GROUP BY p."+ProductsController.CODE+", p."+ProductsController.DESCRIPTION+" " +
                "ORDER BY p."+ProductsController.DESCRIPTION;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{s.getCODE()});
        while(c.moveToNext()){
            result.add(new KV(c.getString(0), c.getString(1)));
        }c.close();

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


    public void searchDetailChanges(OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        Date mdate = DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceDetailFireStore().
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceDetailFireStore().
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }

    }



    public void consumeQuerySnapshot(QuerySnapshot querySnapshot){
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Sales obj = doc.toObject(Sales.class);
                if(update(obj) <=0){
                    insert(obj);
                }
            }
        }

    }

    public void consumeQuerySnapshotDetail(QuerySnapshot querySnapshot){
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                SalesDetails obj = doc.toObject(SalesDetails.class);
                if(update_Detail(obj) <=0){
                    insert_Detail(obj);
                }
            }
        }

    }

    public ArrayList<SalesDetailModel> getSaleDetailModels(String codeReceipt){
        ArrayList<SalesDetailModel> result = new ArrayList<>();
        String sql = "SELECT sd."+SalesController.DETAIL_CODEPRODUCT+" AS CODEPRODUCT, p."+ProductsController.DESCRIPTION+" AS  PRODUCTDESCRIPTION, " +
                "sd."+SalesController.DETAIL_CODEUND+" AS CODEMEASURE, mu."+MeasureUnitsController.DESCRIPTION+" AS MEASUREDESCRIPTION, "+
                "SUM(sd."+SalesController.DETAIL_QUANTITY+") AS QUANTITY, SUM(sd."+SalesController.DETAIL_PRICE+" * "+SalesController.DETAIL_QUANTITY+" ) AS SALESTOTAL "+
                "FROM "+SalesController.TABLE_NAME+" s " +
                "INNER JOIN "+SalesController.TABLE_NAME_DETAIL+" sd on s."+SalesController.CODE+" = sd."+SalesController.DETAIL_CODESALES+" "+
                "INNER JOIN "+ProductsController.TABLE_NAME+" p on p."+ProductsController.CODE+" = sd."+SalesController.DETAIL_CODEPRODUCT+" "+
                "LEFT JOIN "+MeasureUnitsController.TABLE_NAME+" mu on mu."+MeasureUnitsController.CODE+" = sd."+SalesController.DETAIL_CODEUND+" "+
                "WHERE s."+CODERECEIPT+" = ? " +
                "GROUP BY sd."+SalesController.DETAIL_CODEPRODUCT+",sd."+SalesController.DETAIL_CODEUND+" " +
                "ORDER BY p."+ProductsController.DESCRIPTION+",  mu."+MeasureUnitsController.DESCRIPTION;

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{codeReceipt});
        while (c.moveToNext()){
            result.add(new SalesDetailModel(
                    c.getString(c.getColumnIndex("CODEPRODUCT")),
                    c.getString(c.getColumnIndex("PRODUCTDESCRIPTION")),
                    c.getString(c.getColumnIndex("CODEMEASURE")),
                    c.getString(c.getColumnIndex("MEASUREDESCRIPTION")),
                    c.getString(c.getColumnIndex("QUANTITY")),
                    c.getString(c.getColumnIndex("SALESTOTAL"))));
        }c.close();

        return result;
    }
}
