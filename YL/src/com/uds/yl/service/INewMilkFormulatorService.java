package com.uds.yl.service;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.NodeBean;

public interface INewMilkFormulatorService {
	
	//����ѡ�еİ汾�����ʼ����
	public NodeBean initRootNode(TCComponentItemRevision rootItemRevsion);
	
	//����ѡ�еİ汾�����ʼ����
	public NodeBean initRootNode(TCComponentItemRevision rootItemRevsion, String bomType);
	
	//��ȡ�ð汾����Ľڵ�����
	public String getNodeType(TCComponentItemRevision nodeRevision);
	
	//�ж������汾���� �ǲ���Ӫ��������
	public boolean isNitrition(TCComponentItemRevision itemRevision);
	

	//���� ԭ�ϵ����� ��ȡ��ص�ԭ��
	public List<NodeBean> getMaterialByType(String materialType);
	
	//���ݸ��ڵ����mFormulatorTableNodeBeanList�е�ֵ
	public List<NodeBean> getFormulatorTableNodeBeanListByRoot(NodeBean rootBean);
	
	//���ݽڵ���� mIndexTableNodeBeanList �е�ֵ
	public List<NodeBean> getIndexTableNodeBeanListBySelectedMaterialNode(NodeBean selectedMaterialNodeBean);
	
	//����ѡ�е��䷽��ȡ���еı�ǩ����Ŀ ���� ��׼ �� ����Ӫ����ǩ ��ͼ��
	public List<NodeBean> getLableTableNodeBeanListByFormulatorRev(TCComponentItemRevision formulatorRevsion);

	//����Ӫ���������ƴ���һ��Ӫ����
	public TCComponentItemRevision createNutritionItemByName(String nutritionName);
	
	//�����䷽�����ƴ���һ�������䷽
	public TCComponentItemRevision createFormulatorItemByName(String formulatorName);
	
	//�����ӽڵ�Ӹ��ڵ��е��������Ƴ�
	public void removeChildFromParent(NodeBean parentNodeBean, NodeBean childNodeBean);
}
