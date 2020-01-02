package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.far.basesales.Adapters.Models.NewOrderProductModel;
import com.far.basesales.Adapters.Models.ProductRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Products;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.DataBase.CloudFireStoreDB;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Generic.KV2;
import com.far.basesales.Globales.CODES;
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


public class ProductsController {

    public static final String TABLE_NAME ="PRODUCTS";
    public static  String CODE = "code", DESCRIPTION = "description",
            TYPE = "type",SUBTYPE = "subtype",  COMBO = "combo", DATE = "date", MDATE="mdate";
    public static String[] columns = new String[]{CODE, DESCRIPTION,TYPE, SUBTYPE, COMBO, DATE, MDATE};

    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("
            +CODE+" TEXT, "+DESCRIPTION+" TEXT, "+TYPE+" TEXT, "+SUBTYPE+" TEXT, "+
            COMBO+" BOOLEAN, "+DATE+" TEXT, "+MDATE+" TEXT)";
    Context context;
    FirebaseFirestore db;
    static ProductsController instance;

    private ProductsController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static ProductsController getInstance(Context context){
        if(instance == null){
            instance = new ProductsController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersProducts);
        return reference;
    }


    public long insert(Products p){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(DESCRIPTION,p.getDESCRIPTION());
        cv.put(TYPE, p.getTYPE());
        cv.put(SUBTYPE,p.getSUBTYPE() );
        cv.put(COMBO,p.isCOMBO() );
        cv.put(DATE, Funciones.getFormatedDate(p.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Products p){
        return  update(p, CODE+" = ?", new String[]{p.getCODE()});
    }
    public long update(Products p, String where, String[] args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,p.getCODE() );
        cv.put(DESCRIPTION,p.getDESCRIPTION());
        cv.put(TYPE, p.getTYPE());
        cv.put(SUBTYPE,p.getSUBTYPE());
        cv.put(COMBO,p.isCOMBO() );
        cv.put(MDATE, Funciones.getFormatedDate(p.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args);
        return result;
    }

    public long delete(Products p){
        return delete(CODE+" = ?", new String[]{p.getCODE()});
    }
    public long delete(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
        return result;
    }

    public ArrayList<Products> getProducts(String where, String[]args, String orderBy){
        ArrayList<Products> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Products(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public Products getProductByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Products> pts = getProducts(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }



    public ArrayList<ProductRowModel> getProductsPRM(String where, String[] args, String campoOrder){
        ArrayList<ProductRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION, pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, pst."+ProductsSubTypesController.CODE+" AS PSTCODE, pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" p " +
                    "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+TYPE+" "+
                    "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+SUBTYPE+" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new ProductRowModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("DESCRIPTION")),c.getString(c.getColumnIndex("PTCODE")) ,c.getString(c.getColumnIndex("PTDESCRIPTION")),c.getString(c.getColumnIndex("PSTCODE")),c.getString(c.getColumnIndex("PSTDESCRIPTION")),c.getString(c.getColumnIndex("MDATE")) != null));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    public ArrayList<NewOrderProductModel> getNewProductRowModels(String where, String[] args, String campoOrder){
        ArrayList<NewOrderProductModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = DESCRIPTION;}
        where=((where != null)? "WHERE "+where:"");
        String data = "";
        try {

            String sql = "SELECT * FROM ("+
                    "SELECT toc."+TempOrdersController.DETAIL_CODE+" AS CODEORDERDETAIL, p."+CODE+" as CODE, p."+DESCRIPTION+" AS DESCRIPTION, ifnull(toc."+TempOrdersController.DETAIL_QUANTITY+", 0) AS QUANTITY, " +
                    //COLOCANDO UNA UNIDAD DE MEDIDA POR DEFECTO A CADA PRODUCTO QUE VENGA EN EL QUERY. SI NO ESTA GUARDADO EN LA TABLA TEMPORAL TOMARA UNA UNIDAD CUALQUIERA DE LAS QUE EL PRODUCTO YA TIENE REGISTRADA.
                    "ifnull(toc."+TempOrdersController.DETAIL_CODEUND+", pmc."+ProductsMeasureController.CODEMEASURE+" ) as MEASURE,ifnull(toc."+TempOrdersController.DETAIL_MANUALPRICE+", pmc."+ProductsMeasureController.PRICE+") as MANUALPRICE, " +
                    "ifnull(toc."+TempOrdersController.DETAIL_POSITION+", 0) as POSITION, pt."+ProductsTypesController.CODE+" as PTCODE, pt."+ProductsTypesController.DESCRIPTION+" as PTDESCRIPTION, " +
                    "pst."+ProductsSubTypesController.CODE+" AS PSTCODE, pst."+ProductsSubTypesController.DESCRIPTION+" AS PSTDESCRIPTION, p."+MDATE+" AS MDATE, ifnull(pc."+ProductsControlController.BLOQUED+", 0) as BLOQUED " +
                    "FROM "+TABLE_NAME+" p " +
                    "INNER JOIN "+ProductsMeasureController.TABLE_NAME+" pmc on pmc."+ProductsMeasureController.CODEPRODUCT+" = p."+ProductsController.CODE+" AND pmc."+ProductsMeasureController.ENABLED+" = '1' "+
                    "INNER JOIN "+ProductsTypesController.TABLE_NAME+" pt ON pt."+ProductsTypesController.CODE+" = p."+TYPE+" "+
                    "INNER JOIN "+ProductsSubTypesController.TABLE_NAME+" pst ON pst."+ProductsSubTypesController.CODE+" = "+SUBTYPE+" "+
                    "LEFT JOIN "+TempOrdersController.TABLE_NAME_DETAIL+" toc  on toc."+TempOrdersController.DETAIL_CODEPRODUCT+" = p."+CODE+" AND toc."+TempOrdersController.DETAIL_CODEUND+" = pmc."+ProductsMeasureController.CODEMEASURE+" "+
                    "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+" = p."+ProductsController.CODE+" "+
                     where+" " +
                    "ORDER BY toc."+TempOrdersController.DETAIL_POSITION+" ASC " +
                    ")"+
                    "GROUP BY  "+CODE+" "+
                    "ORDER BY  "+DESCRIPTION+" ASC";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);

            while(c.moveToNext()){
                String codeOrderdetail = c.getString(c.getColumnIndex("CODEORDERDETAIL"));
                String code = c.getString(c.getColumnIndex("CODE"));
                String desc = c.getString(c.getColumnIndex("DESCRIPTION"));
                String qty = String.valueOf(c.getInt(c.getColumnIndex("QUANTITY")));
                String measure = c.getString(c.getColumnIndex("MEASURE"));
                String manualPrice = c.getString(c.getColumnIndex("MANUALPRICE"));
                String position =  c.getString(c.getColumnIndex("POSITION"));
                String blocked = c.getString(c.getColumnIndex("BLOQUED"));
                data+="DESC:"+desc+" MEASURE: "+measure+" ORDER:"+position+"\n";

                result.add(new NewOrderProductModel(codeOrderdetail,
                        code,
                        desc,
                        qty,
                        measure,
                        manualPrice,
                        blocked,
                        ProductsMeasureController.getInstance(context).getProductsMeasureKVByCodeProduct(c.getString(c.getColumnIndex("CODE")))));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

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
                            Products object = dc.getDocument().toObject(Products.class);
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

    public void sendToFireBase(Products product, OnCompleteListener completeListener, OnSuccessListener successListener, OnFailureListener failureListener){
        sendToFireBase(product, null, completeListener, successListener, failureListener);
    }
    public void sendToFireBase(Products product, ArrayList<ProductsMeasure> newMeasures, OnCompleteListener completeListener, OnSuccessListener successListener, OnFailureListener failureListener){
            WriteBatch lote = db.batch();

            if(product.getMDATE() == null){
                lote.set(getReferenceFireStore().document(product.getCODE()), product.toMap());
            }else{
                lote.update(getReferenceFireStore().document(product.getCODE()), product.toMap());
            }
            String notIn=" NOT IN ('1'";

            if (newMeasures != null && !newMeasures.isEmpty()){

                for(ProductsMeasure pm: newMeasures){
                    String where = ProductsMeasureController.CODEMEASURE+" = ? AND "+ProductsMeasureController.CODEPRODUCT+" = ?";
                    String[]args = new String[]{pm.getCODEMEASURE(), pm.getCODEPRODUCT()};
                    ArrayList<ProductsMeasure> existingPM = ProductsMeasureController.getInstance(context).getProductsMeasure(where, args);

                    if(existingPM.size() >0){//ACTUALIZAR
                        pm.setCODE(existingPM.get(0).getCODE());//sustituye el codigo nuevo por el existente en la base de datos
                        pm.setDATE(existingPM.get(0).getDATE());//permanecer la fecha de creacion.
                        pm.setMDATE(null);

                        //ENVIAR A FIRE BASE
                        lote.update(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());

                        //ACTUALIZAR LOCAL
                        //where = ProductsMeasureController.CODE+" = ?";
                        //ProductsMeasureController.getInstance(context).update(pm,where, new String[]{pm.getCODE()});
                    }else{//INSERTAR
                        lote.set(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());
                        //ProductsMeasureController.getInstance(context).insert(pm);
                    }

                    notIn+=",'"+pm.getCODE()+"'";
                }
            }

            notIn+=")";
            String where = ProductsMeasureController.CODEPRODUCT+" = ? AND "+ProductsMeasureController.ENABLED+" = ? AND  "+ProductsMeasureController.CODE+notIn;
            ArrayList<ProductsMeasure> toDisable = ProductsMeasureController.getInstance(context).getProductsMeasure(where, new String[]{product.getCODE(), "1"});
            for(ProductsMeasure pm: toDisable){
                pm.setENABLED(false);
                pm.setMDATE(null);
                where = ProductsMeasureController.CODE+" = ?";
                //ProductsMeasureController.getInstance(context).update(pm,where, new String[]{pm.getCODE()});

                lote.update(ProductsMeasureController.getInstance(context).getReferenceFireStore().document(pm.getCODE()), pm.toMap());
            }

            lote.commit().addOnCompleteListener(completeListener)
                    .addOnSuccessListener(successListener)
                    .addOnFailureListener(failureListener);


    }

    public void deleteFromFireBase(Products product, OnCompleteListener completeListener, OnSuccessListener onSuccessListener, OnFailureListener failureListener){
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(product.getCODE()));
            for(KV2 data: getDependencies(product.getCODE())){
                for(DocumentReference dr : CloudFireStoreDB.getInstance(context, null, null).getDocumentsReferencesByTableName(data)){
                    lote.delete(dr);
                }
            }

            lote.commit().addOnCompleteListener(completeListener).
                    addOnSuccessListener(onSuccessListener).
                    addOnFailureListener(failureListener);
    }


    /**
     * retorna un arrayList con todas las  dependencias en otras tablas (llave foranea)
     * @param code
     * @return
     */
    public ArrayList<KV2> getDependencies(String code){
        ArrayList<KV2> tables = new ArrayList<>();
       /* if(DB.getInstance(context).hasDependencies(CombosController.TABLE_NAME,CombosController.CODEPRODUCT,code))
            tables.add(new KV2(CombosController.TABLE_NAME,CombosController.CODEPRODUCT,code));
        if(DB.getInstance(context).hasDependencies(PriceListController.TABLE_NAME,PriceListController.CODEPRODUCT,code))
            tables.add(new KV2(PriceListController.TABLE_NAME,PriceListController.CODEPRODUCT,code));*/
        if(DB.getInstance(context).hasDependencies(ProductsControlController.TABLE_NAME,ProductsControlController.CODEPRODUCT,code))
            tables.add(new KV2(ProductsControlController.TABLE_NAME,ProductsControlController.CODEPRODUCT,code));
        if(DB.getInstance(context).hasDependencies(ProductsMeasureController.TABLE_NAME,ProductsMeasureController.CODEPRODUCT,code))
            tables.add(new KV2(ProductsMeasureController.TABLE_NAME,ProductsMeasureController.CODEPRODUCT,code));


        return tables;
        //priceList,productsControl, productsMeasure,combos
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
                Products obj = doc.toObject(Products.class);
                if(update(obj, CODE+"=?", new String[]{obj.getCODE()}) <=0){
                    insert(obj);
                }
            }
        }

    }
}
