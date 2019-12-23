package com.far.basesales.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ProductMeasureRowModel;
import com.far.basesales.CloudFireStoreObjects.Products;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsInvController;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsSubTypesInvController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class ProductsDialogfragment extends DialogFragment implements ListableActivity, OnFailureListener {

    private Products tempObj;

    LinearLayout llSave, llBack;
    TextInputEditText etCode, etName;
    Spinner spnFamily, spnGroup;
    RecyclerView rvMeasures;
    LinearLayout llMeasureScreen, llMainScreen, llNext;

    TextView tvMeasureName;
    CheckBox cbActiveMeasure,cbActiveRange;
    TextInputEditText etMeasurePrice, etMin, etMax;
    CardView btnApply;
    ProductMeasureRowModel selectedRowModel;

    ProductsController productsController;
    ProductsInvController productsInvController;
    ArrayList<ProductMeasureRowModel> selected = new ArrayList<>() ;
    boolean firstTime = true;
    String type;

    Dialog loadingDialg;
    Dialog errorDialog;

    public  static ProductsDialogfragment newInstance(String type, Products pt) {


        ProductsDialogfragment f = new ProductsDialogfragment();
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
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);
        productsController = ProductsController.getInstance(getActivity());
        productsInvController = ProductsInvController.getInstance(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.dialog_add_edit_product, container, true);
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
        llMainScreen = view.findViewById(R.id.llMainScreen);
        llMeasureScreen = view.findViewById(R.id.llMeasureScreen);
        llNext = view.findViewById(R.id.llNext);
        llSave = view.findViewById(R.id.llSave);
        llBack = view.findViewById(R.id.llBack);
        etCode = view.findViewById(R.id.etCode);
        etName = view.findViewById(R.id.etName);
        spnFamily = view.findViewById(R.id.spnFamilia);
        spnGroup = view.findViewById(R.id.spnGrupo);
        rvMeasures = view.findViewById(R.id.rvMeasures);
        rvMeasures.setLayoutManager(new LinearLayoutManager(getActivity()));

        tvMeasureName = view.findViewById(R.id.tvMeasureName);
        cbActiveMeasure = view.findViewById(R.id.cbActiveMeasure);
        cbActiveRange = view.findViewById(R.id.cbActiveRange);
        etMeasurePrice = view.findViewById(R.id.etMeasurePrice);
        etMin = view.findViewById(R.id.etMin);
        etMax = view.findViewById(R.id.etMax);
        btnApply = view.findViewById(R.id.btnApply);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedRowModel == null){
                    return;
                }
                if(!validateRowModelEdition()){
                    return;
                }
                selectedRowModel.setAmount(Double.parseDouble(etMeasurePrice.getText().toString()));
                selectedRowModel.setChecked(cbActiveMeasure.isChecked());
                selectedRowModel.setPriceRange(cbActiveRange.isChecked());
                selectedRowModel.setMinPrice(Double.parseDouble(etMin.getText().toString()));
                selectedRowModel.setMaxPrice(Double.parseDouble(etMax.getText().toString()));

                ((ProductMeasureSelectionAdapter)rvMeasures.getAdapter()).update();
               /* //si esta en los selected actualizar
                rvMeasures.getAdapter().notifyDataSetChanged();
                rvMeasures.invalidate();*/
                clearMeasureData();
            }
        });

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(getActivity()).fillSpinner(spnFamily, false);
            ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(getActivity()).fillSpinner(spnFamily, false);
            ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false);
        }

        etCode.setEnabled(false);
        etCode.setText(Funciones.generateCode());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llSave.setEnabled(false);
                selected = ((ProductMeasureSelectionAdapter)rvMeasures.getAdapter()).getSelectedObjects();
                if(tempObj == null){
                    Save();
                }else{
                    Edit();
                }
                llSave.setEnabled(true);
            }
        });

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
            }
        });

        llNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMainScreen.setVisibility((llMainScreen.getVisibility() == View.VISIBLE)?View.GONE:View.VISIBLE);
                llMeasureScreen.setVisibility((llMeasureScreen.getVisibility() == View.GONE)?View.VISIBLE:View.GONE);
            }
        });

        if(tempObj != null){//EDIT
            setUpToEditUsers();
        }

        fillMeasures();


        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnFamily.getSelectedItem();
                if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                    ProductsSubTypesController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                    ProductsSubTypesInvController.getInstance(getActivity()).fillSpinner(spnGroup, false, familia.getKey());
                }

                if(firstTime){//para que seleccione el subType del producto automaticamente la primera vez que abra el dialogo.
                    firstTime= false;
                    if(tempObj != null){
                        setSpinnerposition(spnGroup, tempObj.getSUBTYPE());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean validate(){
        if(etName.getText().toString().trim().equals("")){
            Snackbar.make(getView(), "Especifique un nombre.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnFamily.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione una familia.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(spnGroup.getSelectedItem()== null){
            Snackbar.make(getView(), "Seleccione un grupo.", Snackbar.LENGTH_SHORT).show();
            return false;
        }else if(selected.size() == 0){
            Snackbar.make(getView(), "Seleccione por lo menos 1 unidad de medida", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void Save(){

        if(validate()) {
            SaveProduct();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void Edit(){
        if(validate()) {
            EditProduct();
        }else{
            llSave.setEnabled(true);
        }
    }

    public void SaveProduct(){
        try {
            String code = etCode.getText().toString();
            String description = etName.getText().toString();
            String productType = ((KV)spnFamily.getSelectedItem()).getKey();
            String productSubType = ((KV)spnGroup.getSelectedItem()).getKey();
            Products product = new Products(code, description, productType, productSubType, false);

            ArrayList<ProductsMeasure> list = new ArrayList<>();
            for(ProductMeasureRowModel ssrm: selected){
                //String code, String codeProduct, String codeMeasure,double price,boolean range, double minPrice, double maxPrice, boolean enabled, String date, String mdate
                list.add(new ProductsMeasure(Funciones.generateCode(), code, ssrm.getCodeMeasure(),ssrm.getAmount(),ssrm.isPriceRange(), ssrm.getMinPrice(),ssrm.getMaxPrice(),ssrm.isChecked(), null, null));
            }

            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsController.sendToFireBase(product, list);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsInvController.sendToFireBase(product, list);
            }


            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void EditProduct(){
        try {
            Products products = ((Products)tempObj);
            products.setDESCRIPTION(etName.getText().toString());
            products.setTYPE(((KV)spnFamily.getSelectedItem()).getKey());
            products.setSUBTYPE(((KV)spnGroup.getSelectedItem()).getKey());
            products.setMDATE(null);

            ArrayList<ProductsMeasure> list = new ArrayList<>();
            for(ProductMeasureRowModel ssrm: selected){
                //String code, String codeProduct, String codeMeasure,double price,boolean range, double minPrice, double maxPrice, boolean enabled, String date, String mdate
                list.add(new ProductsMeasure(Funciones.generateCode(), products.getCODE(), ssrm.getCodeMeasure(),ssrm.getAmount(),
                        ssrm.isPriceRange(),ssrm.getMinPrice(),ssrm.getMaxPrice() ,true, null, null));
            }

            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                productsController.sendToFireBase(products, list);
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                productsInvController.sendToFireBase(products, list);
            }

            this.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


    public void setUpToEditUsers(){
        Products p = ((Products)tempObj);
        etCode.setText(p.getCODE());
        etCode.setEnabled(false);
        etName.setText(p.getDESCRIPTION());
        setSpinnerposition(spnFamily, p.getTYPE());
        setSpinnerposition(spnGroup, p.getSUBTYPE());


    }

    public void setSpinnerposition(Spinner spn, String key){
        for(int i = 0; i< spn.getAdapter().getCount(); i++){
            if(((KV)spn.getAdapter().getItem(i)).getKey().equals(key)){
                spn.setSelection(i);
                break;
            }
        }
    }




    @Override
    public void onFailure(@NonNull Exception e) {
        //Funciones.showNetworkErrorWithText(getView(), e.getMessage());
        llSave.setEnabled(true);
    }

    public void fillMeasures(){

        if(tempObj != null) {
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                selected.addAll(ProductsMeasureController.getInstance(getActivity()).getSSRMByCodeProduct((tempObj).getCODE()));
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
               // selectedObjs.addAll(ProductsMeasureInvController.getInstance(getActivity()).getSSRMByCodeProduct(((Products) tempObj).getCODE()));
            }
        }
        ArrayList<ProductMeasureRowModel> arr = null;
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            arr =  MeasureUnitsController.getInstance(getActivity()).getUnitMeasuresSSRM(null, null, null);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            //arr = MeasureUnitsInvController.getInstance(getActivity()).getUnitMeasuresSSRM(null, null, null);
        }

        rvMeasures.setAdapter(new ProductMeasureSelectionAdapter(getActivity(),this, arr, selected));
        rvMeasures.getAdapter().notifyDataSetChanged();
        rvMeasures.invalidate();
    }

    OnFailureListener onFailureSerachProduct = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        showErrorDialog(e.getMessage());
        llSave.setEnabled(true);
        closeLoadingDialog();
        }
    };

    OnSuccessListener<QuerySnapshot> onSuccessSeachProduct = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {

            if(querySnapshot != null && querySnapshot.getDocuments().size() >0){
            showErrorDialog("No puede editar productos que esten actualmente en ventas (Abiertas o Entregadas)");
            }else{
                EditProduct();
            }

            llSave.setEnabled(true);
            closeLoadingDialog();
        }
    };

    /**
     * validamos que el producto a editar no este en una venta (Abierta, entregada, anulada) es decir no historica
     * @param codeProduct
     */
    public void searchProduct(String codeProduct){
       // SalesController.getInstance(getActivity()).searchProductInSalesDetail(codeProduct,onSuccessSeachProduct, onFailureSerachProduct);
    }

    public void showLoadingDialog(){
        loadingDialg = null;
        loadingDialg = Funciones.getLoadingDialog(getActivity(),"Loading...");
        loadingDialg.show();
    }

    public void closeLoadingDialog(){
        if(loadingDialg != null){
            loadingDialg.dismiss();
        }
    }

    public void showErrorDialog(String msg){
        errorDialog = null;
        errorDialog = Funciones.getCustomDialog(getActivity(),getResources().getColor(R.color.red_700), "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    @Override
    public void onClick(Object obj) {
        if(obj instanceof ProductMeasureRowModel){
            ProductMeasureRowModel item = (ProductMeasureRowModel)obj;
            setMeasureData(item);
        }
    }

    public void setMeasureData(ProductMeasureRowModel item){
        selectedRowModel = item;
        tvMeasureName.setText(item.getMeasureDescription());
        cbActiveMeasure.setChecked(item.isChecked());
        cbActiveRange.setChecked(item.isPriceRange());
        etMeasurePrice.setText(String.valueOf(item.getAmount()));
        etMin.setText(String.valueOf(item.getMinPrice()));
        etMax.setText(String.valueOf(item.getMaxPrice()));
    }

    public void clearMeasureData(){
        selectedRowModel = null;
        tvMeasureName.setText("");
        cbActiveMeasure.setChecked(false);
        cbActiveRange.setChecked(false);
        etMeasurePrice.setText("");
        etMin.setText("");
        etMax.setText("");
    }


    public boolean validateRowModelEdition(){
        double price = 0.0;
        double minPrice = 0.0;
        double maxPrice = 0.0;
        try{
           price = Double.parseDouble(etMeasurePrice.getText().toString());
        }catch (Exception e){
            Snackbar.make(getView(), "Precio invalido", Snackbar.LENGTH_LONG).show();
            etMeasurePrice.requestFocus();
            return false;
        }
        try{
            minPrice = Double.parseDouble(etMin.getText().toString());
        }catch (Exception e){
            Snackbar.make(getView(), "Precio minimo invalido", Snackbar.LENGTH_LONG).show();
            etMin.requestFocus();
            return false;
        }
        try{
            maxPrice = Double.parseDouble(etMax.getText().toString());
        }catch (Exception e){
            Snackbar.make(getView(), "Precio maximo invalido", Snackbar.LENGTH_LONG).show();
            etMax.requestFocus();
            return false;
        }
        return true;
    }
}
