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


import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.R;

import java.util.ArrayList;

public class SimpleSelectionRowAdapter extends RecyclerView.Adapter<SimpleSelectionRowAdapter.SimpleSelectionRowHolder> {

    Activity activity;
    ArrayList<SimpleSeleccionRowModel> objects;

    public SimpleSelectionRowAdapter(Activity act, ArrayList<SimpleSeleccionRowModel> objs) {
        this.activity = act;
        this.objects = objs;
    }

    @NonNull
    @Override
    public SimpleSelectionRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SimpleSelectionRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.simple_check_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleSelectionRowHolder holder, final int position) {
        holder.fillData(objects.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SimpleSeleccionRowModel obj = objects.get(position);
                    obj.setChecked(!obj.isChecked());
                    notifyDataSetChanged();
                }
            });



    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public ArrayList<SimpleSeleccionRowModel> getObjects() {
        return objects;
    }

    public ArrayList<SimpleSeleccionRowModel> getSelectedObjects() {
        ArrayList<SimpleSeleccionRowModel> selected = new ArrayList<>();
        for(SimpleSeleccionRowModel o: objects){
            if(o.isChecked()){
                selected.add(o);
            }
        }
        return selected;
    }

    public void setSelections(ArrayList<SimpleSeleccionRowModel> selections){
        for (SimpleSeleccionRowModel ssrm: selections){
            for(SimpleSeleccionRowModel item: objects){
                if(item.getCode().equals(ssrm.getCode())){
                    item.setChecked(true);
                    break;
                }
            }
        }

        notifyDataSetChanged();
    }

    public class SimpleSelectionRowHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView cbCheck;
        public SimpleSelectionRowHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            cbCheck = itemView.findViewById(R.id.cbCheck);
        }

        public void fillData(SimpleSeleccionRowModel model){
            tvName.setText(model.getName());
            cbCheck.setImageResource(model.isChecked()?R.drawable.ic_check_box:R.drawable.ic_check_box_outline_blank);

        }
    }
}
