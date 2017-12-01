package com.company;

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

    private JFileChooser fc;
    GUI(){
        setContentPane(mainPanel);
        pack();
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fc = new JFileChooser();
                fc.setCurrentDirectory(new File("/DriveProject"));
                int returnVal = fc.showOpenDialog(null);
                if(returnVal == fc.APPROVE_OPTION){
                    String filePath = fc.getSelectedFile().getPath();
                    filePathLabel.setText(filePath);
                }
            }
        });
    }

}
