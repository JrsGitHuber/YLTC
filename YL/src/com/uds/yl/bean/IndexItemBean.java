package com.uds.yl.bean;


import java.io.Serializable;

import org.eclipse.ui.internal.StartupThreading;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;

public class IndexItemBean implements Serializable{
	public String lawName;//��Ӧ�ķ������֣�������ע�� ����ʹ��
	
	
	public String parentMaterialInventory;//ָ����ϲ�ԭ��BOM��Ͷ����
	
	
	@FieldAnotation(value = "uom_tag" ,type =FieldTypeEmu.ITEM)
	public String uom_tag = "U8_Cust";
	
	
	@FieldAnotation(value="object_name",type=FieldTypeEmu.REVISON)
	public String objectName;//����
	
	@FieldAnotation(value = "u8_samplename" ,type = FieldTypeEmu.REVISON)
	public String sampleName = "";//����Աȵ�ʱ�� �䷽�е���Ŀ��������Դ
	
	@FieldAnotation(value="item_id",type=FieldTypeEmu.ITEM)
	public String itemID="";
	
	@FieldAnotation(value="bl_quantity",type=FieldTypeEmu.BOMLINE)
	public String bl_quantity;//��� ������ͼ���ʱ��İٷֱ�
	
	@FieldAnotation(value="U8_up",type=FieldTypeEmu.BOMLINE)
	public String up;//����
	
	@FieldAnotation(value="U8_down",type=FieldTypeEmu.BOMLINE)
	public String down;//����
	
	
	
	@FieldAnotation(value="U8_STAND_UPLINE")
	public String GB_UP;//��������
	@FieldAnotation(value="U8_STAND_DOWNLINE")
	public String GB_DOWN;//��������
	
	@FieldAnotation(value="U8_STANDUP_OPERATION")
	public String GB_UP_SYMBOL;//�������޷���
	@FieldAnotation(value="U8_STDDOWN_OPERATION")
	public String GB_DOWN_SYMBOL;//�������޷���
	
	
//	@FieldAnotation(value = "U8_STANDUP_OPERATION", type = FieldTypeEmu.BOMLINE)
//	public String upSymbol="";// �������޷���
//
//	@FieldAnotation(value = "U8_STDDOWN_OPERATION", type = FieldTypeEmu.BOMLINE)
//	public String downSymbol="";// �������޷���

	
	@FieldAnotation(value="U8_detectvalue",type=FieldTypeEmu.BOMLINE)
	public String detectValue;//���ֵ����  Ҳ�� ���ƽ��ֵ
	
	@FieldAnotation(value="u8_type",type=FieldTypeEmu.REVISON)
	public String type;//�䷽�е��������� rev��
	
	@FieldAnotation(value="U8_category",type=FieldTypeEmu.BOMLINE)
	public String category;//��Ʒ���

	@FieldAnotation(value="U8_warnings",type=FieldTypeEmu.BOMLINE)
	public String warning;//����
	
	@FieldAnotation(value = "U8_remark",type=FieldTypeEmu.BOMLINE)
	public String remark;//ָ�걸ע
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String average;//���ƽ��ֵ  up+down����2
	
	public boolean isFirst = true;//�������ȥ�ظ���ʱ��ĵ�һ��
	
	@FieldAnotation("U8_SystemID")
	public String systemId;//��ϵID
	
	@FieldAnotation("U8_AssociationID")
	public String relatedSystemId;//������ϵID
	
	@FieldAnotation("U8_SystemNameNote")
	public String systemNameNote;//��ϵ���Ʊ�ע
	
	@FieldAnotation("U8_indexrequirment")
	public String indicatorRequire;//ָ��Ҫ��
	
	@FieldAnotation(value ="u8_uom",type = FieldTypeEmu.REVISON)
	public String u8Uom="";//��λ kg g ml ��
	
	@FieldAnotation(value="U8_testcriterion",type=FieldTypeEmu.BOMLINE)
	public String testCriterion="";//��ⷽ��
	
	@FieldAnotation(value="U8_Loss",type=FieldTypeEmu.BOMLINE)
	public String u8Loss="0";//���ֵ Ĭ��ֵ��û�����
	
	
	@FieldAnotation(value = "U8_inventory", type = FieldTypeEmu.BOMLINE)
	public String U8_inventory="";// Ͷ���� bom
	
	public String getU8Uom() {
		return u8Uom;
	}

	public void setU8Uom(String u8Uom) {
		this.u8Uom = u8Uom;
	}

	public String getTestCriterion() {
		return testCriterion;
	}

	public void setTestCriterion(String testCriterion) {
		this.testCriterion = testCriterion;
	}

	public String getIndicatorRequire() {
		return indicatorRequire;
	}

	public void setIndicatorRequire(String indicatorRequire) {
		this.indicatorRequire = indicatorRequire;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getRelatedSystemId() {
		return relatedSystemId;
	}

	public void setRelatedSystemId(String relatedSystemId) {
		this.relatedSystemId = relatedSystemId;
	}

	public String getSystemNameNote() {
		return systemNameNote;
	}

	public void setSystemNameNote(String systemNameNote) {
		this.systemNameNote = systemNameNote;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getBl_quantity() {
		return bl_quantity;
	}

	public void setBl_quantity(String bl_quantity) {
		this.bl_quantity = bl_quantity;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getDown() {
		return down;
	}

	public void setDown(String down) {
		this.down = down;
	}

	public String getDetectValue() {
		return detectValue;
	}

	public void setDetectValue(String detectValue) {
		this.detectValue = detectValue;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getWarning() {
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getLawName() {
		return lawName;
	}

	public void setLawName(String lawName) {
		this.lawName = lawName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getU8_inventory() {
		return U8_inventory;
	}

	public void setU8_inventory(String u8_inventory) {
		U8_inventory = u8_inventory;
	}

	public String getU8Loss() {
		return u8Loss;
	}

	public void setU8Loss(String u8Loss) {
		this.u8Loss = u8Loss;
	}

//	public String getUpSymbol() {
//		return upSymbol;
//	}
//
//	public void setUpSymbol(String upSymbol) {
//		this.upSymbol = upSymbol;
//	}
//
//	public String getDownSymbol() {
//		return downSymbol;
//	}
//
//	public void setDownSymbol(String downSymbol) {
//		this.downSymbol = downSymbol;
//	}

	public String getUom_tag() {
		return uom_tag;
	}

	public void setUom_tag(String uom_tag) {
		this.uom_tag = uom_tag;
	}

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	
	@Override
	public String toString() {
		return objectName;
	}

	public String getGB_UP() {
		return GB_UP;
	}

	public void setGB_UP(String gB_UP) {
		GB_UP = gB_UP;
	}

	public String getGB_DOWN() {
		return GB_DOWN;
	}

	public void setGB_DOWN(String gB_DOWN) {
		GB_DOWN = gB_DOWN;
	}

	public String getGB_UP_SYMBOL() {
		return GB_UP_SYMBOL;
	}

	public void setGB_UP_SYMBOL(String gB_UP_SYMBOL) {
		GB_UP_SYMBOL = gB_UP_SYMBOL;
	}

	public String getGB_DOWN_SYMBOL() {
		return GB_DOWN_SYMBOL;
	}

	public void setGB_DOWN_SYMBOL(String gB_DOWN_SYMBOL) {
		GB_DOWN_SYMBOL = gB_DOWN_SYMBOL;
	}
	
	
	
	
	
	
}
