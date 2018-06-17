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
	 * ���� ָ�������ָ������ƻ�ȡָ��汾
	 */
	@Override
	public List<TCComponentItemRevision> getSearchIndexItemRevsionList(
			String indexType, String indexName) {
		
		List<TCComponentItemRevision> searchRevsList = new ArrayList<>();
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEXITEM_USEDINLAW.getValue());
		if (query == null){
			MessageBox.post("ָ���ѯ����ȡʧ��","",MessageBox.ERROR);
			return null;
		}
		if(StringsUtil.isEmpty(indexName))//nameΪ��
			indexName ="*";
		
		TCComponent[] searchResult = null;
		
		if(StringsUtil.isEmpty(indexType)){//����Ϊ��
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
	 * (non-Javadoc) ����itemId��ȡ��Ӧ�ķ���汾
	 */
//	@Override
//	public List<TCComponentItemRevision> getSearchLawItemRevisionList(String itemId,String name) {
//		List<TCComponentItemRevision> searchRevsList = new ArrayList<>();
//		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
//		if (query == null){
//			MessageBox.post("�����ѯ����ȡʧ��","",MessageBox.ERROR);
//			return null;
//		}
//
//		if(StringsUtil.isEmpty(itemId))//idΪ��	
//			itemId = "*";
//		if(StringsUtil.isEmpty(name))//nameΪ��
//			name ="*";
//		if(StringsUtil.isEmpty(itemId)&&StringsUtil.isEmpty(name)){//��Ϊ��
//			MessageBox.post("","�벹����������",MessageBox.INFORMATION);
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
	 * (non-Javadoc) ��ȡ���е���Ŀ������
	 */
	@Override
	public Set<String> getAllIndexItemNames(TCComponentItemRevision revision,
			List<TCComponentItemRevision> selectLawList) {
		Set<String> strSet = new HashSet<>();
		String name = "";
		TCComponentBOMLine topBomLine = null;
		try {
			// ѡ�е�����������׼
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
			// ����ѡ�еķ��漯��
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
	 * (non-Javadoc) ���ݰ汾�Ż�ȡ��һ���汾�ŵ�����������׼�İ汾
	 */
	@Override
	public TCComponentItemRevision getOriginRev(String revNum, String revItemID) {
		int num = Integer.valueOf(revNum.charAt(0));
		if (num == 65) {// ѡ�еİ汾��A�汾 �Ѿ�����Ͱ汾��
			return null;
		}
		int originNum = num - 1;
		String originNumStr = (char) originNum + "";

		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_INDEX.getValue());
		if (query == null) {
			MessageBox.post("��ѯ����ȡ��ȡʧ��", "", MessageBox.ERROR);
			return null;
		}
		TCComponent[] searchResult = QueryUtil.getSearchResult(query,
				new String[] { Const.TechStandarModify.QUERY_ITME_ID, Const.TechStandarModify.QUERY_REVISION_ID },
				new String[] { revItemID, originNumStr });

		TCComponentItemRevision itemRev = (TCComponentItemRevision) searchResult[0];
		return itemRev;
	}

	/*
	 * (non-Javadoc) ���ݺϲ��İ汾����ͨ�����ƽ��й�������ȡ���е���Table�ж�Ӧ��Bean����
	 */
	@Override
	public List<TechStandarTableBean> getAllTableBeans(List<TCComponentItemRevision> allRevs,
			boolean hasPreRev,List<TechStandarTableBean> indexBeanList) {
		
		List<TechStandarTableBean> allTableBeans = new ArrayList<>();
		if (hasPreRev) {// ��ǰһ���汾
			
			//�ȱ�����һ���汾�ļ�����׼
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
						if(itemId.equals(bean.itemId)){//�����һ���汾�д��ں�ѡ�а汾�е�ָ��һ�� �Ͳ������´���bean�� ������Ҫ���� �ϵ��ڿر�׼
							TechUpDownProperty upDownBean = null;
							try {
								upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
							bean.oldStandard = TechStandardUtil.initResult(upDownBean.ICS_UP, upDownBean.ICS_DOWN,
									upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;;// ԭ�ڿر�׼
							
							bean.oldWaring = TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
									upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// ԭԤ��ֵ
							
							//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
					if(!isSameFlag){//�ж�Ϊ�� isSame ��false ˵������Ŀû�д��� Ҫ�½�
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
								upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;// ԭ�ڿر�׼
						
						bean.oldWaring = TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
								upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// ԭԤ��ֵ
						
						
						//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
			
			//�ٱ�������ķ���
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
						
						String relatedSystemId = bomLine.getProperty("U8_AssociationID");//������ϵid
						
						if(!StringsUtil.isEmpty(relatedSystemId)){
							continue ;//����й�����ϵID ˵�������Ŀ������ΪBean
						}
						boolean isSameFlag = false;
						for(TechStandarTableBean bean : indexBeanList){
							if(itemId.equals(bean.itemId)){//�����һ���汾�д��ں�ѡ�а汾�е�ָ��һ�� �Ͳ������´���bean�� ������Ҫ���� �ϵ��ڿر�׼
								TechUpDownProperty upDownBean = null;
								try {
									upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
								} catch (InstantiationException | IllegalAccessException e) {
									e.printStackTrace();
								}
								String lawStandard= TechStandardUtil.initResult(upDownBean.GB_UP, upDownBean.GB_DOWN,
										upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// ����
								
								
								//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
						if(!isSameFlag){//�ж�Ϊ�� isSame ��false ˵������Ŀû�д��� Ҫ�½�
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
									upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// ����
							
							//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
			
		} else {// û������������׼֮ǰ�İ汾
			//�ٱ�������ķ���
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
							//���в���
							if(itemId.equals(bean.itemId)){//�����һ���汾�д��ں�ѡ�а汾�е�ָ��һ�� �Ͳ������´���bean�� ������Ҫ���� �ϵ��ڿر�׼
								TechUpDownProperty upDownBean = null;
								try {
									upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
								} catch (InstantiationException | IllegalAccessException e) {
									e.printStackTrace();
								}
								String lawStandard= TechStandardUtil.initResult(upDownBean.GB_UP, upDownBean.GB_DOWN,
										upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// ����
								
								//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
						//�����е������ڵĲ���
						if(!isSameFlag){//�ж�Ϊ�� isSame ��false ˵������Ŀû�д��� Ҫ�½�
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
									upDownBean.GB_UP_SYMBOL, upDownBean.GB_DOWN, upDownBean.detectValue).resultStr;// ����
							
							//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
	 * (non-Javadoc) ��֤��׼�Ƿ����
	 */
	@Override
	public boolean vertifyStandardIsOk(List<TechStandarTableBean> allTechTableBean, JTable table) {
		boolean isOk = true;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		List<Integer> redIndexList = new ArrayList<>();
		// ����ALLBean�ж��Ƿ�Ϲ�
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
			
			//�Ƚ�ԭ���ڿر�׼
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
			
			if(!StringsUtil.isEmpty(bean.oldStandard)){//����ǷǿղŴ���
				if(StringsUtil.isEmpty(originDetectvalue)){//����ֵΪ�ղ�ȥ�Ƚ�������
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
				if(StringsUtil.isEmpty(lawStandards.get(j))){//�������ֵ�ǿյ���ֱ��Ĭ�ϸ�ָ��ͨ��
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
				if (!lawDetectValue.equals("")) {//�����ֵ���������ʵ�Ҳֱ��ͨ��
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
	 *            ѡ�еļ�����׼�汾
	 * @param allTechBeans
	 *            ���е�Bean����
	 * @param allItemRevision
	 *            ���еİ汾����
	 * @param hasPreRev
	 *            �Ƿ����ϸ��汾 �����Ƿ����ϸ��汾�������TC�Ļ�д
	 */
	@Override
	public void writeBack2Tc(TCComponentItemRevision techItemRev, List<TechStandarTableBean> allTechBeans,
			List<TCComponentItemRevision> allItemRevision, boolean hasPreRev) {

		
		
		if (hasPreRev) {// ���ϸ��汾 �� 1��ʼ
			
			// ��д����������׼			
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(techItemRev, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(techItemRev);
			}
			for (TechStandarTableBean bean : allTechBeans) {
				try {
					boolean bomExit = false;
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {// ѭ��
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						String itemID = bomLine.getItem().getProperty("item_id");
						String bomLineName = bomLine.getItemRevision().getProperty("object_name");
						if (bean.itemId.equals(itemID)) {// ��BOM�ҵ���Ӧ����
							bomExit = true;
							
//							setUpAndDownBynewStandard(bean.newStandard);//�ڿر�׼
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							bomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_DOWNLINE", initUpAndDonwBean.down);
							bomLine.setProperty("U8_UP_OPERATION", initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//Ԥ��ֵ
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							bomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							bomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							
							//ָ��˵��
							bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//���鷽������
							bomLine.setProperty("U8_testgist", bean.testGis);
							//��ⷽ��
							bomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//��ע
							bomLine.setProperty("U8_remark", bean.remark);
							
							
							
							//��λ
							bomLine.setProperty("U8_standardunit",bean.unit);
							bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//����
//							bomLine.getItemRevision().setProperty("u8_category", bean.type);
							
						}
					}
					
					
					if (!bomExit) {// ��bomû�ڼ�����׼���ҵ�Ҫ�Ȳ����ٴ���
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
						
						
						if(resultSearch==null || resultSearch.length<=0){//����
							
							TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
							TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
							
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
//							setUpAndDownBynewStandard(bean.newStandard);//�ڿر�׼
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//Ԥ��ֵ
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							
							
							//ָ��˵��
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//���鷽������
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//��ⷽ��
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//��ע
							addBomLine.setProperty("U8_remark", bean.remark);
							//��λ
							addBomLine.setProperty("U8_standardunit",bean.unit);
							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							
							//��ⷽ��
							createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
							
							//UsedInLaw
							addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
							
							//����
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							
						}else {//���ҵ���
							TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
//							setUpAndDownBynewStandard(bean.newStandard);//�ڿر�׼
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//Ԥ��ֵ
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
					
							
							//ָ��˵��
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//���鷽������
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//��ⷽ��
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//��ע
							addBomLine.setProperty("U8_remark", bean.remark);
							//��λ
							addBomLine.setProperty("U8_standardunit",bean.unit);
//							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//����
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
						}
						
					}
					
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
			
			//�����׼
			try {
				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				topBomLine.refresh();
				bomWindow.save();
				bomWindow.close();
			} catch (TCException e2) {
				e2.printStackTrace();
			}
			
			
			

			// ��д����
			for (int i = 1; i < allItemRevision.size(); i++) {

				if(!ItemUtil.isModifiable(allItemRevision.get(i))){
					continue;
				}
				
				topBomLine = BomUtil.getTopBomLine(allItemRevision.get(i), Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(allItemRevision.get(i));
				}
				
				for(TechStandarTableBean bean : allTechBeans){
					//������Bean��Table���ڶ�Ӧ����λ��û��ֵ �������÷��� ȥ��һ�������п���
					if(StringsUtil.isEmpty(bean.lawStandards.get(i-1))){
						continue;
					}
					
//					setUpAndDownBynewStandard(bean.lawStandards.get(i-1));
					UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.lawStandards.get(i-1));
					
					try {
						boolean bomExit = false;
						AIFComponentContext[] children = topBomLine.getChildren();
						for (AIFComponentContext context : children) {// ѭ��
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
								
								//ָ��˵��
								bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//���鷽������
								bomLine.setProperty("U8_testgist", bean.testGis);
								//��ⷽ��
								bomLine.setProperty("U8_testcriterion", bean.currentMethod);
								//��λ
								bomLine.setProperty("U8_standardunit",bean.unit);
								bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//����
//								bomLine.getItemRevision().setProperty("u8_category", bean.type);
								break;
							}
						}
						
						if (!bomExit) {// ��bomû�ڷ������ҵ�Ҫ���һ��ߴ���
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
							
							
							if(resultSearch==null || resultSearch.length<=0){//����
								TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
								TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
								
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//ָ��˵��
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//���鷽������
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//��ⷽ��
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//��λ
								addBomLine.setProperty("U8_standardunit",bean.unit);
								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								
								//��ⷽ��
								createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
								
								//UsedInLaw
								addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
								//����
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
								
							}else {//���ҵ���
								TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								//ָ��˵��
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//���鷽������
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//��ⷽ��
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//��λ
								addBomLine.setProperty("U8_standardunit",bean.unit);
//								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//����
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							}
							
							topBomLine.refresh();
							
						}
					} catch (TCException e) {
						e.printStackTrace();
					}
					
				}
				
				//���淨��
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
		} else {// û���ϸ��汾 ��0��ʼ
			
			// ��д����������׼
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(techItemRev, Const.TechStandarModify.BOMNAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(techItemRev);
			}
			
			for (TechStandarTableBean bean : allTechBeans) {
				try {
					boolean bomExit = false;
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {// ѭ��
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
						String itemID = bomLine.getItem().getProperty("item_id");
						String bomLineName = bomLine.getItemRevision().getProperty("object_name");
						if (bean.itemId.equals(itemID)) {// ��BOM�ҵ���Ӧ����
							bomExit = true;
							
//							setUpAndDownBynewStandard(bean.newStandard);//�ڿر�׼
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							bomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_DOWNLINE",  initUpAndDonwBean.down);
							bomLine.setProperty("U8_UP_OPERATION",  initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_detectvalue",  initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//Ԥ��ֵ
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							bomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							bomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							bomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							bomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							bomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							//ָ��˵��
							bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//���鷽������
							bomLine.setProperty("U8_testgist", bean.testGis);
							//��ⷽ��
							bomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//��ע
							bomLine.setProperty("U8_remark", bean.remark);
							
							//��λ
							bomLine.setProperty("U8_standardunit",bean.unit);
							bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//����
//							bomLine.getItemRevision().setProperty("u8_category", bean.type);
							break;
						}
					}
					
					
					if (!bomExit) {// ��bomû�ڼ�����׼���ҵ�Ҫ����
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
						
						
						if(resultSearch==null || resultSearch.length<=0){//����
							TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
							TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
							
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
							
//							setUpAndDownBynewStandard(bean.newStandard);//�ڿر�׼
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE",  initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION",  initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue",  initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//Ԥ��ֵ
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							//ָ��˵��
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//���鷽������
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//��ⷽ��
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//��ע
							addBomLine.setProperty("U8_remark", bean.remark);
							
							//��λ
							addBomLine.setProperty("U8_standardunit",bean.unit);
							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							
							//��ⷽ��
							createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
							
							//UsedInLaw
							addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
							//����
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							
						}else {//�ҵ���
							TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
							TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
									null, false);
							
//							setUpAndDownBynewStandard(bean.newStandard);//�ڿر�׼
							UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newStandard);
							addBomLine.setProperty("U8_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_DOWNLINE",  initUpAndDonwBean.down);
							addBomLine.setProperty("U8_UP_OPERATION",  initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_DOWN_OPERATION", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_detectvalue",  initUpAndDonwBean.detectValue);
							
//							setUpAndDownBynewStandard(bean.newWaring);//Ԥ��ֵ
							initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.newWaring);
							addBomLine.setProperty("U8_EARLYWARN_UPLINE", initUpAndDonwBean.up);
							addBomLine.setProperty("U8_EARLYWARNDOWNLINE", initUpAndDonwBean.down);
							addBomLine.setProperty("U8_EARLYWARNUP_OPT", initUpAndDonwBean.upSymbol);
							addBomLine.setProperty("U8_EARLYWARNDOWNOPT", initUpAndDonwBean.downSymbol);
							addBomLine.setProperty("U8_EARLYWARNDESC", initUpAndDonwBean.detectValue);
							
							//ָ��˵��
							addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
							//���鷽������
							addBomLine.setProperty("U8_testgist", bean.testGis);
							//��ⷽ��
							addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
							//��ע
							addBomLine.setProperty("U8_remark", bean.remark);
							
							//��λ
							addBomLine.setProperty("U8_standardunit",bean.unit);
//							addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
							//����
//							addBomLine.getItemRevision().setProperty("u8_category", bean.type);
						}
					}
					
					
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
			
			//��������������׼
			try {
				TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
				topBomLine.refresh();
				bomWindow.save();
				bomWindow.close();
			} catch (TCException e) {
				e.printStackTrace();
			}
			
			
			try {
				//�ڼ�����׼�İ汾�µ�BOM�汾�µ�����������дһ������
				TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(techItemRev);
				String indexType = techItemRev.getProperty("u8_techstandardtype");
				
				if(Const.IndexType.MATERIAL_STANDARD.equals(indexType)){//ԭ�ϼ�����׼
					bomRevByItemRev.setProperty("object_desc", Const.BomViewType.MATERIAL_STANDARD);
				}else if(Const.IndexType.PRODUCT_STANDARD.equals(indexType)){//��Ʒ������׼
					bomRevByItemRev.setProperty("object_desc", Const.BomViewType.PRODUCT_STANDARD);
					
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			
			
			
			// ��д����
			for (int i = 0; i < allItemRevision.size(); i++) {
				
				//û��Ȩ�� ��������һ��
				if(!ItemUtil.isModifiable(allItemRevision.get(i))){
					continue;
				}
				
				topBomLine = BomUtil.getTopBomLine(allItemRevision.get(i), Const.TechStandarModify.BOMNAME);
				if(topBomLine==null){
					topBomLine = BomUtil.setBOMViewForItemRev(allItemRevision.get(i));
				}
				
				for(TechStandarTableBean bean : allTechBeans){
					//������Bean��Table���ڶ�Ӧ����λ��û��ֵ �������÷��� ȥ��һ�������п���
					if(StringsUtil.isEmpty(bean.lawStandards.get(i))){
						continue;
					}
//					setUpAndDownBynewStandard(bean.lawStandards.get(i));
					UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(bean.lawStandards.get(i));
					
					try {
						boolean bomExit = false;
						AIFComponentContext[] children = topBomLine.getChildren();
						for (AIFComponentContext context : children) {// ѭ��
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
								
								//ָ��˵��
								bomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//���鷽������
								bomLine.setProperty("U8_testgist", bean.testGis);
								//��ⷽ��
								bomLine.setProperty("U8_testcriterion", bean.currentMethod);
								//��λ
								bomLine.setProperty("U8_standardunit",bean.unit);
								bomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//����
//								bomLine.getItemRevision().setProperty("u8_category", bean.type);
								break;
							}
						}
						
						if (!bomExit) {// ��bomû�ڷ�����
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
							
							
							if(resultSearch==null || resultSearch.length<=0){//����
								TCComponentItem createtItem = ItemUtil.createtItem(Const.ItemType.INDEXITEM_ITEM, bean.name, "UsedInLaw");
								TCComponentItemRevision createtItemRev = createtItem.getLatestItemRevision();
								
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//ָ��˵��
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//���鷽������
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//��ⷽ��
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//��λ
								addBomLine.setProperty("U8_standardunit",bean.unit);
								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								
								//��ⷽ��
								createtItemRev.setProperty("u8_testmethod2",bean.currentMethod);
								
								//UsedInLaw
								addBomLine.getItemRevision().setProperty("object_desc", "UsedInLaw");
								//����
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
								
							}else {//�ҵ���
								TCComponentItemRevision createtItemRev = (TCComponentItemRevision) resultSearch[0];
								TCComponentBOMLine addBomLine = topBomLine.add(createtItemRev.getItem(), createtItemRev,
										null, false);
								addBomLine.setProperty("U8_STAND_UPLINE", initUpAndDonwBean.up);
								addBomLine.setProperty("U8_STAND_DOWNLINE", initUpAndDonwBean.down);
								addBomLine.setProperty("U8_STANDUP_OPERATION", initUpAndDonwBean.upSymbol);
								addBomLine.setProperty("U8_STDDOWN_OPERATION", initUpAndDonwBean.downSymbol);
								addBomLine.setProperty("U8_detectvalue", initUpAndDonwBean.detectValue);
								
								//ָ��˵��
								addBomLine.setProperty("U8_indexdesc", bean.indexIntroduceString);
								//���鷽������
								addBomLine.setProperty("U8_testgist", bean.testGis);
								//��ⷽ��
								addBomLine.setProperty("U8_testcriterion", bean.currentMethod);
								
								//��λ
								addBomLine.setProperty("U8_standardunit",bean.unit);
//								addBomLine.getItemRevision().setProperty("u8_uom", bean.unit);
								//����
//								addBomLine.getItemRevision().setProperty("u8_category", bean.type);
							}
						}
					} catch (TCException e) {
						e.printStackTrace();
					}
				}
				
				//���淨��
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
	 * @return �ж��ַ����Ƿ�Ϊ��
	 */
	public boolean strIsEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}


	/* (non-Javadoc)
	 * ��ѡ�еļ�����׼�л�ȡBOM��Ϣ�����µ�tableBean��ȥ
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
			if(topBomLine==null){//��Ҫ����һ���յ���ͼ
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
								upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;// ԭ�ڿر�׼
						
						bean.newWaring = TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
								upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// ԭԤ��ֵ
						
						
					}
				}

			} catch (TCException e) {
				e.printStackTrace();
			}
		}
	}



	/* (non-Javadoc)
	 * ��ѡ�еļ�����׼�л�ȡ���е�ָ����Ŀ  ֻ���������� �� ��λ �� ���� 
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
				
				
				bean.indexIntroduceString = bomLine.getProperty("U8_indexdesc");//ָ��˵��
				bean.currentMethod = bomLine.getProperty("U8_testcriterion");//��ⷽ��
				bean.testGis = bomLine.getProperty("U8_testgist");//��ⷽ������
				bean.remark = bomLine.getProperty("U8_remark"); //��ע
				
				TechUpDownProperty upDownBean = AnnotationFactory.getInstcnce(TechUpDownProperty.class, bomLine);
				bean.newStandard =TechStandardUtil.initResult(upDownBean.ICS_UP, upDownBean.ICS_DOWN,
						upDownBean.ICS_UP_SYMBOL, upDownBean.ICS_DOWN_SYMBOL, upDownBean.detectValue).resultStr;// ԭ�ڿر�׼
				bean.newWaring =TechStandardUtil.initResult(upDownBean.WARING_UP, upDownBean.WARING_DOWN,
						upDownBean.WARING_UP_SYMBOL, upDownBean.WARING_DOWN_SYMBOL, upDownBean.waring_detectValue).resultStr;// ԭԤ��ֵ
				
				bean.oldStandard = "";
				bean.oldWaring = "";

				
				//��ȡ ��ⷽ�� ����ȥ����Щ�ǿյ�
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
