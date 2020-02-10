package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.DataBase.DB;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class DayController{
    public static final String TABLE_NAME ="DAY";
    public static  String CODE = "code",CODEUSER = "codeuser", DATESTART = "datestart",DATEEND="dateend", STATUS = "status",  CASHSALESCOUNT = "cashsalescount",CASHSALESAMOUNT = "cashsalesamount",
            CREDITSALESCOUNT = "creditsalescount", CREDITSALESAMOUNT="creditsalesamount", SALESCOUNT = "salescount", SALESAMOUNT = "salesamount",  DISCOUNTAMOUNT = "discountamount",
            CASHPAIDCOUNT = "cashpaidcount", CASHPAIDAMOUNT = "cashpaidamount",CREDITPAIDCOUNT = "creditpaidcount", CREDITPAIDAMOUNT = "creditpaidamount",
            ANULATEDSALESCOUNT = "anulatedsalescount", ANULATEDSALESAMOUNT = "anulatedsalesamount", ANULATEDCASHPAYMENTCOUNT = "anulatedcashpaymentcount",ANULATEDCASHPAYMENTAMOUNT = "anulatedcashpaymentamount",
            ANULATEDCREDITPAYMENTCOUNT = "anulatedcreditpaymentcount",ANULATEDCREDITPAYMENTAMOUNT="anulatedcreditpaymentamount",LASTRECEIPTNUMBER = "lastreceiptnumber", DATE = "date", MDATE = "mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+CODEUSER+" TEXT,"+DATESTART+" TEXT, "+DATEEND+" TEXT,  "+STATUS+" TEXT, "+CASHSALESCOUNT+" NUMERIC,"+CASHSALESAMOUNT+" NUMERIC,"+CREDITSALESCOUNT+" NUMERIC,"+
            CREDITSALESAMOUNT+" NUMERIC, "+CASHPAIDCOUNT+" NUMERIC,  "+CASHPAIDAMOUNT+" NUMERIC, "+CREDITPAIDCOUNT+" NUMERIC,  "+CREDITPAIDAMOUNT+" NUMERIC, " +
            DISCOUNTAMOUNT+", NUMERIC, "+SALESCOUNT+" NUMERIC, "+SALESAMOUNT+" NUMERIC, "+ANULATEDSALESCOUNT+" NUMERIC, "+ANULATEDSALESAMOUNT+" NUMERIC, "+ANULATEDCASHPAYMENTCOUNT+" NUMERIC, " +
            ANULATEDCASHPAYMENTAMOUNT+" NUMERIC, "+ANULATEDCREDITPAYMENTCOUNT+" NUMERIC, "+ANULATEDCREDITPAYMENTAMOUNT+" NUMERIC," +
            LASTRECEIPTNUMBER+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

    public static String[] columns = new String[]{CODE,CODEUSER,DATESTART, DATEEND, STATUS, CASHSALESCOUNT,CASHSALESAMOUNT,CREDITSALESCOUNT,CREDITSALESAMOUNT,
            CASHPAIDCOUNT, CASHPAIDAMOUNT,CREDITPAIDCOUNT, CREDITPAIDAMOUNT,DISCOUNTAMOUNT, SALESCOUNT, SALESAMOUNT,
            ANULATEDSALESCOUNT, ANULATEDSALESAMOUNT, ANULATEDCASHPAYMENTCOUNT,ANULATEDCASHPAYMENTAMOUNT, ANULATEDCREDITPAYMENTCOUNT,ANULATEDCREDITPAYMENTAMOUNT,
            LASTRECEIPTNUMBER, DATE, MDATE};
    Context context;
    FirebaseFirestore db;
    private static DayController instance;
    private DayController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }
    public static DayController getInstance(Context c){
        if(instance == null){
            instance = new DayController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersDay);
        return reference;
    }


    public long insert(Day r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(DATESTART,  Funciones.getFormatedDate(r.getDatestart()));
        cv.put(DATEEND,Funciones.getFormatedDate(r.getDateend()));
        cv.put(STATUS, r.getStatus());
        cv.put(CASHSALESCOUNT,r.getCashsalescount());
        cv.put(CASHSALESAMOUNT, r.getCashsalesamount());
        cv.put(CREDITSALESCOUNT, r.getCreditsalescount());
        cv.put(CREDITSALESAMOUNT, r.getCreditsalesamount());
        cv.put(DISCOUNTAMOUNT, r.getDiscountamount());
        cv.put(SALESCOUNT, r.getSalescount());
        cv.put(SALESAMOUNT, r.getSalesamount());
        cv.put(CASHPAIDCOUNT, r.getCashpaidcount());
        cv.put(CASHPAIDAMOUNT, r.getCashpaidamount());
        cv.put(CREDITPAIDCOUNT, r.getCreditpaidcount());
        cv.put(CREDITPAIDAMOUNT, r.getCreditpaidamount());
        cv.put(ANULATEDSALESCOUNT,r.getAnulatedsalescount());
        cv.put(ANULATEDSALESAMOUNT,r.getAnulatedsalesamount());
        cv.put(ANULATEDCASHPAYMENTCOUNT,r.getAnulatedcashpaymentcount());
        cv.put(ANULATEDCASHPAYMENTAMOUNT,r.getAnulatedcashpaymentamount());
        cv.put(ANULATEDCREDITPAYMENTCOUNT,r.getAnulatedcreditpaymentcount());
        cv.put(ANULATEDCREDITPAYMENTAMOUNT,r.getAnulatedcreditpaymentamount());
        cv.put(LASTRECEIPTNUMBER, r.getLastreceiptnumber());
        cv.put(DATE, Funciones.getFormatedDate(r.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Day r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCode() );
        cv.put(CODEUSER, r.getCodeuser());
        cv.put(DATESTART,  Funciones.getFormatedDate(r.getDatestart()));
        cv.put(DATEEND,Funciones.getFormatedDate(r.getDateend()));
        cv.put(STATUS, r.getStatus());
        cv.put(CASHSALESCOUNT,r.getCashsalescount());
        cv.put(CASHSALESAMOUNT, r.getCashsalesamount());
        cv.put(CREDITSALESCOUNT, r.getCreditsalescount());
        cv.put(CREDITSALESAMOUNT, r.getCreditsalesamount());
        cv.put(DISCOUNTAMOUNT, r.getDiscountamount());
        cv.put(SALESCOUNT, r.getSalescount());
        cv.put(SALESAMOUNT, r.getSalesamount());
        cv.put(CASHPAIDCOUNT, r.getCashpaidcount());
        cv.put(CASHPAIDAMOUNT, r.getCashpaidamount());
        cv.put(CREDITPAIDCOUNT, r.getCreditpaidcount());
        cv.put(CREDITPAIDAMOUNT, r.getCreditpaidamount());
        cv.put(ANULATEDSALESCOUNT,r.getAnulatedsalescount());
        cv.put(ANULATEDSALESAMOUNT,r.getAnulatedsalesamount());
        cv.put(ANULATEDCASHPAYMENTCOUNT,r.getAnulatedcashpaymentcount());
        cv.put(ANULATEDCASHPAYMENTAMOUNT,r.getAnulatedcashpaymentamount());
        cv.put(ANULATEDCREDITPAYMENTCOUNT,r.getAnulatedcreditpaymentcount());
        cv.put(ANULATEDCREDITPAYMENTAMOUNT,r.getAnulatedcreditpaymentamount());
        cv.put(LASTRECEIPTNUMBER, r.getLastreceiptnumber());
        cv.put(DATE, Funciones.getFormatedDate(r.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMdate()));


        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,CODE+"=?", new String[]{r.getCode()});
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Day> getDays(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Day> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DATE;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Day(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<Day> getDays(String where, String[]args, String campoOrderBy){

        ArrayList<Day> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DATE;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Day(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Returna un Objecto Day Totalizado de todos los Day con status 0
     * @return
     */
    public Day getGeneralDay(String year, String month){
        Day d = new Day();
        String date = year+month;
        //, ,, ,,
    String sql = "SELECT SUM("+SALESCOUNT+") as SALESCOUNT, SUM("+SALESAMOUNT+") as SALESAMOUNT,  " +
            "SUM("+CASHPAIDCOUNT+") as CASHPAIDCOUNT, SUM("+CASHPAIDAMOUNT+") as CASHPAIDAMOUNT," +
            "SUM("+CREDITPAIDCOUNT+") as CREDITPAIDCOUNT, SUM("+CREDITPAIDAMOUNT+") as CREDITPAIDAMOUNT, " +
            "SUM("+DISCOUNTAMOUNT+") as DISCOUNTAMOUNT " +
            "FROM "+TABLE_NAME+" " +
            "WHERE "+STATUS+" = ? AND   SUBSTR("+DATESTART+", 1,6) = ?";
    Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{CODES.CODE_DAY_STATUS_CLOSED, date});
    if(c.moveToFirst()){
        d.setSalescount(c.getInt(c.getColumnIndex("SALESCOUNT")));
        d.setSalesamount(c.getDouble(c.getColumnIndex("SALESAMOUNT")));
        d.setCashpaidcount(c.getInt(c.getColumnIndex("CASHPAIDCOUNT")));
        d.setCashpaidamount(c.getDouble(c.getColumnIndex("CASHPAIDAMOUNT")));
        d.setCreditpaidcount(c.getInt(c.getColumnIndex("CREDITPAIDCOUNT")));
        d.setCreditpaidamount(c.getDouble(c.getColumnIndex("CREDITPAIDAMOUNT")));
        d.setDiscountamount(c.getDouble(c.getColumnIndex("DISCOUNTAMOUNT")));
    }c.close();
    return d;

    }


    public Day getDayByCode(String code){
        ArrayList<Day> array = getDays(new String[]{CODE}, new String[]{code}, null);
        return array.size()>0?array.get(0):null;
    }

    public Day getCurrentOpenDay(){
        ArrayList<Day> array = getDays(new String[]{CODEUSER, STATUS}, new String[]{Funciones.getCodeuserLogged(context), CODES.CODE_DAY_STATUS_OPEN}, null);
        return array.size()>0?array.get(0):null;
    }

    public void getDataFromFireBase(OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> days = getReferenceFireStore().get();
            days.addOnSuccessListener(onSuccessListener);
            days.addOnFailureListener(onFailureListener);
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
                            Day object = dc.getDocument().toObject(Day.class);
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

    public void sendToFireBase(Day day, OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            if (day.getMdate() == null) {
                lote.set(getReferenceFireStore().document(day.getCode()), day.toMap());
            } else {
                lote.update(getReferenceFireStore().document(day.getCode()), day.toMap());
            }

            lote.commit()
                    .addOnFailureListener(failureListener);
    }




    public void searchDayFromFireBase(String code, OnSuccessListener<QuerySnapshot> success, OnFailureListener failure){

            getReferenceFireStore().
                    whereEqualTo(CODE, code).
                    get().addOnSuccessListener(success).
                    addOnFailureListener(failure);

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


    public void consumeQuerySnapshot(boolean clear, QuerySnapshot querySnapshot){
        if(clear){
            delete(null, null);
        }
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Day obj = doc.toObject(Day.class);
                //if(update(obj) <=0){
                    insert(obj);
                //}
            }
        }

    }



    public void searchCurrentDayStartedFromFireBase(OnSuccessListener<QuerySnapshot> success, OnFailureListener failure){
        Query query =getReferenceFireStore().
                whereEqualTo(CODEUSER, Funciones.getCodeuserLogged(context)).whereEqualTo(STATUS, CODES.CODE_DAY_STATUS_OPEN);

        query.get().
                addOnSuccessListener(success).
                addOnFailureListener(failure);

    }


    public void searchDaysRangeFromFireBase(Date dateIni, Date dateEnd, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){
        Query query =getReferenceFireStore().whereEqualTo(STATUS, CODES.CODE_DAY_STATUS_CLOSED).
                whereGreaterThanOrEqualTo(DATESTART, dateIni).whereLessThanOrEqualTo(DATESTART, dateEnd);

        query.get().
                addOnSuccessListener(success).addOnCompleteListener(complete).
                addOnFailureListener(failure);

    }



}
