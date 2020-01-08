package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class DayAdapter  extends RecyclerView.Adapter<DayAdapter.DayHolder>{

    Activity activity;
    ArrayList<Day> objects;
    public DayAdapter(Activity act, ArrayList<Day> objs){
        this.activity = act;
        this.objects = objs;
    }
    @NonNull
    @Override
    public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new DayHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DayHolder holder, final int position) {

        holder.fillData(objects.get(position));

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }




    public class DayHolder extends RecyclerView.ViewHolder {
        TextView tvDateStart,tvDateEnd,  tvSalesCount, tvSalesTotal, tvCashCount,tvCashAmount,  tvCreditCount,tvCreditAmount,  tvDiscountAmount, tvNetAmount;
        public DayHolder(View itemView) {
            super(itemView);
            tvDateStart = itemView.findViewById(R.id.tvDateStart);
            tvDateEnd= itemView.findViewById(R.id.tvDateEnd);
            tvSalesCount= itemView.findViewById(R.id.tvSalesCount);
            tvSalesTotal= itemView.findViewById(R.id.tvSalesTotal);
            tvCashCount= itemView.findViewById(R.id.tvCashCount);
            tvCashAmount =  itemView.findViewById(R.id.tvCashAmount);
            tvCreditCount= itemView.findViewById(R.id.tvCreditCount);
            tvCreditAmount = itemView.findViewById(R.id.tvCreditAmount);
            tvDiscountAmount= itemView.findViewById(R.id.tvDiscountAmount);
            tvNetAmount= itemView.findViewById(R.id.tvNetAmount);
        }

        public void fillData(Day day){
            tvDateStart.setText(Funciones.getFormatedDateRepDom(day.getDatestart()));
            tvDateEnd.setText(Funciones.getFormatedDateRepDom(day.getDateend()));
            tvSalesCount.setText(day.getSalescount()+"");
            tvSalesTotal.setText(Funciones.formatMoney(day.getSalesamount()));
            tvCashCount.setText(day.getCashpaidcount()+"");
            tvCashAmount.setText(Funciones.formatMoney(day.getCashpaidamount()));
            tvCreditCount.setText(day.getCreditpaidcount()+"");
            tvCreditAmount.setText(Funciones.formatMoney(day.getCreditpaidamount()));
            tvDiscountAmount.setText(Funciones.formatMoney(day.getDiscountamount()));
            tvNetAmount.setText(Funciones.formatMoney(day.getSalesamount()-day.getDiscountamount()));
        }
    }
}
