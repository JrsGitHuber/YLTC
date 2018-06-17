package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;















import org.jacorb.idl.runtime.int_token;

import com.sun.mail.imap.protocol.Item;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.bean.TechStandarTableBean;
import com.uds.yl.bean.TechUpDownProperty;
import com.uds.yl.bean.UpAndDonwBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.service.ITechStandarModifyService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.tcutils.Utils;
import com.uds.yl.utils.StringsUtil;
import com.uds.yl.utils.TableUtils;
import com.uds.yl.utils.TechStandardUtil;

public class TechStandarModifyServiceImpl implements ITechStandarModifyService {

	
	
	
	/* (non-Javadoc)
	 * 根据 指标的类别和指标的名称获取指标版本
	 */
	@Override
	public List<TCComponentItemRevision> getSearchIndexItemRevsionList(
			String indexType, String indexName) {
		
		List<TCComponentItemRevision> searchRevsList = new ArrayList<>();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());
		if (query == null){
			MessageBox.post("指标查询器获取失败","",MessageBox.ERROR);
			return null;
		}
		if(StringsUtil.isEmpty(indexName))//name为空
			indexName ="*";
		
		TCComponent[] searchResult = null;
		
		if(StringsUtil.isEmpty(indexType)){//类型为空
			searchResult = QueryUtil.getSearchResult(query,
					new String[] { Const.TechStandarModify.QUERY_NAME }, 
					new String[] {  indexName});
		}else {
			searchResult = QueryUtil.getSearchResult(query,
					new String[] { Const.TechStandarModify.QUERY_INDEX_TYPE,Const.TechStandarModify.QUERY_NAME }, 
					new String[] { indexType ,indexName});
		}
		
		if (searchResult.length == 0 )
			return searchRevsList;

		for(TCComponent component:searchResult){
			if(component instanceof TCComponentItemRevision){
				TCComponentItemRevision itemRevision =  (TCComponentItemRevision) component;
				searchRevsList.add(itemRevision);
			}
			
		}
		return searchRevsList;
	}
	
	

	/*
	 * (non-Javadoc) 根据itemId获取对应的法规版本
	 */
//	@Override
//	public List<TCComponentItemRevision> getSearchLawItemRevisionList(String itemId,String name) {
//		List<TCComponentItemRevision> searchRevsList = new ArrayList<>();
//		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
//		if (query == null){
//			MessageBox.post("法规查询器获取失败","",MessageBox.ERROR);
//			return null;
//		}
//
//		if(StringsUtil.isEmpty(itemId))//id为空	
//			itemId = "*";
//		if(StringsUtil.isEmpty(name))//name为空
//			name ="*";
//		if(StringsUtil.isEmpty(itemId)&&StringsUtil.isEmpty(name)){//都为空
//			MessageBox.post("","请补充搜索条件",MessageBox.INFORMATION);
//			return null;
//		}
//		TCComponent[] searchResult = QueryUtil.getSearchResult(query,
//				new String[] { Const.TechStandarModify.QUERY_ITME_ID,Const.TechStandarModify.QUERY_NAME }, new String[] { itemId ,name});
//		if (searchResult.length == 0)
//			return null;
//
//		try {
//			for(TCComponent component:searchResult){
//				TCComponentItemRevision itemRevision = ((TCComponentItem) component).getLatestItemRevision();
//				searchRevsList.add(itemRevision);
//			}
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		return searchRevsList;
//	}

	
	/*
	 * (non-Javadoc) 获取所有的条目的名字
	 */
	@Override
	public Set<String> getAllIndexItemNames(TCComponentItemRevision revision,
			List<TCComponentItemRevision> selectLawList) {
		Set<String> strSet = new HashSet<>();
		String name = "";
		TCComponentBOMLine topBomLine = null;
		try {
			// 选中的质量技术标准
			topBomLine = BomUtil.getTopBomLine(revision, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(revision);
			}
			if (topBomLine != null) {
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					name = bomLine.getItemRevision().getProperty("object_name");
					strSet.add(name);
				}
			}
			// 便利选中的法规集合
			for (TCComponentItemRevision rev : selectLawList) {
				topBomLine = BomUtil.getTopBomLine(rev, Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(revision);
				}
				if (topBomLine != null) {
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						name = bomLine.getItemRevision().getProperty("object_name");
						strSet.add(name);
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}

		return strSet;
	}

	/*
	 * (non-Javadoc) 根据版本号获取上一个版本号的质量技术标准的版本
	 */
	@Override
	public TCComponentItemRevision getOriginRev(String revNum, String revItemID) {
		int num = Integer.valueOf(revNum.charAt(0));
		if (num == 65) {// 选中的版本是A版本 已经是最低版本了
			return null;
		}
		int originNum = num - 1;
		String originNumStr = (char) originNum + "";

		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEX.getValue());
		if (query == null) {
			MessageBox.post("查询器获取获取失败", "", MessageBox.ERROR);
			return null;
		}
		TCComponent[] searchResult = QueryUtil.getSearchResult(query,
				new String[] { Const.TechStandarModify.QUERY_ITME_ID, Const.TechStandarModify.QUERY_REVISION_ID },
				new String[] { revItemID, originNumStr });

		TCComponentItemRevision itemRev = (TCComponentItemRevision) searchResult[0];
		return itemRev;
	}

	/*
	 * (non-Javadoc) 根据合并的版本集合通过名称进行过滤来获取所有的在Table中对应的Bean对象
	 */
	@Override
	public List<TechStandarTableBean> getAllTableBeans(List<TCComponentItemRevision> allRevs,
			boolean hasPreRev,List<TechStandarTableBean> indexBeanList) {
		
		List<TechStandarTableBean> allTableBeans = new ArrayList<>();
		if (hasPreRev) {// 有前一个版本
			
			//先遍历上一个版本的技术标准
			TCComponentItemRevision itemRevision = allRevs.get(0);
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
			}
			
			try {
				AIFComponentContext[] childrens = topBomLine.getChildren();
				for (AIFComponentContext context : childrens) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					String indexName = bomLine.getItemRevision().getProperty("object_name");
					String itemId = bomLine.getItem().getProperty("item_id");
					String indexUint = bomLine.getItemRevision().getProperty("u8_uom");
					String indexType = bomLine.getItemRevision().getProperty("u8_category");
					
					boolean isSameFlag = false;
					for(TechStandarTableBean bean : indexBeanList){
						if(itemId.equals(bean.itemId)){//如果上一个版本中存在和选中版本中的指标一样 就不用重新创建bean了 但是需要更新 老的内控标准
							TechUpDownProperty upDownBean = null;
							try {
								upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
							bean.oldStandard = TechStandardUtil.initResult(upDownBean.ICS_UP, upDownBean.ICS_DOWN,
									upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;;// 原内控标准
							
							bean.oldWaring = TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
									upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// 原预警值
							
							//获取 检测方法 并且去除哪些是空的
							bean.allMethodsList = new ArrayList<String>();
							String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
							for(String method : methodSplit){
								if(StringsUtil.isEmpty(method)||" ".equals(method)){
									continue;
								}
								bean.allMethodsList.add(method);
							}
							
							
							isSameFlag =true;
						}
					}
					if(!isSameFlag){//判断为真 isSame 是false 说明该条目没有存在 要新建
						TechStandarTableBean bean = new TechStandarTableBean();
						bean.name = indexName;
						bean.itemId = itemId;
						bean.type = indexType;
						bean.unit = indexUint;
						
						
						TechUpDownProperty upDownBean = null;
						try {
							upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
						bean.oldStandard =TechStandardUtil.initResult(upDownBean.ICS_UP, upDownBean.ICS_DOWN,
								upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;// 原内控标准
						
						bean.oldWaring = TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
								upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// 原预警值
						
						
						//获取 检测方法 并且去除哪些是空的
						bean.allMethodsList = new ArrayList<String>();
						String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
						for(String method : methodSplit){
							if(StringsUtil.isEmpty(method)||" ".equals(method)){
								continue;
							}
							bean.allMethodsList.add(method);
						}
						
						
						
						
						bean.lawStandards = new ArrayList<String>();
						for(int j=0;j<allRevs.size()-1;j++){
							bean.lawStandards.add("");
						}
						
						indexBeanList.add(bean);
					}
					
				}

			} catch (TCException e) {
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
			
			//再遍历跟随的法规
			for(int k=1;k<allRevs.size();k++){
				
				itemRevision = allRevs.get(k);
				topBomLine = BomUtil.getTopBomLine(itemRevision, Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
				}
				
				try {
					AIFComponentContext[] childrens = topBomLine.getChildren();
					for (AIFComponentContext context : childrens) {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						String indexName = bomLine.getItemRevision().getProperty("object_name");
						String itemId = bomLine.getItem().getProperty("item_id");
						String indexUint = bomLine.getItemRevision().getProperty("u8_uom");
						String indexType = bomLine.getItemRevision().getProperty("u8_category");
						
						String relatedSystemId = bomLine.getProperty("U8_AssociationID");//关联体系id
						
						if(!StringsUtil.isEmpty(relatedSystemId)){
							continue ;//如果有关联体系ID 说明这个条目不能作为Bean
						}
						boolean isSameFlag = false;
						for(TechStandarTableBean bean : indexBeanList){
							if(itemId.equals(bean.itemId)){//如果上一个版本中存在和选中版本中的指标一样 就不用重新创建bean了 但是需要更新 老的内控标准
								TechUpDownProperty upDownBean = null;
								try {
									upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
								} catch (InstantiationException | IllegalAccessException e) {
									e.printStackTrace();
								}
								String lawStandard= TechStandardUtil.initResult(upDownBean.GB_UP, upDownBean.GB_DOWN,
										upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// 国标
								
								
								//获取 检测方法 并且去除哪些是空的
								bean.allMethodsList = new ArrayList<String>();
								String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
								for(String method : methodSplit){
									if(StringsUtil.isEmpty(method)||" ".equals(method)){
										continue;
									}
									bean.allMethodsList.add(method);
								}
								
								bean.lawStandards.remove(k-1);
								bean.lawStandards.add(k-1, lawStandard);
								
								isSameFlag =true;
							}
						}
						if(!isSameFlag){//判断为真 isSame 是false 说明该条目没有存在 要新建
							TechStandarTableBean bean = new TechStandarTableBean();
							bean.name = indexName;
							bean.itemId = itemId;
							bean.type = indexType;
							bean.unit = indexUint;
							
							TechUpDownProperty upDownBean = null;
							try {
								upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
							String lawStandard= TechStandardUtil.initResult(upDownBean.GB_UP, upDownBean.GB_DOWN,
									upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// 国标
							
							//获取 检测方法 并且去除哪些是空的
							bean.allMethodsList = new ArrayList<String>();
							String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
							for(String method : methodSplit){
								if(StringsUtil.isEmpty(method)||" ".equals(method)){
									continue;
								}
								bean.allMethodsList.add(method);
							}
							
							
							bean.lawStandards = new ArrayList<String>();
							for(int j=0;j<allRevs.size()-1;j++){
								bean.lawStandards.add("");
							}
							bean.lawStandards.remove(k-1);
							bean.lawStandards.add(k-1, lawStandard);
							
							indexBeanList.add(bean);
						}
						
					}

				} catch (TCException e) {
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
			
			allTableBeans.addAll(indexBeanList);
			
		} else {// 没有质量技术标准之前的版本
			//再遍历跟随的法规
			for(int k=0;k<allRevs.size();k++){
				
				TCComponentItemRevision itemRevision = allRevs.get(k);
				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
				}
				
				try {
					AIFComponentContext[] childrens = topBomLine.getChildren();
					for (AIFComponentContext context : childrens) {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						String indexName = bomLine.getItemRevision().getProperty("object_name");
						String itemId = bomLine.getItem().getProperty("item_id");
						String indexUint = bomLine.getItemRevision().getProperty("u8_uom");
						String indexType = bomLine.getItemRevision().getProperty("u8_category");
						
						boolean isSameFlag = false;
						for(TechStandarTableBean bean : indexBeanList){
							//共有部分
							if(itemId.equals(bean.itemId)){//如果上一个版本中存在和选中版本中的指标一样 就不用重新创建bean了 但是需要更新 老的内控标准
								TechUpDownProperty upDownBean = null;
								try {
									upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
								} catch (InstantiationException | IllegalAccessException e) {
									e.printStackTrace();
								}
								String lawStandard= TechStandardUtil.initResult(upDownBean.GB_UP, upDownBean.GB_DOWN,
										upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// 国标
								
								//获取 检测方法 并且去除哪些是空的
								bean.allMethodsList = new ArrayList<String>();
								String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
								for(String method : methodSplit){
									if(StringsUtil.isEmpty(method)||" ".equals(method)){
										continue;
									}
									bean.allMethodsList.add(method);
								}
								
								bean.lawStandards.remove(k);
								bean.lawStandards.add(k, lawStandard);
								
								isSameFlag =true;
							}
						}
						//法规中单独存在的部分
						if(!isSameFlag){//判断为真 isSame 是false 说明该条目没有存在 要新建
							TechStandarTableBean bean = new TechStandarTableBean();
							bean.name = indexName;
							bean.itemId = itemId;
							bean.type = indexType;
							bean.unit = indexUint;
							
							TechUpDownProperty upDownBean = null;
							try {
								upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
							String lawStandard= TechStandardUtil.initResult(upDownBean.GB_UP, upDownBean.GB_DOWN,
									upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// 国标
							
							//获取 检测方法 并且去除哪些是空的
							bean.allMethodsList = new ArrayList<String>();
							String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
							for(String method : methodSplit){
								if(StringsUtil.isEmpty(method)||" ".equals(method)){
									continue;
								}
								bean.allMethodsList.add(method);
							}
							
							
							bean.lawStandards = new ArrayList<String>();
							for(int j=0;j<allRevs.size();j++){
								bean.lawStandards.add("");
							}
							bean.lawStandards.remove(k);
							bean.lawStandards.add(k, lawStandard);
							
							indexBeanList.add(bean);
						}
						
					}

				} catch (TCException e) {
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
			
			allTableBeans.addAll(indexBeanList);
		}

		return allTableBeans;
	}

	/*
	 * (non-Javadoc) 验证标准是否合适
	 */
	@Override
	public boolean vertifyStandardIsOk(List<TechStandarTableBean> allTechTableBean, JTable table) {
		boolean isOk = true;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		List<Integer> redIndexList = new ArrayList<>();
		// 便利ALLBean判断是否合规
		for (int i = 0; i < allTechTableBean.size(); i++) {
			TechStandarTableBean bean = allTechTableBean.get(i);
			List<String> lawStandards = bean.lawStandards;
			if (lawStandards.size() == 0 || lawStandards == null) {
				redIndexList.add(i);
				isOk = false;
			}

			String indexDown = "";
			String indexUp = "";
			String indexDownSymbol = "";
			String indexUpSymbol = "";
			String indexDetectvalue = "";
			if (!bean.newStandard.equals("") && bean.newStandard != null) {
//				setUpAndDownBynewStandard(bean.newStandard);
				UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
				indexDown = initUpAndDonwBean.down;
				indexUp = initUpAndDonwBean.up;
				indexDownSymbol = initUpAndDonwBean.downSymbol;
				indexUpSymbol = initUpAndDonwBean.upSymbol;
				indexDetectvalue = initUpAndDonwBean.detectValue;
			}
			
			//比较原来内控标准
			String originDown = "";
			String originDownSymbol = "";
			String originUp = "";
			String originUpSymbol = "";
			String originDetectvalue = "";
			
//			setUpAndDownBynewStandard(bean.oldStandard);
			UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.oldStandard);
			originDown = initUpAndDonwBean.down;
			originUp = initUpAndDonwBean.up;
			originDownSymbol = initUpAndDonwBean.downSymbol;
			originUpSymbol = initUpAndDonwBean.upSymbol;
			originDetectvalue = initUpAndDonwBean.detectValue;
			
			if(!StringsUtil.isEmpty(bean.oldStandard)){//如果是非空才处理
				if(StringsUtil.isEmpty(originDetectvalue)){//描述值为空才去比较上下线
					if(Utils.convertStr2Double(indexUp)>Utils.convertStr2Double(originUp)
							||Utils.convertStr2Double(indexDown)<Utils.convertStr2Double(originDown)){
						isOk = false;
						redIndexList.add(i);
						continue;
					}
				}
			}
			

			for (int j = 0; j < lawStandards.size(); j++) {
				String lawDown = "";
				String lawUp = "";
				String lawDownSymbol = "";
				String lawUpSymbol = "";
				String lawDetectValue = "";
				if(StringsUtil.isEmpty(lawStandards.get(j))){//如果法规值是空的则直接默认该指标通过
					continue;
				}
				if (!lawStandards.get(j).equals("") && lawStandards.get(j) != null) {
//					setUpAndDownBynewStandard(lawStandards.get(j));
					initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(lawStandards.get(j));
					lawDown = initUpAndDonwBean.down;
					lawUp = initUpAndDonwBean.up;
					lawDownSymbol = initUpAndDonwBean.downSymbol;
					lawUpSymbol = initUpAndDonwBean.upSymbol;
					lawDetectValue = initUpAndDonwBean.detectValue;
				}
				if (!lawDetectValue.equals("")) {//如果法值是描述性质的也直接通过
					continue;
				}
				if(Utils.convertStr2Double(indexUp)>Utils.convertStr2Double(lawUp)||Utils.convertStr2Double(indexDown)<Utils.convertStr2Double(lawDown)){
					isOk = false;
					redIndexList.add(i);
					continue;
				}
			}
		}
		TableUtils.setRowBackgroundColor(table, redIndexList, 2);
		return isOk;
	}

	/**
	 * @param techItemRev
	 *            选中的技术标准版本
	 * @param allTechBeans
	 *            所有的Bean对象
	 * @param allItemRevision
	 *            所有的版本对象
	 * @param hasPreRev
	 *            是否有上个版本 根据是否有上个版本对象进行TC的回写
	 */
	@Override
	public void writeBack2Tc(TCComponentItemRevision techItemRev, List<TechStandarTableBean> allTechBeans,
			List<TCComponentItemRevision> allItemRevision, boolean hasPreRev) {

		
		
		if (hasPreRev) {// 有上个版本 从 1开始
			
			// 先写质量技术标准			
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(techItemRev, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(techItemRev);
			}
			for (TechStandarTableBean bean : allTechBeans) {
				try {
					boolean bomExit = false;
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {// 循环
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						String itemID = bomLine.getItem().getProperty("item_id");
						String bomLineName = bomLine.getItemRevision().getProperty("object_name");
						if (bean.itemId.equals(itemID)) {// 在BOM找到对应的行
							bomExit = true;
							
//							setUpAndDownBynewStandard(bean.newStandard);//内控标准
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							bomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_DOWNLINE", initUpAndDonwBean.down);
							bomLine.setProperty("U8_UP_OPERATION", initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//预警值
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							bomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							bomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							
							//指标说明
							bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//检验方法依据
							bomLine.setProperty("U8_testgist", bean.testGis);
							//检测方法
							bomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//备注
							bomLine.setProperty("U8_remark", bean.remark);
							
							
							
							//单位
							bomLine.setProperty("U8_standardunit",bean.unit);
							bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//类型
//							bomLine.getItemRevision().setProperty("u8_category", bean.type);
							
						}
					}
					
					
					if (!bomExit) {// 改bom没在技术标准中找到要先查找再创建
						TCComponentQuery query = null;
						TCComponent[] resultSearch = null;
						if(StringsUtil.isEmpty(bean.unit)){
							query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());	
							resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID" },
									new String[] { bean.itemId});
						}else{
							query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());
							resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID"},
									new String[] {  bean.itemId});
						}
						
						
						if(resultSearch==null || resultSearch.length<=0){//创建
							
							TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
							TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
							
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
//							setUpAndDownBynewStandard(bean.newStandard);//内控标准
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//预警值
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							
							
							//指标说明
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//检验方法依据
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//检测方法
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//备注
							addBomLine.setProperty("U8_remark", bean.remark);
							//单位
							addBomLine.setProperty("U8_standardunit",bean.unit);
							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							
							//检测方法
							createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
							
							//UsedInLaw
							addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
							
							//类型
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							
						}else {//查找到了
							TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
//							setUpAndDownBynewStandard(bean.newStandard);//内控标准
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//预警值
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
					
							
							//指标说明
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//检验方法依据
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//检测方法
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//备注
							addBomLine.setProperty("U8_remark", bean.remark);
							//单位
							addBomLine.setProperty("U8_standardunit",bean.unit);
//							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//类型
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
						}
						
					}
					
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
			
			//保存标准
			try {
				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				topBomLine.refresh();
				bomWindow.save();
				bomWindow.close();
			} catch (TCException e2) {
				e2.printStackTrace();
			}
			
			
			

			// 在写法规
			for (int i = 1; i < allItemRevision.size(); i++) {

				if(!ItemUtil.isModifiable(allItemRevision.get(i))){
					continue;
				}
				
				topBomLine = BomUtil.getTopBomLine(allItemRevision.get(i), Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(allItemRevision.get(i));
				}
				
				for(TechStandarTableBean bean : allTechBeans){
					//如果这个Bean在Table中在对应法规位置没有值 就跳过该法规 去下一个法规中看下
					if(StringsUtil.isEmpty(bean.lawStandards.get(i-1))){
						continue;
					}
					
//					setUpAndDownBynewStandard(bean.lawStandards.get(i-1));
					UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.lawStandards.get(i-1));
					
					try {
						boolean bomExit = false;
						AIFComponentContext[] children = topBomLine.getChildren();
						for (AIFComponentContext context : children) {// 循环
							TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
							String itemID = bomLine.getItem().getProperty("item_id");
							String bomLineName = bomLine.getItemRevision().getProperty("object_name");
							if (bean.itemId.equals(itemID)) {
								bomExit = true;
								bomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								bomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								bomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								bomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								bomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//指标说明
								bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//检验方法依据
								bomLine.setProperty("U8_testgist", bean.testGis);
								//检测方法
								bomLine.setProperty("U8_testcriterion", bean.currentMethod);
								//单位
								bomLine.setProperty("U8_standardunit",bean.unit);
								bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//类型
//								bomLine.getItemRevision().setProperty("u8_category", bean.type);
								break;
							}
						}
						
						if (!bomExit) {// 改bom没在法规中找到要查找或者创建
							TCComponentQuery query = null;
							TCComponent[] resultSearch = null;
							if(StringsUtil.isEmpty(bean.unit)){
								query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());	
								resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID" },
										new String[] { bean.itemId});
							}else{
								query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());
								resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID"},
										new String[] {  bean.itemId});
							}
							
							
							if(resultSearch==null || resultSearch.length<=0){//创建
								TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
								TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
								
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//指标说明
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//检验方法依据
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//检测方法
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//单位
								addBomLine.setProperty("U8_standardunit",bean.unit);
								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								
								//检测方法
								createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
								
								//UsedInLaw
								addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
								//类型
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
								
							}else {//查找到了
								TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								//指标说明
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//检验方法依据
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//检测方法
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//单位
								addBomLine.setProperty("U8_standardunit",bean.unit);
//								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//类型
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							}
							
							topBomLine.refresh();
							
						}
					} catch (TCException e) {
						e.printStackTrace();
					}
					
				}
				
				//保存法规
				try {
					TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
					topBomLine.refresh();
					bomWindow.save();
					bomWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
				
				
			}
			
			
			
			MessageBox.post("OK", "", MessageBox.INFORMATION);
		} else {// 没有上个版本 从0开始
			
			// 先写质量技术标准
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(techItemRev, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(techItemRev);
			}
			
			for (TechStandarTableBean bean : allTechBeans) {
				try {
					boolean bomExit = false;
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {// 循环
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						String itemID = bomLine.getItem().getProperty("item_id");
						String bomLineName = bomLine.getItemRevision().getProperty("object_name");
						if (bean.itemId.equals(itemID)) {// 在BOM找到对应的行
							bomExit = true;
							
//							setUpAndDownBynewStandard(bean.newStandard);//内控标准
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							bomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_DOWNLINE",  initUpAndDonwBean.down);
							bomLine.setProperty("U8_UP_OPERATION",  initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_detectvalue",  initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//预警值
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							bomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							bomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							//指标说明
							bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//检验方法依据
							bomLine.setProperty("U8_testgist", bean.testGis);
							//检测方法
							bomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//备注
							bomLine.setProperty("U8_remark", bean.remark);
							
							//单位
							bomLine.setProperty("U8_standardunit",bean.unit);
							bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//类型
//							bomLine.getItemRevision().setProperty("u8_category", bean.type);
							break;
						}
					}
					
					
					if (!bomExit) {// 改bom没在技术标准中找到要创建
						TCComponentQuery query = null;
						TCComponent[] resultSearch = null;
						if(StringsUtil.isEmpty(bean.unit)){
							query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());	
							resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID" },
									new String[] { bean.itemId});
						}else{
							query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());
							resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID"},
									new String[] {  bean.itemId});
						}
						
						
						if(resultSearch==null || resultSearch.length<=0){//创建
							TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
							TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
							
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
							
//							setUpAndDownBynewStandard(bean.newStandard);//内控标准
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE",  initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION",  initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue",  initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//预警值
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							//指标说明
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//检验方法依据
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//检测方法
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//备注
							addBomLine.setProperty("U8_remark", bean.remark);
							
							//单位
							addBomLine.setProperty("U8_standardunit",bean.unit);
							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							
							//检测方法
							createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
							
							//UsedInLaw
							addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
							//类型
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							
						}else {//找到了
							TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
//							setUpAndDownBynewStandard(bean.newStandard);//内控标准
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE",  initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION",  initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue",  initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//预警值
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							//指标说明
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//检验方法依据
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//检测方法
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//备注
							addBomLine.setProperty("U8_remark", bean.remark);
							
							//单位
							addBomLine.setProperty("U8_standardunit",bean.unit);
//							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//类型
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
						}
					}
					
					
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
			
			//保存质量技术标准
			try {
				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				topBomLine.refresh();
				bomWindow.save();
				bomWindow.close();
			} catch (TCException e) {
				e.printStackTrace();
			}
			
			
			try {
				//在技术标准的版本下的BOM版本下的描述属性中写一个属性
				TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(techItemRev);
				String indexType = techItemRev.getProperty("u8_techstandardtype");
				
				if(Const.IndexType.MATERIAL_STANDARD.equals(indexType)){//原料技术标准
					bomRevByItemRev.setProperty("object_desc", Const.BomViewType.MATERIAL_STANDARD);
				}else if(Const.IndexType.PRODUCT_STANDARD.equals(indexType)){//产品技术标准
					bomRevByItemRev.setProperty("object_desc", Const.BomViewType.PRODUCT_STANDARD);
					
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			
			
			
			// 在写法规
			for (int i = 0; i < allItemRevision.size(); i++) {
				
				//没有权限 跳过看下一个
				if(!ItemUtil.isModifiable(allItemRevision.get(i))){
					continue;
				}
				
				topBomLine = BomUtil.getTopBomLine(allItemRevision.get(i), Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(allItemRevision.get(i));
				}
				
				for(TechStandarTableBean bean : allTechBeans){
					//如果这个Bean在Table中在对应法规位置没有值 就跳过该法规 去下一个法规中看下
					if(StringsUtil.isEmpty(bean.lawStandards.get(i))){
						continue;
					}
//					setUpAndDownBynewStandard(bean.lawStandards.get(i));
					UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.lawStandards.get(i));
					
					try {
						boolean bomExit = false;
						AIFComponentContext[] children = topBomLine.getChildren();
						for (AIFComponentContext context : children) {// 循环
							TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
							String itemID = bomLine.getItem().getProperty("item_id");
							String bomLineName = bomLine.getItemRevision().getProperty("object_name");
							if (bean.itemId.equals(itemID)) {
								bomExit = true;
								bomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								bomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								bomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								bomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								bomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//指标说明
								bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//检验方法依据
								bomLine.setProperty("U8_testgist", bean.testGis);
								//检测方法
								bomLine.setProperty("U8_testcriterion", bean.currentMethod);
								//单位
								bomLine.setProperty("U8_standardunit",bean.unit);
								bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//类型
//								bomLine.getItemRevision().setProperty("u8_category", bean.type);
								break;
							}
						}
						
						if (!bomExit) {// 改bom没在法规中
							TCComponentQuery query = null;
							TCComponent[] resultSearch = null;
							if(StringsUtil.isEmpty(bean.unit)){
								query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());	
								resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID" },
										new String[] { bean.itemId});
							}else{
								query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());
								resultSearch = QueryUtil.getSearchResult(query, new String[] { "ID"},
										new String[] {  bean.itemId});
							}
							
							
							if(resultSearch==null || resultSearch.length<=0){//创建
								TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
								TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
								
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//指标说明
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//检验方法依据
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//检测方法
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//单位
								addBomLine.setProperty("U8_standardunit",bean.unit);
								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								
								//检测方法
								createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
								
								//UsedInLaw
								addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
								//类型
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
								
							}else {//找到了
								TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//指标说明
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//检验方法依据
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//检测方法
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//单位
								addBomLine.setProperty("U8_standardunit",bean.unit);
//								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//类型
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							}
						}
					} catch (TCException e) {
						e.printStackTrace();
					}
				}
				
				//保存法规
				try {
					TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
					topBomLine.refresh();
					bomWindow.save();
					bomWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
				
			}
			
			MessageBox.post("OK", "", MessageBox.INFORMATION);
		}
		
		
		
		
	}


	/**
	 * @param str
	 * @return 判断字符串是否为空
	 */
	public boolean strIsEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * 从选中的技术标准中获取BOM信息，跟新到tableBean中去
	 */
	@Override
	public void getNewStatdard(TCComponentItemRevision itemRev, List<TechStandarTableBean> techTableBeanList) {
		for(int i=0;i<techTableBeanList.size();i++){
			
			String techStandardType = "";
			String unit = "";
			try {
				techStandardType = itemRev.getProperty("u8_techstandardtype");
			} catch (TCException e) {
				e.printStackTrace();
			}
			
			TechStandarTableBean bean = techTableBeanList.get(i);
			String name =bean.name;
//			bean.type = techStandardType;
			
			TCComponentItemRevision itemRevision = itemRev;
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){//需要创建一个空的视图
				 topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
			}
			try {
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					String bomLineName = bomLine.getItemRevision().getProperty("object_name");
					if (name.equals(bomLineName)) {
						TechUpDownProperty upDownBean = null;
						try {
							upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
//							unit = bomLine.getProperty("U8_standardunit");
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
//						bean.unit = unit;
						
						bean.newStandard =TechStandardUtil.initResult(upDownBean.ICS_UP, upDownBean.ICS_DOWN,
								upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;// 原内控标准
						
						bean.newWaring = TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
								upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// 原预警值
						
						
					}
				}

			} catch (TCException e) {
				e.printStackTrace();
			}
		}
	}



	/* (non-Javadoc)
	 * 从选中的技术标准中获取所有的指标项目  只有属性名称 和 单位 和 类型 
	 */
	@Override
	public List<TechStandarTableBean> getIndexFormSelectedIndexRev(
			TCComponentItemRevision itemRevision,int lawsNum) {
		
		List<TechStandarTableBean> beans = new ArrayList<TechStandarTableBean>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision,  Const.TechStandarModify.BOMNAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
			return beans;
		}
		try {
			AIFComponentContext[] bomChildrens = topBomLine.getChildren();
			for(int i=0;i<bomChildrens.length;i++){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) bomChildrens[i].getComponent();
				TechStandarTableBean bean = new TechStandarTableBean();
				bean.name = bomLine.getItemRevision().getProperty("object_name");
				bean.itemId = bomLine.getItem().getProperty("item_id");
				bean.type = bomLine.getItemRevision().getProperty("u8_category");
				bean.unit = bomLine.getItemRevision().getProperty("u8_uom");
				
				
				bean.indexIntroduceString = bomLine.getProperty("U8_indexdesc");//指标说明
				bean.currentMethod = bomLine.getProperty("U8_testcriterion");//检测方法
				bean.testGis = bomLine.getProperty("U8_testgist");//检测方法依据
				bean.remark = bomLine.getProperty("U8_remark"); //备注
				
				TechUpDownProperty upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
				bean.newStandard =TechStandardUtil.initResult(upDownBean.ICS_UP, upDownBean.ICS_DOWN,
						upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;// 原内控标准
				bean.newWaring =TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
						upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// 原预警值
				
				bean.oldStandard = "";
				bean.oldWaring = "";

				
				//获取 检测方法 并且去除哪些是空的
				bean.allMethodsList = new ArrayList<String>();
				String[] methodSplit = bomLine.getItemRevision().getProperty("u8_testmethod2").split(",");
				for(String method : methodSplit){
					if(StringsUtil.isEmpty(method)||" ".equals(method)){
						continue;
					}
					bean.allMethodsList.add(method);
				}
				
				
					
				bean.lawStandards = new ArrayList<>();
				for(int j=0;j<lawsNum;j++){
					bean.lawStandards.add("");
				}
				
				beans.add(bean);
				
			}
			
			return beans;
			
			
		} catch (TCException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return beans;
	}
	
	
	

}
