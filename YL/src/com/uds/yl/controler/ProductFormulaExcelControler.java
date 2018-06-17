package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.encoding.ser.DateSerializer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.regexp.recompile;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.ComplexMaterialBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IProductFormulaExcelService;
import com.uds.yl.service.impl.ProductFormulaExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;
//�����䷽��
public class ProductFormulaExcelControler implements BaseControler {

	private IProductFormulaExcelService iProductFormulaExcelService = new ProductFormulaExcelServiceImpl();

	private List<MaterialBean> allMaterialBeanList;// �䷽�ĵ����ԭ��
	private List<TCComponentBOMLine> allMaterialBomLineList;// �䷽�ĵ���ԭ�϶�ӦBOM

	private List<MinMaterialBean> allMinMaterialBeanLis;// ���е�С��
	private List<ComplexMaterialBean> allComplexMaterialBeanList;// ���еĸ���ԭ��

	private Workbook wb = null;
	private TCComponentItemRevision mItemRev;

	@Override
	public void userTask(TCComponentItemRevision itemRev) {
		this.mItemRev = itemRev;
		// ��ȡ���еĵ����ԭ��ʵ����
		allMaterialBeanList = iProductFormulaExcelService.getAllMaterialBeanList(itemRev);
		if (allMaterialBeanList == null ) {
			MessageBox.post("��鿴�䷽��BOM�ṹ", "", MessageBox.ERROR);
			return;
		}

		// ��ȡ���еĵ����ԭ��BOMLine
		allMaterialBomLineList = iProductFormulaExcelService.getAllMaterialBomLineList(itemRev);

		// �������п��Ա������ԭ��
		iProductFormulaExcelService.handleCanReplectMaterial(allMaterialBeanList);

		// ��ȡ���е�С��
		allMinMaterialBeanLis = iProductFormulaExcelService.getAllMimMaterialList(allMaterialBeanList);

		// ��ȡ���еĸ������͵�ԭ��
		allComplexMaterialBeanList = iProductFormulaExcelService.getAllComplexMaterialBeanList(itemRev);

		// �������еĿ��������ԭ��
		for (ComplexMaterialBean complexMaterialBean : allComplexMaterialBeanList) {
			iProductFormulaExcelService.handleCanReplectMaterial(complexMaterialBean.allChildsMaterial);
		}

		
		//����ģ��
		if(!downTemplate()){
			MessageBox.post("ģ������ʧ������","",MessageBox.INFORMATION);
			return;
		}
		
		// ���ɱ���
		{
			File inFile = new File(Const.ProductFormulaExcel.IN_EXCEL_PATH);
			File outFile = new File(Const.ProductFormulaExcel.OUT_EXCEL_PATH);
			if (outFile.exists()) {
				outFile.delete();
			}
			try {
				wb = new XSSFWorkbook(new FileInputStream(inFile));
			} catch (FileNotFoundException e2) {
				e2.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			//�����䷽sheet
			writeProductSheetToExcel(allMaterialBeanList, allMinMaterialBeanLis);
			
			//С��sheet
			writeMinMaterialSheetToExcel(allMinMaterialBeanLis);
			
			
			//����ԭ��sheet
			writeComplexMaterialSheetToExcel(allComplexMaterialBeanList);
			
			FileOutputStream out;
			try {
				out = new FileOutputStream(outFile);
				wb.write(out);
				out.close();
				
				//�ϴ�
				if(!uploadFile(mItemRev)){//�ϴ�ʧ��
					MessageBox.post("�ϴ�ʧ������","",MessageBox.INFORMATION);
					return ;
				}
			} catch (FileNotFoundException e1) {
			} catch (IOException e) {
			}

//			try {
//				Runtime.getRuntime().exec("cmd /c start " + Const.ProductFormulaExcel.OUT_EXCEL_PATH);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

		}
		
	}

	/**
	 * @param materialBeanList  �䷽�µ����е�ԭ��
	 * @param minMaterialBeanList  ���е�С��
	 * �����䷽sheetҳ
	 */
	private void writeProductSheetToExcel(List<MaterialBean> materialBeanList,
			List<MinMaterialBean> minMaterialBeanList) {

		// ���ñ߿�
		 CellStyle cellBorderStyle = wb.createCellStyle();
		 cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

		Sheet sheet = wb.getSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);
		if(sheet==null){
			sheet = wb.createSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);// �����䷽
		}
		
		// ��ʼ�����⣺

		Row row = sheet.createRow(102);
		{
			//���ϲ��ĵ�Ԫ����ϱ߿�
			Cell cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
			
			cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
		}

		Cell cell1 = row.createCell(1);
		Cell cell2 = row.createCell(4);

//		CellStyle cellColorStyle = wb.createCellStyle();
//		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

//		cell1.setCellStyle(cellColorStyle);
		cell1.setCellStyle(cellBorderStyle);
//		cell2.setCellStyle(cellColorStyle);
		cell2.setCellStyle(cellBorderStyle);
		cell1.setCellValue("����");
		cell2.setCellValue("Ͷ����");

		// дԭ��
		int rowIndex = 103;
		for (MaterialBean bean : materialBeanList){
			if("".equals(bean.minMaterialType)){//����С�ϵ�
				row = sheet.createRow(rowIndex);
				Cell cell = row.createCell(1);
				cell.setCellValue(bean.objectName);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(bean.U8_inventory+"");
				rowIndex++;
				
				{
					//���ϲ��ĵ�Ԫ����ϱ߿�
					cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
					
					cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
				}
			}
		}

		// дС��
		for (MinMaterialBean bean : minMaterialBeanList) {
			row = sheet.createRow(rowIndex);
			Cell cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
			cell.setCellValue(bean.name);
			cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
			cell.setCellValue("һ��");
			rowIndex++;
			
			
			{
				//���ϲ��ĵ�Ԫ����ϱ߿�
				cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
				
				cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
				cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
			}
		}
		
	}
	
	
	/**
	 * @param minMaterialBeanList
	 * дС��sheetҳ
	 */
	private void writeMinMaterialSheetToExcel(List<MinMaterialBean> minMaterialBeanList) {

		// ���ñ߿�
		 CellStyle cellBorderStyle = wb.createCellStyle();
		 cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		 cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

		Sheet sheet = wb.getSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);// С��sheetҳ
		if(sheet==null){
			wb.createSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);
		}
				
		// ��ʼ�����⣺

		CellRangeAddress mergeArea=new CellRangeAddress(157, 157, 1, 2);
		sheet.addMergedRegion(mergeArea);
		Row row = sheet.createRow(157);
		

		{
			//���ϲ��ĵ�Ԫ����ϱ߿�
			Cell cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
			
			cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
			
			cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
			cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
		}

		Cell cell1 = row.createCell(1);cell1.setCellStyle(cellBorderStyle);
		Cell cell2 = row.createCell(3);cell2.setCellStyle(cellBorderStyle);
		Cell cell3 = row.createCell(5);cell3.setCellStyle(cellBorderStyle);

//		CellStyle cellColorStyle = wb.createCellStyle();
//		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

//		cell1.setCellStyle(cellColorStyle);
//		cell2.setCellStyle(cellColorStyle);
//		cell3.setCellStyle(cellColorStyle);
		cell1.setCellValue("����");
		cell2.setCellValue("���");
		cell3.setCellValue("Ͷ����");
		

		// дԭ��
		int rowIndex = 158;

		// дС��
		for (MinMaterialBean minMaterialBean : minMaterialBeanList) {
			List<MaterialBean> replaceOtherBeanList = new ArrayList<>();
			List<MaterialBean> canBeReplaceBeanList = new ArrayList<>();
			for(MaterialBean materialBean : minMaterialBean.allChildsMaterial){
				if(materialBean.canReplace){
					replaceOtherBeanList.add(materialBean);
				}else {
					canBeReplaceBeanList.add(materialBean);
				}
			}
			mergeArea=new CellRangeAddress(rowIndex, rowIndex+canBeReplaceBeanList.size()-1, 1, 2);
			sheet.addMergedRegion(mergeArea);
			for(MaterialBean materialBean : canBeReplaceBeanList){
				row = sheet.createRow(rowIndex++);
				Cell cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(minMaterialBean.name);
				
				cell = row.createCell(3);cell.setCellStyle(cellBorderStyle);
				String materialName = getReplaceMaterialStr(materialBean,replaceOtherBeanList);
				cell.setCellValue(materialName);
				
				cell = row.createCell(5);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(materialBean.U8_inventory);
				{
					//���ϲ��ĵ�Ԫ����ϱ߿�
					cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
					
					cell = row.createCell(4);cell.setCellStyle(cellBorderStyle);
					
					cell = row.createCell(6);cell.setCellStyle(cellBorderStyle);
					cell = row.createCell(7);cell.setCellStyle(cellBorderStyle);
				}
			}
		}
	}

	//����ԭ�ϱ�
	private void writeComplexMaterialSheetToExcel(List<ComplexMaterialBean> complexMaterialBeanList){
		File inFile = new File(Const.ProductFormulaExcel.COMPLEX_IN_EXCEL_PATH);
		File outFile = new File(Const.ProductFormulaExcel.COMPLEX_OUT_EXCEL_PATH);
		
		Workbook wbTemp = null;
		try {
			wbTemp = new XSSFWorkbook(new FileInputStream(inFile));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Sheet sheet = wbTemp.getSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);// ������sheet
		if(sheet==null){
			sheet = wbTemp.createSheet(Const.ProductFormulaExcel.EXCEL_SHEET1);
		}
		// ���ñ߿�
		CellStyle cellBorderStyle = wbTemp.createCellStyle();
		cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);
		// ��ʼ�����⣺

		Row row = sheet.createRow(102);

		Cell cell1 = row.createCell(0);
		Cell cell2 = row.createCell(1);
		Cell cell3 = row.createCell(2);

//		CellStyle cellColorStyle = wb.createCellStyle();
//		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
//		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

		cell1.setCellStyle(cellBorderStyle);
		cell2.setCellStyle(cellBorderStyle);
		cell3.setCellStyle(cellBorderStyle);
		
		cell1.setCellValue("��Ʒ����");
		cell2.setCellValue("ԭ�ϴ���");
		cell3.setCellValue("�滻���");
		

		// д����ԭ��
		int rowIndex = 103;

		// д����ԭ��
		for (ComplexMaterialBean complexMaterialBean : complexMaterialBeanList) {
			List<MaterialBean> replaceOtherBeanList = new ArrayList<>();
			List<MaterialBean> canBeReplaceBeanList = new ArrayList<>();
			List<MaterialBean> singleBeanList = new ArrayList<>();
			List<MaterialBean> canBeReplaceExceptSingleBeanList = new ArrayList<>();
			for(MaterialBean materialBean : complexMaterialBean.allChildsMaterial){
				if(materialBean.canReplace){
					replaceOtherBeanList.add(materialBean);
				}else {
					canBeReplaceBeanList.add(materialBean);
				}
			}
			for(MaterialBean canBeReplaceBean : canBeReplaceBeanList){
				boolean isSingle = true;
				for(MaterialBean replaceOtherBean : replaceOtherBeanList){
					if(replaceOtherBean.alternateItem.equals(canBeReplaceBean.objectName)){
						//˵���ǿ��Ա��滻��
						isSingle = false;
						canBeReplaceExceptSingleBeanList.add(canBeReplaceBean);
						break;
					}
				}
				if(isSingle) singleBeanList.add(canBeReplaceBean);
			}
			
			//������û���滻���ԭ��
			CellRangeAddress mergeArea=new CellRangeAddress(rowIndex, rowIndex+complexMaterialBean.allChildsMaterial.size()-1, 0, 0);
			sheet.addMergedRegion(mergeArea);
			for(MaterialBean materialBean : singleBeanList){
				row = sheet.createRow(rowIndex++);
				Cell cell = row.createCell(0);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(complexMaterialBean.rootMaterial.objectName);
				
				cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(materialBean.objectName);
				
				cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
				
			}
			//�����滻���ԭ��
			for(MaterialBean materialBean : canBeReplaceExceptSingleBeanList){//�ɱ������
				List<MaterialBean> allReplaceBeanList = new ArrayList<>();
				for(MaterialBean replaceOtehrBean : replaceOtherBeanList){
					if(materialBean.objectName.equals(replaceOtehrBean.alternateItem)){//���������滻��ԭ��
						allReplaceBeanList.add(replaceOtehrBean);
					}
				}
				
				mergeArea=new CellRangeAddress(rowIndex, rowIndex+allReplaceBeanList.size(), 2, 2);
				sheet.addMergedRegion(mergeArea);
				//дallReplaceBeanList
				row = sheet.createRow(rowIndex++);
				Cell cell = row.createCell(0);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(complexMaterialBean.rootMaterial.objectName);
				cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue(materialBean.objectName);
				cell = row.createCell(2);cell.setCellStyle(cellBorderStyle);
				cell.setCellValue("��ѡ��һ�ϲ���Ԫ��");
				
				//�������滻��ѡ��
				for(MaterialBean bean :allReplaceBeanList){
					row = sheet.createRow(rowIndex++);
					
					cell = row.createCell(1);cell.setCellStyle(cellBorderStyle);
					cell.setCellValue(bean.objectName);
				}
			}
		}
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(outFile);
			wbTemp.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}

//		try {
//			Runtime.getRuntime().exec("cmd /c start " + Const.ProductFormulaExcel.COMPLEX_OUT_EXCEL_PATH);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	/**
	 * @param canBeReplaceMaterialBean  Ҫ�������
	 * @param materialBeanList  ���е������������ԭ�ϵļ���
	 * @return
	 * ��ȡ���Ա������ԭ�ϵ����п�����
	 */
	private String getReplaceMaterialStr(MaterialBean canBeReplaceMaterialBean,List<MaterialBean> materialBeanList){
		StringBuilder result = new StringBuilder();
		result.append(canBeReplaceMaterialBean.objectName);
		for(MaterialBean bean : materialBeanList){
			if(canBeReplaceMaterialBean.objectName.equals(bean.alternateItem)){
				result.append(" �� ");
				result.append(bean.objectName);
			}
		}
		return result.toString();
	}



	/**
	 * ����ģ���ļ�
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.ProductFormulaExcel.Product_Formula_Excel_Input_Path);
		if(file.exists()){//���ھ�ɾ��
			file.delete();
		}
		file = new File(Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path);
		if(file.exists()){//���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.ProductFormulaExcel.Product_Formula_Excel_Name);
		if(dataset==null){
			return false;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.ProductFormulaExcel.Template_Dir);
		if(resultStrs.length==0){//����ʧ��
			return false;
		}
		dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.ProductFormulaExcel.Product_Complex_Excel_Name);
		resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, Const.ProductFormulaExcel.Template_Dir);
		if(resultStrs.length==0){//����ʧ��
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * �����ɵ��ļ�
	 * @param component
	 * @return
	 */
	public boolean uploadFile(TCComponent component){
		 File file = new File(Const.ProductFormulaExcel.Product_Formula_Excel_Input_Path);
		 File fileComplex = new File(Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path);
		 if(file.exists()&&fileComplex.exists()){//���ھ��ϴ�
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					 Const.ProductFormulaExcel.Product_Formula_Excel_Input_Path,
					 DataSetUtil.DataSetType.MSExcelX, 
					 "excel", 
					 Const.ProductFormulaExcel.Product_Formula_Excel_Upload_Name);
			 
			 try {
				 component.add("IMAN_specification", dataSet);
				 
				 dataSet = DataSetUtil.setDatasetFileToTC(
						 Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path,
						 DataSetUtil.DataSetType.MSExcelX, 
						 "excel", 
						 Const.ProductFormulaExcel.Product_Complex_Excel_Upload_Name);
				 component.add("IMAN_specification", dataSet);
				 
			} catch (TCException e) {
				e.printStackTrace();
				return false;
			}
		 }
		
		return true;
	}


}
