package com.far.basesales.CloudFireStoreObjects;

import android.database.Cursor;

import com.far.basesales.Controllers.ProductsSubTypesController;
import com.far.basesales.Utils.Funciones;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;


@IgnoreExtraProperties
    public class ProductsSubTypes {
        private String CODE,CODETYPE, DESCRIPTION;
        private int ORDEN;
        private @ServerTimestamp
        Date DATE, MDATE;
        public ProductsSubTypes(){

        }
        public ProductsSubTypes(String code, String codeType,String description, int order){
            this.CODE = code; this.CODETYPE = codeType; this.DESCRIPTION = description; this.ORDEN = order;
        }
        public HashMap<String, Object> toMap(){
            HashMap<String, Object> map = new HashMap<>();
            map.put(ProductsSubTypesController.CODE, CODE);
            map.put(ProductsSubTypesController.CODETYPE, CODETYPE);
            map.put(ProductsSubTypesController.DESCRIPTION, DESCRIPTION);
            map.put(ProductsSubTypesController.ORDER, ORDEN);
            map.put(ProductsSubTypesController.DATE, (DATE == null)? FieldValue.serverTimestamp():DATE);
            map.put(ProductsSubTypesController.MDATE, (MDATE == null)? FieldValue.serverTimestamp():MDATE);

            return map;

        }
        public ProductsSubTypes(Cursor c){
            this.CODE = c.getString(c.getColumnIndex(ProductsSubTypesController.CODE));
            this.CODETYPE = c.getString(c.getColumnIndex(ProductsSubTypesController.CODETYPE));
            this.DESCRIPTION = c.getString(c.getColumnIndex(ProductsSubTypesController.DESCRIPTION));
            this.ORDEN = c.getInt(c.getColumnIndex(ProductsSubTypesController.ORDER));
            this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsSubTypesController.DATE)));
            this.DATE = Funciones.parseStringToDate(c.getString(c.getColumnIndex(ProductsSubTypesController.MDATE)));
        }

        public String getCODE() {
            return CODE;
        }

        public void setCODE(String CODE) {
            this.CODE = CODE;
        }

        public String getDESCRIPTION() {
            return DESCRIPTION;
        }

        public void setDESCRIPTION(String DESCRIPTION) {
            this.DESCRIPTION = DESCRIPTION;
        }

        public int getORDEN() {
            return ORDEN;
        }

        public void setORDEN(int ORDEN) {
            this.ORDEN = ORDEN;
        }

        public String getCODETYPE() {
            return CODETYPE;
        }

        public void setCODETYPE(String CODETYPE) {
            this.CODETYPE = CODETYPE;
        }

        public Date getDATE() {
            return DATE;
        }

        public void setDATE(Date DATE) {
            this.DATE = DATE;
        }

        public Date getMDATE() {
            return MDATE;
        }

        public void setMDATE(Date MDATE) {
            this.MDATE = MDATE;
        }
}
