package com.uds.yl.bean;

import java.io.Serializable;
import java.util.List;

import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;
public class MaterialBean implements Serializable{

	public String lawName="";// 对应的法规名字，不适用注解 单独使用
	
	@FieldAnotation(value = "uom_tag" ,type =FieldTypeEmu.ITEM)
	public String uom_tag = "U8_Cust";
	
	@FieldAnotation(value="item_id",type=FieldTypeEmu.ITEM)
	public String itemID="";
	
	@FieldAnotation(value = "U8_MilkPowderMethod", type = FieldTypeEmu.BOMLINE)
	public String productMethod="";//对应湿法或者干法

	@FieldAnotation(value = "object_name", type = FieldTypeEmu.REVISON)
	public String objectName="";
	
	@FieldAnotation(value = "u8_samplename" ,type = FieldTypeEmu.REVISON)
	public String sampleName = "";//法规对比的时候 配方中的条目的名字来源

	@FieldAnotation(value = "u8_code", type = FieldTypeEmu.REVISON) // 保密代码
	public String code="";

	@FieldAnotation(value = "u8_supplierinfo", type = FieldTypeEmu.REVISON) // 供应商
	public String suppplier="";

	@FieldAnotation(value = "u8_price", type = FieldTypeEmu.REVISON)
	public String price="";// 单位成本

	@FieldAnotation(value = "U8_inventory", type = FieldTypeEmu.BOMLINE)
	public String U8_inventory="";// 投料量 bom

	@FieldAnotation(value = "bl_quantity", type = FieldTypeEmu.BOMLINE)
	public String bl_quantity="";// 配比 bom

	@FieldAnotation(value = "U8_STAND_UPLINE", type = FieldTypeEmu.BOMLINE)
	public String up="";// 上限 在在法规中有值

	@FieldAnotation(value = "U8_STAND_DOWNLINE", type = FieldTypeEmu.BOMLINE)
	public String down="";// 下限 在法规中有值
	
	@FieldAnotation(value = "U8_STANDUP_OPERATION", type = FieldTypeEmu.BOMLINE)
	public String upSymbol="";// 上限符号

	@FieldAnotation(value = "U8_STDDOWN_OPERATION", type = FieldTypeEmu.BOMLINE)
	public String downSymbol="";// 下限符号在法规中有值

	@FieldAnotation(value="U8_detectvalue", type=FieldTypeEmu.BOMLINE)
	public String detectValue="";//描述值在法规中有值
	
	
	
	@FieldAnotation(value="U8_up",type=FieldTypeEmu.BOMLINE)
	public String u8Up;//上限
	
	@FieldAnotation(value="U8_down",type=FieldTypeEmu.BOMLINE)
	public String u8Down;//下限

	
	@FieldAnotation(value = "u8_type", type = FieldTypeEmu.REVISON)
	public String type="";// 配方中的类型名字 rev中

	@FieldAnotation(value = "U8_category", type = FieldTypeEmu.BOMLINE)
	public String category="";// 产品类别

	@FieldAnotation(value = "U8_warnings", type = FieldTypeEmu.BOMLINE)
	public String warning="";// 警告

	@FieldAnotation(value = "U8_alternate", type = FieldTypeEmu.BOMLINE)
	public String alternate="";// 互替说明

	@FieldAnotation(value = "U8_groupitem", type = FieldTypeEmu.BOMLINE)
	public String groupItem="";// 组合说明

	@FieldAnotation(value = "U8_alternateitem", type = FieldTypeEmu.BOMLINE)
	public String alternateItem="";// 替换项
	
	@FieldAnotation(value ="u8_uom",type = FieldTypeEmu.REVISON)
	public String u8Uom="";//单位 kg g ml 等
	
	@FieldAnotation(value="U8_minmaterial",type = FieldTypeEmu.BOMLINE)
	public String minMaterialType="";//小料类型
	
	@FieldAnotation(value = "U8_orderinventory",type = FieldTypeEmu.BOMLINE)
	public String orderInventory="";//订单新增投料量

	public boolean canReplace;//是否可以替换
	
	@FieldAnotation(value="U8_COMPONENTTYPE",type = FieldTypeEmu.BOMLINE)
	public String componentType="";//组分的补足类型
	
	@FieldAnotation(value = "U8_COMPONENTVALUE",type=FieldTypeEmu.BOMLINE)
	public String componentValue="";//组分的补足的值
	
	@FieldAnotation(value = "U8_remark",type=FieldTypeEmu.BOMLINE)
	public String remark;//指标备注
	
	@FieldAnotation("U8_SystemID")
	public String systemId;//体系ID
	
	@FieldAnotation("U8_AssociationID")
	public String relatedSystemId;//关联体系ID
	
	@FieldAnotation("U8_SystemNameNote")
	public String systemNameNote;//体系名称备注
	
	
	@FieldAnotation("U8_indexrequirment")
	public String indicatorRequire;//指标要求
	
	
	
	
	public String sourceOfCompound="";//化合物来源
	
	public MaterialBean replaceMaterialBean;//作为标记该原料的可替代原料
	
	public List<IndexItemBean> indexBeanList;//原料下面的指标条目
	
	public boolean isNutrition=false;//是营养包
	
	@FieldAnotation(value = "u8_isbacteria", type = FieldTypeEmu.REVISON)
	public String isbacteria=""; // 是否是菌
	
	
	public String getIsbacteria() {
		return isbacteria;
	}

	public void setIsbacteria(String isbacteria) {
		this.isbacteria = isbacteria;
	}
	
	public String getSourceOfCompound() {
		return sourceOfCompound;
	}

	public void setSourceOfCompound(String sourceOfCompound) {
		this.sourceOfCompound = sourceOfCompound;
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

	
	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}

	public String getComponentValue() {
		return componentValue;
	}

	public void setComponentValue(String componentValue) {
		this.componentValue = componentValue;
	}

	public String getU8Uom() {
		return u8Uom;
	}

	public void setU8Uom(String u8Uom) {
		this.u8Uom = u8Uom;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSuppplier() {
		return suppplier;
	}

	public void setSuppplier(String suppplier) {
		this.suppplier = suppplier;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getU8_inventory() {
		return U8_inventory;
	}

	public void setU8_inventory(String u8_inventory) {
		U8_inventory = u8_inventory;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLawName() {
		return lawName;
	}

	public void setLawName(String lawName) {
		this.lawName = lawName;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public String getGroupItem() {
		return groupItem;
	}

	public void setGroupItem(String groupItem) {
		this.groupItem = groupItem;
	}

	public String getAlternateItem() {
		return alternateItem;
	}

	public void setAlternateItem(String alternateItem) {
		this.alternateItem = alternateItem;
	}

	public String getMinMaterialType() {
		return minMaterialType;
	}

	public void setMinMaterialType(String minMaterialType) {
		this.minMaterialType = minMaterialType;
	}

	public String getOrderInventory() {
		return orderInventory;
	}

	public void setOrderInventory(String orderInventory) {
		this.orderInventory = orderInventory;
	}

	

	public String getProductMethod() {
		return productMethod;
	}

	public void setProductMethod(String productMethod) {
		this.productMethod = productMethod;
	}

	public String getUpSymbol() {
		return upSymbol;
	}

	public void setUpSymbol(String upSymbol) {
		this.upSymbol = upSymbol;
	}

	public String getDownSymbol() {
		return downSymbol;
	}

	public void setDownSymbol(String downSymbol) {
		this.downSymbol = downSymbol;
	}

	public String getDetectValue() {
		return detectValue;
	}

	public void setDetectValue(String detectValue) {
		this.detectValue = detectValue;
	}

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

	public String getU8Up() {
		return u8Up;
	}

	public void setU8Up(String u8Up) {
		this.u8Up = u8Up;
	}

	public String getU8Down() {
		return u8Down;
	}

	public void setU8Down(String u8Down) {
		this.u8Down = u8Down;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	
}
