package com.company;

import javax.activation.MimetypesFileTypeMap;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
    private JTable driveTable;
    private JButton whatsInYourDriveButton;

    private JFileChooser fc;
    private Drive drive;
    private String filePath = "";
    private File driveFile;
    private DriveTableModel driveTableModel;
    GUI(){
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        drive = new Drive();
        driveTableModel = new DriveTableModel(drive);

        driveTable.setModel(driveTableModel);
        driveTable.setRowSelectionAllowed(true);
        driveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


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
                    uploadResultLabel.setText(result);
                }else{
                    JOptionPane.showMessageDialog(GUI.this, "Please choose a file to upload!");
                }
            }
        });
        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                int index  = driveTable.getSelectedRow();
                if(driveTable.getSelectedRow() != -1) {
                    String fileName = (String)driveTableModel.getValueAt(index, 0);
                    String result = drive.downloadFile(drive.getFile(fileName));
                    downloadResultLabel.setText(result);
                }else{
                    JOptionPane.showMessageDialog(GUI.this, "Please choose a file to download!");
                    }
                }
                catch (Exception exc){
                    JOptionPane.showMessageDialog(GUI.this, "Check all fields to see tamper ment");
                }
            }
        });
        whatsInYourDriveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                driveTableModel.updateTable();
            }
        });

    }

}
