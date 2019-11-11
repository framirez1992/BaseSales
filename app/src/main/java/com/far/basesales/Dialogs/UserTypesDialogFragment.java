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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.far.basesales.CloudFireStoreObjects.UserTypes;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.UUID;


public class UserTypesDialogFragment extends DialogFragment implements OnFailureListener {

    private static  Object tempObj;

    LinearLayout llSave;
    TextInputEditText etName;

    UserTypesController userTypesController;

    public  static UserTypesDialogFragment newInstance(Object pt) {

        tempObj = pt;
        UserTypesDialogFragment f = new UserTypesDialogFragment();

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
        userTypesController = UserTypesController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.product_type_fragment_dialog, container, true);
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
        etName = view.findViewById(R.id.etName);
        ((EditText)view.findViewById(R.id.etOrden)).setVisibility(View.GONE);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditUserType();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditMeasureUnits();
        }
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveUserType();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveUserType(){
        try {
            String code = UUID.randomUUID().toString();
            String name = etName.getText().toString();
            int orden = userTypesController.getNextOrden();
            UserTypes pt = new UserTypes(code, name, orden);
            userTypesController.sendToFireBase(pt);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditUserType(){
        try {
            UserTypes mu = ((UserTypes)tempObj);
            mu.setDESCRIPTION(etName.getText().toString());
            mu.setMDATE(null);
            userTypesController.sendToFireBase(mu);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditMeasureUnits(){
        etName.setText(((UserTypes)tempObj).getDESCRIPTION());

    }


    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
