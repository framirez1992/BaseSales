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

import com.far.basesales.Adapters.Models.UserControlRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;

import java.util.ArrayList;

public class UserControlRowEditionAdapter extends RecyclerView.Adapter<UserControlRowEditionAdapter.UserControlRowHolder> {

    Activity activity;
    ArrayList<UserControlRowModel> objects;
    ListableActivity listableActivity;
    public UserControlRowEditionAdapter(Activity act, ListableActivity la, ArrayList<UserControlRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public UserControlRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UserControlRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.usercontrol_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserControlRowHolder holder, final int position) {

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

    public class UserControlRowHolder extends RecyclerView.ViewHolder {
        TextView tvControl, tvTarget, tvTargetDescription;
        CheckBox cbActive;
        ImageView imgMenu,imgTime ;
        public UserControlRowHolder(View itemView) {
            super(itemView);
            tvControl = itemView.findViewById(R.id.tvControl);
            tvTarget = itemView.findViewById(R.id.tvTarget);
            tvTargetDescription = itemView.findViewById(R.id.tvTargetDescription);
            cbActive = itemView.findViewById(R.id.cbActive);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(UserControlRowModel u){
            tvControl.setText(u.getDescription());
            tvTarget.setText(u.getTargetDescription());
            tvTargetDescription.setText(u.getTargetCodedescription());
            cbActive.setChecked(u.isActive());
            imgTime.setVisibility((u.isInserver())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgMenu;
        }
    }

}
