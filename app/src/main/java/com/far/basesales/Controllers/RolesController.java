package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Roles;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class RolesController {
    public static final String TABLE_NAME ="GENERAL_ROLES";
    public static String CODE = "code", DESCRIPTION = "description" ,
            DATE = "date", MDATE = "mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT,"+DATE+" TEXT, "+MDATE+" TEXT)";
    public static String[] columns = new String[]{CODE, DESCRIPTION, DATE, MDATE};
    Context context;
    FirebaseFirestore db;
    private static RolesController instance;
    private RolesController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }
    public static RolesController getInstance(Context c){
        if(instance == null){
            instance = new RolesController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalRoles);
        return reference;
    }

    public long insert(Roles r){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCODE() );
        cv.put(DESCRIPTION,r.getDESCRIPTION());
        cv.put(DATE, Funciones.getFormatedDate(r.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(r.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Roles r, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,r.getCODE() );
        cv.put(DESCRIPTION,r.getDESCRIPTION());
        cv.put(MDATE, Funciones.getFormatedDate(r.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Roles> getRoles(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<Roles> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new Roles(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public void fillGeneralRoles(Spinner spn){
        ArrayList<Roles> result = getRoles(null, null, null);
        ArrayList<KV> spnList = new ArrayList<>();

        for(Roles r : result){
            spnList.add(new KV(r.getCODE(), r.getDESCRIPTION()));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public void getDataFromFireBase(OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalRoles).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
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
                            Roles object = dc.getDocument().toObject(Roles.class);
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
                Roles obj = doc.toObject(Roles.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }

            }
        }

    }
}
