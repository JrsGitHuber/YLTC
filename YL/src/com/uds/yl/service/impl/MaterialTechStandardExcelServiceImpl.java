package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.common.Const;
import com.uds.yl.service.IMaterialTechStandardExcelService;
import com.uds.yl.tcutils.BomUtil;

public class MaterialTechStandardExcelServiceImpl implements IMaterialTechStandardExcelService{

	
	/* (non-Javadoc)
	 *��ȡ����������׼��topBomline 
	 */
	@Override
	public TCComponentBOMLine getTopBOMLine(TCComponentItemRevision itemRevision) {
		TCComponentBOMLine topBOMLine=null;
		topBOMLine=BomUtil.getTopBomLine(itemRevision, Const.MaterialorAccIndexStandard.BOMNAME);
		return topBOMLine;	
	}

	/* (non-Javadoc)
	 * ��ȡ����������׼�µ�����ָ�����Bom
	 */
	@Override
	public List<TCComponentBOMLine> getAllIndexBomList(TCComponentBOMLine topBomLine) {
		List<TCComponentBOMLine> allIndexBomList= new ArrayList<>();
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				allIndexBomList.add(bomLine);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	
		return allIndexBomList;
	}

	/* (non-Javadoc)
	 * ������������׼��Bomת��Ϊ��Ӧ��Bean����
	 */
	@Override
	public List<IndexItemBean> getAllIndexBeanList(List<TCComponentBOMLine> allIndexBomList) {
		List<IndexItemBean> allIndexBeanList = new ArrayList<>();
		for(TCComponentBOMLine bomLine : allIndexBomList){
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class, bomLine);
				allIndexBeanList.add(bean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return allIndexBeanList;
	}

	
	/* (non-Javadoc)
	 * �й�Ҫ��
	 */
	@Override
	public List<TCComponentBOMLine> getSensoryBomList(List<TCComponentBOMLine> allIndexBomList) {
		List<TCComponentBOMLine> bomList = new ArrayList<>();
		try {
			for(TCComponentBOMLine bomLine : allIndexBomList){
				TCComponentItemRevision itemRev = bomLine.getItem().getLatestItemRevision();
				String bomCategory = itemRev.getProperty("u8_category");
				if("�й�ָ��".equals(bomCategory)){
					bomList.add(bomLine);
				}
				
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return bomList;
		
	}

	/* (non-Javadoc)
	 * ��ָ��
	 */
	@Override
	public List<TCComponentBOMLine> getPhysicalBomList(List<TCComponentBOMLine> allIndexBomList) {
		List<TCComponentBOMLine> bomList = new ArrayList<>();
		try {
			for(TCComponentBOMLine bomLine : allIndexBomList){
				TCComponentItemRevision itemRev = bomLine.getItem().getLatestItemRevision();
				String bomCategory = itemRev.getProperty("u8_category");
				if("��ָ��".equals(bomCategory)){
					bomList.add(bomLine);
				}
				
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return bomList;
	}

	/* (non-Javadoc)
	 * ��Ⱦ��ָ��
	 */
	@Override
	public List<TCComponentBOMLine> getHealthBomList(List<TCComponentBOMLine> allIndexBomList) {
		List<TCComponentBOMLine> bomList = new ArrayList<>();
		try {
			for(TCComponentBOMLine bomLine : allIndexBomList){
				TCComponentItemRevision itemRev = bomLine.getItem().getLatestItemRevision();
				String bomCategory = itemRev.getProperty("u8_category");
				if("��Ⱦ��".equals(bomCategory)){//ʵ������Ⱦ��ָ��  ��ʱ����Ⱦ��
					bomList.add(bomLine);
				}
				
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return bomList;
	}

	/* (non-Javadoc)
	 * ΢����ָ��
	 */
	@Override
	public List<TCComponentBOMLine> getMicroorganismBomList(List<TCComponentBOMLine> allIndexBomList) {
		List<TCComponentBOMLine> bomList = new ArrayList<>();
		try {
			for(TCComponentBOMLine bomLine : allIndexBomList){
				TCComponentItemRevision itemRev = bomLine.getItem().getLatestItemRevision();
				String bomCategory = itemRev.getProperty("u8_category");
				if("΢����ָ��".equals(bomCategory)){
					bomList.add(bomLine);
				}
				
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return bomList;
	}

	/* (non-Javadoc)
	 * ��������
	 */
	@Override
	public List<TCComponentBOMLine> getOtherBomList(List<TCComponentBOMLine> allIndexBomList) {
		//����
		return null;
	}

	/* (non-Javadoc)
	 * �й�ָ��
	 */
	@Override
	public List<IndexItemBean> getSensoryBeanList(List<TCComponentBOMLine> mSensoryBomList) {
		List<IndexItemBean> beanList = new ArrayList<>();
		for(TCComponentBOMLine bomLine : mSensoryBomList){
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class,bomLine);
				beanList.add(bean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return beanList;
	}

	/* (non-Javadoc)
	 * ��ָ��
	 */
	@Override
	public List<IndexItemBean> getPhysicalBeanList(List<TCComponentBOMLine> mPhysicalBomList) {
		List<IndexItemBean> beanList = new ArrayList<>();
		for(TCComponentBOMLine bomLine : mPhysicalBomList){
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class,bomLine);
				beanList.add(bean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return beanList;
	}

	/* (non-Javadoc)
	 * ��Ⱦ��ָ��
	 */
	@Override
	public List<IndexItemBean> getHealthBeanList(List<TCComponentBOMLine> mHealthBomList) {
		List<IndexItemBean> beanList = new ArrayList<>();
		for(TCComponentBOMLine bomLine : mHealthBomList){
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class,bomLine);
				beanList.add(bean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return beanList;
	}

	
	/* (non-Javadoc)
	 * ΢����ָ��
	 */
	@Override
	public List<IndexItemBean> getMicroorganismBeanList(List<TCComponentBOMLine> mMicroorganismBomList) {
		List<IndexItemBean> beanList = new ArrayList<>();
		for(TCComponentBOMLine bomLine : mMicroorganismBomList){
			try {
				IndexItemBean bean = AnnotationFactory.getInstcnce(IndexItemBean.class,bomLine);
				beanList.add(bean);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return beanList;
	}

	/* (non-Javadoc)
	 * ����ָ��
	 */
	@Override
	public List<IndexItemBean> getOtherBeanList(List<TCComponentBOMLine> mOtherBomList) {
		//�����Ŀ����ݲ�����
		return null;
	}

	  
	
}
