package com.far.basesales;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.UserControlRowModel;
import com.far.basesales.Adapters.UserControlRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Dialogs.UserControlDialogFragment;
import com.far.basesales.Generic.KV;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MaintenanceUsersControl extends AppCompatActivity implements ListableActivity {

    RecyclerView rvList;
    ArrayList<UserControlRowModel> objects;
    UserControlRowEditionAdapter adapter;
    UserControlController userControlController;
    UserControl userControl = null;
    Licenses licence;
    String lastSearch = null;
    Spinner spnUserControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);
        userControlController = UserControlController.getInstance(MaintenanceUsersControl.this);
        licence = LicenseController.getInstance(MaintenanceUsersControl.this).getLicense();


        rvList = findViewById(R.id.rvList);
        spnUserControl = findViewById(R.id.spn);
        ((TextView)findViewById(R.id.spnTitle)).setText("Nivel");

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceUsersControl.this);
        rvList.setLayoutManager(manager);
        adapter = new UserControlRowEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        userControlController.fillSpinnerControlLevels(spnUserControl, false);
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
        userControlController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                try {
                    userControlController.delete(null, null);//limpia la tabla

                    for (DocumentSnapshot ds : querySnapshot) {

                        UserControl pt = ds.toObject(UserControl.class);
                        userControlController.insert(pt);
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        newFragment.show(ft, "dialog");
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
                    userControlController.deleteFromFireBase(userControl);
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
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();

        if(lastSearch != null){
            where+=" AND UC."+UserControlController.CONTROL+" like  ? ";
            x.add(lastSearch+"%");
        }
        if(spnUserControl.getSelectedItem() != null && !((KV)spnUserControl.getSelectedItem()).getKey().equals("-1")){
            where+= "AND UC."+ UserControlController.TARGET+" = ? ";
            x.add(((KV)spnUserControl.getSelectedItem()).getKey());
        }

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }

        objects.addAll(userControlController.getTablaCodeSRM(where,args, null));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        userControl = null;
        UserControlRowModel sr = (UserControlRowModel)obj;
        userControl = userControlController.getUserControlByCode(sr.getCode());

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

}
