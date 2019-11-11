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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.SimpleRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsSubTypes;
import com.far.basesales.CloudFireStoreObjects.ProductsTypes;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsSubTypesInvController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Dialogs.ProductSubTypeDialogFragment;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MaintenanceProductSubTypes extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    ProductsTypesController productsTypesController;
    ProductsSubTypesController productsSubTypesController;
    ProductsTypesInvController productsTypesInvController;
    ProductsSubTypesInvController productsSubTypesInvController;
    ProductsSubTypes productsSubType = null;
    Licenses licence;
    String lastSearch = null;
    Spinner spnFamily;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_TYPE_FAMILY) ){
            finish();
            return;
        }

        type = getIntent().getStringExtra(CODES.EXTRA_TYPE_FAMILY);

        productsTypesController = ProductsTypesController.getInstance(MaintenanceProductSubTypes.this);
        productsSubTypesController = ProductsSubTypesController.getInstance(MaintenanceProductSubTypes.this);
        productsTypesInvController = ProductsTypesInvController.getInstance(MaintenanceProductSubTypes.this);
        productsSubTypesInvController = ProductsSubTypesInvController.getInstance(MaintenanceProductSubTypes.this);

        licence = LicenseController.getInstance(MaintenanceProductSubTypes.this).getLicense();


        rvList = findViewById(R.id.rvList);
        spnFamily = findViewById(R.id.spn);
        ((TextView)findViewById(R.id.spnTitle)).setText("Familia");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProductSubTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsTypesController.fillSpinner(spnFamily, true);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            productsTypesInvController.fillSpinner(spnFamily, true);
        }
        spnFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV obj = ((KV)spnFamily.getSelectedItem());
                if(obj.getKey().equals("0"))
                refreshList();
                else
                refreshList();//DESCRIPCION
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpListeners();
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


    public void setUpListeners(){

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            productsTypesController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsTypesController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        ProductsTypes pt = ds.toObject(ProductsTypes.class);
                        productsTypesController.insert(pt);
                    }

                    refreshList();
                }
            });
            productsSubTypesController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsSubTypesController.delete(null, null);

                    for (DocumentSnapshot ds : querySnapshot) {
                        if (ds.exists()) {
                            ProductsSubTypes pst = ds.toObject(ProductsSubTypes.class);
                            productsSubTypesController.insert(pst);
                        }
                    }

                    refreshList();
                }
            });
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            productsTypesInvController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsTypesInvController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        ProductsTypes pt = ds.toObject(ProductsTypes.class);
                        productsTypesInvController.insert(pt);
                    }

                    refreshList();
                }
            });
            productsSubTypesInvController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsSubTypesInvController.delete(null, null);

                    for (DocumentSnapshot ds : querySnapshot) {
                        if (ds.exists()) {
                            ProductsSubTypes pst = ds.toObject(ProductsSubTypes.class);
                            productsSubTypesInvController.insert(pst);
                        }
                    }

                    refreshList();
                }
            });
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
        if(isNew)
            newFragment = ProductSubTypeDialogFragment.newInstance(type, null);
            else
            newFragment = ProductSubTypeDialogFragment.newInstance(type, productsSubType);


        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productsSubType != null){
            description = productsSubType.getDESCRIPTION();
        }

        final Dialog d = new Dialog(MaintenanceProductSubTypes.this);
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
                    Funciones.showAlertDependencies(MaintenanceProductSubTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }
                 if(productsSubType != null){
                     if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                         productsSubTypesController.deleteFromFireBase(productsSubType);
                     }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                         productsSubTypesInvController.deleteFromFireBase(productsSubType);
                     }
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

    public void refreshList(){

        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();
        String order = null;

        if(lastSearch != null){
            where+=" AND pst."+ProductsSubTypesController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }

        if(spnFamily.getSelectedItem() != null && !((KV)spnFamily.getSelectedItem()).getKey().equals("0")){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(((KV)spnFamily.getSelectedItem()).getKey());
        }else{
            order = "pst."+ProductsTypesController.DESCRIPTION;
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            objects.addAll(productsSubTypesController.getAllProductSubTypesSRM(where, args, order));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            objects.addAll(productsSubTypesInvController.getAllProductSubTypesSRM(where, args, order));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        productsSubType = null;
        SimpleRowModel sr = (SimpleRowModel)obj;
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            productsSubType = productsSubTypesController.getProductTypeByCode(sr.getId());
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            productsSubType = productsSubTypesInvController.getProductTypeByCode(sr.getId());
        }

    }


    public SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if(!query.equals("")) {
                lastSearch = query;
                refreshList();
                return true;
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(newText.equals("")){
                lastSearch = null;
                refreshList();
                return true;
            }
            return false;
        }
    };

    public String getMsgDependency(){
        String msgDependency ="";
        if(productsSubType != null){
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                msgDependency = productsSubTypesController.hasDependencies(productsSubType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
                msgDependency = productsSubTypesInvController.hasDependencies(productsSubType.getCODE());
            }

        }
        return msgDependency;
    }

}

