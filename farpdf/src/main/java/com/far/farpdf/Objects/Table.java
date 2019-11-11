package com.far.farpdf.Objects;

import java.util.ArrayList;

public class Table {
    ArrayList<TableColumn> columns;
    ArrayList<TableCell> cells;

    // t.setBorderColor(BaseColor.GRAY);
    // t.setPadding(4);
    // t.setSpacing(4);
    // t.setBorderWidth(1);
    public Table(ArrayList<TableColumn> columns, ArrayList<TableCell> tableCells){
        this.columns = columns;
        this.cells = tableCells;
    }


    public ArrayList<TableCell> getCells() {
        return cells;
    }

    public ArrayList<TableColumn> getColumns() {
        return columns;
    }

    public float[] getColumnPercent() {
        float[]result = new float[columns.size()];
        for(int i=0; i< columns.size(); i++){
            result[i] = columns.get(i).width;
        }
        return result;
    }
}
