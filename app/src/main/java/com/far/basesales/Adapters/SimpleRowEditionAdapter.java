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

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;

import java.util.ArrayList;


public class SimpleRowEditionAdapter extends RecyclerView.Adapter<SimpleRowEditionAdapter.SimpleRowHolder> {

    Activity activity;
    ArrayList<SimpleRowModel> objects;
    ListableActivity listableActivity;
    public SimpleRowEditionAdapter(Activity act, ListableActivity la, ArrayList<SimpleRowModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public SimpleRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SimpleRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.simple_row_edition, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleRowHolder holder, final int position) {

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


    public class SimpleRowHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView imgmenu, imgTime;
        public SimpleRowHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
            imgmenu = itemView.findViewById(R.id.imgMenu);
            imgTime = itemView.findViewById(R.id.imgTime);
        }

        public void fillData(SimpleRowModel model){
            tvText.setText(model.getText());
            imgTime.setVisibility((model.isInServer())?View.INVISIBLE:View.VISIBLE);
        }

        public ImageView getMenuImage(){
            return imgmenu;
        }
    }

}
