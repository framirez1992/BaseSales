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


public class UserRowEditionAdapter extends RecyclerView.Adapter<UserRowEditionAdapter.UserRowHolder> {

    Activity activity;
    ArrayList<UserRowModel> objects;
    ListableActivity listableActivity;
    public UserRowEditionAdapter(Activity act, ListableActivity la, ArrayList<UserRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public UserRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UserRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.user_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserRowHolder holder, final int position) {

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


    public class UserRowHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvUserName, tvUserRole;
        CheckBox cbActive;
        ImageView imgMenu,imgTime ;
        public UserRowHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            cbActive = itemView.findViewById(R.id.cbActive);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(UserRowModel urm){
            tvCode.setText(urm.getCode());
            tvUserName.setText(urm.getUserName());
            tvUserRole.setText(urm.getUserRole());
            cbActive.setChecked(urm.isActive());
            imgTime.setVisibility((urm.isInserver())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgMenu;
        }
    }
}
