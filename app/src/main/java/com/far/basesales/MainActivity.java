package com.far.basesales;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsControl;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.ProductsControlController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Controllers.UsersDevicesController;
import com.far.basesales.Dialogs.BirthDayDialog;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, DialogCaller {

    Fragment lastFragment;
    MaintenanceFragment fragmentMaintenance;
    DayFragment dayFragment;
    LogoFragment logoFragment;
    LicenseController licenseController;
    UsersController usersController;
    DevicesController devicesController;
    UsersDevicesController usersDevicesController;
    UserControlController userControlController;
    ProductsControlController productsControlController;
    RelativeLayout rlNotifications;
    CardView cvNotificacions;
    TextView tvNotificationsNumber, tvTotalOrders;
    ImageView imgMenu;

    DrawerLayout drawer;
    NavigationView nav;

    boolean exit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentMaintenance = new MaintenanceFragment();
        fragmentMaintenance.setParentActivity(MainActivity.this);

        logoFragment = new LogoFragment();
        logoFragment.setParentActivity(this);

        dayFragment = new DayFragment();
        dayFragment.setParentActivity(MainActivity.this);


        licenseController = LicenseController.getInstance(MainActivity.this);
        devicesController = DevicesController.getInstance(MainActivity.this);
        productsControlController = ProductsControlController.getInstance(MainActivity.this);
        usersController = UsersController.getInstance(MainActivity.this);
        usersDevicesController = UsersDevicesController.getInstance(MainActivity.this);
        userControlController = UserControlController.getInstance(MainActivity.this);

        rlNotifications = findViewById(R.id.rlNotifications);
        cvNotificacions = findViewById(R.id.cvNotifications);
        tvNotificationsNumber = findViewById(R.id.tvNotificationNumber);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav = (NavigationView)findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        imgMenu = findViewById(R.id.imgMenu);

        imgMenu.setVisibility(View.VISIBLE);
        findViewById(R.id.imgSearch).setVisibility(View.GONE);



        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (nav.isShown()) {
                        drawer.closeDrawer(nav);
                    } else {
                        drawer.openDrawer(nav);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.rlBirthDays).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBirthDialog();
            }
        });

        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callNotificationsDialog();
            }
        });

        Users u = usersController.getUserByCode(Funciones.getCodeuserLogged(MainActivity.this));
        TextView tvUser = (nav.getHeaderView(0).findViewById(R.id.tvUserName));
        tvUser.setText(u != null ? u.getUSERNAME() : "UNKNOWN");
        setInitialFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();
        licenseController.getReferenceFireStore().addSnapshotListener(MainActivity.this, licenceListener);
        usersController.getReferenceFireStore().addSnapshotListener(MainActivity.this,usersListener);
        devicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(MainActivity.this,deviceListener);
        usersDevicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(MainActivity.this,userDevicesListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        searchBirthDays();
        //licenseController.setLastUpdateToFireBase();//Actualiza la licencia
        setupUserControls();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.goDay || (( id == R.id.goMenu || id == R.id.goReceip) && DayController.getInstance(MainActivity.this).getCurrentOpenDay() == null)) {
            showDayFragment();

        }else{

            if (id == R.id.goMenu) {
                goToOrders();
            } else if (id == R.id.goReports) {
            goToReports();

        } else if(id == R.id.goReceip){
                goToReceipts();
            } else if(id == R.id.logout){
                logout();
            }else  {
                    fragmentMaintenance.setLastModule(id);
                    if(lastFragment instanceof  MaintenanceFragment){
                     fragmentMaintenance.loadModule(id);
                    }else{
                        showMaintenanceFragment();
                    }
            }

        }


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public EventListener<QuerySnapshot> licenceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if(e != null){
                Toast.makeText(MainActivity.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Licenses lic = null;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                licenseController.delete(null, null);
                for(DocumentSnapshot ds: querySnapshot){
                    lic = ds.toObject(Licenses.class);
                    if(lic.getCODE().equals(Funciones.getCodeLicense(MainActivity.this))){
                        licenseController.insert(lic);
                    }
                }
            }
            validateLicence(lic);
        }
    };

    public EventListener<QuerySnapshot> usersListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
            if(e != null){
                Toast.makeText(MainActivity.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            Users u = null;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                usersController.delete(null, null);

                for(DocumentSnapshot doc: querySnapshot){
                    u = doc.toObject(Users.class);
                    if(u.getCODE().equals(Funciones.getCodeuserLogged(MainActivity.this))){
                        usersController.insert(u);
                        break;
                    }u = null;

                }
            }
            validateUser(u);
        }
    };

    public EventListener<QuerySnapshot> deviceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if(e != null){
                Toast.makeText(MainActivity.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }
            Devices devices =null;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                devicesController.delete(null, null);

                for(DocumentSnapshot doc: querySnapshot){
                    devices = doc.toObject(Devices.class);
                    if(devices.getCODE().equals(Funciones.getPhoneID(MainActivity.this))) {
                        devicesController.insert(devices);
                        break;
                    }devices = null;
                }
            }
            validateDevices(devices);
        }
    };

    public EventListener<QuerySnapshot> userDevicesListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if(e != null){
                Toast.makeText(MainActivity.this, e.getMessage()+" - "+e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return;
            }

            boolean valid = false;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                for(DocumentSnapshot doc: querySnapshot){
                    UsersDevices ud = doc.toObject(UsersDevices.class);
                    if(ud.getCODEDEVICE().equals(Funciones.getPhoneID(MainActivity.this))
                            && ud.getCODEUSER().equals(Funciones.getCodeuserLogged(MainActivity.this))){
                        valid = true;
                        break;
                    }ud = null;
                }
            }
            if(!valid){
                exitWithNoLoginCode(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER);
            }
        }
    };


    public boolean validateUser(Users u){

        int code = usersController.validateUser(u);

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public boolean validateDevices(Devices d){

        int code = DevicesController.getInstance(MainActivity.this).validateDevice(d);

        if(code == CODES.CODE_DEVICES_UNREGISTERED || code == CODES.CODE_DEVICES_DISABLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public void startActivityLoginFromBegining(int code){
        Funciones.savePreferences(MainActivity.this, CODES.EXTRA_SECURITY_ERROR_CODE, code);
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void setupUserControls(){
        UserControlController uc = UserControlController.getInstance(MainActivity.this);
        MenuItem mantenimientoEmpresas = nav.getMenu().findItem(R.id.goMantCompany);
        MenuItem mantenimientoInventario = nav.getMenu().findItem(R.id.goMantInventario);
        MenuItem mantenimientoProductos = nav.getMenu().findItem(R.id.goMantProductos);
        MenuItem mantenimientoUsuarios = nav.getMenu().findItem(R.id.goMantUsuarios);
        MenuItem mantenimientoClientes = nav.getMenu().findItem(R.id.goMantClientes);
        MenuItem mantenimientoControles = nav.getMenu().findItem(R.id.goMantControls);
        MenuItem crearOrdenes = nav.getMenu().findItem(R.id.goMenu);
        MenuItem facturar = nav.getMenu().findItem(R.id.goReceip);
        MenuItem reportes = nav.getMenu().findItem(R.id.goReports);

        mantenimientoInventario.setVisible(false);
        mantenimientoProductos.setVisible(uc.searchSimpleControl(CODES.USERSCONTROL_PRODUCTS)!=null?true:false);
        mantenimientoUsuarios.setVisible(false);
        mantenimientoClientes.setVisible(uc.searchSimpleControl(CODES.USERSCONTROL_CLIENTS)!=null?true:false);
        mantenimientoControles.setVisible(false);
        crearOrdenes.setVisible(uc.searchSimpleControl(CODES.USERSCONTROL_SALES)!=null?true:false);
        facturar.setVisible(uc.searchSimpleControl(CODES.USERSCONTROL_RECEIPTS)!=null?true:false);
        mantenimientoEmpresas.setVisible(uc.searchSimpleControl(CODES.USERSCONTROL_COMPANY)!=null?true:false);
        reportes.setVisible(false);

        if(usersController.isSuperUser()){//SU o Administrador
            mantenimientoInventario.setVisible(true);
            mantenimientoProductos.setVisible(true);
            mantenimientoUsuarios.setVisible(true);
            mantenimientoClientes.setVisible(true);
            mantenimientoControles.setVisible(true);
            crearOrdenes.setVisible(true);
            facturar.setVisible(true);
            mantenimientoEmpresas.setVisible(true);
            reportes.setVisible(true);
        }


    }


    public void setInitialFragment(){
        if(usersController.isSuperUser() || usersController.isAdmin()) {//SU o Administrador
            showDayFragment();
        }else {
           showLogoFragment();
        }
    }

    public void showMaintenanceFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, fragmentMaintenance);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        lastFragment = fragmentMaintenance;
    }

    public void showLogoFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, logoFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        lastFragment = logoFragment;
    }

    public void showDayFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.details, dayFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        lastFragment = dayFragment;
    }




    public boolean validateLicence(Licenses lic){

        int code = licenseController.validateLicense(lic);
        switch (code){
            //Validando vigencia de la licencia.
            case CODES.CODE_LICENSE_EXPIRED:
            case CODES.CODE_LICENSE_DISABLED:
            case CODES.CODE_LICENSE_NO_LICENSE:
                licenceListener = null;
                exitWithNoLoginCode(code);
                return false;

        }

        return true;
    }

    public void exitWithNoLoginCode(int code){
        if(!exit){
            exit = true;
            Toast.makeText(MainActivity.this, Funciones.gerErrorMessage(code), Toast.LENGTH_LONG).show();
            Funciones.savePreferences(MainActivity.this, CODES.PREFERENCE_LOGIN_BLOQUED, "1");
            Funciones.savePreferences(MainActivity.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, code+"");
            startActivityLoginFromBegining(code);
        }

    }

    public void goToOrders(){

        startActivity(new Intent(MainActivity.this, MainOrders.class));
    }

    public void  goToReceipts(){
        startActivity(new Intent(MainActivity.this, MainReceipt.class));
    }

    public void goToReports(){
        startActivity(new Intent(MainActivity.this, MainReports.class));
    }
    public void logout(){
        final Dialog d =Funciones.getCustomDialog2Btn(MainActivity.this, getResources().getColor(R.color.colorPrimary), "Logout", "Esta seguro que desea cerrar la sesion?", R.drawable.ic_power, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Funciones.savePreferences(MainActivity.this, CODES.PREFERENCE_USERSKEY_CODE, "");
                Funciones.savePreferences(MainActivity.this, CODES.PREFERENCE_USERSKEY_USERTYPE, "");
                finish();
            }
        }, null);
        d.findViewById(R.id.btnNegative).setOnClickListener(new View.OnClickListener() {
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


    public void searchBirthDays(){
        if(ClientsController.getInstance(MainActivity.this).getBirthDayClients(Calendar.getInstance()).size() > 0){
            findViewById(R.id.rlBirthDays).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvBdNumber)).setText(ClientsController.getInstance(MainActivity.this).getBirthDayClients(Calendar.getInstance()).size()+"");

        }else{
            findViewById(R.id.rlBirthDays).setVisibility(View.GONE);
        }
    }

    public void callBirthDialog(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogBD");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  BirthDayDialog.newInstance(this, this);
        // Create and show the dialog.
        newFragment.show(ft, "dialogBD");
    }

    @Override
    public void dialogClosed(Object o) {

    }
}
