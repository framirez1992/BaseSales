package com.far.farpdf.Objects;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;

public class Line {
    /** The thickness of the line. */
    public float thickness;
    /** The width of the line as a percentage of the available page width. */
    public float width;
    /** The color of the line. */
    public BaseColor color;
    /** The alignment of the line. */
    public int alignment;//Element.ALIGN_BOTTOM;

    boolean dotted;

    public Line(){
        this.thickness = 1;
        this.width = 100;
        this.color = BaseColor.BLACK;
        this.alignment = Element.ALIGN_CENTER;
        this.dotted = false;

    }

    public Line thickness(float thickness){
        this.thickness = thickness;
        return this;
    }

    public Line width(float width){
        this.width = width;
        return this;
    }

    public Line color(BaseColor color){
        this.color = color;
        return this;
    }

    public Line left(){
        this.thickness = Element.ALIGN_LEFT;
        return this;
    }

    public Line right(){
        this.thickness = Element.ALIGN_RIGHT;
        return this;
    }

    public Line dotted(){
        this.dotted = true;
        return this;
    }

    public boolean isDotted(){
        return dotted;
    }

}
