package com.far.basesales.Dialogs;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.far.basesales.AdminConfiguration;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.DataBase.CloudFireStoreDB;
import com.far.basesales.Globales.CODES;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LicenseDialogFragment extends DialogFragment implements OnFailureListener {

    AdminConfiguration adminConfiguration;
    public Licenses tempObj;
    public String type;

    LinearLayout llSave;
    TextInputEditText etCode, etClient, etDateIni, etDateEnd, etDevices;
    ImageView imgDateIni, imgDateEnd;
    CheckBox cbEnabled;
    boolean firstLicense;


    LicenseController licenseController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static LicenseDialogFragment newInstance(AdminConfiguration adminConfiguration, Licenses l, boolean firstLicense) {

        LicenseDialogFragment f = new LicenseDialogFragment();
        f.adminConfiguration = adminConfiguration;
        f.tempObj = l;
        f.firstLicense = firstLicense;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(l != null) {
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
        licenseController = LicenseController.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_edit_license, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etClient);
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
        etClient = view.findViewById(R.id.etClient);
        etDateIni = view.findViewById(R.id.etDateIni);
        etDateEnd = view.findViewById(R.id.etDateEnd);
        etDevices = view.findViewById(R.id.etDevices);
        imgDateIni = view.findViewById(R.id.imgDateIni);
        imgDateEnd = view.findViewById(R.id.imgDateEnd);
        cbEnabled = view.findViewById(R.id.cbEnabled);

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

        imgDateIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etDateIni);
            }
        });

        imgDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(etDateEnd);
            }
        });

        etCode.setText(Funciones.generateCode());

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validateProductType(){
        if(etClient.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etDateIni.getText().toString().trim().isEmpty()){
            Snackbar.make(getView(), "Especifique una fecha de inicio", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etDateEnd.getText().toString().trim().isEmpty()){
            Snackbar.make(getView(), "Especifique una fecha de vencimiento", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(etDevices.getText().toString().trim().isEmpty()){
            Snackbar.make(getView(), "Especifique un limite de dispositivos", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(Integer.parseInt(etDevices.getText().toString())<1){
            Snackbar.make(getView(), "El numero de dispositivos debe ser mayor a 1", Snackbar.LENGTH_SHORT).show();
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
            String client = etClient.getText().toString();
            Date dateIni = new SimpleDateFormat("dd/MM/yyyy").parse(etDateIni.getText().toString());
            Date dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(etDateEnd.getText().toString());
            String devices = etDevices.getText().toString();




            //String code, String password,String clientName, Date dateIni, Date dateEnd, int counter, int days, int devices, boolean enabled, boolean updated, Date lastUpdate, int status
            Licenses license = new Licenses(code, code, client,dateIni, dateEnd, 0,Funciones.calcularDias(dateEnd, dateIni),Integer.parseInt(devices),cbEnabled.isChecked(),true,null, CODES.CODE_LICENSE_VALID);

           LicenseController.getInstance(getContext()).createNewLicense(firstLicense, license);
           adminConfiguration.searchLicenses();
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void showDatePicker(final TextInputEditText et){
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, month);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                et.setText(Funciones.getFormatedDateRepDom(c.getTime()));
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    public void EditLicense(){
        try {
            String code = etCode.getText().toString();
            String client = etClient.getText().toString();
            Date dateIni = new SimpleDateFormat("dd/MM/yyyy").parse(etDateIni.getText().toString());
            Date dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(etDateEnd.getText().toString());
            String devices = etDevices.getText().toString();

            tempObj.setCLIENTNAME(client);
            tempObj.setDATEINI(dateIni);
            tempObj.setDATEEND(dateEnd);
            tempObj.setDAYS(Funciones.calcularDias(dateEnd, dateIni));
            tempObj.setDEVICES(Integer.parseInt(devices));
            tempObj.setLASTUPDATE(null);
            tempObj.setENABLED(cbEnabled.isChecked());
            //tempObj.setSTATUS(CODES.LIC);

            LicenseController.getInstance(getContext()).updateLicense(tempObj);
            adminConfiguration.searchLicenses();
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etCode.setText(tempObj.getCODE());
        etClient.setText(tempObj.getCLIENTNAME());
        etDateIni.setText(Funciones.getFormatedDateRepDom(tempObj.getDATEINI()));
        etDateEnd.setText(Funciones.getFormatedDateRepDom(tempObj.getDATEEND()));
        etDevices.setText(tempObj.getDEVICES()+"");
        cbEnabled.setChecked(tempObj.isENABLED());

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }



}
