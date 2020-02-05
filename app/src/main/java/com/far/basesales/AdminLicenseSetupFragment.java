package com.far.basesales;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Globales.CODES;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminLicenseSetupFragment extends Fragment {

    AdminConfiguration adminConfiguration;
    Licenses licenses;
    ImageView btnDevices, btnTokens, btnUserDevices, btnUsers, btnControls;

    public AdminLicenseSetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_license_setup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnDevices = view.findViewById(R.id.btnDevices);
        btnTokens = view.findViewById(R.id.btnTokens);
        btnUserDevices = view.findViewById(R.id.btnUserDevices);
        btnUsers = view.findViewById(R.id.btnUsers);
        btnControls = view.findViewById(R.id.btnControls);

        btnTokens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AdminLicenseTokens.class);
                i.putExtra(CODES.EXTRA_ADMIN_LICENSE, licenses);
                startActivity(i);
            }
        });
        btnDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getContext(), AdminLicenseDevices.class);
                i.putExtra(CODES.EXTRA_ADMIN_LICENSE, licenses);
                startActivity(i);
            }
        });

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AdminLicenseUsers.class);
                i.putExtra(CODES.EXTRA_ADMIN_LICENSE, licenses);
                startActivity(i);
            }
        });

        btnUserDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AdminLicenseUserDevice.class);
                i.putExtra(CODES.EXTRA_ADMIN_LICENSE, licenses);
                startActivity(i);
            }
        });

        btnControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AdminLicenseUsersControl.class);
                i.putExtra(CODES.EXTRA_ADMIN_LICENSE, licenses);
                startActivity(i);
            }
        });

        if(licenses != null){
            ((TextView)view.findViewById(R.id.tvLicenceDescription)).setText(licenses.getCODE()+" - "+licenses.getCLIENTNAME());
        }
    }

    public void setAdminConfiguration(AdminConfiguration adminConfiguration){
        this.adminConfiguration = adminConfiguration;
    }
    public void setLicense(Licenses l){
        this.licenses = l;
    }

}
