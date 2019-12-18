package com.far.basesales.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.MainOrders;
import com.far.basesales.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;

public class ReceiptOptionsDialog extends DialogFragment  {

    Activity activity;
    public Receipts receipts;
    LinearLayout llProgress, llPrint, llShare;
    CardView btnClose;
    TextView tvErrorMessage;
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
        llPrint = view.findViewById(R.id.llPrint);
        llShare = view.findViewById(R.id.llShare);
        btnClose = view.findViewById(R.id.btnClose);
        tvErrorMessage = view.findViewById(R.id.tvErrorMsg);

        llPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execute(v);
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
                    value= ReceiptController.getInstance(activity).createPDF(receipts.getCode());
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

}
