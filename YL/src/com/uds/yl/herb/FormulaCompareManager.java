package com.uds.yl.herb;

import java.util.List;

import com.uds.yl.common.Const;


public class FormulaCompareManager {
	public String m_wsdlAddr = "";
	
	
	
 	public String DoCreate(List<String> uids, List<String> names,String userUid){
 		
 		CompareFrame compareFrame = new CompareFrame(uids, names);
 
 		//wake up the frame
 		int index = compareFrame.ShowDialog();
 		if (index >= 0)
 		{
			String uid = uids.get(index);
			uids.remove(index);
			uids.add(0, uid);
			String result = CallCompare(uids,userUid);
			return result;
 		}
 		else
 		{
 		    return null;
 		}
 	}
	private String CallCompare(List<String> uids,String userUid){
 		//
 		String uidList = "";
 		uidList = uidList + "[";
		for (int i = 1; i < uids.size(); i++) {
			uidList = uidList + "'" + uids.get(i) + "'" + ",";
		}
		uidList = uidList.substring(0, uidList.length() - 1);
		uidList = uidList + "]";
		//
 		String result = null;
		String commandId = Const.FormulatorCompare.COMMOND_ID;
		String commandGuid = null;
		try{
			String command1 = "{";
			command1 += "'MajorFormula':'" + uids.get(0) + "'";
			command1 += ",";
			command1 += "'MinorFormulaList':" + uidList;
			command1 += ",";
			command1 += "'UserUid':'" + userUid + "'";
			command1 += "}";
	 		String[] retVals = CreateFromServer(commandId,command1);
	 		if(retVals != null){
	 			if(retVals.length > 0){
	 				result = retVals[0];
	 			}
	 			if(retVals.length > 1){
	 				commandGuid = retVals[1];
	 			}
	 		}
		}catch(Exception ex){
 			String msg = "Do creation has error:" + ex.getMessage();
			result = msg;
 		}

 		
 		//看任务是否完成
 		if(result.startsWith("Success") && commandGuid != null){
 			String command11 = commandId;
 			String guid = commandGuid;
 			CheckTaskStatusManager checkManager = new CheckTaskStatusManager();
 			checkManager.m_wsdlAddr = m_wsdlAddr;
 			String[] retVals = checkManager.GetTaskResult(command11, guid);
 			if(retVals.length > 1){
 				result = retVals[1];
 			}else{
 				result = retVals[0];
 			}
 		}
 		
 		return result;

	}
 	private String[] CreateFromServer(String commandId, String command1){
 		
		String result = null;
		String wsdlAddr = m_wsdlAddr;
		try{
			String[] retVals = com.uds.tc.common.webservice.IWebCommonService_BasicHttpBindingIWebCommonService_Client.DoSendSoaCommand(wsdlAddr, commandId, command1, null, null);
			return retVals;
		}catch(Exception ex){
			
			String msg = "Connect server has error:" + ex.getMessage();
			result = msg;
		}
		return new String[]{result};
	} 	

}
