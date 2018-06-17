package com.uds.yl.service;

import java.util.List;


import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.uds.yl.bean.DispatchLogBean;

public interface IDispatchDocumentToProjectService {
	//ָ�ɵ���Ŀ����  ������ָ�ɵ���Ϣ��¼
	public List<DispatchLogBean> assignToProject(List<TCComponentDataset> docDataSetList,List<TCComponentProject> projectList);
	
	//����Ŀ�����Ƴ�  �������Ƴ�����Ϣ��¼
	public List<DispatchLogBean> removeFromProject(List<TCComponentDataset> docDataSetList,List<TCComponentProject> projectList);
	
	//��������ڵ��ȡ�������ݼ�
	public List<TCComponentDataset> getDateSetList(TCComponentTask task);
	
	//������Ŀ��ID��ȡ��Ŀ����
	public List<TCComponentProject> getProjectListByNames(List<String> projectIDList);
	
	//��Ҫ��¼��¼����
	public void logInfo(List<DispatchLogBean> dispatchLogBeanList);
	
}
