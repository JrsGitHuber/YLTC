package com.uds.yl.herb;


public class CheckTaskStatusManager {
	public String m_wsdlAddr = "";

	/*
	 * ��ȡ����ִ�н��
	 * ����ֵ:String[0] - ���״̬
	 *       String[1] - ���,����û��ֵ,˵��ֻ��״̬
	 */
	public String[] GetTaskResult(String command, String guid){
		String result = AskTaskStatus(command,guid,true);
		if(result != null && result != ""){
			int splitPos = result.indexOf("@");
			if(splitPos > 0){
				String statusStr = result.substring(0, splitPos);
				String retValStr = result.substring(splitPos + 1);
				return new String[]{statusStr,retValStr};
			}
		}
		return new String[]{result};
	}
	/*
	 * ��ȡ����ִ��״̬
	 * ����ֵ:״̬
	 */
	public String CheckStatus(String command, String guid){
		return AskTaskStatus(command,guid,false);
	}
	private String AskTaskStatus(String command, String guid, boolean askResult){
		
		ProgressDlg progressdlg = new ProgressDlg();
		progressdlg.open("�ȴ�������");
		
		String wantResult = null;
		if(askResult){
			wantResult = "GetResult";
		}
		String result = "LongWait";
		int count = 1;
		while(count <= 100){
			String feedback = CheckStatusFromServer(command,guid,wantResult);
			if(feedback != null){
				int splitPos = feedback.indexOf(":");
				if(splitPos > 0){
					String statusValueStr = feedback.substring(0, splitPos);
					String statusDisplay = feedback.substring(splitPos + 1);
					try{
						int statusValue = Integer.parseInt(statusValueStr);
						//�ж��Ƿ������
						if(statusValue < 0){
							result = statusDisplay;
							break;
						}else if(statusValue == 0){
							result = statusDisplay;
							break;
						}
					}catch(Exception ex){
						String msg = "Check status - get status value has error:" + ex.getMessage();
					}
					
				}
				count++;
				progressdlg.updateProgress((10*count)%100, "����״̬: " + feedback);
				
			}
			try{
				Thread.sleep(5000);				
			}catch(Exception ex){
				String msg = "Check status thread sleep has error:" + ex.getMessage();
				break;
			}
			if(progressdlg.getTaskStatus() == ProgressDlg.TaskStatus.STOP){
				result = "Stop";
				break;
			}
		}
		progressdlg.dispose();
		
		return result;
	}
 	private String CheckStatusFromServer(String command, String guid, String askResult){
 		String result = "";
 		
		String wsdlAddr = m_wsdlAddr;
		String commandId = "TC_COMMAND__get_soatask_status";
		String command1 = command;
		String command2 = guid;
		String command3 = askResult;
		try{
			String[] retVals = com.uds.tc.common.webservice.IWebCommonService_BasicHttpBindingIWebCommonService_Client.DoSendSoaCommand(wsdlAddr, commandId, command1, command2, command3);
			result = retVals[0];
			
		}catch(Exception ex){
			String msg = "Connect server has error:" + ex.getMessage();
			result = msg;
		}		
 		
 		return result;
 	}
}
