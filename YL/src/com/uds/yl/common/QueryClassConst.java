package com.uds.yl.common;


public enum QueryClassConst {
	GENERAL("General..."),//�����ѯ��
	U8SCHEDULE("U8_Schedule"),//�Զ���Ĳ�ѯʱ���Ĳ�ѯ
	U8TASK("U8_Task"),//�Զ���Ĳ�ѯʱ�������Ĳ�ѯ
	U8MATERIAL("U8_MaterialRevision"),//����ԭ�ϰ汾  ԭ�Ͽ��е� 
	U8_MATERIAL_USEDINLAW("U8_MaterialRevision(UsedInLaw)"),//����ԭ�ϰ汾 ��׼��� ���浼���ǵ�ԭ��
	U8_MATERIAL_NULL_USEDINLAW("U8_MaterialRevisionNull(UsedInLaw)"),//����ԭ�ϰ汾   ��׼��ͷ��浼���ǵ�ԭ�� �汾�ϵĵ�λΪ��
	U8_INDEXITEM_USEDINLAW("U8_IndexItemRevision(UsedInLaw)"),//����ָ��汾  ��׼�����
	U8_INDEXITEM_USEDINLAW_FOR_IMPORT("U8_IndexItemRevision(UsedInLaw)ForImport"),//����ָ��汾  ���浼���ǵ�ָ��
	U8_INDEXITEM_NULL_USEDINLAW("U8_IndexItemRevisionNull(UsedInLaw)"),//����ָ��汾  ��׼��� �汾�ϵĵ�λΪ��
	U8_INDEXITEM_NULL_USEDINLAW_FOR_IMPORT("U8_IndexItemRevisionNull(UsedInLaw)ForImport"),//����ָ��汾 ���浼���ָ��  �汾�ϵĵ�λΪ��
	U8_LawRevsion("U8_LawRevsion"),//��ѯ���汾�汾   ͨ��  ��ϵID
	U8_LawItem("U8_Law"),//��ѯ����  ����Item_id��ѯ���� ����item   
	U8_INDEX("U8_Index"),//������׼��ѯ ��ѯ���ǰ汾
	U8_PROJECT("U8_Project"),//��ѯ��Ŀ
	U8_FORM("U8_Form"),//����ѯ
	U8_LOSSITEM("U8_LossItem"),//��ѯ��ĵ�item
	U8_YNKPI("U8_YNKPI"),//��ѯҺ��KPIĳ���û��ύ�Ĵ�ֵ��ĵ�
	U8_FormulatorRevision("U8_FormulatorRevision"),//��ѯ���� �����䷽
	U8_MATERIAL_REV_CD("U8_MaterialRevision_CD"),//����ԭ�ϲ�ѯ��
	U8_MATERIAL_REV_LM("U8_MaterialRevision_LM"),//Һ��ԭ�ϲ�ѯ��
	U8_MATERIAL_REV_MP("U8_MaterialRevision_MP"),//�̷�ԭ�ϲ�ѯ��
	U8_MATERIAL_REV_YG("U8_MaterialRevision_YG");//����ԭ�ϲ�ѯ��
	String value;
	private QueryClassConst(String val) {
		this.value = val;
	}
	
	public String getValue(){
		return this.value;
	}
	
}
