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
import android.widget.Spinner;

import com.far.basesales.CloudFireStoreObjects.ProductsSubTypes;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsSubTypesInvController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Generic.KV;
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

public class ProductSubTypeDialogFragment extends DialogFragment implements   OnFailureListener {

    DialogCaller dialogCaller;
    ProductsSubTypes tempObj;
    ProductsSubTypes toInsertObject;

    LinearLayout llFamilia, llProgress;
    Spinner spnFamilia;
    LinearLayout llSave;
    TextInputEditText etName, etOrden;
    String type;

    ProductsSubTypesController productsSubTypesController;
    ProductsSubTypesInvController productsSubTypesInvController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductSubTypeDialogFragment newInstance(String type, ProductsSubTypes pt, DialogCaller dialogCaller) {

        ProductSubTypeDialogFragment f = new ProductSubTypeDialogFragment();
        f.tempObj = pt;
        f.type = type;
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
        productsSubTypesController = ProductsSubTypesController.getInstance(getActivity());
        productsSubTypesInvController = ProductsSubTypesInvController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_spn_save, container, true);
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
        llFamilia = view.findViewById(R.id.llFamilia);
        spnFamilia = view.findViewById(R.id.spnFamilia);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);
        view.findViewById(R.id.tilOrden).setVisibility(View.VISIBLE);


        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamilia, false);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(getActivity()).fillSpinner(spnFamilia, false);
        }

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                llProgress.setVisibility(View.VISIBLE);
                if(tempObj == null){
                    Save();
                }else{
                    EditProductSubType();
                }
            }
        });



        if(tempObj != null) {//EDIT
            prepareForProductSubType();
        }
    }


    public boolean validateProductSubType(){
        if(spnFamilia.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void Save(){
            if(validateProductSubType()){
                SaveProductSubType();
            }else{
                llSave.setEnabled(true);
                llProgress.setVisibility(View.INVISIBLE);
            }

    }

    public void SaveProductSubType(){

            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            String codeProductType = ((KV)spnFamilia.getSelectedItem()).getKey();
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            toInsertObject = new ProductsSubTypes(code,codeProductType,name, orden);
            toInsertObject.setDATE(new Date());
            toInsertObject.setMDATE(new Date());
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsSubTypesController.sendToFireBase(toInsertObject, this);
                productsSubTypesController.searchProductSubTypeFromFireBase(toInsertObject.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        ProductsSubTypes pst = null;
                        if(querySnapshot != null && querySnapshot.getDocuments().size() > 0){
                            pst = querySnapshot.getDocuments().get(0).toObject(ProductsSubTypes.class);
                        }
                        if(pst != null){
                            ProductsSubTypesController.getInstance(getContext()).insert(pst);
                            dialogCaller.dialogClosed(pst);
                            dismiss();
                        }else{
                        failure("Error guardando grupo. Intente nuevamente");
                        }
                    }
                }, this);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsSubTypesInvController.sendToFireBase(toInsertObject);
            }

    }

    public void EditProductSubType(){
        try {
            ProductsSubTypes pst = tempObj;
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            pst.setDESCRIPTION(etName.getText().toString());
            pst.setCODETYPE(((KV)spnFamilia.getSelectedItem()).getKey());
            pst.setMDATE(new Date());
            pst.setORDEN(orden);
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsSubTypesController.sendToFireBase(pst, this);
            productsSubTypesController.searchProductSubTypeFromFireBase(pst.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    ProductsSubTypes pst = null;
                    if(querySnapshot != null && querySnapshot.getDocuments().size() > 0){
                        pst = querySnapshot.getDocuments().get(0).toObject(ProductsSubTypes.class);
                    }
                    if(pst != null){
                        ProductsSubTypesController.getInstance(getContext()).update(tempObj);
                        dialogCaller.dialogClosed(pst);
                        dismiss();
                    }else{
                        failure("Error editando grupo. Intente nuevamente");
                    }

                }
            }, this);

            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
             productsSubTypesInvController.sendToFireBase(pst);
            }

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public void prepareForProductSubType(){
        setFamilia();
        etName.setText(tempObj.getDESCRIPTION());
        etOrden.setText(tempObj.getORDEN()+"");
    }
    public void setFamilia(){
        for(int i = 0; i< spnFamilia.getAdapter().getCount(); i++){
            if(((KV)spnFamilia.getAdapter().getItem(i)).getKey().equals(tempObj.getCODETYPE())){
                spnFamilia.setSelection(i);
                break;
            }
        }
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
