package com.far.basesales;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.Adapters.SalesDetailAdapter;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptResumeFragment extends Fragment {

    Activity parentActivity;
    TextView tvCode, tvDate, tvClientName, tvDocument, tvPhone, tvStatus, tvTotal;
    ReceiptRowModel model;
    RecyclerView rvList;
    ProgressBar pb;
    LinearLayout llGoReceipts, llMenu;

    public ReceiptResumeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt_resume, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvCode = view.findViewById(R.id.tvCode);
        tvDate = view.findViewById(R.id.tvDate);
        tvClientName = view.findViewById(R.id.tvClientName);
        tvDocument = view.findViewById(R.id.tvDocument);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvStatus= view.findViewById(R.id.tvStatus);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        tvTotal = view.findViewById(R.id.tvTotal);
        llGoReceipts = view.findViewById(R.id.llGoReceipts);
        llMenu = view.findViewById(R.id.llMenu);



        registerForContextMenu(llMenu);
        llMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.openContextMenu(v);
            }
        });

        llGoReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainReceipt)parentActivity).showReceiptList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void setParentActivity(Activity parentActivity){
        this.parentActivity = parentActivity;
    }
    public void setModel(ReceiptRowModel model){
        this.model = model;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = parentActivity.getMenuInflater();
        inflater.inflate(R.menu.receipt_context_menu, menu);
        //menu.setHeaderTitle("Select The Action");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getItemId() == R.id.print){
            printReceipt();
        }else if(item.getItemId() == R.id.email){
            sendEmail();
        }
        return true;
    }

    public void refresh(){
        if(model != null){
            tvCode.setText(model.getCode());
            tvDate.setText(Funciones.getFormatedDateRepDomHour(Funciones.parseStringToDate(model.getDate())));
            tvClientName.setText(model.getClientName());
            tvDocument.setText(model.getClientDocument());
            tvPhone.setText(model.getClientPhone());
            tvTotal.setText("$"+Funciones.formatDecimal(model.getTotal()));

            String status ="UNKNOWN";
            if(model.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
                status = "Pagado";
            }else if(model.getStatus().equals(CODES.CODE_RECEIPT_STATUS_OPEN)){
                status = "Abierto";
            }
            tvStatus.setText(status);
            search();
        }
    }


    public void search(){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<String, Void, ArrayList<SalesDetailModel>> a = new AsyncTask<String, Void, ArrayList<SalesDetailModel>>() {
            @Override
            protected ArrayList<SalesDetailModel> doInBackground(String... strings) {
                return SalesController.getInstance(parentActivity).getSaleDetailModels(model.getCode());
            }

            @Override
            protected void onPostExecute(ArrayList<SalesDetailModel> clientRowModels) {
                super.onPostExecute(clientRowModels);
                pb.setVisibility(View.GONE);
                SalesDetailAdapter adapter = new SalesDetailAdapter(parentActivity, (ListableActivity) parentActivity, clientRowModels);
                rvList.setAdapter(adapter);
                rvList.getAdapter().notifyDataSetChanged();
                rvList.invalidate();
            }
        };
        a.execute();

    }



    public void printReceipt(){
        AsyncTask<Void, Void, String> a = new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((MainReceipt)parentActivity).showLoadingDialog();
            }

            @Override
            protected String doInBackground(Void... voids) {
                try{
                    ReceiptController.getInstance(parentActivity).printReceipt(model.getCode());
                }catch (Exception e){
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ((MainReceipt)parentActivity).closeLoadingDialog();
                if(s!= null){
                    ((MainReceipt)parentActivity).showErrorDialog(s);
                }
            }
        };
        a.execute();
    }

    public void sendEmail(){

    }


}
