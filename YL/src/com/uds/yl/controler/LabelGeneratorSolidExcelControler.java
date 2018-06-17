package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.ILabelGeneratorSolidExcelService;
import com.uds.yl.service.IMilkPowderFormulatorService;
import com.uds.yl.service.impl.LabelGeneratorSolidExcelServiceImpl;
import com.uds.yl.service.impl.MilkPowderFormulatorServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;
//��̬��ǩ������
public class LabelGeneratorSolidExcelControler implements BaseControler{
	private ILabelGeneratorSolidExcelService iLabelGeneratorSolidExcelService = new LabelGeneratorSolidExcelServiceImpl();

	private IMilkPowderFormulatorService milkPowderFormulatorService = new MilkPowderFormulatorServiceImpl();
	
	private List<IndexItemBean> sortedIndexItemBeanList;// ���ձ�ǩ�е��ֶε�˳���ʼ����һ������
	private List<IndexItemBean> allIndexItemBeanList;// ����BOM�µ����е�ָ��

	private List<ResultExcelBean> resultExcelBeansList;
	
	
	@Override
	public void userTask(TCComponentItemRevision itemRev) {
	}
	
	public void userTask(TCComponentItemRevision itemRev
			,TCComponentItemRevision wetLossItemRevsion,
			TCComponentItemRevision dryLossItemRevsion,
			TCComponentItemRevision dateLossItemRevsion){
		
		
		
		TCComponentBOMLine topBOmLine = BomUtil.getTopBomLine(itemRev, "��ͼ");
		if(topBOmLine==null){
			MessageBox.post("�����䷽��ͼ�ṹ","",MessageBox.INFORMATION);
			return;
		}
		
		// ��ȡ����ԭ�����������IndexItem
//		allIndexItemBeanList = iLabelGeneratorSolidExcelService.getIndexBeanList(topBOmLine);
		
		allIndexItemBeanList = milkPowderFormulatorService.getFinallIndexBeanList(topBOmLine, 
				wetLossItemRevsion, dryLossItemRevsion, dateLossItemRevsion);
		
		if (allIndexItemBeanList == null) {
			MessageBox.post("����BOM�ṹ", "", MessageBox.ERROR);
			return;
		}

		
		
		//����  ��ǩ��ģ���0����NRV
		File file = new File(Const.LabelGeneratorSolidExcel.LABEL_EXCEL_INPUT_PATH);
		if (file.exists()) {// ���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.LabelGeneratorSolidExcel.LABEL_EXCEL_NAME);
		if (dataset == null) {
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel,
				Const.LabelGeneratorSolidExcel.Template_Dir);
		if (resultStrs.length == 0) {// ����ʧ��
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}

		// ����0��ֵ��NRVֵֻ����ʹ�ò����ϴ�
		file = new File(Const.LabelGeneratorSolidExcel.NRV_ZERO_VALUE_EXCEL_INPUT_PATH);
		if (file.exists()) {// ���ھ�ɾ��
			file.delete();
		}
		code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.LabelGeneratorSolidExcel.NRV_ZERO_VALUE__EXCEL_NAME);
		if (dataset == null) {
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}
		resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel,
				Const.LabelGeneratorSolidExcel.Template_Dir);
		if (resultStrs.length == 0) {// ����ʧ��
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}
		
		
		// ���ݱ�ǩ˳�򴴽�List<IndexItem>
		sortedIndexItemBeanList = iLabelGeneratorSolidExcelService.getSortedIndexItemBeanList();

		// �������е�IndexItem���ۼӶ�Ӧ��ֵ������ļ������Bean��ȥ
		sortedIndexItemBeanList = iLabelGeneratorSolidExcelService.initSortedIndexItemBeanList(allIndexItemBeanList, sortedIndexItemBeanList);

		//����0��ֵ�Ĺ���
		iLabelGeneratorSolidExcelService.filterZero(sortedIndexItemBeanList);
		
		
		resultExcelBeansList = new ArrayList<>();//resultbean��Ϊ�洢 100g�ĵ�λֵ��˵
		for(IndexItemBean bean : sortedIndexItemBeanList){
			ResultExcelBean resultExcelBean = new ResultExcelBean();
			resultExcelBean.name = bean.objectName;
			resultExcelBean.value = bean.bl_quantity;
			resultExcelBeansList.add(resultExcelBean);
		}
		
		//����NRV�ļ���
		iLabelGeneratorSolidExcelService.NRVCompute(sortedIndexItemBeanList);
		for(int i=0;i<sortedIndexItemBeanList.size();i++){
			IndexItemBean bean = sortedIndexItemBeanList.get(i);
			ResultExcelBean resultExcelBean = resultExcelBeansList.get(i);
			resultExcelBean.nrv = bean.bl_quantity;
		}
		
		

		// д�뵽Excel
		writeToExcel();
		
		
		
		//�ϴ� ��ǩģ��
		file = new File(Const.LabelGeneratorSolidExcel.LABEL_EXCEL_INPUT_PATH);
		if (file.exists()) {// ���ھ��ϴ�
			try {
				TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						Const.LabelGeneratorSolidExcel.LABEL_EXCEL_INPUT_PATH, DataSetUtil.DataSetType.MSExcelX,
						"excel", Const.LabelGeneratorSolidExcel.Label_Excel_Upload_Name);
				itemRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
				return;
			}
		}
	}
	

	/**
	 * ���ĺ�˳���Bena����д���Excel��ȥ
	 */
	private void writeToExcel() {
		// ======д����
		File outFile = new File(Const.LabelGeneratorSolidExcel.LABEL_EXCEL_INPUT_PATH);
		// д��Excel
		Workbook wb = null;
		try {
			wb = new XSSFWorkbook(new FileInputStream(outFile));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		// =======================Һ̬��ǩSheetҳ
		Sheet sheet = wb.getSheet(Const.LabelGeneratorSolidExcel.SHEET_NAME);// ��̬��ǩ

		// �Ǵ�row(18)��ʼ��
		int start = 18;
		for (int i = 0; i < sortedIndexItemBeanList.size(); i++) {
			IndexItemBean sortBean = sortedIndexItemBeanList.get(i);
			ResultExcelBean resultBean = resultExcelBeansList.get(i);
//			if ("".equals(sortBean.up) && "".equals(sortBean.down)) {
//				continue;
//			}

			Row row = sheet.getRow(start);
			if (row == null) {
				row = sheet.createRow(start);
			}

			//�ϲ���Ԫ��
			
			CellRangeAddress cra=new CellRangeAddress(start, start, 1, 2);  
			sheet.addMergedRegion(cra); 
			cra=new CellRangeAddress(start, start, 3, 4);  
			sheet.addMergedRegion(cra); 
			cra=new CellRangeAddress(start, start, 5, 6);  
			sheet.addMergedRegion(cra); 
			
			Cell cell1 = row.getCell(1);
			Cell cell2 = row.getCell(3);
			Cell cell3 = row.getCell(5);
			if (cell1 == null) {
				cell1 = row.createCell(1);
			}
			if (cell2 == null) {
				cell2 = row.createCell(3);
			}
			if (cell3 == null) {
				cell3 = row.createCell(5);
			}

			cell1.setCellValue(resultBean.name);
			cell2.setCellValue(resultBean.value);
			cell3.setCellValue(resultBean.nrv);
			
			sheet.shiftRows(start, sheet.getLastRowNum(), 1);//����һ��
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(new File("C:\\temp\\��̬��ǩ��temp.xlsx"));
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

		try {
			Runtime.getRuntime().exec("cmd /c start " + "C:\\temp\\��̬��ǩ��temp.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	class ResultExcelBean{
		String name ;
		String value;
		String nrv;
	}




}
