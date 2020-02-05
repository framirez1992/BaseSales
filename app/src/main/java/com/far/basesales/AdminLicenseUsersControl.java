package com.far.basesales;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.far.basesales.Adapters.Models.SimpleSeleccionRowModel;
import com.far.basesales.Adapters.Models.UserControlRowModel;
import com.far.basesales.Adapters.SimpleSelectionRowAdapter;
import com.far.basesales.Adapters.UserControlRowEditionAdapter;
import com.far.basesales.CloudFireStoreObjects.Company;
import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.UserControl;
import com.far.basesales.CloudFireStoreObjects.UserTypes;
import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Generic.KV;
import com.far.basesales.Globales.CODES;
import com.far.basesales.Globales.Tablas;
import com.far.basesales.Interfases.ListableActivity;
import com.far.basesales.Utils.Funciones;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class AdminLicenseUsersControl extends AppCompatActivity implements ListableActivity {

    LinearLayout llSave;
    RecyclerView rvList;
    ArrayList<UserControl> userControls;
    UserControl userControl = null;
    Licenses license;
    FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_license_usercontrol);

        if(getIntent().getExtras()== null || !getIntent().getExtras().containsKey(CODES.EXTRA_ADMIN_LICENSE) ){
            finish();
            return;
        }
        fs = FirebaseFirestore.getInstance();
        license = (Licenses) getIntent().getSerializableExtra(CODES.EXTRA_ADMIN_LICENSE);

        llSave = findViewById(R.id.llSave);
        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveControls();
            }
        });

        rvList = findViewById(R.id.rvList);
        userControls = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(AdminLicenseUsersControl.this);
        rvList.setLayoutManager(manager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUserControl)
                .addSnapshotListener(AdminLicenseUsersControl.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                    userControls = new ArrayList<>();

                    for (DocumentSnapshot ds : querySnapshot) {
                        UserControl pt = ds.toObject(UserControl.class);
                        userControls.add(pt);
                    }

                refreshList();
                refreshValues();
            }
        });

    }

    @Override
    public void onClick(Object obj) {
        userControl = null;
        UserControlRowModel sr = (UserControlRowModel)obj;
        for(UserControl uc: userControls){
            if(sr.getCode().equals(uc.getCODE())){
                userControl = uc;
                break;
            }
        }

    }



    public void refreshList(){
        SimpleSelectionRowAdapter adapter = new SimpleSelectionRowAdapter(AdminLicenseUsersControl.this, UserControlController.getInstance(AdminLicenseUsersControl.this).getAdminUserControlSSRM());
        rvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void refreshValues(){

        ArrayList<SimpleSeleccionRowModel> objs = new ArrayList<>();
        for(UserControl uc: userControls){
            if(uc.getACTIVE()){
                objs.add(new SimpleSeleccionRowModel(uc.getCODE(), uc.getCONTROL(), uc.getACTIVE()));
            }
        }
        ((SimpleSelectionRowAdapter)rvList.getAdapter()).setSelections(objs);

    }


    public void saveControls(){
        WriteBatch lote =fs.batch();
        for(SimpleSeleccionRowModel ssrm: ((SimpleSelectionRowAdapter)rvList.getAdapter()).getObjects()){
            //String code, String target, String targetCode, String control, String value, boolean active
            UserControl uc =new UserControl(ssrm.getCode(), CODES.USERSCONTROL_TARGET_COMPANY, "", ssrm.getCode(), "1", ssrm.isChecked());
            lote.set(fs.collection(Tablas.generalUsers).document(license.getCODE()).collection(Tablas.generalUsersUserControl).document(ssrm.getCode()), uc.toMap());
        }
        lote.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AdminLicenseUsersControl.this, "Saved", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminLicenseUsersControl.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
