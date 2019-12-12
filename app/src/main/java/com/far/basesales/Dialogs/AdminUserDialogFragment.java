package com.far.basesales.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.basesales.AdminLicenseUsers;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Controllers.RolesController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class AdminUserDialogFragment extends DialogFragment implements OnFailureListener {

    AdminLicenseUsers adminLicenseUsers;
    public Users tempObj;
    public String codeLicense;

    LinearLayout llSave;
    TextInputEditText etName, etPassword, etCode;
    Spinner spnLevel;
    CheckBox cbEnabled;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static AdminUserDialogFragment newInstance(AdminLicenseUsers adminLicenseUsers, Users users, String codeLicense) {

        AdminUserDialogFragment f = new AdminUserDialogFragment();
        f.adminLicenseUsers = adminLicenseUsers;
        f.tempObj = users;
        f.codeLicense = codeLicense;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(users != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admin_user_dialog_fragment, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        llSave = view.findViewById(R.id.llSave);
        etCode = view.findViewById(R.id.etCode);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        spnLevel = view.findViewById(R.id.spnLevel);
        cbEnabled = view.findViewById(R.id.cbEnabled);

        RolesController.getInstance(getActivity()).fillGeneralRolesLocal(spnLevel);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditLicense();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validate(){
        if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "El codigo de usuario es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(tempObj == null && adminLicenseUsers.getUserByCode(etCode.getText().toString()) != null){
            Snackbar.make(getView(), "Ya existe el codigo de usuario", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etPassword.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Debe escribir una contraseña", Snackbar.LENGTH_SHORT).show();
            return false;
        }if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validate()) {
            SaveUser();
        }

        llSave.setEnabled(true);

    }

    public void SaveUser(){
        try {
            String code = etCode.getText().toString();
            String systemCode = ((KV)spnLevel.getSelectedItem()).getKey();
            String userName = etName.getText().toString();
            String password = etPassword.getText().toString().trim();
            boolean enabled = cbEnabled.isChecked();

            Users u = new Users(code,systemCode, password, userName, enabled);


            adminLicenseUsers.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersUsers).document(u.getCODE()).set(u.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismiss();
                        }
                    }).addOnFailureListener(this);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void EditLicense(){
        try {
            Users u = tempObj;
            u.setUSERNAME(etName.getText().toString());
            u.setPASSWORD(etPassword.getText().toString().trim());
            u.setENABLED(cbEnabled.isChecked());
            u.setMDATE(null);

            adminLicenseUsers.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersUsers)
                    .document(u.getCODE()).update(tempObj.toMap())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dismiss();
                        }
                    }).addOnFailureListener(this);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){

        Users u = ((Users)tempObj);
        etCode.setText(u.getCODE());
        etCode.setEnabled(false);
        etName.setText(u.getUSERNAME());
        etPassword.setText(u.getPASSWORD());
        setLevelPosition(u.getSYSTEMCODE());
        cbEnabled.setChecked(u.isENABLED());

    }


    public void setLevelPosition(String key){
        for(int i = 0; i< spnLevel.getAdapter().getCount(); i++){
            if(((KV)spnLevel.getAdapter().getItem(i)).getKey().equals(key)){
                spnLevel.setSelection(i);
                break;
            }
        }
    }


    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }



}
