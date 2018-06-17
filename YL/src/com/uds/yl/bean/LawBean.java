package com.uds.yl.bean;

import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;

public class LawBean {
	
	@FieldAnotation(value = "uom_tag" ,type =FieldTypeEmu.ITEM)
	public String uom_tag = "U8_Cust";
	
	@FieldAnotation("U8_category")
	public String productCategory;//体系名称
	
	@FieldAnotation("U8_SystemID")
	public String systemId;//体系ID
	
	public String productCategoryDesc;//体系介绍

	public String indicatorName;//指标名称  ---仅这个不需要写到对应的属性里面去
	
	@FieldAnotation("U8_indexrequirment")
	public String indicatorRequire;//指标要求
	
	@FieldAnotation("U8_AssociationID")
	public String relatedSystemId;//关联体系ID
	
	public String indicatorIntroduce;//指标介绍
	
	@FieldAnotation("U8_standardunit")
	public String unit;//指标单位
	
	@FieldAnotation("U8_STDDOWN_OPERATION")
	public String downOperation;//下限符号
	
	@FieldAnotation("U8_STANDUP_OPERATION")
	public String upOperation;//上限符号 
	
	@FieldAnotation("U8_remark")
	public String remark;//指标备注
	
	@FieldAnotation("U8_STAND_UPLINE")
	public String maxValue;//最大值
	
	@FieldAnotation("U8_STAND_DOWNLINE")
	public String minValue;//最小值
	
	@FieldAnotation("U8_testcriterion")
	public String detectionMethod;//检测方法
	
	public String sourceStandard;//来源标准  依据
	
	public String effectiveness;//有效性
	
	public String start_date;//实行日期
	
	public String end_date;//废止日期
	
	@FieldAnotation("U8_SystemNameNote")
	public String systemNameNote;//体系名称备注
	
	@FieldAnotation("U8_CNS")
	public String cns;
	
	@FieldAnotation("U8_INS")
	public String ins;
	
	public String getCns() {
		return cns;
	}
	public void setCns(String cns) {
		this.cns = cns;
	}
	public String getIns() {
		return ins;
	}
	public void setIns(String ins) {
		this.ins = ins;
	}
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	public String getProductCategoryDesc() {
		return productCategoryDesc;
	}
	public void setProductCategoryDesc(String productCategoryDesc) {
		this.productCategoryDesc = productCategoryDesc;
	}
	public String getIndicatorName() {
		return indicatorName;
	}
	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}
	public String getIndicatorRequire() {
		return indicatorRequire;
	}
	public void setIndicatorRequire(String indicatorRequire) {
		this.indicatorRequire = indicatorRequire;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDetectionMethod() {
		return detectionMethod;
	}
	public void setDetectionMethod(String detectionMethod) {
		this.detectionMethod = detectionMethod;
	}
	public String getIndicatorIntroduce() {
		return indicatorIntroduce;
	}
	public void setIndicatorIntroduce(String indicatorIntroduce) {
		this.indicatorIntroduce = indicatorIntroduce;
	}
	public String getSystemNameNote() {
		return systemNameNote;
	}
	public void setSystemNameNote(String systemNameNote) {
		this.systemNameNote = systemNameNote;
	}
	public String getSourceStandard() {
		return sourceStandard;
	}
	public void setSourceStandard(String sourceStandard) {
		this.sourceStandard = sourceStandard;
	}
	public String getEffectiveness() {
		return effectiveness;
	}
	public void setEffectiveness(String effectiveness) {
		this.effectiveness = effectiveness;
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
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
	public String getDownOperation() {
		return downOperation;
	}
	public void setDownOperation(String downOperation) {
		this.downOperation = downOperation;
	}
	public String getUpOperation() {
		return upOperation;
	}
	public void setUpOperation(String upOperation) {
		this.upOperation = upOperation;
	}
	public String getUom_tag() {
		return uom_tag;
	}
	public void setUom_tag(String uom_tag) {
		this.uom_tag = uom_tag;
	}
	
	
	
	
}
