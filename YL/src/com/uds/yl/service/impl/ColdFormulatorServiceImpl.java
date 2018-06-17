package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.service.IColdFormulatorService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.ui.ColdFormulatorFrame.ComponenetBean;
import com.uds.yl.ui.ColdFormulatorFrame.ComponentBom;
import com.uds.yl.utils.StringsUtil;

public class ColdFormulatorServiceImpl implements IColdFormulatorService{

	/* (non-Javadoc)
	 * 根据配方版本获取组分BOm的集合
	 * 默认获取的是营养标签
	 */
	@Override
	public List<ComponentBom> getComponentBomLineList(TCComponentItemRevision formulatorRev) {
		List<ComponentBom> componentBomLineList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, Const.ColdFormula.BOMNAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context:children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ComponentBom componentBom = new ComponentBom();
				componentBom.componentBOMLine = bomLine;
				componentBom.childBomList = new ArrayList<>();
				componentBomLineList.add(componentBom);
				
				AIFComponentContext[] bomChild = bomLine.getChildren();//组分中的原料
				for(AIFComponentContext childContext : bomChild ){
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) childContext.getComponent();
					TCComponentItemRevision materialRev = materialBomLine.getItemRevision();
					TCComponentBOMLine  childBomLine= BomUtil.getTopBomLine(materialRev, "冷饮营养标签");
					componentBom.childBomList.add(childBomLine);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return componentBomLineList;
	}

	/* (non-Javadoc)
	 * 根据配方获取组分实体类的集合
	 */
	@Override
	public List<ComponenetBean> getComponentBeanList(TCComponentItemRevision formulatorRev) {
		List<ComponenetBean> componentBeansList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, Const.ColdFormula.BOMNAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			//获取组分
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context:children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ComponenetBean componenetBean = new ComponenetBean();
				componenetBean.component = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				//TODO:属性 componentType 代表组分的类型	componenetBean.complementType = ""
				componenetBean.childBeanList = new ArrayList<>();//原料组
				
//				componenetBean.childMinBeanList = new ArrayList<>();//小料组  小料组这个属性不使用暂时
				
				
				//获取组分下的孩子  分为原料和小料
				AIFComponentContext[] bomChild = bomLine.getChildren();
				for(AIFComponentContext childContext : bomChild ){
					TCComponentBOMLine childBomLine = (TCComponentBOMLine) childContext.getComponent();
					//将组分下的单层原料和下料都当做原料处理了
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, childBomLine);
					componenetBean.childBeanList.add(materialBean);
					
					//将原料(单个原料和小料)下的指标信息获取得到
				}
				
				componentBeansList.add(componenetBean);
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return componentBeansList;
	}

	
	/**
	 * 
	 * @param bomLine
	 * @return
	 */
	private boolean isMaterail(TCComponentBOMLine bomLine){
		//TODO:需要有单层的买来的小料是和原料的结构是一样的说
		boolean flag = true;
		try {
			AIFComponentContext[] children = bomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context.getComponent();
				String type = tempBomLine.getItem().getType();
				if("U8_Material".equals(type)){
					//原料下面含有原料说明是不是一个单独的原料
					flag =  false;
				}else if("U8_IndexItem".equals(type)){
					//含有指标就不用处理了
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 获去小料下面的所有的原料对象
	 * @param bomLine
	 * @return
	 */
	private List<MaterialBean> getMinMaterialChilds(TCComponentBOMLine bomLine){
		List<MaterialBean> allMaterialList = new ArrayList<>();
		try {
			AIFComponentContext[] children = bomLine.getChildren();
			for(AIFComponentContext context:children){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context.getComponent();
				String type = tempBomLine.getItem().getType();
				if("U8_Material".equals(type)){//原料下面的原料类型
					MaterialBean bean =  AnnotationFactory.getInstcnce(MaterialBean.class, tempBomLine);
					allMaterialList.add(bean);
				}else if("U8_IndexItem".equals(type)){//指标不处理
				}
			}

		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return allMaterialList;
	}

	/**
	 * @param rootBomLine
	 * @param rootMaterialBean 
	 * 根据组分先得第一层原料获取其下面的指标信息 (如:能量，蛋白质，脂肪，碳水化合物等等)
	 */
	
	private void getIndexBean(TCComponentBOMLine rootBomLine,MaterialBean rootMaterialBean){
		boolean isMaterialFlag = isMaterail(rootBomLine);
		if(isMaterialFlag){//为真就是原料
			//原料下面直接就是指标
			List<IndexItemBean> indexBeanList = new ArrayList<>();
			try {
				AIFComponentContext[] contexts = rootBomLine.getChildren();
				for(AIFComponentContext context : contexts){
					TCComponentBOMLine indexBom  = (TCComponentBOMLine) context.getComponent();
					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBom);
					indexBeanList.add(indexItemBean);
				}
				
				rootMaterialBean.indexBeanList = indexBeanList;//将指标赋到跟原料下面
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{//是小料
			//小料下面值含有单层的原料。或者直接的含有指标项目 
			//TODO:目前没有考虑到买来的小料的说
			List<IndexItemBean> indexBeanList = new ArrayList<>();
			try {
				AIFComponentContext[] children = rootBomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine childMaterialBom = (TCComponentBOMLine) context.getComponent();//小料下面的原料
					//u8Uom单位    bl_quantity配比
					AIFComponentContext[] indexBomContext = childMaterialBom.getChildren();
					for(AIFComponentContext indexContext : indexBomContext){//遍历小料下的原料下的指标
						TCComponentBOMLine indexBom = (TCComponentBOMLine) indexContext.getComponent();
						IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBom);
						computeIndexItemBean(indexBeanList,indexItemBean);//每一个指标都要进行判断看是否要累加
					}
				}
				rootMaterialBean.indexBeanList = indexBeanList;//将指标赋到跟原料下面
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 对小料下的指标项目进行抽取累加
	 */
	private void computeIndexItemBean(List<IndexItemBean> indexBeanList,IndexItemBean indexBean){
		//看该指标是否存在集合中
		boolean exitFlag = false;
		IndexItemBean exitIndexBean = null;
		for(IndexItemBean bean : indexBeanList){
			if(bean.objectName.equals(indexBean.objectName)){//匹配上了就说明存在
				exitIndexBean = bean;
				exitFlag = true;
				break;
			}
		}
		if(exitFlag){//存在就直接获取 并累加
			Double uTomExit = StringsUtil.convertStr2Double(exitIndexBean.u8Uom);//集合中存在的的单位
			Double qualityExit = StringsUtil.convertStr2Double(exitIndexBean.bl_quantity)/100;//集合中存在的配比这个才是百分比
			
			Double uTomACC = StringsUtil.convertStr2Double(indexBean.u8Uom);//要累加的
			Double qualityACC = StringsUtil.convertStr2Double(indexBean.bl_quantity)/100;//要累加的配比
			
			Double uTom = uTomExit*qualityExit + uTomACC*qualityACC;
			Double quality = 0.0;
			
			exitIndexBean.u8Uom = uTom+"";
			exitIndexBean.bl_quantity =  quality+"";
		}else{//不存在直接添加进去
			indexBeanList.add(indexBean);
		}
	}

	/* (non-Javadoc)
	 * 创建一个临时的配方对象
	 */
	@Override
	public TCComponentBOMLine getCacheTopBomLine(
			List<ComponentBom> formulatorBomList,List<ComponenetBean> formulatorBeanList) {
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
				ComponenetBean componenetBean = formulatorBeanList.get(i);
				ComponentBom componentBom = formulatorBomList.get(i);
				TCComponentItemRevision componentRev = componentBom.componentBOMLine.getItemRevision();
				
				//添加成功后 将在视图展示的BOMLine赋值给组分的对象的说
				TCComponentBOMLine componentBOMLine = topBomLine.add(componentRev.getItem(), componentRev, null, false);
				AnnotationFactory.setObjectInTC(componenetBean.component, componentBOMLine);//对组分BOM进行赋值
				componentBom.componentBOMLine = componentBOMLine;
				
				//清空这个组分BOMLine下的所有原料然后在再将组分实体类中孩子原料都给赋值上去
				children = componentBOMLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					bomLine.cut();
				}
				int size = componentBom.childBomList.size();
				for(int j=0;j<size;j++){
					//还要计算一下组分中各原料的含量  有的是补水到1000 组分可能用了100kg 
					TCComponentItemRevision materialRev = componentBom.childBomList.get(j).getItemRevision();
					TCComponentBOMLine materialBom = componentBOMLine.add(materialRev.getItem(), materialRev, null, false);
					
					Double materialQuantity =  StringsUtil.convertStr2Double(componenetBean.childBeanList.get(i).bl_quantity);
					Double componentInventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
					componenetBean.childBeanList.get(i).U8_inventory =materialQuantity /100 * componentInventory+"";
					
					AnnotationFactory.setObjectInTC(componenetBean.childBeanList.get(j), materialBom);//对原料BOM赋值
					componentBom.childBomList.add(materialBom);
					
					computeMaterialBom(materialBom);//计算这个组分中的原料信息
					
					
				}
				size = componentBom.childBomList.size();
				for(int j=0;j<size/2;j++){//上面翻倍所以在这里要删除多余的部分
					componentBom.childBomList.remove(0);//顺序的删除一半的个数
				}
			}
			
			//保存bom
			TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
			bomWindow.refresh();
			bomWindow.save();
			bomWindow.close();
			topBomLine.refresh();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topBomLine;
		
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
					Double childQuantity = StringsUtil.convertStr2Double(bomChild.getProperty("bl_quantity"));
					Double childInvnetory = childQuantity  * invnetory;
					bomChild.setProperty("U8_inventory", childInvnetory+"");
					
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
	
	
	
	/**
	 * 创建一固定的文件夹
	 * @return
	 */
	public TCComponentFolder getTempFormulatorFolder() {
		TCComponentFolder tempFormulatorFolder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称","描述","类型"}, new String[]{"FormulatorTEMP","FormulatorTEMP","Fnd0HomeFolder"} );
		if(searchResult.length>0){
			tempFormulatorFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(tempFormulatorFolder==null){//没有找到 创建
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
				tempFormulatorFolder = ItemUtil.createFolder("FormulatorTEMP", "FormulatorTEMP");
				homeFolder.add("contents", tempFormulatorFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return tempFormulatorFolder;
	}
}
