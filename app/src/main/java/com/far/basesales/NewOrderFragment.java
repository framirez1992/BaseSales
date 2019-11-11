package com.far.basesales;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.far.basesales.Adapters.NewOrderProductRowAdapter;
import com.far.basesales.CloudFireStoreObjects.Sales;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Generic.KV;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewOrderFragment extends Fragment {


    Spinner spnFamilia, spnGrupo;
    RecyclerView rvList;
    LinearLayout llGoResumen;

    String lastType = null;
    String lastSubType = null;
    String lastSeach = null;
    MainOrders parentActivity;

    public NewOrderFragment() {
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_new_order, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public void init(View v){

        llGoResumen = v.findViewById(R.id.llGoResumen);
        spnFamilia = v.findViewById(R.id.spnFamilia);
        spnGrupo = v.findViewById(R.id.spnGrupo);
        rvList = v.findViewById(R.id.rvProductsList);
        rvList.setLayoutManager(new LinearLayoutManager(this.getContext()));


        if(llGoResumen != null) {
            llGoResumen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailt();
                }
            });
        }

         spnFamilia.setOnItemSelectedListener(spnFamiliaListener);
         spnGrupo.setOnItemSelectedListener(spnGrupoListener);


        ProductsTypesController.getInstance(parentActivity).fillSpinner(spnFamilia, false);
    }

    AdapterView.OnItemSelectedListener spnFamiliaListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lastType = ((KV)parent.getItemAtPosition(position)).getKey();
            ProductsSubTypesController.getInstance(parentActivity).fillSpinner(spnGrupo,false,lastType);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener spnGrupoListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            lastSeach = null;
            lastSubType = ((KV)parent.getItemAtPosition(position)).getKey();
            search();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void search(){

        String where = "1 = 1 ";
        String[]args = null;
        ArrayList<String> x = new ArrayList();


            if (lastSeach != null && !searchBlocked()) {
                where += "AND p." + ProductsController.DESCRIPTION + " like ?";
                x.add("%"+lastSeach+"%");
            }else {
                if (lastType != null && lastType != "0") {
                    where += "AND p." + ProductsController.TYPE + " = ?";
                    x.add(lastType);
                }
                if (lastSubType != null && lastSubType != "0") {
                    where += "AND p." + ProductsController.SUBTYPE + " = ?";
                    x.add(lastSubType);
                }
            }


        if(x.size() >0)
        args = x.toArray(new String[x.size()]);

        NewOrderProductRowAdapter adapter = new NewOrderProductRowAdapter(parentActivity, parentActivity,ProductsController.getInstance(parentActivity).getNewProductRowModels(where, args, null) );
        rvList.setAdapter(adapter);
        rvList.getAdapter().notifyDataSetChanged();
        rvList.invalidate();

    }

    public void setSelection(int pos){
        rvList.scrollToPosition(pos);
    }


    public void showDetailt(){
        parentActivity.showDetail();
    }

    public void setLastSearch(String ls){
        this.lastSeach = ls;
    }

   /* public void setUpEditSplitedOrder(Sales s){
        if(s.getCODEPRODUCTTYPE()!= null){
            setSpnSelection(spnFamilia,s.getCODEPRODUCTTYPE());
            spnFamilia.setEnabled(false);
        }else if(s.getCODEPRODUCTSUBTYPE() != null){
            spnFamilia.setOnItemSelectedListener(null);
            spnGrupo.setOnItemSelectedListener(null);
            ProductsSubTypes ps = ProductsSubTypesController.getInstance(parentActivity).getProductTypeByCode(s.getCODEPRODUCTSUBTYPE());
            setSpnSelection(spnFamilia,ps.getCODETYPE());
            spnFamilia.setEnabled(false);
            ProductsSubTypesController.getInstance(parentActivity).fillSpinner(spnGrupo,false,ps.getCODETYPE());
            setSpnSelection(spnGrupo,ps.getCODE());
            spnGrupo.setEnabled(false);

            lastType = ((KV)spnFamilia.getSelectedItem()).getKey();
            lastSubType = ((KV)spnGrupo.getSelectedItem()).getKey();
            search();
        }
    }*/

    public void setSpnSelection(Spinner spn, String code){
        for(int i = 0; i<spn.getAdapter().getCount(); i++){
            KV kv = (KV)spn.getAdapter().getItem(i);
            if(kv.getKey().equals(code)){
                spn.setSelection(i);
                break;
            }
        }
    }

    /**
     * si 1 de los espinners estan bloqueados obviamos la busqueda search. esto es debido a que se esta editando una orden que fue splited, asi que solo
     * puede agregar productos del mismo tipo por el que la orden fue splited.
     * @return
     */
    public boolean searchBlocked(){
        return !spnFamilia.isEnabled() || !spnGrupo.isEnabled();
    }

    public void setUpSpinners(){
        spnFamilia.setEnabled(true);
        spnGrupo.setEnabled(true);
        spnFamilia.setOnItemSelectedListener(spnFamiliaListener);
        spnGrupo.setOnItemSelectedListener(spnGrupoListener);
        ProductsTypesController.getInstance(parentActivity).fillSpinner(spnFamilia, false);
    }

}
