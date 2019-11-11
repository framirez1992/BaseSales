package com.far.farpdf.Objects;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

/**
 * Representa una linea en el documento
 */
public class LineItem {
     String text;
     BaseColor color;
     int align;
     int size;
     int fontStyle;
     Font.FontFamily fontFamily;
    /*private static Font smallBold;// = new Font(Font.FontFamily.TIMES_ROMAN, 12,Font.NORMAL);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);*/

    public LineItem(String text){
       this.text = text;
       this.size = 16;
       this.align = Paragraph.ALIGN_LEFT;
       this.color = BaseColor.BLACK;
       this.fontFamily = Font.FontFamily.TIMES_ROMAN;
       this.fontStyle = Font.NORMAL;
    }
    public LineItem bold(){
    fontStyle = Font.BOLD;
    return this;
    }

    public LineItem size(int size){
        this.size = size;
        return this;
    }

    public LineItem fontFamily(Font.FontFamily font){
        this.fontFamily = font;
        return this;
    }

    public LineItem color(BaseColor color){
        this.color = color;
        return this;
    }

    public LineItem left(){
        this.align = Paragraph.ALIGN_LEFT;
        return this;
    }
    public LineItem right(){
        this.align = Paragraph.ALIGN_RIGHT;
        return this;
    }

    public LineItem center(){
        this.align = Paragraph.ALIGN_CENTER;
        return this;
    }


    public String getText() {
        return text;
    }

    public Font getFont(){
        Font font = new Font(fontFamily, size,
                fontStyle, color);
        return font;
    }




    public int getAlign() {
        return align;
    }

    public int getSize() {
        return size;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public Font.FontFamily getFontFamily() {
        return fontFamily;
    }
}
