package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.NewOrderProductNoMeasureModel;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Dialogs.AddProductDialog;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.MainOrders;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;
import java.util.Date;

public class SalesRowNoMeasureAdapter extends RecyclerView.Adapter<SalesRowNoMeasureAdapter.SalesRowNoMeasureHolder> {

    Activity activity;
    ArrayList<NewOrderProductNoMeasureModel> objects;
    ListableActivity listableActivity;
    int lastPosition = 0;
    AddProductDialog productDialog;
    boolean productMeasureControl = false;
    public SalesRowNoMeasureAdapter(Activity act, ListableActivity la, ArrayList<NewOrderProductNoMeasureModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public SalesRowNoMeasureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SalesRowNoMeasureHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.sales_row1, parent, false));
    }

    @Override
    public void onBindViewHolder(final SalesRowNoMeasureHolder holder, final int position) {

        holder.fillData(objects.get(position));
        holder.setBackgroundColor(activity.getResources(), objects.get(position).isBlocked());

        final TextView etQuantity = holder.getEtQuantity();
        final CardView cvDelete =  holder.getCvDelete();


        if(TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProduct(
                objects.get(position).getCodeProduct()).size()>0) {
            cvDelete.setVisibility(View.VISIBLE);
        }else{
            cvDelete.setVisibility(View.GONE);
        }

        cvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                objects.get(position).setQuantity("0");
                objects.get(position).setManualPrice(null);

                cvDelete.setVisibility(View.GONE);
                etQuantity.setText("0");
                deleteOrderLine(objects.get(position));
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                callAddProductDialog(objects.get(position),holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    public void saveOrderLine(NewOrderProductNoMeasureModel opm){

        String code = Funciones.generateCode();
        String codeSale = ((MainOrders)activity).getOrderCode();
        String codeProduct = opm.getCodeProduct();
        int position = Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date()));
        double quantity = Double.parseDouble(opm.getQuantity());
        double tax = 0;
        double price = opm.getPrice();
        double manualPrice = opm.getManualPrice().isEmpty()?0:Double.parseDouble(opm.getManualPrice());
        double discount = 0;

        //String code,String codeSales, String codeProduct, String codeUnd,int position,double quantity,double price, double discount, double tax
        SalesDetails sd = new SalesDetails(code,codeSale,Funciones.getCodeuserLogged(activity), codeProduct, "", position, quantity, price,manualPrice,  discount, tax, "");
        TempOrdersController.getInstance(activity).insert_Detail(sd);
        ((MainOrders)activity).refreshResume();

    }

    public void updateOrderLine(SalesDetails sd){
        sd.setPOSITION(Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date())));
        TempOrdersController.getInstance(activity).update_Detail(sd);
        ((MainOrders)activity).refreshResume();
    }

    public void deleteOrderLine(NewOrderProductNoMeasureModel obj){

        String where = TempOrdersController.DETAIL_CODEPRODUCT+" = ?  ";
        String[]args = new String[]{obj.getCodeProduct()};
        TempOrdersController.getInstance(activity).
                delete_Detail(where, args);
        ((MainOrders)activity).refreshResume();
    }



    public void callAddProductDialog(NewOrderProductNoMeasureModel obj, SalesRowNoMeasureHolder holder){
        FragmentTransaction ft = ((AppCompatActivity)activity).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((AppCompatActivity)activity).getSupportFragmentManager().findFragmentByTag("AddProductDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        productDialog = AddProductDialog.newInstance(activity, obj, holder, this);
        // Create and show the dialog.
        productDialog.show(ft, "AddProductDialog");
    }

    public void EditLineFromExternal(NewOrderProductNoMeasureModel editedLine, SalesRowNoMeasureHolder holder){
        ArrayList<SalesDetails> details = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProduct(editedLine.getCodeProduct());
        SalesDetails sd =(details.size()>0)?details.get(0):null;
        if(sd == null){
            saveOrderLine(editedLine);
            holder.getCvDelete().setVisibility(View.VISIBLE);

        }else {
            double newQuantity = Double.parseDouble(editedLine.getQuantity());
            double manualPrice = Double.parseDouble(editedLine.getManualPrice());
            if (newQuantity > 0) {
                sd.setQUANTITY(newQuantity);
                sd.setMANUALPRICE(manualPrice);
                updateOrderLine(sd);
                editedLine.setQuantity((int)newQuantity+"");
            }
        }
        holder.getEtQuantity().setText(editedLine.getQuantity());

    }






    public class SalesRowNoMeasureHolder extends RecyclerView.ViewHolder {

        TextView tvName,tvQuantity, tvTotal;
        CardView cvDelete;
        LinearLayout llPadre;
        public SalesRowNoMeasureHolder(View itemView) {
            super(itemView);
            this.llPadre = itemView.findViewById(R.id.llParent);
            this.tvName = itemView.findViewById(R.id.tvName);
            this.tvQuantity = itemView.findViewById(R.id.tvQuantity);
            this.tvTotal = itemView.findViewById(R.id.tvTotal);
            this.cvDelete = itemView.findViewById(R.id.cvDelete);
        }

        public void fillData(NewOrderProductNoMeasureModel obj){
            this.tvName.setText(obj.getName());
            this.tvQuantity.setText(obj.getQuantity());
            this.tvTotal.setText("$"+Funciones.formatMoney(obj.getPrice()));

        }

        public void setBackgroundColor(Resources re, boolean isBloqued){
            if(isBloqued){
                llPadre.setBackgroundColor(re.getColor(R.color.red_200));
            }else{
                llPadre.setBackgroundColor(re.getColor(R.color.white));
            }
        }

        public CardView getCvDelete(){return cvDelete;}
        public TextView getEtQuantity(){ return tvQuantity; }
    }


}
