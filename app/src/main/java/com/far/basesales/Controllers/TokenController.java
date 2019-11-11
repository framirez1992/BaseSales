package com.far.basesales.Controllers;

import android.content.Context;

import com.far.basesales.CloudFireStoreObjects.Licenses;
import com.far.basesales.CloudFireStoreObjects.Token;
import com.far.basesales.Globales.Tablas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TokenController {
    public static String CODE = "code";

    FirebaseFirestore db;
    Context context;
    private static TokenController instance;
    private TokenController(Context c){
        this.context = c;
        db = FirebaseFirestore.getInstance();
    }

    public static TokenController getInstance(Context context){
        if(instance == null){
            instance = new TokenController(context);
        }
        return instance;
    }

    public CollectionReference getReferenceFireStore(){
        Licenses l = LicenseController.getInstance(context).getLicense();
        if(l == null){
            return null;
        }
        CollectionReference reference = db.collection(Tablas.generalUsers).document(l.getCODE()).collection(Tablas.generalUsersToken);
        return reference;
    }

    public void deleteFromFireBase(Token token){
        try {
            getReferenceFireStore().document(token.getCODE()).delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void getQueryTokenByCode(String code, OnSuccessListener<QuerySnapshot> success, OnCompleteListener<QuerySnapshot> complete, OnFailureListener failute){
        getReferenceFireStore().
                whereEqualTo(CODE, code).get().
                addOnSuccessListener(success).
                addOnCompleteListener(complete).
                addOnFailureListener(failute);

    }
}
