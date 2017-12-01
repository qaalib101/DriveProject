package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileChooser extends JPanel implements ActionListener{

    private JButton button1;
    private JLabel filePath;
    private JPanel mainPanel;
    private File file;
    private JFileChooser fc;

    public FileChooser(){


        button1.addActionListener(this);
        fc.setCurrentDirectory(new File("/DriveProject"));
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == button1){
            int returnVal = fc.showOpenDialog(button1);
            if(returnVal == fc.APPROVE_OPTION){
                file = fc.getSelectedFile();
            }
        }
    }
    public String getFilePath(){
        String filePath = "";
        try{
            filePath = file.getAbsolutePath();
        }catch( Exception e){
            filePath = null;
        }
        return filePath;
    }
}
