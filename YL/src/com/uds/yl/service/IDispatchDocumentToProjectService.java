package com.uds.yl.service;

import java.util.List;


import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.uds.yl.bean.DispatchLogBean;

public interface IDispatchDocumentToProjectService {
	//指派到项目组中  并返回指派的信息记录
	public List<DispatchLogBean> assignToProject(List<TCComponentDataset> docDataSetList,List<TCComponentProject> projectList);
	
	//从项目组中移除  并返回移除的信息记录
	public List<DispatchLogBean> removeFromProject(List<TCComponentDataset> docDataSetList,List<TCComponentProject> projectList);
	
	//根据任务节点获取所有数据集
	public List<TCComponentDataset> getDateSetList(TCComponentTask task);
	
	//根据项目的ID获取项目对象
	public List<TCComponentProject> getProjectListByNames(List<String> projectIDList);
	
	//将要记录记录下来
	public void logInfo(List<DispatchLogBean> dispatchLogBeanList);
	
}
