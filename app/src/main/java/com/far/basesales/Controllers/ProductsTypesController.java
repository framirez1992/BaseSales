package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsTypes;
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

public class ProductsTypesController {
    public static final  String TABLE_NAME = "PRODUCTSTYPES";
    public static String CODE = "code", DESCRIPTION = "description", ORDER = "orden", DATE="date", MDATE="mdate";
    public static String[]colums = new String[]{CODE, DESCRIPTION, ORDER, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT,"+ORDER+" INTEGER, "+DATE+" TEXT, "+MDATE+" TEXT)";

    Context context;
    FirebaseFirestore db;
    private static ProductsTypesController instance;
    private ProductsTypesController(Context c){
        this.context = c;
        this.db = FirebaseFirestore.getInstance();
    }
    public static ProductsTypesController getInstance(Context context){
        if(instance == null){
            instance = new ProductsTypesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsTypes);
        return reference;
    }
    public long insert(ProductsTypes pt){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE());
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(DATE, Funciones.getFormatedDate(pt.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(pt.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductsTypes pt){
        String where = CODE+" = ?";
        return update(pt, where, new String[]{pt.getCODE()});
    }
    public long update(ProductsTypes pt, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,pt.getCODE() );
        cv.put(DESCRIPTION,pt.getDESCRIPTION());
        cv.put(ORDER, pt.getORDEN());
        cv.put(DATE, Funciones.getFormatedDate(pt.getMDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(pt.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(ProductsTypes pt){
        return delete(CODE+" = ?", new String[]{pt.getCODE()});
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<ProductsTypes> getProductTypes(String[] camposFiltros,String[]argumentos, String campoOrderBy){

        ArrayList<ProductsTypes> result = new ArrayList<>();
        if(campoOrderBy == null){
            campoOrderBy=DESCRIPTION;
        }
        try {
            Cursor c =  DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, ((camposFiltros!=null)?DB.getWhereFormat(camposFiltros):null), argumentos, null, null, campoOrderBy);
            while (c.moveToNext()){
                result.add(new ProductsTypes(c));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public int getCount(String destiny){
        int result = 0;
        ArrayList<ProductsTypes> pts = getProductTypes(null, null, null);
        if(pts != null){
            result =  pts.size();
        }
        return result;
    }
    public ProductsTypes getProductTypeByCode(String code){
        ArrayList<ProductsTypes> pts = getProductTypes(new String[]{CODE}, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public int getNextOrden(){
        int result = 9999;
        /*String sql = "SELECT MAX("+ORDER+" + 1) " +
                "FROM "+TABLE_NAME;
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToFirst()){
                result = c.getInt(0);
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }*/
        return result;
    }


    public ArrayList<SimpleRowModel> getAllProductTypesSRM(String where, String[] args){
        ArrayList<SimpleRowModel> result = new ArrayList<>();
        String orderBy = ORDER+" ASC, "+DESCRIPTION;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, colums, where, args, null, null, orderBy);
            while(c.moveToNext()){
                result.add(new SimpleRowModel(c.getString(c.getColumnIndex(CODE)), c.getString(c.getColumnIndex(DESCRIPTION)), c.getString(c.getColumnIndex(MDATE)) != null));
            }
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
    public ArrayList<SimpleSeleccionRowModel> getProductTypesSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT "+CODE+" as CODE, "+DESCRIPTION+" as DESCRIPTION " +
                    "FROM "+TABLE_NAME+"  " +
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

    public void sendToFireBase(ProductsTypes pt, OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(pt.getCODE()), pt.toMap());
            lote.commit()
                    .addOnFailureListener(failureListener);
    }


    public void deleteFromFireBase(ProductsTypes pt, OnFailureListener failureListener){
            getReferenceFireStore()
                    .document(pt.getCODE())
                    .delete()
                    .addOnFailureListener(failureListener);
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> measureUnits = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProductsTypes).get();
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
                            ProductsTypes object = dc.getDocument().toObject(ProductsTypes.class);
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




    public void fillSpinner(Spinner spn, boolean addTodos){
        String orderBy = ProductsTypesController.ORDER+" ASC, "+ProductsTypesController.DESCRIPTION;
        ArrayList<ProductsTypes> list = getProductTypes(null,null, orderBy);
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        for(ProductsTypes pt : list){
            data.add(new KV(pt.getCODE(), pt.getDESCRIPTION()));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    /**
     * Retorna los ProductTypes de los productos que estan bloqueados
     * @param spn
     * @param addTodos
     */
    public void fillSpinnerForLockedProducts(Spinner spn, boolean addTodos){
        ArrayList<KV> data = new ArrayList<>();
        if(addTodos){
            KV obj = new KV("0", "TODOS");
            data.add(obj);
        }
        try {
            String sql = "SELECT pt." + ProductsTypesController.CODE + ", pt." + ProductsTypesController.DESCRIPTION + " " +
                    "FROM " + ProductsTypesController.TABLE_NAME + " pt " +
                    "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.TYPE + " = pt." + ProductsTypesController.CODE + " " +
                    "INNER JOIN " + ProductsControlController.TABLE_NAME + " pc on pc." + ProductsControlController.CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                    "WHERE pc." + ProductsControlController.BLOQUED + " = ? " +
                    "GROUP BY pt." + ProductsTypesController.CODE + ", pt." + ProductsTypesController.DESCRIPTION + " " +
                    "ORDER BY pt." + ProductsTypesController.ORDER + " ASC, pt." + ProductsTypesController.DESCRIPTION;

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{"1"});
            while (c.moveToNext()) {
                data.add(new KV(c.getString(0), c.getString(1)));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, data);
        spn.setAdapter(adapter);
    }

    /**
     * retorna true si el codigo tiene dependencias en otras tablas (llave foranea)
     * @param code
     * @return
     */
    public String hasDependencies(String code){
        String msg = "";
        ArrayList<String> tables = new ArrayList<>();
        if(DB.getInstance(context).hasDependencies(ProductsSubTypesController.TABLE_NAME,ProductsSubTypesController.CODETYPE,code))
           tables.add(ProductsSubTypesInvController.TABLE_NAME);
        if(DB.getInstance(context).hasDependencies(ProductsController.TABLE_NAME,ProductsController.TYPE,code))
            tables.add(ProductsInvController.TABLE_NAME);

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



    public void searchProductTypeFromFireBase(String code, OnSuccessListener<QuerySnapshot> success, OnFailureListener failure){

            getReferenceFireStore().
                    whereEqualTo(CODE, code).//mayor que, ya que las fechas (la que buscamos de la DB) tienen hora, minuto y segundos.
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
                ProductsTypes obj = doc.toObject(ProductsTypes.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
