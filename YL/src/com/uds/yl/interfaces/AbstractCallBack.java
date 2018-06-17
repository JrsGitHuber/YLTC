package com.uds.yl.interfaces;

import java.io.File;
import java.util.List;

import javax.swing.JTextField;

import org.eclipse.jface.preference.BooleanPropertyAction;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.MaterialBean;


public abstract class AbstractCallBack {
	//��ѯ���ķ���ͷ��������
	public void setLawAndName(String lawName,TCComponentItemRevision lawRev) {}
	
	//ѡ���ļ�
	public void setFilesAndType(String type,File[] files){}
	
	
	//ͨ����ͨ�����������
	public void setComment(String passOrNot,String comment,
			String box1,String box2,String box3,String box4,String box5)throws TCException{}
	
	//��ѯԭ�ϵĻص�����
	public void setMaterialItem(TCComponentItemRevision materialItemRev){}
	
	//�����䷽���������
	public void setCompelemnet(String complementType,String complementContent){	}
	
	//�̷��䷽�����������
	public void setLawRev(TCComponentItemRevision lawItemRev){}
	
	
	//�̷��䷽�������ָ��
	public void setIndexRev(TCComponentItemRevision indexItemRev){}
	
	
	//�鿴Ӫ�����Ľ���
	public void modifyNutritionRev(List<TCComponentItemRevision> materialList,List<MaterialBean> materialBeanList){}

	//����ʪ���䷽
	public void createWetFormulator(boolean selected ,String name){}

	
	//��ӻ���
	public void addWetFormulator(TCComponentItemRevision wetFormulator,String name){};
	
	//��ֺ��д��ǰ�û�ID
	public void setUserIdInProposalForm(String userID){};
	
	
	//����������׼������������������޵Ļص�
	public void setUpAndDownResult(String result){};
	
	//������׼���� ����һ���µ�ָ�����Ŀ
	public void addNewIndexResult(String indexName,String indexUnit){};

	//������׼��� �޸�һ��ָ�����Ŀ
	public void modifyIndexBeanResult(String indexIntroduce,String testGistEdt){};
	
	//��Ʒ��׼��� �޸����ڿر�׼����Ԥ��ֵ
	public void setStandardAndWaringResult(String standardResult,String waringResult,String indexTesStr,String indexIntroduceStr,String remarkStr){}
	
	//�̷��䷽����в���Ӫ�����Ļص�
	public void setNutritionName(String nutritionName){};
	
	//�̷��䷽����в�����ۻ��к�ˮ���Ļص�
	public void setFormulatorName(String formulatorName, String warterValue){};
}
