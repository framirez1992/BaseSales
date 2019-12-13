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

import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Controllers.RolesController;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;


public class UsersDialogFragment extends DialogFragment implements OnFailureListener {

    private static  Object tempObj;

    LinearLayout llSave;
    TextInputEditText etName, etPassword, etPassword2, etCode;
    Spinner /*spnLevel,*/ spnRol;
    CheckBox cbEnabled;

    UsersController usersController;
    UserTypesController userTypesController;

    public  static UsersDialogFragment newInstance(Object pt) {

        tempObj = pt;
        UsersDialogFragment f = new UsersDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(pt != null) {
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
        usersController = UsersController.getInstance(getActivity());
        userTypesController = UserTypesController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_add_edit_users, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

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
        etPassword2 = view.findViewById(R.id.etPassword2);
        spnRol = view.findViewById(R.id.spnRole);
        //spnLevel = view.findViewById(R.id.spnLevel);
        cbEnabled = view.findViewById(R.id.cbEnabled);

        userTypesController.fillSpnUserTypes(spnRol,false);
        //RolesController.getInstance(getActivity()).fillGeneralRoles(spnLevel);

        //etCode.setEnabled(false);
        //etCode.setText(UUID.randomUUID().toString());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    Edit();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditUsers();
        }
    }

    public boolean validate(){
        if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "El codigo de usuario es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(tempObj == null && usersController.getUserByCode(etCode.getText().toString()) != null){
            Snackbar.make(getView(), "Ya existe el codigo de usuario", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etPassword.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Debe escribir una contraseña", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(!etPassword.getText().toString().trim().equals(etPassword2.getText().toString().trim())){
            Snackbar.make(getView(), "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnRol.getSelectedItem()== null){
            Snackbar.make(getView(), "Especifique un rol", Snackbar.LENGTH_SHORT).show();
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
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveUser(){
        try {
            String code = etCode.getText().toString();
            String systemCode = CODES.USER_SYSTEM_CODE_USER;/*((KV)spnLevel.getSelectedItem()).getKey();*/
            String userName = etName.getText().toString();
            String password = etPassword.getText().toString().trim();
            boolean enabled = cbEnabled.isChecked();
            String role = ((KV)spnRol.getSelectedItem()).getKey();
            String empresa = "01";
            Users users = new Users(code,systemCode, password, userName, role, empresa, enabled);
            usersController.sendToFireBase(users);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void Edit(){
        if(validate()) {
            EditUser();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void EditUser(){
        try {
            Users user = ((Users)tempObj);
            user.setUSERNAME(etName.getText().toString());
            user.setPASSWORD(etPassword.getText().toString().trim());
            user.setCOMPANY("01");
            user.setENABLED(user.getCODE().equals(Funciones.getCodeuserLogged(getActivity()))?true:cbEnabled.isChecked());//Para que es mismo usuario no se deshabilite
            user.setROLE(((KV)spnRol.getSelectedItem()).getKey());
            user.setMDATE(null);

            usersController.sendToFireBase(user);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditUsers(){
        Users u = ((Users)tempObj);
        etCode.setText(u.getCODE());
        etCode.setEnabled(false);
        etName.setText(u.getUSERNAME());
        etPassword.setText(u.getPASSWORD());
        etPassword2.setText(u.getPASSWORD());
        setRolePosition(u.getROLE());
        cbEnabled.setChecked(u.isENABLED());

    }

    public void setRolePosition(String key){
        for(int i = 0; i< spnRol.getAdapter().getCount(); i++){
            if(((KV)spnRol.getAdapter().getItem(i)).getKey().equals(key)){
                spnRol.setSelection(i);
                break;
            }
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
