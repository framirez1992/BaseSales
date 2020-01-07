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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.far.basesales.Adapters.Models.NewOrderProductModel;
import com.far.basesales.CloudFireStoreObjects.ProductsMeasure;
import com.far.basesales.CloudFireStoreObjects.SalesDetails;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.TempOrdersController;
import com.far.basesales.Dialogs.AddProductDialog;
import com.far.basesales.Generic.KV;
import com.far.basesales.Generic.KV2;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.MainOrders;
import com.far.basesales.R;
import com.far.basesales.Utils.Funciones;

import java.util.ArrayList;
import java.util.Date;

public class NewOrderProductRowAdapter extends RecyclerView.Adapter<NewOrderProductRowAdapter.NewOrderProductHolder> {

    Activity activity;
    ArrayList<NewOrderProductModel> objects;
    ListableActivity listableActivity;
    int lastPosition = 0;
    AddProductDialog productDialog;
    public NewOrderProductRowAdapter(Activity act, ListableActivity la, ArrayList<NewOrderProductModel> objs){
        this.activity = act;
        this.objects = objs;
        this.listableActivity = la;
    }
    @NonNull
    @Override
    public NewOrderProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new NewOrderProductHolder(((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.product_row_new_order, parent, false));
    }

    @Override
    public void onBindViewHolder(final NewOrderProductHolder holder, final int position) {

        ArrayAdapter adapter = null;
        if( objects.get(position).getMeasures() != null &&  objects.get(position).getMeasures().size() >0){
            adapter = new ArrayAdapter<KV2>(activity, android.R.layout.simple_list_item_1,objects.get(position).getMeasures());
        }
        holder.fillData(objects.get(position), adapter);
        holder.setBackgroundColor(activity.getResources(), objects.get(position).isBlocked());

        final EditText etQuantity = holder.getEtQuantity();
        final Button btnLess = holder.getBtnLess();
        final Button btnMore = holder.getBtnMore();
        final Spinner spnUnitMeasure =holder.getSpnUnitMeasure();
        final ImageView imgDelete =  holder.getImgDelete();


        if(TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                objects.get(position).getCodeProduct(),
                objects.get(position).getMeasure())!= null) {

            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.teal_A100));
            imgDelete.setVisibility(View.VISIBLE);
        }else{
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            imgDelete.setVisibility(View.GONE);
        }

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastPosition = position;
                objects.get(position).setQuantity("0");
                objects.get(position).setManualPrice(null);

                btnLess.setEnabled(false);
                imgDelete.setVisibility(View.GONE);
                etQuantity.setText("0");
                deleteOrderLine(objects.get(position));
                moveSpnMeasure(objects.get(position), spnUnitMeasure);
            }
        });
        btnLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se inhabilita el boton para evitar que el usuario clicke muy rapido y genere inconsistencias en el sistema.
                v.setEnabled(false);

                lastPosition = position;

                KV measure = (KV)spnUnitMeasure.getSelectedItem();
                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        measure.getKey());
                if(sd == null){
                    return;
                }
                double newQuantity = sd.getQUANTITY() - 1;
                if(newQuantity <= 0){
                    imgDelete.setVisibility(View.GONE);
                    objects.get(position).setQuantity("0");
                    objects.get(position).setManualPrice(null);

                    deleteOrderLine(objects.get(position));
                    moveSpnMeasure(objects.get(position), spnUnitMeasure);
                }else{
                    objects.get(position).setQuantity(""+(int)newQuantity);
                    sd.setQUANTITY((int)newQuantity);
                    updateOrderLine(sd);
                }
                etQuantity.setText(objects.get(position).getQuantity());
                v.setEnabled(newQuantity>0); //se habilita nuevamente el boton

            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se inhabilita el boton para evitar que el usuario clicke muy rapido y genere inconsistencias en el sistema.
                v.setEnabled(false);
                lastPosition = position;

                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        objects.get(position).getMeasure());
                if(sd == null){
                    objects.get(position).setQuantity("1");
                    saveOrderLine(objects.get(position));
                    btnLess.setEnabled(true);//si se presiona el boton Less cuando el item ya es 0, se bloquea. por esto agrego esta linea, para que siempre la primera vez que se agregue el item se habilite el Less.
                    imgDelete.setVisibility(View.VISIBLE);

                }else {
                    double newQuantity = sd.getQUANTITY() + 1;
                    if (newQuantity > 0) {
                        sd.setQUANTITY(newQuantity);
                        updateOrderLine(sd);
                        objects.get(position).setQuantity((int)newQuantity+"");
                    }
                }
                etQuantity.setText(objects.get(position).getQuantity());
                v.setEnabled(true); //Habilitando el boton nuevamente para su uso.
            }
        });


        spnUnitMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                KV measure = ((KV)parent.getSelectedItem());
                objects.get(position).setMeasure(measure.getKey());
                SalesDetails sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(
                        objects.get(position).getCodeProduct(),
                        measure.getKey());
                if(sd == null){
                    etQuantity.setText("0");
                    btnLess.setEnabled(false);
                    imgDelete.setVisibility(View.GONE);
                }else{
                    etQuantity.setText((int)sd.getQUANTITY()+"");
                    btnLess.setEnabled(true);
                    imgDelete.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etQuantity.setOnClickListener(new View.OnClickListener() {
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


    public void saveOrderLine(NewOrderProductModel opm){
        ProductsMeasure pm = ProductsMeasureController.getInstance(activity).getProductsMeasure(ProductsMeasureController.CODEMEASURE+"=? AND "+ProductsMeasureController.CODEPRODUCT+" = ?",
                new String[]{opm.getMeasure(), opm.getCodeProduct()})
                .get(0);

        String code = Funciones.generateCode();
        String codeSale = ((MainOrders)activity).getOrderCode();
        String codeProduct = opm.getCodeProduct();
        String codeUnd = opm.getMeasure();
        int position = Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date()));
        double quantity = Double.parseDouble(opm.getQuantity());
        double tax = 0;
        double price =pm.getPRICE();
        double manualPrice = opm.getManualPrice().isEmpty()?0:Double.parseDouble(opm.getManualPrice());
        double discount = 0;
        //String code,String codeSales, String codeProduct, String codeUnd,int position,double quantity,double price, double discount, double tax
        SalesDetails sd = new SalesDetails(code,codeSale,Funciones.getCodeuserLogged(activity), codeProduct, codeUnd, position, quantity, price,manualPrice, discount, tax, "");
        TempOrdersController.getInstance(activity).insert_Detail(sd);
        ((MainOrders)activity).refreshResume();

    }

    public void updateOrderLine(SalesDetails sd){
       sd.setPOSITION(Integer.parseInt(Funciones.getSimpleTimeFormat().format(new Date())));
       TempOrdersController.getInstance(activity).update_Detail(sd);
       ((MainOrders)activity).refreshResume();
    }

    public void deleteOrderLine(NewOrderProductModel obj){

        String where = TempOrdersController.DETAIL_CODEPRODUCT+" = ? AND "+TempOrdersController.DETAIL_CODEUND+" = ? ";
        String[]args = new String[]{obj.getCodeProduct(), obj.getMeasure()};
        TempOrdersController.getInstance(activity).
                delete_Detail(where, args);
        ((MainOrders)activity).refreshResume();
    }

    public void moveSpnMeasure(NewOrderProductModel obj, Spinner measure){
        ArrayList<SalesDetails> sd = TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProduct(obj.getCodeProduct());
        if(sd != null && sd.size() > 0){
            SalesDetails detail = sd.get(0);
            for(int i=0; i<measure.getAdapter().getCount(); i++){
                if(((KV)measure.getItemAtPosition(i)).getKey().equals(detail.getCODEUND())){
                    measure.setSelection(i);
                    break;
                }
            }
        }

    }


    public void callAddProductDialog(NewOrderProductModel obj, NewOrderProductHolder holder){
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

    public void EditLineFromExternal(NewOrderProductModel editedLine, NewOrderProductHolder holder){
        SalesDetails sd =TempOrdersController.getInstance(activity).getTempSaleDetailByCodeProductAndCodeMeasure(editedLine.getCodeProduct(), editedLine.getMeasure());
            if(sd == null){
                saveOrderLine(editedLine);
                holder.getBtnLess().setEnabled(true);//si se presiona el boton Less cuando el item ya es 0, se bloquea. por esto agrego esta linea, para que siempre la primera vez que se agregue el item se habilite el Less.
                holder.getImgDelete().setVisibility(View.VISIBLE);

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






    public class NewOrderProductHolder extends RecyclerView.ViewHolder {

        TextView tvDescription;
        EditText etQuantity;
        Button btnLess, btnMore;
        Spinner spnUnitMeasure;
        ImageView imgDelete;
        LinearLayout llPadre;
        public NewOrderProductHolder(View itemView) {
            super(itemView);
            this.llPadre = itemView.findViewById(R.id.llParent);
            this.tvDescription = itemView.findViewById(R.id.tvDescription);
            this.etQuantity = itemView.findViewById(R.id.etQuantity);
            this.spnUnitMeasure = itemView.findViewById(R.id.spnUnitMeasure);
            this.imgDelete = itemView.findViewById(R.id.imgDelete);
            this.btnLess = itemView.findViewById(R.id.btnLess);
            this.btnMore = itemView.findViewById(R.id.btnMore);
        }

        public void fillData(NewOrderProductModel obj, ArrayAdapter<KV> adapter){
            this.tvDescription.setText(obj.getName());
            this.etQuantity.setText(obj.getQuantity());
            this.spnUnitMeasure.setAdapter(adapter);
            this.btnMore.setEnabled(!obj.isBlocked());
            this.btnLess.setEnabled(!obj.isBlocked());
            this.spnUnitMeasure.setEnabled(!obj.isBlocked());

            if(adapter != null){
                spnUnitMeasure.setVisibility(View.VISIBLE);
                spnUnitMeasure.setAdapter(adapter);
//moviendo el spinner a la unidad de medida por defecto del producto.
                for (int i = 0; i < obj.getMeasures().size(); i++) {
                    if ((obj.getMeasures().get(i)).getKey().equals(obj.getMeasure())) {
                        spnUnitMeasure.setSelection(i);
                        break;
                    }
                }

            }else{
                spnUnitMeasure.setVisibility(View.INVISIBLE);
            }


        }

        public void setBackgroundColor(Resources re, boolean isBloqued){
            if(isBloqued){
                llPadre.setBackgroundColor(re.getColor(R.color.red_200));
            }else{
                llPadre.setBackgroundColor(re.getColor(R.color.white));
            }
        }


        public ImageView getImgDelete() {
            return imgDelete;
        }

        public Button getBtnLess() {
            return btnLess;
        }

        public Button getBtnMore() {
            return btnMore;
        }

        public Spinner getSpnUnitMeasure() {
            return spnUnitMeasure;
        }

        public EditText getEtQuantity() {
            return etQuantity;
        }
    }


}
