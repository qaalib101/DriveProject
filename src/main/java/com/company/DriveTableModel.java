package com.company;


import javax.swing.table.AbstractTableModel;
import java.util.*;



// used to get the data from the database and display it
public class DriveTableModel extends AbstractTableModel{
    private static Database db;
    private static HashMap<Integer, ArrayList<String>> driveMap = new HashMap<>();
    // titles
    private static String[] titles = {"File Name", "File Size(bytes)", "Time and Date", "Action"};

    // creating a new database object and calling the 'updateTable' object
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
        // the number of keys in the map plus an extra row for the titles
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
    // calling the insert values method
    public void insertValues(String filename, double fileSize, String date, String action){
        db.insertValues(filename, fileSize, date, action);
        driveMap = db.getAllData();
    }
    // updating the table
    public void updateTable(){
        driveMap = db.getAllData();
    }
}
