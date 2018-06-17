package com.uds.yl.common;


public enum FieldTypeEmu {//注解的属性要从那些类型的对象上获取数据
	TCCOMPONENT("TCComponent",0),
	ITEM("TCComponentItem",1),
	REVISON("TCComponentItemRevision",2),
	BOMLINE("TCComponentBOMLine",3),
	SCHEDULE_TASK("TCComponentScheduleTask",4),
	SCHEDULE("TCComponentSchedule",5);
	
	String type;
	int typeCode;
	FieldTypeEmu(String type,int typeCode){
		this.type = type;
		this.typeCode = typeCode;
	}
	
	public String getType(){
		return this.type;
	}
	public int getTypeCode(){
		return this.typeCode;
	}
	

}
