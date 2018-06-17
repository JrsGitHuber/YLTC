package com.uds.yl.common;


public enum QueryClassConst {
	GENERAL("General..."),//常规查询器
	U8SCHEDULE("U8_Schedule"),//自定义的查询时间表的查询
	U8TASK("U8_Task"),//自定义的查询时间表任务的查询
	U8MATERIAL("U8_MaterialRevision"),//查找原料版本  原料库中的 
	U8_MATERIAL_USEDINLAW("U8_MaterialRevision(UsedInLaw)"),//查找原料版本 标准搭建和 法规导入标记的原料
	U8_MATERIAL_NULL_USEDINLAW("U8_MaterialRevisionNull(UsedInLaw)"),//查找原料版本   标准搭建和法规导入标记的原料 版本上的单位为空
	U8_INDEXITEM_USEDINLAW("U8_IndexItemRevision(UsedInLaw)"),//查找指标版本  标准搭建器用
	U8_INDEXITEM_USEDINLAW_FOR_IMPORT("U8_IndexItemRevision(UsedInLaw)ForImport"),//查找指标版本  法规导入标记的指标
	U8_INDEXITEM_NULL_USEDINLAW("U8_IndexItemRevisionNull(UsedInLaw)"),//查找指标版本  标准搭建用 版本上的单位为空
	U8_INDEXITEM_NULL_USEDINLAW_FOR_IMPORT("U8_IndexItemRevisionNull(UsedInLaw)ForImport"),//查找指标版本 法规导入的指标  版本上的单位为空
	U8_LawRevsion("U8_LawRevsion"),//查询法规本版本   通过  体系ID
	U8_LawItem("U8_Law"),//查询法规  根据Item_id查询法规 法规item   
	U8_INDEX("U8_Index"),//技术标准查询 查询的是版本
	U8_PROJECT("U8_Project"),//查询项目
	U8_FORM("U8_Form"),//表单查询
	U8_LOSSITEM("U8_LossItem"),//查询损耗的item
	U8_YNKPI("U8_YNKPI"),//查询液奶KPI某个用户提交的打分的文档
	U8_FormulatorRevision("U8_FormulatorRevision"),//查询基粉 就是配方
	U8_MATERIAL_REV_CD("U8_MaterialRevision_CD"),//冷饮原料查询器
	U8_MATERIAL_REV_LM("U8_MaterialRevision_LM"),//液奶原料查询器
	U8_MATERIAL_REV_MP("U8_MaterialRevision_MP"),//奶粉原料查询器
	U8_MATERIAL_REV_YG("U8_MaterialRevision_YG");//酸奶原料查询器
	String value;
	private QueryClassConst(String val) {
		this.value = val;
	}
	
	public String getValue(){
		return this.value;
	}
	
}
