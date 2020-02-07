package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptRowHolder> {

    Activity activity;
    ArrayList<ReceiptRowModel> objects;
    ListableActivity listableActivity;
    ReceiptRowModel selected;
    boolean clientsControl;

    public ReceiptAdapter(Activity act, ListableActivity la, ArrayList<ReceiptRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;

        clientsControl = UserControlController.getInstance(act).searchSimpleControl(CODES.USERSCONTROL_CLIENTS)!= null;
    }

    @NonNull
    @Override
    public ReceiptRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ReceiptRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.receipt_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptRowHolder holder, final int position) {

        if(selected!= null && selected.getCode().equals(objects.get(position).getCode())){
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.amber_200));
        }else{
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }

        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = objects.get(position);
                listableActivity.onClick(selected);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public ReceiptRowModel getSelected(){
        return selected;
    }

    public class ReceiptRowHolder extends RecyclerView.ViewHolder {
        TextView tvCode,  tvDate, tvClientName, tvClientDocument, tvClientPhone, tvStatus,tvTotal, tvPaid;
        public ReceiptRowHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvClientName = itemView.findViewById(R.id.tvName);
            tvClientDocument = itemView.findViewById(R.id.tvDocument);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvClientPhone = itemView.findViewById(R.id.tvPhone);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvPaid = itemView.findViewById(R.id.tvPaid);

            tvClientName.setVisibility(clientsControl?View.VISIBLE:View.GONE);
            tvClientPhone.setVisibility(clientsControl?View.VISIBLE:View.GONE);
            tvClientDocument.setVisibility(clientsControl?View.VISIBLE:View.GONE);
        }

        public void fillData(ReceiptRowModel obj){
           tvCode.setText(obj.getReceiptNum());
           tvDate.setText(Funciones.getFormatedDateRepDomHour(Funciones.parseStringToDate(obj.getDate())));
           tvClientName.setText(obj.getClientName());
           tvClientDocument.setText(obj.getClientDocument());
           tvClientPhone.setText(obj.getClientPhone());
           tvTotal.setText("$"+Funciones.formatMoney(obj.getTotal()));
           tvPaid.setText("$"+Funciones.formatMoney(obj.getPaid()));
           String status ="UNKNOWN";
           if(obj.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
              status = "Pagado";
           }else if(obj.getStatus().equals(CODES.CODE_RECEIPT_STATUS_OPEN)){
               status = "Abierto";
           }
           tvStatus.setText(status);

        }

    }
}
