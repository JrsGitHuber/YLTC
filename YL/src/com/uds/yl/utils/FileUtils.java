package com.uds.yl.utils;

import java.io.File;

public class FileUtils {
	
	/**
	 * ����·�������̶���Ŀ¼ �����ļ�
	 * @param path
	 * @return
	 */
	public static boolean createFolder(String path){
		File file = new File(path);
		if(file.exists()&&file.isDirectory()){
			return true;
		}else{
			file.mkdirs();
			
			return true;
		}
		
	}

}
