package com.far.basesales;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.LicenseRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.DataBase.CloudFireStoreDB;
import com.far.basesales.Dialogs.LicenseDialogFragment;
import com.far.basesales.Interfases.FireBaseOK;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;


public class AdminConfiguration extends AppCompatActivity implements ListableActivity, OnFailureListener, FireBaseOK {


    AdminCredentialsFragment adminCredentialsFragment;
    AdminLicensesFragment adminLicensesFragment;
    AdminLicenseSetupFragment adminLicenseSetupFragment;

    Fragment lastFragment;
    MenuItem addLicense;
    Licenses licenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_configuration);
        adminCredentialsFragment = new AdminCredentialsFragment();
        adminCredentialsFragment.setAdminConfiguration(AdminConfiguration.this);
        adminLicensesFragment = new AdminLicensesFragment();
        adminLicensesFragment.setAdminConfiguration(AdminConfiguration.this);
        adminLicenseSetupFragment= new AdminLicenseSetupFragment();
        adminLicenseSetupFragment.setAdminConfiguration(AdminConfiguration.this);

        setFragment(adminCredentialsFragment);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try{
            getMenuInflater().inflate(R.menu.admin_configuration_menu, menu);
            addLicense = menu.findItem(R.id.action_new);
            hideMenu();
            /*MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView search = (SearchView) searchItem.getActionView();

            search.setOnQueryTextListener(searchListener);*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new:
                callAddDialog(true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.license_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                callAddDialog(false);
                return true;
            /*case R.id.actionDelete:
                callDeleteConfirmation();
                return  true;*/
            case R.id.actionConfigure:
                showLicenseSetup();
                return  true;

            default:return super.onContextItemSelected(item);
        }
    }

    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
        if(isNew){
            newFragment = LicenseDialogFragment.newInstance(AdminConfiguration.this, null, false);
        }else {
            newFragment = LicenseDialogFragment.newInstance(AdminConfiguration.this, licenses, false);
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void setFragment(Fragment f){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        lastFragment = f;
    }

    public void showMenu(){
        addLicense.setVisible(true);
    }
    public void hideMenu(){
        addLicense.setVisible(false);
    }
    public void addLicense() {
        try {
            CloudFireStoreDB.getInstance(AdminConfiguration.this, AdminConfiguration.this, AdminConfiguration.this).crearNuevaEstructuraFireStore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showLicences(){
        setFragment(adminLicensesFragment);
    }
    public void searchLicenses(){
        adminLicensesFragment.searchLicenses();
    }

    public void showLicenseSetup(){
        adminLicenseSetupFragment.setLicense(licenses);
        setFragment(adminLicenseSetupFragment);
    }

    @Override
    public void OnFireBaseEndContact(int code) {
        if(code == 1){
            Toast.makeText(AdminConfiguration.this, "Finalizado", Toast.LENGTH_LONG).show();
           finish();

        }
    }

    @Override
    public void sendMessage(String message) {
        Toast.makeText(AdminConfiguration.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(AdminConfiguration.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(Object obj) {
    LicenseRowModel licenseRowModel = (LicenseRowModel)obj;
    licenses = licenseRowModel.getLicenses();
    }



    public void callDeleteConfirmation(){

        String msg = "Esta seguro que desea eliminar la licencia \'"+licenses.getCODE()+" - "+licenses.getCLIENTNAME()+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LicenseController.getInstance(AdminConfiguration.this).deleteLicence(licenses);
                licenses = null;
                searchLicenses();
                d.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }
}
