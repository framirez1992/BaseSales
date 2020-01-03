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
import com.far.basesales.CloudFireStoreObjects.MeasureUnits;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.MeasureUnitsInvController;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Dialogs.MeasureUnitDialogFragment;
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


public class MaintenanceUnitMeasure extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;
    MeasureUnitsController measureUnitsController;
    MeasureUnitsInvController measureUnitsInvController;
    MeasureUnits measureUnit;
    Licenses licence;
    String lastSearch = null;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_screen);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_TYPE_FAMILY) ){
            finish();
            return;
        }

        type = getIntent().getStringExtra(CODES.EXTRA_TYPE_FAMILY);
        measureUnitsController = MeasureUnitsController.getInstance(MaintenanceUnitMeasure.this);
        measureUnitsInvController = MeasureUnitsInvController.getInstance(MaintenanceUnitMeasure.this);
        licence = LicenseController.getInstance(MaintenanceUnitMeasure.this).getLicense();


        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceUnitMeasure.this);
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
        /*
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            measureUnitsController.getReferenceFireStore().addSnapshotListener(MaintenanceUnitMeasure.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    measureUnitsController.delete(null, null);//limpia la tabla

                    for(DocumentSnapshot ds: querySnapshot){

                        MeasureUnits mu = ds.toObject(MeasureUnits.class);
                        measureUnitsController.insert(mu);
                    }

                    refreshList(lastSearch);
                }
            });
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            measureUnitsInvController.getReferenceFireStore().addSnapshotListener(MaintenanceUnitMeasure.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    measureUnitsInvController.delete(null, null);//limpia la tabla

                    for(DocumentSnapshot ds: querySnapshot){

                        MeasureUnits mu = ds.toObject(MeasureUnits.class);
                        measureUnitsInvController.insert(mu);
                    }

                    refreshList(lastSearch);
                }
            });
        }*/

    }
    public void callAddDialog(boolean isNew){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  MeasureUnitDialogFragment.newInstance(type, (isNew)?null:measureUnit, this);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(measureUnit != null){
            description = measureUnit.getDESCRIPTION();
        }

        final Dialog d = Funciones.getAlertDeleteAllDependencies(MaintenanceUnitMeasure.this,description,
                measureUnitsController.getDependencies(measureUnit.getCODE()));
        final CardView btnAceptar = d.findViewById(R.id.btnPositive);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.findViewById(R.id.llProgress).setVisibility(View.VISIBLE);
                btnAceptar.setEnabled(false);
                d.findViewById(R.id.btnNegative).setEnabled(false);

                if(measureUnit != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        measureUnitsController.deleteFromFireBase(measureUnit, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.getException() != null){
                                    btnAceptar.setEnabled(true);
                                    d.findViewById(R.id.btnNegative).setEnabled(true);
                                    d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                    Toast.makeText(MaintenanceUnitMeasure.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                for(KV2 data: MeasureUnitsController.getInstance(MaintenanceUnitMeasure.this).getDependencies(measureUnit.getCODE())){
                                    String sql = "DELETE FROM "+data.getKey()+" WHERE "+data.getValue()+" = '"+data.getValue2()+"'";
                                    DB.getInstance(MaintenanceUnitMeasure.this).getWritableDatabase().execSQL(sql);
                                }
                                measureUnitsController.delete(measureUnit);
                                refreshList(lastSearch);
                                d.dismiss();
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnAceptar.setEnabled(true);
                                d.findViewById(R.id.btnNegative).setEnabled(true);
                                d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                                Toast.makeText(MaintenanceUnitMeasure.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        measureUnitsInvController.deleteFromFireBase(measureUnit);
                    }

                }
                //d.dismiss();
            }
        });

        d.show();
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }
   /* public void callDeleteConfirmation(){

        String description = "";
        if(measureUnit != null){
            description = measureUnit.getDESCRIPTION();
        }

        String msg = "Esta seguro que desea eliminar \'"+description+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(measureUnit != null){
                    if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                        measureUnitsController.deleteFromFireBase(measureUnit);
                    }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
                        measureUnitsInvController.deleteFromFireBase(measureUnit);
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
        Window window = d.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }*/

    public void refreshList(String data){
        objects.clear();
        String where = (data!= null)?MeasureUnitsController.DESCRIPTION+" like  ? ":null;
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            objects.addAll(measureUnitsController.getMeasureUnitsSRM(where, (data != null)?new String[]{data+"%"}:null, null));
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            objects.addAll(measureUnitsInvController.getMeasureUnitsSRM(where, (data != null)?new String[]{data+"%"}:null, null));
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        measureUnit = null;
        SimpleRowModel sr = (SimpleRowModel)obj;
        if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
            measureUnit = measureUnitsController.getMeasureUnitByCode(sr.getId());
        }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY)){
            measureUnit = measureUnitsInvController.getMeasureUnitByCode(sr.getId());
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

    @Override
    public void dialogClosed(Object o) {
        refreshList(lastSearch);
    }
}
