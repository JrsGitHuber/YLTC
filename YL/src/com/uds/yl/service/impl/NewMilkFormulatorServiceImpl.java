package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;

import COM.inovie.services.integration.applicationRegistry.xml.ContactInfo;

import com.aspose.words.Node;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.INewMilkFormulatorService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.StringsUtil;

public class NewMilkFormulatorServiceImpl implements INewMilkFormulatorService{
	
	private String down;
	private String downSymbol;
	private String up;
	private String upSymbol;
	private String detectValue;
	
	private String mFormulatorFolderName = "_FORMULATOR";
	private String mNutritionFolderName = "_NUTRITION";

	
	/* (non-Javadoc)
	 * ����ѡ�еİ汾��ʼ���ڵ�
	 */
	@Override
	public NodeBean initRootNode(
			TCComponentItemRevision rootItemRevsion) {
		
		LinkedList<NodeBean> nodeQueue = new LinkedList<NodeBean>(); 
		
		//��ȡ���ڵ��BomLine
		TCComponentBOMLine rootBomLine = BomUtil.getTopBomLine(rootItemRevsion, Const.BomType.BOM_VIEW);
		if(rootBomLine == null){
			rootBomLine = BomUtil.setBOMViewForItemRev(rootItemRevsion);
		}
		
		
		try {
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
			NodeBean rootNodeBean = AnnotationFactory.getInstcnce(NodeBean.class, rootItemRevsion);
			
			rootNodeBean.nodeType = getNodeTypeByRevision(rootItemRevsion);
			rootNodeBean.nodeItemRev = rootItemRevsion;
			rootNodeBean.node = rootNode;
			rootNodeBean.nodeBomLine = rootBomLine;
			rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
			rootNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
			
			rootNode.setUserObject(rootNodeBean);
			
			//��ʼ������
			nodeQueue.offer(rootNodeBean);
			
			
			//��ʼ�� ���ڵ��µĺ���
			while(!nodeQueue.isEmpty()){
				NodeBean bean = nodeQueue.peek();//�鿴һ���ڵ�
				
				if(bean.nodeBomLine == null || !bean.nodeBomLine.hasChildren()){//Ϊ�ջ���û�к��ӽڵ�
					nodeQueue.poll();//�Ƴ�����ͷ
					continue;
				}
				
				AIFComponentContext[] bomContexts = bean.nodeBomLine.getChildren();
				for(AIFComponentContext bomContext : bomContexts){
					//bom �� itemRev
					TCComponentBOMLine bomChild = (TCComponentBOMLine) bomContext.getComponent();
					TCComponentItemRevision itemRevChild = bomChild.getItemRevision();
					
					//node
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					
					//nodeBean
					NodeBean childNodeBean =  AnnotationFactory.getInstcnce(NodeBean.class, bomChild);
					childNodeBean.nodeType = getNodeType(itemRevChild);
					childNodeBean.nodeItemRev = itemRevChild;
					childNodeBean.nodeBomLine = bomChild;
					childNodeBean.node = childNode;
					childNodeBean.parentNode = bean.node;
					childNodeBean.childNodeBeans = new ArrayList<NodeBean>();
					childNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
					childNode.setUserObject(childNodeBean);
					
					//add child 
					bean.chidNodes.add(childNode);
					bean.childNodeBeans.add(childNodeBean);
					
					bean.node.add(childNode);
					
					//���
					nodeQueue.offer(childNodeBean);
				}
				
				//����
				nodeQueue.poll();
				
			}
			
			//�ر�BOM
			BomUtil.closeBom(rootBomLine);
			
			return rootNodeBean;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/* (non-Javadoc)
	 * ����ѡ�еİ汾��ʼ���ڵ� 
	 * ���ݲ��� bomType�Ĳ�ͬ ѡ��ͬ��bom��ͼ
	 */
	@Override
	public NodeBean initRootNode(TCComponentItemRevision rootItemRevsion, String bomType) {
		
		LinkedList<NodeBean> nodeQueue = new LinkedList<NodeBean>(); 
		
		//��ȡ���ڵ��BomLine
		TCComponentBOMLine rootBomLine = null;
		if(StringsUtil.isEmpty(bomType)){//Ĭ���� ��ͼ
			rootBomLine = BomUtil.getTopBomLine(rootItemRevsion, Const.BomType.BOM_VIEW);
		}else {
			rootBomLine = BomUtil.getTopBomLine(rootItemRevsion, bomType);
		}
		if(rootBomLine == null){
			rootBomLine = BomUtil.setBOMViewForItemRev(rootItemRevsion);
		}
		
		
		try {
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
			NodeBean rootNodeBean = AnnotationFactory.getInstcnce(NodeBean.class, rootItemRevsion);
			
			rootNodeBean.nodeType = getNodeTypeByRevision(rootItemRevsion);
			rootNodeBean.nodeItemRev = rootItemRevsion;
			rootNodeBean.node = rootNode;
			rootNodeBean.nodeBomLine = rootBomLine;
			rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
			rootNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
			
			rootNode.setUserObject(rootNodeBean);
			
			//��ʼ������
			nodeQueue.offer(rootNodeBean);
			
			
			//��ʼ�� ���ڵ��µĺ���
			while(!nodeQueue.isEmpty()){
				NodeBean bean = nodeQueue.peek();//�鿴һ���ڵ�
				
				if(bean.nodeBomLine == null || !bean.nodeBomLine.hasChildren()){//Ϊ�ջ���û�к��ӽڵ�
					nodeQueue.poll();//�Ƴ�����ͷ
					continue;
				}
				
				AIFComponentContext[] bomContexts = bean.nodeBomLine.getChildren();
				for(AIFComponentContext bomContext : bomContexts){
					//bom �� itemRev
					TCComponentBOMLine bomChild = (TCComponentBOMLine) bomContext.getComponent();
					TCComponentItemRevision itemRevChild = bomChild.getItemRevision();
					
					//node
					DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
					
					//nodeBean
					NodeBean childNodeBean =  AnnotationFactory.getInstcnce(NodeBean.class, bomChild);
					childNodeBean.nodeType = getNodeType(itemRevChild);
					childNodeBean.nodeItemRev = itemRevChild;
					childNodeBean.nodeBomLine = bomChild;
					childNodeBean.node = childNode;
					childNodeBean.parentNode = bean.node;
					childNodeBean.childNodeBeans = new ArrayList<NodeBean>();
					childNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
					childNode.setUserObject(childNodeBean);
					
					//add child 
					bean.chidNodes.add(childNode);
					bean.childNodeBeans.add(childNodeBean);
					
					bean.node.add(childNode);
					
					//���
					nodeQueue.offer(childNodeBean);
				}
				
				//����
				nodeQueue.poll();
				
			}
			
			//�ر�BOM
			BomUtil.closeBom(rootBomLine);
			
			return rootNodeBean;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * ��ȡ�ð汾����Ľڵ�����
	 * @param nodeRevision
	 * @return
	 */
	@Override
	public String getNodeType(TCComponentItemRevision nodeRevision){
		String nodeRevType = nodeRevision.getType();
		if(nodeRevType.equals(Const.ItemRevType.FORMUALTOR_REV)){
			return Const.NodeType.NODE_FORMULA;
		}
		if(nodeRevType.equals(Const.ItemRevType.INDEXITEM_REV)){
			return Const.NodeType.NODE_INDEXITEM;
		}
		if(nodeRevType.equals(Const.ItemRevType.MATERIAL_REV)){
			return Const.NodeType.NODE_MATERIAL;
		}
		
		return nodeRevType;
		
		
	}
	
	
	/**
	 * �ж������汾���� �ǲ���Ӫ��������
	 * @param itemRevision
	 * @return
	 */
	@Override
	public boolean isNitrition(TCComponentItemRevision itemRevision){
		
		String type = itemRevision.getType();
		if(!Const.ItemRevType.MATERIAL_REV.equals(type)){//��ԭ�����͵Ĳſ�����Ӫ����
			return false;
		}
		
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.BomType.BOM_VIEW);
		if(topBomLine == null){
			topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
		}
		
		try {
			AIFComponentContext[] contexts = topBomLine.getChildren();
			for(AIFComponentContext context : contexts){
				TCComponentBOMLine bomChild = (TCComponentBOMLine) context.getComponent();
				type = bomChild.getItemRevision().getType();
				if(Const.ItemRevType.MATERIAL_REV.equals(type)){//�����һ�������������ԭ�� ��˵�������һ��Ӫ����
					return true;
				}
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		//�ر�bom
		BomUtil.closeBom(topBomLine);
		
		return false;
		
	}


	
	
	
	/* 
	 * ����ԭ�����Ͳ�ѯԭ��
	 */
	@Override
	public List<NodeBean> getMaterialByType(String materialType) {
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8MATERIAL.getValue());
		TCComponent[] result = QueryUtil.getSearchResult(query, new String[]{"����"}, new String[]{"��ɳ��-Cold"});
		List<NodeBean> nodeBeanList = new ArrayList<NodeBean>();
		//��ʼ��
		for(TCComponent component : result){
			TCComponentItemRevision materialItemRev = (TCComponentItemRevision) component;
			NodeBean materialNodeBean  = initRootNode(materialItemRev);
			
			nodeBeanList.add(materialNodeBean);
		}
		return nodeBeanList;
	}

	
	/**
	 * ���ݰ汾�����Ͳ�ͬ�����䲻�õ����ʹ���
	 * @param itemRevision
	 * @return
	 */
	public String getNodeTypeByRevision(TCComponentItemRevision itemRevision){
		try {
			String isNutrition = itemRevision.getProperty("u8_isNUtrition");
			String isBaseFormulator = itemRevision.getProperty("u8_isBaseFormulator");
			String type = itemRevision.getType();
			
			if(Const.ItemRevType.FORMUALTOR_REV.equals(type)){//�䷽
				if("True".equals(isBaseFormulator)){//�ǻ���
					return Const.NodeType.NODE_BASE_FORMULATOR;
				}else{
					return Const.NodeType.NODE_FORMULA;	
				}
				
			}else if(Const.ItemRevType.MATERIAL_REV.equals(type)){//ԭ��
				if("True".equals(isNutrition)){//��Ӫ����
					return Const.NodeType.NODE_NUTRITION;
				}else{//����һ��ԭ��
					return Const.NodeType.NODE_MATERIAL;
				}
			}else if(Const.ItemRevType.INDEXITEM_REV.equals(type)){//ָ��
				return Const.NodeType.NODE_INDEXITEM;
			}else if(Const.ItemRevType.INDEX_REV.equals(type)){//��׼
				return Const.NodeType.NODE_INDEX;
			}else if(Const.ItemRevType.LAW_REV.equals(type)){//����
				return Const.NodeType.NODE_LAW;
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return "";
	}


	
	
	/* (non-Javadoc)
	 * �����ڵ�ĵ�һ����Ϣ �õ� �����䷽���е���Ϣ
	 */
	@Override
	public List<NodeBean> getFormulatorTableNodeBeanListByRoot(NodeBean rootBean) {
		List<NodeBean> formulatorTableNodeBeanList = new ArrayList<NodeBean>();
		
		if(rootBean.childNodeBeans == null ){
			return formulatorTableNodeBeanList;
		}
		
		for(NodeBean childBean : rootBean.childNodeBeans){
			formulatorTableNodeBeanList.add(childBean);
		}
		
		return formulatorTableNodeBeanList;
	}


	/* (non-Javadoc)
	 * ��ѡ�е�ԭ�Ͻڵ����һ����ϢĬ����Ϊָ�괦��
	 */
	@Override
	public List<NodeBean> getIndexTableNodeBeanListBySelectedMaterialNode(
			NodeBean selectedMaterialNodeBean) {
		List<NodeBean> indexTableNodeBeanList = new ArrayList<NodeBean>();
		
		if(selectedMaterialNodeBean.childNodeBeans == null){
			return indexTableNodeBeanList;
		}
		
		for(NodeBean childBean : selectedMaterialNodeBean.childNodeBeans){
			indexTableNodeBeanList.add(childBean);
		}
		return indexTableNodeBeanList;
	}


	/* (non-Javadoc)
	 * ����ѡ�е��䷽��ȡ ��ǩ��Ŀ 
	 * �䷽�� ������ǩBOM �������� ����ָ��
	 */
	@Override
	public List<NodeBean> getLableTableNodeBeanListByFormulatorRev(
			TCComponentItemRevision formulatorRevsion) {
		//��ǩ��Ŀ
		List<NodeBean> lableNodeBeanList = new ArrayList<NodeBean>();
		//�����е���Ŀ
		List<NodeBean> lawNodeBeanList = new ArrayList<NodeBean>();
		//��ǩ�е���Ŀ
		List<NodeBean> indexNodeBeanList = new ArrayList<NodeBean>();
		
		//�����е� ��Ŀ
		List<TCComponentItemRevision> lawRevList = new ArrayList<TCComponentItemRevision>();
		//��׼�е���Ŀ
		List<TCComponentItemRevision> indexRevList = new ArrayList<TCComponentItemRevision>();
		
		try {
			//����
			TCComponent[] componentChilds = formulatorRevsion.getRelatedComponents("U8_LawRel");
			for(TCComponent componentChild : componentChilds){
				String type = componentChild.getType();
				if(Const.ItemRevType.LAW_REV.equals(type)){//����
					TCComponentItemRevision lawRev = (TCComponentItemRevision) componentChild;
					lawRevList.add(lawRev);
				}
			}
			
			//ָ��
			componentChilds = formulatorRevsion.getRelatedComponents("U8_techstandardRel");
			for(TCComponent componentChild : componentChilds){
				String type = componentChild.getType();
				if(Const.ItemRevType.INDEX_REV.equals(type)){//ָ��
					TCComponentItemRevision indexRev = (TCComponentItemRevision) componentChild;
					indexRevList.add(indexRev);
				}
			}
			
			//�䷽ ����Ӫ����ǩ ����bom
			NodeBean rootNodeBean = initRootNode(formulatorRevsion, Const.BomType.BOM_NUTRITION);//�� ����Ӫ����ǩ �洢�̷����õ��ı�ǩ
			if(rootNodeBean.childNodeBeans==null) rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
			for(NodeBean nodeBean : rootNodeBean.childNodeBeans){
				lableNodeBeanList.add(nodeBean);
			}
			
			//��ӷ��� 
			for(TCComponentItemRevision lawRev : lawRevList){
				rootNodeBean = initRootNode(lawRev);
				if(rootNodeBean.childNodeBeans==null) rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
				for(NodeBean nodeBean : rootNodeBean.childNodeBeans){
					lawNodeBeanList.add(nodeBean);
				}
			}
			
			//�����е���Ŀ�ϲ� idһ����Ҫ���� �����Ƿ���ֵ
			for(NodeBean lawNodeBean : lawNodeBeanList){
				boolean exitFlag = false;
				for(NodeBean lableNodeBean : lableNodeBeanList){
					if(lawNodeBean.itemID.equals(lableNodeBean.itemID)){//����Ŀ�Ѿ��� ��ǩ����
						exitFlag = true;
						lableNodeBean.down = lawNodeBean.down;
						lableNodeBean.downSymbol = lawNodeBean.downSymbol;
						lableNodeBean.up = lawNodeBean.up;
						lableNodeBean.upSymbol = lawNodeBean.upSymbol;
						lableNodeBean.detectValue = lawNodeBean.detectValue;
						
						lableNodeBean.lawValue = getStandardFormUPAndDown(lableNodeBean.up, lableNodeBean.down,
								lableNodeBean.upSymbol, lableNodeBean.downSymbol, lableNodeBean.detectValue);
						
					}
				}
				if(!exitFlag){//�����������е���Ŀ�����ڱ�ǩ������ ����ӽ�ȥ
					lableNodeBeanList.add(lawNodeBean);
				}
			}
			
			//��ӱ��׼
			for(TCComponentItemRevision indexRev : indexRevList){
				rootNodeBean = initRootNode(indexRev);
				if(rootNodeBean.childNodeBeans==null) rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
				for(NodeBean nodeBean : rootNodeBean.childNodeBeans){
					indexNodeBeanList.add(nodeBean);
				}
			}
			
			//��׼����Ŀ�ϲ� idһ����Ҫ���� ������ �ڿر�׼ֵ
			for(NodeBean indexNodeBean : indexNodeBeanList){
				boolean exitFlag = false;
				for(NodeBean lableNodeBean : lableNodeBeanList){
					if(indexNodeBean.itemID.equals(lableNodeBean.itemID)){//����Ŀ�Ѿ��� ��ǩ����
						exitFlag = true;
						lableNodeBean.ICS_DOWN = indexNodeBean.ICS_DOWN;
						lableNodeBean.ICS_DOWN_SYMBOL = indexNodeBean.ICS_DOWN_SYMBOL;
						lableNodeBean.ICS_UP = indexNodeBean.ICS_UP;
						lableNodeBean.ICS_UP_SYMBOL = indexNodeBean.ICS_UP_SYMBOL;
						
						lableNodeBean.standardValue = getStandardFormUPAndDown(lableNodeBean.ICS_UP, lableNodeBean.ICS_DOWN,
								lableNodeBean.ICS_UP_SYMBOL, lableNodeBean.ICS_DOWN_SYMBOL, "");
					}
				}
				if(!exitFlag){//�����������е���Ŀ�����ڱ�ǩ������ ����ӽ�ȥ
					lableNodeBeanList.add(indexNodeBean);
				}
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return lableNodeBeanList;
	}
	
	
	
	/**
	 * @param up
	 *            ����
	 * @param down
	 *            ����
	 * @param upSymbol
	 *            ���޷���
	 * @param downSymbol
	 *            ���޷���
	 * @param detectValue
	 *            ���ֵ����
	 * @return
	 */
	public String getStandardFormUPAndDown(String up, String down, String upSymbol, String downSymbol,
			String detectValue) {
		String result = "";
		if (StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// up��down��Ϊ����
			result = detectValue;
		}
		if (!StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// up��down����ֵ
			downSymbol = ">=".equals(downSymbol) ? "��" : "��";
			upSymbol = "<=".equals(upSymbol) ? "��" : "��";
			result = down+downSymbol+"," + upSymbol + up;// ����һ���Ǵ��ڵ���С�ڵ���
		}
		if (!StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// ֻ��up��ֵ
			upSymbol = "<=".equals(upSymbol) ? "��" : "��";
			result = ","+upSymbol + up;
		}
		if (StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// ֻ��down��ֵ
			downSymbol = ">=".equals(downSymbol) ? "��" : "��";
			result = downSymbol + down+",";
		}
		return result;
	}
	
	/**
	 * @param newStandard
	 * ���ݱ�׼��ֳ��� up  down �Ͷ�Ӧ�ķ��� Ҫ��д������
	 */
	public void setUpAndDownBynewStandard(String newStandard) {
		//��ʼ������
		detectValue = "";
		down = "";
		up = "";
		downSymbol = "";
		upSymbol = "";
		
		if(StringsUtil.isEmpty(newStandard)){//���Ϊ��
			detectValue = "";
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
			return;
		}
		
		if(!newStandard.contains("��") && !newStandard.contains("��")){//���� <,<���ָ�ʽ ����
			detectValue = newStandard;
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
			return;
		}
		if (!StringsUtil.isEmpty(newStandard)) {// ��Ϊ��
			String[] result = newStandard.split(",");
			
			//����
			if(result[0].contains("��")){
				down = result[0].split("��")[0];//����
				downSymbol = ">=";
			}else if(result[0].contains("��")){
				down = result[0].split("��")[0];//����
				downSymbol = ">";
			}
			
			//����
			if(result.length==2){
				if(result[1].contains("��")){
					up = result[1].split("��")[1];//����
					upSymbol = "<=";
				}else if(result[1].contains("��")) {
					up = result[1].split("��")[1];//����
					upSymbol = "<";
				}
			}
		
		}else if(newStandard.equals("")||newStandard==null){
			detectValue = "";
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
		}
	}



	/* (non-Javadoc)
	 * �������ƴ���һ��Ӫ���� �����汾������ȥ
	 */
	@Override
	public TCComponentItemRevision createNutritionItemByName(
			String nutritionName) {
		
		//�ҵ���Ż����䷽��רһ�ļ���
		TCComponentFolder folder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query,new String[] { "����", "����", "����" },
				new String[] { mNutritionFolderName, mNutritionFolderName,"Folder" });
		if (searchResult.length > 0) {
			folder = (TCComponentFolder) searchResult[0];
		}

		if (folder == null) {// û���ҵ� ����
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
			searchResult = QueryUtil.getSearchResult(query, new String[] {"����", "����", "����Ȩ�û�" },
					new String[] { "Home","Fnd0HomeFolder", userName });
			for (TCComponent component : searchResult) {
				String type = component.getType();
				if ("Fnd0HomeFolder".equals(type)) {// �ҵ�����Ҫ�ҵ��ļ���
					homeFolder = (TCComponentFolder) component;
				}
			}
			try {
				folder = ItemUtil.createFolder(mNutritionFolderName,mNutritionFolderName);
				homeFolder.add("contents", folder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}

		try {
			// ����Ӫ����Item
			TCComponentItem nutritionItem = ItemUtil.createtItem(Const.ItemType.MATERIAL_ITEM, nutritionName, "");
			TCComponentItemRevision nutritionItemRevision = nutritionItem.getLatestItemRevision();
			//д��Ӫ����������
			nutritionItemRevision.setProperty("u8_isNutrition", "True");
			
			// ��ӵ��ļ���
			folder.add("contents", nutritionItem);

			return nutritionItemRevision;
		} catch (TCException e) {
			e.printStackTrace();
		}

		return null;
		
	}



	/* (non-Javadoc)
	 * �������ƴ���һ�������䷽ ���ҽ��汾������ȥ
	 */
	@Override
	public TCComponentItemRevision createFormulatorItemByName(
			String formulatorName) {
		
		//�ҵ���Ż����䷽��רһ�ļ���
		TCComponentFolder folder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����"}, new String[]{mFormulatorFolderName,mFormulatorFolderName,"Folder"} );
		if(searchResult.length>0){
			folder = (TCComponentFolder) searchResult[0];
		}
		
		if(folder==null){//û���ҵ� ����
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
			searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����Ȩ�û�"}, new String[]{"Home","Fnd0HomeFolder",userName} );
			for(TCComponent component : searchResult){
				String type = component.getType();
				if("Fnd0HomeFolder".equals(type)){//�ҵ�����Ҫ�ҵ��ļ���
					homeFolder = (TCComponentFolder) component;
				}
			}
			try {
				folder = ItemUtil.createFolder(mFormulatorFolderName, mFormulatorFolderName);
				homeFolder.add("contents", folder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		try {
			//���������䷽Item
			TCComponentItem formulatorItem = ItemUtil.createtItem(Const.ItemType.FORMUALTOR_ITEM, formulatorName, "");
			TCComponentItemRevision formulaItemRevision = formulatorItem.getLatestItemRevision();
			
			formulaItemRevision.setProperty("u8_isBaseFormulator", "True");//��ע�ǻ���
			
			//��ӵ��ļ���
			folder.add("contents", formulatorItem);
			
			return formulaItemRevision;
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}



	/* (non-Javadoc)
	 * �����ӽڵ�Ӹ��׽ڵ�ļ������Ƴ�
	 */
	@Override
	public void removeChildFromParent(NodeBean parentNodeBean,
			NodeBean childNodeBean) {
		//����������ǲ�Ϊ�յ�
		if(parentNodeBean.childNodeBeans == null) parentNodeBean.childNodeBeans = new ArrayList<NodeBean>();
		if(parentNodeBean.chidNodes == null) parentNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
		
		//�ҵ��±�
		int index = -1;
		for(int i=0;i<parentNodeBean.childNodeBeans.size();i++){
			NodeBean nodeBean = parentNodeBean.childNodeBeans.get(i);
			if(nodeBean.itemID.equals(childNodeBean.itemID)){
				index = i;
				break;
			}
		}
		//�ҵ�����λ��
		if(index != -1){
			parentNodeBean.chidNodes.remove(index);
			parentNodeBean.childNodeBeans.remove(index);
		}
	}
}
