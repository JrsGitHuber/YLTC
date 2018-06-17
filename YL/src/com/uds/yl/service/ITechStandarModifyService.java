package com.uds.yl.service;

import java.util.List;
import java.util.Set;

import javax.swing.JTable;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.uds.yl.bean.TechStandarTableBean;


public interface ITechStandarModifyService {

	//����itemId��ȡ��Ӧ�ķ���İ汾
//	public List<TCComponentItemRevision> getSearchLawItemRevisionList(String itemId,String name);
	
	//����ָ������ ������ ����ָ��İ汾
	public List<TCComponentItemRevision> getSearchIndexItemRevsionList(String indexType,String indexName);
	
	
	//��ȡѡ�е�����������׼�ͷ����е����еļ���ָ������Ƽ���
	public Set<String> getAllIndexItemNames(TCComponentItemRevision revision,List<TCComponentItemRevision> selectLawList);
	
	//���ݰ汾�Ż�ȡ��һ���汾�ŵ�����������׼�İ汾
	public TCComponentItemRevision getOriginRev(String revNum,String revItemID);
	
	//�����������������еİ汾���й��ˣ��õ�����TechTabelBean����
	public List<TechStandarTableBean> getAllTableBeans(List<TCComponentItemRevision> allRevs,boolean hasPreRev,List<TechStandarTableBean> indexBeanList);
	
	//��֤�߼�
	public boolean vertifyStandardIsOk(List<TechStandarTableBean> allTechTableBean,JTable table);
	
	//�������Ƿ�����һ���汾���д���
	public void writeBack2Tc(TCComponentItemRevision techItemRev,List<TechStandarTableBean> allTechBeans,List<TCComponentItemRevision> allItemRevision,boolean hasPreRev);
	
	//��ѡ�еļ�����׼�İ汾�е�BOM��Ϣд��tableBeans��ȥ����newStandard����
	public void getNewStatdard(TCComponentItemRevision itemRev,List<TechStandarTableBean> techTableBeanList);
	
	//��ȡѡ�м�����׼�е�ָ���Bean  ���ƺ͵�λ
	public List<TechStandarTableBean> getIndexFormSelectedIndexRev(TCComponentItemRevision itemRevision,int lawsNum);
}
