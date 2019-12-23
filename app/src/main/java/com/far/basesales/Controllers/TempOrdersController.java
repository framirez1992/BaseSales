package com.far.basesales.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.far.basesales.Adapters.Models.OrderDetailModel;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.DataBase.DB;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;



public class TempOrdersController {
        public static String TABLE_NAME = Tablas.tempOrders;
    public static String CODE = "code",STATUS = "status", SUBTOTAL="subtotal",TOTALTAXES="totaltaxes" ,TOTALDISCOUNT = "totaldiscount",TOTAL="total",CODEUSER = "codeuser",  CODERECEIPT = "codereceipt", DATE = "date",MDATE = "mdate" ;
    String[]columns = new String[]{CODE,STATUS, DATE, MDATE, SUBTOTAL, TOTALTAXES,TOTALDISCOUNT, TOTAL , CODEUSER, CODERECEIPT};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+" ("
            +CODE+" TEXT,"+STATUS+" TEXT,"+SUBTOTAL+" DECIMAL(11, 3),"+TOTALTAXES+" DECIMAL(11, 3), "+TOTALDISCOUNT+" DECIMAL(11, 3), "+TOTAL+" DECIMAL(11, 3),  "+CODEUSER+" TEXT, "+CODERECEIPT+" TEXT, "+DATE+" TEXT,"+MDATE+" TEXT )";

    public static final String TABLE_NAME_DETAIL = Tablas.tempOrdersDetails;
    public static String DETAIL_CODE = "code",DETAIL_CODESALES = "codesales", DETAIL_CODEPRODUCT = "codeproduct",
            DETAIL_DISCOUNT = "discount", DETAIL_POSITION = "position", DETAIL_PRICE = "price",DETAIL_MANUALPRICE = "manualprice", DETAIL_TAX = "tax",
            DETAIL_QUANTITY = "quantity", DETAIL_CODEUND = "codeund", DETAIL_DATE="date", DETAIL_MDATE="mdate";
    String[]columnsDetails = new String[]{DETAIL_CODE,DETAIL_CODESALES, DETAIL_CODEPRODUCT,DETAIL_CODEUND,DETAIL_DISCOUNT,DETAIL_POSITION,DETAIL_QUANTITY,DETAIL_PRICE,DETAIL_MANUALPRICE, DETAIL_TAX, DATE, MDATE};
    public static String QUERY_CREATE_DETAIL = "CREATE TABLE "+TABLE_NAME_DETAIL+" ("
            +DETAIL_CODE+" TEXT,"+DETAIL_CODESALES+" TEXT, "+DETAIL_CODEPRODUCT+" TEXT, "+DETAIL_DISCOUNT+" DECIMAL(11,3), "+DETAIL_POSITION+" INTEGER, "
            +DETAIL_PRICE+" DECIMAL(11, 3), "+DETAIL_MANUALPRICE+" DECIMAL(11, 3),  "+DETAIL_QUANTITY+" DOUBLE, "+DETAIL_TAX+" DECIMAL(11,3), "+DETAIL_CODEUND+" TEXT, " +
            DETAIL_DATE+" TEXT, "+DETAIL_MDATE+" TEXT)";

    Context context;
    DB sqlite;

        private static TempOrdersController instance;
        private TempOrdersController(Context c){
            this.context = c;
            sqlite = DB.getInstance(c);
        }
        public static TempOrdersController getInstance(Context c){
            if(instance == null){
                instance = new TempOrdersController(c);
            }
            return instance;
        }

    public long insert(Sales s){
        String table = TABLE_NAME;

        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(TOTALTAXES,s.getTOTALTAXES());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(TOTAL,s.getTOTAL());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(CODERECEIPT, s.getCODERECEIPT());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));

        long result = DB.getInstance(context).getWritableDatabase().insert(table,null,cv);
        return result;
    }


    public long update(Sales s, String where, String[]args){
        ContentValues cv = new ContentValues();
        cv.put(CODE,s.getCODE());
        cv.put(STATUS, s.getSTATUS());
        cv.put(TOTALTAXES,s.getTOTALTAXES());
        cv.put(TOTALDISCOUNT,s.getTOTALDISCOUNT());
        cv.put(TOTAL,s.getTOTAL());
        cv.put(CODEUSER, s.getCODEUSER());
        cv.put(CODERECEIPT, s.getCODERECEIPT());
        cv.put(DATE, Funciones.getFormatedDate(s.getDATE()));
        cv.put(MDATE,Funciones.getFormatedDate(s.getMDATE()));

            long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME,cv,where, args );
            return result;
        }



        public long delete(String where, String[] args){
            long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME,where, args);
            return result;
        }


    public long insert_Detail(SalesDetails sd){
        String tabla = TABLE_NAME_DETAIL;
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_MANUALPRICE, sd.getMANUALPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_TAX,sd.getTAX());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_DATE, Funciones.getFormatedDate(sd.getDATE()));
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));


        long result = DB.getInstance(context).getWritableDatabase().insert(tabla,null,cv);
        return result;
    }


    public long update_Detail(SalesDetails sd){
        ContentValues cv = new ContentValues();
        cv.put(DETAIL_CODE,sd.getCODE());
        cv.put(DETAIL_CODESALES, sd.getCODESALES());
        cv.put(DETAIL_CODEPRODUCT,sd.getCODEPRODUCT());
        cv.put(DETAIL_DISCOUNT,sd.getDISCOUNT());
        cv.put(DETAIL_POSITION,sd.getPOSITION());
        cv.put(DETAIL_PRICE,sd.getPRICE());
        cv.put(DETAIL_MANUALPRICE, sd.getMANUALPRICE());
        cv.put(DETAIL_QUANTITY,sd.getQUANTITY());
        cv.put(DETAIL_TAX,sd.getTAX());
        cv.put(DETAIL_CODEUND, sd.getCODEUND());
        cv.put(DETAIL_MDATE, Funciones.getFormatedDate(sd.getMDATE()));

        String where = DETAIL_CODE+"= ?  AND "+DETAIL_CODEPRODUCT+"= ? AND "+DETAIL_CODEUND+" = ?";

        long result = DB.getInstance(context).getWritableDatabase().update(TABLE_NAME_DETAIL,cv,where, new String[] {sd.getCODE(), sd.getCODEPRODUCT(), sd.getCODEUND()});
        return result;
    }


      /*  public ArrayList<OrderModel> getOrderModels(String where){
            ArrayList<OrderModel> objects = new ArrayList<>();
            String sql = "Select "+CODE+", "+TOTALDISCOUNT+", "+TOTAL+", " +DATE+", "+MDATE+" "+
                    "FROM "+TABLE_NAME+" " +
                    ((where == null)?"":"WHERE "+where);
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
            while(c.moveToNext()){

                String code = c.getString(c.getColumnIndex(CODE));
                String fecha = c.getString(c.getColumnIndex(MDATE));
                OrderModel om = new OrderModel(code,String.valueOf(Funciones.calcularMinutos(fecha, Funciones.getFormatedDate())), getOrderDetailModels(code));
                objects.add(om);
            }
            return objects;
        }*/

        public ArrayList<OrderDetailModel> getOrderDetailModels(String code){

            ArrayList<OrderDetailModel> objects = new ArrayList<>();
            try {
                String where = "sd."+DETAIL_CODESALES + " = '" + code+"'";

                String sql = "Select sd."+DETAIL_CODE+" as CODE,sd."+DETAIL_CODESALES+" as CODESALES ,p."+ProductsController.CODE+" AS CODEPRODUCT" +
                        ", p." + ProductsController.DESCRIPTION + " as PRODUCTO, sd." + DETAIL_QUANTITY + " as CANTIDAD, sd." + DETAIL_TAX + " as IMPUESTO," +
                        " m."+MeasureUnitsController.CODE+" AS CODEMEDIDA, m." + MeasureUnitsController.DESCRIPTION + " AS MEDIDA, ifnull(pc."+ProductsControlController.BLOQUED+", '0') as BLOQUED, " +
                        "sd."+DETAIL_MANUALPRICE+" as MANUALPRICE,  sd."+DETAIL_DATE+" as DATE, sd."+DETAIL_MDATE+" as MDATE "+
                        "FROM " + TABLE_NAME_DETAIL + " sd " +
                        "LEFT JOIN " + ProductsController.TABLE_NAME + " p on sd." + DETAIL_CODEPRODUCT + " = p." + ProductsController.CODE + " " +
                        "LEFT JOIN " + MeasureUnitsController.TABLE_NAME + " m on m." + MeasureUnitsController.CODE + " = sd." + DETAIL_CODEUND + " " +
                        "LEFT JOIN "+ProductsControlController.TABLE_NAME+" pc on pc."+ProductsControlController.CODEPRODUCT+ " = p." + ProductsController.CODE + " " +
                        "WHERE " + where+" " +
                        "GROUP BY p."+ProductsController.CODE+", sd."+DETAIL_CODEUND;
                Cursor c = sqlite.getReadableDatabase().rawQuery(sql, null);
                while (c.moveToNext()) {

                    OrderDetailModel om = new OrderDetailModel(
                            c.getString(c.getColumnIndex("CODEPRODUCT")),
                            c.getString(c.getColumnIndex("CODE")),
                            c.getString(c.getColumnIndex("CODESALES")),
                            c.getString(c.getColumnIndex("PRODUCTO")),
                            c.getString(c.getColumnIndex("CANTIDAD")),
                            c.getString(c.getColumnIndex("MANUALPRICE")),
                            c.getString(c.getColumnIndex("CODEMEDIDA")),
                            c.getString(c.getColumnIndex("MEDIDA")),
                            c.getString(c.getColumnIndex("BLOQUED")),
                            ProductsMeasureController.getInstance(context).getProductsMeasureKVByCodeProduct(c.getString(c.getColumnIndex("CODEPRODUCT"))));
                    objects.add(om);

                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return objects;
        }


        public Sales getTempSale(){
           Sales s = null;

            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, null);
            if(c.moveToFirst()){
                s = new Sales(c);
            }
            s.setTOTAL(getSumPrice());
            c.close();
            return s;
        }
/*
    public ArrayList<ArrayList> getSplitedTempSale(String notes, String codeAreaDetail){


        ArrayList<ArrayList> result = new ArrayList<>();

        try {
            Sales originalSale = null;
            Cursor cos = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME, columns, null, null, null, null, null);
            if (cos.moveToFirst()) {
                originalSale = new Sales(cos);
                originalSale.setNOTES(notes);
                originalSale.setCODEAREADETAIL(codeAreaDetail);
            }
            cos.close();

            ArrayList<Sales> sales = new ArrayList<>();
            ArrayList<SalesDetails> salesDetails = new ArrayList<>();
            ArrayList<String> ArraySplit = new ArrayList<>();

            if (originalSale != null) {

                String splitBy = UserControlController.getInstance(context).orderSplitType();
                String fieldOrder = "p." + ProductsController.TYPE;//DEFAULT
                if (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY)) {
                    fieldOrder = "p." + ProductsController.TYPE;
                    ArraySplit = getDistinctProductTypesInOrderDetail();
                } else if (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP)) {
                    fieldOrder = "p." + ProductsController.SUBTYPE;
                    ArraySplit = getDistinctProductSubTypesInOrderDetail();
                }
                String sql = "SELECT od." + DETAIL_CODE + " as " + DETAIL_CODE + " , od." + DETAIL_CODESALES + " as " + DETAIL_CODESALES + " , od." + DETAIL_CODEPRODUCT + " as " + DETAIL_CODEPRODUCT + ",od." +
                        DETAIL_DISCOUNT + " as " + DETAIL_DISCOUNT + ",od." + DETAIL_POSITION + " as " + DETAIL_POSITION + ",od." + DETAIL_PRICE + " as " + DETAIL_PRICE + ",od." +
                        DETAIL_QUANTITY + " as " + DETAIL_QUANTITY + ",od." + DETAIL_UNIT + " as " + DETAIL_UNIT + ",od." + DETAIL_CODEUND + " as " + DETAIL_CODEUND + ", od." + DETAIL_DATE + " as "+DETAIL_DATE+", od." + DETAIL_MDATE + " as " + DETAIL_MDATE
                        + ", " + fieldOrder + " as FO " +
                        "FROM " + TABLE_NAME_DETAIL + " od " +
                        "INNER JOIN " + ProductsController.TABLE_NAME + " p on p." + ProductsController.CODE + " = od." + DETAIL_CODEPRODUCT + " " +
                        "ORDER BY " + fieldOrder;

                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
                int lastIndex = -1;
                for (String spl : ArraySplit) {
                    String codeSalesOrigen =originalSale.getCODE();
                    String code = Funciones.generateCode();
                    String codeuser = originalSale.getCODEUSER();
                    String codeAreaDet = originalSale.getCODEAREADETAIL();
                    double totalDiscount = originalSale.getTOTALDISCOUNT();
                    double total = originalSale.getTOTAL();
                    int status = originalSale.getSTATUS();
                    String note = originalSale.getNOTES();
                    String codeReason = originalSale.getCODEREASON();
                    String reasonDescription = originalSale.getCODEREASON();
                    String codeProductType= (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_FAMILY))?spl: null;
                    String codeProductSubType = (splitBy.equals(CODES.VAL_USERCONTROL_ORDERSPLITTYPE_GROUP))?spl: null;


                    Sales s = new Sales(code, codeuser, codeAreaDet, totalDiscount, total, status, note, codeReason, reasonDescription, codeProductType, codeProductSubType,codeSalesOrigen, null );
                    sales.add(s);
                    c.moveToPosition(lastIndex);
                    while (c.moveToNext()) {
                        lastIndex++;
                        if (!c.getString(c.getColumnIndex("FO")).equals(spl)) {
                            lastIndex-=1;
                            break;
                        }
                        SalesDetails sd = new SalesDetails(c);
                        sd.setCODESALES(code);//Codigo de la orden
                        salesDetails.add(sd);

                    }
                }
                c.close();

                result.add(sales);//SALES position 0
                result.add(salesDetails);//SALES_DETAILS  position 1

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }*/



    public long delete_Detail(String where, String[] args){
        long result = DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL,where, args);
        return result;
    }

    public ArrayList<String> getDistinctProductTypesInOrderDetail(){
            ArrayList<String> familys = new ArrayList<>();
            String sql = "SELECT  p."+ProductsController.TYPE+" " +
                    "FROM "+ProductsController.TABLE_NAME+" p " +
                    "INNER JOIN "+ TABLE_NAME_DETAIL+" od on od."+DETAIL_CODEPRODUCT+" = p."+ProductsController.CODE+" " +
                    "GROUP BY p."+ProductsController.TYPE;
            try {
                Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
                while(c.moveToNext()){
                    familys.add(c.getString(0));
                }c.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            return familys;
    }
    public ArrayList<String> getDistinctProductSubTypesInOrderDetail(){
        ArrayList<String> groups = new ArrayList<>();
        String sql = "SELECT  p."+ProductsController.SUBTYPE+" " +
                "FROM "+ProductsController.TABLE_NAME+" p " +
                "INNER JOIN "+ TABLE_NAME_DETAIL+" od on od."+DETAIL_CODEPRODUCT+" = p."+ProductsController.CODE+" " +
                "GROUP BY p."+ProductsController.SUBTYPE;
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            while(c.moveToNext()){
                groups.add(c.getString(0));
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return groups;
    }



        public ArrayList<SalesDetails> getTempSalesDetails( Sales s){
           ArrayList<SalesDetails> result = new ArrayList<>();

            String selection = DETAIL_CODESALES +" = ?";
            String[] args = new String[]{s.getCODE()};
            Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

            while (c.moveToNext()){
                result.add(new SalesDetails(c));
            }
            c.close();
            return result;
        }

    public SalesDetails getTempSaleDetailByCode( String code_sale_detail){
       SalesDetails result = null;

        String selection = DETAIL_CODE +" = ?";
        String[] args = new String[]{code_sale_detail};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

        if (c.moveToFirst()){
           result = new SalesDetails(c);
        }
        c.close();
        return result;
    }

    public ArrayList<SalesDetails> getTempSaleDetailByCodeProduct( String codeProduct){
        ArrayList<SalesDetails> result = new ArrayList<>();

        String selection = DETAIL_CODEPRODUCT +" = ?";
        String[] args = new String[]{codeProduct};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

        if (c.moveToFirst()){
            result.add( new SalesDetails(c));
        }
        c.close();
        return result;
    }

    public SalesDetails getTempSaleDetailByCodeProductAndCodeMeasure( String code_product, String code_measure){
        SalesDetails result = null;

        String selection = DETAIL_CODEPRODUCT +" = ? AND "+DETAIL_CODEUND+" = ? ";
        String[] args = new String[]{code_product, code_measure};
        Cursor c = DB.getInstance(context).getReadableDatabase().query(TABLE_NAME_DETAIL, columnsDetails, selection, args, null, null, null);

        if (c.moveToFirst()){
            result = new SalesDetails(c);
        }
        c.close();
        return result;
    }

    public void deleteTempSaleDetailByCodeProductAndCodeMeasure( String code_product, String code_measure){

        try {
            String selection = DETAIL_CODEPRODUCT + " = ? AND " + DETAIL_CODEUND + " = ? ";
            String[] args = new String[]{code_product, code_measure};
            DB.getInstance(context).getWritableDatabase().delete(TABLE_NAME_DETAIL, selection, args);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public double getSumPrice(){
            double result = 0.0;
             String sql = "SELECT  SUM(ifnull("+DETAIL_MANUALPRICE+", "+DETAIL_PRICE+") * "+DETAIL_QUANTITY+") AS TOTAL " +
                "FROM "+TABLE_NAME_DETAIL+" ";
        try {
            Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToFirst()){
                result =c.getDouble(0);
            }c.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    public void updatePrices(){
            String sql = "SELECT sd."+DETAIL_CODEPRODUCT+" as CODEPRODUCT, sd."+DETAIL_CODEUND+" as CODEUND, pm."+ProductsMeasureController.PRICE+" as PRICE " +
                    "FROM "+TABLE_NAME_DETAIL+" sd " +
                    "INNER JOIN "+ProductsMeasureController.TABLE_NAME+" pm on sd."+DETAIL_CODEPRODUCT+" = pm."+ProductsMeasureController.CODEPRODUCT+" " +
                    "AND sd."+DETAIL_CODEUND+" = pm."+ProductsMeasureController.CODEMEASURE+" ";
            Cursor c = sqlite.getReadableDatabase().rawQuery(sql,null);
            while(c.moveToNext()){
                ContentValues cv = new ContentValues();
                cv.put(DETAIL_PRICE, c.getDouble(c.getColumnIndex("PRICE")));
                        String where = DETAIL_CODEPRODUCT+" = ? AND "+DETAIL_CODEUND+" = ?";
                sqlite.getWritableDatabase().update(TABLE_NAME_DETAIL,cv,where,new String[]{c.getString(c.getColumnIndex("CODEPRODUCT")), c.getString(c.getColumnIndex("CODEUND"))});
            }c.close();
    }




}
