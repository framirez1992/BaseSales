package com.far.basesales;


import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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

import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.Models.SalesDetailModel;
import com.far.basesales.Adapters.PaymentAdapter;
import com.far.basesales.Adapters.SalesDetailAdapter;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Generic.KV;
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
    TextView tvCode, tvDate, tvClientName, tvDocument, tvPhone, tvStatus, tvTotal, tvTotalPayment;
    ReceiptRowModel model;
    RecyclerView rvList, rvListPayment;
    ProgressBar pb;
    LinearLayout llGoReceipts, llMenu;
    Dialog paymentDialog;
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
                searchPayments();
            }
        });



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
       if(model.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
           menu.findItem(R.id.payments).setVisible(false);
       }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        if(item.getItemId() == R.id.payments){
            showPaymentDialog();
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
            tvTotal.setText("$"+Funciones.formatDecimal(model.getTotal()));
            tvTotalPayment.setText("$"+Funciones.formatDecimal(model.getPaid()));

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

    public void searchPayments(){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<String, Void, ArrayList<Payment>> a = new AsyncTask<String, Void, ArrayList<Payment>>() {
            @Override
            protected ArrayList<Payment> doInBackground(String... strings) {
                return PaymentController.getInstance(parentActivity).getPayments(PaymentController.CODERECEIPT+"=?", new String[]{model.getCode()}, null);
            }

            @Override
            protected void onPostExecute(ArrayList<Payment> payments) {
                super.onPostExecute(payments);
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

    public void showPaymentDialog(){
        paymentDialog = null;
        paymentDialog = new Dialog(parentActivity);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.payment_dialog);
        final Spinner spnPaymentType = paymentDialog.findViewById(R.id.spnPaymentType);
        final TextInputEditText etAmount = paymentDialog.findViewById(R.id.etAmount);
        final CardView cvPay = paymentDialog.findViewById(R.id.cvPay);
        TextView tvPendingAmount = paymentDialog.findViewById(R.id.tvPendingAmount);

        PaymentController.getInstance(parentActivity).fillSpinnerPaymentType(spnPaymentType);
        etAmount.setText(Funciones.formatDecimal(model.getTotal()-model.getPaid()));
        tvPendingAmount.setText("$"+Funciones.formatDecimal(model.getTotal()-model.getPaid()));
        if(UserControlController.getInstance(parentActivity).multiPayment()) {
            etAmount.setFocusableInTouchMode(true);
        }
        cvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEditedAmount(etAmount)){
                    paymentDialog.setCancelable(false);
                    cvPay.setEnabled(false);
                    savePayment(((KV)spnPaymentType.getSelectedItem()).getKey(), etAmount.getText().toString());
                }
            }
        });

        paymentDialog.show();
        Window window = paymentDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }

    public boolean validateEditedAmount(TextInputEditText etAmount){
        String editAmount = etAmount.getText().toString();
        double editAmountD=0.0;
        if(editAmount.isEmpty()){
            ((MainReceipt)parentActivity).showErrorDialog("Introduzca un monto valido");
            return false;
        }
        try{
            editAmountD=  Double.parseDouble(editAmount.replace(",", "").replace("$", ""));
        }catch (Exception e){
            ((MainReceipt)parentActivity).showErrorDialog("Introduzca un monto valido");
            return false;
        }
        if (editAmountD > (model.getTotal()-model.getPaid())){
            ((MainReceipt)parentActivity).showErrorDialog("El importe no puede ser superior a la deuda.");
            return false;
        }
        return true;
    }

    public void savePayment(String paymentType, String editAmount){
        paymentDialog.dismiss();
        paymentDialog=null;
        ((MainReceipt)parentActivity).showLoadingDialog();

        double paymentAmount = Double.parseDouble(editAmount.replace(",", "").replace("$", ""));

        String receiptStatus = (model.getTotal()> (model.getPaid()+paymentAmount))?CODES.CODE_RECEIPT_STATUS_OPEN:CODES.CODE_RECEIPT_STATUS_CLOSED;
        //String code, String codeUser,String codesale, String codeclient,  String status, String ncf, double subTotal, double taxes, double discount, double total, double paidAmount
        Receipts r =ReceiptController.getInstance(parentActivity).getReceiptByCode(model.getCode());
        r.setStatus(receiptStatus);
        r.setPaidamount(r.getPaidamount()+paymentAmount);
        r.setMdate(null);//para que lo envi con TIMESTAMP
        //String code, String codeReceipt,String codeUser, String codeClient, String type, double subTotal, double tax, double discount, double total
        Payment p = new Payment(Funciones.generateCode(), r.getCode(), Funciones.getCodeuserLogged(parentActivity),model.getCodeClient(), paymentType,0,0,0,paymentAmount);

        ReceiptController.getInstance(parentActivity).update(r);
        PaymentController.getInstance(parentActivity).insert(p);
        ((MainReceipt)parentActivity).addPayment(r, p);

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

}
