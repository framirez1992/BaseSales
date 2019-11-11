package com.far.basesales;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.SimpleRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.UserTypes;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Dialogs.UserTypesDialogFragment;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MaintenanceUserTypes extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    UserTypesController userTypesController;
    UserTypes userTypes;
    Licenses licence;
    String lastSearch = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_screen);

        userTypesController = UserTypesController.getInstance(MaintenanceUserTypes.this);
        licence = LicenseController.getInstance(MaintenanceUserTypes.this).getLicense();


        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceUserTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList(lastSearch);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userTypesController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                userTypesController.delete(null, null);//limpia la tabla

                for(DocumentSnapshot ds: querySnapshot){

                    UserTypes mu = ds.toObject(UserTypes.class);
                    userTypesController.insert(mu);
                }

                refreshList(lastSearch);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        try{
            getMenuInflater().inflate(R.menu.search_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView search = (SearchView) searchItem.getActionView();

            search.setOnQueryTextListener(searchListener);
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
        inflater.inflate(R.menu.menu_edit_delete, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEdit:
                callAddDialog(false);
                return true;
            case R.id.actionDelete:
                callDeleteConfirmation();
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
        DialogFragment newFragment =  UserTypesDialogFragment.newInstance((isNew)?null:userTypes);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(userTypes != null){
            description = userTypes.getDESCRIPTION();
        }

        final Dialog d = new Dialog(MaintenanceUserTypes.this);
        d.setTitle("Delete");
        d.setContentView(R.layout.msg_2_buttons);
        TextView tvMsg = d.findViewById(R.id.tvMsg);
        Button btnAceptar = d.findViewById(R.id.btnPositive);
        Button btnCancelar = d.findViewById(R.id.btnNegative);

        tvMsg.setText("Esta seguro que desea eliminar \'"+description+"\' permanentemente?");
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgDependency = getMsgDependency();
                if(!msgDependency.isEmpty()) {
                    Funciones.showAlertDependencies(MaintenanceUserTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }

                if(userTypes != null){
                    userTypesController.deleteFromFireBase(userTypes);
                }
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

    }

    public void refreshList(String data){
        objects.clear();
        String where = (data!= null)?userTypesController.DESCRIPTION+" like  ? ":null;
        objects.addAll(userTypesController.getUserTypesSRM(where, (data != null)?new String[]{data+"%"}:null, null));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        userTypes = null;
        SimpleRowModel sr = (SimpleRowModel)obj;

        userTypes = userTypesController.getUserTypeByCode(sr.getId());

    }

    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                refreshList(lastSearch);
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                refreshList(lastSearch);
                return true;
            }
            return false;
        }
    };

    public String getMsgDependency(){
        UsersController.getInstance(MaintenanceUserTypes.this).getUsers(null, null, null);
        String msgDependency ="";
        if(userTypes != null){
                msgDependency = userTypesController.hasDependencies(userTypes.getCODE());

        }
        return msgDependency;
    }

}
