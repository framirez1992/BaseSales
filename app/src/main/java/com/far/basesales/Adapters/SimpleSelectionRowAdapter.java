package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.R;

import java.util.ArrayList;

public class SimpleSelectionRowAdapter extends RecyclerView.Adapter<SimpleSelectionRowAdapter.SimpleSelectionRowHolder> {

    Activity activity;
    ArrayList<SimpleSeleccionRowModel> objects;
    ArrayList<SimpleSeleccionRowModel> selectedObjects;
    boolean noEditable;

    public SimpleSelectionRowAdapter(Activity act, ArrayList<SimpleSeleccionRowModel> objs, ArrayList<SimpleSeleccionRowModel> selectedObjs) {
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
    }

    public SimpleSelectionRowAdapter(Activity act, ArrayList<SimpleSeleccionRowModel> objs, ArrayList<SimpleSeleccionRowModel> selectedObjs, boolean noEditable) {
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
        this.noEditable = noEditable;
    }
    @NonNull
    @Override
    public SimpleSelectionRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SimpleSelectionRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.simple_check_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleSelectionRowHolder holder, final int position) {

        if(isSelected(objects.get(position))){
            objects.get(position).setChecked(true);
        }
        CheckBox cb =  holder.getCbCheck();
        cb.setOnCheckedChangeListener(null);
        holder.fillData(objects.get(position));

        if(noEditable) {
            cb.setEnabled(false);
            cb.setFocusable(false);

        }else {
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    objects.get(position).setChecked(isChecked);
                    if (isChecked && findPositionInSeleted(objects.get(position)) == -1) {//SI NO ESTA AGREGALO
                        selectedObjects.add(objects.get(position));
                    } else {
                        selectedObjects.remove(findPositionInSeleted(objects.get(position)));
                    }

                    notifyDataSetChanged();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cbCheck = v.findViewById(R.id.cbCheck);
                    cbCheck.setChecked(!cbCheck.isChecked());
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public ArrayList<SimpleSeleccionRowModel> getObjects() {
        return objects;
    }

    public boolean isSelected(SimpleSeleccionRowModel srm){
        for(SimpleSeleccionRowModel selected: selectedObjects){
            if(srm.getCode().equals(selected.getCode())){
                return true;
            }
        }
        return false;
    }

    public void setObjects(ArrayList<SimpleSeleccionRowModel> objs){
        this.objects.clear();
        this.objects.addAll(objs);
        notifyDataSetChanged();
    }
    public int findPositionInSeleted(SimpleSeleccionRowModel srm){
        for(int i = 0; i < selectedObjects.size(); i++){
            if(selectedObjects.get(i).getCode().equals(srm.getCode())){
                return i;
            }
        }
        return  -1;
    }

    public ArrayList<SimpleSeleccionRowModel> getSelectedObjects() {
        return selectedObjects;
    }


    public class SimpleSelectionRowHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox cbCheck;
        public SimpleSelectionRowHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            cbCheck = itemView.findViewById(R.id.cbCheck);
        }

        public void fillData(SimpleSeleccionRowModel model){
            tvName.setText(model.getName());
            cbCheck.setOnCheckedChangeListener(null);
            cbCheck.setChecked(model.isChecked());

        }

        public CheckBox getCbCheck() {
            return cbCheck;
        }
    }
}
