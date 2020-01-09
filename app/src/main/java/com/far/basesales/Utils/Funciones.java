package com.far.basesales.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.far.basesales.Generic.KV2;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.AsyncExecutor;
import com.far.basesales.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Funciones {
    public static String getPhoneID(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getFormatedDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }
    public static String getFormatedDate(Date d){
        if(d == null){
            return null;
        }
        return getSimpleDateFormat().format(d);
    }

    public static String getFormatedDateNoTime(Date d){
        if(d == null){
            return null;
        }
        return  new SimpleDateFormat("yyyyMMdd").format(d);
    }

    public static String getFormatedDateRepDom(Date date){
        if(date == null){
            return "NO DATE";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
    public static String getFormatedDateRepDomHour(Date date){
        if(date == null){
            return "NO DATE";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        return sdf.format(date);
    }
    public static String sumaDiasFecha(String formatedDated, int dias){
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(c.getTimeInMillis());
    }

    public static SimpleDateFormat getSimpleDateFormat(){
     return new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    }
    public static SimpleDateFormat getSimpleTimeFormat(){
        return new SimpleDateFormat("HHmmss");
    }
    public static Date parseStringToDate(String date){
        if(date == null){
            return null;
        }
        Date d = new Date();
        try {
            d = getSimpleDateFormat().parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }

    public static Date sumaDiasFecha(int dias){
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dias);
        return c.getTime();
    }

    public static String gerErrorMessage(int code){
        String message = "UNKNOWN";
        switch (code){
            case CODES.CODE_LICENSE_INVALID:message = "Clave de producto invalida";
                break;
            case CODES.CODE_LICENSE_EXPIRED:message = "La licencia expiro";
                break;
            case CODES.CODE_LICENSE_DISABLED:message = "La licencia fue desabilitada";
                break;
            case CODES.CODE_LICENSE_DEVICES_LIMIT_REACHED:message = "Alcanzo el limite maximo de dispositivos permitidos de la licencia";
                break;
            case CODES.CODE_LICENSE_NO_LICENSE:message = "Debe realizar una carga inicial";
                break;
            case CODES.CODE_USERS_INVALID:message = "Usuario invalido ";
                break;
            case CODES.CODE_USERS_DISBLED:message = "Usuario deshabilitado";
                break;
            case CODES.CODE_DEVICES_UNREGISTERED:message = "Este dispositivo no esta registrado";
                break;
            case CODES.CODE_DEVICES_DISABLED:message = "Este dispositivo  esta deshabilitado";
                break;
            case CODES.CODE_DEVICES_NOT_ASSIGNED_TO_USER:message = "Este dispositivo  no esta asignado a este usuario";
                break;

        }
        return message;
    }

    public static boolean isNetDisponible(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isAvailable() && actNetInfo.isConnected() );
    }

    public static Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public static String getDateOnline(final AsyncExecutor ae){
        AsyncTask<Void, Void, String> x = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String url = "https://time.is/Unix_time_now";
                    Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
                    String[] tags = new String[]{
                            "div[id=time_section]",
                            "div[id=clock0_bg]"
                    };
                    Elements elements = doc.select(tags[0]);
                    for (int i = 0; i < tags.length; i++) {
                        elements = elements.select(tags[i]);
                    }
                    long x = Long.parseLong(elements.text() + "000");
                    String result = Funciones.getSimpleDateFormat().format(new Date(x));
                    return result;
                } catch (Exception e) {
                    return CODES.CODE_ERROR_GET_INTERNET_DATE;
                }

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
             ae.setMessage(s);
            }
        };
        x.execute();

        return "";
    }

   /* public static void showLoading(View v){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        llLoading.setVisibility(View.VISIBLE);
        llNetwork.setVisibility(View.GONE);
    }

    public static void showNetworkError(View v){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        llLoading.setVisibility(View.GONE);
        llNetwork.setVisibility(View.VISIBLE);
    }

    public static void showNetworkErrorWithText(View v, String msg){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        TextView tvMsgNetwork = v.findViewById(R.id.tvMsgNetwork);
        llLoading.setVisibility(View.GONE);
        llNetwork.setVisibility(View.VISIBLE);
        tvMsgNetwork.setText(msg);
    }

    public static void hideLoadingNetwork(View v){
        LinearLayout llLoading = v.findViewById(R.id.llProgress);
        LinearLayout llNetwork = v.findViewById(R.id.llNetwork);
        llLoading.setVisibility(View.INVISIBLE);
        llNetwork.setVisibility(View.GONE);
    }*/

    public static boolean fechaMayorQue(String fechaProtagonista, String fecha){
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            Calendar c1 = Calendar.getInstance();c1.setTime(sdf.parse(fechaProtagonista));
            Calendar c2 = Calendar.getInstance();c2.setTime(sdf.parse(fecha));
            return c1.after(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean fechaMayorQue(Date fechaProtagonista, Date fecha){
        try {
            Calendar c1 = Calendar.getInstance();c1.setTime(fechaProtagonista);
            Calendar c2 = Calendar.getInstance();c2.setTime(fecha);
            return c1.after(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static boolean fechaMenorQue(Date fechaProtagonista, Date fecha){
        try {
            Calendar c1 = Calendar.getInstance();c1.setTime(fechaProtagonista);
            Calendar c2 = Calendar.getInstance();c2.setTime(fecha);
            return c1.before(c2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public static int calcularDias(String fechaProtagonista, String fecha){
        try {
            SimpleDateFormat sdf =Funciones.getSimpleDateFormat();

            Calendar c1 = Calendar.getInstance();c1.setTime(sdf.parse(fechaProtagonista));
            Calendar c2 = Calendar.getInstance();c2.setTime(sdf.parse(fecha));

            double d = c1.getTimeInMillis() - c2.getTimeInMillis();

            long dias = Math.round(d / ( 24 * 60 * 60 * 1000));
            return ((int) dias);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static int calcularDias(Date dateEnd, Date dateIni){
        try {
            Calendar c1 = Calendar.getInstance();c1.setTime(dateEnd);
            Calendar c2 = Calendar.getInstance();c2.setTime(dateIni);

            double d = c1.getTimeInMillis() - c2.getTimeInMillis();

            long dias = Math.round(d / ( 24 * 60 * 60 * 1000));
            return ((int) dias);

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static int calcularMinutos(Date fechaProtagonista, Date fecha){
        try {
            if(fechaProtagonista == null){
                return 0;
            }


            Calendar c1 = Calendar.getInstance();
            c1.setTime(fechaProtagonista);
            Calendar c2 = Calendar.getInstance();
            c2.setTime(fecha);
            long minutes = c1.getTime().getTime()/(1000 * 60) - c2.getTime().getTime()/(1000 * 60);

            return  (int)minutes;

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public static void setAlarm(Context c, String date){
        try {
            AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent();
            intent.setAction("far.com.eatit.ALARM");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
/*no funciona*/
    /*
    public static String getCode(String field, String entity, Context cont){
            String code = "0000";
            String sql = "Select CAST(MAX("+field+") AS INTEGER) + 1 from "+entity;
            Cursor c = DB.getInstance(cont).getReadableDatabase().rawQuery(sql, null);
            if(c.moveToNext() && c.getString(0) != null){
                code = c.getString(0);
            }
            return code;
    }

    public String getNextCodeWhithPrefix(String prefix, String field, String entity, Context context){
        String code = prefix+"0000";
        String sql = "Select CAST(SUBSTR(MAX("+field+"), "+(prefix.length() + 1)+", LEN("+field+")) AS INTEGER) + 1 from "+entity
                +" WHERE "+field+" LIKE '"+prefix+"%'";
        Cursor c = DB.getInstance(context).getReadableDatabase().rawQuery(sql, null);
        if(c.moveToNext()){
            code = c.getString(0);
        }
        return code;
    }*/

    public static void savePreferences(Context context, String key, Object value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        if(value instanceof String){
            edit.putString(key, String.valueOf(value));
        }else if(value instanceof Integer){
            edit.putInt(key, (Integer) value);
        }else if(value instanceof Long){
            edit.putLong(key, (Long) value);
        }else if(value instanceof Boolean){
            edit.putBoolean(key, (Boolean) value);
        }

        edit.commit();
    }

    public static String getCodeLicense(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_LICENSE_CODE, "");
    }
    public static String getCodeuserLogged(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_USERSKEY_CODE, "");
    }
    public static String getRoleUserLogged(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_USERSKEY_USERTYPE, "");
    }
    public static String getMacAddress(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(CODES.PREFERENCE_BLUETOOTH_MAC_ADDRESS, "");
    }

    public static String getPreferences(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
       return preferences.getString(key, "");
    }
    public static int getPreferencesInt(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, -1);
    }

    public static void clearPreference(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    public static void showKeyBoard(EditText et, Context c){
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyBoard(final EditText et){
        et.post(new Runnable() {
            @Override
            public void run() {
                final InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                et.requestFocus(); // needed if you have more then one input
            }
        });
    }

    public static void hideKeyBoard(EditText et, Context c){
        InputMethodManager imm = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }


    public static void sendSMS(String number, String msg){

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number,null,msg,null, null);
    }

    public static void showAlertDependencies(Context c, String msgDependency){
        AlertDialog a = new AlertDialog.Builder(c).create();
        a.setMessage("No se puede eliminar debido a las siguientes dependencias: \n"+msgDependency);
        a.show();
    }

    public static Dialog getAlertDeleteAllDependencies(Context c, String itemName, ArrayList<KV2> tables){
        String fullDependencyMsg ="";
        String msgDependency = "";
        for(KV2 s: tables){
            msgDependency+= s.getKey()+"\n";
        }
        if(!msgDependency.isEmpty()){
            fullDependencyMsg="\nTambien seran eliminadas todas las dependencias en: \n"+msgDependency;
        }

        String msg = "Esta seguro que desea eliminar ["+itemName+"]  permanentemente?"+fullDependencyMsg;
        final Dialog d = getCustomDialog2Btn(c,c.getResources().getColor(R.color.red_700),"Delete", msg,R.drawable.delete,null, null);
        d.findViewById(R.id.btnNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        return d;
    }

    public static String generateCode(){
        Calendar calendar = Calendar.getInstance();
        String year =  String.format("%04d", calendar.get(Calendar.YEAR));
        String month = String.format("%02d", calendar.get(Calendar.MONTH)+1);
        String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        String hour =  String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
        String minute =  String.format("%02d", calendar.get(Calendar.MINUTE));
        String seconds = String.format("%02d", calendar.get(Calendar.SECOND));
        String milliseconds = String.format("%03d", calendar.get(Calendar.MILLISECOND));
        String data = year+month+day+hour+minute+seconds+milliseconds;
        return data;
    }

    public static void saveScreenMetrics(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        savePreferences(activity,CODES.PREFERENCE_SCREEN_HEIGHT,height);
        savePreferences(activity,CODES.PREFERENCE_SCREEN_WIDTH,width);
    }

    public static String reservarCaracteres(String text, int caracteres){
        return  reservarCaracteres(text, caracteres, false);
    }

    public static String reservarCaracteresAlinearDerecha(String text, int caracteres){
        return  reservarCaracteres(text, caracteres, true);
    }
    public static String reservarCaracteres(String text, int caracteres, boolean alinearDerecha){
        String format = "%"+(alinearDerecha?"":"-")+caracteres+"s";
        String data = String.format(format, text);
        return data;
    }

    public static String centrarTexto(String texto, int longitud){
        int lRestante = longitud - texto.length();
        if(lRestante >0){
            int espacio = texto.length()+lRestante/2;
            String data = reservarCaracteres(texto,espacio);
            espacio = data.length()+lRestante/2;
            data = reservarCaracteresAlinearDerecha(data,espacio);
            return data;
        }

        return texto;


    }

    public static String formatDecimal(String decimal){
        return formatDecimal(Double.parseDouble(decimal.replace(",", "")));
    }
    public static String formatDecimal(double decimal){
        return String.format("%.2f", decimal);
    }

    public static String formatPhone(String phone){
        String formatted =phone;
        if(phone.length() == 10){
            formatted = phone.substring(0, 3)+"-"+phone.substring(3, 6)+"-"+phone.substring(6);
        }
        return formatted;
    }

    public static String formatMoney(double amount){
            DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
            return formatter.format(amount);
    }
    public static Dialog getCustomDialog(Context context, int color, String title, String msg, int icon, View.OnClickListener listener){
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.custom_dialog_1btn);
        ((RelativeLayout)d.findViewById(R.id.rlBackground)).setBackgroundColor(color);
        ((TextView)d.findViewById(R.id.tvTitle)).setText(title);
        ((TextView)d.findViewById(R.id.tvMsg)).setText(msg);
        ((ImageView)d.findViewById(R.id.img)).setImageResource(icon);
        CardView cvOk = ((CardView)d.findViewById(R.id.cvOk));
        cvOk.setCardBackgroundColor(color);
        cvOk.setOnClickListener(listener);
        try{
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }

        return d;
    }


    public static Dialog getCustomDialog2Btn(Context context,int color,  String title, String msg, int icon, View.OnClickListener positive, View.OnClickListener negative){
         Dialog d = new Dialog(context);
         d.setContentView(R.layout.msg_2_buttons);
         d.setCancelable(false);
        ((CardView)d.findViewById(R.id.cvCard)).setCardBackgroundColor(color);
        ((TextView)d.findViewById(R.id.tvTitle)).setText(title);
        ((TextView)d.findViewById(R.id.tvMsg)).setText(msg);
        ImageView img = ((ImageView)d.findViewById(R.id.img));
        img.setImageResource(icon);
        ImageViewCompat.setImageTintList(img, ColorStateList.valueOf(color));
        CardView btnPositive = ((CardView)d.findViewById(R.id.btnPositive));
        btnPositive.setCardBackgroundColor(color);
        btnPositive.setOnClickListener(positive);
        ((CardView)d.findViewById(R.id.btnNegative)).setOnClickListener(negative);
        try{
            d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception e){
            e.printStackTrace();
        }

        return d;
    }

    public static String getFileExtension(Context context, Uri uri){
        ContentResolver cr = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    public static Dialog getLoadingDialog(Context context, String msg){
        Dialog d = new Dialog(context);
        d.setContentView(R.layout.loading);
        ((TextView)d.findViewById(R.id.tvMsg)).setText(msg);
        return d;
    }

}
