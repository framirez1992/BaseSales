package com.far.basesales;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;

public class MainReceipt extends AppCompatActivity implements ListableActivity {

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
        errorDialog = Funciones.getCustomDialog(MainReceipt.this, "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }



}
