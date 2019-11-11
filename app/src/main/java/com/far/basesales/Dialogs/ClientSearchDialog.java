package com.far.basesales.Dialogs;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.far.basesales.Adapters.ClientsAdapter;
import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.MainOrders;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;

public class ClientSearchDialog extends DialogFragment {

    Activity parentActivity;
    TextInputEditText etSearch;
    RecyclerView rvList;
    ProgressBar pb;
    ClientsController clientsController;
    CardView btnOK;

    public  static ClientSearchDialog newInstance(Activity parentActivity) {
        ClientSearchDialog f = new ClientSearchDialog();
        f.parentActivity = parentActivity;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientsController = ClientsController.getInstance(parentActivity);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
        Funciones.showKeyBoard(etSearch);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.client_search_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void init(View view){
        etSearch = view.findViewById(R.id.etSearch);
        rvList= view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        btnOK = view.findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClientRowModel crm = rvList.getAdapter()!= null?((ClientsAdapter)rvList.getAdapter()).getSelected():null;
                if(crm!= null){
                    ((MainOrders)parentActivity).setSelectedClientReceipt(crm);
                    dismiss();
                }else{
                    Funciones.hideKeyBoard(etSearch,parentActivity);
                    Snackbar.make(getView(), "Seleccione un cliente.", Snackbar.LENGTH_LONG).show();
                }


            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
             search(s.toString());
            }
        });

    }



    public void search(String text){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<String, Void, ArrayList<ClientRowModel>> a = new AsyncTask<String, Void, ArrayList<ClientRowModel>>() {
            @Override
            protected ArrayList<ClientRowModel> doInBackground(String... strings) {
                String value = strings[0];
                String where = ClientsController.DOCUMENT+" like ? OR "+ ClientsController.NAME+" like ? OR "+ClientsController.PHONE+" like ? ";
                return ClientsController.getInstance(parentActivity).getClientSRM(where, new String[]{"%"+value+"%", "%"+value+"%", "%"+value+"%"}, ClientsController.NAME);
            }

            @Override
            protected void onPostExecute(ArrayList<ClientRowModel> clientRowModels) {
                super.onPostExecute(clientRowModels);
                pb.setVisibility(View.GONE);
                ClientsAdapter adapter = new ClientsAdapter(parentActivity, (ListableActivity) parentActivity, clientRowModels);
                rvList.setAdapter(adapter);
                rvList.getAdapter().notifyDataSetChanged();
                rvList.invalidate();
            }
        };
        a.execute(text);

    }






}
