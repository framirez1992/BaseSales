package com.far.basesales;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.ProductRowModel;
import com.far.basesales.Adapters.ProductRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Products;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsInvController;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.ProductsMeasureInvController;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsSubTypesInvController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Dialogs.ProductsDialogfragment;
import com.far.basesales.Generic.KV;
import com.far.basesales.Generic.KV2;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MaintenanceProducts extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    Spinner spnProductType, spnProductSubType;
    ArrayList<ProductRowModel> objects;
    ProductRowEditionAdapter adapter;
    ProductsController productsController;
    ProductsInvController productsInvController;
    ProductsMeasureController productsMeasureController;
    ProductsMeasureInvController productsMeasureInvController;
    Products products;
    String lastSearch = null;
    String lastFamilia;
    String lastGrupo;

    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w2_spinner);
        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_TYPE_FAMILY) ){
            finish();
            return;
        }

        type = getIntent().getStringExtra(CODES.EXTRA_TYPE_FAMILY);
        productsController = ProductsController.getInstance(MaintenanceProducts.this);
        productsInvController = ProductsInvController.getInstance(MaintenanceProducts.this);
        productsMeasureController = ProductsMeasureController.getInstance(MaintenanceProducts.this);
        productsMeasureInvController = ProductsMeasureInvController.getInstance(MaintenanceProducts.this);

        rvList = findViewById(R.id.rvList);
        spnProductType = findViewById(R.id.spn);
        spnProductSubType = findViewById(R.id.spn2);
        ((TextView)findViewById(R.id.spnTitle)).setText("Familia");
        ((TextView)findViewById(R.id.spnTitle2)).setText("Grupo");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProducts.this);
        rvList.setLayoutManager(manager);
        adapter = new ProductRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            ProductsTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductType,true);
            ProductsSubTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType,true);
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            ProductsTypesInvController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductType,true);
            ProductsSubTypesInvController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType,true);
        }


        spnProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV familia = (KV)spnProductType.getSelectedItem();
                lastFamilia = (familia.getKey().equals("0"))?null: familia.getKey();

                if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
                    ProductsSubTypesController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType, true, familia.getKey());
                }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                    ProductsSubTypesInvController.getInstance(MaintenanceProducts.this).fillSpinner(spnProductSubType, true, familia.getKey());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnProductSubType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV grupo = (KV)spnProductSubType.getSelectedItem();
                lastGrupo = (grupo.getKey().equals("0"))?null:grupo.getKey() ;
                refreshList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        inflater.inflate(R.menu.main_menu_product, menu);
        menu.findItem(R.id.actionEnable).setVisible(!products.isENABLED());
        menu.findItem(R.id.actionDisable).setVisible(products.isENABLED());

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionEnable:
                enableDisableConfirmation(false);
                return true;
            case R.id.actionDisable:
                enableDisableConfirmation(true);
                return true;
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
        DialogFragment newFragment =  ProductsDialogfragment.newInstance(type, (isNew)?null:products, this);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void enableDisableConfirmation(final boolean disable){
         final Dialog d = Funciones.getCustomDialog2Btn(MaintenanceProducts.this, getResources().getColor(R.color.colorPrimary),
                 "Confirmacion", "Esta seguro que desea aplicar los cambios a ["+products.getDESCRIPTION()+"]?",
                 disable ? R.drawable.ic_visibility_off : R.drawable.ic_visibility, null, null);
         final CardView btnAceptar = d.findViewById(R.id.btnPositive);
         CardView btnNegative = d.findViewById(R.id.btnNegative);

         btnAceptar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 d.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                 btnAceptar.setEnabled(false);
                 d.findViewById(R.id.btnNegative).setEnabled(false);

                 if(products != null){
                     products.setENABLED(!disable);
                         productsController.sendToFireBase(products, new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 btnAceptar.setEnabled(true);
                                 d.findViewById(R.id.btnNegative).setEnabled(true);
                                 d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                 Toast.makeText(MaintenanceProducts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                             }
                         });

                         productsController.searchProductFromFireBase(products.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                             @Override
                             public void onSuccess(QuerySnapshot querySnapshot) {
                                 if(querySnapshot != null && querySnapshot.size()> 0){
                                     ProductsController.getInstance(MaintenanceProducts.this).update(products);
                                     refreshList();
                                     d.dismiss();
                                 }else{
                                     btnAceptar.setEnabled(true);
                                     d.findViewById(R.id.btnNegative).setEnabled(true);
                                     d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                     Toast.makeText(MaintenanceProducts.this,"Error aplicando cambios. Intente nuevamente", Toast.LENGTH_LONG).show();
                                 }

                             }
                         }, new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 btnAceptar.setEnabled(true);
                                 d.findViewById(R.id.btnNegative).setEnabled(true);
                                 d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                 Toast.makeText(MaintenanceProducts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                             }
                         });


                 }
             }
         });
         btnNegative.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 d.dismiss();
             }
         });

         d.show();
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(products != null){
            description = products.getDESCRIPTION();
        }
        final Dialog d = Funciones.getAlertDeleteAllDependencies(MaintenanceProducts.this,description,
                (type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)?productsController.getDependencies(products.getCODE()):productsInvController.getDependencies(products.getCODE())));
        final CardView btnAceptar = d.findViewById(R.id.btnPositive);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                btnAceptar.setEnabled(false);
                d.findViewById(R.id.btnNegative).setEnabled(false);

                if(products != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        productsController.deleteFromFireBase(products, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                d.findViewById(R.id.btnNegative).setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProducts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                        productsController.searchProductFromFireBase(products.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if(querySnapshot == null || querySnapshot.size() == 0){
                                    for(KV2 data: ProductsController.getInstance(MaintenanceProducts.this).getDependencies(products.getCODE())){
                                        String sql = "DELETE FROM "+data.getKey()+" WHERE "+data.getValue()+" = '"+data.getValue2()+"'";
                                        DB.getInstance(MaintenanceProducts.this).getWritableDatabase().execSQL(sql);
                                    }
                                    ProductsController.getInstance(MaintenanceProducts.this).delete(products);
                                    refreshList();
                                    d.dismiss();
                                }else{
                                    btnAceptar.setEnabled(true);
                                    d.findViewById(R.id.btnNegative).setEnabled(true);
                                    d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                    Toast.makeText(MaintenanceProducts.this,"Error eliminando producto. Intente nuevamente", Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                d.findViewById(R.id.btnNegative).setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProducts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        productsInvController.deleteFromFireBase(products);
                    }

                }
            }
        });

        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void refreshList(){
        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;

        ArrayList<String> x = new ArrayList<>();
        if(lastSearch != null){
            where+=" AND p."+ProductsController.DESCRIPTION+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(lastFamilia != null){
            where+= "AND pt."+ ProductsTypesController.CODE+" = ? ";
            x.add(lastFamilia);
        }

        if(lastGrupo != null){
            where+= "AND pst."+ProductsSubTypesController.CODE+" = ? ";
            x.add(lastGrupo);
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            objects.addAll(productsController.getProductsPRM(where, args, null));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            objects.addAll(productsInvController.getProductsPRM(where, args, null));
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        products = null;
        ProductRowModel sr = (ProductRowModel)obj;

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            products = productsController.getProductByCode(sr.getCode());
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            products = productsInvController.getProductByCode(sr.getCode());
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

    @Override
    public void dialogClosed(Object o) {
        refreshList();
    }

}

