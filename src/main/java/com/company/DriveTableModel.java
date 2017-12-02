package com.company;

import javax.swing.table.AbstractTableModel;

public class DriveTableModel extends AbstractTableModel{
    DriveTableModel(){

    }

    @Override
    public int getRowCount() {
        return 2;
    }
    @Override
    public int getColumnCount() {
        return 1;  // TODO
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return "test";
    }
    @Override
    public void setValueAt(Object value, int row, int column){

    }
    @Override
    public boolean isCellEditable(int row, int column){
        return false;
    }
}
