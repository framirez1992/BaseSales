package com.far.basesales;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.far.basesales.DataBase.CloudFireStoreDB;
import com.far.basesales.Interfases.FireBaseOK;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.Date;

public class AdminConfiguration extends AppCompatActivity implements OnFailureListener, FireBaseOK {

    LinearLayout llCredentials, llConfiguration;
    Button btnOk, initializeLicense;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_configuration);
        init();
    }

    public void init(){
        llCredentials = findViewById(R.id.llCredentials);
        llConfiguration = findViewById(R.id.llConfiguration);
        btnOk = findViewById(R.id.btnOK);
        initializeLicense = findViewById(R.id.initializeLicense);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticate();
            }
        });

        initializeLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLicense();
            }
        });

    }

    public void autenticate(){
        String user = ((EditText)findViewById(R.id.etUser)).getText().toString();
        String pass = ((EditText)findViewById(R.id.etPass)).getText().toString();
        String date =Funciones.getFormatedDate(new Date()).replace(" ", "").replace(":", "");
        String p = date.substring(4,12);
        if(user.equals("AdminNimda") && pass.equals(p)){
            llCredentials.setVisibility(View.GONE);
            llConfiguration.setVisibility(View.VISIBLE);
        }
    }

    public void addLicense() {
        try {
            CloudFireStoreDB.getInstance(AdminConfiguration.this, AdminConfiguration.this, AdminConfiguration.this).crearNuevaEstructuraFireStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnFireBaseEndContact(int code) {
        if(code == 1){
            Toast.makeText(AdminConfiguration.this, "Finalizado", Toast.LENGTH_LONG).show();
           finish();

        }
    }

    @Override
    public void sendMessage(String message) {
        Toast.makeText(AdminConfiguration.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(AdminConfiguration.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
