package com.uds.yl.tcutils;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentFormType;
import com.teamcenter.rac.kernel.TCSession;

public class FormUtil {
	/**
	 * 创建一个Form
	 */
	public static TCComponentForm createtForm(String type,String name,String desc){
		
		try {
			AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			TCComponentFormType form_type = (TCComponentFormType) session.getTypeComponent(type);
			
			TCComponentForm newForm = form_type.create(name,desc ,type );
			return newForm;
		} catch (Exception e) {
			return null;
		}
	}
}
