package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.DispatchLogBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.service.IDispatchDocumentToProjectService;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.LogFactory;


public class DispatchDocumentToProjectImpl implements IDispatchDocumentToProjectService{
	private Logger logger = LogFactory.initLog("DispatchDocumentToProjectImpl", LogLevel.ERROE.getValue());
	private File logFile = new File(Const.DispatchDocumentToProject.LOG_FILE_PATH);//记录信息的文件
	/* (non-Javadoc)
	 * 讲文档指派的到项目组中   并同时生成指派记录
	 */
	@Override
	public List<DispatchLogBean> assignToProject(List<TCComponentDataset> docDataSetList,
			List<TCComponentProject> projectList) {
		List<DispatchLogBean> assignLogBeanList = new ArrayList<>();
		for(TCComponentDataset docDataSet : docDataSetList){
			String dataSetName = "";
			String receiverName = "";
			try {
				dataSetName = docDataSet.getProperty("object_name");
			} catch (TCException e1) {
				e1.printStackTrace();
			}
			for(TCComponentProject project : projectList){
				try {
					receiverName = project.getProperty("object_name");
					project.assignToProject(new TCComponentDataset[]{docDataSet});
				} catch (TCException e) {
					e.printStackTrace();
				}
				DispatchLogBean bean = new DispatchLogBean();
				bean.time = Utils.getCurrentDateStr(Const.DispatchDocumentToProject.DATA_TYPE);
				bean.sendor = Utils.getCurrentUserName();
				bean.handleType = Const.DispatchDocumentToProject.SNED_TYPE;
				bean.data = dataSetName;
				bean.receiver = receiverName;
				assignLogBeanList.add(bean);
			}
		}
		
		return assignLogBeanList;
	}

	/* (non-Javadoc)
	 * 从项目 组中移除文档   并同时生成移除的记录
	 */
	@Override
	public List<DispatchLogBean> removeFromProject(List<TCComponentDataset> docDataSetList,
			List<TCComponentProject> projectList) {
		List<DispatchLogBean> removeLogBeanList = new ArrayList<>();
		for(TCComponentDataset docDataSet : docDataSetList){
			String dataSetName = "";
			String receiverName = "";
			try {
				dataSetName = docDataSet.getProperty("object_name");
			} catch (TCException e1) {
				e1.printStackTrace();
			}
			for(TCComponentProject project : projectList){
				try {
					receiverName = project.getProperty("object_name");
					project.removeFromProject(new TCComponentDataset[]{docDataSet});
				} catch (TCException e) {
					e.printStackTrace();
				}
				DispatchLogBean bean = new DispatchLogBean();
				bean.time = Utils.getCurrentDateStr(Const.DispatchDocumentToProject.DATA_TYPE);
				bean.sendor = Utils.getCurrentUserName();
				bean.handleType = Const.DispatchDocumentToProject.REMOVE_TYPE;
				bean.data = dataSetName;
				bean.receiver = receiverName;
				removeLogBeanList.add(bean);
			}
		}
		return removeLogBeanList;
	}

	
	/* (non-Javadoc)
	 * 讲指派或者移除的记录写到文档中
	 */
	@Override
	public void logInfo(List<DispatchLogBean> dispatchLogBeanList) {
		initLogFile();//初始化发文日志文件
		
		String oneRecord = "";
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(new FileOutputStream(logFile,true));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		for(DispatchLogBean bean : dispatchLogBeanList){
			oneRecord = "[ "+ bean.time +" ]"
					+" : "+bean.sendor
					+"  [ "+ bean.handleType +" ]"
					+"	"+bean.data
					+"	--->"+bean.receiver;
			try {
				osw.append(oneRecord+System.lineSeparator());
				osw.flush();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(osw!=null){
			try {
				osw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 初始化日志文件，存在就使用
	 * 不存在就创建
	 * 或者按照日期删除重新创建
	 */
	public void initLogFile(){
		if(!logFile.exists()){//不存在
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				logger.info("创建发文系统日志文件异常");e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * 根据任务节点获取所有数据集合
	 */
	@Override
	public List<TCComponentDataset> getDateSetList(TCComponentTask task) {
		List<TCComponentDataset> dateSetList = new ArrayList<>();
		try {
			TCComponent[] relatedDateSets = task.getRelatedComponents(Const.DispatchDocumentToProject.TASK_TARGET_RELATED);
			for(TCComponent component : relatedDateSets){
				TCComponentDataset dateSet = (TCComponentDataset) component;
				dateSetList.add(dateSet);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return dateSetList;
	}

	/* (non-Javadoc)
	 * 根据项目ID集合获取对应的项目对象
	 */
	@Override
	public List<TCComponentProject> getProjectListByNames(List<String> projectIDList) {
		List<TCComponentProject> projectList = new ArrayList<>();
		
		TCComponentQuery projectQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8_PROJECT.getValue());
		for(String projectID : projectIDList){
			TCComponent[] projectQueryResult = QueryUtil.getSearchResult(projectQuery, new String[]{Const.DispatchDocumentToProject.PROJECT_QUERY}, new String[]{projectID});
			for(TCComponent component : projectQueryResult){
				TCComponentProject project = (TCComponentProject) component;
				projectList.add(project);
			}
		}
		
		return projectList;
	}
	

}
