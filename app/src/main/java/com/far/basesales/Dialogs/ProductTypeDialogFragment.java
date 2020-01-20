package com.far.basesales.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.far.basesales.CloudFireStoreObjects.ProductsTypes;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
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


public  class ProductTypeDialogFragment extends DialogFragment implements  OnFailureListener {

    DialogCaller dialogCaller;
    public ProductsTypes tempObj;
    public String type;

    LinearLayout llSave;
    TextInputEditText etName;
    TextInputEditText etOrden;
    LinearLayout llProgress;


    ProductsTypesController productsTypesController;
    ProductsTypesInvController productsTypesInvController;

    ProductsTypes toInsertObject;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ProductTypeDialogFragment newInstance(String type, ProductsTypes pt, DialogCaller dialogCaller) {

        ProductTypeDialogFragment f = new ProductTypeDialogFragment();
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
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        Funciones.showKeyBoard(etName);
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        Window window = getDialog().getWindow();
        window.setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    public void init(View view){
        llProgress = view.findViewById(R.id.llProgress);
        llSave = view.findViewById(R.id.llSave);
        etName = view.findViewById(R.id.etName);
        etOrden = view.findViewById(R.id.etOrden);

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
        }else{
            llSave.setEnabled(true);
            llProgress.setVisibility(View.INVISIBLE);
        }

    }

    public void SaveProductType(){
            String code = Funciones.generateCode();
            String name = etName.getText().toString();
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            toInsertObject = new ProductsTypes(code, name, orden);
            toInsertObject.setDATE(new Date());
            toInsertObject.setMDATE(new Date());

            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                productsTypesController.sendToFireBase(toInsertObject, this);
                productsTypesController.searchProductTypeFromFireBase(toInsertObject.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        ProductsTypes pt = null;
                        if(querySnapshot!= null && querySnapshot.size()>0){
                            pt = querySnapshot.getDocuments().get(0).toObject(ProductsTypes.class);
                        }

                        if(pt != null){
                            ProductsTypesController.getInstance(getContext()).insert(pt);
                            dialogCaller.dialogClosed(pt);
                            dismiss();
                        }else{
                            failure("Error guardando familia. Intente nuevamente.");
                        }
                    }
                }, this);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsTypesInvController.sendToFireBase(toInsertObject);
            }


    }

    public void EditProductType(){
            int orden = etOrden.getText().toString().trim().equals("")?9999:Integer.parseInt(etOrden.getText().toString());
            tempObj.setDESCRIPTION(etName.getText().toString());
            tempObj.setMDATE(new Date());
            tempObj.setORDEN(orden);
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                productsTypesController.sendToFireBase(tempObj, this);
                productsTypesController.searchProductTypeFromFireBase(tempObj.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {

                        ProductsTypes pt = null;
                        if(querySnapshot!= null && querySnapshot.size()>0){
                            pt = querySnapshot.getDocuments().get(0).toObject(ProductsTypes.class);
                        }

                        if(pt != null){
                            ProductsTypesController.getInstance(getContext()).update(pt);
                            dialogCaller.dialogClosed(pt);
                            dismiss();
                        }else{
                            failure("Error editando familia. Intente nuevamente.");
                        }
                    }
                }, this);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsTypesInvController.sendToFireBase(tempObj);
            }

    }


    public void setUpToEditProductType(){
        etName.setText(tempObj.getDESCRIPTION());
        etOrden.setText(tempObj.getORDEN()+"");

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
