package com.far.basesales;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.ClientRowModel;
import com.far.basesales.Adapters.Models.ReceiptRowModel;
import com.far.basesales.Adapters.ReceiptAdapter;
import com.far.basesales.CloudFireStoreObjects.Clients;
import com.far.basesales.CloudFireStoreObjects.Receipts;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Dialogs.ClientSearchDialog;
import com.far.basesales.Dialogs.ClientsDialogFragment;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Interfases.DialogCaller;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.far.farpdf.Entities.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiptSearchFragment extends Fragment implements DialogCaller  {

    Activity parentActivity;
    Spinner spnStatus;
    RecyclerView rvList;
    ProgressBar pb;

    CardView btnSearch, btnSearchClients;
    CheckBox cbClient, cbDate;
    TextInputEditText etClient, etDateIni, etDateEnd;
    ClientRowModel selectedClient;
    LinearLayout llClients;

    int lastDatePressed;
    Date ini= null;
    Date end = null;
    KV lastStatus = null;
    boolean lastCheckClient = false;
    boolean lastCheckDate = false;

    boolean firstTime=true;

    boolean clientControl = false;


    public ReceiptSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientControl = UserControlController.getInstance(parentActivity).searchSimpleControl(CODES.USERSCONTROL_CLIENTS)!= null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spnStatus = view.findViewById(R.id.spnStatus);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearchClients = view.findViewById(R.id.btnSearchClients);
        etDateIni = view.findViewById(R.id.etDateIni);
        etDateEnd = view.findViewById(R.id.etDateEnd);
        etClient = view.findViewById(R.id.etClient);
        cbDate = view.findViewById(R.id.cbDate);
        cbClient = view.findViewById(R.id.cbClient);
        llClients = view.findViewById(R.id.llClients);

        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                KV obj= (KV)parent.getItemAtPosition(position);
                if(!clientControl){
                    if(obj.getKey().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
                        cbDate.setChecked(true);
                        cbDate.setEnabled(false);
                    }else{
                        cbDate.setChecked(false);
                        cbDate.setEnabled(true);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    searchReceiptFromFireBase();
                }

            }
        });

        btnSearchClients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            searchClient();
            }
        });

        etDateIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDatePicker(v.getId());
            }
        });

        etDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v.getId());
            }
        });

        fillSpnStatus();

        setUpControls();

    }

    @Override
    public void onResume() {
        super.onResume();
       refreshUI();
       if(!firstTime){
           search("");
       }else{
           firstTime = false;
       }

    }

    public void setParentActivity(Activity activity){
        this.parentActivity = activity;
    }


    public void setUpControls(){
        llClients.setVisibility(clientControl?View.VISIBLE:View.GONE);
    }
    public void fillSpnStatus(){
        ArrayList<KV> list = new ArrayList<>();
        list.add(new KV(CODES.CODE_RECEIPT_STATUS_OPEN, "Abierto"));
        list.add(new KV(CODES.CODE_RECEIPT_STATUS_CLOSED, "Cerrado"));
        list.add(new KV(CODES.CODE_RECEIPT_STATUS_ANULATED, "Anulado"));
        spnStatus.setAdapter(new ArrayAdapter<KV>(parentActivity, android.R.layout.simple_list_item_1, list));
    }


    public void search(String text){
        pb.setVisibility(View.VISIBLE);
        dissableAll();
        AsyncTask<String, Void, ArrayList<ReceiptRowModel>> a = new AsyncTask<String, Void, ArrayList<ReceiptRowModel>>() {
            @Override
            protected ArrayList<ReceiptRowModel> doInBackground(String... strings) {
                String where="1 = 1 ";
                ArrayList<String> data = new ArrayList<>();
                if(lastStatus!= null && !lastStatus.getKey().equals("-1")){
                   where+=" AND r."+ReceiptController.STATUS+" = ?";
                   data.add(lastStatus.getKey());

                }
                if(clientControl && lastCheckClient && selectedClient!= null){
                    where+=" AND r."+ReceiptController.CODECLIENT+" = ? ";
                    data.add(selectedClient.getCode());
                }

                if(lastCheckDate && (ini != null && end != null)){
                    where+="AND (r."+ReceiptController.DATE+" >= ? AND r."+ReceiptController.DATE+" <= ? )";
                    data.add(Funciones.getFormatedDate(ini));data.add(Funciones.getFormatedDate(end));
                }
                //String value = strings[0];
                //String where = ClientsController.DOCUMENT+" like ? OR "+ ClientsController.NAME+" like ? OR "+ClientsController.PHONE+" like ? ";
                return ReceiptController.getInstance(parentActivity).getReceiptsRM(where, data.toArray(new String[data.size()]));
            }

            @Override
            protected void onPostExecute(ArrayList<ReceiptRowModel> clientRowModels) {
                super.onPostExecute(clientRowModels);
                pb.setVisibility(View.GONE);
                enableAll();
                ReceiptAdapter adapter = new ReceiptAdapter(parentActivity, (ListableActivity) parentActivity, clientRowModels);
                rvList.setAdapter(adapter);
                rvList.getAdapter().notifyDataSetChanged();
                rvList.invalidate();
            }
        };
        a.execute(text);

    }

    public void searchReceiptFromFireBase(){
        pb.setVisibility(View.VISIBLE);
        dissableAll();

        lastStatus = ((KV)spnStatus.getSelectedItem());
        lastCheckClient = cbClient.isChecked();
        lastCheckDate = cbDate.isChecked();

        String status =null;
        if(!lastStatus.getKey().equals("-1")){
            status = lastStatus.getKey();
        }
        String codeClient = null;
        if(selectedClient!= null && lastCheckClient){
            codeClient = selectedClient.getCode();
        }

        Date dateIniSearch = null;
        Date dateEndSearch = null;

        if(lastCheckDate){
            Calendar dateIni = Calendar.getInstance();
            Calendar dateEnd = Calendar.getInstance();
                String[]di = etDateIni.getText().toString().split("/");
                dateIni.set(Calendar.YEAR, Integer.parseInt(di[2]));dateIni.set(Calendar.MONTH, Integer.parseInt(di[1])-1);dateIni.set(Calendar.DAY_OF_MONTH, Integer.parseInt(di[0]));
                dateIni.set(Calendar.HOUR_OF_DAY, 0);dateIni.set(Calendar.MINUTE, 0);dateIni.set(Calendar.SECOND, 0);dateIni.set(Calendar.MILLISECOND, 0);
                ini = dateIni.getTime();

                String[]de = etDateEnd.getText().toString().split("/");
                dateEnd.set(Calendar.YEAR, Integer.parseInt(de[2]));dateEnd.set(Calendar.MONTH, Integer.parseInt(de[1])-1);dateEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(de[0]));
                dateEnd.set(Calendar.HOUR_OF_DAY, 23);dateEnd.set(Calendar.MINUTE, 59);dateEnd.set(Calendar.SECOND, 59);dateEnd.set(Calendar.MILLISECOND, 59);
                end = dateEnd.getTime();

            //validar que el rango de fhcas este correcto.
            if(Funciones.fechaMayorQue(ini, end)){
                pb.setVisibility(View.GONE);
                enableAll();
                Snackbar.make(getView(), "Rango de fechas invalido", Snackbar.LENGTH_LONG).show();
                return;
            }


        }else{
            ini = null;
            end = null;
        }

        ReceiptController.getInstance(parentActivity).searchReceiptFilteredFromFireBase(status, codeClient, dateIniSearch, dateEndSearch, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                ReceiptController.getInstance(parentActivity).delete(null, null);

                for(DocumentSnapshot ds: querySnapshot){
                    Receipts r = ds.toObject(Receipts.class);
                    //if(ReceiptController.getInstance(parentActivity).update(r) <= 0){
                        ReceiptController.getInstance(parentActivity).insert(r);
                    //}
                }

                search("");
            }
        }, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getException()!= null){
                    pb.setVisibility(View.GONE);
                    enableAll();
                    Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pb.setVisibility(View.GONE);
                enableAll();
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }





    public boolean validate(){
        KV status = (KV)spnStatus.getSelectedItem();
        if(status.getKey().equals(CODES.CODE_RECEIPT_STATUS_CLOSED) && !cbClient.isChecked() && !cbDate.isChecked()){
        Snackbar.make(getView(), "Seleccione al menos un filtro mas para buscar los cerrados",Snackbar.LENGTH_LONG).show();
        return false;
        }else if(cbClient.isChecked() && selectedClient == null){
            Snackbar.make(getView(), "Seleccione un cliente",Snackbar.LENGTH_LONG).show();
            return false;
        }else if(cbDate.isChecked() && (etDateIni.getText().toString().isEmpty() || etDateEnd.getText().toString().isEmpty())){
            Snackbar.make(getView(), "Especifique un rango valido de fechas.",Snackbar.LENGTH_LONG).show();
            return false;
        }


        return true;
    }


    public void searchClient(){
        FragmentTransaction ft = ((AppCompatActivity)parentActivity).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)parentActivity).getSupportFragmentManager().findFragmentByTag("dialogClientSearch");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment newFragment = null;

        newFragment = ClientSearchDialog.newInstance(parentActivity, this);

        // Create and show the dialog.
        newFragment.show(ft, "dialog");
    }


    public void showDatePicker(int id){
        lastDatePressed = id;
        Calendar c = Calendar.getInstance();
        DatePickerDialog a = new DatePickerDialog(parentActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);c.set(Calendar.MONTH, month);c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(lastDatePressed == etDateIni.getId()){
                    c.set(Calendar.HOUR_OF_DAY, 0);c.set(Calendar.MINUTE, 0);c.set(Calendar.SECOND, 0);c.set(Calendar.MILLISECOND, 0);
                    ini = c.getTime();
                   etDateIni.setText(Funciones.getFormatedDateRepDom(c.getTime()));
                }else if(lastDatePressed == etDateEnd.getId()){
                    etDateEnd.setText(Funciones.getFormatedDateRepDom(c.getTime()));
                    c.set(Calendar.HOUR_OF_DAY, 23);c.set(Calendar.MINUTE, 59);c.set(Calendar.SECOND, 59);c.set(Calendar.MILLISECOND, 59);
                    end = c.getTime();
                }

            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(c.DAY_OF_MONTH));
        a.show();
    }

    @Override
    public void dialogClosed(Object o) {
       selectedClient = (ClientRowModel)o;
       etClient.setText(selectedClient.getName());
    }


    public void dissableAll(){
    spnStatus.setEnabled(false);
    cbDate.setEnabled(false);
    cbClient.setEnabled(false);
    etDateIni.setEnabled(false);
    etDateEnd.setEnabled(false);
    btnSearch.setEnabled(false);
    btnSearchClients.setEnabled(false);
    }

    public void enableAll(){
        spnStatus.setEnabled(true);
        cbDate.setEnabled(true);
        cbClient.setEnabled(true);
        etDateIni.setEnabled(true);
        etDateEnd.setEnabled(true);
        btnSearch.setEnabled(true);
        btnSearchClients.setEnabled(true);


        if(!clientControl){
            if(((KV)spnStatus.getSelectedItem()).getKey().equals(CODES.CODE_RECEIPT_STATUS_CLOSED)){
                cbDate.setChecked(true);
                cbDate.setEnabled(false);
            }else{
                cbDate.setChecked(false);
                cbDate.setEnabled(true);
            }

        }
    }

    public void refreshUI(){
        cbClient.setChecked(lastCheckClient);
        if(selectedClient!= null){
            etClient.setText(selectedClient.getName());
        }
        cbDate.setChecked(lastCheckDate);
        String stringDi = Funciones.getFormatedDateRepDom(ini!= null?ini:new Date());
        String stringDe = Funciones.getFormatedDateRepDom(end!= null?end:new Date());
        etDateIni.setText(stringDi);
        etDateEnd.setText(stringDe);

        Calendar dateIni = Calendar.getInstance();
        Calendar dateEnd = Calendar.getInstance();

        String[]di = stringDi.split("/");
        dateIni.set(Calendar.YEAR, Integer.parseInt(di[2]));dateIni.set(Calendar.MONTH, Integer.parseInt(di[1])-1);dateIni.set(Calendar.DAY_OF_MONTH, Integer.parseInt(di[0]));
        dateIni.set(Calendar.HOUR_OF_DAY, 0);dateIni.set(Calendar.MINUTE, 0);dateIni.set(Calendar.SECOND, 0);dateIni.set(Calendar.MILLISECOND, 0);
        ini = dateIni.getTime();

        String[]de = stringDe.split("/");
        dateEnd.set(Calendar.YEAR, Integer.parseInt(de[2]));dateEnd.set(Calendar.MONTH, Integer.parseInt(de[1])-1);dateEnd.set(Calendar.DAY_OF_MONTH, Integer.parseInt(de[0]));
        dateEnd.set(Calendar.HOUR_OF_DAY, 23);dateEnd.set(Calendar.MINUTE, 59);dateEnd.set(Calendar.SECOND, 59);dateEnd.set(Calendar.MILLISECOND, 59);
        end = dateEnd.getTime();

        if(lastStatus != null){
            for(int i = 0; i < spnStatus.getAdapter().getCount(); i++){
                if(((KV)spnStatus.getAdapter().getItem(i)).getKey().equals(lastStatus.getKey())){
                    spnStatus.setSelection(i);
                    break;
                }
            }
        }
    }




}
