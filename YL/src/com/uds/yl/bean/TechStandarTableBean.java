package com.uds.yl.bean;

import java.util.ArrayList;
import java.util.List;
//������׼��ʹ�õ�
public class TechStandarTableBean{
	public String name;//����
	public String itemId="";//itemId
	public String oldStandard ="";
	public String newStandard= "";
	
	public String unit;//��λ
	public String type="";
	public String newWaring="";//��Ԥ��ֵ
	public String oldWaring = "";//ԭԤ��ֵ
	public String remark = "";//��ע
	public List<String> lawStandards = new ArrayList<String>();
	
	public String indexIntroduceString = "";//ָ��˵��
	
	public String testGis="";//��ⷽ������
	public String currentMethod = "";//��ǰ��ⷽ��  ȡ��ָ��BOM����  U8_testcriterion
	public List<String> allMethodsList = new ArrayList<String>();//���еļ�ⷽ�� ȡ��ָ��İ汾��  u8_testmethod2
}