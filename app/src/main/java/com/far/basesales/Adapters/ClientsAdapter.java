package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientRowHolder> {

    Activity activity;
    ArrayList<ClientRowModel> objects;
    ListableActivity listableActivity;
    ClientRowModel selected;

    public ClientsAdapter(Activity act, ListableActivity la, ArrayList<ClientRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ClientRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ClientRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.client_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientRowHolder holder, final int position) {

        if(selected!= null && selected.getCode().equals(objects.get(position).getCode())){
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            holder.setTextColor(activity.getResources().getColor(R.color.white));
        }else{
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            holder.setTextColor(activity.getResources().getColor(R.color.text_view));
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

    public ClientRowModel getSelected(){
        return selected;
    }

    public class ClientRowHolder extends RecyclerView.ViewHolder {
        TextView tvTitle1, tvTitle2, tvTitle3,tvTitleData,  tvDocument,  tvName, tvPhone, tvData;
        public ClientRowHolder(View itemView) {
            super(itemView);
            tvDocument = itemView.findViewById(R.id.tvDocument);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvData = itemView.findViewById(R.id.tvData);
            tvTitle1 = itemView.findViewById(R.id.tvTitle1);
            tvTitle2 = itemView.findViewById(R.id.tvTitle2);
            tvTitle3 = itemView.findViewById(R.id.tvTitle3);
            tvTitleData = itemView.findViewById(R.id.tvTitleData);

        }

        public void fillData(ClientRowModel crm){
            tvDocument.setText(crm.getDocument());
            tvName.setText(crm.getName());
            tvPhone.setText(Funciones.formatPhone(crm.getPhone()));
            tvData.setText(crm.getData());
        }

        public void setTextColor(int c){
            tvTitle1.setTextColor(c);
            tvTitle2.setTextColor(c);
            tvTitle3.setTextColor(c);
            tvTitleData.setTextColor(c);
            tvDocument.setTextColor(c);
            tvName.setTextColor(c);
            tvPhone.setTextColor(c);
            tvData.setTextColor(c);
        }

    }
}
