package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.bluetoothlibrary.Printer.Print;
import com.far.basesales.Adapters.Models.CompanyRowModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Generic.KV;
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


public class CompanyController {
    public static final String TABLE_NAME ="COMPANY";
    public static  String CODE = "code", NAME = "name" ,
            RNC = "rnc",PHONE = "phone", PHONE2="phone2",LOGO = "logo",
            ADDRESS="address", ADDRESS2="address2", DATE = "date", MDATE = "mdate";
    String[] columns = new String[]{CODE, NAME, RNC, PHONE, PHONE2, ADDRESS, ADDRESS2,LOGO, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+NAME+" TEXT, "+RNC+" TEXT, "+PHONE+" TEXT, "+PHONE2+" TEXT, "+ADDRESS+" TEXT," +
            ADDRESS2+" TEXT,"+LOGO+" TEXT,  "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    private static CompanyController instance;
    private CompanyController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static CompanyController getInstance(Context context){
        if(instance == null){
            instance = new CompanyController(context);
        }
        return instance;
    }
    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersCompany);
        return reference;
    }

    public long insert(Company c){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(NAME,c.getNAME());
        cv.put(RNC, c.getRNC());
        cv.put(PHONE,c.getPHONE() );
        cv.put(PHONE2,c.getPHONE2() );
        cv.put(ADDRESS,c.getADDRESS() );
        cv.put(ADDRESS2,c.getADDRESS2() );
        cv.put(LOGO, c.getLOGO());
        cv.put(DATE, Funciones.getFormatedDate((Date) c.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate((Date) c.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Company c, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,c.getCODE() );
        cv.put(NAME,c.getNAME());
        cv.put(RNC, c.getRNC());
        cv.put(PHONE,c.getPHONE() );
        cv.put(PHONE2,c.getPHONE2() );
        cv.put(ADDRESS,c.getADDRESS() );
        cv.put(ADDRESS2,c.getADDRESS2() );
        cv.put(LOGO, c.getLOGO());
        cv.put(MDATE, Funciones.getFormatedDate(c.getMDATE()));

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
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersCompany).get();
            combos.addOnSuccessListener(onSuccessListener);
            combos.addOnFailureListener(onFailureListener);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> company =getReferenceFireStore().get();
            company.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Company object = dc.getDocument().toObject(Company.class);
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

    public void sendToFireBase(Company company){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(company.getCODE()), company.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public Company getCompanyByCode(String code){
        ArrayList<Company> arrayList  = getCompanys(CODE+" = ?", new String[]{code}, null);
        return arrayList.size()>0?arrayList.get(0):null;
    }
    public ArrayList<Company> getCompanys(String where, String[]args, String orderBy){
        ArrayList<Company> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Company(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Company getCompany(){
        Company result=null;
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,null,null,null,null,null);
            if(c.moveToFirst()){
                result=new Company(c);
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<CompanyRowModel> getCompanyRM(String where, String[] args, String campoOrder){
        ArrayList<CompanyRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = NAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE,"+RNC+" as RNC, "+NAME+" AS NAME, "+PHONE+" AS PHONE, "+PHONE2+" as PHONE2, " +
                    ""+ADDRESS+" as ADDRESS, "+ADDRESS2+" as ADDRESS2,"+LOGO+" as LOGO, "+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new CompanyRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("NAME")),
                        c.getString(c.getColumnIndex("RNC")),
                        c.getString(c.getColumnIndex("ADDRESS")) ,
                        c.getString(c.getColumnIndex("ADDRESS2")) ,
                        c.getString(c.getColumnIndex("PHONE")) ,
                        c.getString(c.getColumnIndex("PHONE2")) ,
                        c.getString(c.getColumnIndex("LOGO")) ,
                        c.getString(c.getColumnIndex("MDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

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
                Company obj = doc.toObject(Company.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }





    public void addCompanyToPrint(Print p){
        ArrayList<Company> companys = getCompanys(null, null, null);
        String name="NONE";
        String direction ="NONE";
        String phone = "NONE";
        if(companys.size() > 0){
            name = companys.get(0).getNAME();
            direction = companys.get(0).getADDRESS();
            phone = companys.get(0).getPHONE();
        }
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_CENTER);
        //p.addImage(R.drawable.ic_action_monetization_on);
        p.drawText(name);
        p.drawText(direction);
        p.drawText(phone);
        p.drawText("");
        p.addAlign(Print.PRINTER_ALIGN.ALIGN_LEFT);
    }


    public void deleteFromFireBase(Company company){
        try {
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(company.getCODE()));


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


    public void fillSpnCompany(Spinner spn){
        ArrayList<Company> result = getCompanys(null, null, null);
        ArrayList<KV> spnList = new ArrayList<>();
        for(Company ut : result){
            spnList.add(new KV(ut.getCODE(), ut.getNAME()+" ["+ut.getRNC()+"]"));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

}
