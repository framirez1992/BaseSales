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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.SimpleRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsTypes;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Dialogs.ProductTypeDialogFragment;
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

public class MaintenanceProductTypes extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    ProductsTypesController productsTypesController;
    ProductsTypesInvController productsTypesInvController;

    ProductsTypes productsType = null;
    Licenses licence;
    String lastSearch = null;

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
        productsTypesController = ProductsTypesController.getInstance(MaintenanceProductTypes.this);
        productsTypesInvController = ProductsTypesInvController.getInstance(MaintenanceProductTypes.this);
        licence = LicenseController.getInstance(MaintenanceProductTypes.this).getLicense();

        findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceProductTypes.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList(lastSearch);



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
           /* productsTypesController.getReferenceFireStore().addSnapshotListener(MaintenanceProductTypes.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsTypesController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        ProductsTypes pt = ds.toObject(ProductsTypes.class);
                        productsTypesController.insert(pt);
                    }
                    refreshList(lastSearch);

                }
            });*/
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
           /* productsTypesInvController.getReferenceFireStore().addSnapshotListener(MaintenanceProductTypes.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    productsTypesInvController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        ProductsTypes pt = ds.toObject(ProductsTypes.class);
                        productsTypesInvController.insert(pt);
                    }
                    refreshList(lastSearch);

                }
            });*/
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
            newFragment = ProductTypeDialogFragment.newInstance(type,null, this);
        }else {
            newFragment = ProductTypeDialogFragment.newInstance(type, productsType, this);
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(productsType != null){
            description = productsType.getDESCRIPTION();
        }

        String msg = "Esta seguro que desea eliminar \'"+description+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        final CardView btnAceptar = d.findViewById(R.id.btnPositive);
        final CardView btnCancelar = d.findViewById(R.id.btnNegative);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                btnAceptar.setEnabled(false);
                btnCancelar.setEnabled(false);

                String msgDependency = getMsgDependency();
                if(!msgDependency.isEmpty()) {
                    Funciones.showAlertDependencies(MaintenanceProductTypes.this, msgDependency);
                    d.dismiss();
                    return;
                }

                if(productsType != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        productsTypesController.deleteFromFireBase(productsType, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                btnCancelar.setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProductTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        productsTypesController.searchProductTypeFromFireBase(productsType.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                if(querySnapshot == null || querySnapshot.size()==0){
                                    productsTypesController.delete(productsType);
                                    refreshList(lastSearch);
                                    d.dismiss();
                                }else{
                                    btnAceptar.setEnabled(true);
                                    btnCancelar.setEnabled(true);
                                    d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                    Toast.makeText(MaintenanceProductTypes.this, "Error borrando Familia. Intente nuevamente", Toast.LENGTH_LONG).show();
                                }

                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                btnCancelar.setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceProductTypes.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        productsTypesInvController.deleteFromFireBase(productsType);
                    }

                }
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

    public void refreshList(String data){
        objects.clear();
        String where = " 1 = 1 ";
        ArrayList<String> values = new ArrayList<>();
        String[] args = null;

        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
            if(data != null){
                where += " AND "+ProductsTypesController.DESCRIPTION+" like ?";
                values.add(data+"%");
            }
            args = values.toArray(new String[values.size()]);

            objects.addAll(productsTypesController.getAllProductTypesSRM(where, args));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            if(data != null){
                where += " AND "+ProductsTypesInvController.DESCRIPTION+" like ?";
                values.add(data+"%");
            }
            args = values.toArray(new String[values.size()]);
            objects.addAll(productsTypesInvController.getAllProductTypesSRM(where, args));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
         productsType = null;
         SimpleRowModel sr = (SimpleRowModel)obj;
         if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)) {
             productsType = productsTypesController.getProductTypeByCode(sr.getId());
         }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
             productsType = productsTypesInvController.getProductTypeByCode(sr.getId());
         }

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
        String msgDependency ="";
        if(productsType != null){
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
               msgDependency = productsTypesController.hasDependencies(productsType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
               msgDependency = productsTypesInvController.hasDependencies(productsType.getCODE());
            }

        }
       return msgDependency;
    }

    @Override
    public void dialogClosed(Object o) {
        refreshList(lastSearch);
    }
}
