package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.Adapters.Models.UserControlRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.CloudFireStoreObjects.Users;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class UserControlController {
    public static final String TABLE_NAME = "USERCONTROL";
    public static String CODE = "code",TARGET ="target", TARGETCODE = "targetcode",CONTROL = "control",VALUE = "value",ACTIVE = "active", DATE = "date", MDATE = "mdate";
    public static String[]columns = new String[]{CODE, TARGET, TARGETCODE, CONTROL,VALUE,ACTIVE, DATE, MDATE};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT, "+TARGET+" TEXT,"+TARGETCODE+" TEXT, "+CONTROL+" TEXT," +VALUE+" TEXT, "+ACTIVE+" BOOLEAN, "+
            DATE+" TEXT, "+MDATE+" TEXT)";
    public static String[]RolesControl = new String[]{CODES.USER_CONTROL_CREATEORDER,  CODES.USER_CONTROL_CHARGE_ORDERS};

    FirebaseFirestore db;
    Context context;
    private static UserControlController instance;
    private UserControlController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UserControlController getInstance(Context context){
        if(instance == null){
            instance = new UserControlController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUserControl);
        return reference;
    }

    public long insert(UserControl uc){
        ContentValues cv = new ContentValues();
        cv.put(CODE, uc.getCODE());
        cv.put(TARGET, uc.getTARGET());
        cv.put(TARGETCODE, uc.getTARGETCODE());
        cv.put(CONTROL, uc.getCONTROL());
        cv.put(VALUE, uc.getVALUE());
        cv.put(ACTIVE, uc.getACTIVE());
        cv.put(DATE, Funciones.getFormatedDate(uc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(uc.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
        return result;
    }

    public long update(UserControl uc, String where, String[]whereArgs){
        ContentValues cv = new ContentValues();
        cv.put(CODE, uc.getCODE());
        cv.put(TARGET, uc.getTARGET());
        cv.put(TARGETCODE, uc.getTARGETCODE());
        cv.put(CONTROL, uc.getCONTROL());
        cv.put(VALUE, uc.getVALUE());
        cv.put(ACTIVE, uc.getACTIVE());
        cv.put(DATE, Funciones.getFormatedDate(uc.getDATE()));
        cv.put(MDATE, Funciones.getFormatedDate(uc.getMDATE()));

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
            Task<QuerySnapshot> client = db.collection(Tablas.generalUsers).document(key).collection(Tablas.generalUsersUserControl).get();
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
                            UserControl object = dc.getDocument().toObject(UserControl.class);
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



    public void sendToFireBase(UserControl uc){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(uc.getCODE()), uc.toMap());
            lote.commit();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void sendToFireBase(String control, String target, String targetCode, ArrayList<UserControl> newControls){
        try {
            WriteBatch lote = db.batch();

            if (newControls != null ){

                String notIn=" NOT IN ('1'";
                for(UserControl uc: newControls){

                    String where = UserControlController.CONTROL+" = ? AND "+UserControlController.TARGET+" = ? AND "+UserControlController.TARGETCODE+"= ? AND "+UserControlController.VALUE+" = ?";
                    String[]args = new String[]{control, target, targetCode, uc.getVALUE()};
                    ArrayList<UserControl> existingPM = getUserControls(where, args, null);

                    if(existingPM.size() >0){//ACTUALIZAR
                        uc.setCODE(existingPM.get(0).getCODE());//sustituye el codigo nuevo por el existente en la base de datos
                        uc.setDATE(existingPM.get(0).getDATE());//permanecer la fecha de creacion.
                        uc.setMDATE(null);

                        //ENVIAR A FIRE BASE
                        lote.update(getReferenceFireStore().document(uc.getCODE()), uc.toMap());

                        //ACTUALIZAR LOCAL
                        where = UserControlController.CODE+" = ?";
                        update(uc,where, new String[]{uc.getCODE()});


                    }else{//INSERTAR
                        lote.set(getReferenceFireStore().document(uc.getCODE()), uc.toMap());
                        insert(uc);
                    }
                    notIn+=",'"+uc.getCODE()+"'";
                }

                notIn+=")";
                String where = UserControlController.CONTROL+" = ? AND "+UserControlController.TARGET+" = ? AND "+UserControlController.TARGETCODE+"= ? AND "+UserControlController.ACTIVE+" = ? AND  "+UserControlController.CODE+notIn;
                ArrayList<UserControl> toDisable = getUserControls(where, new String[]{control,target,targetCode, "1"}, null);
                for(UserControl uc: toDisable){
                    uc.setACTIVE(false);
                    uc.setMDATE(null);
                    where = UserControlController.CODE+" = ?";
                    update(uc,where, new String[]{uc.getCODE()});

                    lote.update(getReferenceFireStore().document(uc.getCODE()), uc.toMap());
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

    public void sendToFireBase(String target, String targetCode, ArrayList<UserControl> newControls){
        try {
            WriteBatch lote = db.batch();

            if (newControls != null ){

                String notIn=" NOT IN ('1'";
                for(UserControl uc: newControls){

                    String where = UserControlController.CONTROL+" = ? AND "+UserControlController.TARGET+" = ? AND "+UserControlController.TARGETCODE+"= ? AND "+UserControlController.VALUE+" = ?";
                    String[]args = new String[]{uc.getCONTROL(), target, targetCode, uc.getVALUE()};
                    ArrayList<UserControl> existingPM = getUserControls(where, args, null);

                    if(existingPM.size() >0){//ACTUALIZAR
                        uc.setCODE(existingPM.get(0).getCODE());//sustituye el codigo nuevo por el existente en la base de datos
                        uc.setDATE(existingPM.get(0).getDATE());//permanecer la fecha de creacion.
                        uc.setMDATE(null);

                        //ENVIAR A FIRE BASE
                        lote.update(getReferenceFireStore().document(uc.getCODE()), uc.toMap());

                        //ACTUALIZAR LOCAL
                        where = UserControlController.CODE+" = ?";
                        update(uc,where, new String[]{uc.getCODE()});


                    }else{//INSERTAR
                        lote.set(getReferenceFireStore().document(uc.getCODE()), uc.toMap());
                        insert(uc);
                    }
                    notIn+=",'"+uc.getCODE()+"'";
                }

                notIn+=")";//obtener todos los controles del mismo target y targetCode (usuario x por ejemplo) que estan activos en la base dee datos pero que no fueron seleccionados.
                String where = UserControlController.TARGET+" = ? AND "+UserControlController.TARGETCODE+"= ? AND "+UserControlController.ACTIVE+" = ? AND  "+UserControlController.CODE+notIn;
                ArrayList<UserControl> toDisable = getUserControls(where, new String[]{target,targetCode, "1"}, null);
                for(UserControl uc: toDisable){
                    uc.setACTIVE(false);
                    uc.setMDATE(null);
                    where = UserControlController.CODE+" = ?";
                    update(uc,where, new String[]{uc.getCODE()});

                    lote.update(getReferenceFireStore().document(uc.getCODE()), uc.toMap());
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


    public void deleteFromFireBase(UserControl uc){
        try {
            getReferenceFireStore().document(uc.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ArrayList<UserControl> getUserControls(String where, String[]args, String orderBy){
        ArrayList<UserControl> result = new ArrayList<>();
        try{
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,columns,where,args,null,null,orderBy);
            while(c.moveToNext()){
                result.add(new UserControl(c));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public UserControl getUserControlByCode(String code){
        String where = CODE+" = ?";
        ArrayList<UserControl> pts = getUserControls(where, new String[]{code}, null);
        if(pts.size()>0){
            return  pts.get(0);
        }
        return null;
    }

    public boolean createOrders(){
        String c = searchControl(CODES.USER_CONTROL_CREATEORDER);
        return (c != null);
    }


    public boolean chargeOrders(){
        String c = searchControl(CODES.USER_CONTROL_CHARGE_ORDERS);
        return (c != null);
    }

    public boolean printOrders(){
        String c = searchControl(CODES.USER_CONTROL_PRINTORDERS);
        return (c != null);
    }

    public boolean editOrders(){
        String c = searchControl(CODES.USER_CONTROL_MODIFYORDER);
        return (c != null);
    }

    public boolean cancelOrders(){
        String c = searchControl(CODES.USER_CONTROL_ANULATEORDER);
        return (c != null);
    }



    /**
     * Simple seleccion row model
     * @param where
     * @param args
     * @param campoOrder
     * @return
     */

        public ArrayList<UserControlRowModel> getTablaCodeSRM(String where, String[] args, String campoOrder){
            ArrayList<UserControlRowModel> result = new ArrayList<>();
            if(campoOrder == null){campoOrder = TARGET+" DESC,"+TARGETCODE+", "+CONTROL;}
            where=((where != null)? "WHERE "+where:"");
            try {

                String sql = "SELECT UC."+CODE+" as CODE, UC."+CONTROL+" AS CONTROL,UC."+TARGET+" as TARGET, UC."+TARGETCODE+" as TARGETCODE, IFNULL(C."+CompanyController.NAME+", IFNULL(UT."+UserTypesController.DESCRIPTION+", IFNULL(U."+UsersController.USERNAME+", ''))) as TARGETCODEDESCRIPTION, UC."+VALUE+" as VALUE, UC."+ACTIVE+" as ACTIVE,  UC."+MDATE+" AS MDATE " +
                        "FROM "+TABLE_NAME+" UC  " +
                        "LEFT JOIN "+CompanyController.TABLE_NAME+" C on UC."+TARGETCODE+" = C."+CompanyController.CODE+" AND UC."+TARGET+" = '"+CODES.USERSCONTROL_TARGET_COMPANY+"' "+
                        "LEFT JOIN "+UserTypesController.TABLE_NAME+" UT on UC."+TARGETCODE+" = UT."+UserTypesController.CODE+" AND UC."+TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER_ROL+"' "+
                        "LEFT JOIN "+UsersController.TABLE_NAME+" U on UC."+TARGETCODE+" = U."+UsersController.CODE+" AND UC."+TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER+"' "+
                         where+" " +
                        "ORDER BY "+campoOrder;
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
                while(c.moveToNext()){
                    String code = c.getString(c.getColumnIndex("CODE"));
                    String value = c.getString(c.getColumnIndex("VALUE"));
                    String description = c.getString(c.getColumnIndex("CONTROL"))+" ["+value+"]";
                    String target = c.getString(c.getColumnIndex("TARGET"));
                    String targetCode = c.getString(c.getColumnIndex("TARGETCODE"));
                    String active = c.getString(c.getColumnIndex("ACTIVE"));
                    String mdate = c.getString(c.getColumnIndex("MDATE"));
                    String targetName ="";
                    if(target.equals(CODES.USERSCONTROL_TARGET_COMPANY)){
                        targetName = "Compañia";
                    }else if(target.equals(CODES.USERSCONTROL_TARGET_USER_ROL)){
                        targetName="Cargo";
                    }else if(target.equals(CODES.USERSCONTROL_TARGET_USER)){
                        targetName="Usuario";
                    }else{ targetName="UNKNOWN";}

                    String targetDesc =  c.getString(c.getColumnIndex("TARGETCODEDESCRIPTION"));

                    result.add(new UserControlRowModel(code,description, target, targetName,targetCode ,targetDesc,active.equals("1"),(mdate!= null)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            return result;

        }




    public String searchControl(String control){
        String result = null;
        String sql = "SELECT "+VALUE+" " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+CONTROL+" = ? AND "+ACTIVE+" = ?  AND ( ("+TARGET+" = ? AND "+TARGETCODE+" = ? ) OR ("+TARGET+" = ? AND "+TARGETCODE+" = ? )  OR ("+TARGET+" = ? AND "+TARGETCODE+" = ? )    )  ";
        Users u = UsersController.getInstance(context).getUserByCode(Funciones.getCodeuserLogged(context));
        String[]args = new String[]{control, "1",
                CODES.USERSCONTROL_TARGET_USER,u.getCODE(),
                CODES.USERSCONTROL_TARGET_USER_ROL,u.getROLE(),
                CODES.USERSCONTROL_TARGET_COMPANY,u.getCOMPANY() };
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            if (c.moveToFirst()) {
                result = c.getString(0);
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public KV getLowTargetLevel(String control){
        //USER =0, USER_ROL = 1, COMPANY = 2
        KV result = null;
        String sql = "SELECT "+TARGET+", "+TARGETCODE+" " +
                "FROM "+TABLE_NAME+" " +
                "WHERE "+CONTROL+" = ? AND "+ACTIVE+" = ?  AND ( ("+TARGET+" = ? AND "+TARGETCODE+" = ? ) OR ("+TARGET+" = ? AND "+TARGETCODE+" = ? )  OR ("+TARGET+" = ? AND "+TARGETCODE+" = ? )    )  " +
                "ORDER BY "+TARGET+" ASC";
        Users u = UsersController.getInstance(context).getUserByCode(Funciones.getCodeuserLogged(context));
        String[]args = new String[]{control, "1",
                CODES.USERSCONTROL_TARGET_USER,u.getCODE(),
                CODES.USERSCONTROL_TARGET_USER_ROL,u.getROLE(),
                CODES.USERSCONTROL_TARGET_COMPANY,u.getCOMPANY() };
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, args);
            if (c.moveToFirst()) {
                result = new KV(c.getString(0), c.getString(1));
            }
            c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public void fillSpinnerControlLevels(Spinner spn, boolean addTodos){
        ArrayList<KV> list = new ArrayList<>();
        if(addTodos)
        list.add(new KV("-1", "TODOS"));

        list.add(new KV(CODES.USERSCONTROL_TARGET_USER_ROL, "Cargo"));
        list.add(new KV(CODES.USERSCONTROL_TARGET_COMPANY, "Empresa"));
        list.add(new KV(CODES.USERSCONTROL_TARGET_USER, "Usuario"));
        spn.setAdapter(new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1,list));
    }

    public void fillSpinnerByControlLevel(Spinner spn, String target){
        if(target.equals(CODES.USERSCONTROL_TARGET_COMPANY)){
         CompanyController.getInstance(context).fillSpnCompany(spn, false);
        }else if(target.equals(CODES.USERSCONTROL_TARGET_USER_ROL)){
        UserTypesController.getInstance(context).fillSpnUserTypes(spn, false);
        }else if(target.equals(CODES.USERSCONTROL_TARGET_USER)){
        UsersController.getInstance(context).fillSpnUserWithCode(spn, false);
        }
    }

    public void fillSpinnerOrderSplitType(Spinner spn){
        ArrayList<KV> list = new ArrayList<>();

        list.add(new KV("-1", "NONE"));
        list.add(new KV(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY, "Family"));
        list.add(new KV(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP, "Group"));
        spn.setAdapter(new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1,list));
    }

    /**
     * obtiene todas las mesas asignadas a un usuario, rol o empresa
     * @return
     */
    /*
    public ArrayList<SimpleSeleccionRowModel> getUserTableSSRM(String target, String targerCode){

        ArrayList<SimpleSeleccionRowModel> list = new ArrayList<>();
        String sql = "SELECT ad."+AreasDetailController.CODE+" as CODEAREADETAIL, ad."+AreasDetailController.DESCRIPTION+" as AREADETAILDESCRIPTION, ifnull(uc."+CODE+", -1), ifnull(uc."+TARGET+", '"+target+"'), ifnull(uc."+TARGETCODE+", '"+targerCode+"'), ifnull(uc."+ACTIVE+", 0) as CHECKED " +
                     "FROM "+AreasDetailController.TABLE_NAME+" ad " +
                     "LEFT JOIN "+TABLE_NAME+" uc ON uc."+TARGET+" = '"+target+"' AND uc."+TARGETCODE+" = '"+targerCode+"' AND  uc."+CONTROL+" = '"+CODES.USERCONTROL_TABLEASSIGN+"' AND  ad."+AreasDetailController.CODE+" = uc."+VALUE+" " +
                     "ORDER BY ad."+AreasDetailController.ORDER+" ASC ";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            list.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODEAREADETAIL")),c.getString(c.getColumnIndex("AREADETAILDESCRIPTION")), c.getString(c.getColumnIndex("CHECKED")).equals("1")));
        }c.close();

        return list;
    }*/


    /**
     * obtiene todos los controles asignados a un rol.
     * @return
     */
    public ArrayList<SimpleSeleccionRowModel> getRolesControlSSRM(String targerCode){
        ArrayList<SimpleSeleccionRowModel> list = new ArrayList<>();
        String controls = getControlsQueryRol();
        String sql = "SELECT c.CODE as CODE, c.DESCRIPTION as DESCRIPTION, ifnull(uc."+CODE+", -1), ifnull(uc."+TARGET+", '"+CODES.USERSCONTROL_TARGET_USER_ROL+"'), ifnull(uc."+TARGETCODE+", '"+targerCode+"'), ifnull(uc."+ACTIVE+", 0) as CHECKED " +
                "FROM "+controls+" c "+
                "LEFT JOIN "+TABLE_NAME+" uc ON  uc."+CONTROL+" = c.CODE AND uc."+TARGET+" = c.TARGET AND uc."+TARGETCODE+" = '"+targerCode+"'  " +
                "ORDER BY c.DESCRIPTION ";

        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        while(c.moveToNext()){
            list.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")),c.getString(c.getColumnIndex("DESCRIPTION")), c.getString(c.getColumnIndex("CHECKED")).equals("1")));
        }c.close();

        return list;
    }


    /**
     * obtiene todos los controles asignados a un Usuario.
     * @return
     */
    public ArrayList<SimpleSeleccionRowModel> getUsersControlSSRM( String targerCodeUser){
        ArrayList<SimpleSeleccionRowModel> list = new ArrayList<>();
        try {
            String controls = getControlsQueryUser();
            String sql;
            if(targerCodeUser.equals("-1")){
                 sql = "SELECT c.CODE as CODE, c.DESCRIPTION as DESCRIPTION, 0 as CHECKED " +
                        "FROM "+controls+" c ";
            }else{
                 sql = "SELECT c.CODE as CODE, c.DESCRIPTION as DESCRIPTION, ifnull(uc." + CODE + ", -1), ifnull(uc." + TARGET + ", '" + CODES.USERSCONTROL_TARGET_USER_ROL + "'), ifnull(uc." + TARGETCODE + ", '" + targerCodeUser + "'), ifnull(uc." + ACTIVE + ", 0) as CHECKED " +
                        "FROM " + controls + " c " +
                        "LEFT JOIN " + TABLE_NAME + " uc ON uc." + TARGET + " = c.TARGET AND uc." + TARGETCODE + " = '" + targerCodeUser + "'  AND  c.CODE = uc." + CONTROL + " " +
                        "ORDER BY c.DESCRIPTION ";
            }


            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                list.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("DESCRIPTION")), c.getString(c.getColumnIndex("CHECKED")).equals("1")));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }


    /*public ArrayList<SimpleSeleccionRowModel> getOrderSplitSSRM(String splitType){
        ArrayList<SimpleSeleccionRowModel> list = new ArrayList<>();
        try{
            String sql ="";
            if(splitType.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY)){
            sql = "SELECT pt."+ProductsTypesController.CODE+" as CODE, pt."+ProductsTypesController.DESCRIPTION+" as DESCRIPTION, ifnull(uc." + ACTIVE + ", 0) as CHECKED " +
                  "FROM "+ProductsTypesController.TABLE_NAME+" pt " +
                  "LEFT JOIN "+UserControlController.TABLE_NAME+" uc ON uc."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_ORDERSPLIT+"' AND uc."+UserControlController.VALUE+" = pt."+ProductsTypesController.CODE+" " +
                  "ORDER BY pt."+ProductsTypesController.DESCRIPTION;
            }else if(splitType.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP)){
            sql = "SELECT pt."+ProductsSubTypesController.CODE+" as CODE, pt."+ProductsSubTypesController.DESCRIPTION+" as DESCRIPTION, ifnull(uc." + ACTIVE + ", 0) as CHECKED " +
                    "FROM "+ProductsSubTypesController.TABLE_NAME+" pt " +
                    "LEFT JOIN "+UserControlController.TABLE_NAME+" uc ON uc."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_ORDERSPLIT+"' AND uc."+UserControlController.VALUE+" = pt."+ProductsSubTypesController.CODE+" " +
                    "ORDER BY pt."+ProductsTypesController.DESCRIPTION;
            }

            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while(c.moveToNext()){
                list.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")), c.getString(c.getColumnIndex("CHECKED")).equals("1")));
            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }


    public ArrayList<SimpleSeleccionRowModel> getOrderSplitDestinySSRM(String codeUser){
        ArrayList<SimpleSeleccionRowModel> list = new ArrayList<>();
        try{

            String company = UsersController.getInstance(context).getUserByCode(codeUser).getCOMPANY();
            String tabla = "SELECT ifnull(pt."+ProductsTypesController.CODE+", pst."+ProductsSubTypesController.CODE+") as CODE, " +
                    "ifnull(pt."+ProductsTypesController.DESCRIPTION+", pst."+ProductsSubTypesController.DESCRIPTION+") as DESCRIPTION, 0 as CHECKED " +
                    "FROM "+UserControlController.TABLE_NAME+" uc " +
                    "LEFT JOIN "+ProductsTypesController.TABLE_NAME+" pt on pt."+ProductsTypesController.CODE+" = uc."+UserControlController.VALUE+" " +
                    "LEFT JOIN "+ProductsSubTypesController.TABLE_NAME+" pst on pst."+ProductsSubTypesController.CODE+" = uc."+UserControlController.VALUE+" " +
                    "WHERE uc."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_ORDERSPLIT+"' AND uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_COMPANY+"' " +
                    "AND uc."+UserControlController.TARGETCODE+" = '"+company+"' AND uc."+UserControlController.ACTIVE+" = '1' ";

            String sql = "SELECT t.CODE, t.DESCRIPTION, ifnull(uc."+UserControlController.ACTIVE+", 0) as CHECKED " +
                    "FROM ("+tabla+") as t " +
                    "LEFT JOIN "+UserControlController.TABLE_NAME+" uc on uc."+UserControlController.CONTROL+" = '"+CODES.USERCONTROL_ORDERSPLITDESTINY+"' " +
                    "AND uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER+"' AND uc."+UserControlController.TARGETCODE+" = '"+codeUser+"' " +
                    "AND uc."+UserControlController.VALUE+" = t.CODE";


            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql,null);
            while(c.moveToNext()){
                list.add(new SimpleSeleccionRowModel(c.getString(c.getColumnIndex("CODE")),
                        c.getString(c.getColumnIndex("DESCRIPTION")), c.getString(c.getColumnIndex("CHECKED")).equals("1")));
            }c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }*/




    public static String getControlsQueryRol(){
        String controls = "(SELECT CODE, DESCRIPTION, TARGET FROM  ( " +
                "SELECT '"+CODES.USER_CONTROL_CREATEORDER+"' as CODE, 'Create orders' as DESCRIPTION, '"+CODES.USERSCONTROL_TARGET_USER_ROL +"' as TARGET "+
                "UNION " +
                "SELECT '"+CODES.USER_CONTROL_CHARGE_ORDERS+"' as CODE, 'Charge orders' as DESCRIPTION,  '"+CODES.USERSCONTROL_TARGET_USER_ROL +"' as TARGET " +
                " ) )";
        return controls;
    }

    public static String getControlsQueryUser(){
        //String RolesControls =  getControlsQueryRol();
        String controls = //"(" +
               // "SELECT * FROM " +
                "(SELECT CODE, DESCRIPTION, TARGET FROM  ( " +
                "SELECT '"+CODES.USER_CONTROL_MODIFYORDER+"' as CODE, 'Modify orders' as DESCRIPTION, '"+CODES.USERSCONTROL_TARGET_USER +"' as TARGET "+
                "UNION " +
                "SELECT '"+CODES.USER_CONTROL_ANULATEORDER+"' as CODE, 'Anulate orders' as DESCRIPTION,  '"+CODES.USERSCONTROL_TARGET_USER +"' as TARGET " +
                "UNION " +
                "SELECT '"+CODES.USER_CONTROL_PRINTORDERS+"' as CODE, 'Print orders' as DESCRIPTION,  '"+CODES.USERSCONTROL_TARGET_USER +"' as TARGET " +
                " ) )  "; //+
               // "UNION " +
               // "SELECT * FROM "+RolesControls+"  " +
              //  ")";
        return controls;
    }

    /**
     * llena un spinner con una lista de los roles que pueden despachar ordenes (CONTROL DISPATCHORDER activo para el rol)
     * @return
     */
    /*
    public ArrayList<KV> getOrderDispachRoles(){
        ArrayList<KV> result = new ArrayList<>();
        try {
            String sql = "SELECT ut." + UserTypesController.CODE + " as CODE, ut." + UserTypesController.DESCRIPTION + " as DESCRIPTION " +
                    "FROM " + UserTypesController.TABLE_NAME + " ut " +
                    "INNER JOIN " + UserControlController.TABLE_NAME + " uc on uc." + UserControlController.CONTROL + " = '" + CODES.USER_CONTROL_DISPATCHORDER + "' AND uc." + UserControlController.ACTIVE + " = 1  " +
                    "AND uc." + UserControlController.TARGET + " = '" + CODES.USERSCONTROL_TARGET_USER_ROL + "' AND uc." + UserControlController.TARGETCODE + " = ut." + UserTypesController.CODE + " " +
                    "ORDER BY ut." + UserTypesController.DESCRIPTION;
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while (c.moveToNext()) {
                result.add(new KV(c.getString(c.getColumnIndex("CODE")), c.getString(c.getColumnIndex("DESCRIPTION"))));
            }
            c.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }*/

 /*   public void fillOrderDispachRoles(Spinner spn){
        spn.setAdapter(new ArrayAdapter<KV>(context, android.R.layout.simple_list_item_1,getOrderDispachRoles()));
    }*/


    /**
     * Llena un spinner con todos los usuarios que: 1- Puedan crear ordenes, 2- esten activos, 3-tengan ordenes abiertas
     * @param spn
     */
  /*  public void fillSpinnerOrderMoveUsersFrom(Spinner spn){
        ArrayList<KV> list = new ArrayList<>();
        list.add(new KV("-1", "NONE"));
        String sql = "SELECT u."+UsersController.CODE+", u."+UsersController.USERNAME+" " +
                "FROM "+UsersController.TABLE_NAME+" u " +
                "INNER JOIN "+SalesController.TABLE_NAME+" s on s."+SalesController.CODEUSER+" = u."+UsersController.CODE+" AND s."+SalesController.STATUS+" IN ("+CODES.CODE_ORDER_STATUS_OPEN+", "+CODES.CODE_ORDER_STATUS_DELIVERED+", "+CODES.CODE_ORDER_STATUS_READY+") " +
                "INNER JOIN "+UserControlController.TABLE_NAME+" uc on uc."+UserControlController.CONTROL+" = '"+CODES.USER_CONTROL_CREATEORDER+"' AND uc."+UserControlController.TARGET+" = '"+CODES.USERSCONTROL_TARGET_USER_ROL+"' AND uc."+UserControlController.TARGETCODE+" = u."+UsersController.ROLE+" " +
                "WHERE u."+UsersController.ENABLED+" = ? " +
                "GROUP BY u."+UsersController.CODE+", u."+UsersController.USERNAME;
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{"1"});
        while(c.moveToNext()){
            list.add(new KV(c.getString(0), c.getString(1)));
        }
        spn.setAdapter(new ArrayAdapter<KV>(context,android.R.layout.simple_list_item_1,list));
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
