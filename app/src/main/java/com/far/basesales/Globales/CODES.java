package com.far.basesales.Globales;

public class CODES {
    //LICENCIAS
    public static final int CODE_LICENSE_INVALID = 1000;
    public static final int CODE_LICENSE_EXPIRED = 2000;
    public static final int CODE_LICENSE_DISABLED = 3000;
    public static final int CODE_LICENSE_DEVICES_LIMIT_REACHED = 4000;
    public static final int CODE_LICENSE_NO_LICENSE = 5000;
    public static final int CODE_LICENSE_VALID = 6000;

    //DEVICES
    public static final int CODE_DEVICES_ENABLED = 1100;
    public static final int CODE_DEVICES_DISABLED = 1200;
    public static final int CODE_DEVICES_UNREGISTERED = 1300;

    //USERS
    public static final int CODE_USERS_INVALID = 2100;
    public static final int CODE_USERS_DISBLED = 2200;
    public static final int CODE_USERS_ENABLED = 2300;

    public static final String USER_SYSTEM_CODE_SU = "0";
    public static final String USER_SYSTEM_CODE_ADMIN = "1";
    public static final String USER_SYSTEM_CODE_USER = "2";


    //USERS DEVICES
    public static final int CODE_DEVICES_NOT_ASSIGNED_TO_USER = 3100;


    public static String CODE_ERROR_GET_INTERNET_DATE = "0";


   // Estatus Ordenes
    public static final int CODE_ORDER_STATUS_CLOSED = 0;//CERRADA
    public static final int CODE_ORDER_STATUS_OPEN = 1;//ABIERTA
    public static final int CODE_ORDER_STATUS_CANCELED = 2;//ANULADA
   // public static final int CODE_ORDER_STATUS_READY = 4;//LISTA
    //public static final int CODE_ORDER_STATUS_DELIVERED = 5;//ENTREGADA

    // Estatus Receipts
    public static final String CODE_RECEIPT_STATUS_CLOSED = "0";//CERRADA
    public static final String CODE_RECEIPT_STATUS_OPEN = "1";//ABIERTA
/*
    //TIPOS DE OPERACIONES
    public static final int CODE_TYPE_OPERATION_SALES = 1;
    public static final int CODE_TYPE_OPERATION_MESSAGE = 2;

    // USERINBOX
    public static final int CODE_USERINBOX_STATUS_NO_READ = 0;
    public static final int CODE_USERINBOX_STATUS_READ = 1;
    //TIPO DE FILTROS MESSAGES
    public static final int CODE_MESSAGE_TARGET_ALL = 0;
    public static final int CODE_MESSAGE_TARGET_USERS = 1;
    public static final int CODE_MESSAGE_TARGET_GRUPOS = 2;
    //CODIGO ICONO MENSAJE
    public static final String CODE_ICON_MESSAGE_NEW = "1";
    public static final String CODE_ICON_MESSAGE_ALERT = "2";
    public static final String CODE_ICON_MESSAGE_CHECK = "3";*/



    //PREFERENCES
    //CARGA INICIAL
    public static final String PREFERENCE_LICENSE_CODE = "LICENSE_CODE_PREFERENCE";//CODIGO DE USUARIO
    //LOGIN
    public static final String PREFERENCE_USERSKEY_CODE = "USERSKEY_CODE";//CODIGO DE USUARIOI
    public static final String PREFERENCE_USERSKEY_USERTYPE = "USERSKEY_USERTYPE";//TIPO DE USUARIO
    public static final String PREFERENCE_LOGIN_BLOQUED = "LOGIN_BLOQUED";//LOGEO BLOQUEADO
    public static final String PREFERENCE_LOGIN_BLOQUED_REASON = "LOGIN_BLOQUED_REASON";//RAZON DE BLOQUEO DE LOGIN
    public static final String PREFERENCE_LOGIN_BLOQUED_TOKEN_ATTEMPS = "LOGIN_BLOQUED_TOKEN_ATTEMPS";//NUMERO DE INTENTOS TOKEN

    //PANTALLA
    public static final String PREFERENCE_SCREEN_HEIGHT = "SCREEN_HEIGHT";
    public static final String PREFERENCE_SCREEN_WIDTH = "SCREEN_WIDTH";
    //PREFERENCES FIN
/*
    //TABLAS Y TABLAS_CODE
    public static final String TABLA_MOTIVOS_ANULADO = "Motivo Devolucion";
    public static final String TABLA_MOTIVOS_ANULADO_CODE = "motreturn";
    public static final String TABLA_TABLEFILTER_TASK = "Task Tablas Filtro";
    public static final String TABLA_TABLEFILTER_TASK_CODE = "tasktablefilter";*/


    /////////////////////////////////
    //USERCONTROL               /////
    /////////////////////////////////
    public static final String USERSCONTROL_TARGET_USER = "0";
    public static final String USERSCONTROL_TARGET_USER_ROL = "1";
    public static final String USERSCONTROL_TARGET_COMPANY = "2";

    public static final String USER_CONTROL_CREATEORDER = "CREATEORDER";//modulo de menu (para crear ordenes)
    public static final String USER_CONTROL_CHARGE_ORDERS = "CHARGE_ORDERS";//modulo de facturacion (cobrar)
    public static final String USER_CONTROL_MODIFYORDER = "MODIFYORDER";//editar ordenes desde el modulo de menu
    public static final String USER_CONTROL_ANULATEORDER = "ANULATEORDER";//anular ordenes desde el modulo de menu
    public static final String USER_CONTROL_PRINTORDERS = "PRINTORDERS";//imprimir ordenes
    //RECIBOS
    public static final String USER_CONTROL_MULTIPAYMENT="MULTIPAYMENT";//Permite hacerle multiples pagos (abonar) un recibo.



    //////////////////////////////////
    // TABLE FILTER               ///
    /////////////////////////////////
    /*Quien trabaja (Orden lista, etc..)la orden desde order board.
     */
    /*public static final String TABLE_FILTER_CODETASK_WORKORDER = "workorderboard";

    public static final String TABLE_FILTER_DESTINY_USER = "USER";
    public static final String TABLE_FILTER_DESTINY_USERTYPE = "USERTYPE";
    public static final String TABLE_FILTER_ORIGIN_PRODUCTTYPE = "PRODUCTTYPE";
    public static final String TABLE_FILTER_ORIGIN_PRODUCTSUBTYPE = "PRODUCTSUBTYPE";*/


    //////////////////////////////////
    ///  ACTIVITY EXTRAS KEYS     ////
    public static final String MAIN_REPORTS_EXTRA_IDCALLER = "MAIN_REPORTS_CALLER";
    public static final String MAIN_REPORTS_TOTALORDERS = "MAIN_REPORTS_TOTALORDERS";
    public static final String MAIN_REPORTS_TOTALORDERSAMOUNT = "MAIN_REPORTS_TOTALORDERSAMOUNT";
    public static final String MAIN_REPORTS_EXTRA_LASTDATEINI = "MAIN_REPORTS_LASTDATEINI";
    public static final String MAIN_REPORTS_EXTRA_LASTDATEEND = "MAIN_REPORTS_LASTDATEEND";

    public static final String EXTRA_TYPE_FAMILY = "MAINTENANCE_PRODUCT_TYPE_EXTRA_ENTITY_TYPE";

    public static final String EXTRA_SECURITY_ERROR_CODE = "SECURITY_ERROR_CODE";//int

    //indica la tabla con la que se va a trabajar en el activity MainAssignation.java
    public static final String EXTRA_MAINASSIGNATION_TABLE = "MAINASSIGNATION_TABLE";
    public static final String EXTRA_MAINASSIGNATION_TARGET = "MAINASSIGNATION_TARGET";

    public static final String EXTRA_MAINASSIGNATION_TARGET_ROLESCONTROL = "MAINASSIGNATION_TARGET_ROLESCONTROL";//configurar controles de ROL
    public static final String EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL = "MAINASSIGNATION_TARGET_USERSCONTROL";//configurar controles de USUARIO
    public static final String EXTRA_MAINASSIGNATION_TARGET_ORDERMOVE = "MAINASSIGNATION_TARGET_ORDERMOVE";//reasigna las ordenes abiertas de un usuario hacia otro.

    public static final String EXTRA_ADMIN_LICENSE = "EXTRA_ADMIN_LICENSE";

    public static final String PREFERENCE_BLUETOOTH_MAC_ADDRESS="BLUETOOTH_MAC_ADDRESS";

    //////////////////////////////////
    ///  REPORTS KEYS            ////
    public static final String REPORTS_FILTER_KEY_VENTAS = "0";
    public static final String REPORTS_FILTER_KEY_DEVOLUCIONES = "1";
    public static final String REPORTS_FILTER_KEY_INVENTARIOS = "2";
    public static final String REPORTS_FILTER_KEY_VENDEDORES = "3";


    //////////////////////////////////
    /// MAINTENANCE PRODUCTS_TYPES
    public static final String ENTITY_TYPE_EXTRA_INVENTORY = "ENTITY_TYPE_EXTRA_INVENTORY";
    public static final String ENTITY_TYPE_EXTRA_PRODUCTSFORSALE = "ENTITY_TYPE_EXTRA_PRODUCTSFORSALE";


    /////////////////////////////////
    //PAYMENTS                  /////
    /////////////////////////////////
    public static final String PAYMENTTYPE_CASH = "1";
    public static final String PAYMENTTYPE_CREDIT = "2";

    /////////////////////////////////
    // CONTROLS               //////
    /////////////////////////////////
    public static final String CONTROL_SALES="CONTROL_SALES";
    public static final String CONTROL_RECEIPTS = "CONTROL_RECEIPTS";
    public static final String CONTROL_REPORTS ="CONTROL_REPORTS";


    //REQUESTS CODES
    public static  final int REQUEST_BLUETOOTH_ACTIVITY=1000;

}
