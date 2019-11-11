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

import com.far.basesales.CloudFireStoreObjects.ProductsTypes;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Globales.CODES;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;


public  class ProductTypeDialogFragment extends DialogFragment implements OnFailureListener {

    public ProductsTypes tempObj;
    public String type;

    LinearLayout llSave;
    TextInputEditText etName;
    TextInputEditText etOrden;


    ProductsTypesController productsTypesController;
    ProductsTypesInvController productsTypesInvController;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductTypeDialogFragment newInstance(String type, ProductsTypes pt) {

        ProductTypeDialogFragment f = new ProductTypeDialogFragment();
        f.type = type;
        f.tempObj = pt;

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
        productsTypesController = ProductsTypesController.getInstance(getActivity());
        productsTypesInvController = ProductsTypesInvController.getInstance(getActivity());

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
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);

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
            String name = etName.getText().toString();
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            ProductsTypes pt = new ProductsTypes(code, name, orden);
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                productsTypesController.sendToFireBase(pt);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsTypesInvController.sendToFireBase(pt);
            }
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProductType(){
        try {
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            tempObj.setDESCRIPTION(etName.getText().toString());
            tempObj.setMDATE(null);
            tempObj.setORDEN(orden);
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                productsTypesController.sendToFireBase(tempObj);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsTypesInvController.sendToFireBase(tempObj);
            }
            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }



    public void setUpToEditProductType(){
        etName.setText(tempObj.getDESCRIPTION());
        etOrden.setText(tempObj.getORDEN()+"");

    }



    @Override
    public void onFailure(@NonNull Exception e) {
        llSave.setEnabled(true);
    }
}
