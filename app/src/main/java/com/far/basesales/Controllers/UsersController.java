package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.UserRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.DataBase.CloudFireStoreDB;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Generic.KV;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;


public class UsersController {

    public static String TABLE_NAME = "USERS";
    public static String CODE = "code", COMPANY= "company", ENABLED = "enabled",ROLE = "role",USERNAME = "username",
            PASSWORD = "password",SYSTEMCODE = "systemcode",  DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE,SYSTEMCODE, COMPANY, ENABLED, ROLE, USERNAME, PASSWORD, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+SYSTEMCODE+" TEXT, "+COMPANY+" TEXT,"+ROLE+" TEXT, "+USERNAME+" TEXT," +
             PASSWORD+" TEXT, "+ENABLED+" TEXT, "+DATE+" TEXT, "+MDATE+" TEXT)";

    FirebaseFirestore db;
    Context context;
    private static UsersController instance;
    private UsersController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UsersController getInstance(Context context){
        if(instance == null){
            instance = new UsersController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUsers);
        return reference;
    }
    public CollectionReference getReferenceFireStore(Licenses l){
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUsers);
        return reference;
    }
    public long insert(Users user){
        ContentValues cv = new ContentValues();
        cv.put(CODE, user.getCODE());
        cv.put(SYSTEMCODE, user.getSYSTEMCODE());
        cv.put(COMPANY, user.getCOMPANY());
        cv.put(ENABLED, user.isENABLED());
        cv.put(ROLE, user.getROLE());
        cv.put(USERNAME, user.getUSERNAME());
        cv.put(PASSWORD, user.getPASSWORD());
        cv.put(DATE, Funciones.getFormatedDate(user.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(user.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(Users user, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, user.getCODE());
        cv.put(SYSTEMCODE, user.getSYSTEMCODE());
        cv.put(COMPANY, user.getCOMPANY());
        cv.put(ENABLED, user.isENABLED());
        cv.put(ROLE, user.getROLE());
        cv.put(USERNAME, user.getUSERNAME());
        cv.put(PASSWORD, user.getPASSWORD());
        cv.put(MDATE, Funciones.getFormatedDate(user.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, whereArgs);
        return result;
    }

    public long delete(String where, String[]whereArgs){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, whereArgs);
        return result;
    }


    public void getDataFromFireBase(String key, OnSuccessListener<QuerySnapshot> onSuccessListener,
                                    OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersUsers).get();
            client.addOnSuccessListener(onSuccessListener);
            client.addOnFailureListener(onFailureListener);
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
                            Users object = dc.getDocument().toObject(Users.class);
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

    public void sendToFireBase(Users user){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(user.getCODE()), user.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void deleteFromFireBase(Users u){
        try {
            WriteBatch lote = db.batch();
            lote.delete(getReferenceFireStore().document(u.getCODE()));
            /*for(KV2 data: getDependencies(u.getCODE())){
                for(DocumentReference dr : CloudFireStoreDB.getInstance(context, null, null).getDocumentsReferencesByTableName(data)){
                    lote.delete(dr);
                }
            }*/

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

    public ArrayList<Users> getUsers(String where, String[]args, String orderBy){
        ArrayList<Users> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new Users(c));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public Users getUserByCode(String code){
        String where = CODE+" = ?";
        ArrayList<Users> pts = getUsers(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }
    public int validateUser(Users u){
        if(u == null){
            return CODES.CODE_USERS_INVALID;
        }
        if(!u.isENABLED()){
            return CODES.CODE_USERS_DISBLED;
        }

        return CODES.CODE_USERS_ENABLED;
    }

    public Task<QuerySnapshot> getUserFromFireBase(String user, String pass){

        // Create a query against the collection.
        Query query = getReferenceFireStore().whereEqualTo("code", user).whereEqualTo("password", pass);
// retrieve  query results asynchronously using query.get()
        return query.get();
    }

    public void getQueryUsersByCode(Licenses l, String code, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore(l).
                whereEqualTo(CODE, code).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }

    public boolean isAdmin(){
        Users u = getUserByCode(Funciones.getCodeuserLogged(context));
        return u!= null && u.getSYSTEMCODE() != null && u.getSYSTEMCODE().equals(CODES.USER_SYSTEM_CODE_ADMIN);
    }

    public boolean isSuperUser(){
        Users u = getUserByCode(Funciones.getCodeuserLogged(context));
        return u != null && u.getSYSTEMCODE()!= null && u.getSYSTEMCODE().equals(CODES.USER_SYSTEM_CODE_SU);
    }

    public boolean isUserRole(String role){
        return getUserByCode(Funciones.getCodeuserLogged(context)).getROLE().equals(role);
    }

    public void fillSpnUser(Spinner spn, boolean addTodos){
        ArrayList<Users> result = getUsers(null, null, USERNAME);
        ArrayList<KV> spnList = new ArrayList<>();
        if(addTodos){
            spnList.add(new KV("0", "TODOS"));
        }
        for(Users u : result){
            spnList.add(new KV(u.getCODE(), u.getUSERNAME()));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnUserWithCode(Spinner spn, boolean addTodos){
        ArrayList<Users> result = getUsers(null, null, USERNAME);
        ArrayList<KV> spnList = new ArrayList<>();
        if(addTodos){
            spnList.add(new KV("0", "TODOS"));
        }
        for(Users u : result){
            spnList.add(new KV(u.getCODE(), u.getCODE()+" "+u.getUSERNAME()));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpnUserByUserType(Spinner spn, String role, boolean addTodos){
        String where = ROLE+" = ?";
        ArrayList<Users> result = getUsers(where, new String[]{role}, USERNAME);
        ArrayList<KV> spnList = new ArrayList<>();
        if(addTodos){
            spnList.add(new KV("-1", "TODOS"));
        }
        for(Users u : result){
            spnList.add(new KV(u.getCODE(), u.getUSERNAME()));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,spnList));
    }

    public ArrayList<UserRowModel> getUserSRM(String where, String[] args, String campoOrder){
        ArrayList<UserRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = USERNAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+CODE+" as CODE,u."+SYSTEMCODE+" as SYSTEMCODE, u."+USERNAME+" AS USERNAME, u."+ENABLED+" ENABLED, ut."+UserTypesController.DESCRIPTION+" as ROLE, u."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on u."+ROLE+" = ut."+UserTypesController.CODE+" "+
                    where;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                result.add(new UserRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("SYSTEMCODE")),
                        c.getString(c.getColumnIndex("USERNAME")),
                        c.getString(c.getColumnIndex("ROLE")) ,
                        c.getString(c.getColumnIndex("ENABLED")).equals("1"),
                        c.getString(c.getColumnIndex("MDATE")) != null));
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
  /*  public ArrayList<SimpleSeleccionRowModel> getUserSSRM(String where, String[] args, String campoOrder){
        ArrayList<SimpleSeleccionRowModel> result = new ArrayList<>();
        if(campoOrder == null){campoOrder = USERNAME;}
        where=((where != null)? "WHERE "+where:"");
        try {

            String sql = "SELECT u."+CODE+" as CODE, u."+USERNAME+" AS USERNAME, u."+ENABLED+" ENABLED, ut."+UserTypesController.DESCRIPTION+" as ROLE, u."+MDATE+" AS MDATE " +
                    "FROM "+TABLE_NAME+" u " +
                    "INNER JOIN "+UserTypesController.TABLE_NAME+" ut on u."+ROLE+" = ut."+UserTypesController.CODE+" "+
                     where
                    +" ORDER BY "+campoOrder;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            while(c.moveToNext()){
                String code = c.getString(c.getColumnIndex("CODE"));
                String name = code+"\n"+c.getString(c.getColumnIndex("USERNAME"));
                result.add(new SimpleSeleccionRowModel(code,name ,false));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }*/



    /**
     * llena un spinner con todos los usuarios que pueden crear ordenes (meseros)
     */
  /*  public void fillSpnUsersCreateOrders(Spinner spn){
        ArrayList<KV> data = new ArrayList<>();
        String sql = "SELECT u."+CODE+", u."+USERNAME+" " +
                "FROM "+UsersController.TABLE_NAME+" u " +
                "INNER JOIN "+UserControlController.TABLE_NAME+" uc on uc."+UserControlController.CONTROL+" = '"+CODES.USER_CONTROL_CREATEORDER+"' AND uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER_ROL+"' " +
                "AND uc."+UserControlController.TARGETCODE+" = u."+ROLE+" AND uc."+UserControlController.ACTIVE+" = '1' " +
                "ORDER BY u."+USERNAME;
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            data.add(new KV(c.getString(0), "["+c.getString(0)+"]"+c.getString(1)));
        }c.close();

        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,data));
    }*/

    /**
     * llena un spinner con todos los usuarios que pueden crear ordenes (meseros) segun el codigo de mesa (areaDetail) asignado.
     */
    /*public void fillSpnUsersCreateOrders(Spinner spn, String codeAreaDetail){
        ArrayList<KV> data = new ArrayList<>();
        try {
            String tabla =
                    "SELECT u2."+UsersController.CODE+" as CODEUSER, u2."+USERNAME+" as USER, uc2."+UserControlController.TARGET+" as TARGET, uc2."+UserControlController.TARGETCODE+" AS TARGETCODE " +
                    "FROM "+UsersController.TABLE_NAME+" u2 " +
                    "INNER JOIN " + UserControlController.TABLE_NAME + " uc2 on uc2." + UserControlController.CONTROL + " = '" + CODES.USERCONTROL_TABLEASSIGN + "' AND uc2." + UserControlController.ACTIVE + " = '1' AND uc2."+UserControlController.VALUE+" = '"+codeAreaDetail+"' " +
                    "AND(   (uc2." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER + "' AND uc2." + UserControlController.TARGETCODE + " = u2." + CODE + " ) " +
                    "     OR(uc2." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER_ROL + "' AND uc2." + UserControlController.TARGETCODE + " = u2." + ROLE + " ) " +
                    "     OR(uc2." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_COMPANY + "' AND uc2." + UserControlController.TARGETCODE + " = u2." + COMPANY + " )   " +
                    ") " +
                    "GROUP BY u2."+UsersController.CODE+", u2."+UsersController.USERNAME+", uc2."+UserControlController.TARGET+" "+
                    "ORDER BY uc2."+UserControlController.TARGET+" DESC ";
            String tabla2 = "SELECT * FROM ("+tabla+") as t GROUP BY t.CODEUSER ";

            String sql = "SELECT u." + CODE + ", u." + USERNAME +" " +
                    "FROM " + UsersController.TABLE_NAME + " u " +
                    "INNER JOIN " + UserControlController.TABLE_NAME + " uc on uc." + UserControlController.CONTROL + " = '" + CODES.USER_CONTROL_CREATEORDER + "' AND uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER_ROL + "' " +
                    "AND uc." + UserControlController.TARGETCODE + " = u." + ROLE + " AND uc." + UserControlController.ACTIVE + " = '1' " +
                    "INNER JOIN ("+tabla+") as t on t.CODEUSER = u."+CODE+" "+
                    "GROUP BY u." + CODE + ", u." + USERNAME + " " +
                    "ORDER BY u." + USERNAME + " ";
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                data.add(new KV(c.getString(0), "[" + c.getString(0) + "]" + c.getString(1)));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,data));
    }*/

    /**
     * retorna un arrayList con todas las  dependencias en otras tablas (llave foranea)
     * @param code
     * @return
     */
    /*public ArrayList<KV2> getDependencies(String code){
        ArrayList<KV2> tables = new ArrayList<>();
        if(DB.getInstance(context).hasDependencies(UserInboxController.TABLE_NAME,UserInboxController.CODESENDER,code))//enviados
            tables.add(new KV2(UserInboxController.TABLE_NAME,UserInboxController.CODESENDER,code));
        if(DB.getInstance(context).hasDependencies(UserInboxController.TABLE_NAME,UserInboxController.CODEUSER,code))//recibidos
            tables.add(new KV2(UserInboxController.TABLE_NAME,UserInboxController.CODEUSER,code));

        return tables;
        //priceList,productsControl, productsMeasure,combos
    }*/



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
}
