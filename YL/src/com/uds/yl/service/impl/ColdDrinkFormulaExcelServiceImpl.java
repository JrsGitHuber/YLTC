package com.uds.yl.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.apache.regexp.recompile;
import org.jacorb.idl.runtime.int_token;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.Const.ColdDrinkFormulaExcel;
import com.uds.yl.service.IColdDrinkFormulaExcelService;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.utils.StringsUtil;

public class ColdDrinkFormulaExcelServiceImpl implements IColdDrinkFormulaExcelService {

	/* (non-Javadoc)
	 * 	��ȡ�������ϵ���������
	 */
	@Override
	public List<String> getAllWillSelectedMaterialNameList(TCComponentItemRevision itemRev) {
		List<String> nameList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.ColdDrinkFormulaExcel.BOMNAME);
		if(topBomLine==null){
			return null;
		}
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(int i =0;i<children.length;i++){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
				if(bomLine.getItem().getType().equals("U8_Material")){//ԭ�����͵�
					String name = bomLine.getItem().getProperty("object_name");
					nameList.add(name);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return nameList;
	}

	/* (non-Javadoc)
	 * ��ȡ�������ϵ�Bom����
	 */
	@Override
	public List<TCComponentBOMLine> getAllWillSelectedMaterialBomList(TCComponentItemRevision itemRev) {
		
		List<TCComponentBOMLine> bomList = new ArrayList<>();
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRev, Const.ColdDrinkFormulaExcel.BOMNAME);
		if(topBomLine==null){
			return null;
		}
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				if(bomLine.getItem().getType().equals("U8_Material")){//ԭ�����͵�
					bomList.add(bomLine);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return bomList;
	}

	/**
	 * ����ѡ�е�BomLine����ȡ����ĵ����BomLine��ֵ
	 * @param selectBomLine
	 * @return
	 */
	@Override
	public List<MaterialBean> getSingleMaterialBeanList(TCComponentBOMLine selectBomLine) {
		List<MaterialBean> singleMaterialBeanList = new ArrayList<>();
		try {
			AIFComponentContext[] children = selectBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				String type = bomLine.getItem().getType();
				String minMaterialType = bomLine.getProperty("U8_minmaterial");
				if("U8_Material".equals(type)&&StringsUtil.isEmpty(minMaterialType)){//��ԭ��,����û��С�����ͱ�ʶ
					MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
					singleMaterialBeanList.add(materialBean);
				}
			}
			
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return singleMaterialBeanList;
	}

	/* (non-Javadoc)
	 * ��������ҵ�С��������һ�µ�ԭ��  ����һ��С�� 
	 */
	@Override
	public List<MinMaterialBean> getComplexMaterialBeanList(TCComponentBOMLine selectBomLine) {
		List<MinMaterialBean> complexMaterialBeanList = new ArrayList<>();
		
		HashSet<String> minMaterialTypeSet = new HashSet<String>();
		try {
			AIFComponentContext[] children = selectBomLine.getChildren();//��ֵĺ���
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				String type = bomLine.getItem().getType();
				if("U8_Material".equals(type)){//��ԭ��
					String minMaterialType = bomLine.getProperty("U8_minmaterial");
					if(!StringsUtil.isEmpty(minMaterialType)){//�����С�ϵı��
						minMaterialTypeSet.add(minMaterialType);
					}
				}
			}
			
			
			//��ʼ��֯С��
			Iterator<String> iterator = minMaterialTypeSet.iterator();
			while(iterator.hasNext()){
				String name = iterator.next();
				MinMaterialBean minMaterialBean = new MinMaterialBean();
				minMaterialBean.name = name;
				minMaterialBean.allChildsMaterial = new ArrayList<MaterialBean>();
				complexMaterialBeanList.add(minMaterialBean);
				
				for(AIFComponentContext context : children){
					TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
					String type = bomLine.getItem().getType();
					if("U8_Material".equals(type)){//��ԭ��
						String minMaterialType = bomLine.getProperty("U8_minmaterial");
						if(!StringsUtil.isEmpty(minMaterialType)&&minMaterialType.equals(name)){//�����С������ ����û����set��
							MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class,bomLine);
							minMaterialBean.allChildsMaterial.add(materialBean);
						}
					}
				}
			}
			
			
			//�������С���ܵ�Ͷ����
			for(MinMaterialBean minMaterialBean : complexMaterialBeanList){
				Double sumInventory = 0d;
				for(MaterialBean materialBean : minMaterialBean.allChildsMaterial){
					Double inventory = StringsUtil.convertStr2Double(materialBean.U8_inventory);
					sumInventory += inventory;
				}
				minMaterialBean.inventory = sumInventory+"";
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return complexMaterialBeanList;
	}
	
	
	/**
	 * �жϸ�BOMLine�ĺ�ֽ�Ƿ���ԭ�����͵�
	 * @param waitCheckBomLine
	 * @return
	 */
	public boolean hasMaterialBom(TCComponentBOMLine waitCheckBomLine){
		try {
			AIFComponentContext[] children = waitCheckBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				String type = bomLine.getItem().getType();
				if("U8_Material".equals(type)&&bomLine.hasChildren()){//��ԭ�Ϻ���
					return true;
				}	
			}
			
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		return false;//������˵��û��ԭ�Ϻ�ֽ
	}
}
