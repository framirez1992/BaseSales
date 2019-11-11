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
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;

public class ProductSubTypeDialogFragment extends DialogFragment implements OnFailureListener {

    ProductsSubTypes tempObj;

    LinearLayout llFamilia;
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
    public  static ProductSubTypeDialogFragment newInstance(String type, ProductsSubTypes pt) {

        ProductSubTypeDialogFragment f = new ProductSubTypeDialogFragment();
        f.tempObj = pt;
        f.type = type;

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
            }

    }

    public void SaveProductSubType(){
        try {
            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            String codeProductType = ((KV)spnFamilia.getSelectedItem()).getKey();
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            ProductsSubTypes pst = new ProductsSubTypes(code,codeProductType,name, orden);
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsSubTypesController.sendToFireBase(pst);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsSubTypesInvController.sendToFireBase(pst);
            }

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EditProductSubType(){
        try {
            ProductsSubTypes pst = tempObj;
            int orden = (etOrden.getText().toString().trim().equals(""))?9999:Integer.parseInt(etOrden.getText().toString());
            pst.setDESCRIPTION(etName.getText().toString());
            pst.setCODETYPE(((KV)spnFamilia.getSelectedItem()).getKey());
            pst.setMDATE(null);
            pst.setORDEN(orden);
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsSubTypesController.sendToFireBase(pst);
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
        llSave.setEnabled(true);
    }
}
