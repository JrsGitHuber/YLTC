package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.MaterialaccBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IAccessoriesTechStandardExcelService;
import com.uds.yl.tcutils.BomUtil;

public class AccessoriesTechStandardServiceImpl implements IAccessoriesTechStandardExcelService{

	//创建一个全局的wb
	Workbook wb = new XSSFWorkbook();
	CellStyle cellColorStyle = wb.createCellStyle();
	
	@Override
	public TCComponentBOMLine getTopBOMLine(TCComponentItemRevision itemRevision) {
		TCComponentBOMLine topBOMLine=null;
		topBOMLine=BomUtil.getTopBomLine(itemRevision, Const.MaterialorAccIndexStandard.BOMNAME);
		return topBOMLine;	
	}

	@Override
	public List<TCComponentBOMLine> getmaterialIndexBomList(TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> materialIndexBomList = new ArrayList<>();
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				bomQueue.offer(bomLineTemp);
				
			}
		} catch (TCException e) {
			return materialIndexBomList;
		}
		// 遍历
		while (!bomQueue.isEmpty()) {// 队列不为空就接着遍历
			TCComponentBOMLine parentBom = bomQueue.poll();
			materialIndexBomList.add(parentBom);
			try {
				AIFComponentContext[] children = parentBom.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
						bomQueue.offer(bomLineTemp);
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return materialIndexBomList;
	}

	@Override
	public void getIndexBeanList(List<TCComponentBOMLine> materialIndexBomList) {
		
		List<MaterialaccBean> materialaccBeanList =new ArrayList<>();
		for(TCComponentBOMLine bomLine : materialIndexBomList)
		{
			try {
				MaterialaccBean bean = AnnotationFactory.getInstcnce(MaterialaccBean.class, bomLine);
				materialaccBeanList.add(bean);

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
	
		Set<String> categorySet = new HashSet<>();
		for(int i=0;i<materialaccBeanList.size();i++)
		{
			categorySet.add(materialaccBeanList.get(i).indexType);
		}
		Iterator<String> iterator = categorySet.iterator();
		
		//创建一个全局sheet页
		File outFile = new File(Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH);
		if (outFile.exists()) {
			outFile.delete();
		}
		// 设置边框
		CellStyle cellBorderStyle = wb.createCellStyle();
		cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

	
		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellColorStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());

		Sheet sheet = wb.createSheet("sheet1");
		//便利Bean对象，对sheet页写
		int k=0;
		int j=0;
		int[] arr=new int[100];
		int i=0;
		while(iterator.hasNext()){
			
			String next = iterator.next();
			List<MaterialaccBean> tempList = new ArrayList<>();
			for(MaterialaccBean bean : materialaccBeanList){
				if(next.equals(bean.indexType)){
					tempList.add(bean);
				}
			}
			//存储了第一次的size
			arr[i]=tempList.size();
			i++;
			if(j>0)
			{
				k=k+arr[i-2]+3;
			}
			j++;
			DoIndexExcel(tempList,sheet,k,j);
		}
		
		//将WB保存到文件中
		FileOutputStream out;
		try {
			out = new FileOutputStream(outFile);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		try {
			Runtime.getRuntime().exec("cmd /c start "+Const.MaterialorAccIndexStandard.ACCESSORIES_INDEXSTANDARD_EXCEL_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
        
	// k要为上一个tempList的size
	public void DoIndexExcel(List<MaterialaccBean> tempList,Sheet sheet,int k,int j) {
		// TODO Auto-generated method stub
		sheet.setColumnWidth(1,3000);sheet.setColumnWidth(6,4000);
		// 初始化标题：
		Row row = sheet.createRow(0);
		Cell cell1 = row.createCell(0);
		Cell cell2 = row.createCell(1);
		Cell cell3 = row.createCell(2);
		Cell cell4 = row.createCell(3);
		Cell cell5 = row.createCell(4);
		Cell cell6 = row.createCell(5);
		Cell cell7 = row.createCell(6);

		cell1.setCellStyle(cellColorStyle);
		cell2.setCellStyle(cellColorStyle);
		cell3.setCellStyle(cellColorStyle);
		cell4.setCellStyle(cellColorStyle);
		cell5.setCellStyle(cellColorStyle);
		cell6.setCellStyle(cellColorStyle);
		cell7.setCellStyle(cellColorStyle);
		cell1.setCellValue("指标类型");
		cell2.setCellValue("指标名称");
		cell3.setCellValue("上限");
		cell4.setCellValue("上限符号");
		cell5.setCellValue("下限");
		cell6.setCellValue("下限符号");
		cell7.setCellValue("标准描述");
		if(j==1)
		{
			for (int i = 0; i < tempList.size(); i++) {
			MaterialaccBean checkedBean = tempList.get(i);
			row = sheet.createRow(i+1);
			cell1 = row.createCell(0);
			cell2 = row.createCell(1);
			cell3 = row.createCell(2);
			cell4 = row.createCell(3);
			cell5 = row.createCell(4);
			cell6 = row.createCell(5);
			cell7 = row.createCell(6);
			cell1.setCellValue(checkedBean.indexType);
			cell2.setCellValue(checkedBean.indexName);
			cell3.setCellValue(checkedBean.up);
			cell4.setCellValue(checkedBean.upMark);
			cell5.setCellValue(checkedBean.down);
			cell6.setCellValue(checkedBean.downMark);
			if((checkedBean.up==null||"".equals(checkedBean.up))&&(checkedBean.down==null||"".equals(checkedBean.down))){
				cell7.setCellValue(checkedBean.indexDec);
			}
			}
			
		}else {
			for (int i = 0; i < tempList.size(); i++) {
				MaterialaccBean checkedBean = tempList.get(i);
				row = sheet.createRow(k+i);
				cell1 = row.createCell(0);
				cell2 = row.createCell(1);
				cell3 = row.createCell(2);
				cell4 = row.createCell(3);
				cell5 = row.createCell(4);
				cell6 = row.createCell(5);
				cell7 = row.createCell(6);
				cell1.setCellValue(checkedBean.indexType);
				cell2.setCellValue(checkedBean.indexName);
				cell3.setCellValue(checkedBean.up);
				cell4.setCellValue(checkedBean.upMark);
				cell5.setCellValue(checkedBean.down);
				cell6.setCellValue(checkedBean.downMark);
				if((checkedBean.up==null||"".equals(checkedBean.up))&&(checkedBean.down==null||"".equals(checkedBean.down))){
					cell7.setCellValue(checkedBean.indexDec);
				}
				}
		}
		
	}

}
