package com.far.basesales;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.OrderDetailModel;
import com.far.basesales.Adapters.OrderResumeAdapter;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResumenOrderFragment extends Fragment {


    RecyclerView rvList;
    LinearLayout  llMore, llCancel, llPay;
    TempOrdersController tempOrdersController;
    SalesController salesController;
    TextInputEditText etNotas;
    ImageView imgMore;
    LinearLayout llGoMenu;
    TextView tvTotal;

    MainOrders parentActivity;
    boolean fragmentCreated;

    public ResumenOrderFragment() {
        // Required empty public constructor
    }
    public void setParent(MainOrders parent){
        this.parentActivity = parent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tempOrdersController = TempOrdersController.getInstance(parentActivity);
        salesController = SalesController.getInstance(parentActivity);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resume_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(View v){
        llCancel = v.findViewById(R.id.llCancel);
        llPay = v.findViewById(R.id.llPay);
        rvList = v.findViewById(R.id.rvResultList);
        etNotas = v.findViewById(R.id.etNotas);
        imgMore = v.findViewById(R.id.imgMore);
        llMore = v.findViewById(R.id.llMore);
        llGoMenu = v.findViewById(R.id.llGoMenu);
        tvTotal = v.findViewById(R.id.tvTotal);

        if(llGoMenu != null) {
            llGoMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToMenu();
                }
            });
        }

        rvList.setLayoutManager(new LinearLayoutManager(parentActivity));

        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(llMore.getVisibility() == View.GONE){
                   llMore.setVisibility(View.VISIBLE);
                   imgMore.setImageResource(R.drawable.ic_arrow_drop_up);
                }else{
                    llMore.setVisibility(View.GONE);
                    imgMore.setImageResource(R.drawable.ic_arrow_drop_down);
                }
            }
        });

     llPay.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             goToPayment();
         }
     });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llCancel.setVisibility(View.GONE);
                parentActivity.prepareNewOrder();
                //parentActivity.setThemeNormal();
                parentActivity.refresh();
            }
        });

        parentActivity.refreshResume();
        fragmentCreated = true;
    }



    public boolean validate(){
        if(rvList.getAdapter() == null ||((OrderResumeAdapter)rvList.getAdapter()).hasBlockedProducts()){
            Snackbar.make(getView(), "No puede realizar la orden con productos NO DISPONIBLES.", Snackbar.LENGTH_LONG).show();
            return false;
        } else if(rvList.getAdapter() == null ||rvList.getAdapter().getItemCount() ==0){
            Snackbar.make(getView(), "Debe seleccionar al menos 1 item", Snackbar.LENGTH_LONG).show();
            return false;
        }else if(!validQuantitys()){
            Snackbar.make(getView(), "Las cantidades deben ser mayor que 0", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
    public boolean validQuantitys(){
       ArrayList<OrderDetailModel> items  = tempOrdersController.getOrderDetailModels((parentActivity).getOrderCode());
       for(OrderDetailModel odm : items){
           if(odm.getQuantity().equals("") || odm.getQuantity().equals(".") || odm.getQuantity().equalsIgnoreCase("0")){
         return false;
           }
       }

       return true;
    }




    public void refreshList(){
            OrderResumeAdapter adapter = new OrderResumeAdapter(parentActivity, parentActivity, TempOrdersController.getInstance(parentActivity).getOrderDetailModels(((parentActivity).getOrderCode())));
            rvList.setAdapter(adapter);
            rvList.getAdapter().notifyDataSetChanged();
            rvList.invalidate();
    }

    public void refreshTotal(){
        tvTotal.setText("$"+Funciones.formatMoney(TempOrdersController.getInstance(parentActivity).getSumPrice()));

    }

    public void prepareResumeForEdition(){
        llCancel.setVisibility(View.VISIBLE);
        Sales s = TempOrdersController.getInstance(parentActivity).getTempSale();

    }


    public void goToMenu(){
        parentActivity.showMenu();
        parentActivity.refreshProductsSearch(0);
    }

    public void goToPayment(){
        parentActivity.showReceipt();
    }

    public void setSelection(int pos){
        rvList.scrollToPosition(pos);
    }


    public boolean isFragmentCreated(){
        return fragmentCreated;
    }
}
