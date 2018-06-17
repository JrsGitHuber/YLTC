package com.uds.yl.service.impl;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.httpclient.contrib.benchmark.BenchmarkWorker;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.formula.functions.Index;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.FormulatorExcelMaterialBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.IMilkPowderFormulatorService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.DataSetUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.PreferenceUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.TemplateFilePathUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.StringsUtil;

public class MilkPowderFormulatorServiceImpl implements IMilkPowderFormulatorService{

	
	/* (non-Javadoc)
	 * 获取查询原料的结果集合
	 */
	@Override
	public List<TCComponentItemRevision> searchMaterialResult(String name,String type,String supplier) {
		List<TCComponentItemRevision> materialList = new ArrayList<>();
		String value = "*"+name+"*";
		TCComponentQuery materialQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_MP.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(materialQuery, new String[]{"名称"}, new String[]{value});
		
		for(TCComponent component : searchResult){
			TCComponentItemRevision revision = (TCComponentItemRevision) component;
			materialList.add(revision);
		}
		return materialList;
	}
	
	/**
	 * @param name
	 * @return 搜索到的原料对应的实体类
	 */
	public List<MaterialBean> searchMaterialBeansList(String name){
		List<MaterialBean> materialBeanList = new ArrayList<>();
		String value = "*"+name+"*";
		TCComponentQuery materialQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_MP.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(materialQuery, new String[]{"名称"}, new String[]{value});
		
		for(TCComponent component : searchResult){
			TCComponentItemRevision revision = (TCComponentItemRevision) component;
			MaterialBean materialBean = null;
			try {
				materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, revision);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			materialBeanList.add(materialBean);
		}
		return materialBeanList;
		
	}
	
	
	
	/**
	 * @param formulatorItemRevList
	 * @param formulatorTableList
	 * @return 获取作为临时缓存配方用来生成营养成分表的
	 */
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
//		Logger.fine("准备获取临时配方topBomline");
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
//					Logger.fine("临时配方查询得到");
					itemExit = true;
					break;
				}
			}
			
			if(!itemExit){//item没有存在 就创建一个
				//为空就创建
				cacheItem = ItemUtil.createtItem("U8_Formula", "TempFormula", userId);
//				Logger.fine("临时配方查询不到，创建一个TempFormula");
			}else {
			}
			itemRevision = cacheItem.getLatestItemRevision();
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			if (topBomLine == null) {// 如过为null需要为版本创建视图
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
//				Logger.fine("获取得到临时配方表的topBomLine");
			}

			// 获取BOM结构中的原材料
//			Logger.fine("将临时配方的BOMLine内容删除");
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
				bomMaterialBomLineList.add(bomLineChild);
			}

			// 根据配方表的数据对BOM结构进行调整
			for (TCComponentBOMLine materialBomLine : bomMaterialBomLineList) {// 移出原有
				materialBomLine.cut();
			}
//			Logger.fine("将在table中搭建的结构配置在临时配方BOM结构中");
			for (TCComponentItemRevision materialItemRev : formulatorItemRevList) {// 添加新
				topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();

			// 更新更新的BOM结构后的数据
//			Logger.fine("将在table中搭建的结构数据写在临时配方BOM结构中");
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

	
	
	
	/**
	 * @param topBomLine
	 * @return 获取配方对象中的原料Bom对象
	 */
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


		return materialBomList;
	}

	
	
	/**
	 * @param topBomLine
	 * @return 获取配方中的指标的Bom对象
	 */
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine)  {
		// ===================获取所有的可以使用的indexItem类型的BOM
		Double sumInventory = 0d;//用来记录这个配方中的总量
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		List<TCComponentBOMLine> bomList = new ArrayList<>();//存储的是所有的原料的说
		List<MaterialBean> materialBeanList = new ArrayList<>();//存储的是所有的原料对应的实体Bean
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();//第一层的原料
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				String type = bomLineTemp.getItem().getType();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
					bomList.add(bomLineTemp);
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
					materialBeanList.add(materialBean);
					sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
				}else if(bomLineTemp.getItem().getType().equals("U8_Formula")){//是基粉的话  bomLineTemp是基粉
					Double outPut = StringsUtil.convertStr2Double(bomLineTemp.getItemRevision().getProperty("u8_OutPut"));//出粉量
					outPut =1000d;
					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class,bomLineTemp);//基粉的实体列
					sumInventory += StringsUtil.convertStr2Double(baseBean.U8_inventory);
					Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);
					AIFComponentContext[] baseChilds = bomLineTemp.getChildren();
					for(AIFComponentContext baseChildContext : baseChilds){//基粉下面的第一层不管是不是营养包
						TCComponentBOMLine materialBomLine = (TCComponentBOMLine) baseChildContext.getComponent();
						bomList.add(materialBomLine);
						MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,materialBomLine);
						materialBean.U8_inventory = (baseInventory/outPut)*StringsUtil.convertStr2Double(materialBean.U8_inventory)+"";//转换一下量的说
						materialBeanList.add(materialBean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
		}
		// 遍历 获取所有的指标bom
		for(int i=0;i<bomList.size();i++){
			TCComponentBOMLine bomLine = bomList.get(i);// 不论这个元素是营养包还是普通的原料 里面的内容都作为指标来看待
			MaterialBean bean = materialBeanList.get(i);
			try {
				AIFComponentContext[] indexChilds = bomLine.getChildren();
				for(AIFComponentContext indexContext : indexChilds){
 					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
 					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class,indexBomLine);
 					
 					if(isNutritionBom(bomLine)){//如果是营养包的话 下面的指标是上下线的均值
 						indexItemBean.bl_quantity = (StringsUtil.convertStr2Double(indexItemBean.up)+StringsUtil.convertStr2Double(indexItemBean.down))/2+"";
 					}
 					indexItemBean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)*StringsUtil.convertStr2Double(indexItemBean.bl_quantity)/100+"";
 					
					allIndexBomList.add(indexBomLine);
					allIndexBeanList.add(indexItemBean);
				}
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		
		// 初始化allIndexBomList
		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (IndexItemBean bean : allIndexBeanList) {
			// 去重复
			if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
				int index = nameList.indexOf(bean.objectName);
				IndexItemBean exitBean = indexBeanList.get(index);
				
				Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
				Double inventory = Utils.convertStr2Double(bean.U8_inventory);
				if (exitBean.isFirst) {// 标记过之后就直接相加
					exitBean.U8_inventory = inventory + "";
				} else {
					exitBean.down = inventory+exitInventory+ "";
					exitBean.isFirst = false;
				}

			} else {
				indexBeanList.add(bean);
				nameList.add(bean.objectName);
			}
		}
		
		//计算每个指标的数量   
		for(IndexItemBean bean : indexBeanList){
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/sumInventory*100+"";//根据总的投料量来计算的说
		}
		return indexBeanList;
	}

	
	/* (non-Javadoc)
	 * 干法搭建中的因为可能是多个基粉 
	 *	所以这里的话就事一个集合
	 *	里面的指标是去过重复的
	 */
	@Override
	public List<IndexItemBean> getIndexBeanListFromBaseBom(List<TCComponentBOMLine> baseBomList) {
		
		List<IndexItemBean> allBaseIndexBeansList = new ArrayList<>();
		Double baseSumInventory = 0d;
		for(TCComponentBOMLine topBOmLine : baseBomList){
			try {
				List<MaterialBean> materialBeanList = new ArrayList<>();
				List<TCComponentBOMLine> materialBomList = new ArrayList<>();//这个里面可能含有营养包 但是营养包下的元素也是直接作为指标来进行处理
				List<IndexItemBean> allIndexBeanList = new ArrayList<>();
				
				Double outPut = StringsUtil.convertStr2Double(topBOmLine.getItemRevision().getProperty("u8_OutPut"));//出粉量
				MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class,topBOmLine);//基粉的实体列
				Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);
				baseSumInventory += baseInventory;
				AIFComponentContext[] baseChilds = topBOmLine.getChildren();
				for(AIFComponentContext baseChildContext : baseChilds){//基粉下面的第一层不管是不是营养包
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) baseChildContext.getComponent();
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,materialBomLine);
					materialBean.U8_inventory = (baseInventory/outPut)*StringsUtil.convertStr2Double(materialBean.U8_inventory)+"";//转换一下量的说
					materialBeanList.add(materialBean);
					materialBomList.add(materialBomLine);
				}
				
				// 遍历 获取所有的指标bom
				for(int i=0;i<materialBomList.size();i++){
					TCComponentBOMLine bomLine = materialBomList.get(i);// 不论这个元素是营养包还是普通的原料 里面的内容都作为指标来看待
					MaterialBean bean = materialBeanList.get(i);
					try {
						AIFComponentContext[] indexChilds = bomLine.getChildren();
						for(AIFComponentContext indexContext : indexChilds){
		 					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
		 					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class,indexBomLine);
		 					
		 					if(isNutritionBom(bomLine)){//如果是营养包的话 下面的指标是上下线的均值
		 						indexItemBean.bl_quantity = (StringsUtil.convertStr2Double(indexItemBean.up)+StringsUtil.convertStr2Double(indexItemBean.down))/2+"";
		 						indexItemBean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)*StringsUtil.convertStr2Double(indexItemBean.bl_quantity)+"";
		 					}else{
		 						indexItemBean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)*StringsUtil.convertStr2Double(indexItemBean.bl_quantity)/100+"";
		 					}
		 					
							allIndexBeanList.add(indexItemBean);
						}
					} catch (TCException | InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
				
				
				// 初始化allIndexBomList
				List<IndexItemBean> indexBeanList = new ArrayList<>();
				List<String> nameList = new ArrayList<>(); // 去重复的标记数组
				for (IndexItemBean bean : allIndexBeanList) {
					// 去重复
					if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
						int index = nameList.indexOf(bean.objectName);
						IndexItemBean exitBean = indexBeanList.get(index);
						
						Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
						Double inventory = Utils.convertStr2Double(bean.U8_inventory);
						if (exitBean.isFirst) {// 标记过之后就直接相加
							exitBean.U8_inventory = inventory + "";
						} else {
							exitBean.U8_inventory = inventory+exitInventory+ "";
							exitBean.isFirst = false;
						}

					} else {
						indexBeanList.add(bean);
						nameList.add(bean.objectName);
						bean.isFirst = false;
					}
				}
				
				//每一个指标在基粉中重量既投料量是知道了  然后去求出每个指标在基粉中的数量既百分比
				for(IndexItemBean bean : indexBeanList){
					bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/StringsUtil.convertStr2Double(baseBean.U8_inventory)*100+"";//根据总的投料量来计算的说
				}
				allBaseIndexBeansList.addAll(indexBeanList);
			} catch (Exception e) {
			}
		}
		
		//开始总的去重
		
		
		
		//去重后的指标 要根据总的投料量来计算配比的
		for(IndexItemBean bean : allBaseIndexBeansList){
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/baseSumInventory*100+"";//根据总的投料量来计算的说
		}
		return allBaseIndexBeansList; 
	}
	
	
	/* (non-Javadoc)
	 * 获取配方中的干法部分的去重计算后的指标
	 */
	@Override
	public List<IndexItemBean> getIndexBeanListFromDryBom(TCComponentBOMLine topBomLine) {
		// ===================获取所有的可以使用的indexItem类型的BOM
		Double sumInventory = 0d;// 用来记录这个配方中的总量
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		List<TCComponentBOMLine> bomList = new ArrayList<>();// 存储的是所有的原料的说
		List<MaterialBean> materialBeanList = new ArrayList<>();// 存储的是所有的原料对应的实体Bean
		// 先将第一层进队列
		try {
			AIFComponentContext[] children = topBomLine.getChildren();// 第一层的原料
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				String type = bomLineTemp.getItem().getType();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// 是原材料
					bomList.add(bomLineTemp);
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
					materialBeanList.add(materialBean);
					sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
				} else if (bomLineTemp.getItem().getType().equals("U8_Formula")) {// 是基粉的话
																					// bomLineTemp是基粉
					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);// 基粉的实体列
					sumInventory += StringsUtil.convertStr2Double(baseBean.U8_inventory);
					//基粉的话只用计算配方的总重量的说
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
		}
		// 遍历 获取所有的指标bom
		for (int i = 0; i < bomList.size(); i++) {
			TCComponentBOMLine bomLine = bomList.get(i);// 不论这个元素是营养包还是普通的原料
														// 里面的内容都作为指标来看待
			MaterialBean bean = materialBeanList.get(i);
			try {
				AIFComponentContext[] indexChilds = bomLine.getChildren();
				for (AIFComponentContext indexContext : indexChilds) {
					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBomLine);

					if (isNutritionBom(bomLine)) {// 如果是营养包的话 下面的指标是上下线的均值
						indexItemBean.bl_quantity = (StringsUtil.convertStr2Double(indexItemBean.up)
								+ StringsUtil.convertStr2Double(indexItemBean.down)) / 2 + "";
					}
					indexItemBean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)
							* StringsUtil.convertStr2Double(indexItemBean.bl_quantity) / 100 + "";

					allIndexBomList.add(indexBomLine);
					allIndexBeanList.add(indexItemBean);
				}
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		// 初始化allIndexBomList
		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (IndexItemBean bean : allIndexBeanList) {
			// 去重复
			if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
				int index = nameList.indexOf(bean.objectName);
				IndexItemBean exitBean = indexBeanList.get(index);

				Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
				Double inventory = Utils.convertStr2Double(bean.U8_inventory);
				if (exitBean.isFirst) {// 标记过之后就直接相加
					exitBean.U8_inventory = inventory + "";
				} else {
					exitBean.down = inventory + exitInventory + "";
					exitBean.isFirst = false;
				}

			} else {
				indexBeanList.add(bean);
				nameList.add(bean.objectName);
			}
		}

		// 计算每个指标的数量
		for (IndexItemBean bean : indexBeanList) {
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory) / sumInventory * 100 + "";// 根据总的投料量来计算的说
		}
		return indexBeanList;
	}
	
	
	/* (non-Javadoc)
	 * 将配方中的湿法中的指标
	 * 干法中的指标 分别经过湿法损耗 干法损耗  保质期损耗的计算
	 * 得到的最终结果是最终的经过去重后的指标集合
	 */
	@Override
	public List<IndexItemBean> getFinallIndexBeanList(TCComponentBOMLine cacheTopBomLine,
			TCComponentItemRevision wetLossItemRevsion, TCComponentItemRevision dryLossItemRevsion,
			TCComponentItemRevision dateLossItemRevsion) {
		
		//湿法损耗里面的指标项目
		TCComponentBOMLine wetLossTopBomLine = BomUtil.getTopBomLine(wetLossItemRevsion, "视图");
		List<IndexItemBean> wetLossIndexBeanList = getLossIndexBeanList(wetLossTopBomLine);
		//干法损耗里面的指标项目
		TCComponentBOMLine dryLossTopBomLine = BomUtil.getTopBomLine(dryLossItemRevsion, "视图");
		List<IndexItemBean> dryLossIndexBeanList = getLossIndexBeanList(dryLossTopBomLine);
		//保质期损耗里面的指标项目
		TCComponentBOMLine dateLossTopBomLine = BomUtil.getTopBomLine(dateLossItemRevsion, "视图");
		List<IndexItemBean> dateLossIndexBeanList = getLossIndexBeanList(dateLossTopBomLine);
		
		
		
	 
		/**
		 * 1、获取配方中的基粉（湿法搭建的）
		 * 2、根据湿法的损耗来计算出来一个集合
		 * 3、将湿法根据损耗计算出来的东西累加上干法的指标
		 * 4、将3的结果去根据干法的损耗进行计算出一个集合
		 * 5、将4的结果就是所有的指标项了 最后需要根据保质期损耗进行计算的说
		 * **/
		
		//配方中的的所有基粉既湿法的指标集合
		List<TCComponentBOMLine> basePowderBomLineList = getBasePowderBomLine(cacheTopBomLine);
		List<IndexItemBean> baseIndexItemBeanList = getIndexBeanListFromBaseBom(basePowderBomLineList);
		
		
		
		//baseIndexItemBeanList需要根据是湿法损耗来计算出来一套结果
		for(IndexItemBean indexItemBean : baseIndexItemBeanList){
			for(IndexItemBean bean : wetLossIndexBeanList){
				if(indexItemBean.objectName.equals(bean.objectName)){//匹配到了
					indexItemBean.u8Loss = bean.u8Loss;
					break;
				}
			}
		}
		computeIndexBeanByLoss(baseIndexItemBeanList);	//根据损耗计算出来各个指标的投料量和配比

		//获取配方中的干法处的所有原料的指标
		List<IndexItemBean> dryIndexItemBeanList = getIndexBeanListFromDryBom(cacheTopBomLine);
		
		//将湿法的结果和干法的累加（只是投料量）
		List<IndexItemBean> finallIndexBeanList = new ArrayList<>(); 
		Set<String> nameSet = new HashSet<>();
		for(IndexItemBean wetBean : baseIndexItemBeanList){
			nameSet.add(wetBean.objectName);
		}
		for(IndexItemBean dryBean : dryIndexItemBeanList){
			nameSet.add(dryBean.objectName);
		}
		Iterator<String> iterator = nameSet.iterator();
		while(iterator.hasNext()){
			String name = iterator.next();
			IndexItemBean bean = new IndexItemBean();
			bean.objectName = name;
			for(IndexItemBean wetBean : baseIndexItemBeanList){
				if(name.equals(wetBean.objectName)){
					bean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)
							+StringsUtil.convertStr2Double(wetBean.U8_inventory)+"";
				}
			}
			for(IndexItemBean dryBean : dryIndexItemBeanList){
				if(name.equals(dryBean.objectName)){
					bean.U8_inventory = StringsUtil.convertStr2Double(bean.U8_inventory)
							+StringsUtil.convertStr2Double(dryBean.U8_inventory)+"";
				}
			}
			finallIndexBeanList.add(bean);
		}
		
		//根据配方总的投料量计算出干法和湿法合集的各个指标的投料量和配比
		Double sumInventory = 0d;
		try {
			AIFComponentContext[] materialChilds = cacheTopBomLine.getChildren();
			for(AIFComponentContext materialContext : materialChilds){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialContext.getComponent();
				MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);
				sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		for (IndexItemBean bean : finallIndexBeanList) {
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory) / sumInventory * 100 + "";// 根据总的投料量来计算的说
		}
		
		//将干湿法指标集合计算干法损耗
		for(IndexItemBean indexItemBean : finallIndexBeanList){
			for(IndexItemBean bean : dryLossIndexBeanList){
				if(indexItemBean.objectName.equals(bean.objectName)){//匹配到了
					indexItemBean.u8Loss = bean.u8Loss;
					break;
				}
			}
		}
		computeIndexBeanByLoss(finallIndexBeanList);
		
		//将干湿法指标集合计算保质期损耗
		for(IndexItemBean indexItemBean : finallIndexBeanList){
			for(IndexItemBean bean : dateLossIndexBeanList){
				if(indexItemBean.objectName.equals(bean.objectName)){//匹配到了
					indexItemBean.u8Loss = bean.u8Loss;
					break;
				}
			}
		}
		computeIndexBeanByLoss(finallIndexBeanList);
		
		
		return finallIndexBeanList;
	}
	
	
	/**
	 * @param materialBomList
	 * @param indexBeanList
	 * 将转换为实体类的数据写入到excel中去
	 */
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


	/**
	 * @param materialBomList
	 * @return 将数据转换为要写入excel中的实体类的类型
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
	
	
	
	/**
	 * @param itemRevision
	 * @param formulatorItemRevList
	 * @param formulatorTableList
	 * 创建配方的视图
	 */
	public void createFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList,TCComponentItemRevision lossItemRev) {
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
			//构造BOM并更新数据
			for(int i=0;i<formulatorItemRevList.size();i++){
				TCComponentItemRevision materialItemRev = formulatorItemRevList.get(i);
				MaterialBean bean  = formulatorTableList.get(i);
				TCComponentBOMLine addBomLine = topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
				AnnotationFactory.setObjectInTC(bean, addBomLine);
				
			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();
			topBomLine.refresh();
			
			//计算当前基粉的出粉量
			Double sumInventory  = 0d;//总的干物质计算出来的说
			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			AIFComponentContext[] childrens = topBomLine.getChildren();
			for(AIFComponentContext context : childrens){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();
				if(isNutritionBom(materialBomLine)){//如果是营养包
					Double nutritionLoss = 1-(StringsUtil.convertStr2Double(materialBomLine.getItemRevision().getProperty("u8_Loss"))/100);
					Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
					sumInventory += nutritionLoss*inventory;
				}else {//就是一个单独的原料
					Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
					
					AIFComponentContext[] indexChildrens = materialBomLine.getChildren();
					for(AIFComponentContext indexContext : indexChildrens){
						TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
						IndexItemBean indexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBomLine);
						if(indexBean.objectName.contains("干物质")){
							Double quantity = inventory*StringsUtil.convertStr2Double(indexBean.bl_quantity)/100;
							sumInventory += quantity;
						}
					}
				}
			}
			
			TCComponentBOMLine lossTopBomLine = BomUtil.getTopBomLine(lossItemRev, "视图");
			if(lossTopBomLine==null){
				MessageBox.post("请检查损耗的BOM结构","",MessageBox.INFORMATION);
				return;
			}
			
			//计算并写入出粉值
			Double outPut = 0d;
			AIFComponentContext[] lossChildrens = lossTopBomLine.getChildren();
			for(AIFComponentContext lossIndexContext : lossChildrens){
				TCComponentBOMLine lossBomLine = (TCComponentBOMLine) lossIndexContext.getComponent();
				IndexItemBean lossIndexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, lossBomLine);
				if(lossIndexBean.objectName.contains("干物质")){//找到干物质的损耗
					Double loss = 1-StringsUtil.convertStr2Double(lossIndexBean.u8Loss)/100;
					outPut = sumInventory/loss;
				}
			}
			
			if(0==outPut){
				outPut = sumInventory;
			}
			//写到配方上面
			itemRevision.setProperty("u8_OutPut", outPut+"");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/* (non-Javadoc)
	 * 创建干法配方
	 */
	@Override
	public void createDryFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList) {
		
		try {
			itemRevision.setProperty("object_desc", "PF");
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		
		//找到湿法的对象 如果是原料而不是配方就要报错
		for(int i=0;i<formulatorItemRevList.size();i++){
			MaterialBean bean = formulatorTableList.get(i);
			String type = formulatorItemRevList.get(i).getType();
			if("U8_MaterialRevision".equals(type)&&"湿法".equals(bean.productMethod)){
				MessageBox.post("请不要使用湿法原料，请选择基粉","",MessageBox.INFORMATION);
				return;
			}
		}
		
		
		//搭建配方
		try {
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
			//构造BOM并更新数据
			for(int i=0;i<formulatorItemRevList.size();i++){
				TCComponentItemRevision materialItemRev = formulatorItemRevList.get(i);
				MaterialBean addBean  = formulatorTableList.get(i);
				TCComponentBOMLine addBomLine = topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
				AnnotationFactory.setObjectInTC(addBean, addBomLine);
			}
			
			
			//这里的逻辑是用来计算在干法中用到的基粉中的原料的量的计算
//			AIFComponentContext[] materialChildren = topBomLine.getChildren();
//			for(AIFComponentContext materialChildContext : materialChildren){
//				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialChildContext.getComponent();
//				TCComponentItemRevision materialItemRev = materialBomLine.getItemRevision(); 
//				
//				if("U8_FormulaRevision".equals(materialItemRev.getType())){//如果是基粉要单独处理基粉之下的原料的说
//					List<MaterialBean> wetFormulatorBeanList = new ArrayList<>();
//					TCComponentBOMLine wetTopBomLine = BomUtil.getTopBomLine(materialItemRev, "视图");
//					
//					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);//在干法配方中 基粉的投料量
//					if(wetTopBomLine==null){//如果没有视图的话就跳出
//						break;
//					}
//					AIFComponentContext[] wetChildren = wetTopBomLine.getChildren();
//					for(AIFComponentContext wetContext : wetChildren){//基粉配方中
//						TCComponentBOMLine tempBomLine = (TCComponentBOMLine) wetContext.getComponent();
//						MaterialBean wetBean = AnnotationFactory.getInstcnce(MaterialBean.class, tempBomLine);
//						wetFormulatorBeanList.add(wetBean);
//					}
//					
//					//获取了基础数据（基粉中各种原料的投料量 ） 根据基粉的投料量去计算在干法中基粉中的原料的各种投料
//					Double output = StringsUtil.convertStr2Double(materialItemRev.getProperty("u8_OutPut"));
//					output = 1000d;//要删掉的
//					AIFComponentContext[] dryChildren = materialBomLine.getChildren();
//					for(int j=0;j<dryChildren.length;j++){//干法中的基粉下的原料
//						TCComponentBOMLine dryBomLine = (TCComponentBOMLine) dryChildren[j].getComponent();
//						MaterialBean dryBean = wetFormulatorBeanList.get(j);//基粉配方中的原料
//						
//						//(基粉的投料量 / 基粉配方的出粉量 )* 基粉中某原料的投料量
//						Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);//基粉的投料量
//						Double dryMaterialInventory = StringsUtil.convertStr2Double(dryBean.U8_inventory);//一份基粉中的一个原料的投料量
//						
//						if(0==output){//出粉量为0说明有问题
//							//投料量是不变的说
//						}else {
//							dryBean.U8_inventory = baseInventory*dryMaterialInventory/output+"";
//						} 
//						AnnotationFactory.setObjectInTC(dryBean, dryBomLine);
//						System.out.println("");
//					}
//				}
//			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();

			
			topBomLine.refresh();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	/* (non-Javadoc)
	 * 创建个湿法配方 放到home下
	 */
	@Override
	public void createWetFormulatorInHome(List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList, String name,TCComponentItemRevision lossItemRev)  {
			try {
				String osUserName = UserInfoSingleFactory.getInstance().getUser().getOSUserName();
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, 
						new String[]{"名称","类型","所有权用户"}, 
						new String[]{"Home","Home 文件夹",
								UserInfoSingleFactory.getInstance().getUser().getOSUserName()});
				if(searchResult.length==0){
					MessageBox.post("请检查Home文件夹是否存在","",MessageBox.INFORMATION);
					return ;
				}
				
				TCComponentItem  item= ItemUtil.createtItem("U8_Formula", name, "");
				if(item==null){
					MessageBox.post("创建失败","",MessageBox.INFORMATION);
					return ;
				}
				TCComponentItemRevision itemRevision = item.getLatestItemRevision();
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
				
				//构造BOM并更新数据
				for(int i=0;i<formulatorItemRevList.size();i++){
					TCComponentItemRevision materialItemRev = formulatorItemRevList.get(i);
					MaterialBean bean  = formulatorTableList.get(i);
					TCComponentBOMLine addBomLine = topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
					AnnotationFactory.setObjectInTC(bean, addBomLine);
					
				}

				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				bomWindow.save();
				bomWindow.close();

				
				topBomLine.refresh();

				TCComponentFolder homeFolder = (TCComponentFolder) searchResult[0];
				homeFolder.add("contents", item);
				MessageBox.post("OK","",MessageBox.INFORMATION);
				
				
				//计算当前基粉的出粉量
				Double sumInventory  = 0d;//总的干物质计算出来的说
				AIFComponentContext[] childrens = topBomLine.getChildren();
				for(AIFComponentContext context : childrens){
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();
					if(isNutritionBom(materialBomLine)){//如果是营养包
						Double nutritionLoss = StringsUtil.convertStr2Double(materialBomLine.getItemRevision().getProperty("u8_Loss"));
						Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
						sumInventory += nutritionLoss*inventory;
					}else {//就是一个单独的原料
						Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
						
						AIFComponentContext[] indexChildrens = materialBomLine.getChildren();
						for(AIFComponentContext indexContext : indexChildrens){
							TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
							IndexItemBean indexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBomLine);
							if(indexBean.objectName.contains("干物质")){
								Double quantity = inventory*StringsUtil.convertStr2Double(indexBean.bl_quantity);
								sumInventory += quantity*inventory;
							}
						}
					}
				}
				
				TCComponentBOMLine lossTopBomLine = BomUtil.getTopBomLine(lossItemRev, "视图");
				if(lossTopBomLine==null){
					MessageBox.post("请检查损耗的BOM结构","",MessageBox.INFORMATION);
					return;
				}
				
				//计算并写入出粉值
				Double outPut = 0d;
				AIFComponentContext[] lossChildrens = lossTopBomLine.getChildren();
				for(AIFComponentContext lossIndexContext : lossChildrens){
					TCComponentBOMLine lossBomLine = (TCComponentBOMLine) lossIndexContext.getComponent();
					IndexItemBean lossIndexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, lossBomLine);
					if(lossIndexBean.objectName.contains("干物质")){//找到干物质的损耗
						Double loss = StringsUtil.convertStr2Double(lossIndexBean.u8Loss);
						outPut = loss * sumInventory;
					}
				}
				
				//写到配方上面
				itemRevision.setProperty("u8_OutPut", outPut+"");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	
	/* (non-Javadoc)
	 * 更新营养包的结构
	 */
	@Override
	public void updateNutritionStruct(TCComponentItemRevision nutritionItemRev,
			List<TCComponentItemRevision> materialRevList, List<MaterialBean> materialBeanList) {
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(nutritionItemRev,Const.CommonCosnt.BOM_VIEW_NAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(nutritionItemRev);
		}
		try {
			//先清空
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				bomLine.cut();
			}
			
			//添加并回写信息
			for(int i=0;i<materialRevList.size();i++){
				TCComponentItemRevision materialRev = materialRevList.get(i);
				MaterialBean materialBean = materialBeanList.get(i);
				TCComponentBOMLine materialBom = topBomLine.add(materialRev.getItem(), materialRev, null, false);
				AnnotationFactory.setObjectInTC(materialBean,materialBom);
			}
			
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}finally {
			TCComponentBOMWindow cachedWindow = topBomLine.getCachedWindow();
			try {
				cachedWindow.save();
				cachedWindow.close();
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
	}

	
	

	/* (non-Javadoc)
	 * 获取损耗中的指标的Bean的集合
	 */
	@Override
	public List<IndexItemBean> getLossIndexBeanList(TCComponentBOMLine topBomLine) {
		List<IndexItemBean> lossIndexBeanList = new ArrayList<>();
		if(topBomLine==null){
			return lossIndexBeanList;
		}
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				lossIndexBeanList.add(bean);
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return lossIndexBeanList;
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
		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (TCComponentBOMLine bomLine : waitMaterialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				// 去重复
				if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
					int index = nameList.indexOf(bean.objectName);
					MaterialBean exitBean = waitMaterialBeanList.get(index);
					Double exitInvnetory = 0.0d;
					Double inventory = 0.0d;
					try {
						exitInvnetory = StringsUtil.convertStr2Double(exitBean.U8_inventory);
					} catch (Exception e) {
						exitInvnetory = 0.0d;
					}
					try {
						inventory = StringsUtil.convertStr2Double(bean.U8_inventory);
					} catch (Exception e) {
						inventory = 0.0d;
					}
					exitBean.U8_inventory = (exitInvnetory + inventory) + "";
				} else {
					waitMaterialBeanList.add(bean);
					nameList.add(bean.objectName);
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
				AIFComponentContext[] children = bomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {
						allIndexBomList.add(bomLineTemp);
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
	public List<IndexItemBean> getWaitIndexBeanList(List<TCComponentBOMLine> waitIndexBomList) {
		List<IndexItemBean> waitIndexBeanList = new ArrayList<>();

		List<String> nameList = new ArrayList<>(); // 去重复的标记数组
		for (TCComponentBOMLine bomLine : waitIndexBomList) {
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				// 去重复
				if (nameList.contains(bean.objectName)) {// 这个是要合并的Bean 不存入数组
					int index = nameList.indexOf(bean.objectName);
					IndexItemBean exitBean = waitIndexBeanList.get(index);
					Double exitUp = StringsUtil.convertStr2Double(exitBean.up);
					Double up = StringsUtil.convertStr2Double(bean.up);
					Double exitDown = StringsUtil.convertStr2Double(exitBean.down);
					Double down = StringsUtil.convertStr2Double(bean.down);
					Double exitQuantity = StringsUtil.convertStr2Double(exitBean.bl_quantity);
					Double quantity = StringsUtil.convertStr2Double(bean.bl_quantity);
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
						exitBean.isFirst = true;
					}

				} else {
					waitIndexBeanList.add(bean);
					nameList.add(bean.objectName);
				}

			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return waitIndexBeanList;
	}
	
	/*
	 * @param checkLawRevList
	 * @return 获取法规中的添加剂Bean
	 */
	@Override
	public List<MaterialBean> getCheckMaterialBeanList(List<TCComponentItemRevision> checkLawRevList) {
		List<MaterialBean> checkMaterialBeanList = new ArrayList<>();
		for (TCComponentItemRevision itemRevision : checkLawRevList) {
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.FormulatorCheck.BOMNAME);
			try {
				String lawName = itemRevision.getProperty("object_name");
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_Material")) {
						MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class ,bomLineTemp);
						if(StringsUtil.isEmpty(bean.relatedSystemId)||"0".equals(bean.relatedSystemId)){//无连接  不处理
						}else{//有连接的
							getAllLinkMaterialBean(lawName, bean, checkMaterialBeanList);
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
	 * @param lawName  法规的名字
	 * @param bean		指标的名字
	 * @param checkMaterialBeanList  存放原料的数组
	 * 
	 * 匹配到 2760 或者14880的就是原料
	 */
	public void getAllLinkMaterialBean(String lawName, MaterialBean bean, List<MaterialBean> checkMaterialBeanList){
		//根据连接的法规的ID找到法规
		String[] splitsLawIds = bean.indicatorRequire.split("#");
		String relatedIds = bean.relatedSystemId;

		for (String lawId : splitsLawIds) {
			if (lawId.startsWith("GB") && lawId.contains("2760") || lawId.contains("14880")) {// 说明合适
																								// 是原料
																								// 不来自2760或者14880
				// 搜索找到法规
				TCComponentItemRevision itemRevision = null;
				String lawID = relatedIds + " " + lawId;
				List<TCComponentItemRevision> checkLawRevList = getCheckLawRevList(lawID);
				
				if (checkLawRevList.size() > 0) {
					itemRevision = checkLawRevList.get(0);
				}else{
					continue;//如果在一个要链接的法规 没有找到的话就要跳过接续下一次的
				}

				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.FormulatorCheck.BOMNAME);
				try {
					String lawNameTemp = itemRevision.getProperty("object_name");
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
						if (bomLineTemp.getItem().getType().equals("U8_Material")) {
							MaterialBean beanTemp = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
							// 只有一层
							if (beanTemp.systemId.equals(bean.relatedSystemId)) {// 有连接的
								beanTemp.lawName = lawNameTemp;
								checkMaterialBeanList.add(beanTemp);
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
		}
	}
	
	
	/*
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
	
	/*
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
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {
						IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
						if(StringsUtil.isEmpty(bean.relatedSystemId)||"0".equals(bean.relatedSystemId)){//空   无连接
							bean.lawName = lawName;
							checkIndexBeanList.add(bean);
						}else{//有连接的  
							getAllLinkIndexBean(lawName,bean,checkIndexBeanList);
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
	
	
	/**
	 * 根据有链接的指标项目找到对应的所有的指标项目 存放到数组里面去
	 * @param lawName 法规的名字
	 * @param bean  有链接的指标项目
	 * @param checkIndexBeanList  存放所有用法规重的指标项目
	 */
	private void getAllLinkIndexBean(String lawName, IndexItemBean bean, List<IndexItemBean> checkIndexBeanList) {
		//根据连接的法规的ID找到法规
		String[] splitsLawIds = bean.indicatorRequire.split("#");
		String relatedIds = bean.relatedSystemId;
		
		for(String lawId : splitsLawIds){
			if(lawId.startsWith("GB")&&!lawId.contains("2760")&&!lawId.contains("14880")){//说明合适  是产品标准 不来自2760或者14880
				//搜索找到法规
				TCComponentItemRevision itemRevision = null;
				String lawID = relatedIds+" "+lawId;
				List<TCComponentItemRevision> checkLawRevList = getCheckLawRevList(lawId);
				if(checkLawRevList.size()>0){
					itemRevision = checkLawRevList.get(0);
				}

				if(itemRevision==null){
					//说明这个链接没有找到对应的法规
					break;
				}
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.FormulatorCheck.BOMNAME);
				try {
					String lawNameTemp = itemRevision.getProperty("object_name");
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
						if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {
							IndexItemBean beanTemp = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
							//只有一层
							if(beanTemp.systemId.equals(bean.relatedSystemId)){//有连接的
								beanTemp.lawName = lawNameTemp;
								checkIndexBeanList.add(beanTemp);
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
		}
		
	}
	
	

	/*
	 * (non-Javadoc) 获取检查添加剂后的Bean
	 */
	@Override
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,
			List<MaterialBean> checkMaterialBeanList) {
		List<FormulatorCheckedBean> materialCheckedBean = new ArrayList<>();
		for (MaterialBean waitBean : waitMaterialBeanList) {
			for (MaterialBean checkBean : checkMaterialBeanList) {
				if (waitBean.type.equals(checkBean.category) && checkBean.objectName.contains(waitBean.objectName)) {// 检查类型是否一致
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "添加剂";
					checkedBean.formulatorValue = Utils.convertStr2Double(waitBean.U8_inventory)+"";
					checkedBean.lawName = checkBean.lawName;
					checkedBean.lawValue = Utils.convertStr2Double(checkBean.down) + "~" + Utils.convertStr2Double(checkBean.up);
					if (true) {// 超标计算
						Double waitValue = Utils.convertStr2Double(waitBean.U8_inventory);
						Double checkDown = Utils.convertStr2Double(checkBean.down);
						Double checkUp = Utils.convertStr2Double(checkBean.up);
						if (waitValue < checkDown) {// 下限超标
							checkedBean.excessiveDesc = "低于下限";
							checkedBean.wranings = checkBean.warning;
						}
						if (waitValue > checkUp) {// 上限超标
							checkedBean.excessiveDesc = "超过上限";
							checkedBean.wranings = checkBean.warning;
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
	 * @param waitIndexBeanList
	 * @param checkIndexBeanList
	 * @return 检查过后的指标项
	 */
	@Override
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,
			List<IndexItemBean> checkIndexBeanList) {
		List<FormulatorCheckedBean> indexCheckedBean = new ArrayList<>();
		for (IndexItemBean waitBean : waitIndexBeanList) {
			for (IndexItemBean checkBean : checkIndexBeanList) {
				if (waitBean.type.equals(checkBean.category) &&checkBean.objectName.contains( waitBean.objectName)) {// 检查类型是否一致
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "指标";
					checkedBean.formulatorValue = Utils.convertStr2Double(waitBean.down) + "~" + Utils.convertStr2Double(waitBean.up);
					checkedBean.lawName = checkBean.lawName;
					checkedBean.lawValue = Utils.convertStr2Double(checkBean.down) + "~" + Utils.convertStr2Double(checkBean.up);
					if (true) {// 超标计算
						Double waitDown = Utils.convertStr2Double(waitBean.down);
						Double waitUp = Utils.convertStr2Double(waitBean.up);
						Double checkDown = Utils.convertStr2Double(checkBean.down);
						Double checkUp = Utils.convertStr2Double(checkBean.up);
						if (waitDown > checkDown) {// 下限超标
							checkedBean.excessiveDesc = "下限超标";
							checkedBean.wranings = checkBean.warning;
						}
						if (waitUp > checkUp) {// 上限超标
							checkedBean.excessiveDesc = "上限超标";
							checkedBean.wranings = checkBean.warning;
						}
					}
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
			out = new FileOutputStream(outFile);
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


	
	
	/* (non-Javadoc)
	 * 将执行标准(质量技术标准的说)
	 */
	@Override
	public void getCheckIndexBeanListByIndexStandard(List<IndexItemBean> chechIndexItemBeanList,
			TCComponentItemRevision indexStandardRev) {
		TCComponentBOMLine topBomLine  = BomUtil.getTopBomLine(indexStandardRev, Const.CommonCosnt.BOM_VIEW_NAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(indexStandardRev);
		}
		
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				
			}

		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * 根据选中的干法配方的版本来生成生产配方表
	 */
	@Override
	public void createFormulatorExcel(TCComponentItemRevision formulatorRev) {
		
		List<MaterialBean> materialBeanList = new ArrayList<>();//干法中的所有都当做一个原料看
		List<MaterialBean> wetBeanList = new ArrayList<>();//基粉中的原料 包含营养包

		
//		//先下载模板
		File file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		if(file.exists()){//存在就删除
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.MilkPowderFormulator.Formulator_Excel_Name);
		if(dataset==null){
			MessageBox.post("数据集下载失败","",MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.MilkPowderFormulator.Template_Dir);
		if(resultStrs.length==0){//下载失败
			MessageBox.post("数据集下载失败","",MessageBox.INFORMATION);
			return ;
		}
		
		//获取数据的说
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, "视图");
		if(topBomLine==null){//如果结构为空就创建出来一个新的空的根视图
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			AIFComponentContext[] materialChildrens = topBomLine.getChildren();
			for(AIFComponentContext materialContext : materialChildrens){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialContext.getComponent();
				MaterialBean dryBean  = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);
				materialBeanList.add(dryBean);
				
				if(isWetBom(materialBomLine)){//这个是湿法配方的BOM
					AIFComponentContext[] wetChildrens = materialBomLine.getChildren();
					for(AIFComponentContext wetContext : wetChildrens){//只有湿法中存在营养包
						TCComponentBOMLine wetBomLine = (TCComponentBOMLine) wetContext.getComponent();
						MaterialBean wetBean  = AnnotationFactory.getInstcnce(MaterialBean.class, wetBomLine);
						if(isNutritionBom(wetBomLine)){//如果是营养包
							wetBean.isNutrition=true;
						}
						wetBeanList.add(wetBean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		//写数据
		int wetBeanStart = 14;//基粉的信息
		int dryBeanStart = 23;//干混部分的信息
		int mIncrement = 0;//记录插入了多少行
		File inFile = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet(Const.MilkPowderFormulator.EXCEL_SHEET1);
			
			/*
			 * 1、将基粉的信息写到excel中(营养包的单位是一套)
			 * 2、将干混部分的信息写到excel中
			 */
			
			
			//基粉信息 下面都营养包
			for(int i=0;i<wetBeanList.size();i++){
				//先合并单元格
				CellRangeAddress cra=new CellRangeAddress(wetBeanStart, wetBeanStart, 1, 3);  
				sheet.addMergedRegion(cra); 
				
				//合并单元格第2个字段
			    cra=new CellRangeAddress(wetBeanStart,wetBeanStart, 4, 6);        
			    sheet.addMergedRegion(cra); 
			    
			    MaterialBean bean = wetBeanList.get(i);
				Cell cell = getCell(sheet, wetBeanStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				if(bean.isNutrition){//如果是营养包
					cell = getCell(sheet, wetBeanStart, 4);
					cell.setCellValue("一套");setCellBorder(cell, wb);
				}else{
					cell = getCell(sheet, wetBeanStart, 4);
					cell.setCellValue(bean.U8_inventory);setCellBorder(cell, wb);	
				}
				
				if(i<wetBeanList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
					sheet.shiftRows(wetBeanStart, sheet.getLastRowNum(), 1);
					mIncrement++;//最后一个增量不增加1
				}else{
//					mIncrement++;//最后一个增量不增加1
				}
			}
//			//填写干混的所有的原料信息
			dryBeanStart = dryBeanStart+mIncrement;//根据之前的增量的值
			for(int i=0;i<materialBeanList.size();i++){
				//先合并单元格
				CellRangeAddress cra=new CellRangeAddress(dryBeanStart, dryBeanStart, 1, 3);  
				sheet.addMergedRegion(cra); 
				
				//合并单元格第2个字段
			    cra=new CellRangeAddress(dryBeanStart,dryBeanStart, 4, 6);        
			    sheet.addMergedRegion(cra); 
			    
			    MaterialBean bean = materialBeanList.get(i);
				Cell cell = getCell(sheet, dryBeanStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				
				cell = getCell(sheet, dryBeanStart, 4);
				cell.setCellValue(bean.U8_inventory);setCellBorder(cell, wb);	
				
				if(i<materialBeanList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
					sheet.shiftRows(dryBeanStart, sheet.getLastRowNum(), 1);
					mIncrement++;//最后一个增量不增加1
				}else{
					mIncrement++;//最后一个增量不增加1
				}
			}
			
			//保存
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		//上传
		file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		if(file.exists()){//存在就上传
			 try {
				 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						 Const.MilkPowderFormulator.Foumulator_Excel_Input_Path,
						 DataSetUtil.DataSetType.MSExcelX, 
						 "excel", 
						 Const.MilkPowderFormulator.Formulator_Excel_Upload_Name);
				 formulatorRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("数据集下载失败","",MessageBox.INFORMATION);
				return ;
			}
		 }
		
		
	}

	/* (non-Javadoc)
	 * 根据选中的干法配方来生成营养包的信息表格
	 */
	@Override
	public void createNutritionExcel(TCComponentItemRevision formulatorRev) {
		
		List<NutritionBean> nutritionBeansList = new ArrayList<>();
	
		//先下载模板
		File file = new File(Const.MilkPowderFormulator.Nutrition_Excel_Input_Path);
		if (file.exists()) {// 存在就删除
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.MilkPowderFormulator.Nutrition_Excel_Name);
		if (dataset == null) {
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.MilkPowderFormulator.Template_Dir);
		if(resultStrs.length==0){//下载失败
			MessageBox.post("数据集下载失败","",MessageBox.INFORMATION);
			return ;
		}
		
		//获取数据的说
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, "视图");
		if(topBomLine==null){//如果结构为空就创建出来一个新的空的根视图
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			AIFComponentContext[] materialChildrens = topBomLine.getChildren();
			for(AIFComponentContext materialContext : materialChildrens){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialContext.getComponent();
				if(isWetBom(materialBomLine)){//这个是湿法配方的BOM
					
					TCComponentItemRevision wetItemRev = materialBomLine.getItemRevision();
					Double output = StringsUtil.convertStr2Double(wetItemRev.getProperty("u8_OutPut"));//wetBean
					
					AIFComponentContext[] wetChildrens = materialBomLine.getChildren();//这个才是基粉
					MaterialBean baseBean  = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);
					for(AIFComponentContext wetContext : wetChildrens){//只有湿法中存在营养包
						TCComponentBOMLine wetBomLine = (TCComponentBOMLine) wetContext.getComponent();
						MaterialBean wetBean  = AnnotationFactory.getInstcnce(MaterialBean.class, wetBomLine);
						if(isNutritionBom(wetBomLine)){//如果是营养包
							wetBean.isNutrition=true;
							NutritionBean nutritionBean = new NutritionBean();
							nutritionBean.rootBean = wetBean;
							nutritionBean.childList = new ArrayList<>();
							//要计算一下营养包的投料量 根据基粉的投料量  -ps:暂时不改变 湿法配方中投多少就是多少 
//							wetBean.U8_inventory = StringsUtil.convertStr2Double(baseBean.U8_inventory)*StringsUtil.convertStr2Double(wetBean.U8_inventory)/output+"";
							AIFComponentContext[] nutritionChilds = wetBomLine.getChildren();
							for(AIFComponentContext nutritionContext : nutritionChilds){
								TCComponentBOMLine nutritionBom = (TCComponentBOMLine) nutritionContext.getComponent();
								MaterialBean nutritionMaterialBean = AnnotationFactory.getInstcnce(MaterialBean.class, nutritionBom);
								nutritionBean.childList.add(nutritionMaterialBean);
							}
							nutritionBeansList.add(nutritionBean);//将一个营养包放到结构中
						}
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		//写数据
		int start = 15;
		int mIncrement  =0;//作为增长的变量
		File inFile = new File(Const.MilkPowderFormulator.Nutrition_Excel_Input_Path);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet(Const.MilkPowderFormulator.EXCEL_SHEET1);
			
			for(int i=0;i<nutritionBeansList.size();i++){//一个营养包
				start = start + mIncrement;
				MaterialBean rootBena = nutritionBeansList.get(i).rootBean;
				List<MaterialBean> nutritionMaterialList = nutritionBeansList.get(i).childList;
				
				for(int j=0;j<nutritionMaterialList.size();j++){
					MaterialBean nutritionMaterialBean = nutritionMaterialList.get(j);
					
					CellRangeAddress cra=new CellRangeAddress(start, start, 1, 2);  
					sheet.addMergedRegion(cra); 
					
					cra=new CellRangeAddress(start, start, 3, 4);  
					sheet.addMergedRegion(cra); 
					
					cra=new CellRangeAddress(start, start, 5, 6);  
					sheet.addMergedRegion(cra); 
					
					Cell cell = getCell(sheet, start, 1);
					cell.setCellValue(nutritionMaterialBean.objectName);setCellBorder(cell, wb);
					
					cell = getCell(sheet, start, 3);//化合物来源
					cell.setCellValue(nutritionMaterialBean.sourceOfCompound);setCellBorder(cell, wb);
					
					cell = getCell(sheet, start, 5);//标准
					cell.setCellValue(nutritionMaterialBean.down+"-"+nutritionMaterialBean.up);setCellBorder(cell, wb);
					
					if(j<nutritionMaterialList.size()-1){//当时最后一个的时候直接跳过了然后就不用在插入一行了
						sheet.shiftRows(start, sheet.getLastRowNum(), 1);
						mIncrement++;//
					}else{
						mIncrement++;//这里这个竟然要+1
					}
				}
				
				
				//最后进行单元格的合并写值啊然后写名称
				CellRangeAddress cra=new CellRangeAddress(start, start+nutritionMaterialList.size()-1, 0, 0);  
				sheet.addMergedRegion(cra); 
				
				cra=new CellRangeAddress(start, start+nutritionMaterialList.size()-1, 7, 7);  
				sheet.addMergedRegion(cra); 
				
				Cell cell = getCell(sheet, start, 0);
				cell.setCellValue(rootBena.objectName);setCellBorder(cell, wb);
				
				
				cell = getCell(sheet, start, 7);//包重
				cell.setCellValue(rootBena.U8_inventory);setCellBorder(cell, wb);
//				
			}
			
			//保存
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		
		//上传
		file = new File(Const.MilkPowderFormulator.Nutrition_Excel_Input_Path);
		if (file.exists()) {// 存在就上传
			try {
				TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						Const.MilkPowderFormulator.Nutrition_Excel_Input_Path, DataSetUtil.DataSetType.MSExcelX,
						"excel", Const.MilkPowderFormulator.Nutrition_Excel_Upload_Name);
				formulatorRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
				return;
			}
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * 将已经获取到的指标
	 * 营养包中的营养素是原料  但是要生成指标的Bean然后和已经存在的指标进行合并
	 */
	@Override
	public List<IndexItemBean> getAllIndexBeanContainNutrition(List<IndexItemBean> indexBeanList,TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> nutritionBomList = new ArrayList<>();
		List<IndexItemBean> nutritionIndexList = new ArrayList<>();
		try {
			AIFComponentContext[] allChildren = topBomLine.getChildren();//第一层
			for(AIFComponentContext allContext : allChildren){
				TCComponentBOMLine alllBom = (TCComponentBOMLine) allContext.getComponent();
				TCComponentItemRevision allRev = alllBom.getItemRevision();
				if("U8_FormulaRevision".equals(allRev.getType())){//找到基粉
					//找营养包
					AIFComponentContext[] materialChildren = alllBom.getChildren();
					for(AIFComponentContext materialContext : materialChildren){
						TCComponentBOMLine materailBom = (TCComponentBOMLine) materialContext.getComponent();
						AIFComponentContext[] nutritionChildren = materailBom.getChildren();//可能是营养包的对象
						for(AIFComponentContext nutritionContext : nutritionChildren){
							TCComponentBOMLine nutritionBom = (TCComponentBOMLine) nutritionContext.getComponent();
							TCComponentItemRevision nutritionRev = nutritionBom.getItemRevision();
							if("U8_MaterialRevision".equals(nutritionRev.getType())){//找到了营养包  materailBom
								nutritionBomList.add(materailBom);
								continue;
							}
						}
					}
				}
			}
			
			//将所有营养包下面的原料作为指标的bean进行封装
			for(TCComponentBOMLine bomLine : nutritionBomList){
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine childBom = (TCComponentBOMLine) context.getComponent();
					IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class,childBom);
					nutritionIndexList.add(bean);
				}
			}
			
			
			//找到不存在与原集合中的指标条目
			List<IndexItemBean> noExitIndexList = new ArrayList<>();
			for(IndexItemBean nutritinBean : nutritionIndexList){
				boolean flag = false;
				for(IndexItemBean allBean : indexBeanList){
					if(nutritinBean.objectName.equals(allBean.objectName)){//如果存在了就怎么办呢
						flag = true;
						allBean.up = StringsUtil.convertStr2Double(allBean.up) + StringsUtil.convertStr2Double(nutritinBean.up)+"";
						allBean.down = StringsUtil.convertStr2Double(allBean.down) + StringsUtil.convertStr2Double(nutritinBean.down)+"";
						allBean.average = StringsUtil.convertStr2Double(allBean.average) + StringsUtil.convertStr2Double(nutritinBean.average)+"";
					}
				}
				
				if(!flag){//如果不存在
					noExitIndexList.add(nutritinBean);
				}
			}
			indexBeanList.addAll(noExitIndexList);
			
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return indexBeanList;
	}
	
	
	/* (non-Javadoc)
	 * 生成营养成分表
	 */
	@Override
	public void createNutritionIndexExcel(List<IndexItemBean> allIndexBeanList,TCComponentItemRevision formulatorRev) {
		
		//先下载模板
		File file = new File(Const.MilkPowderFormulator.Index_Excel_Input_Path);
		if (file.exists()) {// 存在就删除
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.MilkPowderFormulator.Index_Excel_Name);
		if (dataset == null) {
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel,
				Const.MilkPowderFormulator.Template_Dir);
		if (resultStrs.length == 0) {// 下载失败
			MessageBox.post("数据集下载失败", "", MessageBox.INFORMATION);
			return;
		}

		// 写数据
		int start = 30;
		File inFile = new File(Const.MilkPowderFormulator.Index_Excel_Input_Path);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet(Const.MilkPowderFormulator.EXCEL_SHEET1);

			for(IndexItemBean bean : allIndexBeanList){
				CellRangeAddress cra = new CellRangeAddress(start, start, 0, 1);
				sheet.addMergedRegion(cra);

				cra = new CellRangeAddress(start, start, 2, 3);
				sheet.addMergedRegion(cra);

				cra = new CellRangeAddress(start, start, 4, 5);
				sheet.addMergedRegion(cra);
				cra = new CellRangeAddress(start, start, 6, 7);
				sheet.addMergedRegion(cra);
				
				 Cell cell = getCell(sheet, start, 0);
				 cell.setCellValue(bean.objectName);setCellBorder(cell,wb);
				
				 cell = getCell(sheet, start, 2);
				 cell.setCellValue(bean.bl_quantity);setCellBorder(cell,wb);
				
				 sheet.shiftRows(start, sheet.getLastRowNum(), 1);
			}



			// 保存
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 上传
		file = new File(Const.MilkPowderFormulator.Index_Excel_Input_Path);
		if (file.exists()) {// 存在就上传
			try {
				TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						Const.MilkPowderFormulator.Index_Excel_Input_Path, DataSetUtil.DataSetType.MSExcelX, "excel",
						Const.MilkPowderFormulator.Index_Excel_Upload_Name);
				formulatorRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("数据集上传失败", "", MessageBox.INFORMATION);
				return;
			}
		}
		
	}
	/**
	 * 判断一个原料是否是营养包的说
	 * @param bomLine
	 * @return 
	 */
	public boolean isNutritionBom(TCComponentBOMLine bomLine){
		boolean isNutritionFlag = false;
		try {
			//判断是否是营养包
			AIFComponentContext[] childrens2 = bomLine.getChildren();
			for(AIFComponentContext context2 : childrens2){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context2.getComponent();
				String type = tempBomLine.getItemRevision().getType();
				if("U8_MaterialRevision".equals(type)){//如果第一层下面包含的是原料 就说明这个是一个营养包
					isNutritionFlag = true;
					break;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isNutritionFlag;
	}
	
	
	/**
	 * @param bomLine  判断这个BOM是否是基粉  就是一个配配方来的
	 * @return
	 */
	public boolean isWetBom(TCComponentBOMLine bomLine){
		boolean isWetFlag = false;
		try {
			String type = bomLine.getItemRevision().getType();
			if("U8_FormulaRevision".equals(type)){//这个基粉是来自一个配方
				isWetFlag = true;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isWetFlag;
	}


	
	/*
	 * 下载模板文件 三个文件
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		if(file.exists()){//存在就删除
			file.delete();
		}
//		file = new File(Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path);
//		if(file.exists()){//存在就删除
//			file.delete();
//		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.MilkPowderFormulator.Formulator_Excel_Name);
		if(dataset==null){
			return false;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.MilkPowderFormulator.Template_Dir);
		if(resultStrs.length==0){//下载失败
			return false;
		}
		
//		dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.ProductFormulaExcel.Product_Complex_Excel_Name);
//		if(dataset==null){
//			return false;
//		}
//		resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, Const.ProductFormulaExcel.Template_Dir);
//		if(resultStrs.length==0){//下载失败
//			return false;
//		}
		return true;
	}
	
	
	
	/**
	 * 将生成的文件
	 * @param component
	 * @return
	 */
	public boolean uploadFile(TCComponent component){
		 File file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		 if(file.exists()){//存在就上传
			 try {
				 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						 Const.MilkPowderFormulator.Foumulator_Excel_Input_Path,
						 DataSetUtil.DataSetType.MSExcelX, 
						 "excel", 
						 Const.MilkPowderFormulator.Formulator_Excel_Upload_Name);
				 component.add("IMAN_specification", dataSet);
				 
			} catch (TCException e) {
				e.printStackTrace();
				return false;
			}
		 }
		
		return true;
	}

	
	/**
	 * @param sheet 那个sheet页面
	 * @param rowIndex 多少行
	 * @param cellIndex 这一行的哪一个单元格  如果是合并单元格的话就是第一个
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
	 * @param cell
	 * @param wb
	 * 为单元格设置边框
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
	 * 根据topBomLine获取下面的基粉 
	 * 基粉都是在第一层的说
	 * @return
	 */
	public List<TCComponentBOMLine> getBasePowderBomLine(TCComponentBOMLine topBomLine){
		List<TCComponentBOMLine> basePowderBomList = new ArrayList<>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				try {
					MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
					bean.objectName="";
				} catch (InstantiationException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				TCComponentItemRevision itemRevision =  bomLine.getItemRevision();
				if(itemRevision.getType().equals("U8_FormulaRevision")){//找到基粉
					basePowderBomList.add(bomLine);
				}
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return basePowderBomList;
	}
	
	/**
	 * 通过基粉来获取下面你的营养包
	 * @param basePowderBomList 基粉的集合
	 * @return
	 */
	private List<TCComponentBOMLine> getNutritionBomList(List<TCComponentBOMLine> basePowderBomList){
		List<TCComponentBOMLine> nutritionBomList = new ArrayList<>();
		for(TCComponentBOMLine basePowderBomLine : basePowderBomList){
			try {
				AIFComponentContext[] children = basePowderBomLine.getChildren();
				for(AIFComponentContext context : children){//基粉下面的孩子 可能存在营养包
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();//这个是基粉下面的原料,营养包可能存在的地方
					AIFComponentContext[] children2 = materialBomLine.getChildren();
					for(AIFComponentContext context2 : children2){
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context2.getComponent();
						if(bomLine.getItemRevision().getType().equals("U8_MaterialRevision")){//孩子是原料的原料 就是营养包了
							nutritionBomList.add(materialBomLine);
						}
					}
					
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		return nutritionBomList;
	}
	
	
	/**
	 * 通过基粉来获取下面你的营养包
	 * @param basePowderBomList 基粉的集合
	 * @return
	 */
	private List<TCComponentBOMLine> getNutritionBomList(TCComponentBOMLine basePowderBomLine){
		List<TCComponentBOMLine> nutritionBomList = new ArrayList<>();
		try {
			AIFComponentContext[] children = basePowderBomLine.getChildren();
			for(AIFComponentContext context : children){//基粉下面的孩子 可能存在营养包
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();//这个是基粉下面的原料,营养包可能存在的地方
				AIFComponentContext[] children2 = materialBomLine.getChildren();
				for(AIFComponentContext context2 : children2){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context2.getComponent();
					if(bomLine.getItemRevision().getType().equals("U8_MaterialRevision")){//孩子是原料的原料 就是营养包了
						nutritionBomList.add(materialBomLine);
					}
				}
				
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return nutritionBomList;
	}

	
	/**
	 * @param finalIndexBeanList
	 * 根据损耗值去计算指标项目中的上线和下限
	 */
	private void computeIndexBeanByLoss(List<IndexItemBean> finalIndexBeanList) {
		for(IndexItemBean indexItemBean : finalIndexBeanList){
			Double u8Loss = StringsUtil.convertStr2Double(indexItemBean.u8Loss);
			Double quantity = StringsUtil.convertStr2Double(indexItemBean.bl_quantity);
			Double inventory = StringsUtil.convertStr2Double(indexItemBean.U8_inventory);
			quantity = quantity * (100-u8Loss)/100;
			inventory = inventory * (100-u8Loss)/100;
			indexItemBean.bl_quantity = quantity+"";
			indexItemBean.U8_inventory = inventory+"";
		}
	}
	
	
	/**
	 * @param cacheTopBomLine 作为缓存的临时配方对象
	 * @return
	 */
	private Double getDryFormulatorSumInventory(TCComponentBOMLine cacheTopBomLine){
		Double sumInventory = 0d;
		try {
			AIFComponentContext[] children = cacheTopBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				sumInventory += StringsUtil.convertStr2Double(bean.U8_inventory);
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return sumInventory;
	}

	// 用来表示一个营养包的所有信息
	class NutritionBean {
		MaterialBean rootBean;
		List<MaterialBean> childList;
	}






	
	
	

}
