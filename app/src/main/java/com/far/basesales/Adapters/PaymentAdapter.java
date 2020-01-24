package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class PaymentAdapter  extends RecyclerView.Adapter<PaymentAdapter.PaymentHolder>{

    Activity activity;
    ArrayList<Payment> objects;
    ListableActivity listableActivity;
    public PaymentAdapter(Activity act, ListableActivity la, ArrayList<Payment> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public PaymentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new PaymentHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.payment_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentHolder holder, final int position) {

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




    public class PaymentHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDescription, tvTotal;
        public PaymentHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }

        public void fillData(Payment obj){
            String description = obj.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)?"TARJETA DE CREDITO":"EFECTIVO";
            tvDate.setText(Funciones.getFormatedDateRepDomHour(obj.getDATE()));
            tvDescription.setText(description);
            tvTotal.setText("$"+Funciones.formatMoney(obj.getTOTAL()));
        }
    }
}
