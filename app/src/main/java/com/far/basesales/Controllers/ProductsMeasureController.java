package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.far.basesales.Adapters.Models.EditSelectionRowModel;
import com.far.basesales.Adapters.Models.ProductMeasureRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Generic.KV;
import com.far.basesales.Generic.KV2;
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


public class ProductsMeasureController {

    public static final String TABLE_NAME ="PRODUCTSMEASURE";
    public static  String CODE = "code", CODEPRODUCT = "codeproduct", CODEMEASURE = "codemeasure" ,PRICE="price",
            ENABLED = "enabled",RANGE ="range", MINPRICE="minprice", MAXPRICE = "maxprice",  DATE = "date", MDATE= "mdate";
    private static String[] columns = new String[]{CODE, CODEPRODUCT, CODEMEASURE,PRICE,ENABLED,RANGE, MINPRICE, MAXPRICE,  DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+CODEMEASURE+" TEXT,"+PRICE+" DECIMAL,"+ENABLED+" TEXT,"+RANGE+" TEXT," +
            ""+MINPRICE+" DECIMAL, "+MAXPRICE+" DECIMAL,  "+DATE+" TEXT," + MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    private static  ProductsMeasureController instance;

    private ProductsMeasureController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsMeasureController getInstance(Context c){
        if(instance == null){
            instance = new ProductsMeasureController(c);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
       return db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsMeasure);
    }

    public ArrayList<ProductsMeasure>getProductsMeasureByCodeProduct(String codeProduct){
        String where = CODEPRODUCT+" = ? AND "+ENABLED+" = ?";
        String[] args = new String[]{codeProduct, "1"};
        return  getProductsMeasure(where, args);
    }
    public ArrayList<ProductsMeasure>getProductsMeasure(String where, String[] args){
       ArrayList<ProductsMeasure> result = new ArrayList<>();
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, where, args, null, null, CODEMEASURE);
            while(c.moveToNext()){

                String code =  c.getString(c.getColumnIndex(CODE));
                String codeProduct = c.getString(c.getColumnIndex(CODEPRODUCT));
                String codeMeasure = c.getString(c.getColumnIndex(CODEMEASURE));
                double price = c.getDouble(c.getColumnIndex(PRICE));
                String range = c.getString(c.getColumnIndex(RANGE));
                double minPrice = c.getDouble(c.getColumnIndex(MINPRICE));
                double maxPrice = c.getDouble(c.getColumnIndex(MAXPRICE));
                String enabled = c.getString(c.getColumnIndex(ENABLED));
                String date = c.getString(c.getColumnIndex(DATE));
                String mdate = c.getString(c.getColumnIndex(MDATE));
               result.add(new ProductsMeasure(
                       code,
                       codeProduct,
                       codeMeasure,
                       price,
                       (range != null && range.equals("1")),
                       minPrice,
                       maxPrice,
                       (enabled!= null && enabled.equals("1")),
                       date,
                       mdate));

            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public ArrayList<KV2>getProductsMeasureKVByCodeProduct(String codeProduct){
        ArrayList<KV2> result = new ArrayList<>();
        try {
            String sql = "select um."+MeasureUnitsController.CODE+" as CODE, um."+MeasureUnitsController.DESCRIPTION+" as DESCRIPTION, "+PRICE+" as PRICE " +
                    "FROM "+MeasureUnitsController.TABLE_NAME+" um " +
                    "INNER JOIN "+TABLE_NAME+" pm on pm."+CODEMEASURE+" = um."+MeasureUnitsController.CODE+" "+
                    "WHERE "+CODEPRODUCT+" = ? AND "+ENABLED+" = ?";
            String[] args = new String[]{codeProduct, "1"};

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,args );
            while(c.moveToNext()){
                result.add(new KV2(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")),
                        c.getString(c.getColumnIndex("PRICE"))));

            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }


    public ProductsMeasure getProductMeasureByCode(String code){
        ArrayList<ProductsMeasure> pm =getProductsMeasure(CODE+" = ?",new String[]{code});
        return pm!=null && pm.size()>0?pm.get(0):null;
    }

    public ProductsMeasure getProductMeasureByProductAndMeasure(String codeProduct, String codeMeasure){
        ArrayList<ProductsMeasure> pm =getProductsMeasure(ProductsMeasureController.CODEPRODUCT+" = ? AND "+ProductsMeasureController.CODEMEASURE+" = ?", new String[]{codeProduct, codeMeasure});
        return pm!=null && pm.size()>0?pm.get(0):null;
    }

    public long insert(ProductsMeasure p){
        ContentValues cv = new ContentValues();
        cv.put(CODE, p.getCODE());
        cv.put(CODEPRODUCT,p.getCODEPRODUCT() );
        cv.put(CODEMEASURE,p.getCODEMEASURE());
        cv.put(PRICE,p.getPRICE());
        cv.put(ENABLED, p.getENABLED());
        cv.put(RANGE, p.getRANGE());
        cv.put(MINPRICE, p.getMINPRICE());
        cv.put(MAXPRICE, p.getMAXPRICE());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductsMeasure p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE, p.getCODE());
        cv.put(CODEPRODUCT,p.getCODEPRODUCT() );
        cv.put(CODEMEASURE,p.getCODEMEASURE());
        cv.put(PRICE,p.getPRICE());
        cv.put(ENABLED, p.getENABLED());
        cv.put(RANGE, p.getRANGE());
        cv.put(MINPRICE, p.getMINPRICE());
        cv.put(MAXPRICE, p.getMAXPRICE());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<ProductMeasureRowModel> getSSRMByCodeProduct(String codeProduct){
        ArrayList<ProductMeasureRowModel> result = new ArrayList<>();
        try {
            String sql = "SELECT pm." + CODEMEASURE + " AS CODEMEASURE, mu." + MeasureUnitsController.DESCRIPTION + " AS MEASUREDESCRIPTION, " +
                    " ifnull(pm."+RANGE+", '0') as RANGE,ifnull("+MINPRICE+", 0) as MINPRICE, ifnull("+MAXPRICE+", 0) as MAXPRICE,   ifnull(pm."+PRICE+", 0) as PRICE, pm."+ENABLED+" AS ENABLED " +
                    "FROM " + TABLE_NAME + " pm " +
                    "INNER JOIN " + MeasureUnitsController.TABLE_NAME + " mu ON pm." + CODEMEASURE + " = mu." + MeasureUnitsController.CODE + " " +
                    "WHERE pm." + CODEPRODUCT + " = ? AND pm.ENABLED = ?";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{codeProduct, "1"});

            //String codeMeasure,String measureDescription,String amount,  boolean priceRange,String minPrice, String maxPrice,  boolean checked
            while (c.moveToNext()) {
                result.add(new ProductMeasureRowModel(
                        c.getString(c.getColumnIndex("CODEMEASURE")),
                        c.getString(c.getColumnIndex("MEASUREDESCRIPTION")),
                        c.getDouble(c.getColumnIndex("PRICE")),
                        c.getString(c.getColumnIndex("RANGE")).equals("1"),
                        c.getDouble(c.getColumnIndex("MINPRICE")),
                        c.getDouble(c.getColumnIndex("MAXPRICE")),
                        c.getString(c.getColumnIndex("ENABLED")).equals("1")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> combos = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProductsMeasure).get();
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
                            ProductsMeasure object = dc.getDocument().toObject(ProductsMeasure.class);
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



    public ArrayList<ProductsMeasure> getdifference(ArrayList<ProductsMeasure> sdOriginal, ArrayList<ProductsMeasure> newsalesDetails){
        ArrayList<ProductsMeasure>toDelete = new ArrayList<>();
        for(ProductsMeasure del: sdOriginal){
            boolean delete = true;
            for(ProductsMeasure update: newsalesDetails){
                if(del.getCODE().equals(update.getCODE()) && del.getPRICE() == update.getPRICE()){
                    delete = false;
                    break;
                }
            }

            if(delete)
                toDelete.add(del);


        }

        return toDelete;
    }



    public ArrayList<DocumentReference> getReferences(String field, String value){
        ArrayList<DocumentReference> references = new ArrayList<>();
        ArrayList<ProductsMeasure> objs = getProductsMeasure(field+" = ? ", new String[]{value});
        if(objs != null){
            for(ProductsMeasure c: objs){
                references.add(getReferenceFireStore().document(c.getCODE()));
            }
        }
        return references;
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
                ProductsMeasure obj = doc.toObject(ProductsMeasure.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }



}
