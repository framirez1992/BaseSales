package com.example.bluetoothlibrary.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.bluetoothlibrary.Interfaces.ListableActivity;
import com.example.bluetoothlibrary.Models.Item;
import com.example.bluetoothlibrary.R;

import java.util.ArrayList;

public class ScanAdapter extends ArrayAdapter<Item> {


    ArrayList<Item> objects;
    Context context;
    ListableActivity listableActivity;


    public ScanAdapter(Context context,ListableActivity listableActivity, int resource,  ArrayList<Item> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.listableActivity = listableActivity;

    }

    @Override
    public View getView(final int position,  View convertView,  ViewGroup parent) {
        Item i = objects.get(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_row, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.tvName)).setText(i.toString());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listableActivity.onClick(objects.get(position));
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

}
