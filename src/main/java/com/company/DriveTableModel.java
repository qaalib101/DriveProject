package com.company;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.util.*;

import com.google.api.services.drive.model.File;


public class DriveTableModel extends AbstractTableModel{
    private static Database db;
    private static HashMap<Integer, ArrayList<String>> driveMap = new HashMap<>();
    private static String[] titles = {"File Name", "File Size", "Time and Date", "Action"};
    DriveTableModel(){
        db = new Database();
        updateTable();
    }
    @Override
    public int getRowCount() {
        int rowcount = 0;
        for(int key :driveMap.keySet()){
            rowcount += 1;
        }
        return rowcount + 1;   //TODO
    }
    @Override
    public int getColumnCount() {
        return 4;  // TODO
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if(rowIndex == 0){
            return titles[columnIndex];
        }
        else {
            return driveMap.get(rowIndex - 1).get(columnIndex);
        }
    }
    public void insertValues(String filename, double fileSize, String date, String action){
        db.insertValues(filename, fileSize, date, action);
        driveMap = db.getAllData();
    }
    public void updateTable(){
        driveMap = db.getAllData();
    }
}
