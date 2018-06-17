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
	 * �����䷽�汾��ȡ���BOm�ļ���
	 * Ĭ�ϻ�ȡ����Ӫ����ǩ
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
				
				AIFComponentContext[] bomChild = bomLine.getChildren();//����е�ԭ��
				for(AIFComponentContext childContext : bomChild ){
					TCComponentBOMLine materialBomLine = (TCComponentBOMLine) childContext.getComponent();
					TCComponentItemRevision materialRev = materialBomLine.getItemRevision();
					TCComponentBOMLine  childBomLine= BomUtil.getTopBomLine(materialRev, "����Ӫ����ǩ");
					componentBom.childBomList.add(childBomLine);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return componentBomLineList;
	}

	/* (non-Javadoc)
	 * �����䷽��ȡ���ʵ����ļ���
	 */
	@Override
	public List<ComponenetBean> getComponentBeanList(TCComponentItemRevision formulatorRev) {
		List<ComponenetBean> componentBeansList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, Const.ColdFormula.BOMNAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
		}
		try {
			//��ȡ���
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context:children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				ComponenetBean componenetBean = new ComponenetBean();
				componenetBean.component = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				//TODO:���� componentType ������ֵ�����	componenetBean.complementType = ""
				componenetBean.childBeanList = new ArrayList<>();//ԭ����
				
//				componenetBean.childMinBeanList = new ArrayList<>();//С����  С����������Բ�ʹ����ʱ
				
				
				//��ȡ����µĺ���  ��Ϊԭ�Ϻ�С��
				AIFComponentContext[] bomChild = bomLine.getChildren();
				for(AIFComponentContext childContext : bomChild ){
					TCComponentBOMLine childBomLine = (TCComponentBOMLine) childContext.getComponent();
					//������µĵ���ԭ�Ϻ����϶�����ԭ�ϴ�����
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, childBomLine);
					componenetBean.childBeanList.add(materialBean);
					
					//��ԭ��(����ԭ�Ϻ�С��)�µ�ָ����Ϣ��ȡ�õ�
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
		//TODO:��Ҫ�е����������С���Ǻ�ԭ�ϵĽṹ��һ����˵
		boolean flag = true;
		try {
			AIFComponentContext[] children = bomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine tempBomLine = (TCComponentBOMLine) context.getComponent();
				String type = tempBomLine.getItem().getType();
				if("U8_Material".equals(type)){
					//ԭ�����溬��ԭ��˵���ǲ���һ��������ԭ��
					flag =  false;
				}else if("U8_IndexItem".equals(type)){
					//����ָ��Ͳ��ô�����
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * ��ȥС����������е�ԭ�϶���
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
				if("U8_Material".equals(type)){//ԭ�������ԭ������
					MaterialBean bean =  AnnotationFactory.getInstcnce(MaterialBean.class, tempBomLine);
					allMaterialList.add(bean);
				}else if("U8_IndexItem".equals(type)){//ָ�겻����
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
	 * ��������ȵõ�һ��ԭ�ϻ�ȡ�������ָ����Ϣ (��:�����������ʣ�֬����̼ˮ������ȵ�)
	 */
	
	private void getIndexBean(TCComponentBOMLine rootBomLine,MaterialBean rootMaterialBean){
		boolean isMaterialFlag = isMaterail(rootBomLine);
		if(isMaterialFlag){//Ϊ�����ԭ��
			//ԭ������ֱ�Ӿ���ָ��
			List<IndexItemBean> indexBeanList = new ArrayList<>();
			try {
				AIFComponentContext[] contexts = rootBomLine.getChildren();
				for(AIFComponentContext context : contexts){
					TCComponentBOMLine indexBom  = (TCComponentBOMLine) context.getComponent();
					IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBom);
					indexBeanList.add(indexItemBean);
				}
				
				rootMaterialBean.indexBeanList = indexBeanList;//��ָ�긳����ԭ������
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}else{//��С��
			//С������ֵ���е����ԭ�ϡ�����ֱ�ӵĺ���ָ����Ŀ 
			//TODO:Ŀǰû�п��ǵ�������С�ϵ�˵
			List<IndexItemBean> indexBeanList = new ArrayList<>();
			try {
				AIFComponentContext[] children = rootBomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine childMaterialBom = (TCComponentBOMLine) context.getComponent();//С�������ԭ��
					//u8Uom��λ    bl_quantity���
					AIFComponentContext[] indexBomContext = childMaterialBom.getChildren();
					for(AIFComponentContext indexContext : indexBomContext){//����С���µ�ԭ���µ�ָ��
						TCComponentBOMLine indexBom = (TCComponentBOMLine) indexContext.getComponent();
						IndexItemBean indexItemBean = AnnotationFactory.getInstcnce(IndexItemBean.class, indexBom);
						computeIndexItemBean(indexBeanList,indexItemBean);//ÿһ��ָ�궼Ҫ�����жϿ��Ƿ�Ҫ�ۼ�
					}
				}
				rootMaterialBean.indexBeanList = indexBeanList;//��ָ�긳����ԭ������
			} catch (TCException | InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��С���µ�ָ����Ŀ���г�ȡ�ۼ�
	 */
	private void computeIndexItemBean(List<IndexItemBean> indexBeanList,IndexItemBean indexBean){
		//����ָ���Ƿ���ڼ�����
		boolean exitFlag = false;
		IndexItemBean exitIndexBean = null;
		for(IndexItemBean bean : indexBeanList){
			if(bean.objectName.equals(indexBean.objectName)){//ƥ�����˾�˵������
				exitIndexBean = bean;
				exitFlag = true;
				break;
			}
		}
		if(exitFlag){//���ھ�ֱ�ӻ�ȡ ���ۼ�
			Double uTomExit = StringsUtil.convertStr2Double(exitIndexBean.u8Uom);//�����д��ڵĵĵ�λ
			Double qualityExit = StringsUtil.convertStr2Double(exitIndexBean.bl_quantity)/100;//�����д��ڵ����������ǰٷֱ�
			
			Double uTomACC = StringsUtil.convertStr2Double(indexBean.u8Uom);//Ҫ�ۼӵ�
			Double qualityACC = StringsUtil.convertStr2Double(indexBean.bl_quantity)/100;//Ҫ�ۼӵ����
			
			Double uTom = uTomExit*qualityExit + uTomACC*qualityACC;
			Double quality = 0.0;
			
			exitIndexBean.u8Uom = uTom+"";
			exitIndexBean.bl_quantity =  quality+"";
		}else{//������ֱ����ӽ�ȥ
			indexBeanList.add(indexBean);
		}
	}

	/* (non-Javadoc)
	 * ����һ����ʱ���䷽����
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
			TCComponent[] searchResult = QueryUtil.getSearchResult(query,new String[]{"����","����Ȩ�û�","����Ȩ��"}, new String[]{"TempFormula",userId,group});
			
			
			boolean itemExit = false;
			for(TCComponent component : searchResult){
				if(component.getType().equals("U8_Formula")&&
						component.getProperty("object_name").equals("TempFormula")&&
						component.getProperty("object_desc").equals(userId)
						){//��ǰ��¼�û���ID
					cacheItem = (TCComponentItem) component;
					itemExit = true;
					break;
				}
			}
			
			//����һ���䷽Item�����̶����ļ��л���
			TCComponentFolder tempFormulatorFolder = getTempFormulatorFolder();
			if(!itemExit){//itemû�д��� �ʹ���һ��
				//Ϊ�վʹ���
				cacheItem = ItemUtil.createtItem("U8_Formula", "TempFormula", userId);
				tempFormulatorFolder.add("contents", cacheItem);
			}else {
			}
			itemRevision = cacheItem.getLatestItemRevision();
			List<TCComponentBOMLine> bomMaterialBomLineList = new ArrayList<>();

			topBomLine = BomUtil.getTopBomLine(itemRevision, Const.Formulator.MATERIALBOMNAME);
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
			
			//==========================
			
			//���䷽�汾�����bom���
			
			//�ȴ�����ֵ�bom��ͼ
			for(int i=0;i<formulatorBeanList.size();i++){
				ComponenetBean componenetBean = formulatorBeanList.get(i);
				ComponentBom componentBom = formulatorBomList.get(i);
				TCComponentItemRevision componentRev = componentBom.componentBOMLine.getItemRevision();
				
				//��ӳɹ��� ������ͼչʾ��BOMLine��ֵ����ֵĶ����˵
				TCComponentBOMLine componentBOMLine = topBomLine.add(componentRev.getItem(), componentRev, null, false);
				AnnotationFactory.setObjectInTC(componenetBean.component, componentBOMLine);//�����BOM���и�ֵ
				componentBom.componentBOMLine = componentBOMLine;
				
				//���������BOMLine�µ�����ԭ��Ȼ�����ٽ����ʵ�����к���ԭ�϶�����ֵ��ȥ
				children = componentBOMLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					bomLine.cut();
				}
				int size = componentBom.childBomList.size();
				for(int j=0;j<size;j++){
					//��Ҫ����һ������и�ԭ�ϵĺ���  �е��ǲ�ˮ��1000 ��ֿ�������100kg 
					TCComponentItemRevision materialRev = componentBom.childBomList.get(j).getItemRevision();
					TCComponentBOMLine materialBom = componentBOMLine.add(materialRev.getItem(), materialRev, null, false);
					
					Double materialQuantity =  StringsUtil.convertStr2Double(componenetBean.childBeanList.get(i).bl_quantity);
					Double componentInventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
					componenetBean.childBeanList.get(i).U8_inventory =materialQuantity /100 * componentInventory+"";
					
					AnnotationFactory.setObjectInTC(componenetBean.childBeanList.get(j), materialBom);//��ԭ��BOM��ֵ
					componentBom.childBomList.add(materialBom);
					
					computeMaterialBom(materialBom);//�����������е�ԭ����Ϣ
					
					
				}
				size = componentBom.childBomList.size();
				for(int j=0;j<size/2;j++){//���淭������������Ҫɾ������Ĳ���
					componentBom.childBomList.remove(0);//˳���ɾ��һ��ĸ���
				}
			}
			
			//����bom
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
	 * ��������µ�ԭ��BOM �����ڲ��Ͷ���� һ��һ����� ���ռ��㵽ָ��
	 */
	public void computeMaterialBom(TCComponentBOMLine materialBomLine){
		Queue<TCComponentBOMLine> bomQueue = new LinkedList<>();
		bomQueue.offer(materialBomLine);//��ʼ��
		
		while(!bomQueue.isEmpty()){//��Ϊ��
			try {
				TCComponentBOMLine bomLine = bomQueue.poll();
				Double invnetory = StringsUtil.convertStr2Double(bomLine.getProperty("U8_inventory"));
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomChild = (TCComponentBOMLine) context.getComponent();
					Double childQuantity = StringsUtil.convertStr2Double(bomChild.getProperty("bl_quantity"));
					Double childInvnetory = childQuantity  * invnetory;
					bomChild.setProperty("U8_inventory", childInvnetory+"");
					
					//������к��Ӿ�Ҫ����������ȥ
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
	 * ����һ�̶����ļ���
	 * @return
	 */
	public TCComponentFolder getTempFormulatorFolder() {
		TCComponentFolder tempFormulatorFolder = null;
		TCComponentFolder homeFolder = null;
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.GENERAL.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����","����","����"}, new String[]{"FormulatorTEMP","FormulatorTEMP","Fnd0HomeFolder"} );
		if(searchResult.length>0){
			tempFormulatorFolder = (TCComponentFolder) searchResult[0];
		}
		
		if(tempFormulatorFolder==null){//û���ҵ� ����
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
				tempFormulatorFolder = ItemUtil.createFolder("FormulatorTEMP", "FormulatorTEMP");
				homeFolder.add("contents", tempFormulatorFolder);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		return tempFormulatorFolder;
	}
}
