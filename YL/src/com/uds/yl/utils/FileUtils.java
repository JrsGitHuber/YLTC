package com.uds.yl.utils;

import java.io.File;

public class FileUtils {
	
	/**
	 * 根据路径创建固定的目录 不是文件
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
