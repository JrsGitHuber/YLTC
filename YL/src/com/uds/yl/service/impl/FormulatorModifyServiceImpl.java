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
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorExcelMaterialBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.LogFactory;

public class FormulatorModifyServiceImpl implements IFormulatorModifyService {
	public Logger Logger = LogFactory.initLog("FormulatorServiceImpl", LogLevel.INFO.getValue());

	/**
	 * @param itemRevision
	 * @return 获取版本下已经有的BOM视图中的原材料
	 */
	@Override
	public List<MaterialBean> getInitBean(TCComponentItemRevision itemRevision) {
		List<MaterialBean> allMaterial = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
		AIFComponentContext[] bomChilds = null;
		if (topBomLine == null)
			return allMaterial;// 为空说明没有BOM视图
		try {
			bomChilds = topBomLine.getChildren();
			if (bomChilds == null || bomChilds.length == 0)
				return allMaterial;// 为空说明没有BOM视图下没有值
		} catch (TCException e) {
			Logger.log(Level.ALL, "获取孩子BOMLine异常", e);
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
	 * @return 获取查询到的原材料
	 */
	@Override
	public List<MaterialBean> getSearchBean(String materialName, String materialCode, String materialSupplier) {
		String keys = "";
		String valuse  = "";
		// 查询的输入
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
		
		String queryName = QueryClassConst.U8MATERIAL.getValue();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(queryName);
		if(query == null){
			return null;
		}
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, keys.split(","), valuse.split(","));
		List<MaterialBean> materialBeans = new ArrayList<>();
		for (TCComponent component : searchResult) {
			if (component instanceof TCComponentItem)
				continue;
			try {
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) component;
				if(itemRevision.getItem()==null){
					continue;
				}
				itemRevision = itemRevision.getItem().getLatestItemRevision();
				MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, itemRevision);
				materialBeans.add(materialBean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
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
	 * teamcenter.rac.kernel.TCComponentItemRevision) 获取初始化的配方中的BOM中的原材料的版本数组
	 */
	@Override
	public List<TCComponentItemRevision> getInitMaterialItemRevList(TCComponentItemRevision itemRevision) {
		List<TCComponentItemRevision> materialItemRevList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
		AIFComponentContext[] bomChilds = null;
		if (topBomLine == null)
			return materialItemRevList;// 为空说明没有BOM视图
		try {
			bomChilds = topBomLine.getChildren();
			if (bomChilds == null || bomChilds.length == 0)
				return materialItemRevList;// 为空说明没有BOM视图下没有值
		} catch (TCException e) {
			Logger.log(Level.ALL, "获取孩子BOMLine异常", e);
		}
		for (AIFComponentContext context : bomChilds) {
			TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
			TCComponentItemRevision bomLineChildRev = null;
			try {
				bomLineChildRev = bomLineChild.getItemRevision();
				materialItemRevList.add(bomLineChildRev);
			} catch (TCException e) {
				Logger.log(Level.ALL, "BOM行没有版本", e);
			}
		}
		return materialItemRevList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#getSearchItemRev(java.lang.String,
	 * java.lang.String, java.lang.String) 获取搜索到的原材料的版本
	 */
	@Override
	public List<TCComponentItemRevision> getSearchItemRev(String materialName, String materialCode, String materialSupplier) {
		List<TCComponentItemRevision> searchItemRevisionList = new ArrayList<>();
		String keys = "";
		String valuse  = "";
		// 查询的输入
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
		
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8MATERIAL.getValue());
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
			searchItemRevisionList.add(itemRevision);
		}

		return searchItemRevisionList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#createFormulatorBOM(com.teamcenter.
	 * rac.kernel.TCComponentItemRevision, java.util.List) 创建BOM视图
	 * 创建结构之后根据用户在Table中的修改更新对应的
	 */
	@Override
	public void createFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList) {
		try {
			itemRevision.setProperty("object_desc", "PF");
			
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
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
			for (TCComponentItemRevision materialItemRev : formulatorItemRevList) {// 添加新
				topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();

			// 更新更新的BOM结构后的数据
			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			children = topBomLine.getChildren();
			for (int i = 0; i < children.length; i++) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) children[i].getComponent();
				AnnotationFactory.setObjectInTC(formulatorTableList.get(i), bomLineChild);
				TCProperty tcProperty = bomLineChild.getTCProperty("U8_alternate");
				if(formulatorTableList.get(i).alternate.equals("互替")){
					tcProperty.setStringValue("alternate");
				}else if(formulatorTableList.get(i).alternate.equals("组合互替")){
					tcProperty.setStringValue("alternate_group");
				}
				
				
				// bomLineChild.setProperty("U8_inventory",
				// formulatorTableList.get(i).U8_inventory);
			}
			topBomLine.refresh();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.IFormulatorService#write2Excel(com.teamcenter.rac.
	 * kernel.TCComponentBOMLine) 将数据写到Excel
	 */
	@Override
	public void write2Excel(List<TCComponentBOMLine> materialBomList, List<IndexItemBean> indexBeanList) {
		List<FormulatorExcelMaterialBean> excelMaterialBeanList = getExcelMaterialBeanList(materialBomList);
		
		// ======写数据
		File outFile = new File(Const.FormulatorModify.EXCEL_PATH);
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

		//=======================营养素信息
		Sheet sheet = wb.createSheet(Const.FormulatorModify.EXCEL_SHEET1_NAME);//营养成分表
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
		cell1.setCellValue("营养素");
		cell2.setCellValue("内控上限");
		cell3.setCellValue("内控下限");
		cell4.setCellValue("内控标准值");
		cell5.setCellValue("检测含量上限");
		cell6.setCellValue("检测含量下限");
		cell7.setCellValue("检测标准值");

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

		//===============================配方清单
		sheet = wb.createSheet(Const.FormulatorModify.EXCEL_SHEET2_NAME);//配方清单
		sheet.setColumnWidth(0,6000);sheet.setColumnWidth(3,8000);
		// 初始化标题：

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
		cell1.setCellValue("原料名称");
		cell2.setCellValue("内控上限");
		cell3.setCellValue("内控下限");
		cell4.setCellValue("内控标准值");
		cell5.setCellValue("检测含量上限");
		cell6.setCellValue("检测含量下限");
		cell7.setCellValue("检测标准值");
		
		//另外建立一个sheet页面写配方清单
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
	 * (non-Javadoc) 获取原材料的Bean对象
	 */
	@Override
	public List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine) {
		// =================获取所有的添加剂BOM
		List<TCComponentBOMLine> materialBomList = new ArrayList<>();
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
		while (!bomQueue.isEmpty()) {// 队列不为空就接着遍历
			TCComponentBOMLine queueBom = bomQueue.peek();// 查看对头的元素
			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// 判断是否可用
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// 可用
						canBeUsed = true;
						break;
					}
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			if (canBeUsed) {// 可用
				queueBom = bomQueue.poll();// 出队列
				materialBomList.add(queueBom);
			} else {
				queueBom = bomQueue.poll();// 不可用直接出队 然后将孩子的是添加剂类型的入队
				try {
					AIFComponentContext[] children = queueBom.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
						if (bomTemp.getItem().getType().equals("U8_Material")) {// 可用
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}


		// ======================获取所有的添加剂对应的Bean
		// List<MaterialBean> materialBeanList = new ArrayList<>();
		// List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		// for (TCComponentBOMLine bomLine : materialBomList) {
		// try {
		// MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class,
		// bomLine);
		// if("互替".equals(bean.alternate)||"组合互替".equals(bean.alternate))
		// continue;//互替说明有值得不算
		// // 去重复
		// if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
		// int index = nameList.indexOf(bean.objectName);
		// MaterialBean exitBean = materialBeanList.get(index);
		// Double exitInvnetory = 0.0d;
		// Double inventory = 0.0d;
		// try {
		// exitInvnetory = Utils.convertStr2Double(exitBean.U8_inventory);
		// } catch (Exception e) {
		// exitInvnetory = 0.0d;
		// }
		// try {
		// inventory = Utils.convertStr2Double(bean.U8_inventory);
		// } catch (Exception e) {
		// inventory = 0.0d;
		// }
		// exitBean.U8_inventory = (exitInvnetory + inventory) + "";
		// } else {
		// materialBeanList.add(bean);
		// nameList.add(bean.objectName);
		// }
		//
		// } catch (InstantiationException | IllegalAccessException e) {
		// e.printStackTrace();
		// }
		// }
		return materialBomList;
	}

	/*
	 * (non-Javadoc) 获取指标的Bean对象
	 */
	@Override
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine) {
		// ===================获取所有的可以使用的indexItem类型的BOM
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
		}

		// 遍历
		while (!bomQueue.isEmpty()) {
			TCComponentBOMLine queueBom = bomQueue.peek();// 查看对头的元素

			boolean canBeUsed = false;
			try {
				AIFComponentContext[] children = queueBom.getChildren();
				for (AIFComponentContext context : children) {// 判断是否可用
					TCComponentBOMLine bomTemp = (TCComponentBOMLine) context.getComponent();
					if (bomTemp.getItem().getType().equals("U8_IndexItem")) {// 可用
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
						if (bomTemp.getItem().getType().equals("U8_Material")) {// 可用
							bomQueue.offer(bomTemp);
						}
					}
				} catch (TCException e) {
					e.printStackTrace();
				}
			}

		}
		// 初始化allIndexBomList
		for (TCComponentBOMLine bomLine : waitIndexBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				if ("互替".equals(bean.alternate) || "组合互替".equals(bean.alternate))
					continue;// 互替说明有值得不算
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
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (TCComponentBOMLine bomLine : allIndexBomList) {
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				// 去重复
				if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
					int index = nameList.indexOf(bean.objectName);
					IndexItemBean exitBean = indexBeanList.get(index);
					Double exitUp = Utils.convertStr2Double(exitBean.up);
					Double up = Utils.convertStr2Double(bean.up);
					Double exitDown = Utils.convertStr2Double(exitBean.down);
					Double down = Utils.convertStr2Double(bean.down);
					Double exitQuantity = Utils.convertStr2Double(exitBean.bl_quantity);
					Double quantity = Utils.convertStr2Double(bean.bl_quantity);
					if (exitBean.isFirst) {// 标记过之后就直接相加
						exitUp += up * quantity / 100.0;
						exitDown += down * quantity / 100.0;
						exitBean.up = exitUp + "";
						exitBean.down = exitDown + "";
					} else {
						exitUp = (exitUp * exitQuantity / 100.0) + (up * quantity / 100.0);
						exitDown = (exitDown * exitQuantity / 100.0) + (down * quantity / 100.0);
						exitBean.up = exitUp + "";
						exitBean.down = exitDown + "";
						exitBean.isFirst = false;
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

	/*
	 * (non-Javadoc) 根据没有去重复的BOM来获取每个Material对应的数据结构并且合并重复的项目
	 */
	private List<FormulatorExcelMaterialBean> getExcelMaterialBeanList(List<TCComponentBOMLine> materialBomList) {
		List<FormulatorExcelMaterialBean> formulatorExcelMaterialBeansList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (TCComponentBOMLine bomLine : materialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				if ("互替".equals(bean.alternate) || "组合互替".equals(bean.alternate))
					continue;// 互替说明有值得不算
				List<IndexItemBean> indexItemBeansList = new ArrayList<>();
				// 获取原料下的指标
				AIFComponentContext[] children = bomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {// 指标
						IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
						indexItemBeansList.add(indexItemBean);
					}
				}
				// 一个原料的数据结构
				FormulatorExcelMaterialBean formulatorExcelMaterialBean = new FormulatorExcelMaterialBean();
				// 去重复
				if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
					int index = nameList.indexOf(bean.objectName);
					MaterialBean exitBean = formulatorExcelMaterialBeansList.get(index).materialBean;
					Double exitInvnetory = 0.0d;
					Double inventory = 0.0d;
					exitInvnetory = Utils.convertStr2Double(exitBean.U8_inventory);
					inventory = Utils.convertStr2Double(bean.U8_inventory);
					exitBean.U8_inventory = (exitInvnetory + inventory) + "";

					// 求下面的IndexItem
					List<IndexItemBean> exitIndexItemBeanList = formulatorExcelMaterialBeansList
							.get(index).indexItemBeanList;
					for (IndexItemBean exitIndexItemBean : exitIndexItemBeanList) {// 新的
						for (IndexItemBean indexItemBean : indexItemBeansList) {// 重复的
							if (exitIndexItemBean.isFirst&&exitIndexItemBean.objectName.equals(indexItemBean.objectName)) {// 是第一次去重复
								Double exitUp = Utils.convertStr2Double(exitIndexItemBean.up)
										* Utils.convertStr2Double(exitIndexItemBean.bl_quantity) / 100.0;
								Double exitDown = Utils.convertStr2Double(exitIndexItemBean.down)
										* Utils.convertStr2Double(exitIndexItemBean.bl_quantity) / 100.0;
								Double up = Utils.convertStr2Double(indexItemBean.up)
										* Utils.convertStr2Double(indexItemBean.bl_quantity) / 100.0;
								Double down = Utils.convertStr2Double(indexItemBean.down)
										* Utils.convertStr2Double(indexItemBean.bl_quantity) / 100.0;

								exitIndexItemBean.up = exitUp + up + "";
								exitIndexItemBean.down = exitDown + down + "";
							} else if(!exitIndexItemBean.isFirst&&exitIndexItemBean.objectName.equals(indexItemBean.objectName)){// 不是第一次就直接
								Double up = Utils.convertStr2Double(indexItemBean.up)
										* Utils.convertStr2Double(indexItemBean.bl_quantity) / 100.0;
								Double down = Utils.convertStr2Double(indexItemBean.down)
										* Utils.convertStr2Double(indexItemBean.bl_quantity) / 100.0;
								exitIndexItemBean.up = Utils.convertStr2Double(exitIndexItemBean.up) + up + "";
								exitIndexItemBean.down = Utils.convertStr2Double(exitIndexItemBean.down) + down + "";
								exitIndexItemBean.isFirst = false;
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

	/* (non-Javadoc)
	 * 获取到的作为临时的topBomLine
	 */
	@Override
	public TCComponentBOMLine getCacheTopBomLine(List<TCComponentItemRevision> formulatorItemRevList,
			List<MaterialBean> formulatorTableList) {
		String userId = "";
		String group = "";
		try {
			userId = UserInfoSingleFactory.getInstance().getUser().getUserId();
			TCComponentUser user = UserInfoSingleFactory.getInstance().getUser();
			group = UserInfoSingleFactory.getInstance().getTCSession().getCurrentGroup().toDisplayString();
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		Logger.fine("准备获取临时配方topBomline");
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
					Logger.fine("临时配方查询得到");
					itemExit = true;
					break;
				}
			}
			
			if(!itemExit){//item没有存在 就创建一个
				//为空就创建
				cacheItem = ItemUtil.createtItem("U8_Formula", "TempFormula", userId);
				Logger.fine("临时配方查询不到，创建一个TempFormula");
			}else {
			}
			itemRevision = cacheItem.getLatestItemRevision();
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			if (topBomLine == null) {// 如过为null需要为版本创建视图
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
				Logger.fine("获取得到临时配方表的topBomLine");
			}

			// 获取BOM结构中的原材料
			Logger.fine("将临时配方的BOMLine内容删除");
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
				bomMaterialBomLineList.add(bomLineChild);
			}

			// 根据配方表的数据对BOM结构进行调整
			for (TCComponentBOMLine materialBomLine : bomMaterialBomLineList) {// 移出原有
				materialBomLine.cut();
			}
			Logger.fine("将在table中搭建的结构配置在临时配方BOM结构中");
			for (TCComponentItemRevision materialItemRev : formulatorItemRevList) {// 添加新
				topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();

			// 更新更新的BOM结构后的数据
			Logger.fine("将在table中搭建的结构数据写在临时配方BOM结构中");
			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			children = topBomLine.getChildren();
			for (int i = 0; i < children.length; i++) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) children[i].getComponent();
				AnnotationFactory.setObjectInTC(formulatorTableList.get(i), bomLineChild);
				// bomLineChild.setProperty("U8_inventory",
				// formulatorTableList.get(i).U8_inventory);
			}
			topBomLine.refresh();

			return topBomLine;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topBomLine;
	}

	/* (non-Javadoc)
	 * 删除临时的配方对象
	 */
	@Override
	public void deleteTempFormula() {
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query,new String[]{"名称"}, new String[]{"TempFormula"});
		if(searchResult.length!=0&&searchResult!=null){
			//为空就跳过
			try {
				searchResult[0].delete();
			} catch (TCException e) {
				e.printStackTrace();
			}
		}else{
			return;
		}
	}

}
