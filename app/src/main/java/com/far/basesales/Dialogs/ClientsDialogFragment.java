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
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ClientsDialogFragment extends DialogFragment implements OnFailureListener {

    DialogCaller dialogCaller;
    public Clients tempObj;
    public String type;

    LinearLayout llSave;
    TextInputEditText etName, etDocument, etPhone;


    ClientsController clientsController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ClientsDialogFragment newInstance(Clients c, DialogCaller dialogCaller) {

        ClientsDialogFragment f = new ClientsDialogFragment();
        f.tempObj = c;
        f.dialogCaller = dialogCaller;

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
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
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

    public boolean validateClient(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void Save(){
        if(validateClient()) {
            SaveClient();
        }else{
            llSave.setEnabled(true);
        }

    }

    public void SaveClient(){
        try {
            String code = Funciones.generateCode();
            String document = etDocument.getText().toString();
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            final Clients pt = new Clients(code,document, name, phone);
            clientsController.insert(pt);

            clientsController.sendToFireBase(pt, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    llSave.setEnabled(true);
                    dismiss();
                    dialogCaller.dialogClosed(pt);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    llSave.setEnabled(true);
                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });

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

            clientsController.sendToFireBase(tempObj, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    dismiss();
                    dialogCaller.dialogClosed(tempObj);
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            });

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
