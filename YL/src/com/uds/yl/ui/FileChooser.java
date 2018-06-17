package com.uds.yl.ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class FileChooser {  
    public FileChooser(){  
    	 JFileChooser jfc=new JFileChooser();  
         jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
         jfc.showDialog(new JLabel(), "选择");  
         File file=jfc.getSelectedFile();  
         if(file.isDirectory()){  
             System.out.println("文件夹:"+file.getAbsolutePath());  
         }else if(file.isFile()){  
             System.out.println("文件:"+file.getAbsolutePath());  
         }  
         System.out.println(jfc.getSelectedFile().getName());  
    }  
  
}  