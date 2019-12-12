package com.far.basesales;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.basesales.Utils.Funciones;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminCredentialsFragment extends Fragment {
    AdminConfiguration adminConfiguration;
    EditText etUser, etPass;
    TextView btnOk;

    public AdminCredentialsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_credentials, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void setAdminConfiguration(AdminConfiguration adminConfiguration){
        this.adminConfiguration = adminConfiguration;
    }
    public void init(View v){
        btnOk = v.findViewById(R.id.btnOK);
        etUser = v.findViewById(R.id.etUser);
        etPass = v.findViewById(R.id.etPass);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autenticate();
            }
        });
    }

    public void autenticate(){
        String user = etUser.getText().toString();
        String pass = etPass.getText().toString();
        String date = Funciones.getFormatedDate(new Date()).replace(" ", "").replace(":", "");
        String p = date.substring(4,12);
        if(user.equals("AdminNimda") && pass.equals(p)){
          adminConfiguration.showLicences();
        }
    }
}
