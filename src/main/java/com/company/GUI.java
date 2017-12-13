package com.company;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by si8822fb on 11/28/2017.
 */
public class GUI extends JFrame{
    private JButton chooseFileButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JLabel filePathLabel;
    private JLabel uploadResultLabel;
    private JLabel downloadResultLabel;
    private JPanel mainPanel;
    private JButton whatsInYourDriveButton;
    private JList driveList;
    private JTable driveTable;
    private JButton chooseDirectoryButton;
    private JButton recentActivityButton;

    private JFileChooser fc;
    private Drive drive;
    private DriveTableModel driveTableModel;
    private String filePath = "";
    private DateFormat formatter= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private File driveFile;

    private DefaultListModel driveListModel;
    private HashMap<String, com.google.api.services.drive.model.File> driveMap;
    GUI(){
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Drive Desktop");


        drive = new Drive();
        driveListModel = new DefaultListModel<>();
        driveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driveList.setModel(driveListModel);
        driveTableModel = new DriveTableModel();
        driveTable.setModel(driveTableModel);
        driveTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        pack();

        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                fc.setCurrentDirectory(drive.getDataStoreDir());
                int returnVal = fc.showOpenDialog(null);
                if(returnVal == fc.APPROVE_OPTION){
                    filePath = fc.getSelectedFile().getPath();
                    String fileName = fc.getSelectedFile().getName();
                    filePathLabel.setText(fileName);

                }
            }
        });
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!filePathLabel.getText().equals("")){
                    String mimetype = new MimetypesFileTypeMap().getContentType(filePath);
                    String result = drive.uploadFile(false, filePath, mimetype);
                    if(result != null){
                        uploadResultLabel.setText(result);
                        filePathLabel.setText("");
                        filePath = "";
                        String date = formatter.format(new Date());
                        String filename = fc.getSelectedFile().getName();
                        double size = fc.getSelectedFile().length();
                        driveTableModel.insertValues(filename, size, date, "UPLOAD");
                    }
                    else{
                        JOptionPane.showMessageDialog(GUI.this, "There was a problem that occured");
                    }
                    updateList();
                }else{
                    JOptionPane.showMessageDialog(GUI.this, "Please choose a file to upload!");
                }
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                int index  = driveList.getSelectedIndex();
                if(index > -1) {
                    String fileName = (String)driveListModel.get(index);
                    com.google.api.services.drive.model.File downloadFile = driveMap.get(fileName);
                    String downloadPath = drive.downloadFile(downloadFile);
                    downloadResultLabel.setText(downloadPath);
                    String filename = downloadFile.getTitle();
                    String date = formatter.format(new Date());
                    double size = downloadFile.getFileSize();
                    driveTableModel.insertValues(filename, size , date, "DOWNLOAD");
                }else{
                    JOptionPane.showMessageDialog(GUI.this, "Please choose a file to download!");
                    }
                    driveList.clearSelection();
                }
                catch (Exception exc){
                    JOptionPane.showMessageDialog(GUI.this, "Check all fields to see tamper ment");
                }
            }
        });
        whatsInYourDriveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateList();
            }
        });
        chooseDirectoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File dir = fileChooser.getSelectedFile();
                    drive.setDirForDownloads(dir);
                }
            }
        });
        recentActivityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                driveTableModel.updateTable();
                pack();
            }
        });
    }
    public void updateList(){
        ArrayList<com.google.api.services.drive.model.File> list = new ArrayList<>();
        list.addAll(drive.getAllFiles());
        driveListModel.removeAllElements();
        driveMap = new HashMap<>();
        for(com.google.api.services.drive.model.File file : list){
            if(!file.getExplicitlyTrashed()){
                driveListModel.addElement(file.getTitle());
                driveMap.put(file.getTitle(), file);
            }

        }
    }
}
