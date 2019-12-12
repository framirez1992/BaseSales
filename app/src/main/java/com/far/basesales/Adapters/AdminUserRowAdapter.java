package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.UserRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;

import java.util.ArrayList;

public class AdminUserRowAdapter extends RecyclerView.Adapter<AdminUserRowAdapter.AdminUserRowHolder> {

    Activity activity;
    ArrayList<UserRowModel> objects;
    ListableActivity listableActivity;
    public AdminUserRowAdapter(Activity act, ListableActivity la, ArrayList<UserRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public AdminUserRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new AdminUserRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.admin_user_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserRowHolder holder, final int position) {

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


    public class AdminUserRowHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvUserName,tvPassword,  tvUserRole;
        CheckBox cbActive;
        ImageView imgMenu,imgTime ;
        public AdminUserRowHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPassword = itemView.findViewById(R.id.tvUserPassword);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            cbActive = itemView.findViewById(R.id.cbActive);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(UserRowModel urm){
            tvCode.setText(urm.getCode());
            tvUserName.setText(urm.getUserName());
            tvPassword.setText(urm.getUserPassword());
            tvUserRole.setText(urm.getSystemCodeDescription());
            cbActive.setChecked(urm.isActive());
            imgTime.setVisibility((urm.isInserver())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgMenu;
        }
    }
}
