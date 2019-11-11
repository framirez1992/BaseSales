package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.EditSelectionRowModel;
import com.far.basesales.R;

import java.util.ArrayList;


public class EditSelectionRowAdapter extends RecyclerView.Adapter<EditSelectionRowAdapter.EditSelectionRowHolder> {

    Activity activity;
    ArrayList<EditSelectionRowModel> objects;
    ArrayList<EditSelectionRowModel> selectedObjects;
    boolean noEditable;

    public EditSelectionRowAdapter(Activity act, ArrayList<EditSelectionRowModel> objs, ArrayList<EditSelectionRowModel> selectedObjs) {
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
    }

    public EditSelectionRowAdapter(Activity act, ArrayList<EditSelectionRowModel> objs, ArrayList<EditSelectionRowModel> selectedObjs, boolean noEditable) {
        this.activity = act;
        this.objects = objs;
        this.selectedObjects = selectedObjs;
        this.noEditable = noEditable;
    }
    @NonNull
    @Override
    public EditSelectionRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new EditSelectionRowHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.edit_selection_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EditSelectionRowHolder holder, final int position) {

        if(isSelected(objects.get(position))){
            objects.get(position).setChecked(true);
            objects.get(position).setText(selectedObjects.get(findPositionInSeleted(objects.get(position))).getText());
        }
        holder.fillData(objects.get(position));

        CheckBox cb =  holder.getCbCheck();
        final EditText etText = holder.getEtEditable();
        //if(noEditable) {
          //  cb.setEnabled(false);
           // cb.setFocusable(false);

       // }else {
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    objects.get(position).setChecked(isChecked);
                    if (isChecked && findPositionInSeleted(objects.get(position)) == -1) {//SI NO ESTA AGREGALO
                        selectedObjects.add(objects.get(position));
                    } else {
                        selectedObjects.remove(findPositionInSeleted(objects.get(position)));
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cbCheck = v.findViewById(R.id.cbCheck);
                    cbCheck.setChecked(!cbCheck.isChecked());
                }
            });
       // }

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //if(objects.get(position).isChecked()){
                objects.get(position).setText(s.toString());
                if(findPositionInSeleted(objects.get(position)) != -1){
                    selectedObjects.get(findPositionInSeleted(objects.get(position))).setText(s.toString());
                }
                //}
            }
        });


    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public ArrayList<EditSelectionRowModel> getObjects() {
        return objects;
    }

    public boolean isSelected(EditSelectionRowModel srm){
        for(EditSelectionRowModel selected: selectedObjects){
            if(srm.getCode().equals(selected.getCode())){
                return true;
            }
        }
        return false;
    }

    public void setObjects(ArrayList<EditSelectionRowModel> objs){
        this.objects.clear();
        this.objects.addAll(objs);
        notifyDataSetChanged();
    }
    public int findPositionInSeleted(EditSelectionRowModel srm){
        for(int i = 0; i < selectedObjects.size(); i++){
            if(selectedObjects.get(i).getCode().equals(srm.getCode())){
                return i;
            }
        }
        return  -1;
    }

    public ArrayList<EditSelectionRowModel> getSelectedObjects() {
        return selectedObjects;
    }



    public class EditSelectionRowHolder extends RecyclerView.ViewHolder {
        TextView tvDescription;
        EditText etEditable;
        CheckBox cbCheck;
        public EditSelectionRowHolder(View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            etEditable = itemView.findViewById(R.id.etEdit);
            cbCheck = itemView.findViewById(R.id.cbCheck);
        }

        public void fillData(EditSelectionRowModel obj){
            tvDescription.setText(obj.getDescription());
            etEditable.setText(obj.getText());
            cbCheck.setChecked(obj.isChecked());
        }

        public CheckBox getCbCheck() {
            return cbCheck;
        }

        public EditText getEtEditable() {
            return etEditable;
        }
    }
}