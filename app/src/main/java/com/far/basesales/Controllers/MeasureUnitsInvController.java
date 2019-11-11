package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.MeasureUnits;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Generic.KV;
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

import java.util.ArrayList;
import java.util.Date;


public class MeasureUnitsInvController {
    public static final  String TABLE_NAME = "MEASUREUNITSINV";
    public static String CODE = "code", DESCRIPTION = "description", DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE, DESCRIPTION, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static MeasureUnitsInvController instance;
    private MeasureUnitsInvController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }

    public static MeasureUnitsInvController getInstance(Context context){
        if(instance == null){
            instance = new MeasureUnitsInvController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersMeasureUnitsInv);
        return reference;
    }

    public long insert(MeasureUnits mu){
        ContentValues cv = new ContentValues();
        cv.put(CODE,mu.getCODE());
        cv.put(DESCRIPTION,mu.getDESCRIPTION());
        cv.put(DATE, Funciones.getFormatedDate(mu.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(mu.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(MeasureUnits d, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,d.getCODE() );
        cv.put(DESCRIPTION,d.getDESCRIPTION());
        cv.put(MDATE, Funciones.getFormatedDate(d.getMDATE()));

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
            Task<QuerySnapshot> measureUnits = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersMeasureUnitsInv).get();
            measureUnits.addOnSuccessListener(onSuccessListener);
            measureUnits.addOnFailureListener(onFailureListener);
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
                            MeasureUnits object = dc.getDocument().toObject(MeasureUnits.class);
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

    public void sendToFireBase(MeasureUnits mu){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(mu.getCODE()), mu.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void deleteFromFireBase(MeasureUnits mu){
        try {
            getReferenceFireStore().document(mu.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<MeasureUnits> getMeasureUnits(String where, String[]args, String orderBy){
        ArrayList<MeasureUnits> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new MeasureUnits(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public MeasureUnits getMeasureUnitByCode(String code){
        String where = CODE+" = ?";
        ArrayList<MeasureUnits> pts = getMeasureUnits(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public ArrayList<SimpleRowModel> getMeasureUnitsSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, campoOrder);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public void fillSpinner(Spinner spn){
        try {
            ArrayList<KV> result = new ArrayList<>();
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, DESCRIPTION);
            while(c.moveToNext()){
                result.add(new KV(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION))));
            }
            c.close();

            spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,result));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<KV> getMeasureUnitsKV(){
        ArrayList<KV> result = new ArrayList<>();
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, DESCRIPTION);
            while(c.moveToNext()){
                result.add(new KV(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION))));
            }
            c.close();


        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */
    public ArrayList<SimpleSeleccionRowModel> getUnitMeasuresSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+CODE+" as CODE, u."+DESCRIPTION+" AS DESCRIPTION,  u."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("CODE"));
                String name = c.getString(c.getColumnIndex("DESCRIPTION"));
                result.add(new SimpleSeleccionRowModel(code,name ,false));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public ArrayList<DocumentReference> getReferences(String field, String value){
        ArrayList<DocumentReference> references = new ArrayList<>();
        ArrayList<MeasureUnits> objs = getMeasureUnits(field+" = ? ", new String[]{value}, null);
        if(objs != null){
            for(MeasureUnits c: objs){
                references.add(getReferenceFireStore().document(c.getCODE()));
            }
        }
        return references;
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
                MeasureUnits obj = doc.toObject(MeasureUnits.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }


}
