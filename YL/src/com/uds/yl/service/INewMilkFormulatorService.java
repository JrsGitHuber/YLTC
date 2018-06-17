package com.uds.yl.service;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.NodeBean;

public interface INewMilkFormulatorService {
	
	//根据选中的版本对象初始化树
	public NodeBean initRootNode(TCComponentItemRevision rootItemRevsion);
	
	//根据选中的版本对象初始化树
	public NodeBean initRootNode(TCComponentItemRevision rootItemRevsion, String bomType);
	
	//获取该版本对象的节点类型
	public String getNodeType(TCComponentItemRevision nodeRevision);
	
	//判断整个版本对象 是不是营养包类型
	public boolean isNitrition(TCComponentItemRevision itemRevision);
	

	//根据 原料的类型 获取相关的原料
	public List<NodeBean> getMaterialByType(String materialType);
	
	//根据根节点更新mFormulatorTableNodeBeanList中的值
	public List<NodeBean> getFormulatorTableNodeBeanListByRoot(NodeBean rootBean);
	
	//根据节点更新 mIndexTableNodeBeanList 中的值
	public List<NodeBean> getIndexTableNodeBeanListBySelectedMaterialNode(NodeBean selectedMaterialNodeBean);
	
	//根据选中的配方获取所有的标签项条目 法规 标准 和 冷饮营养标签 视图中
	public List<NodeBean> getLableTableNodeBeanListByFormulatorRev(TCComponentItemRevision formulatorRevsion);

	//根据营养包的名称创建一个营养包
	public TCComponentItemRevision createNutritionItemByName(String nutritionName);
	
	//根据配方的名称创建一个基粉配方
	public TCComponentItemRevision createFormulatorItemByName(String formulatorName);
	
	//将孩子节点从父节点中的数组中移除
	public void removeChildFromParent(NodeBean parentNodeBean, NodeBean childNodeBean);
}
