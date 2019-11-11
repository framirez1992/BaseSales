package com.far.farpdf.Objects;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;

public class TableColumn extends TableCell {

    float width;
    public TableColumn(String text, float width){
        super(text);
        this.width = width;
        this.fontStyle = Font.BOLD;
        this.align = Element.ALIGN_CENTER;
        this.backgroundColor = BaseColor.BLACK;
        this.color = BaseColor.WHITE;
        this.borderColor = BaseColor.WHITE;

    }
    public TableColumn(String text) {
        super(text);
        this.width = 1f;
        this.fontStyle = Font.BOLD;
        this.align = Element.ALIGN_CENTER;
        this.backgroundColor = BaseColor.BLACK;
        this.color = BaseColor.WHITE;
        this.borderColor = BaseColor.WHITE;
    }

}
