package com.far.basesales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.bluetoothlibrary.BluetoothScan;
import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.Transaction;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class MainReceipt extends AppCompatActivity implements ListableActivity, OnFailureListener, OnCompleteListener, OnSuccessListener<QuerySnapshot> {

    Fragment lastFragment;
    ReceiptSearchFragment receiptSearchFragment;
    ReceiptResumeFragment receiptResumeFragment;
    Dialog loadingDialog;
    Dialog errorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_receipt);
        receiptSearchFragment = new ReceiptSearchFragment();
        receiptSearchFragment.setParentActivity(this);

        receiptResumeFragment = new ReceiptResumeFragment();
        receiptResumeFragment.setParentActivity(this);

        changeFragment(receiptSearchFragment, R.id.frame);
    }

    @Override
    public void onClick(Object obj) {
        if(obj instanceof ReceiptRowModel){
            receiptResumeFragment.setModel((ReceiptRowModel)obj);
            showReceiptResume();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODES.REQUEST_BLUETOOTH_ACTIVITY && resultCode == Activity.RESULT_OK){
            String macAdress = data.getExtras().getString(BluetoothScan.EXTRA_MAC_ADDRESS);
            Funciones.savePreferences(MainReceipt.this, CODES.PREFERENCE_BLUETOOTH_MAC_ADDRESS, macAdress);
        }
    }


    public void showReceiptResume(){
        changeFragment(receiptResumeFragment, R.id.frame);
    }

    public void showReceiptList(){
        changeFragment(receiptSearchFragment, R.id.frame);
    }

    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();
        lastFragment = f;
    }

    public void showLoadingDialog(){
        loadingDialog=null;
        loadingDialog = Funciones.getLoadingDialog(MainReceipt.this,"Please wait...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    public void closeLoadingDialog(){
        if(loadingDialog!= null){
            loadingDialog.dismiss();
            loadingDialog=null;
        }
    }

    public void showErrorDialog(String msg){
        errorDialog=null;
        errorDialog = Funciones.getCustomDialog(MainReceipt.this,getResources().getColor(R.color.red_700), "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

   public void refreshReceiptsResume(){
        receiptResumeFragment.refreshModel();
        receiptResumeFragment.searchPayments(false);
   }

    @Override
    public void onBackPressed() {
        if(lastFragment instanceof ReceiptResumeFragment){
            showReceiptList();
        }else{
            super.onBackPressed();
        }


    }

    @Override
    public void onComplete(@NonNull Task task) {
        closeLoadingDialog();
        if(task.getException()!= null){
            showErrorDialog(task.getException().getMessage()+"\n"+task.getException().getLocalizedMessage());
        }

    }

    @Override
    public void onFailure(@NonNull Exception e) {
        closeLoadingDialog();
        if(errorDialog!= null && !errorDialog.isShowing()){
            showErrorDialog(e.getMessage()+"\n"+e.getLocalizedMessage());
        }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
        showReceiptList();
    }
}
