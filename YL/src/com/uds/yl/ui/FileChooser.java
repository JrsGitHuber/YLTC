package com.uds.yl.ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class FileChooser {  
    public FileChooser(){  
    	 JFileChooser jfc=new JFileChooser();  
         jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
         jfc.showDialog(new JLabel(), "ѡ��");  
         File file=jfc.getSelectedFile();  
         if(file.isDirectory()){  
             System.out.println("�ļ���:"+file.getAbsolutePath());  
         }else if(file.isFile()){  
             System.out.println("�ļ�:"+file.getAbsolutePath());  
         }  
         System.out.println(jfc.getSelectedFile().getName());  
    }  
  
}  