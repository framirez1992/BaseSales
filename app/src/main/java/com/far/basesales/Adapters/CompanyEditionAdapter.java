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

import com.far.basesales.Adapters.Models.CompanyRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class CompanyEditionAdapter extends RecyclerView.Adapter<CompanyEditionAdapter.CompanyRowHolder> {

    Activity activity;
    ArrayList<CompanyRowModel> objects;
    ListableActivity listableActivity;
    public CompanyEditionAdapter(Activity act, ListableActivity la, ArrayList<CompanyRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public CompanyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new CompanyRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.company_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyRowHolder holder, final int position) {

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


    public class CompanyRowHolder extends RecyclerView.ViewHolder {
        TextView tvRnc, tvName, tvPhone, tvPhone2, tvAddress, tvAddress2;
        ImageView imgmenu, imgTime;
        public CompanyRowHolder(View itemView) {
            super(itemView);
            tvRnc= itemView.findViewById(R.id.tvRnc);
            tvName= itemView.findViewById(R.id.tvName);
            tvPhone= itemView.findViewById(R.id.tvPhone);
            tvPhone2= itemView.findViewById(R.id.tvPhone2);
            tvAddress= itemView.findViewById(R.id.tvAddress);
            tvAddress2= itemView.findViewById(R.id.tvAddress2);
            imgmenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(CompanyRowModel model){
            tvRnc.setText(model.getRnc());
            tvName.setText(model.getName());
            tvPhone.setText(Funciones.formatPhone(model.getPhone()));
            tvPhone2.setText(model.getPhone2());
            tvAddress.setText(model.getAddress());
            tvAddress2.setText(model.getAddress2());
            imgTime.setVisibility((model.isInserver())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgmenu;
        }
    }

}
