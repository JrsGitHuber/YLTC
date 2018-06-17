 /**************************************************************************************************                                      
 *                                               版权归UDS所有，2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Oct-2015    ChenChun               Initial
 * 09-May-2016    ChenChun               获取SOA服务器地址
 **************************************************************************************************/


package com.uds.yl.herb;

public class EnvironmentVariableManager {

	public static String GetTcPluginsDir(){
		String tcPlugin = null;
		String tcPortal = GetTcPortalDir();
		if(tcPortal != null){
			tcPlugin = tcPortal+"\\plugins";
		}
		
		return tcPlugin;
	}
	public static String GetTcPortalDir(){
		String tcPortal = EnvironmentVariableReader.getEnvironmentVariable(EnvironmentVariables.ENV_TC_PORTAL);
		if(tcPortal == null){
			String tcRoot = GetTcRootDir();
			if(tcRoot != null){
				tcPortal = tcRoot + "\\portal";
			}
		}
		return tcPortal;
	}
	public static String GetTcRootDir(){
		String tcRoot = EnvironmentVariableReader.getEnvironmentVariable(EnvironmentVariables.ENV_TC_ROOT);
		
		return tcRoot;
	}
	public static String GetSoaServer(){
		String soaAddr = EnvironmentVariableReader.getEnvironmentVariable(EnvironmentVariables.ENV_TC_UDSSERVER_WEB_SERVICE);
		return soaAddr;
	}
}
