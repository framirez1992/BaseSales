package com.far.basesales.Controllers;

import android.content.Context;
import android.database.Cursor;

import com.far.basesales.DataBase.DB;

public class VersionAppController {

    public static final String TABLE_NAME ="VERSIONAPP";
    public static  String VERSION = "version";

    String[] columns = new String[]{VERSION};
    public static String QUERY_CREATE = "CREATE TABLE "+TABLE_NAME+"("+VERSION+" TEXT )";

    Context context;

    private static VersionAppController instance;
    private VersionAppController(Context c){
        this.context = c;
    }

    public static VersionAppController getInstance(Context context){
        if(instance == null){
            instance = new VersionAppController(context);
        }
        return instance;
    }

    public void updateAppStructure(){
        //v1();
    }

    public void v1(){
        if(!existVersion("1")){
           // DB.getInstance(context).getWritableDatabase().execSQL(CounterController.);
        }

    }

    public boolean existVersion(String v){
        boolean value;
        String sql = "SELECT "+VERSION+" as VERSION FROM "+TABLE_NAME+" WHERE "+VERSION+" = ?";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, new String[]{"1"});
        value = c.moveToFirst();
        c.close();
        return value;
    }

}
