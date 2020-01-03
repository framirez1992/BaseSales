package com.far.basesales.DataBase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Controllers.CompanyController;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.DevicesController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.MeasureUnitsInvController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ProductsControlController;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsInvController;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.ProductsMeasureInvController;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsSubTypesInvController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.RolesController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.SalesHistoryController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Utils.Funciones;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DB extends SQLiteOpenHelper {
    private static DB instance;
    public static DB getInstance(Context c){
    if(instance == null){
        instance = new DB(c, Tablas.DB_NAME,null,1);
    }
    return instance;
    }

    private DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.beginTransaction();

            //db.execSQL(AreasController.QUERY_CREATE);
            //db.execSQL(AreasDetailController.QUERY_CREATE);
            db.execSQL(DayController.QUERY_CREATE);
            db.execSQL(LicenseController.QUERY_CREATE);
            db.execSQL(UsersController.QUERY_CREATE);
            db.execSQL(UserTypesController.QUERY_CREATE);
            db.execSQL(ClientsController.QUERY_CREATE);
            //db.execSQL(CombosController.QUERY_CREATE);
            db.execSQL(CompanyController.QUERY_CREATE);
            db.execSQL(DevicesController.QUERY_CREATE);
            db.execSQL(MeasureUnitsController.QUERY_CREATE);
            db.execSQL(MeasureUnitsInvController.QUERY_CREATE);
            //db.execSQL(PriceListController.QUERY_CREATE);
            db.execSQL(ProductsController.QUERY_CREATE);
            db.execSQL(ProductsInvController.QUERY_CREATE);
            db.execSQL(ProductsTypesController.QUERY_CREATE);
            db.execSQL(ProductsTypesInvController.QUERY_CREATE);
            db.execSQL(ProductsSubTypesController.QUERY_CREATE);
            db.execSQL(ProductsSubTypesInvController.QUERY_CREATE);
            db.execSQL(ReceiptController.QUERY_CREATE);
            db.execSQL(PaymentController.QUERY_CREATE);
            db.execSQL(SalesController.getQueryCreateHead());
            db.execSQL(SalesController.getQueryCreateDetail());
            db.execSQL(SalesHistoryController.getQueryCreateHead());
            db.execSQL(SalesHistoryController.getQueryCreateDetail());
            //db.execSQL(StoreHouseController.QUERY_CREATE);
            //db.execSQL(StoreHouseDetailController.QUERY_CREATE);
            //db.execSQL(UserInboxController.QUERY_CREATE);
            db.execSQL(ProductsMeasureController.QUERY_CREATE);
            db.execSQL(ProductsMeasureInvController.QUERY_CREATE);
            db.execSQL(ProductsControlController.QUERY_CREATE);
            //db.execSQL(TableCodeController.QUERY_CREATE);
            //db.execSQL(TableFilterController.QUERY_CREATE);
            db.execSQL(UserControlController.QUERY_CREATE);
            db.execSQL(RolesController.QUERY_CREATE);

            db.execSQL(TempOrdersController.QUERY_CREATE);
            db.execSQL(TempOrdersController.QUERY_CREATE_DETAIL);


            db.setTransactionSuccessful();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createStructure(){

    }

    public static String getWhereFormat(String[] campos){
        String result ="";
        for(int i = 0; i< campos.length; i++){
            result+=(i == 0)?campos[i]+" = ? ":" AND "+campos[i]+" = ?";
        }
        return result;
    }
    public boolean hasDependencies(String table, String field, String code){
        boolean resutl= false;
        String sql ="SELECT "+field+" from "+table+" WHERE "+field+" = ? ";
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{code});
        if(c.moveToFirst()){
            resutl = true;
        }c.close();
        return resutl;
    }

    /**
     * obtiene el ultimo MDATE guardada en la base de datos local en la tabla especificada.
     * @return
     */
    public static Date getLastMDateSaved(Context context, String table){
        Date date = null;
        String sql = "SELECT mdate as MDATE " +
                "FROM "+table+" " +
                "ORDER BY mdate DESC " +
                "LIMIT 1 ";
        Cursor c = getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToFirst()){
            date = Funciones.parseStringToDate(c.getString(c.getColumnIndex("MDATE")));
        }c.close();
        return date;
    }
}
