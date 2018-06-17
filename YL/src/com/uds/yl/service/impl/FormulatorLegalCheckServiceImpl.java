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
	 * teamcenter.rac.kernel.TCComponentItemRevision) 根据配方版本获取topBomLine
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
	 * com.teamcenter.rac.kernel.TCComponentBOMLine) 根据配方获取原料的BOM
	 */
	@Override
	public List<TCComponentBOMLine> getWaitMaterialBomList(TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> waitMaterailBomList = new ArrayList<>();
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
					bomQueue.offer(bomLineTemp);
				}
			}
		} catch (TCException e) {
			return waitMaterailBomList;
		}
		// 遍历
		while (!bomQueue.isEmpty()) {// 队列不为空就接着遍历
			TCComponentBOMLine parentBom = bomQueue.poll();
			waitMaterailBomList.add(parentBom);
			// 获取该元素的孩子进队
			try {
				AIFComponentContext[] children = parentBom.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
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
	 * @return 根据待检的添加剂的Bom获取对应的Bean 去重复的BomLine
	 */
	@Override
	public List<MaterialBean> getWaitMaterialBeanList(List<TCComponentBOMLine> waitMaterialBomList) {
		List<MaterialBean> waitMaterialBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组  原料去重复是依靠 sampleName
		for (TCComponentBOMLine bomLine : waitMaterialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				// 去重复
				if (nameList.contains(bean.sampleName)) {// 这个是要合并的Bean 不存入数组
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
	 * @return 根据配方获取指标的
	 */
	@Override
	public List<TCComponentBOMLine> getWaitIndexBomList(TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> waitIndexBomList = new ArrayList<>();
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
					bomQueue.offer(bomLineTemp);
				}
			}
		} catch (TCException e) {
			return waitIndexBomList;
		}

		// 遍历
		while (!bomQueue.isEmpty()) {
			TCComponentBOMLine queueBom = bomQueue.peek();// 查看对头的元素 都是原料

			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// 判断是否可用
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// 原料下面有指标  可用
						canBeUsed = true;
						break;
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (canBeUsed) {// 可用
				queueBom = bomQueue.poll();// 出队列
				waitIndexBomList.add(queueBom);
			} else {
				queueBom = bomQueue.poll();// 不可用直接出队 然后将孩子的是添加剂类型的入队
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// 原料下面有原料的加到队列中等待遍历
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

		}
		// 初始化allIndexBomList
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
	 * @return 根据对应的原料的BOM获取对应的指标的Bean
	 */
	@Override
	public List<IndexItemBean> getWaitIndexBeanList(TCComponentBOMLine topBomLine) {
		
		
		List<TCComponentBOMLine> canUseterialBomList = new ArrayList<>();
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
					bomQueue.offer(bomLineTemp);
				}
			}
		} catch (TCException e) {
			
		}

		// 遍历
		while (!bomQueue.isEmpty()) {
			TCComponentBOMLine queueBom = bomQueue.peek();// 查看对头的元素 都是原料

			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// 判断是否可用
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// 原料下面有指标  可用
						canBeUsed = true;
						break;
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (canBeUsed) {// 可用
				queueBom = bomQueue.poll();// 出队列
				canUseterialBomList.add(queueBom);
			} else {
				queueBom = bomQueue.poll();// 不可用直接出队 然后将孩子的是添加剂类型的入队
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// 原料下面有原料的加到队列中等待遍历
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

		}
		// 初始化allIndexBean
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
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组  指标是通过 itemId 来进行唯一的标识
		for (IndexItemBean bean : allIndexBeanList) {
			// 去重复
			if (nameList.contains(bean.itemID)) {// 这个是要合并的Bean 不存入数组
				int index = nameList.indexOf(bean.itemID);
				IndexItemBean exitBean = allIndexBeanList.get(index);
				
				Double exitInventory = convertStr2Double(exitBean.U8_inventory);
				Double enventory = convertStr2Double(bean.U8_inventory);
				
				Double exitUp = convertStr2Double(exitBean.up);
				Double up = convertStr2Double(bean.up);
				
				Double exitDown = convertStr2Double(exitBean.down);
				Double down = convertStr2Double(bean.up);
				
				if (exitBean.isFirst) {// 标记过之后就直接相加
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
	 * @return 获取法规中的添加剂Bean
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
						if(StringsUtil.isEmpty(bean.relatedSystemId)){//没有链接的要放到数组中
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
	 * @return 获取法规中的指标Bean
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
						if(StringsUtil.isEmpty(bean.relatedSystemId)){//无连接的要放到集合中
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
	 * (non-Javadoc) 获取检查添加剂后的Bean
	 * 
	 * 配方中的原料（sampleName）和法规中的原料（object_name）进行比较 
	 */
	@Override
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,
			List<MaterialBean> checkMaterialBeanList) {
		List<FormulatorCheckedBean> materialCheckedBean = new ArrayList<>();
		for (MaterialBean waitBean : waitMaterialBeanList) {
			for (MaterialBean checkBean : checkMaterialBeanList) {
				if (waitBean.u8Uom.equals(checkBean.u8Uom) && checkBean.objectName.equals(waitBean.sampleName)) {// 检查类型是否一致
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "添加剂";
					checkedBean.formulatorValue = Utils.convertStr2Double(waitBean.U8_inventory)+"";
					checkedBean.lawName = checkBean.lawName;
					
					checkedBean.lawValue = YLCommonUtil.getStandardFormUPAndDown(checkBean.up,checkBean.down, checkBean.upSymbol, checkBean.downSymbol, checkBean.detectValue);
					if (true) {// 超标计算
						Double waitValue = Utils.convertStr2Double(waitBean.U8_inventory);
						Double checkDown = Utils.convertStr2Double(checkBean.down);
						Double checkUp = Utils.convertStr2Double(checkBean.up);
						if(StringsUtil.isEmpty(checkBean.up)&&StringsUtil.isEmpty(checkBean.down)){//如果法规中的是描述值
							checkedBean.excessiveDesc = "";
						}else if(StringsUtil.isEmpty(checkBean.up)&&!StringsUtil.isEmpty(checkBean.down)){//上限为空
							if (waitValue < checkDown) {// 下限超标
								checkedBean.excessiveDesc = "低于下限";
							}
						}else if(StringsUtil.isEmpty(checkBean.down)&&!StringsUtil.isEmpty(checkBean.up)){//下限为空
							if (waitValue > checkUp) {// 上限超标
								checkedBean.excessiveDesc = "超过上限";
							}
							
						}else{//上下限制都不为空
							if (waitValue < checkDown) {// 下限超标
								checkedBean.excessiveDesc = "低于下限";
							}
							if (waitValue > checkUp) {// 上限超标
								checkedBean.excessiveDesc = "超过上限";
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
	 * @return 根据配方获取法规
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
	 * @return 检查过后的指标项
	 * 
	 * 配方中的指标（itemID）和法规中的指标（itemID）对比
	 */
	@Override
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,
			List<IndexItemBean> checkIndexBeanList) {
		List<FormulatorCheckedBean> indexCheckedBean = new ArrayList<>();
		for (IndexItemBean waitBean : waitIndexBeanList) {
			for (IndexItemBean checkBean : checkIndexBeanList) {
				if (checkBean.itemID.equals( waitBean.itemID)) {// 检查类型是否一致
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "指标";
					checkedBean.formulatorValue =  YLCommonUtil.getStandardFormUPAndDown(waitBean.up,waitBean.down, "<=", ">=", waitBean.detectValue);
					checkedBean.lawName = checkBean.lawName;
					checkedBean.lawValue = YLCommonUtil.getStandardFormUPAndDown(checkBean.GB_UP,checkBean.GB_DOWN, checkBean.GB_UP_SYMBOL, checkBean.GB_DOWN_SYMBOL, checkBean.detectValue);;
					if (true) {// 超标计算
						Double waitInventory = Utils.convertStr2Double(waitBean.U8_inventory);
						Double waitDown = Utils.convertStr2Double(waitBean.down);
						Double waitUp = Utils.convertStr2Double(waitBean.up);
						
						Double checkDown = Utils.convertStr2Double(checkBean.GB_DOWN);
						Double checkUp = Utils.convertStr2Double(checkBean.GB_UP);
						
						if(!StringsUtil.isEmpty(checkBean.detectValue)){//如果法规中的是描述值
							checkedBean.excessiveDesc = "";
						}else if(StringsUtil.isEmpty(checkBean.GB_UP)&&!StringsUtil.isEmpty(checkBean.GB_DOWN)){//上限为空
							if (waitDown < checkDown) {// 下限超标
								checkedBean.excessiveDesc = "低于下限";
							}
						}else if(StringsUtil.isEmpty(checkBean.GB_DOWN)&&!StringsUtil.isEmpty(checkBean.GB_UP)){//下限为空
							if (waitUp > checkUp) {// 上限超标
								checkedBean.excessiveDesc = "超过上限";
							}
							
						}else if(!StringsUtil.isEmpty(checkBean.GB_DOWN)&&!StringsUtil.isEmpty(checkBean.GB_UP)){//上下限制都不为空
							if (waitDown < checkDown && waitUp < checkUp) {// 下限超标
								checkedBean.excessiveDesc = "下限超标";
							}else if (waitUp > checkUp && waitDown >checkDown) {// 上限超标
								checkedBean.excessiveDesc = "上限超标";
							}else if(waitUp < checkUp && waitDown >checkDown){
								checkedBean.excessiveDesc = "";
							}else if(waitUp > checkUp && waitDown <checkDown){
								checkedBean.excessiveDesc = "上下限都超标";
							}
						}else if(StringsUtil.isEmpty(waitBean.down)&&StringsUtil.isEmpty(waitBean.up)){//上下限都为空 说明是单个值 在投料量中
							if(waitInventory < checkDown || waitInventory > checkUp){//不符合
								checkedBean.excessiveDesc = "不在上下限范围内";
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
	 * List) 将数据写到excel中
	 */
	@Override
	public void write2Excel(List<FormulatorCheckedBean> checkedBeanList) {
		File outFile = new File(Const.FormulatorCheck.FORMULATORCHEKC_EXCEL_PATH);
		if (outFile.exists()) {
			outFile.delete();
		}
		// 写入Excel
		Workbook wb = null;
		wb = new XSSFWorkbook();

		// 设置边框
		CellStyle cellBorderStyle = wb.createCellStyle();
		cellBorderStyle.setBorderTop(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderBottom(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderLeft(CellStyle.BORDER_THIN);
		cellBorderStyle.setBorderRight(CellStyle.BORDER_THIN);

		Sheet sheet = wb.createSheet("sheet1");
		sheet.setColumnWidth(0,6000);sheet.setColumnWidth(3,8000);
		// 初始化标题：

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
		cell1.setCellValue("项目");
		cell2.setCellValue("类别");
		cell3.setCellValue("配方值");
		cell4.setCellValue("法规名称");
		cell5.setCellValue("法规值");
		cell6.setCellValue("超标说明");
		cell7.setCellValue("警示语");
		cell8.setCellValue("备注");

		for (int i = 0; i < checkedBeanList.size(); i++) {
			FormulatorCheckedBean checkedBean = checkedBeanList.get(i);
			row = sheet.createRow(i+1);
			cell1 = row.createCell(0);cell2 = row.createCell(1);
			cell3 = row.createCell(2);cell4 = row.createCell(3);
			cell5 = row.createCell(4);cell6 = row.createCell(5);
			cell7 = row.createCell(6);cell8 = row.createCell(7);
			
			if("".equals(checkedBean.excessiveDesc)||checkedBean.excessiveDesc==null){//绿色
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
			}else{//红色
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
	 * 根据id查询到的法规的名称
	 */
	@Override
	public List<String> getCheckLawNameList(List<TCComponentItemRevision> lawRevList) {
		List<String> nameList = new ArrayList<>();
		for(TCComponentItemRevision itemRevision : lawRevList){
			String name  ="";
			try {
//				name = itemRevision.getProperty("object_name");
				name = itemRevision.getProperty("object_desc");//因为正确的法规名称是从描述中了
				nameList.add(name);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return nameList;
	}

	/* (non-Javadoc)
	 * 查询固定的检查原料的法规
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
	 * 获取的是去重复过后的所有的法规
	 */
	@Override
	public List<TCComponentItemRevision> getRelatedIDLaws(
			TCComponentItemRevision lawRev) {
		
		List<TCComponentItemRevision> revList = new ArrayList<TCComponentItemRevision>();
		Queue<TCComponentItemRevision> queue = new LinkedList<TCComponentItemRevision>();
		
		try {
			//初始化队列
			queue.offer(lawRev);//将直接关联的法规添加到集合中
			
			//递归遍历队列
			while(!queue.isEmpty()){
				TCComponentItemRevision lawRevsion = queue.poll();
				revList.add(lawRevsion);//所有的法规都会存在这个list中
				
				
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(lawRevsion, "视图");
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(lawRevsion);
				}
				AIFComponentContext[] bomChilds = topBomLine.getChildren();
				for(int i=0;i<bomChilds.length;i++){
					TCComponentBOMLine bomChild = (TCComponentBOMLine) bomChilds[i].getComponent();
					
					String indicatorRequire = bomChild.getProperty("U8_indexrequirment");
					String relatedSystemId = bomChild.getProperty("U8_AssociationID");
					if(StringsUtil.isEmpty(relatedSystemId)){//跳过
						continue;
					}
					
					
					TCComponentItemRevision linkedLawRevision = getLinkedLaw(indicatorRequire, relatedSystemId);
					if(linkedLawRevision == null){
						continue;
					}
					queue.offer(linkedLawRevision);
					
				}
				
			}
			
			
			//去重复
			HashMap<String, TCComponentItemRevision> revisionMap = new HashMap<String, TCComponentItemRevision>();
			for(TCComponentItemRevision lawRevision : revList){
				String id = lawRevision.getProperty("current_id");
				if(revisionMap.containsKey(id)){//id是唯一标识法规的字段
					continue;
				}
				revisionMap.put(id, lawRevision);
			}
			
			
			//重新装入数据
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
	 * 根据关联体系id
	 * 
	 * 抽取出来的GB 2199 名字
	 * 
	 * 作为法规 id 进行查询
	 * @param indicatorRequire
	 * @param relatedSystemId
	 * @return
	 */
	private TCComponentItemRevision getLinkedLaw(String indicatorRequire,String relatedSystemId) {
		//根据连接的法规的ID找到法规
		String[] splitsLawIds = indicatorRequire.split("#");
		String relatedIds = relatedSystemId;
		
		for(String lawId : splitsLawIds){
			if(lawId.startsWith("GB")&&(lawId.contains("2760")||lawId.contains("14880"))){//说明合适  是产品标准  来自2760或者14880
				//搜索找到法规
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
	 * @return 根据法规ID查询法规
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
	 * 创建一个临时的配方对象
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
			TCComponent[] searchResult = QueryUtil.getSearchResult(query,new String[]{"名称","所有权用户","所有权组"}, new String[]{"TempFormula",userId,group});
			
			
			boolean itemExit = false;
			for(TCComponent component : searchResult){
				if(component.getType().equals("U8_Formula")&&
						component.getProperty("object_name").equals("TempFormula")&&
						component.getProperty("object_desc").equals(userId)
						){//当前登录用户的ID
					cacheItem = (TCComponentItem) component;
					itemExit = true;
					break;
				}
			}
			
			//创建一个配方Item丢到固定的文件夹汇总
			TCComponentFolder tempFormulatorFolder = getTempFormulatorFolder();
			if(!itemExit){//item没有存在 就创建一个
				//为空就创建
				cacheItem = ItemUtil.createtItem("U8_Formula", "TempFormula", userId);
				tempFormulatorFolder.add("contents", cacheItem);
			}else {
			}
			itemRevision = cacheItem.getLatestItemRevision();
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			if (topBomLine == null) {// 如过为null需要为版本创建视图
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
			}

			// 获取BOM结构中的原材料
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
				bomMaterialBomLineList.add(bomLineChild);
			}
			// 根据配方表的数据对BOM结构进行调整
			for (TCComponentBOMLine materialBomLine : bomMaterialBomLineList) {// 移出原有
				materialBomLine.cut();
			}
			
			//==========================
			
			//将配方版本下面的bom清空
			
			//先创建组分的bom视图
			for(int i=0;i<formulatorBeanList.size();i++){
				MaterialBean materialBean = formulatorBeanList.get(i);
				TCComponentItemRevision materialItemRevision = formulatorItemRevList.get(i);
				
				//添加成功后 将在视图展示的BOMLine赋值给组分的对象的说
				TCComponentBOMLine materialBomLine = topBomLine.add(materialItemRevision.getItem(), materialItemRevision, null, false);
				AnnotationFactory.setObjectInTC(materialBean, materialBomLine);//原料BOM进行赋值
				
				computeMaterialBom(materialBomLine);//计算这个原料中所有层次的值
				
			}
			
			//保存bom
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
	 * 创建一固定的文件夹
	 * @return
	 */
	public TCComponentFolder getTempFormulatorFolder() {
		
		String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
		TCComponentFolder tempFormulatorFolder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","描述","类型","所有权用户"}, new String[]{"FormulatorTEMP","FormulatorTEMP","Folder",userName} );
		if(searchResult.length>0){
			tempFormulatorFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(tempFormulatorFolder==null){//没有找到 创建
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","类型","所有权用户"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//找到我们要找的文件夹
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
	 * 计算组分下的原料BOM 所有内层的投料量 一层一层计算 最终计算到指标
	 */
	public void computeMaterialBom(TCComponentBOMLine materialBomLine){
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		bomQueue.offer(materialBomLine);//初始化
		
		while(!bomQueue.isEmpty()){//不为空
			try {
				TCComponentBOMLine bomLine = bomQueue.poll();
				Double invnetory = StringsUtil.convertStr2Double(bomLine.getProperty("U8_inventory"));
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomChild = (TCComponentBOMLine) context.getComponent();
					
					if(isMateiral(bomChild)){//如果是原料计算的是投料量
						Double childQuantity = StringsUtil.convertStr2Double(bomChild.getProperty("bl_quantity"));
						Double childInvnetory = childQuantity  * invnetory;
						bomChild.setProperty("U8_inventory", childInvnetory+"");
					}else{//如果是指标计算的上下限
						Double childUp = StringsUtil.convertStr2Double(bomChild.getProperty("U8_up"));
						Double childDown = StringsUtil.convertStr2Double(bomChild.getProperty("U8_down"));
						
						childUp = childUp * invnetory;
						childDown = childDown * invnetory;
						
						bomChild.setProperty("U8_up", childUp+"");
						bomChild.setProperty("U8_down", childDown+"");
					}
					
					
					//如果还有孩子就要丢到队列中去
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
			if("U8_Material".equals(type)){//原料
				return true;
			}else if("U8_IndexItem".equals(type)){//指标
				return false;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	
	
}
