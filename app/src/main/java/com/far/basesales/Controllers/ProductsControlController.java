package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsControl;
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


public class ProductsControlController {

    public static final String TABLE_NAME ="PRODUCTSCONTROL";
    public static  String CODE = "code",CODEPRODUCT = "codeproduct",BLOQUED = "bloqued", DATE = "date", MDATE="mdate";
    public static String[] columns = new String[]{CODE, CODEPRODUCT,BLOQUED,  DATE, MDATE};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+CODEPRODUCT+" TEXT, "+BLOQUED+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    static ProductsControlController instance;

    private ProductsControlController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsControlController getInstance(Context context){
        if(instance == null){
            instance = new ProductsControlController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProductsControl);
        return reference;
    }


    public long insert(ProductsControl p){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE());
        cv.put(CODEPRODUCT,p.getCODEPRODUCT());
        cv.put(BLOQUED, p.getBLOQUED());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(ProductsControl p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE());
        cv.put(CODEPRODUCT,p.getCODEPRODUCT());
        cv.put(BLOQUED, p.getBLOQUED());
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<ProductsControl> getProductsControl(String where, String[]args, String orderBy){
        ArrayList<ProductsControl> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new ProductsControl(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<ProductsControl> getProductsControlByCodeProduct(String codeProduct){
        ArrayList<ProductsControl> result = new ArrayList<>();
        try{
            String where = CODE+" = ?";
            String[]args = new String[]{codeProduct};

            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,null);
            while(c.moveToNext()){
                result.add(new ProductsControl(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ProductsControl getProductsControlByCode(String code){
        String where = CODE+" = ?";
        ArrayList<ProductsControl> pts = getProductsControl(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

  /*  public ArrayList<SimpleSeleccionRowModel> getSSRMProducts(String where, String[]args){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        try {
            String sql = "SELECT p." + ProductsController.CODE + " AS CODE, p." + ProductsController.DESCRIPTION + " AS DESCRIPTION, ifnull("+BLOQUED+", 0) as BLOQUED " +
                    "FROM " + ProductsController.TABLE_NAME + " p " +
                    "INNER JOIN "+ProductsSubTypesController.TABLE_NAME+" ps on p."+ProductsController.SUBTYPE+" = ps."+ProductsSubTypesController.CODE+" "+
                    "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = ps."+ProductsSubTypesController.CODETYPE+" "+
                    "LEFT JOIN " + TABLE_NAME + " pc  ON pc." + CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                    "WHERE  "+where+" "+
                    "ORDER BY p."+ProductsController.DESCRIPTION;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while (c.moveToNext()) {
                result.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")),false ));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }*/



    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> products = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersProducts).get();
            products.addOnSuccessListener(onSuccessListener);
            products.addOnFailureListener(onFailureListener);
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
                            ProductsControl object = dc.getDocument().toObject(ProductsControl.class);
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


    public void sendToFireBase(ArrayList<ProductsControl> newProductsControl){
        try {
            WriteBatch lote = db.batch();

            if (newProductsControl != null && !newProductsControl.isEmpty()){

                for (ProductsControl obj : newProductsControl) {//Insertando o actualizando el nuevo detalle
                    if (obj.getMDATE() == null) {
                        lote.set(getReferenceFireStore().document(obj.getCODE()), obj.toMap());
                    } else {
                        lote.update(getReferenceFireStore().document(obj.getCODE()), obj.toMap());
                    }

                }
            }

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


    public void deleteFromFireBase(ProductsControl p){
        try {
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(p.getCODE()));

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

    public void fillSpinnerStatus(Spinner spn, boolean unlock){
        ArrayList<KV> objects = new ArrayList<>();
        if(unlock){
            objects.add(new KV("1", "Bloqueado"));
        }else{
            objects.add(new KV("0", "Activo"));
        }

        ArrayAdapter<KV> adapter = new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1, objects);
        spn.setAdapter(adapter);
    }

    public ArrayList<ProductsControl> getdifference(ArrayList<ProductsControl> pcOriginal, ArrayList<ProductsControl> newProductsControl){
        ArrayList<ProductsControl>toDelete = new ArrayList<>();
        for(ProductsControl del: pcOriginal){
            boolean delete = true;
            for(ProductsControl update: newProductsControl){
                if(del.getCODEPRODUCT().equals(update.getCODE()) && del.getBLOQUED().equals(update.getBLOQUED())){
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
        ArrayList<ProductsControl> objs = getProductsControl(field+" = ? ", new String[]{value}, null);
        if(objs != null){
            for(ProductsControl c: objs){
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
                ProductsControl obj = doc.toObject(ProductsControl.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
