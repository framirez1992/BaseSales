package com.far.basesales.Dialogs;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ProductMeasureRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class ProductMeasureSelectionAdapter extends RecyclerView.Adapter<ProductMeasureSelectionAdapter.ProductMeasureRowHolder> {

    Activity activity;
    ListableActivity listableActivity;
    ArrayList<ProductMeasureRowModel> objects;
    ArrayList<ProductMeasureRowModel> previousSaved;

    public ProductMeasureSelectionAdapter(Activity act, ListableActivity la,  ArrayList<ProductMeasureRowModel> objs, ArrayList<ProductMeasureRowModel> previousSaved) {
        this.activity = act;
        this.listableActivity = la;
        this.objects = objs;
        this.previousSaved = previousSaved;


        for(ProductMeasureRowModel pm: objects){
            if (isSaved(pm)) {
                ProductMeasureRowModel p= previousSaved.get(findPositionInPreviousSaved(pm));
                pm.setChecked(p.isChecked());
                pm.setAmount(p.getAmount());
                pm.setMaxPrice(p.getMaxPrice());
                pm.setMinPrice(p.getMinPrice());
                pm.setPriceRange(p.isPriceRange());

            }
        }

    }

    @NonNull
    @Override
    public ProductMeasureRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductMeasureRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.product_measure_edit_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductMeasureRowHolder holder, final int position) {
        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listableActivity.onClick(objects.get(position));
            }
        });



    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public ArrayList<ProductMeasureRowModel> getObjects() {
        return objects;
    }

    public boolean isSaved(ProductMeasureRowModel srm) {
        for (ProductMeasureRowModel selected : previousSaved) {
            if (srm.getCodeMeasure().equals(selected.getCodeMeasure())) {
                return true;
            }
        }
        return false;
    }

    public void setObjects(ArrayList<ProductMeasureRowModel> objs) {
        this.objects.clear();
        this.objects.addAll(objs);
        notifyDataSetChanged();
    }

    public int findPositionInPreviousSaved(ProductMeasureRowModel srm) {
        for (int i = 0; i < previousSaved.size(); i++) {
            if (previousSaved.get(i).getCodeMeasure().equals(srm.getCodeMeasure())) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<ProductMeasureRowModel> getSelectedObjects() {
        ArrayList<ProductMeasureRowModel> selectedObjects = new ArrayList<>();
        for(ProductMeasureRowModel o: objects){
            if(o.isChecked()){
                selectedObjects.add(o);
            }
        }
        return selectedObjects;
    }

    public void update(){
        /*if(o.isChecked()){
            if(isSelected(o)){//Actualizar en el array de selected
                ProductMeasureRowModel selected = selectedObjects.get(findPositionInSeleted(o));
                selected.setChecked(o.isChecked());
                selected.setAmount(o.getAmount());
                selected.setPriceRange(o.isPriceRange());
                selected.setMinPrice(o.getMinPrice());
                selected.setMaxPrice(o.getMaxPrice());
            }else{//agregalo a selected
                selectedObjects.add(o);
            }
        }else{
            if(isSelected(o)){//si esta e selected sacalo
                selectedObjects.remove(0);
            }
        }*/


        notifyDataSetChanged();//ya se actualizo la referencia del elemento mientras estaba afuera
    }


    public class ProductMeasureRowHolder extends RecyclerView.ViewHolder {
        TextView tvMeasure,etAmount, tvMinPrice, tvMaxPrice;
        CheckBox cbCheck, cbCheckRange;

        public ProductMeasureRowHolder(View itemView) {
            super(itemView);
            tvMeasure = itemView.findViewById(R.id.tvMeasure);
            tvMinPrice = itemView.findViewById(R.id.tvMinPrice);
            tvMaxPrice = itemView.findViewById(R.id.tvMaxPrice);
            etAmount = itemView.findViewById(R.id.etAmount);
            cbCheck = itemView.findViewById(R.id.cbCheck);
            cbCheckRange = itemView.findViewById(R.id.cbCheckRange);
        }

        public void fillData(ProductMeasureRowModel obj) {
            tvMeasure.setText(obj.getMeasureDescription());
            etAmount.setText("$"+Funciones.formatMoney(obj.getAmount()));
            tvMinPrice.setText("$"+Funciones.formatMoney(obj.getMinPrice()));
            tvMaxPrice.setText("$"+Funciones.formatMoney(obj.getMaxPrice()));
            cbCheckRange.setChecked(obj.isPriceRange());
            cbCheck.setChecked(obj.isChecked());
        }

    }
}
