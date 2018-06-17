package com.uds.yl.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.ProjectStatisticsBean;
import com.uds.yl.bean.WorkStatisticsBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.service.IProjectStatisticsService;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.LogFactory;


public class ProjectStatisticsServiceImpl implements IProjectStatisticsService {
	Logger logger = LogFactory.initLog("ProjectStatisticsServiceImpl", LogLevel.ERROE.getValue());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IProjectStatisticsService#getQueryScheduleList(java.
	 * lang.String, java.lang.String, java.lang.String, java.lang.String)
	 * 通过查询获取符合的时间表
	 */
	@Override
	public List<TCComponentSchedule> getQueryScheduleList(String dateStart, String dateEnd, String startOrCompleteStr) {
		List<TCComponentSchedule> scheduleList = new ArrayList<>();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
			Date startDate = dateFormat.parse(dateStart);
			Date endDate = dateFormat.parse(dateEnd);
			dateStart = dateFormat.format(startDate);
			dateEnd = dateFormat.format(endDate);
			// 查询
			TCComponentQuery scheduleQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8SCHEDULE.getValue());
			if (scheduleQuery == null) {
				logger.log(Level.ALL, "U8Schedule查询器获取失败");
				return scheduleList;
			}
			TCComponent[] searchResult = null;
			if (Const.ProjectStatistics.SCHEDULE_COMPLETE.equals(startOrCompleteStr)) {// 时间段内完成
				searchResult = QueryUtil.getSearchResult(scheduleQuery,
						new String[] { Const.Pro_Statistics_Query_Condition.FINISH_DATE_AFTER,
								Const.Pro_Statistics_Query_Condition.FINISH_DATE_AFTER },
						new String[] { dateStart,dateEnd });

			} else if (Const.ProjectStatistics.SCHEDULE_START.equals(startOrCompleteStr)) {// 时间段内开始
				searchResult = QueryUtil.getSearchResult(scheduleQuery,
						new String[] { Const.Pro_Statistics_Query_Condition.START_DATE_AFTER,
								Const.Pro_Statistics_Query_Condition.START_DATE_BEFORE },
						new String[] { dateStart,dateEnd });
			}

			if (searchResult == null || searchResult.length == 0) {
				logger.log(Level.ALL, "没有查询到条件下的时间表");
				return scheduleList;
			}

			for (TCComponent component : searchResult) {
				TCComponentSchedule scheduleTemp = (TCComponentSchedule) component;
				scheduleList.add(scheduleTemp);

			}

		} catch (ParseException e) {
			logger.log(Level.ALL, "Date转换异常", e);
		}

		return scheduleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IProjectStatisticsService#generateScheduleBeanList(
	 * java.util.List) 根据查询的到的时间表封装对应的Bean对象
	 */
	@Override
	public List<ProjectStatisticsBean> generateScheduleBeanList(List<TCComponentSchedule> scheduleList) {
		List<ProjectStatisticsBean> scheduleBeanList = new ArrayList<>();
		try {
			for (TCComponentSchedule schedule : scheduleList) {
				ProjectStatisticsBean scheduleBean = AnnotationFactory.getInstcnce(ProjectStatisticsBean.class,
						schedule);
				scheduleBean.currentTaskList = new ArrayList<>();
				// 为每一个schedule查找到在执行的TASK
				TCComponent[] allTasks = schedule.getAllTasks();
				for (TCComponent taskComponent : allTasks) {
					TCComponentScheduleTask scheduleTask = (TCComponentScheduleTask) taskComponent;
					String state = scheduleTask.getProperty("fnd0state");
					if (Const.ProjectStatistics.PROGRESSING_STATE.equals(state)) {// 进行中的任务
						WorkStatisticsBean workStatisticsBean = AnnotationFactory.getInstcnce(WorkStatisticsBean.class,
								scheduleTask);
						scheduleBean.currentTaskList.add(workStatisticsBean);
					}
				}
				scheduleBeanList.add(scheduleBean);

			}
		} catch (TCException e) {
			logger.log(Level.ALL, "属性获取异常", e);
		} catch (InstantiationException e) {
			logger.log(Level.ALL, "注解工厂获取Bean异常", e);
		} catch (IllegalAccessException e) {
			logger.log(Level.ALL, "注解工厂非法参数异常", e);
		}
		return scheduleBeanList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IProjectStatisticsService#writeSchedule2Excel(java.
	 * util.List) 项目情况统计表
	 */
	@Override
	public void writeSchedule2Excel(List<ProjectStatisticsBean> projectStatisticsBeans) {

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("项目执行情况");
		sheet.setDefaultColumnWidth((short) 20);
		XSSFCellStyle style = wb.createCellStyle();
		XSSFCellStyle style1 = wb.createCellStyle();
		Row row = sheet.createRow(0);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Cell cell = row.createCell((short) 0);
		cell.setCellValue("项目编号");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("项目名称");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("项目情况");
		cell.setCellStyle(style);

		cell = row.createCell((short) 3);
		cell.setCellValue("开始时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("计划完成时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("当前任务");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("当前任务负责人");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("当前任务状态");
		cell.setCellStyle(style);

		cell = row.createCell((short) 8);
		cell.setCellValue("当前任务计划完成时间");
		cell.setCellStyle(style);
		int k = 0;
		for (int i = 0; i < projectStatisticsBeans.size(); i++) {
			if (projectStatisticsBeans.get(i).currentTaskList.size() == 0) {
				XSSFRow row1 = sheet.createRow(k + 1);
				k++;
				XSSFCell cell2 = row1.createCell(0);
				cell2.setCellValue(projectStatisticsBeans.get(i).itemId);
				cell2 = row1.createCell(1);
				cell2.setCellValue(projectStatisticsBeans.get(i).objectName);
				cell2 = row1.createCell(2);
				cell2.setCellValue(projectStatisticsBeans.get(i).projectState);
				cell2 = row1.createCell(3);
				cell2.setCellValue(projectStatisticsBeans.get(i).startDate);
				cell2 = row1.createCell(4);
				cell2.setCellValue(projectStatisticsBeans.get(i).finishDate);
			} else {
				for (int j = 0; j < projectStatisticsBeans.get(i).currentTaskList.size(); j++) {
					XSSFRow row1 = sheet.createRow(k + 1);
					k++;
					XSSFCell cell2 = row1.createCell(0);
					cell2.setCellValue(projectStatisticsBeans.get(i).itemId);
					cell2 = row1.createCell(1);
					cell2.setCellValue(projectStatisticsBeans.get(i).objectName);
					cell2 = row1.createCell(2);
					cell2.setCellValue(projectStatisticsBeans.get(i).projectState);
					cell2 = row1.createCell(3);
					cell2.setCellValue(projectStatisticsBeans.get(i).startDate);
					cell2 = row1.createCell(4);
					cell2.setCellValue(projectStatisticsBeans.get(i).finishDate);
					cell2 = row1.createCell(5);
					cell2.setCellValue(projectStatisticsBeans.get(i).getCurrentTaskList().get(j).objectName);
					cell2 = row1.createCell(6);
					cell2.setCellValue(projectStatisticsBeans.get(i).getCurrentTaskList().get(j).assignment);
					cell2 = row1.createCell(7);
					cell2.setCellValue(projectStatisticsBeans.get(i).getCurrentTaskList().get(j).state);
					cell2 = row1.createCell(8);
					cell2.setCellValue(projectStatisticsBeans.get(i).getCurrentTaskList().get(j).finishDate);

				}

			}
		}

		try {
			FileOutputStream out = new FileOutputStream(Const.ProjectStatistics.PROJECTSTATISTICS_EXCEL_PATH);
			wb.write(out);
			out.close();
			// MessageBox.post("生成成功","",MessageBox.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Runtime.getRuntime().exec("cmd /c start " + Const.ProjectStatistics.PROJECTSTATISTICS_EXCEL_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IProjectStatisticsService#getQueryScheduleTaskList(
	 * java.lang.String, java.lang.String, java.lang.String) 通过查询获取符合的时间表任务的List
	 */
	@Override
	public List<TCComponentScheduleTask> getQueryScheduleTaskList(String dateStart, String dateEnd,
			String startOrCompleteStr) {
		List<TCComponentScheduleTask> scheduleTaskList = new ArrayList<>();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
			Date startDate = dateFormat.parse(dateStart);
			Date endDate = dateFormat.parse(dateEnd);
			dateStart = dateFormat.format(startDate);
			dateEnd = dateFormat.format(endDate);
			// 查询
			TCComponentQuery scheduleQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8TASK.getValue());
			if (scheduleQuery == null) {
				logger.log(Level.ALL, "U8Task查询器获取失败");
				return scheduleTaskList;
			}
			TCComponent[] searchResult = null;
			if (Const.ProjectStatistics.SCHEDULE_COMPLETE.equals(startOrCompleteStr)) {// 时间段内完成
				searchResult = QueryUtil.getSearchResult(scheduleQuery,
						new String[] { Const.Pro_Statistics_Query_Condition.FINISH_DATE_AFTER,
								Const.Pro_Statistics_Query_Condition.FINISH_DATE_AFTER },
						new String[] { dateStart,dateEnd });

			} else if (Const.ProjectStatistics.SCHEDULE_START.equals(startOrCompleteStr)) {// 时间段内开始
				searchResult = QueryUtil.getSearchResult(scheduleQuery,
						new String[] { Const.Pro_Statistics_Query_Condition.START_DATE_AFTER,
								Const.Pro_Statistics_Query_Condition.START_DATE_BEFORE },
						new String[] { dateStart,dateEnd });
			}

			if (searchResult == null || searchResult.length == 0) {
				logger.log(Level.ALL, "没有查询到条件下的时间表任务");
				return scheduleTaskList;
			}

			for (TCComponent component : searchResult) {
				TCComponentScheduleTask scheduleTemp = (TCComponentScheduleTask) component;
				scheduleTaskList.add(scheduleTemp);

			}

		} catch (ParseException e) {
			logger.log(Level.ALL, "Date转换异常", e);
		}
		return scheduleTaskList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IProjectStatisticsService#generateScheduleTaskBeanList
	 * (java.util.List) 根据查询到的时间表任务生成对应Bean对象List
	 */
	@Override
	public List<WorkStatisticsBean> generateScheduleTaskBeanList(List<TCComponentScheduleTask> scheduleTaskList) {
		List<WorkStatisticsBean> scheduleTaskBeanList = new ArrayList<>();
		try {
			for(TCComponentScheduleTask scheduleTask : scheduleTaskList){
				WorkStatisticsBean scheduleTaskBean = AnnotationFactory.getInstcnce(WorkStatisticsBean.class, scheduleTask);
				//多一个属性 任务对应的项目名称
				scheduleTaskBeanList.add(scheduleTaskBean);
			}
		} catch (InstantiationException e) {
			logger.log(Level.ALL, "注解工厂获取Bean异常", e);
		} catch (IllegalAccessException e) {
			logger.log(Level.ALL, "注解工厂非法参数异常", e);
		}
		//最后排下顺序
		List<WorkStatisticsBean> scheduleTaskBeanListFinal = new ArrayList<>();
		LinkedHashSet<String> nameSet = new LinkedHashSet<>();
		for(WorkStatisticsBean bean : scheduleTaskBeanList){
			nameSet.add(bean.assignment);
		}
		
		for(String name:nameSet){
			for(WorkStatisticsBean bean : scheduleTaskBeanList){
				if(name.equals(bean.assignment)){
					scheduleTaskBeanListFinal.add(bean);
				}
			}
		}
		scheduleTaskBeanList.clear();
		return scheduleTaskBeanListFinal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IProjectStatisticsService#writeScheduleTask2Excel(java
	 * .util.List) 工作情况统计表
	 */
	@Override
	public void writeScheduleTask2Excel(List<WorkStatisticsBean> workStatisticsBeans) {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("工作执行情况");
		sheet.setDefaultColumnWidth((short) 20);
		XSSFCellStyle style = wb.createCellStyle();
		XSSFCellStyle style1 = wb.createCellStyle();
		Row row = sheet.createRow(0);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		Cell cell = row.createCell((short) 0);
		cell.setCellValue("人员名册");
		cell.setCellStyle(style);

		cell = row.createCell((short) 1);
		cell.setCellValue("关联项目名称");
		cell.setCellStyle(style);

		cell = row.createCell((short) 2);
		cell.setCellValue("关联项目编号");
		cell.setCellStyle(style);
		
		cell = row.createCell((short) 3);
		cell.setCellValue("参与任务");
		cell.setCellStyle(style);

		cell = row.createCell((short) 4);
		cell.setCellValue("计划开始时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 5);
		cell.setCellValue("实际开始时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 6);
		cell.setCellValue("计划完成时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 7);
		cell.setCellValue("实际完成时间");
		cell.setCellStyle(style);

		cell = row.createCell((short) 8);
		cell.setCellValue("当前任务状态");
		cell.setCellStyle(style);

		int k = 0;
		for (int i = 0; i < workStatisticsBeans.size(); i++) {

			XSSFRow row1 = sheet.createRow(k + 1);
			k++;
			XSSFCell cell2 = row1.createCell(0);
			cell2.setCellValue(workStatisticsBeans.get(i).assignment);
			cell2 = row1.createCell(1);
			cell2.setCellValue(workStatisticsBeans.get(i).prjName);
//			cell2 = row1.createCell(2);    这里这个是关联的项目ID  暂时为空
//			cell2.setCellValue(workStatisticsBeans.get(i).objectName);
			cell2 = row1.createCell(3);
			cell2.setCellValue(workStatisticsBeans.get(i).objectName);
			cell2 = row1.createCell(4);
			cell2.setCellValue(workStatisticsBeans.get(i).startDate);
			cell2 = row1.createCell(5);
			cell2.setCellValue(workStatisticsBeans.get(i).actualStartDate);
			cell2 = row1.createCell(6);
			cell2.setCellValue(workStatisticsBeans.get(i).finishDate);
			cell2 = row1.createCell(7);
			cell2.setCellValue(workStatisticsBeans.get(i).actualFinishDate);
			cell2 = row1.createCell(8);
			cell2.setCellValue(workStatisticsBeans.get(i).state);
		}

		try {
			FileOutputStream out = new FileOutputStream(Const.ProjectStatistics.WORKSTATISTICS_EXCEL_PATH);
			wb.write(out);
			out.close();
			
			//TODO:将文件上传到什么位置:
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				Runtime.getRuntime().exec("cmd /c start " + Const.ProjectStatistics.WORKSTATISTICS_EXCEL_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
