package com.far.farpdf.Objects;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class Image {
    Bitmap bitmap;
    public float width, height;
    public int alignment;
    public Image(Bitmap b, float width, float height){
        this.width = width;
        this.height = height;
        this.alignment = com.itextpdf.text.Image.LEFT;
        this.bitmap = b;

    }

    public Image(Bitmap b){
        this.width = 200f;
        this.height = 200f;
        this.alignment = com.itextpdf.text.Image.LEFT;
        this.bitmap = b;

    }

    public Image center(){
        alignment = com.itextpdf.text.Image.MIDDLE;
        return this;
    }

    public Image right(){
        alignment = com.itextpdf.text.Image.RIGHT;
        return this;
    }

    public byte[] getBytes(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
