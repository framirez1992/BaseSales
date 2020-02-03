package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;

import com.far.basesales.CloudFireStoreObjects.Counter;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

public class CounterController {

    public static final String TABLE_NAME ="COUNTERS";
    public static  String CODE = "code",TYPE = "type",  CODEUSER = "codeuser" ,
            COUNT = "count",DATA = "data", DATA2="data2", DATA3 = "data3", DATE = "date", MDATE = "mdate";

    /*String[] columns = new String[]{CODE,TYPE, CODEUSER, COUNT,DATA, DATA2, DATA3,  DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+TYPE+" TEXT,  "+CODEUSER+" TEXT, "+COUNT+" NUMERIC, "+DATA+" TEXT, "+DATA2+" TEXT, "+DATA3+" TEXT,  "+DATE+" TEXT, "+MDATE+" TEXT)";
    */
    Context context;
    FirebaseFirestore db;

    private static CounterController instance;
    private CounterController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static CounterController getInstance(Context context){
        if(instance == null){
            instance = new CounterController(context);
        }
        return instance;
    }
    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersCounters);
        return reference;
    }


    public void sendToFireBase(Counter counter, OnFailureListener failure){
        WriteBatch lote = db.batch();
        lote.set(getReferenceFireStore().document(counter.getCode()), counter.toMap());
        lote.commit()
                .addOnFailureListener(failure);
    }

    public void deleteFromFireBase(Counter counter, OnFailureListener failure){
        WriteBatch lote = db.batch();
        lote.delete(getReferenceFireStore().document(counter.getCode()));
        lote.commit()
                .addOnFailureListener(failure);
    }

    public long insert(Counter c){//CODE,TYPE, CODEUSER, COUNT,DATA, DATA2, DATA3,  DATE, MDATE
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCode() );
        cv.put(TYPE,c.getType() );
        cv.put(CODEUSER,c.getCodeuser());
        cv.put(COUNT,c.getCount());
        cv.put(DATA, c.getData());
        cv.put(DATA2, c.getData2());
        cv.put(DATA3, c.getData3());
        cv.put(DATE, Funciones.getFormatedDate(c.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(c.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Counter c){
        return update(c,CODE+" = ?", new String[]{c.getCode()});
    }
    public long update(Counter c, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCode() );
        cv.put(TYPE,c.getType() );
        cv.put(CODEUSER,c.getCodeuser());
        cv.put(COUNT,c.getCount());
        cv.put(DATA, c.getData());
        cv.put(DATA2, c.getData2());
        cv.put(DATA3, c.getData3());
        cv.put(DATE, Funciones.getFormatedDate(c.getDate()));
        cv.put(MDATE, Funciones.getFormatedDate(c.getMdate()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(Counter c){
        return delete(CODE+" = ?", new String[]{c.getCode()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersCounters).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void searchCounterFromFireBase(String codeUser, String type, OnSuccessListener<QuerySnapshot> success, OnFailureListener failure){

        getReferenceFireStore().
                whereEqualTo(CODEUSER, codeUser).
                whereEqualTo(TYPE, type).
                get().addOnSuccessListener(success).
                addOnFailureListener(failure);

    }

}
