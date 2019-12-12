package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.basesales.CloudFireStoreObjects.Devices;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DevicesController {
    public static final String TABLE_NAME = "DEVICES";
    public static String CODE = "code", ENABLED = "enabled", DATE = "date", MDATE ="mdate";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+ENABLED+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

    FirebaseFirestore db;
    Context context;
    private static DevicesController instance;
    private DevicesController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }

    public static DevicesController getInstance(Context c){
        if(instance == null){
            instance = new DevicesController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(Licenses l){
        return db.collection(Tablas.generalLicencias).document(l.getCODE()).collection(Tablas.generalLicenciasDevices);
    }
    public void getDevices(Licenses l, OnSuccessListener<QuerySnapshot> onSuccessListener){

        Task<QuerySnapshot> devices = getReferenceFireStore(l).get();
        devices.addOnSuccessListener(onSuccessListener);
    }

    public void RegisterDevice(Licenses l){

        Devices dev = new Devices(Funciones.getPhoneID(context),true);
        getReferenceFireStore(l).document(dev.getCODE()).set(dev);
        Map<String, Object> update = new HashMap<>();
        update.put("date", FieldValue.serverTimestamp());
        update.put("mdate", FieldValue.serverTimestamp());
        getReferenceFireStore(l).document(dev.getCODE()).update(update);

    }

    public long insert(Devices d){
        ContentValues cv = new ContentValues();
        cv.put(CODE,d.getCODE());
        cv.put(ENABLED,d.isENABLED());
        cv.put(DATE, Funciones.getFormatedDate(d.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(d.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Devices d, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,d.getCODE() );
        cv.put(ENABLED,d.isENABLED());
        cv.put(MDATE, Funciones.getFormatedDate((Date) d.getMDATE()));

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
            Task<QuerySnapshot> devices = db.collection(Tablas.generalLicencias).document(key).collection(Tablas.generalLicenciasDevices).get();
            devices.addOnSuccessListener(onSuccessListener);
            devices.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public int validateDevice(Devices d){
        //Devices d = getDeviceByCode();
        if(d == null){
            return CODES.CODE_DEVICES_UNREGISTERED;
        }
        if(!d.isENABLED()){
            return CODES.CODE_DEVICES_DISABLED;
        }

        return CODES.CODE_DEVICES_ENABLED;
    }

    public Devices getDeviceByCode(){
        Devices device = null;
        try {
            String code = Funciones.getPhoneID(context);
            String sql = "SELECT " + CODE + ", " + ENABLED + ", " + DATE + ", " + MDATE+" " +
                    "FROM " + TABLE_NAME + " " +
                    "WHERE " + CODE + " = ?";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{code});
            if(c.moveToFirst()){
                device = new Devices(c);
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return device;
    }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = getReferenceFireStore(LicenseController.getInstance(context).getLicense()).get();
            measureUnits.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Devices object = dc.getDocument().toObject(Devices.class);
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

    public void getQueryDevicesByCode(Licenses licence, String code, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore(licence).
                whereEqualTo(CODE, code).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }


    public void searchChanges(OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        Date mdate = DB.getLastMDateSaved(context, TABLE_NAME);
        if(mdate != null){
            getReferenceFireStore(LicenseController.getInstance(context).getLicense()).
                    whereGreaterThan(MDATE, mdate).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }else{//TODOS
            getReferenceFireStore(LicenseController.getInstance(context).getLicense()).
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);
        }

    }


    public Task<QuerySnapshot> getFindThisDeviceFromFireBase(Licenses license, OnSuccessListener onSuccessListener, OnFailureListener failureListener){
        // Create a query against the collection.
        Query query = getReferenceFireStore(license).whereEqualTo("code", Funciones.getPhoneID(context));
        // retrieve  query results asynchronously using query.get()
        return query.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(failureListener);
    }
}
