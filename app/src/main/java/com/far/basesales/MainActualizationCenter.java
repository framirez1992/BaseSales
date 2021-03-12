package com.far.basesales;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.far.basesales.CloudFireStoreObjects.Users;
import com.far.basesales.Controllers.ClientsController;
import com.far.basesales.Controllers.CompanyController;
import com.far.basesales.Controllers.DayController;
import com.far.basesales.Controllers.MeasureUnitsController;
import com.far.basesales.Controllers.MeasureUnitsInvController;
import com.far.basesales.Controllers.PaymentController;
import com.far.basesales.Controllers.ProductsControlController;
import com.far.basesales.Controllers.ProductsController;
import com.far.basesales.Controllers.ProductsInvController;
import com.far.basesales.Controllers.ProductsMeasureController;
import com.far.basesales.Controllers.ProductsMeasureInvController;
import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Controllers.ProductsSubTypesInvController;
import com.far.basesales.Controllers.ProductsTypesController;
import com.far.basesales.Controllers.ProductsTypesInvController;
import com.far.basesales.Controllers.ReceiptController;
import com.far.basesales.Controllers.RolesController;
import com.far.basesales.Controllers.SalesController;
import com.far.basesales.Controllers.UserControlController;
import com.far.basesales.Controllers.UserTypesController;
import com.far.basesales.Controllers.UsersController;
import com.far.basesales.Globales.Tablas;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActualizationCenter extends AppCompatActivity implements OnSuccessListener<QuerySnapshot>, OnFailureListener, OnCompleteListener, OnCanceledListener {

    ProgressBar pb;
    TextView tvMessage;
    Button btnExit;
    UsersController usersController;
    int currentindex =0;

    //TABLAS QUE SE BAJAN SIMEPRE POR LOS LISTENER DEL MAIN Usuarios, Devices, Licencias
    String[]Tables = {RolesController.TABLE_NAME, CompanyController.TABLE_NAME, ClientsController.TABLE_NAME,
            ProductsTypesController.TABLE_NAME, ProductsSubTypesController.TABLE_NAME, ProductsController.TABLE_NAME, ProductsMeasureController.TABLE_NAME, ProductsControlController.TABLE_NAME,
            ProductsTypesInvController.TABLE_NAME, ProductsSubTypesInvController.TABLE_NAME, ProductsInvController.TABLE_NAME, ProductsMeasureInvController.TABLE_NAME,
            SalesController.TABLE_NAME,SalesController.TABLE_NAME_DETAIL, ReceiptController.TABLE_NAME, PaymentController.TABLE_NAME, MeasureUnitsController.TABLE_NAME, MeasureUnitsInvController.TABLE_NAME,
            DayController.TABLE_NAME};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_actualization_center);
        usersController = UsersController.getInstance(MainActualizationCenter.this);


        pb = findViewById(R.id.pb);
        tvMessage = findViewById(R.id.tvMessage);
        btnExit = findViewById(R.id.btnExit);

        tvMessage.setText("Por favor espere mietras se actualizan los datos...");
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pb.setVisibility(View.VISIBLE);
        //currentindex=15;
        loadData();


    }

    @Override
    public void onBackPressed() {

    }


    public void loadData(){
       switch (currentindex){
           case 0:UserTypesController.getInstance(MainActualizationCenter.this).searchChanges(true,this, this, this); break;//ALL
           case 1:CompanyController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 2:ClientsController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 3:ProductsTypesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 4:ProductsSubTypesController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 5:ProductsController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 6:ProductsMeasureController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 7:ProductsControlController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 8:ProductsTypesInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 9:ProductsSubTypesInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 10:ProductsInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 11:ProductsMeasureInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
          // case 12:SalesController.getInstance(MainActualizationCenter.this).searchChanges(this, this, this); break;//PETICION
          // case 13:SalesController.getInstance(MainActualizationCenter.this).searchDetailChanges(this, this, this); break;//PETICION
          // case 14:ReceiptController.getInstance(MainActualizationCenter.this).searchChanges(this, this, this); break;//PETICION
          // case 15:PaymentController.getInstance(MainActualizationCenter.this).searchChanges(this, this, this); break;//PETICION
           case 12:MeasureUnitsController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 13:MeasureUnitsInvController.getInstance(MainActualizationCenter.this).searchChanges(true, this, this, this); break;//ALL
           case 14:DayController.getInstance(MainActualizationCenter.this).searchCurrentDayStartedFromFireBase( this, this); break;//ALL
           case 15: UserControlController.getInstance(MainActualizationCenter.this).searchChanges( true, this, this); break;//ALL
          default:
              currentindex=0;
              tvMessage.setText("Finalizado Correctamente");
              pb.setVisibility(View.INVISIBLE);
              btnExit.setVisibility(View.VISIBLE);
              break;
       }
    }



    @Override
    public void onFailure(@NonNull Exception e) {
        currentindex=0;
        tvMessage.setText(e.getMessage()+" - "+e.getLocalizedMessage());
        pb.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCanceled() {
        currentindex=0;
        tvMessage.setText("Canceled");
        pb.setVisibility(View.INVISIBLE);
        btnExit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(@NonNull Task task) {
     if(task.getException() != null){
         currentindex=0;
         tvMessage.setText(task.getException().toString());
         pb.setVisibility(View.INVISIBLE);
         btnExit.setVisibility(View.VISIBLE);
     }
    }

    @Override
    public void onSuccess(QuerySnapshot querySnapshot) {
        switch (currentindex){
            case 0: UserTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 1:CompanyController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 2:ClientsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 3:ProductsTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 4:ProductsSubTypesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 5:ProductsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 6:ProductsMeasureController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 7:ProductsControlController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 8:ProductsTypesInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 9:ProductsSubTypesInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 10:ProductsInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 11:ProductsMeasureInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            //case 12:SalesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(querySnapshot); break;
            //case 13:SalesController.getInstance(MainActualizationCenter.this).consumeQuerySnapshotDetail(querySnapshot); break;
            //case 14:ReceiptController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(querySnapshot); break;
            ///case 15:PaymentController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(querySnapshot); break;
            case 12:MeasureUnitsController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 13:MeasureUnitsInvController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 14:DayController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            case 15:UserControlController.getInstance(MainActualizationCenter.this).consumeQuerySnapshot(true, querySnapshot); break;
            default:break;
        }
        currentindex++;
        loadData();

    }
}
