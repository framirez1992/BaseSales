package com.far.basesales;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.far.basesales.Adapters.LicenseAdapter;
import com.far.basesales.Adapters.Models.LicenseRowModel;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Controllers.LicenseController;
import com.far.basesales.Interfases.ListableActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminLicensesFragment extends Fragment {


    AdminConfiguration adminConfiguration;
    RecyclerView rvList;
    ProgressBar pb;
    public AdminLicensesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_licenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = view.findViewById(R.id.rvList);
        rvList.setLayoutManager(new LinearLayoutManager(adminConfiguration));
        pb = view.findViewById(R.id.pb);

        searchLicenses();
        adminConfiguration.showMenu();
    }

    public void setAdminConfiguration(AdminConfiguration adminConfiguration){
        this.adminConfiguration = adminConfiguration;
    }

    public void searchLicenses(){
        startLoading();
        LicenseController.getInstance(adminConfiguration).getAllLicenses(onSuccessLicenses, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getException()!= null){
                    endLoading();
                    Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }

            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                endLoading();
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public OnSuccessListener<QuerySnapshot> onSuccessLicenses = new OnSuccessListener<QuerySnapshot>() {
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            ArrayList<LicenseRowModel> l = new ArrayList();

            if(queryDocumentSnapshots != null && queryDocumentSnapshots.size() >0 ){
                for( DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Licenses license = doc.toObject(Licenses.class);
                    l.add(new LicenseRowModel(license));
                }
            }

            refreshList(l);
            endLoading();

        }
    };

    public void refreshList(ArrayList<LicenseRowModel> list){

        LicenseAdapter la = new LicenseAdapter(adminConfiguration, adminConfiguration,list);
        rvList.setAdapter(la);
        rvList.invalidate();

    }
    public void startLoading(){
        pb.setVisibility(View.VISIBLE);
    }
    public void endLoading(){
        pb.setVisibility(View.GONE);
    }



}
