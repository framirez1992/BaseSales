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

import com.far.basesales.AdminLicenseDevices;
import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class DeviceDialogFragment extends DialogFragment implements OnFailureListener {

    AdminLicenseDevices adminLicenseDevices;
    public Devices tempObj;
    public String codeLicense;

    LinearLayout llSave;
    TextInputEditText etCode;
    CheckBox cbEnabled;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static DeviceDialogFragment newInstance(AdminLicenseDevices adminLicenseDevices, Devices devices, String codeLicense) {

        DeviceDialogFragment f = new DeviceDialogFragment();
        f.adminLicenseDevices = adminLicenseDevices;
        f.tempObj = devices;
        f.codeLicense = codeLicense;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(devices != null) {
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
        return inflater.inflate(R.layout.dialog_add_edit_device, container, true);
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
        cbEnabled = view.findViewById(R.id.cbEnabled);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditDevice();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validateDevice(){
        if(etCode.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un codigo", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateDevice()) {
            SaveDevice();
        }

        llSave.setEnabled(true);

    }

    public void SaveDevice(){
        try {
            String code = etCode.getText().toString();
            Devices t = new Devices(code, cbEnabled.isChecked());

            adminLicenseDevices.getFs().collection(Tablas.generalLicencias).document(codeLicense).collection(Tablas.generalLicenciasDevices).document(t.getCODE()).set(t.toMap())
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


    public void EditDevice(){
        try {
            String code = etCode.getText().toString();

            tempObj.setCODE(code);
            tempObj.setENABLED(cbEnabled.isChecked());
            tempObj.setMDATE(null);

            adminLicenseDevices.getFs().collection(Tablas.generalLicencias).document(codeLicense).collection(Tablas.generalLicenciasDevices)
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
        cbEnabled.setChecked(tempObj.isENABLED());

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }



}
