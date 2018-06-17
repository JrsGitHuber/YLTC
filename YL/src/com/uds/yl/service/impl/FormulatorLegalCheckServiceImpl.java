package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;















import org.apache.poi.ss.formula.functions.Index;
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
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.IFrameProvider;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.ui.ColdFormulatorFrame.ComponenetBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponentBom;
import com.uds.yl.utils.StringsUtil;
import com.uds.yl.utils.YLCommonUtil;

public class FormulatorLegalCheckServiceImpl implements IFormulatorLegalCheckService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uds.yl.service.IFormulatorLegalCheckService#getTopBomLine(com.
	 * teamcenter.rac.kernel.TCComponentItemRevision) �����䷽�汾��ȡtopBomLine
	 */
	@Override
	public TCComponentBOMLine getTopBomLine(TCComponentItemRevision itemRev) {
		TCComponentBOMLine topBomLine = null;
		topBomLine = BomUtil.getTopBomLine(itemRev, Const.FormulatorCheck.BOMNAME);
		return topBomLine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorLegalCheckService#getWaitMaterialBomList(
	 * com.teamcenter.rac.kernel.TCComponentBOMLine) �����䷽��ȡԭ�ϵ�BOM
	 */
	@Override
	public List<TCComponentBOMLine> getWaitMaterialBomList(TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> waitMaterailBomList = new ArrayList<>();
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
			return waitMaterailBomList;
		}
		// ����
		while (!bomQueue.isEmpty()) {// ���в�Ϊ�վͽ��ű���
			TCComponentBOMLine parentBom = bomQueue.poll();
			waitMaterailBomList.add(parentBom);
			// ��ȡ��Ԫ�صĺ��ӽ���
			try {
				AIFComponentContext[] children = parentBom.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
						bomQueue.offer(bomLineTemp);
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return waitMaterailBomList;
	}

	/**	
	 * * @param waitMaterialBomList
	 * @return ���ݴ������Ӽ���Bom��ȡ��Ӧ��Bean ȥ�ظ���BomLine
	 */
	@Override
	public List<MaterialBean> getWaitMaterialBeanList(List<TCComponentBOMLine> waitMaterialBomList) {
		List<MaterialBean> waitMaterialBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������  ԭ��ȥ�ظ������� sampleName
		for (TCComponentBOMLine bomLine : waitMaterialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				// ȥ�ظ�
				if (nameList.contains(bean.sampleName)) {// �����Ҫ�ϲ���Bean ����������
					int index = nameList.indexOf(bean.sampleName);
					MaterialBean exitBean = waitMaterialBeanList.get(index);
					Double exitInvnetory = 0.0d;
					Double inventory = 0.0d;
					try {
						exitInvnetory = convertStr2Double(exitBean.U8_inventory);
					} catch (Exception e) {
						exitInvnetory = 0.0d;
					}
					try {
						inventory = convertStr2Double(bean.U8_inventory);
					} catch (Exception e) {
						inventory = 0.0d;
					}
					exitBean.U8_inventory = (exitInvnetory + inventory) + "";
				} else {
					waitMaterialBeanList.add(bean);
					nameList.add(bean.sampleName);
				}

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return waitMaterialBeanList;
	}

	/**
	 * @param topBomLine
	 * @return �����䷽��ȡָ���
	 */
	@Override
	public List<TCComponentBOMLine> getWaitIndexBomList(TCComponentBOMLine topBomLine) {
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
			return waitIndexBomList;
		}

		// ����
		while (!bomQueue.isEmpty()) {
			TCComponentBOMLine queueBom = bomQueue.peek();// �鿴��ͷ��Ԫ�� ����ԭ��

			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// �ж��Ƿ����
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// ԭ��������ָ��  ����
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
				queueBom = bomQueue.poll();// ������ֱ�ӳ��� Ȼ�󽫺��ӵ�����Ӽ����͵����
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// ԭ��������ԭ�ϵļӵ������еȴ�����
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

		}
		// ��ʼ��allIndexBomList
		for (TCComponentBOMLine canUsematerialBom : waitIndexBomList) {
			try {
				
				AIFComponentContext[] materialContexts = canUsematerialBom.getChildren();
				for (AIFComponentContext materialContext : materialContexts) {
					TCComponentBOMLine childBom = (TCComponentBOMLine) materialContext.getComponent();
					if (childBom.getItem().getType().equals("U8_IndexItem")) {
						allIndexBomList.add(childBom);
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return allIndexBomList;
	}

	/**
	 * @param waitIndexBomList
	 * @return ���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
	 */
	@Override
	public List<IndexItemBean> getWaitIndexBeanList(TCComponentBOMLine topBomLine) {
		
		
		List<TCComponentBOMLine> canUseterialBomList = new ArrayList<>();
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
		while (!bomQueue.isEmpty()) {
			TCComponentBOMLine queueBom = bomQueue.peek();// �鿴��ͷ��Ԫ�� ����ԭ��

			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// �ж��Ƿ����
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// ԭ��������ָ��  ����
						canBeUsed = true;
						break;
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (canBeUsed) {// ����
				queueBom = bomQueue.poll();// ������
				canUseterialBomList.add(queueBom);
			} else {
				queueBom = bomQueue.poll();// ������ֱ�ӳ��� Ȼ�󽫺��ӵ�����Ӽ����͵����
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// ԭ��������ԭ�ϵļӵ������еȴ�����
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

		}
		// ��ʼ��allIndexBean
		List<IndexItemBean> allIndexBeanList = new ArrayList<IndexItemBean>();
		for (TCComponentBOMLine canUsematerialBom : canUseterialBomList) {
			try {
				String materialInventory = canUsematerialBom.getProperty("U8_inventory");
				AIFComponentContext[] materialContexts = canUsematerialBom.getChildren();
				for (AIFComponentContext materialContext : materialContexts) {
					TCComponentBOMLine childBom = (TCComponentBOMLine) materialContext.getComponent();
					if (childBom.getItem().getType().equals("U8_IndexItem")) {
						IndexItemBean indexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, childBom);
						indexBean.parentMaterialInventory = materialInventory;
						indexBean.up = convertStr2Double(indexBean.up) * convertStr2Double(indexBean.parentMaterialInventory)+"";
						indexBean.down =  convertStr2Double(indexBean.down) * convertStr2Double(indexBean.parentMaterialInventory)+"";
						allIndexBeanList.add(indexBean);
					}
				}
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		
		

		List<IndexItemBean> finalAllIndexBeanList = new ArrayList<IndexItemBean>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������  ָ����ͨ�� itemId ������Ψһ�ı�ʶ
		for (IndexItemBean bean : allIndexBeanList) {
			// ȥ�ظ�
			if (nameList.contains(bean.itemID)) {// �����Ҫ�ϲ���Bean ����������
				int index = nameList.indexOf(bean.itemID);
				IndexItemBean exitBean = allIndexBeanList.get(index);
				
				Double exitInventory = convertStr2Double(exitBean.U8_inventory);
				Double enventory = convertStr2Double(bean.U8_inventory);
				
				Double exitUp = convertStr2Double(exitBean.up);
				Double up = convertStr2Double(bean.up);
				
				Double exitDown = convertStr2Double(exitBean.down);
				Double down = convertStr2Double(bean.up);
				
				if (exitBean.isFirst) {// ��ǹ�֮���ֱ�����
					exitInventory +=enventory;
					exitBean.U8_inventory= exitInventory+"";
					
					exitUp += up;
					exitBean.up = exitUp+"";
					
					exitDown += down;
					exitBean.down = exitDown+"";
					
					
				} else {
					exitInventory +=enventory;
					exitBean.U8_inventory= exitInventory+"";
					
					exitUp += up;
					exitBean.up = exitUp+"";
					
					exitDown += down;
					exitBean.down = exitDown+"";
					
					exitBean.isFirst = true;
				}

			} else {
				finalAllIndexBeanList.add(bean);
				nameList.add(bean.itemID);
			}
		}
		return finalAllIndexBeanList;
	}

	/**
	 * @param checkLawRevList
	 * @return ��ȡ�����е���Ӽ�Bean
	 * 
	 */
	@Override
	public List<MaterialBean> getCheckMaterialBeanList(List<TCComponentItemRevision> checkLawRevList) {
		List<MaterialBean> checkMaterialBeanList = new ArrayList<>();
		for (TCComponentItemRevision lawItemRevsion : checkLawRevList) {
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawItemRevsion, Const.FormulatorCheck.BOMNAME);
			try {
				String lawName = lawItemRevsion.getProperty("object_name");
				String id = lawItemRevsion.getProperty("item_id");
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_Material")) {
						MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class ,bomLineTemp);
						bean.lawName = id+" "+lawName;
						if(StringsUtil.isEmpty(bean.relatedSystemId)){//û�����ӵ�Ҫ�ŵ�������
							checkMaterialBeanList.add(bean);
						}
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}finally {
				TCComponentBOMWindow cachedWindow = topBomLine.getCachedWindow();
				try {
					cachedWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}

		return checkMaterialBeanList;
	}

	/**
	 * @param checkLawRevList
	 * @return ��ȡ�����е�ָ��Bean
	 */
	@Override
	public List<IndexItemBean> getCheckIndexBeanList(List<TCComponentItemRevision> checkLawRevList) {

		List<IndexItemBean> checkIndexBeanList = new ArrayList<>();
		for (TCComponentItemRevision itemRevision : checkLawRevList) {
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.FormulatorCheck.BOMNAME);
			try {
				String lawName = itemRevision.getProperty("object_name");
				String id = itemRevision.getProperty("item_id");
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {
						IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
						bean.lawName = id+" "+lawName;
						if(StringsUtil.isEmpty(bean.relatedSystemId)){//�����ӵ�Ҫ�ŵ�������
							checkIndexBeanList.add(bean);
						}
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}finally {
				TCComponentBOMWindow cachedWindow = topBomLine.getCachedWindow();
				try {
					cachedWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}

		return checkIndexBeanList;
	}

	

	
	

	
	
	/*
	 * (non-Javadoc) ��ȡ�����Ӽ����Bean
	 * 
	 * �䷽�е�ԭ�ϣ�sampleName���ͷ����е�ԭ�ϣ�object_name�����бȽ� 
	 */
	@Override
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,
			List<MaterialBean> checkMaterialBeanList) {
		List<FormulatorCheckedBean> materialCheckedBean = new ArrayList<>();
		for (MaterialBean waitBean : waitMaterialBeanList) {
			for (MaterialBean checkBean : checkMaterialBeanList) {
				if (waitBean.u8Uom.equals(checkBean.u8Uom) && checkBean.objectName.equals(waitBean.sampleName)) {// ��������Ƿ�һ��
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "��Ӽ�";
					checkedBean.formulatorValue = Utils.convertStr2Double(waitBean.U8_inventory)+"";
					checkedBean.lawName = checkBean.lawName;
					
					checkedBean.lawValue = YLCommonUtil.getStandardFormUPAndDown(checkBean.up,checkBean.down, checkBean.upSymbol, checkBean.downSymbol, checkBean.detectValue);
					if (true) {// �������
						Double waitValue = Utils.convertStr2Double(waitBean.U8_inventory);
						Double checkDown = Utils.convertStr2Double(checkBean.down);
						Double checkUp = Utils.convertStr2Double(checkBean.up);
						if(StringsUtil.isEmpty(checkBean.up)&&StringsUtil.isEmpty(checkBean.down)){//��������е�������ֵ
							checkedBean.excessiveDesc = "";
						}else if(StringsUtil.isEmpty(checkBean.up)&&!StringsUtil.isEmpty(checkBean.down)){//����Ϊ��
							if (waitValue < checkDown) {// ���޳���
								checkedBean.excessiveDesc = "��������";
							}
						}else if(StringsUtil.isEmpty(checkBean.down)&&!StringsUtil.isEmpty(checkBean.up)){//����Ϊ��
							if (waitValue > checkUp) {// ���޳���
								checkedBean.excessiveDesc = "��������";
							}
							
						}else{//�������ƶ���Ϊ��
							if (waitValue < checkDown) {// ���޳���
								checkedBean.excessiveDesc = "��������";
							}
							if (waitValue > checkUp) {// ���޳���
								checkedBean.excessiveDesc = "��������";
							}
						}
						
					}
					checkedBean.wranings = checkBean.warning;
					materialCheckedBean.add(checkedBean);
				}
			}
		}
		return materialCheckedBean;
	}

	/**
	 * @param area
	 * @return �����䷽��ȡ����
	 */
	@Override
	public List<TCComponentItemRevision> getCheckLawRevList(String id) {
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
		TCComponent[] result = QueryUtil.getSearchResult(query, new String[] { Const.FormulatorCheck.QUERY_ITME_ID },
				new String[] { id });

		List<TCComponentItemRevision> lawItemRevisionList = new ArrayList<>();
		if (result == null) {
			return lawItemRevisionList;
		}
		for (TCComponent component : result) {
			if(component instanceof TCComponentItem){
				TCComponentItemRevision itemRevision = null;
				try {
					itemRevision = ((TCComponentItem) component).getLatestItemRevision();
				} catch (TCException e) {
					e.printStackTrace();
				}
				lawItemRevisionList.add(itemRevision);
			}
		}
		return lawItemRevisionList;
	}

	/**
	 * @param waitIndexBeanList
	 * @param checkIndexBeanList
	 * @return �������ָ����
	 * 
	 * �䷽�е�ָ�꣨itemID���ͷ����е�ָ�꣨itemID���Ա�
	 */
	@Override
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,
			List<IndexItemBean> checkIndexBeanList) {
		List<FormulatorCheckedBean> indexCheckedBean = new ArrayList<>();
		for (IndexItemBean waitBean : waitIndexBeanList) {
			for (IndexItemBean checkBean : checkIndexBeanList) {
				if (checkBean.itemID.equals( waitBean.itemID)) {// ��������Ƿ�һ��
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "ָ��";
					checkedBean.formulatorValue =  YLCommonUtil.getStandardFormUPAndDown(waitBean.up,waitBean.down, "<=", ">=", waitBean.detectValue);
					checkedBean.lawName = checkBean.lawName;
					checkedBean.lawValue = YLCommonUtil.getStandardFormUPAndDown(checkBean.GB_UP,checkBean.GB_DOWN, checkBean.GB_UP_SYMBOL, checkBean.GB_DOWN_SYMBOL, checkBean.detectValue);;
					if (true) {// �������
						Double waitInventory = Utils.convertStr2Double(waitBean.U8_inventory);
						Double waitDown = Utils.convertStr2Double(waitBean.down);
						Double waitUp = Utils.convertStr2Double(waitBean.up);
						
						Double checkDown = Utils.convertStr2Double(checkBean.GB_DOWN);
						Double checkUp = Utils.convertStr2Double(checkBean.GB_UP);
						
						if(!StringsUtil.isEmpty(checkBean.detectValue)){//��������е�������ֵ
							checkedBean.excessiveDesc = "";
						}else if(StringsUtil.isEmpty(checkBean.GB_UP)&&!StringsUtil.isEmpty(checkBean.GB_DOWN)){//����Ϊ��
							if (waitDown < checkDown) {// ���޳���
								checkedBean.excessiveDesc = "��������";
							}
						}else if(StringsUtil.isEmpty(checkBean.GB_DOWN)&&!StringsUtil.isEmpty(checkBean.GB_UP)){//����Ϊ��
							if (waitUp > checkUp) {// ���޳���
								checkedBean.excessiveDesc = "��������";
							}
							
						}else if(!StringsUtil.isEmpty(checkBean.GB_DOWN)&&!StringsUtil.isEmpty(checkBean.GB_UP)){//�������ƶ���Ϊ��
							if (waitDown < checkDown && waitUp < checkUp) {// ���޳���
								checkedBean.excessiveDesc = "���޳���";
							}else if (waitUp > checkUp && waitDown >checkDown) {// ���޳���
								checkedBean.excessiveDesc = "���޳���";
							}else if(waitUp < checkUp && waitDown >checkDown){
								checkedBean.excessiveDesc = "";
							}else if(waitUp > checkUp && waitDown <checkDown){
								checkedBean.excessiveDesc = "�����޶�����";
							}
						}else if(StringsUtil.isEmpty(waitBean.down)&&StringsUtil.isEmpty(waitBean.up)){//�����޶�Ϊ�� ˵���ǵ���ֵ ��Ͷ������
							if(waitInventory < checkDown || waitInventory > checkUp){//������
								checkedBean.excessiveDesc = "���������޷�Χ��";
							}
						}
						
					}
					checkedBean.wranings = checkBean.warning;
					indexCheckedBean.add(checkedBean);
				}
			}
		}
		return indexCheckedBean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorLegalCheckService#write2Excel(java.util.
	 * List) ������д��excel��
	 */
	@Override
	public void write2Excel(List<FormulatorCheckedBean> checkedBeanList) {
		File outFile = new File(Const.FormulatorCheck.FORMULATORCHEKC_EXCEL_PATH);
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

		Sheet sheet = wb.createSheet("sheet1");
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
		Cell cell8 = row.createCell(7);

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
		cell8.setCellStyle(cellColorStyle);
		cell1.setCellValue("��Ŀ");
		cell2.setCellValue("���");
		cell3.setCellValue("�䷽ֵ");
		cell4.setCellValue("��������");
		cell5.setCellValue("����ֵ");
		cell6.setCellValue("����˵��");
		cell7.setCellValue("��ʾ��");
		cell8.setCellValue("��ע");

		for (int i = 0; i < checkedBeanList.size(); i++) {
			FormulatorCheckedBean checkedBean = checkedBeanList.get(i);
			row = sheet.createRow(i+1);
			cell1 = row.createCell(0);cell2 = row.createCell(1);
			cell3 = row.createCell(2);cell4 = row.createCell(3);
			cell5 = row.createCell(4);cell6 = row.createCell(5);
			cell7 = row.createCell(6);cell8 = row.createCell(7);
			
			if("".equals(checkedBean.excessiveDesc)||checkedBean.excessiveDesc==null){//��ɫ
				CellStyle colorStyle = wb.createCellStyle();
				colorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				colorStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
				cell1.setCellStyle(colorStyle);
				cell2.setCellStyle(colorStyle);
				cell3.setCellStyle(colorStyle);
				cell4.setCellStyle(colorStyle);
				cell5.setCellStyle(colorStyle);
				cell6.setCellStyle(colorStyle);
				cell7.setCellStyle(colorStyle);
				cell8.setCellStyle(colorStyle);
			}else{//��ɫ
				CellStyle colorStyle = wb.createCellStyle();
				colorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				colorStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
				cell1.setCellStyle(colorStyle);
				cell2.setCellStyle(colorStyle);
				cell3.setCellStyle(colorStyle);
				cell4.setCellStyle(colorStyle);
				cell5.setCellStyle(colorStyle);
				cell6.setCellStyle(colorStyle);
				cell7.setCellStyle(colorStyle);
				cell8.setCellStyle(colorStyle);
			}
			
			cell1.setCellValue(checkedBean.name);cell2.setCellValue(checkedBean.category);
			cell3.setCellValue(checkedBean.formulatorValue);cell4.setCellValue(checkedBean.lawName);
			cell5.setCellValue(checkedBean.lawValue);cell6.setCellValue(checkedBean.excessiveDesc);
			cell7.setCellValue(checkedBean.wranings);cell8.setCellValue("");
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(new File(Const.FormulatorCheck.FORMULATORCHEKC_EXCEL_PATH));
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
		}
		
		
		try {
			Runtime.getRuntime().exec("cmd /c start "+Const.FormulatorCheck.FORMULATORCHEKC_EXCEL_PATH);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Double convertStr2Double(String str) {
		Double d = 0.0d;
		if (str.equals("") || str == null) {
			d = 0.0d;
		} else {
			try {
				d = Double.valueOf(str);
			} catch (NumberFormatException e) {
				d = 0.0d;
			}

		}
		return d;
	}

	/* (non-Javadoc)
	 * ����id��ѯ���ķ��������
	 */
	@Override
	public List<String> getCheckLawNameList(List<TCComponentItemRevision> lawRevList) {
		List<String> nameList = new ArrayList<>();
		for(TCComponentItemRevision itemRevision : lawRevList){
			String name  ="";
			try {
//				name = itemRevision.getProperty("object_name");
				name = itemRevision.getProperty("object_desc");//��Ϊ��ȷ�ķ��������Ǵ���������
				nameList.add(name);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return nameList;
	}

	/* (non-Javadoc)
	 * ��ѯ�̶��ļ��ԭ�ϵķ���
	 */
	@Override
	public List<TCComponentItemRevision> getCheckMaterialLawRev() {
		List<TCComponentItemRevision> lawRevList = new ArrayList<>();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"ID"}, new String[]{""});
		for(TCComponent component:searchResult){
			try {
				TCComponentItemRevision revision = ((TCComponentItem)component).getLatestItemRevision();
				lawRevList.add(revision);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return lawRevList;
	}

	
	
	/* (non-Javadoc)
	 * ��ȡ����ȥ�ظ���������еķ���
	 */
	@Override
	public List<TCComponentItemRevision> getRelatedIDLaws(
			TCComponentItemRevision lawRev) {
		
		List<TCComponentItemRevision> revList = new ArrayList<TCComponentItemRevision>();
		Queue<TCComponentItemRevision> queue = new LinkedList<TCComponentItemRevision>();
		
		try {
			//��ʼ������
			queue.offer(lawRev);//��ֱ�ӹ����ķ�����ӵ�������
			
			//�ݹ��������
			while(!queue.isEmpty()){
				TCComponentItemRevision lawRevsion = queue.poll();
				revList.add(lawRevsion);//���еķ��涼��������list��
				
				
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawRevsion, "��ͼ");
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(lawRevsion);
				}
				AIFComponentContext[] bomChilds = topBomLine.getChildren();
				for(int i=0;i<bomChilds.length;i++){
					TCComponentBOMLine bomChild = (TCComponentBOMLine) bomChilds[i].getComponent();
					
					String indicatorRequire = bomChild.getProperty("U8_indexrequirment");
					String relatedSystemId = bomChild.getProperty("U8_AssociationID");
					if(StringsUtil.isEmpty(relatedSystemId)){//����
						continue;
					}
					
					
					TCComponentItemRevision linkedLawRevision = getLinkedLaw(indicatorRequire, relatedSystemId);
					if(linkedLawRevision == null){
						continue;
					}
					queue.offer(linkedLawRevision);
					
				}
				
			}
			
			
			//ȥ�ظ�
			HashMap<String, TCComponentItemRevision> revisionMap = new HashMap<String, TCComponentItemRevision>();
			for(TCComponentItemRevision lawRevision : revList){
				String id = lawRevision.getProperty("current_id");
				if(revisionMap.containsKey(id)){//id��Ψһ��ʶ������ֶ�
					continue;
				}
				revisionMap.put(id, lawRevision);
			}
			
			
			//����װ������
			revList.clear();
			for(TCComponentItemRevision lawRevsion : revisionMap.values()){
				revList.add(lawRevsion);
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return revList;
		
	}

	
	/**
	 * ���ݹ�����ϵid
	 * 
	 * ��ȡ������GB 2199 ����
	 * 
	 * ��Ϊ���� id ���в�ѯ
	 * @param indicatorRequire
	 * @param relatedSystemId
	 * @return
	 */
	private TCComponentItemRevision getLinkedLaw(String indicatorRequire,String relatedSystemId) {
		//�������ӵķ����ID�ҵ�����
		String[] splitsLawIds = indicatorRequire.split("#");
		String relatedIds = relatedSystemId;
		
		for(String lawId : splitsLawIds){
			if(lawId.startsWith("GB")&&(lawId.contains("2760")||lawId.contains("14880"))){//˵������  �ǲ�Ʒ��׼  ����2760����14880
				//�����ҵ�����
				TCComponentItemRevision itemRevision = null;
				String lawID = relatedIds+" "+lawId;
				TCComponentItemRevision lawRevision = getLawRevisionById(lawID);
				
				if(lawRevision == null){
					return null;
				}
				return lawRevision;
			}
		}
		
		return null;
	}
	
	
	/**
	 * @param area
	 * @return ���ݷ���ID��ѯ����
	 */
	public TCComponentItemRevision getLawRevisionById(String lawId) {
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
		TCComponent[] result = QueryUtil.getSearchResult(query, new String[] { Const.FormulatorCheck.QUERY_ITME_ID },
				new String[] { lawId });

		if (result == null || result.length == 0 ) {
			return null;
		}
		
		TCComponentItemRevision itemRevision = null;
		try {
			itemRevision = ((TCComponentItem) result[0]).getLatestItemRevision();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return itemRevision;
	}
	
	
	/* (non-Javadoc)
	 * ����һ����ʱ���䷽����
	 */
	@Override
	public TCComponentBOMLine getCacheTopBomLine(
			List<TCComponentItemRevision> formulatorItemRevList,List<MaterialBean> formulatorBeanList) {
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
			}else {
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
			
			//�ȴ�����ֵ�bom��ͼ
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
