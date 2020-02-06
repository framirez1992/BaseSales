package com.far.basesales.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.OrderDetailModel;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Dialogs.AddProductDialog;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.MainOrders;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;
import java.util.Date;

public class OrderResumeNoMeasureAdapter extends RecyclerView.Adapter<OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder> {
    ArrayList<OrderDetailModel> objects;
    ListableActivity listableActivity;
    Activity activity;
    AddProductDialog productDialog;

    public OrderResumeNoMeasureAdapter(Activity act, ListableActivity la, ArrayList<OrderDetailModel> objs){
        this.activity = act;
        this.listableActivity = la;
        this.objects = objs;
    }
    @NonNull
    @Override
    public OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder(((LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.order_resume_row ,parent, false));
    }

    @Override
    public void onBindViewHolder(final OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder holder, final int position) {

        holder.fillData(objects.get(position));
        final Button btnLess =  holder.getBtnLess();
        final Button btnMore = holder.getBtnMore();
        final EditText etQuantity = holder.getEtCantidad();

        holder.setBackgroundColor(activity.getResources(), objects.get(position).isBlocked());
        holder.getImgMenu().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.registerForContextMenu(v);
                v.showContextMenu();
                listableActivity.onClick(objects.get(position));
            }
        });

        btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProduct(
                        objects.get(position).getCodeProduct()).get(0);

                if(sd == null){
                    return;
                }
                double newQuantity = sd.getQUANTITY() - 1;
                if(newQuantity <= 0){
                    objects.get(position).setQuantity("0");
                    deleteOrderLine(objects.get(position), position);
                }else{
                    objects.get(position).setQuantity(""+newQuantity);
                    sd.setQUANTITY(newQuantity);
                    updateOrderLine(sd);
                    etQuantity.setText(""+(int)newQuantity);
                }

                ((MainOrders)activity).refreshResume();

            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProduct(
                        objects.get(position).getCodeProduct()).get(0);

                if(sd == null){
                    return;
                }
                double newQuantity = sd.getQUANTITY() + 1;
                if(newQuantity > 0){
                    sd.setQUANTITY(newQuantity);
                    updateOrderLine(sd);
                    etQuantity.setText(""+(int)newQuantity);
                }

                ((MainOrders)activity).refreshResume();
            }
        });

        etQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAddProductDialog(objects.get(position), holder);
            }
        });


    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void updateOrderLine(SalesDetails sd){
        sd.setPOSITION(Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date())));
        TempOrdersController.getInstance(activity).update_Detail(sd);

        ((MainOrders)activity).refreshProductsSearch(0);
    }

    public void deleteOrderLine(OrderDetailModel obj, int lastPos){

        String where = TempOrdersController.DETAIL_CODE+" = ?";
        String[]args = new String[]{obj.getCode()};
        TempOrdersController.getInstance(activity).delete_Detail(where, args);

        ((MainOrders)activity).refreshResume();
        ((MainOrders)activity).refreshProductsSearch(0);
        ((MainOrders)activity).setResumeSelection(lastPos);


    }

    public boolean hasBlockedProducts(){
        for(OrderDetailModel od: objects){
            if(od.isBlocked()){
                return true;
            }
        }
        return false;
    }


    public void callAddProductDialog(OrderDetailModel obj, OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder holder){
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

    public void EditLineFromExternal(OrderDetailModel editedLine, OrderResumeNoMeasureAdapter.OrderResumeNoMeasureHolder holder) {
        SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProduct(editedLine.getCodeProduct()).get(0);
        if (sd == null) {
            return;

        } else {
            double newQuantity = Double.parseDouble(editedLine.getQuantity());
            double manualPrice = Double.parseDouble(editedLine.getManualPrice());
            if (newQuantity > 0) {
                sd.setQUANTITY(newQuantity);
                sd.setMANUALPRICE(manualPrice);
                updateOrderLine(sd);
                holder.getEtCantidad().setText("" + (int) newQuantity);
            }

            ((MainOrders)activity).refreshResume();
        }
    }




    public class OrderResumeNoMeasureHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        EditText etCantidad;
        Button btnLess, btnMore;
        ImageView imgMenu;
        LinearLayout llPadre;
        public OrderResumeNoMeasureHolder(View itemView) {
            super(itemView);
            llPadre = itemView.findViewById(R.id.llParent);
            tvName = itemView.findViewById(R.id.tvName);
            etCantidad = itemView.findViewById(R.id.etQuantity);
            imgMenu = itemView.findViewById(R.id.imgMenu);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnLess = itemView.findViewById(R.id.btnLess);
        }

        public void fillData(OrderDetailModel od){
            tvName.setText(od.getProduct_name());
            etCantidad.setText(od.getQuantity());
            if(od.isBlocked()){
                btnMore.setEnabled(false);
                btnLess.setEnabled(false);
            }else{
                btnMore.setEnabled(true);
                btnLess.setEnabled(true);
            }
        }

        public void setBackgroundColor(Resources re, boolean isBloqued){
            if(isBloqued){
                llPadre.setBackgroundColor(re.getColor(R.color.red_200));
            }else{
                llPadre.setBackgroundColor(re.getColor(R.color.white));
            }
        }

        public ImageView getImgMenu() {
            return imgMenu;
        }

        public Button getBtnLess() {
            return btnLess;
        }

        public Button getBtnMore() {
            return btnMore;
        }

        public EditText getEtCantidad() {
            return etCantidad;
        }
    }
}
