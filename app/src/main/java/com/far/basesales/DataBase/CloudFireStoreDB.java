package com.far.basesales.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.far.basesales.CloudFireStoreObjects.Devices;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Roles;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.CloudFireStoreObjects.UsersDevices;
import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Generic.KV2;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Interfases.FireBaseOK;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class CloudFireStoreDB {

    private static Context context;
    private static FirebaseFirestore fs;
    private static CloudFireStoreDB instance;

    Licenses license = null;

    //AreasController areasController;
    //AreasDetailController areasDetailController;
    DevicesController devicesController;
    UsersController usersController;
    //UserTypesController userTypesController;
    LicenseController licenseController;
    /*CombosController combosController;
    CompanyController companyController;
    MeasureUnitsController measureUnitsController;
    MeasureUnitsInvController measureUnitsInvController;
    PriceListController priceListController;
    ProductsController productsController;
    ProductsInvController productsInvController;
    ProductsControlController productsControlController;
    ProductsMeasureController productsMeasureController;
    ProductsMeasureInvController productsMeasureInvController;
    ProductsTypesController productsTypesController;
    ProductsTypesInvController productsTypesInvController;
    ProductsSubTypesController productsSubTypesController;
    ProductsSubTypesInvController productsSubTypesInvController;
    SalesController salesController;
    UserInboxController userInboxController;
    UserControlController userControlController;
    TableCodeController tableCodeController;
    TableFilterController tableFilterController;
    RolesController rolesController;
    StoreHouseController storeHouseController;
    StoreHouseDetailController storeHouseDetailController;*/

    SQLiteDatabase sqlWritable;
    OnFailureListener failureListener;
    FireBaseOK okListener;

    private CloudFireStoreDB(Context con, OnFailureListener fl, FireBaseOK ol){
        context = con;
        this.failureListener = fl;
        this.okListener = ol;
        fs = FirebaseFirestore.getInstance();

        //areasController = AreasController.getInstance(context);
        //areasDetailController = AreasDetailController.getInstance(context);
        devicesController =  DevicesController.getInstance(context);
        usersController =  UsersController.getInstance(context);
        //userTypesController = UserTypesController.getInstance(context);
        licenseController = LicenseController.getInstance(context);
        /*combosController = new CombosController(context);
        companyController =  CompanyController.getInstance(context);
        measureUnitsController =  MeasureUnitsController.getInstance(context);
        measureUnitsInvController = MeasureUnitsInvController.getInstance(context);
        priceListController = new PriceListController(context);
        productsController =  ProductsController.getInstance(context);
        productsInvController = ProductsInvController.getInstance(context);
        productsMeasureController = ProductsMeasureController.getInstance(context);
        productsMeasureInvController = ProductsMeasureInvController.getInstance(context);
        productsTypesController = ProductsTypesController.getInstance(context);
        productsTypesInvController = ProductsTypesInvController.getInstance(context);
        productsSubTypesController = ProductsSubTypesController.getInstance(context);
        productsSubTypesInvController = ProductsSubTypesInvController.getInstance(context);
        salesController =  SalesController.getInstance(context);
        userInboxController = UserInboxController.getInstance(context);
        userControlController = UserControlController.getInstance(context);
        tableCodeController = TableCodeController.getInstance(context);
        tableFilterController = TableFilterController.getInstance(context);
        rolesController = RolesController.getInstance(context);
        productsControlController = ProductsControlController.getInstance(context);
        storeHouseController = StoreHouseController.getInstance(context);
        storeHouseDetailController = StoreHouseDetailController.getInstance(context);*/

        sqlWritable = DB.getInstance(context).getWritableDatabase();
    }

    public static CloudFireStoreDB getInstance(Context con, OnFailureListener fl, FireBaseOK ol){
        if(instance == null){
            instance = new CloudFireStoreDB(con, fl, ol);
        }
        return instance;
    }
    public void crearNuevaEstructuraFireStore(){

        ////////////////////////////////////////////////////////////////////////
        ////////  JERARQUIA DE LICENCIAS         //////////////////////////////
        String licCode = Funciones.generateCode();
        Licenses licencia = new Licenses(licCode,licCode ,new Date(),Funciones.sumaDiasFecha(370), 0, 370, 5, true,true,new Date(),1);

        //creando documento con el key del nuevo cliente en la coleccion GENERAL_LICENSES
        CollectionReference GeneralLicensesCollection = fs.collection(Tablas.generalLicencias);
        DocumentReference Cliente = GeneralLicensesCollection.document(licencia.getCODE());
        //Creando y llenando el documento Cliente
        Cliente.set(licencia);


        //agregando el primer dispositivo
        devicesController.RegisterDevice(licencia);

        //////////////////////////////////////////////////////////////////////
        //////////// JERARQUIA DE ROLES     /////////////////////////////////
        CollectionReference GeneralRolesCollection = fs.collection(Tablas.generalRoles);

        Roles su = new Roles("0","SU");
        Roles admin = new Roles("1","Administrador");
        Roles usuario = new Roles("2", "Usuario");

        GeneralRolesCollection.document(su.getCODE()).set(su);
        GeneralRolesCollection.document(admin.getCODE()).set(admin);
        GeneralRolesCollection.document(usuario.getCODE()).set(usuario);

        /////////////////////////////////////////////////////////////////////
        //////////// JERARQUIA USUARIOS      ///////////////////////////////
        CollectionReference GeneralUsersCollection = fs.collection(Tablas.generalUsers);
        DocumentReference userLicense = GeneralUsersCollection.document(licencia.getCODE());
        userLicense.collection(Tablas.generalUsersUsers).add(new Users("Admin", "0", "admin1212345", "admin", "", "", true).toMap());
        UsersDevices ud = new UsersDevices();
        ud.setCODE(Funciones.generateCode());
        ud.setCODEDEVICE(Funciones.getPhoneID(context));
        ud.setCODEUSER("Admin");
        userLicense.collection(Tablas.generalUsersUsersDevices).add(ud);


    }

    public void CargaInicial(Licenses lic, boolean registerDevice){

        this.license = lic;
        //////       BEGIN TRANSACTION      ////////
       // sqlWritable.beginTransaction();
        ////////////////////////////////////////////

        try {
            if (registerDevice) {
                devicesController.RegisterDevice(license);
            }
            //////////////////////////////////////////////////
            //////////        LICENSES        ////////////////
            licenseController.delete("", null);
            licenseController.getDataFromFireBase(license.getCODE(), onSuccessListenerLicense,failureListener);


            //////       END TRANSACTION      ////////
            //sqlWritable.setTransactionSuccessful();
            ////////////////////////////////////////////

        }catch(Exception e){
            e.printStackTrace();
        }finally {
           // sqlWritable.endTransaction();
        }
    }

    public OnSuccessListener<DocumentSnapshot> onSuccessListenerLicense = new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
            if(documentSnapshot.exists()){
                Licenses license = documentSnapshot.toObject(Licenses.class);
                licenseController.insert(license);

                //okListener.sendMessage("CARGANDO AREAS ");
                //areasController.getDataFromFireBase(license.getCODE(),onSuccessListenerArea,failureListener);
                okListener.sendMessage("CARGANDO USERS ");
                usersController.getDataFromFireBase(license.getCODE(), onSuccessListenerUsers, failureListener);

            }
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerUsers = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            usersController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                usersController.insert(doc.toObject(Users.class));
            }
            okListener.sendMessage("CARGANDO DEVICES ");
            devicesController.getDataFromFireBase(license.getCODE(), onSuccessListenerDevice, failureListener);
            //okListener.sendMessage("CARGANDO COMBOS ");
            //combosController.getDataFromFireBase(license.getCODE(), onSuccessListenerCombos, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerDevice = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            devicesController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                devicesController.insert(doc.toObject(Devices.class));
            }
            okListener.sendMessage("FINALIZADO CORRECTAMENTE ");
            okListener.OnFireBaseEndContact(1);
            //okListener.sendMessage("CARGANDO MEASURE UNITS ");
            //measureUnitsController.getDataFromFireBase(license.getCODE(), onSuccessListenerMeasureUnits, failureListener);
        }
    };

    /*public OnSuccessListener<QuerySnapshot> onSuccessListenerArea = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            areasController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                areasController.insert(doc.toObject(Areas.class));
            }
            okListener.sendMessage("CARGANDO AREAS DETAIL ");
            areasDetailController.getDataFromFireBase(license.getCODE(), onSuccessListenerAreasDetail, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerAreasDetail = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            areasDetailController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                areasDetailController.insert(doc.toObject(AreasDetail.class));
            }
            okListener.sendMessage("CARGANDO USERS ");
            usersController.getDataFromFireBase(license.getCODE(), onSuccessListenerUsers, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerCombos = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            combosController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                combosController.insert(doc.toObject(Combos.class));
            }
            okListener.sendMessage("CARGANDO COMPANY ");
            companyController.getDataFromFireBase(license.getCODE(), onSuccessListenerCompany, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerCompany = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            companyController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                companyController.insert(doc.toObject(Company.class));
            }
            okListener.sendMessage("CARGANDO DEVICES ");
            devicesController.getDataFromFireBase(license.getCODE(), onSuccessListenerDevice, failureListener);
        }
    };



    public OnSuccessListener<QuerySnapshot> onSuccessListenerMeasureUnits = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            measureUnitsController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                measureUnitsController.insert(doc.toObject(MeasureUnits.class));
            }
            okListener.sendMessage("CARGANDO MEASURE UNITS INV ");
            measureUnitsInvController.getDataFromFireBase(license.getCODE(), onSuccessListenerMeasureUnitsInv, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerMeasureUnitsInv = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            measureUnitsInvController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                measureUnitsInvController.insert(doc.toObject(MeasureUnits.class));
            }
            okListener.sendMessage("CARGANDO PRICE LIST ");
            priceListController.getDataFromFireBase(license.getCODE(), onSuccessListenerPriceList, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerPriceList = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            priceListController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                priceListController.insert(doc.toObject(PriceList.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS ");
            productsController.getDataFromFireBase(license.getCODE(), onSuccessListenerProducts, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerProducts = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsController.insert(doc.toObject(Products.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS INV ");
            productsInvController.getDataFromFireBase(license.getCODE(), onSuccessListenerProductsInv, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsInv = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsInvController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsInvController.insert(doc.toObject(Products.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS MEASURES ");
            productsMeasureController.getDataFromFireBase(license.getCODE(), onSuccessListenerProductsMeasures, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsMeasures = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsMeasureController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsMeasureController.insert(doc.toObject(ProductsMeasure.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS MEASURES INV ");
            productsMeasureInvController.getDataFromFireBase(license.getCODE(), onSuccessListenerProductsMeasuresInv, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsMeasuresInv = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsMeasureInvController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsMeasureInvController.insert(doc.toObject(ProductsMeasure.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS TYPES ");
            productsTypesController.getDataFromFireBase(license.getCODE(), onSuccessListenerProductsTypes, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsTypes = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsTypesController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsTypesController.insert(doc.toObject(ProductsTypes.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS TYPES INV ");
            productsTypesInvController.getDataFromFireBase(license.getCODE(),onSuccessListenerProductsTypesInv, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsTypesInv = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsTypesInvController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsTypesInvController.insert(doc.toObject(ProductsTypes.class));
            }
            okListener.sendMessage("CARGANDO ROLES ");
            rolesController.getDataFromFireBase(onSuccessListenerRoles, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerRoles = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            rolesController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                rolesController.insert(doc.toObject(Roles.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS SUB TYPES ");
            productsSubTypesController.getDataFromFireBase(license.getCODE(), onSuccessListenerProductsSubTypes, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsSubTypes = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsSubTypesController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsSubTypesController.insert(doc.toObject(ProductsSubTypes.class));
            }
            okListener.sendMessage("CARGANDO PRODUCTS SUB TYPES INV ");
            productsSubTypesInvController.getDataFromFireBase(license.getCODE(), onSuccessListenerProductsSubTypesInv, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerProductsSubTypesInv = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            productsSubTypesInvController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                productsSubTypesInvController.insert(doc.toObject(ProductsSubTypes.class));
            }
            okListener.sendMessage("CARGANDO SALES ");
            salesController.getDataFromFireBase(license.getCODE(), onSuccessListenerSales, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerSales = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            salesController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                salesController.insert(doc.toObject(Sales.class));
            }
            okListener.sendMessage("CARGANDO SALES DETAIL ");
            salesController.getDataDetailsFromFireBase(license.getCODE(), onSuccessListenerSalesDetails, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerSalesDetails = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            salesController.delete_Detail("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                salesController.insert_Detail(doc.toObject(SalesDetails.class));
            }
            okListener.sendMessage("CARGANDO STORE HOUSE ");
            storeHouseController.getDataFromFireBase(license.getCODE(), onSuccessListenerStoreHouse, failureListener);
        }
    };


    public OnSuccessListener<QuerySnapshot> onSuccessListenerStoreHouse = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            storeHouseController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                storeHouseController.insert(doc.toObject(StoreHouse.class));
            }
            okListener.sendMessage("CARGANDO STORE HOUSE DETAIL ");
            storeHouseDetailController.getDataFromFireBase(license.getCODE(),onSuccessListenerStoreHouseDetail, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerStoreHouseDetail = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            storeHouseDetailController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                storeHouseDetailController.insert(doc.toObject(StoreHouseDetail.class));
            }
            okListener.sendMessage("CARGANDO USER INBOX ");
            userInboxController.getDataFromFireBase(license.getCODE(),onSuccessListenerUserInbox, failureListener);
        }
    };



    public OnSuccessListener<QuerySnapshot> onSuccessListenerUserInbox = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            userInboxController.delete(null, null);
            for (DocumentSnapshot doc : querySnapshot) {
                userInboxController.insert(doc.toObject(UserInbox.class));
            }
            okListener.sendMessage("CARGANDO USER CONTROL ");
            userControlController.getDataFromFireBase(license.getCODE(),onSuccessListenerUserControl, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerUserControl = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            userControlController.delete(null, null);
            for (DocumentSnapshot doc : querySnapshot) {
                userControlController.insert(doc.toObject(UserControl.class));
            }
            okListener.sendMessage("CARGANDO USER TYPES ");
            userTypesController.getDataFromFireBase(license.getCODE(),onSuccessListenerUserTypes, failureListener);
        }
    };

    public OnSuccessListener<QuerySnapshot> onSuccessListenerUserTypes = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            userTypesController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                userTypesController.insert(doc.toObject(UserTypes.class));
            }
            okListener.sendMessage("CARGANDO TABLE CODE ");
            tableCodeController.getDataFromFireBase(license.getCODE(), onSuccessListenerTableCode, failureListener);
        }
    };
    public OnSuccessListener<QuerySnapshot> onSuccessListenerTableCode = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            tableCodeController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                tableCodeController.insert(doc.toObject(TableCode.class));
            }
            okListener.sendMessage("CARGANDO TABLE FILTER ");
            tableFilterController.getDataFromFireBase(license.getCODE(), onSuccessListenerTableFilter, failureListener);
        }
    };
    public OnSuccessListener<QuerySnapshot> onSuccessListenerTableFilter = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot querySnapshot) {
            tableFilterController.delete("", null);
            for (DocumentSnapshot doc : querySnapshot) {
                tableFilterController.insert(doc.toObject(TableFilter.class));
            }
            okListener.sendMessage("FINALIZADO CORRECTAMENTE ");
            okListener.OnFireBaseEndContact(1);
        }
    };*/


   /* public void ActualizarTabla(String key, KV tabla){
        switch (tabla.getKey()){
            case Tablas.generalUsersAreas: areasController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersAreasDetail: areasDetailController.getAllDataFromFireBase(key, failureListener); break;
            case Tablas.generalUsersCombos: combosController.getAllDataFromFireBase(key, failureListener);  break;
            case Tablas.generalUsersCompany: companyController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalLicenciasDevices: devicesController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersMeasureUnits: measureUnitsController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersMeasureUnitsInv: measureUnitsInvController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersPriceList: priceListController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProducts: productsController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsInv: productsInvController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsControl: productsControlController.getAllDataFromFireBase(key, failureListener);
            case Tablas.generalUsersProductsMeasure: productsMeasureController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsMeasureInv: productsMeasureInvController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsTypes: productsTypesController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsTypesInv: productsTypesInvController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsSubTypes: productsSubTypesController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersProductsSubTypesInv: productsSubTypesInvController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersSales: salesController.getAllDataFromFireBase(failureListener);break;
            case Tablas.generalUsersSalesDetails: salesController.getAllDataDetailFromFireBase(failureListener);break;
            //case Tablas.generalUsersSalesHistory: salesController.getAllDataHistoryFromFireBase(failureListener);break;
            //case Tablas.generalUsersSalesDetailsHistory: salesController.getAllDataDetailHistoryFromFireBase(failureListener);break;
            case Tablas.generalUsersTableCode: tableCodeController.getAllDataFromFireBase(key, failureListener); break;
            case Tablas.generalUsersTableFilter: tableFilterController.getAllDataFromFireBase(key, failureListener); break;
            case Tablas.generalUsersUsers: usersController.getAllDataFromFireBase(key, failureListener);
            case Tablas.generalUsersUserControl: userControlController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersUserTypes: userTypesController.getAllDataFromFireBase(key, failureListener);break;
            case Tablas.generalUsersUserInbox: userInboxController.getAllDataFromFireBase(key, failureListener);break;



        }
    }*/

    public ArrayList<DocumentReference> getDocumentsReferencesByTableName(KV2 data){
        switch (data.getCode()){
        //case AreasController.TABLE_NAME: break;
        //case AreasDetailController.TABLE_NAME: break;
        //case CombosController.TABLE_NAME: return combosController.getReferences(data.getDescription(), data.getDescription2());
        //case CompanyController.TABLE_NAME: break;
        case DevicesController.TABLE_NAME: break;
        case LicenseController.TABLE_NAME: break;
        //case MeasureUnitsController.TABLE_NAME: break;
        //case MeasureUnitsInvController.TABLE_NAME: break;
        //case PriceListController.TABLE_NAME: break;
        //case ProductsControlController.TABLE_NAME: break;
        //case ProductsController.TABLE_NAME: break;
        //case ProductsInvController.TABLE_NAME: break;
        //case ProductsMeasureController.TABLE_NAME: return productsMeasureController.getReferences(data.getDescription(), data.getDescription2());
        //case ProductsMeasureInvController.TABLE_NAME: return productsMeasureInvController.getReferences(data.getDescription(), data.getDescription2());
        //case ProductsSubTypesController.TABLE_NAME: break;
        //case ProductsSubTypesInvController.TABLE_NAME: break;
        //case ProductsTypesController.TABLE_NAME: break;
       // case ProductsTypesInvController.TABLE_NAME: break;
        //case RolesController.TABLE_NAME: break;
        //case SalesController.TABLE_NAME: break;
        //case SalesController.TABLE_NAME_DETAIL: break;
       // case StoreHouseController.TABLE_NAME: break;
        //case StoreHouseDetailController.TABLE_NAME: break;
        //case TableCodeController.TABLE_NAME: break;
       // case TableFilterController.TABLE_NAME: break;
       // case UserControlController.TABLE_NAME: break;
        //case UsersDevicesController.TABLE_NAME: break;
       // case UserTypesController.TABLE_NAME: break;
        }
        return null;
    }
}
