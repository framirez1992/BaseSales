package com.far.basesales;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Dialogs.ClientSearchDialog;
import com.far.basesales.Dialogs.ClientsDialogFragment;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.Utils.Funciones;
import com.far.farpdf.Entities.Client;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptFragment extends Fragment implements DialogCaller {


    TextInputEditText etDocument, etName, etAmount, etDiscount, etDiscountDescription;
    TextView tvTotal;
    CardView btnSearch, btnAddClient;
    LinearLayout llGoResumen;
    LinearLayout llPay;
    ClientSearchDialog dialog;
    ClientRowModel client;
    Spinner spnPaymentType;
    CheckBox cbDiscount;

    Activity parentActivity;
    public ReceiptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTotal = view.findViewById(R.id.tvTotal);
        etDiscount = view.findViewById(R.id.etDiscount);
        etDiscountDescription = view.findViewById(R.id.etDiscountDescription);
        etDocument = view.findViewById(R.id.etDocument);
        etName = view.findViewById(R.id.etName);
        etAmount = view.findViewById(R.id.etAmount);
        llGoResumen = view.findViewById(R.id.llGoResumen);
        llPay = view.findViewById(R.id.llPay);
        btnSearch = view.findViewById(R.id.btnSearch);
        spnPaymentType = view.findViewById(R.id.spnPaymentType);
        btnAddClient = view.findViewById(R.id.btnAddClient);
        cbDiscount = view.findViewById(R.id.cbDiscount);


        PaymentController.getInstance(parentActivity).fillSpinnerPaymentType(spnPaymentType);

        llGoResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainOrders)parentActivity).showDetail();
            }
        });

        llPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    ((MainOrders)parentActivity).showPaymentConfirmation();
                }

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClient();
            }
        });

        btnAddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAddDialog();
            }
        });

        cbDiscount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etAmount.setEnabled(!isChecked);
                etDiscount.setEnabled(isChecked);
                etDiscountDescription.setEnabled(isChecked);
                if(isChecked){
                    etAmount.setText(Funciones.formatDecimal(TempOrdersController.getInstance(parentActivity).getSumPrice() - getManualDiscount()));
                    etDiscount.requestFocus();
                    etDiscount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            double discount = 0.0;
                            try{
                                discount = Double.parseDouble(s.toString());
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                            etAmount.setText(Funciones.formatDecimal(TempOrdersController.getInstance(parentActivity).getSumPrice() - discount));

                        }
                    });
                }else{
                    etDiscount.setText("0.0");
                    etDiscountDescription.setText("");
                }
            }
        });


        setUpControls();

    }

    @Override
    public void onResume() {
        super.onResume();
        tvTotal.setText("$"+Funciones.formatMoney(TempOrdersController.getInstance(parentActivity).getSumPrice()));
        etAmount.setText(Funciones.formatDecimal(TempOrdersController.getInstance(parentActivity).getSumPrice() - getManualDiscount()));
    }

    public void setParent(Activity activity){
        this.parentActivity = activity;
    }


    public void searchClient(){
        FragmentTransaction ft = ((AppCompatActivity)parentActivity).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)parentActivity).getSupportFragmentManager().findFragmentByTag("dialogClientSearch");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;

        newFragment = ClientSearchDialog.newInstance(parentActivity, this);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void createReceipt(){
        Day day = DayController.getInstance(parentActivity).getCurrentOpenDay();

        Sales s = TempOrdersController.getInstance(parentActivity).getTempSale();
        s.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);
        s.setCODEDAY(day.getCode());


        double paidAmount = Double.parseDouble(etAmount.getText().toString().replace(",", ""));
        double receiptSubTotal = s.getTOTAL();
        double receiptManualDiscount = getManualDiscount();
        double receiptTaxes = 0;
        double receiptTotal = receiptSubTotal-receiptManualDiscount+receiptTaxes;

        String receiptStatus = (receiptTotal > paidAmount)?CODES.CODE_RECEIPT_STATUS_OPEN:CODES.CODE_RECEIPT_STATUS_CLOSED;
        //String code, String codeUser,String codesale, String codeclient,  String status, String ncf, double subTotal, double taxes, double discount, double total, double paidAmount
        Receipts r = new Receipts(Funciones.generateCode(), Funciones.getCodeuserLogged(parentActivity),s.getCODE(),client.getCode(),receiptStatus,"",receiptSubTotal,receiptTaxes,receiptManualDiscount,receiptTotal,paidAmount, day.getCode());

        //String code, String codeReceipt,String codeUser, String codeClient, String type, double subTotal, double tax, double discount, double total
        Payment p = new Payment(Funciones.generateCode(), r.getCode(), Funciones.getCodeuserLogged(parentActivity),client.getCode(), ((KV)spnPaymentType.getSelectedItem()).getKey(),0,0,0,paidAmount, day.getCode());

        s.setCODERECEIPT(r.getCode());


        ((MainOrders)parentActivity).closeOrders(r,p,s, TempOrdersController.getInstance(parentActivity).getTempSalesDetails(s));

    }

    public void refresh(){
        client = null;
        etDocument.setText("");
        etName.setText("");
        spnPaymentType.setSelection(0);
        etAmount.setText(Funciones.formatDecimal(TempOrdersController.getInstance(parentActivity).getSumPrice()));
    }

    public boolean validate(){
       // boolean abono = true;//UserControlController.getInstance(parentActivity).multiPayment();
        if(TempOrdersController.getInstance(parentActivity).getOrderDetailModels(TempOrdersController.getInstance(parentActivity).getTempSale().getCODE()).size()==0){
            Snackbar.make(getView(), "No hay productos para facturar", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(!validateEditedAmount()){
            return false;
        }else if(client == null){
            Snackbar.make(getView(), "Seleccione un cliente", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    public void setUpControls(){
        if(UserControlController.getInstance(parentActivity).multiPayment()) {
            etAmount.setFocusableInTouchMode(true);
        }
    }

    public double getManualDiscount(){
        double manualDiscount=0.0;
            try{
                manualDiscount=  Double.parseDouble(etDiscount.getText().toString());
            }catch (Exception e){

            }
        return manualDiscount;
    }

    public boolean validateEditedAmount(){
        String editAmount = etAmount.getText().toString();
        String mDiscount = etDiscount.getText().toString();

        double editAmountD=0.0;
        if(editAmount.isEmpty()){
            Snackbar.make(getView(), "Introduzca un monto valido", Snackbar.LENGTH_LONG).show();
            return false;
        }
        try{
          editAmountD=  Double.parseDouble(editAmount);
        }catch (Exception e){
            Snackbar.make(getView(), "Introduzca un monto valido", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(!cbDiscount.isChecked() && editAmountD <1){
            Snackbar.make(getView(), "El importe debe ser superior a 1", Snackbar.LENGTH_LONG).show();
            return false;
        }


        double manualDiscount=0.0;
        if(cbDiscount.isChecked()){
            try{
                manualDiscount=  Double.parseDouble(mDiscount);
            }catch (Exception e){
                Snackbar.make(getView(), "Introduzca un descuento valido", Snackbar.LENGTH_LONG).show();
                return false;
            }

            if(manualDiscount <1){
                Snackbar.make(getView(), "El descuento debe ser superior a 0", Snackbar.LENGTH_LONG).show();
                return false;
            }


            if( TempOrdersController.getInstance(parentActivity).getSumPrice() < manualDiscount){
                Snackbar.make(getView(), "El descuento no puede ser superior al importe", Snackbar.LENGTH_LONG).show();
                return false;
            }
        }



        if (editAmountD > TempOrdersController.getInstance(parentActivity).getSumPrice()){
            Snackbar.make(getView(), "El importe no puede ser superior a la deuda.", Snackbar.LENGTH_LONG).show();
            return false;
        }


        return true;
    }

    public void callAddDialog(){
        FragmentTransaction ft = ((AppCompatActivity)parentActivity).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)parentActivity).getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ClientsDialogFragment.newInstance(null, this);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    @Override
    public void dialogClosed(Object o) {
        if(o instanceof Clients){
            Clients c = (Clients)o;
            this.client = new ClientRowModel(c.getCODE(),c.getDOCUMENT(),c.getNAME(),c.getPHONE(),c.getDATA(), c.getDATA2(), c.getDATA3(), true);
            etName.setText(client.getName());
            etDocument.setText(client.getDocument());
        }else if(o instanceof ClientRowModel){
                ClientRowModel crm = (ClientRowModel)o;
                this.client = crm;
                etDocument.setText(client.getDocument());
                etName.setText(client.getName());

        }

    }
}
