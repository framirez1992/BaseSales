package com.far.farpdf.Objects;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;

public class TableCell {
    public String text;
    public BaseColor color;
    public BaseColor borderColor;
    public BaseColor backgroundColor;
    public int align;
    public int size;
    public int fontStyle;
    public Font.FontFamily fontFamily;

    public TableCell(String text){
        this.text = text;
        this.size = 16;
        this.align = Element.ALIGN_LEFT;
        this.color = BaseColor.BLACK;
        this.borderColor = BaseColor.BLACK;
        this.backgroundColor = BaseColor.WHITE;
        this.fontFamily = Font.FontFamily.TIMES_ROMAN;
        this.fontStyle = Font.NORMAL;
    }
    public TableCell bold(){
        fontStyle = Font.BOLD;
        return this;
    }

    public TableCell size(int size){
        this.size = size;
        return this;
    }

    public TableCell fontFamily(Font.FontFamily font){
        this.fontFamily = font;
        return this;
    }

    public TableCell color(BaseColor color){
        this.color = color;
        return this;
    }

    public TableCell borderColor(BaseColor color){
        this.borderColor = color;
        return this;
    }

    public TableCell backgroundColor(BaseColor color){
        this.backgroundColor = color;
        return this;
    }

    public TableCell left(){
        this.align = Element.ALIGN_LEFT;
        return this;
    }
    public TableCell right(){
        this.align = Element.ALIGN_RIGHT;
        return this;
    }

    public TableCell center(){
        this.align = Element.ALIGN_CENTER;
        return this;
    }

    public TableCell noBorder(){
        borderColor = BaseColor.WHITE;
        return this;
    }

    public Font getFont(){
        Font font = new Font(fontFamily, size,
                fontStyle, color);
        return font;
    }
}
