package com.uds.yl.controler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.base.BaseControler;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IMaterialTechStandardExcelService;
import com.uds.yl.service.impl.MaterialTechStandardExcelServiceImpl;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;

//���ϼ�����׼����
public class AccessoriesTechStandardExcelControler implements BaseControler {
	private IMaterialTechStandardExcelService imaterialTechStandardService = new MaterialTechStandardExcelServiceImpl();
	
	private TCComponentBOMLine topBomLine = null;//������׼�汾��BOM��topbomline
	private List<TCComponentBOMLine> allIndexBomList = null;//������׼�е�����ָ����BOM����
	private List<IndexItemBean> allIndexBeanList = null;//������׼������ָ�����Bean����
	
	private int mIncrement = 0;//����  �ӵڶ������Ϳ�ʼ��Ҫ�ۼ��������
	private int mSensoryStart = 130;//�й�Ҫ��ʼ�к� *
	private int mPhysicalStart = 142;//��ָ�꿪ʼ�к� *
	private int mHealthStart = 152;//��Ⱦ��ָ��(�滻������ȫ)
	private int mMicroorganismStart = 161;//΢����ָ�꿪ʼ�к�*
	private int mOtherStart = 170;//����ָ�꿪ʼ�к�
	
	private List<TCComponentBOMLine> mSensoryBomList = new ArrayList<>();//�й�Ҫ���BOm����
	private List<TCComponentBOMLine> mPhysicalBomList = new ArrayList<>();//��ָ���Bom����
	private List<TCComponentBOMLine> mHealthBomList = new ArrayList<>();//��Ⱦ��ָ��
	private List<TCComponentBOMLine> mMicroorganismBomList = new ArrayList<>();//΢����ָ��
	private List<TCComponentBOMLine> mOtherBomList = new ArrayList<>();//������������ʱ��ʹ��
	
	private List<IndexItemBean> mSensoryBeanList = new ArrayList<>();//�й�ָ���Bean����
	private List<IndexItemBean> mPhysicalBeanList = new ArrayList<>();//��ָ���Bean����
	private List<IndexItemBean> mHealthBeanList = new ArrayList<>();//��Ⱦ��ָ���Bean����
	private List<IndexItemBean> mMicroorganismBeanList = new ArrayList<>();//΢����ָ��Bean����
	private List<IndexItemBean> mOtherBeanList = new ArrayList<>();//���������͵�Bean������ʱ����ʹ��
	
	
	
	//ָ����������������u8_category
	//
	public void userTask(TCComponentItemRevision itemRev) {
		//����ģ��
		if(!downTemplate()){
			MessageBox.post("ģ������ʧ��","",MessageBox.INFORMATION);
			return;
		}
		
		
		
		
		//�����䷽��ȡ�䷽��topBOM
		 topBomLine=imaterialTechStandardService.getTopBOMLine(itemRev);
		 if(topBomLine==null){
				MessageBox.post("���鼼����׼��BOM�ṹ","",MessageBox.ERROR);
				return;
		 }
		//�����䷽��ȡԭ���ϱ�׼�ĵ�BOM
		 allIndexBomList = imaterialTechStandardService.getAllIndexBomList(topBomLine);
		 //��ȡ������׼�µ�����ָ���Bean����
		 allIndexBeanList = imaterialTechStandardService.getAllIndexBeanList(allIndexBomList);
		 
		//��ʼ�� ��ȡ����������׼�µĲ�ͬ�����bom���Ϻ�bom��Ӧ��ʵ����ļ���
		mSensoryBomList = imaterialTechStandardService.getSensoryBomList(allIndexBomList);
		mPhysicalBomList = imaterialTechStandardService.getPhysicalBomList(allIndexBomList);
		mHealthBomList = imaterialTechStandardService.getHealthBomList(allIndexBomList);
		mMicroorganismBomList = imaterialTechStandardService.getMicroorganismBomList(allIndexBomList);

		mSensoryBeanList = imaterialTechStandardService.getSensoryBeanList(mSensoryBomList);
		mPhysicalBeanList = imaterialTechStandardService.getPhysicalBeanList(mPhysicalBomList);
		mHealthBeanList = imaterialTechStandardService.getHealthBeanList(mHealthBomList);
		mMicroorganismBeanList = imaterialTechStandardService.getMicroorganismBeanList(mMicroorganismBomList);
		
		
		// �ж�BOM�Ĳ�ͬ��������д��excel�еĹ̶�λ��
		File inFile = new File(Const.MaterialorAccIndexStandard.MATERIAL_INDEXSTANDARD_EXCEL_INPUT_PATH);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet("Sheet1");
			
			//ÿһ��Ŀ�ʼ��Ҫ�ۼ�һ��ֵ
			
			
			/*
			 * 1.�й�ָ��
			 * 2.��ָ��
			 * 3.��Ⱦ��ָ��
			 * 4.΢����ָ��
			 * ÿ��дһ�оͲ���һ�� ����++������Ƿֿ�����һ���Ļ���ֱ��дһ�е��ǲ�����һ����
			 * */
			
			//1.�й�ָ��  mSensoryBomList  mSensoryBeanList
			for(int i=0;i<mSensoryBeanList.size();i++){
				
				//�Ⱥϲ���Ԫ��
				CellRangeAddress cra=new CellRangeAddress(mSensoryStart, mSensoryStart, 1, 3);        
			    sheet.addMergedRegion(cra);  
			    //�ϲ���Ԫ���2���ֶ�
			    cra=new CellRangeAddress(mSensoryStart,mSensoryStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //�ϲ���Ԫ���3���ֶ�
			    cra=new CellRangeAddress(mSensoryStart,mSensoryStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
			       
				IndexItemBean bean = mSensoryBeanList.get(i);
				Cell cell = getCell(sheet, mSensoryStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mSensoryStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mSensoryStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				if(i<mSensoryBeanList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
					sheet.shiftRows(mSensoryStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//���һ������������1
				}
			}
			
			//2.��ָ�� mPhysicalBomList mPhysicalBeanList
			mPhysicalStart = mPhysicalStart+mIncrement;//����֮ǰ��������ֵ
			for(int i=0;i<mPhysicalBeanList.size();i++){
				
				//�Ⱥϲ���Ԫ��
				CellRangeAddress cra=new CellRangeAddress(mPhysicalStart, mPhysicalStart, 1, 3);        
			    sheet.addMergedRegion(cra);  
			    //�ϲ���Ԫ���2���ֶ�
			    cra=new CellRangeAddress(mPhysicalStart,mPhysicalStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //�ϲ���Ԫ���3���ֶ�
			    cra=new CellRangeAddress(mPhysicalStart,mPhysicalStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
				
				IndexItemBean bean = mPhysicalBeanList.get(i);
				Cell cell = getCell(sheet, mPhysicalStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mPhysicalStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mPhysicalStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				
				if(i<mPhysicalBeanList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
					sheet.shiftRows(mPhysicalStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//���һ������������1
				}
			}
			
			//3.��Ⱦ��ָ��  mHealthBomList   mHealthBeanList
			mHealthStart = mHealthStart +mIncrement;
			for(int i=0;i<mHealthBeanList.size();i++){
				
				//�Ⱥϲ���Ԫ��
				CellRangeAddress cra=new CellRangeAddress(mHealthStart, mHealthStart, 1, 3);        
			    sheet.addMergedRegion(cra);
			    //�ϲ���Ԫ���2���ֶ�
			    cra=new CellRangeAddress(mHealthStart,mHealthStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //�ϲ���Ԫ���3���ֶ�
			    cra=new CellRangeAddress(mHealthStart,mHealthStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
				
				IndexItemBean bean = mHealthBeanList.get(i);
				Cell cell = getCell(sheet, mHealthStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mHealthStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mHealthStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				
				if(i<mHealthBeanList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
					sheet.shiftRows(mHealthStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//���һ������������1
				}
			}
			
			//4.΢����ָ��  mMicroorganismBomList mMicroorganismBeanList
			mMicroorganismStart = mMicroorganismStart + mIncrement;
			for(int i=0;i<mMicroorganismBeanList.size();i++){
				
				//�ϲ���Ԫ���1���ֶ�
				CellRangeAddress cra=new CellRangeAddress(mMicroorganismStart,mMicroorganismStart, 1, 3);        
			    sheet.addMergedRegion(cra); 
			    //�ϲ���Ԫ���2���ֶ�
			    cra=new CellRangeAddress(mMicroorganismStart,mMicroorganismStart, 4, 5);        
			    sheet.addMergedRegion(cra); 
			    //�ϲ���Ԫ���3���ֶ�
			    cra=new CellRangeAddress(mMicroorganismStart,mMicroorganismStart, 6, 7);        
			    sheet.addMergedRegion(cra); 
			    
				IndexItemBean bean = mHealthBeanList.get(i);
				Cell cell = getCell(sheet, mMicroorganismStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				cell = getCell(sheet, mMicroorganismStart, 4);
				cell.setCellValue(bean.indicatorRequire);setCellBorder(cell, wb);
				cell = getCell(sheet, mMicroorganismStart, 6);
				cell.setCellValue(bean.testCriterion);setCellBorder(cell, wb);
				if(i<mMicroorganismBeanList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
					sheet.shiftRows(mMicroorganismStart, sheet.getLastRowNum(), 1);
				}else{
					mIncrement++;//���һ������������1
				}
			}
	        
			//����
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		 
		if(!uploadFile(itemRev)){
			MessageBox.post("�ļ��ϴ�ʧ��","",MessageBox.INFORMATION);
			return;
		}
		MessageBox.post("OK","",MessageBox.INFORMATION);
	}
	
	
	/**
	 * @param cell
	 * @param wb
	 * Ϊ��Ԫ�����ñ߿�
	 */
	private void setCellBorder(Cell cell,Workbook wb){
		 CellStyle cellStyle = wb.createCellStyle();
	     cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	     cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	     cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	     cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	     
	     cell.setCellStyle(cellStyle);
	}
	
	
	/**
	 * @param sheet �Ǹ�sheetҳ��
	 * @param rowIndex ������
	 * @param cellIndex ��һ�е���һ����Ԫ��  ����Ǻϲ���Ԫ��Ļ����ǵ�һ��
	 * @return
	 */
	private Cell getCell(Sheet sheet,int rowIndex,int cellIndex){
		Row row = sheet.getRow(rowIndex);
		if(row==null){
			row = sheet.createRow(rowIndex);
		}
		Cell cell = row.getCell(cellIndex);
		if(cell==null){
			cell = row.createCell(cellIndex);
		}
		return cell;
	}



	/**
	 * ����ģ���ļ�
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH);
		if(file.exists()){//���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_NAME);
		if(dataset==null){
			return false;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, Const.MaterialorAccIndexStandard.TEMPLATE_DIR);
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
		 File file = new File(Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH);
		 if(file.exists()){//���ھ��ϴ�
			 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
					 Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH,
					 DataSetUtil.DataSetType.MSExcelX, 
					 "excel", 
					 Const.MaterialorAccIndexStandard.Accessories_Excel_Name);
			 
			 try {
				 component.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				return false;
			}
		 }
		
		return true;
	}

	
}
