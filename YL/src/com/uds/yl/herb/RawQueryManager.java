package com.uds.yl.herb;


import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.teamcenter.rac.kernel.TCSession;
import com.uds.yl.common.Const;
import com.uds.yl.tcutils.LOVUtil;

public class RawQueryManager {
	
	public String m_wsdlAddr = "";
	public TCSession m_session = null;
	
	public String DoCreate(String userUid){
		
		Map<String,String> typeLOVs = LOVUtil.getLocaleLovPair(m_session,Const.MaterialUseQuery.MATERIAL_TYPE_LOV_NAME);
		Set<String> keys = typeLOVs.keySet();
		Collection<String> values = typeLOVs.values();
		String[] lovValueArray = new String[values.size()];
		String[] lovKeyArray = new String[keys.size()];
		String[] valueLovs = values.toArray(lovValueArray);
		RawQueryFrame queryFrame = new RawQueryFrame(valueLovs);
		
		if(queryFrame.ShowDialog()){
			String erpcode = queryFrame.m_code;
			String objectName = queryFrame.m_name;
			int index = queryFrame.m_comboboxIndex;
			
			if(index >= 0){
				String keyLov = keys.toArray(lovKeyArray)[index];
				String result = CallQuery(erpcode, objectName,keyLov,userUid);
				return result;
			}else{
				String keyLov = "";
				String result = CallQuery(erpcode, objectName,keyLov,userUid);
				return result;
			}
		}else{
			return null;
		}
		
	}
	
	
	public String CallQuery(String code,String name,String lov,String userUid){
		
		String result = null;
		String commandId = Const.Material_Query_Condition.COMMOND_ID;
		String commandGuid = null;
		if(code != null && !"".equals(code)){
			try{
			String command1 = "{";
			command1 += "'MaterialId':'" + code + "'";
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
	
		}else if(name != null && !"".equals(name)){
			try{
			String command1 = "{";
			command1 += "'InciName':'" + name + "'";
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
	 		}}catch(Exception ex){
	 			String msg = "Do creation has error:" + ex.getMessage();
				result = msg;
	 		}
	
		}else if(lov != null && !"".equals(lov)){
			try{
			String command1 = "{";
			command1 += "'Catalog':'" + lov + "'";
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
