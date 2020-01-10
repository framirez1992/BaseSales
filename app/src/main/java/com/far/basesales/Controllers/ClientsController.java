package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Company;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ClientsController {
    public static final String TABLE_NAME ="CLIENTS";
    public static  String CODE = "code",DOCUMENT = "document",  NAME = "name" ,
            PHONE = "phone",DATA = "data", DATA2="data2", DATA3 = "data3", DATE = "date", MDATE = "mdate";
    String[] columns = new String[]{CODE,DOCUMENT, NAME, PHONE,DATA, DATA2, DATA3,  DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT,"+DOCUMENT+" TEXT,  "+NAME+" TEXT, "+PHONE+" TEXT, "+DATA+" TEXT, "+DATA2+" TEXT, "+DATA3+" TEXT,  "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;

    private static ClientsController instance;
    private ClientsController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ClientsController getInstance(Context context){
        if(instance == null){
            instance = new ClientsController(context);
        }
        return instance;
    }
    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersClients);
        return reference;
    }


    public void sendToFireBase(Clients clients, OnFailureListener failure){
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(clients.getCODE()), clients.toMap());
            lote.commit()
                    .addOnFailureListener(failure);
    }

    public void deleteFromFireBase(Clients clients, OnFailureListener failure){
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(clients.getCODE()));
            lote.commit()
                    .addOnFailureListener(failure);
    }

    public long insert(Clients c){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(DOCUMENT,c.getDOCUMENT() );
        cv.put(NAME,c.getNAME());
        cv.put(PHONE,c.getPHONE());
        cv.put(DATA, c.getDATA());
        cv.put(DATA2, c.getDATA2());
        cv.put(DATA3, c.getDATA3());
        cv.put(DATE, Funciones.getFormatedDate((Date) c.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate((Date) c.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Clients c){
        return update(c,CODE+" = ?", new String[]{c.getCODE()});
    }
    public long update(Clients c, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(DOCUMENT,c.getDOCUMENT() );
        cv.put(NAME,c.getNAME());
        cv.put(PHONE,c.getPHONE() );
        cv.put(DATA, c.getDATA());
        cv.put(DATA2, c.getDATA2());
        cv.put(DATA3, c.getDATA3());
        cv.put(MDATE, Funciones.getFormatedDate(c.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(Clients c){
        return delete(CODE+" = ?", new String[]{c.getCODE()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersClients).get();
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
                            Clients object = dc.getDocument().toObject(Clients.class);
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

    public ArrayList<Clients> getClients(String where, String[]args, String orderBy){
        ArrayList<Clients> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Clients(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public Clients getClientByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Clients> pts = getClients(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }


    public ArrayList<ClientRowModel> getClientSRM(String where, String[] args, String campoOrder){
        ArrayList<ClientRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = NAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+CODE+" as CODE,u."+DOCUMENT+" as DOCUMENT, u."+NAME+" AS NAME, u."+PHONE+" AS PHONE, " +
                    "u."+DATA+" as DATA, u."+DATA2+" as DATA2, u."+DATA3+" as DATA3,  u."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new ClientRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DOCUMENT")),
                        c.getString(c.getColumnIndex("NAME")),
                        c.getString(c.getColumnIndex("PHONE")) ,
                        c.getString(c.getColumnIndex("DATA")) ,
                        c.getString(c.getColumnIndex("DATA2")) ,
                        c.getString(c.getColumnIndex("DATA3")) ,
                        c.getString(c.getColumnIndex("MDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public ArrayList<Clients> getBirthDayClients(Calendar c){
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        if(month.length()==1){
            month="0"+month;
        }

        if(day.length()==1){
            day="0"+day;
        }
       return getClients(DATA+" like ?", new String[]{(day+"/"+month+"%")}, null);
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


    public void searchClientFromFireBase(String code, OnSuccessListener<QuerySnapshot> success,  OnFailureListener failure){
            getReferenceFireStore().
                    whereEqualTo(CODE, code).
                    get().
                    addOnSuccessListener(success).
                    addOnFailureListener(failure);

    }

    public void consumeQuerySnapshot(boolean clear, QuerySnapshot querySnapshot){
        if(clear){
            delete(null, null);
        }
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Clients obj = doc.toObject(Clients.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }

}
