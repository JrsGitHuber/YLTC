 /**************************************************************************************************                                      
 *                                               ��Ȩ��UDS���У�2015
 **************************************************************************************************                             
 *  
 *        Function Description
 *        
 **************************************************************************************************
 * Date           Author                   History  
 * 29-Oct-2015    ChenChun               Initial
 * 25-Mar-2016    ChenChun               Add ENV_TC_WEB_LINK_ADDRESS for sysware@snptc
 * 09-May-2016    ChenChun               Add ENV_TC_UDSSERVER_WEB_SERVICE
 **************************************************************************************************/


package com.uds.yl.herb;

public class EnvironmentVariables {

	public static String ENV_CONFIGURATION_FILE = "UDS_CONFIGURATION_FILE";
	public static String ENV_TC_ROOT = "TC_ROOT";
	public static String ENV_TC_PORTAL = "TPR";
	
	//���ﶨ��Ŀ�������ϵͳ��������,Ҳ��������TC��ѡ��
	//��ʽӦ���ǣ�http://pdmserver:port/tc/launchapp
	public static String ENV_TC_WEB_LINK_ADDRESS = "UDS_WEB_LINK_ADDRESS";
	public static String ENV_TC_INTEGRATION_WEB_SERVICE = "UDS_INTEGRATION_WEB_SERVICE";
	
	//UDS SOA������: ip:port
	public static String ENV_TC_UDSSERVER_WEB_SERVICE = "UDS_ENV_UDSSERVER_IP_PORT";
	//UDS ����item����ѡ��: itemIdentifier:displayName,itemIdentifier:displayName,...
	public static String ENV_TC_ITEMCREATION_TYPE = "UDS_ITEMCREATION_TYPE";
}

