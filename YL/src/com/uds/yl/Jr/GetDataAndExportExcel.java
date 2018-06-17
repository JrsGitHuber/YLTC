package com.uds.yl.Jr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.QueryUtil;

public class GetDataAndExportExcel {
	TCComponentItemRevision mItemRev;
	
	// ����Ŀ¼ �ϴ����ݼ�ʱ·��ֻ����\\����ʹ//������ᵼ�����ݼ��ڵ��ļ����Ƴ���·��������
	String path = "c:\\temp\\ExportExcel\\";
	
	// ��������
	String shengchanName = "�����䷽.xlsx";
	String caiwuName = "�����䷽.xlsx";
	String dingdanName = "��Ʒ����.xlsx";
	String fupeiName = "�����䷽.xlsx";
	
	// ���� ����/����/��Ʒ ���ݼ�������
	String excelName = "";
	// ���ڸ������ݼ�������
	String fupeiName1 = "";
	
	// �䷽��һ�����ܱ�
	private ArrayList<ExcelBean> excelList = null;
	// ����༶���ܱ�
	private ArrayList<Excel1Bean> excelList1 = null;

	/** ԭ���滻�������� **/
	private ArrayList<String> list = null;
	/** ����滻�������� **/
	private ArrayList<String> list1 = null;
	/** ����滻˵������ **/
	private ArrayList<String> list2 = null;
	/** С������ **/
	private ArrayList<String> list3 = null;
	
	private String[] bomLineProperties = new String[] { "bl_item_item_id", "bl_item_object_name", 
			"U8_inventory", "U8_alternate", "U8_alternateitem", "U8_groupitem", "U8_minmaterial" };
	
	private ArrayList<String> filePathList = null;
	
	public GetDataAndExportExcel(TCComponentItemRevision mItemRev, String sessionName, String excelName) throws Exception {
		this.mItemRev = mItemRev;
		this.excelName = excelName;
		this.fupeiName1 = excelName + "_�����䷽";
		path = path + sessionName + new SimpleDateFormat("_yyyyMMdd_HHmmss").format(new Date()) + "\\";
		
		excelList = new ArrayList<ExcelBean>();
		excelList1 = new ArrayList<Excel1Bean>();
		list = new ArrayList<String>();
		list1 = new ArrayList<String>();
		list2 = new ArrayList<String>();
		list3 = new ArrayList<String>();
		filePathList = new ArrayList<String>();
		
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(mItemRev, Const.Formulator.MATERIALBOMNAME);
		if(topBomLine == null || !topBomLine.hasChildren()){
			CloseWindow(topBomLine);
			throw new Exception("��ѡ�汾û���䷽������");
		}
		
		// ��ʼ�� �䷽��һ�����ܱ�����������ܱ�
		try {
			// TODO �˺�������Ҫ��� ���Ȩ��
			GetExcelList(topBomLine);
		} catch (Exception e) {
			e.printStackTrace();
			CloseWindow(topBomLine);
			throw new Exception("��ȡ�䷽���Գ�����鿴����̨����Ĵ�����Ϣ");
		}
		CloseWindow(topBomLine);
		
		// ����Ƿ���Ҫ�������  1.�������Ƿ�û������  2.ÿ���汾���Ƿ��Ѿ����˶�Ӧ�ı�
		CheckIfContinue();
		
		// ��������Ŀ¼
		File file = new File(path);
		if (file.exists()) {
			throw new Exception("����Ŀ¼�쳣����");
		} else {
			if (!file.mkdirs()) {
				throw new Exception("��������Ŀ¼ʧ��");
			}
		}
		
		// ����
		Collections.sort(list);
		Collections.sort(list1);
		Collections.sort(list2);
		Collections.sort(list3);
	}

	public void AllExcelOperation() throws Exception {
		if (excelName.equals("�����䷽")) {
			AboutShengchanExcel();
		} else if (excelName.equals("��Ʒ����")) {
			AboutDingdanExcel();
		} else if (excelName.equals("�����䷽")) {
			AboutCaiwuExcel();
		} else {
			MessageBox.post("����ĵ���", "��ʾ", MessageBox.INFORMATION);
			return;
		}
	}
	
	private void AboutCaiwuExcel() throws Exception {
		// ���ر��ģ��
		DownTemplate(new String[] { caiwuName, fupeiName });
		
		// ��ȡ �䷽ҳ ������
		ArrayList<ExcelBean> exportList = GetDingdanExportList();
		
		// ����������
		ExportCaiwuExcel(exportList);
		// ���������
		ExportFupeiExcel();
		
		// �ϴ����ݼ�
		OrganiseUpload();

		MessageBox.post("�������ɳɹ�", "��ʾ", MessageBox.INFORMATION);
	}
	
	private void AboutDingdanExcel() throws Exception {
		// ���ر��ģ��
		DownTemplate(new String[] { dingdanName, fupeiName });

		// ��ȡ ����ҳ ������
		ArrayList<ExcelBean> exportList = GetDingdanExportList();

		// ����������
		ExportDingdanExcel(exportList);
		// ���������
		ExportFupeiExcel();

		// �ϴ����ݼ�
		OrganiseUpload();

		MessageBox.post("�������ɳɹ�", "��ʾ", MessageBox.INFORMATION);
	}
	
	private void AboutShengchanExcel() throws Exception {
		// ���ر��ģ��
		DownTemplate(new String[] { shengchanName, fupeiName });

		// ��ȡ �䷽ҳ ������
		ArrayList<ExcelBean> exportList = GetShengchanExportList();
		// ��ȡ С���䷽ҳ ������
		ArrayList<ExcelBean> exportList1 = GetShengchanExportList1();

		// ����������
		ExportShengchanExcel(exportList, exportList1);
		// ���������
		ExportFupeiExcel();

		// �ϴ����ݼ�
		OrganiseUpload();

		MessageBox.post("�������ɳɹ�", "��ʾ", MessageBox.INFORMATION);
	}
	
	
	// ��غ���---------------------------------------------------------------------------------------------------------------------------------
	
	private void SetFengyeName(Workbook wb, String excelName, TCComponentItemRevision itemRevision, int rowNumber) throws Exception {
		String sheetName = "��ҳ";
		Sheet sheet = wb.getSheet("��ҳ");
		if(sheet == null){
			throw new Exception("ģ�� " + caiwuName + " �����޷���ȡ��һҳ-" + sheetName + "\n����ϵ����Ա");
		}
		
		String[] revisionProperties = new String[] { "object_name", "item_id", "item_revision_id" };
		String[] properties = itemRevision.getProperties(revisionProperties);
		
		Row row = GetRow(sheet, rowNumber);
		Cell cell = GetCell(row, 0);
		cell.setCellValue(properties[0]);
		
		String num = properties[1];
		if (excelName.equals("��Ʒ����_�����䷽")) {
			// ��Ʒ���������ҳ�滻����
			// YLYT/FPPF/YF/043 ->YLYT/DD/YF/FP/043
			num = num.replace("/FPPF/", "/DD/");
			num = num.replace("/YF/", "/YF/FP/");
		}
		if (excelName.equals("��Ʒ����")) {
			// ��Ʒ������ҳ�滻����
			// YLYT/PF/YF/043 -> YLYT/DD/YF/043
			num = num.replace("/PF/", "/DD/");
		}
		num = num + "[" + properties[2] + "]";
		row = GetRow(sheet, 22);
		cell = GetCell(row, 4);
		cell.setCellValue(num);
	}
	
	private void ExportCaiwuExcel(ArrayList<ExcelBean> exportList) throws Exception {
		File inFile = new File(path + caiwuName);
		if(!inFile.exists()){
			throw new Exception("û���ҵ�ģ���ļ�-" + caiwuName);
		}
		
		Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
		
		// ���÷�ҳ����
		SetFengyeName(wb, excelName, mItemRev, 12);
		
		// ��һҳsheet---�䷽��*****************************************************************************************
		String sheetName = "�䷽ҳ";
		Sheet sheet = wb.getSheet(sheetName);
		if(sheet == null){
			throw new Exception("ģ�� " + caiwuName + " �����޷���ȡ�ڶ�ҳ-" + sheetName + "\n����ϵ����Ա");
		}
		
		// ����Cell�߿�
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		// ��ʼ����������ʼ��---5
		int index = 5;
		// ͳ����Ҫ���ص���
		boolean hidden = true;
		boolean hidden1 = true;
		boolean hidden2 = true;
		
		for(ExcelBean bean : exportList){
			Row row = GetRow(sheet, index);
			
			Cell cell = GetCell(row, 1);
			cell.setCellValue(bean.U8_groupitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_groupitem.equals("")){
				hidden = false;
			}
			
			cell = GetCell(row, 2);
			cell.setCellValue(bean.U8_alternateitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternateitem.equals("")){
				hidden1 = false;
			}
			
			cell = GetCell(row, 3);
			cell.setCellValue(bean.U8_alternate);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternate.equals("")){
				hidden2 = false;
			}
			
			cell = GetCell(row, 4);
			cell.setCellValue(bean.bl_item_object_name);
			cell.setCellStyle(cellStyle);
			
			cell = GetCell(row, 5);
			cell.setCellValue(bean.U8_inventory);
			cell.setCellStyle(cellStyle);
			
			index++;
		}
		
		// ��ʼ�ϲ���Ԫ��
		index = 5;
		int listSize = exportList.size();
		for(int i = 1; i <= 3; i++){
			int column = i;
			
			String cellValue = GetCell(GetRow(sheet, index), column).getStringCellValue();
			int startRow = index;
			for(int j = 1; j <= listSize; j++){
				int index1 = index + j;
				Row row = GetRow(sheet, index1);
				String cellValue1 = GetCell(row, column).getStringCellValue();
				if(!cellValue1.equals(cellValue)){
					if(cellValue.equals("")){
						System.out.println("��ֵ���ϲ�");
					}else{
						if(startRow != index1-1){
							CellRangeAddress region = new CellRangeAddress(startRow, index1-1, column, column);
							sheet.addMergedRegion(region);
							
							if(column == 3){
								row = GetRow(sheet, startRow);
								GetCell(row, column).setCellValue("��");
							}
						}
					}
					
					cellValue = cellValue1;
					startRow = index1;
				}
			}
		}
		
		// �������ص���
		if(hidden){
			sheet.setColumnHidden(1, true);
		}
		if(hidden1){
			sheet.setColumnHidden(2, true);
		}
		if(hidden2){
			sheet.setColumnHidden(3, true);
		}
		
		String filePath = path + StringUtils.GetNameByString(mItemRev.getProperty("object_name")) + caiwuName;
		File outFile = new File(filePath);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(outFile);
			wb.write(out);
			out.close();
			filePathList.add(filePath);
		} catch (Exception e) {
			throw e;
		}
	}

	private void ExportDingdanExcel(ArrayList<ExcelBean> exportList) throws Exception {		
		File inFile = new File(path + dingdanName);
		if(!inFile.exists()){
			throw new Exception("û���ҵ�ģ���ļ�-" + dingdanName);
		}
		
		Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
		
		// ���÷�ҳ����
		SetFengyeName(wb, excelName, mItemRev, 12);
		
		// ��һҳsheet---�䷽��*****************************************************************************************
		String sheetName = "����ҳ";
		Sheet sheet = wb.getSheet(sheetName);
		if(sheet == null){
			throw new Exception("ģ�� " + dingdanName + " �����޷���ȡ�ڶ�ҳ-" + sheetName + "\n����ϵ����Ա");
		}
		
		// ����Cell�߿�
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		// ��ʼ����������ʼ��---5
		int index = 5;
		// ͳ����Ҫ���ص���
		boolean hidden = true;
		boolean hidden1 = true;
		boolean hidden2 = true;
		
		for(ExcelBean bean : exportList){
			Row row = GetRow(sheet, index);
			
			Cell cell = GetCell(row, 1);
			cell.setCellValue(bean.U8_groupitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_groupitem.equals("")){
				hidden = false;
			}
			
			cell = GetCell(row, 2);
			cell.setCellValue(bean.U8_alternateitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternateitem.equals("")){
				hidden1 = false;
			}
			
			cell = GetCell(row, 3);
			cell.setCellValue(bean.U8_alternate);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternate.equals("")){
				hidden2 = false;
			}
			
			cell = GetCell(row, 4);
			cell.setCellValue(bean.bl_item_object_name);
			cell.setCellStyle(cellStyle);
			
			cell = GetCell(row, 5);
			cell.setCellValue(bean.U8_inventory);
			cell.setCellStyle(cellStyle);
			
			index++;
		}
		
		// ��ʼ�ϲ���Ԫ��
		index = 5;
		int listSize = exportList.size();
		for(int i = 1; i <= 3; i++){
			int column = i;
			
			String cellValue = GetCell(GetRow(sheet, index), column).getStringCellValue();
			int startRow = index;
			for(int j = 1; j <= listSize; j++){
				int index1 = index + j;
				Row row = GetRow(sheet, index1);
				String cellValue1 = GetCell(row, column).getStringCellValue();
				if(!cellValue1.equals(cellValue)){
					if(cellValue.equals("")){
						System.out.println("��ֵ���ϲ�");
					}else{
						if(startRow != index1-1){
							CellRangeAddress region = new CellRangeAddress(startRow, index1-1, column, column);
							sheet.addMergedRegion(region);
							
							if(column == 3){
								row = GetRow(sheet, startRow);
								GetCell(row, column).setCellValue("��");
							}
						}
					}
					
					cellValue = cellValue1;
					startRow = index1;
				}
			}
		}
		
		// �������ص���
		if(hidden){
			sheet.setColumnHidden(1, true);
		}
		if(hidden1){
			sheet.setColumnHidden(2, true);
		}
		if(hidden2){
			sheet.setColumnHidden(3, true);
		}
		
		String filePath = path + StringUtils.GetNameByString(mItemRev.getProperty("object_name")) + dingdanName;
		File outFile = new File(filePath);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(outFile);
			wb.write(out);
			out.close();
			filePathList.add(filePath);
		} catch (Exception e) {
			throw e;
		}
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
	
	private void ExportShengchanExcel(ArrayList<ExcelBean> exportList, ArrayList<ExcelBean> exportList1) throws Exception {
		
		File inFile = new File(path + shengchanName);
		if(!inFile.exists()){
			throw new Exception("û���ҵ�ģ���ļ�-" + shengchanName);
		}
		
		Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
		
		// ���÷�ҳ����
		SetFengyeName(wb, excelName, mItemRev, 12);
		
		// �ӹ�ϵ--����䷽��U8_FormulaRel�� �й����ĵ�һ���䷽�汾��U8_FormulaRevision����ȡu8_dosagebase2����
		String u8_dosagebase2 = GetU8_dosagebase2();
		if (u8_dosagebase2.equals("")) {
			u8_dosagebase2 = "??";
		}
		u8_dosagebase2 += "ǧ�˳�Ʒ�к�����ǧ��";
		String remark = mItemRev.getProperty("object_desc");
		if (remark.equals("")) {
			//remark = "��û�б�ע";
			remark = "";
		}
		
		// ��һҳsheet---�䷽��*****************************************************************************************
		String sheetName = "�䷽ҳ";
		Sheet sheet = wb.getSheet(sheetName);
		if(sheet == null){
			throw new Exception("ģ�� " + shengchanName + " �����޷���ȡ�ڶ�ҳ-" + sheetName + "\n����ϵ����Ա");
		}
		
		Row specilRow = GetRow(sheet, 2);
		Cell specilCell = GetCell(specilRow, 0);
		specilCell.setCellValue(u8_dosagebase2);
		
		// ����Cell�߿�
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		
		// ��ʼ����������ʼ��---5
		int index = 5;
		// ͳ����Ҫ���ص���
		boolean hidden = true;
		boolean hidden1 = true;
		boolean hidden2 = true;
		
		for(ExcelBean bean : exportList){
			Row row = GetRow(sheet, index);
			
			Cell cell = GetCell(row, 1);
//			Cell cell = row.getCell(1);
			cell.setCellValue(bean.U8_groupitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_groupitem.equals("")){
				hidden = false;
			}
			
			cell = GetCell(row, 2);
			cell.setCellValue(bean.U8_alternateitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternateitem.equals("")){
				hidden1 = false;
			}
			
			cell = GetCell(row, 3);
			cell.setCellValue(bean.U8_alternate);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternate.equals("")){
				hidden2 = false;
			}
			
			cell = GetCell(row, 4);
			cell.setCellValue(bean.bl_item_object_name);
			cell.setCellStyle(cellStyle);
			
			cell = GetCell(row, 5);
			cell.setCellValue(bean.U8_inventory);
			cell.setCellStyle(cellStyle);
			
			index++;
		}
		
		// ��ʼ�ϲ���Ԫ��
		index = 5;
		int listSize = exportList.size();
		for(int i = 1; i <= 3; i++){
			int column = i;
			
			String cellValue = GetCell(GetRow(sheet, index), column).getStringCellValue();
			int startRow = index;
			for(int j = 1; j <= listSize; j++){
				int index1 = index + j;
				Row row = GetRow(sheet, index1);
				String cellValue1 = GetCell(row, column).getStringCellValue();
				if(!cellValue1.equals(cellValue)){
					if(cellValue.equals("")){
						System.out.println("��ֵ���ϲ�");
					}else{
						if(startRow != index1-1){
							CellRangeAddress region = new CellRangeAddress(startRow, index1-1, column, column);
							sheet.addMergedRegion(region);
							
							if(column == 3){
								row = GetRow(sheet, startRow);
								GetCell(row, column).setCellValue("��");
							}
						}
					}
					
					cellValue = cellValue1;
					startRow = index1;
				}
			}
		}
		
		// �������ص���
		if(hidden){
			sheet.setColumnHidden(1, true);
		}
		if(hidden1){
			sheet.setColumnHidden(2, true);
		}
		if(hidden2){
			sheet.setColumnHidden(3, true);
		}
		
		
		// �ڶ�ҳsheet---С���䷽��*****************************************************************************************
		sheetName = "С���䷽ҳ";
		sheet = wb.getSheet(sheetName);
		if(sheet == null){
			throw new Exception("ģ�� " + shengchanName + " �����޷���ȡ����ҳ-" + sheetName + "\n����ϵ����Ա");
		}
		
		specilRow = GetRow(sheet, 2);
		specilCell = GetCell(specilRow, 0);
		specilCell.setCellValue(u8_dosagebase2);
		
		index = 5;
		// ͳ����Ҫ���ص���
		hidden = true;
		hidden1 = true;
		hidden2 = true;
		for(ExcelBean bean : exportList1){
			Row row = GetRow(sheet, index);
			Cell cell = GetCell(row, 1);
			cell.setCellValue(bean.U8_minmaterial);
			cell.setCellStyle(cellStyle);
			
			cell = GetCell(row, 2);
			cell.setCellValue(bean.U8_groupitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_groupitem.equals("")){
				hidden = false;
			}
			
			cell = GetCell(row, 3);
			cell.setCellValue(bean.U8_alternateitem);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternateitem.equals("")){
				hidden1 = false;
			}
			
			cell = GetCell(row, 4);
			cell.setCellValue(bean.U8_alternate);
			cell.setCellStyle(cellStyle);
			if(!bean.U8_alternate.equals("")){
				hidden2 = false;
			}
			
			cell = GetCell(row, 5);
			cell.setCellValue(bean.bl_item_object_name);
			cell.setCellStyle(cellStyle);
			
			cell = GetCell(row, 6);
			cell.setCellValue(bean.U8_inventory);
			cell.setCellStyle(cellStyle);
			
			index++;
		}
		
		if (index < 17) {
			index = 17;
		}
		specilRow = GetRow(sheet, index);
		specilCell = GetCell(specilRow, 0);
		specilCell.setCellValue(remark);
		
		// ��ʼ�ϲ���Ԫ��
		index = 5;
		listSize = exportList1.size();
		for(int i = 1; i <= 4; i++){			
			int column = i;
			
			String cellValue = GetCell(GetRow(sheet, index), column).getStringCellValue();
			int startRow = index;
			for(int j = 1; j <= listSize; j++){
				int index1 = index + j;
				Row row = GetRow(sheet, index1);
				String cellValue1 = GetCell(row, column).getStringCellValue();
				if(!cellValue1.equals(cellValue)){
					if(cellValue.equals("")){
						System.out.println("��ֵ���ϲ�");
					}else{
						if(startRow != index1-1){
							CellRangeAddress region = new CellRangeAddress(startRow, index1-1, column, column);
							sheet.addMergedRegion(region);
							
							if(column == 4){
								row = GetRow(sheet, startRow);
								GetCell(row, column).setCellValue("��");
							}
						}						
					}
					
					cellValue = cellValue1;
					startRow = index1;
				}
			}
		}
		
		// �������ص���
		if (hidden) {
			sheet.setColumnHidden(2, true);
		}
		if (hidden1) {
			sheet.setColumnHidden(3, true);
		}
		if (hidden2) {
			sheet.setColumnHidden(4, true);
		}
		
		String filePath = path + StringUtils.GetNameByString(mItemRev.getProperty("object_name")) + shengchanName;
		File outFile = new File(filePath);
		if (outFile.exists()) {
			outFile.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(outFile);
			wb.write(out);
			out.close();
			filePathList.add(filePath);
		} catch (Exception e) {
			throw e;
		}
	}
	
	private String GetU8_dosagebase2() throws TCException {
		TCComponent[] components = mItemRev.getReferenceListProperty("U8_FormulaRel");
		if (components == null || components.length == 0) {
			return "";
		} else {
			for (TCComponent component : components) {
				if (component instanceof TCComponentItemRevision) {
					TCComponentItemRevision revision = (TCComponentItemRevision)component;
					return revision.getProperty("u8_dosagebase2");
				}				
			}
		}
		
		return "";
	}

	private void ExportFupeiExcel() throws Exception {
		File inFile = new File(path + fupeiName);
		if(!inFile.exists()){
			throw new Exception("û���ҵ�ģ���ļ�-" + fupeiName);
		}
		
		for(Excel1Bean excel1Bean : excelList1){
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			
			// ���÷�ҳ����
			SetFengyeName(wb, fupeiName1, excel1Bean.itemRevision, 13);
			
			// ��һҳsheet---����ҳ*****************************************************************************************
			String sheetName = "����ҳ";
			Sheet sheet = wb.getSheet(sheetName);
			if(sheet == null){
				throw new Exception("ģ�� " + fupeiName + "���޷���ȡ�ڶ�ҳ-" + sheetName + "\n����ϵ����Ա");
			}
			
			CellStyle cellStyle = wb.createCellStyle();
			// ����Cell�߿�
			cellStyle.setBorderTop(CellStyle.BORDER_THIN);
			cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
			cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
			cellStyle.setBorderRight(CellStyle.BORDER_THIN);
			//���þ���
			cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
			// �����Զ�����
			cellStyle.setWrapText(true);
			
			// ��ʼ����������ʼ��---5
			int index = 5;
			// ͳ����Ҫ���ص���
			boolean hidden = true;
			boolean hidden1 = true;
			boolean hidden2 = true;
			
			ArrayList<ExcelBean> list = excel1Bean.list;
			for(ExcelBean bean : list){
				Row row = GetRow(sheet, index);
				
				Cell cell = GetCell(row, 1);
				cell.setCellValue(bean.U8_groupitem);
				cell.setCellStyle(cellStyle);
				if(!bean.U8_groupitem.equals("")){
					hidden = false;
				}
				
				cell = GetCell(row, 2);
				cell.setCellValue(bean.U8_alternateitem);
				cell.setCellStyle(cellStyle);
				if(!bean.U8_alternateitem.equals("")){
					hidden1 = false;
				}
				
				cell = GetCell(row, 3);
				cell.setCellValue(bean.U8_alternate);
				cell.setCellStyle(cellStyle);
				if(!bean.U8_alternate.equals("")){
					hidden2 = false;
				}
				
				cell = GetCell(row, 4);
				cell.setCellValue(bean.bl_item_object_name);
				cell.setCellStyle(cellStyle);
				
				cell = GetCell(row, 5);
				cell.setCellValue(bean.U8_inventory);
				cell.setCellStyle(cellStyle);
				
				index++;
			}
			
			// ��ʼ�ϲ���Ԫ��
			index = 5;
			int listSize = excel1Bean.list.size();
			for(int i = 1; i <= 3; i++){
				int column = i;
				
				String cellValue = GetCell(GetRow(sheet, index), column).getStringCellValue();
				int startRow = index;
				for(int j = 1; j <= listSize; j++){
					int index1 = index + j;
					Row row = GetRow(sheet, index1);
					String cellValue1 = GetCell(row, column).getStringCellValue();
					if(!cellValue1.equals(cellValue)){
						if(cellValue.equals("")){
							System.out.println("��ֵ���ϲ�");
						}else{
							if(startRow != index1-1){
								CellRangeAddress region = new CellRangeAddress(startRow, index1-1, column, column);
								sheet.addMergedRegion(region);
								
								if(column == 3){
									row = GetRow(sheet, startRow);
									GetCell(row, column).setCellValue("��");
								}
							}
						}
						
						cellValue = cellValue1;
						startRow = index1;
					}
				}
			}
			
			// �������ص���
			if(hidden){
				sheet.setColumnHidden(1, true);
			}
			if(hidden1){
				sheet.setColumnHidden(2, true);
			}
			if(hidden2){
				sheet.setColumnHidden(3, true);
			}
			
			String filePath = path + StringUtils.GetNameByString(excel1Bean.itemRevision.getProperty("item_id")) + fupeiName1 + ".xlsx";
			File outFile = new File(filePath);
			if (outFile.exists()) {
				outFile.delete();
			}
			try {
				
				FileOutputStream out = new FileOutputStream(outFile);
				wb.write(out);
				out.close();
				filePathList.add(filePath);
			} catch (Exception e) {
				throw e;
			}
		}
		
	}
	
	private ArrayList<ExcelBean> GetDingdanExportList() throws Exception {
		ArrayList<ExcelBean> exportList = new ArrayList<ExcelBean>();
		for(String str : list2){
			ArrayList<ExcelBean> thisList = new ArrayList<ExcelBean>();
			for(ExcelBean bean : excelList){
				if(bean.U8_groupitem.equals(str)){
					thisList.add(bean);
				}
			}
			
			if(thisList.size() == 0){
				continue;
			}
			
			for(String str1 : list1){
				ArrayList<ExcelBean> thisList1 = new ArrayList<ExcelBean>();
				for(ExcelBean bean : thisList){
					if(bean.U8_alternateitem.equals(str1)){
						thisList1.add(bean);
					}
				}
				
				if(thisList1.size() == 0){
					continue;
				}
				for(String str2 : list){
					for(ExcelBean bean : thisList1){
						if(bean.U8_alternate.equals(str2)){
							exportList.add(bean);
						}
					}
				}
			}
		}
		
		return exportList;
	}
	
	private ArrayList<ExcelBean> GetShengchanExportList1() throws Exception {
		ArrayList<ExcelBean> exportList = new ArrayList<ExcelBean>();
		for(String str : list3){
			ArrayList<ExcelBean> thisList = new ArrayList<ExcelBean>();
			for(ExcelBean bean : excelList){
				if(!bean.U8_minmaterial.equals("") && bean.U8_minmaterial.equals(str)){
					thisList.add(bean);
				}
			}
			
			if(thisList.size() == 0){
				continue;
			}
			
			for(String str1 : list2){
				ArrayList<ExcelBean> thisList1 = new ArrayList<ExcelBean>();
				for(ExcelBean bean : thisList){
					if(bean.U8_groupitem.equals(str1)){
						thisList1.add(bean);
					}
				}
				
				for(String str2 : list1){
					ArrayList<ExcelBean> thisList2 = new ArrayList<ExcelBean>();
					for(ExcelBean bean : thisList1){
						if(bean.U8_alternateitem.equals(str2)){
							thisList2.add(bean);
						}
					}
					
					for(String str3 : list){
						for(ExcelBean bean : thisList2){
							if(bean.U8_alternate.equals(str3)){
								exportList.add(bean);
							}
						}
					}
				}
			}
		}
		
		return exportList;
	}
	
	private ArrayList<ExcelBean> GetShengchanExportList() throws Exception{
		ArrayList<ExcelBean> exportList = new ArrayList<ExcelBean>();
		for(String str : list2){
			ArrayList<ExcelBean> thisList = new ArrayList<ExcelBean>();
			for(ExcelBean bean : excelList){
				if(bean.U8_minmaterial.equals("") && bean.U8_groupitem.equals(str)){
					thisList.add(bean);
				}
			}
			
			if(thisList.size() == 0){
				continue;
			}
			
			for(String str1 : list1){
				ArrayList<ExcelBean> thisList1 = new ArrayList<ExcelBean>();
				for(ExcelBean bean : thisList){
					if(bean.U8_alternateitem.equals(str1)){
						thisList1.add(bean);
					}
				}
				
				if(thisList1.size() == 0){
					continue;
				}
				for(String str2 : list){
					for(ExcelBean bean : thisList1){
						if(bean.U8_alternate.equals(str2)){
							exportList.add(bean);
						}
					}
				}
			}
		}
		
		// ���� С�ϲ���
		for(String str : list3){
			if(str.equals("")){
				continue;
			}
			exportList.add(new ExcelBean("", str, "һ��", "", "", "", ""));
		}
		
		return exportList;
	}
	
	private void GetExcelList(TCComponentBOMLine topBomLine) throws Exception {
		AIFComponentContext[] children = topBomLine.getChildren();		
		for (AIFComponentContext context : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();			
			String[] properties = bomLine.getProperties(bomLineProperties);
			
			String U8_alternate = properties[3];
			if(!list.contains(U8_alternate)){
				list.add(U8_alternate);
			}
			String U8_alternateitem = properties[4];
			if(!list1.contains(U8_alternateitem)){
				list1.add(U8_alternateitem);
			}
			String U8_groupitem = properties[5];
			if(!list2.contains(U8_groupitem)){
				list2.add(U8_groupitem);
			}
			String U8_minmaterial = properties[6];
			if(!list3.contains(U8_minmaterial)){
				list3.add(U8_minmaterial);
			}
			
			// 20180409 jinagren �������ȿ��ǰ汾�ϵ�u8_code
			String name = bomLine.getItemRevision().getProperty("u8_code");
			if (!name.equals("")) {
				properties[1] = name;
			}
			
			ExcelBean excelBean = new ExcelBean(properties);
			excelList.add(excelBean);
		}
		
		GetExcelList1(topBomLine);
	}
	
	private void CheckIfContinue() throws Exception {
		if(excelList.size() == 0){
			// ���������Ҫ�ж�...
			throw new Exception("��ʾ\n\n��ѡ�䷽��ͼ��û��ԭ��");
		}else{
			String warningMessage = "";
			mItemRev.refresh();
			TCComponent[] components = mItemRev.getReferenceListProperty("IMAN_specification");
			if(components != null && components.length !=0){
				for(TCComponent component : components){
					if(component instanceof TCComponentDataset){
						if(component.getProperty("object_name").equals(excelName)){
							String message = mItemRev.getProperty("object_name") + "(idΪ" + mItemRev.getProperty("item_id") + ")���Ѵ��� " + excelName + " ���\n";
							warningMessage += message;
							break;
						}
					}
				}
			}
			
			String warningMessage1 = "";
			for(Excel1Bean bean : excelList1){
				bean.itemRevision.refresh();
				TCComponent[] components1 = bean.itemRevision.getReferenceListProperty("IMAN_specification");
				if(components1 != null && components1.length !=0){
					for(TCComponent component : components1){
						if(component instanceof TCComponentDataset){
							if(component.getProperty("object_name").equals(fupeiName1)){
								warningMessage1 += bean.itemRevision.getProperty("object_name") + "(idΪ" + bean.itemRevision.getProperty("item_id") + ")��";
							}
						}
					}
				}
			}
			
			if(!warningMessage1.equals("")){
				warningMessage1 = warningMessage1.substring(0, warningMessage1.length()-1);
				warningMessage1 += "���Ѵ��� " + fupeiName1 + " ���";
			}
			warningMessage += warningMessage1;
			if(!warningMessage.equals("")){
				warningMessage = "��ʾ\n\n" + warningMessage;
				throw new Exception(warningMessage);
			}
		}
	}
	
	private void GetExcelList1(TCComponentBOMLine topBomLine) throws Exception {
		TCComponentItem item = topBomLine.getItem();
		TCComponentItemRevision itemRevision = item.getLatestItemRevision();
		Excel1Bean excel1Bean = new Excel1Bean(itemRevision);
		
		AIFComponentContext[] children = topBomLine.getChildren();		
		for (AIFComponentContext context : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
			if(bomLine.getItem().getType().equals("U8_Material")){
				String[] properties = bomLine.getProperties(bomLineProperties);
;				ExcelBean excelBean = new ExcelBean(properties);
				excel1Bean.AddBeanToList(excelBean);
			}
			
			if(bomLine.hasChildren()){
				GetExcelList1(bomLine);
			}
		}
		
		// ������ѡ�е��䷽��û�и�����һ˵�ģ�ֻ��ԭ�ϲ��и���
		if(!excel1Bean.itemRevision.getProperty("item_id").equals(mItemRev.getProperty("item_id"))){
			if(excel1Bean.list != null && excel1Bean.list.size() != 0){
				excelList1.add(excel1Bean);
			}
		}		
	}
	
	private void CloseWindow(TCComponentBOMLine topBomLine) {

		// �رղ�����Bom View
		if (topBomLine != null) {
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			try {
				bomWindow.save();
				bomWindow.close();
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void DownTemplate(String[] fileNames) throws Exception {
		for(String fileName : fileNames){
			String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
			if(code == null || code.equals("")){
				throw new Exception("UDSCodeConfigGroup�����ڻ�û��ֵ������ϵ����Ա����");
			}
			TCComponentDataset dataset = GetDatasetByNameAndCode(code, fileName);
			if(dataset==null){
				throw new Exception("û���ҵ���Ӧ��ģ�����ݼ�");
			}
			String[] resultStrs = DownDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, path);
			boolean ifSuccess = false;
			for(String obj : resultStrs){
				if(obj.contains(fileName)){
					ifSuccess = true;
				}
			}
			if(!ifSuccess){
				throw new Exception("��ģ�����ݼ���û�л�ȡ��ģ���ļ�" + fileName);
			}
		}
	}
	
	private String[] DownDateSetToLocalDir(TCComponentDataset componentDataset, String namedRefName, String localDir) throws Exception {
		componentDataset = componentDataset.latest();

		// ע�⣺��������[������]��ͬ���ļ����ܴ��ڶ��
		String namedRefFileName[] = componentDataset.getFileNames(namedRefName);
		if ((namedRefFileName == null) || (namedRefFileName.length == 0)) {
			throw new Exception("���ݼ�" + componentDataset.getProperty("object_name") + namedRefName + "����������û���ļ�");
		}

		String fileDirName[] = new String[namedRefFileName.length];
		for (int i = 0; i < namedRefFileName.length; i++) {
			File tempFileObject = new File(localDir, namedRefFileName[i]);
			if (tempFileObject.exists()) {
				tempFileObject.delete();
			}
			File fileObject = componentDataset.getFile(namedRefName, namedRefFileName[i], localDir);
			fileDirName[i] = fileObject.getAbsolutePath();
		}
		
		return fileDirName;
	}
	
	private void UploadFile(TCComponent component, String path1) throws Exception {
		File file = new File(path1);
		if (file.exists()) {
			// ����MSExcelXҪ�����ù�ϵexcel��Ӧ���У�����
//			TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
//					path1,
//					"MSExcelX",
//					"excel",
//					DatasetName);
			String DatasetName = excelName;
			if (path1.endsWith(fupeiName1 + ".xlsx")) {
				DatasetName = fupeiName1;
			}
			TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					path1,
					"U8_Excel",
					"U8_excel",
					DatasetName);
			component.add("IMAN_specification", dataSet);
		}
	}
	
	private void OrganiseUpload() throws Exception {
		for (String filePath : filePathList) {
			UploadFile(mItemRev, filePath);
		}
		
//		String path1 = path + mItemRev.getProperty("item_id") + excelName + ".xlsx";
//		UploadFile(mItemRev, path1, excelName);
//		for(Excel1Bean bean : excelList1){
//			path1 = path + bean.itemRevision.getProperty("item_id") + fupeiName1 + ".xlsx";
//			UploadFile(bean.itemRevision, path1, fupeiName1);
//		}
	}
	
	private TCComponentDataset GetDatasetByNameAndCode(String code, String fileName) throws Exception {
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(
				query, 
				new String[]{ Const.QueryKey.TYPE, Const.QueryKey.NAME, Const.QueryKey.OWNER }, 
				new String[]{ "Folder", "Home", "infodba" });
		if(searchResult == null || searchResult.length == 0){
			throw new Exception("infodba��û���ҵ�Home�ļ��У�����ϵ����Ա����");
		}
		if(searchResult.length != 1){
			throw new Exception("infodba���ж��Home�ļ��У�����ϵ����Ա���");
		}
		
		TCComponentFolder homeFolder = (TCComponentFolder) searchResult[0];
		homeFolder.refresh();
		AIFComponentContext[] children = homeFolder.getChildren();
		// ����Home�ļ����ҵ� ���ݼ�ģ�� �ļ���
		for(AIFComponentContext context : children){
			TCComponent component = (TCComponent) context.getComponent();
			if(component instanceof TCComponentFolder && component.getProperty("object_name").equals(Const.CommonCosnt.Model_File_Root_Name)){
				component.refresh();
				AIFComponentContext[] children_1 = component.getChildren();
				// ���� ���ݼ�ģ�� �ļ����ҵ���Ӧ�Ĳ����ļ���
				for(AIFComponentContext context_1 : children_1){
					TCComponent component_1 = (TCComponent) context_1.getComponent();
					if(component_1 instanceof TCComponentFolder && component_1.getProperty("object_name").equals(code)){
						component_1.refresh();
						AIFComponentContext[] children_2 = component_1.getChildren();
						// ������Ӧ�Ĳ����ļ����ҵ���Ӧ�����ݼ�ģ��
						for(AIFComponentContext context_2 : children_2){
							TCComponent component_2 = (TCComponent) context_2.getComponent();
							if(component_2 instanceof TCComponentDataset && fileName.contains(component_2.getProperty("object_name"))){
								String datasetType = component_2.getType();
								if(datasetType.equals("U8_Excel")){
									return (TCComponentDataset)component_2;
								}
							}
						}
					}
				}
			}				
		}
		
		return null;
	}
	
	@SuppressWarnings("unused")
	private boolean GetNewDirectory() {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
			System.gc();
			if (file.exists()) {
				return false;
			} else {
				return file.mkdirs();
			}
		} else {
			return file.mkdirs();
		}
	}
}

class Excel1Bean{
	TCComponentItemRevision itemRevision = null;
	ArrayList<ExcelBean> list = null;
	
	public Excel1Bean(TCComponentItemRevision itemRevision){
		this.itemRevision = itemRevision;
	}
	
	public void AddBeanToList(ExcelBean excelBean){
		if(list == null){
			list = new ArrayList<ExcelBean>();
		}
		
		list.add(excelBean);
	}
}

/**
 * bl_item_item_id --- ID</br>
 * bl_item_object_name --- ����</br>
 * U8_inventory --- Ͷ����</br>
 * U8_alternate --- ԭ���滻����</br>
 * U8_alternateitem --- ����滻����</br>
 * U8_groupitem --- ����滻˵��</br>
 * U8_minmaterial --- С��</br>
 * 
 * @author Jr
 * @see nothing
 */
class ExcelBean{
	String bl_item_item_id = "";
	String bl_item_object_name = "";
	String U8_inventory = "";
	String U8_alternate = "";
	String U8_alternateitem = "";
	String U8_groupitem = "";
	String U8_minmaterial = "";
	
	public ExcelBean(
			String bl_item_item_id, String bl_item_object_name, String U8_inventory, 
			String U8_alternate, String U8_alternateitem, String U8_groupitem,
			String U8_minmaterial)
	{
		this.bl_item_item_id = bl_item_item_id;
		this.bl_item_object_name =bl_item_object_name;
		this.U8_inventory = U8_inventory;
		this.U8_alternate = U8_alternate;
		this.U8_alternateitem = U8_alternateitem;
		this.U8_groupitem = U8_groupitem;
		this.U8_minmaterial = U8_minmaterial;
	}
	
	public ExcelBean(String[] properties) {
		this.bl_item_item_id = properties[0];
		this.bl_item_object_name = properties[1];
		this.U8_inventory = properties[2];
		this.U8_alternate = properties[3];
		this.U8_alternateitem = properties[4];
		this.U8_groupitem = properties[5];
		this.U8_minmaterial = properties[6];
	}
}