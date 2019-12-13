package com.far.basesales;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.SimpleRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Dialogs.UserDeviceDialogFragment;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminLicenseUserDevice extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<SimpleRowModel> objects;
    SimpleRowEditionAdapter adapter;

    UsersDevices ud = null;
    Licenses licenses;
    String lastSearch = null;
    FirebaseFirestore fs;
    ArrayList<UsersDevices> usersDevices;
    ArrayList<Users>users;
    ArrayList<Devices> devices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        licenses = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();
        usersDevices = new ArrayList<>();
        users = new ArrayList<>();
        devices = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseUserDevice.this);
        rvList.setLayoutManager(manager);
        adapter = new SimpleRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList();



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
                prepareForCallDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_delete, menu);
        menu.findItem(R.id.actionEdit).setVisible(false);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionDelete:
                callDeleteConfirmation();
                return  true;

            default:return super.onContextItemSelected(item);
        }
    }

    public void setUpListeners(){

        fs.collection(Tablas.generalUsers).document(licenses.getCODE())
                .collection(Tablas.generalUsersUsersDevices)
                .addSnapshotListener(AdminLicenseUserDevice.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        usersDevices = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            UsersDevices t = ds.toObject(UsersDevices.class);
                            usersDevices.add(t);
                        }
                        refreshList();

                    }
                });

        fs.collection(Tablas.generalLicencias).document(licenses.getCODE())
                .collection(Tablas.generalLicenciasDevices)
                .addSnapshotListener(AdminLicenseUserDevice.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        devices = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Devices t = ds.toObject(Devices.class);
                            devices.add(t);
                        }
                    }
                });

        fs.collection(Tablas.generalUsers).document(licenses.getCODE())
                .collection(Tablas.generalUsersUsers)
                .addSnapshotListener(AdminLicenseUserDevice.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        users = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Users t = ds.toObject(Users.class);
                            users.add(t);
                        }
                    }
                });
    }
    public void callAddDialog(ArrayList<Users> nonAssignedUser, ArrayList<Devices>nonAssignedDevices){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
            newFragment = UserDeviceDialogFragment.newInstance(this,licenses.getCODE(),nonAssignedUser,nonAssignedDevices );


        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void callDeleteConfirmation(){

        String msg = "Esta seguro que desea eliminar la relacion \'Device:"+ud.getCODEDEVICE()+"- Codeuser:"+ud.getCODEUSER()+"\'  permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fs.collection(Tablas.generalUsers).document(licenses.getCODE()).collection(Tablas.generalUsersUsersDevices).document(ud.getCODE()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ud = null;
                                d.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLicenseUserDevice.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

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

    public void refreshList(){
        objects.clear();
        for(UsersDevices t: usersDevices){
            objects.add(new SimpleRowModel(t.getCODE(), "Device:"+t.getCODEDEVICE()+"  - CodeUser: "+t.getCODEUSER(), true));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        SimpleRowModel item = (SimpleRowModel)obj;
        for(UsersDevices t :usersDevices){
            if(t.getCODE().equals(item.getId())){
                ud = t;
                break;
            }
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


  /*  public String getMsgDependency(){
        String msgDependency ="";
        if(productsType != null){
            if(type.equals(CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE)){
                msgDependency = productsTypesController.hasDependencies(productsType.getCODE());
            }else if(type.equals(CODES.ENTITY_TYPE_EXTRA_INVENTORY) ){
                msgDependency = productsTypesInvController.hasDependencies(productsType.getCODE());
            }

        }
        return msgDependency;
    }*/



    public FirebaseFirestore getFs(){
        return fs;
    }

    public void prepareForCallDialog(){
        ArrayList<Users> nau = new ArrayList<>();
        ArrayList<Devices> nad = new ArrayList<>();

        for(Users u: users){
            boolean finded = false;
            for(UsersDevices ud : usersDevices){
                if(u.getCODE().equals(ud.getCODEUSER())){
                    finded = true;
                    break;
                }
            }
            if(!finded){
                nau.add(u);
            }

        }

        if(nau.size()<1){
            Toast.makeText(AdminLicenseUserDevice.this, "Todos los usuarios estan asignados", Toast.LENGTH_LONG).show();
            return;
        }

        for(Devices d: devices){
            boolean finded = false;
            for(UsersDevices ud : usersDevices){
                if(d.getCODE().equals(ud.getCODEDEVICE())){
                    finded = true;
                    break;
                }
            }
            if(!finded){
                nad.add(d);
            }

        }

        if(nad.size()<1){
            Toast.makeText(AdminLicenseUserDevice.this, "Todos los dispositivos estan asignados", Toast.LENGTH_LONG).show();
            return;
        }

        callAddDialog(nau, nad);
    }
}
