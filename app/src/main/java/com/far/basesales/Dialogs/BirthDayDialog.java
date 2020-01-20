package com.far.basesales.Dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.far.basesales.Adapters.ClientsAdapter;
import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Controllers.CompanyController;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class BirthDayDialog extends DialogFragment implements ListableActivity {

    Activity parentActivity;
    DialogCaller dialogCaller;
    RecyclerView rvList;
    ProgressBar pb;
    ClientsController clientsController;
    CardView btnOK, btnClose;
    ClientRowModel selectedClient;

    public  static BirthDayDialog newInstance(Activity parentActivity, DialogCaller dialogCaller) {
        BirthDayDialog f = new BirthDayDialog();
        f.parentActivity = parentActivity;
        f.dialogCaller = dialogCaller;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientsController = ClientsController.getInstance(parentActivity);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.birthday_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        search();
    }


    public void init(View view){
        rvList= view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        btnOK = view.findViewById(R.id.btnOK);
        btnClose = view.findViewById(R.id.btnClose);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }



    public void search(){
        pb.setVisibility(View.VISIBLE);
        AsyncTask<Void, Void, ArrayList<ClientRowModel>> a = new AsyncTask<Void, Void, ArrayList<ClientRowModel>>() {
            @Override
            protected ArrayList<ClientRowModel> doInBackground(Void... voids) {
                Calendar c = Calendar.getInstance();
                String month = String.valueOf(c.get(Calendar.MONTH)+1);
                String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                if(month.length()==1){
                    month="0"+month;
                }

                if(day.length()==1){
                    day="0"+day;
                }
                return ClientsController.getInstance(parentActivity).getClientSRM(ClientsController.DATA+" like ?", new String[]{(day+"/"+month+"%")}, ClientsController.NAME);
            }

            @Override
            protected void onPostExecute(ArrayList<ClientRowModel> clientRowModels) {
                super.onPostExecute(clientRowModels);
                pb.setVisibility(View.GONE);
                ClientsAdapter adapter = new ClientsAdapter(parentActivity, BirthDayDialog.this, clientRowModels);
                rvList.setAdapter(adapter);
                rvList.getAdapter().notifyDataSetChanged();
                rvList.invalidate();
            }
        };
        a.execute();

    }


    @Override
    public void onClick(Object obj) {
        selectedClient = (ClientRowModel)obj;
    }

    public void sendMessage(){
        Company company = CompanyController.getInstance(parentActivity).getCompany();
        //Convertir caracteres a exadecimales
        //https://norfipc.com/herramientas/escapar-codificar-caracteres-direcciones-url-hexadecimal.php
        String msg = company.getNAME().replace("&", "%26")+" quiere desearte muchas felicidades en tu dia "+selectedClient.getName()+";";

        try {
            String mobile = "1"+selectedClient.getPhone();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=" + mobile + "&text=" + msg)));
        }catch (Exception e){
            Toast.makeText(parentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        ///Otra altenativa :To create your own link with a pre-filled message that will automatically appear in the text field of a chat, use https://wa.me/whatsappphonenumber/?text=urlencodedtext where whatsappphonenumber is a full phone number in international format and URL-encodedtext is the URL-encoded pre-filled message.

    }


    public void Share(){
        try {

            AsyncTask<Void, Void, String> as = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Company company = CompanyController.getInstance(parentActivity).getCompany();
                    String data = company.getNAME()+" quiere desearte muchas feliciades en tu dia Fulano";
                    String rutaImagen="";
                    try {

                        rutaImagen = company.getLOGO();


                    }catch(Exception e){
                        return "FAIL-"+e.getMessage().toString();
                    }

                    return "OK-"+rutaImagen+"xcut"+data;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    if(s.startsWith("OK-")){
                        String data = s.replace("OK-", "").split("xcut")[1];
                        String[] rutaImagen = (s.replace("OK-", "").split("xcut")[0]).split("ximg");

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,data);//WorkGreat
                        ArrayList<Uri> images = new ArrayList<>();
                        for(String url: rutaImagen){
                            images.add(Uri.parse(url)/*Uri.fromFile(new File(url))*/);
                        }
                        if(images.size() == 1){
                            sendIntent.putExtra(Intent.EXTRA_STREAM, images.get(0)); // 1 sola imagen o video
                        }else{
                            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, images);//varias imagens o videos etc
                        }

                        sendIntent.setType("*/*");

                        startActivity(Intent.createChooser(sendIntent, "com.whatsapp"));
                    }else{
                        Toast.makeText(parentActivity, s, Toast.LENGTH_LONG).show();
                    }

                }
            };
            as.execute();
        }catch (Exception e){
            Toast.makeText(parentActivity,  e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
