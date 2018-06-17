package com.uds.yl.utils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.uds.yl.common.LogLevel;

public class LogFactory {
  
    // 这个文件路径必须存在，不存在会报错，并不会自动创建  
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
         // 为log设置全局等级  
         log.setLevel(Level.ALL);  
         if(level.equals(LogLevel.INFO.getValue())){
             LogUtil.addConsoleHandler(log, Level.ALL); // 添加控制台handler  
             String log_filepath = LOG_FOLDER + File.separator + "info" + ".log";// 添加文件输出handler  
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
         
   
         // 设置不适用父类的handlers，这样不会在控制台重复输出信息  
         log.setUseParentHandlers(false);  
   
         return log;  
    }
  
}
