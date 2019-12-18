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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.ClientsController;
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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptFragment extends Fragment implements DialogCaller {


    TextInputEditText etDocument, etName, etAmount;
    CardView btnSearch, btnAddClient;
    LinearLayout llGoResumen;
    LinearLayout llPay;
    ClientSearchDialog dialog;
    ClientRowModel client;
    Spinner spnPaymentType;

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
        etDocument = view.findViewById(R.id.etDocument);
        etName = view.findViewById(R.id.etName);
        etAmount = view.findViewById(R.id.etAmount);
        llGoResumen = view.findViewById(R.id.llGoResumen);
        llPay = view.findViewById(R.id.llPay);
        btnSearch = view.findViewById(R.id.btnSearch);
        spnPaymentType = view.findViewById(R.id.spnPaymentType);
        btnAddClient = view.findViewById(R.id.btnAddClient);


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

        setUpControls();

    }

    @Override
    public void onResume() {
        super.onResume();
        etAmount.setText(Funciones.formatDecimal(TempOrdersController.getInstance(parentActivity).getSumPrice()));
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

        newFragment = ClientSearchDialog.newInstance(parentActivity);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void setClientSelected(ClientRowModel client){
     this.client = client;
     etDocument.setText(client.getDocument());
     etName.setText(client.getName());

    }

    public void createReceipt(){
        Sales s = TempOrdersController.getInstance(parentActivity).getTempSale();
        s.setSTATUS(CODES.CODE_ORDER_STATUS_CLOSED);

        String receiptStatus = (s.getTOTAL()> getEditedAmount())?CODES.CODE_RECEIPT_STATUS_OPEN:CODES.CODE_RECEIPT_STATUS_CLOSED;
        //String code, String codeUser,String codesale, String codeclient,  String status, String ncf, double subTotal, double taxes, double discount, double total, double paidAmount
        Receipts r = new Receipts(Funciones.generateCode(), Funciones.getCodeuserLogged(parentActivity),s.getCODE(),client.getCode(),receiptStatus,"",0,0,0,s.getTOTAL(),getEditedAmount());

        //String code, String codeReceipt,String codeUser, String codeClient, String type, double subTotal, double tax, double discount, double total
        Payment p = new Payment(Funciones.generateCode(), r.getCode(), Funciones.getCodeuserLogged(parentActivity),client.getCode(), ((KV)spnPaymentType.getSelectedItem()).getKey(),0,0,0,getEditedAmount());

        s.setCODERECEIPT(r.getCode());

        SalesController.getInstance(parentActivity).insert(s);
        for(SalesDetails sd : TempOrdersController.getInstance(parentActivity).getTempSalesDetails(s)){
            SalesController.getInstance(parentActivity).insert_Detail(sd);
        }
        ReceiptController.getInstance(parentActivity).insert(r);
        PaymentController.getInstance(parentActivity).insert(p);

        ((MainOrders)parentActivity).closeOrders(r,p,s);

    }

    public void refresh(){
        client = null;
        etDocument.setText("");
        etName.setText("");
        spnPaymentType.setSelection(0);
        etAmount.setText(Funciones.formatDecimal(TempOrdersController.getInstance(parentActivity).getSumPrice()));
    }

    public boolean validate(){
        boolean abono = UserControlController.getInstance(parentActivity).multiPayment();
        if(TempOrdersController.getInstance(parentActivity).getOrderDetailModels(TempOrdersController.getInstance(parentActivity).getTempSale().getCODE()).size()==0){
            Snackbar.make(getView(), "No hay productos para facturar", Snackbar.LENGTH_LONG).show();
            return false;
        }else if( abono && !validateEditedAmount()){
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

    public double getEditedAmount(){
        return Double.parseDouble(etAmount.getText().toString().replace(",", "").replace("$", ""));
    }

    public boolean validateEditedAmount(){
        String editAmount = etAmount.getText().toString();
        double editAmountD=0.0;
        if(editAmount.isEmpty()){
            ((MainOrders)parentActivity).showErrorDialog("Introduzca un monto valido");
            return false;
        }
        try{
          editAmountD=  Double.parseDouble(editAmount.replace(",", "").replace("$", ""));
        }catch (Exception e){
            ((MainOrders)parentActivity).showErrorDialog("Introduzca un monto valido");
            return false;
        }
        if (editAmountD > TempOrdersController.getInstance(parentActivity).getSumPrice()){
            ((MainOrders)parentActivity).showErrorDialog("El importe no puede ser superior a la deuda.");
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
        Clients c = (Clients)o;
        client = new ClientRowModel(c.getCODE(),c.getDOCUMENT(),c.getNAME(),c.getPHONE(),true);
        etName.setText(client.getName());
        etDocument.setText(client.getDocument());
    }
}
