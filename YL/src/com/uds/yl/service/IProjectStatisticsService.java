package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.uds.yl.bean.ProjectStatisticsBean;
import com.uds.yl.bean.WorkStatisticsBean;

public interface IProjectStatisticsService {
	
	//通过查询获取符合的时间表
	List<TCComponentSchedule> getQueryScheduleList(String dateStart,String dateEnd,String startOrCompleteStr);
	
	//根据查询的到的时间表封装对应的Bean对象
	List<ProjectStatisticsBean> generateScheduleBeanList(List<TCComponentSchedule> scheduleList);
	
	//项目情况统计表
	void writeSchedule2Excel(List<ProjectStatisticsBean> projectStatisticsBeans);
	
	//通过查询获取符合的
	List<TCComponentScheduleTask> getQueryScheduleTaskList(String dateStart,String dateEnd,String startOrCompleteStr);
	
	//根据查询到的时间表任务封装对应的Bean对象
	List<WorkStatisticsBean> generateScheduleTaskBeanList(List<TCComponentScheduleTask> scheduleTaskList);
	
	//工作情况统计表
	void writeScheduleTask2Excel(List<WorkStatisticsBean> workStatisticsBeans);
	
	
}
