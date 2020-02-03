package com.far.basesales;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.Controllers.CompanyController;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class LogoFragment extends Fragment {

    Activity parent;
    ImageView imgLogo;
    TextView tvName;

    public LogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgLogo = view.findViewById(R.id.imgLogo);
        tvName = view.findViewById(R.id.tvName);
    }

    @Override
    public void onResume() {
        super.onResume();
        setLogo();
    }

    public void setParentActivity(Activity activity){
        this.parent = activity;
    }

    public void setLogo(){
        if(CompanyController.getInstance(parent).getCompany()!= null){
            Company company =CompanyController.getInstance(parent).getCompany();
            tvName.setText(company.getNAME()!=null?company.getNAME():parent.getResources().getString(R.string.app_name));
            if(!company.getLOGO().isEmpty()){
                Picasso.with(parent).load(company.getLOGO()).into(imgLogo);
            }
        }
    }
}
