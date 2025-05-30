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

import com.far.basesales.CloudFireStoreObjects.MeasureUnits;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.MeasureUnitsInvController;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;


public class MeasureUnitDialogFragment extends DialogFragment implements   OnFailureListener {

    DialogCaller dialogCaller;
    private MeasureUnits tempObj;

    LinearLayout llSave, llProgress;
    TextInputEditText etName;

    MeasureUnits toInsertObject;
    MeasureUnitsController measureUnitsController;
    MeasureUnitsInvController measureUnitsInvController;
    String type;

    public  static MeasureUnitDialogFragment newInstance(String type, MeasureUnits pt, DialogCaller dialogCaller) {

        MeasureUnitDialogFragment f = new MeasureUnitDialogFragment();
        f.type = type;
        f.tempObj = pt;
        f.dialogCaller = dialogCaller;

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
        measureUnitsController = MeasureUnitsController.getInstance(getActivity());
        measureUnitsInvController = MeasureUnitsInvController.getInstance(getActivity());

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
        llProgress = view.findViewById(R.id.llProgress);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        ((EditText)view.findViewById(R.id.etOrden)).setVisibility(View.GONE);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                llProgress.setVisibility(View.VISIBLE);
                if(tempObj == null){
                    Save();
                }else{
                   EditMeasureUnit();
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
                SaveMeasureUnit();
            }else{
                llSave.setEnabled(true);
                llProgress.setVisibility(View.INVISIBLE);
            }
    }

    public void SaveMeasureUnit(){
            String code =Funciones.generateCode();
            String name = etName.getText().toString();
            toInsertObject = new MeasureUnits(code, name);
            toInsertObject.setDATE(new Date());
            toInsertObject.setMDATE(new Date());
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                measureUnitsController.sendToFireBase(toInsertObject,  this);
                measureUnitsController.searchMeasureUnitFromFireBase(toInsertObject.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        MeasureUnits measureUnits =null;
                        if(querySnapshot!= null && querySnapshot.size()>0){
                            measureUnits = querySnapshot.getDocuments().get(0).toObject(MeasureUnits.class);
                        }

                        if(measureUnits!= null){
                            MeasureUnitsController.getInstance(getContext()).insert(measureUnits);
                            dialogCaller.dialogClosed(measureUnits);
                            dismiss();
                        }else{
                            failure("Error guardando medida. Intente nuevamente");
                        }
                    }
                }, this);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                measureUnitsInvController.sendToFireBase(toInsertObject);
            }
            //this.dismiss();


    }

    public void EditMeasureUnit(){
        try {
            MeasureUnits mu = ((MeasureUnits)tempObj);
            mu.setDESCRIPTION(etName.getText().toString());
            mu.setMDATE(new Date());

            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                measureUnitsController.sendToFireBase(mu,this);
                measureUnitsController.searchMeasureUnitFromFireBase(mu.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        MeasureUnits measureUnits =null;
                        if(querySnapshot!= null && querySnapshot.size()>0){
                            measureUnits = querySnapshot.getDocuments().get(0).toObject(MeasureUnits.class);
                        }

                        if(measureUnits!= null){
                            MeasureUnitsController.getInstance(getContext()).update(measureUnits);
                            dialogCaller.dialogClosed(measureUnits);
                            dismiss();
                        }else{
                            failure("Error guardando medida. Intente nuevamente");
                        }
                    }
                }, this);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                measureUnitsInvController.sendToFireBase(mu);
            }
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditMeasureUnits(){
        etName.setText(((MeasureUnits)tempObj).getDESCRIPTION());

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
