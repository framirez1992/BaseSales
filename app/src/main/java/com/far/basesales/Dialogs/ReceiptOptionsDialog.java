package com.far.basesales.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothlibrary.BluetoothScan;
import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.Transaction;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.MainOrders;
import com.far.basesales.MainReceipt;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ReceiptOptionsDialog extends DialogFragment  {

    Activity activity;
    public Receipts receipts;
    Payment lastPayment;
    LinearLayout llProgress, llPrint, llShare, llPayment, llAnulate;
    CardView btnClose;
    TextView tvErrorMessage;
    Dialog paymentDialog, anulateConfirmation ;
    int lastAction=-1;

    LinearLayout llProgressPayment;
    Spinner spnPaymentType;
    TextInputEditText etPaymentAmount;
    CardView cvPay;

    boolean multiPayment = false;


    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public  static ReceiptOptionsDialog newInstance(Activity activity, Receipts receipts) {

        ReceiptOptionsDialog f = new ReceiptOptionsDialog();
        f.activity = activity;
        f.receipts = receipts;

        // Supply num input as an argument.
        Bundle args = new Bundle();
        if(receipts != null) {
            f.setArguments(args);
        }

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        multiPayment = UserControlController.getInstance(getActivity()).searchSimpleControl(CODES.USERSCONTROL_MULTIPAYMENT)!= null;

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_receipt_options, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llProgress = view.findViewById(R.id.llProgress);
        llPayment = view.findViewById(R.id.llPayment);
        llPrint = view.findViewById(R.id.llPrint);
        llShare = view.findViewById(R.id.llShare);
        btnClose = view.findViewById(R.id.btnClose);
        llAnulate = view.findViewById(R.id.llAnulate);
        tvErrorMessage = view.findViewById(R.id.tvErrorMsg);

        if(!multiPayment || receipts.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED) || receipts.getStatus().equals(CODES.CODE_RECEIPT_STATUS_ANULATED) || activity instanceof MainOrders){
            llPayment.setVisibility(View.GONE);
        }else if(multiPayment && activity instanceof MainReceipt){
            llPayment.setVisibility(View.VISIBLE);
        }

        if(!receipts.getStatus().equals(CODES.CODE_RECEIPT_STATUS_ANULATED) &&
                DayController.getInstance(activity).getCurrentOpenDay() != null && receipts.getReceiptnumber().equals(DayController.getInstance(activity).getCurrentOpenDay().getLastreceiptnumber())){
            llAnulate.setVisibility(View.VISIBLE);
        }else{
            llAnulate.setVisibility(View.GONE);
        }


        llPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaymentDialog();
                dismiss();
            }
        });
        llPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Funciones.getPreferences(activity, CODES.PREFERENCE_BLUETOOTH_MAC_ADDRESS).equals("")){
                    execute(v);
                }else{
                    activity.startActivityForResult(new Intent(activity, BluetoothScan.class), CODES.REQUEST_BLUETOOTH_ACTIVITY);
                }

            }
        });

        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute(v);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity instanceof MainOrders){
                    ((MainOrders)activity).startNewOrder();
                }else if(activity instanceof MainReceipt){

                }
                dismiss();
            }
        });

        llAnulate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnulateConfirmation();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void enableButtons(){
        llPrint.setEnabled(true);
        llShare.setEnabled(true);
        btnClose.setEnabled(true);
    }

    public void disableButtons(){
        llPrint.setEnabled(false);
        llShare.setEnabled(false);
        btnClose.setEnabled(false);
    }

    public void execute(View v){
        lastAction = v.getId();

        showLoading();
        disableButtons();
        if(receipts.getMdate() == null){
            ReceiptController.getInstance(activity).searchReceiptByCodeFromFireBase(receipts.getCode(), new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    if(querySnapshot!= null && !querySnapshot.isEmpty()){
                        receipts = querySnapshot.getDocuments().get(0).toObject(Receipts.class);
                        ReceiptController.getInstance(activity).update(receipts);
                        doAction();
                    }else{
                        enableButtons();
                        hideLoading();
                        showError("Receipt no found");
                    }

                }
            }, new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getException() != null){
                        enableButtons();
                        hideLoading();
                        showError(task.getException().getMessage());
                    }

                }
            }, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    enableButtons();
                    hideLoading();
                    showError(e.getMessage());
                }
            });
        }else{
            doAction();
        }
    }

    public void showError(String msg){
        tvErrorMessage.setText(msg);
        tvErrorMessage.setVisibility(View.VISIBLE);
    }

    public void showLoading(){
        llProgress.setVisibility(View.VISIBLE);
        tvErrorMessage.setVisibility(View.GONE);
        tvErrorMessage.setText("");
    }

    public void hideLoading(){
        llProgress.setVisibility(View.INVISIBLE);
    }

    public void doAction(){
        switch(lastAction){
            case R.id.llPrint: printReceipt();break;
            case R.id.llShare: share(); break;
            default:break;
        }
    }

    public void printReceipt(){
        AsyncTask<Void, Void, String> a = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String error = null;
                try{
                    ReceiptController.getInstance(activity).printReceipt(receipts.getCode());
                }catch (Exception e){
                    error = e.getMessage();
                }
                return error;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s!= null){
                    enableButtons();
                    hideLoading();
                    showError(s);
                    return;
                }

                enableButtons();
                hideLoading();
            }
        };
        a.execute();

    }


    public void share(){
        AsyncTask<Void, Void, String> a = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                String value = "ERROR-";
                try{
                    if(ReceiptController.getInstance(activity).update(receipts) <1){
                        ReceiptController.getInstance(activity).insert(receipts);
                    }
                    value= ReceiptController.getInstance(activity).createPDF(receipts.getCode(), 1);
                }catch (Exception e){
                    value +=e.getMessage();
                }
                return value;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.contains("ERROR-")){
                    hideLoading();
                    showError(s.replace("ERROR-", ""));
                    enableButtons();
                    return;
                }
                promptForNextAction(s);

                enableButtons();
                hideLoading();
            }
        };
        a.execute();

    }



    public void showPDF(String fileRoute){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(fileRoute)), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void emailNote(String fileRoute)
    {
        try{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
            intent.putExtra(Intent.EXTRA_TEXT, "Message body");
            Uri uri = Uri.fromFile(new File(fileRoute));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public void promptForNextAction(final String filePath)
    {
        final String[] options = { "Compartir", "Visualizar",
                "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Archivo guardado");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("Compartir")){
                    emailNote(filePath);
                }else if (options[which].equals("Visualizar")){
                    showPDF(filePath);
                }else if (options[which].equals("Cancelar")){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog a =builder.create();
        a.setCancelable(false);
        a.show();

    }




    public void showPaymentDialog(){
        paymentDialog = null;
        paymentDialog = new Dialog(activity);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.payment_dialog);
        spnPaymentType = paymentDialog.findViewById(R.id.spnPaymentType);
        etPaymentAmount = paymentDialog.findViewById(R.id.etAmount);
        cvPay =paymentDialog.findViewById(R.id.cvPay);
        llProgressPayment = paymentDialog.findViewById(R.id.llProgress);
        TextView tvPendingAmount = paymentDialog.findViewById(R.id.tvPendingAmount);

        PaymentController.getInstance(activity).fillSpinnerPaymentType(spnPaymentType);
        etPaymentAmount.setText(Funciones.formatDecimal(receipts.getTotal()-receipts.getPaidamount()));
        tvPendingAmount.setText("$"+Funciones.formatDecimal(receipts.getTotal()-receipts.getPaidamount()));
        cvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEditedAmount(etPaymentAmount)){
                    paymentDialog.setCancelable(false);
                    disableViewsPayment();
                    llProgressPayment.setVisibility(View.VISIBLE);
                    savePayment(((KV)spnPaymentType.getSelectedItem()).getKey(), etPaymentAmount.getText().toString());
                }
            }
        });

        paymentDialog.show();
        Window window = paymentDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        Funciones.showKeyBoard(etPaymentAmount);

    }

    public void refreshData(){
        if(activity instanceof  MainOrders){
            ((MainOrders)activity).startNewOrder();
        }else if(activity instanceof MainReceipt){
            ((MainReceipt)activity).refreshReceiptsResume();
        }
    }


    public void enableViewsPayment(){
        spnPaymentType.setEnabled(true);
        etPaymentAmount.setEnabled(true);
        cvPay.setEnabled(true);
    }
    public void disableViewsPayment(){
        spnPaymentType.setEnabled(false);
        etPaymentAmount.setEnabled(false);
        cvPay.setEnabled(false);
    }




    public void savePayment(String paymentType, String editAmount){
        double paymentAmount = Double.parseDouble(editAmount.replace(",", "").replace("$", ""));

        String receiptStatus = (receipts.getTotal()> (receipts.getPaidamount()+paymentAmount))?CODES.CODE_RECEIPT_STATUS_OPEN:CODES.CODE_RECEIPT_STATUS_CLOSED;
        receipts.setStatus(receiptStatus);
        receipts.setPaidamount(receipts.getPaidamount()+paymentAmount);
        receipts.setMdate(null);//para que lo envi con TIMESTAMP

        final Day day = DayController.getInstance(activity).getCurrentOpenDay();
        //String code, String codeReceipt,String codeUser, String codeClient, String type, double subTotal, double tax, double discount, double total
        Payment p = new Payment(Funciones.generateCode(), receipts.getCode(), Funciones.getCodeuserLogged(activity),receipts.getCodeclient(), paymentType,0,0,0,paymentAmount, day.getCode());
        p.setDATE(new Date());
        if(p.getTYPE().equals(CODES.PAYMENTTYPE_CASH)){
            day.setCashpaidamount(day.getCashpaidamount()+p.getTOTAL());
            day.setCashpaidcount(day.getCashpaidcount()+1);
        }else if(p.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)){
            day.setCreditpaidamount(day.getCreditpaidamount()+p.getTOTAL());
            day.setCreditpaidcount(day.getCreditpaidcount()+1);
        }


        Transaction.getInstance(activity).sendToFireBase(receipts, p,day, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                enableViewsPayment();
                llProgressPayment.setVisibility(View.INVISIBLE);
            }
        });
        PaymentController.getInstance(activity).getPaymentFromFireBase(p.getCODE(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                Payment payment = null;
                if(queryDocumentSnapshots!= null && queryDocumentSnapshots.getDocuments().size() > 0){
                    payment = queryDocumentSnapshots.getDocuments().get(0).toObject(Payment.class);
                }

                if(payment != null){
                    lastPayment = payment;
                    ReceiptController.getInstance(activity).update(receipts);
                    PaymentController.getInstance(activity).insert(payment);
                    DayController.getInstance(activity).update(day);

                    paymentDialog.dismiss();
                    paymentDialog=null;
                    refreshData();

                    if(activity instanceof MainReceipt){
                        ((MainReceipt)activity).showPrintPaymentConfirmation(lastPayment);
                    }


                }else{
                    Toast.makeText(activity, "Error efectuando el pago, intente otra vez", Toast.LENGTH_LONG).show();
                    enableViewsPayment();
                    llProgressPayment.setVisibility(View.INVISIBLE);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                enableViewsPayment();
                llProgressPayment.setVisibility(View.INVISIBLE);
            }
        });


    }




    public boolean validateEditedAmount(TextInputEditText etAmount){
        String editAmount = etAmount.getText().toString();
        double editAmountD=0.0;
        if(editAmount.isEmpty()){
            Snackbar.make(paymentDialog.findViewById(R.id.llParent), "Introduzca un monto valido", Snackbar.LENGTH_LONG).show();
            //((MainReceipt)parentActivity).showErrorDialog();
            return false;
        }
        try{
            editAmountD=  Double.parseDouble(editAmount.replace(",", "").replace("$", ""));
        }catch (Exception e){
            Snackbar.make(paymentDialog.findViewById(R.id.llParent), "Introduzca un monto valido", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(editAmountD <1){
            Snackbar.make(paymentDialog.findViewById(R.id.llParent), "El importe debe ser superior a $1.00", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if (editAmountD > (receipts.getTotal()-receipts.getPaidamount())){
            Snackbar.make(paymentDialog.findViewById(R.id.llParent), "El importe no puede ser superior a la deuda.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }



    public void showAnulateConfirmation(){
        anulateConfirmation = null;
        anulateConfirmation = Funciones.getCustomDialog2Btn(activity,getResources().getColor(R.color.red_700),"Anular Factura", "Esta seguro que desea eliminar la factura '"+receipts.getReceiptnumber()+"'? ",R.drawable.ic_cancel, null, null);
        CardView btnPositive= anulateConfirmation.findViewById(R.id.btnPositive);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anulateConfirmation.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                anulateReceipt();

            }
        });
        CardView btnNegative = anulateConfirmation.findViewById(R.id.btnNegative);
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anulateConfirmation.dismiss();
            }
        });

        anulateConfirmation.show();
        Window window = anulateConfirmation.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);


    }



    public void anulateReceipt(){
        PaymentController.getInstance(activity).searchPaymentFromFireBaseByCodeReceipt(receipts.getCode(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                Day day = DayController.getInstance(activity).getCurrentOpenDay();
                int anulatedReceiptsCount = day.getAnulatedreceiptscount();
                double anulatedReceiptsAmount = day.getAnulatedreceiptsamount() + receipts.getTotal();
                int anulatedCashPaymentCount = day.getAnulatedcashpaymentcount();
                double anulatedCashPaymentAmount = day.getAnulatedcashpaymentamount();
                int anulatedCreditPaymentCount= day.getAnulatedcreditpaymentcount();
                double anulatedCreditPaymentAmount=day.getAnulatedcreditpaymentamount();

                receipts.setStatus(CODES.CODE_RECEIPT_STATUS_ANULATED);
                anulatedReceiptsCount++;

                for(DocumentSnapshot ds: querySnapshot){
                    Payment s = ds.toObject(Payment.class);
                    if(s.getTYPE().equals(CODES.PAYMENTTYPE_CASH)){
                        anulatedCashPaymentCount++;
                        anulatedCashPaymentAmount+=s.getTOTAL();
                    }else if(s.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)){
                        anulatedCreditPaymentCount++;
                        anulatedCreditPaymentAmount+=s.getTOTAL();
                    }

                    if(PaymentController.getInstance(activity).update(s) <= 0){
                        PaymentController.getInstance(activity).insert(s);
                    }
                }

                ArrayList<Sales> anulatedSales = new ArrayList<>();
                for(Sales s: SalesController.getInstance(activity).getSales(SalesController.CODERECEIPT+" = ?", new String[]{receipts.getCode()})){
                    s.setSTATUS(CODES.CODE_ORDER_STATUS_ANULATED);
                    anulatedSales.add(s);
                }

                final Day d = DayController.getInstance(activity).getCurrentOpenDay();
                d.setAnulatedreceiptscount(anulatedReceiptsCount);
                d.setAnulatedreceiptsamount(anulatedReceiptsAmount);
                d.setAnulatedcashpaymentcount(anulatedCashPaymentCount);
                d.setAnulatedcashpaymentamount(anulatedCashPaymentAmount);
                d.setAnulatedcreditpaymentcount(anulatedCreditPaymentCount);
                d.setAnulatedcreditpaymentamount(anulatedCreditPaymentAmount);


                Transaction.getInstance(activity).sendToFireBase(receipts, null, anulatedSales, d, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                ReceiptController.getInstance(activity).searchReceiptByCodeFromFireBase(receipts.getCode(), new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if(querySnapshot!= null && !querySnapshot.isEmpty()){
                            Receipts r = querySnapshot.getDocuments().get(0).toObject(Receipts.class);
                            if(r.getStatus().equals(CODES.CODE_RECEIPT_STATUS_ANULATED)){
                                ReceiptController.getInstance(activity).update(r);
                                for(Sales s: SalesController.getInstance(activity).getSales(SalesController.CODERECEIPT+" = ?", new String[]{r.getCode()})){
                                    s.setSTATUS(CODES.CODE_ORDER_STATUS_ANULATED);
                                    SalesController.getInstance(activity).update(s);
                                }
                                DayController.getInstance(activity).update(d);
                            }
                            anulateConfirmation.dismiss();
                            refreshData();
                        }else{
                            anulateConfirmation.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                            Toast.makeText(activity, "No se pudo anular la factura. Intente nuevamente", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getException() != null){
                            anulateConfirmation.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                            Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        anulateConfirmation.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });



            }
        }, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getException()!= null){
                    anulateConfirmation.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                    Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                anulateConfirmation.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }




}
