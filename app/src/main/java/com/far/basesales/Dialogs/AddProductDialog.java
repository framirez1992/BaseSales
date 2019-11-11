package com.far.basesales.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.NewOrderProductModel;
import com.far.basesales.Adapters.Models.OrderDetailModel;
import com.far.basesales.Adapters.NewOrderProductRowAdapter;
import com.far.basesales.Adapters.OrderResumeAdapter;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Generic.KV;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

public class AddProductDialog extends DialogFragment {

    Object tempOrderModel;
    TextView tvName;
    TextView tvUnidad;
    TextInputEditText etCantidad;
    TempOrdersController tempOrdersController;
    MeasureUnitsController measureUnitsController;
    TextView btnOK;
    RecyclerView.Adapter adapter;
    RecyclerView.ViewHolder holder;

    public  static AddProductDialog newInstance(Object pt, RecyclerView.ViewHolder holder, RecyclerView.Adapter adapter) {
        AddProductDialog f = new AddProductDialog();
        f.tempOrderModel = pt;
        f.holder = holder;
        f.adapter = adapter;

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tempOrdersController = TempOrdersController.getInstance(getActivity());
        measureUnitsController = MeasureUnitsController.getInstance(getActivity());
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
        tvName = view.findViewById(R.id.tvName);
        tvUnidad = view.findViewById(R.id.tvUnitMeasure);
        etCantidad = view.findViewById(R.id.etCantidad);
        btnOK = view.findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                   saveOrderLine();
                }

            }
        });

        inicializeEditOrderLine();
    }


    public boolean validate(){
        if(etCantidad.getText().toString().trim().equals("") || etCantidad.getText().toString().trim().equals(".") || etCantidad.getText().toString().trim().equals("0")){
            Snackbar.make(getView(), "La cantidad debe ser mayor a 0", Snackbar.LENGTH_SHORT).show();
            return false;
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
            ((NewOrderProductRowAdapter)adapter).EditLineFromExternal((NewOrderProductModel)tempOrderModel, (NewOrderProductRowAdapter.NewOrderProductHolder)holder);
        }else if(adapter instanceof OrderResumeAdapter){
            ((OrderDetailModel)tempOrderModel).setQuantity(String.valueOf((int)quantity));
            ((OrderResumeAdapter)adapter).EditLineFromExternal((OrderDetailModel)tempOrderModel, (OrderResumeAdapter.OrderResumeHolder)holder);
        }
        dismiss();
    }


    public void inicializeEditOrderLine(){
        String name="";
        String measure="";
        String quantity = "";

        if(tempOrderModel instanceof NewOrderProductModel){
            NewOrderProductModel obj = (NewOrderProductModel)tempOrderModel;
            name = obj.getName();
            quantity = (obj.getQuantity().equals("0"))?"":obj.getQuantity();
            for(KV m: obj.getMeasures()){
                if(m.getKey().equals(obj.getMeasure())){
                   measure = m.getValue();
                    break;
                }
            }
        }else if(tempOrderModel instanceof OrderDetailModel){
            OrderDetailModel obj = (OrderDetailModel)tempOrderModel;
            name = obj.getProduct_name();
            quantity = obj.getQuantity();
            measure = obj.getMeasureDescription();
        }


        tvName.setText(name);
        tvUnidad.setText(measure);
        etCantidad.setText(quantity);
        etCantidad.setSelectAllOnFocus(true);
    }

}
