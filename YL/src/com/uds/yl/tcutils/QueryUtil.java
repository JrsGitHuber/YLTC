package com.uds.yl.tcutils;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.LogLevel;
import com.uds.yl.utils.LogFactory;

public class QueryUtil {
	public static Logger logger = LogFactory.initLog("QueryUtil", LogLevel.INFO.getValue());
	
	
	// ��ȡ��ѯ��
	public static TCComponentQuery getTCComponentQuery(String query_class){
		try {
			TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
			String QUERY_CLASS = query_class;
			TCComponentQueryType typeComponent = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery query = null;
			TCComponent queryComponent = typeComponent.find(QUERY_CLASS);
			if (queryComponent == null) {
				logger.log(Level.ALL, "��ȡ��ѯ��ʧ��");
				return null;
			}
			query = (TCComponentQuery) queryComponent;
			return query;
		} catch (TCException e) {
			logger.log(Level.ALL, "��ȡ��ѯ��"+query_class+"�쳣",e);
		}
		return null;
		
	}
	
	
	/**
	 * @param query
	 * @param values  ������������
	 * @param values  ����ֵ����
	 * @return	���ص��ǲ�ѯ�õ��Ľ������
	 * @throws TCException
	 */
	public static TCComponent[] getSearchResult(TCComponentQuery query,String[] propertyName,String[] values){
	
		TCComponent[] results = null;
		try {
			results = query.execute(propertyName, values);
		} catch (TCException e) {
			MessageBox.post("��ѯ����쳣","",MessageBox.ERROR);
			logger.log(Level.ALL,"��ѯ����쳣", e);
		}
		return results;
	}
	
	
}
