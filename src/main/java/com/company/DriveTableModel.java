package com.company;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import java.util.*;

import com.google.api.services.drive.model.File;


public class DriveTableModel extends DefaultListModel{

    private List<String> list  = new ArrayList<String>();
    DriveTableModel(List<File> fileList){
        try{
            for(File f : fileList){
                if(!f.getExplicitlyTrashed()){
                    list.add(f.getTitle());
                }
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}
