package com.far.basesales;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Token;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.TokenController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Controllers.UsersDevicesController;
import com.far.basesales.DataBase.CloudFireStoreDB;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.AsyncExecutor;
import com.far.basesales.Interfases.FireBaseOK;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity implements OnFailureListener, FireBaseOK, AsyncExecutor {


    FirebaseFirestore db;
    LicenseController licenseController;
    DevicesController devicesController;
    UsersController usersController;

    Licenses license = null;
    Dialog cargaInicialDialog;
    LinearLayout llProgressBar;
    EditText etUser, etPassword;
    Button btnLogin, btnAceptar;
    EditText etUserDialog, etKeyDialog;
    TextView tvMessageDialog, tvPhoneID;


    TextView tvMsgToken;
    EditText etToken;
    Button btnOKToken;
    LinearLayout llProgressBarToken;
    Dialog tokenDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initDialog();
        if(getIntent().getExtras()!= null && getIntent().getExtras().containsKey(CODES.EXTRA_SECURITY_ERROR_CODE)){
            int code = getIntent().getExtras().getInt(CODES.EXTRA_SECURITY_ERROR_CODE);
            ((TextView)findViewById(R.id.tvErrorMsg)).setText(Funciones.gerErrorMessage(code));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(licenseController.getLicense() == null) {
           Snackbar.make(findViewById(R.id.root), "Realize una carga inicial", Snackbar.LENGTH_LONG).show();
        }

        if(Funciones.getPreferencesInt(Login.this, CODES.PREFERENCE_SCREEN_HEIGHT) <=0){
            Funciones.saveScreenMetrics(Login.this);
        }
    }

    public void startActivityLoginFromBegining(){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        new MenuInflater(Login.this).inflate(R.menu.main_menu, menu);
        boolean loginBloqued = Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED).equals("1");
        menu.findItem(R.id.token).setVisible(loginBloqued);
        menu.findItem(R.id.initialize).setVisible(!loginBloqued);
        return (super.onCreateOptionsMenu(menu));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.initialize:
                showCargaInicialDialog();
                return true;
            case R.id.token:
                showTokenDialog();
                return true;
            case R.id.configuration:
                goToConfiguration();
                return true;

        }
        return false;
    }

    public void init() {
        try {

            FirebaseApp.initializeApp(Login.this);
            db = FirebaseFirestore.getInstance();
            licenseController = LicenseController.getInstance(Login.this);
            devicesController = DevicesController.getInstance(Login.this);
            usersController = UsersController.getInstance(Login.this);



            btnLogin = findViewById(R.id.btnLogin);
            tvPhoneID = findViewById(R.id.tvPhoneID);
            etUser = findViewById(R.id.etUser);
            etPassword = findViewById(R.id.etPass);

        } catch (Exception e) {
            e.printStackTrace();
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED).equals("1")) {
                        AlertDialog a = new AlertDialog.Builder(Login.this).create();
                        a.setTitle("Alerta");
                        a.setMessage(Funciones.gerErrorMessage(Integer.parseInt(Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON))));
                        a.show();
                    } else {
                        login();
                    }
            }
        });

        showPhoneID();
    }

    public void login() {
        try {
            findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);

            Licenses l = licenseController.getLicense();
            if(l != null){
                usersController.getUserFromFireBase(etUser.getText().toString(), etPassword.getText().toString())
                        .addOnSuccessListener(onSuccessListenerLogin).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        btnLogin.setEnabled(true);
                        Snackbar.make(findViewById(R.id.root), e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                        return;
                    }
                });

            }else{
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), Funciones.gerErrorMessage(CODES.CODE_LICENSE_NO_LICENSE), Snackbar.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void showCargaInicialDialog() {
        cargaInicialDialog.show();
    }

    public void startLoading(){
        license = null;
        tvMessageDialog.setText("");
        llProgressBar.setVisibility(View.VISIBLE);
        etKeyDialog.setEnabled(false);
        etUserDialog.setEnabled(false);
        cargaInicialDialog.setCancelable(false);
        btnAceptar.setEnabled(false);
    }
    public void endLoading(){
        btnAceptar.setEnabled(true);
        llProgressBar.setVisibility(View.INVISIBLE);
        etKeyDialog.setEnabled(true);
        etUserDialog.setEnabled(true);
        cargaInicialDialog.setCancelable(true);
    }

    public void startLoadingToken(){
        String intentos = getTokenAttemps();
        tvMsgToken.setText("Intentos: "+intentos+"/3");
        llProgressBarToken.setVisibility(View.VISIBLE);
        etToken.setEnabled(false);
        tokenDialog.setCancelable(false);
        btnOKToken.setEnabled(false);
    }
    public void endLoadingToken(){

        llProgressBarToken.setVisibility(View.INVISIBLE);
        tokenDialog.setCancelable(true);
        String intentos = getTokenAttemps();
        if(Integer.parseInt(intentos) >= 3){
            btnOKToken.setEnabled(false);
            etToken.setEnabled(false);
            tvMsgToken.setText("Agoto el numero de intentos permitidos");
        }else{
            tvMsgToken.setText("Intentos: "+intentos+"/3");
            btnOKToken.setEnabled(true);
            etToken.setEnabled(true);
        }
    }

    public void setMessageCargaInicial(String message){
        setMessageCargaInicial(message, android.R.color.black);
    }
    public void setMessageCargaInicial(String message, int color){
        tvMessageDialog.setText(message);
        tvMessageDialog.setTextColor(getResources().getColor(color));
    }


    @Override
    public void OnFireBaseEndContact(int code) {
        if(code == 1){
            Toast.makeText(Login.this, "Finalizado", Toast.LENGTH_LONG).show();
            endLoading();
            tvMessageDialog.setText("Finalizado");
            cargaInicialDialog.dismiss();
            //Funciones.getDateOnline(Login.this);
            recreate();

        }
    }

    @Override
    public void sendMessage(String message) {
        tvMessageDialog.setText(message);
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        endLoading();
        setMessageCargaInicial(e.getMessage(),R.color.red_700);
    }

    OnSuccessListener<QuerySnapshot> onSuccessListenerLogin = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot == null ){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "Error de autenticacion", Snackbar.LENGTH_LONG).show();
                return;
            }
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null){
                    Users u = document.toObject(Users.class);
                    usersController.delete(null, null);
                    usersController.insert(u);

                    if(!isValidUser(u)){
                        btnLogin.setEnabled(true);
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        return;
                    }

                    String codeUser = u.getCODE();
                    Funciones.clearPreference(Login.this);
                    Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_CODE, codeUser);
                    Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_USERTYPE, UsersController.getInstance(Login.this).getUserByCode(codeUser).getROLE());
                    ((TextView)findViewById(R.id.tvErrorMsg)).setText("");

                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);

                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo Usuario", Snackbar.LENGTH_LONG).show();
                }
            }

            btnLogin.setEnabled(true);
            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

        }

    };


    public boolean validateDevice(){

        int code = devicesController.validateDevice();

        if(code == CODES.CODE_DEVICES_UNREGISTERED){
            Toast.makeText(Login.this, "Dispositivo no registrado. Contacte con el administrador", Toast.LENGTH_LONG).show();
            startActivityLoginFromBegining();
            return false;
        }

        if(code == CODES.CODE_DEVICES_DISABLED){
            Toast.makeText(Login.this, "Dispositivo inactivo. Contacte con el administrador", Toast.LENGTH_LONG).show();
            startActivityLoginFromBegining();
            return false;
        }

        return true;
    }

    public boolean isValidUser(Users u){
        int code = usersController.validateUser(u);
        if(code != CODES.CODE_USERS_ENABLED){

            if(code == CODES.CODE_USERS_DISBLED){
                Toast.makeText(Login.this, "Usuario inactivo. Contacte con el administrador", Toast.LENGTH_LONG).show();
            }

            if(code == CODES.CODE_USERS_INVALID){
                Toast.makeText(Login.this, "Usuario deshabilitado. Contacte con el administrador", Toast.LENGTH_LONG).show();

            }
            return false;
        }

        return true;
    }

    public boolean validateUserCargaInicial(Users u){

        int code = (u != null)?usersController.validateUser(usersController.getUserByCode(u.getCODE())):CODES.CODE_USERS_INVALID;

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            setMessageCargaInicial(Funciones.gerErrorMessage(code), R.color.red_700);
            endLoading();
            return false;
        }

        return true;
    }




    @Override
    public void setMessage(String fechaActual) {


    }
    public void showPhoneID(){
        tvPhoneID.setText("Device: "+Funciones.getPhoneID(Login.this));
    }
    public void agregarAlServer(){

    }
    public void filtroConWhere(){

    }

    public void colocandoMarcaDeTiempo(){

    }

    public void Transaccciones(){

    }



    public int checkPermissions(String permission){
        int check = ContextCompat.checkSelfPermission(Login.this, permission);
        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){

            if (grantResults.length > 0) {

                boolean granted = true;
                for (int grantResult : grantResults){
                    if (grantResult == PackageManager.PERMISSION_DENIED){
                        granted = false;
                    }
                }
                if(granted){
                    //Funciones.sendSMS("8099983580", "hola vato");
                }else {
                    Toast.makeText(Login.this, "Denegado", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void initDialog(){
        try {
            cargaInicialDialog = new Dialog(Login.this);
            cargaInicialDialog.setContentView(R.layout.dialog_2edit_button);
            llProgressBar = cargaInicialDialog.findViewById(R.id.llProgress);
            etKeyDialog = cargaInicialDialog.findViewById(R.id.etKey);
            etUserDialog = cargaInicialDialog.findViewById(R.id.etUser);
            btnAceptar = cargaInicialDialog.findViewById(R.id.btnCargaInicial);
            tvMessageDialog = cargaInicialDialog.findViewById(R.id.tvMessage);
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (etKeyDialog.getText().toString().trim().isEmpty() || etUserDialog.getText().toString().trim().isEmpty()) {
                            setMessageCargaInicial("Debe llenar los campos KEY y USER");
                            return;
                        }
                        startLoading();
                        licenseController.getDataFromFireBase(etKeyDialog.getText().toString(), LicenceListener, Login.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showTokenDialog(){
        try {
            tokenDialog = new Dialog(Login.this);
            tokenDialog.setContentView(R.layout.dialog_edit_button);
            tvMsgToken = tokenDialog.findViewById(R.id.tvMessage);
            etToken = tokenDialog.findViewById(R.id.etValue);
            btnOKToken = tokenDialog.findViewById(R.id.btnOK);
            llProgressBarToken = tokenDialog.findViewById(R.id.llProgress);
            etToken.setHint("Token");
            etToken.setText("");
            String intentos = getTokenAttemps();
            if (Integer.parseInt(intentos) >= 3) {
                btnOKToken.setEnabled(false);
                etToken.setEnabled(false);
                tvMsgToken.setText("Agoto el numero de intentos permitidos");
            } else {
                tvMsgToken.setText("Intentos: " + intentos + "/3");
            }


            btnOKToken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = etToken.getText().toString();
                    if (token.equals("")) {
                        return;
                    }
                    if (Integer.parseInt(getTokenAttemps()) >= 3) {
                        endLoadingToken();
                        return;
                    }
                    startLoadingToken();
                    TokenController.getInstance(Login.this).getQueryTokenByCode(token, onSuccessToken, onCompleteToken, onFailureToken);
                }
            });

            tokenDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getTokenAttemps(){
        String intentos = Funciones.getPreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS);
        if(intentos.equals(""))
            intentos = "0";
        return intentos;
    }


    public OnSuccessListener<DocumentSnapshot> LicenceListener = new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if(documentSnapshot.exists()){
                try {
                    license = documentSnapshot.toObject(Licenses.class);
                    licenseController.delete(null, null);
                    licenseController.insert(license);
                    int code = licenseController.validateLicense(license);
                    String msg = ""; int color = R.color.red_700;
                    switch (code){
                        case  CODES.CODE_LICENSE_EXPIRED: msg =Funciones.gerErrorMessage(CODES.CODE_LICENSE_EXPIRED); endLoading(); break;
                        case  CODES.CODE_LICENSE_DISABLED: msg = Funciones.gerErrorMessage(CODES.CODE_LICENSE_DISABLED);  endLoading(); break;
                        case  CODES.CODE_LICENSE_VALID:
                            color = android.R.color.black;
                            UsersController.getInstance(Login.this).getQueryUsersByCode(license,etUserDialog.getText().toString(),onSuccessUsers, onComplete,Login.this);
                            break;
                        default:msg = Funciones.gerErrorMessage(CODES.CODE_LICENSE_INVALID);  endLoading(); break;
                    }
                    setMessageCargaInicial(msg, color);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return;
            }else{
                setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_LICENSE_INVALID), R.color.red_700);
                endLoading();
            }
        }
    };
    public OnSuccessListener<QuerySnapshot> onSuccessUsers = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            Users u = null;
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                u = doc.toObject(Users.class);
                usersController.delete(null, null);
                usersController.insert(u);

            }

            if (validateUserCargaInicial(u)) {
                UsersDevicesController.getInstance(Login.this).getQueryusersDevices(license,u.getCODE(),Funciones.getPhoneID(Login.this),onSuccessUsersDevices,onComplete,Login.this);
            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessUsersDevices = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                UsersDevices ud = doc.toObject(UsersDevices.class);
                devicesController.getDevices(license, DevicesListener);
                return;
            }
            setMessageCargaInicial("Este dispositivo no esta asociado al usuario", R.color.red_700);
            endLoading();
        }
    };

    public OnSuccessListener<QuerySnapshot> DevicesListener = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            boolean registrado = false;
            boolean enabled = true;
            int devicesCount = querySnapshot.getDocuments().size();//registrados
            String phoneID = Funciones.getPhoneID(Login.this);
            for(DocumentSnapshot doc : querySnapshot){
                Devices dev = doc.toObject(Devices.class);
                if(dev.getCODE().equals(phoneID)){
                    enabled = dev.isENABLED();
                    registrado = true;
                    break;
                }
            }

            if(registrado && !enabled){//Dispositivo registrado e inactivo
                setMessageCargaInicial("Dispositivo inactivo. contacte con el administrador.", R.color.red_700);
                endLoading();
                return;
            }

            if (!registrado && (devicesCount >= license.getDEVICES())) {
                setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_LICENSE_DEVICES_LIMIT_REACHED),  R.color.red_700);
                endLoading();
                return;
            } else if (registrado || (!registrado && (devicesCount < license.getDEVICES()))) {
                boolean registerDevice =  (!registrado && (devicesCount < license.getDEVICES()));
                CloudFireStoreDB.getInstance(Login.this, Login.this, Login.this).CargaInicial(license,  registerDevice);
                setMessageCargaInicial("Cargando datos...", android.R.color.black);
            }
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessToken = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Token t = doc.toObject(Token.class);
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED, "");
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, "");
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, "");

                Licenses actualLicence = LicenseController.getInstance(Login.this).getLicense();
                LicenseController.getInstance(Login.this).getQueryLicenceByCode(actualLicence.getCODE(), onSuccessLicence, onCompleteToken, onFailureToken);
                //TokenController.getInstance(Login.this).deleteFromFireBase(t);
                return;
            }
            String intentos = getTokenAttemps();
            intentos = String.valueOf(Integer.parseInt(intentos)+1);
            Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, intentos);

            endLoadingToken();
            //setMessageCargaInicial("Invalid User", R.color.red_700);
            //endLoading();
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessLicence = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Licenses l = doc.toObject(Licenses.class);
                LicenseController.getInstance(Login.this).delete(null, null);
                LicenseController.getInstance(Login.this).insert(l);
                recreate();
                return;
            }
        }
    };

    public OnCompleteListener onComplete = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            //Fin de query
            if(task.getException() != null){
                tvMessageDialog.setText(task.getException().getMessage().toString());
                endLoading();
            }
        }
    };

    public OnCompleteListener onCompleteToken = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            //Fin de query
            if(task.getException() != null){
                tvMsgToken.setText(task.getException().getMessage().toString());
                endLoading();
            }
        }
    };

    public OnFailureListener onFailureToken = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            tvMsgToken.setText(e.getMessage().toString());
        }
    };

    public void goToConfiguration(){
        startActivity(new Intent(Login.this, AdminConfiguration.class));
    }
}
