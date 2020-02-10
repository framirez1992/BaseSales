package com.far.basesales;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.CloudFireStoreObjects.Day;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.Transaction;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class DayFragment extends Fragment implements  OnSuccessListener<QuerySnapshot>, OnFailureListener {

    Activity parentActivity;
    LinearLayout llLoading, llDayStart, llDayEnd, llLoadingCloseDay;
    TextInputEditText etInitialDateStart, etStart, etEnd;
    TextView btnStartDay, btnEndDay, tvErrorMsg;
    EditText etSalesCount, etSalesAmount,etNetSalesAmount, etCashCount, etCashAmount, etCreditCount,etCreditAmount, etDiscount, etTotalPayments;

    int lastDatePressed;
    int lastFireBaseaction = 0;

    public DayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llLoading = view.findViewById(R.id.llLoading);
        llDayStart = view.findViewById(R.id.llDayStart);
        llDayEnd = view.findViewById(R.id.llDayEnd);
        llLoadingCloseDay = view.findViewById(R.id.llLoadingCloseDay);
        tvErrorMsg = view.findViewById(R.id.tvErrorMsg);


        etInitialDateStart = view.findViewById(R.id.etInitialDateStart);
        btnStartDay = view.findViewById(R.id.btnStartDay);

        btnEndDay = view.findViewById(R.id.btnEndDay);
        etStart = view.findViewById(R.id.etStart);
        etEnd = view.findViewById(R.id.etEnd);
        etSalesCount = view.findViewById(R.id.etSalesCount);
        etSalesAmount = view.findViewById(R.id.etSalesAmount);
        etNetSalesAmount = view.findViewById(R.id.etNetSalesAmount);
        etCashCount = view.findViewById(R.id.etCashCount);
        etCashAmount = view.findViewById(R.id.etCashAmount);
        etCreditCount = view.findViewById(R.id.etCreditCount);
        etCreditAmount = view.findViewById(R.id.etCreditAmount);
        etDiscount = view.findViewById(R.id.etDiscount);
        etTotalPayments = view.findViewById(R.id.etTotalPayments);

        btnStartDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            saveDay();
            }
        });
        btnEndDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DayController.getInstance(parentActivity).getCurrentOpenDay()!= null){
                    lastFireBaseaction = 1;
                    showWaitingCloseDay();
                    dissableCloseDayButtons();
                    execute();
                }
            }
        });

        etInitialDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });

        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        searchCurrentOpenDay();
    }

    public void setParentActivity(Activity activity){
        this.parentActivity = activity;
    }



    public void searchCurrentOpenDay(){
        llDayStart.setVisibility(View.GONE);
        llDayEnd.setVisibility(View.GONE);
        llLoading.setVisibility(View.VISIBLE);

        DayController.getInstance(parentActivity).searchCurrentDayStartedFromFireBase(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                Day day =  null;
                if(querySnapshot!= null && !querySnapshot.isEmpty()){
                    day = querySnapshot.getDocuments().get(0).toObject(Day.class);
                    if(DayController.getInstance(parentActivity).update(day) == 0){
                        DayController.getInstance(parentActivity).insert(day);
                    }
                }

                llLoading.setVisibility(View.GONE);
                if(day == null){
                  initNewDay();
                }else{
                 initStartedDay(day);
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(parentActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void searchCurrentCloseDay(Day day){
        DayController.getInstance(parentActivity).searchDayFromFireBase(day.getCode(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                Day day =  null;
                if(querySnapshot!= null && !querySnapshot.isEmpty()){
                    day = querySnapshot.getDocuments().get(0).toObject(Day.class);
                    if(day.getStatus().equals(CODES.CODE_DAY_STATUS_CLOSED)){
                        DayController.getInstance(parentActivity).delete(DayController.CODE+" = ?", new String[]{day.getCode()});
                    }
                }
                if(day == null){
                    Toast.makeText(parentActivity, "Error intentando cerrar el dia. Intente nuevamente", Toast.LENGTH_LONG).show();
                }else{
                    hideWaitingCloseDay();
                    setErrorText("");
                    enableCloseDayButtons();
                    initNewDay();
                }
                lastFireBaseaction = 0;
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(parentActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void initNewDay(){
        llDayEnd.setVisibility(View.GONE);
        llDayStart.setVisibility(View.VISIBLE);
        etInitialDateStart.setText(Funciones.getFormatedDateRepDom(new Date()));
    }

    public void initStartedDay(Day day){
        llLoading.setVisibility(View.GONE);
        llDayStart.setVisibility(View.GONE);
        llDayEnd.setVisibility(View.VISIBLE);

        etStart.setText(Funciones.getFormatedDateRepDom(day.getDatestart()));
        etEnd.setText(Funciones.getFormatedDateRepDom(new Date()));
        etSalesCount.setText(day.getSalescount()+"");
        etSalesAmount.setText("$"+Funciones.formatMoney(day.getSalesamount()));
        etNetSalesAmount.setText("$"+Funciones.formatMoney(day.getSalesamount() - day.getDiscountamount()));
        etCashCount.setText((int)day.getCashpaidcount()+"");
        etCashAmount.setText("$"+Funciones.formatMoney(day.getCashpaidamount()));
        etCreditCount.setText((int)day.getCreditpaidcount()+"");
        etCreditAmount.setText("$"+Funciones.formatMoney(day.getCreditpaidamount()));
        etDiscount.setText("$"+Funciones.formatMoney(day.getDiscountamount()));
        etTotalPayments.setText("$"+Funciones.formatMoney(day.getCreditpaidamount()+ day.getCashpaidamount()));
    }




    public void showDatePicker(View v){
        lastDatePressed = v.getId();

        Calendar c = Calendar.getInstance();
            try{
               String date = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
            }catch (Exception e){

            }

        DatePickerDialog a = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                c.set(Calendar.YEAR, year);c.set(Calendar.MONTH, month);c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                if(lastDatePressed == etInitialDateStart.getId()){
                    etInitialDateStart.setText(sdf.format(c.getTime()));
                }else if(lastDatePressed == etStart.getId()){
                    etStart.setText(sdf.format(c.getTime()));
                }else if(lastDatePressed == etEnd.getId()){
                    etEnd.setText(sdf.format(c.getTime()));
                }

            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(c.DAY_OF_MONTH));
        a.show();
    }


    public void saveDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateStart = new Date();
        try{
            dateStart = sdf.parse(etInitialDateStart.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        Day day = new Day(Funciones.generateCode(), Funciones.getCodeuserLogged(parentActivity), dateStart, null, CODES.CODE_DAY_STATUS_OPEN,
                0, 0.0, 0, 0.0, 0, 0.0, 0.0,
                0, 0.0,0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, "");

        DayController.getInstance(parentActivity).sendToFireBase(day,  new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Toast.makeText(parentActivity, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        searchCurrentOpenDay();
    }

    public void execute(){
        switch (lastFireBaseaction){
            case 1:SalesController.getInstance(parentActivity).searchAllSalesFromFireBase( this, this); break;
            case 2: SalesController.getInstance(parentActivity).searchAllSalesDetailFromFireBase(this, this); break;
            case 3: ReceiptController.getInstance(parentActivity).searchAllReceiptsFromFireBase( this, this); break;
            case 4: PaymentController.getInstance(parentActivity).searchAllPaymentsFromFireBase( this, this); break;
            case 5: Transaction.getInstance(parentActivity).deleteDataFromFireBase( this);//los delete y los insert no retornan OnSucess si no hay conexion.
                Transaction.getInstance(parentActivity).deleteLocalData();

                Date dateEnd = new Date();
                try {
                    dateEnd = new SimpleDateFormat("dd/MM/yyyy").parse(etEnd.getText().toString());
                }catch (Exception e){e.getMessage();}

                Day day = DayController.getInstance(parentActivity).getCurrentOpenDay();
                day.setStatus(CODES.CODE_DAY_STATUS_CLOSED);
                day.setDateend(dateEnd);
                day.setMdate(null);
                DayController.getInstance(parentActivity).sendToFireBase(day, this);
                searchCurrentCloseDay(day);
                break;
            default:break;
        }
    }


    @Override
    public void onFailure(@NonNull Exception e) {
        hideWaitingCloseDay();
        setErrorText(e.getMessage());
        enableCloseDayButtons();
    }


    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
            switch (lastFireBaseaction){
                case 1: SalesController.getInstance(parentActivity).consumeQuerySnapshot(querySnapshot); break;
                case 2: SalesController.getInstance(parentActivity).consumeQuerySnapshotDetail(querySnapshot); break;
                case 3: ReceiptController.getInstance(parentActivity).consumeQuerySnapshot(querySnapshot); break;
                case 4: PaymentController.getInstance(parentActivity).consumeQuerySnapshot(querySnapshot); break;
                default:break;
            }
            lastFireBaseaction++;
            execute();

    }

    public void enableCloseDayButtons(){
        etStart.setEnabled(true);
        etEnd.setEnabled(true);
        btnEndDay.setEnabled(true);
    }

    public void dissableCloseDayButtons(){
        etStart.setEnabled(false);
        etEnd.setEnabled(false);
        btnEndDay.setEnabled(false);
    }

    public void showWaitingCloseDay(){
        llLoadingCloseDay.setVisibility(View.VISIBLE);
    }
    public void hideWaitingCloseDay(){
        llLoadingCloseDay.setVisibility(View.INVISIBLE);
    }

    public void setErrorText(String msg){
        tvErrorMsg.setText(msg);
    }

}
