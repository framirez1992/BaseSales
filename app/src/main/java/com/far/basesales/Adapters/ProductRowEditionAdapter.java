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

import com.far.basesales.Adapters.Models.ProductRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;

import java.util.ArrayList;

public class ProductRowEditionAdapter extends RecyclerView.Adapter<ProductRowEditionAdapter.ProductRowHolder> {

    Activity activity;
    ArrayList<ProductRowModel> objects;
    ListableActivity listableActivity;
    public ProductRowEditionAdapter(Activity act, ListableActivity la, ArrayList<ProductRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public ProductRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ProductRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.product_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductRowHolder holder, final int position) {

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



    public class ProductRowHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName, tvFamily, tvGroup;
        ImageView imgMenu,imgTime ;
        public ProductRowHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvName = itemView.findViewById(R.id.tvDescription);
            tvFamily = itemView.findViewById(R.id.tvFamily);
            tvGroup = itemView.findViewById(R.id.tvGroup);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(ProductRowModel prm){
            tvCode.setText(prm.getCode());
            tvName.setText(prm.getDescription());
            tvFamily.setText(prm.getCodeTypeDesc());
            tvGroup.setText(prm.getCodeSubTypeDesc());
            imgTime.setVisibility((prm.isInServer())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgMenu;
        }
    }
}

