package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class ClientEditionAdapter extends RecyclerView.Adapter<ClientEditionAdapter.ClientRowHolder> {

    Activity activity;
    ArrayList<ClientRowModel> objects;
    ListableActivity listableActivity;
    public ClientEditionAdapter(Activity act, ListableActivity la, ArrayList<ClientRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ClientRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ClientRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.client_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientRowHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.getMenuImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    public class ClientRowHolder extends RecyclerView.ViewHolder {
        TextView tvDocument,  tvName, tvPhone, tvBirth;
        ImageView imgMenu,imgTime ;
        public ClientRowHolder(View itemView) {
            super(itemView);
            tvDocument = itemView.findViewById(R.id.tvDocument);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvBirth = itemView.findViewById(R.id.tvBirth);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(ClientRowModel crm){
            tvDocument.setText(crm.getDocument());
            tvName.setText(crm.getName());
            tvPhone.setText(Funciones.formatPhone(crm.getPhone()));
            tvBirth.setText(crm.getData());
            imgTime.setVisibility((crm.isInserver())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgMenu;
        }
    }
}
