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
	 * ��ȡ��ѯԭ�ϵĽ������
	 */
	@Override
	public List<TCComponentItemRevision> searchMaterialResult(String name,String type,String supplier) {
		List<TCComponentItemRevision> materialList = new ArrayList<>();
		String value = "*"+name+"*";
		TCComponentQuery materialQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_MP.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(materialQuery, new String[]{"����"}, new String[]{value});
		
		for(TCComponent component : searchResult){
			TCComponentItemRevision revision = (TCComponentItemRevision) component;
			materialList.add(revision);
		}
		return materialList;
	}
	
	/**
	 * @param name
	 * @return ��������ԭ�϶�Ӧ��ʵ����
	 */
	public List<MaterialBean> searchMaterialBeansList(String name){
		List<MaterialBean> materialBeanList = new ArrayList<>();
		String value = "*"+name+"*";
		TCComponentQuery materialQuery = QueryUtil.getTCComponentQuery(QueryClassConst.U8_MATERIAL_REV_MP.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(materialQuery, new String[]{"����"}, new String[]{value});
		
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
	 * @return ��ȡ��Ϊ��ʱ�����䷽��������Ӫ���ɷֱ��
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
//		Logger.fine("׼����ȡ��ʱ�䷽topBomline");
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
//					Logger.fine("��ʱ�䷽��ѯ�õ�");
					itemExit = true;
					break;
				}
			}
			
			if(!itemExit){//itemû�д��� �ʹ���һ��
				//Ϊ�վʹ���
				cacheItem = ItemUtil.createtItem("U8_Formula", "TempFormula", userId);
//				Logger.fine("��ʱ�䷽��ѯ����������һ��TempFormula");
			}else {
			}
			itemRevision = cacheItem.getLatestItemRevision();
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			if (topBomLine == null) {// ���Ϊnull��ҪΪ�汾������ͼ
				topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
//				Logger.fine("��ȡ�õ���ʱ�䷽���topBomLine");
			}

			// ��ȡBOM�ṹ�е�ԭ����
//			Logger.fine("����ʱ�䷽��BOMLine����ɾ��");
			AIFComponentContext[] children = topBomLine.getChildren();
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineChild = (TCComponentBOMLine) context.getComponent();
				bomMaterialBomLineList.add(bomLineChild);
			}

			// �����䷽������ݶ�BOM�ṹ���е���
			for (TCComponentBOMLine materialBomLine : bomMaterialBomLineList) {// �Ƴ�ԭ��
				materialBomLine.cut();
			}
//			Logger.fine("����table�д�Ľṹ��������ʱ�䷽BOM�ṹ��");
			for (TCComponentItemRevision materialItemRev : formulatorItemRevList) {// �����
				topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			}

			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.save();
			bomWindow.close();

			// ���¸��µ�BOM�ṹ�������
//			Logger.fine("����table�д�Ľṹ����д����ʱ�䷽BOM�ṹ��");
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
	 * @return ��ȡ�䷽�����е�ԭ��Bom����
	 */
	public List<TCComponentBOMLine> getMaterialBomList(TCComponentBOMLine topBomLine) {
		// =================��ȡ���е���Ӽ�BOM
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
				queueBom = bomQueue.poll();// ������ֱ�ӳ��� Ȼ�󽫺��ӵ�����Ӽ����͵����
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


		return materialBomList;
	}

	
	
	/**
	 * @param topBomLine
	 * @return ��ȡ�䷽�е�ָ���Bom����
	 */
	public List<IndexItemBean> getIndexBeanList(TCComponentBOMLine topBomLine)  {
		// ===================��ȡ���еĿ���ʹ�õ�indexItem���͵�BOM
		Double sumInventory = 0d;//������¼����䷽�е�����
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		List<TCComponentBOMLine> bomList = new ArrayList<>();//�洢�������е�ԭ�ϵ�˵
		List<MaterialBean> materialBeanList = new ArrayList<>();//�洢�������е�ԭ�϶�Ӧ��ʵ��Bean
		// �Ƚ���һ�������
		try {
			AIFComponentContext[] children = topBomLine.getChildren();//��һ���ԭ��
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				String type = bomLineTemp.getItem().getType();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
					bomList.add(bomLineTemp);
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
					materialBeanList.add(materialBean);
					sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
				}else if(bomLineTemp.getItem().getType().equals("U8_Formula")){//�ǻ��۵Ļ�  bomLineTemp�ǻ���
					Double outPut = StringsUtil.convertStr2Double(bomLineTemp.getItemRevision().getProperty("u8_OutPut"));//������
					outPut =1000d;
					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class,bomLineTemp);//���۵�ʵ����
					sumInventory += StringsUtil.convertStr2Double(baseBean.U8_inventory);
					Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);
					AIFComponentContext[] baseChilds = bomLineTemp.getChildren();
					for(AIFComponentContext baseChildContext : baseChilds){//��������ĵ�һ�㲻���ǲ���Ӫ����
						TCComponentBOMLine materialBomLine = (TCComponentBOMLine) baseChildContext.getComponent();
						bomList.add(materialBomLine);
						MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,materialBomLine);
						materialBean.U8_inventory = (baseInventory/outPut)*StringsUtil.convertStr2Double(materialBean.U8_inventory)+"";//ת��һ������˵
						materialBeanList.add(materialBean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
		}
		// ���� ��ȡ���е�ָ��bom
		for(int i=0;i<bomList.size();i++){
			TCComponentBOMLine bomLine = bomList.get(i);// �������Ԫ����Ӫ����������ͨ��ԭ�� ��������ݶ���Ϊָ��������
			MaterialBean bean = materialBeanList.get(i);
			try {
				AIFComponentContext[] indexChilds = bomLine.getChildren();
				for(AIFComponentContext indexContext : indexChilds){
 					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
 					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class,indexBomLine);
 					
 					if(isNutritionBom(bomLine)){//�����Ӫ�����Ļ� �����ָ���������ߵľ�ֵ
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
		
		
		// ��ʼ��allIndexBomList
		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (IndexItemBean bean : allIndexBeanList) {
			// ȥ�ظ�
			if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
				int index = nameList.indexOf(bean.objectName);
				IndexItemBean exitBean = indexBeanList.get(index);
				
				Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
				Double inventory = Utils.convertStr2Double(bean.U8_inventory);
				if (exitBean.isFirst) {// ��ǹ�֮���ֱ�����
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
		
		//����ÿ��ָ�������   
		for(IndexItemBean bean : indexBeanList){
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/sumInventory*100+"";//�����ܵ�Ͷ�����������˵
		}
		return indexBeanList;
	}

	
	/* (non-Javadoc)
	 * �ɷ���е���Ϊ�����Ƕ������ 
	 *	��������Ļ�����һ������
	 *	�����ָ����ȥ���ظ���
	 */
	@Override
	public List<IndexItemBean> getIndexBeanListFromBaseBom(List<TCComponentBOMLine> baseBomList) {
		
		List<IndexItemBean> allBaseIndexBeansList = new ArrayList<>();
		Double baseSumInventory = 0d;
		for(TCComponentBOMLine topBOmLine : baseBomList){
			try {
				List<MaterialBean> materialBeanList = new ArrayList<>();
				List<TCComponentBOMLine> materialBomList = new ArrayList<>();//���������ܺ���Ӫ���� ����Ӫ�����µ�Ԫ��Ҳ��ֱ����Ϊָ�������д���
				List<IndexItemBean> allIndexBeanList = new ArrayList<>();
				
				Double outPut = StringsUtil.convertStr2Double(topBOmLine.getItemRevision().getProperty("u8_OutPut"));//������
				MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class,topBOmLine);//���۵�ʵ����
				Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);
				baseSumInventory += baseInventory;
				AIFComponentContext[] baseChilds = topBOmLine.getChildren();
				for(AIFComponentContext baseChildContext : baseChilds){//��������ĵ�һ�㲻���ǲ���Ӫ����
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) baseChildContext.getComponent();
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,materialBomLine);
					materialBean.U8_inventory = (baseInventory/outPut)*StringsUtil.convertStr2Double(materialBean.U8_inventory)+"";//ת��һ������˵
					materialBeanList.add(materialBean);
					materialBomList.add(materialBomLine);
				}
				
				// ���� ��ȡ���е�ָ��bom
				for(int i=0;i<materialBomList.size();i++){
					TCComponentBOMLine bomLine = materialBomList.get(i);// �������Ԫ����Ӫ����������ͨ��ԭ�� ��������ݶ���Ϊָ��������
					MaterialBean bean = materialBeanList.get(i);
					try {
						AIFComponentContext[] indexChilds = bomLine.getChildren();
						for(AIFComponentContext indexContext : indexChilds){
		 					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
		 					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class,indexBomLine);
		 					
		 					if(isNutritionBom(bomLine)){//�����Ӫ�����Ļ� �����ָ���������ߵľ�ֵ
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
				
				
				// ��ʼ��allIndexBomList
				List<IndexItemBean> indexBeanList = new ArrayList<>();
				List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
				for (IndexItemBean bean : allIndexBeanList) {
					// ȥ�ظ�
					if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
						int index = nameList.indexOf(bean.objectName);
						IndexItemBean exitBean = indexBeanList.get(index);
						
						Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
						Double inventory = Utils.convertStr2Double(bean.U8_inventory);
						if (exitBean.isFirst) {// ��ǹ�֮���ֱ�����
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
				
				//ÿһ��ָ���ڻ�����������Ͷ������֪����  Ȼ��ȥ���ÿ��ָ���ڻ����е������Ȱٷֱ�
				for(IndexItemBean bean : indexBeanList){
					bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/StringsUtil.convertStr2Double(baseBean.U8_inventory)*100+"";//�����ܵ�Ͷ�����������˵
				}
				allBaseIndexBeansList.addAll(indexBeanList);
			} catch (Exception e) {
			}
		}
		
		//��ʼ�ܵ�ȥ��
		
		
		
		//ȥ�غ��ָ�� Ҫ�����ܵ�Ͷ������������ȵ�
		for(IndexItemBean bean : allBaseIndexBeansList){
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory)/baseSumInventory*100+"";//�����ܵ�Ͷ�����������˵
		}
		return allBaseIndexBeansList; 
	}
	
	
	/* (non-Javadoc)
	 * ��ȡ�䷽�еĸɷ����ֵ�ȥ�ؼ�����ָ��
	 */
	@Override
	public List<IndexItemBean> getIndexBeanListFromDryBom(TCComponentBOMLine topBomLine) {
		// ===================��ȡ���еĿ���ʹ�õ�indexItem���͵�BOM
		Double sumInventory = 0d;// ������¼����䷽�е�����
		List<TCComponentBOMLine> allIndexBomList = new ArrayList<>();
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		List<TCComponentBOMLine> bomList = new ArrayList<>();// �洢�������е�ԭ�ϵ�˵
		List<MaterialBean> materialBeanList = new ArrayList<>();// �洢�������е�ԭ�϶�Ӧ��ʵ��Bean
		// �Ƚ���һ�������
		try {
			AIFComponentContext[] children = topBomLine.getChildren();// ��һ���ԭ��
			for (AIFComponentContext context : children) {
				TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
				String type = bomLineTemp.getItem().getType();
				if (bomLineTemp.getItem().getType().equals("U8_Material")) {// ��ԭ����
					bomList.add(bomLineTemp);
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
					materialBeanList.add(materialBean);
					sumInventory += StringsUtil.convertStr2Double(materialBean.U8_inventory);
				} else if (bomLineTemp.getItem().getType().equals("U8_Formula")) {// �ǻ��۵Ļ�
																					// bomLineTemp�ǻ���
					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);// ���۵�ʵ����
					sumInventory += StringsUtil.convertStr2Double(baseBean.U8_inventory);
					//���۵Ļ�ֻ�ü����䷽����������˵
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
		}
		// ���� ��ȡ���е�ָ��bom
		for (int i = 0; i < bomList.size(); i++) {
			TCComponentBOMLine bomLine = bomList.get(i);// �������Ԫ����Ӫ����������ͨ��ԭ��
														// ��������ݶ���Ϊָ��������
			MaterialBean bean = materialBeanList.get(i);
			try {
				AIFComponentContext[] indexChilds = bomLine.getChildren();
				for (AIFComponentContext indexContext : indexChilds) {
					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBomLine);

					if (isNutritionBom(bomLine)) {// �����Ӫ�����Ļ� �����ָ���������ߵľ�ֵ
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

		// ��ʼ��allIndexBomList
		List<IndexItemBean> indexBeanList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (IndexItemBean bean : allIndexBeanList) {
			// ȥ�ظ�
			if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
				int index = nameList.indexOf(bean.objectName);
				IndexItemBean exitBean = indexBeanList.get(index);

				Double exitInventory = Utils.convertStr2Double(exitBean.U8_inventory);
				Double inventory = Utils.convertStr2Double(bean.U8_inventory);
				if (exitBean.isFirst) {// ��ǹ�֮���ֱ�����
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

		// ����ÿ��ָ�������
		for (IndexItemBean bean : indexBeanList) {
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory) / sumInventory * 100 + "";// �����ܵ�Ͷ�����������˵
		}
		return indexBeanList;
	}
	
	
	/* (non-Javadoc)
	 * ���䷽�е�ʪ���е�ָ��
	 * �ɷ��е�ָ�� �ֱ𾭹�ʪ����� �ɷ����  ��������ĵļ���
	 * �õ������ս�������յľ���ȥ�غ��ָ�꼯��
	 */
	@Override
	public List<IndexItemBean> getFinallIndexBeanList(TCComponentBOMLine cacheTopBomLine,
			TCComponentItemRevision wetLossItemRevsion, TCComponentItemRevision dryLossItemRevsion,
			TCComponentItemRevision dateLossItemRevsion) {
		
		//ʪ����������ָ����Ŀ
		TCComponentBOMLine wetLossTopBomLine = BomUtil.getTopBomLine(wetLossItemRevsion, "��ͼ");
		List<IndexItemBean> wetLossIndexBeanList = getLossIndexBeanList(wetLossTopBomLine);
		//�ɷ���������ָ����Ŀ
		TCComponentBOMLine dryLossTopBomLine = BomUtil.getTopBomLine(dryLossItemRevsion, "��ͼ");
		List<IndexItemBean> dryLossIndexBeanList = getLossIndexBeanList(dryLossTopBomLine);
		//��������������ָ����Ŀ
		TCComponentBOMLine dateLossTopBomLine = BomUtil.getTopBomLine(dateLossItemRevsion, "��ͼ");
		List<IndexItemBean> dateLossIndexBeanList = getLossIndexBeanList(dateLossTopBomLine);
		
		
		
	 
		/**
		 * 1����ȡ�䷽�еĻ��ۣ�ʪ����ģ�
		 * 2������ʪ����������������һ������
		 * 3����ʪ��������ļ�������Ķ����ۼ��ϸɷ���ָ��
		 * 4����3�Ľ��ȥ���ݸɷ�����Ľ��м����һ������
		 * 5����4�Ľ���������е�ָ������ �����Ҫ���ݱ�������Ľ��м����˵
		 * **/
		
		//�䷽�еĵ����л��ۼ�ʪ����ָ�꼯��
		List<TCComponentBOMLine> basePowderBomLineList = getBasePowderBomLine(cacheTopBomLine);
		List<IndexItemBean> baseIndexItemBeanList = getIndexBeanListFromBaseBom(basePowderBomLineList);
		
		
		
		//baseIndexItemBeanList��Ҫ������ʪ��������������һ�׽��
		for(IndexItemBean indexItemBean : baseIndexItemBeanList){
			for(IndexItemBean bean : wetLossIndexBeanList){
				if(indexItemBean.objectName.equals(bean.objectName)){//ƥ�䵽��
					indexItemBean.u8Loss = bean.u8Loss;
					break;
				}
			}
		}
		computeIndexBeanByLoss(baseIndexItemBeanList);	//������ļ����������ָ���Ͷ���������

		//��ȡ�䷽�еĸɷ���������ԭ�ϵ�ָ��
		List<IndexItemBean> dryIndexItemBeanList = getIndexBeanListFromDryBom(cacheTopBomLine);
		
		//��ʪ���Ľ���͸ɷ����ۼӣ�ֻ��Ͷ������
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
		
		//�����䷽�ܵ�Ͷ����������ɷ���ʪ���ϼ��ĸ���ָ���Ͷ���������
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
			bean.bl_quantity = StringsUtil.convertStr2Double(bean.U8_inventory) / sumInventory * 100 + "";// �����ܵ�Ͷ�����������˵
		}
		
		//����ʪ��ָ�꼯�ϼ���ɷ����
		for(IndexItemBean indexItemBean : finallIndexBeanList){
			for(IndexItemBean bean : dryLossIndexBeanList){
				if(indexItemBean.objectName.equals(bean.objectName)){//ƥ�䵽��
					indexItemBean.u8Loss = bean.u8Loss;
					break;
				}
			}
		}
		computeIndexBeanByLoss(finallIndexBeanList);
		
		//����ʪ��ָ�꼯�ϼ��㱣�������
		for(IndexItemBean indexItemBean : finallIndexBeanList){
			for(IndexItemBean bean : dateLossIndexBeanList){
				if(indexItemBean.objectName.equals(bean.objectName)){//ƥ�䵽��
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
	 * ��ת��Ϊʵ���������д�뵽excel��ȥ
	 */
	public void write2Excel(List<TCComponentBOMLine> materialBomList, List<IndexItemBean> indexBeanList) {
		List<FormulatorExcelMaterialBean> excelMaterialBeanList = getExcelMaterialBeanList(materialBomList);
		
		// ======д����
		File outFile = new File(Const.FormulatorModify.EXCEL_PATH);
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
		Sheet sheet = wb.createSheet(Const.FormulatorModify.EXCEL_SHEET1_NAME);//Ӫ���ɷֱ�
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
		cell2.setCellValue("�ڿ�����");
		cell3.setCellValue("�ڿ�����");
		cell4.setCellValue("�ڿر�׼ֵ");
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
		sheet = wb.createSheet(Const.FormulatorModify.EXCEL_SHEET2_NAME);//�䷽�嵥
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


	/**
	 * @param materialBomList
	 * @return ������ת��ΪҪд��excel�е�ʵ���������
	 */
	private List<FormulatorExcelMaterialBean> getExcelMaterialBeanList(List<TCComponentBOMLine> materialBomList) {
		List<FormulatorExcelMaterialBean> formulatorExcelMaterialBeansList = new ArrayList<>();
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (TCComponentBOMLine bomLine : materialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				if ("����".equals(bean.alternate) || "��ϻ���".equals(bean.alternate))
					continue;// ����˵����ֵ�ò���
				List<IndexItemBean> indexItemBeansList = new ArrayList<>();
				// ��ȡԭ���µ�ָ��
				AIFComponentContext[] children = bomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {// ָ��
						IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
						indexItemBeansList.add(indexItemBean);
					}
				}
				// һ��ԭ�ϵ����ݽṹ
				FormulatorExcelMaterialBean formulatorExcelMaterialBean = new FormulatorExcelMaterialBean();
				// ȥ�ظ�
				if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
					int index = nameList.indexOf(bean.objectName);
					MaterialBean exitBean = formulatorExcelMaterialBeansList.get(index).materialBean;
					Double exitInvnetory = 0.0d;
					Double inventory = 0.0d;
					exitInvnetory = Utils.convertStr2Double(exitBean.U8_inventory);
					inventory = Utils.convertStr2Double(bean.U8_inventory);
					exitBean.U8_inventory = (exitInvnetory + inventory) + "";

					// �������IndexItem
					List<IndexItemBean> exitIndexItemBeanList = formulatorExcelMaterialBeansList
							.get(index).indexItemBeanList;
					for (IndexItemBean exitIndexItemBean : exitIndexItemBeanList) {// �µ�
						for (IndexItemBean indexItemBean : indexItemBeansList) {// �ظ���
							if (exitIndexItemBean.isFirst&&exitIndexItemBean.objectName.equals(indexItemBean.objectName)) {// �ǵ�һ��ȥ�ظ�
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
							} else if(!exitIndexItemBean.isFirst&&exitIndexItemBean.objectName.equals(indexItemBean.objectName)){// ���ǵ�һ�ξ�ֱ��
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
	 * �����䷽����ͼ
	 */
	public void createFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList,TCComponentItemRevision lossItemRev) {
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
			//����BOM����������
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
			
			//���㵱ǰ���۵ĳ�����
			Double sumInventory  = 0d;//�ܵĸ����ʼ��������˵
			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
			AIFComponentContext[] childrens = topBomLine.getChildren();
			for(AIFComponentContext context : childrens){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();
				if(isNutritionBom(materialBomLine)){//�����Ӫ����
					Double nutritionLoss = 1-(StringsUtil.convertStr2Double(materialBomLine.getItemRevision().getProperty("u8_Loss"))/100);
					Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
					sumInventory += nutritionLoss*inventory;
				}else {//����һ��������ԭ��
					Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
					
					AIFComponentContext[] indexChildrens = materialBomLine.getChildren();
					for(AIFComponentContext indexContext : indexChildrens){
						TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
						IndexItemBean indexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBomLine);
						if(indexBean.objectName.contains("������")){
							Double quantity = inventory*StringsUtil.convertStr2Double(indexBean.bl_quantity)/100;
							sumInventory += quantity;
						}
					}
				}
			}
			
			TCComponentBOMLine lossTopBomLine = BomUtil.getTopBomLine(lossItemRev, "��ͼ");
			if(lossTopBomLine==null){
				MessageBox.post("������ĵ�BOM�ṹ","",MessageBox.INFORMATION);
				return;
			}
			
			//���㲢д�����ֵ
			Double outPut = 0d;
			AIFComponentContext[] lossChildrens = lossTopBomLine.getChildren();
			for(AIFComponentContext lossIndexContext : lossChildrens){
				TCComponentBOMLine lossBomLine = (TCComponentBOMLine) lossIndexContext.getComponent();
				IndexItemBean lossIndexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, lossBomLine);
				if(lossIndexBean.objectName.contains("������")){//�ҵ������ʵ����
					Double loss = 1-StringsUtil.convertStr2Double(lossIndexBean.u8Loss)/100;
					outPut = sumInventory/loss;
				}
			}
			
			if(0==outPut){
				outPut = sumInventory;
			}
			//д���䷽����
			itemRevision.setProperty("u8_OutPut", outPut+"");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/* (non-Javadoc)
	 * �����ɷ��䷽
	 */
	@Override
	public void createDryFormulatorBOM(TCComponentItemRevision itemRevision,
			List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList) {
		
		try {
			itemRevision.setProperty("object_desc", "PF");
		} catch (TCException e1) {
			e1.printStackTrace();
		}
		
		//�ҵ�ʪ���Ķ��� �����ԭ�϶������䷽��Ҫ����
		for(int i=0;i<formulatorItemRevList.size();i++){
			MaterialBean bean = formulatorTableList.get(i);
			String type = formulatorItemRevList.get(i).getType();
			if("U8_MaterialRevision".equals(type)&&"ʪ��".equals(bean.productMethod)){
				MessageBox.post("�벻Ҫʹ��ʪ��ԭ�ϣ���ѡ�����","",MessageBox.INFORMATION);
				return;
			}
		}
		
		
		//��䷽
		try {
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
			//����BOM����������
			for(int i=0;i<formulatorItemRevList.size();i++){
				TCComponentItemRevision materialItemRev = formulatorItemRevList.get(i);
				MaterialBean addBean  = formulatorTableList.get(i);
				TCComponentBOMLine addBomLine = topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
				AnnotationFactory.setObjectInTC(addBean, addBomLine);
			}
			
			
			//������߼������������ڸɷ����õ��Ļ����е�ԭ�ϵ����ļ���
//			AIFComponentContext[] materialChildren = topBomLine.getChildren();
//			for(AIFComponentContext materialChildContext : materialChildren){
//				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialChildContext.getComponent();
//				TCComponentItemRevision materialItemRev = materialBomLine.getItemRevision(); 
//				
//				if("U8_FormulaRevision".equals(materialItemRev.getType())){//����ǻ���Ҫ�����������֮�µ�ԭ�ϵ�˵
//					List<MaterialBean> wetFormulatorBeanList = new ArrayList<>();
//					TCComponentBOMLine wetTopBomLine = BomUtil.getTopBomLine(materialItemRev, "��ͼ");
//					
//					MaterialBean baseBean = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);//�ڸɷ��䷽�� ���۵�Ͷ����
//					if(wetTopBomLine==null){//���û����ͼ�Ļ�������
//						break;
//					}
//					AIFComponentContext[] wetChildren = wetTopBomLine.getChildren();
//					for(AIFComponentContext wetContext : wetChildren){//�����䷽��
//						TCComponentBOMLine tempBomLine = (TCComponentBOMLine) wetContext.getComponent();
//						MaterialBean wetBean = AnnotationFactory.getInstcnce(MaterialBean.class, tempBomLine);
//						wetFormulatorBeanList.add(wetBean);
//					}
//					
//					//��ȡ�˻������ݣ������и���ԭ�ϵ�Ͷ���� �� ���ݻ��۵�Ͷ����ȥ�����ڸɷ��л����е�ԭ�ϵĸ���Ͷ��
//					Double output = StringsUtil.convertStr2Double(materialItemRev.getProperty("u8_OutPut"));
//					output = 1000d;//Ҫɾ����
//					AIFComponentContext[] dryChildren = materialBomLine.getChildren();
//					for(int j=0;j<dryChildren.length;j++){//�ɷ��еĻ����µ�ԭ��
//						TCComponentBOMLine dryBomLine = (TCComponentBOMLine) dryChildren[j].getComponent();
//						MaterialBean dryBean = wetFormulatorBeanList.get(j);//�����䷽�е�ԭ��
//						
//						//(���۵�Ͷ���� / �����䷽�ĳ����� )* ������ĳԭ�ϵ�Ͷ����
//						Double baseInventory = StringsUtil.convertStr2Double(baseBean.U8_inventory);//���۵�Ͷ����
//						Double dryMaterialInventory = StringsUtil.convertStr2Double(dryBean.U8_inventory);//һ�ݻ����е�һ��ԭ�ϵ�Ͷ����
//						
//						if(0==output){//������Ϊ0˵��������
//							//Ͷ�����ǲ����˵
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
	 * ������ʪ���䷽ �ŵ�home��
	 */
	@Override
	public void createWetFormulatorInHome(List<TCComponentItemRevision> formulatorItemRevList, List<MaterialBean> formulatorTableList, String name,TCComponentItemRevision lossItemRev)  {
			try {
				String osUserName = UserInfoSingleFactory.getInstance().getUser().getOSUserName();
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, 
						new String[]{"����","����","����Ȩ�û�"}, 
						new String[]{"Home","Home �ļ���",
								UserInfoSingleFactory.getInstance().getUser().getOSUserName()});
				if(searchResult.length==0){
					MessageBox.post("����Home�ļ����Ƿ����","",MessageBox.INFORMATION);
					return ;
				}
				
				TCComponentItem  item= ItemUtil.createtItem("U8_Formula", name, "");
				if(item==null){
					MessageBox.post("����ʧ��","",MessageBox.INFORMATION);
					return ;
				}
				TCComponentItemRevision itemRevision = item.getLatestItemRevision();
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
				
				//����BOM����������
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
				
				
				//���㵱ǰ���۵ĳ�����
				Double sumInventory  = 0d;//�ܵĸ����ʼ��������˵
				AIFComponentContext[] childrens = topBomLine.getChildren();
				for(AIFComponentContext context : childrens){
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();
					if(isNutritionBom(materialBomLine)){//�����Ӫ����
						Double nutritionLoss = StringsUtil.convertStr2Double(materialBomLine.getItemRevision().getProperty("u8_Loss"));
						Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
						sumInventory += nutritionLoss*inventory;
					}else {//����һ��������ԭ��
						Double inventory = StringsUtil.convertStr2Double(materialBomLine.getProperty("U8_inventory"));
						
						AIFComponentContext[] indexChildrens = materialBomLine.getChildren();
						for(AIFComponentContext indexContext : indexChildrens){
							TCComponentBOMLine indexBomLine = (TCComponentBOMLine) indexContext.getComponent();
							IndexItemBean indexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBomLine);
							if(indexBean.objectName.contains("������")){
								Double quantity = inventory*StringsUtil.convertStr2Double(indexBean.bl_quantity);
								sumInventory += quantity*inventory;
							}
						}
					}
				}
				
				TCComponentBOMLine lossTopBomLine = BomUtil.getTopBomLine(lossItemRev, "��ͼ");
				if(lossTopBomLine==null){
					MessageBox.post("������ĵ�BOM�ṹ","",MessageBox.INFORMATION);
					return;
				}
				
				//���㲢д�����ֵ
				Double outPut = 0d;
				AIFComponentContext[] lossChildrens = lossTopBomLine.getChildren();
				for(AIFComponentContext lossIndexContext : lossChildrens){
					TCComponentBOMLine lossBomLine = (TCComponentBOMLine) lossIndexContext.getComponent();
					IndexItemBean lossIndexBean = AnnotationFactory.getInstcnce(IndexItemBean.class, lossBomLine);
					if(lossIndexBean.objectName.contains("������")){//�ҵ������ʵ����
						Double loss = StringsUtil.convertStr2Double(lossIndexBean.u8Loss);
						outPut = loss * sumInventory;
					}
				}
				
				//д���䷽����
				itemRevision.setProperty("u8_OutPut", outPut+"");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	
	/* (non-Javadoc)
	 * ����Ӫ�����Ľṹ
	 */
	@Override
	public void updateNutritionStruct(TCComponentItemRevision nutritionItemRev,
			List<TCComponentItemRevision> materialRevList, List<MaterialBean> materialBeanList) {
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(nutritionItemRev,Const.CommonCosnt.BOM_VIEW_NAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(nutritionItemRev);
		}
		try {
			//�����
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				bomLine.cut();
			}
			
			//��Ӳ���д��Ϣ
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
	 * ��ȡ����е�ָ���Bean�ļ���
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
		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (TCComponentBOMLine bomLine : waitMaterialBomList) {
			try {
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				// ȥ�ظ�
				if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
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
				queueBom = bomQueue.poll();// ������ֱ�ӳ��� Ȼ�󽫺��ӵ�����Ӽ����͵����
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
	 * @return ���ݶ�Ӧ��ԭ�ϵ�BOM��ȡ��Ӧ��ָ���Bean
	 */
	@Override
	public List<IndexItemBean> getWaitIndexBeanList(List<TCComponentBOMLine> waitIndexBomList) {
		List<IndexItemBean> waitIndexBeanList = new ArrayList<>();

		List<String> nameList = new ArrayList<>(); // ȥ�ظ��ı������
		for (TCComponentBOMLine bomLine : waitIndexBomList) {
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				// ȥ�ظ�
				if (nameList.contains(bean.objectName)) {// �����Ҫ�ϲ���Bean ����������
					int index = nameList.indexOf(bean.objectName);
					IndexItemBean exitBean = waitIndexBeanList.get(index);
					Double exitUp = StringsUtil.convertStr2Double(exitBean.up);
					Double up = StringsUtil.convertStr2Double(bean.up);
					Double exitDown = StringsUtil.convertStr2Double(exitBean.down);
					Double down = StringsUtil.convertStr2Double(bean.down);
					Double exitQuantity = StringsUtil.convertStr2Double(exitBean.bl_quantity);
					Double quantity = StringsUtil.convertStr2Double(bean.bl_quantity);
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
	 * @return ��ȡ�����е���Ӽ�Bean
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
						if(StringsUtil.isEmpty(bean.relatedSystemId)||"0".equals(bean.relatedSystemId)){//������  ������
						}else{//�����ӵ�
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
	 * @param lawName  ���������
	 * @param bean		ָ�������
	 * @param checkMaterialBeanList  ���ԭ�ϵ�����
	 * 
	 * ƥ�䵽 2760 ����14880�ľ���ԭ��
	 */
	public void getAllLinkMaterialBean(String lawName, MaterialBean bean, List<MaterialBean> checkMaterialBeanList){
		//�������ӵķ����ID�ҵ�����
		String[] splitsLawIds = bean.indicatorRequire.split("#");
		String relatedIds = bean.relatedSystemId;

		for (String lawId : splitsLawIds) {
			if (lawId.startsWith("GB") && lawId.contains("2760") || lawId.contains("14880")) {// ˵������
																								// ��ԭ��
																								// ������2760����14880
				// �����ҵ�����
				TCComponentItemRevision itemRevision = null;
				String lawID = relatedIds + " " + lawId;
				List<TCComponentItemRevision> checkLawRevList = getCheckLawRevList(lawID);
				
				if (checkLawRevList.size() > 0) {
					itemRevision = checkLawRevList.get(0);
				}else{
					continue;//�����һ��Ҫ���ӵķ��� û���ҵ��Ļ���Ҫ����������һ�ε�
				}

				TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.FormulatorCheck.BOMNAME);
				try {
					String lawNameTemp = itemRevision.getProperty("object_name");
					AIFComponentContext[] children = topBomLine.getChildren();
					for (AIFComponentContext context : children) {
						TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
						if (bomLineTemp.getItem().getType().equals("U8_Material")) {
							MaterialBean beanTemp = AnnotationFactory.getInstcnce(MaterialBean.class, bomLineTemp);
							// ֻ��һ��
							if (beanTemp.systemId.equals(bean.relatedSystemId)) {// �����ӵ�
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
	
	/*
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
				AIFComponentContext[] children = topBomLine.getChildren();
				for (AIFComponentContext context : children) {
					TCComponentBOMLine bomLineTemp = (TCComponentBOMLine) context.getComponent();
					if (bomLineTemp.getItem().getType().equals("U8_IndexItem")) {
						IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLineTemp);
						if(StringsUtil.isEmpty(bean.relatedSystemId)||"0".equals(bean.relatedSystemId)){//��   ������
							bean.lawName = lawName;
							checkIndexBeanList.add(bean);
						}else{//�����ӵ�  
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
	 * ���������ӵ�ָ����Ŀ�ҵ���Ӧ�����е�ָ����Ŀ ��ŵ���������ȥ
	 * @param lawName ���������
	 * @param bean  �����ӵ�ָ����Ŀ
	 * @param checkIndexBeanList  ��������÷����ص�ָ����Ŀ
	 */
	private void getAllLinkIndexBean(String lawName, IndexItemBean bean, List<IndexItemBean> checkIndexBeanList) {
		//�������ӵķ����ID�ҵ�����
		String[] splitsLawIds = bean.indicatorRequire.split("#");
		String relatedIds = bean.relatedSystemId;
		
		for(String lawId : splitsLawIds){
			if(lawId.startsWith("GB")&&!lawId.contains("2760")&&!lawId.contains("14880")){//˵������  �ǲ�Ʒ��׼ ������2760����14880
				//�����ҵ�����
				TCComponentItemRevision itemRevision = null;
				String lawID = relatedIds+" "+lawId;
				List<TCComponentItemRevision> checkLawRevList = getCheckLawRevList(lawId);
				if(checkLawRevList.size()>0){
					itemRevision = checkLawRevList.get(0);
				}

				if(itemRevision==null){
					//˵���������û���ҵ���Ӧ�ķ���
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
							//ֻ��һ��
							if(beanTemp.systemId.equals(bean.relatedSystemId)){//�����ӵ�
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
	 * (non-Javadoc) ��ȡ�����Ӽ����Bean
	 */
	@Override
	public List<FormulatorCheckedBean> getMaterialCheckedBean(List<MaterialBean> waitMaterialBeanList,
			List<MaterialBean> checkMaterialBeanList) {
		List<FormulatorCheckedBean> materialCheckedBean = new ArrayList<>();
		for (MaterialBean waitBean : waitMaterialBeanList) {
			for (MaterialBean checkBean : checkMaterialBeanList) {
				if (waitBean.type.equals(checkBean.category) && checkBean.objectName.contains(waitBean.objectName)) {// ��������Ƿ�һ��
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "��Ӽ�";
					checkedBean.formulatorValue = Utils.convertStr2Double(waitBean.U8_inventory)+"";
					checkedBean.lawName = checkBean.lawName;
					checkedBean.lawValue = Utils.convertStr2Double(checkBean.down) + "~" + Utils.convertStr2Double(checkBean.up);
					if (true) {// �������
						Double waitValue = Utils.convertStr2Double(waitBean.U8_inventory);
						Double checkDown = Utils.convertStr2Double(checkBean.down);
						Double checkUp = Utils.convertStr2Double(checkBean.up);
						if (waitValue < checkDown) {// ���޳���
							checkedBean.excessiveDesc = "��������";
							checkedBean.wranings = checkBean.warning;
						}
						if (waitValue > checkUp) {// ���޳���
							checkedBean.excessiveDesc = "��������";
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
	 * @return �������ָ����
	 */
	@Override
	public List<FormulatorCheckedBean> getIndexCheckedBean(List<IndexItemBean> waitIndexBeanList,
			List<IndexItemBean> checkIndexBeanList) {
		List<FormulatorCheckedBean> indexCheckedBean = new ArrayList<>();
		for (IndexItemBean waitBean : waitIndexBeanList) {
			for (IndexItemBean checkBean : checkIndexBeanList) {
				if (waitBean.type.equals(checkBean.category) &&checkBean.objectName.contains( waitBean.objectName)) {// ��������Ƿ�һ��
					FormulatorCheckedBean checkedBean = new FormulatorCheckedBean();
					checkedBean.name = waitBean.objectName;
					checkedBean.category = "ָ��";
					checkedBean.formulatorValue = Utils.convertStr2Double(waitBean.down) + "~" + Utils.convertStr2Double(waitBean.up);
					checkedBean.lawName = checkBean.lawName;
					checkedBean.lawValue = Utils.convertStr2Double(checkBean.down) + "~" + Utils.convertStr2Double(checkBean.up);
					if (true) {// �������
						Double waitDown = Utils.convertStr2Double(waitBean.down);
						Double waitUp = Utils.convertStr2Double(waitBean.up);
						Double checkDown = Utils.convertStr2Double(checkBean.down);
						Double checkUp = Utils.convertStr2Double(checkBean.up);
						if (waitDown > checkDown) {// ���޳���
							checkedBean.excessiveDesc = "���޳���";
							checkedBean.wranings = checkBean.warning;
						}
						if (waitUp > checkUp) {// ���޳���
							checkedBean.excessiveDesc = "���޳���";
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
	 * ��ִ�б�׼(����������׼��˵)
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
	 * ����ѡ�еĸɷ��䷽�İ汾�����������䷽��
	 */
	@Override
	public void createFormulatorExcel(TCComponentItemRevision formulatorRev) {
		
		List<MaterialBean> materialBeanList = new ArrayList<>();//�ɷ��е����ж�����һ��ԭ�Ͽ�
		List<MaterialBean> wetBeanList = new ArrayList<>();//�����е�ԭ�� ����Ӫ����

		
//		//������ģ��
		File file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		if(file.exists()){//���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.MilkPowderFormulator.Formulator_Excel_Name);
		if(dataset==null){
			MessageBox.post("���ݼ�����ʧ��","",MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.MilkPowderFormulator.Template_Dir);
		if(resultStrs.length==0){//����ʧ��
			MessageBox.post("���ݼ�����ʧ��","",MessageBox.INFORMATION);
			return ;
		}
		
		//��ȡ���ݵ�˵
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, "��ͼ");
		if(topBomLine==null){//����ṹΪ�վʹ�������һ���µĿյĸ���ͼ
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			AIFComponentContext[] materialChildrens = topBomLine.getChildren();
			for(AIFComponentContext materialContext : materialChildrens){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialContext.getComponent();
				MaterialBean dryBean  = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);
				materialBeanList.add(dryBean);
				
				if(isWetBom(materialBomLine)){//�����ʪ���䷽��BOM
					AIFComponentContext[] wetChildrens = materialBomLine.getChildren();
					for(AIFComponentContext wetContext : wetChildrens){//ֻ��ʪ���д���Ӫ����
						TCComponentBOMLine wetBomLine = (TCComponentBOMLine) wetContext.getComponent();
						MaterialBean wetBean  = AnnotationFactory.getInstcnce(MaterialBean.class, wetBomLine);
						if(isNutritionBom(wetBomLine)){//�����Ӫ����
							wetBean.isNutrition=true;
						}
						wetBeanList.add(wetBean);
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		//д����
		int wetBeanStart = 14;//���۵���Ϣ
		int dryBeanStart = 23;//�ɻ첿�ֵ���Ϣ
		int mIncrement = 0;//��¼�����˶�����
		File inFile = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet(Const.MilkPowderFormulator.EXCEL_SHEET1);
			
			/*
			 * 1�������۵���Ϣд��excel��(Ӫ�����ĵ�λ��һ��)
			 * 2�����ɻ첿�ֵ���Ϣд��excel��
			 */
			
			
			//������Ϣ ���涼Ӫ����
			for(int i=0;i<wetBeanList.size();i++){
				//�Ⱥϲ���Ԫ��
				CellRangeAddress cra=new CellRangeAddress(wetBeanStart, wetBeanStart, 1, 3);  
				sheet.addMergedRegion(cra); 
				
				//�ϲ���Ԫ���2���ֶ�
			    cra=new CellRangeAddress(wetBeanStart,wetBeanStart, 4, 6);        
			    sheet.addMergedRegion(cra); 
			    
			    MaterialBean bean = wetBeanList.get(i);
				Cell cell = getCell(sheet, wetBeanStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				if(bean.isNutrition){//�����Ӫ����
					cell = getCell(sheet, wetBeanStart, 4);
					cell.setCellValue("һ��");setCellBorder(cell, wb);
				}else{
					cell = getCell(sheet, wetBeanStart, 4);
					cell.setCellValue(bean.U8_inventory);setCellBorder(cell, wb);	
				}
				
				if(i<wetBeanList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
					sheet.shiftRows(wetBeanStart, sheet.getLastRowNum(), 1);
					mIncrement++;//���һ������������1
				}else{
//					mIncrement++;//���һ������������1
				}
			}
//			//��д�ɻ�����е�ԭ����Ϣ
			dryBeanStart = dryBeanStart+mIncrement;//����֮ǰ��������ֵ
			for(int i=0;i<materialBeanList.size();i++){
				//�Ⱥϲ���Ԫ��
				CellRangeAddress cra=new CellRangeAddress(dryBeanStart, dryBeanStart, 1, 3);  
				sheet.addMergedRegion(cra); 
				
				//�ϲ���Ԫ���2���ֶ�
			    cra=new CellRangeAddress(dryBeanStart,dryBeanStart, 4, 6);        
			    sheet.addMergedRegion(cra); 
			    
			    MaterialBean bean = materialBeanList.get(i);
				Cell cell = getCell(sheet, dryBeanStart, 1);
				cell.setCellValue(bean.objectName);setCellBorder(cell, wb);
				
				cell = getCell(sheet, dryBeanStart, 4);
				cell.setCellValue(bean.U8_inventory);setCellBorder(cell, wb);	
				
				if(i<materialBeanList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
					sheet.shiftRows(dryBeanStart, sheet.getLastRowNum(), 1);
					mIncrement++;//���һ������������1
				}else{
					mIncrement++;//���һ������������1
				}
			}
			
			//����
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		//�ϴ�
		file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		if(file.exists()){//���ھ��ϴ�
			 try {
				 TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						 Const.MilkPowderFormulator.Foumulator_Excel_Input_Path,
						 DataSetUtil.DataSetType.MSExcelX, 
						 "excel", 
						 Const.MilkPowderFormulator.Formulator_Excel_Upload_Name);
				 formulatorRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("���ݼ�����ʧ��","",MessageBox.INFORMATION);
				return ;
			}
		 }
		
		
	}

	/* (non-Javadoc)
	 * ����ѡ�еĸɷ��䷽������Ӫ��������Ϣ���
	 */
	@Override
	public void createNutritionExcel(TCComponentItemRevision formulatorRev) {
		
		List<NutritionBean> nutritionBeansList = new ArrayList<>();
	
		//������ģ��
		File file = new File(Const.MilkPowderFormulator.Nutrition_Excel_Input_Path);
		if (file.exists()) {// ���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.MilkPowderFormulator.Nutrition_Excel_Name);
		if (dataset == null) {
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.MilkPowderFormulator.Template_Dir);
		if(resultStrs.length==0){//����ʧ��
			MessageBox.post("���ݼ�����ʧ��","",MessageBox.INFORMATION);
			return ;
		}
		
		//��ȡ���ݵ�˵
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, "��ͼ");
		if(topBomLine==null){//����ṹΪ�վʹ�������һ���µĿյĸ���ͼ
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			AIFComponentContext[] materialChildrens = topBomLine.getChildren();
			for(AIFComponentContext materialContext : materialChildrens){
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) materialContext.getComponent();
				if(isWetBom(materialBomLine)){//�����ʪ���䷽��BOM
					
					TCComponentItemRevision wetItemRev = materialBomLine.getItemRevision();
					Double output = StringsUtil.convertStr2Double(wetItemRev.getProperty("u8_OutPut"));//wetBean
					
					AIFComponentContext[] wetChildrens = materialBomLine.getChildren();//������ǻ���
					MaterialBean baseBean  = AnnotationFactory.getInstcnce(MaterialBean.class, materialBomLine);
					for(AIFComponentContext wetContext : wetChildrens){//ֻ��ʪ���д���Ӫ����
						TCComponentBOMLine wetBomLine = (TCComponentBOMLine) wetContext.getComponent();
						MaterialBean wetBean  = AnnotationFactory.getInstcnce(MaterialBean.class, wetBomLine);
						if(isNutritionBom(wetBomLine)){//�����Ӫ����
							wetBean.isNutrition=true;
							NutritionBean nutritionBean = new NutritionBean();
							nutritionBean.rootBean = wetBean;
							nutritionBean.childList = new ArrayList<>();
							//Ҫ����һ��Ӫ������Ͷ���� ���ݻ��۵�Ͷ����  -ps:��ʱ���ı� ʪ���䷽��Ͷ���پ��Ƕ��� 
//							wetBean.U8_inventory = StringsUtil.convertStr2Double(baseBean.U8_inventory)*StringsUtil.convertStr2Double(wetBean.U8_inventory)/output+"";
							AIFComponentContext[] nutritionChilds = wetBomLine.getChildren();
							for(AIFComponentContext nutritionContext : nutritionChilds){
								TCComponentBOMLine nutritionBom = (TCComponentBOMLine) nutritionContext.getComponent();
								MaterialBean nutritionMaterialBean = AnnotationFactory.getInstcnce(MaterialBean.class, nutritionBom);
								nutritionBean.childList.add(nutritionMaterialBean);
							}
							nutritionBeansList.add(nutritionBean);//��һ��Ӫ�����ŵ��ṹ��
						}
					}
				}
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		//д����
		int start = 15;
		int mIncrement  =0;//��Ϊ�����ı���
		File inFile = new File(Const.MilkPowderFormulator.Nutrition_Excel_Input_Path);
		try {
			Workbook wb = new XSSFWorkbook(new FileInputStream(inFile));
			Sheet sheet = wb.getSheet(Const.MilkPowderFormulator.EXCEL_SHEET1);
			
			for(int i=0;i<nutritionBeansList.size();i++){//һ��Ӫ����
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
					
					cell = getCell(sheet, start, 3);//��������Դ
					cell.setCellValue(nutritionMaterialBean.sourceOfCompound);setCellBorder(cell, wb);
					
					cell = getCell(sheet, start, 5);//��׼
					cell.setCellValue(nutritionMaterialBean.down+"-"+nutritionMaterialBean.up);setCellBorder(cell, wb);
					
					if(j<nutritionMaterialList.size()-1){//��ʱ���һ����ʱ��ֱ��������Ȼ��Ͳ����ڲ���һ����
						sheet.shiftRows(start, sheet.getLastRowNum(), 1);
						mIncrement++;//
					}else{
						mIncrement++;//���������ȻҪ+1
					}
				}
				
				
				//�����е�Ԫ��ĺϲ�дֵ��Ȼ��д����
				CellRangeAddress cra=new CellRangeAddress(start, start+nutritionMaterialList.size()-1, 0, 0);  
				sheet.addMergedRegion(cra); 
				
				cra=new CellRangeAddress(start, start+nutritionMaterialList.size()-1, 7, 7);  
				sheet.addMergedRegion(cra); 
				
				Cell cell = getCell(sheet, start, 0);
				cell.setCellValue(rootBena.objectName);setCellBorder(cell, wb);
				
				
				cell = getCell(sheet, start, 7);//����
				cell.setCellValue(rootBena.U8_inventory);setCellBorder(cell, wb);
//				
			}
			
			//����
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	
		
		//�ϴ�
		file = new File(Const.MilkPowderFormulator.Nutrition_Excel_Input_Path);
		if (file.exists()) {// ���ھ��ϴ�
			try {
				TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						Const.MilkPowderFormulator.Nutrition_Excel_Input_Path, DataSetUtil.DataSetType.MSExcelX,
						"excel", Const.MilkPowderFormulator.Nutrition_Excel_Upload_Name);
				formulatorRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
				return;
			}
		}
		
	}
	
	
	
	/* (non-Javadoc)
	 * ���Ѿ���ȡ����ָ��
	 * Ӫ�����е�Ӫ������ԭ��  ����Ҫ����ָ���BeanȻ����Ѿ����ڵ�ָ����кϲ�
	 */
	@Override
	public List<IndexItemBean> getAllIndexBeanContainNutrition(List<IndexItemBean> indexBeanList,TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> nutritionBomList = new ArrayList<>();
		List<IndexItemBean> nutritionIndexList = new ArrayList<>();
		try {
			AIFComponentContext[] allChildren = topBomLine.getChildren();//��һ��
			for(AIFComponentContext allContext : allChildren){
				TCComponentBOMLine alllBom = (TCComponentBOMLine) allContext.getComponent();
				TCComponentItemRevision allRev = alllBom.getItemRevision();
				if("U8_FormulaRevision".equals(allRev.getType())){//�ҵ�����
					//��Ӫ����
					AIFComponentContext[] materialChildren = alllBom.getChildren();
					for(AIFComponentContext materialContext : materialChildren){
						TCComponentBOMLine materailBom = (TCComponentBOMLine) materialContext.getComponent();
						AIFComponentContext[] nutritionChildren = materailBom.getChildren();//������Ӫ�����Ķ���
						for(AIFComponentContext nutritionContext : nutritionChildren){
							TCComponentBOMLine nutritionBom = (TCComponentBOMLine) nutritionContext.getComponent();
							TCComponentItemRevision nutritionRev = nutritionBom.getItemRevision();
							if("U8_MaterialRevision".equals(nutritionRev.getType())){//�ҵ���Ӫ����  materailBom
								nutritionBomList.add(materailBom);
								continue;
							}
						}
					}
				}
			}
			
			//������Ӫ���������ԭ����Ϊָ���bean���з�װ
			for(TCComponentBOMLine bomLine : nutritionBomList){
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine childBom = (TCComponentBOMLine) context.getComponent();
					IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class,childBom);
					nutritionIndexList.add(bean);
				}
			}
			
			
			//�ҵ���������ԭ�����е�ָ����Ŀ
			List<IndexItemBean> noExitIndexList = new ArrayList<>();
			for(IndexItemBean nutritinBean : nutritionIndexList){
				boolean flag = false;
				for(IndexItemBean allBean : indexBeanList){
					if(nutritinBean.objectName.equals(allBean.objectName)){//��������˾���ô����
						flag = true;
						allBean.up = StringsUtil.convertStr2Double(allBean.up) + StringsUtil.convertStr2Double(nutritinBean.up)+"";
						allBean.down = StringsUtil.convertStr2Double(allBean.down) + StringsUtil.convertStr2Double(nutritinBean.down)+"";
						allBean.average = StringsUtil.convertStr2Double(allBean.average) + StringsUtil.convertStr2Double(nutritinBean.average)+"";
					}
				}
				
				if(!flag){//���������
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
	 * ����Ӫ���ɷֱ�
	 */
	@Override
	public void createNutritionIndexExcel(List<IndexItemBean> allIndexBeanList,TCComponentItemRevision formulatorRev) {
		
		//������ģ��
		File file = new File(Const.MilkPowderFormulator.Index_Excel_Input_Path);
		if (file.exists()) {// ���ھ�ɾ��
			file.delete();
		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code,
				Const.MilkPowderFormulator.Index_Excel_Name);
		if (dataset == null) {
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel,
				Const.MilkPowderFormulator.Template_Dir);
		if (resultStrs.length == 0) {// ����ʧ��
			MessageBox.post("���ݼ�����ʧ��", "", MessageBox.INFORMATION);
			return;
		}

		// д����
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



			// ����
			FileOutputStream out = new FileOutputStream(inFile);
			wb.write(out);
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// �ϴ�
		file = new File(Const.MilkPowderFormulator.Index_Excel_Input_Path);
		if (file.exists()) {// ���ھ��ϴ�
			try {
				TCComponentDataset dataSet = DataSetUtil.setDatasetFileToTC(
						Const.MilkPowderFormulator.Index_Excel_Input_Path, DataSetUtil.DataSetType.MSExcelX, "excel",
						Const.MilkPowderFormulator.Index_Excel_Upload_Name);
				formulatorRev.add("IMAN_specification", dataSet);
			} catch (TCException e) {
				e.printStackTrace();
				MessageBox.post("���ݼ��ϴ�ʧ��", "", MessageBox.INFORMATION);
				return;
			}
		}
		
	}
	/**
	 * �ж�һ��ԭ���Ƿ���Ӫ������˵
	 * @param bomLine
	 * @return 
	 */
	public boolean isNutritionBom(TCComponentBOMLine bomLine){
		boolean isNutritionFlag = false;
		try {
			//�ж��Ƿ���Ӫ����
			AIFComponentContext[] childrens2 = bomLine.getChildren();
			for(AIFComponentContext context2 : childrens2){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context2.getComponent();
				String type = tempBomLine.getItemRevision().getType();
				if("U8_MaterialRevision".equals(type)){//�����һ�������������ԭ�� ��˵�������һ��Ӫ����
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
	 * @param bomLine  �ж����BOM�Ƿ��ǻ���  ����һ�����䷽����
	 * @return
	 */
	public boolean isWetBom(TCComponentBOMLine bomLine){
		boolean isWetFlag = false;
		try {
			String type = bomLine.getItemRevision().getType();
			if("U8_FormulaRevision".equals(type)){//�������������һ���䷽
				isWetFlag = true;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isWetFlag;
	}


	
	/*
	 * ����ģ���ļ� �����ļ�
	 * @return
	 */
	public boolean downTemplate(){
		File file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		if(file.exists()){//���ھ�ɾ��
			file.delete();
		}
//		file = new File(Const.ProductFormulaExcel.Product_Complex_Excel_Input_Path);
//		if(file.exists()){//���ھ�ɾ��
//			file.delete();
//		}
		String code = PreferenceUtil.getPreference(Const.CommonCosnt.Model_File_Path_Preference);
		TCComponentDataset dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.MilkPowderFormulator.Formulator_Excel_Name);
		if(dataset==null){
			return false;
		}
		String[] resultStrs = DataSetUtil.downDateSetToLocalDir(dataset,  DataSetUtil.DataSetNameRef.Excel, Const.MilkPowderFormulator.Template_Dir);
		if(resultStrs.length==0){//����ʧ��
			return false;
		}
		
//		dataset = TemplateFilePathUtil.getDatasetByNameAndCode(code, Const.ProductFormulaExcel.Product_Complex_Excel_Name);
//		if(dataset==null){
//			return false;
//		}
//		resultStrs = DataSetUtil.downDateSetToLocalDir(dataset, DataSetUtil.DataSetNameRef.Excel, Const.ProductFormulaExcel.Template_Dir);
//		if(resultStrs.length==0){//����ʧ��
//			return false;
//		}
		return true;
	}
	
	
	
	/**
	 * �����ɵ��ļ�
	 * @param component
	 * @return
	 */
	public boolean uploadFile(TCComponent component){
		 File file = new File(Const.MilkPowderFormulator.Foumulator_Excel_Input_Path);
		 if(file.exists()){//���ھ��ϴ�
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
	 * ����topBomLine��ȡ����Ļ��� 
	 * ���۶����ڵ�һ���˵
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
				if(itemRevision.getType().equals("U8_FormulaRevision")){//�ҵ�����
					basePowderBomList.add(bomLine);
				}
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return basePowderBomList;
	}
	
	/**
	 * ͨ����������ȡ�������Ӫ����
	 * @param basePowderBomList ���۵ļ���
	 * @return
	 */
	private List<TCComponentBOMLine> getNutritionBomList(List<TCComponentBOMLine> basePowderBomList){
		List<TCComponentBOMLine> nutritionBomList = new ArrayList<>();
		for(TCComponentBOMLine basePowderBomLine : basePowderBomList){
			try {
				AIFComponentContext[] children = basePowderBomLine.getChildren();
				for(AIFComponentContext context : children){//��������ĺ��� ���ܴ���Ӫ����
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();//����ǻ��������ԭ��,Ӫ�������ܴ��ڵĵط�
					AIFComponentContext[] children2 = materialBomLine.getChildren();
					for(AIFComponentContext context2 : children2){
						TCComponentBOMLine bomLine = (TCComponentBOMLine) context2.getComponent();
						if(bomLine.getItemRevision().getType().equals("U8_MaterialRevision")){//������ԭ�ϵ�ԭ�� ����Ӫ������
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
	 * ͨ����������ȡ�������Ӫ����
	 * @param basePowderBomList ���۵ļ���
	 * @return
	 */
	private List<TCComponentBOMLine> getNutritionBomList(TCComponentBOMLine basePowderBomLine){
		List<TCComponentBOMLine> nutritionBomList = new ArrayList<>();
		try {
			AIFComponentContext[] children = basePowderBomLine.getChildren();
			for(AIFComponentContext context : children){//��������ĺ��� ���ܴ���Ӫ����
				TCComponentBOMLine materialBomLine = (TCComponentBOMLine) context.getComponent();//����ǻ��������ԭ��,Ӫ�������ܴ��ڵĵط�
				AIFComponentContext[] children2 = materialBomLine.getChildren();
				for(AIFComponentContext context2 : children2){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context2.getComponent();
					if(bomLine.getItemRevision().getType().equals("U8_MaterialRevision")){//������ԭ�ϵ�ԭ�� ����Ӫ������
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
	 * �������ֵȥ����ָ����Ŀ�е����ߺ�����
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
	 * @param cacheTopBomLine ��Ϊ�������ʱ�䷽����
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

	// ������ʾһ��Ӫ������������Ϣ
	class NutritionBean {
		MaterialBean rootBean;
		List<MaterialBean> childList;
	}






	
	
	

}
