package com.far.basesales.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.NewOrderProductModel;
import com.far.basesales.Adapters.Models.NewOrderProductNoMeasureModel;
import com.far.basesales.Adapters.Models.OrderDetailModel;
import com.far.basesales.Adapters.NewOrderProductRowAdapter;
import com.far.basesales.Adapters.OrderResumeAdapter;
import com.far.basesales.Adapters.OrderResumeNoMeasureAdapter;
import com.far.basesales.Adapters.SalesRowAdapter;
import com.far.basesales.Adapters.SalesRowNoMeasureAdapter;
import com.far.basesales.CloudFireStoreObjects.Products;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Generic.KV2;
import com.far.basesales.Globales.CODES;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.security.interfaces.DSAPublicKey;
import java.util.ArrayList;

public class AddProductDialog extends DialogFragment {

    Context context;
    Object tempOrderModel;
    TextView tvName;
    TextView tvRangePrice;
    Spinner spnUnitMeasure;

    TextInputLayout tilPrecio;
    TextInputEditText etCantidad, etPrice;
    TempOrdersController tempOrdersController;
    MeasureUnitsController measureUnitsController;
    TextView btnOK, tvMeasure;
    LinearLayout llMeasure;
    RecyclerView.Adapter adapter;
    RecyclerView.ViewHolder holder;

    boolean productMeasureControl = false;
    boolean rangeControl = false;

    public  static AddProductDialog newInstance(Context context, Object pt, RecyclerView.ViewHolder holder, RecyclerView.Adapter adapter) {
        AddProductDialog f = new AddProductDialog();
        f.tempOrderModel = pt;
        f.holder = holder;
        f.adapter = adapter;
        f.context = context;

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tempOrdersController = TempOrdersController.getInstance(getActivity());
        measureUnitsController = MeasureUnitsController.getInstance(getActivity());
        productMeasureControl = UserControlController.getInstance(getActivity()).searchSimpleControl(CODES.USERSCONTROL_PRODUCTS_MEASURE)!= null;
        rangeControl = UserControlController.getInstance(getActivity()).searchSimpleControl(CODES.USERSCONTROL_PRODUCT_PRICES_RANGE)!= null;
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etCantidad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.add_product_dialog, container, true);
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
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        llMeasure = view.findViewById(R.id.llMeasure);
        tvMeasure = view.findViewById(R.id.tvMeasure);
        tvName = view.findViewById(R.id.tvName);
        tvRangePrice = view.findViewById(R.id.tvRangePrice);
        etCantidad = view.findViewById(R.id.etCantidad);
        spnUnitMeasure = view.findViewById(R.id.spnUnitMeasure);
        etPrice = view.findViewById(R.id.etPrice);
        tilPrecio = view.findViewById(R.id.tilPrecio);

        btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                   saveOrderLine();
                }

            }
        });

        if(adapter instanceof NewOrderProductRowAdapter){
            initDialogForNewOrderProductRowAdapter();
            //inicializeEditOrderLine();
        }else if(adapter instanceof SalesRowAdapter) {
            initDialogForSalesRowAdapter((NewOrderProductModel)tempOrderModel);
            //inicializeEditOrderLine();
        }else if(adapter instanceof SalesRowNoMeasureAdapter){
            initDialogForSalesRowNoMeasureAdapter((NewOrderProductNoMeasureModel)tempOrderModel);
        }else if(adapter instanceof OrderResumeAdapter ){
            initDialogForOrderResumeAdapter((OrderDetailModel) tempOrderModel);
        }else if(adapter instanceof OrderResumeNoMeasureAdapter){
            initDialogForOrderResumeNoMeasureAdapter((OrderDetailModel) tempOrderModel);
        }

        initMeasure();

    }


    public boolean validate(){
        String codeProduct = "";
        if(tempOrderModel instanceof NewOrderProductModel){
            codeProduct = ((NewOrderProductModel)tempOrderModel).getCodeProduct();
        }else if(tempOrderModel instanceof  NewOrderProductNoMeasureModel){
            codeProduct = ((NewOrderProductNoMeasureModel)tempOrderModel).getCodeProduct();
        }else if(tempOrderModel instanceof OrderDetailModel){
            codeProduct = ((OrderDetailModel)tempOrderModel).getCodeProduct();
        }
        double manualPrice = -1;
        try{
            manualPrice = Double.parseDouble(etPrice.getText().toString());
        }catch (Exception e){
            Snackbar.make(getView(), "Precio invalido", Snackbar.LENGTH_SHORT).show();
            etPrice.requestFocus();
            return false;
        }

        if(etCantidad.getText().toString().trim().equals("") || etCantidad.getText().toString().trim().equals(".") || etCantidad.getText().toString().trim().equals("0")){
            Snackbar.make(getView(), "La cantidad debe ser mayor a 0", Snackbar.LENGTH_SHORT).show();
            etCantidad.requestFocus();
            return false;
        }else if(adapter instanceof NewOrderProductRowAdapter ){
            if(spnUnitMeasure.getAdapter()== null && spnUnitMeasure.getSelectedItem()== null){
                Snackbar.make(getView(), "Seleccione una unidad de venta", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            ProductsMeasure pm = ProductsMeasureController.getInstance(context).getProductMeasureByProductAndMeasure(codeProduct, ((KV2)spnUnitMeasure.getSelectedItem()).getKey());
            if(rangeControl && pm.getRANGE() && manualPrice < pm.getMINPRICE()){
                Snackbar.make(getView(), "El precio no puede ser menor a $"+Funciones.formatMoney(pm.getMINPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }else if(rangeControl && pm.getRANGE() && manualPrice > pm.getMAXPRICE()){
                Snackbar.make(getView(), "El precio no puede ser superior a $"+Funciones.formatMoney(pm.getMAXPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }
        }else if(adapter instanceof  SalesRowAdapter){
            ProductsMeasure pm = ProductsMeasureController.getInstance(context).getProductMeasureByProductAndMeasure(codeProduct, ((NewOrderProductModel)tempOrderModel).getMeasure());
            if(rangeControl && pm.getRANGE() && manualPrice < pm.getMINPRICE()){
                Snackbar.make(getView(), "El precio no puede ser menor a $"+Funciones.formatMoney(pm.getMINPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }else if(rangeControl && pm.getRANGE() && manualPrice > pm.getMAXPRICE()){
                Snackbar.make(getView(), "El precio no puede ser superior a $"+Funciones.formatMoney(pm.getMAXPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }
        }else if(adapter instanceof  SalesRowNoMeasureAdapter || adapter instanceof OrderResumeNoMeasureAdapter){
            Products p = ProductsController.getInstance(context).getProductByCode(codeProduct);
            if(rangeControl && p.isRANGE() && manualPrice < p.getMINPRICE()){
                Snackbar.make(getView(), "El precio no puede ser menor a $"+Funciones.formatMoney(p.getMINPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }else if(rangeControl && p.isRANGE() && manualPrice > p.getMAXPRICE()){
                Snackbar.make(getView(), "El precio no puede ser superior a $"+Funciones.formatMoney(p.getMAXPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }
        }else if(adapter instanceof  OrderResumeAdapter){
            ProductsMeasure pm = ProductsMeasureController.getInstance(context).getProductMeasureByProductAndMeasure(codeProduct, ((OrderDetailModel)tempOrderModel).getCodeMeasure());
            if(rangeControl && pm.getRANGE() && manualPrice < pm.getMINPRICE()){
                Snackbar.make(getView(), "El precio no puede ser menor a $"+Funciones.formatMoney(pm.getMINPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }else if(rangeControl && pm.getRANGE() && manualPrice > pm.getMAXPRICE()){
                Snackbar.make(getView(), "El precio no puede ser superior a $"+Funciones.formatMoney(pm.getMAXPRICE()), Snackbar.LENGTH_SHORT).show();
                etPrice.requestFocus();
                return false;
            }
        }


        return true;
    }
    public void Save(){

        if(validate()){
            saveOrderLine();
        }

    }

    public void saveOrderLine(){
        double quantity = Double.parseDouble(etCantidad.getText().toString());
        if(adapter instanceof NewOrderProductRowAdapter){
            ((NewOrderProductModel)tempOrderModel).setQuantity(String.valueOf((int)quantity));
            ((NewOrderProductModel)tempOrderModel).setManualPrice(etPrice.getText().toString());
            ((NewOrderProductRowAdapter)adapter).EditLineFromExternal((NewOrderProductModel)tempOrderModel, (NewOrderProductRowAdapter.NewOrderProductHolder)holder);
        }else if(adapter instanceof OrderResumeAdapter){
            ((OrderDetailModel)tempOrderModel).setQuantity(String.valueOf((int)quantity));
            ((OrderDetailModel)tempOrderModel).setManualPrice(etPrice.getText().toString());
            ((OrderResumeAdapter)adapter).EditLineFromExternal((OrderDetailModel)tempOrderModel, (OrderResumeAdapter.OrderResumeHolder)holder);
        }else if(adapter instanceof OrderResumeNoMeasureAdapter){
            ((OrderDetailModel)tempOrderModel).setQuantity(String.valueOf((int)quantity));
            ((OrderDetailModel)tempOrderModel).setManualPrice(etPrice.getText().toString());
            ((OrderResumeNoMeasureAdapter)adapter).EditLineFromExternal((OrderDetailModel)tempOrderModel, (OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder)holder);
        }else if(adapter instanceof SalesRowAdapter){
            ((NewOrderProductModel)tempOrderModel).setQuantity(String.valueOf((int)quantity));
            ((NewOrderProductModel)tempOrderModel).setManualPrice(etPrice.getText().toString());
            ((SalesRowAdapter)adapter).EditLineFromExternal((NewOrderProductModel)tempOrderModel, (SalesRowAdapter.SalesRowHolder)holder);
        }else if(adapter instanceof  SalesRowNoMeasureAdapter){
            ((NewOrderProductNoMeasureModel)tempOrderModel).setQuantity(String.valueOf((int)quantity));
            ((NewOrderProductNoMeasureModel)tempOrderModel).setManualPrice(etPrice.getText().toString());
            ((SalesRowNoMeasureAdapter)adapter).EditLineFromExternal((NewOrderProductNoMeasureModel)tempOrderModel, (SalesRowNoMeasureAdapter.SalesRowNoMeasureHolder)holder);
        }
        dismiss();
    }


   /* public void inicializeEditOrderLine(){
        String name="";
        String measure="";
        String quantity = "";
        String price="";

        if(tempOrderModel instanceof NewOrderProductModel){
            NewOrderProductModel obj = (NewOrderProductModel)tempOrderModel;
            name = obj.getName();
            quantity = (obj.getQuantity().equals("0"))?"":obj.getQuantity();
            if(adapter instanceof NewOrderProductRowAdapter){
                fillSpn(obj.getMeasures(), spnUnitMeasure);
                setSpnUnitMeasurePosition((NewOrderProductModel)tempOrderModel);
            }else{
                for(KV2 k : obj.getMeasures()){
                    if(k.getKey().equals(obj.getMeasure())){
                        measure = k.getValue();
                    }
                }
                price = obj.getManualPrice();
            }


        }else if(tempOrderModel instanceof OrderDetailModel){
            OrderDetailModel obj = (OrderDetailModel)tempOrderModel;
            name = obj.getProduct_name();
            quantity = obj.getQuantity();
            measure = obj.getMeasureDescription();
            price = obj.getManualPrice();
        }

        tvMeasure.setText(measure);
        tvName.setText(name);
        etCantidad.setText(quantity);
        etPrice.setText(price);
    }*/


    public void fillSpn(ArrayList<KV2> items, Spinner spn){
        spn.setAdapter(new ArrayAdapter<KV2>(context, android.R.layout.simple_list_item_1,items));
    }
    public void setSpnUnitMeasurePosition(NewOrderProductModel model){
            for (int i = 0; i<spnUnitMeasure.getAdapter().getCount(); i++) {
                if (((KV2)spnUnitMeasure.getAdapter().getItem(i)).getKey().equals(model.getMeasure())) {
                    spnUnitMeasure.setSelection(i);
                    break;
                }
        }
    }


    public void initDialogForNewOrderProductRowAdapter(){
        llMeasure.setVisibility(View.VISIBLE);
        tvMeasure.setVisibility(View.GONE);

        spnUnitMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(tempOrderModel instanceof NewOrderProductModel) {
                    KV2 value = (KV2)spnUnitMeasure.getSelectedItem();
                    ProductsMeasure pm = ProductsMeasureController.getInstance(context).getProductMeasureByProductAndMeasure(((NewOrderProductModel)tempOrderModel).getCodeProduct(), value.getKey());
                    tilPrecio.setHint(Funciones.formatMoney(pm.getPRICE()));
                    etPrice.setText(((NewOrderProductModel)tempOrderModel).getManualPrice().equals("")?pm.getPRICE()+"":((NewOrderProductModel)tempOrderModel).getManualPrice());
                    tvRangePrice.setText(pm.getRANGE()?"Precio Minimo: $"+Funciones.formatMoney(pm.getMINPRICE())+" - Precio Maximo: $"+Funciones.formatMoney(pm.getMAXPRICE()):"");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initDialogForSalesRowAdapter(NewOrderProductModel model){
        llMeasure.setVisibility(View.GONE);
        tvMeasure.setVisibility(View.VISIBLE);

        String codeMeasure = model.getMeasure();
        String codeProduct = model.getCodeProduct();
        String manualPrice = model.getManualPrice();

        ProductsMeasure pm = ProductsMeasureController.getInstance(context).getProductMeasureByProductAndMeasure(codeProduct, codeMeasure);
        double price = pm.getPRICE();
        double minPrice = pm.getMINPRICE();
        double maxPrice = pm.getMAXPRICE();
        boolean range = pm.getRANGE();

        initValues(model.getName(), model.getQuantity(), price, manualPrice, range, minPrice, maxPrice);

    }

    public void initDialogForSalesRowNoMeasureAdapter(NewOrderProductNoMeasureModel model){
        llMeasure.setVisibility(View.GONE);
        tvMeasure.setVisibility(View.INVISIBLE);
        String manualPrice= model.getManualPrice();
        double price = model.getPrice();
        double minPrice = model.getMinPrice();
        double maxPrice = model.getMaxPrice();
        boolean range = model.isRange();

       initValues(model.getName(), model.getQuantity(), price, manualPrice, range, minPrice, maxPrice);
    }



    public void initDialogForOrderResumeAdapter(OrderDetailModel model){
        llMeasure.setVisibility(View.GONE);
        tvMeasure.setVisibility(View.VISIBLE);

        String codeMeasure= model.getCodeMeasure();
        String codeProduct = model.getCodeProduct();
        String manualPrice = model.getManualPrice();

        ProductsMeasure pm = ProductsMeasureController.getInstance(context).getProductMeasureByProductAndMeasure(codeProduct, codeMeasure);
        double price = pm.getPRICE();
        double minPrice = pm.getMINPRICE();
        double maxPrice = pm.getMAXPRICE();
        boolean range = pm.getRANGE();


       initValues(model.getProduct_name(), model.getQuantity(), price, manualPrice, range, minPrice, maxPrice);
    }


    public void initDialogForOrderResumeNoMeasureAdapter(OrderDetailModel model){

        llMeasure.setVisibility(View.GONE);
        tvMeasure.setVisibility(View.INVISIBLE);

        Products p = ProductsController.getInstance(getActivity()).getProductByCode(model.getCodeProduct());
        double price = p.getPRICE();
        double minPrice = p.getMINPRICE();
        double maxPrice = p.getMAXPRICE();
        boolean range = p.isRANGE();
        String manualPrice = model.getManualPrice();


        initValues(model.getProduct_name(), model.getQuantity(), price, manualPrice, range, minPrice, maxPrice);
    }


    public void initValues(String name, String quantity, double price,String manualPrice, boolean range, double minPrice, double maxPrice){

        if(rangeControl && range){
            etPrice.setFocusableInTouchMode(true);
            tilPrecio.setHint(Funciones.formatMoney(price));
            tvRangePrice.setText("Precio Minimo: $"+Funciones.formatMoney(minPrice)+" - Precio Maximo: $"+Funciones.formatMoney(maxPrice));
            etPrice.setText(manualPrice.equals("")?price+"":manualPrice);
        }else{
            tvRangePrice.setText("");
            etPrice.setFocusableInTouchMode(false);
            etPrice.setText(price+"");

        }
        tvName.setText(name);
        etCantidad.setText(quantity);

    }

    public void initMeasure(){
        String measure ="";
        if(tempOrderModel instanceof NewOrderProductModel){
            NewOrderProductModel obj = (NewOrderProductModel)tempOrderModel;

            if(adapter instanceof NewOrderProductRowAdapter){
                fillSpn(obj.getMeasures(), spnUnitMeasure);
                setSpnUnitMeasurePosition((NewOrderProductModel)tempOrderModel);
            }else{
                for(KV2 k : obj.getMeasures()){
                    if(k.getKey().equals(obj.getMeasure())){
                        measure = k.getValue();
                    }
                }
            }
        }else if(tempOrderModel instanceof OrderDetailModel){
            OrderDetailModel obj = (OrderDetailModel)tempOrderModel;
            measure = obj.getMeasureDescription();
        }

        tvMeasure.setText(measure);
    }




}
