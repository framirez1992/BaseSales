package com.far.basesales.Controllers;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Roles;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Utils.Funciones;
import com.far.farpdf.Objects.Table;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

public class LicenseController  {
    public static final String TABLE_NAME = "LICENSE";
    public static String CODE = "code",CLIENTNAME="clientname", DATEINI= "dateini", DATEEND = "dateend",DAYS = "days",COUNTER = "counter",
            UPDATED = "updated",STATUS = "status", LASTUPDATE ="lastupdate", PASSWORD = "password", DEVICES = "devices", ENABLED = "enabled";
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT,"+CLIENTNAME+" TEXT, "+DATEINI+" TEXT,"+DATEEND+" TEXT, "+DAYS+" INTEGER, "+COUNTER+" INTEGER, "+UPDATED+" TEXT, "+STATUS+" INTEGER ," +
            LASTUPDATE+" TEXT, "+PASSWORD+" TEXT, "+DEVICES+" INTEGER, "+ENABLED+" TEXT)";
    private String[]colums = new String[]{CODE,CLIENTNAME, DATEINI,DATEEND,DAYS,COUNTER,UPDATED,STATUS,LASTUPDATE,PASSWORD,DEVICES,ENABLED};

    FirebaseFirestore db;
    Context context;
    ArrayList<Devices> devices;
    private static LicenseController instance;

    private  LicenseController (Context c){
        db = FirebaseFirestore.getInstance();
        context = c;
        devices = new ArrayList<>();
    }

    public static LicenseController getInstance(Context c){
        if(instance == null){
            instance = new LicenseController(c);
        }
        return instance;
    }
    public CollectionReference getReferenceFireStore(){
        Licenses l = getLicense();
        if(l == null){
            return null;
        }

        CollectionReference reference = db.collection(Tablas.generalLicencias);
        return reference;
    }

    public ArrayList<Licenses> select(String where, String[] whereArgs, String orderBy){
        ArrayList<Licenses> lic = new ArrayList<>();
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME,colums,where,whereArgs,null,null,null);
        while(c.moveToNext()){
            Licenses l = new Licenses(c.getString(c.getColumnIndex(CODE)),c.getString(c.getColumnIndex(CLIENTNAME)),
                    c.getString(c.getColumnIndex(PASSWORD)),
                    Funciones.parseStringToDate(c.getString(c.getColumnIndex(DATEINI))),Funciones.parseStringToDate(c.getString(c.getColumnIndex(DATEEND))),c.getInt(c.getColumnIndex(COUNTER)), c.getInt(c.getColumnIndex(DAYS)),
                    c.getInt(c.getColumnIndex(DEVICES)),c.getString(c.getColumnIndex(ENABLED)).equals("1"),
                    c.getString(c.getColumnIndex(UPDATED)).equals("1"),Funciones.parseStringToDate(c.getString(c.getColumnIndex(LASTUPDATE))), c.getInt(c.getColumnIndex(STATUS)));
            lic.add(l);
        }c.close();

        return lic;
    }

    public Licenses getLicense(){
        ArrayList<Licenses> al = select(null, null, null);
        return  (al.size() > 0)?al.get(0):null;
    }

    public int validateLicense(Licenses lic){
        if(lic == null){
            return CODES.CODE_LICENSE_NO_LICENSE;
        }
        //Validando vigencia de la licencia.
        if(Funciones.fechaMayorQue(Funciones.getFormatedDate(lic.getLASTUPDATE()), Funciones.getFormatedDate(lic.getDATEEND()))){
           return CODES.CODE_LICENSE_EXPIRED;

        }else if(lic.getSTATUS() == CODES.CODE_LICENSE_DISABLED){ //Validando vigencia de la licencia.
           return CODES.CODE_LICENSE_DISABLED;
        }else if(!lic.isENABLED()){ //Validar si la licencia esta activa
            return CODES.CODE_LICENSE_DISABLED;
        }
        return CODES.CODE_LICENSE_VALID;
    }

    public long insert(Licenses l){
        ContentValues cv = new ContentValues();
        cv.put(CODE, l.getCODE());
        cv.put(CLIENTNAME, l.getCLIENTNAME());
        cv.put(DATEINI, Funciones.getFormatedDate((Date) l.getDATEINI()));
        cv.put(DATEEND, Funciones.getFormatedDate((Date) l.getDATEEND()));
        cv.put(DAYS, l.getDAYS());
        cv.put(COUNTER, l.getCOUNTER());
        cv.put(UPDATED, l.isUPDATED());
        cv.put(STATUS, l.getSTATUS());
        cv.put(LASTUPDATE, Funciones.getFormatedDate((Date) l.getLASTUPDATE()));
        cv.put(PASSWORD, l.getPASSWORD());
        cv.put(DEVICES,l.getDEVICES());
        cv.put(ENABLED, l.isENABLED());

        long result = DB.getInstance(context).getWritableDatabase().insert(TABLE_NAME,null,cv);
       return result;
    }

    public long update(Licenses l){
        ContentValues cv = new ContentValues();
        cv.put(CODE, l.getCODE());
        cv.put(CLIENTNAME, l.getCLIENTNAME());
        cv.put(DATEINI, Funciones.getFormatedDate((Date) l.getDATEINI()));
        cv.put(DATEEND, Funciones.getFormatedDate((Date) l.getDATEEND()));
        cv.put(DAYS, l.getDAYS());
        cv.put(COUNTER, l.getCOUNTER());
        cv.put(UPDATED, l.isUPDATED());
        cv.put(STATUS, l.getSTATUS());
        cv.put(LASTUPDATE, Funciones.getFormatedDate((Date) l.getLASTUPDATE()));
        cv.put(PASSWORD, l.getPASSWORD());
        cv.put(DEVICES,l.getDEVICES());
        cv.put(ENABLED, l.isENABLED());

        String where = CODE+" = ?";

        return  DB.getInstance(context).getWritableDatabase().update(TABLE_NAME, cv,where,new String[]{l.getCODE()});
    }

    public long delete(String where, String[]whereArgs){
        return  DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where,whereArgs);
    }

    public String FillLicenseData(Licenses license){//CARGA INICIAL
        try {
            delete("", null);
            insert(license);

        }catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

    public int updateLicenciaDiaria(String fechaActual){

        int codeResult = -1;
        Licenses l = getLicense();
        if(l != null  &&  l.getLASTUPDATE() != null
                && !Funciones.getFormatedDate((Date) l.getLASTUPDATE()).equals(fechaActual)) {//ya habia realizado una carga inicial y la fecha de ultima actualizacion es diferente
            l.setLASTUPDATE(Funciones.parseStringToDate(fechaActual));
            l.setCOUNTER(Funciones.calcularDias(fechaActual, Funciones.getFormatedDate((Date) l.getDATEINI())));
            l.setUPDATED(false);

            if (Funciones.fechaMayorQue(fechaActual, l.getDATEEND().toString())) {//ya se vencio
                l.setENABLED(false);
                l.setSTATUS(CODES.CODE_LICENSE_EXPIRED);
                codeResult =  CODES.CODE_LICENSE_EXPIRED;
            }

            update(l);
            sendToFireBase(l);
        }

        return codeResult;
    }


    ////////////////   FIREBASE     ////////////////////////////

    public void getDataFromFireBase(String key, OnSuccessListener<DocumentSnapshot> onSuccessListener,
                                    OnFailureListener failureListener){
        Task<DocumentSnapshot> client = db.collection(Tablas.generalLicencias).document(key).get();
        client.addOnSuccessListener(onSuccessListener);
        client.addOnFailureListener(failureListener);
    }

    public void getAllDataFromFireBase(String key, OnFailureListener onFailureListener){
        try {
            Task<QuerySnapshot> reference = getReferenceFireStore().get();
            reference.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot != null && querySnapshot.getDocumentChanges()!= null && !querySnapshot.getDocumentChanges().isEmpty()){
                        for(DocumentChange dc : querySnapshot.getDocumentChanges()) {
                            Licenses object = dc.getDocument().toObject(Licenses.class);
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

    public void sendToFireBase(Licenses l){
        try {
            WriteBatch lote = db.batch();
            lote.set(getReferenceFireStore().document(l.getCODE()), l.toMap());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setLastUpdateToFireBase(){
        try {
            WriteBatch lote = db.batch();
            lote.update(getReferenceFireStore().document(getLicense().getCODE()),LASTUPDATE, FieldValue.serverTimestamp());
            lote.commit();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void setAlarm(String fecha, int hora, int minutos){
    /*    try {
            Licenses l = getLicense();
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if(l != null) {

                AlarmManager.AlarmClockInfo alarmInfo = am.getNextAlarmClock();
                if(alarmInfo == null) {//no hay alarma pendiente

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new SimpleDateFormat("yyyyMMdd-HHmmss").parse(fecha));
                    //cal.set(Calendar.HOUR_OF_DAY, hora);
                    //cal.set(Calendar.MINUTE, minutos);
                    cal.add(Calendar.SECOND, 10);
                    am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, getAlarmPendingIntent(fecha));
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }*/
    }

    /*public PendingIntent getAlarmPendingIntent(String extra){
        Intent intent = new Intent(context, Receiver.class);
        intent.setAction("far.com.eatit.ALARM");
        intent.putExtra("fecha", extra);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return alarmIntent;
    }*/

    public void getQueryLicenceByCode(String code, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore().
                whereEqualTo(CODE, code).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }


    public void getAllLicenses(OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failure){

        db.collection(Tablas.generalLicencias).
                    get().
                    addOnSuccessListener(success).addOnCompleteListener(complete).
                    addOnFailureListener(failure);

    }

   /* public void consumeQuerySnapshot(QuerySnapshot querySnapshot){
        delete(null, null);
        if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
            for(DocumentSnapshot doc: querySnapshot){
                Licenses obj = doc.toObject(Licenses.class);
                insert(obj);
            }
        }

    }*/

    public void createNewLicense(boolean first, Licenses l){

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        ////////////////////////////////////////////////////////////////////////
        ////////  JERARQUIA DE LICENCIAS         //////////////////////////////
        /*String licCode = Funciones.generateCode();
        Licenses licencia = new Licenses(licCode,licCode ,"",new Date(),Funciones.sumaDiasFecha(370), 0, 370, 5, true,true,new Date(),1);
         */
        //creando documento con el key del nuevo cliente en la coleccion GENERAL_LICENSES
        CollectionReference GeneralLicensesCollection = fs.collection(Tablas.generalLicencias);
        DocumentReference Cliente = GeneralLicensesCollection.document(l.getCODE());
        //Creando y llenando el documento Cliente
        Cliente.set(l.toMap());

        //agregando el primer dispositivo
        DevicesController.getInstance(context).RegisterDevice(l);

        if(first){

            //////////////////////////////////////////////////////////////////////
            //////////// JERARQUIA DE ROLES     /////////////////////////////////
            CollectionReference GeneralRolesCollection = fs.collection(Tablas.generalRoles);

            Roles su = new Roles("0","SU");
            Roles admin = new Roles("1","Administrador");
            Roles usuario = new Roles("2", "Usuario");

            GeneralRolesCollection.document(su.getCODE()).set(su);
            GeneralRolesCollection.document(admin.getCODE()).set(admin);
            GeneralRolesCollection.document(usuario.getCODE()).set(usuario);
        }
        /////////////////////////////////////////////////////////////////////
        //////////// JERARQUIA USUARIOS      ///////////////////////////////
        CollectionReference GeneralUsersCollection = fs.collection(Tablas.generalUsers);
        DocumentReference userLicense = GeneralUsersCollection.document(l.getCODE());
        userLicense.collection(Tablas.generalUsersUsers).add(new Users("Admin", "0", "admin1212345", "admin", "", "", true).toMap());
        UsersDevices ud = new UsersDevices();
        ud.setCODE(Funciones.generateCode());
        ud.setCODEDEVICE(Funciones.getPhoneID(context));
        ud.setCODEUSER("Admin");
        userLicense.collection(Tablas.generalUsersUsersDevices).add(ud);


    }


    public void updateLicense(Licenses l){
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        //creando documento con el key del nuevo cliente en la coleccion GENERAL_LICENSES
        CollectionReference GeneralLicensesCollection = fs.collection(Tablas.generalLicencias);
        DocumentReference Cliente = GeneralLicensesCollection.document(l.getCODE());
        //Creando y llenando el documento Cliente
        Cliente.set(l.toMap());
    }

    public void deleteLicence(final Licenses l){

            final FirebaseFirestore fs = FirebaseFirestore.getInstance();
            CollectionReference GeneralLicensesDevices = fs.collection(Tablas.generalLicenciasDevices);

            GeneralLicensesDevices.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    CollectionReference GeneralLicensesDevicesCollection = fs.collection(Tablas.generalLicenciasDevices);
                    CollectionReference GeneralLicensesCollection = fs.collection(Tablas.generalLicencias);
                    for(DocumentSnapshot ds: querySnapshot){

                    }

                    GeneralLicensesCollection.document(l.getCODE()).delete();
                }
            });



            /////////////////////////////////////////////////////////////////////
            //////////// JERARQUIA USUARIOS      ///////////////////////////////
            CollectionReference GeneralUsersCollection = fs.collection(Tablas.generalUsers);
            GeneralUsersCollection.document(l.getCODE()).delete();

    }




}
