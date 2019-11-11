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
import android.widget.LinearLayout;

import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;

public class ClientsDialogFragment extends DialogFragment implements OnFailureListener {

    public Clients tempObj;
    public String type;

    LinearLayout llSave;
    TextInputEditText etName, etDocument, etPhone;


    ClientsController clientsController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ClientsDialogFragment newInstance(Clients c) {

        ClientsDialogFragment f = new ClientsDialogFragment();
        f.tempObj = c;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(c != null) {
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
        clientsController = ClientsController.getInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_adit_clients, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
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
        etDocument = view.findViewById(R.id.etDocument);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductType();
                }
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditProductType();
        }
    }

    public boolean validateProductType(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateProductType()) {
            SaveProductType();
        }

    }

    public void SaveProductType(){
        try {
            String code = Funciones.generateCode();
            String document = etDocument.getText().toString();
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            Clients pt = new Clients(code,document, name, phone);

            clientsController.sendToFireBase(pt);
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProductType(){
        try {
            tempObj.setDOCUMENT(etDocument.getText().toString());
            tempObj.setNAME(etName.getText().toString());
            tempObj.setMDATE(null);
            tempObj.setPHONE(etPhone.getText().toString());

            clientsController.sendToFireBase(tempObj);

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etDocument.setText(tempObj.getDOCUMENT());
        etName.setText(tempObj.getNAME());
        etPhone.setText(tempObj.getPHONE());

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
