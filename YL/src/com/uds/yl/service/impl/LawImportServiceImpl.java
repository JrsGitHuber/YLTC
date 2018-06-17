package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.LawBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.ILawImportService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.DateUtil;
import com.uds.yl.utils.StringsUtil;

public class LawImportServiceImpl implements ILawImportService {
	private HashMap<String,Integer> sortedCloumNameList = null;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uds.yl.service.ILawImportService#getLawBeansFromExcel(java.lang.
	 * String) 根据文件路径进行LawBean的获取
	 */
	@Override
	public List<LawBean> getLawBeansFromExcel(String filePath) {
		// 写入Excel
		List<LawBean> lawBeanList = new ArrayList<>();
		FileInputStream fis = null;
		Workbook wb = null;
		sortedCloumNameList = new HashMap<>();
		try {
			fis = new FileInputStream(new File(filePath));
			wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheet(Const.Law.SHEET_NAME);
			
			Row row = sheet.getRow(0); 
			initCloumNameSort(row,sortedCloumNameList);
			
			for (int i = 1;; i++) {
				row = sheet.getRow(i);
				if (row == null||row.getCell(0)==null || StringsUtil.isEmpty(row.getCell(0).toString())) {// 获取不到了row的话就跳出了
					break;
				}

				// 每一行代表一个Bean对象  有异常抛出说明数据格式有问题了
				try {
					initBeanByRow(row, lawBeanList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return lawBeanList;
	}

	/**
	 * 将第一行的列名信息，
	 * @param row
	 * @param sortedCloumNameList
	 */
	private void initCloumNameSort(Row row, HashMap<String, Integer> sortedCloumNameList) {
		for(int i=0;;i++){
			Cell cell = row.getCell(i);
			if(cell==null){
				break;
			}
			String cloumName = cell.getStringCellValue();
			if(StringsUtil.isEmpty(cloumName)){//如果为空就跳出
				break;
			}else{
				sortedCloumNameList.put(cloumName,i);
			}
		}
	}

	/**
	 * 根据行来初始化一个Bean对象
	 * 
	 * @param row
	 */
	private void initBeanByRow(Row row, List<LawBean> lawBeanList) throws Exception {
		LawBean bean = new LawBean();
		int index = 0;
		Cell cell = null;
		index = sortedCloumNameList.get("体系名称");cell = row.getCell(index);
		if(cell==null){
			bean.productCategory="";
		}else{
			try {
				bean.productCategory = cell.getStringCellValue();// 体系名称
			} catch (Exception e) {
				bean.productCategory = cell.getNumericCellValue()+"";// 体系名称
			}
			bean.productCategory.replace(" ", "");//去空格
		}
		index = sortedCloumNameList.get("体系ID");
		cell = row.getCell(index);
		if(cell==null){
			bean.systemId="";
		}else{
			try {
				bean.systemId = (int)cell.getNumericCellValue()+"";//体系ID
			} catch (Exception e) {
				bean.systemId = cell.getStringCellValue()+"";//体系ID
			}
		}
		index = sortedCloumNameList.get("体系介绍");cell = row.getCell(index);
		if(cell==null){
			bean.productCategoryDesc="";
		}else{
			try {
				bean.productCategoryDesc = cell.getStringCellValue();// 体系介绍
			} catch (Exception e) {
				bean.productCategoryDesc = cell.getNumericCellValue()+"";// 体系介绍
			}
		}
		index = sortedCloumNameList.get("指标名称");cell = row.getCell(index);
		if(cell==null){
			bean.indicatorName="";
		}else{
			try {
				bean.indicatorName = cell.getStringCellValue();// 指标名称
			} catch (Exception e) {
				bean.indicatorName = cell.getNumericCellValue()+"";// 指标名称
			}
		}
		index = sortedCloumNameList.get("指标要求");cell = row.getCell(index);
		if(cell==null){
			bean.indicatorRequire="";
		}else{
			try {
				bean.indicatorRequire =cell.getStringCellValue();// 指标要求
			} catch (Exception e) {
				bean.indicatorRequire =cell.getNumericCellValue()+"";// 指标要求
			}
		}
		index = sortedCloumNameList.get("关联ID");cell = row.getCell(index);
		if(cell==null){
			bean.relatedSystemId="";
		}else{
			try {
				bean.relatedSystemId = (int)cell.getNumericCellValue()+"";//关联ID
			} catch (Exception e) {
				bean.relatedSystemId = cell.getStringCellValue()+"";//关联ID
			}
		}
		index = sortedCloumNameList.get("指标介绍");cell = row.getCell(index);
		if(cell==null){
			bean.indicatorIntroduce="";
		}else{
			try {
				bean.indicatorIntroduce=cell.getStringCellValue();//指标介绍
			} catch (Exception e) {
				bean.indicatorIntroduce=cell.getNumericCellValue()+"";//指标介绍
			}
		}
		index = sortedCloumNameList.get("指标单位");cell = row.getCell(index);
		if(cell==null){
			bean.unit="";
		}else{
			try {
				bean.unit = cell.getStringCellValue();//指标单位
			} catch (Exception e) {
				bean.unit = cell.getNumericCellValue()+"";//指标单位
			}
			bean.unit.replace(" ", "");//去空格
		}
		index = sortedCloumNameList.get("指标备注");cell = row.getCell(index);
		if(cell==null){
			bean.remark="";
		}else{
			try {
				bean.remark = cell.getStringCellValue();//指标备注
			} catch (Exception e) {
				bean.remark = cell.getNumericCellValue()+"";//指标备注
			}
		}
		index = sortedCloumNameList.get("最小值");cell = row.getCell(index);
		if(cell==null){
			bean.minValue="";
		}else{
			try {
				bean.minValue = cell.getNumericCellValue()+"";// 最小值
			} catch (Exception e) {
				bean.minValue = cell.getStringCellValue()+"";// 最小值
			}
		}
		index = sortedCloumNameList.get("最大值");cell = row.getCell(index);
		if(cell==null){
			bean.maxValue="";
		}else{
			try {
				bean.maxValue = cell.getNumericCellValue()+"";// 最大值
			} catch (Exception e) {
				bean.maxValue = cell.getStringCellValue()+"";// 最大值
			}
		}
		index = sortedCloumNameList.get("检测方法");cell = row.getCell(index);
		if(cell==null){
			bean.detectionMethod="";
		}else{
			try {
				bean.detectionMethod = cell.getStringCellValue();// 检测方法
			} catch (Exception e) {
				bean.detectionMethod = cell.getNumericCellValue()+"";// 检测方法
			}
		}
		index = sortedCloumNameList.get("来源标准");cell = row.getCell(index);
		if(cell==null){
			bean.sourceStandard="";
		}else{
			try {
				bean.sourceStandard = cell.getStringCellValue();// 来源标准
			} catch (Exception e) {
				bean.sourceStandard = cell.getNumericCellValue()+"";// 来源标准
			}
		}
		index = sortedCloumNameList.get("有效性");cell = row.getCell(index);
		if(cell==null){
			bean.effectiveness="";
		}else{
			try {
				bean.effectiveness = cell.getStringCellValue();// 有效性
			} catch (Exception e) {
				bean.effectiveness = cell.getNumericCellValue()+"";// 有效性
			}
		}
		index = sortedCloumNameList.get("实施日期");cell = row.getCell(index);
		if(cell==null){
			bean.start_date="";
		}else{
			bean.start_date = DateUtil.getDateStr(cell.getDateCellValue());//实行日期
		}
		index = sortedCloumNameList.get("废止日期");cell = row.getCell(index);
		if(cell==null){
			bean.end_date="";
		}else{
			bean.end_date = DateUtil.getDateStr(cell.getDateCellValue());//废止日期
		}
		index = sortedCloumNameList.get("体系备注");cell = row.getCell(index);
		if(cell==null){
			bean.systemNameNote="";
		}else{
			try {
				bean.systemNameNote = cell.getStringCellValue();//体系名称备注
			} catch (Exception e) {
				bean.systemNameNote = cell.getNumericCellValue()+"";//体系名称备注
			}
		}
		//CNS
		index = sortedCloumNameList.get("CNS");cell = row.getCell(index);
		if(cell==null){
			bean.cns="";
		}else{
			try {
				bean.cns = cell.getStringCellValue();//CNS号
			} catch (Exception e) {
				bean.cns = cell.getNumericCellValue()+"";//CNS号
			}
		}
		//INS
		index = sortedCloumNameList.get("INS");cell = row.getCell(index);
		if(cell==null){
			bean.ins="";
		}else{
			try {
				bean.ins = cell.getStringCellValue();//INS字段
			} catch (Exception e) {
				bean.ins = cell.getNumericCellValue()+"";//INS字段
			}
		}
		//上限符号 最大值符号
		
		index = sortedCloumNameList.containsKey("最大值符号") ? sortedCloumNameList.get("最大值符号") : -1;
		cell = index == -1 ? null : row.getCell(index);
		if(cell==null){
			bean.upOperation="";
		}else{
			try {
				bean.upOperation = cell.getStringCellValue();// 最大值符号
			} catch (Exception e) {
				bean.upOperation = cell.getNumericCellValue()+"";// 最大值符号
			}
		}
		//下限符号  最小值符号
		
		index = sortedCloumNameList.containsKey("最小值符号") ? sortedCloumNameList.get("最小值符号") : -1;
		cell = index == -1 ? null : row.getCell(index);
		if(cell==null){
			bean.downOperation="";
		}else{
			try {
				bean.downOperation = cell.getStringCellValue();// 最小值符号
			} catch (Exception e) {
				bean.downOperation = cell.getNumericCellValue()+"";// 最小值符号
			}
		}
		lawBeanList.add(bean);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ILawImportService#getLawCategoryFromExcel(java.lang.
	 * String) 获取要导入的是U8_IndexItem或者是U8_Material类型的
	 */
	@Override
	public String getLawCategoryFromExcel(String filePath) {
		String categroyStr = "";
		FileInputStream fis = null;
		Workbook wb = null;
		try {
			fis = new FileInputStream(new File(filePath));
			wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheet("Sheet1");
			Row row = sheet.getRow(1);
			Cell cell = row.getCell(2);
			categroyStr = cell.getStringCellValue();

		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return categroyStr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uds.yl.service.ILawImportService#createOrUpdateLawBom(java.util.List,
	 * java.util.List, java.lang.String) 根据Excel更新BOM，修改或者创建
	 */
	@Override
	public void createOrUpdateLawBom(TCComponentBOMLine topBomLine, List<LawBean> lawBeansFromExcel,
			List<TCComponentBOMLine> lawBomLineChilds,String bomType
			,TCComponentFolder indexFolder,TCComponentFolder materialFolder) throws TCException, InstantiationException, IllegalAccessException {
		String excelCategoryStr = bomType;
		
		String category = "";//体系名称
		String unit  = "";//指标单位
		String systemId = "";//体系ID 
		String systemNote = "";//体系备注
		for (LawBean bean : lawBeansFromExcel) {
			boolean exitInBOM = false;
			for (TCComponentBOMLine bomLine : lawBomLineChilds) {// 判断是否在BOM中  唯一的判定是：体系名称+体系ID+指标名称+指标单位
				String name = bomLine.getItemRevision().getProperty("object_name");//指标名称
				category = bomLine.getProperty("U8_category");//体系名称
				unit  = bomLine.getProperty("U8_standardunit");//指标单位
				systemId = bomLine.getProperty("u8_systemid");//体系ID
				if (bean.indicatorName.equals(name) && bean.productCategory.equals(category) && bean.unit.equals(unit)) {// 要导入的条目存在bom中更新
					exitInBOM = true;
					AnnotationFactory.setObjectInTC(bean, bomLine);
					bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
				} else if (bean.indicatorName.equals(name) && !bean.productCategory.equals(category)) {// 存在，体系名不同说明要重复使用同一个指标两次，故copy一分，修改属性
					exitInBOM = true;
					TCComponentBOMLine addBomLine = topBomLine.add(bomLine.getItem(), bomLine.getItemRevision(), null,
							false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
				}else if(bean.indicatorName.equals(name) && !bean.unit.equals(unit)){//如果是名称一样 但是单位不一样也是认为是两个指标
					exitInBOM = true;
					TCComponentBOMLine addBomLine = topBomLine.add(bomLine.getItem(), bomLine.getItemRevision(), null,
							false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
				}
			}
			
			if (!exitInBOM) {// 如果不存在
				// 搜索，没有就创建，赋值
				TCComponentQuery query = null;
				TCComponent[] resultSearch = null;
				if("U8_Material".equals(bomType)){
					if(StringsUtil.isEmpty(bean.unit)){
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_NULL_USEDINLAW.getValue());	
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "名称" },
								new String[] { bean.indicatorName });
					}else{
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_USEDINLAW.getValue());
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "名称","单位" },
								new String[] { bean.indicatorName ,bean.unit});
					}
					
					
				}else if("U8_IndexItem".equals(bomType)){
					if(StringsUtil.isEmpty(bean.unit)){
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_NULL_USEDINLAW_FOR_IMPORT.getValue());
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "名称"},
								new String[] { bean.indicatorName});
					}else{
						query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW_FOR_IMPORT.getValue());
						resultSearch = QueryUtil.getSearchResult(query, new String[] { "名称","单位" },
								new String[] { bean.indicatorName,bean.unit });
					}

				}
				if(resultSearch==null){
					System.out.println(bean.indicatorName+"指标查询结果为NULL");
				}
				if (resultSearch.length == 0) {// 没有找到需要创建
					AbstractAIFUIApplication app = AIFDesktop.getActiveDesktop().getCurrentApplication();
					TCSession session = (TCSession) app.getSession();
					TCComponentItemType item_type = (TCComponentItemType) session
							.getTypeComponent(excelCategoryStr);

					String newID = item_type.getNewID();
					String newRev = item_type.getNewRev(null);
					String type = excelCategoryStr;
					String name = bean.indicatorName;
					String desc = Const.LawImport.USED_IN_LAW;//用来标记法规导入的时候创建的指标或者原料
					TCComponentItem newItem = item_type.create(newID, newRev,
							type, name, desc, null);
					
					TCComponentItemRevision newIndexItemRev = newItem.getLatestItemRevision();
					TCComponentBOMLine addBomLine = topBomLine.add(newItem, newItem.getLatestItemRevision(), null, false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					newIndexItemRev.setProperty("u8_uom", bean.unit);//单位
					
					String  detectionMethod = newItem.getLatestItemRevision().getProperty("u8_testmethod2");
					if(!detectionMethod.contains(bean.detectionMethod)){
						newIndexItemRev.setProperty("u8_testmethod2", detectionMethod+", "+bean.detectionMethod);
					}
					
					 category = bean.productCategory;//体系名称
					 systemId = bean.systemId;//体系ID 
					 systemNote = bean.systemNameNote;//体系备注
					
					addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");//所有法规导入功能要创建的指标和原料都要标记为UsedInLaw
					
					//把Item都添加到固定的文件夹下
					if("U8_IndexItem".equals(bomType)){//指标文件夹
						indexFolder.add("contents", newItem);
					}else if("U8_Material".equals(bomType)){//原料文件夹
						materialFolder.add("contents", newItem);
					}
					
					
					
				} else {//查找到了
					TCComponentItemRevision itemRevision = null;
					for (TCComponent component : resultSearch) {
						if (component instanceof TCComponentItemRevision) {
							String lawDesc = component.getProperty("object_desc");
							if(!Const.LawImport.USED_IN_LAW.equals(lawDesc)){
								//如果找到的类型不是使用在法规中的就直接跳过
								continue;
							}
							itemRevision = (TCComponentItemRevision) component;
							// 找到版本--在Bom中添加
							TCComponentBOMLine addBomLine = topBomLine.add(itemRevision.getItem(), itemRevision, null,
									false);
							AnnotationFactory.setObjectInTC(bean, addBomLine);
							if(ItemUtil.isModifiable(itemRevision)){//有写权限
								//添加版本上的属性 检测方法
								String  detectionMethod = addBomLine.getItemRevision().getProperty("u8_testmethod2");
								if(!detectionMethod.contains(bean.detectionMethod)){
									addBomLine.getItemRevision().setProperty("u8_testmethod2", detectionMethod+", "+bean.detectionMethod);
								}
							}
							
							category = bean.productCategory;//体系名称
							systemId = bean.systemId;//体系ID 
							systemNote = bean.systemNameNote;//体系备注
							
							break;// 就找到一个就好暂时
						}
					}
				}
			}
		}
		
		TCComponentItemRevision lawRevision = topBomLine.getItemRevision();
		//关闭
		try {
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			topBomLine.refresh();
			bomWindow.save();
			bomWindow.close();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		//写版本和bomRevision属性
		lawRevision.setProperty("u8_systemid",systemId);//体系id
		lawRevision.setProperty("u8_category",category);//体系名称
		lawRevision.setProperty("u8_systemnamenote",systemNote);//体系备注

		
	}

	@Override
	public TCComponentItem searchLawItemByID(String id){
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"ID"}, new String[]{id});
		
		if(searchResult.length!=1){
			return null;
		}
		TCComponentItem lawItem = (TCComponentItem) searchResult[0];
		return lawItem;
		
	};
	
	//检查是否有多与64个汉字128个字节的名字
	@Override
	public boolean isCanImportByName(List<LawBean> lawBeansFromExcel){
		for(LawBean bean : lawBeansFromExcel){
			String name = bean.indicatorName;
			if(name.toCharArray().length>64){
				return false;
						
			}
		}
		return true;
		
	}

	/* (non-Javadoc)
	 * 创建一个新的法规
	 * 法规的ID
	 * 法规的名称
	 * 法规版本的版本号
	 * 
	 * 最后将版本返回
	 */
	@Override
	public TCComponentItemRevision createLawItem(TCComponentFolder folder,String lawID, String lawName, String lawRevNum,Logger logger) {
		String name = lawName.substring(0,lawName.length()>42?40:lawName.length());
		String desc = lawName;
		TCComponentItem lawItem = ItemUtil.createtLawItemWithRevNum(lawID, name, lawRevNum,desc);
		if(lawItem==null){   
			//检查excel的名称是否正确
			logger.fine("请检查"+lawID+"-"+lawRevNum+lawName+"-文件名称是否合适");
			MessageBox.post("请检查"+lawID+"-"+lawRevNum+lawName+"  的名称","",MessageBox.INFORMATION);
			return null;
		}
		try {
			folder.add("contents", lawItem);
			TCComponentItemRevision latestItemRevision = lawItem.getLatestItemRevision();
			return latestItemRevision;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * 根据已经存在法规更新版本
	 * 法规Item
	 * 要更新的版本号
	 * 
	 * 最后将版本返回
	 */
	@Override
	public TCComponentItemRevision updateLawItem(TCComponentItem lawItem, String lawRevNum) {
		try {
			TCComponentItemRevision itemRev = lawItem.getLatestItemRevision();
			String revNum = itemRev.getProperty("item_revision_id");
			if(lawRevNum.equals(revNum)){
				return null;
			}
			TCComponentItemRevision newItemRev = itemRev.saveAs(lawRevNum);
			return newItemRev;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public TCComponentFolder getIndexFolder() {
		
		TCComponentFolder indexFolder = null;
		TCComponentFolder homeFolder = null;
		String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","描述","类型"}, new String[]{"_INDEX", "_INDEX", "Folder"} );
		if(searchResult.length>0){
			indexFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(indexFolder==null){//没有找到 创建
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","类型","所有权用户"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//找到我们要找的文件夹
					homeFolder = (TCComponentFolder) component;
				}
			}
			
			
			try {
				indexFolder = ItemUtil.createFolder("_INDEX", "_INDEX");
				homeFolder.add("contents", indexFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return indexFolder;
	}

	@Override
	public TCComponentFolder getMaterialFolder() {
		
		TCComponentFolder materialFolder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","描述","类型"}, new String[]{"WN_MATERIAL","WN_MATERIAL","Folder"} );
		if(searchResult.length>0){
			materialFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(materialFolder==null){//没有找到 创建
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
			searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","类型","所有权用户"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//找到我们要找的文件夹
					homeFolder = (TCComponentFolder) component;
				}
			}
			try {
				materialFolder = ItemUtil.createFolder("WN_MATERIAL", "WN_MATERIAL");
				homeFolder.add("contents", materialFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return materialFolder;
	}
}
