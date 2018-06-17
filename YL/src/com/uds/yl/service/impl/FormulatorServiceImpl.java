package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


















import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorExcelMaterialBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.IFormulatorService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.LogFactory;
import com.uds.yl.utils.StringsUtil;

public class FormulatorServiceImpl implements IFormulatorService {
	public Logger Logger = LogFactory.initLog("FormulatorServiceImpl", LogLevel.INFO.getValue());
	

	/**
	 * @param itemRevision
	 * @return ��ȡ�汾���Ѿ��е�BOM��ͼ�е�ԭ����
	 */
	@Override
	public List<MaterialBean> getInitBean(TCComponentItemRevision itemRevision) {
		List<MaterialBean> allMaterial = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
		AIFComponentContext[] bomChilds = null;
		if (topBomLine == null)
			return allMaterial;// Ϊ��˵��û��BOM��ͼ
		try {
			bomChilds = topBomLine.getChildren();
			if (bomChilds == null || bomChilds.length == 0)
				return allMaterial;// Ϊ��˵��û��BOM��ͼ��û��ֵ
		} catch (TCException e) {
			Logger.log(Level.ALL, "��ȡ����BOMLine�쳣", e);
		}
		for (AIFComponentContext context : bomChilds) {
			TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
			try {
				MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineChild);
				allMaterial.add(materialBean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return allMaterial;
	}

	/**
	 * @param materialName
	 * @param materialCode
	 * @param materialSupplier
	 * @return ��ȡ��ѯ����ԭ����
	 */
	@Override
	public List<MaterialBean> getSearchBean(List<TCComponentItemRevision> materialRevList) {
		List<MaterialBean> materialBeans = new ArrayList<>();
		for(TCComponentItemRevision revision : materialRevList){
			try {
				MaterialBean materialBean = new MaterialBean();
				materialBean.objectName = revision.getProperty("object_name");
				materialBean.code = revision.getProperty("u8_code");
				materialBean.price = revision.getProperty("u8_price");
				materialBean.suppplier = revision.getProperty("u8_supplierinfo");
				materialBean.u8Uom = revision.getProperty("u8_uom");
				materialBean.isbacteria = revision.getProperty("u8_isbacteria");
				materialBean.itemID = revision.getItem().getProperty("item_id");
				materialBeans.add(materialBean);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return materialBeans;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#getInitMaterialItemRevList(com.
	 * teamcenter.rac.kernel.TCComponentItemRevision) ��ȡ��ʼ�����䷽�е�BOM�е�ԭ���ϵİ汾����
	 */
	@Override
	public List<TCComponentItemRevision> getInitMaterialItemRevList(TCComponentItemRevision itemRevision) {
		List<TCComponentItemRevision> materialItemRevList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
		AIFComponentContext[] bomChilds = null;
		if (topBomLine == null)
			return materialItemRevList;// Ϊ��˵��û��BOM��ͼ
		try {
			bomChilds = topBomLine.getChildren();
			if (bomChilds == null || bomChilds.length == 0)
				return materialItemRevList;// Ϊ��˵��û��BOM��ͼ��û��ֵ
		} catch (TCException e) {
			Logger.log(Level.ALL, "��ȡ����BOMLine�쳣", e);
		}
		for (AIFComponentContext context : bomChilds) {
			TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
			TCComponentItemRevision bomLineChildRev = null;
			try {
				bomLineChildRev = bomLineChild.getItemRevision();
				materialItemRevList.add(bomLineChildRev);
			} catch (TCException e) {
				Logger.log(Level.ALL, "BOM��û�а汾", e);
			}
		}
		return materialItemRevList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#getSearchItemRev(java.lang.String,
	 * java.lang.String, java.lang.String) ��ȡ��������ԭ���ϵİ汾
	 */
	@Override
	public List<TCComponentItemRevision> getSearchItemRev(String materialName, String materialCode, String materialSupplier) {
		List<TCComponentItemRevision> searchItemRevList = new ArrayList<>();
		String keys = "";
		String valuse  = "";
		// ��ѯ������
		if (!"".equals(materialName)){
			if("".equals(keys)){
				keys+=Const.Material_Query_Condition.NAME;
				valuse+=materialName;
			}else{
				keys+=","+Const.Material_Query_Condition.NAME;
				valuse+=","+materialName;
			}
		}
		if (!"".equals(materialCode)){
			if("".equals(keys)){
				keys+=Const.Material_Query_Condition.CODE;
				valuse+=materialCode;
			}else{
				keys+=","+Const.Material_Query_Condition.CODE;
				valuse+=","+materialCode;
			}
		}
		
		TCComponentQuery query = null;
		
		String groupName = UserInfoSingleFactory.getInstance().getTCSession().getGroup().toString();
		if(groupName.contains("����")){//���̵Ĳ�ѯ��
			query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_YG.getValue());
		}else if(groupName.contains("Һ��")){//Һ�̵Ĳ�ѯ��
			query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_LM.getValue());
		}else if(groupName.contains("����")){//�����Ĳ�ѯ��
			query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_CD.getValue());
		}else{
			query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_CD.getValue());
		}
		
		if(query == null){
			return searchItemRevList;
		}
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, keys.split(","),
				valuse.split(","));
		List<MaterialBean> materialBeans = new ArrayList<>();
		TCComponentItemRevision itemRevision = null;
		for (TCComponent component : searchResult) {
			if (component instanceof TCComponentItem)
				continue;
			itemRevision = (TCComponentItemRevision) component;
			try {
				if(itemRevision.getItem()==null){
					continue;
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			searchItemRevList.add(itemRevision);
		}
		return searchItemRevList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#createFormulatorBOM(com.teamcenter.
	 * rac.kernel.TCComponentItemRevision, java.util.List) ����BOM��ͼ
	 * �����ṹ֮������û���Table�е��޸ĸ��¶�Ӧ��
	 */
	@Override
	public void createFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList) {
		try {
			itemRevision.setProperty("object_desc", "PF");
			
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			if (topBomLine == null) {// ���Ϊnull��ҪΪ�汾������ͼ
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
			}

			// ��ȡBOM�ṹ�е�ԭ����
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
				bomMaterialBomLineList.add(bomLineChild);
			}

			// �����䷽������ݶ�BOM�ṹ���е���
			for (TCComponentBOMLine materialBomLine : bomMaterialBomLineList) {// �Ƴ�ԭ��
				materialBomLine.cut();
			}
			for (TCComponentItemRevision materialItemRev : formulatorItemRevList) {// �����
				topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();

			// ���¸��µ�BOM�ṹ�������
			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			children = topBomLine.getChildren();
			for (int i = 0; i < children.length; i++) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) children[i].getComponent();
				AnnotationFactory.setObjectInTC(formulatorTableList.get(i), bomLineChild);
				// bomLineChild.setProperty("U8_inventory",
				// formulatorTableList.get(i).U8_inventory);
			}
			bomWindow = topBomLine.getCachedWindow();
			topBomLine.refresh();
			bomWindow.save();
			bomWindow.close();
			
			
			//���䷽�İ汾�µ�BOM�汾�µ�����������дһ�����ԣ����棩
			String type = itemRevision.getType();
			if("U8_FormulaRevision".equals(type)){
				TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(itemRevision);
				bomRevByItemRev.setProperty("object_desc", Const.BomViewType.FORMULATOR);//������䷽
			}else if("U8_MaterialRevision".equals(type)){
				TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(itemRevision);
				bomRevByItemRev.setProperty("object_desc", Const.BomViewType.MATERIAL);//�����ԭ��
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#write2Excel(com.teamcenter.rac.
	 * kernel.TCComponentBOMLine) ������д��Excel
	 */
	@Override
	public void write2Excel(List<TCComponentBOMLine> materialBomList,List<IndexItemBean> indexBeanList) {
List<FormulatorExcelMaterialBean> excelMaterialBeanList = getExcelMaterialBeanList(materialBomList);
		
		// ======д����
		File outFile = new File(Const.Formulator.EXCEL_PATH);
		if (outFile.exists()) {
			outFile.delete();
		}
		// д��Excel
		Workbook wb = null;
		wb = new XSSFWorkbook();

		// ���ñ߿�
		CellStyle cellBorderStyle = wb.createCellStyle();
		cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

		//=======================Ӫ������Ϣ
		Sheet sheet = wb.createSheet(Const.Formulator.EXCEL_SHEET1_NAME);//Ӫ���ɷֱ�
		sheet.setColumnWidth(0,6000);sheet.setColumnWidth(3,8000);
		// ��ʼ�����⣺

		Row row = sheet.createRow(0);
		Cell cell1 = row.createCell(0);
		Cell cell2 = row.createCell(1);
		Cell cell3 = row.createCell(2);
		Cell cell4 = row.createCell(3);
		Cell cell5 = row.createCell(4);
		Cell cell6 = row.createCell(5);
		Cell cell7 = row.createCell(6);

		CellStyle cellColorStyle = wb.createCellStyle();
		cellColorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		cellColorStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());

		cell1.setCellStyle(cellColorStyle);
		cell2.setCellStyle(cellColorStyle);
		cell3.setCellStyle(cellColorStyle);
		cell4.setCellStyle(cellColorStyle);
		cell5.setCellStyle(cellColorStyle);
		cell6.setCellStyle(cellColorStyle);
		cell7.setCellStyle(cellColorStyle);
		cell1.setCellValue("Ӫ����");
		cell2.setCellValue("����");
		cell3.setCellValue("����");
		cell4.setCellValue("��׼ֵ");
		cell5.setCellValue("��⺬������");
		cell6.setCellValue("��⺬������");
		cell7.setCellValue("����׼ֵ");

		for (int i = 0; i < indexBeanList.size(); i++) {
			IndexItemBean indexBean = indexBeanList.get(i);
			row = sheet.createRow(i+1);
			cell1 = row.createCell(0);cell2 = row.createCell(1);
			cell3 = row.createCell(2);cell4 = row.createCell(3);
			cell5 = row.createCell(4);cell6 = row.createCell(5);
			cell7 = row.createCell(6);
			
			cell1.setCellValue(indexBean.objectName);cell2.setCellValue(indexBean.up);
			cell3.setCellValue(indexBean.down);cell4.setCellValue("");
			cell5.setCellValue("");cell6.setCellValue("");
			cell7.setCellValue("");
		}

		//===============================�䷽�嵥
		sheet = wb.createSheet(Const.Formulator.EXCEL_SHEET2_NAME);//�䷽�嵥
		sheet.setColumnWidth(0,6000);sheet.setColumnWidth(3,8000);
		// ��ʼ�����⣺

		 row = sheet.createRow(0);
		 cell1 = row.createCell(0);
		 cell2 = row.createCell(1);
		 cell3 = row.createCell(2);
		 cell4 = row.createCell(3);
		 cell5 = row.createCell(4);
		 cell6 = row.createCell(5);
		 cell7 = row.createCell(6);

		cell1.setCellStyle(cellColorStyle);
		cell2.setCellStyle(cellColorStyle);
		cell3.setCellStyle(cellColorStyle);
		cell4.setCellStyle(cellColorStyle);
		cell5.setCellStyle(cellColorStyle);
		cell6.setCellStyle(cellColorStyle);
		cell7.setCellStyle(cellColorStyle);
		cell1.setCellValue("ԭ������");
		cell2.setCellValue("�ڿ�����");
		cell3.setCellValue("�ڿ�����");
		cell4.setCellValue("�ڿر�׼ֵ");
		cell5.setCellValue("��⺬������");
		cell6.setCellValue("��⺬������");
		cell7.setCellValue("����׼ֵ");
		
		int rowIdex = 1;
		for (int i = 0; i < excelMaterialBeanList.size(); i++) {
			FormulatorExcelMaterialBean excelMaterialBean = excelMaterialBeanList.get(i);
			MaterialBean materialBean = excelMaterialBean.materialBean;
			List<IndexItemBean> indexItemBeanList = excelMaterialBean.indexItemBeanList;
			row = sheet.createRow(rowIdex++);
			cell1 = row.createCell(0);cell2 = row.createCell(1);
			cell3 = row.createCell(2);cell4 = row.createCell(3);
			cell5 = row.createCell(4);cell6 = row.createCell(5);
			cell7 = row.createCell(6);
			
			cell1.setCellStyle(cellBorderStyle);
			
			cell1.setCellValue(materialBean.objectName);cell2.setCellValue(materialBean.up);
			cell3.setCellValue(materialBean.down);cell4.setCellValue("");
			cell5.setCellValue("");cell6.setCellValue("");
			cell7.setCellValue("");
			
			for(IndexItemBean indexItemBean : indexItemBeanList){
				row = sheet.createRow(rowIdex++);
				cell1 = row.createCell(0);cell2 = row.createCell(1);
				cell3 = row.createCell(2);cell4 = row.createCell(3);
				cell5 = row.createCell(4);cell6 = row.createCell(5);
				cell7 = row.createCell(6);
				
				cell1.setCellValue(indexItemBean.objectName);cell2.setCellValue(indexItemBean.up);
				cell3.setCellValue(indexItemBean.down);cell4.setCellValue("");
				cell5.setCellValue("");cell6.setCellValue("");
				cell7.setCellValue("");
			}
		}
		
		FileOutputStream out;
		try {
			out = new FileOutputStream(outFile);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		
		
		try {
			Runtime.getRuntime().exec("cmd /c start "+Const.FormulatorModify.EXCEL_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc) ��ȡ����ԭ���ϵ�BOM���� û�кϲ�
	 */
	@Override
	public List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine) {
		// =================��ȡ���з���ֱ�ӹ���ָ�����BOM
		List<TCComponentBOMLine> materialBomList = new ArrayList<>();
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		// �Ƚ���һ�������
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
					bomQueue.offer(bomLineTemp);
				}
			}
		} catch (TCException e) {
		}
		// ����
		while (!bomQueue.isEmpty()) {// ���в�Ϊ�վͽ��ű���
			TCComponentBOMLine queueBom = bomQueue.peek();// �鿴��ͷ��Ԫ��
			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// �ж��Ƿ����
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// ����
						canBeUsed = true;
						break;
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (canBeUsed) {// ����
				queueBom = bomQueue.poll();// ������
				materialBomList.add(queueBom);
			} else {
				queueBom = bomQueue.poll();// ������ֱ�ӳ��� Ȼ�󽫺����к�������Ӽ����͵�BOM���
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// ����
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}

		// ======================��ȡ���е���Ӽ���Ӧ��Bean
//		List<MaterialBean> materialBeanList = new ArrayList<>();
//		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
//		for (TCComponentBOMLine bomLine : materialBomList) {
//			try {
//				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
//				// ȥ�ظ�
//				if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
//					int index = nameList.indexOf(bean.objectName);
//					MaterialBean exitBean = materialBeanList.get(index);
//					Double exitInvnetory = 0.0d;
//					Double inventory = 0.0d;
//					try {
//						exitInvnetory = Utils.convertStr2Double(exitBean.U8_inventory);
//					} catch (Exception e) {
//						exitInvnetory = 0.0d;
//					}
//					try {
//						inventory = Utils.convertStr2Double(bean.U8_inventory);
//					} catch (Exception e) {
//						inventory = 0.0d;
//					}
//					exitBean.U8_inventory = (exitInvnetory + inventory) + "";
//				} else {
//					materialBeanList.add(bean);
//					nameList.add(bean.objectName);
//				}
//
//			} catch (InstantiationException | IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		}
		return materialBomList;
	}

	/*
	 * (non-Javadoc) ��ȡָ���Bean����
	 * ��ǰ��ʹ����һ����ʱ�Ļ����䷽����
	 * ����ʹ�õ��䷽����е��䷽table�е��䷽�Ľṹ��˵
	 */
	@Override
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine) {

		// ===================��ȡ���еĿ���ʹ�õ�indexItem���͵�BOM
		List<TCComponentBOMLine> waitIndexBomList = new ArrayList<>();
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		// �Ƚ���һ�������
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
					bomQueue.offer(bomLineTemp);
				}
			}
		} catch (TCException e) {
		}
		
//		try {
//			AIFComponentContext[] children = topBomLine.getChildren();
//			for (AIFComponentContext context : children) {
//				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
//				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
//					bomQueue.offer(bomLineTemp);
//				}
//			}
//		} catch (TCException e) {
//		}

		// ����
		while (!bomQueue.isEmpty()) {
			TCComponentBOMLine queueBom = bomQueue.peek();// �鿴��ͷ��Ԫ��
			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// �ж��Ƿ����
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// ����
						canBeUsed = true;
						break;
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (canBeUsed) {// ����
				queueBom = bomQueue.poll();// ������
				waitIndexBomList.add(queueBom);
			} else {
				queueBom = bomQueue.poll();// ������ֱ�ӳ��� Ȼ�󽫺��ӵ�ԭ�����͵����
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// ����
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

		}
		// ��ʼ��allIndexBomList
		for (TCComponentBOMLine bomLine : waitIndexBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				AIFComponentContext[] children = bomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {
						allIndexBomList.add(bomLineTemp);
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (TCComponentBOMLine bomLine : allIndexBomList) {
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				// ȥ�ظ�
				if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
					int index = nameList.indexOf(bean.objectName);
					IndexItemBean exitBean = indexBeanList.get(index);
					Double exitUp = Utils.convertStr2Double(exitBean.up);
					Double up = Utils.convertStr2Double(bean.up);
					Double exitDown = Utils.convertStr2Double(exitBean.down);
					Double down = Utils.convertStr2Double(bean.down);
					Double exitQuantity = Utils.convertStr2Double(exitBean.bl_quantity);
					Double quantity = Utils.convertStr2Double(bean.bl_quantity);
					if (exitBean.isFirst) {// ��ǹ�֮���ֱ�����
						exitUp += up * quantity / 100.0;
						exitDown += down * quantity / 100.0;
						exitBean.up = exitUp + "";
						exitBean.down = exitDown + "";
					} else {
						exitUp = (exitUp * exitQuantity / 100.0) + (up * quantity / 100.0);
						exitDown = (exitDown * exitQuantity / 100.0) + (down * quantity / 100.0);
						exitBean.up = exitUp + "";
						exitBean.down = exitDown + "";
						exitBean.isFirst = true;
					}

				} else {
					indexBeanList.add(bean);
					nameList.add(bean.objectName);
				}

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return indexBeanList;
	}

	/* (non-Javadoc)
	 * ����û��ȥ�ظ���BOM����ȡÿ��Material��Ӧ�����ݽṹ���Һϲ��ظ�����Ŀ
	 */
	private List<FormulatorExcelMaterialBean> getExcelMaterialBeanList(List<TCComponentBOMLine> materialBomList) {
		List<FormulatorExcelMaterialBean> formulatorExcelMaterialBeansList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (TCComponentBOMLine bomLine : materialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				List<IndexItemBean> indexItemBeansList = new ArrayList<>();
				//��ȡԭ���µ�ָ��
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if(bomLineTemp.getItem().getType().equals("U8_IndexItem")){//ָ��
						IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
						indexItemBeansList.add(indexItemBean);
					}
				}
				//һ��ԭ�ϵ����ݽṹ
				FormulatorExcelMaterialBean formulatorExcelMaterialBean = new FormulatorExcelMaterialBean();
				// ȥ�ظ�
				if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
					int index = nameList.indexOf(bean.objectName);
					MaterialBean exitBean = formulatorExcelMaterialBeansList.get(index).materialBean;
					Double exitInvnetory = 0.0d;
					Double inventory = 0.0d;
					try {
						exitInvnetory = Utils.convertStr2Double(exitBean.U8_inventory);
					} catch (Exception e) {
						exitInvnetory = 0.0d;
					}
					try {
						inventory = Utils.convertStr2Double(bean.U8_inventory);
					} catch (Exception e) {
						inventory = 0.0d;
					}
					exitBean.U8_inventory = (exitInvnetory + inventory) + "";
					
					//�������IndexItem
					List<IndexItemBean> exitIndexItemBeanList = formulatorExcelMaterialBeansList.get(index).indexItemBeanList;
					for(IndexItemBean exitIndexItemBean : exitIndexItemBeanList){//�µ�
						for(IndexItemBean indexItemBean : indexItemBeansList){//�ظ���
							if(exitIndexItemBean.isFirst&&exitIndexItemBean.objectName.equals(indexItemBean.objectName)){//�ǵ�һ��ȥ�ظ�
								Double exitUp = Utils.convertStr2Double(exitIndexItemBean.up) * Utils.convertStr2Double(exitIndexItemBean.bl_quantity)/100.0;
								Double exitDown = Utils.convertStr2Double(exitIndexItemBean.down) * Utils.convertStr2Double(exitIndexItemBean.bl_quantity)/100.0;
								Double up = Utils.convertStr2Double(indexItemBean.up) * Utils.convertStr2Double(indexItemBean.bl_quantity)/100.0;
								Double down = Utils.convertStr2Double(indexItemBean.down) * Utils.convertStr2Double(indexItemBean.bl_quantity)/100.0;
								
								exitIndexItemBean.up = exitUp + up +"";
								exitIndexItemBean.down = exitDown + down +"";
							}else if(!exitIndexItemBean.isFirst&&exitIndexItemBean.objectName.equals(indexItemBean.objectName)){//���ǵ�һ�ξ�ֱ��
								Double up = Utils.convertStr2Double(indexItemBean.up) * Utils.convertStr2Double(indexItemBean.bl_quantity)/100.0;
								Double down = Utils.convertStr2Double(indexItemBean.down) * Utils.convertStr2Double(indexItemBean.bl_quantity)/100.0;
								exitIndexItemBean.up = Utils.convertStr2Double(exitIndexItemBean.up)+ up +"";
								exitIndexItemBean.down =  Utils.convertStr2Double(exitIndexItemBean.down)+down +"";
								exitIndexItemBean.isFirst = true;
							}
							
						}
					}
					
					
				} else {
					formulatorExcelMaterialBean.materialBean = bean;
					formulatorExcelMaterialBean.indexItemBeanList = indexItemBeansList;
					formulatorExcelMaterialBeansList.add(formulatorExcelMaterialBean);
					nameList.add(bean.objectName);
				}

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (TCException e1) {
				e1.printStackTrace();
			}
		}
		return formulatorExcelMaterialBeansList;
	}

	/**
	 * @param formulatorItemRevList
	 * @param formulatorTableList
	 * @return ��ȡ������Ϊ��ʱ��topBomLine
	 */
	@Override
	public TCComponentBOMLine getCacheTopBomLine(List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorBeanList) {
		String userId = "";
		String group = "";
		try {
			userId = UserInfoSingleFactory.getInstance().getUser().getUserId();
			TCComponentUser user = UserInfoSingleFactory.getInstance().getUser();
			group = UserInfoSingleFactory.getInstance().getTCSession().getCurrentGroup().toDisplayString();
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		TCComponentBOMLine topBomLine = null;
		try {
			TCComponentItem cacheItem = null;
			TCComponentItemRevision itemRevision = null;
			TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			TCComponent[] searchResult = QueryUtil.getSearchResult(query,new String[]{"����","����Ȩ�û�","����Ȩ��"}, new String[]{"TempFormula",userId,group});
			
			
			boolean itemExit = false;
			for(TCComponent component : searchResult){
				if(component.getType().equals("U8_Formula")&&
						component.getProperty("object_name").equals("TempFormula")&&
						component.getProperty("object_desc").equals(userId)
						){//��ǰ��¼�û���ID
					cacheItem = (TCComponentItem) component;
					itemExit = true;
					break;
				}
			}
			
			//����һ���䷽Item�����̶����ļ��л���
			TCComponentFolder tempFormulatorFolder = getTempFormulatorFolder();
			
			if(!itemExit){//itemû�д��� �ʹ���һ��
				//Ϊ�վʹ���
				cacheItem = ItemUtil.createtItem("U8_Formula", "TempFormula", userId);
				tempFormulatorFolder.add("contents", cacheItem);
			}else if(tempFormulatorFolder.getChildren().length==0){//���ڻ����䷽����û�����ļ�����
				tempFormulatorFolder.add("contents", cacheItem);
			}
			itemRevision = cacheItem.getLatestItemRevision();
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			if (topBomLine == null) {// ���Ϊnull��ҪΪ�汾������ͼ
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
			}

			// ��ȡBOM�ṹ�е�ԭ����
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
				bomMaterialBomLineList.add(bomLineChild);
			}
			// �����䷽������ݶ�BOM�ṹ���е���
			for (TCComponentBOMLine materialBomLine : bomMaterialBomLineList) {// �Ƴ�ԭ��
				materialBomLine.cut();
			}
			
			//==========================
			
			//���䷽�汾�����bom���
			
			//�ȴ���ԭ��
			for(int i=0;i<formulatorBeanList.size();i++){
				MaterialBean materialBean = formulatorBeanList.get(i);
				TCComponentItemRevision materialItemRevision = formulatorItemRevList.get(i);
				
				//��ӳɹ��� ������ͼչʾ��BOMLine��ֵ����ֵĶ����˵
				TCComponentBOMLine materialBomLine = topBomLine.add(materialItemRevision.getItem(), materialItemRevision, null, false);
				AnnotationFactory.setObjectInTC(materialBean, materialBomLine);//ԭ��BOM���и�ֵ
				
				computeMaterialBom(materialBomLine);//�������ԭ�������в�ε�ֵ
				
			}
			
			//����bom
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.refresh();
			bomWindow.save();
			topBomLine.refresh();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topBomLine;
	}
	

	
	/**
	 * ����һ�̶����ļ���
	 * @return
	 */
	public TCComponentFolder getTempFormulatorFolder() {
		String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
		TCComponentFolder tempFormulatorFolder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����","����Ȩ�û�"}, new String[]{"FormulatorTEMP","FormulatorTEMP","Folder",userName} );
		if(searchResult.length>0){
			tempFormulatorFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(tempFormulatorFolder==null){//û���ҵ� ����
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����Ȩ�û�"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//�ҵ�����Ҫ�ҵ��ļ���
					homeFolder = (TCComponentFolder) component;
				}
			}
			try {
				tempFormulatorFolder = ItemUtil.createFolder("FormulatorTEMP", "FormulatorTEMP");
				homeFolder.add("contents", tempFormulatorFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return tempFormulatorFolder;
	}
	
	
	
	/**
	 * ��������µ�ԭ��BOM �����ڲ��Ͷ���� һ��һ����� ���ռ��㵽ָ��
	 */
	public void computeMaterialBom(TCComponentBOMLine materialBomLine){
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		bomQueue.offer(materialBomLine);//��ʼ��
		
		while(!bomQueue.isEmpty()){//��Ϊ��
			try {
				TCComponentBOMLine bomLine = bomQueue.poll();
				Double invnetory = StringsUtil.convertStr2Double(bomLine.getProperty("U8_inventory"));
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomChild = (TCComponentBOMLine) context.getComponent();
					
					if(isMateiral(bomChild)){//�����ԭ�ϼ������Ͷ����
						Double childQuantity = StringsUtil.convertStr2Double(bomChild.getProperty("bl_quantity"));
						Double childInvnetory = childQuantity  * invnetory;
						bomChild.setProperty("U8_inventory", childInvnetory+"");
					}else{//�����ָ������������
						Double childUp = StringsUtil.convertStr2Double(bomChild.getProperty("U8_up"));
						Double childDown = StringsUtil.convertStr2Double(bomChild.getProperty("U8_down"));
						
						childUp = childUp * invnetory;
						childDown = childDown * invnetory;
						
						bomChild.setProperty("U8_up", childUp+"");
						bomChild.setProperty("U8_down", childDown+"");
					}
					
					
					//������к��Ӿ�Ҫ����������ȥ
					if(bomChild.hasChildren()){
						bomQueue.offer(bomChild);
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	public boolean isMateiral(TCComponentBOMLine bomLine){
		try {
			String type = bomLine.getItem().getType();
			if("U8_Material".equals(type)){//ԭ��
				return true;
			}else if("U8_IndexItem".equals(type)){//ָ��
				return false;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}

}
