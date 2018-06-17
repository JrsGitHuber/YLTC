package com.uds.yl.bean;


import com.uds.yl.annotation.FieldAnotation;

public class WorkStatisticsBean {
	
	@FieldAnotation(value="ResourceAssignment")
	public String assignment;//负责人
	
	@FieldAnotation(value="object_name")
	public String objectName;//任务的名称
	
	@FieldAnotation(value="start_date")
	public String startDate;//计划开始时间
	
	@FieldAnotation(value="actual_start_date")
	public String actualStartDate;//实际开始时间
	
	@FieldAnotation(value="finish_date")
	public String finishDate;//计划完成时间
	
	@FieldAnotation(value="actual_finish_date")
	public String actualFinishDate;//实际完成时间
	
	@FieldAnotation(value="fnd0state")
	public String state;//状态
	
	@FieldAnotation(value="schedule_tag")
	public String prjName;//项目名称

	public String getAssignment() {
		return assignment;
	}

	public void setAssignment(String assignment) {
		this.assignment = assignment;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(String actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public String getActualFinishDate() {
		return actualFinishDate;
	}

	public void setActualFinishDate(String actualFinishDate) {
		this.actualFinishDate = actualFinishDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPrjName() {
		return prjName;
	}

	public void setPrjName(String prjName) {
		this.prjName = prjName;
	}
	
	
	
	
}
