package com.uds.yl.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class LogUtil {
	
	 // ���������ڸ�ʽ  
    public static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss";  
    // �������ŵ����ڸ�ʽ��������¼ʱ���  
    public static final String DATE_PATTERN_NOMARK = "yyyyMMddHHmmss";  
  
    /**  
     * Ϊlog���õȼ�  
     *   
     * @param log  
     * @param level  
     */  
    public static void setLogLevel(Logger log, Level level) {  
        log.setLevel(level);  
    }  
  
    /**  
     * Ϊlog��ӿ���̨handler  
     *   
     * @param log  
     *            Ҫ���handler��log  
     * @param level  
     *            ����̨������ȼ�  
     */  
    public static void addConsoleHandler(Logger log, Level level) {  
        // ����̨�����handler  
        ConsoleHandler consoleHandler = new ConsoleHandler();  
        // ���ÿ���̨����ĵȼ������ConsoleHandler�ĵȼ����ڻ��ߵ���log��level������FileHandler��level���������̨��������ڣ�����Log�ȼ������  
        consoleHandler.setLevel(level);  
  
        // ��ӿ���̨��handler  
        log.addHandler(consoleHandler);  
    }  
  
    /**  
     * Ϊlog����ļ����Handler  
     *   
     * @param log  
     *            Ҫ����ļ����handler��log  
     * @param level  
     *            log����ȼ�  
     * @param filePath  
     *            ָ���ļ�ȫ·��  
     */  
    public static void addFileHandler(Logger log, Level level, String filePath) {  
        FileHandler fileHandler = null;  
        try {  
        	File file = new File(filePath);
        	if(!file.exists()){
        		file.createNewFile();
        	}
            fileHandler = new FileHandler(filePath,true);  
            // ��������ļ��ĵȼ������FileHandler�ĵȼ����ڻ��ߵ���log��level������FileHandler��level������ļ���������ڣ�����Log�ȼ������  
            fileHandler.setLevel(level);  
            fileHandler.setFormatter(new Formatter() {  
                @Override  
                public String format(LogRecord record) {  
  
                    // �����ļ������ʽ  
                	String result = "[ " + getCurrentDateStr(DATE_PATTERN_FULL) + " - Level:"  
                            + record.getLevel().getName().substring(0, 1) + " ]-" + "[" + record.getSourceClassName()  
                            + " -> " + record.getSourceMethodName() + "()] " + record.getMessage(); 
                	
                    return result+System.getProperty("line.separator");  
                }  
            });  
  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        // �������ļ�handler  
        log.addHandler(fileHandler);  
    }  
  
    /**  
     * ��ȡ��ǰʱ��  
     *   
     * @return  
     */  
    public static String getCurrentDateStr(String pattern) {  
        Date date = new Date();  
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);  
        return sdf.format(date);  
    }  
    
}
