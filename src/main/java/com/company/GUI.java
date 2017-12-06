package com.company;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

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

    private JFileChooser fc;
    private Drive drive;
    private String filePath = "";
    private File driveFile;
    private DefaultListModel driveListModel;
    GUI(){
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        drive = new Drive();
        driveListModel = new DefaultListModel<>();
        driveList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        driveList.setModel(driveListModel);
        updateList();


        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                fc.setCurrentDirectory(new File("/DriveProject"));
                int returnVal = fc.showOpenDialog(null);
                if(returnVal == fc.APPROVE_OPTION){
                    filePath = fc.getSelectedFile().getPath();
                    filePathLabel.setText(filePath);
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
                    String result = drive.downloadFile(drive.getFile(fileName));
                    downloadResultLabel.setText(result);
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
    }
    public void updateList(){
        ArrayList<com.google.api.services.drive.model.File> list = new ArrayList<>();
        list.addAll(drive.getAllFiles());
        driveListModel.removeAllElements();
        for(com.google.api.services.drive.model.File file : list){
            driveListModel.addElement(file);
        }
    }
}
