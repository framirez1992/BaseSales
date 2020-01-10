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
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.far.farpdf.Entities.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ClientsDialogFragment extends DialogFragment implements OnFailureListener {

    DialogCaller dialogCaller;
    public Clients tempObj;
    private Clients toInsertObject;
    public String type;

    LinearLayout llSave, llProgress;
    TextInputEditText etName, etDocument, etPhone, etBirth;


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
        llProgress = view.findViewById(R.id.llProgress);
        llSave = view.findViewById(R.id.llSave);
        etDocument = view.findViewById(R.id.etDocument);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        etBirth = view.findViewById(R.id.etBirth);

        etBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                llProgress.setVisibility(View.VISIBLE);
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
            llProgress.setVisibility(View.INVISIBLE);
        }

    }

    public void SaveClient(){
            String code = Funciones.generateCode();
            String document = etDocument.getText().toString();
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String data = "";
            String data2 = "";
            String data3 = "";
            if(!etBirth.getText().toString().trim().isEmpty()){
                data = etBirth.getText().toString();
            }
            toInsertObject = new Clients(code,document, name, phone, data, data2, data3);
            toInsertObject.setDATE(new Date());
            toInsertObject.setMDATE(new Date());

            clientsController.sendToFireBase(toInsertObject, this);
            clientsController.searchClientFromFireBase(toInsertObject.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    Clients c = null;
                    if(querySnapshot!= null && querySnapshot.size() > 0){
                        c = querySnapshot.getDocuments().get(0).toObject(Clients.class);
                    }

                    if(c != null){
                            ClientsController.getInstance(getContext()).insert(c);
                            dialogCaller.dialogClosed(c);
                        dismiss();
                    }else{
                        failure("Error guardando cliente. Intente nuevamente.");
                    }
                }
            }, this);



    }

    public void EditProductType(){
            String data = "";
            String data2 = "";
            String data3 = "";
            if(!etBirth.getText().toString().trim().isEmpty()){
                data = etBirth.getText().toString();
            }

            tempObj.setDOCUMENT(etDocument.getText().toString());
            tempObj.setNAME(etName.getText().toString());
            tempObj.setMDATE(new Date());
            tempObj.setPHONE(etPhone.getText().toString());
            tempObj.setDATA(data);
            tempObj.setDATA2(data2);
            tempObj.setDATA3(data3);

            clientsController.sendToFireBase(tempObj,  this);
            clientsController.searchClientFromFireBase(tempObj.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    Clients c = null;
                    if(querySnapshot!= null && querySnapshot.size() > 0){
                        c = querySnapshot.getDocuments().get(0).toObject(Clients.class);
                    }

                    if(c != null){
                        ClientsController.getInstance(getContext()).update(c);
                        dialogCaller.dialogClosed(c);
                        dismiss();
                    }else{
                        failure("Error Editando cliente. Intente nuevamente.");
                    }
                }
            }, this);


    }


    public void setUpToEditProductType(){
        etDocument.setText(tempObj.getDOCUMENT());
        etName.setText(tempObj.getNAME());
        etPhone.setText(tempObj.getPHONE());
        etBirth.setText(tempObj.getDATA()!= null? tempObj.getDATA(): "");
    }


    public void showDatePicker(){
        String dob = etBirth.getText().toString();
        Calendar c = Calendar.getInstance();
        if(!dob.trim().isEmpty()){
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                c.setTimeInMillis(sdf.parse(dob).getTime());
            }catch (Exception e){

            }
        }

        DatePickerDialog a = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                c.set(Calendar.YEAR, year);c.set(Calendar.MONTH, month);c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                etBirth.setText(sdf.format(c.getTime()));

            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(c.DAY_OF_MONTH));
        a.show();
    }


    @Override
    public void onFailure(@NonNull Exception e) {
       failure(e.getMessage());
    }

    public void failure(String msg){
        llSave.setEnabled(true);
        llProgress.setVisibility(View.INVISIBLE);
        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

}
