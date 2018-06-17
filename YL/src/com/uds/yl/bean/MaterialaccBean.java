package com.uds.yl.bean;

import com.uds.yl.annotation.FieldAnotation;
import com.uds.yl.common.FieldTypeEmu;

public class MaterialaccBean {
	@FieldAnotation(value="u8_category",type=FieldTypeEmu.REVISON)
	public String indexType="";//指标类型
	@FieldAnotation(value="object_name",type=FieldTypeEmu.REVISON)
	public String indexName="";//指标名称
	@FieldAnotation(value="U8_UPLINE")
	public String up="";//上限
	@FieldAnotation(value="U8_DOWNLINE")
	public String down="";//下限
	@FieldAnotation(value="U8_UP_OPERATION")
	public String upMark="";//上线符号
	@FieldAnotation(value="U8_DOWN_OPERATION")
	public String downMark="";//下线符号
	@FieldAnotation(value="U8_detectvalue")
	public String indexDec="";//标准描述
	public String getIndexType() {
		return indexType;
	}
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	public String getIndexName() {
		return indexName;
	}
	public void setIndexName(String indexName) {
		this.indexName = indexName;
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
	public String getUpMark() {
		return upMark;
	}
	public void setUpMark(String upMark) {
		this.upMark = upMark;
	}
	public String getDownMark() {
		return downMark;
	}
	public void setDownMark(String downMark) {
		this.downMark = downMark;
	}
	public String getIndexDec() {
		return indexDec;
	}
	public void setIndexDec(String indexDec) {
		this.indexDec = indexDec;
	}
	
}
