package com.uds.yl.bean;

import java.util.List;

import com.uds.yl.annotation.FieldAnotation;

public class ProjectStatisticsBean {
	@FieldAnotation(value="object_name")
	public String objectName;
	
	@FieldAnotation(value="item_id")
	public String itemId;
	
	@FieldAnotation(value="fnd0SSTState")
	public String projectState;	
	
	@FieldAnotation(value="start_date")
	public String startDate;
	
	@FieldAnotation(value="finishDate")
	public String finishDate;
	
	public List<WorkStatisticsBean> currentTaskList;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getProjectState() {
		return projectState;
	}

	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public List<WorkStatisticsBean> getCurrentTaskList() {
		return currentTaskList;
	}

	public void setCurrentTaskList(List<WorkStatisticsBean> currentTaskList) {
		this.currentTaskList = currentTaskList;
	}
	

	
}
