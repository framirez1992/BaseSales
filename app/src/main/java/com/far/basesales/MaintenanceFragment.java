package com.far.basesales;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Globales.CODES;


public class MaintenanceFragment extends Fragment {


    ImageView btnFamily, btnGroup, btnMeasures, btnProducts,btnFamilyInv, btnGroupInv, btnMeasuresInv, btnProductsInv,btnCompany,  btnUsers, btnUserRol/*, btnControls*/
    ,btnActualizationCenter, btnUsersControl, btnRolesControl, btnClients, btnPrinter;
    LinearLayout llMainScreen,llClients, llMaintenanceControls, llMaintenanceUsers, llMaintenanceProducts,llMaintenanceInventory,llMaintenanceCompany,  llConfiguration;

    int REQUEST_PRINTER=1000;
    public MaintenanceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        llMainScreen = view.findViewById(R.id.llMainScreen);
        llMaintenanceControls = view.findViewById(R.id.llMaintenanceControls);
        llMaintenanceUsers = view.findViewById(R.id.llMaintenanceUsers);
        llMaintenanceProducts = view.findViewById(R.id.llMaintenanceProducts);
        llMaintenanceInventory = view.findViewById(R.id.llMaintenanceInventory);
        llMaintenanceCompany = view.findViewById(R.id.llMaintenanceCompany);
        llClients = view.findViewById(R.id.llClients);
        llConfiguration = view.findViewById(R.id.llConfiguration);

        btnFamily = view.findViewById(R.id.btnFamily);
        btnGroup = view.findViewById(R.id.btnGroups);
        btnMeasures = view.findViewById(R.id.btnMeasures);
        btnProducts = view.findViewById(R.id.btnProducts);
        btnFamilyInv = view.findViewById(R.id.btnFamilyInv);
        btnGroupInv = view.findViewById(R.id.btnGroupsInv);
        btnMeasuresInv = view.findViewById(R.id.btnMeasuresInv);
        btnProductsInv = view.findViewById(R.id.btnProductsInv);
        btnCompany = view.findViewById(R.id.btnCompany);
        btnUsers = view.findViewById(R.id.btnUsers);
        btnUserRol = view.findViewById(R.id.btnUserRol);
        //btnControls = view.findViewById(R.id.btnControls);
        btnActualizationCenter = view.findViewById(R.id.btnActualizationCenter);
        btnUsersControl = view.findViewById(R.id.btnUsersControl);
        btnRolesControl = view.findViewById(R.id.btnRolesControl);
        btnClients = view.findViewById(R.id.btnClients);
        btnPrinter = view.findViewById(R.id.btnPrinter);

        btnFamily.setOnClickListener(imageClick);
        btnGroup.setOnClickListener(imageClick);
        btnMeasures.setOnClickListener(imageClick);
        btnProducts.setOnClickListener(imageClick);

        btnFamilyInv.setOnClickListener(imageClick);
        btnGroupInv.setOnClickListener(imageClick);
        btnMeasuresInv.setOnClickListener(imageClick);
        btnProductsInv.setOnClickListener(imageClick);

        btnUsers.setOnClickListener(imageClick);
        btnUserRol.setOnClickListener(imageClick);

        //btnControls.setOnClickListener(imageClick);
        btnUsersControl.setOnClickListener(imageClick);
        btnRolesControl.setOnClickListener(imageClick);

        btnCompany.setOnClickListener(imageClick);

        btnClients.setOnClickListener(imageClick);

        btnPrinter.setOnClickListener(imageClick);



        btnActualizationCenter.setOnClickListener(imageClick);



    }

    public View.OnClickListener imageClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = null;
            switch (v.getId()){
                case R.id.btnFamily:
                case R.id.btnFamilyInv:
                    i = new Intent(getActivity(), MaintenanceProductTypes.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnFamilyInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnMeasures:
                case R.id.btnMeasuresInv:
                    i = new Intent(getActivity(), MaintenanceUnitMeasure.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnMeasuresInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnUsers:
                    i = new Intent(getActivity(), MaintenanceUsers.class);
                    break;
                case R.id.btnUserRol:
                    i = new Intent(getActivity(), MaintenanceUserTypes.class);
                    break;
                case R.id.btnGroups:
                case R.id.btnGroupsInv:
                    i = new Intent(getActivity(), MaintenanceProductSubTypes.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnGroupsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnProducts:
                case R.id.btnProductsInv:
                    i = new Intent(getActivity(), MaintenanceProducts.class);
                    i.putExtra(CODES.EXTRA_TYPE_FAMILY, (v.getId() == R.id.btnProductsInv)? CODES.ENTITY_TYPE_EXTRA_INVENTORY:CODES.ENTITY_TYPE_EXTRA_PRODUCTSFORSALE );
                    break;
                case R.id.btnActualizationCenter:
                    i = new Intent(getActivity(), MainActualizationCenter.class);
                    break;
               /* case R.id.btnControls:
                    i = new Intent(getActivity(), MaintenanceUsersControl.class);
                    break;*/
                case R.id.btnCompany:
                    i = new Intent(getActivity(), MaintenanceCompany.class);
                    break;
                case R.id.btnRolesControl:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.EXTRA_MAINASSIGNATION_TARGET_ROLESCONTROL);
                    break;
                case R.id.btnUsersControl:
                    i =new Intent(getActivity(), MainAssignation.class);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TABLE, UserControlController.TABLE_NAME);
                    i.putExtra(CODES.EXTRA_MAINASSIGNATION_TARGET, CODES.EXTRA_MAINASSIGNATION_TARGET_USERSCONTROL);
                    break;
                case R.id.btnClients:
                    i = new Intent(getActivity(), MaintenanceClients.class);
                    break;
                case R.id.btnPrinter:
                    i = new Intent(getActivity(), com.example.bluetoothlibrary.BluetoothScan.class);
                    startActivityForResult(i, REQUEST_PRINTER);
                    return;


            }

            try {
                startActivity(i);
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    };
}
