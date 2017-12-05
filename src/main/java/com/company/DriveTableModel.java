package com.company;

import javax.swing.table.AbstractTableModel;

import java.util.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveTableModel extends AbstractTableModel{
    private Drive drive;
    private List<com.google.api.services.drive.model.File> list;
    DriveTableModel(Drive drive){
        try{
            this.drive = drive;
            for(File f : drive.getAllFiles()){
                list.add(f);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public int getRowCount() {
        return list.size();
    }
    @Override
    public int getColumnCount() {
        return 1;  // TODO
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0){
            String name= list.get(rowIndex).getTitle();
            return name;
        }else{
            return null;
        }
    }
    @Override
    public void setValueAt(Object value, int row, int column){

    }
    @Override
    public boolean isCellEditable(int row, int column){
        return false;
    }

    public void updateTable(){
        while(list.size() > 0){
            list.remove(0);
        }
        list.addAll(drive.getAllFiles());
    }
}
