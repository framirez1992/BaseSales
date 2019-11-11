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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.Controllers.CompanyController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;


public class UserControlDialogFragment extends DialogFragment implements OnFailureListener {

    private static  Object tempObj;

    LinearLayout llSave;
    TextInputEditText etControl, etValue;
    Spinner spnTarget, spnDestiny;
    CheckBox cbEnabled;
    boolean firtTime = true;

    UserControlController userControlController;

    public  static UserControlDialogFragment newInstance(Object pt) {

        tempObj = pt;
        UserControlDialogFragment f = new UserControlDialogFragment();

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
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
        userControlController = UserControlController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etControl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_add_edit_usercontrol, container, true);
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
        etControl = view.findViewById(R.id.etControl);
        etValue = view.findViewById(R.id.etValue);
        spnDestiny = view.findViewById(R.id.spnDestiny);
        spnTarget = view.findViewById(R.id.spnTarget);
        cbEnabled = view.findViewById(R.id.cbEnabled);

        userControlController.fillSpinnerControlLevels(spnTarget, false);
        spnTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV kv = (KV)parent.getItemAtPosition(position);
                switch (kv.getKey()){
                    case CODES.USERSCONTROL_TARGET_COMPANY:
                        CompanyController.getInstance(getActivity()).fillSpnCompany(spnDestiny, false); break;
                    case CODES.USERSCONTROL_TARGET_USER_ROL:
                        UserTypesController.getInstance(getActivity()).fillSpnUserTypes(spnDestiny, false); break;
                    case CODES.USERSCONTROL_TARGET_USER:
                        UsersController.getInstance(getActivity()).fillSpnUser(spnDestiny, false);  break;
                }
                if(firtTime && tempObj != null){
                    firtTime = false;
                    setDestinyPosition(((UserControl)tempObj).getTARGETCODE());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //etCode.setEnabled(false);
        //etCode.setText(UUID.randomUUID().toString());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditUserControl();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditUsers();
        }
    }

    public boolean validate(){
        if(etControl.getText().toString().trim().equals("")/* y no existe*/){
            Snackbar.make(getView(), "El control  es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etValue.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "El valor es obligatorio", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnTarget.getSelectedItem()== null){
            Snackbar.make(getView(), "Especifique un target", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnDestiny.getSelectedItem()== null){
        Snackbar.make(getView(), "Especifique un destiny", Snackbar.LENGTH_SHORT).show();
        return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveUserControl();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveUserControl(){
        try {
            String code = Funciones.generateCode();
            String control = etControl.getText().toString();
            String target = ((KV)spnTarget.getSelectedItem()).getKey();
            String destiny = ((KV)spnDestiny.getSelectedItem()).getKey();
            String value = etValue.getText().toString();
            boolean enabled = cbEnabled.isChecked();
            UserControl userControl = new UserControl(code,target, destiny, control, value,  enabled);
            userControlController.sendToFireBase(userControl);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditUserControl(){
        try {
            UserControl userControl = ((UserControl)tempObj);
            userControl.setVALUE(etValue.getText().toString());
            userControl.setTARGET(((KV)spnTarget.getSelectedItem()).getKey());
            userControl.setTARGETCODE(((KV)spnDestiny.getSelectedItem()).getKey());
            userControl.setACTIVE(cbEnabled.isChecked());
            userControl.setMDATE(null);

            userControlController.sendToFireBase(userControl);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditUsers(){
        UserControl u = ((UserControl)tempObj);
        etControl.setText(u.getCONTROL());
        etControl.setEnabled(false);
        etValue.setText(u.getVALUE());
        setTargetPosition(u.getTARGET());
        cbEnabled.setChecked(u.getACTIVE());

    }

    public void setTargetPosition(String key){
        for(int i = 0; i< spnTarget.getAdapter().getCount(); i++){
            if(((KV)spnTarget.getAdapter().getItem(i)).getKey().equals(key)){
                spnTarget.setSelection(i);
                break;
            }
        }
    }

    public void setDestinyPosition(String key){
        for(int i = 0; i< spnDestiny.getAdapter().getCount(); i++){
            if(((KV)spnDestiny.getAdapter().getItem(i)).getKey().equals(key)){
                spnDestiny.setSelection(i);
                break;
            }
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
