package com.far.basesales.Globales;

public class Tablas {

    ////////////////////////////////////////////////////////////////////////
    /////////////           CLOUD FIRESTORE         ///////////////////////
    //clientes de la app
    public static final String generalUsers = "GENERAL_USERS";
    public static final String generalLicencias = "GENERAL_LICENSES";
    public static final String generalRoles = "GENERAL_ROLES";

    //GENERAL_USERS childs
    //public static final String generalUsersAreas = "Areas";
    //public static final String generalUsersAreasDetail = "AreasDetail";
    //public static final String generalUsersCombos = "Combos";
    public static final String generalUsersCompany = "Company";
    public static final String generalUsersClients = "Clients";
    public static final String generalUsersDay = "Day";
    public static final String generalUsersMeasureUnits = "MeasureUnits";
    public static final String generalUsersMeasureUnitsInv = "MeasureUnitsInv";
    public static final String generalUsersPayments = "Payments";
    public static final String generalUsersPriceList = "PriceList";
    public static final String generalUsersProducts = "Products";
    public static final String generalUsersProductsInv = "ProductsInv";
    public static final String generalUsersProductsControl = "ProductsControl";
    public static final String generalUsersProductsMeasure = "ProductsMeasure";
    public static final String generalUsersProductsMeasureInv = "ProductsMeasureInv";
    public static final String generalUsersProductsTypes = "ProductsTypes";
    public static final String generalUsersProductsTypesInv = "ProductsTypesInv";
    public static final String generalUsersProductsSubTypes = "ProductsSubTypes";
    public static final String generalUsersProductsSubTypesInv = "ProductsSubTypesInv";
    public static final String generalUsersReceipts = "Receipts";
    public static final String generalUsersReceiptsHistory = "ReceiptsHistory";
    public static final String generalUsersSales = "Sales";
    public static final String generalUsersSalesDetails = "SalesDetails";
    public static final String generalUsersSalesHistory = "SalesHistory";
    public static final String generalUsersSalesDetailsHistory = "SalesDetailsHistory";
    public static final String generalUsersStoreHouse = "StoreHouse";
    public static final String generalUsersStoreHouseDetail = "StoreHouseDetail";
    //public static final String generalUsersTableCode = "TableCode";
    //public static final String generalUsersTableFilter = "TableFilter";
    public static final String generalUsersToken = "Token";
    public static final String generalUsersUserControl = "UserControl";
    public static final String generalUsersUsers = "Users";
    public static final String generalUsersUsersDevices = "UsersDevices";
    public static final String generalUsersUserTypes = "UserTypes";
    public static final String generalUsersUserInbox = "UserInbox";
    //GENERAL_LICENSES childs
    public static final String generalLicenciasDevices = "Devices";

    public static String[]tablesFireBase = new String[]{/*generalUsersAreas, generalUsersAreasDetail, generalUsersCombos,*/generalUsersCompany,
            generalLicenciasDevices, generalUsersMeasureUnits,generalUsersMeasureUnitsInv, generalUsersPriceList, generalUsersProducts,generalUsersProductsInv,generalUsersProductsControl,generalUsersProductsMeasure,
            generalUsersProductsMeasureInv,generalUsersProductsTypes,generalUsersProductsTypesInv, generalUsersProductsSubTypes,generalUsersProductsSubTypesInv ,generalUsersSales, generalUsersSalesDetails, generalUsersSalesHistory,
            generalUsersSalesDetailsHistory, /*generalUsersTableCode,generalUsersTableFilter,*/ generalUsersUserControl, generalUsersUsers,
            generalUsersUserTypes, generalUsersUserInbox};



   ///////////////////////////////////////////////////////////////////////
    ///////////////////        SQLITE         ///////////////////////////
   public static final String DB_NAME = "EATIT.db";
   public static final String tempOrders = "TempOrders";
    public static final String tempOrdersDetails = "TempOrdersDetails";



}
