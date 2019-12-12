package com.far.basesales.Controllers;

import android.content.Context;

import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.Globales.Tablas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class UsersDevicesController {
    public static String CODE = "code", CODEUSER= "codeuser", CODEDEVICE = "codedevice",  DATE = "date", MDATE = "mdate";

    FirebaseFirestore db;
    Context context;
    private static UsersDevicesController instance;
    private UsersDevicesController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static UsersDevicesController getInstance(Context context){
        if(instance == null){
            instance = new UsersDevicesController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(Licenses l){
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersUsersDevices);
        return reference;
    }



    public void getQueryusersDevices(Licenses l, String codeUser, String deviceID, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore(l).
                whereEqualTo(CODEUSER, codeUser).
                whereEqualTo(CODEDEVICE, deviceID).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }

    public Task<QuerySnapshot> getUserDeviceFromFireBase(Licenses license, String codeUser, String codeDevice, OnSuccessListener onSuccessListener, OnFailureListener failureListener){
        // Create a query against the collection.
        Query query = getReferenceFireStore(license).whereEqualTo(CODEUSER, codeUser).whereEqualTo(CODEDEVICE, codeDevice);
        // retrieve  query results asynchronously using query.get()
        return query.get().addOnSuccessListener(onSuccessListener).addOnFailureListener(failureListener);
    }


}
