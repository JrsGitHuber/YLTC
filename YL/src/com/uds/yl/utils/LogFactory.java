package com.uds.yl.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.uds.yl.common.LogLevel;

public class LogFactory {
  
    // ����ļ�·��������ڣ������ڻᱨ���������Զ�����  
    public static final String LOG_FOLDER = "C:\\YLLog"; 
    static{
    	File file = new File(LOG_FOLDER);
    	if(!file.exists()){
    		file.mkdir();
    	}
    }
  
  
  
    public static Logger initLog(String log_name,String level){
//    	 String log_name = cls.getName();
    	 Logger log = Logger.getLogger(log_name);  
         // Ϊlog����ȫ�ֵȼ�  
         log.setLevel(Level.ALL);  
         if(level.equals(LogLevel.INFO.getValue())){
             LogUtil.addConsoleHandler(log, Level.ALL); // ��ӿ���̨handler  
             String log_filepath = LOG_FOLDER + File.separator + "info" + ".log";// ����ļ����handler  
             LogUtil.addFileHandler(log, Level.ALL, log_filepath); 	 
         }else if (level.equals(LogLevel.ERROE.getValue())) {
             LogUtil.addConsoleHandler(log, Level.ALL);  
             String log_filepath = LOG_FOLDER + File.separator + "error" + ".log";  
             LogUtil.addFileHandler(log, Level.ALL, log_filepath); 
		}else if(level.equals(LogLevel.DEBUG.getValue())){
            LogUtil.addConsoleHandler(log, Level.ALL);  
            String log_filepath = LOG_FOLDER + File.separator + "debug" + ".log";  
            LogUtil.addFileHandler(log, Level.ALL, log_filepath); 
		}
         
   
         // ���ò����ø����handlers�����������ڿ���̨�ظ������Ϣ  
         log.setUseParentHandlers(false);  
   
         return log;  
    }
  
}
