package com.uds.yl.common;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCSession;

public class UserInfoSingleFactory {
	private TCSession mSession;
	private AbstractAIFUIApplication mAPP;
	private TCComponentUser mUser;
	
	private static UserInfoSingleFactory mInstance;
	
	private UserInfoSingleFactory(){
		if(mSession==null){
			mSession = (TCSession) AIFUtility.getCurrentApplication().getSession();	
		}
		
		if(mUser==null){
			mUser = mSession.getUser();
		}
		
	}
	
	public static UserInfoSingleFactory getInstance(){
		if(mInstance==null){
			mInstance = new UserInfoSingleFactory();
		}
		return mInstance;
	}
	
	
	public TCComponentUser getUser(){
		return mInstance.mUser;
	}
	
	
	public TCSession getTCSession(){
		return mInstance.mSession;
	}
	
	
	
}
