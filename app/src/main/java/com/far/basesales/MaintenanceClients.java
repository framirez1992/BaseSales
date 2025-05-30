package com.far.basesales;

import android.app.Dialog;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.Adapters.ClientEditionAdapter;
import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Dialogs.ClientsDialogFragment;
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

public class MaintenanceClients extends AppCompatActivity implements ListableActivity, DialogCaller {

    RecyclerView rvList;
    ArrayList<ClientRowModel> objects;
    ClientEditionAdapter adapter;
    ClientsController clientsController;
    Clients clients;
    Licenses licence;
    String lastSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenance_w_spinner);

        clientsController = ClientsController.getInstance(MaintenanceClients.this);
        licence = LicenseController.getInstance(MaintenanceClients.this).getLicense();

        findViewById(R.id.cvSpinner).setVisibility(View.GONE);

        rvList = findViewById(R.id.rvList);

        objects = new ArrayList<>();

        LinearLayoutManager manager = new LinearLayoutManager(MaintenanceClients.this);
        rvList.setLayoutManager(manager);
        adapter = new ClientEditionAdapter(this,this, objects);
        rvList.setAdapter(adapter);

        refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        clientsController.getReferenceFireStore().addSnapshotListener(MaintenanceClients.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                clientsController.delete(null, null);//limpia la tabla

                for(DocumentSnapshot ds: querySnapshot){

                    Clients c = ds.toObject(Clients.class);
                    clientsController.insert(c);
                }

                refreshList();
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
        DialogFragment newFragment =  ClientsDialogFragment.newInstance((isNew)?null:clients, this);
        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }

    public void callDeleteConfirmation(){

        String description = "";
        if(clients != null){
            description = clients.getNAME();
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

                if(clients != null){
                    clientsController.deleteFromFireBase(clients, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btnAceptar.setEnabled(true);
                            btnCancelar.setEnabled(true);
                            d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                            Toast.makeText(MaintenanceClients.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                   clientsController.searchClientFromFireBase(clients.getCODE(), new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot querySnapshot) {

                           if(querySnapshot == null || querySnapshot.size()==0){
                               clientsController.delete(clients);
                               refreshList();
                               d.dismiss();
                           }else{
                               btnAceptar.setEnabled(true);
                               btnCancelar.setEnabled(true);
                               d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                               Toast.makeText(MaintenanceClients.this, "Error eliminando cliente. Intente nuevamente", Toast.LENGTH_LONG).show();
                           }

                       }
                   }, new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           btnAceptar.setEnabled(true);
                           btnCancelar.setEnabled(true);
                           d.findViewById(R.id.llProgress).setVisibility(View.INVISIBLE);
                           Toast.makeText(MaintenanceClients.this, e.getMessage(), Toast.LENGTH_LONG).show();
                       }
                   }) ;
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

    public void refreshList(){
        objects.clear();
        String where = "1 = 1 ";
        String[] args = null;
        ArrayList<String> x = new ArrayList<>();

        if(lastSearch != null){
            where += "AND (u."+ClientsController.NAME+" like  ? OR "+ClientsController.CODE+" like ? OR "+ClientsController.PHONE+" like ?)";
            x.add(lastSearch+"%" );
            x.add("%"+lastSearch+"%");
            x.add("%"+lastSearch+"%");
        }
       /* if(spn.getSelectedItem() != null && !((KV)spn.getSelectedItem()).getKey().equals("0")){
            where += " AND ut."+ UserTypesController.CODE+" like ? ";
            x.add(((KV)spn.getSelectedItem()).getKey());
        }*/

        if(x.size() > 0){
            args = x.toArray(new String[x.size()]);
        }
        objects.addAll(clientsController.getClientSRM(where, args, null));
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(Object obj) {
        clients = null;
        ClientRowModel sr = (ClientRowModel)obj;

        clients = clientsController.getClientByCode(sr.getCode());

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
