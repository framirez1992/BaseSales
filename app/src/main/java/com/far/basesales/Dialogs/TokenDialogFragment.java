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

import com.far.basesales.AdminLicenseTokens;
import com.far.basesales.CloudFireStoreObjects.Token;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class TokenDialogFragment extends DialogFragment implements OnFailureListener {

    AdminLicenseTokens adminLicenseTokens;
    public Token tempObj;
    public String codeLicense;

    LinearLayout llSave;
    TextInputEditText etCode;
    CheckBox cbAutoDelete;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static TokenDialogFragment newInstance(AdminLicenseTokens adminLicenseTokens, Token token, String codeLicense) {

        TokenDialogFragment f = new TokenDialogFragment();
        f.adminLicenseTokens = adminLicenseTokens;
        f.tempObj = token;
        f.codeLicense = codeLicense;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(token != null) {
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
        return inflater.inflate(R.layout.dialog_add_edit_token, container, true);
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
        cbAutoDelete = view.findViewById(R.id.cbAutoDelete);

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

    public boolean validateProductType(){
        if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un codigo", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveProductType();
        }

        llSave.setEnabled(true);

    }

    public void SaveProductType(){
        try {
            String code = etCode.getText().toString();
            Token t = new Token(code, cbAutoDelete.isChecked());

            adminLicenseTokens.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersToken).document(t.getCODE()).set(t.toMap())
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
            String code = etCode.getText().toString();

            tempObj.setCODE(code);
            tempObj.setAutodelete(cbAutoDelete.isChecked());

            adminLicenseTokens.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersToken)
                    .document(code).update(tempObj.toMap())
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
        etCode.setText(tempObj.getCODE());
        etCode.setEnabled(false);
        cbAutoDelete.setChecked(tempObj.isAutodelete());

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }



}
