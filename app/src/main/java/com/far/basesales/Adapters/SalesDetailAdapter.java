package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;


public class SalesDetailAdapter extends RecyclerView.Adapter<SalesDetailAdapter.SalesDetailHolder>{

    Activity activity;
    ArrayList<SalesDetailModel> objects;
    ListableActivity listableActivity;
    public SalesDetailAdapter(Activity act, ListableActivity la, ArrayList<SalesDetailModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public SalesDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SalesDetailHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_sales_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SalesDetailHolder holder, final int position) {

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




    public class SalesDetailHolder extends RecyclerView.ViewHolder {
        TextView tvQuantity, tvMeasure, tvDescription, tvTotal;
        public SalesDetailHolder(View itemView) {
            super(itemView);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvMeasure = itemView.findViewById(R.id.tvMeasure);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }

        public void fillData(SalesDetailModel obj){
            tvQuantity.setText(obj.getQuantity());
            tvMeasure.setText(obj.getMeasureDescription());
            tvDescription.setText(obj.getProductDescription());
            double amount = 0.0;
            try{
                amount = Double.parseDouble(obj.getTotal());
                tvTotal.setText("$"+ Funciones.formatMoney(amount));
            }catch (Exception e){
                tvTotal.setText(obj.getTotal());
            }


        }
    }
}
