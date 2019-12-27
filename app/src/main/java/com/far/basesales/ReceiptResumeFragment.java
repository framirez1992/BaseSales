package com.far.basesales;


import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.Adapters.PaymentAdapter;
import com.far.basesales.Adapters.SalesDetailAdapter;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Dialogs.ReceiptOptionsDialog;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptResumeFragment extends Fragment {

    Activity parentActivity;
    TextView tvCode, tvDate, tvClientName, tvDocument, tvPhone, tvStatus, tvSubTotal, tvDiscount, tvTotal, tvTotalPayment;
    ReceiptRowModel model;
    RecyclerView rvList, rvListPayment;
    ProgressBar pb;
    LinearLayout llGoReceipts, llMenu;
    CardView cvDetails, cvPayments;
    LinearLayout llTotal, llTotalPayment;

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
        rvListPayment = view.findViewById(R.id.rvListPayment);
        rvListPayment.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        tvSubTotal = view.findViewById(R.id.tvSubTotal);
        tvDiscount = view.findViewById(R.id.tvDiscount);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvTotalPayment = view.findViewById(R.id.tvTotalPayment);
        llGoReceipts = view.findViewById(R.id.llGoReceipts);
        llMenu = view.findViewById(R.id.llMenu);
        cvDetails = view.findViewById(R.id.cvDetails);
        cvPayments = view.findViewById(R.id.cvPayments);
        llTotal = view.findViewById(R.id.llTotal);
        llTotalPayment = view.findViewById(R.id.llTotalPayment);

        cvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeList(v);
            }
        });
        cvPayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeList(v);
                searchPayments(false);
            }
        });



        //registerForContextMenu(llMenu);
        llMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // parentActivity.openContextMenu(v);
                showReceiptOptions();
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
       if(model.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
           menu.findItem(R.id.payments).setVisible(false);
       }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getItemId() == R.id.payments){
            //showPaymentDialog();
        }else if(item.getItemId() == R.id.print){
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
            tvSubTotal.setText("$"+Funciones.formatMoney(model.getSubTotal()));
            tvDiscount.setText("$"+Funciones.formatMoney(model.getDiscount()));
            tvTotal.setText("$"+Funciones.formatMoney(model.getTotal()));
            tvTotalPayment.setText("$"+Funciones.formatMoney(model.getPaid()));

            String status ="UNKNOWN";
            if(model.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
                status = "Pagado";
            }else if(model.getStatus().equals(CODES.CODE_RECEIPT_STATUS_OPEN)){
                status = "Abierto";
            }
            tvStatus.setText(status);
            search(false);
        }
    }


    public void search(final boolean fromFireBaseSearch){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<String, Void, ArrayList<SalesDetailModel>> a = new AsyncTask<String, Void, ArrayList<SalesDetailModel>>() {
            @Override
            protected ArrayList<SalesDetailModel> doInBackground(String... strings) {
                return SalesController.getInstance(parentActivity).getSaleDetailModels(model.getCode());
            }

            @Override
            protected void onPostExecute(ArrayList<SalesDetailModel> clientRowModels) {
                super.onPostExecute(clientRowModels);
                if(!fromFireBaseSearch && clientRowModels.size() ==0){
                    searchSale();
                    return;
                }
                pb.setVisibility(View.GONE);
                SalesDetailAdapter adapter = new SalesDetailAdapter(parentActivity, (ListableActivity) parentActivity, clientRowModels);
                rvList.setAdapter(adapter);
                rvList.getAdapter().notifyDataSetChanged();
                rvList.invalidate();
            }
        };
        a.execute();

    }

    public void searchPayments(final boolean fromFireBaseSearch){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<String, Void, ArrayList<Payment>> a = new AsyncTask<String, Void, ArrayList<Payment>>() {
            @Override
            protected ArrayList<Payment> doInBackground(String... strings) {
                ArrayList<Payment> payments = PaymentController.getInstance(parentActivity).getPayments(PaymentController.CODERECEIPT+"=?", new String[]{model.getCode()}, null);
               /* int index = 0;
                while(index < payments.size()){//sacamos los payments que no tengan fecha para obligar a que lo busque.
                    if(payments.get(index).getDATE() == null){
                        payments.remove(index);
                    }else{
                        index++;
                    }
                }*/
                return payments;
            }

            @Override
            protected void onPostExecute(ArrayList<Payment> payments) {
                super.onPostExecute(payments);
                if(!fromFireBaseSearch /*&& payments.size() == 0*/){
                    searchPaymentsFireBase();
                    return;
                }
                pb.setVisibility(View.GONE);
                PaymentAdapter adapter = new PaymentAdapter(parentActivity, (ListableActivity) parentActivity, payments);
                rvListPayment.setAdapter(adapter);
                rvListPayment.getAdapter().notifyDataSetChanged();
                rvListPayment.invalidate();
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
        try{
            ReceiptController.getInstance(parentActivity).createPDF(model.getCode(), 1);
        }catch (Exception e){
         e.printStackTrace();
        }

    }



    public void changeList(View v){

         cvPayments.setCardBackgroundColor(getResources().getColor(R.color.gray_200));
        ((TextView)cvPayments.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_view));

        cvDetails.setCardBackgroundColor(getResources().getColor(R.color.gray_200));
        ((TextView)cvDetails.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_view));

        ((CardView)v).setCardBackgroundColor(getResources().getColor(R.color.white));
        ((TextView)((CardView) v).getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));


        rvList.setVisibility(v.getId()== R.id.cvDetails?View.VISIBLE:View.INVISIBLE);
        llTotal.setVisibility(v.getId()== R.id.cvDetails?View.VISIBLE:View.GONE);

        rvListPayment.setVisibility(v.getId()== R.id.cvPayments?View.VISIBLE:View.INVISIBLE);
        llTotalPayment.setVisibility(v.getId()== R.id.cvPayments?View.VISIBLE:View.GONE);

    }

    public void searchSale(){
        SalesController.getInstance(parentActivity).searchSalesFromFireBaseByCodeReceipt(model.getCode(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                String codeSale = null;
                for(DocumentSnapshot ds: querySnapshot){
                    Sales s = ds.toObject(Sales.class);
                    if(SalesController.getInstance(parentActivity).update(s) <= 0){
                        SalesController.getInstance(parentActivity).insert(s);
                    }
                    codeSale = s.getCODE();
                }

                if(codeSale != null){
                    searchSaleDetail(codeSale);
                }else{
                    Snackbar.make(getView(), "Sale for this receipt not found ", Snackbar.LENGTH_LONG).show();
                }

            }
        }, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getException()!= null){
                    pb.setVisibility(View.GONE);
                    //enableAll();
                    Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                //enableAll();
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public void searchSaleDetail(String codeSale){
        SalesController.getInstance(parentActivity).searchSalesDetailFromFireBaseByCodeSale(codeSale, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for(DocumentSnapshot ds: querySnapshot){
                    SalesDetails sd = ds.toObject(SalesDetails.class);
                    if(SalesController.getInstance(parentActivity).update_Detail(sd) <= 0){
                        SalesController.getInstance(parentActivity).insert_Detail(sd);
                    }
                }
                search(true);
            }
        }, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getException()!= null){
                    pb.setVisibility(View.GONE);
                    //enableAll();
                    Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                //enableAll();
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }



    public void searchPaymentsFireBase(){
            PaymentController.getInstance(parentActivity).searchPaymentFromFireBaseByCodeReceipt(model.getCode(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    for(DocumentSnapshot ds: querySnapshot){
                        Payment s = ds.toObject(Payment.class);
                        if(PaymentController.getInstance(parentActivity).update(s) <= 0){
                            PaymentController.getInstance(parentActivity).insert(s);
                        }
                    }

                   searchPayments(true);
                }
            }, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getException()!= null){
                        pb.setVisibility(View.GONE);
                        //enableAll();
                        Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pb.setVisibility(View.GONE);
                    //enableAll();
                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });

    }



    public void showReceiptOptions(){
        if(model == null){
            Toast.makeText(parentActivity, "unable to get Receipt", Toast.LENGTH_LONG).show();
            return;
        }
        FragmentTransaction ft = ((AppCompatActivity)parentActivity).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)parentActivity).getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ReceiptOptionsDialog.newInstance(parentActivity, ReceiptController.getInstance(parentActivity).getReceiptByCode(model.getCode()));
        newFragment.setCancelable(false);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

}
