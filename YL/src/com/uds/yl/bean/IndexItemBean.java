package com.uds.yl.bean;


import java.io.Serializable;

import org.eclipse.ui.internal.StartupThreading;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;

public class IndexItemBean implements Serializable{
	public String lawName;//对应的法规名字，不适用注解 单独使用
	
	
	public String parentMaterialInventory;//指标的上层原料BOM的投料量
	
	
	@FieldAnotation(value = "uom_tag" ,type =FieldTypeEmu.ITEM)
	public String uom_tag = "U8_Cust";
	
	
	@FieldAnotation(value="object_name",type=FieldTypeEmu.REVISON)
	public String objectName;//名字
	
	@FieldAnotation(value = "u8_samplename" ,type = FieldTypeEmu.REVISON)
	public String sampleName = "";//法规对比的时候 配方中的条目的名字来源
	
	@FieldAnotation(value="item_id",type=FieldTypeEmu.ITEM)
	public String itemID="";
	
	@FieldAnotation(value="bl_quantity",type=FieldTypeEmu.BOMLINE)
	public String bl_quantity;//配比 就是求和计算时候的百分比
	
	@FieldAnotation(value="U8_up",type=FieldTypeEmu.BOMLINE)
	public String up;//上限
	
	@FieldAnotation(value="U8_down",type=FieldTypeEmu.BOMLINE)
	public String down;//下限
	
	
	
	@FieldAnotation(value="U8_STAND_UPLINE")
	public String GB_UP;//国标上限
	@FieldAnotation(value="U8_STAND_DOWNLINE")
	public String GB_DOWN;//国标下限
	
	@FieldAnotation(value="U8_STANDUP_OPERATION")
	public String GB_UP_SYMBOL;//国标上限符号
	@FieldAnotation(value="U8_STDDOWN_OPERATION")
	public String GB_DOWN_SYMBOL;//国标下限符号
	
	
//	@FieldAnotation(value = "U8_STANDUP_OPERATION", type = FieldTypeEmu.BOMLINE)
//	public String upSymbol="";// 国标上限符号
//
//	@FieldAnotation(value = "U8_STDDOWN_OPERATION", type = FieldTypeEmu.BOMLINE)
//	public String downSymbol="";// 国标下限符号

	
	@FieldAnotation(value="U8_detectvalue",type=FieldTypeEmu.BOMLINE)
	public String detectValue;//检测值描述  也是 检测平均值
	
	@FieldAnotation(value="u8_type",type=FieldTypeEmu.REVISON)
	public String type;//配方中的类型名字 rev中
	
	@FieldAnotation(value="U8_category",type=FieldTypeEmu.BOMLINE)
	public String category;//产品类别

	@FieldAnotation(value="U8_warnings",type=FieldTypeEmu.BOMLINE)
	public String warning;//警告
	
	@FieldAnotation(value = "U8_remark",type=FieldTypeEmu.BOMLINE)
	public String remark;//指标备注
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String average;//检测平均值  up+down除以2
	
	public boolean isFirst = true;//用来标记去重复的时候的第一个
	
	@FieldAnotation("U8_SystemID")
	public String systemId;//体系ID
	
	@FieldAnotation("U8_AssociationID")
	public String relatedSystemId;//关联体系ID
	
	@FieldAnotation("U8_SystemNameNote")
	public String systemNameNote;//体系名称备注
	
	@FieldAnotation("U8_indexrequirment")
	public String indicatorRequire;//指标要求
	
	@FieldAnotation(value ="u8_uom",type = FieldTypeEmu.REVISON)
	public String u8Uom="";//单位 kg g ml 等
	
	@FieldAnotation(value="U8_testcriterion",type=FieldTypeEmu.BOMLINE)
	public String testCriterion="";//检测方法
	
	@FieldAnotation(value="U8_Loss",type=FieldTypeEmu.BOMLINE)
	public String u8Loss="0";//损耗值 默认值是没有损耗
	
	
	@FieldAnotation(value = "U8_inventory", type = FieldTypeEmu.BOMLINE)
	public String U8_inventory="";// 投料量 bom
	
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
