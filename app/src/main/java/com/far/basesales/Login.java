package com.far.basesales;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.bluetoothlibrary.BluetoothScan;
import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Token;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.TokenController;
import com.far.basesales.Controllers.Transaction;
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

import java.util.ArrayList;

public class Login extends AppCompatActivity implements OnFailureListener, FireBaseOK, AsyncExecutor {


    FirebaseFirestore db;
    LicenseController licenseController;
    DevicesController devicesController;
    UsersController usersController;

    Licenses license = null;
    Dialog cargaInicialDialog;
    LinearLayout llProgressBar;
    EditText etUser, etPassword;
    TextView btnLogin;
    CardView btnAceptar;
    EditText etUserDialog, etKeyDialog;
    TextView tvMessageDialog, tvPhoneID;


    TextView tvMsgToken;
    EditText etToken;
    CardView btnOKToken;
    LinearLayout llProgressBarToken;
    Dialog tokenDialog;

    Users lastUser;
    Devices lastDevice;
    UsersDevices usersDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getUngrantedPermissions().size()>0){
            requestPermissions(getUngrantedPermissions());
        }else{
            init();
            initDialog();
            if(Funciones.getPreferencesInt(Login.this, CODES.EXTRA_SECURITY_ERROR_CODE)>-1){
                int code = Funciones.getPreferencesInt(Login.this, CODES.EXTRA_SECURITY_ERROR_CODE);
                ((TextView)findViewById(R.id.tvErrorMsg)).setText(Funciones.gerErrorMessage(code));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(licenseController != null && licenseController.getLicense() == null) {
           Snackbar.make(findViewById(R.id.root), "Realize una carga inicial", Snackbar.LENGTH_LONG).show();
        }
    }

   /* public void startActivityLoginFromBegining(){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }*/

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
            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
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
            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
        }

    }



    public void showCargaInicialDialog() {
        cargaInicialDialog.show();
        Window window = cargaInicialDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
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

            if(querySnapshot == null || (querySnapshot!= null && querySnapshot.isEmpty())){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "Error de autenticacion", Snackbar.LENGTH_LONG).show();
                return;
            }
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null){
                    lastUser = document.toObject(Users.class);
                    usersController.delete(null, null);

                    if(!isValidUser(lastUser)){
                        btnLogin.setEnabled(true);
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        return;
                    }

                    DevicesController.getInstance(Login.this).getFindThisDeviceFromFireBase(licenseController.getLicense(), onSuccessDevice, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            btnLogin.setEnabled(true);
                            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    });
                    break;

                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo Usuario", Snackbar.LENGTH_LONG).show();
                }
            }

            btnLogin.setEnabled(true);
            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

        }

    };


    public boolean validateDevice(Devices d){

        int code = devicesController.validateDevice(d);

        if(code == CODES.CODE_DEVICES_UNREGISTERED){
            Snackbar.make(findViewById(R.id.root), "Dispositivo no registrado. Contacte con el administrador", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(Login.this, "Dispositivo no registrado. Contacte con el administrador", Toast.LENGTH_LONG).show();
            //startActivityLoginFromBegining();
            return false;
        }

        if(code == CODES.CODE_DEVICES_DISABLED){
            Snackbar.make(findViewById(R.id.root), "Dispositivo inactivo. Contacte con el administrador", Snackbar.LENGTH_LONG).show();
            //Toast.makeText(Login.this, "Dispositivo inactivo. Contacte con el administrador", Toast.LENGTH_LONG).show();
            //startActivityLoginFromBegining();
            return false;
        }

        return true;
    }
    public boolean isValidUser(Users u){
        int code = usersController.validateUser(u);
        if(code != CODES.CODE_USERS_ENABLED){

            if(code == CODES.CODE_USERS_DISBLED){
                Snackbar.make(findViewById(R.id.root), "Usuario inactivo. Contacte con el administrador", Snackbar.LENGTH_LONG).show();
            }

            if(code == CODES.CODE_USERS_INVALID){
                Snackbar.make(findViewById(R.id.root), "Usuario deshabilitado. Contacte con el administrador", Snackbar.LENGTH_LONG).show();

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



    public int checkPermissions(String permission){
        int check = ContextCompat.checkSelfPermission(Login.this, permission);
        return check;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        if(requestCode == 123){
            for(int result: grantResults){
               if(result == PackageManager.PERMISSION_DENIED){
                   granted = false;
                   break;
               }
            }
            if(granted){
                recreate();
            }else{
                finish();
            }
        }
    }


    public void initDialog(){
        try {
            cargaInicialDialog = new Dialog(Login.this);
            cargaInicialDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            tokenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            Window window = tokenDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
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
                            //UsersController.getInstance(Login.this).getQueryUsersByCode(license,etUserDialog.getText().toString(),onSuccessUsers, onComplete,Login.this);
                            DevicesController.getInstance(Login.this).getFindThisDeviceFromFireBase(license, onSuccessDeviceCargaInicial,Login.this);
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
    public OnSuccessListener<QuerySnapshot> onSuccessUsersCargaInicial = new OnSuccessListener<QuerySnapshot>() {
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
                UsersDevicesController.getInstance(Login.this).getQueryusersDevices(license,u.getCODE(),Funciones.getPhoneID(Login.this),onSuccessUsersDevicesCargaInicial,onComplete,Login.this);
            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessDeviceCargaInicial = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            Devices d = null;
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                d = doc.toObject(Devices.class);
                if(!d.isENABLED()){
                    setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_DEVICES_DISABLED), R.color.red_700);
                    endLoading();
                }else{
                    UsersController.getInstance(Login.this).getQueryUsersByCode(license,etUserDialog.getText().toString(),onSuccessUsersCargaInicial, onComplete,Login.this);
                }
            }else{
                    setMessageCargaInicial(Funciones.gerErrorMessage(CODES.CODE_DEVICES_UNREGISTERED), R.color.red_700);
                    endLoading();
            }
        }
    };



    OnSuccessListener<QuerySnapshot> onSuccessDevice = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot == null || (querySnapshot != null && querySnapshot.isEmpty()) ){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "Dispositivo no autorizado", Snackbar.LENGTH_LONG).show();
                return;
            }

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null ){
                    lastDevice = document.toObject(Devices.class);
                    devicesController.delete(null, null);

                    if(!validateDevice(lastDevice)){
                        lastUser = null;
                        btnLogin.setEnabled(true);
                        findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                        return;
                    }

                    UsersDevicesController.getInstance(Login.this).getUserDeviceFromFireBase(licenseController.getLicense(), lastUser.getCODE(), lastDevice.getCODE(), onSuccessUserDevice, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            btnLogin.setEnabled(true);
                            Snackbar.make(findViewById(R.id.root), e.getMessage(), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    });



                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo Device", Snackbar.LENGTH_LONG).show();
                }
            }

            //btnLogin.setEnabled(true);
            // findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

        }

    };



    OnSuccessListener<QuerySnapshot> onSuccessUserDevice = new OnSuccessListener<QuerySnapshot>() {

        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot == null || (querySnapshot != null && querySnapshot.isEmpty()) ){
                btnLogin.setEnabled(true);
                findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                Snackbar.make(findViewById(R.id.root), "El dispositivo no esta asignado al usuario", Snackbar.LENGTH_LONG).show();
                return;
            }

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                if(document != null){
                    UsersDevices ud = document.toObject(UsersDevices.class);

                    String lastLicenseCodeSaved = Funciones.getCodeLicense(Login.this);
                    String lastSavedMacAddress = Funciones.getMacAddress(Login.this);
                    if(lastLicenseCodeSaved.isEmpty()){
                        Snackbar.make(findViewById(R.id.root), "Realize una carga inicial. No se encontro licencia", Snackbar.LENGTH_LONG).show();
                    }else{
                        Funciones.clearPreference(Login.this);
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_LICENSE_CODE, lastLicenseCodeSaved);
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_CODE, lastUser.getCODE());
                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_USERSKEY_USERTYPE, lastUser.getROLE());

                        Funciones.savePreferences(Login.this, CODES.PREFERENCE_BLUETOOTH_MAC_ADDRESS, lastSavedMacAddress);
                        ((TextView)findViewById(R.id.tvErrorMsg)).setText("");

                        UsersController.getInstance(Login.this).insert(lastUser);
                        DevicesController.getInstance(Login.this).insert(lastDevice);


                        Intent i = new Intent(Login.this, MainActivity.class);
                        startActivity(i);
                    }


                }else{
                    Snackbar.make(findViewById(R.id.root), "ERROR obteniendo UserDevice", Snackbar.LENGTH_LONG).show();
                }
            }

            btnLogin.setEnabled(true);
            findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);

        }

    };


    public OnSuccessListener<QuerySnapshot> onSuccessUsersDevicesCargaInicial = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                UsersDevices ud = doc.toObject(UsersDevices.class);
                //devicesController.getDevices(license, DevicesValidationListener);
                CloudFireStoreDB.getInstance(Login.this, Login.this, Login.this).CargaInicial(license/*,  registerDevice*/);
                setMessageCargaInicial("Cargando datos...", android.R.color.black);
                return;
            }
            setMessageCargaInicial("Este dispositivo no esta asociado al usuario", R.color.red_700);
            endLoading();
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessToken = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Token t = doc.toObject(Token.class);
                if(t.isAutodelete()){
                    TokenController.getInstance(Login.this).deleteFromFireBase(t);
                }

                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED, "");
                Funciones.savePreferences(Login.this, CODES.EXTRA_SECURITY_ERROR_CODE, -1);
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, "");
                Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, "");
                ((TextView)findViewById(R.id.tvErrorMsg)).setText("");
                tokenDialog.dismiss();
                recreate();
                return;
            }
            String intentos = getTokenAttemps();
            intentos = String.valueOf(Integer.parseInt(intentos)+1);
            Funciones.savePreferences(Login.this, CODES.PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS, intentos);

            endLoadingToken();
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



    public ArrayList<String> getUngrantedPermissions(){
        ArrayList<String> p = new ArrayList<>();
        if(checkPermissions(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
         p.add(Manifest.permission.INTERNET);
        }
        if(checkPermissions(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(checkPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(checkPermissions(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        /*if(checkPermissions(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }*/
        if(checkPermissions(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.BLUETOOTH);
        }
        if(checkPermissions(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
            p.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        return p;
    }

    public void requestPermissions(ArrayList<String> permissions){
        ActivityCompat.requestPermissions(Login.this,
               /* new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN}*/permissions.toArray(new String[permissions.size()]),
                123);
    }

}
