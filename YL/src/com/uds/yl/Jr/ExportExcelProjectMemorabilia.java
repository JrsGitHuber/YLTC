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
	 * ��������-��Ҫ��U8_TaskRevision�ϻ�ȡ������
	 */
	private String[] innovationProperties = new String[] { "item_id", "object_name", "date_released" };
	/***
	 * ��������-��Ҫ��U8_ProjectRevision�ϻ�ȡ������
	 */
	private String[] innovationProperties1 = new String[] { "item_id", "object_name" };
	/***
	 * ��������-��Ҫ��U8_TaskRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event�ϻ�ȡ������
	 */
	private String[] innovationProperties2 = new String[] { "u8_cychsdate", "u8_kxxfxfadate",
			"u8_kxxfxbgdate", " u8_lxsqdate" };
	/***
	 * ��������-��Ҫ��U8_ProjectRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event�ϻ�ȡ������
	 */
	private String[] innovationProperties3 = new String[] { "u8_xmlxsdate", "u8_xmchfadate",
			"u8_tastetestdate", "u8_fxpgdate", "u8_wzdate", "u8_fdate", "u8_pdate",
			"u8_idate", "u8_zjdate", "u8_firstproddate", "u8_ysdate" };
	
	/***
	 * �з�����-��Ҫ��ȡ������
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
			MessageBox.post("û���ҵ���ѯ��\n" + queryName + "\n����ϵ����Ա����", "��ʾ", MessageBox.INFORMATION);
		}
		
		innovationList = new ArrayList<InnovationBean>();
		developmentList = new ArrayList<DevelopmentBean>();
		
		if (!taskType.equals("�з��������ϰ汾")) {
			ifGetInnovation = true;
		}
		if (taskType.contains("�з��������ϰ汾")) {
			ifGetDevelopment = true;
		}
	}
	
	public void GetDateAndExportExcel() throws Exception {
		GetData();
		String newFileName = Export();
		MessageBox.post("���ܱ����ɳɹ�\n\n�ļ�����λ������:\n" + newFileName, "��ʾ", MessageBox.INFORMATION);
		Runtime.getRuntime().exec("cmd  /c  start  " + newFileName);
	}
	
	private void GetData() throws Exception {
		TCComponent[] components = GetSearchResult(
				query, new String[]{ "����Ȩ��", "����", "����ʱ������", "����ʱ������" }, 
				new String[]{ "*.Һ��.��������;Һ��.��������", taskType, startDate, endDate });
//				query, new String[]{ "����Ȩ��", "����� ID" }, 
//				new String[]{ "*.Һ��.��������;Һ��.��������", "YYF-RW-CX-2018-123" });
		
		for (TCComponent component : components) {
			String type = component.getType();
			if (type.equals("U8_TaskRevision")) {
				// if--����Ǵ�������(��Ӧobject_typeΪ�������ϰ汾)
				
				// ��ȡU8_TaskRevision
				// ��U8_TaskRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event
				TCComponentItemRevision taskRevision = (TCComponentItemRevision)component;
				TCComponentForm taskRevisionForm = null;
				TCComponent[] components_1 = taskRevision.getReferenceListProperty("U8_Proj_Task_EventRel");
				for (TCComponent component_1 : components_1) {
					if (component_1 instanceof TCComponentForm) {
						taskRevisionForm = (TCComponentForm)component_1;
						break;
					}
				}
				
				// ��ȡU8_ProjectRevision
				// ��U8_ProjectRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event
				TCComponentItemRevision projectRevision = null;
				TCComponentForm projectRevisionForm = null;
				AIFComponentContext[] relatedComponents = taskRevision.whereReferenced();
				for (AIFComponentContext relatedComponent : relatedComponents) {
					InterfaceAIFComponent interfaceAIFComponent = relatedComponent.getComponent();
					if (interfaceAIFComponent instanceof TCComponentItemRevision
							&& interfaceAIFComponent.getType().equals("U8_ProjectRevision")) {
						// ����������Ŀ���ã�ֻȡ��һ��
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
				
				// ��ȡ����
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
				
				// ͨ���û�ѡ�������״̬���й���
				if (status.equals("δ���")) {
					if (projectRevision == null) {
						if (bean.projectProposal.equals("")) {
							continue;
						}
					} else {
						if (bean.projectAcceptance.equals("")) {
							continue;
						}
					}
				} else if (status.equals("���")) {
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
				// else if--������з�����(��Ӧobject_typeΪ�з��������ϰ汾)
				
				// ��ȡU8_Task2Revision
				// ��U8_Task2Revision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event
				TCComponentItemRevision taskRevision = (TCComponentItemRevision)component;
				TCComponentForm taskRevisionForm = null;
				TCComponent[] components_1 = taskRevision.getReferenceListProperty("U8_Proj_Task_EventRel");
				for (TCComponent component_1 : components_1) {
					if (component_1 instanceof TCComponentForm) {
						taskRevisionForm = (TCComponentForm)component_1;
						break;
					}
				}
				
				// ��ȡ����
				String[] properties = null;
				String[] properties1 = null;
				
				properties = taskRevision.getProperties(developmentProperties);
				if (taskRevisionForm != null) {
					properties1 = taskRevisionForm.getProperties(developmentProperties1);
				}
				
				DevelopmentBean bean = new DevelopmentBean(properties, properties1);
				
				// ͨ���û�ѡ�������״̬���й���
				if (status.equals("δ���")) {
					if (bean.taskSummary.equals("")) {
						continue;
					}
				} else if (status.equals("���")) {
					if (!bean.taskSummary.equals("")) {
						continue;
					}
				}
				
				developmentList.add(bean);
			}
		}
		
		// ��innovationList��������
		Collections.sort(innovationList);
	}
	
	private String Export() throws Exception {
		Workbook wb = new XSSFWorkbook();
		
		// ��ͷ��ʽ
		CellStyle headCellStyle = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("����");
		font.setFontHeightInPoints((short) 14);
		headCellStyle.setFont(font);
		headCellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		headCellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		headCellStyle.setWrapText(true);
		
		// ż���и�ʽ
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		// ż���и�ʽ For ���������к���Ŀ������
		CellStyle cellStyle_1 = wb.createCellStyle();
		cellStyle_1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		cellStyle_1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellStyle_1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		cellStyle_1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle_1.setWrapText(true);
		
		// �����и�ʽ
		CellStyle cellStyle1 = wb.createCellStyle();
		cellStyle1.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle1.setWrapText(true);
		
		// �����и�ʽ For ���������к���Ŀ������
		CellStyle cellStyle1_1 = wb.createCellStyle();
		cellStyle1_1.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		cellStyle1_1.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle1_1.setWrapText(true);
		
		if (ifGetInnovation) {
			// ��һҳ--��Ŀ&��������
			// ���ñ�ͷ
			Sheet sheet = wb.createSheet("��Ŀ&��������");
			String[] heads = new String[] { "������", "��������", "��Ŀ���", "��Ŀ����",
					"��������", "����߻���", "�����Է����о�����", "��Ŀ������/�����Է�������",
					"��������", "��Ŀ������", "��Ŀ�߻�����", "��ζ����", "����������ȫ��������",
					"���������ᱨ", "�䷽", "����", "������׼", "��Ŀ�ܽᱨ��", "��һ����������֪ͨ", "��Ŀ��������" };
			
			int index = 0;
			Row headRrow = GetRow(sheet, 0);
			// �����и�|ע���и߻����Զ�����cellStyle1.setWrapText(true);��ͻ
//			headRrow.setHeightInPoints(18);
			for (String head : heads) {
				// �����п�
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
			// ��������
			int size = innovationList.size();
			for (int i = 0; i < size; i++) {
				int rowNum = i + 1;
				Row row = GetRow(sheet, rowNum);
				// �����и�|ע���и߻����Զ�����cellStyle1.setWrapText(true);��ͻ
//				row.setHeightInPoints(15);
				
				InnovationBean bean = innovationList.get(i);
				// �޸����ڸ�ʽ yyyy-M-d HH:mm �޸�Ϊ yyyy-M-d
				HandleTimeStrFormat(bean);
				
				// ��ż���в��ò�ͬ�ĸ�ʽ
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
				// ���������� ������Ҫ�����
				GetCellWithStyle(row, 1, style1).setCellValue(bean.taskName);	
				GetCellWithStyle(row, 2, style).setCellValue(bean.projectNum);
				// ��Ŀ������ ������Ҫ�����
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
			// �ڶ�ҳ--��Ŀ&��������
			// ���ñ�ͷ
			Sheet sheet1 = wb.createSheet("�з�����");
			String[] heads1 = new String[] { "������", "��������", "��������", "����ʵ��", "�����ܽᱨ��" };
			
			int index1 = 0;
			Row headRrow1 = GetRow(sheet1, 0);
			// �����и�|ע���и߻����Զ�����cellStyle1.setWrapText(true);��ͻ
//			headRrow.setHeightInPoints(18);
			for (String head : heads1) {
				// �����п�
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
			// ��������
			int size1 = developmentList.size();
			for (int i = 0; i < size1; i++) {
				int rowNum = i + 1;
				Row row = GetRow(sheet1, rowNum);
				// �����и�|ע���и߻����Զ�����cellStyle1.setWrapText(true);��ͻ
//				row.setHeightInPoints(15);
				
				DevelopmentBean bean = developmentList.get(i);
				// �޸����ڸ�ʽ yyyy-M-d HH:mm �޸�Ϊ yyyy-M-d
				HandleTimeStrFormat1(bean);
				
				// ��ż���в��ò�ͬ�ĸ�ʽ
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
				// ���������� ������Ҫ�����
				GetCellWithStyle(row, 1, style1).setCellValue(bean.taskName);
				GetCellWithStyle(row, 2, style).setCellValue(bean.taskDemand);
				GetCellWithStyle(row, 3, style).setCellValue(bean.taskImplementation);
				GetCellWithStyle(row, 4, style).setCellValue(bean.taskSummary);
			}
		}
		
		String newFile = filePath + "\\��Ŀ���¼�" + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
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
 * ���������������
 */
class InnovationBean implements Comparable<InnovationBean> {
	// U8_Task�����ԣ�������U8_TaskRevisio��ȡ��
	String taskNum = ""; // ������--item_id
	
	// U8_TaskRevisio������
	String taskName = ""; // ��������--object_name
	
	// U8_Project������
	String projectNum = ""; // ��Ŀ���--item_id
	
	// U8_ProjectRevision������
	String projectName = ""; // ��Ŀ����--object_name
	
	// U8_TaskRevision������
	String taskDemand = ""; // ��������--date_released
	
	// U8_TaskRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event������
	String CreativePlanning = ""; // ����߻���--u8_cychsdate
	String feasibility = ""; // �����Է����о�����--u8_kxxfxfadate
	String projectProposal = ""; // ��Ŀ������/�����Է�������--u8_kxxfxbgdate
	String projectApplication = ""; // ��������--u8_lxsqdate
	
	// U8_ProjectRevision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event������
	String projectApproval = ""; // ��Ŀ������--u8_xmlxsdate
	String projectPlanning = ""; // ��Ŀ�߻�����--u8_xmchfadate
	String tasteDebugging = ""; // ��ζ����--u8_tastetestdate
	String safetyRisk = ""; // ����������ȫ��������--u8_fxpgdate
	String technicalWords = ""; // ���������ᱨ--u8_wzdate
	String formula = ""; // �䷽--u8_fdate
	String process = ""; // ����--u8_pdate
	String qualityStandard = ""; // ������׼--u8_idate
	String projectSummary = ""; // ��Ŀ�ܽᱨ��--u8_zjdate
	String firstNotification = ""; // ��һ����������֪ͨ--u8_firstproddate
	String projectAcceptance = ""; // ��Ŀ��������--u8_ysdate
	
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
 * �з������������
 */
class DevelopmentBean implements Comparable<InnovationBean> {
	// U8_Task2�����ԣ�������U8_TaskRevisio��ȡ��
	String taskNum = ""; // ������--item_id
	// U8_Task2Revisio������
	String taskName = ""; // ��������--object_name
	String taskDemand = ""; // ��������--date_released
	
	// U8_Task2Revision:: U8_Proj_Task_EventRel:: U8_Proj_Task_Event������
	String taskImplementation = ""; // ����ʵ��--u8_rwsxdate
	String taskSummary = ""; // �����ܽᱨ��--u8_rwzjdate
	
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