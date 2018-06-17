package com.uds.yl.service;

import java.util.List;

import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.uds.yl.bean.ProjectStatisticsBean;
import com.uds.yl.bean.WorkStatisticsBean;

public interface IProjectStatisticsService {
	
	//ͨ����ѯ��ȡ���ϵ�ʱ���
	List<TCComponentSchedule> getQueryScheduleList(String dateStart,String dateEnd,String startOrCompleteStr);
	
	//���ݲ�ѯ�ĵ���ʱ����װ��Ӧ��Bean����
	List<ProjectStatisticsBean> generateScheduleBeanList(List<TCComponentSchedule> scheduleList);
	
	//��Ŀ���ͳ�Ʊ�
	void writeSchedule2Excel(List<ProjectStatisticsBean> projectStatisticsBeans);
	
	//ͨ����ѯ��ȡ���ϵ�
	List<TCComponentScheduleTask> getQueryScheduleTaskList(String dateStart,String dateEnd,String startOrCompleteStr);
	
	//���ݲ�ѯ����ʱ��������װ��Ӧ��Bean����
	List<WorkStatisticsBean> generateScheduleTaskBeanList(List<TCComponentScheduleTask> scheduleTaskList);
	
	//�������ͳ�Ʊ�
	void writeScheduleTask2Excel(List<WorkStatisticsBean> workStatisticsBeans);
	
	
}
