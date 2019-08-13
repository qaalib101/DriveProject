package com.company;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.io.IOException;

public class GUI extends JFrame{
    private JPanel mainPanel;
    private JButton chooseFileButton;
    private JButton uploadButton;
    private JButton chooseDirectoryButton;
    private JList driveList;
    private JTable driveTable;
    private JButton whatsInYourDriveButton;
    private JButton recentActivityButton;
    private JLabel uploadResultLabel;
    private JButton downloadButton;
    private JLabel downloadResultLabel;
    private JLabel filePathLabel;
    private JButton connectToYourDriveButton;

    private JFileChooser fc;
    private Drive drive;
    private DriveTableModel driveTableModel;
    private String filePath = "";
    private DateFormat formatter= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private File driveFile;

    private DefaultListModel driveListModel;
    private HashMap<String, com.google.api.services.drive.model.File> driveMap;

    public GUI(){
        setContentPane(mainPanel);
        mainPanel.setSize(mainPanel.getPreferredSize());
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Drive Desktop");

        // creating all the objects needed in the gui
        drive = new Drive();


        driveListModel = new DefaultListModel<>();
        driveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driveList.setModel(driveListModel);

        driveTableModel = new DriveTableModel();
        driveTable.setModel(driveTableModel);
        driveTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);


        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // creating a new jfile chooser object

                fc = new JFileChooser();
                fc.setCurrentDirectory(drive.getDataStoreDir());
                int returnVal = fc.showOpenDialog(null);

                // returns a postitive integer if a file is selected

                if(returnVal == fc.APPROVE_OPTION){
                    filePath = fc.getSelectedFile().getPath();
                    String fileName = fc.getSelectedFile().getName();
                    filePathLabel.setText(fileName);

                }
            }
        });
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (drive.getConnectedStatus()) {
                    if (!filePathLabel.getText().equals("")) {
                        // using a instance MimeTypesFileTypeMap to get the mime type of the file
                        Path path = Paths.get(filePath);
                        String result = null;
                        try {
                            String mimetype = Files.probeContentType(path);
                            result = drive.uploadFile(false, filePath, mimetype);
                        } catch (IOException error) {
                            System.out.println(error);
                        }
                        // if the result is not null
                        if (result != null || result != "") {
                            uploadResultLabel.setText(result);
                            filePathLabel.setText("");
                            filePath = "";
                            String date = formatter.format(new Date());
                            String filename = fc.getSelectedFile().getName();
                            long size = fc.getSelectedFile().length();
                            // adding new values into the drive table and database
                            driveTableModel.insertValues(filename, size, date, "UPLOAD");
                        } else {
                            showMessage("There was a problem that occured");
                        }
                        updateList();
                    } else {
                        showMessage("Please choose a file to upload!");
                    }
                } else {
                showMessage("Click the connect button to connect to your drive to upload a file. ");
                }
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    int index  = driveList.getSelectedIndex();
                    // checking to see if a list item is selected

                    if(index > -1) {
                        String fileName = (String)driveListModel.get(index);
                        com.google.api.services.drive.model.File downloadFile = driveMap.get(fileName);
                        String downloadPath = drive.downloadFile(downloadFile);
                        downloadResultLabel.setText(downloadPath);
                        String filename = downloadFile.getName();
                        String date = formatter.format(new Date());
                        long size = downloadFile.getSize();
                        // inserting new values
                        driveTableModel.insertValues(filename, size , date, "DOWNLOAD");
                    }else{
                        showMessage("Please choose a file to download!");
                    }
                    driveList.clearSelection();
                }
                catch (Exception exc){
                    showMessage("Check all fields to see tamper ment");
                }
            }
        });
        whatsInYourDriveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(drive.getConnectedStatus()){
                    updateList();
                }
                else{
                    showMessage("Connect to your drive to see what's in it");
                }

            }
        });

        // choosing a directory for downloads
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
        connectToYourDriveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drive.connectToDrive();
            }
        });
    }

    public void updateList(){
        try{
            ArrayList<com.google.api.services.drive.model.File> list = new ArrayList<>();

            list.addAll(drive.getAllFiles());
            driveListModel.removeAllElements();
            driveMap = new HashMap<>();
            for(com.google.api.services.drive.model.File file : list){
                if(!file.getExplicitlyTrashed()){
                    driveListModel.addElement(file.getName());
                    driveMap.put(file.getName(), file);
                }

            }
        }
        catch(NullPointerException noe){
            showMessage("A null pointer exception happened");
        }
    }
    public void showMessage(String message){
        JOptionPane.showMessageDialog(GUI.this, message);
    }
}
