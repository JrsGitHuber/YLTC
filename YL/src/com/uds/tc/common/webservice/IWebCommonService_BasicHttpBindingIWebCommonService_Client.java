 /**************************************************************************************************                                      
 *                                               ��Ȩ��UDS���У�2016
 **************************************************************************************************                             
 *  
 *        Function Description
 *        ����UDS������
 **************************************************************************************************
 * Date           Author                   History  
 * 14-Jun-2016    ChenChun               ֧�ַ���
 **************************************************************************************************/

package com.uds.tc.common.webservice;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;


/**
 * This class was generated by Apache CXF 3.0.8
 * 2016-05-06T14:08:22.527+08:00
 * Generated source version: 3.0.8
 * 
 */
public final class IWebCommonService_BasicHttpBindingIWebCommonService_Client {

    private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "WebCommonService");

    private IWebCommonService_BasicHttpBindingIWebCommonService_Client() {
    }

/*    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = WebCommonService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        WebCommonService ss = new WebCommonService(wsdlURL, SERVICE_NAME);
        IWebCommonService port = ss.getBasicHttpBindingIWebCommonService();  
        
        {
        System.out.println("Invoking customizeNonTcCommand2...");
        java.lang.String _customizeNonTcCommand2_commandId = "";
        java.lang.Object _customizeNonTcCommand2_command1 = new java.lang.Object();
        java.lang.Object _customizeNonTcCommand2_command2 = new java.lang.Object();
        java.lang.Object _customizeNonTcCommand2_command3 = new java.lang.Object();
        java.lang.String _customizeNonTcCommand2__return = port.customizeNonTcCommand2(_customizeNonTcCommand2_commandId, _customizeNonTcCommand2_command1, _customizeNonTcCommand2_command2, _customizeNonTcCommand2_command3);
        System.out.println("customizeNonTcCommand2.result=" + _customizeNonTcCommand2__return);


        }
        {
        System.out.println("Invoking customizeNonTcCommand3...");
        java.lang.String _customizeNonTcCommand3_commandId = "";
        byte[] _customizeNonTcCommand3_command1 = new byte[0];
        byte[] _customizeNonTcCommand3_command2 = new byte[0];
        byte[] _customizeNonTcCommand3_command3 = new byte[0];
        java.lang.String _customizeNonTcCommand3__return = port.customizeNonTcCommand3(_customizeNonTcCommand3_commandId, _customizeNonTcCommand3_command1, _customizeNonTcCommand3_command2, _customizeNonTcCommand3_command3);
        System.out.println("customizeNonTcCommand3.result=" + _customizeNonTcCommand3__return);


        }
        {
        System.out.println("Invoking customizeCommand...");
        java.lang.String _customizeCommand_commandId = "";
        java.lang.String _customizeCommand_command1 = "";
        java.lang.String _customizeCommand__return = port.customizeCommand(_customizeCommand_commandId, _customizeCommand_command1);
        System.out.println("customizeCommand.result=" + _customizeCommand__return);


        }
        {
        System.out.println("Invoking sendSpecialCommand...");
        java.lang.String _sendSpecialCommand_commandId = "";
        java.lang.String _sendSpecialCommand_command1 = "";
        java.lang.String _sendSpecialCommand__return = port.sendSpecialCommand(_sendSpecialCommand_commandId, _sendSpecialCommand_command1);
        System.out.println("sendSpecialCommand.result=" + _sendSpecialCommand__return);


        }
        {
        System.out.println("Invoking customizeNonTcCommand...");
        java.lang.String _customizeNonTcCommand_commandId = "";
        java.lang.String _customizeNonTcCommand_command1 = "";
        java.lang.String _customizeNonTcCommand_command2 = "";
        java.lang.String _customizeNonTcCommand_command3 = "";
        java.lang.String _customizeNonTcCommand__return = port.customizeNonTcCommand(_customizeNonTcCommand_commandId, _customizeNonTcCommand_command1, _customizeNonTcCommand_command2, _customizeNonTcCommand_command3);
        System.out.println("customizeNonTcCommand.result=" + _customizeNonTcCommand__return);


        }
        {
        System.out.println("Invoking downloadFileCommand...");
        java.lang.String _downloadFileCommand_commandId = "";
        java.lang.String _downloadFileCommand_command1 = "";
        byte[] _downloadFileCommand__return = port.downloadFileCommand(_downloadFileCommand_commandId, _downloadFileCommand_command1);
        System.out.println("downloadFileCommand.result=" + _downloadFileCommand__return);


        }
        {
        System.out.println("Invoking sendSpecialCommand3...");
        java.lang.String _sendSpecialCommand3_commandId = "";
        java.lang.String _sendSpecialCommand3_command1 = "";
        java.lang.String _sendSpecialCommand3_command2 = "";
        java.lang.String _sendSpecialCommand3_command3 = "";
        java.lang.String _sendSpecialCommand3__return = port.sendSpecialCommand3(_sendSpecialCommand3_commandId, _sendSpecialCommand3_command1, _sendSpecialCommand3_command2, _sendSpecialCommand3_command3);
        System.out.println("sendSpecialCommand3.result=" + _sendSpecialCommand3__return);


        }
        {
        System.out.println("Invoking sendSpecialCommand2...");
        java.lang.String _sendSpecialCommand2_commandId = "";
        java.lang.String _sendSpecialCommand2_command1 = "";
        java.lang.String _sendSpecialCommand2_command2 = "";
        java.lang.String _sendSpecialCommand2__return = port.sendSpecialCommand2(_sendSpecialCommand2_commandId, _sendSpecialCommand2_command1, _sendSpecialCommand2_command2);
        System.out.println("sendSpecialCommand2.result=" + _sendSpecialCommand2__return);


        }

        System.exit(0);
    }*/

    public static String DoWork(String args[]) throws java.lang.Exception{
    	return SendNonTcCommand(args);
    }
    public static String DoSendCustomizedCommand(String wsdlUrl, String commandId, String command1,String command2,String command3) throws java.lang.Exception{
    	String args[] = {wsdlUrl,commandId,command1,command2,command3};
    	return SendNonTcCommand(args);
    }
    private static String SendNonTcCommand(String args[]) throws java.lang.Exception{
    	URL wsdlURL = WebCommonService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        WebCommonService ss = new WebCommonService(wsdlURL, SERVICE_NAME);
        IWebCommonService port = ss.getBasicHttpBindingIWebCommonService();  
        
        System.out.println("Invoking customizeNonTcCommand...");
        java.lang.String _customizeNonTcCommand_commandId = "";
        java.lang.String _customizeNonTcCommand_command1 = "";
        java.lang.String _customizeNonTcCommand_command2 = "";
        java.lang.String _customizeNonTcCommand_command3 = "";
        
        if(args.length > 1 && args[1] != null && !"".equals(args[1])){
        	_customizeNonTcCommand_commandId = args[1];
        }
        if(args.length > 2 && args[2] != null && !"".equals(args[2])){
        	_customizeNonTcCommand_command1 = args[2];
        }
        if(args.length > 3 && args[3] != null && !"".equals(args[3])){
        	_customizeNonTcCommand_command2 = args[3];
        }
        if(args.length > 4 && args[4] != null && !"".equals(args[4])){
        	_customizeNonTcCommand_command3 = args[4];
        }
        
        java.lang.String _customizeNonTcCommand__return = port.customizeNonTcCommand(_customizeNonTcCommand_commandId, _customizeNonTcCommand_command1, _customizeNonTcCommand_command2, _customizeNonTcCommand_command3);
        System.out.println("customizeNonTcCommand.result=" + _customizeNonTcCommand__return);

    	return _customizeNonTcCommand__return;
    }
    
    public static String DoCreateItemWork(String args[]) throws java.lang.Exception{
    	String[] retVal = SendSoaTcCommand(args);
    	if(retVal != null){
    		return retVal[0];
    	}
    	return null;
    }
    public static String[] DoSendSoaCommand(String wsdlUrl, String commandId, String command1,String command2,String command3) throws java.lang.Exception{
    	String args[] = {wsdlUrl,commandId,command1,command2,command3};
    	return SendSoaTcCommand(args);
    }
    private static String[] SendSoaTcCommand(String args[]) throws java.lang.Exception{
    	String result = ExecuteSendSoaTcCommand(args);
    	String successStr = "Success";
    	if(result != null && result.startsWith(successStr)){
    		String guid = result.substring(successStr.length());
    		return new String[]{successStr,guid};
    	}else{
    		return new String[]{result};
    	}
    }
    private static String ExecuteSendSoaTcCommand(String args[]) throws java.lang.Exception{
    	URL wsdlURL = WebCommonService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        WebCommonService ss = new WebCommonService(wsdlURL, SERVICE_NAME);
        IWebCommonService port = ss.getBasicHttpBindingIWebCommonService();  
        
        System.out.println("Invoking sendSpecialCommand3...");
        java.lang.String _sendSpecialCommand3_commandId = "";
        java.lang.String _sendSpecialCommand3_command1 = "";
        java.lang.String _sendSpecialCommand3_command2 = "";
        java.lang.String _sendSpecialCommand3_command3 = "";
        
        if(args.length > 1 && args[1] != null && !"".equals(args[1])){
        	_sendSpecialCommand3_commandId = args[1];
        }
        if(args.length > 2 && args[2] != null && !"".equals(args[2])){
        	_sendSpecialCommand3_command1 = args[2];
        }
        if(args.length > 3 && args[3] != null && !"".equals(args[3])){
        	_sendSpecialCommand3_command2 = args[3];
        }
        if(args.length > 4 && args[4] != null && !"".equals(args[4])){
        	_sendSpecialCommand3_command3 = args[4];
        }
        
        java.lang.String _sendSpecialCommand3__return = port.sendSpecialCommand3(_sendSpecialCommand3_commandId, _sendSpecialCommand3_command1, _sendSpecialCommand3_command2, _sendSpecialCommand3_command3);
        System.out.println("sendSpecialCommand3.result=" + _sendSpecialCommand3__return);

    	return _sendSpecialCommand3__return;
    }
}