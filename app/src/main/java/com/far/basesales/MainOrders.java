package com.far.basesales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothlibrary.BluetoothScan;
import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.Adapters.Models.OrderDetailModel;
import com.far.basesales.Adapters.Models.SimpleRowModel;
import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Payment;
import com.far.basesales.CloudFireStoreObjects.Products;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Controllers.Transaction;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Dialogs.ClientsDialogFragment;
import com.far.basesales.Dialogs.ReceiptOptionsDialog;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MainOrders extends AppCompatActivity implements ListableActivity, NavigationView.OnNavigationItemSelectedListener, OnFailureListener,  OnSuccessListener<QuerySnapshot> {

    SalesController salesController;
    ProductsMeasureController productsMeasureController;

    CollectionReference productsMeasure;

    NewOrderFragment newOrderFragment;
    ResumenOrderFragment resumenOrderFragment;
    ReceiptFragment receiptFragment;
    //ReceipFragment receipFragment;
    //ReceiptResumeFragment receiptResumeFragment;
    Fragment lastFragment;

    TempOrdersController tempOrdersController;
    String orderCode = null;
    //RelativeLayout rlNotifications;

    Receipts lastReceipt;
    Payment lastPayment;
    Sales lastSale;
    ArrayList<SalesDetails> lastSalesDetails;
    Day lastDay;



    /*DrawerLayout drawer;
    NavigationView nav;*/

   /* NotificationsDialog notificationsDialog;
    WorkedOrdersDialog workedOrdersDialog;*/
    OrderDetailModel objectToEditFromResume = null;
    Dialog dialogConfirmPayment;
    Dialog dialogLoading;
    Dialog errorDialog;
    Dialog postPayment;

    public static final String KEY_ORDERCODE = "KEYORDERCODE";
    boolean editingOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_orders);
        tempOrdersController = TempOrdersController.getInstance(MainOrders.this);
        salesController = SalesController.getInstance(MainOrders.this);
        productsMeasureController = ProductsMeasureController.getInstance(MainOrders.this);

        productsMeasure = productsMeasureController.getReferenceFireStore();




        newOrderFragment = new NewOrderFragment();
        newOrderFragment.setParent(this);
        resumenOrderFragment = new ResumenOrderFragment();
        resumenOrderFragment.setParent(this);
        receiptFragment = new ReceiptFragment();
        receiptFragment.setParent(this);
       /* receipFragment = new ReceipFragment();
        receipFragment.setMainActivityReference(this);
        receiptResumeFragment = new ReceiptResumeFragment();
        receiptResumeFragment.setMainActivityReference(this);*/


       /* drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nav = (NavigationView)findViewById(R.id.nav_view);

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


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainOrders.this);

        setupUserControls();*/

        // SI SE ENTRA POR PRIMERA VEZ, SI SE GIRA LA PANTALLA NO CORRER OTRA VEZ.
        if(savedInstanceState == null){
            prepareNewOrder();
        }
        goToNewOrder();

    }

    @Override
    protected void onStart() {
        super.onStart();
       /* productsMeasure.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if(querySnapshot == null ){
                    return;
                }
                productsMeasureController.delete(null, null);
                for(DocumentSnapshot dc: querySnapshot){
                    ProductsMeasure pm = dc.toObject(ProductsMeasure.class);
                    productsMeasureController.insert(pm);
                }

                updateTempSalesDetail();
            }
        });*/
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
            case R.id.actionEdit:
                callAddDialog(objectToEditFromResume);
                return true;
            case R.id.actionDelete:
                deleteOrderLine(objectToEditFromResume);
                return  true;

            default:return super.onContextItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(!isEditingOrder()) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.goMenu) {
                goToMenu();
            }/* else if (id == R.id.openOrders) {
               // callWorkedOrdersDialog();
            } else if (id == R.id.goReceip) {
                //showReceiptFragment();
            }*/
        }
        /*drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState .putString(KEY_ORDERCODE, orderCode);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        orderCode = savedInstanceState.getString(KEY_ORDERCODE);
        resumenOrderFragment.refreshList();
        resumenOrderFragment.refreshTotal();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CODES.REQUEST_BLUETOOTH_ACTIVITY && resultCode == Activity.RESULT_OK){
            String macAdress = data.getExtras().getString(BluetoothScan.EXTRA_MAC_ADDRESS);
            Funciones.savePreferences(MainOrders.this, CODES.PREFERENCE_BLUETOOTH_MAC_ADDRESS, macAdress);
        }
    }

    public void goToNewOrder(){
        changeFragment(newOrderFragment, R.id.details);
        changeFragment(resumenOrderFragment, R.id.result);
    }


    public void goToMenu(){
        changeFragment(newOrderFragment, R.id.details);
    }

    public void changeFragment(Fragment f, int id){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(id, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        ft.commit();

        if(id == R.id.details) {
            lastFragment = f;
        }
    }

    @Override
    public void onClick(Object obj) {
        if(obj instanceof SimpleRowModel) {
            Products p = ProductsController.getInstance(MainOrders.this).getProductByCode(((SimpleRowModel) obj).getId());
            callAddDialog(p);
        }/*else if(obj instanceof NotificationRowModel){
            UserInboxController.getInstance(MainOrders.this).setMessageReaded(((NotificationRowModel) obj).getCode());
            notificationsDialog.showDetail((NotificationRowModel)obj);
        }*/else if(obj instanceof OrderDetailModel){
            objectToEditFromResume = (OrderDetailModel) obj;
        }/*else if(obj instanceof WorkedOrdersRowModel){
            workedOrdersDialog.showDetail((WorkedOrdersRowModel) obj);
        }else if(obj instanceof OrderReceiptModel){
            OrderReceiptModel orderReceiptModel = (OrderReceiptModel)obj;
            receiptResumeFragment.setCodeAreaDetail(orderReceiptModel.getMesaID());
            showReceiptResumeFragment();
        }*/
    }


    public void callAddDialog(Object object){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;

        //newFragment = AddProductDialog.newInstance(object, null);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void prepareNewOrder(){
        setEditing(false);
        tempOrdersController.delete(null, null);
        tempOrdersController.delete_Detail(null, null);
        orderCode  = Funciones.generateCode();
        String codeUser = Funciones.getCodeuserLogged(MainOrders.this);
//String code,String codeuser, double totalDiscount,double totalTaxes,  double total, int status,  String codeReceipt
        Sales s = new Sales(orderCode,codeUser,0.0,0.0, 0.0, CODES.CODE_ORDER_STATUS_OPEN, null, null);
        tempOrdersController.insert(s);//
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void refreshResume(){
        resumenOrderFragment.refreshList();
        resumenOrderFragment.refreshTotal();
    }

    public void prepareResumeForEdition(){
        //setThemeEditing();
        resumenOrderFragment.prepareResumeForEdition();
    }
    public void refreshProductsSearch(int goToPosition){
        newOrderFragment.search();
        newOrderFragment.setSelection(goToPosition);
    }

    public void setResumeSelection(int pos){
        resumenOrderFragment.setSelection(pos);
    }


    public void editOrder(Sales s){
        orderCode = s.getCODE();
        tempOrdersController.delete(null, null);
        tempOrdersController.delete_Detail(null, null);

        tempOrdersController.insert(s);

        for(SalesDetails sd: salesController.getSalesDetailsByCodeSales(orderCode)){
            tempOrdersController.insert_Detail(sd);
        }

        refreshResume();
        prepareResumeForEdition();


        FrameLayout menuFrameLayout = findViewById(R.id.details);
        FrameLayout resumenFrameLayout = findViewById(R.id.result);

        //Si la visivilidad esta en modo "telefono" mostrar el layout del resumen si actualmente se esta visualizando el "menu"
        if(menuFrameLayout.getVisibility()== View.VISIBLE && resumenFrameLayout.getVisibility() == View.GONE){
            menuFrameLayout.setVisibility(View.GONE);
            resumenFrameLayout.setVisibility(View.VISIBLE);
        }

        setEditing(true);
    }

    public void deleteOrderLine(OrderDetailModel d){
        String where = TempOrdersController.DETAIL_CODE +" = ? AND "+TempOrdersController.DETAIL_CODESALES+" = ?";
        tempOrdersController.delete_Detail(where, new String[]{d.getCode(), d.getCode_sales()});
        refreshResume();
        objectToEditFromResume = null;
    }

    public void showReceipt(){
        changeFragment(receiptFragment, R.id.result);
        ((ViewGroup)findViewById(R.id.details)).setVisibility(View.GONE);
        ((ViewGroup)findViewById(R.id.result)).setVisibility(View.VISIBLE);
    }

    public void showDetail(){
        changeFragment(resumenOrderFragment, R.id.result);
        ((ViewGroup)findViewById(R.id.details)).setVisibility(View.GONE);
        ((ViewGroup)findViewById(R.id.result)).setVisibility(View.VISIBLE);

    }

    public void showMenu(){
        changeFragment(newOrderFragment, R.id.details);
        ((ViewGroup)findViewById(R.id.details)).setVisibility(View.VISIBLE);
        ((ViewGroup)findViewById(R.id.result)).setVisibility(View.GONE);
    }


    public void refresh(){
        prepareNewOrder();
        resumenOrderFragment.refreshList();
        resumenOrderFragment.refreshTotal();
        resumenOrderFragment.llCancel.setVisibility(View.GONE);
        resumenOrderFragment.etNotas.setText("");
        resumenOrderFragment.llMore.setVisibility(View.GONE);
        resumenOrderFragment.imgMore.setImageResource(R.drawable.ic_arrow_drop_down);

        receiptFragment.refresh();
        //resumenOrderFragment.setUpSpinnersAreas();

        newOrderFragment.setUpSpinners();
    }


    public void updateTempSalesDetail(){
        TempOrdersController.getInstance(MainOrders.this).updatePrices();
    }




    public void setupUserControls(){
        MenuItem printOrders = ((NavigationView)findViewById(R.id.nav_view)).getMenu().findItem(R.id.goReceip);
        printOrders.setVisible(false);
        if(UserControlController.getInstance(MainOrders.this).printOrders()){
            printOrders.setVisible(true);
        }

    }




    public void setEditing(boolean b){
        this.editingOrder = b;
    }
    public boolean isEditingOrder(){
        return this.editingOrder;
    }

    public void showPaymentConfirmation(){
        dialogConfirmPayment = null;
        dialogConfirmPayment = Funciones.getCustomDialog2Btn(MainOrders.this,getResources().getColor(R.color.colorPrimary), "Confirmacion", "Esta seguro que desea concretar el pago?", R.drawable.money, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showLoadingDialog();
             closePaymentConfirmation();
             receiptFragment.createReceipt();

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             closePaymentConfirmation();
            }
        });
        dialogConfirmPayment.show();
        Window window = dialogConfirmPayment.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void showPostPaymentConfirmation(){
        if(lastReceipt == null){
            Toast.makeText(MainOrders.this, "unable to get Receipt", Toast.LENGTH_LONG).show();
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment =  ReceiptOptionsDialog.newInstance(MainOrders.this, lastReceipt);
        newFragment.setCancelable(false);
        // Create and show the dialog.
         newFragment.show(ft, "dialog");
    }

    public void closePaymentConfirmation(){
        if(dialogConfirmPayment!= null){
            dialogConfirmPayment.dismiss();
            dialogConfirmPayment = null;
        }
    }


    public void showLoadingDialog(){
        dialogLoading = null;
        dialogLoading = Funciones.getLoadingDialog(MainOrders.this, "Guardando la transaccion");
        dialogLoading.setCancelable(false);
        dialogLoading.show();
    }

    public void closeLoadingDialog(){
        if(dialogLoading != null){
            dialogLoading.dismiss();
            dialogLoading = null;
        }

    }


    public void showErrorDialog(String msg){
        errorDialog = null;
        errorDialog = Funciones.getCustomDialog(MainOrders.this,getResources().getColor(R.color.red_700), "Error", msg, R.drawable.ic_error_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorDialog.dismiss();
                errorDialog = null;
            }
        });
        errorDialog.setCancelable(false);
        errorDialog.show();
    }

    public void closeOrders(Receipts receipt, Payment payment, Sales sales, ArrayList<SalesDetails> salesDetails){
        Day day = DayController.getInstance(MainOrders.this).getCurrentOpenDay();
        day.setSalescount(day.getSalescount()+1);
        day.setSalesamount(day.getSalesamount()+sales.getTOTAL());
        day.setDiscountamount(day.getDiscountamount()+receipt.getDiscount());
        if(payment.getTYPE().equals(CODES.PAYMENTTYPE_CASH)){
            day.setCashpaidamount(day.getCashpaidamount()+payment.getTOTAL());
            day.setCashpaidcount(day.getCashpaidcount()+1);
        }else if(payment.getTYPE().equals(CODES.PAYMENTTYPE_CREDIT)){
            day.setCreditpaidamount(day.getCreditpaidamount()+payment.getTOTAL());
            day.setCreditpaidcount(day.getCreditpaidcount()+1);
        }

        lastReceipt = receipt;
        lastPayment = payment;
        lastSale = sales;
        lastSalesDetails = salesDetails;
        lastDay = day;



        Transaction.getInstance(MainOrders.this).sendToFireBase(sales, salesDetails, receipt, payment, day, this);
        ReceiptController.getInstance(MainOrders.this).searchReceiptFromFireBase(receipt.getCode(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                Receipts r = null;
                if(querySnapshot!= null && querySnapshot.size() > 0){
                    r = querySnapshot.getDocuments().get(0).toObject(Receipts.class);
                }

                if(r != null){
                    SalesController.getInstance(MainOrders.this).insert(lastSale);
                    for(SalesDetails sd : lastSalesDetails){
                        SalesController.getInstance(MainOrders.this).insert_Detail(sd);
                    }
                    ReceiptController.getInstance(MainOrders.this).insert(lastReceipt);
                    PaymentController.getInstance(MainOrders.this).insert(lastPayment);
                    DayController.getInstance(MainOrders.this).update(lastDay);



                    closeLoadingDialog();
                    showPostPaymentConfirmation();
                }else{
                    closeLoadingDialog();
                    if(errorDialog!= null && !errorDialog.isShowing()){
                        showErrorDialog("El Recibo no pudo ser guardado. Intente nuevamente");
                    }
                }

                lastReceipt = null;
                lastPayment = null;
                lastSale = null;
                lastSalesDetails = null;
                lastDay = null;
            }
        }, this);


    }


    @Override
    public void onFailure(@NonNull Exception e) {
        lastReceipt = null;
        lastPayment = null;
        lastSale = null;
        lastSalesDetails = null;
        lastDay = null;

        closeLoadingDialog();
        if(errorDialog!= null && !errorDialog.isShowing()){
            showErrorDialog(e.getMessage()+"\n"+e.getLocalizedMessage());
        }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {

    }

    public void newOrderAndRefresh(){
        refresh();
        goToNewOrder();
        showMenu();
    }
}
