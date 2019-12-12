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

import com.far.basesales.Adapters.Models.LicenseRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;

import java.util.ArrayList;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseRowHolder> {

    Activity activity;
    ArrayList<LicenseRowModel> objects;
    ListableActivity listableActivity;
    public LicenseAdapter(Activity act, ListableActivity la, ArrayList<LicenseRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public LicenseRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new LicenseRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.license_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseRowHolder holder, final int position) {

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


    public class LicenseRowHolder extends RecyclerView.ViewHolder {
        TextView tvCode,  tvClientName, tvDateIni, tvDateEnd, tvDays, tvCounter, tvDevices;
        CheckBox cbStatus;
        ImageView imgMenu,imgTime;
        public LicenseRowHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            //tvDateIni = itemView.findViewById(R.id.tvDateIni);
            tvDateEnd = itemView.findViewById(R.id.tvDateEnd);
            //tvDays = itemView.findViewById(R.id.tvDays);
            //tvCounter = itemView.findViewById(R.id.tvCounter);
            tvDevices = itemView.findViewById(R.id.tvDevices);
            cbStatus = itemView.findViewById(R.id.cbStatus);

            imgMenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(LicenseRowModel lrm){
            tvCode.setText(lrm.getCode());
            tvClientName.setText(lrm.getClientName());
            //tvDateIni.setText(lrm.getDateIni());
            tvDateEnd.setText(lrm.getDateEnd());
            //tvDays.setText(lrm.getDays());
            //tvCounter.setText(lrm.getCounter());
            tvDevices.setText(lrm.getDevices());
            cbStatus.setChecked(lrm.isStatus());
            imgTime.setVisibility((lrm.isInServer())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgMenu;
        }
    }
}
