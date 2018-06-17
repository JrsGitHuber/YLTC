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
	 * 根据选中的版本初始根节点
	 */
	@Override
	public NodeBean initRootNode(
			TCComponentItemRevision rootItemRevsion) {
		
		LinkedList<NodeBean> nodeQueue = new LinkedList<NodeBean>(); 
		
		//获取根节点的BomLine
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
			
			//初始化队列
			nodeQueue.offer(rootNodeBean);
			
			
			//初始化 根节点下的孩子
			while(!nodeQueue.isEmpty()){
				NodeBean bean = nodeQueue.peek();//查看一个节点
				
				if(bean.nodeBomLine == null || !bean.nodeBomLine.hasChildren()){//为空或者没有孩子节点
					nodeQueue.poll();//移除队列头
					continue;
				}
				
				AIFComponentContext[] bomContexts = bean.nodeBomLine.getChildren();
				for(AIFComponentContext bomContext : bomContexts){
					//bom 和 itemRev
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
					
					//入队
					nodeQueue.offer(childNodeBean);
				}
				
				//出队
				nodeQueue.poll();
				
			}
			
			//关闭BOM
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
	 * 根据选中的版本初始根节点 
	 * 根据参数 bomType的不同 选择不同的bom视图
	 */
	@Override
	public NodeBean initRootNode(TCComponentItemRevision rootItemRevsion, String bomType) {
		
		LinkedList<NodeBean> nodeQueue = new LinkedList<NodeBean>(); 
		
		//获取根节点的BomLine
		TCComponentBOMLine rootBomLine = null;
		if(StringsUtil.isEmpty(bomType)){//默认是 视图
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
			
			//初始化队列
			nodeQueue.offer(rootNodeBean);
			
			
			//初始化 根节点下的孩子
			while(!nodeQueue.isEmpty()){
				NodeBean bean = nodeQueue.peek();//查看一个节点
				
				if(bean.nodeBomLine == null || !bean.nodeBomLine.hasChildren()){//为空或者没有孩子节点
					nodeQueue.poll();//移除队列头
					continue;
				}
				
				AIFComponentContext[] bomContexts = bean.nodeBomLine.getChildren();
				for(AIFComponentContext bomContext : bomContexts){
					//bom 和 itemRev
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
					
					//入队
					nodeQueue.offer(childNodeBean);
				}
				
				//出队
				nodeQueue.poll();
				
			}
			
			//关闭BOM
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
	 * 获取该版本对象的节点类型
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
	 * 判断整个版本对象 是不是营养包类型
	 * @param itemRevision
	 * @return
	 */
	@Override
	public boolean isNitrition(TCComponentItemRevision itemRevision){
		
		String type = itemRevision.getType();
		if(!Const.ItemRevType.MATERIAL_REV.equals(type)){//是原料类型的才可能是营养包
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
				if(Const.ItemRevType.MATERIAL_REV.equals(type)){//如果第一层下面包含的是原料 就说明这个是一个营养包
					return true;
				}
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		//关闭bom
		BomUtil.closeBom(topBomLine);
		
		return false;
		
	}


	
	
	
	/* 
	 * 根据原料类型查询原料
	 */
	@Override
	public List<NodeBean> getMaterialByType(String materialType) {
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8MATERIAL.getValue());
		TCComponent[] result = QueryUtil.getSearchResult(query, new String[]{"名称"}, new String[]{"白沙糖-Cold"});
		List<NodeBean> nodeBeanList = new ArrayList<NodeBean>();
		//初始化
		for(TCComponent component : result){
			TCComponentItemRevision materialItemRev = (TCComponentItemRevision) component;
			NodeBean materialNodeBean  = initRootNode(materialItemRev);
			
			nodeBeanList.add(materialNodeBean);
		}
		return nodeBeanList;
	}

	
	/**
	 * 根据版本的类型不同来分配不用的类型代码
	 * @param itemRevision
	 * @return
	 */
	public String getNodeTypeByRevision(TCComponentItemRevision itemRevision){
		try {
			String isNutrition = itemRevision.getProperty("u8_isNUtrition");
			String isBaseFormulator = itemRevision.getProperty("u8_isBaseFormulator");
			String type = itemRevision.getType();
			
			if(Const.ItemRevType.FORMUALTOR_REV.equals(type)){//配方
				if("True".equals(isBaseFormulator)){//是基粉
					return Const.NodeType.NODE_BASE_FORMULATOR;
				}else{
					return Const.NodeType.NODE_FORMULA;	
				}
				
			}else if(Const.ItemRevType.MATERIAL_REV.equals(type)){//原料
				if("True".equals(isNutrition)){//是营养包
					return Const.NodeType.NODE_NUTRITION;
				}else{//就是一个原料
					return Const.NodeType.NODE_MATERIAL;
				}
			}else if(Const.ItemRevType.INDEXITEM_REV.equals(type)){//指标
				return Const.NodeType.NODE_INDEXITEM;
			}else if(Const.ItemRevType.INDEX_REV.equals(type)){//标准
				return Const.NodeType.NODE_INDEX;
			}else if(Const.ItemRevType.LAW_REV.equals(type)){//法规
				return Const.NodeType.NODE_LAW;
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return "";
	}


	
	
	/* (non-Javadoc)
	 * 将根节点的第一层信息 拿到 就是配方表中的信息
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
	 * 将选中的原料节点的下一层信息默认作为指标处理
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
	 * 根据选中的配方获取 标签条目 
	 * 配方中 冷饮标签BOM 关联法规 关联指标
	 */
	@Override
	public List<NodeBean> getLableTableNodeBeanListByFormulatorRev(
			TCComponentItemRevision formulatorRevsion) {
		//标签条目
		List<NodeBean> lableNodeBeanList = new ArrayList<NodeBean>();
		//法规中的条目
		List<NodeBean> lawNodeBeanList = new ArrayList<NodeBean>();
		//标签中的条目
		List<NodeBean> indexNodeBeanList = new ArrayList<NodeBean>();
		
		//法规中的 条目
		List<TCComponentItemRevision> lawRevList = new ArrayList<TCComponentItemRevision>();
		//标准中的条目
		List<TCComponentItemRevision> indexRevList = new ArrayList<TCComponentItemRevision>();
		
		try {
			//法规
			TCComponent[] componentChilds = formulatorRevsion.getRelatedComponents("U8_LawRel");
			for(TCComponent componentChild : componentChilds){
				String type = componentChild.getType();
				if(Const.ItemRevType.LAW_REV.equals(type)){//法规
					TCComponentItemRevision lawRev = (TCComponentItemRevision) componentChild;
					lawRevList.add(lawRev);
				}
			}
			
			//指标
			componentChilds = formulatorRevsion.getRelatedComponents("U8_techstandardRel");
			for(TCComponent componentChild : componentChilds){
				String type = componentChild.getType();
				if(Const.ItemRevType.INDEX_REV.equals(type)){//指标
					TCComponentItemRevision indexRev = (TCComponentItemRevision) componentChild;
					indexRevList.add(indexRev);
				}
			}
			
			//配方 冷饮营养标签 的中bom
			NodeBean rootNodeBean = initRootNode(formulatorRevsion, Const.BomType.BOM_NUTRITION);//以 冷饮营养标签 存储奶粉中用到的标签
			if(rootNodeBean.childNodeBeans==null) rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
			for(NodeBean nodeBean : rootNodeBean.childNodeBeans){
				lableNodeBeanList.add(nodeBean);
			}
			
			//添加法规 
			for(TCComponentItemRevision lawRev : lawRevList){
				rootNodeBean = initRootNode(lawRev);
				if(rootNodeBean.childNodeBeans==null) rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
				for(NodeBean nodeBean : rootNodeBean.childNodeBeans){
					lawNodeBeanList.add(nodeBean);
				}
			}
			
			//法规中的条目合并 id一样的要更新 这里是法规值
			for(NodeBean lawNodeBean : lawNodeBeanList){
				boolean exitFlag = false;
				for(NodeBean lableNodeBean : lableNodeBeanList){
					if(lawNodeBean.itemID.equals(lableNodeBean.itemID)){//该条目已经在 标签中了
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
				if(!exitFlag){//如果这个法规中的条目不存在标签集合中 就添加进去
					lableNodeBeanList.add(lawNodeBean);
				}
			}
			
			//添加标标准
			for(TCComponentItemRevision indexRev : indexRevList){
				rootNodeBean = initRootNode(indexRev);
				if(rootNodeBean.childNodeBeans==null) rootNodeBean.childNodeBeans = new ArrayList<NodeBean>();
				for(NodeBean nodeBean : rootNodeBean.childNodeBeans){
					indexNodeBeanList.add(nodeBean);
				}
			}
			
			//标准的条目合并 id一样的要更新 这里是 内控标准值
			for(NodeBean indexNodeBean : indexNodeBeanList){
				boolean exitFlag = false;
				for(NodeBean lableNodeBean : lableNodeBeanList){
					if(indexNodeBean.itemID.equals(lableNodeBean.itemID)){//该条目已经在 标签中了
						exitFlag = true;
						lableNodeBean.ICS_DOWN = indexNodeBean.ICS_DOWN;
						lableNodeBean.ICS_DOWN_SYMBOL = indexNodeBean.ICS_DOWN_SYMBOL;
						lableNodeBean.ICS_UP = indexNodeBean.ICS_UP;
						lableNodeBean.ICS_UP_SYMBOL = indexNodeBean.ICS_UP_SYMBOL;
						
						lableNodeBean.standardValue = getStandardFormUPAndDown(lableNodeBean.ICS_UP, lableNodeBean.ICS_DOWN,
								lableNodeBean.ICS_UP_SYMBOL, lableNodeBean.ICS_DOWN_SYMBOL, "");
					}
				}
				if(!exitFlag){//如果这个法规中的条目不存在标签集合中 就添加进去
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
	 *            上限
	 * @param down
	 *            下限
	 * @param upSymbol
	 *            上限符号
	 * @param downSymbol
	 *            下限符号
	 * @param detectValue
	 *            检测值描述
	 * @return
	 */
	public String getStandardFormUPAndDown(String up, String down, String upSymbol, String downSymbol,
			String detectValue) {
		String result = "";
		if (StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// up和down都为空则
			result = detectValue;
		}
		if (!StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// up和down都有值
			downSymbol = ">=".equals(downSymbol) ? "≤" : "＜";
			upSymbol = "<=".equals(upSymbol) ? "≤" : "＜";
			result = down+downSymbol+"," + upSymbol + up;// 这里一定是大于等于小于等于
		}
		if (!StringsUtil.isEmpty(up) && StringsUtil.isEmpty(down)) {// 只有up有值
			upSymbol = "<=".equals(upSymbol) ? "≤" : "＜";
			result = ","+upSymbol + up;
		}
		if (StringsUtil.isEmpty(up) && !StringsUtil.isEmpty(down)) {// 只有down有值
			downSymbol = ">=".equals(downSymbol) ? "≤" : "＜";
			result = downSymbol + down+",";
		}
		return result;
	}
	
	/**
	 * @param newStandard
	 * 根据标准拆分出来 up  down 和对应的符号 要回写的内容
	 */
	public void setUpAndDownBynewStandard(String newStandard) {
		//初始化数据
		detectValue = "";
		down = "";
		up = "";
		downSymbol = "";
		upSymbol = "";
		
		if(StringsUtil.isEmpty(newStandard)){//如果为空
			detectValue = "";
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
			return;
		}
		
		if(!newStandard.contains("≤") && !newStandard.contains("＜")){//不是 <,<这种格式 就是
			detectValue = newStandard;
			down = "";
			up = "";
			downSymbol = "";
			upSymbol = "";
			return;
		}
		if (!StringsUtil.isEmpty(newStandard)) {// 不为空
			String[] result = newStandard.split(",");
			
			//下限
			if(result[0].contains("≤")){
				down = result[0].split("≤")[0];//数字
				downSymbol = ">=";
			}else if(result[0].contains("＜")){
				down = result[0].split("＜")[0];//数字
				downSymbol = ">";
			}
			
			//上限
			if(result.length==2){
				if(result[1].contains("≤")){
					up = result[1].split("≤")[1];//数字
					upSymbol = "<=";
				}else if(result[1].contains("＜")) {
					up = result[1].split("＜")[1];//数字
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
	 * 根据名称创建一个营养包 并将版本返还回去
	 */
	@Override
	public TCComponentItemRevision createNutritionItemByName(
			String nutritionName) {
		
		//找到存放基粉配方的专一文件夹
		TCComponentFolder folder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query,new String[] { "名称", "描述", "类型" },
				new String[] { mNutritionFolderName, mNutritionFolderName,"Folder" });
		if (searchResult.length > 0) {
			folder = (TCComponentFolder) searchResult[0];
		}

		if (folder == null) {// 没有找到 创建
			query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
			String userName = UserInfoSingleFactory.getInstance().getTCSession().getUser().toString();
			searchResult = QueryUtil.getSearchResult(query, new String[] {"名称", "类型", "所有权用户" },
					new String[] { "Home","Fnd0HomeFolder", userName });
			for (TCComponent component : searchResult) {
				String type = component.getType();
				if ("Fnd0HomeFolder".equals(type)) {// 找到我们要找的文件夹
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
			// 创建营养包Item
			TCComponentItem nutritionItem = ItemUtil.createtItem(Const.ItemType.MATERIAL_ITEM, nutritionName, "");
			TCComponentItemRevision nutritionItemRevision = nutritionItem.getLatestItemRevision();
			//写上营养包的属性
			nutritionItemRevision.setProperty("u8_isNutrition", "True");
			
			// 添加到文件夹
			folder.add("contents", nutritionItem);

			return nutritionItemRevision;
		} catch (TCException e) {
			e.printStackTrace();
		}

		return null;
		
	}



	/* (non-Javadoc)
	 * 根据名称创建一个基粉配方 并且将版本返还回去
	 */
	@Override
	public TCComponentItemRevision createFormulatorItemByName(
			String formulatorName) {
		
		//找到存放基粉配方的专一文件夹
		TCComponentFolder folder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","描述","类型"}, new String[]{mFormulatorFolderName,mFormulatorFolderName,"Folder"} );
		if(searchResult.length>0){
			folder = (TCComponentFolder) searchResult[0];
		}
		
		if(folder==null){//没有找到 创建
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
				folder = ItemUtil.createFolder(mFormulatorFolderName, mFormulatorFolderName);
				homeFolder.add("contents", folder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		try {
			//创建基粉配方Item
			TCComponentItem formulatorItem = ItemUtil.createtItem(Const.ItemType.FORMUALTOR_ITEM, formulatorName, "");
			TCComponentItemRevision formulaItemRevision = formulatorItem.getLatestItemRevision();
			
			formulaItemRevision.setProperty("u8_isBaseFormulator", "True");//标注是基粉
			
			//添加到文件夹
			folder.add("contents", formulatorItem);
			
			return formulaItemRevision;
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}



	/* (non-Javadoc)
	 * 将孩子节点从父亲节点的集合中移除
	 */
	@Override
	public void removeChildFromParent(NodeBean parentNodeBean,
			NodeBean childNodeBean) {
		//正常情况下是不为空的
		if(parentNodeBean.childNodeBeans == null) parentNodeBean.childNodeBeans = new ArrayList<NodeBean>();
		if(parentNodeBean.chidNodes == null) parentNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
		
		//找到下标
		int index = -1;
		for(int i=0;i<parentNodeBean.childNodeBeans.size();i++){
			NodeBean nodeBean = parentNodeBean.childNodeBeans.get(i);
			if(nodeBean.itemID.equals(childNodeBean.itemID)){
				index = i;
				break;
			}
		}
		//找到孩子位置
		if(index != -1){
			parentNodeBean.chidNodes.remove(index);
			parentNodeBean.childNodeBeans.remove(index);
		}
	}
}
