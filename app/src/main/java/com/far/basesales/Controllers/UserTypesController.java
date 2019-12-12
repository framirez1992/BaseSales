package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.UserTypes;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;


public class UserTypesController {
    public static final String TABLE_NAME = "USERTYPES";
    public static String CODE = "code", DESCRIPTION = "description", ORDEN = "orden", DATE="date", MDATE="mdate";
    public static String[]colums = new String[]{CODE, DESCRIPTION, ORDEN, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT, "+ORDEN+" INTEGER, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static UserTypesController instance;
    private UserTypesController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static UserTypesController getInstance(Context context){
        if(instance == null){
            instance = new UserTypesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUserTypes);
        return reference;
    }
    public long insert(UserTypes ut){
        ContentValues cv = new ContentValues();
        cv.put(CODE,ut.getCODE());
        cv.put(DESCRIPTION,ut.getDESCRIPTION());
        cv.put(ORDEN, ut.getORDEN());
        cv.put(DATE, Funciones.getFormatedDate(ut.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(ut.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(UserTypes ut){
        String where = CODE+" = ?";
        return update(ut, where, new String[]{ut.getCODE()});
    }
    public long update(UserTypes ut, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,ut.getCODE() );
        cv.put(DESCRIPTION,ut.getDESCRIPTION());
        cv.put(ORDEN, ut.getORDEN());
        cv.put(DATE, Funciones.getFormatedDate(ut.getMDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(ut.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(UserTypes ut){
        return delete(CODE+" = ?", new String[]{ut.getCODE()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<UserTypes> getUserTypes(String[] camposFiltros, String[]argumentos, String campoOrderBy){

        ArrayList<UserTypes> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new UserTypes(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(){
        int result = 0;
        ArrayList<UserTypes> pts = getUserTypes(null, null, null);
        if(pts != null){
            result =  pts.size();
        }
        return result;
    }
    public UserTypes getUserTypeByCode(String code){
        ArrayList<UserTypes> pts = getUserTypes(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public int getNextOrden(){
        int result = 0;
        String sql = "SELECT MAX("+ORDEN+" + 1) " +
                "FROM "+TABLE_NAME;
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToFirst()){
                result = c.getInt(0);
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public ArrayList<SimpleRowModel> getUserTypesSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, campoOrder);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public ArrayList<SimpleSeleccionRowModel> getUserTypesSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+TABLE_NAME+"  " +
                     where
                    +" ORDER BY "+campoOrder;
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


    public void sendToFireBase(UserTypes ut){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(ut.getCODE()), ut.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }


    public void deleteFromFireBase(UserTypes pt){
        try {
            getReferenceFireStore().document(pt.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersUserTypes).get();
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
                            UserTypes object = dc.getDocument().toObject(UserTypes.class);
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
    public void fillSpnUserTypes(Spinner spn, boolean addTodos){
        ArrayList<UserTypes> result = getUserTypes(null, null, null);
        ArrayList<KV> spnList = new ArrayList<>();
        if(addTodos){
            spnList.add(new KV("0", "TODOS"));
        }
        for(UserTypes ut : result){
            spnList.add(new KV(ut.getCODE(), ut.getDESCRIPTION()));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    /**
     * retorna true si el codigo tiene dependencias en otras tablas (llave foranea)
     * @param code
     * @return
     */
    public String hasDependencies(String code){
        String msg = "";
        ArrayList<String> tables = new ArrayList<>();
        if(DB.getInstance(context).hasDependencies(UsersController.TABLE_NAME,UsersController.ROLE,code))
            tables.add(UsersController.TABLE_NAME);

        for(String s: tables){
            msg+= s+"\n";
        }
        return msg;
    }


    public void searchChanges(boolean all, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        Date mdate = all?null: DB.getLastMDateSaved(context, TABLE_NAME);
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
                UserTypes obj = doc.toObject(UserTypes.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
