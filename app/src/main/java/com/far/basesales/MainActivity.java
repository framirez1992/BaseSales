package com.far.basesales;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.ProductsControl;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.ProductsControlController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Controllers.UsersDevicesController;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MaintenanceFragment fragmentMaintenance;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        rlNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callNotificationsDialog();
            }
        });

        setUpForUserType();
        setInitialFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();

        productsControlController.getReferenceFireStore().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    productsControlController.delete(null, null);
                    for (DocumentSnapshot doc : querySnapshot) {
                        ProductsControl pc = doc.toObject(ProductsControl.class);
                        productsControlController.insert(pc);
                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });

        licenseController.getReferenceFireStore().addSnapshotListener(licenceListener);
        usersController.getReferenceFireStore().addSnapshotListener(usersListener);
        devicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(deviceListener);
        usersDevicesController.getReferenceFireStore(licenseController.getLicense()).addSnapshotListener(userDevicesListener);
        userControlController.getReferenceFireStore().addSnapshotListener(userControlListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        licenseController.setLastUpdateToFireBase();//Actualiza la licencia
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

        if (id == R.id.goMenu) {
            goToOrders();
        } /*else if (id == R.id.goPendingOrders) {
            goToOrdersBoard();
        } else if (id == R.id.goReports) {
            goToReports();

        }*/ else if(id == R.id.goReceip){
            goToReceipts();
        }/*else if(id == R.id.goSavedReceipts){
            goToSavedReceipts();
        }else if(id == R.id.goConfiguration){
            //startActivity(new Intent(this, BluetoothScan.class));
        }*/ else  {
            changeModule(id);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public EventListener<QuerySnapshot> licenceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                Licenses lic = querySnapshot.getDocuments().get(0).toObject(Licenses.class);
                licenseController.delete(null, null);
                licenseController.insert(lic);
            }
            validateLicence(licenseController.getLicense());
        }
    };

    public EventListener<QuerySnapshot> usersListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                usersController.delete(null, null);
                for(DocumentSnapshot doc: querySnapshot){
                    Users u = doc.toObject(Users.class);
                    usersController.insert(u);
                }
            }
            validateUser();
        }
    };

    public EventListener<QuerySnapshot> deviceListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                devicesController.delete(null, null);
                for(DocumentSnapshot doc: querySnapshot){
                    Devices d = doc.toObject(Devices.class);
                    if(d.getCODE().equals(Funciones.getPhoneID(MainActivity.this))) {
                        devicesController.insert(d);
                    }
                }
            }
            validateDevices();
        }
    };

    public EventListener<QuerySnapshot> userDevicesListener =  new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {

            boolean valid = false;
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                for(DocumentSnapshot doc: querySnapshot){
                    UsersDevices ud = doc.toObject(UsersDevices.class);
                    if(ud.getCODEDEVICE().equals(Funciones.getPhoneID(MainActivity.this))
                            && ud.getCODEUSER().equals(Funciones.getCodeuserLogged(MainActivity.this))){
                        valid = true;
                        break;
                    }
                }

                if(!valid){
                    startActivityLoginFromBegining(CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER);
                }
            }
        }
    };

    public EventListener<QuerySnapshot> userControlListener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
            if (querySnapshot != null && querySnapshot.getDocuments()!= null && querySnapshot.getDocuments().size() > 0) {
                userControlController.delete(null, null);
                for(DocumentSnapshot doc: querySnapshot){
                    UserControl uc = doc.toObject(UserControl.class);
                    userControlController.insert(uc);
                }
            }
            setUpForUserType();
        }
    };

    public boolean validateUser(){

        int code = usersController.validateUser(usersController.getUserByCode(Funciones.getCodeuserLogged(MainActivity.this)));

        if(code == CODES.CODE_USERS_INVALID || code == CODES.CODE_USERS_DISBLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public boolean validateDevices(){

        int code = DevicesController.getInstance(MainActivity.this).validateDevice();

        if(code == CODES.CODE_DEVICES_UNREGISTERED || code == CODES.CODE_DEVICES_DISABLED) {
            exitWithNoLoginCode(code);
            return false;
        }

        return true;
    }

    public void startActivityLoginFromBegining(int code){
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.putExtra(CODES.EXTRA_SECURITY_ERROR_CODE, code);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void setUpForUserType(){
        MenuItem mantenimientoInventario = nav.getMenu().findItem(R.id.goMantInventario);
        MenuItem mantenimientoProductos = nav.getMenu().findItem(R.id.goMantProductos);
        MenuItem mantenimientoUsuarios = nav.getMenu().findItem(R.id.goMantUsuarios);
        MenuItem mantenimientoClientes = nav.getMenu().findItem(R.id.goMantClientes);
        MenuItem mantenimientoControles = nav.getMenu().findItem(R.id.goMantControls);
        MenuItem crearOrdenes = nav.getMenu().findItem(R.id.goMenu);
        MenuItem facturar = nav.getMenu().findItem(R.id.goReceip);
        MenuItem reportes = nav.getMenu().findItem(R.id.goReports);

        mantenimientoInventario.setVisible(false);
        mantenimientoProductos.setVisible(false);
        mantenimientoUsuarios.setVisible(false);
        mantenimientoClientes.setVisible(false);
        mantenimientoControles.setVisible(false);
        crearOrdenes.setVisible(false);
        facturar.setVisible(false);
        reportes.setVisible(false);

        if(usersController.isSuperUser() || usersController.isAdmin()){//SU o Administrador
            //mantenimientoInventario.setVisible(usersController.isSuperUser());
            mantenimientoProductos.setVisible(usersController.isSuperUser());
            mantenimientoUsuarios.setVisible(usersController.isSuperUser());
            mantenimientoClientes.setVisible(usersController.isSuperUser());
            mantenimientoControles.setVisible(usersController.isSuperUser());

            crearOrdenes.setVisible(usersController.isSuperUser());
            facturar.setVisible(usersController.isSuperUser());
            reportes.setVisible(true);
        }else {

            if(UserControlController.getInstance(MainActivity.this).createOrders()){
                nav.getMenu().findItem(R.id.goMenu).setVisible(true);
            }
            if(UserControlController.getInstance(MainActivity.this).chargeOrders()){
                nav.getMenu().findItem(R.id.goReceip).setVisible(true);
            }


        }

    }

    public void setInitialFragment(){
        if(usersController.isSuperUser() || usersController.isAdmin()) {//SU o Administrador
            fragmentMaintenance = new MaintenanceFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, fragmentMaintenance);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }else {
            logoFragment = new LogoFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.details, logoFragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }
    }


    public void changeModule(int id){

        if((usersController.isSuperUser() || usersController.isAdmin())) {
            fragmentMaintenance.llMaintenanceInventory.setVisibility((id == R.id.goMantInventario) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceProducts.setVisibility((id == R.id.goMantProductos) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceUsers.setVisibility((id == R.id.goMantUsuarios) ? View.VISIBLE : View.GONE);
            //fragmentMaintenance.llMaintenanceAreas.setVisibility((id == R.id.goMantAreas) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMaintenanceControls.setVisibility((id == R.id.goMantControls) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llMainScreen.setVisibility((id == R.id.goMainScreen) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llClients.setVisibility((id == R.id.goMantClientes) ? View.VISIBLE : View.GONE);
            fragmentMaintenance.llConfiguration.setVisibility((id == R.id.goConfiguration) ? View.VISIBLE : View.GONE);

        }


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
        Toast.makeText(MainActivity.this, Funciones.gerErrorMessage(code), Toast.LENGTH_LONG).show();
        Funciones.savePreferences(MainActivity.this, CODES.PREFERENCE_LOGIN_BLOQUED, "1");
        Funciones.savePreferences(MainActivity.this, CODES.PREFERENCE_LOGIN_BLOQUED_REASON, code+"");
        startActivityLoginFromBegining(code);
    }

    public void goToOrders(){

        startActivity(new Intent(MainActivity.this, MainOrders.class));
    }

    public void  goToReceipts(){
        startActivity(new Intent(MainActivity.this, MainReceipt.class));
    }

}
