package com.uds.yl.Jr;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.tcutils.QueryUtil;

public class ExportExcelProjectMemorabilia {
	private String queryName = "U8_ProjRev";
	private TCComponentQuery query;
	private ArrayList<InnovationBean> innovationList;
	private ArrayList<DevelopmentBean> developmentList;
	private boolean ifGetInnovation = false;
	private boolean ifGetDevelopment = false;
	
	private String filePath = "";
	private String taskType = "";
	private String status = "";
	private String startDate = "";
	private String endDate = "";
	
	/***
	 * 创新任务-需要从U8_TaskRevision上获取的属性
	 */
	private String[] innovationProperties = new String[] { "item_id", "object_name", "date_released" };
	/***
	 * 创新任务-需要从U8_ProjectRevision上获取的属性
	 */
	private String[] innovationProperties1 = new String[] { "item_id", "object_name" };
	/***
	 * 创新任务-需要从U8_TaskRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event上获取的属性
	 */
	private String[] innovationProperties2 = new String[] { "u8_cychsdate", "u8_kxxfxfadate",
			"u8_kxxfxbgdate", " u8_lxsqdate" };
	/***
	 * 创新任务-需要从U8_ProjectRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event上获取的属性
	 */
	private String[] innovationProperties3 = new String[] { "u8_xmlxsdate", "u8_xmchfadate",
			"u8_tastetestdate", "u8_fxpgdate", "u8_wzdate", "u8_fdate", "u8_pdate",
			"u8_idate", "u8_zjdate", "u8_firstproddate", "u8_ysdate" };
	
	/***
	 * 研发任务-需要获取的属性
	 */
	private String[] developmentProperties = new String[] { "item_id", "object_name", "date_released" };
	
	private String[] developmentProperties1 = new String[] { "u8_rwsxdate", "u8_rwzjdate" };
	
	public ExportExcelProjectMemorabilia(String filePath, String taskType, String status, String startDate, String endDate) {
		this.filePath = filePath;
		this.taskType = taskType;
		this.status = status;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public void initGetDataAndExportWord() {
		query = InitializeQuery();
		if (query == null) {
			MessageBox.post("没有找到查询器\n" + queryName + "\n请联系管理员配置", "提示", MessageBox.INFORMATION);
		}
		
		innovationList = new ArrayList<InnovationBean>();
		developmentList = new ArrayList<DevelopmentBean>();
		
		if (!taskType.equals("研发任务资料版本")) {
			ifGetInnovation = true;
		}
		if (taskType.contains("研发任务资料版本")) {
			ifGetDevelopment = true;
		}
	}
	
	public void GetDateAndExportExcel() throws Exception {
		GetData();
		String newFileName = Export();
		MessageBox.post("汇总表生成成功\n\n文件所在位置如下:\n" + newFileName, "提示", MessageBox.INFORMATION);
		Runtime.getRuntime().exec("cmd  /c  start  " + newFileName);
	}
	
	private void GetData() throws Exception {
		TCComponent[] components = GetSearchResult(
				query, new String[]{ "所有权组", "类型", "创建时间晚于", "创建时间早于" }, 
				new String[]{ "*.液奶.伊利集团;液奶.伊利集团", taskType, startDate, endDate });
//				query, new String[]{ "所有权组", "零组件 ID" }, 
//				new String[]{ "*.液奶.伊利集团;液奶.伊利集团", "YYF-RW-CX-2018-123" });
		
		for (TCComponent component : components) {
			String type = component.getType();
			if (type.equals("U8_TaskRevision")) {
				// if--如果是创新任务(对应object_type为任务资料版本)
				
				// 获取U8_TaskRevision
				// 和U8_TaskRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event
				TCComponentItemRevision taskRevision = (TCComponentItemRevision)component;
				TCComponentForm taskRevisionForm = null;
				TCComponent[] components_1 = taskRevision.getReferenceListProperty("U8_Proj_Task_EventRel");
				for (TCComponent component_1 : components_1) {
					if (component_1 instanceof TCComponentForm) {
						taskRevisionForm = (TCComponentForm)component_1;
						break;
					}
				}
				
				// 获取U8_ProjectRevision
				// 和U8_ProjectRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event
				TCComponentItemRevision projectRevision = null;
				TCComponentForm projectRevisionForm = null;
				AIFComponentContext[] relatedComponents = taskRevision.whereReferenced();
				for (AIFComponentContext relatedComponent : relatedComponents) {
					InterfaceAIFComponent interfaceAIFComponent = relatedComponent.getComponent();
					if (interfaceAIFComponent instanceof TCComponentItemRevision
							&& interfaceAIFComponent.getType().equals("U8_ProjectRevision")) {
						// 如果被多个项目引用，只取第一个
						projectRevision = (TCComponentItemRevision)relatedComponent.getComponent();
						
						TCComponent[] components_2 = projectRevision.getReferenceListProperty("U8_Proj_Task_EventRel");
						for (TCComponent component_2 : components_2) {
							if (component_2 instanceof TCComponentForm) {
								projectRevisionForm = (TCComponentForm)component_2;
								break;
							}
						}
						break;
					}
				}
				
				// 获取属性
				String[] properties = null;
				String[] properties1 = null;
				String[] properties2 = null;
				String[] properties3 = null;
				
				properties = taskRevision.getProperties(innovationProperties);
				if (projectRevision != null) {
					properties1 = projectRevision.getProperties(innovationProperties1);
				}
				if (taskRevisionForm != null) {
					properties2 = taskRevisionForm.getProperties(innovationProperties2);
				}
				if (projectRevisionForm != null) {
					properties3 = projectRevisionForm.getProperties(innovationProperties3);
				}
				
				InnovationBean bean = new InnovationBean(properties, properties1, properties2, properties3);
				
				// 通过用户选择的任务状态进行过滤
				if (status.equals("未完成")) {
					if (projectRevision == null) {
						if (bean.projectProposal.equals("")) {
							continue;
						}
					} else {
						if (bean.projectAcceptance.equals("")) {
							continue;
						}
					}
				} else if (status.equals("完成")) {
					if (projectRevision == null) {
						if (!bean.projectProposal.equals("")) {
							continue;
						}
					} else {
						if (!bean.projectAcceptance.equals("")) {
							continue;
						}
					}
				}
				
				innovationList.add(bean);
			} else if (type.equals("U8_Task2Revision")) {
				// else if--如果是研发任务(对应object_type为研发任务资料版本)
				
				// 获取U8_Task2Revision
				// 和U8_Task2Revision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event
				TCComponentItemRevision taskRevision = (TCComponentItemRevision)component;
				TCComponentForm taskRevisionForm = null;
				TCComponent[] components_1 = taskRevision.getReferenceListProperty("U8_Proj_Task_EventRel");
				for (TCComponent component_1 : components_1) {
					if (component_1 instanceof TCComponentForm) {
						taskRevisionForm = (TCComponentForm)component_1;
						break;
					}
				}
				
				// 获取属性
				String[] properties = null;
				String[] properties1 = null;
				
				properties = taskRevision.getProperties(developmentProperties);
				if (taskRevisionForm != null) {
					properties1 = taskRevisionForm.getProperties(developmentProperties1);
				}
				
				DevelopmentBean bean = new DevelopmentBean(properties, properties1);
				
				// 通过用户选择的任务状态进行过滤
				if (status.equals("未完成")) {
					if (bean.taskSummary.equals("")) {
						continue;
					}
				} else if (status.equals("完成")) {
					if (!bean.taskSummary.equals("")) {
						continue;
					}
				}
				
				developmentList.add(bean);
			}
		}
		
		// 对innovationList进行排序
		Collections.sort(innovationList);
	}
	
	private String Export() throws Exception {
		Workbook wb = new XSSFWorkbook();
		
		// 表头格式
		CellStyle headCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints((short) 14);
		headCellStyle.setFont(font);
		headCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headCellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headCellStyle.setWrapText(true);
		
		// 偶数行格式
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		// 偶数行格式 For 任务名称列和项目名称列
		CellStyle cellStyle_1 = wb.createCellStyle();
		cellStyle_1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle_1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle_1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		cellStyle_1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle_1.setWrapText(true);
		
		// 奇数行格式
		CellStyle cellStyle1 = wb.createCellStyle();
		cellStyle1.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle1.setWrapText(true);
		
		// 奇数行格式 For 任务名称列和项目名称列
		CellStyle cellStyle1_1 = wb.createCellStyle();
		cellStyle1_1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		cellStyle1_1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle1_1.setWrapText(true);
		
		if (ifGetInnovation) {
			// 第一页--项目&创新任务
			// 设置表头
			Sheet sheet = wb.createSheet("项目&创新任务");
			String[] heads = new String[] { "任务编号", "任务名称", "项目编号", "项目名称",
					"任务需求", "创意策划书", "可行性分析研究方案", "项目建议书/可行性分析报告",
					"立项申请", "项目立项书", "项目策划方案", "口味调试", "集团质量安全风险评估",
					"技术文字提报", "配方", "工艺", "质量标准", "项目总结报告", "第一次生产启动通知", "项目验收评审" };
			
			int index = 0;
			Row headRrow = GetRow(sheet, 0);
			// 设置行高|注意行高会与自动换行cellStyle1.setWrapText(true);冲突
//			headRrow.setHeightInPoints(18);
			for (String head : heads) {
				// 设置行宽
				if (index == 0 || index == 1 || index == 3 || index == 7) {
					sheet.setColumnWidth(index, 24*256);
				} else {
					sheet.setColumnWidth(index, 16*256);
				}			
				
				Cell cell = GetCell(headRrow, index);
				cell.setCellValue(head);
				cell.setCellStyle(headCellStyle);
				index++;
			}
			// 设置内容
			int size = innovationList.size();
			for (int i = 0; i < size; i++) {
				int rowNum = i + 1;
				Row row = GetRow(sheet, rowNum);
				// 设置行高|注意行高会与自动换行cellStyle1.setWrapText(true);冲突
//				row.setHeightInPoints(15);
				
				InnovationBean bean = innovationList.get(i);
				// 修改日期格式 yyyy-M-d HH:mm 修改为 yyyy-M-d
				HandleTimeStrFormat(bean);
				
				// 奇偶数行采用不同的格式
				CellStyle style = null;
				CellStyle style1 = null;
				if (rowNum % 2 == 0) {
					style = cellStyle1;
					style1 = cellStyle1_1;
				} else {
					style = cellStyle;
					style1 = cellStyle_1;
				}
				
				GetCellWithStyle(row, 0, style).setCellValue(bean.taskNum);
				// 任务名称列 内容需要左对齐
				GetCellWithStyle(row, 1, style1).setCellValue(bean.taskName);	
				GetCellWithStyle(row, 2, style).setCellValue(bean.projectNum);
				// 项目名称列 内容需要左对齐
				GetCellWithStyle(row, 3, style1).setCellValue(bean.projectName);
				GetCellWithStyle(row, 4, style).setCellValue(bean.taskDemand);
				GetCellWithStyle(row, 5, style).setCellValue(bean.CreativePlanning);
				GetCellWithStyle(row, 6, style).setCellValue(bean.feasibility);
				GetCellWithStyle(row, 7, style).setCellValue(bean.projectProposal);
				GetCellWithStyle(row, 8, style).setCellValue(bean.projectApplication);
				GetCellWithStyle(row, 9, style).setCellValue(bean.projectApproval);
				GetCellWithStyle(row, 10, style).setCellValue(bean.projectPlanning);
				GetCellWithStyle(row, 11, style).setCellValue(bean.tasteDebugging);
				GetCellWithStyle(row, 12, style).setCellValue(bean.safetyRisk);
				GetCellWithStyle(row, 13, style).setCellValue(bean.technicalWords);
				GetCellWithStyle(row, 14, style).setCellValue(bean.formula);
				GetCellWithStyle(row, 15, style).setCellValue(bean.process);
				GetCellWithStyle(row, 16, style).setCellValue(bean.qualityStandard);
				GetCellWithStyle(row, 17, style).setCellValue(bean.projectSummary);
				GetCellWithStyle(row, 18, style).setCellValue(bean.firstNotification);
				GetCellWithStyle(row, 19, style).setCellValue(bean.projectAcceptance);
			}
		}
		if (ifGetDevelopment) {
			// 第二页--项目&创新任务
			// 设置表头
			Sheet sheet1 = wb.createSheet("研发任务");
			String[] heads1 = new String[] { "任务编号", "任务名称", "任务需求", "任务实现", "任务总结报告" };
			
			int index1 = 0;
			Row headRrow1 = GetRow(sheet1, 0);
			// 设置行高|注意行高会与自动换行cellStyle1.setWrapText(true);冲突
//			headRrow.setHeightInPoints(18);
			for (String head : heads1) {
				// 设置行宽
				if (index1 == 0 || index1 == 1 || index1 == 3 || index1 == 7) {
					sheet1.setColumnWidth(index1, 24*256);
				} else {
					sheet1.setColumnWidth(index1, 16*256);
				}			
				
				Cell cell = GetCell(headRrow1, index1);
				cell.setCellValue(head);
				cell.setCellStyle(headCellStyle);
				index1++;
			}
			// 设置内容
			int size1 = developmentList.size();
			for (int i = 0; i < size1; i++) {
				int rowNum = i + 1;
				Row row = GetRow(sheet1, rowNum);
				// 设置行高|注意行高会与自动换行cellStyle1.setWrapText(true);冲突
//				row.setHeightInPoints(15);
				
				DevelopmentBean bean = developmentList.get(i);
				// 修改日期格式 yyyy-M-d HH:mm 修改为 yyyy-M-d
				HandleTimeStrFormat1(bean);
				
				// 奇偶数行采用不同的格式
				CellStyle style = null;
				CellStyle style1 = null;
				if (rowNum % 2 == 0) {
					style = cellStyle1;
					style1 = cellStyle1_1;
				} else {
					style = cellStyle;
					style1 = cellStyle_1;
				}
				
				GetCellWithStyle(row, 0, style).setCellValue(bean.taskNum);
				// 任务名称列 内容需要左对齐
				GetCellWithStyle(row, 1, style1).setCellValue(bean.taskName);
				GetCellWithStyle(row, 2, style).setCellValue(bean.taskDemand);
				GetCellWithStyle(row, 3, style).setCellValue(bean.taskImplementation);
				GetCellWithStyle(row, 4, style).setCellValue(bean.taskSummary);
			}
		}
		
		String newFile = filePath + "\\项目大事记" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
		File outFile = new File(newFile);
		if (outFile.exists()) {
			outFile.delete();
		}
		FileOutputStream out = new FileOutputStream(outFile);
		wb.write(out);
		out.close();
		
		return newFile;
	}
	
	private Row GetRow(Sheet sheet, int index) {
		Row row = sheet.getRow(index);
		if (row == null) {
			row = sheet.createRow(index);
		}
		return row;
	}
	
	private Cell GetCell(Row row, int index) {
		Cell cell = row.getCell(index);
		if (cell == null) {
			cell = row.createCell(index);
		}
		return cell;
	}
	
	private Cell GetCellWithStyle(Row row, int index, CellStyle cellStyle) {
		Cell cell = row.getCell(index);
		if (cell == null) {
			cell = row.createCell(index);
		}
		cell.setCellStyle(cellStyle);
		return cell;
	}
	
	private void HandleTimeStrFormat(InnovationBean bean) {
		bean.taskDemand = SetTimeStrFormat(bean.taskDemand);
		bean.CreativePlanning = SetTimeStrFormat(bean.CreativePlanning);
		bean.feasibility = SetTimeStrFormat(bean.feasibility);
		bean.projectProposal = SetTimeStrFormat(bean.projectProposal);
		bean.projectApplication = SetTimeStrFormat(bean.projectApplication);
		bean.projectApproval = SetTimeStrFormat(bean.projectApproval);
		bean.projectPlanning = SetTimeStrFormat(bean.projectPlanning);
		bean.tasteDebugging = SetTimeStrFormat(bean.tasteDebugging);
		bean.safetyRisk = SetTimeStrFormat(bean.safetyRisk);
		bean.technicalWords = SetTimeStrFormat(bean.technicalWords);
		bean.formula = SetTimeStrFormat(bean.formula);
		bean.process = SetTimeStrFormat(bean.process);
		bean.qualityStandard = SetTimeStrFormat(bean.qualityStandard);
		bean.projectSummary = SetTimeStrFormat(bean.projectSummary);
		bean.firstNotification = SetTimeStrFormat(bean.firstNotification);
		bean.projectAcceptance = SetTimeStrFormat(bean.projectAcceptance);
	}
	
	private void HandleTimeStrFormat1(DevelopmentBean bean) {
		bean.taskDemand = SetTimeStrFormat(bean.taskDemand);
		bean.taskImplementation = SetTimeStrFormat(bean.taskImplementation);
		bean.taskSummary = SetTimeStrFormat(bean.taskSummary);		
	}
	
	private String SetTimeStrFormat(String timeStr) {
		if (timeStr.equals("")) {
			return "";
		} else {
			return timeStr.split(" ")[0];
		}
	}
	
	private TCComponentQuery InitializeQuery(){
		TCComponentQuery query = QueryUtil.getTCComponentQuery(queryName);
		return query;
	}
	
	public TCComponent[] GetSearchResult(TCComponentQuery query, String[] propertyName, String[] values) throws Exception {
		TCComponent[] results = query.execute(propertyName, values);
		return results;
	}
}

/***
 * 创新任务的属性类
 */
class InnovationBean implements Comparable<InnovationBean> {
	// U8_Task的属性，可以在U8_TaskRevisio中取到
	String taskNum = ""; // 任务编号--item_id
	
	// U8_TaskRevisio的属性
	String taskName = ""; // 任务名称--object_name
	
	// U8_Project的属性
	String projectNum = ""; // 项目编号--item_id
	
	// U8_ProjectRevision的属性
	String projectName = ""; // 项目名称--object_name
	
	// U8_TaskRevision的属性
	String taskDemand = ""; // 任务需求--date_released
	
	// U8_TaskRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event的属性
	String CreativePlanning = ""; // 创意策划书--u8_cychsdate
	String feasibility = ""; // 可行性分析研究方案--u8_kxxfxfadate
	String projectProposal = ""; // 项目建议书/可行性分析报告--u8_kxxfxbgdate
	String projectApplication = ""; // 立项申请--u8_lxsqdate
	
	// U8_ProjectRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event的属性
	String projectApproval = ""; // 项目立项书--u8_xmlxsdate
	String projectPlanning = ""; // 项目策划方案--u8_xmchfadate
	String tasteDebugging = ""; // 口味调试--u8_tastetestdate
	String safetyRisk = ""; // 集团质量安全风险评估--u8_fxpgdate
	String technicalWords = ""; // 技术文字提报--u8_wzdate
	String formula = ""; // 配方--u8_fdate
	String process = ""; // 工艺--u8_pdate
	String qualityStandard = ""; // 质量标准--u8_idate
	String projectSummary = ""; // 项目总结报告--u8_zjdate
	String firstNotification = ""; // 第一次生产启动通知--u8_firstproddate
	String projectAcceptance = ""; // 项目验收评审--u8_ysdate
	
	public InnovationBean(String[] properties, String[] properties1, String[] properties2, String[] properties3) {
		this.taskNum = properties[0];
		this.taskName = properties[1];
		this.taskDemand = properties[2];
		
		if (properties1 != null) {
			this.projectNum = properties1[0];
			this.projectName = properties1[1];
		}
		if (properties2 != null) {
			this.CreativePlanning = properties2[0];
			this.feasibility = properties2[1];
			this.projectProposal = properties2[2];
			this.projectApplication = properties2[3];
		}
		if (properties3 != null) {
			this.projectApproval = properties3[0];
			this.projectPlanning = properties3[1];
			this.tasteDebugging = properties3[2];
			this.safetyRisk = properties3[3];
			this.technicalWords = properties3[4];
			this.formula = properties3[5];
			this.process = properties3[6];
			this.qualityStandard = properties3[7];
			this.projectSummary = properties3[8];
			this.firstNotification = properties3[9];
			this.projectAcceptance = properties3[10];
		}
	}

	@Override
	public int compareTo(InnovationBean arg0) {
		return this.taskNum.compareTo(arg0.taskNum);
//		return 0;
	}
}

/***
 * 研发任务的属性类
 */
class DevelopmentBean implements Comparable<InnovationBean> {
	// U8_Task2的属性，可以在U8_TaskRevisio中取到
	String taskNum = ""; // 任务编号--item_id
	// U8_Task2Revisio的属性
	String taskName = ""; // 任务名称--object_name
	String taskDemand = ""; // 任务需求--date_released
	
	// U8_Task2Revision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event的属性
	String taskImplementation = ""; // 任务实现--u8_rwsxdate
	String taskSummary = ""; // 任务总结报告--u8_rwzjdate
	
	public 	DevelopmentBean(String[] properties, String[] properties1) {
		this.taskNum = properties[0];
		this.taskName = properties[1];
		this.taskDemand = properties[2];
		
		if (properties1 != null) {
			this.taskImplementation = properties1[0];
			this.taskSummary = properties1[1];
		}
	}

	@Override
	public int compareTo(InnovationBean arg0) {
		return this.taskNum.compareTo(arg0.taskNum);
//		return 0;
	}
	
}