package com.far.basesales;

import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.Adapters.SimpleSelectionRowAdapter;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;


public class MainAssignation extends AppCompatActivity {

    String codeTable;
    String codeTarget;

    RecyclerView rvList;
    LinearLayout ll1, ll2, ll3, llSave;
    TextView tvTitle, tvSpn1, tvSpn2, tvSpn3;
    Spinner spn1, spn2, spn3;
    String lastSearch;

    UserControlController userControlController;
    //SalesController salesController;
    ArrayList<SimpleSeleccionRowModel> selected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_assignation);
        codeTable = getIntent().getExtras().getString(CODES.EXTRA_MAINASSIGNATION_TABLE);
        codeTarget = getIntent().getExtras().getString(CODES.EXTRA_MAINASSIGNATION_TARGET);

        rvList = findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(MainAssignation.this));

        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll3 = findViewById(R.id.ll3);
        llSave = findViewById(R.id.llSave);
        tvTitle = findViewById(R.id.tvTitle);
        tvSpn1 = findViewById(R.id.tvSpn1);
        tvSpn2 = findViewById(R.id.tvSpn2);
        tvSpn3 = findViewById(R.id.tvSpn3);
        spn1 = findViewById(R.id.spn1);
        spn2 = findViewById(R.id.spn2);
        spn3 = findViewById(R.id.spn3);

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            save();
            }
        });

        initByCodeTable();
        initByCodetarget();
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
                //callAddDialog(true);
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
                //callAddDialog(false);
                return true;
            case R.id.actionDelete:
                //callDeleteConfirmation();
                return  true;

            default:return super.onContextItemSelected(item);
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

    public void refreshList(String lastSearch){
        selected.clear();
        ArrayList<SimpleSeleccionRowModel> data = new ArrayList<>();
      if(codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_ROLESCONTROL)){
           data = userControlController.getRolesControlSSRM(((KV)spn1.getSelectedItem()).getKey());
        }else if(codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL)){
           data = userControlController.getUsersControlSSRM(((KV)spn2.getSelectedItem()).getKey());
        }

        for(SimpleSeleccionRowModel s: data){
            if(s.isChecked()){
                selected.add(s);
            }
        }
        SimpleSelectionRowAdapter adapter = new SimpleSelectionRowAdapter(MainAssignation.this,
                data,selected);
        rvList.setAdapter(adapter);
    }

    public void initByCodeTable(){
        if(codeTable.equals(UserControlController.TABLE_NAME)){
            userControlController =  UserControlController.getInstance(MainAssignation.this);
        }/*else if(codeTable.equals(SalesController.TABLE_NAME)){
            userControlController =  UserControlController.getInstance(MainAssignation.this);
            salesController = SalesController.getInstance(MainAssignation.this);
        }*/
    }

    public void initByCodetarget(){
       /* if(codeTarget.equals(CODES.USERCONTROL_TABLEASSIGN)){
            ll3.setVisibility(View.GONE);
            tvTitle.setText("Asignacion de Mesas");
            tvSpn1.setText("Nivel");
            tvSpn2.setText("Objetivo");

            spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    userControlController.fillSpinnerByControlLevel(spn2, kv.getKey());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    refreshList(lastSearch);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            userControlController.fillSpinnerControlLevels(spn1, false);


        }else*/ if(codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_ROLESCONTROL)){
            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.GONE);
            tvTitle.setText("Control de roles");
            tvSpn1.setText("Rol");


            spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    refreshList(lastSearch);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            UserTypesController.getInstance(MainAssignation.this).fillSpnUserTypes(spn1, false);

        }else if(codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL)){
            ll3.setVisibility(View.GONE);
            tvTitle.setText("Control de Usuarios");
            tvSpn1.setText("Rol");
            tvSpn2.setText("Usuario");

            spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    UsersController.getInstance(MainAssignation.this).fillSpnUserByUserType(spn2, kv.getKey(), true);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    refreshList(lastSearch);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            UserTypesController.getInstance(MainAssignation.this).fillSpnUserTypes(spn1, false);

        }/*else if(codeTarget.equals(CODES.USERCONTROL_ORDERSPLIT)){
            ll3.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            tvTitle.setText("Agrupar Productos");
            tvSpn1.setText("Por");

            spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    refreshList(lastSearch);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            userControlController.fillSpinnerOrderSplitType(spn1);
        }else if(codeTarget.equals(CODES.USERCONTROL_ORDERSPLITDESTINY)){
            ll3.setVisibility(View.GONE);
            tvTitle.setText("Configuracion de destinos ordenes");
            tvSpn1.setText("Rol");
            tvSpn2.setText("Usuario");

            spn1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    UsersController.getInstance(MainAssignation.this).fillSpnUserByUserType(spn2, kv.getKey(),false);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spn2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    KV kv = (KV)parent.getItemAtPosition(position);
                    refreshList(lastSearch);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            userControlController.fillOrderDispachRoles(spn1);
        }else if(codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_ORDERMOVE)){
            ll3.setVisibility(View.GONE);
            tvTitle.setText("Reasignar Ordenes");
            tvSpn1.setText("Desde");
            tvSpn2.setText("Hasta");

            userControlController.fillSpinnerOrderMoveUsersFrom(spn1);
        }*/
    }

    public void save(){
         if(codeTable.equals(UserControlController.TABLE_NAME) && codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_ROLESCONTROL)){
            saveUserControlRolesControl();
        }else if(codeTable.equals(UserControlController.TABLE_NAME) && codeTarget.equals(CODES.EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL)){
            saveUsersControl();
        }

    }
  /*  public void saveUserControlTableAssign(){
        String control = codeTarget;
        String target = ((KV)spn1.getSelectedItem()).getKey();
        String targetCode = ((KV)spn2.getSelectedItem()).getKey();

        ArrayList<UserControl> list = new ArrayList<>();
        for(SimpleSeleccionRowModel ssr: selected){
           // String code, String target, String targetCode, String control, String value, boolean active
            list.add(new UserControl(Funciones.generateCode(),target,targetCode,control,ssr.getCode(),true));
        }

        userControlController.sendToFireBase(control,target, targetCode,list);
    }*/

    public void saveUserControlRolesControl(){
        String target = CODES.USERSCONTROL_TARGET_USER_ROL;
        String targetCode = ((KV)spn1.getSelectedItem()).getKey();

        ArrayList<UserControl> list = new ArrayList<>();
        for(SimpleSeleccionRowModel ssr: selected){
            // String code, String target, String targetCode, String control, String value, boolean active
            list.add(new UserControl(Funciones.generateCode(),target,targetCode,ssr.getCode(),"",true));
        }

        userControlController.sendToFireBase(target, targetCode,list);
    }

    public void saveUsersControl(){

        String target = CODES.USERSCONTROL_TARGET_USER;
        String targetCode = ((KV)spn2.getSelectedItem()).getKey();
        ArrayList<UserControl> list = new ArrayList<>();
        if(targetCode.equals("-1")){
            String role = ((KV)spn1.getSelectedItem()).getKey();
            for(Users u : UsersController.getInstance(MainAssignation.this).getUsers(UsersController.ROLE+" = ?", new String[]{role}, null)){
                for (SimpleSeleccionRowModel ssr : selected) {
                    // String code, String target, String targetCode, String control, String value, boolean active
                    list.add(new UserControl(Funciones.generateCode(), target, u.getCODE(), ssr.getCode(), "", true));
                }
            }

        }else {
            for (SimpleSeleccionRowModel ssr : selected) {
                // String code, String target, String targetCode, String control, String value, boolean active
                list.add(new UserControl(Funciones.generateCode(), target, targetCode, ssr.getCode(), "", true));
            }
        }

        userControlController.sendToFireBase(target, targetCode,list);
    }

  /*  public void saveUserControlOrderSplit(){
        String control = codeTarget;
        String target = CODES.USERSCONTROL_TARGET_COMPANY;
        String targetCode = UsersController.getInstance(MainAssignation.this).getUserByCode(Funciones.getCodeuserLogged(MainAssignation.this)).getCOMPANY();

        ArrayList<UserControl> list = new ArrayList<>();
        for(SimpleSeleccionRowModel ssr: selected){
            // String code, String target, String targetCode, String control, String value, boolean active
            list.add(new UserControl(Funciones.generateCode(),target,targetCode,control,ssr.getCode(),true));
        }

        userControlController.sendToFireBase(control,target, targetCode,list);
    }

    public void saveUserControlOrderSplitDestiny(){
        String control = codeTarget;
        String target = CODES.USERSCONTROL_TARGET_USER;
        String targetCode = ((KV)spn2.getSelectedItem()).getKey();

        ArrayList<UserControl> list = new ArrayList<>();
        for(SimpleSeleccionRowModel ssr: selected){
            // String code, String target, String targetCode, String control, String value, boolean active
            list.add(new UserControl(Funciones.generateCode(),target,targetCode,control,ssr.getCode(),true));
        }

        userControlController.sendToFireBase(control,target, targetCode,list);
    }*/
}
