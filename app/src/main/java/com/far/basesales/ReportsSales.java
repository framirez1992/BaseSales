package com.far.basesales;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.DayAdapter;
import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsSales extends Fragment implements OnCompleteListener, OnSuccessListener<QuerySnapshot>, OnFailureListener {

    Activity parentActivity;
    Spinner spnMonth, spnYear;
    CardView btnSearch;
    TextView tvSalesTotalAmount, tvSalesTotalDiscount, tvSalesNetAmount, tvSalesCashAmount, tvCreditAmount, tvTotalPayments;

    RecyclerView rvList;
    ProgressBar pb;
    CardView cvGeneral, cvDetalle;
    RelativeLayout rlList;
    LinearLayout llGeneral;
    int lastOption;

    public ReportsSales() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reports_sales, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spnMonth = view.findViewById(R.id.spnMonth);
        spnYear  = view.findViewById(R.id.spnYear);
        btnSearch = view.findViewById(R.id.btnSearch);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));
        pb = view.findViewById(R.id.pb);
        cvGeneral = view.findViewById(R.id.cvGeneral);
        cvDetalle = view.findViewById(R.id.cvDetalle);
        rlList = view.findViewById(R.id.rlList);
        llGeneral = view.findViewById(R.id.llGeneral);
        tvSalesTotalAmount = view.findViewById(R.id.tvSalesTotalAmount);
        tvSalesTotalDiscount = view.findViewById(R.id.tvSalesTotalDiscount);
        tvSalesNetAmount = view.findViewById(R.id.tvSalesNetAmount);
        tvSalesCashAmount = view.findViewById(R.id.tvSalesCashAmount);
        tvCreditAmount = view.findViewById(R.id.tvCreditAmount);
        tvTotalPayments = view.findViewById(R.id.tvTotalPayments);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            search();
            }
        });

        cvGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeList(v);
            }
        });

        cvDetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeList(v);
            }
        });

        changeList(cvGeneral);

        fillSpnMonth();
        fillSpnYear();
    }

    public void setParentActivity(Activity activity){
        this.parentActivity = activity;
    }
    public void search(){
        pb.setVisibility(View.VISIBLE);
        disableAll();

        int year = Integer.parseInt(((KV)spnYear.getSelectedItem()).getKey());
        int month = Integer.parseInt(((KV)spnMonth.getSelectedItem()).getKey());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month-1);//Calendar.MONTH 0 = enero
        c.set(Calendar.DAY_OF_MONTH, 1);

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, year);
        c2.set(Calendar.MONTH, month-1);
        c2.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        DayController.getInstance(parentActivity).searchDaysRangeFromFireBase(c.getTime(), c2.getTime(), this, this, this);
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if(task.getException()!= null){
            pb.setVisibility(View.GONE);
            enableAll();
            Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        pb.setVisibility(View.GONE);
        enableAll();
        Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {

        if(querySnapshot!= null && querySnapshot.size() > 0){
            DayController.getInstance(parentActivity).delete(null, null);

            for(DocumentSnapshot doc : querySnapshot){
                Day d = doc.toObject(Day.class);
                if(DayController.getInstance(parentActivity).update(d) <=0){
                    DayController.getInstance(parentActivity).insert(d);
                }
            }
        }

        refreshData();
    }

    public void fillSpnMonth(){
        ArrayList<KV> list = new ArrayList<>();
        list.add(new KV("01", "Enero"));
        list.add(new KV("02", "Febrero"));
        list.add(new KV("03", "Marzo"));
        list.add(new KV("04", "Abril"));
        list.add(new KV("05", "Mayo"));
        list.add(new KV("06", "Junio"));
        list.add(new KV("07", "Julio"));
        list.add(new KV("08", "Agosto"));
        list.add(new KV("09", "Septiembre"));
        list.add(new KV("10", "Octubre"));
        list.add(new KV("11", "Noviembre"));
        list.add(new KV("12", "Diciembre"));
        spnMonth.setAdapter(new ArrayAdapter<KV>(parentActivity, android.R.layout.simple_list_item_1, list));
        spnMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));
    }

    public void fillSpnYear(){
        Licenses l = LicenseController.getInstance(parentActivity).getLicense();
        Calendar c = Calendar.getInstance();
        c.setTime(l.getDATEINI());
        int yearCreated = c.get(Calendar.YEAR);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        ArrayList<KV> list = new ArrayList<>();
        list.add(new KV(""+yearCreated, ""+yearCreated));
        if(currentYear > yearCreated){
            while (yearCreated<currentYear){
                yearCreated++;
                list.add(new KV(""+yearCreated, ""+yearCreated));
            }
        }
        //list.add(new KV(""+currentYear, ""+currentYear));
        spnYear.setAdapter(new ArrayAdapter<KV>(parentActivity, android.R.layout.simple_list_item_1, list));
    }

    public void disableAll(){
        btnSearch.setEnabled(false);
        spnMonth.setEnabled(false);
        spnYear.setEnabled(false);
    }

    public void enableAll(){
        btnSearch.setEnabled(true);
        spnMonth.setEnabled(true);
        spnYear.setEnabled(true);
    }



    public void changeList(View v){

        lastOption = v.getId();

        cvDetalle.setCardBackgroundColor(getResources().getColor(R.color.gray_200));
        ((TextView)cvDetalle.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_view));

        cvGeneral.setCardBackgroundColor(getResources().getColor(R.color.gray_200));
        ((TextView)cvGeneral.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_view));

        ((CardView)v).setCardBackgroundColor(getResources().getColor(R.color.white));
        ((TextView)((CardView) v).getChildAt(0)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));


        rlList.setVisibility(v.getId()== R.id.cvDetalle?View.VISIBLE:View.GONE);
        llGeneral.setVisibility(v.getId()== R.id.cvGeneral?View.VISIBLE:View.GONE);

    }


    public void refreshData() {

        String year = ((KV)spnYear.getSelectedItem()).getKey();
        String month = ((KV)spnMonth.getSelectedItem()).getKey();
        String date = year+month;

        Day generalDay = DayController.getInstance(parentActivity).getGeneralDay(year, month);
        tvSalesTotalAmount.setText("$"+Funciones.formatMoney(generalDay.getSalesamount()));
        tvSalesTotalDiscount.setText("$"+Funciones.formatMoney(generalDay.getDiscountamount()));
        tvSalesNetAmount.setText("$"+Funciones.formatMoney(generalDay.getSalesamount() - generalDay.getDiscountamount()));
        tvSalesCashAmount.setText("$"+Funciones.formatMoney(generalDay.getCashpaidamount()));
        tvCreditAmount.setText("$"+Funciones.formatMoney(generalDay.getCreditpaidamount()));
        tvTotalPayments.setText("$"+Funciones.formatMoney(generalDay.getCreditpaidamount() + generalDay.getCashpaidamount()));


        pb.setVisibility(View.GONE);
        enableAll();

        DayAdapter adapter = new DayAdapter(parentActivity, DayController.getInstance(parentActivity).getDays(DayController.STATUS+" = ? AND  SUBSTR("+DayController.DATESTART+", 1,6) = ?", new String[]{CODES.CODE_DAY_STATUS_CLOSED, date}, null));
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();
    }



}
