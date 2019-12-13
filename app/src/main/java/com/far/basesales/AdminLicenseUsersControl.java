package com.far.basesales;

import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.UserControlRowModel;
import com.far.basesales.Adapters.UserControlRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.CloudFireStoreObjects.UserTypes;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminLicenseUsersControl extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<UserControlRowModel> objects;
    UserControlRowEditionAdapter adapter;
    ArrayList<Users> users;
    ArrayList<UserTypes> userTypes;
    ArrayList<Company> companies;
    ArrayList<UserControl> userControls;
    UserControl userControl = null;
    Licenses license;
    FirebaseFirestore fs;
    String lastSearch = null;
    Spinner spnUserControl;

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


        rvList = findViewById(R.id.rvList);
        spnUserControl = findViewById(R.id.spn);
        ((TextView)findViewById(R.id.spnTitle)).setText("Nivel");

        objects = new ArrayList<>();
        userControls = new ArrayList<>();
        users = new ArrayList<>();
        userTypes = new ArrayList<>();
        companies = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseUsersControl.this);
        rvList.setLayoutManager(manager);
        adapter = new UserControlRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        fillSpinnerControlLevels(spnUserControl);
        spnUserControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV obj = ((KV)spnUserControl.getSelectedItem());
                if(obj.getKey().equals("-1"))
                    refreshList();
                else
                    refreshList();//DESCRIPCION
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
        fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUserControl)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                try {
                    userControls = new ArrayList<>();

                    for (DocumentSnapshot ds : querySnapshot) {
                        UserControl pt = ds.toObject(UserControl.class);
                        userControls.add(pt);
                    }

                    refreshList();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });


        fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUsers)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                        try {
                            userControls = new ArrayList<>();

                            for (DocumentSnapshot ds : querySnapshot) {
                                UserControl pt = ds.toObject(UserControl.class);
                                userControls.add(pt);
                            }

                            refreshList();
                        }catch (Exception e1){
                            e1.printStackTrace();
                        }
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
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;
        if(isNew)
            newFragment = UserControlDialogFragment.newInstance(null);
        else
            newFragment = UserControlDialogFragment.newInstance(userControl);


        // Create and show the dialog.
        newFragment.show(ft, "dialog");*/
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(userControl != null){
            description = userControl.getCONTROL();
        }

        String msg = "Esta seguro que desea eliminar \'"+description+"\' permanentemente?";
        final Dialog d = Funciones.getCustomDialog2Btn(this,getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        CardView btnAceptar = d.findViewById(R.id.btnPositive);
        CardView btnCancelar = d.findViewById(R.id.btnNegative);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userControl != null){
                    fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUserControl).document(userControl.getCODE()).delete();
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

    }

    public void refreshList(){

        objects.clear();
        for(UserControl uc : userControls){
            if(spnUserControl.getSelectedItem() != null && !((KV)spnUserControl.getSelectedItem()).getKey().equals("-1") && ((KV)spnUserControl.getSelectedItem()).getKey().equals(uc.getTARGET())){
                //String code, String description, String target, String targetDescription, String targetCode, String targetCodedescription, boolean active, boolean inServer
                objects.add(new UserControlRowModel(uc.getCODE(), uc.getCONTROL(),uc.getTARGET(), uc.getTARGET(),uc.getTARGETCODE(),uc.getCODE(), uc.getACTIVE(),true));
            }

        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        userControl = null;
        UserControlRowModel sr = (UserControlRowModel)obj;
        //userControl = userControlController.getUserControlByCode(sr.getCode());

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

    public void fillSpinnerControlLevels(Spinner spn){
        ArrayList<KV> list = new ArrayList<>();
        list.add(new KV(CODES.USERSCONTROL_TARGET_USER_ROL, "Cargo"));
        list.add(new KV(CODES.USERSCONTROL_TARGET_COMPANY, "Empresa"));
        list.add(new KV(CODES.USERSCONTROL_TARGET_USER, "Usuario"));
        spn.setAdapter(new ArrayAdapter<KV>(AdminLicenseUsersControl.this,android.R.layout.simple_list_item_1,list));
    }

}
