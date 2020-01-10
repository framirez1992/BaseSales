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
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ReceiptController;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.Date;

public class ReceiptOptionsDialog extends DialogFragment  {

    Activity activity;
    public Receipts receipts;
    LinearLayout llProgress, llPrint, llShare, llPayment;
    CardView btnClose;
    TextView tvErrorMessage;
    Dialog paymentDialog;
    int lastAction=-1;


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
        tvErrorMessage = view.findViewById(R.id.tvErrorMsg);

        if(receipts.getStatus().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
            llPayment.setVisibility(View.GONE);
        }else{
            llPayment.setVisibility(View.VISIBLE);
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
                    ((MainOrders)activity).newOrderAndRefresh();
                }else if(activity instanceof MainReceipt){
                    //((MainReceipt)activity).refreshPayments();
                }
                dismiss();
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
        final Spinner spnPaymentType = paymentDialog.findViewById(R.id.spnPaymentType);
        final TextInputEditText etAmount = paymentDialog.findViewById(R.id.etAmount);
        final CardView cvPay = paymentDialog.findViewById(R.id.cvPay);
        TextView tvPendingAmount = paymentDialog.findViewById(R.id.tvPendingAmount);

        PaymentController.getInstance(activity).fillSpinnerPaymentType(spnPaymentType);
        etAmount.setText(Funciones.formatDecimal(receipts.getTotal()-receipts.getPaidamount()));
        tvPendingAmount.setText("$"+Funciones.formatDecimal(receipts.getTotal()-receipts.getPaidamount()));
        /*if(UserControlController.getInstance(activity).multiPayment()) {
            etAmount.setFocusableInTouchMode(true);
        }*/
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





    public void savePayment(String paymentType, String editAmount){
        //((MainReceipt)parentActivity).showLoadingDialog();

        final Receipts myReceipt = receipts.clone();
        double paymentAmount = Double.parseDouble(editAmount.replace(",", "").replace("$", ""));

        String receiptStatus = (myReceipt.getTotal()> (myReceipt.getPaidamount()+paymentAmount))?CODES.CODE_RECEIPT_STATUS_OPEN:CODES.CODE_RECEIPT_STATUS_CLOSED;
        //String code, String codeUser,String codesale, String codeclient,  String status, String ncf, double subTotal, double taxes, double discount, double total, double paidAmount
        //Receipts r =ReceiptController.getInstance(activity).getReceiptByCode(receipts.getCode());
        myReceipt.setStatus(receiptStatus);
        myReceipt.setPaidamount(receipts.getPaidamount()+paymentAmount);
        myReceipt.setMdate(null);//para que lo envi con TIMESTAMP

        final Day day = DayController.getInstance(activity).getCurrentOpenDay();
        //String code, String codeReceipt,String codeUser, String codeClient, String type, double subTotal, double tax, double discount, double total
        final Payment p = new Payment(Funciones.generateCode(), myReceipt.getCode(), Funciones.getCodeuserLogged(activity),myReceipt.getCodeclient(), paymentType,0,0,0,paymentAmount, day.getCode());
        p.setDATE(new Date());
        //day.setDiscountamount(day.getDiscountamount()+receipt.getDiscount());
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
                    ReceiptController.getInstance(activity).update(myReceipt);
                    PaymentController.getInstance(activity).insert(p);
                    DayController.getInstance(activity).update(day);

                    paymentDialog.dismiss();
                    paymentDialog=null;
                    if(activity instanceof  MainOrders){
                        ((MainOrders)activity).newOrderAndRefresh();
                    }else if(activity instanceof MainReceipt){
                        ((MainReceipt)activity).refreshReceiptsResume();
                    }
                }else{
                    Toast.makeText(activity, "Error efectuando el pago, intente otra vez", Toast.LENGTH_LONG).show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
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

}
