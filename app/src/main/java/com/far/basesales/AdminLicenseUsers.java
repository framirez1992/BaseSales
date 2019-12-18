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

import com.far.basesales.Adapters.AdminUserRowAdapter;
import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.Adapters.Models.UserRowModel;
import com.far.basesales.Adapters.SimpleRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Controllers.UsersDevicesController;
import com.far.basesales.Dialogs.AdminUserDialogFragment;
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
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class AdminLicenseUsers extends AppCompatActivity implements ListableActivity {

        RecyclerView rvList;
        ArrayList<UserRowModel> objects;
        AdminUserRowAdapter adapter;

        Users user = null;
        Licenses license;
        String lastSearch = null;
        FirebaseFirestore fs;
        ArrayList<Users> users;
        ArrayList<Company> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        license = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);
        objects = new ArrayList<>();
        users = new ArrayList<>();
        companies = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseUsers.this);
        rvList.setLayoutManager(manager);
        adapter = new AdminUserRowAdapter(this,this, objects);
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

        fs.collection(Tablas.generalUsers).document(license.getCODE())
                .collection(Tablas.generalUsersUsers)
                .addSnapshotListener(AdminLicenseUsers.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        users = new ArrayList<>();
                        for (DocumentSnapshot ds : querySnapshot) {
                            Users t = ds.toObject(Users.class);
                            t.setDocumentReference(ds.getReference());
                            users.add(t);
                        }
                        refreshList();

                    }
                });

        fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersCompany).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot querySnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                companies = new ArrayList<>();
                for (DocumentSnapshot ds : querySnapshot) {
                    Company c = ds.toObject(Company.class);
                    companies.add(c);
                }
            }
        });
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
            newFragment = AdminUserDialogFragment.newInstance(this,null,companies, license.getCODE());
        }else {
            newFragment = AdminUserDialogFragment.newInstance(this,user,companies, license.getCODE());
        }

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void callDeleteConfirmation(){

        String msg = "Esta seguro que desea eliminar el usuario \'"+user.getUSERNAME()+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*fs.collection(Tablas.generalUsers).document(codeLicense).collection(Tablas.generalUsersToken).document(user.getCODE()).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                user = null;
                                d.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLicenseUsers.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });*/

                fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUsersDevices)
                        .whereEqualTo(UsersDevicesController.CODEUSER, user.getCODE()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot querySnapshot) {
                                WriteBatch lote = fs.batch();
                                if(querySnapshot!=null && !querySnapshot.isEmpty()){
                                    for(DocumentSnapshot ds : querySnapshot){
                                        lote.delete(ds.getReference());
                                    }
                                }
                                lote.delete(user.getDocumentReference());
                                lote.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        user = null;
                                        d.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdminLicenseUsers.this, e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLicenseUsers.this, e.getMessage(),Toast.LENGTH_LONG).show();
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
        for(Users t: users){
            //String code, String systemCode, String userName,String password, String userRole, boolean active, boolean inServer
            objects.add(new UserRowModel(t.getCODE(), t.getSYSTEMCODE(), t.getUSERNAME(), t.getPASSWORD(), t.getROLE(), t.isENABLED(), true));
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        UserRowModel item = (UserRowModel)obj;
        for(Users t :users){
            if(t.getCODE().equals(item.getCode())){
                user = t;
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

    public Users getUserByCode(String code){
        for(Users u: users){
            if(u.getCODE().equals(code)){
                return u;
            }
        }
        return null;
    }
}

