package com.far.basesales.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.basesales.AdminLicenseUserDevice;
import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class UserDeviceDialogFragment extends DialogFragment implements OnFailureListener {

    AdminLicenseUserDevice adminLicenseUserDevice;
    public String codeLicense;
    ArrayList<Users> users;
    ArrayList<Devices> devices;

    LinearLayout llSave;
    Spinner spnUser, spnDevice;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static UserDeviceDialogFragment newInstance(AdminLicenseUserDevice adminLicenseUserDevice, String codeLicense, ArrayList<Users> users, ArrayList<Devices> devices) {

        UserDeviceDialogFragment f = new UserDeviceDialogFragment();
        f.adminLicenseUserDevice = adminLicenseUserDevice;
        f.codeLicense = codeLicense;
        f.users = users;
        f.devices = devices;

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
        return inflater.inflate(R.layout.dialog_add_user_device, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
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
        spnUser = view.findViewById(R.id.spnUser);
        spnDevice = view.findViewById(R.id.spnDevice);
        fillSpinners();

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                    Save();
            }
        });

    }

    public boolean validateProductType(){
        if(spnUser.getAdapter()== null || spnUser.getAdapter().getCount()<1){
            Snackbar.make(getView(), "Especifique un usuario", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if(spnDevice.getAdapter()== null || spnDevice.getAdapter().getCount()<1){
            Snackbar.make(getView(), "Especifique un dispositivo", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveUserDevice();
        }

        llSave.setEnabled(true);

    }

    public void SaveUserDevice(){
        try {
            String codeUser = ((KV)spnUser.getSelectedItem()).getKey();
            String codeDevice = ((KV)spnDevice.getSelectedItem()).getKey();
            UsersDevices t = new UsersDevices(Funciones.generateCode(), codeUser, codeDevice);

            adminLicenseUserDevice.getFs().collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersUsersDevices).document(t.getCODE()).set(t.toMap())
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



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }

    public void fillUsers(){
        ArrayList<KV> spnList = new ArrayList<>();
        for(Users u: users){
            spnList.add(new KV(u.getCODE(), u.getUSERNAME()));
        }
        spnUser.setAdapter(new ArrayAdapter<KV>(adminLicenseUserDevice, android.R.layout.simple_list_item_1,spnList));
    }

    public void fillSpinners(){
        ArrayList<KV> spnList = new ArrayList<>();
        for(Users u: users){
            spnList.add(new KV(u.getCODE(), u.getUSERNAME()));
        }
        spnUser.setAdapter(new ArrayAdapter<KV>(adminLicenseUserDevice, android.R.layout.simple_list_item_1,spnList));

        spnList = new ArrayList<>();
        for(Devices u: devices){
            spnList.add(new KV(u.getCODE(), u.getCODE()));
        }
        spnDevice.setAdapter(new ArrayAdapter<KV>(adminLicenseUserDevice, android.R.layout.simple_list_item_1,spnList));
    }


}
